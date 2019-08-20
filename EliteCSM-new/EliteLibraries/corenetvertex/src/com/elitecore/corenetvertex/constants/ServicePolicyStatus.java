package com.elitecore.corenetvertex.constants;

public enum ServicePolicyStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private String label;

    ServicePolicyStatus(String label){
        this.label =  label;
    }

    public String getLabel(){
        return label;
    }

    public static String getLabelFromInstanceName(String name){
        if(ACTIVE.name().equals(name)){
            return ACTIVE.label;
        } else if(INACTIVE.name().equals(name)){
            return INACTIVE.label;
        }
        return null;
    }
}
