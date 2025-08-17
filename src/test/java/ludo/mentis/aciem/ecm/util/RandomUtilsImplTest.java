package ludo.mentis.aciem.ecm.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilsImplTest {

    private RandomUtilsImpl randomUtils;

    private enum SampleEnum { A, B, C }
    private enum EmptyEnum { }

    @BeforeEach
    void setUp() {
        randomUtils = new RandomUtilsImpl();
    }

    @Test
    void testCreateRandomSublist_ZeroN_ReturnsEmptyList_EvenIfSourceEmpty() {
        assertEquals(0, randomUtils.createRandomSublist(List.of(), 0).size());
        assertEquals(0, randomUtils.createRandomSublist(List.of(1, 2, 3), 0).size());
    }

    @Test
    void testCreateRandomSublist_InvalidN_Throws() {
        var list = List.of(1, 2, 3);
        assertThrows(IllegalArgumentException.class, () -> randomUtils.createRandomSublist(list, -1));
        assertThrows(IllegalArgumentException.class, () -> randomUtils.createRandomSublist(list, 4));
    }

    @Test
    void testCreateRandomSublist_EmptySourceButPositiveN_Throws() {
        assertThrows(IllegalArgumentException.class, () -> randomUtils.createRandomSublist(List.of(), 1));
    }

    @Test
    void testCreateRandomSublist_ValidRange_SubsetAndUnique() {
        var source = List.of("a", "b", "c", "d", "e");
        int n = 3;
        var sub = randomUtils.createRandomSublist(source, n);

        assertNotNull(sub);
        assertEquals(n, sub.size());
        // All elements must belong to the original list
        assertTrue(source.containsAll(sub));
        // No duplicates expected when source elements are unique
        assertEquals(n, new HashSet<>(sub).size());
    }

    @Test
    void testPickRandomEnumValue_ReturnsOneOfConstants() {
        Set<SampleEnum> seen = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            var v = randomUtils.pickRandomEnumValue(SampleEnum.class);
            assertNotNull(v);
            assertTrue(v == SampleEnum.A || v == SampleEnum.B || v == SampleEnum.C);
            seen.add(v);
        }
        // Very likely we have seen more than one value
        assertFalse(seen.isEmpty());
    }

    @Test
    void testPickRandomEnumValue_EmptyEnum_Throws() {
        assertThrows(IllegalArgumentException.class, () -> randomUtils.pickRandomEnumValue(EmptyEnum.class));
    }

    @Test
    void testPickRandomBoolean_ObservesBothValuesEventually() {
        boolean sawTrue = false;
        boolean sawFalse = false;
        for (int i = 0; i < 2000; i++) {
            boolean v = randomUtils.pickRandomBoolean();
            sawTrue |= v;
            sawFalse |= !v;
            if (sawTrue && sawFalse) break;
        }
        assertTrue(sawTrue, "Expected to observe true at least once");
        assertTrue(sawFalse, "Expected to observe false at least once");
    }

    @Test
    void testGetRandomDate_InclusiveBoundsAndRange() {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2020, 1, 31);
        for (int i = 0; i < 500; i++) {
            LocalDate d = randomUtils.getRandomDate(start, end);
            assertFalse(d.isBefore(start));
            assertFalse(d.isAfter(end));
        }
    }

    @Test
    void testGetRandomDate_SameStartEnd_ReturnsThatDate() {
        LocalDate date = LocalDate.of(2023, 5, 10);
        assertEquals(date, randomUtils.getRandomDate(date, date));
    }

    @Test
    void testGetRandomDate_StartAfterEnd_Throws() {
        LocalDate start = LocalDate.of(2023, 5, 11);
        LocalDate end = LocalDate.of(2023, 5, 10);
        assertThrows(IllegalArgumentException.class, () -> randomUtils.getRandomDate(start, end));
    }

    @Test
    void testGetRandomNumberInRange_ValidRange() {
        int origin = 5;
        int bound = 10;
        for (int i = 0; i < 1000; i++) {
            int v = randomUtils.getRandomNumberInRange(origin, bound);
            assertTrue(v >= origin && v < bound, "Value should be within [origin, bound)");
        }
    }

    @Test
    void testGetRandomNumberInRange_InvalidRanges_Throw() {
        assertThrows(IllegalArgumentException.class, () -> randomUtils.getRandomNumberInRange(5, 5));
        assertThrows(IllegalArgumentException.class, () -> randomUtils.getRandomNumberInRange(10, 5));
    }
}
