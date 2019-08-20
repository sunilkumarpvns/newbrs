package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyEnforcementMethod;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.radius.conf.RadiusGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;
import com.elitecore.netvertex.gateway.radius.utility.RadiusToPCCPacketMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.usagemetering.ServiceUnit;
import com.elitecore.netvertex.usagemetering.UMLevel;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

import java.util.Arrays;

public class RadiusRequestAVPMapping implements RadiusToPCCPacketMapping {

    private static final String MODULE = "RAD-REQ-AVP-MAPPING";
    private static final String MONITORING_KEY = "Default Service";

    @Override
    public boolean apply(PCRFRequestRadiusMappingValuProvider valueProvider) {

        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
        RadServiceRequest request = valueProvider.getRADIUSRequest();
        RadServiceResponse response = valueProvider.getRADIUSResponse();
        RadiusGatewayConfiguration gatewayConfig = valueProvider.getConfiguration();
        IRadiusAttribute attribute;

        if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
            if((attribute = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_MULTI_SESSION_ID)) != null
                    || (attribute = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_ID)) != null) {
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal(), attribute.getStringValue());
            }
        }

        pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), GatewayTypeConstant.RADIUS.getVal());
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), SessionTypeConstant.RADIUS.getVal());

        IRadiusAttribute callingStationIdAttribute;
        if((callingStationIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID)) != null){
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.getVal(), callingStationIdAttribute.getStringValue());
        }

        IRadiusAttribute nasPortIdAttribute;
        if((nasPortIdAttribute = request.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_ID)) != null){
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.getVal(), nasPortIdAttribute.getStringValue());
        }

        pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), response.getNASAddress());
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal(), request.getClientIp());
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val, gatewayConfig.getName());

        if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal())+ ":"+SessionTypeConstant.RADIUS.getVal()+":"+pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
        }else if(request.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
            String sessionID = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal());
            if(sessionID != null){
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal())+ ":"+SessionTypeConstant.RADIUS.getVal()+":"+pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
            } else {
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(),request.getClientIp()+":"+request.getClientPort()+":"+request.getIdentifier()+ ":"+SessionTypeConstant.RADIUS.getVal()+":"+pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
            }
        }

        pcrfRequest.setAttribute(PCRFKeyConstants.RESERVATION_REQUIRED.val, PCRFKeyValueConstants.RESERVATION_REQUIRED_FALSE.val);
        if(request.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE) {
            IRadiusAttribute serviceTypeAvp = request.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE);
            if(serviceTypeAvp != null && serviceTypeAvp.getIntValue() == RadiusAttributeValuesConstants.AUTHORIZE_ONLY) {
                pcrfRequest.setAttribute(PCRFKeyConstants.RESERVATION_REQUIRED.val, PCRFKeyValueConstants.RESERVATION_REQUIRED_TRUE.val);
            }
        }

        PolicyEnforcementMethod policyEnforcementMethod = gatewayConfig.getPolicyEnforcementMethod();

        setReportedUsageInfo(pcrfRequest, request, policyEnforcementMethod);

        setPAPCHAPInfo(pcrfRequest, request, gatewayConfig);

        return true;
    }

    private static void setReportedUsageInfo(PCRFRequest pcrfRequest, RadServiceRequest request, PolicyEnforcementMethod policyEnforcementMethod) {
        if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){

            if(RadiusAttributeValuesConstants.START == request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE).getIntValue()) {
                if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Skipping processing of usage monitoring. Reason: Usage information not found");
                return;
            }

            if(PolicyEnforcementMethod.NONE == policyEnforcementMethod) {
                if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
                    LogManager.getLogger().warn(MODULE, "Skipping Reported Usage Info in PCRF Request. Reason: " +
                            "Policy Enforcement Method: " + policyEnforcementMethod.getVal());
                return;
            }

            long inputOctetsValue = 0;
            long outputOctetsValue = 0;
            long sessionTimeValue = 0;

            IRadiusAttribute inputGigawords = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_GIGAWORDS);
            IRadiusAttribute outputGigawords = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_GIGAWORDS);

            if(inputGigawords != null){
                inputOctetsValue = (4294967296L) * inputGigawords.getLongValue();
            }

            if(outputGigawords != null){
                outputOctetsValue = (4294967296L) * outputGigawords.getLongValue();
            }

            IRadiusAttribute inputOctets = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_INPUT_OCTETS);
            IRadiusAttribute outputOctets = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_OUTPUT_OCTETS);
            IRadiusAttribute sessionTime = request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID,WimaxAttrConstants.ACTIVE_TIME.getIntValue());

            if(sessionTime == null){
                sessionTime = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_SESSION_TIME);
            }

            UsageMonitoringInfo monitoringInfo = new UsageMonitoringInfo();
            monitoringInfo.setMonitoringKey(MONITORING_KEY);
            monitoringInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);

            ServiceUnit usedServiceUnit = new ServiceUnit();
            if(inputOctets != null){
                inputOctetsValue += inputOctets.getLongValue();
            }
            usedServiceUnit.setInputOctets(inputOctetsValue);

            if(outputOctets != null){
                outputOctetsValue += outputOctets.getLongValue();
            }
            usedServiceUnit.setOutputOctets(outputOctetsValue);

            if(sessionTime != null){
                sessionTimeValue = sessionTime.getLongValue();
                usedServiceUnit.setTime(sessionTimeValue);
            }

            if(inputOctetsValue == 0 && outputOctetsValue == 0 && sessionTimeValue == 0) {
                if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Skipping further processing for Reported Usage. Reason: Reported usage values are zero");
                return;
            }

            usedServiceUnit.setTotalOctets(usedServiceUnit.getInputOctets() + usedServiceUnit.getOutputOctets());
            monitoringInfo.setUsedServiceUnit(usedServiceUnit);
            pcrfRequest.addReportedUsageInfo(monitoringInfo);

            GyServiceUnits usedServiceUnits = new GyServiceUnits();
            usedServiceUnits.setVolume(usedServiceUnit.getTotalOctets());
            usedServiceUnits.setTime(usedServiceUnit.getTime());
            MSCC reportedMSCC = new MSCC();
            reportedMSCC.setUsedServiceUnits(usedServiceUnits);
            pcrfRequest.setReportedMSCCs(Arrays.asList(reportedMSCC));

        }
    }



    private void setPAPCHAPInfo(PCRFRequest pcrfRequest, RadServiceRequest request, RadiusGatewayConfiguration gatewayConfig) {
        IRadiusAttribute userPassword = request.getRadiusAttribute(RadiusAttributeConstants.USER_PASSWORD);

        if(userPassword!=null){
            byte[] userPasswordBytes = userPassword.getValueBytes();
            String decodedPassword = RadiusUtility.decryptPasswordRFC2865(userPasswordBytes,request.getAuthenticator(), gatewayConfig.getSharedSecret());
            pcrfRequest.setPapPassword(decodedPassword);
        }

        IRadiusAttribute chapPassword = request.getRadiusAttribute(RadiusAttributeConstants.CHAP_PASSWORD);

        if(chapPassword!=null){
            byte[] chapPswdAttributeBytes = request.getRadiusAttribute(RadiusAttributeConstants.CHAP_PASSWORD).getValueBytes();
            pcrfRequest.setChapPasswordBytes(chapPswdAttributeBytes);
        }

        IRadiusAttribute chapChallenge = request.getRadiusAttribute(RadiusAttributeConstants.CHAP_CHALLENGE);

        if(chapChallenge!=null){
            byte[] chapChallengeBytes = request.getRadiusAttribute(RadiusAttributeConstants.CHAP_CHALLENGE).getValueBytes();
            pcrfRequest.setChapChallengeBytes(chapChallengeBytes);

        } else if(request.getAuthenticator()!=null){
            pcrfRequest.setChapChallengeBytes(request.getAuthenticator());
        }
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Radius Request AVP Mapping -- ");
        return builder.toString();
    }
}
