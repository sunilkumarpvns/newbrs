package com.elitecore.corenetvertex.spr.data;

import java.lang.reflect.InvocationTargetException;

public interface DMLProvider {

    public abstract String insertQuery() throws IllegalArgumentException, NullPointerException, IllegalAccessException, InvocationTargetException;

}