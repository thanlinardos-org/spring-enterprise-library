package com.thanlinardos.spring_enterprise_library.tuple;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class PairTest {

    private static final String HELLO = "hello";
    private static final String KEY = "key";

    @Test
    void of_validElements_createsPair() {
        Pair<String, Integer> pair = Pair.of(HELLO, 42);
        assertEquals(HELLO, pair.first());
        assertEquals(42, pair.second());
    }

    @Test
    void constructor_nullFirst_throws() {
        assertThrows(IllegalArgumentException.class, () -> Pair.of(null, "second"));
    }

    @Test
    void constructor_nullSecond_throws() {
        assertThrows(IllegalArgumentException.class, () -> Pair.of("first", null));
    }

    @Test
    void equals_samePair_returnsTrue() {
        Pair<String, Integer> a = Pair.of(KEY, 1);
        Pair<String, Integer> b = Pair.of(KEY, 1);
        assertEquals(a, b);
    }

    @Test
    void equals_differentFirst_returnsFalse() {
        Pair<String, Integer> a = Pair.of("key1", 1);
        Pair<String, Integer> b = Pair.of("key2", 1);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentSecond_returnsFalse() {
        Pair<String, Integer> a = Pair.of(KEY, 1);
        Pair<String, Integer> b = Pair.of(KEY, 2);
        assertNotEquals(a, b);
    }

    @Test
    void equals_notAPair_returnsFalse() {
        Pair<String, Integer> a = Pair.of(KEY, 1);
        assertNotEquals("something else", a);
    }

    @Test
    void hashCode_equalPairs_haveSameHashCode() {
        Pair<String, Integer> a = Pair.of(KEY, 1);
        Pair<String, Integer> b = Pair.of(KEY, 1);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_formatsFirstArrowSecond() {
        Pair<String, Integer> pair = Pair.of("foo", 7);
        assertEquals("foo->7", pair.toString());
    }

    @Test
    void toMap_collector_createsMappedEntries() {
        Map<String, Integer> map = Stream.of(
                Pair.of("a", 1),
                Pair.of("b", 2),
                Pair.of("c", 3)
        ).collect(Pair.toMap());

        assertEquals(3, map.size());
        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));
        assertEquals(3, map.get("c"));
    }

    @Test
    void toMap_emptyStream_returnsEmptyMap() {
        Map<String, Integer> map = Stream.<Pair<String, Integer>>empty().collect(Pair.toMap());
        assertTrue(map.isEmpty());
    }
}




