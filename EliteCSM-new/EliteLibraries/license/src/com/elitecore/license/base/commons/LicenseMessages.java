package com.elitecore.license.base.commons;

import java.util.HashMap;
import java.util.Map;

public enum LicenseMessages {
    SUCCESS(200,"Success"),
    LICENSE_DEREGISTERED(201, "License deregistered"),
    LICENSE_AUTHENTICITY_CHECK_FAILURE(300,"Server instance with name and id provided does not match with SM record."),
    LICENSE_LIMIT_REACHED(301,"Unable to allocate license, Reason: Maximum license instance limit reached"),
    LICENSE_DOES_NOT_EXIST(302, "License does not exist on server manager."),
    LICENSE_NEVER_REGISTERED(303, "Can not deregister, License has not registered yet or has already been deregistered."),
    LICENSE_ISSUED_BEYOND_LIMIT(304, "License instance limit has been decreased, deregistered the license."),
    MISSING_REQUIRED_PARAMETERS(305, "Missing required parameters."),
    NV_UNKNOWN_SERVER_INSTANCE(401, "This NetVertex server is unknown to the SM."),
    NV_ID_NAME_MISMATCH_WITH_SM(402, "Server instance with name and id provided does not match with SM record."),
    ENCRYPTION_ERROR(501, "Encryption error."),
    INVALID_LICENSE_KEY(502, "Invalid license key"),
    LICENSE_FILE_EXPIRED(503, "Expired license file is being used for update."),
    INVALID_LICENSE_FILE_UPLOADED(504, "File uploaded is not a valid license file."),
    PUBLIC_KEY_GENERATION_ERROR(505, "Couldn't generate public key. Please contact support."),
    INVALID_ENCODING(506, "Encoding error occurred."),
    FAILED_TO_CONNECT_WITH_SM_SERVER(507, "Failed to connect with Server Manager."),
    LICENSE_EXPIRED(508, "License uploaded on server manager has been expired."),
    INVALID_LICENSE_ON_SERVER_MANAGER(509, "Invalid license on server manager."),
    UNDEFINED(555, "UNDEFINED");

    static Map<Integer, LicenseMessages> licenseMessagesMap;

    static {
        licenseMessagesMap = new HashMap<Integer,LicenseMessages>();
        for(LicenseMessages messages:values()){
            licenseMessagesMap.put(messages.messageCode,messages);
        }
    }

    private int messageCode;
    private String message;

    LicenseMessages(int messageCode, String message){
        this.messageCode = messageCode;
        this.message = message;
    }

    public static LicenseMessages getByMessageCode(int messageCode){
        LicenseMessages messages = licenseMessagesMap.get(messageCode);
        if(messages == null){
            return UNDEFINED;
        } else {
            return messages;
        }

    }

    public int getMessageCode(){
        return messageCode;
    }

    public String getMessage(){
        return message;
    }
}