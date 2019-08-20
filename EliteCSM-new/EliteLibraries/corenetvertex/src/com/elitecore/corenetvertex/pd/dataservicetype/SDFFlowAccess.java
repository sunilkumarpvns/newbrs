package com.elitecore.corenetvertex.pd.dataservicetype;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jaidiptrivedi
 */
public enum SDFFlowAccess {
    PERMIT_IN("PERMIT IN", "permit in"),
    PERMIT_OUT("PERMIT OUT", "permit out"),
    DENY_IN("DENY IN", "deny in"),
    DENY_OUT("DENY OUT", "deny out");

    private String displayValue;
    private String val;
    private static Map<String, SDFFlowAccess> valueMap;

    static {
        valueMap = new HashMap<>();
        for (SDFFlowAccess sdfFlowAccess : values()) {
            valueMap.put(sdfFlowAccess.val, sdfFlowAccess);
        }
    }

    SDFFlowAccess(String displayValue, String val) {
        this.displayValue = displayValue;
        this.val = val;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getVal() {
        return val;
    }

    public static SDFFlowAccess fromVal(String val) {
        return valueMap.get(val);
    }

}
