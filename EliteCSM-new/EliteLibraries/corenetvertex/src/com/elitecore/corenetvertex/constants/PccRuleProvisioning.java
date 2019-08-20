package com.elitecore.corenetvertex.constants;

/**
 * Created by Ishani on 6/9/17.
 */
public enum PccRuleProvisioning {

    ALL_ON_NETWORK_ENTRY(0,"All On Network Entry"),
    FIRST_ON_NETWOEK_ENTRY(1,"First On Network Entry"),
    NONE_ON_NETWORK_ENTRY(2,"None On Network Entry");

    private int val;
    private String displayLabel;
    PccRuleProvisioning(int val, String displayLabel){
        this.val = val;
        this.displayLabel = displayLabel;
    }

    public static PccRuleProvisioning fromVal(int id){
        switch (id){
            case 0:
                return ALL_ON_NETWORK_ENTRY;
            case 1:
                return FIRST_ON_NETWOEK_ENTRY;
            case 2:
                return NONE_ON_NETWORK_ENTRY;
            default:
                return null;
        }
    }


    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }
}
