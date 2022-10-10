package de.unisaarland.cs.se.selab.config;

/**
 * Interface for model builders.
 *
 * @param <M> the model class to be built
 */
public interface ModelBuilderInterface<M> {

    // General properties
    String CFG_SEED = "seed";
    String CFG_MAX_PLAYERS = "maxPlayers";
    String CFG_YEARS = "years";
    String CFG_DUNGEON_SIDELENGTH = "dungeonSideLength";
    String CFG_INITIAL_FOOD = "initialFood";
    String CFG_INITIAL_GOLD = "initialGold";
    String CFG_INITIAL_IMPS = "initialImps";
    String CFG_INITIAL_EVILNESS = "initialEvilness";
    String CFG_ID = "id";

    // monsters
    String CFG_MONSTERS = "monsters";
    String CFG_MONSTER_HUNGER = "hunger";
    String CFG_MONSTER_DAMAGE = "damage";
    String CFG_MONSTER_EVILNESS = "evilness";
    String CFG_MONSTER_ATK_STRATEGY = "attackStrategy";

    // adventurers
    String CFG_ADVENTURERS = "adventurers";
    String CFG_ADV_DIFFICULTY = "difficulty";
    String CFG_ADV_HEALTH_POINTS = "healthPoints";
    String CFG_ADV_HEAL_VALUE = "healValue";
    String CFG_ADV_DEFUSE_VALUE = "defuseValue";
    String CFG_ADV_CHARGE = "charge";

    // traps
    String CFG_TRAPS = "traps";
    String CFG_TRAP_ATK_STRATEGY = "attackStrategy";
    String CFG_TRAP_DAMAGE = "damage";
    String CFG_TRAP_TARGET = "target";

    // rooms
    String CFG_ROOMS = "rooms";
    String CFG_ROOM_ACTIVATION = "activation";
    String CFG_ROOM_RESTRICTION = "restriction";
    String CFG_ROOM_FOOD = "food";
    String CFG_ROOM_GOLD = "gold";
    String CFG_ROOM_IMPS = "imps";
    String CFG_ROOM_NICENESS = "niceness";


    void addMonster(int id, int hunger, int damage, int evilness,
                    String attack);

    void addAdventurer(int id, int difficulty, int healthPoints,
                       int healValue, int defuseValue, boolean charge);

    void addTrap(int id, String attack, int damage);

    void addTrap(int id, String attack, int damage, int target);

    void addRoom(int id, int activation, String restriction, int food, int gold, int imps,
                 int niceness);
    

    void setMaxPlayers(int maxPlayers);

    void setSeed(long seed);

    void setYears(int years);

    void setDungeonSideLength(int dungeonSideLength);

    void setInitialFood(int food);

    void setInitialGold(int gold);

    void setInitialImps(int imps);

    void setInitialEvilness(int evilness);

    /**
     * Create a model from this builder.
     *
     * @return the instantiated model
     */
    M build();

}
