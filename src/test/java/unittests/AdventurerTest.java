package unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import de.unisaarland.cs.se.selab.model.Adventurer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdventurerTest {


    final Adventurer adv = new Adventurer(0, 1, 5, 1, 0, 0, false);

    @BeforeEach
    public void resetAdv() {
        adv.setCurrentHealthPoints(5);
        adv.setDefuseBuff(0);
        adv.setHealBuff(0);
        adv.setHealthBuff(0);
    }


    @Test
    void testDamageWithHealthBuff() {
        adv.setHealthBuff(2);
        assertEquals(6, adv.damage(6));
        assertFalse(adv.isDefeated());
        assertEquals(1, adv.getCurrentHealthPoints());
        assertEquals(0, adv.getHealthBuff());
    }


    @Test
    void setHealBuff() {
        adv.setHealBuff(1);
        assertEquals(0, adv.getHealValue());
    }

    @Test
    void setDefuseBuff() {
        adv.setDefuseBuff(1);
        assertEquals(0, adv.getDefuseValue());
    }

    @Test
    void testDebuffHealthBuff() {
        adv.damage(2);
        adv.setHealthBuff(4);
        adv.debuff();
        assertEquals(5, adv.getCurrentHealthPoints());
    }
}