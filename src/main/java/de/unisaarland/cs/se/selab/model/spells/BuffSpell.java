package de.unisaarland.cs.se.selab.model.spells;


public class BuffSpell extends Spell {

    final int healBuff;
    final int healthBuff;
    final int defuseBuff;


    public BuffSpell(final int id, final String bidType, final int slot, final int healthBuff,
            final int healBuff, final int defuseBuff) {
        super(id, bidType, slot);
        this.healBuff = healBuff;
        this.healthBuff = healthBuff;
        this.defuseBuff = defuseBuff;

    }

    @Override
    public void cast() {

    }
}
