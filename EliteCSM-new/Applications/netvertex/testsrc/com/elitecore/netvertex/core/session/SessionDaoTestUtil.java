package com.elitecore.netvertex.core.session;

import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.impl.FieldMappingImpl;
import com.elitecore.core.serverx.sessionx.impl.SchemaMappingImpl;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class SessionDaoTestUtil {

    public static final String CORE_SESS_TABLE_NAME = "TBLT_SESSION";
    public static final String SESSION_RULE_TABLE_NAME = "TBLT_SUB_SESSION";

    public static SchemaMappingImpl createCoreSessionSchemaMapping(){
        SchemaMappingImpl coreSessionSchemaMapping = new SchemaMappingImpl("CS_ID", "START_TIME","LAST_UPDATE_TIME");

        List<FieldMapping> coreSessionFieldMappings = new ArrayList<>();
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal(), "SUBSCRIBER_IDENTITY"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_CORESESSION_ID.getVal(), "CORE_SESSION_ID"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_ID.getVal(), "SESSION_ID"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_MANAGER_ID.getVal(), "SESSION_MANAGER_ID"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_IPV4.getVal(), "SESSION_IP_V4"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_IPV6.getVal(), "SESSION_IP_V6"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_ACCESS_NETWORK.getVal(), "ACCESS_NETWORK"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_TYPE.getVal(), "SESSION_TYPE"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.QOS_PROFILE_NAME.getVal(), "QOS_PROFILE"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal(), "SOURCE_GATEWAY"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SY_SESSION_ID.getVal(), "SY_SESSION_ID"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SY_GATEWAY_NAME.getVal(), "SY_GATEWAY_NAME"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_GATEWAY_NAME.val, "GATEWAY_NAME"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.LOCATION_NEW_CONGESTION_STATUS.val, "CONGESTION_STATUS"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val, "IMSI"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val, "MSISDN"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_NAI.val, "NAI"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_NAI_REALM.val, "NAI_REALM"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_NAI_RELATED_USERNAME.val, "NAI_USER_NAME"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.val, "SIP_URL"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val, "PCC_RULES"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_REQ_IP_CAN_QOS.val, "REQUESTED_QOS"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SESSION_USAGE.val, "SESSION_USAGE"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.REQUEST_NUMBER.val, "REQUEST_NUMBER"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_USAGE_RESERVATION.val, "USAGE_RESERVATION"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), "GATEWAY_ADDRESS"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_GATEWAY_REALM.getVal(), "GATEWAY_REALM"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_PACKAGE_USAGE.getVal(), "PACKAGE_USAGE"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val, "CHARGING_RULE_BASE_NAMES"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_CALLING_STATION_ID.val, "CALLING_STATION_ID"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_CALLED_STATION_ID.val, "CALLED_STATION_ID"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_QUOTA_RESERVATION.val, "QUOTA_RESERVATION"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.val, "PCC_PROFILE_SELECTION_STATE"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val, "UNACCOUNTED_QUOTA"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SGSN_MCC_MNC.val, "SGSN_MCC_MNC"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_SERVICE.val, "SERVICE"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_LOCATION.val, "LOCATION"));

        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.LOCATION_AREA.val, "PARAM1"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.SUB_EMAIL.val, "PARAM2"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.SUB_PHONE.val, "PARAM3"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.GATEWAY_TYPE.val, "PARAM4"));
        coreSessionFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_USERNAME.val, "PARAM5"));

        coreSessionSchemaMapping.setFieldMappings(coreSessionFieldMappings);
        coreSessionSchemaMapping.setIdGenerator(SchemaMapping.UUID_GENERATOR);
        coreSessionSchemaMapping.setSchemaName(CORE_SESS_TABLE_NAME);
        coreSessionSchemaMapping.setTableName(CORE_SESS_TABLE_NAME);

        return coreSessionSchemaMapping;
    }

    public static SchemaMappingImpl createSessionRuleSchemaMapping(){
        SchemaMappingImpl coreSessionSchemaMapping = new SchemaMappingImpl("SR_ID", "START_TIME","LAST_UPDATE_TIME");

        List<FieldMapping> sessionRuleFieldMappings = new ArrayList<>();
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_CORESESSION_ID.getVal(), "SESSION_ID"));
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_AF_SESSION_ID.getVal(), "AF_SESSION_ID"));
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.PCC_RULE_LIST.getVal(), "PCC_RULE"));
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_MEDIA_TYPE.getVal(), "MEDIA_TYPE"));
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_MEDIA_COMPONENT_NUMBER.getVal(), "MEDIA_COMPONENT_NUMBER"));
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_FLOW_NUMBSER.getVal(), "FLOW_NUMBER"));
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), "GATEWAY_ADDRESS"));
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_UPLINK_FLOW.getVal(), "UPLINK_FLOW"));
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.CS_DOWNLINK_FLOW.getVal(), "DOWNLINK_FLOW"));
        sessionRuleFieldMappings.add(new FieldMappingImpl(FieldMapping.STRING_TYPE, PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.getVal(), "ADDITIONAL_PARAMETER"));

        coreSessionSchemaMapping.setFieldMappings(sessionRuleFieldMappings);
        coreSessionSchemaMapping.setIdGenerator(SchemaMapping.UUID_GENERATOR);
        coreSessionSchemaMapping.setSchemaName(SESSION_RULE_TABLE_NAME);
        coreSessionSchemaMapping.setTableName(SESSION_RULE_TABLE_NAME);

        return coreSessionSchemaMapping;
    }

    public static SessionData createDummySessionDataForCoreSession(){
        SessionData sessionData = new SessionDataImpl(CORE_SESS_TABLE_NAME);
        sessionData.addValue(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal(), "1234567890");
        sessionData.addValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), "1234567890_gx:Gx");
        sessionData.addValue(PCRFKeyConstants.CS_SESSION_ID.getVal(), "1234567890_gx");
        sessionData.addValue(PCRFKeyConstants.CS_SESSION_MANAGER_ID.getVal(), "SMI_1234567890");
        sessionData.addValue(PCRFKeyConstants.CS_SESSION_IPV4.getVal(), "127.0.0.1");
        sessionData.addValue(PCRFKeyConstants.CS_SESSION_IPV6.getVal(), null);
        sessionData.addValue(PCRFKeyConstants.CS_ACCESS_NETWORK.getVal(), "GERAN");
        sessionData.addValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), "Gx");
        sessionData.addValue(PCRFKeyConstants.QOS_PROFILE_NAME.getVal(), "RTC_19_BASE#RTC_19_QOS#0");
        sessionData.addValue(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal(), "gx.elite.com");
        sessionData.addValue(PCRFKeyConstants.CS_SY_SESSION_ID.getVal(), null);
        sessionData.addValue(PCRFKeyConstants.CS_SY_GATEWAY_NAME.getVal(), null);
        sessionData.addValue(PCRFKeyConstants.CS_GATEWAY_NAME.val, "Diameter_GW");
        sessionData.addValue(PCRFKeyConstants.LOCATION_NEW_CONGESTION_STATUS.val, null);
        sessionData.addValue(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.val, "1234567890");
        sessionData.addValue(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.val, "1234567890");
        sessionData.addValue(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_NAI.val, null);
        sessionData.addValue(PCRFKeyConstants.CS_NAI_REALM.val, null);
        sessionData.addValue(PCRFKeyConstants.CS_NAI_RELATED_USERNAME.val, null);
        sessionData.addValue(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.val, null);
        sessionData.addValue(PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val, "a2e1284f-6bbb-46ba-a2e7-5a7369cfd6f6:2dc12789-b0bb-4282-bff2-fc56b3cd5610");
        sessionData.addValue(PCRFKeyConstants.CS_REQ_IP_CAN_QOS.val, null);
        sessionData.addValue(PCRFKeyConstants.CS_SESSION_USAGE.val, null);
        sessionData.addValue(PCRFKeyConstants.REQUEST_NUMBER.val, "1");
        sessionData.addValue(PCRFKeyConstants.CS_USAGE_RESERVATION.val, "RTC_19_PCC_1:a2e1284f-6bbb-46ba-a2e7-5a7369cfd6f6;");
        sessionData.addValue(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), "gx.elite.com");
        sessionData.addValue(PCRFKeyConstants.CS_GATEWAY_REALM.getVal(), "elite.com");
        sessionData.addValue(PCRFKeyConstants.CS_PACKAGE_USAGE.getVal(), null);
        sessionData.addValue(PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val, null);
        sessionData.addValue(PCRFKeyConstants.CS_CALLING_STATION_ID.val, "sip:stl.ite@127.0.0.1");
        sessionData.addValue(PCRFKeyConstants.CS_CALLED_STATION_ID.val, "sip:stl.ite@127.0.0.2");
        sessionData.addValue(PCRFKeyConstants.CS_QUOTA_RESERVATION.val, "{\"rgWiseMSCC\":{\"0\":{\"ratingGroup\":0" +
                ",\"grantedServiceUnits\":{\"volume\":0,\"time\":10,\"money\":0,\"serviceSpecificUnits\":0" +
                ",\"quotaProfileIdOrRateCardId\":\"peak_non_monetary_rate_card_3007\",\"isReservationRequired\":true" +
                ",\"packageId\":\"rest_rnc_package_3007\",\"productOfferId\":\"rest_product_offer_3007\"" +
                ",\"balanceId\":\"49bca421-2a34-4c32-b2a9-7aefad14be5b\",\"rateCardGroupId\":\"rest_rate_card_group_3007\"" +
                ",\"rateCardId\":\"peak_non_monetary_rate_card_3007\",\"rateCardName\":\"RTC_3007_NON_MONETARY_RATE_CARD_peak\"" +
                ",\"rateCardGroupName\":\"NM_Rate_Card_Group-3007\",\"reservedMonetaryBalance\":0.0,\"rate\":0.0" +
                ",\"rateMinorUnit\":0,\"volumePulse\":0,\"timePulse\":1,\"calculateVolumePulse\":0,\"calculatedTimePulse\":0" +
                ",\"pulseMinorUnit\":0,\"actualRate\":0.0,\"discount\":0.0,\"fupLevel\":0,\"requestedAction\":0" +
                ",\"discountAmount\":0.0},\"resultCode\":\"SUCCESS\",\"reportingReason\":\"THRESHOLD\",\"validityTime\":86400" +
                ",\"timeQuotaThreshold\":0,\"volumeQuotaThreshold\":0}}}");
        sessionData.addValue(PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.val, "{'states':[{'pid':'a2e1284f-6bbb-46ba-a2e7-5a7369cfd6f6'" +
                ",'qid':'773a07f9-dce0-4aed-beb4-849cc696f5c9','level':'0','rg':'0','service':'1'}]}");
        sessionData.addValue(PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val, null);
        sessionData.addValue(PCRFKeyConstants.CS_SGSN_MCC_MNC.val, null);
        sessionData.addValue(PCRFKeyConstants.CS_SERVICE.val, "VOICE");
        sessionData.addValue(PCRFKeyConstants.CS_LOCATION.val, "{}");

        sessionData.addValue(PCRFKeyConstants.LOCATION_AREA.val, null);
        sessionData.addValue(PCRFKeyConstants.SUB_EMAIL.val, "1234567890@sterlite.com");
        sessionData.addValue(PCRFKeyConstants.SUB_PHONE.val, "1234567890");
        sessionData.addValue(PCRFKeyConstants.GATEWAY_TYPE.val, "Diameter");
        sessionData.addValue(PCRFKeyConstants.CS_USERNAME.val, "1234567890");

        return sessionData;
    }

    public static SessionData createDummySessionDataForSessionRule() {
        SessionData sessionData = new SessionDataImpl(SESSION_RULE_TABLE_NAME);
        sessionData.addValue(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), "1234567890_gx");
        sessionData.addValue(PCRFKeyConstants.CS_AF_SESSION_ID.getVal(), "1234567890_rx");
        sessionData.addValue(PCRFKeyConstants.PCC_RULE_LIST.getVal(), "9ddbdf84-5a51-4061-9e52-efae9e96e18f");
        sessionData.addValue(PCRFKeyConstants.CS_MEDIA_TYPE.getVal(), "0");
        sessionData.addValue(PCRFKeyConstants.CS_MEDIA_COMPONENT_NUMBER.getVal(), "1");
        sessionData.addValue(PCRFKeyConstants.CS_FLOW_NUMBSER.getVal(), "1");
        sessionData.addValue(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), "af.elite.com");
        sessionData.addValue(PCRFKeyConstants.CS_UPLINK_FLOW.getVal(), "permit in ip from 10.106.1.142 to 10.151.1.18 4565");
        sessionData.addValue(PCRFKeyConstants.CS_DOWNLINK_FLOW.getVal(), "permit out IP from 10.151.1.18 to 10.3.252.94 4564");
        sessionData.addValue(PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.getVal(), "{\"PCCRule.QCI\":\"2\",\"PCCRule.FlowStatus\":\"2\"}");

        return sessionData;
    }

    public static String[] getDataArray(SessionData sessionData, SchemaMapping schemaMapping) {
        List<FieldMapping> fieldMappings = schemaMapping.getFieldMappings();
        String[] newSessionDataArr = new String[fieldMappings.size()];
        for (int i=0; i < fieldMappings.size(); i++) {
            newSessionDataArr[i] = sessionData.getValue(fieldMappings.get(i).getPropertyName());
        }
        return newSessionDataArr;
    }

    /**
     * to create (cond1 OR (cond2 OR (cond3 OR (.... chain
     */
    public static Criterion createORCriterian(List<Criterion> criterions){
        Criterion criterion = criterions.get(0);
        for(int i=1; i<criterions.size(); i++){
            criterion = Restrictions.or(criterion, criterions.get(i));
        }
        return criterion;
    }

    public static boolean checkListsAreNullOrEmpty(List<String>... lists){
        for(List<String> list : lists){
            if(isNotEmpty(list)){
                return false;
            }
        }
        return true;
    }

    public static boolean checkListsAreNotEmpty(List<String>... lists){
        for(List<String> list : lists){
            if(isNotEmpty(list) == false){
                return false;
            }
        }
        return true;
    }
}
