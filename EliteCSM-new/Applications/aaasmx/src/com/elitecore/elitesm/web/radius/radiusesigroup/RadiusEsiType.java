package com.elitecore.elitesm.web.radius.radiusesigroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum RadiusEsiType {

    AUTH(1, "AUTH"),
    ACCT(2, "ACCT"),
    CHARGING_GATEWAY(3, "CHARGING GATEWAY"),
    CORRELATED_RADIUS(4, "CORRELATED RADIUS");

    public String name;
    public int id;
    private static final Map<Integer,RadiusEsiType> map;

    public static final RadiusEsiType[] VALUES = values();
    private static final ArrayList<String> esiNames;

    static {
        map = new HashMap<Integer,RadiusEsiType>();
        esiNames = new ArrayList<>();
        for (RadiusEsiType type : VALUES) {
            map.put(type.id, type);
            esiNames.add(type.name);
        }
    }

    private RadiusEsiType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RadiusEsiType fromStatusCheckMethods(int id) {
        return map.get(id);
    }

    public static ArrayList<String> getEsiTypeNames(){
        return esiNames;
    }
}
