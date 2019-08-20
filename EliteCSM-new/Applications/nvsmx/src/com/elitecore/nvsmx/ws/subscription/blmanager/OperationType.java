package com.elitecore.nvsmx.ws.subscription.blmanager;

public enum OperationType{
    CREDIT("CR", "Credit"),
    DEBIT("DR", "Debit"),;
    private String name;
    private String displayName;
    OperationType(String name, String displayName){
        this.name = name;
        this.displayName = displayName;
    }
    public static OperationType fromVal(String name) {
        if (CREDIT.name.equalsIgnoreCase(name)) {
            return CREDIT;
        } else if (DEBIT.name.equalsIgnoreCase(name)) {
            return DEBIT;
        }
        return null;
    }
    public static String getNames(){
        return CREDIT.name+", "+DEBIT.name;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}
