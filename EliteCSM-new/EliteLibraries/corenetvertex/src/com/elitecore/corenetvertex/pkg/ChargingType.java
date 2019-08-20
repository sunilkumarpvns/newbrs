package com.elitecore.corenetvertex.pkg;

import com.elitecore.commons.base.Strings;

import java.util.HashMap;
import java.util.Map;


public enum ChargingType {

    SESSION,
    EVENT;


    private static Map<String,ChargingType> charingTypeMap;

    static {
        charingTypeMap = new HashMap<>();
        for (ChargingType ChargingType : values()) {
                charingTypeMap.put(ChargingType.name(),ChargingType);

        }
    }

    public static ChargingType fromName(String name){
      if(Strings.isNullOrBlank(name) == true ){
          return null;
      }
      return charingTypeMap.get(name);
    }

}
