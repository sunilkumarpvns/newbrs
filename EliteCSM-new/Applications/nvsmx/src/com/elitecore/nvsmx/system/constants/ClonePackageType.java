package com.elitecore.nvsmx.system.constants;

public enum ClonePackageType {
    DATA("DATA", "Data Package"),
    RNC("RNC", "RnC Package");

    private String type;
    private String name;

    ClonePackageType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}