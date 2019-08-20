package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringCustomStyle;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PCCToRadiusNonGroupAVPMapping implements PCCToRadiusPacketMapping {
    private static final String MODULE = "PCRF-TO-NONGROUP-MAPPING";
    @Nonnull
    private final String attributeId;
    @Nonnull
    private final String pcrfKey;
    @Nullable
    private final Map<String, String> valueMapping;
    @Nullable
    private final String defaultValue;
    @Nonnull
    RadiusAttributeFactory radiusAttributeFactory;

    public PCCToRadiusNonGroupAVPMapping(@Nonnull String attributeId,
                                         @Nonnull String pcrfKey,
                                         @Nullable Map<String, String> valueMapping,
                                         @Nullable String defaultValue,
                                         @Nonnull RadiusAttributeFactory radiusAttributeFactory) {
        this.attributeId = attributeId;
        this.pcrfKey = pcrfKey;
        this.valueMapping = valueMapping;
        this.defaultValue = defaultValue;
        this.radiusAttributeFactory = radiusAttributeFactory;
    }

    @Override
    public boolean apply(@Nonnull PCCtoRadiusMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {

        IRadiusAttribute attribute = radiusAttributeFactory.create(attributeId);
        boolean isApplied = false;
        if (attribute == null) {
            getLogger().warn(MODULE, "RADIUS attribute not found from dictionary for attribute: " + attributeId);
            return isApplied;
        }

        if (attribute instanceof GroupedAttribute) {
            getLogger().warn(MODULE, "RADIUS attribute for attribute: " + attributeId + " is not added. Reason: This attribute should not be a group attribute");
            return isApplied;
        }

        String value = getValue(valueProvider);
        if (Objects.nonNull(value)) {
            attribute.setStringValue(value);
            accumalator.add(attribute);
            isApplied = true;
        } else {
            if (Strings.isNullOrEmpty(defaultValue) == true) {
                if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "Skipping this attribute. Reason: Value and Default value is not specified for attribute " + attributeId);
                }
            } else {
                if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "Value is not specified so, using " + defaultValue + " as Default value is not specified for attribute " + attributeId + " so skipping this attribute");
                }
                attribute.setStringValue(defaultValue);
                accumalator.add(attribute);
                isApplied = true;
            }
        }
        return isApplied;
    }

    private String getValue(PCCtoRadiusMappingValueProvider valueProvider) {
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
        builder.appendHeading(" -- Non Group AVP RADIUS Mapping -- ");
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
