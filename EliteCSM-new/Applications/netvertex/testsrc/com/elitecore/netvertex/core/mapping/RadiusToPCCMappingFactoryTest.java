package com.elitecore.netvertex.core.mapping;

import com.elitecore.corenetvertex.util.Maps;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.netvertex.core.conf.impl.PacketMappingData;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCAttributeMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCExpressionAttribiuteMapping;
import com.elitecore.netvertex.gateway.radius.mapping.RadiusToPCCMappingFactory;
import com.elitecore.netvertex.gateway.radius.utility.RadiusToPCCPacketMapping;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.HashMap;
import java.util.Map;

@RunWith(JUnitParamsRunner.class)
public class RadiusToPCCMappingFactoryTest {
    private RadiusToPCCMappingFactory mappingFactory = new RadiusToPCCMappingFactory();
    private static final Compiler DEFAULT_COMPILER = Compiler.getDefaultCompiler();

    @SuppressWarnings("unchecked")
    Object[][] dataProviderFor_test_create_returns_simple_mappings() throws Exception {
        PacketMappingData mapping1 = createPacketMapping("0:5", "CS.RequestType", null, null);
        PacketMappingData mapping2 = createPacketMapping("0:5", "CS.RequestType", "1", null);
        PacketMappingData mapping3 = createPacketMapping("0:1:2:3",
                "PCRF_KEY1",
                "DEFAULT_VALUE1",
                "1=MAPPED1,2=MAPPED2");
        PacketMappingData mapping4 = createPacketMapping("0:4",
                "PCRF_KEY2",
                "DEFAULT_VALUE2",
                null);

        return new Object[][]{
                {
                        mapping1, new RadiusToPCCAttributeMapping(mapping1.getAttribute(), mapping1.getPolicyKey(), mapping1.getDefaultValue(), new HashMap<String, String>())
                },
                {
                        mapping2, new RadiusToPCCAttributeMapping(mapping2.getAttribute(), mapping2.getPolicyKey(), mapping2.getDefaultValue(), new HashMap<String, String>())
                },
                {
                        mapping3, newSimpleMapping("0:1:2:3", "PCRF_KEY1",
                        "DEFAULT_VALUE1", Maps.newLinkedHashMap(Maps.Entry.newEntry("1", "MAPPED1"),
                                Maps.Entry.newEntry("2", "MAPPED2")))
                },
                {
                        mapping4, newSimpleMapping("0:4", "PCRF_KEY2", "DEFAULT_VALUE2", new HashMap<String, String>())
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

    private Expression parseExpression(String string) throws InvalidExpressionException {
        return DEFAULT_COMPILER.parseExpression(string);
    }

    @Test
    @Parameters(method="dataProviderFor_test_create_returns_simple_mappings")
    public void test_create_returns_simple_mappings(PacketMappingData packetMappingData,
                                                                                     RadiusToPCCPacketMapping expectedMapping) throws InvalidExpressionException {
        RadiusToPCCPacketMapping actualMappings = mappingFactory.create(packetMappingData);
        ReflectionAssert.assertReflectionEquals(expectedMapping, actualMappings);
    }

    private RadiusToPCCPacketMapping newExpressionMapping(String key, String value, String defaultValue, Map<String, String> valueMapping)
            throws InvalidExpressionException {
        return new RadiusToPCCExpressionAttribiuteMapping(parseExpression(key), key, value, defaultValue, valueMapping);
    }

    private RadiusToPCCPacketMapping newSimpleMapping(String key, String value, String defaultValue, Map<String, String> valueMapping)
            throws InvalidExpressionException {
        return new RadiusToPCCAttributeMapping(key, value, defaultValue, valueMapping);
    }

    @SuppressWarnings("unchecked")
    public Object[][] dataProviderFor_test_create_returns_expression_mappings() throws Exception {
        PacketMappingData mapping1 = createPacketMapping("  trim(0.5)  ", "CS.RequestType", "2", "1=INITIAL,2=UPDATE,3=TERMINATE");
        PacketMappingData mapping2 = createPacketMapping("trim(  0:5:6)",
                                            "PCRF_KEY3",
                                            "DEFAULT_VALUE3",
                                            "3=MAPPED3");

        return new Object[][] {
                {
                        mapping1,
                        new RadiusToPCCExpressionAttribiuteMapping(parseExpression(mapping1.getAttribute()), mapping1.getAttribute(), mapping1.getPolicyKey(),
                                mapping1.getDefaultValue(), Maps.newLinkedHashMap(Maps.Entry.newEntry("1", "INITIAL"),
                                Maps.Entry.newEntry("2", "UPDATE"), Maps.Entry.newEntry("3", "TERMINATE")))
                },
                {
                        mapping2,
                        newExpressionMapping("trim(  0:5:6)","PCRF_KEY3",
                        "DEFAULT_VALUE3",Maps.newLinkedHashMap(Maps.Entry.newEntry("3", "MAPPED3")))
                }

        };
    }

    @Test
    @Parameters(method="dataProviderFor_test_create_returns_expression_mappings")
    public void test_create_returns_expression_mappings(PacketMappingData packetMappingData, RadiusToPCCPacketMapping expectedMapping)
            throws InvalidExpressionException {
        RadiusToPCCPacketMapping actualMappings = mappingFactory.create(packetMappingData);

        ReflectionAssert.assertReflectionEquals(expectedMapping , actualMappings);
    }
}
