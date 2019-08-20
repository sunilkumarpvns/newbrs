package com.elitecore.corenetvertex.sm.driver.constants;

/**
 * Position for Driver Management
 * @author dhyani.raval
 */
public enum Position {

    SUFFIX("Suffix"),
    PREFIX("Prefix"),
    ;

    private String displayValue;

    Position(String displayValue) {
        this.displayValue = displayValue;
    }


    public String getDisplayValue() {
        return displayValue;
    }
}
