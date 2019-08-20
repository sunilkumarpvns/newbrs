package com.elitecore.corenetvertex.pkg;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author kirpalsinh.raj
 */
public enum PkgType {

    BASE("Base", PkgCategory.USER_PLAN),
    ADDON("AddOn", PkgCategory.USER_PLAN),
    EMERGENCY("Emergency", PkgCategory.GLOBAL_PLAN),
	PROMOTIONAL("Promotional", PkgCategory.GLOBAL_PLAN);

    public final String val;
    public final PkgCategory pkgCategory;

    private static Map<PkgCategory, List<PkgType>> pkgTypeMap;
    private static Map<String,PkgType> namePkgTypeMap;

    PkgType(String val, PkgCategory pkgCategory) {
        this.val = val;
        this.pkgCategory = pkgCategory;
    }



    static {
        pkgTypeMap = new EnumMap<>(PkgCategory.class);
        namePkgTypeMap = new HashMap<>();
        for (PkgCategory pkgCategory : PkgCategory.values()) {
            List<PkgType> pkgTypeList = Collectionz.newArrayList();
            pkgTypeMap.put(pkgCategory, pkgTypeList);
        }
        for (PkgType pkgType : values()) {
                pkgTypeMap.get(pkgType.pkgCategory).add(pkgType);
                namePkgTypeMap.put(pkgType.name(),pkgType);

        }
    }

    public static List<PkgType> getUserPlan() {
        return pkgTypeMap.get(PkgCategory.USER_PLAN);

    }
    public static List<PkgType> getGlobalPlan() {
        return pkgTypeMap.get(PkgCategory.GLOBAL_PLAN);
    }

    public static @Nullable PkgType fromName(String name){
      if(Strings.isNullOrBlank(name) == true ){
          return null;
      }
      return namePkgTypeMap.get(name);
    }

}
