package com.elitecore.nvsmx.policydesigner.model.session;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl;
import com.elitecore.corenetvertex.session.SessionInformation;
import com.elitecore.corenetvertex.session.SessionRuleData;
import com.elitecore.corenetvertex.util.ActivePCCRuleParser;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.policydesigner.model.pkg.ims.IMSPkgDAO;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to manipulate session data from session information which is rmi response or fetch data from DB
 * @author dhyani.raval
 */
public class SessionDataUtility {

    private static final Function<SessionRuleData, String> SubSessionIdentifier = new Function<SessionRuleData, String>() {
        @Override
        public String apply(SessionRuleData sessionRuleData) {
            return sessionRuleData.getAfSessionId() + ":Rx";
        }
    };

    public static SessionData from(@Nonnull SessionInformation sessionInformation){
        Map<String, String> sessionInfoMap = new HashMap<String, String>();
        SessionData sessionData = new SessionData();
        sessionData.setCreationTime(NVSMXUtil.simpleDateFormatPool.get().format(sessionInformation.getCreationTime()));
        sessionData.setLastUpdateTime(NVSMXUtil.simpleDateFormatPool.get().format(sessionInformation.getLastUpdateTime()));
        sessionData.setSchemaName(sessionInformation.getSchemaName());
        sessionData.setSessionId(sessionInformation.getSessionId());

        for (String key : sessionInformation.getKeySet()) {

            String value = sessionInformation.getValue(key);
            sessionInfoMap.put(key, value);

            sessionData.setSessionType(sessionInformation.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()));

            if (PCRFKeyConstants.CS_ACTIVE_PCC_RULES.getVal().equals(key)) {
                List<PCCRuleImpl> pccRules = Collectionz.newArrayList();
                Map<String, String> deserializeActivePccRules = ActivePCCRuleParser.deserialize(value);
                for (String pccRuleId : deserializeActivePccRules.keySet()) {
                    PCCRuleImpl pccRuleImpl = (PCCRuleImpl) DefaultNVSMXContext.getContext().getPolicyRepository().getPCCRuleById(pccRuleId);
                    if (pccRuleImpl != null) {
                        pccRules.add(pccRuleImpl);
                    }
                }
                sessionData.setActivePccrules(pccRules);
            } else if (PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.getVal().equals(key)) {
                sessionData.setActiveChargingRuleBaseNames(fetchChargingRuleBaseNames(value));
            }
        }

        if(SessionTypeConstant.GX.getVal().equals(sessionData.getSessionType()) || SessionTypeConstant.RX.getVal().equals(sessionData.getSessionType())) {
            if(Collectionz.isNullOrEmpty(sessionInformation.getSessionRuleDatas()) == false){
                sessionData.setSubSessionsIdentifier(Strings.join("|", sessionInformation.getSessionRuleDatas(), SubSessionIdentifier));
                sessionData.setAfActivePccrules(fetchDedicatedPCCRules(sessionInformation.getSessionRuleDatas()));
            }
        }

        sessionData.setSessionInfo(sessionInfoMap);
        return sessionData;
    }


    private static List<PCCRuleImpl> fetchDedicatedPCCRules(List<SessionRuleData> sessionRuleDatas) {

        List<PCCRuleImpl> imsPccRules = Collectionz.newArrayList();
        if(Collectionz.isNullOrEmpty(sessionRuleDatas) == true ){
            return imsPccRules;
        }

        for(SessionRuleData sessionRuleData: sessionRuleDatas) {

            String mediaTypeName = "";

            if(Strings.isNullOrBlank(sessionRuleData.getMediaType()) == false) {
                MediaTypeData mediaTypeData = IMSPkgDAO.getMediaTypeBy(sessionRuleData.getMediaType());
                if(mediaTypeData != null) {
                    mediaTypeName = mediaTypeData.getName();
                }
            }

            List<String> serviceDataFlows = Collectionz.newArrayList();
            serviceDataFlows.add(sessionRuleData.getUpLinkFlow());
            serviceDataFlows.add(sessionRuleData.getDownLinkFlow());

            PCCRuleImpl.PCCRuleBuilder pccBuilder = new PCCRuleImpl.PCCRuleBuilder(sessionRuleData.getPccRule(), sessionRuleData.getPccRule())
                    .withServiceName(mediaTypeName)
                    .withServiceDataFlows(serviceDataFlows);
            PCCRuleImpl pccRule = pccBuilder.build();


            Gson gson = GsonFactory.defaultInstance();

            String jsonString = sessionRuleData.getAdditionalParameter();

            if(Strings.isNullOrBlank(jsonString) == false) {

                JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

                JsonElement jsonElement = jsonObject.get(PCRFKeyConstants.PCC_RULE_MBRDL.val);
                if (jsonElement != null && jsonElement.isJsonNull() == false) {
                    pccRule.setMBRDL(jsonElement.getAsLong());
                }

                jsonElement = jsonObject.get(PCRFKeyConstants.PCC_RULE_MBRUL.val);
                if (jsonElement != null && jsonElement.isJsonNull() == false) {
                    pccRule.setMBRUL(jsonElement.getAsLong());
                }

                jsonElement = jsonObject.get(PCRFKeyConstants.PCC_RULE_GBRDL.val);
                if (jsonElement != null && jsonElement.isJsonNull() == false) {
                    pccRule.setGBRDL(jsonElement.getAsLong());
                }

                jsonElement = jsonObject.get(PCRFKeyConstants.PCC_RULE_GBRUL.val);
                if (jsonElement != null && jsonElement.isJsonNull() == false) {
                    pccRule.setGBRUL(jsonElement.getAsLong());
                }

                jsonElement = jsonObject.get(PCRFKeyConstants.PCC_RULE_FLOW_STATUS.val);
                if (jsonElement != null && jsonElement.isJsonNull() == false) {
                    pccRule.setFlowStatus(FlowStatus.fromValue(jsonElement.getAsLong()));
                }
                jsonElement = jsonObject.get(PCRFKeyConstants.PCC_RULE_QCI.val);
                if (jsonElement != null && jsonElement.isJsonNull() == false) {
                    pccRule.setQCI(QCI.fromId(jsonElement.getAsInt()));

                }
            }
            imsPccRules.add(pccRule);

        }
        return imsPccRules;
    }

    private static List<com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename.ChargingRuleBaseName> fetchChargingRuleBaseNames(String value) {

        List<com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename.ChargingRuleBaseName> chargingRuleBaseNames  = Collectionz.newArrayList();
        Map<String, String> deserializeActiveChargingRules = ActivePCCRuleParser.deserialize(value);

        for (String chargingRuleBaseNameId : deserializeActiveChargingRules.keySet()) {
            com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName chargingRuleBaseNameImpl = DefaultNVSMXContext.getContext().getPolicyRepository().getChargingRuleBaseNameById(chargingRuleBaseNameId);

            com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename.ChargingRuleBaseName crbn = new com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename.ChargingRuleBaseName();
            if (chargingRuleBaseNameImpl != null) {
                crbn.setName(chargingRuleBaseNameImpl.getName());
                StringBuilder monitoringKeyAppender = new StringBuilder();
                StringBuilder serviceTypeAppender = new StringBuilder();
                for (String monitoringKey : chargingRuleBaseNameImpl.getMonitoringKeyServiceTypeMap().keySet()) {
                    monitoringKeyAppender.append(monitoringKey).append(",");
                    serviceTypeAppender.append(chargingRuleBaseNameImpl.getMonitoringKeyServiceTypeMap().get(monitoringKey).getName()).append(",");
                }
                crbn.setServiceType(serviceTypeAppender.toString());
                crbn.setMonitoringKey(monitoringKeyAppender.toString());
                chargingRuleBaseNames.add(crbn);
            }
        }
        return chargingRuleBaseNames;
    }

}
