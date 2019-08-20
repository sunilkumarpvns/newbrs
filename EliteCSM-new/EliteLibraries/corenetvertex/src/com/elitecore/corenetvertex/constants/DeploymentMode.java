package com.elitecore.corenetvertex.constants;

import com.elitecore.commons.base.Strings;

import java.util.HashMap;
import java.util.Map;

public enum DeploymentMode {

    PCC("PCC"),
    PCRF("PCRF"),
    OCS("OCS");

    private String value;
    private static Map<String,DeploymentMode> valToDeploymentMode;

    static {
        valToDeploymentMode = new HashMap<>();
        for (DeploymentMode deploymentMode : values()) {
            valToDeploymentMode.put(deploymentMode.name(), deploymentMode);

        }
    }

    DeploymentMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DeploymentMode fromName(String name){
        if(Strings.isNullOrBlank(name) == true ){
            return null;
        }
        return valToDeploymentMode.get(name);
    }

}
