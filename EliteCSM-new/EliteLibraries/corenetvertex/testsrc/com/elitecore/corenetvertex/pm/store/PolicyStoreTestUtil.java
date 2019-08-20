package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;

import java.util.function.Predicate;

import static org.junit.Assert.*;

public class PolicyStoreTestUtil {

    public static <T> Predicate<T> filterAlwaysTrue() {
        return pkg-> true;
    }

    public static <T> Predicate<T> filterAlwaysFalse() {
        return pkg-> false;
    }

}