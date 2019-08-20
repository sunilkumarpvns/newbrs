package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringCustomStyle;
import com.elitecore.netvertex.gateway.radius.utility.RadiusToPCCPacketMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RadiusToPCCAttributeMapping implements RadiusToPCCPacketMapping {
    private static final String MODULE = "DIAMETER-TO-PCC-ATTRIBUTE-MAPPING";
    @Nonnull
    public final String attributeId;
    @Nonnull
    public final String pcrfKey;
    @Nullable
    public final String defaultValue;
    @Nullable
    public final Map<String, String> valueMapping;

    public RadiusToPCCAttributeMapping(@Nonnull String attributeId,
                                       @Nonnull String pcrfKey,
                                       @Nullable String defaultValue,
                                       @Nullable Map<String, String> valueMapping) {
        this.attributeId = attributeId;
        this.pcrfKey = pcrfKey;
        this.defaultValue = defaultValue;
        this.valueMapping = valueMapping;
    }

    @Override
    public boolean apply(@Nonnull PCRFRequestRadiusMappingValuProvider valueProvider) {

        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();

        String value = getValue(valueProvider);
        if (Objects.nonNull(value)) {
            pcrfRequest.setAttribute(pcrfKey, value);
        } else {
            if (Objects.nonNull(defaultValue)) {
                if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "Using " + defaultValue + " as default for key:" + pcrfKey + ". Reason:Value is not found attribute " + attributeId);
                }
                pcrfRequest.setAttribute(pcrfKey, defaultValue);
            } else {
                if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "Skipping mapping for key: "+ pcrfKey+". Reason: value not found for attribute " + attributeId + " and default value is not specified");
                }
            }
        }
        return true;
    }

    private String getValue(PCRFRequestRadiusMappingValuProvider valueProvider) {
        String attributeValue = valueProvider.getStringValue(attributeId);

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
        builder.appendHeading(" --RADIUS to PCC Attribute Mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        ToStringCustomStyle noClassNameStyle = new ToStringCustomStyle();
        noClassNameStyle.setFieldSeparator(", ");
        builder.pushStyle(noClassNameStyle);
        AtomicInteger currentIndentation = builder.getCurrentIndentation();
        builder.append(Strings.repeat("\t", currentIndentation.get()) + attributeId, pcrfKey);
        builder.append("Value Mapping", valueMapping.isEmpty() ? null : valueMapping);
        builder.append("Default Value", defaultValue);
        builder.newline();
        builder.popStyle();
    }
}
