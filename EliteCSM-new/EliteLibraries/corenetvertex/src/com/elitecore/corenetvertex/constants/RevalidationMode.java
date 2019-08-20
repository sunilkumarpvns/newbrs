package com.elitecore.corenetvertex.constants;

public enum RevalidationMode {
    ClientInitiated("Client Initiated"),
    ServerInitiated("Server Initiated");

    private String value;
    RevalidationMode(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
