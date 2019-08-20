package com.elitecore.corenetvertex.constants;

/**
 * Used for Sp Interface Type
 * @author dhyani.raval
 */
public enum  SpInterfaceType {

    DB_SP_INTERFACE(0,"DB Sp Interface"),
    LDAP_SP_INTERFACE(1,"LDAP Sp Interface"),
    ;

    private Integer value;
    private String displayValue;

    SpInterfaceType (Integer value , String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }

    public Integer getValue() {
        return value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static String fetchFromName(String name){
        if (DB_SP_INTERFACE.name().equals(name)) {
            return DB_SP_INTERFACE.displayValue;
        } else if (LDAP_SP_INTERFACE.name().equals(name)) {
            return LDAP_SP_INTERFACE.displayValue;
        }
        return null;
    }
}
