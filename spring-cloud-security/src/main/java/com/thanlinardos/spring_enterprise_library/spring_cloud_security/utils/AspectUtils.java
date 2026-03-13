package com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility methods for working with AspectJ join points and annotations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AspectUtils {

    /**
     * Returns the simple class name of the given object, or an empty string if the object is null.
     *
     * @param result the object whose class name is to be retrieved
     * @return the simple class name, or an empty string if {@code result} is null
     */
    public static String getSimpleClassName(@Nullable Object result) {
        return result != null ? result.getClass().getSimpleName() : "";
    }

    /**
     * Checks if the method or its declaring class in the given {@link ProceedingJoinPoint} has the specified annotation.
     *
     * @param proceedingJoinPoint the join point representing the method invocation
     * @param annotation          the annotation class to check for
     * @param <A>                 the type of the annotation
     * @return {@code true} if the annotation is present, {@code false} otherwise
     */
    public static <A extends Annotation> boolean hasAnnotation(ProceedingJoinPoint proceedingJoinPoint, Class<A> annotation) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return AnnotationUtils.findAnnotation(method, annotation) != null
                || AnnotationUtils.findAnnotation(method.getDeclaringClass(), annotation) != null;
    }

    /**
     * Checks if the specified annotation on the method in the given {@link ProceedingJoinPoint} has a property
     * containing all the provided values.
     *
     * @param proceedingJoinPoint the join point representing the method invocation
     * @param annotation          the annotation class to check for
     * @param property            the property name of the annotation
     * @param values              the values to check for in the annotation property
     * @param <A>                 the type of the annotation
     * @return {@code true} if the annotation property contains all the values, {@code false} otherwise
     */
    public static <A extends Annotation> boolean hasAnnotationWithProperty(ProceedingJoinPoint proceedingJoinPoint, Class<A> annotation, String property, List<Object> values) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        A annotationObject = AnnotationUtils.findAnnotation(method, annotation);
        if (annotationObject != null) {
            Object annotationValue = AnnotationUtils.getValue(annotationObject, property);
            return annotationValue != null && new HashSet<>(values).containsAll(Arrays.asList((Object[]) annotationValue));
        }
        return false;
    }

    /**
     * Retrieves the value of a specific property from the specified annotation on the method in the given join point.
     * If the property is an array, returns the first element or null if empty.
     *
     * @param joinPoint  the join point representing the method invocation
     * @param annotation the annotation class to retrieve the property from
     * @param property   the property name of the annotation
     * @param <A>        the type of the annotation
     * @param <J>        the type of the join point
     * @return the value of the annotation property, or null if not found
     */
    public static <A extends Annotation, J extends JoinPoint> Object getAnnotationValue(J joinPoint, Class<A> annotation, String property) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        A annotationObject = AnnotationUtils.findAnnotation(method, annotation);
        if (annotationObject != null) {
            Object value = AnnotationUtils.getValue(annotationObject, property);
            if (value instanceof Object[] values) {
                if (values.length > 0) {
                    return values[0];
                } else {
                    return null;
                }
            } else {
                return value;
            }
        }
        return null;
    }

    /**
     * Retrieves all non-empty annotation property values as a list of entries from the specified annotation
     * on the method in the given join point.
     *
     * @param joinPoint  the join point representing the method invocation
     * @param annotation the annotation class to retrieve properties from
     * @param <A>        the type of the annotation
     * @param <J>        the type of the join point
     * @return a list of annotation property entries, or an empty list if the annotation is not present
     */
    public static <A extends Annotation, J extends JoinPoint> List<Map.Entry<String, Object>> getAnnotationValues(J joinPoint, Class<A> annotation) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        A annotationObject = AnnotationUtils.findAnnotation(method, annotation);
        return annotationObject != null ? AnnotationUtils.getAnnotationAttributes(annotationObject).entrySet().stream()
                .filter(Objects::nonNull)
                .filter(entry -> !(entry.getValue() instanceof String value && value.isEmpty()))
                .toList()
                : Collections.emptyList();
    }
}
