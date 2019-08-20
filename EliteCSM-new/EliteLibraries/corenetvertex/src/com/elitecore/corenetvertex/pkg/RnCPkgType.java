package com.elitecore.corenetvertex.pkg;

import com.elitecore.commons.base.Strings;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


/**
 * @author kirpalsinh.raj
 */
public enum RnCPkgType {

    BASE("Base"),
    MONETARY_ADDON("Monetary AddOn"),
    NON_MONETARY_ADDON("Non Monetary AddOn");

    private final String val;

    private static Map<String,RnCPkgType> namePkgTypeMap;

    RnCPkgType(String val) {
        this.val = val;
    }

    static {
        namePkgTypeMap = new HashMap<>();
        for (RnCPkgType pkgType : values()) {
                namePkgTypeMap.put(pkgType.name(),pkgType);

        }
    }

    public String getVal() {
        return val;
    }

    public static @Nullable
    RnCPkgType fromName(String name){
      if(Strings.isNullOrBlank(name) == true ){
          return null;
      }
      return namePkgTypeMap.get(name);
    }

}
