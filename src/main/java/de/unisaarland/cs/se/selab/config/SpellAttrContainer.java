package de.unisaarland.cs.se.selab.config;

public record SpellAttrContainer(int id, String spellType, String bidType, int slot, int food,
                                 int gold,
                                 String bidTypeBlocked, String structureEffect, int healthBuff,
                                 int healBuff, int defuseBuff) {


}
