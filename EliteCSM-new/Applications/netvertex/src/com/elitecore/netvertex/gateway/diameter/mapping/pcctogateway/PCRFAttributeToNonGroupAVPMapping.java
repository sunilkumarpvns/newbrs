package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringCustomStyle;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactory;

/**
 * Created by harsh on 6/28/17.
 */
public class PCRFAttributeToNonGroupAVPMapping implements PCRFToDiameterPacketMapping {

	private static final String MODULE = "PCRF-TO-NONGROUP-MAPPING";
	@Nonnull private final String attributeId;
    @Nonnull private final Expression pcrfKey;
    @Nonnull private final String expressionString;
    @Nonnull private final AttributeFactory attributeFactory;
    @Nullable private final Map<String, String> valueMapping;
	@Nullable private final String defaultValue;

    public PCRFAttributeToNonGroupAVPMapping(@Nonnull String attributeId, @Nonnull Expression pcrfKey, @Nonnull String expressionString, @javax.annotation.Nullable Map<String, String> valueMapping, @Nullable String defaultValue, @Nonnull AttributeFactory attributeFactory) {
        this.attributeId = attributeId;
        this.pcrfKey = pcrfKey;
        this.expressionString = expressionString;
		this.valueMapping = valueMapping;
		this.defaultValue = defaultValue;
        this.attributeFactory = attributeFactory;
    }

    @Override
    public void apply(@Nonnull DiameterPacketMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {
    
    	IDiameterAVP diameterAVP = attributeFactory.create(attributeId);
    	
    	if (diameterAVP == null) {
    		getLogger().warn(MODULE, "Diameter AVP not found from dictionary for attribute: " + attributeId);
    		return;
    	}
    	
    	if (diameterAVP.isGrouped()) {
    		getLogger().warn(MODULE, "Diameter AVP for attribute: " + attributeId + " is not added. Reason: This AVP should not be a group AVP");
    		return;
    	}


		String attributeValue = null;
		try {
			String value = pcrfKey.getStringValue(valueProvider);
			
			if(valueMapping != null) {
				attributeValue = getValue(value);
			} else {
				attributeValue = value;
			}
			
		} catch (InvalidTypeCastException | IllegalArgumentException | MissingIdentifierException e) {
			LogManager.ignoreTrace(e);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, e.getMessage());
			}

			if(Strings.isNullOrEmpty(defaultValue) == true) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Default value is not specified for PolicyKey " + pcrfKey.getName() + " so skipping this attribute");
			} else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Value is not specified so, using "+ defaultValue +" as Default value is not specified for PolicyKey " + pcrfKey.getName() + " so skipping this attribute");
				}
				attributeValue =  defaultValue;
			}
				
		}
		
		if (attributeValue != null) {
			diameterAVP.setStringValue(attributeValue);
			accumalator.add(diameterAVP);
		}
    	
    }

	private String getValue(String value) {
		String mappedValue = valueMapping.get(value);
		
		if(mappedValue != null) {
			return mappedValue;
		} else {
			return value;
		}
		
	}
    
	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- Non Group Attribute Mapping -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		ToStringCustomStyle noClassNameStyle = new ToStringCustomStyle();
		noClassNameStyle.setFieldSeparator(", ");
		noClassNameStyle.skipNull();
		builder.pushStyle(noClassNameStyle);
		AtomicInteger currentIndentation = builder.getCurrentIndentation();
		builder.append(Strings.repeat("\t", currentIndentation.get()) + attributeId, expressionString);
		builder.append("Value Mapping", valueMapping.isEmpty() ? null : valueMapping);
		builder.append("Default Value", defaultValue);
		builder.newline();

		builder.popStyle();
	}
}
