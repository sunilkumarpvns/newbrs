package com.elitecore.netvertex.gateway.radius.mapping;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;
import com.elitecore.netvertex.gateway.radius.utility.AvpAccumalator;
import com.elitecore.netvertex.gateway.radius.utility.PCCToRadiusPacketMapping;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RadiusChargingRuleDefinitionPacketMapping implements PCCToRadiusPacketMapping {

    @Nonnull private final LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> staticRuleMapping;
    @Nonnull private final LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> dynamicRuleMapping;

    public RadiusChargingRuleDefinitionPacketMapping(LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> staticRuleMapping,
                                                     LinkedHashMap<LogicalExpression, List<PCCToRadiusPacketMapping>> dynamicRuleMapping) {
        super();
        this.staticRuleMapping = staticRuleMapping;
        this.dynamicRuleMapping = dynamicRuleMapping;
    }

    @Override
    public boolean apply(@Nonnull PCCtoRadiusMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {


        if (valueProvider.getPccRule().isPredifine()) {
            if (Maps.isNullOrEmpty(staticRuleMapping) == false) {
                for (java.util.Map.Entry<LogicalExpression, List<PCCToRadiusPacketMapping>> entry : staticRuleMapping.entrySet()) {
                    evaluateAndAdd(valueProvider, accumalator, entry);
                }
            }
        } else {
            if (Maps.isNullOrEmpty(dynamicRuleMapping) == false) {
                for (java.util.Map.Entry<LogicalExpression, List<PCCToRadiusPacketMapping>> entry : dynamicRuleMapping.entrySet()) {
                    evaluateAndAdd(valueProvider, accumalator, entry);
                }
            }
        }
        return true;
    }

    private void evaluateAndAdd(
            PCCtoRadiusMappingValueProvider valueProvider,
            AvpAccumalator accumalator,
            Map.Entry<LogicalExpression, List<PCCToRadiusPacketMapping>> entry) {

        if (entry.getKey().evaluate(valueProvider)) {


            for (int index = 0; index < entry.getValue().size(); index++) {
                PCCToRadiusPacketMapping mapping = entry.getValue().get(index);
                mapping.apply(valueProvider, accumalator);
            }

        }
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Charging Rule Definition Packet Mapping -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.appendHeading("Static Rule Mappings --");
        builder.incrementIndentation();

        for (Map.Entry<LogicalExpression, List<PCCToRadiusPacketMapping>> entry : staticRuleMapping.entrySet()) {
            builder.appendChildObject(entry.getKey().toString(), entry.getValue());
        }

        builder.decrementIndentation();

        builder.appendHeading("Dynamic Rule Mappings --");
        builder.incrementIndentation();
        for (Map.Entry<LogicalExpression, List<PCCToRadiusPacketMapping>> entry : dynamicRuleMapping.entrySet()) {
            builder.appendChildObject(entry.getKey().toString(), entry.getValue());
        }
        builder.decrementIndentation();
    }
}
