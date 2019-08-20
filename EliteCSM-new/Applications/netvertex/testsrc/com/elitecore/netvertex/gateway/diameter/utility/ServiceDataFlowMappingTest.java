package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gx.ServiceDataFlowMapping;
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

public class ServiceDataFlowMappingTest {
    
    private ServiceDataFlowMapping serviceDataFlowMapping;
    private AttributeFactory attributeFactory = AttributeFactories.fromDummyDictionary();
    private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
    private PCRFResponse pcrfResponse =  new PCRFResponseImpl();
    
    
    @Test
    public void test_apply_should_store_diameterAVP_asPassed_through_mapping() {
        
        PCCRuleImpl pccRule = spy(new PCCRuleImpl("1", "MyPCC"));
        DiameterPacketMappingValueProvider pccRuleValueProvider = new DiameterPacketMappingValueProvider(pccRule, pcrfResponse, null, null, null);

        when(pccRule.getServiceDataFlows()).thenReturn(Arrays.asList("SDF1", "SDF2", "SDF3"));
        serviceDataFlowMapping = new ServiceDataFlowMapping("10415:507", attributeFactory);
        serviceDataFlowMapping.apply(pccRuleValueProvider, accumalator);
        
        List<IDiameterAVP> expected = createExpectedMappings(attributeFactory);
        List<IDiameterAVP> actual = accumalator.getAll();
        
        ReflectionAssert.assertReflectionEquals(expected, actual);
    }

    @Test
    public void test_apply_should_not_store_diameterAVP_if_not_found_from_factory() throws InvalidExpressionException {
        
        PCCRuleImpl pccRule = spy(new PCCRuleImpl("1", "MyPCC"));
        DiameterPacketMappingValueProvider pccRuleValueProvider = new DiameterPacketMappingValueProvider(pccRule, pcrfResponse, null, null, null);
        when(pccRule.getServiceDataFlows()).thenReturn(Arrays.asList("SDF1", "SDF2", "SDF3"));
        serviceDataFlowMapping = new ServiceDataFlowMapping("10415:11112", attributeFactory);
        serviceDataFlowMapping.apply(pccRuleValueProvider, accumalator);
        assertTrue(accumalator.isEmpty());
    }
    
    @Test
    public void test_apply_should_not_store_diameterAVP_if_AVP_is_grouped() throws InvalidExpressionException {
        
        PCCRuleImpl pccRule = spy(new PCCRuleImpl("1", "MyPCC"));
        DiameterPacketMappingValueProvider pccRuleValueProvider = new DiameterPacketMappingValueProvider(pccRule, pcrfResponse, null, null, null);
        when(pccRule.getServiceDataFlows()).thenReturn(Arrays.asList("SDF1", "SDF2", "SDF3"));
        serviceDataFlowMapping = new ServiceDataFlowMapping("10415:307", attributeFactory);
        serviceDataFlowMapping.apply(pccRuleValueProvider, accumalator);
        assertTrue(accumalator.isEmpty());
    }
    
    @Test
    public void test_apply_should_not_store_diameterAVP_if_valueProvider_is_not_pccRuleValueProvider() throws InvalidExpressionException {
        
        PCCRuleImpl pccRule = spy(new PCCRuleImpl("1", "MyPCC"));
        DiameterPacketMappingValueProvider valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, null, null, null);
        when(pccRule.getServiceDataFlows()).thenReturn(Arrays.asList("SDF1", "SDF2", "SDF3"));
        serviceDataFlowMapping = new ServiceDataFlowMapping("10415:307", attributeFactory);
        serviceDataFlowMapping.apply(valueProvider, accumalator);
        assertTrue(accumalator.isEmpty());
    }
    
    private List<IDiameterAVP> createExpectedMappings(AttributeFactory attributeFactory) {

        List<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();

        for (int index = 1 ; index < 4 ; index++) {
            IDiameterAVP flowGroupedAVP = attributeFactory.create("10415:1058");
            ArrayList<IDiameterAVP> childAVPs = new ArrayList<IDiameterAVP>(1);
            IDiameterAVP sdf = attributeFactory.create("10415:507");
            sdf.setStringValue("SDF" + index);
            childAVPs.add(sdf);
            flowGroupedAVP.setGroupedAvp(childAVPs);
            diameterAVPs.add(flowGroupedAVP);
        }
        
        return diameterAVPs;
    }
    
}
