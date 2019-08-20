package com.elitecore.elitesm.web.systemstartup.defaultsetup.controller;

import java.util.HashMap;
import java.util.Map;

public enum CaseSensitivity {

    NO_CASE("0","Case Sensitive"),
    LOWER_CASE("1","Lower Case"),
    UPPER_CASE("2","Upper Case");

    public String id;
    public String name;
    private static final Map<String,String> map;
    final public static String POLICY_CASESENSITIVITY = "CASE_SENSITIVITY_FOR_POLICY";
    final public static String SUBSCRIBER_CASESENSITIVITY = "CASE_SENSITIVITY_FOR_SUBSCRIBER";

    static {
        map = new HashMap<String,String>();
        for (CaseSensitivity type : values()) {
            map.put(type.id, type.name);
        }
    }
    CaseSensitivity(String val,String name){
        this.id = val;
        this.name = name;
    }

    public static String getCaseValueFromId(String paramId){
        return map.get(paramId);
    }
}
