package com.elitecore.corenetvertex.pd.topup;

import com.elitecore.commons.base.Strings;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public enum TopUpType {
    TOP_UP("Top-Up"),
    SPARE_TOP_UP("Spare Top-Up");

    private String val;
    private static Map<String,TopUpType> topupTypeMap = null;

    static{
        topupTypeMap = new HashMap<>();

        for(TopUpType topUpType : values()){
            topupTypeMap.put(topUpType.val,topUpType);
        }
    }

    TopUpType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public @Nullable TopUpType fromVal(String val){
        if(Strings.isNullOrBlank(val)){
            return null;
        }
        return topupTypeMap.get(val);
    }
}
