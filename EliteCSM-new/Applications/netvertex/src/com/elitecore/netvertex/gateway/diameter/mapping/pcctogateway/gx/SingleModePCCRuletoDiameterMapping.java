package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx;

import java.util.List;
import javax.annotation.Nonnull;
import com.elitecore.corenetvertex.constants.PacketMappingConstants;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalators;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactory;
import org.apache.commons.collections.CollectionUtils;

/**
 * Created by harsh on 6/29/17.
 */
public class SingleModePCCRuletoDiameterMapping extends PCCRuleToDiameterPacketMapping {

    @Nonnull private String id;
    @Nonnull private AttributeFactory attributeFactory;

    public SingleModePCCRuletoDiameterMapping(@Nonnull String id,
                                              @Nonnull List<PCRFToDiameterPacketMapping> mappings,
                                              @Nonnull AttributeFactory attributeFactory) {
        super(mappings);
        this.id = id;
        this.attributeFactory = attributeFactory;
    }

    @Override
    public void apply(@Nonnull DiameterPacketMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {
        List<PCCRule> installablePCCRules = valueProvider.getPcrfResponse().getInstallablePCCRules();

        if(CollectionUtils.isEmpty(installablePCCRules)) {
        	return;
		}

        for (int pccRuleIndex = 0; pccRuleIndex < installablePCCRules.size(); pccRuleIndex++) {

			if (PacketMappingConstants.NONE.val.equalsIgnoreCase(id)) {
				processMapping(valueProvider, installablePCCRules.get(pccRuleIndex), accumalator);
			} else {
				IDiameterAVP avp = attributeFactory.create(id);

				if (avp == null) {
					break;
				}

				if (avp.isGrouped() == false) {
					return;
				}

				AvpAccumalator avpAccumalator = AvpAccumalators.of((AvpGrouped)avp);
				processMapping(valueProvider, installablePCCRules.get(pccRuleIndex), avpAccumalator);
				accumalator.add(avp);
			}
        }
    }

	private void processMapping(@Nonnull DiameterPacketMappingValueProvider valueProvider, PCCRule installablePCCRule, AvpAccumalator avpAccumalator) {
		DiameterPacketMappingValueProvider pccRuleDiameterPacketMappingValueProvider = new DiameterPacketMappingValueProvider(installablePCCRule,
				valueProvider.getPcrfResponse(),
				valueProvider.getDiameterPacket(),
				valueProvider.getDiameterGatewayConfiguration(),
				valueProvider.getControllerContext());

		applyMapping(pccRuleDiameterPacketMappingValueProvider, avpAccumalator);
	}

	@Override
    public void toString(IndentingToStringBuilder builder) {
        builder.appendChildObject(id, getMappings());
    }

}
