package de.unisaarland.cs.se.selab.model;

/**
 * Superclass for everything that can be placed as a defense.
 */
public abstract class DefensiveMeasure {

    private final int id;
    private final int damage;
    private final AttackStrategy attackStrategy;
    private int target;

    private boolean ignore;

    protected DefensiveMeasure(final int id, final int damage,
            final AttackStrategy attackStrategy) {
        this.id = id;
        this.damage = damage;
        this.attackStrategy = attackStrategy;
        // no target is set by default
        this.target = 0;
    }

    public int getId() {
        return id;
    }

    public int getDamage() {
        if (ignore) {
            return 0;
        }
        return damage;
    }

    public AttackStrategy getAttackStrategy() {
        return attackStrategy;
    }

    public boolean hasTarget() {
        return this.target > 0;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(final int target) {
        this.target = target;
    }


    public void setIgnore(final boolean ignore) {
        this.ignore = ignore;
    }


}
