package com.elitecore.corenetvertex.constants;

public enum RequestAction {
    PROCESS_REQUEST("Process Request"),
    DROP_REQUEST("Drop Request");
    private String label;

    RequestAction(String label){
        this.label = label;
    }
    public String getLabel(){
        return this.label;
    }

    public static String getLabelFromInstanceName(String name){
        if(PROCESS_REQUEST.name().equals(name)){
            return PROCESS_REQUEST.label;
        }else if(DROP_REQUEST.name().equals(name)){
            return DROP_REQUEST.label;
        }
        return null;
    }
}
