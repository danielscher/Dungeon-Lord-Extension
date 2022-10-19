package de.unisaarland.cs.se.selab.model.spells;

public enum SpellType {

    BIDDING,
    BUFF,
    RESOURCE,
    ROOM,
    STRUCTURE;

    public enum STRUCTURE_EFFECT {
        CONQUER,
        DESTROY
    }

}
