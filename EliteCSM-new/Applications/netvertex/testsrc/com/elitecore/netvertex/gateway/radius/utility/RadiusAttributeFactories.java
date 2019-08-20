package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusDictionaryTestHarness;

public class RadiusAttributeFactories {
    public static RadiusAttributeFactory fromDummyDictionary() {
        return new RadiusAttributeFactory() {

            @Override
            public IRadiusAttribute create(String id) {
                return RadiusDictionaryTestHarness.getInstance().getKnownAttribute(id);
            }

            @Override
            public IRadiusAttribute create(String id, String value) {
                IRadiusAttribute attribute = create(id);

                if (attribute != null) {
                    attribute.setStringValue(value);
                }

                return attribute;
            }
        };
    }
}
