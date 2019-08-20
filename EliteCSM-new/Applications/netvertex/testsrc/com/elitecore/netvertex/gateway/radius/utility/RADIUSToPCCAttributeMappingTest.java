package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.netvertex.gateway.radius.mapping.PCRFRequestRadiusMappingValuProvider;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCAttributeMapping;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.radius.packet.impl.RadServiceRequestImpl;
import com.elitecore.netvertex.gateway.radius.packet.impl.RadServiceResponseImpl;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class RADIUSToPCCAttributeMappingTest {
    private static final String PCRFKEY = "PCRFKEY";

    private RadServiceRequestImpl radiusRequest = mock(RadServiceRequestImpl.class);
    private RadServiceResponseImpl radiusResponse = mock(RadServiceResponseImpl.class);
    private IRadiusAttribute radiusAttribute = mock(IRadiusAttribute.class);
    private PCRFRequest pcrfRequest = new PCRFRequestImpl();
    private PCRFRequestRadiusMappingValuProvider valueProvider = new PCRFRequestRadiusMappingValuProvider(radiusRequest, radiusResponse, pcrfRequest, null);

    @Before
    public void before() {
        DummyDiameterDictionary.getInstance();
    }

    public class ValueMappingProvidedAndValueFound {

        private Map<String, String> mapping = new HashMap<String, String>();
        private RadiusToPCCAttributeMapping radiusToPCCAttributeMapping;

        @Before
        public void setUp() throws InvalidExpressionException {

            mapping.put("elitecore", "sterlite");
            radiusToPCCAttributeMapping = new RadiusToPCCAttributeMapping("0:1", PCRFKEY, null, mapping);
        }


        @Test
        public void test_apply_value_from_valueMapping_if_respectiveKeyFoundInValueMapping() throws InvalidExpressionException {
            when(radiusAttribute.getStringValue()).thenReturn("elitecore");
            when(radiusRequest.getRadiusAttribute("0:1")).thenReturn(radiusAttribute);
            radiusToPCCAttributeMapping.apply(valueProvider);
            Assert.assertEquals("sterlite", pcrfRequest.getAttribute(PCRFKEY));
        }

        @Test
        public void test_apply_value_from_valueProvider_if_respectiveKeyNotFoundInValueMapping() throws InvalidExpressionException {
            when(radiusAttribute.getStringValue()).thenReturn("elite");
            when(radiusRequest.getRadiusAttribute("0:1")).thenReturn(radiusAttribute);

            radiusToPCCAttributeMapping.apply(valueProvider);

            Assert.assertEquals("elite", pcrfRequest.getAttribute(PCRFKEY));
        }

    }

    public class ValueMappingNotProvidedAndValueFound {

        private RadiusToPCCAttributeMapping radiusToPCCAttributeMapping;

        @Before
        public void setUp() throws InvalidExpressionException {
            radiusToPCCAttributeMapping = new RadiusToPCCAttributeMapping("0:1", PCRFKEY, null, null);
        }

        @Test
        public void test_apply_value_from_valueProvider() throws InvalidExpressionException {
            when(radiusAttribute.getStringValue()).thenReturn("elitecore");
            when(radiusRequest.getRadiusAttribute("0:1")).thenReturn(radiusAttribute);

            radiusToPCCAttributeMapping.apply(valueProvider);

            Assert.assertEquals("elitecore", pcrfRequest.getAttribute(PCRFKEY));
        }
    }


    public class ValueNotFound {

        private String defaultVal = "DEFAULT";

        @Test
        public void test_apply_use_defaultValue_when_provided() throws InvalidExpressionException {
            when(radiusAttribute.getStringValue()).thenReturn("elitecore");
            when(radiusRequest.getRadiusAttribute("0:1")).thenReturn(radiusAttribute);

            RadiusToPCCAttributeMapping radiusToPCCAttributeMapping = new RadiusToPCCAttributeMapping("0:1000", PCRFKEY, defaultVal, null);
            radiusToPCCAttributeMapping.apply(valueProvider);

            Assert.assertEquals(defaultVal, pcrfRequest.getAttribute(PCRFKEY));

        }

        @Test
        public void test_apply_notaddAVP_when_defaultValueNotProvided() throws InvalidExpressionException {
            when(radiusAttribute.getStringValue()).thenReturn("elitecore");
            when(radiusRequest.getRadiusAttribute("0:1")).thenReturn(radiusAttribute);

            RadiusToPCCAttributeMapping radiusToPCCAttributeMapping = new RadiusToPCCAttributeMapping("0:1000", PCRFKEY, null, null);
            radiusToPCCAttributeMapping.apply(valueProvider);

            Assert.assertNull(pcrfRequest.getAttribute(PCRFKEY));
        }
    }
}




