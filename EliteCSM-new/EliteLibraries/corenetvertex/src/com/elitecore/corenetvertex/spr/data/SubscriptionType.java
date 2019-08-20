package com.elitecore.corenetvertex.spr.data;

public enum SubscriptionType {

    ADDON("ADDON"),
    TOP_UP("TOP-UP"),
    RO_ADDON("RO-ADDON"),
    BOD("BOD");


    private String val;

    SubscriptionType(String val) {
        this.val = val;
    }


    public static SubscriptionType fromVal(String type) {
        if(ADDON.name().equalsIgnoreCase(type)) {
            return ADDON;
        } else if(TOP_UP.name().equalsIgnoreCase(type)) {
            return TOP_UP;
        }else if(RO_ADDON.name().equalsIgnoreCase(type)) {
            return RO_ADDON;
        }else if(BOD.name().equalsIgnoreCase(type)) {
            return BOD;
        }
        return null;
    }
}
