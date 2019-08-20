package com.elitecore.corenetvertex.constants;

import java.util.HashMap;

/**
 * Possible options for Data Type in Session Configuration Field Mapping
 * @author dhyani.raval
 */
public enum FieldMappingDataType {

    STRING(0,"String"),
    TIMESTAMP(1,"Timestamp"),
    ;

    private Integer value;
    private String displayValue;

    private static final HashMap<Integer,FieldMappingDataType> map;
    static {
        map = new HashMap<Integer,FieldMappingDataType>();
        for (FieldMappingDataType fieldMappingDataType : values()) {
            map.put(fieldMappingDataType.value, fieldMappingDataType);
        }
    }

    FieldMappingDataType(int value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static String fetchDisplayValueFromValue(Integer value) {

       return map.get(value).getDisplayValue();
    }

}
