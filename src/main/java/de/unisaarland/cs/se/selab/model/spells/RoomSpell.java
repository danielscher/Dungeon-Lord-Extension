package de.unisaarland.cs.se.selab.model.spells;


import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Player;

public class RoomSpell extends Spell {


    public RoomSpell(int id, String triggerBid, int triggerSlot) {
        super(id, triggerBid, triggerSlot, 3);
    }

    @Override
    public boolean cast(Player player, ConnectionWrapper connection) {
        int round = player.getRoundOfSpell(this);
        player.curseRooms(round);
        connection.sendRoomsBlocked(player.getId(),round);
        return false;
    }
}
