package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;

import javax.annotation.Nonnull;
import java.util.List;

public class GroupModePCCRuleToRadiusMapping extends PCCRuleToRadiusPacketMapping {
    private String id;
    private RadiusAttributeFactory radiusAttributeFactory;

    public GroupModePCCRuleToRadiusMapping(@Nonnull String id,
                                           @Nonnull List<PCCToRadiusPacketMapping> mappings,
                                           @Nonnull RadiusAttributeFactory radiusAttributeFactory) {
        super(mappings);
        this.id = id;
        this.radiusAttributeFactory = radiusAttributeFactory;

    }
    @Override
    public boolean apply(@Nonnull PCCtoRadiusMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {
        IRadiusAttribute attribute = radiusAttributeFactory.create(id);

        if (attribute == null) {
            return false;
        }

        if(attribute instanceof GroupedAttribute == false) {
            return false;
        }

        List<PCCRule> installablePCCRules = valueProvider.getPcrfResponse().getInstallablePCCRules();

        if(Collectionz.isNullOrEmpty(installablePCCRules)) {
            return false;
        }

        AvpAccumalator avpAccumalator = AvpAccumalators.of((GroupedAttribute) attribute);

        for (int pccRuleIndex = 0; pccRuleIndex < installablePCCRules.size(); pccRuleIndex++) {
            PCCtoRadiusMappingValueProvider pccToRadiusMappingValueProvider = new PCCtoRadiusMappingValueProvider(
                    installablePCCRules.get(pccRuleIndex),
                    valueProvider.getPcrfResponse(),
                    valueProvider.getRadiusPacket(),
                    valueProvider.getRadiusGWConfiguration(),
                    valueProvider.getControllerContext());
            applyMapping(pccToRadiusMappingValueProvider, avpAccumalator);
        }

        accumalator.add(attribute);
        return true;
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.appendChildObject(id, getMappings());
    }
}
