package com.elitecore.corenetvertex.pkg.pccrule;

/**
 * Created by aditya on 17/5/16.
 */
public enum PCCRuleScope {
    GLOBAL,
    LOCAL;

    public static PCCRuleScope fromValue(String value){
        if(GLOBAL.name().equalsIgnoreCase(value)){
            return GLOBAL;
        }else if(LOCAL.name().equalsIgnoreCase(value)){
            return LOCAL;
        } else {
            return null;
        }
    }
}
