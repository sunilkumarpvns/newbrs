package com.elitecore.corenetvertex.sm.systemparameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jaidiptrivedi
 */
public enum SystemParameterValuePool {

    NO_ACTION(SystemParameter.UPDATE_ACTION, "0", "No_Action(0)"),
    REAUTH_SESSION(SystemParameter.UPDATE_ACTION, "1", "ReAuth_Session(1)"),
    DISCONNECT_SESSION(SystemParameter.UPDATE_ACTION, "2", "Disconnect_Session(2)");

    private final SystemParameter key;
    public final String val;
    public final String displayValue;

    private static final Map<String, List<SystemParameterValuePool>> valueMap;

    SystemParameterValuePool(SystemParameter key, String val, String displayValue) {
        this.key = key;
        this.val = val;
        this.displayValue = displayValue;
    }

    public static Map<String, List<SystemParameterValuePool>> getValueMap() {
        return valueMap;
    }

    static {

        valueMap = new HashMap<>();
        for (SystemParameterValuePool systemParameterValuePool : SystemParameterValuePool.values()) {
            List<SystemParameterValuePool> vals = valueMap.get(systemParameterValuePool.key.toString());
            if (vals == null) {
                vals = new ArrayList<>();
                valueMap.put(systemParameterValuePool.key.toString(), vals);
            }
            vals.add(systemParameterValuePool);
        }

    }

}
