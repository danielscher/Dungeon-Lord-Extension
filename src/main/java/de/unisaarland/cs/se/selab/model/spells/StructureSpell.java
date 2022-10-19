package de.unisaarland.cs.se.selab.model.spells;


import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import java.util.Optional;

public class StructureSpell extends Spell {

    final boolean conquer;

    public StructureSpell(final int id, final String triggerBid,
            final int triggerSlot, final String structureEffect) {
        super(id, triggerBid, triggerSlot, 5);
        this.conquer = "CONQUER".equals(structureEffect);
    }


    @Override
    public boolean cast(final Player player, final ConnectionWrapper connection) {
        if (conquer) {
            return true;
        }
        final Optional<Room> room = player.getDungeon().destroyRoom();
        if (room.isPresent()) {
            final int roomId = room.get().getId();
            connection.sendRoomRemoved(player.getId(), roomId);
        }
        return false;
    }
}
