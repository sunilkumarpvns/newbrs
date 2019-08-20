package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.GroupAttributeMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFAttributeToNonGroupAVPMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class GroupAttributeMappingTest {

	private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();
	private GroupAttributeMapping groupedAttributeMapping;
	private AttributeFactory attributeFactory = AttributeFactories.newFactory().create();
	private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
	
	@Test 
	public void test_apply_should_store_diameterAVP_asPassed_through_mapping() throws InvalidExpressionException {
		
		attributeFactory = AttributeFactories.newFactory().addStringAvp(0,1).addGroupedAvp(0, 10).create();
		
		groupedAttributeMapping = new GroupAttributeMapping("0:10", createMapping(attributeFactory), attributeFactory);
		
		groupedAttributeMapping.apply(new DiameterPacketMappingValueProvider(new PCRFResponseImpl(), null, null, null), accumalator);
		
		List<IDiameterAVP> expectedAvps = new ArrayList<IDiameterAVP>();
		AvpGrouped groupedAVP = (AvpGrouped) attributeFactory.create("0:10");
		expectedAvps.add(groupedAVP);
		groupedAVP.addSubAvp(attributeFactory.create("0:1", "INITIAL"));
		
		ReflectionAssert.assertReflectionEquals(expectedAvps, accumalator.getAll());
	}

	@Test 
	public void test_apply_dont_store_diameterAVP_if_no_ChildAvpCreated() throws InvalidExpressionException {
		
		attributeFactory = AttributeFactories.newFactory().addStringAvp(0,1).addGroupedAvp(0, 10).create();
		
		groupedAttributeMapping = new GroupAttributeMapping("0:10", createBlankMapping(attributeFactory), attributeFactory);
		
		groupedAttributeMapping.apply(new DiameterPacketMappingValueProvider(new PCRFResponseImpl(), null, null, null), accumalator);
		
		List<IDiameterAVP> expectedAvps = new ArrayList<IDiameterAVP>();
		AvpGrouped groupedAVP = (AvpGrouped) attributeFactory.create("0:10");
		expectedAvps.add(groupedAVP);
		groupedAVP.addSubAvp(attributeFactory.create("0:1", "INITIAL"));
		
		assertTrue(accumalator.isEmpty());
	}

	private List<PCRFToDiameterPacketMapping> createBlankMapping(
			AttributeFactory attributeFactory2) {
		
		List<PCRFToDiameterPacketMapping> mapping = new ArrayList<PCRFToDiameterPacketMapping>();
		return mapping;
	}

	private List<PCRFToDiameterPacketMapping> createMapping(AttributeFactory attributeFactory) throws InvalidExpressionException {

		List<PCRFToDiameterPacketMapping> mapping = new ArrayList<PCRFToDiameterPacketMapping>();
		
		mapping.add(new PCRFAttributeToNonGroupAVPMapping("0:1", parseExpression("REQ_TYPE") , "REQ_TYPE", null , "INITIAL" , attributeFactory));
		
		return mapping;
	}
	
	@Test
	public void test_apply_should_not_store_diameterAVP_if_not_found_from_factory() throws InvalidExpressionException {
		groupedAttributeMapping = new GroupAttributeMapping("0:10", null, attributeFactory);
		groupedAttributeMapping.apply(null, accumalator);
		assertTrue(accumalator.isEmpty());
	}
	
	@Test
	public void test_apply_should_not_store_diameterAVP_if_AVP_is_not_grouped() throws InvalidExpressionException {
		groupedAttributeMapping = new GroupAttributeMapping("0:10", null, AttributeFactories.newFactory().addStringAvp(0, 10).create());
		groupedAttributeMapping.apply(null, accumalator);
		assertTrue(accumalator.isEmpty());
	}
	
	private Expression parseExpression(String string) throws InvalidExpressionException {
		return DEFAULT_COMPILER.parseExpression(string);
	}
}
