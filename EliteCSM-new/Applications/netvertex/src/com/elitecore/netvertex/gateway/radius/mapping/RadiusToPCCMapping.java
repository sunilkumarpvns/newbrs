package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.GatewayComponent;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyEnforcementMethod;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrValueConstants;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.core.data.ServiceGuide;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFResponseMappingValueProvider;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.utility.RadiusToPCCPacketMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RadiusToPCCMapping implements RadiusToPCCPacketMapping {
    @Nonnull
    private final List<RadiusToPCCPacketMapping> appSpecificMapping;
    @Nonnull private final Map<LogicalExpression, List<RadiusToPCCPacketMapping>> customMappings;

    public RadiusToPCCMapping(@Nonnull List<RadiusToPCCPacketMapping> appSpecificMapping,
                              @Nonnull Map<LogicalExpression, List<RadiusToPCCPacketMapping>> customMappings) {

        this.appSpecificMapping = appSpecificMapping;
        this.customMappings = customMappings;
    }

    @Override
    public boolean apply(PCRFRequestRadiusMappingValuProvider valueProvider) {

        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
        pcrfRequest.setAttribute(PCRFKeyConstants.REVALIDATION_MODE.val, valueProvider.getConfiguration().getRevalidationMode().val);
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val, valueProvider.getConfiguration().getName());

        for(RadiusToPCCPacketMapping packetMapping : appSpecificMapping) {
            packetMapping.apply(valueProvider);
        }

        boolean satisfied = false;
        for (Map.Entry<LogicalExpression, List<RadiusToPCCPacketMapping>> configuredMapping : customMappings.entrySet()) {
            if (configuredMapping.getKey().evaluate(valueProvider)) {
                satisfied = true;
                for (RadiusToPCCPacketMapping radiusToPCRFPacketMapping : configuredMapping.getValue()) {
                    radiusToPCRFPacketMapping.apply(valueProvider);
                }
            }
        }

        RadServiceRequest radiusRequest = valueProvider.getRADIUSRequest();
        String sessionID = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal());
        if(sessionID != null){
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + ":" + SessionTypeConstant.RADIUS.getVal() + ":" +pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
        } else {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(),radiusRequest.getClientIp()+":"+radiusRequest.getClientPort()+":"+radiusRequest.getIdentifier()+ ":"+SessionTypeConstant.RADIUS.getVal()+":"+pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal()));
        }
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.RADIUS.getVal());
        
        setEvent(pcrfRequest, radiusRequest, valueProvider.getConfiguration().getPolicyEnforcementMethod());

        if(GatewayComponent.APPLICATION_FUNCTION == valueProvider.getConfiguration().getGatewayType()){
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.VOICE_SERVICE_ID.val);
        }else {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, PCRFKeyValueConstants.DATA_SERVICE_ID.val);
        }

        for(ServiceGuide serviceGuide: valueProvider.getConfiguration().getServiceGuides()){
            if(serviceGuide.getCondition()==null || serviceGuide.getCondition().evaluate(valueProvider)){
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_SERVICE.val, serviceGuide.getServiceId());
                break;
            }
        }

        return satisfied;

    }

    @Override
    public void apply(PCRFResponseMappingValueProvider valueProvider) {

        for(RadiusToPCCPacketMapping packetMapping : appSpecificMapping) {
            packetMapping.apply(valueProvider);
        }

        for (Map.Entry<LogicalExpression, List<RadiusToPCCPacketMapping>> configuredMapping : customMappings.entrySet()) {

            if (configuredMapping.getKey().evaluate(valueProvider)) {

                for (RadiusToPCCPacketMapping radiusToPCRFPacketMapping : configuredMapping.getValue()) {
                    radiusToPCRFPacketMapping.apply(valueProvider);
                }
            }
        }

    }

    private void setEvent(PCRFRequest pcrfRequest, RadServiceRequest request, PolicyEnforcementMethod policyEnforcementMethod) {
        if(PolicyEnforcementMethod.COA == policyEnforcementMethod){
            if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
                int acctStatusType = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE).getIntValue();

                switch(acctStatusType){
                    case RadiusAttributeValuesConstants.START :
                        pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
                        pcrfRequest.addPCRFEvent(PCRFEvent.AUTHORIZE);
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
                        break;

                    case RadiusAttributeValuesConstants.INTERIM_UPDATE :
                        pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
                        pcrfRequest.addPCRFEvent(PCRFEvent.AUTHORIZE);
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
                        break;

                    case RadiusAttributeValuesConstants.STOP :
                        setEventForAccountStatusTypeStop(pcrfRequest, request);
                }

            } else if(request.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
                pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
                pcrfRequest.addPCRFEvent(PCRFEvent.AUTHORIZE);
                if(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val) != null){
                    pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
                }
            }

        }else if(PolicyEnforcementMethod.ACCESS_ACCEPT == policyEnforcementMethod){
            if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
                int acctStatusType = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE).getIntValue();

                switch(acctStatusType){
                    case RadiusAttributeValuesConstants.START :
                        pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
                        break;

                    case RadiusAttributeValuesConstants.INTERIM_UPDATE :
                        pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
                        break;

                    case RadiusAttributeValuesConstants.STOP :
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);
                }

            }else if(request.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
                pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
                pcrfRequest.addPCRFEvent(PCRFEvent.AUTHORIZE);
                if(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val) != null){
                    pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
                }
            }

        }else if(PolicyEnforcementMethod.Cisco_SCE_API == policyEnforcementMethod){
            if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
                int acctStatusType = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE).getIntValue();

                switch(acctStatusType){

                    case RadiusAttributeValuesConstants.START :
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
                        break;

                    case RadiusAttributeValuesConstants.INTERIM_UPDATE :
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
                        break;

                    case RadiusAttributeValuesConstants.STOP :
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);
                }

            }

        }else if(PolicyEnforcementMethod.NONE == policyEnforcementMethod){
            if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
                int acctStatusType = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE).getIntValue();

                switch(acctStatusType){

                    case RadiusAttributeValuesConstants.START :
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
                        break;

                    case RadiusAttributeValuesConstants.INTERIM_UPDATE :
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_UPDATE);
                        break;

                    case RadiusAttributeValuesConstants.STOP :
                        pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);
                }

            }
        }

        if(request.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE) {
            IRadiusAttribute serviceTypeAvp = request.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE);
            if(Objects.isNull(serviceTypeAvp)
                    || serviceTypeAvp.getIntValue() != RadiusAttributeValuesConstants.AUTHENTICATE_ONLY) {
                pcrfRequest.addPCRFEvent(PCRFEvent.QUOTA_MANAGEMENT);
            }

        } else if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE) {
            pcrfRequest.addPCRFEvent(PCRFEvent.QUOTA_MANAGEMENT);
        }



    }

    private void setEventForAccountStatusTypeStop(PCRFRequest pcrfRequest, RadServiceRequest request) {
        IRadiusAttribute wimaxSessionContinue = request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.SESSION_CONTINUE.getIntValue());
        if(wimaxSessionContinue != null && wimaxSessionContinue.getIntValue() == WimaxAttrValueConstants.SESSION_CONTINUE_TRUE){
            pcrfRequest.addPCRFEvent(PCRFEvent.AUTHENTICATE);
            pcrfRequest.addPCRFEvent(PCRFEvent.AUTHORIZE);
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_RESET);
        }else{
            pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);
        }
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- RADIUS to PCC Mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {

        builder.appendHeading("Custom Mappings --");
        builder.incrementIndentation();
        for (Map.Entry<LogicalExpression, List<RadiusToPCCPacketMapping>> entry: customMappings.entrySet()) {
            builder.appendChildObject(entry.getKey().toString(), entry.getValue());
        }
        builder.decrementIndentation();
    }


    public static class RADIUSToPCCMappingBuilder {

        private Map<LogicalExpression, List<RadiusToPCCPacketMapping>> customMappings;
        private List<RadiusToPCCPacketMapping> appSpecificMapping;

        public RADIUSToPCCMappingBuilder() {
            appSpecificMapping = new ArrayList<>();
        }

        public RADIUSToPCCMappingBuilder withConfiguredMapping(
                @Nonnull Map<LogicalExpression, List<RadiusToPCCPacketMapping>> configuredMappings) {
            this.customMappings = configuredMappings;
            return this;
        }

        public RADIUSToPCCMappingBuilder withARMappings() {
            this.appSpecificMapping.add(new RadiusRequestAVPMapping());
            return this;
        }

        public RADIUSToPCCMappingBuilder withACRMappings() {
            this.appSpecificMapping.add(new RadiusRequestAVPMapping());
            return this;
        }

        public RadiusToPCCMapping build() {

            if(Maps.isNullOrEmpty(customMappings)) {
                return null;
            }

            return new RadiusToPCCMapping(appSpecificMapping, customMappings);
        }
    }
}
