package com.elitecore.nvsmx.system.hibernate;

import com.elitecore.core.commons.util.db.DBVendors;

import java.util.HashMap;
import java.util.Map;

public enum HibernateSupportedVendor {
    ORACLE(DBVendors.ORACLE,"org.hibernate.dialect.Oracle10gDialect"),
    POSTGRESQL(DBVendors.POSTGRESQL,"org.hibernate.dialect.PostgreSQL9Dialect");


    public String getDialect() {
        return dialect;
    }

    private final String dialect;
    private DBVendors vendors;
    private static final Map<DBVendors,HibernateSupportedVendor> vendorDialectMap = new HashMap<>();

    private HibernateSupportedVendor(DBVendors dbVendors, String dialect){
        this.vendors = dbVendors;
        this.dialect = dialect;

    }
    static {
       for(HibernateSupportedVendor hibernateDialect : HibernateSupportedVendor.values()){
               vendorDialectMap.put(hibernateDialect.vendors,hibernateDialect);
       }
    }

    public static HibernateSupportedVendor fromDBVendor(DBVendors dbVendors){
        return vendorDialectMap.get(dbVendors);
    }

}
