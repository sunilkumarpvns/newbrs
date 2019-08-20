package com.elitecore.corenetvertex.pd.ratecard;

public enum RateCardScope {
    GLOBAL,
    LOCAL;

    public static RateCardScope fromValue(String value){
        if(GLOBAL.name().equalsIgnoreCase(value)){
            return GLOBAL;
        }else if(LOCAL.name().equalsIgnoreCase(value)){
            return LOCAL;
        } else {
            return null;
        }
    }
}
