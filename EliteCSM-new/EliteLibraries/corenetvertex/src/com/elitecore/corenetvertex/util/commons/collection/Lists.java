package com.elitecore.corenetvertex.util.commons.collection;

import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Predicates;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by aditya on 5/30/17.
 */
public class Lists {


    public static @Nonnull <T> List<T> copy(@Nonnull Collection<T> collection, @Nonnull Predicate<? super T> predicate) {
        return copy(collection.iterator(), predicate);
    }

    public static @Nonnull <T> List<T> copy(@Nonnull Collection<T> collection) {
        return copy(collection.iterator());
    }

    public static @Nonnull <T> List<T> copy(@Nonnull Iterator<T> iterator) {

        return copy(iterator, Predicates.<T>alwaysTrue());
    }

    public static @Nonnull <T> List<T> copy(@Nonnull Iterator<T> iterator, @Nonnull Predicate<? super T> predicate) {

        ArrayList<T> newList = new ArrayList<T>();

        while(iterator.hasNext()) {
            T t =  iterator.next();
            if(predicate.apply(t)) {
                newList.add(t);
            }
        }

        return newList;
    }
}
