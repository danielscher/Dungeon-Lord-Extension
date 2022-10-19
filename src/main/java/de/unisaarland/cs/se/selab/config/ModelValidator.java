package de.unisaarland.cs.se.selab.config;

import de.unisaarland.cs.se.selab.comm.BidType;
import de.unisaarland.cs.se.selab.model.AttackStrategy;
import de.unisaarland.cs.se.selab.model.Model;
import de.unisaarland.cs.se.selab.model.spells.SpellType;
import de.unisaarland.cs.se.selab.model.spells.SpellType.STRUCTUREEFFECT;
import de.unisaarland.cs.se.selab.state.BuildingState;
import java.util.HashSet;
import java.util.Set;

/**
 * The model validator checks all constraints before inserting elements into the model builder.
 * <p>
 * The model validator combines the builder pattern with delegation to automatically validate the
 * config before creating the model. It does so by delegating all function calls to another builder
 * similar to a decorator pattern.
 * </p>
 *
 * @param <M> the model class to be built
 */
public class ModelValidator<M> implements ModelBuilderInterface<M> {

    private final ModelBuilderInterface<M> builder;
    private final Set<Integer> monsterIds;
    private final Set<Integer> adventurerIds;
    private final Set<Integer> trapIds;
    private final Set<Integer> roomIds;
    private final Set<Integer> spellIds;

    private final Set<String> setProperties;
    private int years;
    private int maxPlayers;

    public ModelValidator(final ModelBuilderInterface<M> builder) {
        this.builder = builder;
        this.monsterIds = new HashSet<>();
        this.adventurerIds = new HashSet<>();
        this.trapIds = new HashSet<>();
        this.roomIds = new HashSet<>();
        this.setProperties = new HashSet<>();
        this.spellIds = new HashSet<>();
    }

    /////////////////////////////////////////////
    //            HELPER FUNCTIONS             //
    /////////////////////////////////////////////


    private void checkPropertiesSet(final String... properties) {
        for (final String property : properties) {
            if (!setProperties.contains(property)) {
                throw new IllegalArgumentException(
                        String.format("%s is required to be present.", property));
            }
        }
    }

    private void checkBounds(final String attrName, final int lowerBound, final int upperBound,
            final int value) {
        if (value < lowerBound || value > upperBound) {
            throw new IllegalArgumentException(
                    String.format("%s is required to be between %d and %d.", attrName, lowerBound,
                            upperBound));
        }
    }

    private void checkPositiveZero(final String attrName, final int value) {
        checkBounds(attrName, 0, Integer.MAX_VALUE, value);
    }

    private void checkPositiveNonZero(final String attrName, final int value) {
        checkBounds(attrName, 1, Integer.MAX_VALUE, value);
    }

    private void checkUniqueId(final String attrName, final Set<Integer> set, final int value) {
        if (set.contains(value)) {
            throw new IllegalArgumentException(
                    String.format("%sIds is required to be unique.", attrName));
        }
    }

    private <T> void checkLength(final String attrName, final Set<T> set, final int bound) {
        if (set.size() < bound) {
            throw new IllegalArgumentException(
                    String.format("%s is supposed to have at least %d elements.", attrName, bound));
        }
    }

    private void checkAttrIsNull(final String attr) {
        if (!"".equals(attr)) {
            throw new IllegalArgumentException(String.format("%s isn't legal in this spell", attr));
        }
    }

    private void checkAttrIsNull(final String attrName, final int attr) {
        if (attr != 0) {
            throw new IllegalArgumentException(
                    String.format("%s=%d is not legal in this spell", attrName, attr));
        }
    }

    private void checkResources(final int food, final int gold) {
        if (food <= 0 && gold <= 0) {
            throw new IllegalArgumentException(
                    "Resource spell missing both resources");
        }
        if (food > 0) {
            checkPositiveNonZero("Food", food);
            checkPositiveZero("gold", gold);

        }
        if (gold > 0) {
            checkPositiveNonZero("Gold", gold);
            checkPositiveZero("food", food);

        }
    }

    /**
     * checks if at least one value is present and that none of the values are negative.
     */
    private void checkBuffs(final int health, final int heal, final int defuse) {
        if (health <= 0 && heal <= 0 && defuse <= 0) {
            throw new IllegalArgumentException(
                    "Buff spell missing buff values");
        }
        if (health < 0 || heal < 0 || defuse < 0) {
            throw new IllegalArgumentException(
                    "Buff values cannot be negative");
        }
        if (health > 0) {
            checkPositiveNonZero("HealthBuff", health);
        }
        if (heal > 0) {
            checkPositiveNonZero("HealBuff", heal);
        }
        if (defuse > 0) {
            checkPositiveNonZero("DefuseBuff", defuse);
        }
    }

    /////////////////////////////////////////////
    //            BUILDER FUNCTIONS            //
    /////////////////////////////////////////////


    @Override
    public void addMonster(final int id, final int hunger, final int damage, final int evilness,
            final String attack) {
        checkUniqueId("Monster", this.monsterIds, id);
        checkPositiveZero("Monster " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveZero("Monster " + ModelBuilderInterface.CFG_MONSTER_HUNGER, hunger);
        checkPositiveNonZero("Monster " + ModelBuilderInterface.CFG_MONSTER_DAMAGE, damage);
        checkPositiveZero("Monster " + ModelBuilderInterface.CFG_MONSTER_EVILNESS, evilness);
        this.monsterIds.add(id);
        this.builder.addMonster(id, hunger, damage, evilness, attack);
    }

    @Override
    public void addAdventurer(final int id, final int difficulty, final int healthPoints,
            final int magicPoints,
            final int healValue,
            final int defuseValue, final boolean charge) {
        checkUniqueId("Adventurer", this.adventurerIds, id);
        checkPositiveZero("Adventurer " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveZero("Adventurer " + ModelBuilderInterface.CFG_ADV_DIFFICULTY, difficulty);
        checkPositiveNonZero("Adventurer " + ModelBuilderInterface.CFG_ADV_HEALTH_POINTS,
                healthPoints);
        checkPositiveZero("Adventurer" + ModelBuilderInterface.CFG_ADV_MAGIC_POINTS,
                magicPoints);
        checkPositiveZero("Adventurer " + ModelBuilderInterface.CFG_ADV_HEAL_VALUE, healValue);
        checkPositiveZero("Adventurer " + ModelBuilderInterface.CFG_ADV_DEFUSE_VALUE, defuseValue);
        this.adventurerIds.add(id);
        this.builder.addAdventurer(id, difficulty, healthPoints, magicPoints, healValue,
                defuseValue, charge);
    }

    @Override
    public void addTrap(final int id, final String attack, final int damage) {
        checkUniqueId("Trap", this.trapIds, id);
        checkPositiveZero("Trap " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveNonZero("Trap " + ModelBuilderInterface.CFG_TRAP_DAMAGE, damage);
        if (AttackStrategy.valueOf(attack) == AttackStrategy.TARGETED) {
            throw new IllegalArgumentException(
                    String.format("Trap(%d) is targeted but has no target", id));
        }
        this.trapIds.add(id);
        this.builder.addTrap(id, attack, damage);
    }

    @Override
    public void addTrap(final int id, final String attack, final int damage, final int target) {
        checkUniqueId("Trap", this.trapIds, id);
        checkPositiveZero("Trap " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveNonZero("Trap " + ModelBuilderInterface.CFG_TRAP_DAMAGE, damage);
        checkBounds("Trap " + ModelBuilderInterface.CFG_TRAP_TARGET, 1, 4, target);
        this.trapIds.add(id);
        if (AttackStrategy.valueOf(attack) != AttackStrategy.TARGETED) {
            this.builder.addTrap(id, attack, damage);
        } else {
            this.builder.addTrap(id, attack, damage, target);
        }
    }

    @Override
    public void addRoom(final int id, final int activation, final String restriction,
            final int food, final int gold,
            final int imps, final int niceness) {
        checkUniqueId("Room", this.roomIds, id);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ID, id);
        checkPositiveNonZero("Room " + ModelBuilderInterface.CFG_ROOM_ACTIVATION, activation);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ROOM_FOOD, food);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ROOM_GOLD, gold);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ROOM_IMPS, imps);
        checkPositiveZero("Room " + ModelBuilderInterface.CFG_ROOM_NICENESS, niceness);
        this.roomIds.add(id);
        this.builder.addRoom(id, activation, restriction, food, gold, imps, niceness);
    }

    @Override
    public void addSpell(final SpellAttrContainer container) {
        final int id = container.id();
        final String spellType = container.spellType();
        final String bidType = container.bidType();
        final int slot = container.slot();
        final String bidTypeBlocked = container.bidTypeBlocked();
        final int food = container.food();
        final int gold = container.gold();
        final String structureEffect = container.structureEffect();
        final int healthBuff = container.healthBuff();
        final int healBuff = container.healBuff();
        final int defuseBuff = container.defuseBuff();
        checkUniqueId("Spell", this.spellIds, id);
        BidType.valueOf(bidType);
        checkPositiveZero("Spell" + ModelBuilderInterface.CFG_ID, id);
        checkPositiveNonZero("Spell" + ModelBuilderInterface.CFG_SPELL_SLOT, slot);
        // check if spell type is legal
        // and if only the needed attributes are present.
        switch (SpellType.valueOf(spellType)) {
            case RESOURCE -> {
                checkResources(food, gold);
                checkAttrIsNull(bidTypeBlocked);
                checkAttrIsNull(structureEffect);
                checkAttrIsNull("healthBuff", healthBuff);
                checkAttrIsNull("healBuff", healBuff);
                checkAttrIsNull("defuseBuff", defuseBuff);
            }
            case BUFF -> {
                checkAttrIsNull("food", food);
                checkAttrIsNull("gold", gold);
                checkAttrIsNull(bidTypeBlocked);
                checkAttrIsNull(structureEffect);
                checkBuffs(healthBuff, healBuff, defuseBuff);
            }
            case ROOM -> {
                checkAttrIsNull("food", food);
                checkAttrIsNull("gold", gold);
                checkAttrIsNull(bidTypeBlocked);
                checkAttrIsNull(structureEffect);
                checkAttrIsNull("healthBuff", healthBuff);
                checkAttrIsNull("healBuff", healBuff);
                checkAttrIsNull("defuseBuff", defuseBuff);
            }
            case BIDDING -> {
                checkAttrIsNull("food", food);
                checkAttrIsNull("gold", gold);
                BidType.valueOf(bidTypeBlocked);
                checkAttrIsNull(structureEffect);
                checkAttrIsNull("healthBuff", healthBuff);
                checkAttrIsNull("healBuff", healBuff);
                checkAttrIsNull("defuseBuff", defuseBuff);
            }
            case STRUCTURE -> {
                checkAttrIsNull("food", food);
                checkAttrIsNull("gold", gold);
                checkAttrIsNull(bidTypeBlocked);
                STRUCTUREEFFECT.valueOf(structureEffect);
                checkAttrIsNull("healthBuff", healthBuff);
                checkAttrIsNull("healBuff", healBuff);
                checkAttrIsNull("defuseBuff", defuseBuff);
            }
            default -> throw new IllegalArgumentException(
                    String.format("%s is an illegal spell type", spellType));
        }

        this.spellIds.add(id);
        this.builder.addSpell(container);


    }


    @Override
    public void setMaxPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.builder.setMaxPlayers(maxPlayers);
        checkPositiveNonZero(ModelBuilderInterface.CFG_MAX_PLAYERS, maxPlayers);
        setProperties.add(ModelBuilderInterface.CFG_MAX_PLAYERS);
    }

    @Override
    public void setSeed(final long seed) {
        this.builder.setSeed(seed);
        setProperties.add(CFG_SEED);
    }

    @Override
    public void setYears(final int years) {
        this.years = years;
        this.builder.setYears(years);
        checkBounds(ModelBuilderInterface.CFG_YEARS, 1, Integer.MAX_VALUE, years);
        setProperties.add(ModelBuilderInterface.CFG_YEARS);
    }

    @Override
    public void setDungeonSideLength(final int dungeonSideLength) {
        this.builder.setDungeonSideLength(dungeonSideLength);
        checkBounds(ModelBuilderInterface.CFG_DUNGEON_SIDELENGTH, 1, 15, dungeonSideLength);
        setProperties.add(ModelBuilderInterface.CFG_DUNGEON_SIDELENGTH);
    }

    @Override
    public void setInitialFood(final int food) {
        this.builder.setInitialFood(food);
        checkPositiveZero(ModelBuilderInterface.CFG_INITIAL_FOOD, food);
        setProperties.add(ModelBuilderInterface.CFG_INITIAL_FOOD);
    }

    @Override
    public void setInitialGold(final int gold) {
        this.builder.setInitialGold(gold);
        checkPositiveZero(ModelBuilderInterface.CFG_INITIAL_GOLD, gold);
        setProperties.add(ModelBuilderInterface.CFG_INITIAL_GOLD);
    }

    @Override
    public void setInitialImps(final int imps) {
        this.builder.setInitialImps(imps);
        checkPositiveZero(ModelBuilderInterface.CFG_INITIAL_IMPS, imps);
        setProperties.add(ModelBuilderInterface.CFG_INITIAL_IMPS);
    }

    @Override
    public void setInitialEvilness(final int evilness) {
        this.builder.setInitialEvilness(evilness);
        checkBounds(ModelBuilderInterface.CFG_INITIAL_EVILNESS, 0, 15, evilness);
        setProperties.add(ModelBuilderInterface.CFG_INITIAL_EVILNESS);
    }

    @Override
    public M build() {
        checkPropertiesSet(
                ModelBuilderInterface.CFG_SEED,
                ModelBuilderInterface.CFG_MAX_PLAYERS,
                ModelBuilderInterface.CFG_YEARS,
                ModelBuilderInterface.CFG_DUNGEON_SIDELENGTH,
                ModelBuilderInterface.CFG_INITIAL_FOOD,
                ModelBuilderInterface.CFG_INITIAL_GOLD,
                ModelBuilderInterface.CFG_INITIAL_IMPS,
                ModelBuilderInterface.CFG_INITIAL_EVILNESS
        );
        checkLength("Adventurers", this.adventurerIds,
                this.years * (Model.MAX_ROUNDS - 1) * this.maxPlayers);
        final int rounds = this.years * Model.MAX_ROUNDS;
        checkLength("Rooms", this.roomIds, rounds * BuildingState.ROOMS_PER_ROUND);
        checkLength("Traps", this.trapIds, rounds * 4);
        checkLength("Monsters", this.monsterIds,
                rounds * BuildingState.MONSTERS_PER_ROUND);
        checkLength("Spells", this.spellIds, this.years * Model.MAX_ROUNDS * 3);
        return this.builder.build();
    }


}
