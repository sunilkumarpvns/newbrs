package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFResponseMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.DiameterToPCCGxMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.UMBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gy.DiameterToPCCGyMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.rx.DiameterToPCCRxMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.sy.DiameterToPCCSyMapping;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

/**
 * Created by harsh on 6/28/17.
 */
public class DiameterToPCCMapping implements DiameterToPCCPacketMapping {
   
	@Nonnull private final List<DiameterToPCCPacketMapping> appSpecificMapping;
    @Nonnull private final Map<LogicalExpression, List<DiameterToPCCPacketMapping>> customMappings;

    public DiameterToPCCMapping(@Nonnull List<DiameterToPCCPacketMapping> appSpecificMapping,
                        @Nonnull Map<LogicalExpression, List<DiameterToPCCPacketMapping>> customMappings) {
    	
        this.appSpecificMapping = appSpecificMapping;
        this.customMappings = customMappings;
    }
    
    @Override
    public void apply(PCRFRequestMappingValueProvider valueProvider) {

        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
        pcrfRequest.setAttribute(PCRFKeyConstants.REVALIDATION_MODE.val, valueProvider.getConfiguration().getRevalidationMode().val);
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val, valueProvider.getConfiguration().getName());

        for(DiameterToPCCPacketMapping packetMapping : appSpecificMapping) {
            packetMapping.apply(valueProvider);
        }

        for (Entry<LogicalExpression, List<DiameterToPCCPacketMapping>> configuredMapping : customMappings.entrySet()) {
            
            if (configuredMapping.getKey().evaluate(valueProvider)) {
                
                for (DiameterToPCCPacketMapping diameterToPCRFPacketMapping : configuredMapping.getValue()) {
                    diameterToPCRFPacketMapping.apply(valueProvider);
                }
            }
        }

        if(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val) == null ) {

            DiameterGatewayConfiguration configuration = valueProvider.getConfiguration();

            DiameterRequest diameterRequest = valueProvider.getDiameterRequest();
            SessionTypeConstant sessionType;
            if (diameterRequest != null) {
                sessionType = configuration.getSessionType(diameterRequest.getApplicationID());
            } else {
                sessionType = configuration.getSessionType(valueProvider.getDiameterAnswer().getApplicationID());
            }


            if (sessionType.hasDBSession) {
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, sessionType.getVal());
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + ":" + sessionType.getVal());
            }
        }

        if(valueProvider.getConfiguration().getSyApplicationId() != valueProvider.getDiameterRequest().getApplicationID()) {
            pcrfRequest.setInterfacePacketCreateTime(valueProvider.getDiameterRequest().creationTimeMillis());
            pcrfRequest.setInterfaceQueueTimeInMillies(valueProvider.getDiameterRequest().getQueueTime());
        }

    }

    @Override
    public void apply(PCRFResponseMappingValueProvider valueProvider) {

        for(DiameterToPCCPacketMapping packetMapping : appSpecificMapping) {
            packetMapping.apply(valueProvider);
        }

        for (Entry<LogicalExpression, List<DiameterToPCCPacketMapping>> configuredMapping : customMappings.entrySet()) {

            if (configuredMapping.getKey().evaluate(valueProvider)) {

                for (DiameterToPCCPacketMapping diameterToPCRFPacketMapping : configuredMapping.getValue()) {
                    diameterToPCRFPacketMapping.apply(valueProvider);
                }
            }
        }

    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Diameter to PCC Mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {

        builder.appendHeading("Custom Mapping --");
        builder.incrementIndentation();
        for (Map.Entry<LogicalExpression, List<DiameterToPCCPacketMapping>> entry: customMappings.entrySet()) {
            builder.appendChildObject(entry.getKey().toString(), entry.getValue());
        }
        builder.decrementIndentation();
    }


    public static class DiameterToPCCMappingBuilder {
       
    	private Map<LogicalExpression, List<DiameterToPCCPacketMapping>> customMappings;
        private List<DiameterToPCCPacketMapping> appSpecificMapping;

        public DiameterToPCCMappingBuilder() {

        	appSpecificMapping = new ArrayList<>();
        }

        public DiameterToPCCMappingBuilder withDefaultMapping(@Nonnull List<DiameterToPCCPacketMapping> defaultMapping) {
            this.appSpecificMapping = defaultMapping;
            return this;
        }

        public DiameterToPCCMappingBuilder withConfiguredMapping(Map<LogicalExpression, List<DiameterToPCCPacketMapping>> configuredMappings) {
            this.customMappings = configuredMappings;
            return this;
        }
        
        public DiameterToPCCMappingBuilder withGxCCRMapping(UMBuilder umBuilder) {
            appSpecificMapping.add(new DiameterRequestAVPMapping(DiameterAVPConstants.TGPP_USER_LOCATION_INFO,
                    DiameterAVPConstants.TGPP_RAT_TYPE,
                    DiameterAVPConstants.TGPP_IP_CAN_TYPE,
                    DiameterAVPConstants.USER_EQUIPMENT_INFO));
            appSpecificMapping.add(new CCRMapping());
        	this.appSpecificMapping.add(new DiameterToPCCGxMapping(umBuilder));
        	return this;
        }
        
        public DiameterToPCCMappingBuilder withGyCCRMapping() {
            appSpecificMapping.add(new DiameterRequestAVPMapping(DiameterAVPConstants.TGPP_USER_LOCATION_INFO, DiameterAVPConstants.TGPP_RAT_TYPE, DiameterAVPConstants.TGPP_IP_CAN_TYPE, DiameterAVPConstants.USER_EQUIPMENT_INFO));
            String usageLocationInfo = DiameterAVPConstants.TGPP_SERVICE_INFORMATION
                    + CommonConstants.DOT + DiameterAVPConstants.TGPP_PS_INFORMATION + CommonConstants.DOT +
                    DiameterAVPConstants.TGPP_USER_LOCATION_INFO;


            String ratType = DiameterAVPConstants.TGPP_SERVICE_INFORMATION
                    + CommonConstants.DOT + DiameterAVPConstants.TGPP_PS_INFORMATION + CommonConstants.DOT +
                    DiameterAVPConstants.TGPP_RAT_TYPE;

            String ipCanType = DiameterAVPConstants.TGPP_SERVICE_INFORMATION
                    + CommonConstants.DOT + DiameterAVPConstants.TGPP_PS_INFORMATION + CommonConstants.DOT +
                    DiameterAVPConstants.TGPP_IP_CAN_TYPE;

            String userEquipmentInfoId = DiameterAVPConstants.TGPP_SERVICE_INFORMATION
                    + CommonConstants.DOT + DiameterAVPConstants.TGPP_PS_INFORMATION + CommonConstants.DOT +
                    DiameterAVPConstants.USER_EQUIPMENT_INFO;

            appSpecificMapping.add(new DiameterRequestAVPMapping(usageLocationInfo, ratType, ipCanType,userEquipmentInfoId));
            appSpecificMapping.add(new CCRMapping());
        	this.appSpecificMapping.add(new DiameterToPCCGyMapping());
        	return this;
        }
        
        public DiameterToPCCMappingBuilder withGxRAAMapping() {
            appSpecificMapping.add(new DiameterRequestAVPMapping(DiameterAVPConstants.TGPP_USER_LOCATION_INFO, DiameterAVPConstants.TGPP_RAT_TYPE, DiameterAVPConstants.TGPP_IP_CAN_TYPE, DiameterAVPConstants.USER_EQUIPMENT_INFO));
        	this.appSpecificMapping.add(new DiameterToPCCGxMapping());
        	return this;
        }
        
        public DiameterToPCCMappingBuilder withGyRAAMapping() {
            appSpecificMapping.add(new DiameterRequestAVPMapping(DiameterAVPConstants.TGPP_USER_LOCATION_INFO, DiameterAVPConstants.TGPP_RAT_TYPE, DiameterAVPConstants.TGPP_IP_CAN_TYPE, DiameterAVPConstants.USER_EQUIPMENT_INFO));
        	this.appSpecificMapping.add(new DiameterToPCCGyMapping());
        	return this;
        }
        
        public DiameterToPCCMappingBuilder withASAMapping() {

            return this;
        }
        
        public DiameterToPCCMappingBuilder withSLAMapping() {
            this.appSpecificMapping.add(new DiameterToPCCSyMapping());
        	return this;
        }
        
        public DiameterToPCCMappingBuilder withSNRMapping() {

            return this;
        }
        
        public DiameterToPCCMappingBuilder withAARMapping() {
            appSpecificMapping.add(new DiameterRequestAVPMapping(DiameterAVPConstants.TGPP_USER_LOCATION_INFO, DiameterAVPConstants.TGPP_RAT_TYPE, DiameterAVPConstants.TGPP_IP_CAN_TYPE, DiameterAVPConstants.USER_EQUIPMENT_INFO));
        	this.appSpecificMapping.add(new DiameterToPCCRxMapping());
        	return this;
        }
        
        public DiameterToPCCMappingBuilder withRxSTRMapping() {
            appSpecificMapping.add(new DiameterRequestAVPMapping(DiameterAVPConstants.TGPP_USER_LOCATION_INFO, DiameterAVPConstants.TGPP_RAT_TYPE, DiameterAVPConstants.TGPP_IP_CAN_TYPE, DiameterAVPConstants.USER_EQUIPMENT_INFO));
            this.appSpecificMapping.add(new DiameterToPCCRxMapping());
        	return this;
        }

        public DiameterToPCCMapping build() {
            Map<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings = new HashMap<LogicalExpression, List<DiameterToPCCPacketMapping>>();
            if (this.customMappings != null) {
                mappings.putAll(this.customMappings);
            }
            return new DiameterToPCCMapping(appSpecificMapping, mappings);
        }


        public DiameterToPCCMappingBuilder withSySTAMappings() {
            return this;
        }
    }
}
