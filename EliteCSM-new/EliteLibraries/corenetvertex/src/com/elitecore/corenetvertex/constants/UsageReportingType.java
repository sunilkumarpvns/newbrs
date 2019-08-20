package com.elitecore.corenetvertex.constants;

public enum UsageReportingType {

    NON_CUMULATIVE("Non-Cumulative"),
    CUMULATIVE("Cumulative");
    private String value;

    UsageReportingType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
