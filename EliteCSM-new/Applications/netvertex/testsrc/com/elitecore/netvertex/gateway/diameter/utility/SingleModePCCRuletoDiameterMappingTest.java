package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl;
import com.elitecore.corenetvertex.util.Maps;
import com.elitecore.corenetvertex.util.Maps.Entry;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFAttributeToNonGroupAVPMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.SingleModePCCRuletoDiameterMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SingleModePCCRuletoDiameterMappingTest {

    private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();
    private SingleModePCCRuletoDiameterMapping singleModePCCRuletoDiameterMapping;
    private PCRFResponse pcrfResponse =  spy(new PCRFResponseImpl());
    private DiameterPacketMappingValueProvider valueProvider =  new DiameterPacketMappingValueProvider(pcrfResponse, null, null, null);
    private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
    
    @Test 
    public void test_apply_should_store_diameterAVP_asPassed_through_mapping() throws InvalidExpressionException {
        AttributeFactory attributeFactory = AttributeFactories.newFactory().addGroupedAvp(0, 1).addStringAvp(0, 2).addStringAvp(0, 3)
                .addStringAvp(0, 4).addGroupedAvp(0, 5).create();
        
        singleModePCCRuletoDiameterMapping = new SingleModePCCRuletoDiameterMapping("0:1", createExtraMappings(attributeFactory), attributeFactory);

        when(pcrfResponse.getInstallablePCCRules()).thenReturn(Arrays.asList(new PCCRuleImpl("1", "MyPCC")));
        
        singleModePCCRuletoDiameterMapping.apply(valueProvider, accumalator);
        
        ReflectionAssert.assertReflectionEquals(createExpectedMappings(attributeFactory), accumalator.getAll());
    }
   
    @Test 
    public void test_apply_should_store_diameterAVPs_for_multiple_pccs() throws InvalidExpressionException {
        
        AttributeFactory attributeFactory = AttributeFactories.fromDummyDictionary();
        
        singleModePCCRuletoDiameterMapping = new SingleModePCCRuletoDiameterMapping("0:260", createExtraMappingsFromDummyDictionary(attributeFactory), attributeFactory);

        when(pcrfResponse.getInstallablePCCRules()).thenReturn(Arrays.asList(new PCCRuleImpl("1", "MyPCC1"), new PCCRuleImpl("2", "MyPCC2")));
        
        singleModePCCRuletoDiameterMapping.apply(valueProvider, accumalator);
        
        ReflectionAssert.assertReflectionEquals(createExpectedMappingsFromDummyDictionary(attributeFactory), accumalator.getAll());
    }
      
    @Test
    public void test_apply_should_not_store_diameterAVP_if_not_found_from_factory() throws InvalidExpressionException {
        when(pcrfResponse.getInstallablePCCRules()).thenReturn(Arrays.asList(new PCCRuleImpl("1", "MyPCC")));
        AttributeFactory attributeFactory = AttributeFactories.newFactory().create();
        singleModePCCRuletoDiameterMapping = new SingleModePCCRuletoDiameterMapping("0:1", null, attributeFactory);
        singleModePCCRuletoDiameterMapping.apply(valueProvider, accumalator);
        assertTrue(accumalator.isEmpty());
    }
    
    @Test
    public void test_apply_should_not_store_diameterAVP_if_AVP_is_not_grouped() throws InvalidExpressionException {
        when(pcrfResponse.getInstallablePCCRules()).thenReturn(Arrays.asList(new PCCRuleImpl("1", "MyPCC")));
        singleModePCCRuletoDiameterMapping = new SingleModePCCRuletoDiameterMapping("0:1", null, AttributeFactories.newFactory().addStringAvp(0, 1).create());
        singleModePCCRuletoDiameterMapping.apply(valueProvider, accumalator);
        assertTrue(accumalator.isEmpty());
    }

    
    private List<IDiameterAVP> createExpectedMappingsFromDummyDictionary(AttributeFactory attributeFactory) {

        List<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
        AvpGrouped groupedAVP = (AvpGrouped) attributeFactory.create("0:260");
        groupedAVP.addSubAvp(attributeFactory.create("0:266", "1"));
        groupedAVP.addSubAvp(attributeFactory.create("0:258", "2"));
        diameterAVPs.add(groupedAVP);
        groupedAVP = (AvpGrouped) attributeFactory.create("0:260");
        groupedAVP.addSubAvp(attributeFactory.create("0:266", "1"));
        groupedAVP.addSubAvp(attributeFactory.create("0:258", "2"));
        diameterAVPs.add(groupedAVP);
        return diameterAVPs;
     }

  @SuppressWarnings("unchecked")
     private List<PCRFToDiameterPacketMapping> createExtraMappingsFromDummyDictionary(
             AttributeFactory attributeFactory) throws InvalidExpressionException {
         List<PCRFToDiameterPacketMapping> mappings = new ArrayList<PCRFToDiameterPacketMapping>();
         mappings.add(new PCRFAttributeToNonGroupAVPMapping("0:266", parseExpression("PCRF_KEY1"), "PCRF_KEY1", Maps.newLinkedHashMap(Entry.newEntry("1", "10")), "1", attributeFactory));
         mappings.add(new PCRFAttributeToNonGroupAVPMapping("0:258", parseExpression("PCRF_KEY2"), "PCRF_KEY2", Maps.newLinkedHashMap(Entry.newEntry("1", "20")), "2", attributeFactory));
         mappings.add(new ChargingRuleDefinitionPacketMapping(null, null));
         return mappings;
     }

     private List<IDiameterAVP> createExpectedMappings(AttributeFactory attributeFactory) {

         List<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
         AvpGrouped groupedAVP = (AvpGrouped) attributeFactory.create("0:1");
         diameterAVPs.add(groupedAVP);
         return diameterAVPs;
     }

     @SuppressWarnings("unchecked")
     private List<PCRFToDiameterPacketMapping> createExtraMappings(AttributeFactory attributeFactory) throws InvalidExpressionException {
         
         List<PCRFToDiameterPacketMapping> mappings = new ArrayList<PCRFToDiameterPacketMapping>();
         mappings.add(new PCRFAttributeToNonGroupAVPMapping("0:2", parseExpression("PCRF_KEY1"), "PCRF_KEY1", Maps.newLinkedHashMap(Entry.newEntry("1", "10")), "DEFAULT1", attributeFactory));
         mappings.add(new PCRFAttributeToNonGroupAVPMapping("0:3", parseExpression("PCRF_KEY2"), "PCRF_KEY2", Maps.newLinkedHashMap(Entry.newEntry("1", "20")), "DEFAULT2", attributeFactory));
         mappings.add(new ChargingRuleDefinitionPacketMapping(null, null));
         return mappings;
     }

    private Expression parseExpression(String string) throws InvalidExpressionException {
        return DEFAULT_COMPILER.parseExpression(string);
    }
}
