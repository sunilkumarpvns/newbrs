package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;

public class ValidationData {

    private ResultCode code;
    private String message;

    public ValidationData(ResultCode resultCode, String responseMessage){
        this.code = resultCode;
        this.message = responseMessage;
    }

    public ResultCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
