package com.elitecore.corenetvertex.pd.topup;

public enum TopUpQuotaType {

    VOLUME("Volume"),
    TIME("Time");


    private final String value;

    TopUpQuotaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }


}
