package com.elitecore.corenetvertex.constants;

/**
 * Used to identify the Usage Type for Quota
 * Created by dhyani on 21/8/17.
 */
public enum UsageType {

    VOLUME("Volume"),
    TIME("Time"),
    ;

    private String val;

    UsageType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
