package com.elitecore.corenetvertex.constants;

/**
 * Driver Type for Driver Management
 * @author dhyani.raval
 */
public enum DriverType {

    CSV_DRIVER("CSV Driver"),
    DB_CDR_DRIVER("DB CDR Driver"),
    ;

    private String displayValue;

    DriverType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
