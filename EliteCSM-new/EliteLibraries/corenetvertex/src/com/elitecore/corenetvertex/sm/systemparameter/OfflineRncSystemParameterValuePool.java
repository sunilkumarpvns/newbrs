package com.elitecore.corenetvertex.sm.systemparameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum OfflineRncSystemParameterValuePool {

	SESSION_COONECTION_TIME(OfflineRnCRatingSystemParameter.RATE_SELECTION_WHEN_DATE_CHANGE, "SESSION-CONNECT-TIME", "SESSION-CONNECT-TIME"),
    SESSION_DISCONNECT_TIME(OfflineRnCRatingSystemParameter.RATE_SELECTION_WHEN_DATE_CHANGE, "SESSION-DISCONNECT-TIME", "SESSION-DISCONNECT-TIME"),
    YES(OfflineRnCRatingSystemParameter.INSERT_ZERO_USAGE_CDR,"Yes","Yes"),
    NO(OfflineRnCRatingSystemParameter.INSERT_ZERO_USAGE_CDR,"No","No"),
	ONE(OfflineRnCRatingSystemParameter.NUMBER_OF_DECIMAL_POINTS_IN_TRANSACTION,"1","1"),
	TWO(OfflineRnCRatingSystemParameter.NUMBER_OF_DECIMAL_POINTS_IN_TRANSACTION,"2","2"),
	THREE(OfflineRnCRatingSystemParameter.NUMBER_OF_DECIMAL_POINTS_IN_TRANSACTION,"3","3"),
	FOUR(OfflineRnCRatingSystemParameter.NUMBER_OF_DECIMAL_POINTS_IN_TRANSACTION,"4","4"),
	FIVE(OfflineRnCRatingSystemParameter.NUMBER_OF_DECIMAL_POINTS_IN_TRANSACTION,"5","5"),
	SIX(OfflineRnCRatingSystemParameter.NUMBER_OF_DECIMAL_POINTS_IN_TRANSACTION,"6","6"),
	TRUNCATE(OfflineRnCRatingSystemParameter.ROUNDING_CURRENCY_TO_SPECIFIED_DECIMAL_POINT,"TRUNCATE","TRUNCATE"),
	UPPER(OfflineRnCRatingSystemParameter.ROUNDING_CURRENCY_TO_SPECIFIED_DECIMAL_POINT,"UPPER","UPPER");

    private final OfflineRnCRatingSystemParameter key;
    public final String val;
    public final String displayValue;

    private static final Map<String, List<OfflineRncSystemParameterValuePool>> valueMap;

    OfflineRncSystemParameterValuePool(OfflineRnCRatingSystemParameter key, String val, String displayValue) {
        this.key = key;
        this.val = val;
        this.displayValue = displayValue;
    }

    public static Map<String, List<OfflineRncSystemParameterValuePool>> getValueMap() {
        return valueMap;
    }

    static {

        valueMap = new HashMap<>();
        for (OfflineRncSystemParameterValuePool systemParameterValuePool : OfflineRncSystemParameterValuePool.values()) {
            List<OfflineRncSystemParameterValuePool> vals = valueMap.get(systemParameterValuePool.key.toString());
            if (vals == null) {
                vals = new ArrayList<>();
                valueMap.put(systemParameterValuePool.key.toString(), vals);
            }
            vals.add(systemParameterValuePool);
        }

    }
}
