package com.elitecore.aaa.diameter.service.application.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

// TODO NARENDRA - add missing test cases of other options
@RunWith(HierarchicalContextRunner.class)
public class CUIAdditionHandlerTest {
	private CUIAdditionHandler<ApplicationRequest, ApplicationResponse> cuiAdditionHandler;
	private ApplicationRequest request = new ApplicationRequest(new DiameterRequest());
	private ApplicationResponse response = new ApplicationResponse(request.getDiameterRequest());
	private AccountData accountData = new AccountData(); 
	
	@BeforeClass
	public static void setUp() {
		DummyDiameterDictionary.getInstance();
	}
	
	public class None_Option {

		@Before
		public void setUp() {
			ChargeableUserIdentityConfiguration data = new ChargeableUserIdentityConfiguration();
			data.setCui(AAAServerConstants.NONE);
			cuiAdditionHandler = new CUIAdditionHandler<ApplicationRequest, ApplicationResponse>(data);
		}
		
		public class RequestContainsCUI {
			
			private IDiameterAVP cuiAvp;

			@Before
			public void setUp() {
				cuiAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CUI);
				request.getDiameterRequest().addAvp(cuiAvp);
			}
			
			public class SubscriberProfileIsPresent {
				
				@Before
				public void setUp() {
					request.setAccountData(accountData);
					request.setParameter(AAAServerConstants.CUI_KEY, "user");
				}
				
				@Test
				public void testHandleRequest_MustAddProfileCuiAsCuiAVPInResponse_IfProfileCuiIsPresent_AccordingToSepcification() {
					accountData.setCUI("cui");
					
					cuiAdditionHandler.handleRequest(request, response, null);
					
					assertCuiIsPresentInResponseWithValue("cui");
				}

				@Test
				public void testHandleRequest_MustAddProfileGroupAsCuiAVPInResponse_IfProfileCuiIsNotPresentAndProfileGroupIsPresent_AccordingToSepcification() {
					accountData.setGroupName("group");
					
					cuiAdditionHandler.handleRequest(request, response, null);
					
					assertCuiIsPresentInResponseWithValue("group");
				}
				
				@Test
				public void testHandleRequest_MustAddUserIdentityAsCuiAVPInResponse_IfNeitherOfProfileCuiOrProfileGroupIsPresent_AccordingToSepcification() {
					cuiAdditionHandler.handleRequest(request, response, null);
					
					assertCuiIsPresentInResponseWithValue("user");
				}
			}
			
			/* FIXME NARENDRA - this situation should never really occur in production. So this test should be removed
			 * with production code that tests for it.
			 */
			public class SubscriberProfileIsAbsent {
				
				@Before
				public void setUp() {
					request.setParameter(AAAServerConstants.CUI_KEY, "user");
				}
				
				@Test
				public void testHandleRequest_WillAddUserIdentity_IfSubscriberProfileIsNotPresent() {
					cuiAdditionHandler.handleRequest(request, response, null);
					
					assertCuiIsPresentInResponseWithValue("user");
				}
			}
		}
		
		public class RequestDoesNotContainCUI {
			
			@Test
			public void testHandle_MustNotAddCUIInResponse_AccordingToSpecification() {
				cuiAdditionHandler.handleRequest(request, response, null);
				
				assertCuiIsNotPresentIn(response);
			}

		}
	}
	
	private void assertCuiIsNotPresentIn(ApplicationResponse response) {
		IDiameterAVP responseCuiAvp = response.getAVP(DiameterAVPConstants.CUI);
		assertNull(responseCuiAvp);
	}
	
	private void assertCuiIsPresentInResponseWithValue(String value) {
		IDiameterAVP responseCuiAvp = response.getAVP(DiameterAVPConstants.CUI);
		assertNotNull(responseCuiAvp);
		assertEquals(value, responseCuiAvp.getStringValue());
	}
}
