package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringCustomStyle;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by harsh on 6/28/17.
 */
public class DiameterToPCCExpressionAttributeMapping implements DiameterToPCCPacketMapping {

    private static final String MODULE = "DIAMETER-TO-PCC-EXPRESSION-ATTRIBUTE-MAPPING";
    @Nonnull public final Expression attributeId;
    @Nonnull public final String expressionString;
    @Nonnull public final String pcrfKey;
    @Nullable public final String defaultValue;
    @Nullable public final Map<String, String> valueMapping;

    public DiameterToPCCExpressionAttributeMapping(@Nonnull Expression attributeId,
                                                   @Nonnull String expressionString,
                                                   @Nonnull String pcrfKey,
                                                   @Nullable String defaultValue,
                                                   @Nullable Map<String, String> valueMapping) {
        this.attributeId = attributeId;
        this.expressionString = expressionString;
        this.pcrfKey = pcrfKey;
        this.defaultValue = defaultValue;
        this.valueMapping = valueMapping;
    }


    @Override
    public void apply(@Nonnull PCRFRequestMappingValueProvider valueProvider) {

        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();

        try{
            String value = getValue(valueProvider);
            pcrfRequest.setAttribute(pcrfKey, value);
        }catch (InvalidTypeCastException | IllegalArgumentException | MissingIdentifierException e) {
            if(getLogger().isLogLevel(LogLevel.DEBUG)) {
                getLogger().debug(MODULE, e.getMessage());
            }

            LogManager.ignoreTrace(e);

            if(defaultValue == null){
                if(getLogger().isLogLevel(LogLevel.DEBUG))
                    getLogger().debug(MODULE, "Skipping mapping for key: "+ pcrfKey+". Reason: value not found for expression " + expressionString + " and default value is not specified");
            }else{
                if (getLogger().isLogLevel(LogLevel.DEBUG)) {
                    getLogger().debug(MODULE, "Using " + defaultValue + " as default for key:" + pcrfKey + ". Reason:Value is not found for expression " + expressionString);
                }
                pcrfRequest.setAttribute(pcrfKey, defaultValue);
            }
        }
    }

    private String getValue(ValueProvider valueProvider) throws InvalidTypeCastException,
            com.elitecore.exprlib.parser.exception.IllegalArgumentException,
            MissingIdentifierException {
        String attributeValue = attributeId.getStringValue(valueProvider);
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
        builder.appendHeading(" --Diameter to PCC Attribute Mapping -- ");
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
