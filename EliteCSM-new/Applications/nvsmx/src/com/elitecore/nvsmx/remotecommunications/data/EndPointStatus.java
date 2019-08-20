package com.elitecore.nvsmx.remotecommunications.data;

/**
 * Created by aditya on 6/20/17.
 */
public enum EndPointStatus {

    SHUT_DOWN("SHUTDOWN"),
    STARTED("STARTED");

    private String val;

    EndPointStatus(String val) {
        this.val = val;
    }

    public String getVal(){
        return val;
    }
}
