package com.elitecore.netvertex.core.mapping;

import com.elitecore.corenetvertex.constants.ChargingRuleInstallMode;
import com.elitecore.corenetvertex.util.Maps;
import com.elitecore.corenetvertex.util.Maps.Entry;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.core.conf.impl.PacketMappingData;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.*;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ChargingRuleDefinitionPacketMapping;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterGatewayProfileConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.GroupModePCCRuletoDiameterMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.SingleModePCCRuletoDiameterMapping;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class PCRFToDiameterMappingFactoryTest {

	private static final String PCRF_INNER_KEY = "PCRF_INNER_KEY";
	private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();
	private PCRFToDiameterMappingFactory mappingFactory = new PCRFToDiameterMappingFactory(null);

	@SuppressWarnings("unchecked")
	Object[][] dataProviderFor_test_createPCRFToDiameterAttributeMappings_returns_nonGrouped_mappings() throws Exception {
        PacketMappingData mapping1 = createPacketMapping("0:5", "CS.RequestType", null, null);
        PacketMappingData mapping2 = createPacketMapping("0:5", "CS.RequestType","1", null);
        PacketMappingData mapping3 = createPacketMapping("0:5", "trim(  CS.RequestType  )", "1", "1=INITIAL,2=UPDATE,3=TERMINATE");

		return new Object[][] {
				{
					mapping1, new PCCAttributeToNonGroupAVPMapping(mapping1.getAttribute(), mapping1.getPolicyKey(), new HashMap<String, String>(), mapping1.getDefaultValue(), null )
				},
				{
					mapping2, new PCCAttributeToNonGroupAVPMapping(mapping2.getAttribute(), mapping2.getPolicyKey(), new HashMap<String, String>(), mapping2.getDefaultValue(), null)
				},
				{
					mapping3,
					new PCRFAttributeToNonGroupAVPMapping(mapping3.getAttribute(), parseExpression(mapping3.getPolicyKey()), mapping3.getPolicyKey(), Maps.newLinkedHashMap(Entry.newEntry("1", "INITIAL"), Entry.newEntry("2", "UPDATE"), Entry.newEntry("3", "TERMINATE")), mapping3.getDefaultValue(), null)
				},
		};
	}
	
	private Expression parseExpression(String string) throws InvalidExpressionException {
		return DEFAULT_COMPILER.parseExpression(string);
	}

	@Test 
	@Parameters(method="dataProviderFor_test_createPCRFToDiameterAttributeMappings_returns_nonGrouped_mappings")
	public void test_createPCRFToDiameterAttributeMappings_returns_nonGrouped_mappings(PacketMappingData packetMappingData,
			PCRFToDiameterPacketMapping expectedMapping) throws InvalidExpressionException {
		PCRFToDiameterPacketMapping actualMappings = mappingFactory.create(packetMappingData, null, null);
		ReflectionAssert.assertReflectionEquals(expectedMapping, actualMappings); 
	}
	
	
	// GROUPED
	@SuppressWarnings("unchecked")
	public Object[][] dataProviderFor_test_createPCRFToDiameterAttributeMappings_returns_grouped_mappings() throws Exception {

        PacketMappingData parentMapping1 = createPacketMapping("0:1", null, null, null);
        PacketMappingData childMapping11 = createPacketMapping("0:2", "PCRF_KEY1", "DEFAULT_VALUE1", "1=MAPPED1, 2=MAPPED2");
        PacketMappingData childMapping12 = createPacketMapping("0:3", "PCRF_KEY2", "DEFAULT_VALUE2", "1=MAPPED3");
        PacketMappingData childMapping13 = createPacketMapping("0:4", null, null, null);
        PacketMappingData childMapping131 = createPacketMapping("0:5", "PCRF_INNER_KEY1", "DEFAULT_VALUE3", "1=MAPPED4");
        PacketMappingData childMapping132 = createPacketMapping("0:6", "PCRF_INNER_KEY2", "DEFAULT_VALUE4", "1=MAPPED5");
        childMapping13.addChildMapping(childMapping131);
        childMapping13.addChildMapping(childMapping132);
        parentMapping1.addChildMapping(childMapping11);
        parentMapping1.addChildMapping(childMapping12);
        parentMapping1.addChildMapping(childMapping13);

        return new Object[][] {

                {
                        parentMapping1
                        , newMapping("0:1"
                        ,newMapping("0:2", "PCRF_KEY1", "DEFAULT_VALUE1", Maps.newLinkedHashMap(Entry.newEntry("1", "MAPPED1"), Entry.newEntry("2", "MAPPED2")))
                        ,newMapping("0:3", "PCRF_KEY2", "DEFAULT_VALUE2", Maps.newLinkedHashMap(Entry.newEntry("1", "MAPPED3")))
                        ,newMapping("0:4"
                                ,newMapping("0:5", PCRF_INNER_KEY+1, "DEFAULT_VALUE3", Maps.newLinkedHashMap(Entry.newEntry("1", "MAPPED4")))
                                ,newMapping("0:6", PCRF_INNER_KEY+2, "DEFAULT_VALUE4", Maps.newLinkedHashMap(Entry.newEntry("1", "MAPPED5")))))

                }
        };
	}
	
	@Test 
	@Parameters(method="dataProviderFor_test_createPCRFToDiameterAttributeMappings_returns_grouped_mappings")
	public void test_createPCRFToDiameterAttributeMappings_returns_grouped_mappings(PacketMappingData packetMappingData, PCRFToDiameterPacketMapping expectedMapping) throws InvalidExpressionException {
		PCRFToDiameterPacketMapping actualMappings = mappingFactory.create(packetMappingData, null, null);
		ReflectionAssert.assertReflectionEquals(expectedMapping , actualMappings); 
	}

	private PCRFToDiameterPacketMapping newMapping(String key, String value, String defaultValue, Map<String, String> valueMapping) throws InvalidExpressionException {
        if (key.contains("(") && key.contains(")")) {
            return new PCRFAttributeToNonGroupAVPMapping(key, parseExpression(value), value, valueMapping, defaultValue, null);
        }else {
            return new PCCAttributeToNonGroupAVPMapping(key, value, valueMapping, defaultValue, null);
        }
	}
	
	private PCRFToDiameterPacketMapping newMapping(String key, PCRFToDiameterPacketMapping... mapping) {
		return new GroupAttributeMapping(key, Arrays.asList(mapping), null);
	}
	

    @SuppressWarnings("unchecked")
    Object[][] dataProviderFor_test_createPCRFToDiameterAttributeMappings_return_pccToDiameterPacketMapping() throws Exception {
        PacketMappingData parentMapping1 = createPacketMapping("0:1", null, null, null);
        PacketMappingData childMapping11 = createPacketMapping("0:2", "trim( PCRF_KEY1 )", "DEFAULT1", "1=10");
        PacketMappingData childMapping12 = createPacketMapping("0:3", "PCRF_KEY2", "DEFAULT2", "1=20");
        PacketMappingData childMapping13 = createPacketMapping("0:4", "PCCRule", null, null);
        parentMapping1.addChildMapping(childMapping11);
        parentMapping1.addChildMapping(childMapping12);
        parentMapping1.addChildMapping(childMapping13);

        PacketMappingData parentMapping2 = createPacketMapping("0:1", null, null, null);
        PacketMappingData childMapping21 = createPacketMapping("0:2", "PCCRule", null, null);
        PacketMappingData childMapping22 = createPacketMapping("0:3", null, null, null);
        PacketMappingData childMapping221 = createPacketMapping("0:4", "trim(  PCRF_KEY1 )", "DEFAULT1", "1=10");
        PacketMappingData childMapping222 = createPacketMapping("0:5", "PCRF_KEY2", "DEFAULT2", "1=20");
        childMapping22.addChildMapping(childMapping221);
        childMapping22.addChildMapping(childMapping222);
        parentMapping2.addChildMapping(childMapping21);
        parentMapping2.addChildMapping(childMapping22);

        PacketMappingData parentMapping3 = createPacketMapping("0:1", null, null, null);
        PacketMappingData childMapping31 = createPacketMapping("0:2", "trim(PCRF_KEY1)", "DEFAULT1", "1=10");
        PacketMappingData childMapping32 = createPacketMapping("0:3", null, null, null);
        PacketMappingData childMapping321 = createPacketMapping("0:4", "PCCRule", null, null);
        PacketMappingData childMapping322 = createPacketMapping("0:5", "PCRF_KEY2", "DEFAULT2", "1=20");
        childMapping32.addChildMapping(childMapping321);
        childMapping32.addChildMapping(childMapping322);
        parentMapping3.addChildMapping(childMapping31);
        parentMapping3.addChildMapping(childMapping32);

        return new Object[][] {

                {
                        parentMapping1
                        , new GroupModePCCRuletoDiameterMapping(parentMapping1.getAttribute(), createExtraMappings(), null)
                },
                {
                        parentMapping2
                        , new GroupModePCCRuletoDiameterMapping(parentMapping2.getAttribute(), createExtraMappings2(), null)
                },
                {
                        parentMapping3
                        , new GroupModePCCRuletoDiameterMapping(parentMapping3.getAttribute(), createExtraMappings3(), null)
                },

        };
    }
    
    @SuppressWarnings("unchecked")
    private List<PCRFToDiameterPacketMapping> createExtraMappings3() throws InvalidExpressionException {

        List<PCRFToDiameterPacketMapping> mappings = new ArrayList<PCRFToDiameterPacketMapping>();
        List<PCRFToDiameterPacketMapping> mappings2 = new ArrayList<PCRFToDiameterPacketMapping>();
        mappings2.add(new ChargingRuleDefinitionPacketMapping(null, null));
        mappings2.add(new PCCAttributeToNonGroupAVPMapping("0:5", "PCRF_KEY2", Maps.newLinkedHashMap(Entry.newEntry("1", "20")), "DEFAULT2", null));
        mappings.add(new PCRFAttributeToNonGroupAVPMapping("0:2", parseExpression("trim(PCRF_KEY1)"), "trim(PCRF_KEY1)", Maps.newLinkedHashMap(Entry.newEntry("1", "10")), "DEFAULT1", null));
        mappings.add(new GroupAttributeMapping("0:3", mappings2, null));
        return mappings;
    }

    @SuppressWarnings("unchecked")
    private List<PCRFToDiameterPacketMapping> createExtraMappings2() throws InvalidExpressionException {
        List<PCRFToDiameterPacketMapping> mappings = new ArrayList<PCRFToDiameterPacketMapping>();
        List<PCRFToDiameterPacketMapping> mappings2 = new ArrayList<PCRFToDiameterPacketMapping>();
        mappings2.add(new PCRFAttributeToNonGroupAVPMapping("0:4", parseExpression("trim(  PCRF_KEY1 )"), "trim(  PCRF_KEY1 )", Maps.newLinkedHashMap(Entry.newEntry("1", "10")), "DEFAULT1", null));
        mappings2.add(new PCCAttributeToNonGroupAVPMapping("0:5", "PCRF_KEY2", Maps.newLinkedHashMap(Entry.newEntry("1", "20")), "DEFAULT2", null));
        mappings.add(new ChargingRuleDefinitionPacketMapping(null, null));
        mappings.add(new GroupAttributeMapping("0:3", mappings2, null));
        return mappings;
    }
    
    @SuppressWarnings("unchecked")
    private List<PCRFToDiameterPacketMapping> createExtraMappings() throws InvalidExpressionException {

        List<PCRFToDiameterPacketMapping> mappings = new ArrayList<PCRFToDiameterPacketMapping>();
        mappings.add(new PCRFAttributeToNonGroupAVPMapping("0:2", parseExpression("trim( PCRF_KEY1 )"), "trim( PCRF_KEY1 )", Maps.newLinkedHashMap(Entry.newEntry("1", "10")), "DEFAULT1", null));
        mappings.add(new PCCAttributeToNonGroupAVPMapping("0:3", "PCRF_KEY2", Maps.newLinkedHashMap(Entry.newEntry("1", "20")), "DEFAULT2", null));
        mappings.add(new ChargingRuleDefinitionPacketMapping(null, null));
        return mappings;
    }
    
    @Test 
    @Parameters(method="dataProviderFor_test_createPCRFToDiameterAttributeMappings_return_pccToDiameterPacketMapping")
    public void test_createPCRFToDiameterAttributeMappings_return_groupMode_pccToDiameterPacketMapping(PacketMappingData packetMappingData, PCRFToDiameterPacketMapping expectedMapping) throws InvalidExpressionException {

        ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping = new ChargingRuleDefinitionPacketMapping(null, null);
        PCRFToDiameterPacketMapping actualMappings = mappingFactory.create(packetMappingData,
                ChargingRuleInstallMode.GROUPED,
                chargingRuleDefinitionPacketMapping);

        ReflectionAssert.assertReflectionEquals(expectedMapping, actualMappings);
    }
    
    @SuppressWarnings("unchecked")
    public Object[][] dataProviderFor_test_createPCRFToDiameterAttributeMappings_return_singleMode_pccToDiameterPacketMapping() throws Exception {
        PacketMappingData object1 = createPacketMapping("0:1", null, null, null);
        PacketMappingData childMapping11 = createPacketMapping("0:2", "trim( PCRF_KEY1 )", "DEFAULT1", "1=10");
        PacketMappingData childMapping12 = createPacketMapping("0:3", "PCRF_KEY2", "DEFAULT2", "1=20");
        PacketMappingData childMapping13 = createPacketMapping("0:4", "PCCRule", null, null);
        object1.addChildMapping(childMapping11);
        object1.addChildMapping(childMapping12);
        object1.addChildMapping(childMapping13);

        PacketMappingData object2 = createPacketMapping("0:1", null, null, null);
        PacketMappingData childMapping21 = createPacketMapping("0:2", "PCCRule", null, null);
        PacketMappingData childMapping22 = createPacketMapping("0:3", null, null, null);
        PacketMappingData childMapping221 = createPacketMapping("0:4", "trim(  PCRF_KEY1 )", "DEFAULT1", "1=10");
        PacketMappingData childMapping222 = createPacketMapping("0:5", "PCRF_KEY2", "DEFAULT2", "1=20");

        childMapping22.addChildMapping(childMapping221);
        childMapping22.addChildMapping(childMapping222);

        object2.addChildMapping(childMapping21);
        object2.addChildMapping(childMapping22);

        PacketMappingData object3 = createPacketMapping("0:1", null, null, null);
        PacketMappingData childMapping31 = createPacketMapping("0:2", "trim(PCRF_KEY1)", "DEFAULT1", "1=10");
        PacketMappingData childMapping32 = createPacketMapping("0:3", null, null, null);
        PacketMappingData childMapping321 = createPacketMapping("0:4", "PCCRule", null, null);
        PacketMappingData childMapping322 = createPacketMapping("0:5", "PCRF_KEY2", "DEFAULT2", "1=20");

        childMapping32.addChildMapping(childMapping321);
        childMapping32.addChildMapping(childMapping322);
        object3.addChildMapping(childMapping31);
        object3.addChildMapping(childMapping32);

        return new Object[][] {

                {
                        object1
                        , new SingleModePCCRuletoDiameterMapping("0:1", createExtraMappings(), null)
                },
                {
                        object2
                        , new SingleModePCCRuletoDiameterMapping("0:1", createExtraMappings2(), null)
                },
                {
                        object3
                        , new SingleModePCCRuletoDiameterMapping("0:1", createExtraMappings3(), null)
                },

        };
    }
    
    @Test 
    @Parameters(method="dataProviderFor_test_createPCRFToDiameterAttributeMappings_return_singleMode_pccToDiameterPacketMapping")
    public void test_createPCRFToDiameterAttributeMappings_return_singleMode_pccToDiameterPacketMapping(PacketMappingData packetMappingData,
                                                        PCRFToDiameterPacketMapping expectedMapping) throws InvalidExpressionException {
        
        DiameterGatewayProfileConfigurationImpl configuration = mock(DiameterGatewayProfileConfigurationImpl.class);
        when(configuration.getChargingRuleInstallMode()).thenReturn(ChargingRuleInstallMode.SINGLE);
        ChargingRuleDefinitionPacketMapping chargingRuleDefinitionPacketMapping = new ChargingRuleDefinitionPacketMapping(null, null);
        PCRFToDiameterPacketMapping actualMappings = mappingFactory.create(packetMappingData,
                ChargingRuleInstallMode.SINGLE,
                chargingRuleDefinitionPacketMapping);
        ReflectionAssert.assertReflectionEquals(expectedMapping, actualMappings);
    }

    @SuppressWarnings("unchecked")
    public Object[][] dataProviderFor_test_createPCRFToDiameterAttributeMappings_return_singleMode_pcc_withoutJson_ToDiameterPacketMapping() throws Exception {
        PacketMappingData mapping1 = createPacketMapping("0:1", "PCCRule", null, null);
        PacketMappingData mapping2 = createPacketMapping("none", "PCCRule", null, null);

        return new Object[][] {
                
                {
                    mapping1
                    , new SingleModePCCRuletoDiameterMapping("0:1", Arrays.asList(new ChargingRuleDefinitionPacketMapping(null, null)), null)
                },
                {
                    mapping2
                    , new SingleModePCCRuletoDiameterMapping("none", Arrays.asList(new ChargingRuleDefinitionPacketMapping(null, null)), null)
                }
                
        };
    }
    
    @Test
    @Parameters(method="dataProviderFor_test_createPCRFToDiameterAttributeMappings_return_singleMode_pcc_withoutJson_ToDiameterPacketMapping")
    public void test_createPCRFToDiameterAttributeMappings_return_singleMode_pcc_withoutJson_ToDiameterPacketMapping(PacketMappingData packetMappingData, PCRFToDiameterPacketMapping expectedMapping) throws InvalidExpressionException {

        PCRFToDiameterPacketMapping actualMappings = mappingFactory.create(packetMappingData, ChargingRuleInstallMode.SINGLE, new ChargingRuleDefinitionPacketMapping(null, null));
        ReflectionAssert.assertReflectionEquals(actualMappings,expectedMapping);
    }
    
    @SuppressWarnings("unchecked")
    public Object[][] dataProviderFor_test_createPCRFToDiameterAttributeMappings_return_groupMode_pcc_withoutJson_ToDiameterPacketMapping() throws Exception {
        PacketMappingData mapping1 = createPacketMapping("0:1", "PCCRule", null, null);
        PacketMappingData mapping2 = createPacketMapping("none", "PCCRule", null, null);

        return new Object[][] {
                
                {
                    mapping1
                    , new GroupModePCCRuletoDiameterMapping("0:1", Arrays.asList(new ChargingRuleDefinitionPacketMapping(null, null)), null)
                },
                {
                    mapping2
                    , new GroupModePCCRuletoDiameterMapping("none", Arrays.asList(new ChargingRuleDefinitionPacketMapping(null, null)), null)
                }
                
        };
    }

    private PacketMappingData createPacketMapping(String attributeId, String policyKey, String defaultVal, String valMapping) {
        PacketMappingData mapping1 = new PacketMappingData();
        mapping1.setAttribute(attributeId);
        mapping1.setPolicyKey(policyKey);
        mapping1.setDefaultValue(defaultVal);
        mapping1.setValueMapping(valMapping);
        return mapping1;
    }

    @Test 
    @Parameters(method="dataProviderFor_test_createPCRFToDiameterAttributeMappings_return_groupMode_pcc_withoutJson_ToDiameterPacketMapping")
    public void test_createPCRFToDiameterAttributeMappings_return_groupMode_pcc_without_groupMapping_ToDiameterPacketMapping(PacketMappingData packetMappingData, PCRFToDiameterPacketMapping expectedMapping) throws InvalidExpressionException {
        
        DiameterGatewayProfileConfigurationImpl configuration = mock(DiameterGatewayProfileConfigurationImpl.class);
        when(configuration.getChargingRuleInstallMode()).thenReturn(ChargingRuleInstallMode.GROUPED);
        PCRFToDiameterPacketMapping actualMappings = mappingFactory.create(packetMappingData, ChargingRuleInstallMode.GROUPED, new ChargingRuleDefinitionPacketMapping(null, null));
        ReflectionAssert.assertReflectionEquals(actualMappings,expectedMapping);
    }
}
