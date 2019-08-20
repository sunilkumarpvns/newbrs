package com.elitecore.corenetvertex.sm.systemparameter;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum contains default value for package parameters. If value doesn't exists in db, then this value will be used.
 * @author jaidiptrivedi
 */
public enum PackageParameter {
    PARAM_1("Param 1 Field Label", "", false, "Label of package param1 attribute. Default Param 1 if no label is configured."),
    PARAM_2("Param 2 Field Label", "", false, "Label of package param2 attribute. Default Param 2 if no label is configured.");

    private String name;
    private String value;
    private boolean systemGenerated;
    private String description;
    private static Map<String, PackageParameter> nameMap;

    static {
        nameMap = new HashMap<>();
        for (PackageParameter systemParameter : values()) {
            nameMap.put(systemParameter.name(), systemParameter);
        }
    }

    PackageParameter(String name, String value, boolean systemGenerated, String description) {
        this.name = name;
        this.value = value;
        this.systemGenerated = systemGenerated;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isSystemGenerated() {
        return systemGenerated;
    }

    public String getDescription() {
        return description;
    }
    
    public static PackageParameter fromName(String name) {
        return nameMap.get(name);
    }
}
