package com.elitecore.netvertex.core.mapping;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.netvertex.core.conf.impl.PacketMappingData;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCAttributeMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCExpressionAttributeMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMappingFactory;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import com.elitecore.corenetvertex.util.Maps;
import com.elitecore.corenetvertex.util.Maps.Entry;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;

@RunWith(JUnitParamsRunner.class)
public class DiameterToPCCMappingFactoryTest {

    private DiameterToPCCMappingFactory mappingFactory = new DiameterToPCCMappingFactory();
    private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();
    private static final String PCRF_INNER_KEY = "PCRF_INNER_KEY";

    @SuppressWarnings("unchecked")
    Object[][] dataProviderFor_test_createDiameterToPCCAttributeMappings_returns_nonGrouped_mappings() throws Exception {
        PacketMappingData mapping1 = createPacketMapping("0:5", "CS.RequestType", null, null);
        PacketMappingData mapping2 = createPacketMapping("0:5", "CS.RequestType","1", null);
        PacketMappingData mapping3 = createPacketMapping("  trim(0.5)  ", "CS.RequestType", "2", "1=INITIAL,2=UPDATE,3=TERMINATE");

        return new Object[][] {
                {
                        mapping1, new DiameterToPCCAttributeMapping(mapping1.getAttribute(), mapping1.getPolicyKey(), mapping1.getDefaultValue(), new HashMap<String, String>())
                },
                {
                        mapping2, new DiameterToPCCAttributeMapping(mapping2.getAttribute(), mapping2.getPolicyKey(), mapping2.getDefaultValue(), new HashMap<String, String>())
                },
                {
                        mapping3,
                        new DiameterToPCCExpressionAttributeMapping(parseExpression(mapping3.getAttribute()), mapping3.getAttribute(), mapping3.getPolicyKey(), mapping3.getDefaultValue(), Maps.newLinkedHashMap(Entry.newEntry("1", "INITIAL"), Entry.newEntry("2", "UPDATE"), Entry.newEntry("3", "TERMINATE")))
                },
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
    
    private Expression parseExpression(String string) throws InvalidExpressionException {
        return DEFAULT_COMPILER.parseExpression(string);
    }

    @Test
    @Parameters(method="dataProviderFor_test_createDiameterToPCCAttributeMappings_returns_nonGrouped_mappings")
    public void test_createPCRFToDiameterAttributeMappings_returns_nonGrouped_mappings(PacketMappingData packetMappingData,
                                                                                       DiameterToPCCPacketMapping expectedMapping) throws InvalidExpressionException {
        DiameterToPCCPacketMapping actualMappings = mappingFactory.create(packetMappingData);
        ReflectionAssert.assertReflectionEquals(expectedMapping, actualMappings);
    }

    private DiameterToPCCPacketMapping newMapping(String key, String value, String defaultValue, Map<String, String> valueMapping) throws InvalidExpressionException {
        if (key.contains("(") && key.contains(")")) {
            return new DiameterToPCCExpressionAttributeMapping(parseExpression(key), key, value, defaultValue, valueMapping);
        }else {
            return new DiameterToPCCAttributeMapping(key, value, defaultValue, valueMapping);
        }
    }

    // GROUPED
    @SuppressWarnings("unchecked")
    public Object[][] dataProviderFor_test_createDiameterToPCCAttributeMappings_returns_grouped_mappings() throws Exception {
        PacketMappingData parentMapping1 = createPacketMapping("0:1.0:2.0:3", "PCRF_KEY1", "DEFAULT_VALUE1", "1=MAPPED1,2=MAPPED2");
        PacketMappingData parentMapping2 = createPacketMapping("0:4", "PCRF_KEY2", "DEFAULT_VALUE2", null);
        PacketMappingData parentMapping3 = createPacketMapping("trim(  0:5.0:6)", "PCRF_KEY3", "DEFAULT_VALUE3", "3=MAPPED3");

        return new Object[][] {
                {
                        parentMapping1
                        ,newMapping("0:1.0:2.0:3", "PCRF_KEY1", "DEFAULT_VALUE1", Maps.newLinkedHashMap(Entry.newEntry("1", "MAPPED1"), Entry.newEntry("2", "MAPPED2")))
                },
                {
                        parentMapping2
                        ,newMapping("0:4", "PCRF_KEY2", "DEFAULT_VALUE2", new HashMap<String, String>())
                },
                {
                        parentMapping3
                        ,newMapping("trim(  0:5.0:6)","PCRF_KEY3","DEFAULT_VALUE3",Maps.newLinkedHashMap(Entry.newEntry("3", "MAPPED3")))
                }

        };
    }

    @Test
    @Parameters(method="dataProviderFor_test_createDiameterToPCCAttributeMappings_returns_grouped_mappings")
    public void test_createDiameterToPCCAttributeMappings_returns_grouped_mappings(PacketMappingData packetMappingData, DiameterToPCCPacketMapping expectedMapping) throws InvalidExpressionException {
        DiameterToPCCPacketMapping actualMappings = mappingFactory.create(packetMappingData);

        ReflectionAssert.assertReflectionEquals(expectedMapping , actualMappings);
    }
}
