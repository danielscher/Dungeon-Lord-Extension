package de.unisaarland.cs.se.selab.model;

/**
 * Class for adventurers.
 */
public class Adventurer {

    private final int id;

    private final int magicPoints;
    private final int difficulty;
    private final int healValue;
    private final int defuseValue;
    private final boolean charge;
    private final int maxHealthPoints;
    private int currentHealthPoints;
    private boolean defeated;

    private int healthBuff;

    private int healBuff;
    private int defuseBuff;

    public Adventurer(final int id, final int difficulty, final int healthPoints,
            int magicPoints, final int healValue, final int defuseValue, final boolean charge) {
        this.id = id;
        this.difficulty = difficulty;
        this.maxHealthPoints = healthPoints;
        this.magicPoints = magicPoints;
        this.currentHealthPoints = this.maxHealthPoints;
        this.healValue = healValue;
        this.defuseValue = defuseValue;
        this.charge = charge;
        this.defeated = false;
    }

    public int getId() {
        return id;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getHealValue() {
        return (healValue + healBuff);
    }

    public int getDefuseValue() {
        return (defuseValue + defuseBuff);
    }

    /**
     * Heal an adventurer by the given amount.
     * <p>
     * This performs a bounds-check for the adventurers maximal health points before healing.
     * </p>
     *
     * @param amount the amount to heal
     * @return the actual amount of health points the player was healed (after bounds-check)
     */
    public int heal(final int amount) {
        final int effectiveHeal = Math.min(this.maxHealthPoints - this.currentHealthPoints, amount);
        this.currentHealthPoints += effectiveHeal;
        return effectiveHeal;
    }

    /**
     * Damage an adventurer by the given amount.
     * <p>
     * This performs a bounds-check for the adventurers minimal health points before damaging and
     * sets the adventurer's defeated flag if the health points drop to 0.
     * </p>
     *
     * @param amount the amount of damage
     * @return the actual amount of health points the player was damaged (after bounds-check)
     */
    public int damage(int amount) {
        //first damage the buffed hp.
        if (amount >= healthBuff) {
            amount -= healBuff;
            healthBuff = 0;
        } else {
            healthBuff -= amount;
        }

        //damage un-buffed hp.
        final int effectiveDamage = Math.min(currentHealthPoints, amount);
        this.currentHealthPoints -= effectiveDamage;
        if (this.currentHealthPoints <= 0) {
            this.defeated = true;
        }
        return effectiveDamage;
    }

    public boolean isDefeated() {
        return this.defeated;
    }

    public boolean isCharging() {
        return this.charge;
    }

    public int getMagicPoints() {
        return magicPoints;
    }

    public void debuff() {
        //reset health buff if surpassed maxHealth
        if (currentHealthPoints + healthBuff > maxHealthPoints) {
            this.healthBuff = 0;
        }
        healthBuff = 0;
        defuseBuff = 0;
    }

    public void setHealthBuff(int healthBuff) {
        this.healthBuff = healthBuff;
    }

    public void setHealBuff(int healBuff) {
        if (healValue > 0) {
            this.healBuff = healBuff;
        }
    }

    public void setDefuseBuff(int defuseBuff) {
        if (defuseValue > 0) {
            this.defuseBuff = defuseBuff;
        }
    }
}
