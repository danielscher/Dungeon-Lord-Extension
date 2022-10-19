package de.unisaarland.cs.se.selab.model.spells;


import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import java.util.Optional;

public class StructureSpell extends Spell {

    final boolean conquer;

    public StructureSpell(int id, String triggerBid,
            int triggerSlot, String structureEffect) {
        super(id, triggerBid, triggerSlot, 5);
        this.conquer = structureEffect.equals("CONQUER");
    }


    @Override
    public boolean cast(Player player, ConnectionWrapper connection, int advMagicPoints) {
        if (conquer) {
            return true;
        }
        Optional<Room> room = player.getDungeon().destroyRoom();
        if (room.isPresent()) {
            final int roomId = room.get().getId();
            connection.sendRoomRemoved(player.getId(), roomId);
        }
        return false;
    }
}
