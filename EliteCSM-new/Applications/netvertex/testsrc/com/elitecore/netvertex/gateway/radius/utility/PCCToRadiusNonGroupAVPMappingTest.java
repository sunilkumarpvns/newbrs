package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.corenetvertex.util.Maps;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;
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

public class PCCToRadiusNonGroupAVPMappingTest {
    private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();
    private PCCToRadiusNonGroupAVPExpressionMapping pccToRadiusNonGroupAVPMapping;
    private PCRFResponse pcrfResponse = new PCRFResponseImpl();
    private PCCtoRadiusMappingValueProvider valueProvider = new PCCtoRadiusMappingValueProvider(pcrfResponse,null, null, null);
    private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
    private RadiusAttributeFactory radiusAttributeFactory = RadiusAttributeFactories.fromDummyDictionary();

    public class ValueMappingProvidedAndValueFound {


        @SuppressWarnings("unchecked")
        @Before
        public void setUp() throws InvalidExpressionException {

            pccToRadiusNonGroupAVPMapping = new PCCToRadiusNonGroupAVPExpressionMapping("0:1", parseExpression("REQ_TYPE"), null
                    , Maps.newLinkedHashMap(Maps.Entry.newEntry("1", "INITIAL"))
                    , "INITIAL", radiusAttributeFactory);

        }

        @Test
        public void test_apply_value_from_valueMapping_if_respectiveKeyFoundInValueMapping() throws InvalidExpressionException {

            pcrfResponse.setAttribute("REQ_TYPE", "1");
            pccToRadiusNonGroupAVPMapping.apply(valueProvider, accumalator);

            List<IRadiusAttribute> expectedAttributes = new ArrayList<>();
            IRadiusAttribute radiusAttribute = radiusAttributeFactory.create("0:1");
            radiusAttribute.setStringValue("INITIAL");
            expectedAttributes.add(radiusAttribute);
            ReflectionAssert.assertReflectionEquals(expectedAttributes, accumalator.getAll());
        }

        @Test
        public void test_apply_value_from_valueProvider_if_respectiveKeyNotFoundInValueMapping() throws InvalidExpressionException {

            pcrfResponse.setAttribute("REQ_TYPE", "2");
            pccToRadiusNonGroupAVPMapping.apply(valueProvider, accumalator);
            List<IRadiusAttribute> expectedAttributes = new ArrayList<IRadiusAttribute>();
            IRadiusAttribute radiusAttribute = radiusAttributeFactory.create("0:1");
            radiusAttribute.setStringValue("2");
            expectedAttributes.add(radiusAttribute);
            ReflectionAssert.assertReflectionEquals(expectedAttributes, accumalator.getAll());
        }


    }

    public class ValueMappingNotProvidedAndValueFound {

        private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
        RadiusAttributeFactory radiusAttributeFactory = RadiusAttributeFactories.fromDummyDictionary();
        @Before
        public void setUp() throws InvalidExpressionException {
            pccToRadiusNonGroupAVPMapping = new PCCToRadiusNonGroupAVPExpressionMapping("0:1", parseExpression("REQ_TYPE"), null,null, "INITIAL", radiusAttributeFactory);

            pcrfResponse.setAttribute("REQ_TYPE", "2");
        }

        @Test
        public void test_apply_value_from_valueProvider() throws InvalidExpressionException {

            pccToRadiusNonGroupAVPMapping.apply(valueProvider, accumalator);
            List<IRadiusAttribute> expectedAttributes = new ArrayList<IRadiusAttribute>();
            IRadiusAttribute radiusAttribute = radiusAttributeFactory.create("0:1");
            radiusAttribute.setStringValue("2");
            expectedAttributes.add(radiusAttribute);
            ReflectionAssert.assertReflectionEquals(expectedAttributes, accumalator.getAll());
        }
    }


    public class ValueNotFound {

        private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
        private RadiusAttributeFactory radiusAttributeFactory = RadiusAttributeFactories.fromDummyDictionary();


        @Test
        public void test_apply_use_defaultValue_when_provided() throws InvalidExpressionException {

            pccToRadiusNonGroupAVPMapping = new PCCToRadiusNonGroupAVPExpressionMapping("0:1", parseExpression("REQ_TYPE"), null,null, "DEFAULT", radiusAttributeFactory);

            AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();

            pccToRadiusNonGroupAVPMapping.apply(valueProvider, accumalator);

            List<IRadiusAttribute> expectedAttributes = new ArrayList<IRadiusAttribute>();
            IRadiusAttribute radiusAttribute = radiusAttributeFactory.create("0:1");
            radiusAttribute.setStringValue("DEFAULT");
            expectedAttributes.add(radiusAttribute);
            ReflectionAssert.assertReflectionEquals(expectedAttributes, accumalator.getAll());
        }

        @Test
        public void test_apply_notaddAttribute_when_defaultValueNotProvided() throws InvalidExpressionException {

            pccToRadiusNonGroupAVPMapping = new PCCToRadiusNonGroupAVPExpressionMapping("0:1", parseExpression("REQ_TYPE"), null,null, null, radiusAttributeFactory);

            pccToRadiusNonGroupAVPMapping.apply(valueProvider, accumalator);

            assertTrue(accumalator.isEmpty());
        }
    }

    @Test
    public void test_apply_should_not_store_radius_attributes_if_not_found_from_factory() throws InvalidExpressionException {
        PCCtoRadiusMappingValueProvider valueProvider = new PCCtoRadiusMappingValueProvider(pcrfResponse, null, null, null);
        pccToRadiusNonGroupAVPMapping = new PCCToRadiusNonGroupAVPExpressionMapping("5:20", null, null
                , null , "2", radiusAttributeFactory);
        radiusAttributeFactory.create("5:20");
        pccToRadiusNonGroupAVPMapping.apply(valueProvider, accumalator);
        assertTrue(accumalator.isEmpty());
    }

    @Test
    public void test_apply_should_not_store_radius_attributes_if_radiusAttribute_is_grouped() throws InvalidExpressionException {
        pccToRadiusNonGroupAVPMapping = new PCCToRadiusNonGroupAVPExpressionMapping("5535:71", parseExpression("REQ_TYPE"), null
                , null , "1", RadiusAttributeFactories.fromDummyDictionary());
        pccToRadiusNonGroupAVPMapping.apply(valueProvider, accumalator);
        assertTrue(accumalator.isEmpty());
    }

    private Expression parseExpression(String string) throws InvalidExpressionException {
        return DEFAULT_COMPILER.parseExpression(string);
    }
}
