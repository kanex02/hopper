package nz.ac.canterbury.seng302.tab.entity.user;

import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HopsLevelTest {

    @Test
    void testNewUserStartsAtLevel1() {
        UserEntity user = new UserEntity();
        assertEquals(1, user.getLevel());
    }

    @Test
    void testNewUserStartsAt0Hops() {
        UserEntity user = new UserEntity();
        assertEquals(0, user.getTotalHops());
    }

    @Test
    void testAddHops() {
        UserEntity user = new UserEntity();
        assertEquals(50, user.addHops(50));
        assertEquals(50, user.getTotalHops());
    }

    @Test
    void testAddHopsTwice() {
        UserEntity user = new UserEntity();
        assertEquals(1, user.getLevel());

        user.addHops(100);
        assertEquals(2, user.getLevel());

        user.addHops(125);
        assertEquals(3, user.getLevel());
    }

    @Test
    void test100HopsShouldBeLevel2() {
        UserEntity user = new UserEntity();
        user.addHops(100);
        assertEquals(2, user.getLevel());
    }

    @Test
    void testMaxLevelIs19() {
        UserEntity user = new UserEntity();
        user.addHops(Integer.MAX_VALUE);
        assertEquals(19, user.getLevel());
    }

    @Test
    void testHopsRequiredForEachLevel() {
        UserEntity user = new UserEntity();
        int x = user.getInitialHopsNeeded();
        int level = 1;
        for (int i = 0; i < user.getMaxLevel() - 1; i++) {
            user.setLevel(level);
            assertEquals(x, user.getTotalHopsRequiredForNextLevel());
            System.out.println(level);
            System.out.println(x);
            level++;
            x += user.getInitialHopsNeeded() + (user.getAdditionalHopsNeededPerLevel() * (level - 1));
        }
    }

    @Test
    void testHopsCannotDecrease() {
        UserEntity user = new UserEntity();
        user.addHops(100);
        assertEquals(100, user.getTotalHops());
        assertEquals(2, user.getLevel());
        user.addHops(-50);
        assertEquals(100, user.getTotalHops());
        assertEquals(2, user.getLevel());
    }

    @Test
    void testHopsCannotBeNegative() {
        UserEntity user = new UserEntity();
        user.setTotalHops(-100);
        assertEquals(0, user.getTotalHops());
        assertEquals(1, user.getLevel());
    }

}
