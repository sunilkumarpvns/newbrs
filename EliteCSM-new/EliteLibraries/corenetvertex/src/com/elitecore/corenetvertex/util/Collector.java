package com.elitecore.corenetvertex.util;

/**
 * Created by aditya on 5/22/17.
 */
public interface Collector<T, newT> {

    void start();
    void stop();
    void collect(T t);
    newT get();
}
