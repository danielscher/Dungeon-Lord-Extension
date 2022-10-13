package de.unisaarland.cs.se.selab.model.spells;

public class BuffSpell extends Spell {


    public BuffSpell(final int id, final String bidType, final int slot, final int healthBuff,
            final int healBuff, final int defuseBuff) {
        super(id,bidType,slot);
    }

    @Override
    public void cast() {

    }
}
