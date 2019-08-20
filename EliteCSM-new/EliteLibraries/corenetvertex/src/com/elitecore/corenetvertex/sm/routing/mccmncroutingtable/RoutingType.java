package com.elitecore.corenetvertex.sm.routing.mccmncroutingtable;

/**
 * @author jaidiptrivedi
 */
public enum RoutingType {
    MCC_MNC_BASED("MCC-MNC-BASED"),
    CUSTOM_REALM_BASED("CUSTOM-REALM-BASED");

    private String value;

    RoutingType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
