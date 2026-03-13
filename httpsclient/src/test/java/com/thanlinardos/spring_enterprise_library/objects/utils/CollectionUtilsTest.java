package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@CoreTest
class CollectionUtilsTest {

    // Test data
    private static final List<String> TEST_LIST = List.of("apple", "banana", "cherry");

    // isEmpty and isNotEmpty tests
    private static Stream<Arguments> isEmptyParams() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of(Collections.emptyList(), true),
                Arguments.of(TEST_LIST, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isEmptyParams")
    void isEmpty_variousCollections_returnsExpectedResult(Collection<String> collection, boolean expected) {
        Assertions.assertEquals(expected, CollectionUtils.isEmpty(collection));
    }

    @ParameterizedTest
    @MethodSource("isEmptyParams")
    void isNotEmpty_variousCollections_returnsOppositeResult(Collection<String> collection, boolean expected) {
        Assertions.assertEquals(!expected, CollectionUtils.isNotEmpty(collection));
    }

    // requireNotEmpty tests
    private static Stream<Arguments> requireNotEmptyParams() {
        return Stream.of(
                Arguments.of((Collection<String>) null),
                Arguments.of(Collections.emptyList())
        );
    }

    @ParameterizedTest
    @MethodSource("requireNotEmptyParams")
    void requireNotEmpty_invalidCollections_throwsException(Collection<String> collection) {
        Assertions.assertThrows(CoreException.class, () -> CollectionUtils.requireNotEmpty(collection));
    }

    @Test
    void requireNotEmpty_nonEmptyCollection_returnsCollection() {
        Collection<String> result = CollectionUtils.requireNotEmpty(TEST_LIST);
        Assertions.assertEquals(TEST_LIST, result);
    }

    // contains tests
    private static Stream<Arguments> containsParams() {
        return Stream.of(
                Arguments.of(null, (Predicate<String>) s -> true, false),
                Arguments.of(TEST_LIST, (Predicate<String>) s -> s.startsWith("b"), true),
                Arguments.of(TEST_LIST, (Predicate<String>) s -> s.startsWith("z"), false)
        );
    }

    @ParameterizedTest
    @MethodSource("containsParams")
    void contains_variousCollections_returnsExpectedResult(Collection<String> collection, Predicate<String> predicate, boolean expected) {
        Assertions.assertEquals(expected, CollectionUtils.contains(collection, predicate));
    }

    @ParameterizedTest
    @MethodSource("containsParams")
    void contains_variousArrays_returnsExpectedResult(Collection<String> collection, Predicate<String> predicate, boolean expected) {
        String[] arrayOrNull = collection != null ? collection.toArray(String[]::new) : null;
        Assertions.assertEquals(expected, CollectionUtils.contains(arrayOrNull, predicate));
    }

    @Test
    void containsAnyOf_containsValue_returnsTrue() {
        Assertions.assertTrue(CollectionUtils.containsAnyOf(TEST_LIST, "apple", "orange"));
    }

    @Test
    void containsAnyOf_noContainsValue_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.containsAnyOf(TEST_LIST, "orange", "grape"));
    }

    // containsAnyNull tests
    @Test
    void containsAnyNull_containsNull_returnsTrue() {
        List<String> listWithNull = new ArrayList<>();
        listWithNull.add("apple");
        listWithNull.add(null);
        listWithNull.add("cherry");
        Assertions.assertTrue(CollectionUtils.containsAnyNull(listWithNull, Function.identity()));
    }

    @Test
    void containsAnyNull_noNull_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.containsAnyNull(TEST_LIST, Function.identity()));
    }

    // notContains tests
    @ParameterizedTest
    @MethodSource("containsParams")
    void notContains_variousCollections_returnsOppositeResult(Collection<String> collection, Predicate<String> predicate, boolean expected) {
        Assertions.assertEquals(!expected, CollectionUtils.notContains(collection, predicate));
    }

    // isIn tests
    private static Stream<Arguments> isInParams() {
        return Stream.of(
                Arguments.of("apple", new String[]{"apple", "banana"}, true),
                Arguments.of("orange", new String[]{"apple", "banana"}, false),
                Arguments.of("orange", null, false),
                Arguments.of(null, new String[]{"apple", "banana"}, false),
                Arguments.of(null, null, true),
                Arguments.of("apple", new String[]{null, "apple"}, true),
                Arguments.of("apple", TEST_LIST, true),
                Arguments.of("orange", TEST_LIST, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isInParams")
    @SuppressWarnings("unchecked")
    void isIn_variousInputs_returnsExpectedResult(String target, Object values, boolean expected) {
        if (values instanceof String[] array) {
            Assertions.assertEquals(expected, CollectionUtils.isIn(target, array));
        } else {
            Assertions.assertEquals(expected, CollectionUtils.isIn(target, (Collection<String>) values));
        }
    }

    private static Stream<Arguments> isInParamsNull() {
        return Stream.of(
                Arguments.of(null, new String[]{"apple", "banana"}, false),
                Arguments.of("orange", null, false),
                Arguments.of(null, null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("isInParamsNull")
    void isInArray_Null(String target, String[] values, boolean expected) {
        Assertions.assertEquals(expected, CollectionUtils.isIn(target, values));
    }

    // isNotIn tests
    @ParameterizedTest
    @MethodSource("isInParams")
    @SuppressWarnings("unchecked")
    void isNotIn_variousInputs_returnsOppositeResult(String target, Object values, boolean expected) {
        if (values instanceof String[] array) {
            Assertions.assertEquals(!expected, CollectionUtils.isNotIn(target, array));
        } else {
            Assertions.assertEquals(!expected, CollectionUtils.isNotIn(target, (Collection<String>) values));
        }
    }

    // combineToList tests
    @Test
    void combineToList_multipleCollections_returnsCombinedList() {
        List<String> result = CollectionUtils.combineToList(
                List.of("a", "b"),
                List.of("c", "d"),
                List.of("e")
        );
        Assertions.assertEquals(List.of("a", "b", "c", "d", "e"), result);
    }

    @Test
    void combineToList_twoLists_returnsCombinedList() {
        List<String> result = CollectionUtils.combineToList(
                List.of("a", "b"),
                List.of("c", "d")
        );
        Assertions.assertEquals(List.of("a", "b", "c", "d"), result);
    }

    // combineToSet tests
    @Test
    void combineToSet_multipleCollections_returnsCombinedSet() {
        Set<String> result = CollectionUtils.combineToSet(
                List.of("a", "b"),
                List.of("b", "c"),
                List.of("c", "d")
        );
        Set<String> expected = Set.of("a", "b", "c", "d");
        Assertions.assertEquals(expected, result);
    }

    @Test
    void combineToSet_emptySets() {
        Set<String> result = CollectionUtils.combineToSet(Collections.emptySet(), Collections.emptySet());
        Set<String> expected = Collections.emptySet();
        Assertions.assertEquals(expected, result);

        testSetViewMethods((CollectionUtils.SetView<String>) result, expected);
    }

    @Test
    void combineToSet_twoSets_returnsCombinedSet() {
        CollectionUtils.SetView<String> result = (CollectionUtils.SetView<String>) CollectionUtils.combineToSet(Set.of("a", "b"), Set.of("b", "c"));
        Set<String> expected = Set.of("a", "b", "c");
        Assertions.assertEquals(expected, result);

        testSetViewMethods(result, expected);
    }

    // difference tests
    @Test
    void difference() {
        CollectionUtils.SetView<String> result = CollectionUtils.difference(Set.of("a", "b"), Set.of("b", "c"));
        Set<String> expected = Set.of("a");
        Assertions.assertEquals(expected, result);

        testSetViewMethods(result, expected);
    }

    @Test
    void difference_empty() {
        CollectionUtils.SetView<String> result = CollectionUtils.difference(Set.of("b", "c"), Collections.emptySet());
        Set<String> expected = Set.of("b", "c");
        Assertions.assertEquals(expected, result);

        testSetViewMethods(result, expected);
        Assertions.assertFalse(result.contains("z"));
    }

    // reverse tests
    @Test
    void reverse_list_returnsReversedCopy() {
        List<String> result = CollectionUtils.reverse(TEST_LIST);
        Assertions.assertEquals(List.of("cherry", "banana", "apple"), result);
        // Verify original list is unchanged
        Assertions.assertEquals(TEST_LIST, List.of("apple", "banana", "cherry"));
    }

    // zipWith tests
    @Test
    void zipWith_equalLists_returnsZippedList() {
        List<String> list1 = List.of("a", "b", "c");
        List<Integer> list2 = List.of(1, 2, 3);

        List<String> result = CollectionUtils.zipWith(
                (s, i) -> s + i,
                list1,
                list2
        );

        Assertions.assertEquals(List.of("a1", "b2", "c3"), result);
    }

    @Test
    void zipWith_unequalLists_throwsException() {
        List<String> list1 = List.of("a", "b");
        List<Integer> list2 = List.of(1, 2, 3);

        Assertions.assertThrows(CoreException.class, () ->
                CollectionUtils.zipWith((s, i) -> s + i, list1, list2));
    }

    @Test
    void zipWith_nullList1_throwsException() {
        List<Integer> list2 = List.of(1);
        Assertions.assertThrows(CoreException.class, () ->
                CollectionUtils.zipWith((s, i) -> i, null, list2));
    }

    @Test
    void zipWith_nullList2_throwsException() {
        List<Integer> list1 = List.of(1);
        Assertions.assertThrows(CoreException.class, () ->
                CollectionUtils.zipWith((s, i) -> i, list1, null));
    }

    // merge tests
    @Test
    void merge_emptyList_returnsEmptyList() {
        List<String> result = CollectionUtils.merge(
                (a, b) -> false,
                (a, b) -> a + b,
                Function.identity(),
                Collections.emptyList()
        );
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void merge_noMerge_returnsOriginalList() {
        List<String> result = CollectionUtils.merge(
                (a, b) -> false,
                (a, b) -> a + b,
                Function.identity(),
                List.of("a", "b", "c")
        );
        Assertions.assertEquals(List.of("c", "b", "a"), result); // Reversed due to stack
    }

    @Test
    void merge_withMerge_returnsMergedList() {
        List<String> result = CollectionUtils.merge(
                (a, b) -> a.length() == b.length(),
                (a, b) -> a + b,
                Function.identity(),
                List.of("a", "bb", "cc", "d")
        );
        Assertions.assertEquals(result, List.of("d", "ccbb", "a"));
    }

    @Test
    void sortAndMerge_withMerge_returnsMergedList() {
        List<String> result = CollectionUtils.sortAndMerge(
                (a, b) -> a.length() == b.length(),
                (a, b) -> a + b,
                Function.identity(),
                Comparator.naturalOrder(),
                List.of("d", "bb", "cc", "a")
        );
        Assertions.assertEquals(result, List.of("d", "ccbb", "a"));
    }

    // consideredEqual tests
    @Test
    void consideredEqual_nullLists_returnsTrue() {
        Assertions.assertTrue(CollectionUtils.consideredEqual(
                Objects::equals, null, null));
    }

    @Test
    void consideredEqual_nullSecondList_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.consideredEqual(
                Objects::equals, List.of("a"), null));
    }

    @Test
    void consideredEqual_nullSecondList_emptyFirstList_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.consideredEqual(
                Objects::equals, Collections.emptyList(), null));
    }

    @Test
    void consideredEqual_emptyLists_returnsTrue() {
        Assertions.assertTrue(CollectionUtils.consideredEqual(
                Objects::equals, Collections.emptyList(), Collections.emptyList()));
    }

    @Test
    void consideredEqual_SecondEmptyList_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.consideredEqual(
                Objects::equals, List.of("a"), Collections.emptyList()));
    }

    @Test
    void consideredEqual_differentSizes_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.consideredEqual(
                Objects::equals, List.of("a"), List.of("a", "b")));
    }

    @Test
    void consideredEqual_equalElements_returnsTrue() {
        Assertions.assertTrue(CollectionUtils.consideredEqual(
                Objects::equals, List.of("a", "b"), List.of("a", "b")));
    }

    // consideredNotEqual tests
    @Test
    void consideredNotEqual_nullLists_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.consideredNotEqual(
                Objects::equals, null, null));
    }

    @Test
    void consideredNotEqual_equalElements_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.consideredNotEqual(
                Objects::equals, List.of("a", "b"), List.of("a", "b")));
    }

    @Test
    void consideredNotEqual_notEqualElements_returnsTrue() {
        Assertions.assertTrue(CollectionUtils.consideredNotEqual(
                Objects::equals, List.of("b"), List.of("a", "b")));
    }

    // consideredEqualIgnoringOrder tests
    @Test
    void consideredEqualIgnoringOrder_equalSets_returnsTrue() {
        Assertions.assertTrue(CollectionUtils.consideredEqualIgnoringOrder(
                Objects::equals, Set.of("a", "b"), Set.of("b", "a")));
    }

    @Test
    void consideredEqualIgnoringOrder_differentSets_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.consideredEqualIgnoringOrder(
                Objects::equals, Set.of("a", "b"), Set.of("a", "c")));
    }

    // intersects tests
    private static Stream<Arguments> intersectsParams() {
        return Stream.of(
                Arguments.of(List.of("a", "b"), List.of("b", "c"), true),
                Arguments.of(List.of("a", "b"), List.of("c", "d"), false)
        );
    }

    @ParameterizedTest
    @MethodSource("intersectsParams")
    void intersects_variousCollections_returnsExpectedResult(List<String> a, List<String> b, boolean expected) {
        Assertions.assertEquals(expected, CollectionUtils.intersects(a, b));
    }

    // disjunction tests
    @Test
    void disjunction_overlappingSets_returnsSymmetricDifference() {
        CollectionUtils.SetView<String> result = CollectionUtils.disjunction(
                Set.of("a", "b", "c"),
                Set.of("b", "c", "d")
        );

        Set<String> expected = Set.of("a", "d");
        Assertions.assertEquals(expected, result);

        testSetViewMethods(result, expected);
    }

    @Test
    void disjunction_emptySets() {
        CollectionUtils.SetView<String> result = CollectionUtils.disjunction(Collections.emptySet(), Collections.emptySet());

        Set<String> expected = Collections.emptySet();
        Assertions.assertEquals(expected, result);
        testSetViewMethods(result, expected);
    }

    private void testSetViewMethods(CollectionUtils.SetView<String> result, Set<String> expected) {
        Set<String> fromResult = new HashSet<>(result);
        Assertions.assertEquals(expected, fromResult);
        Assertions.assertEquals(expected.size(), fromResult.size());
        if (!expected.isEmpty()) {
            Assertions.assertFalse(result.isEmpty());
            Assertions.assertTrue(result.containsAll(expected));
        } else {
            Assertions.assertTrue(result.isEmpty());
        }
    }

    @Test
    void disjunction_null_throws() {
        Set<String> set = Set.of("a", "b", "c");

        Assertions.assertThrows(NullPointerException.class, () -> CollectionUtils.disjunction(null, null));
        Assertions.assertThrows(NullPointerException.class, () -> CollectionUtils.disjunction(set, null));
        Assertions.assertThrows(NullPointerException.class, () -> CollectionUtils.disjunction(null, set));
    }

    private static Stream<Arguments> hasDifferenceBetweenAnyWayParams() {
        return Stream.of(
          Arguments.of(Set.of("a", "b", "c"), Set.of("b", "c", "d"), true),
          Arguments.of(Set.of("a", "b", "c"), Set.of("a", "b", "c"), false),
          Arguments.of(Set.of("a", "b", "c"), Set.of("b", "c"), true),
          Arguments.of(Collections.emptySet(), Set.of("b", "c"), true),
          Arguments.of(Set.of("a", "b", "c"), Collections.emptySet(), true),
          Arguments.of(Collections.emptySet(), Collections.emptySet(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("hasDifferenceBetweenAnyWayParams")
    void hasDifferenceBetweenAnyWay(Set<String> setA, Set<String> setB, boolean expected) {
        Assertions.assertEquals(expected, CollectionUtils.hasDifferenceBetweenAnyWay(setA, setB));
    }

    // listOf tests
    @Test
    void listOf_nullArgument_returnsListWithNull() {
        List<String> result = CollectionUtils.listOf((String[]) null);
        Assertions.assertEquals(1, result.size());
        Assertions.assertNull(result.getFirst());
    }

    @Test
    void listOf_multipleArguments_returnsList() {
        List<String> result = CollectionUtils.listOf("a", "b", "c");
        Assertions.assertEquals(List.of("a", "b", "c"), result);
    }

    // filterByMaxValueForField tests
    @Test
    void filterByMaxValueForField_emptyCollection_returnsEmptyList() {
        List<String> result = CollectionUtils.filterByMaxValueForField(
                Collections.emptyList(),
                String::length,
                Comparator.naturalOrder()
        );
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void filterByMaxValueForField_CollectionWithNull_returnsEmptyList() {
        ArrayList<String> collection = new ArrayList<>();
        collection.add(null);

        List<String> result = CollectionUtils.filterByMaxValueForField(
                collection,
                String::length,
                Comparator.naturalOrder()
        );
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void filterByMaxValueForField_nonEmptyCollection_returnsMaxElements() {
        List<String> result = CollectionUtils.filterByMaxValueForField(
                List.of("a", "bb", "ccc", "dd"),
                String::length,
                Comparator.naturalOrder()
        );
        Assertions.assertEquals(List.of("ccc"), result);
    }

    // removeAll tests
    @Test
    void removeAll_customPredicate_returnsFilteredCollection() {
        Collection<String> result = CollectionUtils.removeAll(
                List.of("apple", "banana", "cherry"),
                List.of("apricot", "blueberry", "bango"),
                (a, b) -> a.startsWith(b.substring(0, 2))
        );

        // "apple" starts with "ap" from "apricot", "banana" starts with "ba" from "bango"
        Assertions.assertEquals(List.of("cherry"), result);
    }

    // subtractToSet tests
    @Test
    void subtractToSet_overlappingCollections_returnsDifference() {
        Set<String> result = CollectionUtils.subtractToSet(
                Set.of("a", "b", "c"),
                Set.of("b", "d")
        );
        Assertions.assertEquals(Set.of("a", "c"), result);
    }

    // retainAll tests
    @Test
    void retainAll_customPredicate_returnsRetainedCollection() {
        Collection<String> result = CollectionUtils.retainAll(
                List.of("apple", "blanana", "cherry"),
                List.of("apricot", "blueberry"),
                (a, b) -> a.startsWith(b.substring(0, 2))
        );

        Assertions.assertEquals(List.of("apple", "blanana"), result);
    }

    // findExactlyOneOrNone tests
    @Test
    void findExactlyOneOrNone_oneMatch_returnsOptional() {
        Optional<String> result = CollectionUtils.findExactlyOneOrNone(
                TEST_LIST,
                s -> s.startsWith("b")
        );
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("banana", result.get());
    }

    @Test
    void findExactlyOneOrNone_noMatch_returnsEmpty() {
        Optional<String> result = CollectionUtils.findExactlyOneOrNone(
                TEST_LIST,
                s -> s.startsWith("z")
        );
        Assertions.assertTrue(result.isEmpty());
    }

    // findExactlyOne tests
    @Test
    void findExactlyOne_oneMatch_returnsElement() {
        String result = CollectionUtils.findExactlyOne(
                TEST_LIST,
                s -> s.startsWith("b")
        );
        Assertions.assertEquals("banana", result);
    }

    // filterByPredicate tests
    @Test
    void filterByPredicate_matchingPredicate_returnsFilteredList() {
        List<String> result = CollectionUtils.filterByPredicate(
                TEST_LIST,
                s -> s.startsWith("b") || s.startsWith("c")
        );
        Assertions.assertEquals(List.of("banana", "cherry"), result);
    }

    // emptyListIfNull tests
    @ParameterizedTest
    @NullSource
    void emptyListIfNull_nullCollection_returnsEmptyList(Collection<String> collection) {
        List<String> result = CollectionUtils.emptyListIfNull(collection);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void emptyListIfNull_nonNullCollection_returnsList() {
        List<String> result = CollectionUtils.emptyListIfNull(TEST_LIST);
        Assertions.assertEquals(TEST_LIST, result);
    }

    // hasSizeInRange tests
    @Test
    void hasSizeInRange_nullCollection_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.hasSizeInRange(null, 1, 5));
    }

    @Test
    void hasSizeInRange_sizeInRange_returnsTrue() {
        Assertions.assertTrue(CollectionUtils.hasSizeInRange(TEST_LIST, 2, 5));
    }

    @Test
    void hasSizeInRange_sizeOutOfRange_returnsFalse() {
        Assertions.assertFalse(CollectionUtils.hasSizeInRange(TEST_LIST, 4, 5));
        Assertions.assertFalse(CollectionUtils.hasSizeInRange(TEST_LIST, 1, 2));
    }


    // isNotEmpty array tests
    private static Stream<Arguments> arrayParams() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of(new Object[0], false),
                Arguments.of(new Object[1], true)
        );
    }

    @ParameterizedTest
    @MethodSource("arrayParams")
    void isNotEmpty_variousArrays_returnsExpectedResult(Object[] array, boolean expected) {
        Assertions.assertEquals(expected, CollectionUtils.isNotEmpty(array));
    }

}