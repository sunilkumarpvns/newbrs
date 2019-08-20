package com.elitecore.corenetvertex.pkg.constants;

public enum OperationType {
    ABSOLUTE("Absolute"),
    PERCENTAGE("Percentage");

    private String displayValue;

    OperationType(String displayValue) {

        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
