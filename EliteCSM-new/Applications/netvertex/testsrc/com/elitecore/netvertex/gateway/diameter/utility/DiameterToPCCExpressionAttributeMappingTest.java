package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCExpressionAttributeMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(HierarchicalContextRunner.class)
public class DiameterToPCCExpressionAttributeMappingTest {

    
    
    private static final String PCRFKEY = "PCRFKEY";
    private Compiler COMPILER = Compiler.getDefaultCompiler();
    
    private DiameterRequest diameterRequest = new DiameterRequest();
    private PCRFRequest pcrfRequest = new PCRFRequestImpl();
    private PCRFRequestMappingValueProvider valueProvider = new PCRFRequestMappingValueProvider(diameterRequest, pcrfRequest, null);
    
    @Before
    public void before() {
        DummyDiameterDictionary.getInstance();
    }
    
    public class ValueMappingProvidedAndValueFound {
        
        
        private Map<String, String> mapping = new HashMap<String, String>();
        private DiameterToPCCExpressionAttributeMapping attributeValueToPCRFAttributeMapping;
        
        @Before
        public void setUp() throws InvalidExpressionException {
            
            mapping.put("elitecore", "sterlite");
            attributeValueToPCRFAttributeMapping = new DiameterToPCCExpressionAttributeMapping(COMPILER.parseExpression("0:1"),"0:1",  PCRFKEY, null, mapping);
            
        }

        
        @Test 
        public void test_apply_value_from_valueMapping_if_respectiveKeyFoundInValueMapping() throws InvalidExpressionException {
            diameterRequest.addAvp("0:1", "elitecore");
           
            attributeValueToPCRFAttributeMapping.apply(valueProvider);
            
            Assert.assertEquals("sterlite", pcrfRequest.getAttribute(PCRFKEY));
           
        }
        
        @Test 
        public void test_apply_value_from_valueProvider_if_respectiveKeyNotFoundInValueMapping() throws InvalidExpressionException {
            
            diameterRequest.addAvp("0:1", "elite");
            
            attributeValueToPCRFAttributeMapping.apply(valueProvider);
            
            Assert.assertEquals("elite", pcrfRequest.getAttribute(PCRFKEY));
        }
    }
    
    public class ValueMappingNotProvidedAndValueFound {
        
        private DiameterToPCCExpressionAttributeMapping attributeValueToPCRFAttributeMapping;
        
        @Before
        public void setUp() throws InvalidExpressionException {
            
            attributeValueToPCRFAttributeMapping = new DiameterToPCCExpressionAttributeMapping(COMPILER.parseExpression("0:1"), "0:1", PCRFKEY, null, null);
            
        }
        
        @Test 
        public void test_apply_value_from_valueProvider() throws InvalidExpressionException {
            
            diameterRequest.addAvp("0:1", "elitecore");
            attributeValueToPCRFAttributeMapping.apply(valueProvider);
            
            Assert.assertEquals("elitecore", pcrfRequest.getAttribute(PCRFKEY));
        }
    }
    
   
    public class ValueNotFound {
    
        private String defaultVal = "DEFAULT";
        
        @Test 
        public void test_apply_use_defaultValue_when_provided() throws InvalidExpressionException {
            
            
            diameterRequest.addAvp("0:1", "elitecore");
            
            DiameterToPCCExpressionAttributeMapping attributeValueToPCRFAttributeMapping = new DiameterToPCCExpressionAttributeMapping(COMPILER.parseExpression("0:1000"), "0:1000", PCRFKEY, defaultVal, null);
            attributeValueToPCRFAttributeMapping.apply(valueProvider);
            
            Assert.assertEquals(defaultVal, pcrfRequest.getAttribute(PCRFKEY));

        }
        
        @Test 
        public void test_apply_notaddAVP_when_defaultValueNotProvided() throws InvalidExpressionException {
            
            diameterRequest.addAvp("0:1", "elitecore");
            
            DiameterToPCCExpressionAttributeMapping attributeValueToPCRFAttributeMapping = new DiameterToPCCExpressionAttributeMapping(COMPILER.parseExpression("0:1000"), "0:1000", PCRFKEY, null, null);
            attributeValueToPCRFAttributeMapping.apply(valueProvider);
            
            Assert.assertNull(pcrfRequest.getAttribute(PCRFKEY));
        }
    }
   
}
