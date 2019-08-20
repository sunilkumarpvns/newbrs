package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx;

import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.utility.AttributeFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ServiceDataFlowMapping implements PCRFToDiameterPacketMapping{

    private static final String MODULE = "SDF-MAPPING";
    @Nonnull private final String key;
    @Nonnull private final AttributeFactory attributeFactory;

    public ServiceDataFlowMapping(@Nonnull String key, @Nonnull AttributeFactory attributeFactory) {
        this.key = key;
        this.attributeFactory = attributeFactory;
    }

    @Override
    public void apply(@Nonnull DiameterPacketMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {

        PCCRule pccRule = valueProvider.getPccRule();

        if(pccRule == null) {
            return;
        }

        List<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>(2);
        for (String serviceDataFlow : pccRule.getServiceDataFlows()) {

            IDiameterAVP flowDescriptionAVP = attributeFactory.create(key);

            if (flowDescriptionAVP == null) {
                getLogger().warn(MODULE, "SDF AVP not found from dictionary for key: " + key);
                return;
            }

            if (flowDescriptionAVP.isGrouped()) {
                getLogger().warn(MODULE, "SDF AVP for key: " + key + " is not added. Reason: This AVP should not be a group AVP");
                return;
            }

            flowDescriptionAVP.setStringValue(serviceDataFlow);
            ArrayList<IDiameterAVP> childDiameterAVPs = new ArrayList<IDiameterAVP>(1);
            childDiameterAVPs.add(flowDescriptionAVP);

            IDiameterAVP flowInformationAVP = attributeFactory.create(DiameterAVPConstants.TGPP_FLOW_INFORMATION);

            if (flowInformationAVP != null) {
                flowInformationAVP.setGroupedAvp(childDiameterAVPs);
                diameterAVPs.add(flowInformationAVP);
            }
        }

        accumalator.add(diameterAVPs);
    }

}
