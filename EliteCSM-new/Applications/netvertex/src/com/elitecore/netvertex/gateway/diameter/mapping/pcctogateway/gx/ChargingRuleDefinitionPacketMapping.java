package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;

/**
 * Created by harsh on 6/29/17.
 */
public class ChargingRuleDefinitionPacketMapping implements PCRFToDiameterPacketMapping{

    @Nonnull private final LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticRuleMapping;
    @Nonnull private final LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicRuleMapping;


    public ChargingRuleDefinitionPacketMapping(
    		LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> staticRuleMapping,
    		LinkedHashMap<LogicalExpression, List<PCRFToDiameterPacketMapping>> dynamicRuleMapping) {
        super();
        this.staticRuleMapping = staticRuleMapping;
        this.dynamicRuleMapping = dynamicRuleMapping;
    }



    @Override
    public void apply(@Nonnull DiameterPacketMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {
   
        if (valueProvider.getPccRule().isPredifine()) {
			if (Maps.isNullOrEmpty(staticRuleMapping)) {
				return;
			}

			for (Entry<LogicalExpression, List<PCRFToDiameterPacketMapping>> entry : staticRuleMapping.entrySet()) {
				if(evaluateAndAdd(valueProvider, accumalator, entry)) {
					break;
				}
			}
		} else {
			if (Maps.isNullOrEmpty(dynamicRuleMapping)) {
				return;
			}

			for (Entry<LogicalExpression, List<PCRFToDiameterPacketMapping>> entry : dynamicRuleMapping.entrySet()) {
				if(evaluateAndAdd(valueProvider, accumalator, entry)) {
					break;
				}
			}
		}
    }

    private boolean evaluateAndAdd(
            DiameterPacketMappingValueProvider valueProvider,
            AvpAccumalator accumalator,
            Entry<LogicalExpression, List<PCRFToDiameterPacketMapping>> entry) {

		if (entry.getKey().evaluate(valueProvider) == false) {
			return false;
		}

		for (int index = 0; index < entry.getValue().size(); index++) {
			PCRFToDiameterPacketMapping mapping = entry.getValue().get(index);
			mapping.apply(valueProvider, accumalator);
		}

		return true;
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

        for (Entry<LogicalExpression, List<PCRFToDiameterPacketMapping>> entry : staticRuleMapping.entrySet()) {
            builder.appendChildObject(entry.getKey().toString(), entry.getValue());
        }

        builder.decrementIndentation();

        builder.appendHeading("Dynamic Rule Mappings --");
        builder.incrementIndentation();
        for (Entry<LogicalExpression, List<PCRFToDiameterPacketMapping>> entry : dynamicRuleMapping.entrySet()) {
            builder.appendChildObject(entry.getKey().toString(), entry.getValue());
        }
        builder.decrementIndentation();
    }
}
