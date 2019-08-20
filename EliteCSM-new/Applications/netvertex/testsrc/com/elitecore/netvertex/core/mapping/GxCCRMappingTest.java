package com.elitecore.netvertex.core.mapping;

import com.elitecore.corenetvertex.constants.*;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Ignore
public class GxCCRMappingTest {

    private static final String DEFAULT_SOURCE_GW = "sourceGW";
    private static final String DEFAULT_CALLED_STATION = "calledStation";
    private static final String DEFAULT_REQ_NUM = "1";
    private static final String DEFAULT_SESSION_ID = "session@Gx";
    private static final String IPV6 = "0x00402402b400100e0021";
    
    private DiameterToPCCMapping gxCCRMapping = new DiameterToPCCMapping.DiameterToPCCMappingBuilder().build();
    
    @Before
    public void  before() {
        DummyDiameterDictionary.getInstance();
    }
    
    @Test
    public void test_apply_should_store_defaultMappings_in_Request() {
        
        PCRFRequestImpl pcrfRequest = new PCRFRequestImpl(MappingUtil.PCRF_REQUEST_CREATION_TIME);
        PCRFRequestMappingValueProvider pcrfPacketPacketMappingValueProvider = new PCRFRequestMappingValueProvider(MappingUtil.createDefaultDiameterRequest(), pcrfRequest, null);
        
        gxCCRMapping.apply(pcrfPacketPacketMappingValueProvider);
        
        PCRFRequest expectedPcrfRequest = new PCRFRequestBuilder(PCRFEvent.SESSION_START, PCRFEvent.AUTHORIZE, PCRFEvent.AUTHENTICATE, MappingUtil.PCRF_REQUEST_CREATION_TIME)
        .addSessionID(DEFAULT_SESSION_ID)
        .addAttribute(PCRFKeyConstants.REQUEST_NUMBER.getVal(), DEFAULT_REQ_NUM)
        .addAttribute(PCRFKeyConstants.REQUEST_TYPE.getVal(), PCRFKeyValueConstants.REQUEST_TYPE_INITIAL_REQUEST.val)
        .addAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(), DEFAULT_CALLED_STATION)
        .addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), "eliteaaa.elitecore.com")
        .addAttribute(PCRFKeyConstants.CS_GATEWAY_REALM.getVal(), "elitecore.com")
        .addAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal(), DEFAULT_SOURCE_GW)
        .addAttribute(PCRFKeyConstants.CS_SESSION_IPV6.val, IPV6.substring(5))
        .addAttribute(PCRFKeyConstants.NETWORK_REQUEST_SUPPORT.val, PCRFKeyValueConstants.NETWORK_REQUEST_SUPPORTED.val)
        .addAttribute(PCRFKeyConstants.GATEWAY_TYPE.val, GatewayTypeConstant.DIAMETER.val)
        .addAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val)
        .addAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), "session@Gx" 
				+ CommonConstants.COLON + SessionTypeConstant.GX.val)
        .build();
        
        System.out.println("Expected=" + expectedPcrfRequest);
        System.out.println("Actual=" + pcrfPacketPacketMappingValueProvider.getPCRFRequest());
        ReflectionAssert.assertLenientEquals(expectedPcrfRequest, pcrfPacketPacketMappingValueProvider.getPCRFRequest());
    }

    @Test
    public void test_apply_should_store_customMappings_in_Request() throws Exception {
        
        PCRFRequestImpl pcrfRequest = new PCRFRequestImpl(MappingUtil.PCRF_REQUEST_CREATION_TIME);
        
        PCRFRequestMappingValueProvider pcrfPacketPacketMappingValueProvider = new PCRFRequestMappingValueProvider(MappingUtil.createDefaultDiameterRequest(), pcrfRequest, null);
        
        gxCCRMapping = new DiameterToPCCMapping.DiameterToPCCMappingBuilder().withConfiguredMapping(createCustomMappings()).build();
        
        gxCCRMapping.apply(pcrfPacketPacketMappingValueProvider);
        
        PCRFRequest expectedPcrfRequest = new PCRFRequestBuilder(PCRFEvent.SESSION_START, PCRFEvent.AUTHENTICATE, PCRFEvent.AUTHORIZE,  MappingUtil.PCRF_REQUEST_CREATION_TIME)
        .addSessionID(DEFAULT_SESSION_ID)
        .addAttribute(PCRFKeyConstants.REQUEST_NUMBER.getVal(), DEFAULT_REQ_NUM)
        .addAttribute(PCRFKeyConstants.REQUEST_TYPE.getVal(), PCRFKeyValueConstants.REQUEST_TYPE_INITIAL_REQUEST.val)
        .addAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(), DEFAULT_CALLED_STATION)
        .addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), "eliteaaa.elitecore.com")
        .addAttribute(PCRFKeyConstants.CS_GATEWAY_REALM.getVal(), "elitecore.com")
        .addAttribute(PCRFKeyConstants.CS_SOURCE_GATEWAY.getVal(), DEFAULT_SOURCE_GW)
        .addAttribute(PCRFKeyConstants.CS_SESSION_IPV6.val, IPV6.substring(5))
        .addAttribute(PCRFKeyConstants.NETWORK_REQUEST_SUPPORT.val, PCRFKeyValueConstants.NETWORK_REQUEST_NOT_SUPPORTED.val)
        .addAttribute(PCRFKeyConstants.GATEWAY_TYPE.val, GatewayTypeConstant.DIAMETER.val)
        .addAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val)
        .addAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), "session@Gx" 
				+ CommonConstants.COLON + SessionTypeConstant.GX.val)
        .build();
        
        ReflectionAssert.assertLenientEquals(expectedPcrfRequest, pcrfPacketPacketMappingValueProvider.getPCRFRequest());
    }
    
    private Map<LogicalExpression, List<DiameterToPCCPacketMapping>> createCustomMappings() throws Exception {

        Map<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings = new HashMap<LogicalExpression, List<DiameterToPCCPacketMapping>>();
        
        mappings.put(Compiler.getDefaultCompiler().parseLogicalExpression("\"a\"=\"a\""), Arrays.asList(new DiameterToPCCPacketMapping() {
            
            @Override
            public void apply(PCRFRequestMappingValueProvider valueProvider) {
                valueProvider.getPCRFRequest().setAttribute(PCRFKeyConstants.NETWORK_REQUEST_SUPPORT.val, PCRFKeyValueConstants.NETWORK_REQUEST_NOT_SUPPORTED.val);
            }
        }));
        
        return mappings;
    }
    
    @Test
    public void test_apply_shouldNot_store_customMappings_in_Request_when_expressionEvaluatesToFalse() throws Exception {
        
        PCRFRequestImpl pcrfRequest = new PCRFRequestImpl(MappingUtil.PCRF_REQUEST_CREATION_TIME);
        
        PCRFRequestMappingValueProvider pcrfPacketPacketMappingValueProvider = new PCRFRequestMappingValueProvider(MappingUtil.createDefaultGyDiameterRequest(), pcrfRequest, null);
        
        gxCCRMapping = new DiameterToPCCMapping.DiameterToPCCMappingBuilder().withConfiguredMapping(createCustomMappingsWithFalseCondition()).build();
        
        gxCCRMapping.apply(pcrfPacketPacketMappingValueProvider);
        
        assertEquals(pcrfRequest.getAttribute(PCRFKeyConstants.NETWORK_REQUEST_SUPPORT.val), PCRFKeyValueConstants.NETWORK_REQUEST_SUPPORTED.val);
    }

    private Map<LogicalExpression, List<DiameterToPCCPacketMapping>> createCustomMappingsWithFalseCondition() throws InvalidExpressionException {

        Map<LogicalExpression, List<DiameterToPCCPacketMapping>> mappings = new HashMap<LogicalExpression, List<DiameterToPCCPacketMapping>>();
        
        mappings.put(Compiler.getDefaultCompiler().parseLogicalExpression("\"a\"=\"b\""), Arrays.asList(new DiameterToPCCPacketMapping() {
            
            @Override
            public void apply(PCRFRequestMappingValueProvider valueProvider) {
                valueProvider.getPCRFRequest().setAttribute(PCRFKeyConstants.NETWORK_REQUEST_SUPPORT.val, PCRFKeyValueConstants.NETWORK_REQUEST_NOT_SUPPORTED.val);
            }
        }));
        
        return mappings;
    }
}
