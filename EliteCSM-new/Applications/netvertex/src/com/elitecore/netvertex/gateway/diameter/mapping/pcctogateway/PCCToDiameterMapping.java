package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.PCCToDiameterGxMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.UMBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gy.PCCToDiameterGyMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.sy.PCCToDiameterSyMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;


import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Jay Trivedi
 *
 *<br><br>
 *<b> Description </b>
 *<br>
 * Apply appSpecificMapping<br> 
 *       |<br>
 * Apply customMappings<br>
 *       |<br>
 * (If customMapping is not successfully applied OR not configured)<br>
 *       |<br>
 * Apply defaultMappings<br>      
 *           
 */
public class PCCToDiameterMapping implements PCRFToDiameterPacketMapping {

    private static final String MODULE = "PCC-TO-DIAMETER-MAPPING";
    @Nonnull private final List<PCRFToDiameterPacketMapping> appSpecificMapping;
    
    @Nonnull private final Map<LogicalExpression, List<PCRFToDiameterPacketMapping>> customMappings;
    @Nonnull private final List<PCRFToDiameterPacketMapping> defaultMappings;
    
    public PCCToDiameterMapping(@Nonnull List<PCRFToDiameterPacketMapping> appSpecificMapping
                        , @Nonnull Map<LogicalExpression, List<PCRFToDiameterPacketMapping>> customMappings
                        , List<PCRFToDiameterPacketMapping> defaultMappings) {
    	
        this.appSpecificMapping = appSpecificMapping;
        this.customMappings = customMappings;
		this.defaultMappings = defaultMappings;
    }
    
    @Override
	public void apply(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {

        PCRFResponse pcrfResponse = valueProvider.getPcrfResponse();
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "PCRF Response received for creating diameter packet: " + pcrfResponse) ;
        }
    	
    	for(PCRFToDiameterPacketMapping packetMapping : appSpecificMapping) {
            packetMapping.apply(valueProvider, accumalator);
        }


        String resultCode = pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal());

        if (resultCode != null && PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(resultCode) == false) {
            return;
        }

        PCRFKeyValueConstants revalidationMode = valueProvider.getDiameterGatewayConfiguration().getRevalidationMode();
        if(pcrfResponse.getRevalidationTime() != null && PCRFKeyValueConstants.REVALIDATION_MODE_CLIENT_INITIATED == revalidationMode) {
            valueProvider.getDiameterPacket().addAvp(DiameterAVPConstants.TGPP_REVALIDATION_TIME, pcrfResponse.getRevalidationTime());
        }

        boolean isApplied = applyConfiguredMapping(valueProvider, accumalator);
        
        if (isApplied == false) {
            applyDefaultMapping(valueProvider, accumalator);
        }
    }

    private void applyDefaultMapping(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {
        for (PCRFToDiameterPacketMapping defaultMapping : defaultMappings) {
            defaultMapping.apply(valueProvider, accumalator);
        }
    }

    private boolean applyConfiguredMapping(DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {
        boolean isApplied = false;
        for (Entry<LogicalExpression, List<PCRFToDiameterPacketMapping>> configuredMapping : customMappings.entrySet()) {

            if (configuredMapping.getKey().evaluate(valueProvider)) {

                for (PCRFToDiameterPacketMapping diameterToPCRFPacketMapping : configuredMapping.getValue()) {
                    diameterToPCRFPacketMapping.apply(valueProvider, accumalator);
                }

                isApplied = true;
            }
        }
        return isApplied;
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- PCC to Diameter Mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {

        builder.appendHeading("Custom Mappings --");
        builder.incrementIndentation();
        for (Map.Entry<LogicalExpression, List<PCRFToDiameterPacketMapping>> entry: customMappings.entrySet()) {
            builder.appendChildObject(entry.getKey().toString(), entry.getValue());
        }
        builder.decrementIndentation();
        builder.appendChildObject("Default Mapping", defaultMappings);
    }

    public static class PCCToDiameterMappingBuilder {
      
    	private Map<LogicalExpression, List<PCRFToDiameterPacketMapping>> customMappings;
        private List<PCRFToDiameterPacketMapping> appSpecificMapping;
        private List<PCRFToDiameterPacketMapping> defaultMappings;
        
        public PCCToDiameterMappingBuilder() {
            appSpecificMapping = new ArrayList<>(2);
            defaultMappings = new ArrayList<>(2);
        }

        public PCCToDiameterMappingBuilder withAppSpecificMapping(@Nonnull List<PCRFToDiameterPacketMapping> defaultMapping) {
            this.appSpecificMapping = defaultMapping;
            return this;
        }
        
        public PCCToDiameterMappingBuilder withDefaultMappings(List<PCRFToDiameterPacketMapping> defaultMappings) {
			this.defaultMappings = defaultMappings;
        	return this;
        }

        public PCCToDiameterMappingBuilder withGxCCAMapping(UMBuilder umBuilder) {
        	appSpecificMapping.add(new PCCToDiameterGxMapping(umBuilder));
        	appSpecificMapping.add(new CCAAppSpecificMapping());
            return this;
        }
        
        public PCCToDiameterMappingBuilder withGxRARMapping(UMBuilder umBuilder) {
        	appSpecificMapping.add(new PCCToDiameterGxMapping(umBuilder));
            appSpecificMapping.add(new PCCToDiameterRequestMapping());
            appSpecificMapping.add(new RARAppSpecifMapping());
        	return this;
        }
        
        public PCCToDiameterMappingBuilder withGyCCAMapping() {
        	appSpecificMapping.add(new PCCToDiameterGyMapping());
            appSpecificMapping.add(new CCAAppSpecificMapping());
        	
        	return this;
        }
        
        
        public PCCToDiameterMappingBuilder withGyRARMapping() {
            appSpecificMapping.add(new PCCToDiameterRequestMapping());
        	appSpecificMapping.add(new RARAppSpecifMapping());
        	appSpecificMapping.add(new PCCToDiameterGyMapping());
            return this;
        }
        
        public PCCToDiameterMappingBuilder withASRMapping() {
        	appSpecificMapping.add(new ASRAppSpecificMapping());
            return this;
        }
        
        public PCCToDiameterMappingBuilder withSLRMapping() {
        	appSpecificMapping.add(new PCCToDiameterSyMapping());
            return this;
        }
        
        public PCCToDiameterMappingBuilder withSNAMapping() {
            return this;
        }
        
        public PCCToDiameterMappingBuilder withAAAMapping() {
            return this;
        }
        
        public PCCToDiameterMappingBuilder withRxSTAMapping() {
            return this;
        }

        public PCCToDiameterMappingBuilder withSySTAMapping() {
            return this;
        }

        public PCCToDiameterMappingBuilder withSySTRMapping() {
            appSpecificMapping.add(new PCCToDiameterSyMapping());
            return this;
        }
        
        public PCCToDiameterMappingBuilder withConfiguredMapping(Map<LogicalExpression, List<PCRFToDiameterPacketMapping>> configuredMappings) {
            this.customMappings = configuredMappings;
            return this;
        }

        public PCCToDiameterMapping build() {
        	Map<LogicalExpression, List<PCRFToDiameterPacketMapping>> mappings = new HashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>>();
            if (this.customMappings != null) {
                mappings.putAll(this.customMappings);
            }
            appSpecificMapping.add(new PCCToDiameterDefaultAVPMapping());
            
            return new PCCToDiameterMapping(appSpecificMapping, mappings, defaultMappings);
        }

    }

}
