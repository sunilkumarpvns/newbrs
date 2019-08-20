package com.elitecore.corenetvertex.util;

/**
 * Created by harsh on 2/20/17.
 */
public interface Bifunction<F, T> {

    public T to(F f);
    public F from(T t);
}
