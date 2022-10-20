package de.unisaarland.cs.se.selab.model;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.spells.Spell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This holds all player-related data.
 */
public class Player {

    public static final int MIN_EVILNESS = 0;
    public static final int MAX_EVILNESS = 15;

    private final int id;
    private final String name;
    private final List<Monster> monsters;
    private final List<Trap> traps;
    private final Dungeon dungeon;
    private final List<Optional<BidType>> bids;
    private final List<BidType> lockedTypes;

    private int evilness;
    private int imps;
    private int gold;
    private int food;
    private int numTunnelDigsAllowed;
    private boolean alive;
    private int scorePoints;

    private int numCounterSpells;

    private int timesCountered;
    private int timesCursed;
    private int timesTriggeredLinus;
    private final Map<Integer, List<Spell>> spells = new HashMap<>();
    private final Set<Integer> roomsCursedInRounds = new HashSet<>();
    private final List<List<BidType>> cursedBids = new ArrayList<>();
    private boolean spellCountered;

    public Player(final int id,
            final String name,
            final int initialFood,
            final int initialGold,
            final int initialImps,
            final int initialEvilness,
            final Dungeon dungeon) {
        this.id = id;
        this.name = name;
        this.monsters = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.dungeon = dungeon;
        this.lockedTypes = new ArrayList<>();
        this.food = initialFood;
        this.gold = initialGold;
        this.imps = initialImps;
        this.evilness = initialEvilness;
        this.bids = new ArrayList<>(Model.BID_LIMIT);
        this.alive = true;
        clearBidTypes();
    }

    public final int getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    /**
     * Place a bid a player sent via a place bid command.
     *
     * @param type     the bid's type
     * @param priority the bid's priority, i.e., whether it is the first, second, or third bid
     * @return {@code true} if the bid could be placed or {@code false} if the bid or priority is
     * not available
     */
    public final boolean placeBid(final BidType type, final int priority) {
        // Bid priorities start at 1 but list indices start at 0, hence the -1
        if (this.bids.get(priority - 1).isEmpty() && checkBidType(type)) {
            this.bids.set(priority - 1, Optional.of(type));
            return true;
        }
        return false;
    }

    /**
     * Check whether the given bid type was already placed.
     *
     * @param type the bid type to check.
     * @return whether the bid is still available for placing, or not
     */
    private boolean checkBidType(final BidType type) {
        return getPlacedBidTypes().stream().noneMatch(placedBid -> placedBid == type);
    }

    /**
     * Check whether the player is still alive and finished with bidding.
     *
     * @return whether the player is finished with bidding
     */
    public final boolean finishedBidding() {
        return isAlive() && !hasToBid();
    }

    /**
     * Check whether the player still has to bid.
     *
     * @return whether the player still has to bid
     */
    public final boolean hasToBid() {
        return isAlive() && getPlacedBidTypes().size() != Model.BID_LIMIT;
    }

    /**
     * Get all placed bid types for this player.
     *
     * @return the list of placed bid types
     */
    public final List<BidType> getPlacedBidTypes() {
        return this.bids.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Clear the list of placed bids.
     */
    public final void clearBidTypes() {
        this.bids.clear();
        for (int i = 0; i < Model.BID_LIMIT; i++) {
            this.bids.add(Optional.empty());
        }
    }

    /**
     * Lock the bids of second and third priority.
     */
    public final void lockBidTypes() {
        // Bid priorities start at 1 but list indices start at 0, hence the off-by-one
        this.bids.get(1).ifPresent(this.lockedTypes::add);
        this.bids.get(2).ifPresent(this.lockedTypes::add);
    }

    /**
     * Unlock previously locked bid types.
     */
    public final void unlockBidTypes() {
        this.lockedTypes.clear();
    }

    /**
     * Check whether the given bid type is locked.
     *
     * @param type the bid type to check
     * @return whether the bid type is locked
     */
    public final boolean isLocked(final BidType type) {
        return this.lockedTypes.contains(type);
    }

    public boolean isCursed(final BidType bid, final int round) {
        if (cursedBids.size() < round) {
            return false;
        }
        return this.cursedBids.get(round - 1).contains(bid);
    }

    /**
     * Get a list of currently locked bid types.
     *
     * @return the list of locked bid types
     */
    public final List<BidType> getLockedTypes() {
        return this.lockedTypes;
    }

    public int getEvilness() {
        return this.evilness;
    }

    public void changeEvilness(final int evilness) {
        this.evilness += evilness;
    }

    public int getImps() {
        return this.imps;
    }

    public void changeImps(final int imps) {
        this.imps += imps;
    }

    public int getGold() {
        return this.gold;
    }

    public void changeGold(final int gold) {
        this.gold += gold;
    }

    public int getFood() {
        return this.food;
    }

    public void changeFood(final int food) {
        this.food += food;
    }

    public final List<Monster> getMonsters() {
        return this.monsters;
    }

    public final Optional<Monster> getMonster(final int monsterId) {
        return this.monsters.stream()
                .filter(monster -> monster.getId() == monsterId)
                .findFirst();
    }

    public int getNumMonsters() {
        return monsters.size();
    }

    /**
     * Make all monsters available for being set on defense again.
     */
    public final void wakeUpMonsters() {
        this.monsters.forEach(monster -> monster.setUsed(false));
    }

    public final void addMonster(final Monster monster) {
        this.monsters.add(monster);
    }

    public List<Trap> getTraps() {
        return this.traps;
    }

    public final Optional<Trap> getTrap(final int trapId) {
        return this.traps.stream()
                .filter(trap -> trap.getId() == trapId)
                .findFirst();
    }

    public void addTrap(final Trap trap) {
        this.traps.add(trap);
    }

    /**
     * Get the number of tunnels the player is allowed to dig.
     *
     * @return the number of tunnels a player may dig
     */
    public final int getNumTunnelDigsAllowed() {
        return this.numTunnelDigsAllowed;
    }

    /**
     * Set the number of tunnels the player is allowed to dig.
     *
     * @param numTunnelDigsAllowed the number of tunnels a player may dig
     */
    public final void setNumTunnelDigsAllowed(final int numTunnelDigsAllowed) {
        this.numTunnelDigsAllowed = numTunnelDigsAllowed;
    }

    /**
     * Reduces the number of tunnels the player is allowed to dig by 1.
     */
    public final void digTunnel() {
        this.numTunnelDigsAllowed--;
    }

    public final Dungeon getDungeon() {
        return this.dungeon;
    }

    /**
     * Indicate that this player has left the game.
     */
    public void kill() {
        this.alive = false;
    }

    /**
     * Check whether this player still participates in the game.
     *
     * @return whether this player still participates in the game
     */
    public boolean isAlive() {
        return this.alive;
    }

    public int getScorePoints() {
        return this.scorePoints;
    }

    public void changeScorePoints(final int amount) {
        this.scorePoints += amount;
    }

    public void curseBid(final BidType bid, final int round) {
        cursedBids.get(round - 1).add(bid);
    }

    public void resetBiddingSpell(final int round) {
        if (cursedBids.size() >= round) {
            cursedBids.remove(round - 1);
        }
    }

    public List<Spell> getSpellsForRound(final int round) {
        if (spells.containsKey(round - 1)) {
            return spells.get(round - 1);
        } else {
            return null;
        }
    }

    public int getRoundOfSpell(final Spell spell) {
        return spells.entrySet().stream().filter(entry -> entry.getValue().contains(spell))
                .findFirst().get().getKey();
    }

    public int getNumCounterSpells() {
        return numCounterSpells;
    }

    public void useCounterSpell() {
        this.numCounterSpells -= 1;
        this.timesCountered += 1;
        this.spellCountered = true;
    }

    public void addCounterSpell() {
        this.numCounterSpells += 1;
    }

    public void addSpell(final List<Spell> triggeredSpells, final int round) {
        if (spells.containsKey(round - 1)) {
            final List<Spell> vals = new ArrayList<>(spells.get(round - 1));
            vals.addAll(triggeredSpells);
            spells.put(round - 1, vals);
        } else {
            spells.put(round - 1, triggeredSpells);
        }
    }

    public boolean hasCountered() {
        return this.spellCountered;
    }

    public void resetCounterFlag() {
        this.spellCountered = false;
    }

    /**
     * increments timeCursedCounter by 1.
     */
    public void curse() {
        timesCursed++;
    }

    public void curseRooms(final int round) {
        if (roomsCursedInRounds.contains(round)) {
            return;
        }
        roomsCursedInRounds.add(round);
    }

    public void clearRoomCurse() {
        roomsCursedInRounds.clear();
    }

    public void triggerLinus() {
        this.timesTriggeredLinus++;
    }


    public Monster removeMonster(final int index) {
        return this.monsters.remove(index);
    }

    public void removeSpells() {
        this.spells.clear();
    }

    public Integer getTimesCursed() {
        return timesCursed;
    }

    public Integer getTimesLinusTriggered() {
        return timesTriggeredLinus;
    }

    public Integer gettimesCountered() {
        return timesCountered;
    }
}
