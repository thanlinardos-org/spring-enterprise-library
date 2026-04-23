package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import com.thanlinardos.spring_enterprise_library.time.api.DateTemporal;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/** Util methods for using predicates in streams. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredicateUtils {

    /**
     * Applies the result of the extractor on the predicate, returns it as a predicate itself.
     *
     * @param pred      the predicate to test the extracted value against.
     * @param extractor the extractor to get the value to test the predicate on.
     * @param <T>       the type of the input object to the returned predicate
     * @param <R>       the type of the result of the extractor
     * @return a {@link Predicate} that tests the extracted value against the input predicate.
     */
    public static <T, R> Predicate<T> filterBy(Predicate<R> pred, Function<T, R> extractor) {
        return t -> pred.test(extractor.apply(t));
    }

    /**
     * Applies the result of the extractor on the predicate together with the argument.
     *
     * @param extractor the extractor to get the value to test the predicate on.
     * @param pred      the predicate to test the extracted value against.
     * @param argument  the argument to use for the input to the predicate
     * @param <R>       the type of the result of the extractor
     * @param <T>       the type of the input object to the returned predicate
     * @param <I>       the type of the input argument to the predicate that on R
     * @return a {@link Predicate} that tests the extracted value against the input predicate.
     */
    public static <T, R, I> Predicate<T> filterBy(BiPredicate<R, I> pred, Function<T, R> extractor, I argument) {
        return t -> pred.test(extractor.apply(t), argument);
    }

    /**
     * Applies the result of the extractor to {@link Objects#equals(Object, Object)}, returns it as a predicate itself.
     * Is often a shorthand to {@link PredicateUtils#filterBy(Predicate, Function)} when the predicate is a someObject::equals method.
     *
     * @param compareTo the object to test the extracted value against.
     * @param extractor the extractor to get the value to test the predicate on.
     * @param <T>       the type of the input object to the returned predicate
     * @param <R>       the type of the result of the extractor
     * @return a {@link Predicate} that tests the extracted value is equal to the compareTo value.
     */
    public static <T, R> Predicate<T> isEqualTo(@Nullable R compareTo, Function<T, R> extractor) {
        return t -> Objects.equals(compareTo, extractor.apply(t));
    }

    /**
     * Applies the result of the extractor to {@link Objects#equals(Object, Object)}, returns it as a predicate itself.
     * Is often a shorthand to {@link PredicateUtils#filterBy(Predicate, Function)} when the predicate is negate(someObject::equals) method.
     *
     * @param compareTo the object to test the extracted value against.
     * @param extractor the extractor to get the value to test the predicate on.
     * @param <T>       the type of the input object to the returned predicate
     * @param <R>       the type of the result of the extractor
     * @return a {@link Predicate} that tests the extracted value is not equal to the compareTo value.
     */
    public static <T, R> Predicate<T> isNotEqualTo(@Nullable R compareTo, Function<T, R> extractor) {
        return t -> !Objects.equals(compareTo, extractor.apply(t));
    }

    /**
     * Applies the result of the extractor to {@link CollectionUtils#isIn(Object, Object[])}, returns it as a predicate itself.
     *
     * @param extractor the extractor to get the value to test the predicate on.
     * @param objects   the objects that the extracted value should be equal to one of
     * @param <T>       the type of the object from which the property to test is extracted.
     * @param <R>       the type of the objects in the array.
     * @return a {@link Predicate} that tests the extracted value is one of the supplied.
     */
    @SafeVarargs
    public static <T, R> Predicate<T> isContainedIn(Function<T, R> extractor, R... objects) {
        return t -> CollectionUtils.isIn(extractor.apply(t), objects);
    }

    /**
     * Applies the result of the extractor to {@link CollectionUtils#isIn(Object, Collection)}, returns it as a predicate itself.
     *
     * @param extractor the extractor to get the value to test the predicate on.
     * @param objects   the {@link Collection} of objects that the extracted value should not be equal to one of.
     * @param <T>       the type of the object from which the property to test is extracted.
     * @param <R>       the type of the objects in the collection.
     * @return a {@link Predicate} that tests the extracted value is not one of the supplied.
     */
    public static <T, R> Predicate<T> isContainedIn(Function<T, R> extractor, Collection<R> objects) {
        return t -> CollectionUtils.isIn(extractor.apply(t), objects);
    }

    /**
     * Applies the result of the extractor to {@link CollectionUtils#isNotIn(Object, Object[])}, returns it as a predicate itself.
     *
     * @param extractor the extractor to get the value to test the predicate on.
     * @param objects   the objects that the extracted value should not be equal to one of
     * @param <T>       the type of the object from which the property to test is extracted.
     * @param <R>       the type of the objects in the array.
     * @return a {@link Predicate} that tests the extracted value is not one of the supplied.
     */
    @SafeVarargs
    public static <T, R> Predicate<T> isNotContainedIn(Function<T, R> extractor, R... objects) {
        return t -> CollectionUtils.isNotIn(extractor.apply(t), objects);
    }

    /**
     * Applies the result of the extractor to {@link CollectionUtils#isNotIn(Object, Collection)}, returns it as a predicate itself.
     *
     * @param extractor the extractor to get the value to test the predicate on.
     * @param objects   the {@link Collection} of objects that the extracted value should not be equal to one of
     * @param <T>       the type of the object from which the property to test is extracted.
     * @param <R>       the type of the objects in the collection.
     * @return a {@link Predicate} that tests the extracted value is not one of the supplied.
     */
    public static <T, R> Predicate<T> isNotContainedIn(Function<T, R> extractor, Collection<R> objects) {
        return t -> CollectionUtils.isNotIn(extractor.apply(t), objects);
    }

    /**
     * Applies the result of the extractor to {@link Collection#contains(Object)}, returns it as a predicate itself.<br/>
     * <b>Important the collection can throw exceptions if as an example it does not allow nulls, it is up to the Collection implementation</b><br/>
     * Is often a shorthand to {@link PredicateUtils#filterBy(Predicate, Function)} when the predicate is a someCollection::contains method.
     *
     * @param collection the collection to test if it contains the extracted value.
     * @param extractor  the extractor to get the value to test the predicate on.
     * @param <T>        the type of the input object to the returned predicate
     * @param <R>        the type of the result of the extractor
     * @return a {@link Predicate} that tests the extracted value is contained in the collection.
     */
    public static <T, R> Predicate<T> contains(Collection<R> collection, Function<T, R> extractor) {
        return t -> collection.contains(extractor.apply(t));
    }

    /**
     * Applies the result of the extractor to {@link Collection#contains(Object)} and inverts the result, returns it as a predicate itself.<br/>
     * <b>Important the collection can throw exceptions if as an example it does not allow nulls, it is up to the Collection implementation</b><br/>
     * Is often a shorthand to {@link PredicateUtils#filterBy(Predicate, Function)} when the predicate is a negation of someCollection::contains method.
     *
     * @param collection the collection to test if it does not contain the extracted value.
     * @param extractor  the extractor to get the value to test the predicate on.
     * @param <T>        the type of the input object to the returned predicate
     * @param <R>        the type of the result of the extractor
     * @return a {@link Predicate} that tests the extracted value is not contained in the collection.
     */
    public static <T, R> Predicate<T> notContains(Collection<R> collection, Function<T, R> extractor) {
        return negate(contains(collection, extractor));
    }

    public static <T, R> Predicate<T> selfContains(R value, Function<T, Collection<R>> collectionExtractor) {
        return t -> collectionExtractor.apply(t).contains(value);
    }

    public static <T, R> Predicate<T> selfNotContains(R value, Function<T, Collection<R>> collectionExtractor) {
        return negate(selfContains(value, collectionExtractor));
    }

    /**
     * Negates the input predicate.
     *
     * @param pred the predicate to negate.
     * @param <T>  the type of the input to the predicate.
     * @return the negated {@link Predicate}.
     */
    public static <T> Predicate<T> negate(Predicate<T> pred) {
        return pred.negate();
    }

    /**
     * Returns a predicate that checks if the result of the extractor is not null.
     *
     * @param extractor the extractor to get the value by.
     * @param <T>       the type of the input object to the returned predicate.
     * @param <R>       the type of the result of the extractor.
     * @return a {@link Predicate} that tests the extracted value is not null
     */
    public static <T, R> Predicate<T> nonNull(Function<T, R> extractor) {
        return t -> Objects.nonNull(extractor.apply(t));
    }

    /**
     * Returns a predicate that checks if the result of the extractor is null.
     *
     * @param extractor the extractor to get the value by.
     * @param <T>       the type of the input object to the returned predicate.
     * @param <R>       the type of the result of the extractor.
     * @return a {@link Predicate} that tests the extracted value is null
     */
    public static <T, R> Predicate<T> isNull(Function<T, R> extractor) {
        return t -> Objects.isNull(extractor.apply(t));
    }

    /**
     * Returns a predicate that always evaluate to true unless the input predicate is not satisfied.
     * If the predicate is not satisfied, a {@link CoreException} is thrown with the supplied error code, message and message arguments.
     * This is meant to be used in an optional or stream chain to throw exceptions if some critical assumption does not hold.
     *
     * @param predicate the predicate that should always evaluate to true.
     * @param errorCode the error code.
     * @param message   the error message
     * @param strings   the error message arguments.
     * @param <T>       the type of the input to the predicate.
     * @return a predicate that throws an exception if the supplied predicate evaluates to false, otherwise the returned predicate evaluates to true.
     */
    public static <T> Predicate<T> throwIfNot(Predicate<T> predicate, ErrorCode errorCode, String message, String... strings) {
        return value -> {
            if (!predicate.test(value)) {
                throw errorCode.createCoreException(message, strings);
            }
            return true;
        };
    }

    /**
     * Returns a predicate that checks if entity is equal to the compare to entity by id.
     * If the object we are comparing to does not have an id then it can never be equal by id.
     *
     * @param compareTo the object to test the id against.
     * @param <T>       the type of the entity that extends {@link BasicIdJpa}.
     * @return a predicate that represents if the entity is equal to the entity by id or false.
     */
    public static <T extends BasicIdJpa> Predicate<T> isEqualByIdTo(@Nonnull T compareTo) {
        return entity -> {
            if (compareTo.getId() == null) {
                return false; //If the object we are comparing to does not have an id, it can never be equal by id
            }

            return Objects.equals(compareTo.getId(), entity.getId());
        };
    }

    /**
     * Returns a predicate that checks if the entity is not equal to the compare to entity by id.
     *
     * @param compareTo the object to test the id against.
     * @param <T>       the type of the entity that extends {@link BasicIdJpa}.
     * @return a predicate that represents if the entity is not equal to the entity by id
     */
    public static <T extends BasicIdJpa> Predicate<T> isNotEqualByIdTo(@Nonnull T compareTo) {
        return isEqualByIdTo(compareTo).negate();
    }

    /**
     * Returns a predicate that tests if {@code compareTo} is not null,
     * and the result of {@code extractor} is equal to result of {@code extractor} on the comparing object.
     *
     * @param compareTo the object to compare against
     * @param extractor the function to apply to the compareTo and the comparing object
     * @param <T>       the type of the object to test
     * @param <R>       the type of the extracted key to compare by
     * @return a predicate that tests true if {@code compareTo} is nonnull,
     * and the result of {@code extractor} applied to both {@code compareTo}
     * and the predicate object are equal (using {@link Objects#equals}).
     */
    public static <T, R> Predicate<T> isEqualToByKey(@Nullable T compareTo, Function<T, R> extractor) {
        return t -> Optional.ofNullable(compareTo)
                .filter(isEqualTo(extractor.apply(t), extractor))
                .isPresent();
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#isInRange(LocalDate, LocalDate)} on a temporal
     * it takes the {@link LocalDate}s as an input.
     *
     * @param from the {@link LocalDate} from
     * @param to   from the {@link LocalDate} from
     * @param <T>  the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#isInRange(LocalDate, LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> isInRange(LocalDate from, LocalDate to) {
        return temporal -> temporal.isInRange(from, to);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#isInRange(LocalDate, LocalDate)} on a temporal
     * it takes the {@link LocalDate}s as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param from      the {@link LocalDate} from
     * @param to        from the {@link LocalDate} from
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#isInRange(LocalDate, LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> isInRange(Function<T, R> extractor, LocalDate from, LocalDate to) {
        return filterBy(isInRange(from, to), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#containsDate(LocalDate)} (LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param date the {@link LocalDate}
     * @param <T>  the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#containsDate(LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> containsDate(LocalDate date) {
        return temporal -> temporal.containsDate(date);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#containsDate(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param date      the {@link LocalDate}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#containsDate(LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> containsDate(Function<T, R> extractor, LocalDate date) {
        return filterBy(containsDate(date), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsInterval(DateTemporal)} on a temporal
     * it takes the {@link DateTemporal} as an input.
     *
     * @param other the {@link DateTemporal}
     * @param <T>   the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsInterval(DateTemporal)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> overlapsInterval(DateTemporal other) {
        return temporal -> temporal.overlapsInterval(other);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsInterval(DateTemporal)} on a temporal
     * it takes the {@link DateTemporal} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param other     the {@link LocalDate}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#containsDate(LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> overlapsInterval(Function<T, R> extractor, DateTemporal other) {
        return filterBy(overlapsInterval(other), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsInterval(LocalDate, LocalDate)} on a temporal
     * it takes the {@link LocalDate}s as an input.
     *
     * @param from the {@link LocalDate} from
     * @param to   from the {@link LocalDate} from
     * @param <T>  the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsInterval(LocalDate, LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> overlapsInterval(LocalDate from, LocalDate to) {
        return temporal -> temporal.overlapsInterval(from, to);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsInterval(LocalDate, LocalDate)} on a temporal
     * it takes the {@link LocalDate}s as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param from      the {@link LocalDate} from
     * @param to        from the {@link LocalDate} from
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsInterval(LocalDate, LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> overlapsInterval(Function<T, R> extractor, LocalDate from, LocalDate to) {
        return filterBy(overlapsInterval(from, to), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#isContainedIn(LocalDate, LocalDate)} on a temporal
     * it takes the {@link LocalDate}s as an input.
     *
     * @param from the {@link LocalDate} from
     * @param to   from the {@link LocalDate} from
     * @param <T>  the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#isContainedIn(LocalDate, LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> isContainedIn(LocalDate from, LocalDate to) {
        return temporal -> temporal.isContainedIn(from, to);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#isContainedIn(LocalDate, LocalDate)} on a temporal
     * it takes the {@link LocalDate}s as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param from      the {@link LocalDate} from
     * @param to        from the {@link LocalDate} from
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsInterval(LocalDate, LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> isContainedIn(Function<T, R> extractor, LocalDate from, LocalDate to) {
        return filterBy(isContainedIn(from, to), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsMonth(YearMonth)} on a temporal
     * it takes the {@link YearMonth} as an input.
     *
     * @param yearMonth the {@link YearMonth}
     * @param <T>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsMonth(YearMonth)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> overlapsMonth(YearMonth yearMonth) {
        return temporal -> temporal.overlapsMonth(yearMonth);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsMonth(YearMonth)} on a temporal
     * it takes the {@link YearMonth} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param yearMonth the {@link YearMonth}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsMonth(YearMonth)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> overlapsMonth(Function<T, R> extractor, YearMonth yearMonth) {
        return filterBy(overlapsMonth(yearMonth), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsYear(Year)} on a temporal
     * it takes the {@link Year} as an input.
     *
     * @param year the {@link Year}
     * @param <T>  the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsYear(Year)}} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> overlapsYear(Year year) {
        return temporal -> temporal.overlapsYear(year);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsYear(Year)} on a temporal
     * it takes the {@link Year} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param year      the {@link Year}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsYear(Year)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> overlapsYear(Function<T, R> extractor, Year year) {
        return filterBy(overlapsYear(year), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsYear(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param year the {@link LocalDate}
     * @param <T>  the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsYear(LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> overlapsYear(LocalDate year) {
        return temporal -> temporal.overlapsYear(year);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsYear(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param year      the {@link LocalDate}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsYear(LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> overlapsYear(Function<T, R> extractor, LocalDate year) {
        return filterBy(overlapsYear(year), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#startsAfter(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param date the {@link LocalDate}
     * @param <T>  the type of the temporal.
     * @return a predicate that checks the {@link DateTemporal#startsAfter(LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> startsAfter(LocalDate date) {
        return temporal -> temporal.startsAfter(date);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#startsAfter(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param date      the {@link LocalDate}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#startsAfter(LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> startsAfter(Function<T, R> extractor, LocalDate date) {
        return filterBy(startsAfter(date), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#startsBefore(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param date the {@link LocalDate}
     * @param <T>  the type of the temporal.
     * @return a predicate that checks the {@link DateTemporal#startsBefore(LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> startsBefore(LocalDate date) {
        return temporal -> temporal.startsBefore(date);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#startsBefore(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param date      the {@link LocalDate}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#startsBefore(LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> startsBefore(Function<T, R> extractor, LocalDate date) {
        return filterBy(startsBefore(date), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#startsBeforeOrOn(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param date the {@link LocalDate}
     * @param <T>  the type of the temporal.
     * @return a predicate that checks the {@link DateTemporal#startsBeforeOrOn(LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> startsBeforeOrOn(LocalDate date) {
        return temporal -> temporal.startsBeforeOrOn(date);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#startsBeforeOrOn(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param date      the {@link LocalDate}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#startsBeforeOrOn(LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> startsBeforeOrOn(Function<T, R> extractor, LocalDate date) {
        return filterBy(startsBeforeOrOn(date), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#endsAfter(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param date the {@link LocalDate}
     * @param <T>  the type of the temporal.
     * @return a predicate that checks the {@link DateTemporal#endsAfter(LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> endsAfter(LocalDate date) {
        return temporal -> temporal.endsAfter(date);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#endsAfter(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param date      the {@link LocalDate}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#endsAfter(LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> endsAfter(Function<T, R> extractor, LocalDate date) {
        return filterBy(endsAfter(date), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#endsAfterOrOn(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param date the {@link LocalDate}
     * @param <T>  the type of the temporal.
     * @return a predicate that checks the {@link DateTemporal#endsAfterOrOn(LocalDate)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> endsAfterOrOn(LocalDate date) {
        return temporal -> temporal.endsAfterOrOn(date);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#endsAfterOrOn(LocalDate)} on a temporal
     * it takes the {@link LocalDate} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param date      the {@link LocalDate}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#endsAfterOrOn(LocalDate)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> endsAfterOrOn(Function<T, R> extractor, LocalDate date) {
        return filterBy(endsAfterOrOn(date), extractor);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsInterval(DateTemporal)} on a temporal
     * it takes the {@link DateTemporal} as an input.
     *
     * @param other the {@link DateTemporal}
     * @param <T>   the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsInterval(DateTemporal)} on a temporal
     */
    public static <T extends DateTemporal> Predicate<T> overlap(DateTemporal other) {
        return temporal -> temporal.overlapsInterval(other);
    }

    /**
     * Returns a predicate that checks the {@link DateTemporal#overlapsInterval(DateTemporal)} on a temporal
     * it takes the {@link DateTemporal} as an input.
     *
     * @param extractor the extractor to get the temporal from an object
     * @param other     the {@link DateTemporal}
     * @param <T>       the type of the object from which the temporal is extracted
     * @param <R>       the type of the temporal
     * @return a predicate that checks the {@link DateTemporal#overlapsInterval(DateTemporal)} on a temporal
     */
    public static <T, R extends DateTemporal> Predicate<T> overlap(Function<T, R> extractor, DateTemporal other) {
        return filterBy(overlap(other), extractor);
    }

    /**
     * Returns a predicate that checks if the input extractor's return value
     * is equal to {@link Boolean#TRUE}.
     *
     * @param extractor the extractor to get the boolean from an object
     * @param <T>       the type of the object from which the boolean is extracted
     * @return the predicate that checks if the input extractor's return value
     * is equal to {@link Boolean#TRUE}.
     */
    @SuppressWarnings("java:S4276")
    public static <T> Predicate<T> isTrue(Function<T, Boolean> extractor) {
        return t -> Boolean.TRUE.equals(extractor.apply(t));
    }

    /**
     * Returns a predicate that checks if the input extractor's return value
     * is equal to {@link Boolean#FALSE}.
     *
     * @param extractor the extractor to get the boolean from an object
     * @param <T>       the type of the object from which the boolean is extracted
     * @return the predicate that checks if the input extractor's return value
     * is equal to {@link Boolean#FALSE}.
     */
    @SuppressWarnings("java:S4276")
    public static <T> Predicate<T> isFalse(Function<T, Boolean> extractor) {
        return t -> Boolean.FALSE.equals(extractor.apply(t));
    }
}
