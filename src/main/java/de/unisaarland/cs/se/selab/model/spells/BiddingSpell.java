package de.unisaarland.cs.se.selab.model.spells;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Player;

public class BiddingSpell extends Spell {

    final BidType blockBidType;

    public BiddingSpell(final int id, final String triggerBid,
            final int triggerSlot, final String blockBidType) {
        super(id, triggerBid, triggerSlot, 4);
        this.blockBidType = BidType.valueOf(blockBidType);
    }

    @Override
    public boolean cast(final Player player, final ConnectionWrapper connection) {
        final int round = player.getRoundOfSpell(this);
        player.curseBid(blockBidType, round);
        return false;
    }
}
