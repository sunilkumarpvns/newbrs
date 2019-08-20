package com.elitecore.aaa.core.radius.service.auth.handlers;

import static com.elitecore.aaa.EliteAAAMatchers.RadServiceResponseMatchers.containsAttribute;
import static com.elitecore.aaa.EliteAAAMatchers.RadServiceResponseMatchers.rejectMessage;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.impl.IMDGConfigurable;
import com.elitecore.aaa.core.conf.impl.ImdgConfigData;
import com.elitecore.aaa.core.conf.impl.ImdgRadiusSessionConfigData;
import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.policy.handlers.RadImdgConcurrencyHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.RadImdgConcurrencyHandlerData;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession.SessionStatus;
import com.elitecore.aaa.radius.sessionx.ConcurrentPolicyConstants;
import com.elitecore.aaa.radius.sessionx.conf.ConcurrentLoginPolicyConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyConfigurable;
import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyData;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class RadImdgConcurrencyHandlerTest {

	@Mock private RadImdgConcurrencyHandlerData radImdgConcurrencyHandlerData;
	@Mock private RadAuthServiceContext serviceContext;
	@Mock private AAAServerContext serverContext;
	@Mock private AAAServerConfiguration serverConfiguration;
	@Mock private HazelcastRadiusSession session;
	@Mock private HazelcastImdgInstance hazelcastInstance;
	@Mock private ConcurrentLoginPolicyConfiguration policyConfiguration;
	@Mock private IMDGConfigurable imdgConfigurable;
	@Mock private ImdgConfigData imdgConfigData;
	@Mock private ImdgRadiusSessionConfigData imdgRadiusConfig;

	private RadAuthResponse response;
	private ConcurrentLoginPolicyConfigurable policyConfigurable;
	private ConcurrentLoginPolicyData policyData;
	private RadImdgConcurrencyHandler radImdgConcurrencyHandler;
	private RadAuthRequest request;

	@Before
	public void setUp()
			throws Exception {
		MockitoAnnotations.initMocks(this);
		readPolicyData();
		setPolicyData();
		setConcurrencyIdentityIndexDetail();
		setUpRequestAndResponse();
		setRadImdgConcurrencyHandlerData();
		radImdgConcurrencyHandler = new RadImdgConcurrencyHandler(serviceContext, radImdgConcurrencyHandlerData);
		radImdgConcurrencyHandler.init();
	}


	private void setPolicyData() {
		Mockito.when(serviceContext.getServerContext()).thenReturn(serverContext);
		Mockito.when(serverContext.getServerConfiguration()).thenReturn(serverConfiguration);
		Mockito.when(serverConfiguration.getConcurrentLoginPolicyConfiguration()).thenReturn(policyConfiguration);
		Mockito.when(policyConfiguration.getConcurrentLoginPolicy(Mockito.anyString())).thenReturn(policyData);
	}

	private void setConcurrencyIdentityIndexDetail() {
		Map<String, ImdgIndexDetail> radiusIndexFieldMap = new HashMap<>();

		ImdgIndexDetail indexdetail = new ImdgIndexDetail();
		indexdetail.setAttributeList(new ArrayList<>(Arrays.asList("0:1")));
		indexdetail.setImdgIndex("index0");
		indexdetail.setImdgAttributeValue("0:1");
		indexdetail.setImdgFieldValue(RadiusAttributeConstants.USER_NAME_STR);

		radiusIndexFieldMap.put(RadiusAttributeConstants.USER_NAME_STR, indexdetail);

		when(serverConfiguration.getImdgConfigurable()).thenReturn(imdgConfigurable);
		when(imdgConfigurable.getImdgConfigData()).thenReturn(imdgConfigData);
		when(imdgConfigData.getImdgRadiusConfig()).thenReturn(imdgRadiusConfig);
		when(imdgRadiusConfig.getRadiusIndexFieldMap()).thenReturn(radiusIndexFieldMap);
	}

	private void setRadImdgConcurrencyHandlerData() {
		Mockito.when(radImdgConcurrencyHandlerData.getConcurrencyIdentityField())
		.thenReturn(RadiusAttributeConstants.USER_NAME_STR);
	}


	private void setUpRequestAndResponse() throws InvalidAttributeIdException {
		request = new RadAuthRequestBuilder()
				.addAttribute("5535:44", "session2")
				.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test")
				.addAttribute("21067:114","csm")
				.addAttribute("21067:144", policyData.getName())
				.packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE)
				.build();

		response = new RadAuthRequestBuilder().buildResponse();

	}

	private void readPolicyData() throws FileNotFoundException, JAXBException, UnsupportedEncodingException {
		File policyDataFile = new File(ClasspathResource.at("system/_sys.concurrentloginpolicy").getAbsolutePath());
		policyConfigurable = ConfigUtil.deserialize(policyDataFile, ConcurrentLoginPolicyConfigurable.class);
		policyConfigurable.postRead();
		List<ConcurrentLoginPolicyData> concurrentLoignPolicyDatas = policyConfigurable.getConcurrentLoginPolicies();
		policyData = concurrentLoignPolicyDatas.get(0);
	}

	@Test
	public void isAlwaysEligible() {
		assertTrue(radImdgConcurrencyHandler.isEligible(null, null));
	}

	public class IndividualTypePolicy {
		
		@Before
		public void before() {
			policyData.setMaxLogins(1l);
			Set<String> sessionKeys = new HashSet<>();
			sessionKeys.add("session1");
			Mockito.when(serverContext.search(Mockito.anyString(), Mockito.anyString())).thenReturn(sessionKeys);
			Mockito.when(serverContext.getOrCreateRadiusSession(Mockito.anyString())).thenReturn(session);
			Mockito.when(session.getSessionStatus()).thenReturn(SessionStatus.ACTIVE);
		}

		@Test
		public void rejectsResponseWhenConcurrencyLoginLimitIsReached() {
			radImdgConcurrencyHandler.handleRequest(request, response, session);
			assertThat(response, rejectMessage()); 
			assertThat(response, containsAttribute(
					RadiusAttributeConstants.REPLY_MESSAGE_STR, AuthReplyMessageConstant.MAX_LOGIN_LIMIT_REACHED));
		}
		
		@Test
		public void ConcurrencyFailsForIndiviualPolicyTypeIfConcurrencyIdentityAttributeIsMissing() throws InvalidAttributeIdException {
			removeAttribute("0:1"); 
			radImdgConcurrencyHandler.handleRequest(request, response, session);
			assertThat(response, rejectMessage());
			assertThat(response, containsAttribute(
					RadiusAttributeConstants.REPLY_MESSAGE_STR, AuthReplyMessageConstant.CONCURRENCY_FAILED));
		}

	}

	public class GroupTypePolicy{ 
		@Before
		public void setUpGroupPolicy() {
			policyData.setPolicyType(ConcurrentPolicyConstants.GROUP_POLICY);
			policyData.setMaxLogins(1l);
			Set<String> sessionKeys = new HashSet<>();
			sessionKeys.add("session1");
			Mockito.when(serverContext.search(Mockito.anyString(), Mockito.anyString())).thenReturn(sessionKeys);
			Mockito.when(serverContext.getOrCreateRadiusSession(Mockito.anyString())).thenReturn(session);
			Mockito.when(session.getSessionStatus()).thenReturn(SessionStatus.ACTIVE);
		}

		@Test
		public void rejectsResponseWhenConcurrencyLoginLimitIsReached() {
			radImdgConcurrencyHandler.handleRequest(request, response, session);
			assertThat(response, rejectMessage()); 
			assertThat(response, containsAttribute(
					RadiusAttributeConstants.REPLY_MESSAGE_STR, AuthReplyMessageConstant.MAX_LOGIN_LIMIT_REACHED));
		}
		
		@Test
		public void ConcurrencyFailsForGroupPolicyTypeIfConcurrencyIdentityAttributeIsMissing() throws InvalidAttributeIdException {
			removeAttribute("21067:114"); 
			radImdgConcurrencyHandler.handleRequest(request, response, session);
			assertThat(response, rejectMessage());
			assertThat(response, containsAttribute(
					RadiusAttributeConstants.REPLY_MESSAGE_STR, AuthReplyMessageConstant.CONCURRENCY_FAILED));
		}

	}

	private void removeAttribute(String attribute) {
		IRadiusAttribute userName = request.getRadiusAttribute(attribute);
		request.removeAttribute(userName, true);
	}

	@Test
	public void concurrencyFailsIfPolicyModeIsNotGeneral() {
		policyData.setPolicyMode("XYZ");
		radImdgConcurrencyHandler.handleRequest(request, response, session);
		assertThat(response, rejectMessage());
		assertThat(response, containsAttribute(
				RadiusAttributeConstants.REPLY_MESSAGE_STR, AuthReplyMessageConstant.CONCURRENCY_FAILED));
	}

	@Test
	public void concurrencyFailsIfPolicyTypeIsNeitherGroupNorIndividual() {
		policyData.setPolicyType("ServiceType");
		radImdgConcurrencyHandler.handleRequest(request, response, session);
		assertThat(response, rejectMessage());
		assertThat(response, containsAttribute(
				RadiusAttributeConstants.REPLY_MESSAGE_STR, AuthReplyMessageConstant.CONCURRENCY_FAILED));
	}

}
