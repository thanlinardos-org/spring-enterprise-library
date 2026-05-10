package com.thanlinardos.spring_enterprise_library.objects.iterators;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class IteratorsTest {

    // ─── EmptyIterator ────────────────────────────────────────────────────────

    @Test
    void emptyIterator_hasNext_returnsFalse() {
        Iterator<String> it = EmptyIterator.emptyIterator();
        assertFalse(it.hasNext());
    }

    @Test
    void emptyIterator_next_throwsNoSuchElement() {
        Iterator<String> it = EmptyIterator.emptyIterator();
        assertThrows(NoSuchElementException.class, it::next);
    }

    // ─── UnmodifiableIterator ─────────────────────────────────────────────────

    @Test
    void unmodifiableIterator_null_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> UnmodifiableIterator.unmodifiableIterator(null));
    }

    @Test
    void unmodifiableIterator_alreadyUnmodifiable_returnsSameInstance() {
        // An already-Unmodifiable iterator should be returned as-is (no double wrap)
        Iterator<String> first = UnmodifiableIterator.unmodifiableIterator(List.of("a").iterator());
        Iterator<String> second = UnmodifiableIterator.unmodifiableIterator(first);
        assertSame(first, second);
    }

    @Test
    void unmodifiableIterator_hasNext_delegatesToUnderlying() {
        Iterator<String> it = UnmodifiableIterator.unmodifiableIterator(List.of("a", "b").iterator());
        assertTrue(it.hasNext());
    }

    @Test
    void unmodifiableIterator_next_delegatesToUnderlying() {
        Iterator<String> it = UnmodifiableIterator.unmodifiableIterator(List.of("x").iterator());
        assertEquals("x", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void unmodifiableIterator_remove_throwsUnsupportedOperation() {
        Iterator<String> it = UnmodifiableIterator.unmodifiableIterator(List.of("a").iterator());
        it.next();
        assertThrows(UnsupportedOperationException.class, it::remove);
    }

    // ─── FilterIterator ───────────────────────────────────────────────────────

    @Test
    void filterIterator_withPredicate_returnsOnlyMatchingElements() {
        List<Integer> src = List.of(1, 2, 3, 4, 5);
        FilterIterator<Integer> it = new FilterIterator<>(src.iterator(), n -> n % 2 == 0);

        List<Integer> result = new ArrayList<>();
        while (it.hasNext()) {
            result.add(it.next());
        }

        assertEquals(List.of(2, 4), result);
    }

    @Test
    void filterIterator_noMatchingElements_returnsNothing() {
        FilterIterator<String> it = new FilterIterator<>(
                List.of("apple", "banana").iterator(),
                s -> s.startsWith("z")
        );
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void filterIterator_allMatchingElements_returnsAll() {
        List<Integer> src = List.of(2, 4, 6);
        FilterIterator<Integer> it = new FilterIterator<>(src.iterator(), n -> n % 2 == 0);

        List<Integer> result = new ArrayList<>();
        while (it.hasNext()) {
            result.add(it.next());
        }

        assertEquals(src, result);
    }

    @Test
    void filterIterator_emptySource_returnsFalse() {
        FilterIterator<String> it = new FilterIterator<>(Collections.emptyIterator(), s -> true);
        assertFalse(it.hasNext());
    }

    @Test
    void filterIterator_setIteratorAndPredicate_worksAfterSet() {
        FilterIterator<String> it = new FilterIterator<>();
        it.setIterator(List.of("hello", "world", "hi").iterator());
        it.setPredicate(s -> s.startsWith("h"));

        List<String> result = new ArrayList<>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        assertEquals(List.of("hello", "hi"), result);
    }

    @Test
    void filterIterator_getters_returnSetValues() {
        Iterator<String> src = List.of("a").iterator();
        FilterIterator<String> it = new FilterIterator<>(src, s -> true);
        assertSame(src, it.getIterator());
        assertNotNull(it.getPredicate());
    }

    @Test
    void filterIterator_remove_afterNext_delegatesToUnderlying() {
        List<String> mutable = new ArrayList<>(List.of("a", "b", "c"));
        FilterIterator<String> it = new FilterIterator<>(mutable.iterator(), s -> true);
        it.next(); // advance so remove has something to act on
        // remove() should not throw (mutable iterator supports it)
        assertDoesNotThrow(it::remove);
    }

    @Test
    void filterIterator_remove_afterHasNext_throws() {
        List<String> mutable = new ArrayList<>(List.of("a", "b"));
        FilterIterator<String> it = new FilterIterator<>(mutable.iterator(), s -> true);
        assertTrue(it.hasNext()); // calling hasNext advances internal state
        assertThrows(IllegalStateException.class, it::remove);
    }

    // ─── IteratorChain ────────────────────────────────────────────────────────

    @Test
    void iteratorChain_twoIterators_iteratesAllElements() {
        IteratorChain<String> chain = new IteratorChain<>(
                List.of("a", "b").iterator(),
                List.of("c", "d").iterator()
        );

        List<String> result = new ArrayList<>();
        while (chain.hasNext()) {
            result.add(chain.next());
        }

        assertEquals(List.of("a", "b", "c", "d"), result);
    }

    @Test
    void iteratorChain_emptyChain_hasNoElements() {
        IteratorChain<String> chain = new IteratorChain<>();
        assertFalse(chain.hasNext());
        assertThrows(NoSuchElementException.class, chain::next);
    }

    @Test
    void iteratorChain_singleIterator_iteratesAll() {
        IteratorChain<Integer> chain = new IteratorChain<>(List.of(1, 2, 3).iterator());

        List<Integer> result = new ArrayList<>();
        while (chain.hasNext()) {
            result.add(chain.next());
        }

        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void iteratorChain_varargs_iteratesAllChained() {
        IteratorChain<String> chain = new IteratorChain<>(
                List.of("x").iterator(),
                List.of("y").iterator(),
                List.of("z").iterator()
        );

        List<String> result = new ArrayList<>();
        while (chain.hasNext()) {
            result.add(chain.next());
        }

        assertEquals(List.of("x", "y", "z"), result);
    }

    @Test
    void iteratorChain_locksAfterFirstUse_preventsFurtherAddIterator() {
        IteratorChain<String> chain = new IteratorChain<>(List.of("a").iterator());
        assertFalse(chain.isLocked());

        assertTrue(chain.hasNext()); // locks the chain
        assertTrue(chain.isLocked());

        Iterator<String> extra = List.of("b").iterator();
        assertThrows(UnsupportedOperationException.class,
                () -> chain.addIterator(extra));
    }

    @Test
    void iteratorChain_addNullIterator_throws() {
        IteratorChain<String> chain = new IteratorChain<>();
        assertThrows(NullPointerException.class, () -> chain.addIterator(null));
    }

    @Test
    void iteratorChain_size_reflectsAddedIterators() {
        IteratorChain<String> chain = new IteratorChain<>();
        assertEquals(0, chain.size());

        chain.addIterator(List.of("a").iterator());
        assertEquals(1, chain.size());

        chain.addIterator(List.of("b", "c").iterator());
        assertEquals(2, chain.size());
    }

    @Test
    void iteratorChain_firstEmptyThenNonEmpty_iteratesNonEmpty() {
        IteratorChain<String> chain = new IteratorChain<>(
                Collections.emptyIterator(),
                List.of("only").iterator()
        );

        assertTrue(chain.hasNext());
        assertEquals("only", chain.next());
        assertFalse(chain.hasNext());
    }
}









