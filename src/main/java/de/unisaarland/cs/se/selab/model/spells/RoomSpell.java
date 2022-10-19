package de.unisaarland.cs.se.selab.model.spells;


import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Player;

public class RoomSpell extends Spell {


    public RoomSpell(final int id, final String triggerBid, final int triggerSlot) {
        super(id, triggerBid, triggerSlot, 3);
    }

    @Override
    public boolean cast(final Player player, final ConnectionWrapper connection) {
        final int round = player.getRoundOfSpell(this);
        player.curseRooms(round);
        connection.sendRoomsBlocked(player.getId(), round);
        return false;
    }
}
