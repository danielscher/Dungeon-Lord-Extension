package de.unisaarland.cs.se.selab.state;

import de.unisaarland.cs.se.selab.ConnectionUtils;
import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.commands.ActionResult;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.DefensiveMeasure;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Monster;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This state handles the combat for all players.
 */
public final class CombatState extends State {

    private static final int EVILNESS_WHEN_CONQUERED = -1;
    private static final int FATIGUE_DAMAGE = 2;

    public CombatState(final Model model, final ConnectionWrapper connection) {
        super(model, connection);
    }

    @Override
    public State run() {
        for (final Player player : model.getPlayers()) {
            if (handleCombatForPlayer(player) == ActionResult.END_GAME) {
                return new EndGameState(this.model, this.connection);
            }
        }

        if (model.hasNextYear()) {
            return new BuildingState(this.model, this.connection);
        } else {
            evaluateTitles();
            return new EndGameState(this.model, this.connection);
        }
    }

    /**
     * Simulate all combat rounds for one player.
     *
     * @param player the player who does combat
     * @return a result that indicates how the game should continue
     */
    private ActionResult handleCombatForPlayer(final Player player) {
        while (model.hasNextRound()) {
            final Dungeon dungeon = player.getDungeon();
            if (player.isAlive() && dungeon.adventurersLeft()) {
                connection.sendNextRound(model.getRound());
                if (dungeon.isConquered()) {
                    prisonerFlees(player);
                } else {
                    if (fight(player) == ActionResult.END_GAME) {
                        return ActionResult.END_GAME;
                    }
                }
            }
        }
        return ActionResult.PROCEED;
    }

    private void prisonerFlees(final Player player) {
        final Dungeon dungeon = player.getDungeon();
        if (dungeon.getNumImprisonedAdventurers() > 0) {
            connection.sendAdventurerFled(dungeon.freeAdventurer().getId());
            if (player.getEvilness() > Player.MIN_EVILNESS) {
                player.changeEvilness(EVILNESS_WHEN_CONQUERED);
                connection.sendEvilnessChanged(player.getId(), EVILNESS_WHEN_CONQUERED);
            }
        }
    }

    /**
     * Simulate a single combat round for a player.
     *
     * @param player the player who does combat
     * @return a result that indicates how the game should continue
     */
    private ActionResult fight(final Player player) {
        // Player can select a battleground.
        connection.sendSetBattleGround(player.getId());
        final ActionResult battlegroundResult = ConnectionUtils.executePlayerCommand(
                model, connection, Phase.SET_BATTLEGROUND, player);
        if (!player.isAlive()) {
            return battlegroundResult;
        }
        // Player can place monsters or traps.
        connection.sendDefendYourself(player.getId());
        final ActionResult defendResult =
                ConnectionUtils.executePlayerCommand(model, connection, Phase.COMBAT, player);
        if (!player.isAlive()) {
            return defendResult;
        }
        damageAdventurers(player);
        adventurersConquer(player);
        priestsHeal(player.getDungeon().getAllAdventurers());
        return ActionResult.PROCEED;
    }

    private void priestsHeal(final Iterable<Adventurer> adventurers) {
        for (final Adventurer healer : adventurers) {
            int healValue = healer.getHealValue();
            for (final Adventurer target : adventurers) {
                final int healed = target.heal(healValue);
                healValue -= healed;
                if (healed > 0) {
                    connection.sendAdventurerHealed(healer.getId(), target.getId(), healed);
                }
            }
        }
    }

    private void adventurersConquer(final Player player) {
        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> battleGround = dungeon.getBattleGround();
        if (battleGround.isPresent()) {
            final Optional<Adventurer> attacker = dungeon.getAdventurer(1);
            if (attacker.isPresent()) {
                dungeon.conquer();
                final Coordinate coordinate = battleGround.get().getCoordinate();
                connection.sendTunnelConquered(attacker.get().getId(), coordinate);
                if (player.getEvilness() > Player.MIN_EVILNESS) {
                    player.changeEvilness(EVILNESS_WHEN_CONQUERED);
                    connection.sendEvilnessChanged(player.getId(), EVILNESS_WHEN_CONQUERED);
                }
            }
            battleGround.get().clearDefenders();
        }
    }

    private void damageAdventurers(final Player player) {
        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> battleGround = dungeon.getBattleGround();
        if (battleGround.isPresent()) {
            final int reduction = dungeon.getAllAdventurers()
                    .stream()
                    .map(Adventurer::getDefuseValue)
                    .reduce(0, Integer::sum);
            // Damage from trap if there is one.
            battleGround.get().getTrap()
                    .ifPresent(trap -> evaluateDefense(trap, player, reduction));
            // Damage from placed monsters.
            for (final Monster monster : battleGround.get().getMonsters()) {
                evaluateDefense(monster, player, 0);
            }
        }
        // Deal fatigue damage
        for (final Adventurer adventurer : dungeon.getAllAdventurers()) {
            hurtAdventurer(adventurer, player, CombatState.FATIGUE_DAMAGE);
        }
    }

    private void evaluateDefense(final DefensiveMeasure defense, final Player player,
                                 final int totalReduction) {
        final Dungeon dungeon = player.getDungeon();
        switch (defense.getAttackStrategy()) {
            case BASIC -> {
                final int reducedDamage = Math.max(0, defense.getDamage() - totalReduction);
                final Optional<Adventurer> attacker = dungeon.getAdventurer(1);
                attacker.ifPresent(adventurer -> hurtAdventurer(adventurer, player, reducedDamage));
            }
            case MULTI -> {
                int alreadyReduced = 0;
                for (final Adventurer adventurer : dungeon.getAllAdventurers()) {
                    final int reductionLeft = totalReduction - alreadyReduced;
                    hurtAdventurer(adventurer, player,
                            Math.max(defense.getDamage() - reductionLeft, 0));
                    alreadyReduced += Math.min(defense.getDamage(), reductionLeft);
                }
            }
            case TARGETED -> {
                if (defense.hasTarget()) {
                    final int reducedDamage = Math.max(0, defense.getDamage() - totalReduction);
                    final Optional<Adventurer> attacker =
                            dungeon.getAdventurer(defense.getTarget());
                    attacker.ifPresent(adventurer -> hurtAdventurer(adventurer, player,
                            reducedDamage));
                }
            }
            default -> {
            }
        }
    }

    private void hurtAdventurer(final Adventurer adventurer, final Player player,
                                final int damage) {
        final int effectiveDamage = adventurer.damage(damage);
        if (effectiveDamage > 0) {
            if (adventurer.isDefeated()) {
                //TODO: check if linus triggered here
                player.getDungeon().imprisonAdventurer(adventurer);
                connection.sendAdventurerImprisoned(adventurer.getId());
            } else {
                connection.sendAdventurerDamaged(adventurer.getId(), effectiveDamage);
            }
        }
    }

    /**
     * Calculates player scores and awards titles at the end of a game.
     */
    private void evaluateTitles() {
        final List<Player> players = model.getPlayers();

        // Scoring individual points
        for (final Player player : players) {
            // 1 point per monster.
            player.changeScorePoints(player.getMonsters().size());
            // 2 points per room.
            player.changeScorePoints(2 * player.getDungeon().getGraph().getNumRooms());
            // -2 points per conquered tunnel! tile.
            player.changeScorePoints(
                    -2 * (int) player.getDungeon().getGraph().stream()
                            .filter(tunnel -> tunnel.isConquered() && !tunnel.isRoom())
                            .count());
            // 2 points per imprisoned adventurer.
            player.changeScorePoints(2 * player.getDungeon().getNumImprisonedAdventurers());
        }

        // Define all titles.
        final List<Function<Player, Integer>> titles = new ArrayList<>();      // The Lord of ...
        titles.add(Player::getEvilness);                                       // Dark Deeds
        titles.add(player -> player.getDungeon().getGraph().getNumRooms());    // Halls
        titles.add(player -> player.getDungeon().getGraph().getNumTunnels());  // Tunnels
        titles.add(player -> player.getMonsters().size());                     // Monsters
        titles.add(Player::getImps);                                           // Imps
        titles.add(player -> player.getGold() + player.getFood());             // Riches
        titles.add(player -> player.getDungeon().getNumUnconqueredTiles());    // Battle

        // Evaluate all titles.
        for (final Function<Player, Integer> title : titles) {
            processTitle(players, title);
        }

        // Find winner.
        final Optional<Player> optWinner =
                players.stream().max(Comparator.comparing(Player::getScorePoints));

        if (optWinner.isPresent()) {
            final int highestScore = optWinner.get().getScorePoints();
            for (final Player player : model.getPlayers()) {
                if (player.getScorePoints() == highestScore) {
                    connection.sendGameEnd(player.getId(), highestScore);
                }
            }
        }
    }

    private void processTitle(final Collection<Player> players,
                              final Function<Player, Integer> title) {
        final Optional<Player> optWinner = players.stream()
                .max(Comparator.comparing(title));
        if (optWinner.isPresent()) {
            final int highestScore = title.apply(optWinner.get());
            int amountOfWinners = 0;
            for (final Player player : players) {
                if (title.apply(player) == highestScore) {
                    amountOfWinners++;
                    player.changeScorePoints(2);
                }
            }
            if (amountOfWinners == 1) {
                optWinner.get().changeScorePoints(1);
            }
        }
    }
}
