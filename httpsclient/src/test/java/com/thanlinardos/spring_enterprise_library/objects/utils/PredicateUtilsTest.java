package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.spring_enterprise_library.time.api.DateTemporal;
import com.thanlinardos.spring_enterprise_library.time.model.Interval;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class PredicateUtilsTest {

    // ─── filterBy(Predicate, Function) ───────────────────────────────────────

    @Test
    void filterBy_predAndExtractor_appliesExtractorThenPredicate() {
        Predicate<String> lengthGreaterThan3 = PredicateUtils.filterBy((Integer n) -> n > 3, String::length);
        assertTrue(lengthGreaterThan3.test("hello"));
        assertFalse(lengthGreaterThan3.test("hi"));
    }

    // ─── filterBy(BiPredicate, Function, arg) ────────────────────────────────

    @Test
    void filterBy_biPredicateExtractorAndArg_appliesCorrectly() {
        // checks whether the length of the string equals the given int argument
        Predicate<String> hasLength3 = PredicateUtils.filterBy(
                Integer::equals,
                String::length,
                3
        );
        assertTrue(hasLength3.test("abc"));
        assertFalse(hasLength3.test("ab"));
    }

    // ─── isEqualTo / isNotEqualTo ─────────────────────────────────────────────

    @Test
    void isEqualTo_matchingExtractedValue_returnsTrue() {
        Predicate<String> startsWithA = PredicateUtils.isEqualTo('A', s -> s.charAt(0));
        assertTrue(startsWithA.test("Apple"));
        assertFalse(startsWithA.test("Banana"));
    }

    @Test
    void isEqualTo_nullCompareTo_matchesNullExtracted() {
        Predicate<String> isNull = PredicateUtils.isEqualTo(null, s -> null);
        assertTrue(isNull.test("anything"));
    }

    @Test
    void isNotEqualTo_differentValue_returnsTrue() {
        Predicate<String> notLength3 = PredicateUtils.isNotEqualTo(3, String::length);
        assertTrue(notLength3.test("hi"));
        assertFalse(notLength3.test("abc"));
    }

    // ─── isContainedIn / isNotContainedIn ────────────────────────────────────

    @Test
    void isContainedIn_varargs_extractedValueInArray_returnsTrue() {
        Predicate<String> firstCharIn = PredicateUtils.isContainedIn(s -> s.charAt(0), 'a', 'b');
        assertTrue(firstCharIn.test("apple"));
        assertFalse(firstCharIn.test("cherry"));
    }

    @Test
    void isContainedIn_collection_extractedValueInCollection_returnsTrue() {
        Predicate<String> in = PredicateUtils.isContainedIn(Function.identity(), List.of("apple", "banana"));
        assertTrue(in.test("apple"));
        assertFalse(in.test("cherry"));
    }

    @Test
    void isNotContainedIn_varargs_extractedValueNotInArray_returnsTrue() {
        Predicate<String> notInArray = PredicateUtils.isNotContainedIn(Function.identity(), "x", "y");
        assertTrue(notInArray.test("a"));
        assertFalse(notInArray.test("x"));
    }

    @Test
    void isNotContainedIn_collection_extractedValueNotInCollection_returnsTrue() {
        Predicate<String> notIn = PredicateUtils.isNotContainedIn(Function.identity(), List.of("x", "y"));
        assertTrue(notIn.test("a"));
        assertFalse(notIn.test("x"));
    }

    // ─── contains / notContains ───────────────────────────────────────────────

    @Test
    void contains_extractedValueInCollection_returnsTrue() {
        Predicate<String> containsFirst = PredicateUtils.contains(List.of("a", "b"), Function.identity());
        assertTrue(containsFirst.test("a"));
        assertFalse(containsFirst.test("c"));
    }

    @Test
    void notContains_extractedValueNotInCollection_returnsTrue() {
        Predicate<String> notPresent = PredicateUtils.notContains(List.of("a", "b"), Function.identity());
        assertTrue(notPresent.test("c"));
        assertFalse(notPresent.test("a"));
    }

    // ─── selfContains / selfNotContains ──────────────────────────────────────

    @Test
    void selfContains_valueFoundInExtractedCollection_returnsTrue() {
        Predicate<Container> hasApple = PredicateUtils.selfContains("apple", Container::items);
        assertTrue(hasApple.test(new Container(List.of("apple", "banana"))));
        assertFalse(hasApple.test(new Container(List.of("cherry"))));
    }

    @Test
    void selfNotContains_valueNotInExtractedCollection_returnsTrue() {
        Predicate<Container> noOrange = PredicateUtils.selfNotContains("orange", Container::items);
        assertTrue(noOrange.test(new Container(List.of("apple"))));
        assertFalse(noOrange.test(new Container(List.of("orange"))));
    }

    // ─── negate ───────────────────────────────────────────────────────────────

    @Test
    void negate_predicate_returnsNegated() {
        Predicate<String> notEmpty = PredicateUtils.negate(String::isEmpty);
        assertTrue(notEmpty.test("hello"));
        assertFalse(notEmpty.test(""));
    }

    // ─── nonNull / isNull ────────────────────────────────────────────────────

    @Test
    void nonNull_nonNullExtracted_returnsTrue() {
        Predicate<Container> hasItems = PredicateUtils.nonNull(Container::items);
        assertTrue(hasItems.test(new Container(List.of())));
        assertFalse(hasItems.test(new Container(null)));
    }

    @Test
    void isNull_nullExtracted_returnsTrue() {
        Predicate<Container> noItems = PredicateUtils.isNull(Container::items);
        assertTrue(noItems.test(new Container(null)));
        assertFalse(noItems.test(new Container(List.of())));
    }

    // ─── throwIfNot ───────────────────────────────────────────────────────────

    @Test
    void throwIfNot_predicateSatisfied_returnsTrue() {
        Predicate<String> guard = PredicateUtils.throwIfNot(
                s -> !s.isEmpty(), ErrorCode.VALIDATION_ERROR, "must not be empty");
        assertTrue(guard.test("hello"));
    }

    @Test
    void throwIfNot_predicateNotSatisfied_throwsCoreException() {
        Predicate<String> guard = PredicateUtils.throwIfNot(
                s -> !s.isEmpty(), ErrorCode.VALIDATION_ERROR, "must not be empty");
        CoreException ex = assertThrows(CoreException.class, () -> guard.test(""));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    // ─── isEqualByIdTo / isNotEqualByIdTo ────────────────────────────────────

    @Test
    void isEqualByIdTo_sameId_returnsTrue() {
        TestEntity a = new TestEntity(1L);
        TestEntity b = new TestEntity(1L);
        Predicate<TestEntity> pred = PredicateUtils.isEqualByIdTo(a);
        assertTrue(pred.test(b));
    }

    @Test
    void isEqualByIdTo_differentId_returnsFalse() {
        TestEntity a = new TestEntity(1L);
        TestEntity b = new TestEntity(2L);
        assertFalse(PredicateUtils.isEqualByIdTo(a).test(b));
    }

    @Test
    void isEqualByIdTo_compareTo_nullId_returnsFalse() {
        TestEntity noId = new TestEntity(null);
        TestEntity other = new TestEntity(1L);
        assertFalse(PredicateUtils.isEqualByIdTo(noId).test(other));
    }

    @Test
    void isNotEqualByIdTo_differentId_returnsTrue() {
        TestEntity a = new TestEntity(1L);
        TestEntity b = new TestEntity(2L);
        assertTrue(PredicateUtils.isNotEqualByIdTo(a).test(b));
    }

    // ─── isEqualToByKey ───────────────────────────────────────────────────────

    @Test
    void isEqualToByKey_matchingKey_returnsTrue() {
        Predicate<String> sameLength = PredicateUtils.isEqualToByKey("abc", String::length);
        assertTrue(sameLength.test("xyz")); // both length 3
    }

    @Test
    void isEqualToByKey_differentKey_returnsFalse() {
        Predicate<String> sameLength = PredicateUtils.isEqualToByKey("abc", String::length);
        assertFalse(sameLength.test("hi"));
    }

    @Test
    void isEqualToByKey_nullCompareTo_returnsFalse() {
        Predicate<String> pred = PredicateUtils.isEqualToByKey(null, String::length);
        assertFalse(pred.test("any"));
    }

    // ─── isTrue / isFalse ────────────────────────────────────────────────────

    @Test
    void isTrue_trueExtracted_returnsTrue() {
        Predicate<Boolean> pred = PredicateUtils.isTrue(Function.identity());
        assertTrue(pred.test(Boolean.TRUE));
        assertFalse(pred.test(Boolean.FALSE));
        assertFalse(pred.test(null));
    }

    @Test
    void isFalse_falseExtracted_returnsTrue() {
        Predicate<Boolean> pred = PredicateUtils.isFalse(Function.identity());
        assertTrue(pred.test(Boolean.FALSE));
        assertFalse(pred.test(Boolean.TRUE));
        assertFalse(pred.test(null));
    }

    @Test
    void dateTemporalPredicates_rangeContainmentAndDate_shouldApplyOnTemporalAndExtractorOverloads() {
        TestDateTemporal temporal = testTemporal();
        DateHolder holder = testHolder();

        assertTrue(PredicateUtils.isInRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)).test(temporal));
        assertTrue(PredicateUtils.isInRange(DateHolder::temporal, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)).test(holder));
        assertTrue(PredicateUtils.containsDate(LocalDate.of(2025, 1, 15)).test(temporal));
        assertTrue(PredicateUtils.containsDate(DateHolder::temporal, LocalDate.of(2025, 1, 15)).test(holder));
        assertTrue(PredicateUtils.isContainedIn(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)).test(temporal));
        assertTrue(PredicateUtils.isContainedIn(DateHolder::temporal, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)).test(holder));
    }

    @Test
    void dateTemporalPredicates_overlapVariants_shouldApplyOnTemporalAndExtractorOverloads() {
        TestDateTemporal temporal = testTemporal();
        TestDateTemporal other = overlappingTemporal();
        DateHolder holder = testHolder();

        assertTrue(PredicateUtils.overlapsInterval(other).test(temporal));
        assertTrue(PredicateUtils.overlapsInterval(DateHolder::temporal, other).test(holder));
        assertTrue(PredicateUtils.overlapsInterval(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 11)).test(temporal));
        assertTrue(PredicateUtils.overlapsInterval(DateHolder::temporal, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 11)).test(holder));
        assertTrue(PredicateUtils.overlap(other).test(temporal));
        assertTrue(PredicateUtils.overlap(DateHolder::temporal, other).test(holder));
    }

    @Test
    void dateTemporalPredicates_monthAndYearOverlap_shouldApplyOnTemporalAndExtractorOverloads() {
        TestDateTemporal temporal = testTemporal();
        DateHolder holder = testHolder();

        assertTrue(PredicateUtils.overlapsMonth(YearMonth.of(2025, 1)).test(temporal));
        assertTrue(PredicateUtils.overlapsMonth(DateHolder::temporal, YearMonth.of(2025, 1)).test(holder));
        assertTrue(PredicateUtils.overlapsYear(Year.of(2025)).test(temporal));
        assertTrue(PredicateUtils.overlapsYear(DateHolder::temporal, Year.of(2025)).test(holder));
        assertTrue(PredicateUtils.overlapsYear(LocalDate.of(2025, 6, 1)).test(temporal));
        assertTrue(PredicateUtils.overlapsYear(DateHolder::temporal, LocalDate.of(2025, 6, 1)).test(holder));
    }

    @Test
    void dateTemporalPredicates_startsChecks_shouldApplyOnTemporalAndExtractorOverloads() {
        TestDateTemporal temporal = testTemporal();
        DateHolder holder = testHolder();

        assertTrue(PredicateUtils.startsAfter(LocalDate.of(2025, 1, 1)).test(temporal));
        assertTrue(PredicateUtils.startsAfter(DateHolder::temporal, LocalDate.of(2025, 1, 1)).test(holder));
        assertTrue(PredicateUtils.startsBefore(LocalDate.of(2025, 1, 11)).test(temporal));
        assertTrue(PredicateUtils.startsBefore(DateHolder::temporal, LocalDate.of(2025, 1, 11)).test(holder));
        assertTrue(PredicateUtils.startsBeforeOrOn(LocalDate.of(2025, 1, 10)).test(temporal));
        assertTrue(PredicateUtils.startsBeforeOrOn(DateHolder::temporal, LocalDate.of(2025, 1, 10)).test(holder));
    }

    @Test
    void dateTemporalPredicates_endsChecks_shouldApplyOnTemporalAndExtractorOverloads() {
        TestDateTemporal temporal = testTemporal();
        DateHolder holder = testHolder();

        assertTrue(PredicateUtils.endsAfter(LocalDate.of(2025, 1, 19)).test(temporal));
        assertTrue(PredicateUtils.endsAfter(DateHolder::temporal, LocalDate.of(2025, 1, 19)).test(holder));
        assertTrue(PredicateUtils.endsAfterOrOn(LocalDate.of(2025, 1, 20)).test(temporal));
        assertTrue(PredicateUtils.endsAfterOrOn(DateHolder::temporal, LocalDate.of(2025, 1, 20)).test(holder));
    }

    // ─── Test helpers ─────────────────────────────────────────────────────────

    private record Container(List<String> items) {}

    private record DateHolder(TestDateTemporal temporal) {}

    private static TestDateTemporal testTemporal() {
        return new TestDateTemporal(new Interval(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20)));
    }

    private static DateHolder testHolder() {
        return new DateHolder(testTemporal());
    }

    private static TestDateTemporal overlappingTemporal() {
        return new TestDateTemporal(new Interval(LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 25)));
    }

    private record TestDateTemporal(Interval interval) implements DateTemporal {
        @Override
        public Interval getInterval() {
            return interval;
        }
    }

    private static class TestEntity extends BasicIdJpa {
        private final Long id;

        TestEntity(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            return "TestEntity{id=" + id + "}";
        }
    }
}



