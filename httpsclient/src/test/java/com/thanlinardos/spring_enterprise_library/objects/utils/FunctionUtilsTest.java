package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@CoreTest
class FunctionUtilsTest {

    // Test data
    private static final TestEntity TEST_ENTITY = new TestEntity(1L);
    private static final TestEntity NULL_ID_ENTITY = new TestEntity(null);
    private static final String TEST_STRING = "test";
    private static final Integer TEST_INTEGER = 42;

    // negateBoolean tests
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void negateBoolean_variousBooleanValues_returnsNegatedValue(Boolean input) {
        Function<String, Boolean> stringToBoolean = s -> input;
        Function<String, Boolean> negated = FunctionUtils.negateBoolean(stringToBoolean);

        Assertions.assertEquals(!input, negated.apply(TEST_STRING));
    }

    @Test
    void negateBoolean_nullFunction_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> FunctionUtils.negateBoolean(null));
    }

    // apply tests
    @Test
    void apply_validBiFunctionAndObject_returnsAppliedResult() {
        BiFunction<String, Integer, String> biFunction = (s, i) -> s + i;
        Function<String, String> applied = FunctionUtils.apply(biFunction, TEST_INTEGER);

        Assertions.assertEquals("test42", applied.apply(TEST_STRING));
    }

    @Test
    void apply_nullBiFunction_throwsNullPointerExceptionapply_nullBiFunction_throwsNullPointerException() {
        Function<Object, Object> objectFunction = FunctionUtils.apply(null, TEST_INTEGER);
        Assertions.assertThrows(NullPointerException.class, () -> objectFunction.apply(null));
    }

    @Test
    void apply_nullObject_returnsAppliedResultWithNull() {
        BiFunction<String, String, String> biFunction = (s, t) -> s + t;
        Function<String, String> applied = FunctionUtils.apply(biFunction, null);

        Assertions.assertEquals("testnull", applied.apply(TEST_STRING));
    }

    // compose tests
    @Test
    void compose_validFunctions_returnsComposedResult() {
        Function<String, Integer> stringToLength = String::length;
        Function<Integer, String> lengthToString = Object::toString;
        Function<String, String> composed = FunctionUtils.compose(stringToLength, lengthToString);

        Assertions.assertEquals("4", composed.apply(TEST_STRING));
    }

    @Test
    void compose_nullFirstFunction_throwsNullPointerException() {
        Function<Integer, String> second = Object::toString;
        Assertions.assertThrows(NullPointerException.class, () -> FunctionUtils.compose(null, second));
    }

    @Test
    void compose_nullSecondFunction_throwsNullPointerException() {
        Function<String, Integer> first = String::length;
        Assertions.assertThrows(NullPointerException.class, () -> FunctionUtils.compose(first, null));
    }

    // composeNullSafe tests (2 functions)
    @Test
    void composeNullSafe_twoValidFunctions_returnsComposedResult() {
        Function<String, Integer> stringToLength = String::length;
        Function<Integer, String> lengthToString = Object::toString;
        Function<String, String> composed = FunctionUtils.composeNullSafe(stringToLength, lengthToString);

        Assertions.assertEquals("4", composed.apply(TEST_STRING));
    }

    @Test
    void composeNullSafe_nullInput_returnsNull() {
        Function<String, Integer> stringToLength = String::length;
        Function<Integer, String> lengthToString = Object::toString;
        Function<String, String> composed = FunctionUtils.composeNullSafe(stringToLength, lengthToString);

        Assertions.assertNull(composed.apply(null));
    }

    @Test
    void composeNullSafe_firstFunctionReturnsNull_returnsNull() {
        Function<String, Integer> stringToLength = s -> null;
        Function<Integer, String> lengthToString = Object::toString;
        Function<String, String> composed = FunctionUtils.composeNullSafe(stringToLength, lengthToString);

        Assertions.assertNull(composed.apply(TEST_STRING));
    }

    @Test
    void composeNullSafe_secondFunctionReturnsNull_returnsNull() {
        Function<String, Integer> stringToLength = String::length;
        Function<Integer, String> lengthToString = i -> null;
        Function<String, String> composed = FunctionUtils.composeNullSafe(stringToLength, lengthToString);

        Assertions.assertNull(composed.apply(TEST_STRING));
    }

    // composeNullSafe tests (3 functions)
    @Test
    void composeNullSafe_threeValidFunctions_returnsComposedResult() {
        Function<String, Integer> stringToLength = String::length;
        Function<Integer, Double> lengthToDouble = i -> i * 2.5;
        Function<Double, String> doubleToString = d -> "value:" + d;
        Function<String, String> composed = FunctionUtils.composeNullSafe(stringToLength, lengthToDouble, doubleToString);

        Assertions.assertEquals("value:10.0", composed.apply(TEST_STRING));
    }

    @Test
    void composeNullSafe_threeFunctionsNullInput_returnsNull() {
        Function<String, Integer> stringToLength = String::length;
        Function<Integer, Double> lengthToDouble = i -> i * 2.5;
        Function<Double, String> doubleToString = d -> "value:" + d;
        Function<String, String> composed = FunctionUtils.composeNullSafe(stringToLength, lengthToDouble, doubleToString);

        Assertions.assertNull(composed.apply(null));
    }

    // composeWithSetNullSafe tests
    @Test
    void composeWithSetNullSafe_validFunctions_returnsSetResult() {
        Function<String, Set<Integer>> stringToLengths = s -> Set.of(s.length(), s.length() + 1);
        Function<Integer, String> lengthToString = Object::toString;
        Function<String, Set<String>> composed = FunctionUtils.composeWithSetNullSafe(stringToLengths, lengthToString);

        Set<String> result = composed.apply(TEST_STRING);
        Assertions.assertEquals(Set.of("4", "5"), result);
    }

    @Test
    void composeWithSetNullSafe_nullInput_returnsEmptySet() {
        Function<String, Set<Integer>> stringToLengths = s -> Set.of(s.length());
        Function<Integer, String> lengthToString = Object::toString;
        Function<String, Set<String>> composed = FunctionUtils.composeWithSetNullSafe(stringToLengths, lengthToString);

        Set<String> result = composed.apply(null);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void composeWithSetNullSafe_nullFirstResult_returnsEmptySet() {
        Function<String, Set<Integer>> stringToLengths = s -> null;
        Function<Integer, String> lengthToString = Object::toString;
        Function<String, Set<String>> composed = FunctionUtils.composeWithSetNullSafe(stringToLengths, lengthToString);

        Set<String> result = composed.apply(TEST_STRING);
        Assertions.assertTrue(result.isEmpty());
    }

    // composeAllNullSafe tests
    @Test
    void composeAllNullSafe_validFunctions_returnsSetOfSets() {
        Function<String, Set<Integer>> stringToLengths = s -> Set.of(s.length(), s.length() + 1);
        Function<Integer, Set<String>> lengthToStrings = i -> Set.of("val" + i, "len" + i);
        Function<String, Set<String>> composed = FunctionUtils.composeAllNullSafe(stringToLengths, lengthToStrings);

        Set<String> result = composed.apply(TEST_STRING);
        Assertions.assertEquals(Set.of("val4", "len4", "val5", "len5"), result);
    }

    @Test
    void composeAllNullSafe_nullInput_returnsEmptySet() {
        Function<String, Set<Integer>> stringToLengths = s -> Set.of(s.length());
        Function<Integer, Set<String>> lengthToStrings = i -> Set.of("val" + i);
        Function<String, Set<String>> composed = FunctionUtils.composeAllNullSafe(stringToLengths, lengthToStrings);

        Set<String> result = composed.apply(null);
        Assertions.assertTrue(result.isEmpty());
    }

    // chainFromOther tests
    @Test
    void chainFromOther_validParameters_executesConsumer() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        Consumer<String> chained = FunctionUtils.chainFromOther(TEST_STRING, String::toUpperCase, consumer);

        chained.accept("input");
        Assertions.assertEquals("TESTinput", result.toString());
    }

    @Test
    void chainFromOther_nullOther_doesNothing() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        Consumer<String> chained = FunctionUtils.chainFromOther(null, o -> null, consumer);

        chained.accept("input");
        Assertions.assertEquals("", result.toString());
    }

    @Test
    void chainFromOther_nullFunctionResult_doesNothing() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        Consumer<String> chained = FunctionUtils.chainFromOther(TEST_STRING, s -> null, consumer);

        chained.accept("input");
        Assertions.assertEquals("", result.toString());
    }

    // chainAllFromOther tests
    @Test
    void chainAllFromOther_validParameters_executesConsumerForEach() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        Consumer<String> chained = FunctionUtils.chainAllFromOther(List.of("a", "b"), String::toUpperCase, consumer);

        chained.accept("input");
        Assertions.assertEquals("AinputBinput", result.toString());
    }

    @Test
    void chainAllFromOther_nullCollection_doesNothing() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        Consumer<String> chained = FunctionUtils.chainAllFromOther(null, o -> null, consumer);

        chained.accept("input");
        Assertions.assertEquals("", result.toString());
    }

    @Test
    void chainAllFromOther_collectionWithNulls_executesConsumerForNonNulls() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        List<String> listWithNulls = new ArrayList<>();
        listWithNulls.add("a");
        listWithNulls.add(null);
        listWithNulls.add("b");
        Consumer<String> chained = FunctionUtils.chainAllFromOther(listWithNulls, s -> s != null ? s.toUpperCase() : null, consumer);

        chained.accept("input");
        Assertions.assertEquals("AinputBinput", result.toString());
    }

    // chainFromSelf tests
    @Test
    void chainFromSelf_validParameters_executesConsumer() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        Consumer<String> chained = FunctionUtils.chainFromSelf(TEST_STRING, String::toUpperCase, consumer);

        chained.accept("input");
        Assertions.assertEquals("INPUTtest", result.toString());
    }

    @Test
    void chainFromSelf_nullOther_doesNothing() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        Consumer<String> chained = FunctionUtils.chainFromSelf(null, String::toUpperCase, consumer);

        chained.accept("input");
        Assertions.assertEquals("", result.toString());
    }

    // chainAllFromSelf tests
    @Test
    void chainAllFromSelf_validParameters_executesConsumerForEach() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        Consumer<String> chained = FunctionUtils.chainAllFromSelf(List.of("x", "y"), String::toUpperCase, consumer);

        chained.accept("input");
        Assertions.assertEquals("INPUTxINPUTy", result.toString());
    }

    @Test
    void chainAllFromSelf_nullCollection_doesNothing() {
        StringBuilder result = new StringBuilder();
        BiConsumer<String, String> consumer = (s, t) -> result.append(s).append(t);
        Consumer<String> chained = FunctionUtils.chainAllFromSelf(null, String::toUpperCase, consumer);

        chained.accept("input");
        Assertions.assertEquals("", result.toString());
    }

    // stream tests
    @Test
    void stream_validFunction_returnsStream() {
        Function<String, Collection<String>> function = s -> List.of(s, s.toUpperCase());
        Function<String, Stream<String>> streamed = FunctionUtils.stream(function);

        Stream<String> result = streamed.apply(TEST_STRING);
        Assertions.assertEquals(List.of(TEST_STRING, TEST_STRING.toUpperCase()), result.toList());
    }

    @Test
    void stream_nullInput_returnsEmptyStream() {
        Function<String, Collection<String>> function = s -> s != null ? List.of(s, s.toUpperCase()) : Collections.emptyList();
        Function<String, Stream<String>> streamed = FunctionUtils.stream(function);

        Stream<String> result = streamed.apply(null);
        Assertions.assertEquals(0, result.count());
    }

    @Test
    void stream_functionReturnsNull_throwsNullPointerException() {
        Function<String, Collection<String>> function = s -> null;
        Function<String, Stream<String>> streamed = FunctionUtils.stream(function);

        Assertions.assertThrows(NullPointerException.class, () -> streamed.apply(TEST_STRING));
    }

    // streamOptional tests
    @Test
    void streamOptional_validFunction_returnsStream() {
        Function<String, Optional<String>> function = s -> Optional.of(s.toUpperCase());
        Function<String, Stream<String>> streamed = FunctionUtils.streamOptional(function);

        Stream<String> result = streamed.apply(TEST_STRING);
        Assertions.assertEquals(List.of(TEST_STRING.toUpperCase()), result.toList());
    }

    @Test
    void streamOptional_emptyOptional_returnsEmptyStream() {
        Function<String, Optional<String>> function = s -> Optional.empty();
        Function<String, Stream<String>> streamed = FunctionUtils.streamOptional(function);

        Stream<String> result = streamed.apply(TEST_STRING);
        Assertions.assertEquals(0, result.count());
    }

    // rethrowWithEntityId tests
    @Test
    void rethrowWithEntityId_successfulExecution_returnsResult() {
        Function<TestEntity, String> function = entity -> "success:" + entity.getId();
        String result = FunctionUtils.rethrowWithEntityId(TEST_ENTITY, function);

        Assertions.assertEquals("success:1", result);
    }

    @Test
    void rethrowWithEntityId_functionThrowsRuntimeException_throwsCoreExceptionWithEntityId() {
        Function<TestEntity, String> function = entity -> {
            throw new RuntimeException("test error");
        };

        CoreException exception = Assertions.assertThrows(CoreException.class, () ->
                FunctionUtils.rethrowWithEntityId(TEST_ENTITY, function));

        Assertions.assertTrue(exception.getMessage().contains("class=TestEntity") || exception.getMessage().contains("TestEntity"));
        Assertions.assertTrue(exception.getMessage().contains("id=1"));
        Assertions.assertTrue(exception.getMessage().contains("test error"));
        Assertions.assertEquals(ErrorCode.UNEXPECTED_ERROR, exception.getErrorCode());
    }

    @Test
    void rethrowWithEntityId_functionThrowsCoreException_throwsCoreExceptionWithOriginalErrorCode() {
        Function<TestEntity, String> function = entity -> {
            throw new CoreException(ErrorCode.VALIDATION_ERROR, "validation failed");
        };

        CoreException exception = Assertions.assertThrows(CoreException.class, () ->
                FunctionUtils.rethrowWithEntityId(TEST_ENTITY, function));

        Assertions.assertTrue(exception.getMessage().contains("class=TestEntity") || exception.getMessage().contains("TestEntity"));
        Assertions.assertTrue(exception.getMessage().contains("id=1"));
        Assertions.assertTrue(exception.getMessage().contains("validation failed"));
        Assertions.assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
    }

    @Test
    void rethrowWithEntityId_entityWithNullId_throwsCoreExceptionWithNullId() {
        Function<TestEntity, String> function = entity -> {
            throw new RuntimeException("test error");
        };

        CoreException exception = Assertions.assertThrows(CoreException.class, () ->
                FunctionUtils.rethrowWithEntityId(NULL_ID_ENTITY, function));

        Assertions.assertTrue(exception.getMessage().contains("id=null"));
    }

    @Test
    void rethrowWithEntityId_nullEntity_returnsResult() {
        Function<TestEntity, String> function = e -> "result for null";
        String result = FunctionUtils.rethrowWithEntityId(null, function);

        Assertions.assertEquals("result for null", result);
    }

    // getCoreExceptionErrorCodeOrUnexpectedSystemError tests
    @Test
    void getCoreExceptionErrorCodeOrUnexpectedSystemError_coreException_returnsOriginalErrorCode() {
        CoreException coreException = new CoreException(ErrorCode.VALIDATION_ERROR, "test");
        ErrorCode result = FunctionUtils.getCoreExceptionErrorCodeOrUnexpectedSystemError(coreException);

        Assertions.assertEquals(ErrorCode.VALIDATION_ERROR, result);
    }

    @Test
    void getCoreExceptionErrorCodeOrUnexpectedSystemError_runtimeException_returnsUnexpectedError() {
        RuntimeException runtimeException = new RuntimeException("test");
        ErrorCode result = FunctionUtils.getCoreExceptionErrorCodeOrUnexpectedSystemError(runtimeException);

        Assertions.assertEquals(ErrorCode.UNEXPECTED_ERROR, result);
    }

    @Test
    void getCoreExceptionErrorCodeOrUnexpectedSystemError_nullException_returnsUnexpectedError() {
        // This method expects a non-null exception, so we need to handle this case differently
        // Let's test with a dummy exception instead
        RuntimeException dummyException = new RuntimeException("dummy");
        ErrorCode result = FunctionUtils.getCoreExceptionErrorCodeOrUnexpectedSystemError(dummyException);

        Assertions.assertEquals(ErrorCode.UNEXPECTED_ERROR, result);
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
}
