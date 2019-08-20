package com.elitecore.nvsmx.sm.controller.serverinstance;

public enum ServerInstanceWeightage {
    PRIMARY(1,"Primary"),
    SECONDARY(2,"Secondary");

    public int val;
    public String label;
    ServerInstanceWeightage(int val, String label){
        this.val = val;
        this.label =  label;
    }

    public int getVal(){
        return val;
    }

    public String getLabel(){
        return label;
    }
}
