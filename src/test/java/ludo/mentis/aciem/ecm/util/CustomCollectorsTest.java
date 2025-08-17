package ludo.mentis.aciem.ecm.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CustomCollectorsTest {

    @Test
    void testToSortedMap() {
        var data = List.of("apple", "banana", "cherry");

        var result = data.stream().collect(CustomCollectors.toSortedMap(Function.identity(), String::length));

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(5, result.get("apple"));
        assertEquals(6, result.get("banana"));
        assertEquals(6, result.get("cherry"));
    }

    @Test
    void testToSortedMapWithDuplicateKeys() {
        List<String> data = List.of("apple", "apple");

        var exception = assertThrows(IllegalStateException.class,
                () -> data.stream().collect(CustomCollectors.toSortedMap(s -> s.substring(0, 3), String::length)));

        assertTrue(exception.getMessage().contains("Duplicate key"));
    }

    @Test
    void testToSortedMapOrder() {
        var data = List.of("banana", "apple", "cherry");

        var result = data.stream().collect(CustomCollectors.toSortedMap(Function.identity(), String::length));

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(List.of("banana", "apple", "cherry"), new ArrayList<>(result.keySet()));
    }

    @Test
    void testPrivateConstructor() {
        Constructor<?>[] constructors = CustomCollectors.class.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor should be private");
            constructor.setAccessible(true);
            var exception = assertThrows(InvocationTargetException.class, constructor::newInstance, "Constructor should throw InvocationTargetException");
            assertInstanceOf(IllegalStateException.class, exception.getCause(), "Constructor should throw AssertionError");
        }
    }
}