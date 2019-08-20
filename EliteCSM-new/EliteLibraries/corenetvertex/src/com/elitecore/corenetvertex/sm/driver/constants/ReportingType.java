package com.elitecore.corenetvertex.sm.driver.constants;

/**
 * Reporting Type for Driver Management
 * @author dhyani.raval
 */
public enum ReportingType {

    UM("Usage Metering"),
    CHARGING_CDR("Charging CDR"),
    ;

    private String displayValue;

    ReportingType(String displayValue){
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static ReportingType fromValue(String value) {
        if (UM.name().equals(value)) {
            return UM;
        } else if (CHARGING_CDR.name().equals(value)) {
            return CHARGING_CDR;
        }
        return null;
    }
}
