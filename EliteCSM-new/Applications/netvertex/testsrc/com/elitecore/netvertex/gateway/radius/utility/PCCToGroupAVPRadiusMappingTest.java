package com.elitecore.netvertex.gateway.radius.utility;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.gateway.radius.mapping.PCCtoRadiusMappingValueProvider;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class PCCToGroupAVPRadiusMappingTest {
    private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();
    private PCCToGroupAVPRadiusMapping groupedAttributeMapping;
    private RadiusAttributeFactory radiusAttributeFactory = RadiusAttributeFactories.fromDummyDictionary();
    private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();

    @Test
    public void test_apply_should_store_radiusAttribute_asPassed_through_mapping() throws InvalidExpressionException {
        groupedAttributeMapping = new PCCToGroupAVPRadiusMapping("5535:71", createMapping(radiusAttributeFactory), radiusAttributeFactory);

        groupedAttributeMapping.apply(new PCCtoRadiusMappingValueProvider(new PCRFResponseImpl(), null, null, null), accumalator);

        List<IRadiusAttribute> expectedAttributes = new ArrayList<>();
        GroupedAttribute groupedAttribute = (GroupedAttribute) radiusAttributeFactory.create("5535:71");
        expectedAttributes.add(groupedAttribute);
        groupedAttribute.addTLVAttribute(radiusAttributeFactory.create("5535:71:1", "1"));
        groupedAttribute.addTLVAttribute(radiusAttributeFactory.create("5535:71:2", "1"));

        ReflectionAssert.assertReflectionEquals(expectedAttributes, accumalator.getAll());
    }

    @Test
    public void test_apply_dont_store_diameterAVP_if_no_child_attributes_created() throws InvalidExpressionException {
        groupedAttributeMapping = new PCCToGroupAVPRadiusMapping("5535:71", createBlankMapping(radiusAttributeFactory), radiusAttributeFactory);

        groupedAttributeMapping.apply(new PCCtoRadiusMappingValueProvider(new PCRFResponseImpl(), null, null, null), accumalator);

        List<IRadiusAttribute> expectedAttributes = new ArrayList<IRadiusAttribute>();
        GroupedAttribute groupedAttribute = (GroupedAttribute) radiusAttributeFactory.create("5535:71");
        expectedAttributes.add(groupedAttribute);
        assertTrue(accumalator.isEmpty());
    }

    @Test
    public void test_apply_should_not_store_radiusAttribute_if_not_found_from_factory() throws InvalidExpressionException {
        groupedAttributeMapping = new PCCToGroupAVPRadiusMapping("5555:72", null, radiusAttributeFactory);
        groupedAttributeMapping.apply(null, accumalator);
        assertTrue(accumalator.isEmpty());
    }

    @Test
    public void test_apply_should_not_store__if_radiusAttribute_is_not_grouped() throws InvalidExpressionException {
        groupedAttributeMapping = new PCCToGroupAVPRadiusMapping("0:1", null, radiusAttributeFactory);
        radiusAttributeFactory.create("0:1");
        groupedAttributeMapping.apply(null, accumalator);
        assertTrue(accumalator.isEmpty());
    }

    private List<PCCToRadiusPacketMapping> createBlankMapping(RadiusAttributeFactory radiusAttributeFactory) {

        List<PCCToRadiusPacketMapping> mapping = new ArrayList<>();
        return mapping;
    }

    private List<PCCToRadiusPacketMapping> createMapping(RadiusAttributeFactory radiusAttributeFactory) throws InvalidExpressionException {

        List<PCCToRadiusPacketMapping> mapping = new ArrayList<>();

        mapping.add(new PCCToRadiusNonGroupAVPExpressionMapping("5535:71:1", parseExpression("REQ_TYPE"), null,null, "1", radiusAttributeFactory));
        mapping.add(new PCCToRadiusNonGroupAVPExpressionMapping("5535:71:2", parseExpression("REQ_TYPE"), null,null, "1", radiusAttributeFactory));

        return mapping;
    }

    private Expression parseExpression(String string) throws InvalidExpressionException {
        return DEFAULT_COMPILER.parseExpression(string);
    }
}
