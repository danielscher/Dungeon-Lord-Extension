package de.unisaarland.cs.se.selab.model.spells;

import de.unisaarland.cs.se.selab.comm.BidType;

public class BiddingSpell extends Spell {

    final BidType blockBidType;

    public BiddingSpell(int id, String triggerBid,
            int triggerSlot, String blockBidType) {
        super(id, triggerBid, triggerSlot);
        this.blockBidType = BidType.valueOf(blockBidType);
    }

    @Override
    public void cast() {

    }
}
