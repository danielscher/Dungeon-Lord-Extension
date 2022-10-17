package de.unisaarland.cs.se.selab.model.spells;


public class StructureSpell extends Spell{

    final boolean conquer;

    public StructureSpell(int id, String triggerBid,
            int triggerSlot, String structureEffect) {
        super(id, triggerBid, triggerSlot);
        this.conquer = structureEffect.equals("CONQUER");
    }

    @Override
    public void cast() {

    }
}
