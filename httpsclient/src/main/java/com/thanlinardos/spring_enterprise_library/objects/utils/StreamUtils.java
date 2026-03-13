package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Utils for processing objects in streams. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtils {

    /**
     * Will filter items in distinct call by key instead of using equals or hashcode methods.
     * <p>
     * Example filtering entities by ID in a list to get a list of distinct entities:
     * <br/>
     * {@code list.stream().filter(StreamUtils.distinctByKey(it -> it.getId())).collect(Collectors.toList())}
     *
     * @param keyExtractor The function used to extract the key that should be applied
     * @param <T>          The type of the input to the predicate.
     * @return A predicate that filters duplicates
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * Will filter items for duplicates by key.
     * Will return multiple duplicates if they exist for the same key, if you only want the first duplicate, then use distinctByKey after.
     * <p>
     * Example filtering entities by ID in a list to get a list of duplicates:
     * <br/>
     * {@code list.stream().filter(StreamUtils.duplicateByKey(it -> it.getId())).collect(Collectors.toList())}
     * <p>
     * Example filtering entities by ID in a list to get a list of distinct duplicates:
     * <br/>
     * {@code list.stream().filter(StreamUtils.duplicateByKey(it -> it.getId())).filter(StreamUtils.distinctByKey(it -> it.getId())).collect(Collectors.toList())}
     * <br/> or <br/>
     * {@code list.stream().filter(StreamUtils.duplicateByKey(it -> it.getId())).collect(Collectors.toSet())}
     *
     * @param keyExtractor The function used to extract the key that should be applied
     * @param <T>          The type of the input to the predicate.
     * @return A predicate that filters uniques
     */
    public static <T> Predicate<T> duplicateByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) != null;
    }

    /**
     * Finds either a single object or no object.
     * Throws a MORE_THAN_ONE_FOUND exception if there are more than one object.
     *
     * @param message the error message.
     * @param strings the strings to pass to the core exception method.
     * @param <T>     the element type.
     * @return optional which either contains one element or is empty.
     */
    public static <T> Collector<T, ?, Optional<T>> findExactlyOneOrNone(String message, String... strings) {
        return findExactlyOneOrNone(ErrorCode.MORE_THAN_ONE_FOUND, message, () -> strings);
    }

    /**
     * Removes duplicates in a stream based on a given {@link Comparator}.
     *
     * @param comparator the comparator used to determine whether something is a duplicate
     * @param <T>        the element type.
     * @return list of non-duplicates.
     */
    public static <T> Collector<T, ?, List<T>> removingDuplicates(Comparator<T> comparator) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<T>(comparator)), ArrayList::new);
    }

    /**
     * Finds either a single object or no object.
     * Throws a exception based on the errorCode if there are more than one object.
     *
     * @param message        the error message.
     * @param stringSupplier supplier of the strings to pass to the core exception method.
     * @param <T>            element type
     * @return optional which either contains one element or is empty.
     */
    public static <T> Collector<T, ?, Optional<T>> findExactlyOneOrNone(String message, Supplier<String[]> stringSupplier) {
        return findExactlyOneOrNone(ErrorCode.MORE_THAN_ONE_FOUND, message, stringSupplier);
    }

    /**
     * Finds either a single object or no object.
     * Throws a exception based on the errorCode if there are more than one object.
     *
     * @param errorCode the error code.
     * @param message   the error message.
     * @param strings   the strings to pass to the core exception method.
     * @param <T>       the element type.
     * @return optional which either contains one element or is empty.
     */
    public static <T> Collector<T, ?, Optional<T>> findExactlyOneOrNone(ErrorCode errorCode, String message, String... strings) {
        return findExactlyOneOrNone(errorCode, message, () -> strings);
    }

    private static <T> Collector<T, ?, Optional<T>> findExactlyOneOrNone(ErrorCode errorCode, String message, Supplier<String[]> stringSupplier) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() > 1) {
                        throw errorCode.createCoreException(message, stringSupplier.get());
                    } else if (list.size() == 1) {
                        return Optional.ofNullable(list.getFirst());
                    } else {
                        return Optional.empty();
                    }
                }
        );
    }

    /**
     * Finds a single object.
     * Throws a MORE_THAN_ONE_FOUND exception if there are more than one object.
     * Throws a NONE_FOUND exception if no objects are in the stream.
     *
     * @param message the error message.
     * @param strings the strings to pass to the core exception method.
     * @param <T>     the element type.
     * @return the single element.
     */
    public static <T> Collector<T, ?, T> findExactlyOne(String message, String... strings) {
        return findExactlyOne(message, () -> strings);
    }

    /**
     * Finds a single object.
     * Throws a MORE_THAN_ONE_FOUND exception if there are more than one object.
     * Throws a NONE_FOUND exception if no objects are in the stream.
     *
     * @param message        the error message.
     * @param stringSupplier the supplier of the strings to pass to the core exception method.
     * @param <T>            the element type.
     * @return the single element.
     */
    public static <T> Collector<T, ?, T> findExactlyOne(String message, Supplier<String[]> stringSupplier) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() > 1) {
                        throw ErrorCode.MORE_THAN_ONE_FOUND.createCoreException(message, stringSupplier.get());
                    } else if (list.size() == 1) {
                        return list.getFirst();
                    } else {
                        throw ErrorCode.NONE_FOUND.createCoreException(message, stringSupplier.get());
                    }
                }
        );
    }

    /**
     * Applies a filter to the stream, which ensures that based on the keyExtractor only one element is present.
     * If two elements with the same key is present, it throws MORE_THAN_ONE_FOUND exception.
     *
     * @param keyExtractor the function used to extract the key that should be applied
     * @param errorMessage the function used to create the error message for the exception.
     * @param strings      the strings to pass to the core exception method.
     * @param <T>          the type of the input to the predicate.
     * @param <R>          the type of the key extracted.
     * @return A predicate that filters throws exception on duplicates
     */
    public static <T, R> Predicate<T> filterExactlyOneByKey(Function<? super T, R> keyExtractor, String errorMessage, String... strings) {
        return filterExactlyOneByKey(keyExtractor, key -> errorMessage, strings);
    }

    /**
     * Applies a filter to the stream, which ensures that based on the keyExtractor only one element is present.
     * If two elements with the same key is present, it throws MORE_THAN_ONE_FOUND exception.
     *
     * @param keyExtractor the function used to extract the key that should be applied
     * @param errorFactory the function used to create the error message for the exception.
     * @param strings      the strings to pass to the core exception method.
     * @param <T>          the type of the input to the predicate.
     * @param <R>          the type of the key extracted.
     * @return A predicate that filters throws exception on duplicates
     */
    public static <T, R> Predicate<T> filterExactlyOneByKey(Function<? super T, R> keyExtractor, Function<R, String> errorFactory, String... strings) {
        Map<R, Boolean> seen = new ConcurrentHashMap<>();
        return t -> {
            R key = keyExtractor.apply(t);
            if (seen.containsKey(key)) {
                throw ErrorCode.MORE_THAN_ONE_FOUND.createCoreException(errorFactory.apply(key), strings);
            } else {
                return seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
            }
        };
    }

    /**
     * Finds the object if there is only a single element in the stream, otherwise if finds nothing.
     * This method returns an empty optional if the stream contains more than one element. If it should throw an exception on more than one element use {@link StreamUtils#findExactlyOneOrNone(String, String...)} instead.
     *
     * @param <T> the element type.
     * @return optional which either contains one element or is empty.
     */
    public static <T> Collector<T, ?, Optional<T>> findExactlyOneOtherwiseNone() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() == 1) {
                        return Optional.of(list.getFirst());
                    } else {
                        return Optional.empty();
                    }
                }
        );
    }

    /**
     * Collects the result of the stream and applies {@link CollectionUtils#isEmpty(Collection)} to it.
     *
     * @param <T> the element type.
     * @return Collector with true if the stream contains no elements otherwise false
     */
    public static <T> Collector<T, ?, Boolean> isEmpty() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                CollectionUtils::isEmpty
        );
    }

    /**
     * Collects the result of the stream and applies {@link CollectionUtils#isNotEmpty(Collection)} to it.
     *
     * @param <T> the element type.
     * @return Collector with true if the stream contains any elements, otherwise false
     */
    public static <T> Collector<T, ?, Boolean> isNotEmpty() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                CollectionUtils::isNotEmpty
        );
    }

    /**
     * Gets the stream of the collection or empty stream if the collection is null.
     *
     * @param collection the collection which could be null.
     * @param <T>        the element type.
     * @return either stream of the collection or else empty stream.
     */
    public static <T> Stream<T> ofNullable(Collection<T> collection) {
        return Stream.ofNullable(collection)
                .flatMap(Collection::stream);
    }

    /**
     * Gets the stream of the array or empty stream if the array is null.
     *
     * @param collection the collection which could be null.
     * @param <T>        the element type.
     * @return either stream of the collection or else empty stream.
     */
    public static <T> Stream<T> ofNullable(T[] collection) {
        return Stream.ofNullable(collection)
                .flatMap(Arrays::stream);
    }

    /**
     * Gets the stream of a collection from the object or empty stream if the collection or object is null.
     *
     * @param object    the object to extract the collection from.
     * @param extractor the function to extract the collection from the object.
     * @param <T>       the type of the object.
     * @param <R>       the element type of the collection.
     * @return either stream of the collection or else empty stream.
     */
    public static <T, R> Stream<R> ofNullable(T object, Function<T, Collection<R>> extractor) {
        return Stream.ofNullable(object)
                .map(extractor)
                .flatMap(Collection::stream);
    }

    /**
     * Gets the stream of collection from a non-null object.
     *
     * @param object    the object to extract the collection from.
     * @param extractor the function to extract the collection from the object
     * @param <T>       the type of the object.
     * @param <R>       the element type of the collection.
     * @return either stream of the collection or else empty stream.
     * @throws NullPointerException if input is null
     */
    public static <T, R> Stream<R> of(T object, Function<T, Collection<R>> extractor) {
        return Stream.of(object)
                .map(extractor)
                .flatMap(Collection::stream);
    }

    /**
     * Returns a Supplier of Strings, derived via {@link ObjectUtils#getDebugString(Object)}, based on the given object instances.
     *
     * @param items objects
     * @param <T>   object type
     * @return a supplier of strings for the given objects
     */
    @SafeVarargs
    public static <T> Supplier<String[]> getDebugStringSuppliers(T... items) {
        return () -> Arrays.stream(items)
                .map(ObjectUtils::getDebugString)
                .toArray(String[]::new);
    }
}
