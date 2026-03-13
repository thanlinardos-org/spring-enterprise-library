package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@CoreTest
class ObjectUtilsTest {

    // Test data
    private static final String TEST_STRING = "test";
    private static final TestEntity TEST_ENTITY = new TestEntity(1L);
    private static final TestEntity NULL_ID_ENTITY = new TestEntity(null);

    // isAnyObjectNull tests
    private static Stream<Arguments> isAnyObjectNullParams() {
        return Stream.of(
                Arguments.of(null, new Object[]{}, true),
                Arguments.of(TEST_STRING, new Object[]{}, false),
                Arguments.of(TEST_STRING, new Object[]{null}, true),
                Arguments.of(TEST_STRING, new Object[]{TEST_STRING, null}, true),
                Arguments.of(TEST_STRING, new Object[]{TEST_STRING, TEST_STRING}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isAnyObjectNullParams")
    void isAnyObjectNull_variousObjects_returnsExpectedResult(Object object, Object[] objects, boolean expected) {
        Assertions.assertEquals(expected, ObjectUtils.isAnyObjectNull(object, objects));
    }

    // isAnyObjectNotNull tests
    private static Stream<Arguments> isAnyObjectNotNullParams() {
        return Stream.of(
                Arguments.of(null, new Object[]{}, false),
                Arguments.of(TEST_STRING, new Object[]{}, true),
                Arguments.of(null, new Object[]{TEST_STRING}, true),
                Arguments.of(null, new Object[]{null, null}, false),
                Arguments.of(TEST_STRING, new Object[]{null, TEST_STRING}, true)
        );
    }

    @ParameterizedTest
    @MethodSource("isAnyObjectNotNullParams")
    void isAnyObjectNotNull_variousObjects_returnsExpectedResult(Object object, Object[] objects, boolean expected) {
        Assertions.assertEquals(expected, ObjectUtils.isAnyObjectNotNull(object, objects));
    }

    // isAllObjectsNull tests
    private static Stream<Arguments> isAllObjectsNullParams() {
        return Stream.of(
                Arguments.of(null, new Object[]{}, true),
                Arguments.of(TEST_STRING, new Object[]{}, false),
                Arguments.of(null, new Object[]{null}, true),
                Arguments.of(null, new Object[]{TEST_STRING}, false),
                Arguments.of(null, new Object[]{null, null, TEST_STRING}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isAllObjectsNullParams")
    void isAllObjectsNull_variousObjects_returnsExpectedResult(Object object, Object[] objects, boolean expected) {
        Assertions.assertEquals(expected, ObjectUtils.isAllObjectsNull(object, objects));
    }

    // isAllObjectsNotNull tests
    private static Stream<Arguments> isAllObjectsNotNullParams() {
        return Stream.of(
                Arguments.of(TEST_STRING, new Object[]{}, true),
                Arguments.of(null, new Object[]{}, false),
                Arguments.of(TEST_STRING, new Object[]{TEST_STRING}, true),
                Arguments.of(TEST_STRING, new Object[]{null}, false),
                Arguments.of(TEST_STRING, new Object[]{TEST_STRING, null, TEST_STRING}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isAllObjectsNotNullParams")
    void isAllObjectsNotNull_variousObjects_returnsExpectedResult(Object object, Object[] objects, boolean expected) {
        Assertions.assertEquals(expected, ObjectUtils.isAllObjectsNotNull(object, objects));
    }

    // isAllObjectsEquals tests
    private static Stream<Arguments> isAllObjectsEqualsParams() {
        return Stream.of(
                Arguments.of(TEST_STRING, new Object[]{}, true),
                Arguments.of(TEST_STRING, new Object[]{TEST_STRING}, true),
                Arguments.of(TEST_STRING, new Object[]{TEST_STRING, TEST_STRING}, true),
                Arguments.of(TEST_STRING, new Object[]{"different"}, false),
                Arguments.of(TEST_STRING, new Object[]{TEST_STRING, "different"}, false),
                Arguments.of(null, new Object[]{null, null}, true),
                Arguments.of(null, new Object[]{null, TEST_STRING}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isAllObjectsEqualsParams")
    void isAllObjectsEquals_variousObjects_returnsExpectedResult(Object object, Object[] objects, boolean expected) {
        Assertions.assertEquals(expected, ObjectUtils.isAllObjectsEquals(object, objects));
    }

    // isAllObjectsNotNullAndEquals tests
    private static Stream<Arguments> isAllObjectsNotNullAndEqualsParams() {
        return Stream.of(
                Arguments.argumentSet("2 nulls", false, null, null, null),
                Arguments.argumentSet("1 null, 1 not null", false, null, new Object[]{LocalDate.MAX}),
                Arguments.argumentSet("1 not null, 1 null", false, LocalDate.MAX, new Object[]{null}),
                Arguments.argumentSet("1 not null, 1 not equal", false, LocalDate.MAX, new Object[]{LocalDate.MIN}),
                Arguments.argumentSet("1 not null, 1 equal", true, LocalDate.MAX, new Object[]{LocalDate.MAX}),
                Arguments.argumentSet("1 not null, 2 equal", true, LocalDate.MAX, new Object[]{LocalDate.MAX, LocalDate.MAX}),
                Arguments.argumentSet("1 not null, 2 not equal", false, LocalDate.MAX, new Object[]{LocalDate.MIN, LocalDate.of(2024, 1, 1)}),
                Arguments.argumentSet("1 not null, 1 equal, 1 not equal", false, LocalDate.MAX, new Object[]{LocalDate.MAX, LocalDate.MIN})
        );
    }

    @ParameterizedTest
    @MethodSource("isAllObjectsNotNullAndEqualsParams")
    void isAllObjectsNotNullAndEquals(boolean expected, Object object, Object... others) {
        boolean actual = ObjectUtils.isAllObjectsNotNullAndEquals(object, others);
        Assertions.assertEquals(expected, actual);
    }

    // evaluateAllTrue tests
    private static Stream<Arguments> evaluateAllTrueParams() {
        return Stream.of(
                Arguments.of(new Supplier<?>[]{}, true),
                Arguments.of(new Supplier<?>[]{() -> true}, true),
                Arguments.of(new Supplier<?>[]{() -> false}, false),
                Arguments.of(new Supplier<?>[]{() -> true, () -> true}, true),
                Arguments.of(new Supplier<?>[]{() -> true, () -> false}, false),
                Arguments.of(new Supplier<?>[]{() -> false, () -> false}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateAllTrueParams")
    void evaluateAllTrue_variousSuppliers_returnsExpectedResult(Supplier<Boolean>[] suppliers, boolean expected) {
        Assertions.assertEquals(expected, ObjectUtils.evaluateAllTrue(suppliers));
    }

    // evaluateAnyTrue tests
    private static Stream<Arguments> evaluateAnyTrueParams() {
        return Stream.of(
                Arguments.of(new Supplier<?>[]{}, false),
                Arguments.of(new Supplier<?>[]{() -> true}, true),
                Arguments.of(new Supplier<?>[]{() -> false}, false),
                Arguments.of(new Supplier<?>[]{() -> true, () -> true}, true),
                Arguments.of(new Supplier<?>[]{() -> true, () -> false}, true),
                Arguments.of(new Supplier<?>[]{() -> false, () -> false}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateAnyTrueParams")
    void evaluateAnyTrue_variousSuppliers_returnsExpectedResult(Supplier<Boolean>[] suppliers, boolean expected) {
        Assertions.assertEquals(expected, ObjectUtils.evaluateAnyTrue(suppliers));
    }

    // getFirstNonNull tests
    private static Stream<Arguments> getFirstNonNullParams() {
        return Stream.of(
                Arguments.of(new Supplier<?>[]{}, Optional.empty()),
                Arguments.of(new Supplier<?>[]{() -> null}, Optional.empty()),
                Arguments.of(new Supplier<?>[]{() -> null, () -> null}, Optional.empty()),
                Arguments.of(new Supplier<?>[]{() -> "first"}, Optional.of("first")),
                Arguments.of(new Supplier<?>[]{() -> null, () -> "second"}, Optional.of("second")),
                Arguments.of(new Supplier<?>[]{() -> "first", () -> "second"}, Optional.of("first"))
        );
    }

    @ParameterizedTest
    @MethodSource("getFirstNonNullParams")
    void getFirstNonNull_variousSuppliers_returnsExpectedResult(Supplier<String>[] suppliers, Optional<String> expected) {
        Optional<String> result = ObjectUtils.getFirstNonNull(suppliers);
        Assertions.assertEquals(expected, result);
    }

    // getFirstPresent tests
    private static Stream<Arguments> getFirstPresentParams() {
        return Stream.of(
                Arguments.of(new Supplier<?>[]{}, Optional.empty()),
                Arguments.of(new Supplier<?>[]{Optional::empty}, Optional.empty()),
                Arguments.of(new Supplier<?>[]{Optional::empty, Optional::empty}, Optional.empty()),
                Arguments.of(new Supplier<?>[]{() -> Optional.of("first")}, Optional.of("first")),
                Arguments.of(new Supplier<?>[]{Optional::empty, () -> Optional.of("second")}, Optional.of("second")),
                Arguments.of(new Supplier<?>[]{() -> Optional.of("first"), () -> Optional.of("second")}, Optional.of("first"))
        );
    }

    @ParameterizedTest
    @MethodSource("getFirstPresentParams")
    void getFirstPresent_variousSuppliers_returnsExpectedResult(Supplier<Optional<String>>[] suppliers, Optional<String> expected) {
        Optional<String> result = ObjectUtils.getFirstPresent(suppliers);
        Assertions.assertEquals(expected, result);
    }

    // getOrNull tests
    @ParameterizedTest
    @NullSource
    void getOrNull_nullObject_returnsNull(String object) {
        String result = ObjectUtils.getOrNull(object, String::toUpperCase);
        Assertions.assertNull(result);
    }

    @Test
    void getOrNull_nonNullObject_returnsMappedValue() {
        String result = ObjectUtils.getOrNull("hello", String::toUpperCase);
        Assertions.assertEquals("HELLO", result);
    }

    // getIdOrNull tests
    @ParameterizedTest
    @NullSource
    void getIdOrNull_nullEntity_returnsNull(BasicIdJpa entity) {
        Long result = ObjectUtils.getIdOrNull(entity);
        Assertions.assertNull(result);
    }

    @Test
    void getIdOrNull_entityWithId_returnsId() {
        Long result = ObjectUtils.getIdOrNull(TEST_ENTITY);
        Assertions.assertEquals(1L, result);
    }

    @Test
    void getIdOrNull_entityWithNullId_returnsNull() {
        Long result = ObjectUtils.getIdOrNull(NULL_ID_ENTITY);
        Assertions.assertNull(result);
    }

    // getCommaSeparatedListOfIds tests
    @Test
    void getCommaSeparatedListOfIds_emptyCollection_returnsEmptyString() {
        String result = ObjectUtils.getCommaSeparatedListOfIds(Collections.emptyList());
        Assertions.assertEquals("", result);
    }

    @Test
    void getCommaSeparatedListOfIds_singleEntity_returnsId() {
        String result = ObjectUtils.getCommaSeparatedListOfIds(List.of(TEST_ENTITY));
        Assertions.assertEquals("1", result);
    }

    @Test
    void getCommaSeparatedListOfIds_multipleEntities_returnsCommaSeparatedIds() {
        String result = ObjectUtils.getCommaSeparatedListOfIds(List.of(TEST_ENTITY, new TestEntity(2L), new TestEntity(3L)));
        Assertions.assertEquals("1, 2, 3", result);
    }

    @Test
    void getCommaSeparatedListOfIds_entitiesWithNullIds_returnsCommaSeparatedIds() {
        String result = ObjectUtils.getCommaSeparatedListOfIds(List.of(TEST_ENTITY, NULL_ID_ENTITY, new TestEntity(3L)));
        Assertions.assertEquals("1, null, 3", result);
    }

    // getIds tests
    @Test
    void getIds_emptyCollection_returnsEmptySet() {
        Set<Long> result = ObjectUtils.getIds(Collections.emptyList());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getIds_singleEntity_returnsSingletonSet() {
        Set<Long> result = ObjectUtils.getIds(List.of(TEST_ENTITY));
        Assertions.assertEquals(Set.of(1L), result);
    }

    @Test
    void getIds_multipleEntities_returnsSetOfIds() {
        Set<Long> result = ObjectUtils.getIds(List.of(TEST_ENTITY, new TestEntity(2L), new TestEntity(3L)));
        Assertions.assertEquals(Set.of(1L, 2L, 3L), result);
    }

    @Test
    void getIds_duplicateIds_returnsUniqueSet() {
        Set<Long> result = ObjectUtils.getIds(List.of(TEST_ENTITY, new TestEntity(1L), new TestEntity(2L)));
        Assertions.assertEquals(Set.of(1L, 2L), result);
    }

    // getDebugString tests
    @Test
    void getDebugString_nullEntity_returnsNullString() {
        String result = ObjectUtils.getDebugString((Object) null);
        Assertions.assertEquals("{entity is null}", result);
    }

    @Test
    void getDebugString_nullCollection_returnsEmptyCollectionString() {
        String result = ObjectUtils.getDebugString((Collection<Object>) null);
        Assertions.assertEquals("[collection is empty]", result);
    }

    @Test
    void getDebugString_emptyCollection_returnsEmptyCollectionString() {
        String result = ObjectUtils.getDebugString(Collections.emptyList());
        Assertions.assertEquals("[collection is empty]", result);
    }

    @Test
    void getDebugString_collectionWithEntities_returnsDebugString() {
        String result = ObjectUtils.getDebugString(List.of(TEST_ENTITY, new TestEntity(2L)));
        Assertions.assertTrue(result.contains("TestEntity"));
        Assertions.assertTrue(result.contains("id=1"));
        Assertions.assertTrue(result.contains("id=2"));
    }

    @Test
    void getDebugString_collectionWithEntitiesCastAsObject_returnsDebugString() {
        String result = ObjectUtils.getDebugString((Object) List.of(TEST_ENTITY, new TestEntity(2L)));
        Assertions.assertTrue(result.contains("TestEntity"));
        Assertions.assertTrue(result.contains("id=1"));
        Assertions.assertTrue(result.contains("id=2"));
    }

    @Test
    void getDebugString_collectionWithNullEntities_returnsDebugString() {
        List<TestEntity> entities = new ArrayList<>();
        entities.add(TEST_ENTITY);
        entities.add(null);
        entities.add(new TestEntity(2L));

        String result = ObjectUtils.getDebugString(entities);
        Assertions.assertTrue(result.contains("TestEntity"));
        Assertions.assertTrue(result.contains("id=1"));
        Assertions.assertTrue(result.contains("id=2"));
        Assertions.assertTrue(result.contains("entity is null"));
    }

    @Test
    void getDebugString_entityWithId_returnsDebugString() {
        String result = ObjectUtils.getDebugString(TEST_ENTITY);
        Assertions.assertEquals("{class=TestEntity, id=1, toString=TestEntity{id=1}}", result);
    }

    @Test
    void getDebugString_entitySupplierWithId_returnsDebugString() {
        String result = ObjectUtils.getDebugString(getBiPredicate());
        Assertions.assertTrue(result.contains("{lambda=ObjectUtilsTest$$Lambda"));
        Assertions.assertTrue(result.contains("parameterTypes=[java.lang.Object, java.lang.Object], returnType=boolean}"));
    }

    private static BiPredicate<Object, Object> getBiPredicate() {
        return Objects::equals;
    }

    @Test
    void getDebugString_entityWithoutId_returnsDebugString() {
        String result = ObjectUtils.getDebugString(NULL_ID_ENTITY);
        Assertions.assertEquals("{class=TestEntity, id=null (from WithId#getId), toString=TestEntity{id=null}}", result);
    }

    @Test
    void getDebugString_entityWithoutWithIdInterface_returnsDebugString() {
        ObjectWithoutId entity = new ObjectWithoutId();
        String result = ObjectUtils.getDebugString(entity);
        Assertions.assertEquals("{class=ObjectWithoutId, id=null (no WithId#getId method), toString=ObjectWithoutId{}}", result);
    }

    @Test
    void getDebugString_collectionMethod_returnsCorrectFormat() {
        String result = ObjectUtils.getDebugString(List.of(TEST_ENTITY));
        Assertions.assertTrue(result.startsWith("["));
        Assertions.assertTrue(result.endsWith("]"));
        Assertions.assertTrue(result.contains("TestEntity"));
    }

    // applyIfBothNotNull tests
    @Test
    void applyIfBothNotNull_bothNull_returnsEmpty() {
        Optional<Object> result = ObjectUtils.applyIfBothNotNull(null, null, (a, b) -> a);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void applyIfBothNotNull_firstNull_returnsEmpty() {
        Optional<String> result = ObjectUtils.applyIfBothNotNull(null, "b", (a, b) -> a + b);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void applyIfBothNotNull_secondNull_returnsEmpty() {
        Optional<String> result = ObjectUtils.applyIfBothNotNull("a", null, (a, b) -> a + b);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void applyIfBothNotNull_bothNotNull_returnsResult() {
        Optional<String> result = ObjectUtils.applyIfBothNotNull("a", "b", (a, b) -> a + b);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("ab", result.get());
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

    private static class ObjectWithoutId {
        @Override
        public String toString() {
            return "ObjectWithoutId{}";
        }
    }
}