package de.unisaarland.cs.se.selab.model.spells;


import de.unisaarland.cs.se.selab.ConnectionWrapper;
import de.unisaarland.cs.se.selab.model.Player;

public class BuffSpell extends Spell {

    final int healBuff;
    final int healthBuff;
    final int defuseBuff;


    public BuffSpell(final int id, final String bidType, final int slot, final int healthBuff,
            final int healBuff, final int defuseBuff) {
        super(id, bidType, slot, 2);
        this.healBuff = healBuff;
        this.healthBuff = healthBuff;
        this.defuseBuff = defuseBuff;

    }

    @Override
    public boolean cast(Player player, ConnectionWrapper connection, int advMagicPoints) {
        if (advMagicPoints >= cost) {
            player.getDungeon().getAllAdventurers().forEach(adv -> {
                adv.setHealBuff(healBuff);
                adv.setHealthBuff(healthBuff);
                adv.setDefuseBuff(defuseBuff);
            });
            player.curse();
        }
        return false;
    }
}
