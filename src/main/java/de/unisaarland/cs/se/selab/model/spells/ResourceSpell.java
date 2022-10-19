package de.unisaarland.cs.se.selab.model.spells;

import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Player;

public class ResourceSpell extends Spell {

    final int food;
    final int gold;

    public ResourceSpell(final int id, final String triggerBid,
            final int triggerSlot, final int food, final int gold) {
        super(id, triggerBid, triggerSlot, 1);
        this.food = food;
        this.gold = gold;

    }


    @Override
    public boolean cast(final Player player, final ConnectionWrapper connection) {
        changeFoodBySpell(player, connection, food);
        changeGoldBySpell(player, connection, gold);
        return false;
    }

    private void changeGoldBySpell(final Player player, final ConnectionWrapper connection,
            final int amount) {
        if (player.getGold() > 0 && amount > 0) {
            final int effectiveAmount = -Math.min(amount, player.getGold());
            player.changeGold(effectiveAmount);
            connection.sendGoldChanged(player.getId(), effectiveAmount);
        }
    }

    private void changeFoodBySpell(final Player player, final ConnectionWrapper connection,
            final int amount) {
        if (player.getFood() > 0 && amount > 0) {
            final int effectiveAmount = -Math.min(amount, player.getFood());
            player.changeFood(effectiveAmount);
            connection.sendFoodChanged(player.getId(), effectiveAmount);
        }
    }
}
