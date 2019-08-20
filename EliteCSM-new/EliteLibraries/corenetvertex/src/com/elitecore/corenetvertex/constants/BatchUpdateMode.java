package com.elitecore.corenetvertex.constants;

/**
 * Possible options for batch update mode in session configuration
 * @author dhyani.raval
 */
public enum BatchUpdateMode {

    TRUE(0,"True"),
    FALSE(1,"False"),
    HYBRID(2,"Hybrid"),
    ;

    private int value;
    private String displayValue;

    BatchUpdateMode(int value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static BatchUpdateMode fromValue(int value) {
        if (TRUE.getValue() == value) {
            return TRUE;
        } else if (FALSE.getValue() == value) {
            return FALSE;
        } else if (HYBRID.getValue() == value) {
            return HYBRID;
        }
        return null;
    }

}
