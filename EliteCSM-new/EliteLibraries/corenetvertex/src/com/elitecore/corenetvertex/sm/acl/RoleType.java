package com.elitecore.corenetvertex.sm.acl;

import java.util.Arrays;

public enum RoleType {

    ADMIN("Admin"),
    READ_ONLY("Read Only"),
    STANDARD("Standard");

    private String label;

    RoleType(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
    public static String getFromName(String name){
        RoleType roleType =  Arrays.stream(RoleType.values())
                        .filter(typeConst->typeConst.name().equals(name))
                        .findFirst()
                        .orElse(ADMIN);
        return roleType.label;
    }
}
