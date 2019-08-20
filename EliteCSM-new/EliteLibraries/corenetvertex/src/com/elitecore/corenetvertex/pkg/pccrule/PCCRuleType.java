package com.elitecore.corenetvertex.pkg.pccrule;

public enum PCCRuleType {
    STATIC("Static"),
    DYNAMIC("Dynamic");

    public final String value;

    private PCCRuleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
