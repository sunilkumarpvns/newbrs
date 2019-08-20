package com.elitecore.corenetvertex.data;

public enum CarryForwardStatus {
    NOT_CARRY_FORWARD,
    CARRY_FORWARD,
    CARRY_FORWARD_DONE;

    public static CarryForwardStatus fromValue(String value) {

        for(CarryForwardStatus status: CarryForwardStatus.values()){
            if(status.name().equals(value)){
                return status;
            }
        }
        return null;
    }
}
