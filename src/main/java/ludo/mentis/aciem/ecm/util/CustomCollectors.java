package ludo.mentis.aciem.ecm.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;


/**
 * Utility class providing custom collectors for use with Java Streams.
 * <p>
 * This class contains static methods that return collectors for specific use cases,
 * such as collecting elements into a LinkedHashMap while preserving the order of elements.
 * </p>
 * <p>
 * The class is designed to be non-instantiable and contains a private constructor
 * to prevent instantiation.
 * </p>
 */
public class CustomCollectors {

    /**
     * Private constructor to prevent instantiation.
     */
    private CustomCollectors() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Provide a Collector for collecting values from a stream into a LinkedHashMap,
     * thus keeping the order.
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a Collector to collect values in a sorted map
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toSortedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper,
                valueMapper,
                (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                LinkedHashMap::new);
    }

}
