package de.unisaarland.cs.se.selab.commands;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.state.State;
import de.unisaarland.cs.se.selab.state.State.Phase;
import java.util.Optional;
import java.util.Set;

public class SpellCommand extends PlayerCommand {

    public SpellCommand(int playerId) {
        super(playerId);
    }


    @Override
    public Set<Phase> inPhase() {
        return Set.of(State.Phase.COMBAT);
    }

    @Override
    protected ActionResult run(Model model, ConnectionWrapper connection) {
        final Player player = model.getPlayerById(getId());

        // if player tries to counter during choosing a battleground fail.
        final Dungeon dungeon = player.getDungeon();
        final Optional<Tunnel> battleGround = dungeon.getBattleGround();
        if (battleGround.isEmpty()) {
            connection.sendActionFailed(getId(), "The battleground has not been set yet.");
            return ActionResult.RETRY;
        }

        // if no counter spells left players needs to end the turn.
        if (player.getNumCounterSpells() < 1) {
            connection.sendActionFailed(getId(), "You have no counter spells left.");
            return ActionResult.RETRY;
        }

        player.useCounterSpell();

        return ActionResult.PROCEED;
    }
}
