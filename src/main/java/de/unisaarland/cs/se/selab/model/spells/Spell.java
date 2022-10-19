package de.unisaarland.cs.se.selab.model.spells;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Player;

public abstract class Spell {

    private final int id;
    private final BidType triggerBid;
    private final int triggerSlot;
    int cost;

    protected Spell(final int id, final String triggerBid, final int triggerSlot, final int cost) {
        this.id = id;
        this.triggerBid = BidType.valueOf(triggerBid);
        this.triggerSlot = triggerSlot;
        this.cost = cost;
    }

    public abstract boolean cast(Player player, ConnectionWrapper connection,
            final int advMagicPoints);


    public int getId() {
        return id;
    }

    public BidType getTriggerBid() {
        return triggerBid;
    }

    public int getTriggerSlot() {
        return triggerSlot;
    }

}
