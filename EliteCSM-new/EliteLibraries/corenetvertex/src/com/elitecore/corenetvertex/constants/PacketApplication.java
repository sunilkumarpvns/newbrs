package com.elitecore.corenetvertex.constants;

public enum PacketApplication {
    GX("Gx"),
    GY("Gy"),
    RX("Rx");
    private String applicationName;

    private PacketApplication(String applicationName){
        this.applicationName = applicationName;

    }
}
