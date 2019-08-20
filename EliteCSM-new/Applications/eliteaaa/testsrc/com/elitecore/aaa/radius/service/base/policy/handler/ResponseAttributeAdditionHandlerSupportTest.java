package com.elitecore.aaa.radius.service.base.policy.handler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.ResponseAttributeAdditionHandlerData;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthRequestImpl;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthResponseImpl;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

public class ResponseAttributeAdditionHandlerSupportTest {

	private static final String CLASS_ATTRIBUTE = "0:25";
	private static final String RESPONSE_ATTRIBUTES = "0:25='poolname=IpPool', 0:25='poolid=1', 0:25='poolserialnumber=10'";
	
	@Mock private RadAuthServiceContext serviceContext;
	@Mock private AAAServerContext serverContext;
	
	private RadiusServicePolicyData radiusServicePolicyData = new RadiusServicePolicyData();
	private RadAuthRequest radAuthRequest ;
	private RadAuthResponse radAuthResponse  ;
	private RadAuthServiceHandler responseAttributeAdditionHandler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws ManagerInitialzationException {
		RadiusDictionaryTestHarness.getInstance();
	}
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		radAuthRequest = new RadiusAuthRequestImpl(new byte[16],InetAddress.getLocalHost(), 0, serverContext, new SocketDetail(InetAddress.getLocalHost().getHostAddress(), 0));
		radAuthResponse = new RadiusAuthResponseImpl(null, 0, serverContext);
		createResponseAttributeAddtionalHandler(RESPONSE_ATTRIBUTES);
	}
	
	@Test
	public void multipleOccurrenceOfSameAvpCanBeConfigured() throws Exception {
		
		responseAttributeAdditionHandler.handleRequest(radAuthRequest, radAuthResponse, null);
		
		Collection<IRadiusAttribute> reponseAVPs = radAuthResponse.getRadiusAttributes(CLASS_ATTRIBUTE);
		
		assertThat(reponseAVPs.size(), is(equalTo(3)));		
	}
	
	@Test
	public void orderOfAddedAvpsWithSameAvpIdShouldBeSameAsOrderInWhichAvpsConfigured() throws Exception {
		
		responseAttributeAdditionHandler.handleRequest(radAuthRequest, radAuthResponse, null);
		
		List<IRadiusAttribute> reponseAVPs = (List<IRadiusAttribute>)radAuthResponse.getRadiusAttributes(CLASS_ATTRIBUTE);
		
		assertThat(reponseAVPs.get(0).getStringValue(), is(equalTo("'poolname=IpPool'")));
		assertThat(reponseAVPs.get(1).getStringValue(), is(equalTo("'poolid=1'")));
		assertThat(reponseAVPs.get(2).getStringValue(), is(equalTo("'poolserialnumber=10'")));
	}
	
	private void createResponseAttributeAddtionalHandler(String responseAttributes) throws Exception {
		
		radiusServicePolicyData.setAuthResponseAttributes(responseAttributes);
		ResponseAttributeAdditionHandlerData responseAttributeAdditionHandlerData = new ResponseAttributeAdditionHandlerData();
		responseAttributeAdditionHandlerData.setRadiusServicePolicyData(radiusServicePolicyData);
		
		responseAttributeAdditionHandler= responseAttributeAdditionHandlerData.createHandler(serviceContext);
		responseAttributeAdditionHandler.init();
		
	}
}
