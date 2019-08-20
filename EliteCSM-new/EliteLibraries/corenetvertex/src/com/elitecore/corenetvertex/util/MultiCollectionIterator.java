package com.elitecore.corenetvertex.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import com.elitecore.corenetvertex.util.commons.Supplier;

/**
 *
 * MultiCollectionIterator will allow to iterate on multiple collections
 *
 * Created by Chetan.Sankhala on 31/5/17.
 */


//TODO FIX EMPTY COLLECTION ISSUE IN SUPPLY
public class MultiCollectionIterator<E> implements Iterator<E> {

    private @Nonnull Iterator<E> currentIterator;
    private @Nonnull  Supplier<Collection<E>> supplier;

    public MultiCollectionIterator(@Nonnull Supplier<Collection<E>> supplier) {
        this.supplier = supplier;
        Collection<E> collection = supplier.supply();
        if (collection == null) {
        	this.currentIterator = new EmptyIterator<E>();
        } else {
        	this.currentIterator = collection.iterator();
        }
    }

    @Override
    public boolean hasNext() {

        if (currentIterator.hasNext()) {
            return true;
        }

        Collection<E> newCollection = this.supplier.supply();

        if (newCollection == null) {
            return false;
        }

        currentIterator = newCollection.iterator();

        return currentIterator.hasNext();
    }

    @Override
    public E next() {
        return currentIterator.next();
    }

    @Override
    public void remove() {
        currentIterator.remove();
    }


    private static class EmptyIterator<E> implements Iterator<E> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
           throw new NoSuchElementException();
        }

        @Override
        public void remove() {

        }
    }
}
