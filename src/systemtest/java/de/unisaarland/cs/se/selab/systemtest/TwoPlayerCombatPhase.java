package de.unisaarland.cs.se.selab.systemtest;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.systemtest.api.SystemTest;
import de.unisaarland.cs.se.selab.systemtest.api.Utils;
import java.util.Set;

public class TwoPlayerCombatPhase extends SystemTest {

    public TwoPlayerCombatPhase() {
        super(TwoPlayerCombatPhase.class, false);
    }

    @Override
    protected String createConfig() {
        return Utils.loadResource(TwoPlayerBuildingPhase.class, "config_destroyRoom.json");
    }

    @Override
    protected long createSeed() {
        return 42;
    }

    @Override
    protected Set<Integer> createSockets() {
        return Set.of(0, 1);
    }

    private void buildingPhase() throws TimeoutException {
        register2Players();
        roundOneBidding();
        roundTwoBidding();
        roundThreeBidding();
        roundFourBidding();
    }


    private void register2Players() throws TimeoutException {
        //register 2 players
        final String config = this.createConfig();
        this.sendRegister(0, "0");
        this.assertConfig(0, config);
        this.sendRegister(1, "1");
        this.assertConfig(1, config);

        this.assertGameStarted(0);
        this.assertGameStarted(1);

        this.assertPlayer(0, "0", 0);
        this.assertPlayer(1, "0", 0);
        this.assertPlayer(0, "1", 1);
        this.assertPlayer(1, "1", 1);

        this.assertNextYear(0, 1);
        this.assertNextYear(1, 1);
        this.assertNextRound(0, 1);
        this.assertNextRound(1, 1);
    }

    /////////////////////////////////////ROUND 1 BUILDING///////////////////////////////////////////
    private void roundOneBidding() throws TimeoutException {
        drawingCards1();
        placeBidFoodNicenessImp();
        evalBids();
        bidRetrivalAdvArrival();
    }

    private void drawingCards1() throws TimeoutException {

        this.assertAdventurerDrawn(0, 29);
        this.assertAdventurerDrawn(1, 29);
        this.assertAdventurerDrawn(0, 23);
        this.assertAdventurerDrawn(1, 23);

        this.assertMonsterDrawn(0, 23);
        this.assertMonsterDrawn(1, 23);
        this.assertMonsterDrawn(0, 13);
        this.assertMonsterDrawn(1, 13);
        this.assertMonsterDrawn(0, 9);
        this.assertMonsterDrawn(1, 9);

        this.assertRoomDrawn(0, 5);
        this.assertRoomDrawn(1, 5);
        this.assertRoomDrawn(0, 4);
        this.assertRoomDrawn(1, 4);

        this.assertSpellDrawn(0, 19);
        this.assertSpellDrawn(1, 19);
        this.assertSpellDrawn(0, 23);
        this.assertSpellDrawn(1, 23);
        this.assertSpellDrawn(0, 7);
        this.assertSpellDrawn(1, 7);

        this.assertBiddingStarted(0);
        this.assertBiddingStarted(1);
        this.assertActNow(0);
        this.assertActNow(1);
    }

    private void placeBidFoodNicenessImp() throws TimeoutException {

        /////////////FIRST PLAYER///////////////////
        // slot 1
        sendPlaceBid(0, BidType.FOOD, 1);
        assertBidPlaced(0, BidType.FOOD, 0, 1);
        assertBidPlaced(1, BidType.FOOD, 0, 1);
        assertActNow(0);

        // slot 2
        sendPlaceBid(0, BidType.NICENESS, 2);
        assertBidPlaced(0, BidType.NICENESS, 0, 2);
        assertBidPlaced(1, BidType.NICENESS, 0, 2);
        assertActNow(0);

        // slot 3
        sendPlaceBid(0, BidType.IMPS, 3);
        assertBidPlaced(0, BidType.IMPS, 0, 3);
        assertBidPlaced(1, BidType.IMPS, 0, 3);

        /////////////SECOND PLAYER///////////////////
        // slot 1
        sendPlaceBid(1, BidType.FOOD, 1);
        assertBidPlaced(0, BidType.FOOD, 1, 1);
        assertBidPlaced(1, BidType.FOOD, 1, 1);
        assertActNow(1);

        //slot 2
        sendPlaceBid(1, BidType.NICENESS, 2);
        assertBidPlaced(0, BidType.NICENESS, 1, 2);
        assertBidPlaced(1, BidType.NICENESS, 1, 2);
        assertActNow(1);

        //slot 3
        sendPlaceBid(1, BidType.IMPS, 3);
        assertBidPlaced(0, BidType.IMPS, 1, 3);
        assertBidPlaced(1, BidType.IMPS, 1, 3);
    }

    private void evalBids() throws TimeoutException {
        // slot 1 - FOOD
        assertSpellUnlocked(0, 19, 0);
        assertSpellUnlocked(1, 19, 0);
        assertGoldChanged(0, -1, 0);
        assertGoldChanged(1, -1, 0);
        assertFoodChanged(0, 2, 0);
        assertFoodChanged(1, 2, 0);

        // slot 2 - FOOD
        assertEvilnessChanged(0, 1, 1);
        assertEvilnessChanged(1, 1, 1);
        assertFoodChanged(0, 3, 1);
        assertFoodChanged(1, 3, 1);

        // slot 1 - NICENESS
        assertSpellUnlocked(0, 23, 0);
        assertSpellUnlocked(1, 23, 0);
        assertEvilnessChanged(0, -1, 0);
        assertEvilnessChanged(1, -1, 0);

        // slot 2 - NICENESS
        assertEvilnessChanged(0, -2, 1);
        assertEvilnessChanged(1, -2, 1);

        // slot 1 - IMPS
        assertFoodChanged(0, -1, 0);
        assertFoodChanged(1, -1, 0);
        assertImpsChanged(0, 1, 0);
        assertImpsChanged(1, 1, 0);

        // slot 2 - IMPS
        assertFoodChanged(0, -2, 1);
        assertFoodChanged(1, -2, 1);
        assertImpsChanged(0, 2, 1);
        assertImpsChanged(1, 2, 1);

    }

    private void bidRetrivalAdvArrival() throws TimeoutException {
        assertBidRetrieved(0, BidType.FOOD, 0);
        assertBidRetrieved(1, BidType.FOOD, 0);
        assertBidRetrieved(0, BidType.FOOD, 1);
        assertBidRetrieved(1, BidType.FOOD, 1);

        assertAdventurerArrived(0, 29, 0);
        assertAdventurerArrived(1, 29, 0);
        assertAdventurerArrived(0, 23, 1);
        assertAdventurerArrived(1, 23, 1);

        assertNextRound(0, 2);
        assertNextRound(1, 2);
    }

    /////////////////////////////////////ROUND 2 BUILDING///////////////////////////////////////////

    private void roundTwoBidding() throws TimeoutException {
        drawingCards2();
        placeBidFoodTunnelGold();
        evalBids2();
        bidRetrivalAdvArrival2();
    }

    private void drawingCards2() throws TimeoutException {
        this.assertAdventurerDrawn(0, 2);
        this.assertAdventurerDrawn(1, 2);
        this.assertAdventurerDrawn(0, 0);
        this.assertAdventurerDrawn(1, 0);

        this.assertMonsterDrawn(0, 7);
        this.assertMonsterDrawn(1, 7);
        this.assertMonsterDrawn(0, 22);
        this.assertMonsterDrawn(1, 22);
        this.assertMonsterDrawn(0, 1);
        this.assertMonsterDrawn(1, 1);

        this.assertRoomDrawn(0, 8);
        this.assertRoomDrawn(1, 8);
        this.assertRoomDrawn(0, 15);
        this.assertRoomDrawn(1, 15);

        this.assertSpellDrawn(0, 10);
        this.assertSpellDrawn(1, 10);
        this.assertSpellDrawn(0, 18);
        this.assertSpellDrawn(1, 18);
        this.assertSpellDrawn(0, 17);
        this.assertSpellDrawn(1, 17);

        this.assertBiddingStarted(0);
        this.assertBiddingStarted(1);
        this.assertActNow(0);
        this.assertActNow(1);
    }

    private void placeBidFoodTunnelGold() throws TimeoutException {
        /////////////FIRST PLAYER///////////////////
        // slot 1
        sendPlaceBid(0, BidType.FOOD, 1);
        assertBidPlaced(0, BidType.FOOD, 0, 1);
        assertBidPlaced(1, BidType.FOOD, 0, 1);
        assertActNow(0);

        // slot 2
        sendPlaceBid(0, BidType.TUNNEL, 2);
        assertBidPlaced(0, BidType.TUNNEL, 0, 2);
        assertBidPlaced(1, BidType.TUNNEL, 0, 2);
        assertActNow(0);

        // slot 3
        sendPlaceBid(0, BidType.GOLD, 3);
        assertBidPlaced(0, BidType.GOLD, 0, 3);
        assertBidPlaced(1, BidType.GOLD, 0, 3);

        /////////////SECOND PLAYER///////////////////
        // slot 1
        sendPlaceBid(1, BidType.FOOD, 1);
        assertBidPlaced(0, BidType.FOOD, 1, 1);
        assertBidPlaced(1, BidType.FOOD, 1, 1);
        assertActNow(1);

        //slot 2
        sendPlaceBid(1, BidType.TUNNEL, 2);
        assertBidPlaced(0, BidType.TUNNEL, 1, 2);
        assertBidPlaced(1, BidType.TUNNEL, 1, 2);
        assertActNow(1);

        //slot 3
        sendPlaceBid(1, BidType.GOLD, 3);
        assertBidPlaced(0, BidType.GOLD, 1, 3);
        assertBidPlaced(1, BidType.GOLD, 1, 3);
    }

    private void evalBids2() throws TimeoutException {
        // slot 1 - FOOD
        assertGoldChanged(0, -1, 1);
        assertGoldChanged(1, -1, 1);
        assertFoodChanged(0, 2, 1);
        assertFoodChanged(1, 2, 1);

        // slot 2 - FOOD
        assertSpellUnlocked(0, 18, 0);
        assertSpellUnlocked(1, 18, 0);
        assertEvilnessChanged(0, 1, 0);
        assertEvilnessChanged(1, 1, 0);
        assertFoodChanged(0, 3, 0);
        assertFoodChanged(1, 3, 0);

        // slot 1 - TUNNEL
        // digging 1,0; 1,1
        assertDigTunnel(1);
        assertActNow(1);
        sendDigTunnel(1, 1, 0);
        assertImpsChanged(0, -1, 1);
        assertImpsChanged(1, -1, 1);
        assertTunnelDug(0, 1, 1, 0);
        assertTunnelDug(1, 1, 1, 0);
        assertActNow(1);
        sendDigTunnel(1, 1, 1);
        assertImpsChanged(0, -1, 1);
        assertImpsChanged(1, -1, 1);
        assertTunnelDug(0, 1, 1, 1);
        assertTunnelDug(1, 1, 1, 1);

        // slot 2 - TUNNEL
        // digging 1,0; 1,1; 1,2
        assertDigTunnel(0);
        assertActNow(0);
        sendCastCounterSpell(0);
        assertActionFailed(0);
        assertActNow(0);
        sendDigTunnel(0, 1, 0);
        assertImpsChanged(0, -1, 0);
        assertImpsChanged(1, -1, 0);
        assertTunnelDug(0, 0, 1, 0);
        assertTunnelDug(1, 0, 1, 0);
        assertActNow(0);
        sendDigTunnel(0, 1, 1);
        assertImpsChanged(0, -1, 0);
        assertImpsChanged(1, -1, 0);
        assertTunnelDug(0, 0, 1, 1);
        assertTunnelDug(1, 0, 1, 1);
        assertActNow(0);
        sendDigTunnel(0, 1, 2);
        assertImpsChanged(0, -1, 0);
        assertImpsChanged(1, -1, 0);
        assertTunnelDug(0, 0, 1, 2);
        assertTunnelDug(1, 0, 1, 2);

        // slot 1 - GOLD
        assertSpellUnlocked(0, 10, 1);
        assertSpellUnlocked(1, 10, 1);
        assertImpsChanged(0, -2, 1);
        assertImpsChanged(1, -2, 1);

        // slot 2 - GOLD
        assertImpsChanged(0, -1, 0);
        assertImpsChanged(1, -1, 0);


    }

    private void bidRetrivalAdvArrival2() throws TimeoutException {

        assertBidRetrieved(0, BidType.NICENESS, 0);
        assertBidRetrieved(1, BidType.NICENESS, 0);
        assertBidRetrieved(0, BidType.IMPS, 0);
        assertBidRetrieved(1, BidType.IMPS, 0);
        assertBidRetrieved(0, BidType.FOOD, 0);
        assertBidRetrieved(1, BidType.FOOD, 0);

        assertBidRetrieved(0, BidType.NICENESS, 1);
        assertBidRetrieved(1, BidType.NICENESS, 1);
        assertBidRetrieved(0, BidType.IMPS, 1);
        assertBidRetrieved(1, BidType.IMPS, 1);
        assertBidRetrieved(0, BidType.FOOD, 1);
        assertBidRetrieved(1, BidType.FOOD, 1);

        // return imps
        assertImpsChanged(0, 4, 0);
        assertImpsChanged(1, 4, 0);
        assertGoldChanged(0, 1, 0);
        assertGoldChanged(1, 1, 0);

        assertImpsChanged(0, 4, 1);
        assertImpsChanged(1, 4, 1);
        assertGoldChanged(0, 2, 1);
        assertGoldChanged(1, 2, 1);

        assertAdventurerArrived(0, 0, 1);
        assertAdventurerArrived(1, 0, 1);
        assertAdventurerArrived(0, 2, 0);
        assertAdventurerArrived(1, 2, 0);

        assertNextRound(0, 3);
        assertNextRound(1, 3);
    }

    /////////////////////////////////////ROUND 3 BUILDING///////////////////////////////////////////

    private void roundThreeBidding() throws TimeoutException {
        drawingCards3();
        placeBidFoodRoomMonster();
        evalBids3();
        bidRetrivalAdvArrival3();
    }

    private void drawingCards3() throws TimeoutException {
        this.assertAdventurerDrawn(0, 18);
        this.assertAdventurerDrawn(1, 18);
        this.assertAdventurerDrawn(0, 11);
        this.assertAdventurerDrawn(1, 11);

        this.assertMonsterDrawn(0, 14);
        this.assertMonsterDrawn(1, 14);
        this.assertMonsterDrawn(0, 3);
        this.assertMonsterDrawn(1, 3);
        this.assertMonsterDrawn(0, 20);
        this.assertMonsterDrawn(1, 20);

        this.assertRoomDrawn(0, 0);
        this.assertRoomDrawn(1, 0);
        this.assertRoomDrawn(0, 10);
        this.assertRoomDrawn(1, 10);

        this.assertSpellDrawn(0, 24);
        this.assertSpellDrawn(1, 24);
        this.assertSpellDrawn(0, 16);
        this.assertSpellDrawn(1, 16);
        this.assertSpellDrawn(0, 0);
        this.assertSpellDrawn(1, 0);

        this.assertBiddingStarted(0);
        this.assertBiddingStarted(1);
        this.assertActNow(0);
        this.assertActNow(1);
    }

    private void placeBidFoodRoomMonster() throws TimeoutException {
        /////////////FIRST PLAYER///////////////////
        // slot 1
        sendPlaceBid(0, BidType.FOOD, 1);
        assertBidPlaced(0, BidType.FOOD, 0, 1);
        assertBidPlaced(1, BidType.FOOD, 0, 1);
        assertActNow(0);

        // slot 2
        sendPlaceBid(0, BidType.ROOM, 2);
        assertBidPlaced(0, BidType.ROOM, 0, 2);
        assertBidPlaced(1, BidType.ROOM, 0, 2);
        assertActNow(0);

        // slot 3
        sendPlaceBid(0, BidType.MONSTER, 3);
        assertBidPlaced(0, BidType.MONSTER, 0, 3);
        assertBidPlaced(1, BidType.MONSTER, 0, 3);

        /////////////SECOND PLAYER///////////////////
        // slot 1
        sendPlaceBid(1, BidType.FOOD, 1);
        assertBidPlaced(0, BidType.FOOD, 1, 1);
        assertBidPlaced(1, BidType.FOOD, 1, 1);
        assertActNow(1);

        //slot 2
        sendPlaceBid(1, BidType.ROOM, 2);
        assertBidPlaced(0, BidType.ROOM, 1, 2);
        assertBidPlaced(1, BidType.ROOM, 1, 2);
        assertActNow(1);

        //slot 3
        sendPlaceBid(1, BidType.MONSTER, 3);
        assertBidPlaced(0, BidType.MONSTER, 1, 3);
        assertBidPlaced(1, BidType.MONSTER, 1, 3);
    }

    private void evalBids3() throws TimeoutException {

        assertGoldChanged(0, -1, 0);
        assertGoldChanged(1, -1, 0);
        assertFoodChanged(0, 2, 0);
        assertFoodChanged(1, 2, 0);

        // slot 2 - FOOD
        assertSpellUnlocked(0, 0, 1);
        assertSpellUnlocked(1, 0, 1);
        assertEvilnessChanged(0, 1, 1);
        assertEvilnessChanged(1, 1, 1);
        assertFoodChanged(0, 3, 1);
        assertFoodChanged(1, 3, 1);

        //slot 1 - MONSTER
        assertSelectMonster(0);
        assertActNow(0);
        sendHireMonster(0, 20);
        assertFoodChanged(0, -3, 0);
        assertFoodChanged(1, -3, 0);
        assertMonsterHired(0, 20, 0);
        assertMonsterHired(1, 20, 0);

        //slot 2 - MONSTER
        assertSelectMonster(1);
        assertActNow(1);
        sendHireMonster(0, 3);
        assertActionFailed(0);
        sendHireMonster(1, 3);
        assertFoodChanged(0, -2, 1);
        assertFoodChanged(1, -2, 1);
        assertEvilnessChanged(0, 1, 1);
        assertEvilnessChanged(1, 1, 1);
        assertMonsterHired(0, 3, 1);
        assertMonsterHired(1, 3, 1);

        // slot 1 - ROOM
        assertGoldChanged(0, -1, 0);
        assertGoldChanged(1, -1, 0);
        assertPlaceRoom(0);
        assertActNow(0);
        sendBuildRoom(0, 1, 0, 0);
        assertActionFailed(0);
        assertActNow(0);
        sendBuildRoom(0, 1, 1, -1);
        assertActionFailed(0);
        assertActNow(0);
        sendBuildRoom(0, 1, 1, 0);
        assertRoomBuilt(0, 0, 0, 1, 1);
        assertRoomBuilt(1, 0, 0, 1, 1);

        // slot 2 - ROOM
        assertGoldChanged(0, -1, 1);
        assertGoldChanged(1, -1, 1);
        assertPlaceRoom(1);
        assertActNow(1);
        sendBuildRoom(1, 1, 1, 0);
        assertActionFailed(1);
        assertActNow(1);
        sendBuildRoom(1, 1, 1, 10);
        assertRoomBuilt(0, 1, 10, 1, 1);
        assertRoomBuilt(1, 1, 10, 1, 1);
    }

    private void bidRetrivalAdvArrival3() throws TimeoutException {
        assertBidRetrieved(0, BidType.TUNNEL, 0);
        assertBidRetrieved(1, BidType.TUNNEL, 0);
        assertBidRetrieved(0, BidType.GOLD, 0);
        assertBidRetrieved(1, BidType.GOLD, 0);
        assertBidRetrieved(0, BidType.FOOD, 0);
        assertBidRetrieved(1, BidType.FOOD, 0);

        assertBidRetrieved(0, BidType.TUNNEL, 1);
        assertBidRetrieved(1, BidType.TUNNEL, 1);
        assertBidRetrieved(0, BidType.GOLD, 1);
        assertBidRetrieved(1, BidType.GOLD, 1);
        assertBidRetrieved(0, BidType.FOOD, 1);
        assertBidRetrieved(1, BidType.FOOD, 1);

        assertAdventurerArrived(0, 18, 0);
        assertAdventurerArrived(1, 18, 0);
        assertAdventurerArrived(0, 11, 1);
        assertAdventurerArrived(1, 11, 1);

        assertNextRound(0, 4);
        assertNextRound(1, 4);
    }

    /////////////////////////////////////ROUND 4 BUILDING///////////////////////////////////////////

    private void roundFourBidding() throws TimeoutException {
        drawingCards4();
        placeBidFoodTunnelRoom();
        evalBids4();
        bidRetrivalAdvArrival4();
    }

    private void drawingCards4() throws TimeoutException {

        this.assertMonsterDrawn(0, 6);
        this.assertMonsterDrawn(1, 6);
        this.assertMonsterDrawn(0, 11);
        this.assertMonsterDrawn(1, 11);
        this.assertMonsterDrawn(0, 16);
        this.assertMonsterDrawn(1, 16);

        this.assertRoomDrawn(0, 2);
        this.assertRoomDrawn(1, 2);
        this.assertRoomDrawn(0, 9);
        this.assertRoomDrawn(1, 9);

        this.assertSpellDrawn(0, 2);
        this.assertSpellDrawn(1, 2);
        this.assertSpellDrawn(0, 11);
        this.assertSpellDrawn(1, 11);
        this.assertSpellDrawn(0, 20);
        this.assertSpellDrawn(1, 20);

        this.assertBiddingStarted(0);
        this.assertBiddingStarted(1);
        this.assertActNow(0);
        this.assertActNow(1);

    }

    private void placeBidFoodTunnelRoom() throws TimeoutException {
        /////////////FIRST PLAYER///////////////////
        // slot 1
        sendCastCounterSpell(0);
        assertActionFailed(0);
        assertActNow(0);
        sendPlaceBid(0, BidType.FOOD, 1);
        assertBidPlaced(0, BidType.FOOD, 0, 1);
        assertBidPlaced(1, BidType.FOOD, 0, 1);
        assertActNow(0);

        // slot 2
        sendPlaceBid(0, BidType.TUNNEL, 2);
        assertBidPlaced(0, BidType.TUNNEL, 0, 2);
        assertBidPlaced(1, BidType.TUNNEL, 0, 2);
        assertActNow(0);

        // slot 3
        sendPlaceBid(0, BidType.NICENESS, 3);
        assertBidPlaced(0, BidType.NICENESS, 0, 3);
        assertBidPlaced(1, BidType.NICENESS, 0, 3);

        /////////////SECOND PLAYER///////////////////
        // slot 1
        sendPlaceBid(1, BidType.FOOD, 1);
        assertBidPlaced(0, BidType.FOOD, 1, 1);
        assertBidPlaced(1, BidType.FOOD, 1, 1);
        assertActNow(1);

        //slot 2
        sendPlaceBid(1, BidType.TUNNEL, 2);
        assertBidPlaced(0, BidType.TUNNEL, 1, 2);
        assertBidPlaced(1, BidType.TUNNEL, 1, 2);
        assertActNow(1);

        //slot 3
        sendPlaceBid(1, BidType.NICENESS, 3);
        assertBidPlaced(0, BidType.NICENESS, 1, 3);
        assertBidPlaced(1, BidType.NICENESS, 1, 3);
    }

    private void evalBids4() throws TimeoutException {
        // slot 1 - FOOD
        assertGoldChanged(0, -1, 1);
        assertGoldChanged(1, -1, 1);
        assertFoodChanged(0, 2, 1);
        assertFoodChanged(1, 2, 1);

        // slot 2 - FOOD
        assertEvilnessChanged(0, 1, 0);
        assertEvilnessChanged(1, 1, 0);
        assertFoodChanged(0, 3, 0);
        assertFoodChanged(1, 3, 0);

        // slot 1 - NICENESS
        assertSpellUnlocked(0, 11, 1);
        assertSpellUnlocked(1, 11, 1);
        assertSpellUnlocked(0, 20, 1);
        assertSpellUnlocked(1, 20, 1);
        assertEvilnessChanged(0, -1, 1);
        assertEvilnessChanged(1, -1, 1);

        // slot 2 - NICENESS
        assertEvilnessChanged(0, -2, 0);
        assertEvilnessChanged(1, -2, 0);

        // slot 1 - TUNNEL
        // digging 1,2; 2,2
        assertDigTunnel(1);
        assertActNow(1);
        sendDigTunnel(1, 1, 0);
        assertActionFailed(1);
        assertActNow(1);
        sendDigTunnel(1, 1, 2);
        assertImpsChanged(0, -1, 1);
        assertImpsChanged(1, -1, 1);
        assertTunnelDug(0, 1, 1, 2);
        assertTunnelDug(1, 1, 1, 2);
        assertActNow(1);
        sendDigTunnel(1, 2, 2);
        assertImpsChanged(0, -1, 1);
        assertImpsChanged(1, -1, 1);
        assertTunnelDug(0, 1, 2, 2);
        assertTunnelDug(1, 1, 2, 2);

        // slot 2 - TUNNEL
        // digging 2,2;2,3;3,3
        assertDigTunnel(0);
        assertActNow(0);
        sendDigTunnel(0, 2, 2);
        assertImpsChanged(0, -1, 0);
        assertImpsChanged(1, -1, 0);
        assertTunnelDug(0, 0, 2, 2);
        assertTunnelDug(1, 0, 2, 2);
        assertActNow(0);
        sendDigTunnel(0, 2, 3);
        assertImpsChanged(0, -1, 0);
        assertImpsChanged(1, -1, 0);
        assertTunnelDug(0, 0, 2, 3);
        assertTunnelDug(1, 0, 2, 3);
        assertCounterSpellFound(0, 0);
        assertCounterSpellFound(1, 0);
        assertActNow(0);
        sendDigTunnel(0, 3, 3);
        assertImpsChanged(0, -1, 0);
        assertImpsChanged(1, -1, 0);
        assertTunnelDug(0, 0, 3, 3);
        assertTunnelDug(1, 0, 3, 3);
    }

    private void bidRetrivalAdvArrival4() throws TimeoutException {
        assertBidRetrieved(0, BidType.ROOM, 0);
        assertBidRetrieved(1, BidType.ROOM, 0);
        assertBidRetrieved(0, BidType.MONSTER, 0);
        assertBidRetrieved(1, BidType.MONSTER, 0);
        assertBidRetrieved(0, BidType.FOOD, 0);
        assertBidRetrieved(1, BidType.FOOD, 0);

        assertBidRetrieved(0, BidType.ROOM, 1);
        assertBidRetrieved(1, BidType.ROOM, 1);
        assertBidRetrieved(0, BidType.MONSTER, 1);
        assertBidRetrieved(1, BidType.MONSTER, 1);
        assertBidRetrieved(0, BidType.FOOD, 1);
        assertBidRetrieved(1, BidType.FOOD, 1);

        // return imps
        assertImpsChanged(0, 3, 0);
        assertImpsChanged(1, 3, 0);
        assertImpsChanged(0, 2, 1);
        assertImpsChanged(1, 2, 1);

        assertNextRound(0, 1);
        assertNextRound(1, 1);

    }


    private void leave() {
        sendLeave(0);
        sendLeave(1);
    }

    @Override
    protected void run() throws TimeoutException, AssertionError {

        buildingPhase();
        combatRoundOnePlayer0();
        leave();


    }

    private void combatRoundOnePlayer0() throws TimeoutException {
        assertSetBattleGround(0);
        assertActNow(0);
        sendBattleGround(0, 0, 0);
        assertBattleGroundSet(0, 0, 0, 0);
        assertBattleGroundSet(1, 0, 0, 0);
        assertDefendYourself(0);
        assertActNow(0);
        sendMonster(0, 20);
        assertMonsterPlaced(0, 20, 0);
        assertMonsterPlaced(1, 20, 0);
        assertActNow(0);
        sendTrap(0, 0);
        assertActionFailed(0);
        assertActNow(0);
        sendEndTurn(0);
        assertSpellCast(0, 19, 0);
        assertSpellCast(1, 19, 0);
        assertCounterSpell(0);
        assertActNow(0);
        sendEndTurn(0);
        assertRoomRemoved(0, 0, 0);
        assertRoomRemoved(1, 0, 0);
        assertSpellCast(0, 23, 0);
        assertSpellCast(1, 23, 0);
        assertCounterSpell(0);
        assertActNow(0);
        sendActivateRoom(0, 0);
        assertActionFailed(0);
        assertActNow(0);
        sendEndTurn(0);
        assertGoldChanged(0, -1, 0);
        assertGoldChanged(1, -1, 0);
        //monster 2 damage multi
        assertAdventurerDamaged(0, 29, 2);
        assertAdventurerDamaged(1, 29, 2);
        assertAdventurerDamaged(0, 2, 2);
        assertAdventurerDamaged(1, 2, 2);
        assertAdventurerDamaged(0, 18, 2);
        assertAdventurerDamaged(1, 18, 2);
        // fatigue
        assertAdventurerDamaged(0, 29, 2);
        assertAdventurerDamaged(1, 29, 2);
        assertAdventurerImprisoned(0, 2);
        assertAdventurerImprisoned(1, 2);
        assertArchmageArrived(0, 0);
        assertArchmageArrived(1, 0);
        assertMonsterRemoved(0, 20, 0);
        assertMonsterRemoved(1, 20, 0);
        // reduced fatigue
        assertAdventurerDamaged(0, 18, 1);
        assertAdventurerDamaged(1, 18, 1);
        assertTunnelConquered(0, 29, 0, 0);
        assertTunnelConquered(1, 29, 0, 0);
        assertEvilnessChanged(0, -1, 0);
        assertEvilnessChanged(1, -1, 0);
        assertAdventurerHealed(0, 2, 29, 29);
        assertAdventurerHealed(1, 2, 29, 29);
    }
}
