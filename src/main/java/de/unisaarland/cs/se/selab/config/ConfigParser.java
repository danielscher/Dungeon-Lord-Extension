package de.unisaarland.cs.se.selab.config;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Parses the config file and builds the model from the config using a {@link ModelBuilder}.
 */
public class ConfigParser {

    public static <M> M parse(final String config, final ModelBuilderInterface<M> builder) {
        final JSONObject json = new JSONObject(config);
        builder.setMaxPlayers(json.getInt(ModelBuilderInterface.CFG_MAX_PLAYERS));
        builder.setYears(json.getInt(ModelBuilderInterface.CFG_YEARS));
        builder.setDungeonSideLength(json.getInt(ModelBuilderInterface.CFG_DUNGEON_SIDELENGTH));
        builder.setInitialFood(json.optInt(ModelBuilderInterface.CFG_INITIAL_FOOD, 3));
        builder.setInitialGold(json.optInt(ModelBuilderInterface.CFG_INITIAL_GOLD, 3));
        builder.setInitialImps(json.optInt(ModelBuilderInterface.CFG_INITIAL_IMPS, 3));
        builder.setInitialEvilness(json.optInt(ModelBuilderInterface.CFG_INITIAL_EVILNESS, 5));
        final JSONArray monsters = json.getJSONArray(ModelBuilderInterface.CFG_MONSTERS);
        for (int i = 0; i < monsters.length(); i++) {
            ConfigParser.parseMonster(monsters.getJSONObject(i), builder);
        }
        final JSONArray adventurers = json.getJSONArray(ModelBuilderInterface.CFG_ADVENTURERS);
        for (int i = 0; i < adventurers.length(); i++) {
            ConfigParser.parseAdventurer(adventurers.getJSONObject(i), builder);
        }
        final JSONArray traps = json.getJSONArray(ModelBuilderInterface.CFG_TRAPS);
        for (int i = 0; i < traps.length(); i++) {
            ConfigParser.parseTrap(traps.getJSONObject(i), builder);
        }
        final JSONArray rooms = json.getJSONArray(ModelBuilderInterface.CFG_ROOMS);
        for (int i = 0; i < rooms.length(); i++) {
            ConfigParser.parseRoom(rooms.getJSONObject(i), builder);
        }
        final JSONArray spells = json.getJSONArray(ModelBuilderInterface.CFG_SPELLS);
        for (int i = 0; i < spells.length(); i++) {
            ConfigParser.parseSpell(spells.getJSONObject(i), builder);
        }
        return builder.build();
    }

    private static <M> void parseSpell(final JSONObject json,
            final ModelBuilderInterface<M> builder) {
        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final String spellType = json.getString(ModelBuilderInterface.CFG_SPELL_TYPE);
        final String bidType = json.getString(ModelBuilderInterface.CFG_SPELL_BID_TYPE);
        final int slot = json.getInt(ModelBuilderInterface.CFG_SPELL_SLOT);
        final int food = json.optInt(ModelBuilderInterface.CFG_SPELL_FOOD);
        final int gold = json.optInt(ModelBuilderInterface.CFG_SPELL_GOLD);
        final String bidTypeBlocked = json.optString(
                ModelBuilderInterface.CFG_SPELL_BLOCKED);
        final String structureEffect = json.optString(
                ModelBuilderInterface.CFG_SPELL_STRUCT);
        final int healthBuff = json.optInt(ModelBuilderInterface.CFG_SPELL_HP);
        final int healBuff = json.optInt(ModelBuilderInterface.CFG_SPELL_HEAL);
        final int defuseBuff = json.optInt(ModelBuilderInterface.CFG_SPELL_DEFUSE);

        final SpellAttrContainer container = new SpellAttrContainer(id, spellType, bidType, slot,
                food, gold, bidTypeBlocked, structureEffect,
                healthBuff, healBuff, defuseBuff);
        builder.addSpell(container);
    }

    private static <M> void parseMonster(final JSONObject json,
            final ModelBuilderInterface<M> builder) {
        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final int hunger = json.optInt(ModelBuilderInterface.CFG_MONSTER_HUNGER);
        final int damage = json.getInt(ModelBuilderInterface.CFG_MONSTER_DAMAGE);
        final int evilness = json.optInt(ModelBuilderInterface.CFG_MONSTER_EVILNESS);
        final String attack = json.getString(ModelBuilderInterface.CFG_MONSTER_ATK_STRATEGY);
        builder.addMonster(id, hunger, damage, evilness, attack);
    }

    private static <M> void parseAdventurer(final JSONObject json,
            final ModelBuilderInterface<M> builder) {
        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final int difficulty = json.getInt(ModelBuilderInterface.CFG_ADV_DIFFICULTY);
        final int healthPoints = json.getInt(ModelBuilderInterface.CFG_ADV_HEALTH_POINTS);
        final int magicPoints = json.optInt(ModelBuilderInterface.CFG_ADV_MAGIC_POINTS);
        final int healValue = json.optInt(ModelBuilderInterface.CFG_ADV_HEAL_VALUE);
        final int defuseValue = json.optInt(ModelBuilderInterface.CFG_ADV_DEFUSE_VALUE);
        final boolean charge = json.optBoolean(ModelBuilderInterface.CFG_ADV_CHARGE);
        builder.addAdventurer(id, difficulty, healthPoints, magicPoints, healValue, defuseValue,
                charge);
    }

    private static <M> void parseTrap(final JSONObject json,
            final ModelBuilderInterface<M> builder) {
        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final String attack = json.getString(ModelBuilderInterface.CFG_TRAP_ATK_STRATEGY);
        final int damage = json.getInt(ModelBuilderInterface.CFG_TRAP_DAMAGE);
        if (json.has(ModelBuilderInterface.CFG_TRAP_TARGET)) {
            builder.addTrap(
                    id, attack, damage, json.getInt(ModelBuilderInterface.CFG_TRAP_TARGET));
        } else {
            builder.addTrap(id, attack, damage);
        }
    }

    private static <M> void parseRoom(final JSONObject json,
            final ModelBuilderInterface<M> builder) {
        final int id = json.getInt(ModelBuilderInterface.CFG_ID);
        final int activation = json.getInt(ModelBuilderInterface.CFG_ROOM_ACTIVATION);
        final String restriction = json.getString(ModelBuilderInterface.CFG_ROOM_RESTRICTION);
        final int food = json.optInt(ModelBuilderInterface.CFG_ROOM_FOOD);
        final int gold = json.optInt(ModelBuilderInterface.CFG_ROOM_GOLD);
        final int imps = json.optInt(ModelBuilderInterface.CFG_ROOM_IMPS);
        final int niceness = json.optInt(ModelBuilderInterface.CFG_ROOM_NICENESS);
        builder.addRoom(id, activation, restriction, food, gold, imps, niceness);
    }

}
