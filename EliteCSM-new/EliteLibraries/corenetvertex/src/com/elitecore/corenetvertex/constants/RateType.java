package com.elitecore.corenetvertex.constants;

import com.elitecore.commons.base.Strings;

public enum  RateType {
    FLAT("FLAT"),
    TIERED("TIERED");

    private String val;

    RateType(String rateType){
        this.val = rateType;
    }
    public RateType fromVal(String rateType){
        if(Strings.isNullOrBlank(rateType)){
            return null;
        }
        if(FLAT.val.equals(rateType)){
            return FLAT;
        }else if(TIERED.val.equals(rateType)){
            return TIERED;
        }
        return null;
    }
}
