package unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.dungeon.Coordinate;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Room.BuildingRestriction;
import de.unisaarland.cs.se.selab.model.dungeon.Tunnel;
import de.unisaarland.cs.se.selab.model.dungeon.TunnelGraph;
import java.util.EnumMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DungeonTest {

    private final TunnelGraph graph = new TunnelGraph();

    @BeforeEach
    void resetGraph() {
        final Tunnel t1 = new Tunnel(new Coordinate(0, 1), false);
        final Tunnel t2 = new Tunnel(new Coordinate(0, 2), false);
        final Tunnel t3 = new Tunnel(new Coordinate(0, 3), false);
        final Tunnel t4 = new Tunnel(new Coordinate(0, 4), false);
        final Tunnel t5 = new Tunnel(new Coordinate(1, 0), false);
        final Tunnel t6 = new Tunnel(new Coordinate(2, 0), false);

        final EnumMap<BidType, Integer> production = new EnumMap<>(BidType.class);
        production.put(BidType.FOOD, 2);
        final Room r1 = new Room(0, 1, BuildingRestriction.INNER_RING,
                production);
        final Room r2 = new Room(1, 1, BuildingRestriction.INNER_RING,
                production);

        t4.buildRoom(r1);
        t6.buildRoom(r2);
        graph.addTunnel(t1);
        graph.addTunnel(t2);
        graph.addTunnel(t3);
        graph.addTunnel(t4);
        graph.addTunnel(t5);
        graph.addTunnel(t6);

    }

    @Test
    void testGetClosestRoom() {
        final Tunnel t1 = new Tunnel(new Coordinate(0, 0), false);
        assertEquals(1, graph.getClosestTunnelWithRoom(t1).flatMap(Tunnel::getRoom).get().getId());
    }

    @Test
    void testEqualDistanceRooms() {
        final Tunnel t1 = new Tunnel(new Coordinate(0, 1), false);
        assertEquals(0, graph.getClosestTunnelWithRoom(t1).flatMap(Tunnel::getRoom).get().getId());
    }

}
