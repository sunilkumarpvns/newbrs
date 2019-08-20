package com.elitecore.corenetvertex.constants;

/**
 * @author Prakash Pala
 * @since 8-Feb-2019
 * This enum is introduced for identifying transation type (w.r.t. DB) which will be used in EDRs
 */
public enum ActionType {
    ADD("Add"),
    UPDATE("Update"),
    DELETE("Delete");

    private String displayName;

    ActionType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
