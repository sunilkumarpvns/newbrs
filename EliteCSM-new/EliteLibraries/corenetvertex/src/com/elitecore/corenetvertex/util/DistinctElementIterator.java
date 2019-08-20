package com.elitecore.corenetvertex.util;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * DistinctElementIterator gives only distinct element when next is called.
 *
 * @param <T> Created by chetan on 1/6/17.
 */
public class DistinctElementIterator<T> implements Iterator<T> {

    private @Nonnull Set<T> iteratedElements;
    private @Nonnull Iterator<T> actualIterator;
    private boolean hasNextVal;
    private boolean hasNextCalled;
    private boolean nextCalled;
    private T value;

    public DistinctElementIterator(@Nonnull Iterator<T> actualIterator) {
        this.actualIterator = actualIterator;
        this.iteratedElements = new HashSet<T>();
    }

    @Override
    public boolean hasNext() {

        if (hasNextCalled && nextCalled == false) {
            return hasNextVal;
        }

        hasNextCalled = true;

        // iterate till next distinct element found
        hasNextVal = actualIterator.hasNext();
        while (hasNextVal) {
            T tempValue = actualIterator.next();
            if (iteratedElements.add(tempValue)) {
                value = tempValue;
                break;
            }
            hasNextVal = actualIterator.hasNext();
        }

        nextCalled = false;
        return hasNextVal;
    }

    @Override
    public @Nonnull T next() {
        nextCalled = true;
        if (hasNextCalled) {
            //when next is called after hasNext()
            if (hasNextVal) {
                return getAndReset();
            }
        } else if (hasNext()) {
            //when next() is called without hasNext()
            return getAndReset();
        }

        throw new NoSuchElementException();
    }

    private T getAndReset() {
        T temp = value;
        reset();
        return temp;
    }

    private void reset() {
        value = null;
        hasNextCalled = false;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported from DistinctElementIterator");
    }
}
