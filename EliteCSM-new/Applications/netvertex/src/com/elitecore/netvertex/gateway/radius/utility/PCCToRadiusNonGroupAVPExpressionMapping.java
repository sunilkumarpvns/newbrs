package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringCustomStyle;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PCCToRadiusNonGroupAVPExpressionMapping implements PCCToRadiusPacketMapping {

    private static final String MODULE = "PCRF-TO-NONGROUP-MAPPING";
    @Nonnull
    private final String attributeId;
    @Nonnull
    private final Expression pcrfKey;
    @Nonnull
    private final String expressionString;
    @Nullable
    private final Map<String, String> valueMapping;
    @Nullable
    private final String defaultValue;
    @Nonnull
    RadiusAttributeFactory radiusAttributeFactory;

    public PCCToRadiusNonGroupAVPExpressionMapping(@Nonnull String attributeId,
                                                   @Nonnull Expression pcrfKey,
                                                   @Nonnull String expressionString,
                                                   @Nullable Map<String, String> valueMapping,
                                                   @Nullable String defaultValue,
                                                   @Nonnull RadiusAttributeFactory radiusAttributeFactory) {
        this.attributeId = attributeId;
        this.pcrfKey = pcrfKey;
        this.expressionString = expressionString;
        this.valueMapping = valueMapping;
        this.defaultValue = defaultValue;
        this.radiusAttributeFactory = radiusAttributeFactory;
    }

    @Override
    public boolean apply(@Nonnull PCCtoRadiusMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {
        String attributeValue = null;
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

        try {
            String value = pcrfKey.getStringValue(valueProvider);

            if (valueMapping != null) {
                attributeValue = getValue(value);
            } else {
                attributeValue = value;
            }

        } catch (InvalidTypeCastException | IllegalArgumentException | MissingIdentifierException e) {
            LogManager.ignoreTrace(e);
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, e.getMessage());

            if (defaultValue == null) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Default value is not specified for  PolicyKey " + pcrfKey.getName() + " so skipping this attribute");
            } else {
                attributeValue = defaultValue;
            }
        }

        if (attributeValue != null) {
            accumalator.addAttribute(attributeId, attributeValue);
            isApplied = true;
            return isApplied;
        }

        return isApplied;
    }

    private String getValue(String value) {
        String mappedValue = valueMapping.get(value);

        if (mappedValue != null) {
            return mappedValue;
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
        builder.append(Strings.repeat("\t", currentIndentation.get()) + attributeId, expressionString);
        builder.append("Value Mapping", valueMapping.isEmpty() ? null : valueMapping);
        builder.append("Default Value", defaultValue);
        builder.newline();

        builder.popStyle();
    }

}
