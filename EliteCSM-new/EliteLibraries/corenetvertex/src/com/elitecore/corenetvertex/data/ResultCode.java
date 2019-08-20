package com.elitecore.corenetvertex.data;

public enum ResultCode{

    SUCCESS("SUCCESS", 2001),
    DIAMETER_END_USER_SERVICE_DENIED("DIAMETER_END_USER_SERVICE_DENIED", 4010),
    BALANCE_DOES_NOT_EXISTS("CREDIT_LIMIT_REACH", 4012);

    public final String val;
    public final int code;
    ResultCode(String val, int code){
        this.val = val;
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    public String getVal(){
        return val;
    }

}