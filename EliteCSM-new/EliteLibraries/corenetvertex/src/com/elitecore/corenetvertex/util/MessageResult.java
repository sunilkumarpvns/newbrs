package com.elitecore.corenetvertex.util;

/**
 * this class is used to get the status of any operation with some valid reasons / message or any value that is passed after
 * operation is performed. Consider a class as a message conveyor that convey the message after operation performed with status/boolean.
 * Created by ishani.bhatt on 1/6/17.
 */
public class MessageResult {

    private boolean flag;
    private String message;

    public MessageResult(){}
    public MessageResult(boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
