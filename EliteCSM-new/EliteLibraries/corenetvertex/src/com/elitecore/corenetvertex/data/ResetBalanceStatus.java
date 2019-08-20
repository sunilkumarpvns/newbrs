package com.elitecore.corenetvertex.data;

public enum ResetBalanceStatus {
    NOT_RESET,
    RESET,
    RESET_DONE,
    MARK_AS_DELETED;

    public static ResetBalanceStatus fromValue(String value) {

        for(ResetBalanceStatus status: ResetBalanceStatus.values()){
            if(status.name().equals(value)){
                return status;
            }
        }
        return null;
    }
}
