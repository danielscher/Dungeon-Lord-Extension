package de.unisaarland.cs.se.selab.model.spells;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Player;

public abstract class Spell {

    private final int id;
    private final BidType triggerBid;
    private final int triggerSlot;
    private int cost; //TODO add costs to each concrete spell
    private boolean countered;

    protected Spell(int id, String triggerBid, int triggerSlot) {
        this.id = id;
        this.triggerBid  = BidType.valueOf(triggerBid);
        this.triggerSlot = triggerSlot;
    }

    public abstract void cast();

    private boolean checkTrigger(Player player) {
        return false;
    }

    private boolean checkIfCountered(Player player) {
        return false;
    }

}
