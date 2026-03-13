package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

@CoreTest
class StreamUtilsTest {

    // Test data
    private static final TestEntity ENTITY_1 = new TestEntity(1L);
    private static final TestEntity ENTITY_2 = new TestEntity(2L);
    private static final TestEntity ENTITY_1_DUPLICATE = new TestEntity(1L);
    private static final String TEST_STRING = "test";
    private static final Collector<String, ?, Optional<String>> EXACTLY_ONE_OR_NONE = StreamUtils.findExactlyOneOrNone("Test message", "param1");
    private static final Collector<String, ?, Optional<String>> EXACTLY_ONE_OR_NONE_STRING = StreamUtils.findExactlyOneOrNone("Test message", () -> new String[]{"param1"});
    private static final Collector<String, ?, Optional<String>> EXACTLY_ONE_OR_NONE_VALIDATION = StreamUtils.findExactlyOneOrNone(ErrorCode.VALIDATION_ERROR, "Test message", "param1");
    private static final Collector<String, ?, String> EXACTLY_ONE = StreamUtils.findExactlyOne("Test message", "param1");
    private static final Collector<String, ?, String> EXACTLY_ONE_STRING = StreamUtils.findExactlyOne("Test message", () -> new String[]{"param1"});

    // distinctByKey tests
    @Test
    void distinctByKey_validData_returnsDistinctElements() {
        List<TestEntity> entities = List.of(ENTITY_1, ENTITY_1_DUPLICATE, ENTITY_2);
        List<TestEntity> result = entities.stream()
                .filter(StreamUtils.distinctByKey(TestEntity::getId))
                .toList();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(ENTITY_1));
        Assertions.assertTrue(result.contains(ENTITY_2));
    }

    @Test
    void distinctByKey_nullKeyExtractor_throwsNullPointerException() {
        Stream<TestEntity> entityStream = Stream.of(ENTITY_1, ENTITY_2)
                .filter(StreamUtils.distinctByKey(null));
        Assertions.assertThrows(NullPointerException.class, entityStream::toList);
    }

    // duplicateByKey tests
    @Test
    void duplicateByKey_validData_returnsOnlyDuplicates() {
        List<TestEntity> entities = List.of(ENTITY_1, ENTITY_1_DUPLICATE, ENTITY_2);
        List<TestEntity> result = entities.stream()
                .filter(StreamUtils.duplicateByKey(TestEntity::getId))
                .toList();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(ENTITY_1_DUPLICATE, result.getFirst());
    }

    @Test
    void duplicateByKey_noDuplicates_returnsEmpty() {
        List<TestEntity> entities = List.of(ENTITY_1, ENTITY_2);
        List<TestEntity> result = entities.stream()
                .filter(StreamUtils.duplicateByKey(TestEntity::getId))
                .toList();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void duplicateByKey_multipleDuplicates_returnsAllDuplicates() {
        List<TestEntity> entities = List.of(ENTITY_1, ENTITY_1_DUPLICATE, ENTITY_2, new TestEntity(2L));
        List<TestEntity> result = entities.stream()
                .filter(StreamUtils.duplicateByKey(TestEntity::getId))
                .toList();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(ENTITY_1_DUPLICATE));
        Assertions.assertTrue(result.contains(entities.get(3)));
    }

    // findExactlyOneOrNone tests (with message and strings)
    @Test
    void findExactlyOneOrNone_singleElement_returnsOptional() {
        List<String> items = List.of(TEST_STRING);
        Optional<String> result = items.stream()
                .collect(EXACTLY_ONE_OR_NONE);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(TEST_STRING, result.get());
    }

    @Test
    void findExactlyOneOrNone_emptyList_returnsEmpty() {
        Optional<String> result = Stream.<String>empty()
                .collect(EXACTLY_ONE_OR_NONE);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findExactlyOneOrNone_multipleElements_throwsException() {
        Stream<String> stream = Stream.of("a", "b");
        CoreException exception = Assertions.assertThrows(CoreException.class, () ->
                stream.collect(EXACTLY_ONE_OR_NONE));

        Assertions.assertEquals(ErrorCode.MORE_THAN_ONE_FOUND, exception.getErrorCode());
        Assertions.assertTrue(exception.getMessage().contains("Test message"));
    }

    // findExactlyOneOrNone tests (with message and supplier)
    @Test
    void findExactlyOneOrNone_withSupplier_singleElement_returnsOptional() {
        List<String> items = List.of(TEST_STRING);
        Optional<String> result = items.stream()
                .collect(EXACTLY_ONE_OR_NONE_STRING);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(TEST_STRING, result.get());
    }

    @Test
    void findExactlyOneOrNone_withSupplier_multipleElements_throwsException() {
        Stream<String> stream = Stream.of("a", "b");
        CoreException exception = Assertions.assertThrows(CoreException.class, () -> stream.collect(EXACTLY_ONE_OR_NONE_STRING));

        Assertions.assertEquals(ErrorCode.MORE_THAN_ONE_FOUND, exception.getErrorCode());
    }

    // findExactlyOneOrNone tests (with error code)
    @Test
    void findExactlyOneOrNone_withErrorCode_singleElement_returnsOptional() {
        List<String> items = List.of(TEST_STRING);
        Optional<String> result = items.stream()
                .collect(EXACTLY_ONE_OR_NONE_VALIDATION);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(TEST_STRING, result.get());
    }

    @Test
    void findExactlyOneOrNone_withErrorCode_multipleElements_throwsException() {
        Stream<String> stream = Stream.of("a", "b");
        CoreException exception = Assertions.assertThrows(CoreException.class, () ->
                stream.collect(EXACTLY_ONE_OR_NONE_VALIDATION));

        Assertions.assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
    }

    // removingDuplicates tests
    @Test
    void removingDuplicates_withComparator_removesDuplicates() {
        List<String> items = List.of("apple", "banana", "apple", "cherry", "banana");
        List<String> result = items.stream()
                .collect(StreamUtils.<String>removingDuplicates(Comparator.naturalOrder()));

        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.contains("apple"));
        Assertions.assertTrue(result.contains("banana"));
        Assertions.assertTrue(result.contains("cherry"));
    }

    @Test
    void removingDuplicates_nullComparator_worksNormally() {
        List<String> items = List.of("a", "b");
        List<String> result = items.stream()
                .collect(StreamUtils.<String>removingDuplicates(null));

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains("a"));
        Assertions.assertTrue(result.contains("b"));
    }

    // findExactlyOne tests (with message and strings)
    @Test
    void findExactlyOne_singleElement_returnsElement() {
        List<String> items = List.of(TEST_STRING);
        String result = items.stream()
                .collect(EXACTLY_ONE);

        Assertions.assertEquals(TEST_STRING, result);
    }

    @Test
    void findExactlyOne_emptyList_throwsException() {
        Stream<String> stream = Stream.empty();
        CoreException exception = Assertions.assertThrows(CoreException.class, () -> stream.collect(EXACTLY_ONE));

        Assertions.assertEquals(ErrorCode.NONE_FOUND, exception.getErrorCode());
    }

    @Test
    void findExactlyOne_multipleElements_throwsException() {
        Stream<String> stream = Stream.of("a", "b");
        CoreException exception = Assertions.assertThrows(CoreException.class, () -> stream.collect(EXACTLY_ONE));

        Assertions.assertEquals(ErrorCode.MORE_THAN_ONE_FOUND, exception.getErrorCode());
    }

    // findExactlyOne tests (with message and supplier)
    @Test
    void findExactlyOne_withSupplier_singleElement_returnsElement() {
        List<String> items = List.of(TEST_STRING);
        String result = items.stream()
                .collect(EXACTLY_ONE_STRING);

        Assertions.assertEquals(TEST_STRING, result);
    }

    @Test
    void findExactlyOne_withSupplier_emptyList_throwsException() {
        Stream<String> emptyStream = Stream.empty();
        CoreException exception = Assertions.assertThrows(CoreException.class, () ->
                emptyStream.collect(EXACTLY_ONE_STRING));

        Assertions.assertEquals(ErrorCode.NONE_FOUND, exception.getErrorCode());
    }

    // filterExactlyOneByKey tests (with error message)
    @Test
    void filterExactlyOneByKey_singleElement_passesFilter() {
        List<TestEntity> entities = List.of(ENTITY_1);
        List<TestEntity> result = entities.stream()
                .filter(StreamUtils.filterExactlyOneByKey(TestEntity::getId, "Duplicate found", "param1"))
                .toList();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(ENTITY_1, result.getFirst());
    }

    @Test
    void filterExactlyOneByKey_duplicateElements_throwsException() {
        Stream<TestEntity> entityStream = Stream.of(ENTITY_1, ENTITY_1_DUPLICATE)
                .filter(StreamUtils.filterExactlyOneByKey(TestEntity::getId, "Duplicate found", "param1"));
        CoreException exception = Assertions.assertThrows(CoreException.class, entityStream::toList);

        Assertions.assertEquals(ErrorCode.MORE_THAN_ONE_FOUND, exception.getErrorCode());
        Assertions.assertTrue(exception.getMessage().contains("Duplicate found"));
    }

    @Test
    void filterExactlyOneByKey_differentElements_passesFilter() {
        List<TestEntity> entities = List.of(ENTITY_1, ENTITY_2);
        List<TestEntity> result = entities.stream()
                .filter(StreamUtils.filterExactlyOneByKey(TestEntity::getId, "Duplicate found", "param1"))
                .toList();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(ENTITY_1));
        Assertions.assertTrue(result.contains(ENTITY_2));
    }

    @Test
    void filterExactlyOneByKey_firstOccurrence_returnsTrue() {
        Predicate<TestEntity> predicate = StreamUtils.filterExactlyOneByKey(TestEntity::getId, "Duplicate found", "param1");

        // First occurrence of each key should return true
        Assertions.assertTrue(predicate.test(ENTITY_1));
        Assertions.assertTrue(predicate.test(ENTITY_2));
    }

    @Test
    void filterExactlyOneByKey_putIfAbsentReturnsFalse_scenario() {
        // This test covers the edge case where putIfAbsent returns false
        // This happens when the key extractor returns different values on the two calls
        // and the second key is already in the map from a previous operation
        Predicate<TestEntity> predicate = StreamUtils.filterExactlyOneByKey(TestEntity::getId, "Duplicate found", "param1");

        // First, add a key to the map by testing a different entity
        TestEntity entity1 = new TestEntity(1L);
        Assertions.assertTrue(predicate.test(entity1)); // Adds key 1 to map

        // Now create an entity whose getId() returns different values on consecutive calls
        TestEntity trickyEntity = new TestEntity(2L) {
            private int callCount = 0;

            @Override
            public Long getId() {
                callCount++;
                // First call returns 3L (not in map), second call returns 1L (already in map)
                return callCount == 1 ? 3L : 1L;
            }
        };

        // First call: keyExtractor returns 3L, seen.containsKey(3L) is false, go to else
        // Second call: keyExtractor returns 1L, putIfAbsent(1L, Boolean.TRUE) returns existing Boolean.TRUE
        // So expression returns false, and the filter should return false
        Assertions.assertFalse(predicate.test(trickyEntity));
    }

    @Test
    void filterExactlyOneByKey_subsequentCallWithSamePredicate_returnsFalseForSecondOccurrence() {
        Predicate<TestEntity> predicate = StreamUtils.filterExactlyOneByKey(TestEntity::getId, "Duplicate found", "param1");

        // First occurrence should return true
        Assertions.assertTrue(predicate.test(ENTITY_1));

        // Second occurrence of same key should throw exception (caught by if branch)
        Assertions.assertThrows(CoreException.class, () -> predicate.test(ENTITY_1_DUPLICATE));
    }

    // filterExactlyOneByKey tests (with error factory)
    @Test
    void filterExactlyOneByKey_withErrorFactory_duplicateElements_throwsException() {
        Stream<TestEntity> entityStream = Stream.of(ENTITY_1, ENTITY_1_DUPLICATE)
                .filter(StreamUtils.filterExactlyOneByKey(TestEntity::getId, key -> "Duplicate for key: " + key, "param1"));
        CoreException exception = Assertions.assertThrows(CoreException.class, entityStream::toList);

        Assertions.assertEquals(ErrorCode.MORE_THAN_ONE_FOUND, exception.getErrorCode());
        Assertions.assertTrue(exception.getMessage().contains("Duplicate for key: 1"));
    }

    // findExactlyOneOtherwiseNone tests
    @Test
    void findExactlyOneOtherwiseNone_singleElement_returnsOptional() {
        List<String> items = List.of(TEST_STRING);
        Optional<String> result = items.stream()
                .collect(StreamUtils.findExactlyOneOtherwiseNone());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(TEST_STRING, result.get());
    }

    @Test
    void findExactlyOneOtherwiseNone_emptyList_returnsEmpty() {
        Optional<String> result = Stream.<String>empty()
                .collect(StreamUtils.findExactlyOneOtherwiseNone());

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findExactlyOneOtherwiseNone_multipleElements_returnsEmpty() {
        List<String> items = List.of("a", "b");
        Optional<String> result = items.stream()
                .collect(StreamUtils.findExactlyOneOtherwiseNone());

        Assertions.assertTrue(result.isEmpty());
    }

    // isEmpty tests
    @Test
    void isEmpty_emptyStream_returnsTrue() {
        Boolean result = Stream.<String>empty().collect(StreamUtils.isEmpty());

        Assertions.assertTrue(result);
    }

    @Test
    void isEmpty_nonEmptyStream_returnsFalse() {
        List<String> items = List.of(TEST_STRING);
        Boolean result = items.stream().collect(StreamUtils.isEmpty());

        Assertions.assertFalse(result);
    }

    // isNotEmpty tests
    @Test
    void isNotEmpty_emptyStream_returnsFalse() {
        Boolean result = Stream.<String>empty().collect(StreamUtils.isNotEmpty());

        Assertions.assertFalse(result);
    }

    @Test
    void isNotEmpty_nonEmptyStream_returnsTrue() {
        List<String> items = List.of(TEST_STRING);
        Boolean result = items.stream().collect(StreamUtils.isNotEmpty());

        Assertions.assertTrue(result);
    }

    // ofNullable tests (Collection)
    @Test
    void ofNullable_collection_validCollection_returnsStream() {
        List<String> items = List.of("a", "b", "c");
        Stream<String> result = StreamUtils.ofNullable(items);

        Assertions.assertEquals(3, result.count());
    }

    @Test
    void ofNullable_collection_nullCollection_returnsEmptyStream() {
        Stream<String> result = StreamUtils.ofNullable((Collection<String>) null);

        Assertions.assertEquals(0, result.count());
    }

    @Test
    void ofNullable_collection_emptyCollection_returnsEmptyStream() {
        List<String> items = Collections.emptyList();
        Stream<String> result = StreamUtils.ofNullable(items);

        Assertions.assertEquals(0, result.count());
    }

    // ofNullable tests (Array)
    @Test
    void ofNullable_array_validArray_returnsStream() {
        String[] items = {"a", "b", "c"};
        Stream<String> result = StreamUtils.ofNullable(items);

        Assertions.assertEquals(3, result.count());
    }

    @Test
    void ofNullable_array_nullArray_returnsEmptyStream() {
        Stream<String> result = StreamUtils.ofNullable((String[]) null);

        Assertions.assertEquals(0, result.count());
    }

    @Test
    void ofNullable_array_emptyArray_returnsEmptyStream() {
        String[] items = new String[0];
        Stream<String> result = StreamUtils.ofNullable(items);

        Assertions.assertEquals(0, result.count());
    }

    // ofNullable tests (Object with extractor)
    @Test
    void ofNullable_objectWithExtractor_validObject_returnsStream() {
        TestContainer container = new TestContainer(List.of("a", "b", "c"));
        Stream<String> result = StreamUtils.ofNullable(container, TestContainer::items);

        Assertions.assertEquals(3, result.count());
    }

    @Test
    void ofNullable_objectWithExtractor_nullObject_returnsEmptyStream() {
        Stream<String> result = StreamUtils.ofNullable(null, TestContainer::items);

        Assertions.assertEquals(0, result.count());
    }

    @Test
    void ofNullable_objectWithExtractor_nullCollection_returnsEmptyStream() {
        TestContainer container = new TestContainer(null);
        Stream<String> result = StreamUtils.ofNullable(container, c -> c.items() != null ? c.items() : Collections.emptyList());

        Assertions.assertEquals(0, result.count());
    }

    // of tests (Object with extractor)
    @Test
    void of_objectWithExtractor_validObject_returnsStream() {
        TestContainer container = new TestContainer(List.of("a", "b", "c"));
        Stream<String> result = StreamUtils.of(container, TestContainer::items);

        Assertions.assertEquals(3, result.count());
    }

    @Test
    void of_objectWithExtractor_nullObject_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, StreamUtilsTest::countStreamWithNull);
    }

    private static void countStreamWithNull() {
        Stream<String> stream = StreamUtils.of(null, TestContainer::items);
        //noinspection ResultOfMethodCallIgnored
        stream.count();
    }

    @Test
    void of_objectWithExtractor_nullCollection_returnsEmptyStream() {
        TestContainer container = new TestContainer(null);
        Stream<String> result = StreamUtils.of(container, c -> c.items() != null ? c.items() : Collections.emptyList());

        Assertions.assertEquals(0, result.count());
    }

    // getDebugStringSuppliers tests
    @Test
    void getDebugStringSuppliers_validItems_returnsSupplier() {
        Supplier<String[]> supplier = StreamUtils.getDebugStringSuppliers(ENTITY_1, ENTITY_2, TEST_STRING);
        String[] result = supplier.get();

        Assertions.assertEquals(3, result.length);
        Assertions.assertTrue(result[0].contains("TestEntity"));
        Assertions.assertTrue(result[0].contains("id=1"));
        Assertions.assertTrue(result[1].contains("TestEntity"));
        Assertions.assertTrue(result[1].contains("id=2"));
        Assertions.assertTrue(result[2].contains("test"));
    }

    @Test
    void getDebugStringSuppliers_nullItems_returnsSupplier() {
        Supplier<String[]> supplier = StreamUtils.getDebugStringSuppliers(ENTITY_1, null, TEST_STRING);
        String[] result = supplier.get();

        Assertions.assertEquals(3, result.length);
        Assertions.assertTrue(result[0].contains("TestEntity"));
        Assertions.assertTrue(result[1].contains("entity is null"));
        Assertions.assertTrue(result[2].contains("test"));
    }

    @Test
    void getDebugStringSuppliers_emptyItems_returnsEmptySupplier() {
        Supplier<String[]> supplier = StreamUtils.getDebugStringSuppliers();
        String[] result = supplier.get();

        Assertions.assertEquals(0, result.length);
    }

    // Test helper classes
    private static class TestEntity extends BasicIdJpa {
        private final Long id;

        public TestEntity(Long id) {
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

    private record TestContainer(List<String> items) {
    }
}
