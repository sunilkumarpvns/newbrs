package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.util.Maps;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpUTF8String;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCCAttributeToNonGroupAVPMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class PCCAttributeToNonGroupAVPMappingTest {
    private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();
    private PCCAttributeToNonGroupAVPMapping nonGroupAVPMapping;
    private PCRFResponse pcrfResponse =  new PCRFResponseImpl();
    private DiameterPacketMappingValueProvider valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, null, null, null);

    public class ValueMappingProvidedAndValueFound {

        private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
        private AttributeFactory attributeFactory = AttributeFactories.newFactory().addStringAvp(0,1).create();

        @SuppressWarnings("unchecked")
        @Before
        public void setUp() throws InvalidExpressionException {

            nonGroupAVPMapping = new PCCAttributeToNonGroupAVPMapping("0:1", "REQ_TYPE"
                    , Maps.newLinkedHashMap(Maps.Entry.newEntry("1", "INITIAL"))
                    , "INITIAL", attributeFactory);

        }


        @Test
        public void test_apply_value_from_valueMapping_if_respectiveKeyFoundInValueMapping() throws InvalidExpressionException {

            pcrfResponse.setAttribute("REQ_TYPE", "1");
            nonGroupAVPMapping.apply(valueProvider, accumalator);

            List<IDiameterAVP> expectedAvps = new ArrayList<IDiameterAVP>();
            AvpUTF8String strAVP = new AvpUTF8String(1, 0, (byte)0, "", "");
            strAVP.setStringValue("INITIAL");
            expectedAvps.add(strAVP);
            ReflectionAssert.assertReflectionEquals(expectedAvps, accumalator.getAll());
        }

        @Test
        public void test_apply_value_from_valueProvider_if_respectiveKeyNotFoundInValueMapping() throws InvalidExpressionException {

            pcrfResponse.setAttribute("REQ_TYPE", "2");
            nonGroupAVPMapping.apply(valueProvider, accumalator);

            List<IDiameterAVP> expectedAvps = new ArrayList<IDiameterAVP>();
            AvpUTF8String strAVP = new AvpUTF8String(1, 0, (byte)0, "", "");
            strAVP.setStringValue("2");
            expectedAvps.add(strAVP);
            ReflectionAssert.assertReflectionEquals(expectedAvps, accumalator.getAll());
        }
    }

    public class ValueMappingNotProvidedAndValueFound {

        private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();

        @Before
        public void setUp() throws InvalidExpressionException {
            AttributeFactory attributeFactory = AttributeFactories.newFactory().addStringAvp(0,1).create();

            nonGroupAVPMapping = new PCCAttributeToNonGroupAVPMapping("0:1", "REQ_TYPE", null, "INITIAL", attributeFactory);

            pcrfResponse.setAttribute("REQ_TYPE", "2");
        }

        @Test
        public void test_apply_value_from_valueProvider() throws InvalidExpressionException {

            nonGroupAVPMapping.apply(valueProvider, accumalator);

            List<IDiameterAVP> expectedAvps = new ArrayList<IDiameterAVP>();
            AvpUTF8String strAVP = new AvpUTF8String(1, 0, (byte)0, "", "");
            strAVP.setStringValue("2");
            expectedAvps.add(strAVP);
            ReflectionAssert.assertReflectionEquals(expectedAvps, accumalator.getAll());
        }
    }


    public class ValueNotFound {

        private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
        private AttributeFactory attributeFactory = AttributeFactories.newFactory().addStringAvp(0,1).create();


        @Test
        public void test_apply_use_defaultValue_when_provided() throws InvalidExpressionException {

            nonGroupAVPMapping = new PCCAttributeToNonGroupAVPMapping("0:1", "REQ_TYPE", null, "DEFAULT", attributeFactory);

            AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();

            nonGroupAVPMapping.apply(valueProvider, accumalator);

            List<IDiameterAVP> expectedAvps = new ArrayList<IDiameterAVP>();
            AvpUTF8String strAVP = new AvpUTF8String(1, 0, (byte)0, "", "");
            strAVP.setStringValue("DEFAULT");
            expectedAvps.add(strAVP);
            ReflectionAssert.assertReflectionEquals(expectedAvps, accumalator.getAll());
        }

        @Test
        public void test_apply_notaddAVP_when_defaultValueNotProvided() throws InvalidExpressionException {

            nonGroupAVPMapping = new PCCAttributeToNonGroupAVPMapping("0:1", "REQ_TYPE", null, null, attributeFactory);

            nonGroupAVPMapping.apply(valueProvider, accumalator);

            assertTrue(accumalator.isEmpty());
        }
    }



    @Test
    public void test_apply_should_not_store_diameterAVP_if_not_found_from_factory() throws InvalidExpressionException {
        nonGroupAVPMapping = new PCCAttributeToNonGroupAVPMapping("0:1", "REQ_TYPE"
                , null , null, AttributeFactories.newFactory().create());

        nonGroupAVPMapping.apply(valueProvider, null);
    }

    @Test
    public void test_apply_should_not_store_diameterAVP_if_AVP_is_grouped() throws InvalidExpressionException {
        nonGroupAVPMapping = new PCCAttributeToNonGroupAVPMapping("0:10", "REQ_TYPE"
                , null , null,AttributeFactories.newFactory().addGroupedAvp(0, 10).create());
        nonGroupAVPMapping.apply(valueProvider, null);
    }

    private Expression parseExpression(String string) throws InvalidExpressionException {
        return DEFAULT_COMPILER.parseExpression(string);
    }
}
