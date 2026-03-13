package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import com.thanlinardos.spring_enterprise_library.objects.iterators.FilterIterator;
import com.thanlinardos.spring_enterprise_library.objects.iterators.IteratorChain;
import com.thanlinardos.spring_enterprise_library.objects.iterators.UnmodifiableIterator;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.thanlinardos.spring_enterprise_library.objects.utils.PredicateUtils.isNotContainedIn;

/**
 * Utility class for handling collections.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtils {

    /**
     * Returns whether the collection is null or empty.
     *
     * @param collection the collection.
     * @param <T>        collection element type
     * @return true if the collection is empty or null.
     */
    public static <T> boolean isEmpty(@Nullable Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Returns whether the collection is not null or empty.
     *
     * @param collection the collection.
     * @param <T>        collection element type
     * @return true if the collection is not empty or null.
     */
    public static <T> boolean isNotEmpty(@Nullable Collection<T> collection) {
        return !isEmpty(collection);
    }

    /**
     * Validates that the collection is not null or empty.
     *
     * @param collection the collection.
     * @param <T>        collection element type
     * @param <U>        collection type
     * @return the collection if not null or empty
     * @throws CoreException if the collection is null or empty
     */
    public static <T, U extends Collection<T>> U requireNotEmpty(@Nullable U collection) {
        if (isEmpty(collection)) {
            throw new CoreException(ErrorCode.ILLEGAL_ARGUMENT, "Collection argument is null or empty");
        }
        return collection;
    }

    /**
     * Returns whether the given collection contains an element that matches the predicate.
     * <p>
     * Returns {@code false} if the collection is null.
     *
     * @param collection the collection.
     * @param predicate  the predicate.
     * @param <T>        collection element type
     * @return true if an element matching the predicate is found, otherwise false.
     */
    public static <T> boolean contains(@Nullable Collection<T> collection, Predicate<T> predicate) {
        return StreamUtils.ofNullable(collection)
                .anyMatch(predicate);
    }

    /**
     * Returns whether the given collection contains an element that matches any value.
     *
     * @param collection the collection
     * @param values     values to check for
     * @param <T>        collection element type
     * @return true if any values is found in the collection, false otherwise.
     */
    @SafeVarargs
    public static <T> boolean containsAnyOf(Collection<T> collection, T... values) {
        return StreamUtils.ofNullable(values)
                .anyMatch(collection::contains);
    }

    /**
     * Returns whether the given array contains an element that matches the predicate.
     * <p>
     * Returns {@code false} if the collection is null.
     *
     * @param collection the collection.
     * @param predicate  the predicate.
     * @param <T>        collection element type
     * @return true if an element matching the predicate is found, otherwise false.
     */
    public static <T> boolean contains(T[] collection, Predicate<T> predicate) {
        return StreamUtils.ofNullable(collection)
                .anyMatch(predicate);
    }

    /**
     * Returns true if the result of the {@code getter} is null for any element of the collection.
     *
     * @param collection the collection.
     * @param getter     a getter of the collection element type
     * @param <A>        collection element type
     * @param <B>        collection element getter return type
     * @return true if the result of the {@code getter} is null for any element of the collection
     */
    public static <A, B> boolean containsAnyNull(Collection<A> collection, Function<A, B> getter) {
        return collection.stream()
                .map(getter)
                .anyMatch(Objects::isNull);
    }

    /**
     * Returns whether the given collection does not contain an element that matches the predicate.
     * <p>
     * <b>WARNING</b> Returns {@code true} if the collection is null.
     *
     * @param collection the collection.
     * @param predicate  the predicate.
     * @param <T>        collection element type
     * @return true if no elements matches the predicate, otherwise false.
     */
    public static <T> boolean notContains(Collection<T> collection, Predicate<T> predicate) {
        return !contains(collection, predicate);
    }

    /**
     * Returns if the given value is in the supplied values.
     * Is null safe.
     *
     * @param target the input value to check.
     * @param values the values to check against.
     * @param <T>    collection element type
     * @return true if the value is in the list of supplied values, otherwise false.
     */
    @SafeVarargs
    public static <T> boolean isIn(T target, T... values) {
        if (values == null) {
            return Predicate.isEqual(target).test(null);
        } else {
            return Stream.of(values)
                    .anyMatch(Predicate.isEqual(target));
        }
    }

    /**
     * Returns if the given value is in the supplied values.
     * Is null safe.
     *
     * @param target the input value to check.
     * @param values the values to check against.
     * @param <T>    collection element type
     * @return true if the value is in the list of supplied values, otherwise false.
     */
    public static <T> boolean isIn(@Nullable T target, @Nullable Collection<T> values) {
        if (values == null) {
            return Predicate.isEqual(target).test(null);
        } else {
            return values.stream()
                    .anyMatch(Predicate.isEqual(target));
        }
    }

    /**
     * Returns if the given value is not in the supplied values.
     * Is null safe.
     *
     * @param target the input value to check.
     * @param values the values to check against.
     * @param <T>    collection element type
     * @return true if the value is not in the list of supplied values, otherwise false.
     */
    @SafeVarargs
    public static <T> boolean isNotIn(T target, T... values) {
        return !isIn(target, values);
    }

    /**
     * Returns if the given value is not in the supplied values.
     * Is null safe.
     *
     * @param target the input value to check.
     * @param values the {@link Collection} of values to check against.
     * @param <T>    collection element type
     * @return true if the value is not in the list of supplied values, otherwise false.
     */
    public static <T> boolean isNotIn(T target, Collection<T> values) {
        return !isIn(target, values);
    }

    /**
     * Returns a combined list of the input collections.
     * Does not modify or copy the elements insides the collections.
     * Does not preserve the order.
     *
     * @param collections the input collections.
     * @param <T>         collection element type
     * @return return a list of all the elements from the collections.
     */
    @SafeVarargs
    public static <T> List<T> combineToList(Collection<T>... collections) {
        return Stream.of(collections)
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Returns a combined list of the two input lists.
     * Does not modify or copy the elements inside the lists.
     * Combines by appending the two lists in order to a new list.
     *
     * @param first  first list
     * @param second second list
     * @param <T>    list element type
     * @return return a list of all the elements from the lists.
     */
    public static <T> List<T> combineToList(List<T> first, List<T> second) {
        ArrayList<T> result = new ArrayList<>(first.size() + second.size());
        result.addAll(first);
        result.addAll(second);
        return result;
    }

    /**
     * Returns a combined set of the input collections.
     * Does not modify or copy the elements insides the collections.
     * Does not preserve the order.
     *
     * @param collections the input collections.
     * @param <T>         collection element type
     * @return return a set of all the elements from the collections.
     */
    @SafeVarargs
    public static <T> Set<T> combineToSet(Collection<T>... collections) {
        return Stream.of(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a combined set of the two input sets.
     * Does not modify or copy the elements inside the sets.
     * Does not preserve the order.
     *
     * @param first  the first set
     * @param second the second set
     * @param <T>    set element type
     * @return return a set of all the elements from the sets.
     */
    public static <T> Set<T> combineToSet(Set<T> first, Set<T> second) {
        SetView<T> bMinusA = difference(second, first);

        return new SetView<>() {
            @Override
            public boolean contains(final Object o) {
                return first.contains(o) || second.contains(o);
            }

            @Override
            public Iterator<T> createIterator() {
                return new IteratorChain<>(first.iterator(), bMinusA.iterator());
            }

            @Override
            public boolean isEmpty() {
                return first.isEmpty() && second.isEmpty();
            }

            @Override
            public int size() {
                return first.size() + bMinusA.size();
            }
        };
    }

    public static <E> SetView<E> difference(final Set<? extends E> firstSet, final Set<? extends E> secondSet) {
        return new SetView<>() {
            @Override
            public boolean contains(final Object o) {
                return firstSet.contains(o) && !secondSet.contains(o);
            }

            @Override
            public Iterator<E> createIterator() {
                return new FilterIterator<>(firstSet.iterator(), object -> !secondSet.contains(object));
            }
        };
    }

    /**
     * Returns a shallow copy of the given list with the elements in reverse order.
     *
     * @param list list to be reversed.
     * @param <T>  collection element type
     * @return copy of list with reversed order of elements.
     */
    public static <T> List<T> reverse(List<T> list) {
        List<T> copyList = new ArrayList<>(list);
        Collections.reverse(copyList);
        return copyList;
    }

    /**
     * Method that joins two lists by applying a two-argument function to corresponding elements
     * in the lists and returning a list of the results. That is, the two lists are traversed
     * together, and the given function is called pairwise on each member of both lists.
     * The two lists must have the same length, which will also be the length of the returned list.
     *
     * @param function     The function that is applied to the elements of the two lists to form the result list.
     * @param elementList1 The first list.
     * @param elementList2 the second list.
     * @return a list of the zipped results.
     * @throws CoreException if any of the list is empty or null
     * @throws CoreException if any of the two lists does not have the same amount of elements.
     */
    public static <T, S, R> List<R> zipWith(BiFunction<T, S, R> function, List<T> elementList1, List<S> elementList2) {
        if (isEmpty(elementList1) || isEmpty(elementList2)) {
            throw ErrorCode.EMPTY_LIST.createCoreException("A list was null or empty for zipping, list 1 = {0}, list 2 = {1}", new Object[]{Objects.toString(elementList1), Objects.toString(elementList2)});
        }

        if (elementList1.size() != elementList2.size()) {
            throw ErrorCode.LIST_NOT_SAME_SIZE.createCoreException("The two list did not have the same size for zipping, list 1 = {0}, list 2 = {1}", new Object[]{Integer.toString(elementList1.size()), Integer.toString(elementList2.size())});
        }

        return IntStream.range(0, elementList1.size())
                .mapToObj(i -> function.apply(elementList1.get(i), elementList2.get(i)))
                .toList();
    }

    /**
     * Method that uses an input {@param elementList} to operate on the 2 elements at a time,
     * after converting them to type {@link R} with the {@code merge} Function.
     * If applying {@code shouldMerge} on a pair of elements results in true, then they are merged in a single element using {@code merge},
     * otherwise they get added to the result list.
     * <p>
     * <p>
     * It is noted that for operating on the pair of current and previous elements a stack {@link Deque} is used,
     * in order to pop the previous converted element and merge it with the current one when {@code shouldMerge} is true.
     * Otherwise, the current converted element gets pushed to the top of the stack.
     *
     * @param shouldMerge the input {@link BiPredicate} that checks whether the pair of elements it is applied to should be merged.
     * @param merge       the {@link BinaryOperator} to use when merging the converted pair of elements.
     * @param convert     the {@link Function} that converts an element of the input list to the result type {@link R} that gets operated on by {@code merge}
     * @param elementList the input list of elements of type {@link R} to operate on.
     *                    May need to be sorted by some criteria, since the shouldMerge function is applied on pairs of elements at a time that are in adjacent indexes of the list.
     * @param <T>         the type class of the input elements to merge.
     * @param <R>         the type class of the merged result list.
     * @return the result list of type {@link R} with all the merged and non-merged converted elements.
     */
    public static <T, R> List<R> merge(BiPredicate<T, T> shouldMerge, BinaryOperator<R> merge, Function<T, R> convert, List<T> elementList) {
        if (isEmpty(elementList)) {
            return new ArrayList<>();
        }

        Deque<R> stack = new ArrayDeque<>();
        stack.add(convert.apply(elementList.getFirst()));

        for (int i = 1; i < elementList.size(); i++) {
            T current = elementList.get(i);
            T previous = elementList.get(i - 1);

            R convertedCurrent = convert.apply(current);

            if (shouldMerge.test(current, previous)) {
                stack.push(merge.apply(convertedCurrent, stack.pop()));
            } else {
                stack.push(convertedCurrent);
            }
        }

        return new ArrayList<>(stack);
    }

    /**
     * Sorts the given {@code elementList} and then merges its elements using {@link CollectionUtils#merge(BiPredicate, BinaryOperator, Function, List)}.
     *
     * @param shouldMerge    the input {@link BiPredicate} that checks whether the pair of elements it is applied to should be merged.
     * @param merge          the {@link BinaryOperator} to use when merging the converted pair of elements.
     * @param convert        the {@link Function} that converts an element of the input list to the result type {@link R} that gets operated on by {@code merge}
     * @param listComparator the comparator to use to sort the given list before merging.
     * @param elementList    the input list of elements of type {@link R} to operate on.
     *                       May need to be sorted by some criteria, since the shouldMerge function is applied on pairs of elements at a time that are in adjacent indexes of the list.
     * @param <T>            the type class of the input elements to merge.
     * @param <R>            the type class of the merged result list.
     * @return the result list of type {@link R} with all the merged and non-merged converted elements.
     */
    public static <T, R> List<R> sortAndMerge(BiPredicate<T, T> shouldMerge, BinaryOperator<R> merge, Function<T, R> convert, Comparator<T> listComparator, List<T> elementList) {
        List<T> sortedList = elementList.stream()
                .sorted(listComparator)
                .toList();
        return merge(shouldMerge, merge, convert, sortedList);
    }

    /**
     * Method that determines if two lists are considered equal by applying a two-argument predicate to corresponding elements
     * in the lists. The two lists are traversed together, and the given predicate is called pairwise on each member of both lists.
     * The two supplied lists should be ordered before use.
     *
     * @param predicate    The predicate that is applied to the elements of the two lists.
     * @param elementList1 The first list.
     * @param elementList2 The second list.
     * @param <T>          element type of first list
     * @param <S>          element type of second list
     * @return True if both lists have the same length and the given predicate returns true for all pairs constructed from traversing the lists,
     * or both lists are empty, or both lists are null, otherwise false.
     */
    public static <T, S> boolean consideredEqual(BiPredicate<T, S> predicate, List<T> elementList1, List<S> elementList2) {
        return isTriviallyEqualOrUnequal(elementList1, elementList2)
                .orElseGet(() -> checkPairwiseEquality(predicate, elementList1, elementList2));
    }

    private static <A, B> Optional<Boolean> isTriviallyEqualOrUnequal(Collection<A> a, Collection<B> b) {
        boolean aNull = Objects.isNull(a);
        boolean bNull = Objects.isNull(b);
        boolean aEmpty = isEmpty(a);
        boolean bEmpty = isEmpty(b);

        if (aNull || bNull) {
            return Optional.of(aNull && bNull);
        } else if (aEmpty || bEmpty) {
            return Optional.of(aEmpty && bEmpty);
        } else if (a.size() != b.size()) {
            return Optional.of(false);
        }
        return Optional.empty();
    }

    private static <T, S> boolean checkPairwiseEquality(BiPredicate<T, S> function, List<T> elementList1, List<S> elementList2) {
        return IntStream.range(0, elementList1.size())
                .allMatch(i -> function.test(elementList1.get(i), elementList2.get(i)));
    }

    /**
     * Returns:
     * <ul>
     *     <li>true if both collections are null or empty</li>
     *     <li>false if one is null or empty and the other isn't</li>
     *     <li>false if the two collections have different sizes</li>
     *     <li>true if none of the previous conditions apply, and all elements of the first collection a return true for the given predicate on any element of the second collection b (and vice versa)</li>
     * </ul>.
     *
     * @param predicate the predicate to check equality for
     * @param first     the first collection
     * @param second    the second collection
     * @param <B>       collection element class
     * @return true if both collections are considered equal, based on the given predicate
     */
    public static <B> boolean consideredEqualIgnoringOrder(BiPredicate<B, B> predicate, @Nullable Collection<B> first, @Nullable Collection<B> second) {
        return isTriviallyEqualOrUnequal(first, second)
                .orElseGet(() -> allMatchAnyOnPredicate(predicate, first, second) && allMatchAnyOnPredicate(predicate, second, first));
    }

    private static <B> boolean allMatchAnyOnPredicate(BiPredicate<B, B> predicate, Collection<B> a, Collection<B> b) {
        return StreamUtils.ofNullable(a)
                .allMatch(x -> StreamUtils.ofNullable(b)
                        .anyMatch(y -> predicate.test(x, y))
                );
    }

    /**
     * Method that determines if two lists are NOT considered equal by applying a two-argument predicate to corresponding elements
     * in the lists. The two lists are traversed together, and the given predicate is called pairwise on each member of both lists.
     * The two supplied lists should be ordered before use.
     *
     * @param predicate    The predicate that is applied to the elements of the two lists.
     * @param elementList1 The first list.
     * @param elementList2 The second list.
     * @param <T>          element type of first list
     * @param <S>          element type of second list
     * @return False if both lists have the same length and the given predicate returns true for all pairs constructed from traversing the lists,
     * or both lists are empty, or both lists are null, otherwise True.
     */
    public static <T, S> boolean consideredNotEqual(BiPredicate<T, S> predicate, List<T> elementList1, List<S> elementList2) {
        return !consideredEqual(predicate, elementList1, elementList2);
    }

    /**
     * Determines if there is any intersection between elements in two collections.
     *
     * @param a   Collection of elements of type {@code U} or any subtype.
     * @param b   Collection of elements to check for intersection with.
     * @param <T> collection {@code a} element type.
     * @param <U> collection {@code b} element type.
     * @return true if there is an intersection, false otherwise.
     */
    public static <T extends U, U> boolean intersects(Collection<T> a, Collection<U> b) {
        return contains(a, b::contains);
    }

    /**
     * Returns an unmodifiable <b>view</b> of the symmetric difference of the given
     * {@link Set}s.
     * <p>
     * The returned view contains all elements of {@code a} and {@code b} that are
     * not a member of the other set.
     * <p>
     * This is equivalent to {@code union(difference(a, b), difference(b, a))}.
     *
     * @param <E> the generic type that is able to represent the types contained
     *            in both input sets.
     * @param a   the first set, must not be null
     * @param b   the second set, must not be null
     * @return a view of the symmetric difference of the two sets
     */
    public static <E> SetView<E> disjunction(final Set<? extends E> a, final Set<? extends E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("Sets must not be null.");
        }

        final SetView<E> aMinusB = difference(a, b);
        final SetView<E> bMinusA = difference(b, a);

        return new SetView<>() {
            @Override
            public boolean contains(final Object o) {
                return a.contains(o) ^ b.contains(o);
            }

            @Override
            public Iterator<E> createIterator() {
                return new IteratorChain<>(aMinusB.iterator(), bMinusA.iterator());
            }

            @Override
            public boolean isEmpty() {
                return aMinusB.isEmpty() && bMinusA.isEmpty();
            }

            @Override
            public int size() {
                return aMinusB.size() + bMinusA.size();
            }
        };
    }

    /**
     * Determines if there is any difference between the two sets.
     * Both by comparing difference in set A to B and set B to A
     *
     * @param a   Set of elements of type {@code U} or any subtype
     * @param b   Set of elements to check for differance with
     * @param <T> set A element type
     * @param <U> set B element type
     * @return true if there is any difference between the two sets, false otherwise.
     */
    public static <T extends U, U> boolean hasDifferenceBetweenAnyWay(Set<T> a, Set<U> b) {
        if (isEmpty(a) && isEmpty(b)) {
            return false;
        } else if (isEmpty(a) || isEmpty(b)) {
            return true;
        }
        return isNotEmpty(disjunction(a, b));
    }

    /**
     * Creates a list of elements.
     * Can create lists containing null values.
     *
     * @param elements to create a list from
     * @param <T>      collection element type
     * @return the created list
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        // Special case where null is passes as a single argument.
        // For the empty set of elements, or multiple nulls, this is not a problem.
        if (elements == null) {
            final ArrayList<T> list = new ArrayList<>();
            list.add(null);
            return list;
        } else {
            return Arrays.asList(elements);
        }
    }

    /**
     * This function filters a collection (and transforms it to a List) to only include the elements having the maximum value based on a
     * given extractor and comparator. Notice that {@link Stream#max(Comparator)} does not support null, so in case null has to be
     * considered as maximum value, the extractor needs change null to the respective maximum values of the type R.
     *
     * @param collection The collection to filter.
     * @param extractor  The extractor to apply on the elements, providing the sub-elements for comparison.
     * @param comparator The comparator comparing extracted values.
     * @param <T>        The type of the elements of the input Collection (and output List).
     * @param <R>        The type of the elements extracted by the extractor and input to the comparator.
     * @return The filtered List of elements of type T (same type as the elements of the input Collection).
     */
    public static <T, R> List<T> filterByMaxValueForField(Collection<T> collection, Function<T, R> extractor, Comparator<R> comparator) {
        if (isNotEmpty(collection)) {
            Optional<R> maxValue = collection.stream()
                    .filter(Objects::nonNull)
                    .map(extractor)
                    .filter(Objects::nonNull)
                    .max(comparator);
            if (maxValue.isPresent()) {
                return collection.stream()
                        .filter(Objects::nonNull)
                        .filter(PredicateUtils.filterBy(Objects::nonNull, extractor))
                        .filter(element -> 0 == comparator.compare(maxValue.get(), extractor.apply(element)))
                        .toList();
            }
        }
        return Collections.emptyList();
    }

    /**
     * Performs an operation equivalent to {@link Collection#removeAll(Collection)}, but with a custom predicate instead of using the equals method of {@code <T>},
     * and returns the result instead of performing the modification in-place.
     *
     * @param base     collection to remove elements from
     * @param filter   collection containing elements to be removed from {@code base}
     * @param removeIf custom bi-predicate method
     * @param <T>      The type of the elements of the input Collection (and output Collection).
     * @return the result of removing elements from {@code base} that satisfy the given {@code equality} predicate against any element in {@code filter}
     */
    public static <T> Collection<T> removeAll(Collection<T> base, Collection<T> filter, BiPredicate<T, T> removeIf) {
        return base.stream()
                .filter(baseElement -> notContains(filter, filterElement -> removeIf.test(baseElement, filterElement)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Subtracts the elements in {@code collection} from {@code base} by checking containment using equals method
     * and returns the result as a Set.
     *
     * @param base       the base collection
     * @param collection the collection to subtract from base
     * @param <T>        The type of the elements of the input Collection (and output Set).
     * @return the result of the subtraction as a Set.
     */
    public static <T> Set<T> subtractToSet(Collection<T> base, Collection<T> collection) {
        return base.stream()
                .filter(isNotContainedIn(Function.identity(), collection))
                .collect(Collectors.toSet());
    }

    /**
     * Performs an operation equivalent to {@link Collection#retainAll(Collection)}, but with a custom predicate instead of using the equals method of {@code <T>},
     * and returns the result instead of performing the modification in-place.
     *
     * @param base     collection to remove elements from
     * @param filter   collection containing elements to retain from {@code base}
     * @param retainIf custom bi-predicate method
     * @param <T>      The type of the elements of the input Collection (and output Collection).
     * @return the result of retaining elements from {@code base} that satisfy the given {@code equality} predicate against any element in {@code filter}, removing all other elements
     */
    public static <T> Collection<T> retainAll(Collection<T> base, Collection<T> filter, BiPredicate<T, T> retainIf) {
        return base.stream()
                .filter(baseElement -> contains(filter, filterElement -> retainIf.test(baseElement, filterElement)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds exactly one or none in the colleciton, based on the predicate, otherwise throws.
     *
     * @param entities  collection of entities
     * @param predicate predicate to filter for
     * @param <T>       entity type
     * @return result of applying {@link StreamUtils#findExactlyOneOrNone(String, String...)} after filtering for the {@code predicate}
     */
    public static <T> Optional<T> findExactlyOneOrNone(Collection<T> entities, Predicate<T> predicate) {
        return filterAndCollect(entities, predicate,
                StreamUtils.findExactlyOneOrNone("Found more than one entity in {0} matching the predicate {1}", StreamUtils.getDebugStringSuppliers(entities, predicate)));
    }

    /**
     * Finds exactly one entity based on the predicate, otherwise throws.
     *
     * @param entities  collection of entities
     * @param predicate predicate to filter for
     * @param <T>       entity type
     * @return result of applying {@link StreamUtils#findExactlyOne(String, String...)} after filtering for the {@code predicate}
     */
    public static <T> T findExactlyOne(Collection<T> entities, Predicate<T> predicate) {
        return filterAndCollect(entities, predicate,
                StreamUtils.findExactlyOne("Couldn't find an entity in {0} matching the predicate {1}", StreamUtils.getDebugStringSuppliers(entities, predicate)));
    }

    /**
     * Filters the entities by the given predicate.
     *
     * @param entities  collection of entities
     * @param predicate predicate to filter for
     * @param <T>       entity type
     * @return resulting list after filtering for the {@code predicate}
     */
    public static <T> List<T> filterByPredicate(Collection<T> entities, Predicate<T> predicate) {
        return filterAndCollect(entities, predicate, Collectors.toList());
    }

    private static <T, U> U filterAndCollect(Collection<T> entities, Predicate<T> predicate, Collector<T, ?, U> collector) {
        return entities.stream()
                .filter(predicate)
                .collect(collector);
    }

    /**
     * Returns an empty list if the given collection object was null, otherwise maps the collection to a list.
     *
     * @param collection a given collection
     * @param <T>        type of the collection
     * @return an empty list if the given collection object was null, otherwise a list of the collection elements
     */
    public static <T> List<T> emptyListIfNull(@Nullable Collection<T> collection) {
        return StreamUtils.ofNullable(collection)
                .toList();
    }

    /**
     * Returns true if the collection has a size in the given range.
     *
     * @param collection a given collection
     * @param from       from
     * @param to         to
     * @param <T>        type of the collection
     * @return true if the collection size (0 if null) is in the given range, otherwise false
     */
    public static <T> boolean hasSizeInRange(@Nullable Collection<T> collection, Integer from, Integer to) {
        int size = emptyListIfNull(collection).size();
        return size >= from && size <= to;
    }

    /**
     * Returns whether the array is not null or empty.
     *
     * @param array the array.
     * @return true if the array is not empty or null, false otherwise.
     */
    public static boolean isNotEmpty(@Nullable Object[] array) {
        return array != null && array.length > 0;
    }

    public abstract static class SetView<E> extends AbstractSet<E> {

        /**
         * Return an iterator for this view; the returned iterator is
         * not required to be unmodifiable.
         *
         * @return a new iterator for this view
         */
        protected abstract Iterator<E> createIterator();

        @Nonnull
        @Override
        public Iterator<E> iterator() {
            return UnmodifiableIterator.unmodifiableIterator(createIterator());
        }

        @Override
        public int size() {
            int size = 0;
            for (E ignored : this) {
                size++;
            }
            return size;
        }
    }
}
