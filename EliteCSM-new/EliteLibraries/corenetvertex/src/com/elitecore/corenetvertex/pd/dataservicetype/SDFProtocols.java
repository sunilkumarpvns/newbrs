package com.elitecore.corenetvertex.pd.dataservicetype;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jaidiptrivedi
 */
public enum SDFProtocols {
    IP("IP", "ip"),
    TCP("TCP", "6"),
    UDP("UDP", "17");

    private String displayValue;
    private String val;
    private static Map<String, SDFProtocols> valueMap;

    static {
        valueMap = new HashMap<>();
        for (SDFProtocols sdfProtocols : values()) {
            valueMap.put(sdfProtocols.val, sdfProtocols);
        }
    }

    SDFProtocols(String displayValue, String val) {
        this.displayValue = displayValue;
        this.val = val;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getVal() {
        return val;
    }

    public static SDFProtocols fromVal(String val) {
        return valueMap.get(val);
    }

}
