package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringCustomStyle;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class PCCAttributeToNonGroupAVPMapping implements PCRFToDiameterPacketMapping {

    private static final String MODULE = "PCC-TO-NONGROUP-MAPPING";
    @Nonnull
    private final String attributeId;

    @Nonnull private final String pcrfKey;
    @Nonnull private final AttributeFactory attributeFactory;
    @Nullable
    private final Map<String, String> valueMapping;
    @Nullable private final String defaultValue;

    public PCCAttributeToNonGroupAVPMapping(@Nonnull String attributeId, @Nonnull String value, @javax.annotation.Nullable Map<String, String> valueMapping, @Nullable String defaultValue, @Nonnull AttributeFactory attributeFactory) {
        this.attributeId = attributeId;
        this.pcrfKey = value;
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

        String value = getValue(valueProvider);
        if(Objects.nonNull(value)){
            diameterAVP.setStringValue(value);
            accumalator.add(diameterAVP);
        }else {
            if(defaultValue == null){
                if(getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "Skipping this attribute. Reason: Value and Default value is not specified for attribute " + attributeId + " so skipping this attribute");
                }
            }else{
                if(getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "Value is not specified so, using " + defaultValue + " as Default value is specified for attribute " + attributeId);
                }
                diameterAVP.setStringValue(defaultValue);
                accumalator.add(diameterAVP);
            }
        }
    }

    private String getValue(DiameterPacketMappingValueProvider valueProvider) {
        String attributeValue = valueProvider.getStringValue(pcrfKey);
        if (valueMapping == null) {
            return attributeValue;
        }
        String value = valueMapping.get(attributeValue);

        if (value == null) {
            return attributeValue;
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
        builder.append(Strings.repeat("\t", currentIndentation.get()) + attributeId, pcrfKey);
        builder.append("Value Mapping", valueMapping.isEmpty() ? null : valueMapping);
        builder.append("Default Value", defaultValue);
        builder.newline();

        builder.popStyle();
    }
}
