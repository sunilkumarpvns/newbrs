package com.elitecore.netvertex.core.session.voltdb;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;

import java.util.List;

import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SELECT_CORE_SESSIONS_BY_CRITERIA;
import static com.elitecore.netvertex.core.session.voltdb.VoltSessionProcedures.SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class VoltCriteriaDataFactory {

    private static List<String> coreSessionIdValues;
    private static List<String> sessionIdValues;
    private static List<String> subscriberIdValues;
    private static List<String> sessionIpV4Values;
    private static List<String> sessionIpV6Values;
    private static List<String> sySessionIdValues;
    private static List<String> gatewayAddressValues;


    public static VoltCriteriaData create(String procName, SchemaMapping schemaMapping, String key, String value){
        return new VoltCriteriaData(procName, schemaMapping, key, value);
    }

    public static VoltCriteriaData create(SchemaMapping schemaMapping, Criteria criteria){

        VoltDBCoreSessionCriteria voltDBCriteria = (VoltDBCoreSessionCriteria) criteria;

        coreSessionIdValues = voltDBCriteria.getCoreSessionIdValues();
        sessionIdValues = voltDBCriteria.getSessionIdValues();
        subscriberIdValues = voltDBCriteria.getSubscriberIdValues();
        sessionIpV4Values = voltDBCriteria.getSessionIpV4Values();
        sessionIpV6Values = voltDBCriteria.getSessionIpV6Values();
        sySessionIdValues = voltDBCriteria.getSySessionIdValues();
        gatewayAddressValues = voltDBCriteria.getGatewayAddressValues();

        String coreSessionId = checkAndGetSingleValueOfCoreSessionId();
        if (nonNull(coreSessionId)) {
            return create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, schemaMapping, PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId);
        }

        String sessionId = checkAndGetSingleValueOfSessionId();
        if (nonNull(sessionId)) {
            return create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, schemaMapping, PCRFKeyConstants.CS_SESSION_ID.val, sessionId);
        }

        String subscriberId = checkAndGetSingleValueOfSubscriberId();
        if (nonNull(subscriberId)) {
            return create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, schemaMapping, PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberId);
        }

        String sessionIpV4 = checkAndGetSingleValueOfSessionIpV4();
        if (nonNull(sessionIpV4)) {
            return create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, schemaMapping, PCRFKeyConstants.CS_SESSION_IPV4.val, sessionIpV4);
        }

        String sessionIpV6 = checkAndGetSingleValueOfSessionIpV6();
        if (nonNull(sessionIpV6)) {
            return create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, schemaMapping, PCRFKeyConstants.CS_SESSION_IPV6.val, sessionIpV6);
        }

        String sySessionId = checkAndGetSingleValueOfSySessionId();
        if (nonNull(sySessionId)) {
            return create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, schemaMapping, PCRFKeyConstants.CS_SY_SESSION_ID.val, sySessionId);
        }

        String gatewayAddress = checkAndGetSingleValueOfGatewayAddress();
        if (nonNull(gatewayAddress)) {
            return create(SELECT_CORE_SESSIONS_BY_SINGLE_KEY_VALUE, schemaMapping, PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, gatewayAddress);
        }

        return new VoltCriteriaData(SELECT_CORE_SESSIONS_BY_CRITERIA, schemaMapping, sessionIdValues, subscriberIdValues
                    , sessionIpV4Values, sessionIpV6Values);


    }


    public static String checkAndGetSingleValueOfCoreSessionId(){
        String coreSessionId = null;
        if(isNotEmpty(coreSessionIdValues) && coreSessionIdValues.size() == 1
                && checkListsAreNullOrEmpty(sessionIdValues, subscriberIdValues, sessionIpV4Values,
                sessionIpV6Values, sySessionIdValues, gatewayAddressValues)
                ){
            coreSessionId = coreSessionIdValues.get(0);
        }
        return coreSessionId;
    }

    public static String checkAndGetSingleValueOfSessionId(){
        String sessionId = null;
        if(isNotEmpty(sessionIdValues) && sessionIdValues.size() == 1
                && checkListsAreNullOrEmpty(coreSessionIdValues, subscriberIdValues, sessionIpV4Values,
                sessionIpV6Values, sySessionIdValues, gatewayAddressValues)
                ){
            sessionId = sessionIdValues.get(0);
        }
        return sessionId;
    }

    private static String checkAndGetSingleValueOfSubscriberId(){
        String subscriberId = null;
        if(isNotEmpty(subscriberIdValues) && subscriberIdValues.size() == 1
                && checkListsAreNullOrEmpty(coreSessionIdValues, sessionIdValues, sessionIpV4Values,
                sessionIpV6Values, sySessionIdValues, gatewayAddressValues)
                ){
            subscriberId = subscriberIdValues.get(0);
        }
        return subscriberId;
    }

    public static String checkAndGetSingleValueOfSessionIpV4(){
        String sessionIpV4 = null;
        if(isNotEmpty(sessionIpV4Values) && sessionIpV4Values.size() == 1
                && checkListsAreNullOrEmpty(coreSessionIdValues, sessionIdValues, subscriberIdValues,
                sessionIpV6Values, sySessionIdValues, gatewayAddressValues)
                ){
            sessionIpV4 = sessionIpV4Values.get(0);
        }
        return sessionIpV4;
    }

    public static String checkAndGetSingleValueOfSessionIpV6(){
        String sessionIpV6 = null;
        if(isNotEmpty(sessionIpV6Values) && sessionIpV6Values.size() == 1
                && checkListsAreNullOrEmpty(coreSessionIdValues, sessionIdValues, subscriberIdValues,
                sessionIpV4Values, sySessionIdValues, gatewayAddressValues)
                ){
            sessionIpV6 = sessionIpV6Values.get(0);
        }
        return sessionIpV6;
    }

    public static String checkAndGetSingleValueOfSySessionId(){
        String sySessionId = null;
        if(isNotEmpty(sySessionIdValues) && sySessionIdValues.size() == 1
                && checkListsAreNullOrEmpty(coreSessionIdValues, sessionIdValues, subscriberIdValues,
                sessionIpV4Values, sessionIpV6Values, gatewayAddressValues)
                ){
            sySessionId = sySessionIdValues.get(0);
        }
        return sySessionId;
    }

    public static String checkAndGetSingleValueOfGatewayAddress(){
        String gatewayAddress = null;
        if(isNotEmpty(gatewayAddressValues) && gatewayAddressValues.size() == 1
                && checkListsAreNullOrEmpty(coreSessionIdValues, sessionIdValues, subscriberIdValues,
                sessionIpV4Values, sessionIpV6Values, sySessionIdValues)
                ){
            gatewayAddress = gatewayAddressValues.get(0);
        }
        return gatewayAddress;
    }


    private static boolean checkListsAreNullOrEmpty(List<String>... lists){
        for(List<String> list : lists){
            if(isNotEmpty(list)){
                return false;
            }
        }
        return true;
    }
}
