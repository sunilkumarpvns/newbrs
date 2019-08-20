package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Predicate;

import javax.annotation.Nonnull;


/**
 * Created by aditya on 5/30/17.
 */
public class NotPredicate<T> implements Predicate<T> {

    private final Predicate<T> predicate;

    private NotPredicate(Predicate<T> predicate) {
        this.predicate =  predicate;
    }

    public static <T> NotPredicate<T> of(@Nonnull Predicate<T> predicate){
            return new NotPredicate<T>(predicate);
    }

    @Override
    public boolean apply(T input) {
        return !predicate.apply(input);
    }
}
