package com.elitecore.corenetvertex.sm.driver.constants;

/**
 * FileTransferProtocol for Driver Management
 * @author dhyani.raval
 */
public enum FileTransferProtocol {

    LOCAL("Local"),
    FTP("FTP"),
    SMTP("SMTP"),
    ;
    private String value;
    FileTransferProtocol(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public static FileTransferProtocol fromValue(String name) {
        if(LOCAL.name().equals(name)) {
            return LOCAL;
        } else if(FTP.name().equals(name)) {
            return FTP;
        } else if(SMTP.name().equals(name)) {
            return SMTP;
        }
        return null;

    }
}
