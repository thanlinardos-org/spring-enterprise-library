package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.model.api.WithId;
import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thanlinardos.spring_enterprise_library.objects.utils.FunctionUtils.streamOptional;
import static com.thanlinardos.spring_enterprise_library.objects.utils.PredicateUtils.negate;

/** Utility class for object-related operations. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtils {

    /**
     * Returns true if any of the objects supplied are null.
     *
     * @param object the first object to check for null.
     * @param objects the objects to check for null.
     * @return true if any of the objects are null
     */
    public static boolean isAnyObjectNull(Object object, Object... objects) {
        if (object == null) {
            return true;
        }

        for (Object item : objects) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any of the objects supplied are not null.
     *
     * @param object the first object to check for null.
     * @param objects the objects to check for null.
     * @return true if any of the objects are not null.
     */
    public static boolean isAnyObjectNotNull(Object object, Object... objects) {
        return !isAllObjectsNull(object, objects);
    }

    /**
     * Returns true if all the objects supplied are null.
     *
     * @param object the first object to check for null.
     * @param objects the objects to check for null
     * @return true if all the objects are null, otherwise false.
     */
    public static boolean isAllObjectsNull(Object object, Object... objects) {
        if (object != null) {
            return false;
        } else {
            for (Object o : objects) {
                if (o != null) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Returns true if all the objects supplied are not null.
     *
     * @param object the first object to check for null.
     * @param objects the objects to check for null
     * @return true if all the objects are not null, otherwise false.
     */
    public static boolean isAllObjectsNotNull(Object object, Object... objects) {
        return !isAnyObjectNull(object, objects);
    }

    /**
     * Returns true if all the objects supplied are equal.
     *
     * @param object the first object to check for equals.
     * @param objects the objects to check for equals.
     * @return true if all the objects supplied are equal.
     */
    public static boolean isAllObjectsEquals(Object object, Object... objects) {
        for (Object o : objects) {
            if (o != object) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if all the objects supplied are not null and are all equal.
     *
     * @param object the first object to check for null and equals.
     * @param objects the objects to check for null and equals.
     * @return true if all the objects supplied are not null and are all equal.
     */
    public static boolean isAllObjectsNotNullAndEquals(Object object, Object... objects) {
        return isAllObjectsNotNull(object, objects) && isAllObjectsEquals(object, objects);
    }

    /**
     * Evaluates all the suppliers if needed and if all returns true then it returns true.
     * It uses noneMatch which is a short-circuiting operation to skip evaluating the rest if one is false.
     *
     * @param suppliers the suppliers to evaluate
     * @return true if all suppliers evaluates to true, otherwise false.
     */
    @SafeVarargs
    public static boolean evaluateAllTrue(Supplier<Boolean>... suppliers) {
        return Stream.of(suppliers)
                .map(Supplier::get)
                .noneMatch(negate(Boolean.TRUE::equals));
    }

    /**
     * Evaluates any of the suppliers if needed and if any of them is true, returns true.
     * It uses anyMatch which is a short-circuiting operation to skip evaluating the rest if one is true.
     *
     * @param suppliers the suppliers to evaluate
     * @return true if any suppliers evaluates to true, otherwise false.
     */
    @SafeVarargs
    public static boolean evaluateAnyTrue(Supplier<Boolean>... suppliers) {
        return Stream.of(suppliers)
                .map(Supplier::get)
                .anyMatch(Boolean.TRUE::equals);
    }

    /**
     * Returns the first non-null element found by evaluating the suppliers.
     * It uses findFirst which is a short-circuiting operation to only evaluate enough suppliers to get the first non-null.
     *
     * @param suppliers the suppliers to possibly evaluate
     * @param <T> the type class of the elements.
     * @return first non-null element found as an optional, otherwise empty optional.
     */
    @SafeVarargs
    public static <T> Optional<T> getFirstNonNull(Supplier<T>... suppliers) {
        return Stream.of(suppliers)
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst();
    }

    /**
     * Returns the first present optional element found by evaluating the suppliers.
     * It uses findFirst which is a short-circuiting operation to only evaluate enough suppliers to get the first present.
     *
     * @param suppliers the suppliers to possibly evaluate.
     * @param <T> the type class of the elements.
     * @return The first present element found, otherwise empty optional.
     */
    @SafeVarargs
    public static <T> Optional<T> getFirstPresent(Supplier<Optional<T>>... suppliers) {
        return Stream.of(suppliers)
                .flatMap(streamOptional(Supplier::get))
                .findFirst();
    }

    /**
     * Returns the value extracted by the getter from the object if the object is not null,
     * otherwise returns null.
     *
     * @param object The object
     * @param getter The getter
     * @param <T> the type class of the given object
     * @param <R> the type class of the extracted value
     * @return the value extracted by the getter from the object if the object is not null, otherwise null.
     */
    public static <T, R> R getOrNull(@Nullable T object, Function<T, R> getter) {
        return Optional.ofNullable(object)
                .map(getter)
                .orElse(null);
    }

    /**
     * Returns the id of the entity, otherwise null.
     *
     * @param entity the entity.
     * @return the id of the entity, otherwise null.
     */
    public static Long getIdOrNull(BasicIdJpa entity) {
        return getOrNull(entity, BasicIdJpa::getId);
    }

    /**
     * Returns a concatenated comma separated list of ids from the entities.
     *
     * @param entities the entities.
     * @param <T> the type class of the given entities.
     * @return a concatenated comma separated list of ids from the entities.
     */
    public static <T extends BasicIdJpa> String getCommaSeparatedListOfIds(Collection<T> entities) {
        return collectWithCommaSeparation(entities, BasicIdJpa::getId);
    }

    private static <T> String collectWithCommaSeparation(Collection<T> entities, Function<T, Long> longGetter) {
        return entities.stream()
                .map(longGetter)
                .map(Objects::toString)
                .collect(Collectors.joining(", "));
    }

    /**
     * Returns a set of ids of the entities.
     *
     * @param entities the entities
     * @param <T> the type class of the given entities.
     * @return a set of ids of the entities
     */
    public static <T extends BasicIdJpa> Set<Long> getIds(Collection<T> entities) {
        return entities.stream()
                .map(BasicIdJpa::getId)
                .collect(Collectors.toSet());
    }

    /**
     * Returns useful debugging information like class, id, and toString representation.
     *
     * @param entities the entities
     * @param <T> type of the entities
     * @return a string describing the entities
     */
    public static <T> String getDebugString(Collection<T> entities) {
        String debugString = CollectionUtils.isEmpty(entities) ? "collection is empty" : collectStringWithCommaSeparation(entities, ObjectUtils::getDebugString);
        return "[" + debugString + "]";
    }

    /**
     * Returns useful debugging information like class, id, and toString representation.
     *
     * @param entity the entity
     * @param <T> type of the entity
     * @return a string describing the entity
     */
        public static <T> String getDebugString(@Nullable T entity) {
        if (entity == null) {
            return "{entity is null}";
        } else if (entity instanceof Collection) {
            return getDebugString((Collection<?>) entity);
        } else if (getSimpleName(entity).contains("$$Lambda")) {
            Method method = entity.getClass().getDeclaredMethods()[0];
            String parameterTypes = collectStringWithCommaSeparation(List.of(method.getParameterTypes()), Class::getName);
            return String.format("{lambda=%s, parameterTypes=[%s], returnType=%s}", getSimpleName(entity), parameterTypes, method.getReturnType());
        }
        return String.format("{class=%s, id=%s, toString=%s}", getSimpleName(entity), getId(entity), entity);
    }

    private static <T> String collectStringWithCommaSeparation(Collection<T> entities, Function<T, String> stringGetter) {
        return entities.stream()
                .map(stringGetter)
                .collect(Collectors.joining(", "));
    }

    private static <T> String getSimpleName(T entity) {
        return entity.getClass().getSimpleName();
    }

    private static <T> String getId(T entity) {
        try {
            return Optional.ofNullable(((WithId) entity).getId())
                    .map(Object::toString)
                    .orElse("null (from WithId#getId)");
        } catch (Exception e) {
            return "null (no WithId#getId method)";
        }
    }

    /**
     * Apply the given merger {@link BiFunction} on two given parameters, if both parameters {@code a} and {@code b} are not null.
     * Returns the result of the merger wrapped in an Optional, otherwise it will return {@code Optional.empty()}.
     * If the merger returns an Optional as well, then apply: <pre>.flatMap(Function.identity())</pre> to the result of this method.
     *
     * @param a      first parameter
     * @param b      second parameter
     * @param merger a method that takes two objects with types {@code A} and {@code B}, and returns a result with type {@code C}
     * @param <A>    the type of the first parameter.
     * @param <B>    the type of the second parameter.
     * @param <C>    the result type.
     * @return an optional containing the result of the merger method if both input parameters are not null, otherwise {@code Optional.empty()}.
     */
    public static <A, B, C> Optional<C> applyIfBothNotNull(@Nullable A a, @Nullable B b, BiFunction<A, B, C> merger) {
        return isAllObjectsNotNull(a, b) ? Optional.of(merger.apply(a, b)) : Optional.empty();
    }

}
