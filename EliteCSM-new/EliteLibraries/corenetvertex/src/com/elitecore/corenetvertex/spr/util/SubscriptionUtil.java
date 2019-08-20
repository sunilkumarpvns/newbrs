package com.elitecore.corenetvertex.spr.util;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.sql.Timestamp;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SubscriptionUtil {

    private  static ObjectMapper objectMapper = new ObjectMapper();

    private SubscriptionUtil(){

    }

    public static void validateStartTimeAndEndTime(long startTimeMs, long endTimeMs, long currentTimeMs) throws OperationFailedException {
        if (endTimeMs <= currentTimeMs) {
            throw new OperationFailedException("End time(" + new Timestamp(endTimeMs).toString() + ") is less or equal to current time", ResultCode.INVALID_INPUT_PARAMETER);
        }

        if (startTimeMs >= endTimeMs) {
            throw new OperationFailedException("Start time(" + new Timestamp(startTimeMs).toString() + ") is more or equal to end time("
                    + new Timestamp(endTimeMs).toString() + ")", ResultCode.INVALID_INPUT_PARAMETER);
        }
    }

    public static SubscriptionMetadata createMetaData(String metadata, String subscriptionId, String module) {
        SubscriptionMetadata subscriptionMetadata = null;
        try {
            if(Strings.isNullOrBlank(metadata)==false){
                subscriptionMetadata = SubscriptionMetadata.parse(metadata);
            }
        }catch (IOException exception){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(module, "Could not create metadata for subscription id "+subscriptionId
                        +", metadata "+metadata+". Reason: "+exception.getMessage());
            }
            getLogger().trace(module, exception);
        }
        return subscriptionMetadata;
    }

    public static String createMetaString(SubscriptionMetadata metadata, String subscriptionId, String module) {
        String subscriptionMetadata = null;
        try {
            if(metadata!=null){
                subscriptionMetadata = objectMapper.writeValueAsString(metadata);
            }
        }catch (IOException exception){
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(module, "Could not metadata for subscription id "+subscriptionId
                        +", metadata "+metadata+". Reason: "+exception.getMessage());
            }
            getLogger().trace(module, exception);
        }
        return subscriptionMetadata;
    }
}
