package com.elitecore.elitesm.web.radius.radiusesigroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum RedundancyMode {

    NM(1, "N+M"),
    ACTIVE_PASSIVE(2, "ACTIVE-PASSIVE");

    public final int typeId;
    public final String redundancyModeName;
    private static Map<Integer, String> nameToType = new HashMap<>();
    private static final  RedundancyMode[] VALUES = values();
    private static final ArrayList<String> modeNames;

    static {
        modeNames = new ArrayList<>();
        for (RedundancyMode entry: VALUES) {
            nameToType.put(entry.typeId, entry.redundancyModeName);
            modeNames.add(entry.redundancyModeName);
        }
    }

    private RedundancyMode (int redundancyModeType, String redundancyMode) {
        this.typeId = redundancyModeType;
        this.redundancyModeName = redundancyMode;
    }

    public static String getRedundancyMode(int typeId) {
        return nameToType.get(typeId);
    }

    public static ArrayList<String> getRedundancyModeNames(){
        return modeNames;
    }
}


