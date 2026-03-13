package com.thanlinardos.spring_enterprise_library.objects.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator<E> implements Iterator<E> {

    /**
     * Get a typed empty iterator instance.
     * @param <E> the element type
     * @return Iterator&lt;E&gt;
     */
    public static <E> Iterator<E> emptyIterator() {
        return new EmptyIterator<>();
    }

    /**
     * Constructor.
     */
    protected EmptyIterator() {
        super();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        throw new NoSuchElementException("Iterator contains no elements");
    }
}