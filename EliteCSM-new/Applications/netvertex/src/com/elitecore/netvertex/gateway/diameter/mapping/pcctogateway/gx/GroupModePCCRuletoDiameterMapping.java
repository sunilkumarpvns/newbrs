package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx;

import java.util.List;
import javax.annotation.Nonnull;
import com.elitecore.commons.base.Collectionz;
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


import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by harsh on 6/29/17.
 */
public class GroupModePCCRuletoDiameterMapping extends PCCRuleToDiameterPacketMapping {
	private static final String MODULE = "GROUP-MODE-PCC-DIA-MAPPING";

    private String id;
    private AttributeFactory attributeFactory;

    public GroupModePCCRuletoDiameterMapping(@Nonnull String id,
                                             @Nonnull List<PCRFToDiameterPacketMapping> mappings,
                                             @Nonnull AttributeFactory attributeFactory) {
        super(mappings);
        this.id = id;
        this.attributeFactory = attributeFactory;
    }


    @Override
    public void apply(@Nonnull DiameterPacketMappingValueProvider valueProvider, AvpAccumalator accumalator) {
		List<PCCRule> installablePCCRules = valueProvider.getPcrfResponse().getInstallablePCCRules();

		if (Collectionz.isNullOrEmpty(installablePCCRules)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping mapping processing for attribute Id: " + id + ". Reason: No installable pcc rules found from pcrf response");
			}
			return;
		}

		if (PacketMappingConstants.NONE.val.equalsIgnoreCase(id)) {
			processMapping(valueProvider, installablePCCRules, accumalator);
		} else {
			IDiameterAVP avp = attributeFactory.create(id);

			if (avp == null) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping mapping processing for attribute Id: " + id + ". Reason: AVP not found");
				}
				return;
			}

			if (avp.isGrouped() == false) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping mapping processing for attribute Id: " + id + ". Reason: Avp is not grouped");
				}
				return;
			}

			AvpAccumalator avpAccumalator = AvpAccumalators.of((AvpGrouped) avp);
			processMapping(valueProvider, installablePCCRules, avpAccumalator);
			accumalator.add(avp);
		}
	}

	private void processMapping(DiameterPacketMappingValueProvider valueProvider, List<PCCRule> installablePCCRules, AvpAccumalator avpAccumalator) {
		for (int pccRuleIndex = 0; pccRuleIndex < installablePCCRules.size(); pccRuleIndex++) {
			DiameterPacketMappingValueProvider pccRuleDiameterPacketMappingValueProvider = new DiameterPacketMappingValueProvider(
                    installablePCCRules.get(pccRuleIndex),
                    valueProvider.getPcrfResponse(),
                    valueProvider.getDiameterPacket(),
                    valueProvider.getDiameterGatewayConfiguration(),
                    valueProvider.getControllerContext());
			applyMapping(pccRuleDiameterPacketMappingValueProvider, avpAccumalator);
		}
	}

	@Override
    public void toString(IndentingToStringBuilder builder) {
        builder.appendChildObject(id, getMappings());
    }
}
