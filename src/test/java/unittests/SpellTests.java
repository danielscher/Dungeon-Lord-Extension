package unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.Adventurer;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.Player;
import de.unisaarland.cs.se.selab.model.dungeon.Dungeon;
import de.unisaarland.cs.se.selab.model.dungeon.Room;
import de.unisaarland.cs.se.selab.model.dungeon.Room.BuildingRestriction;
import de.unisaarland.cs.se.selab.model.spells.BiddingSpell;
import de.unisaarland.cs.se.selab.model.spells.BuffSpell;
import de.unisaarland.cs.se.selab.model.spells.ResourceSpell;
import de.unisaarland.cs.se.selab.model.spells.RoomSpell;
import de.unisaarland.cs.se.selab.model.spells.Spell;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpellTests {

    Player player = new Player(0, "", 5, 5, 5, 5, new Dungeon());
    Model model = new Model(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), 0, 1, 1, 5, 5, 5, 5, 5);
    ConnectionWrapper connection = new ConnectionWrapper(5005, 5000, "config");
    Spell biddingSpell = new BiddingSpell(0, "FOOD", 1, "FOOD");
    Spell buffSpell = new BuffSpell(1, "FOOD", 1, 1, 1, 1);
    Spell resourceSpell = new ResourceSpell(2, "FOOD", 1, 1, 1);

    Spell roomSpell = new RoomSpell(3, "FOOD", 1);

    final EnumMap<BidType, Integer> production = new EnumMap<>(BidType.class);

    final Room r1 = new Room(0, 1, BuildingRestriction.INNER_RING,
            production);
    List<Spell> spells = new ArrayList<>();

    @BeforeEach
    void resetSpell() {
        player.removeSpells();
        spells.clear();
    }

    @AfterEach
    void closeConnection() {
        connection.close();
    }


    @Test
    void testBiddingSpell() {
        // to silence spotbugs.
        assertEquals(0, model.getRound());
        spells.add(biddingSpell);
        player.addSpell(spells, 1);
        biddingSpell.cast(player, connection);
        assertTrue(biddingSpell.isCast());
        assertTrue(player.isCursed(BidType.FOOD, 1));
    }

    @Test
    void testBuffSpell() {
        final Adventurer adv = new Adventurer(0, 1, 1, 1, 1, 1, false);
        player.getDungeon().addAdventurer(adv);
        spells.add(buffSpell);
        player.addSpell(spells, 1);
        buffSpell.cast(player, connection);
        assertEquals(1, adv.getHealthBuff());
        assertEquals(2, adv.getHealValue());
        assertEquals(2, adv.getDefuseValue());
    }

    @Test
    void testResourceSpell() {
        spells.add(resourceSpell);
        player.addSpell(spells, 1);
        resourceSpell.cast(player, connection);
        assertEquals(4, player.getFood());
        assertEquals(4, player.getGold());
    }

    @Test
    void testRoomSpell() {
        spells.add(roomSpell);
        player.addSpell(spells, 1);
        roomSpell.cast(player, connection);
        assertTrue(player.getRoomsCursedInRounds().contains(0));
    }


}
