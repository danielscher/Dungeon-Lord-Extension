package de.unisaarland.cs.se.selab.model.spells;

public class ResourceSpell extends Spell {

    final int food;
    final int gold;

    public ResourceSpell(final int id, String triggerBid,
            final int triggerSlot, final int food, final int gold) {
        super(id, triggerBid, triggerSlot);
        this.food = food;
        this.gold = gold;

    }


    @Override
    public void cast() {

    }
}
