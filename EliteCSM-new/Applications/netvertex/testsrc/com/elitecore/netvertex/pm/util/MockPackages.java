package com.elitecore.netvertex.pm.util;


/**
 * Created by harsh on 5/25/17.
 */
public class MockPackages {

    public static MockBasePackage basePackage(String id, String name) {
        return MockBasePackage.create(id, name);
    }

    public static MockAddOnPackage addOn(String id, String name) {
        return MockAddOnPackage.create(id, name);
    }

    public static MockIMSPackage ims(String id, String name) {
        return MockIMSPackage.create(id, name);
    }

    public static MockPromotionalPackage promotionalPackage(String id, String name) {
        return MockPromotionalPackage.create(id, name);
    }
}
