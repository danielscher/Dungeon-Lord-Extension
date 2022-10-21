package de.unisaarland.cs.se.selab.systemtest;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.comm.TimeoutException;
import de.unisaarland.cs.se.selab.systemtest.api.SystemTest;
import de.unisaarland.cs.se.selab.systemtest.api.Utils;
import java.util.Set;

public class TwoPlayerBuildingPhase extends SystemTest {



    public TwoPlayerBuildingPhase() {
        super(TwoPlayerBuildingPhase.class, false);
    }


    @Override
    protected String createConfig() {
        return Utils.loadResource(TwoPlayerBuildingPhase.class, "config_fullgameplus.json");
    }

    @Override
    protected long createSeed() {
        return 42;
    }

    @Override
    protected Set<Integer> createSockets() {
        return Set.of(0, 1);
    }

    @Override
    protected void run() throws TimeoutException, AssertionError {

        register2Players();
        drawingCards();
        placeBidFoodNicenessImp();
        evalBids();
        bidRetrivalAdvArrival();
        leave();
    }

    private void register2Players() throws TimeoutException {
        //register 2 players
        final String config = this.createConfig();
        this.sendRegister(0, "0");
        this.assertConfig(0, config);
        //this.sendCastCounterSpell(0);
        //this.assertActionFailed(0);
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

    private void drawingCards() throws TimeoutException {

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

    private void leave() {
        sendLeave(0);
        sendLeave(1);
    }

}


