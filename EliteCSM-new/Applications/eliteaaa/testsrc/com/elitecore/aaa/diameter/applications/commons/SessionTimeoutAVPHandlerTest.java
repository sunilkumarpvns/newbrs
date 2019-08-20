package com.elitecore.aaa.diameter.applications.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class SessionTimeoutAVPHandlerTest {

	private static final int ANY_VALUE = 100;
	private static final long ANY_OTHER_VALUE = 200;

	private SessionTimeoutAVPHandler<ApplicationRequest, ApplicationResponse> sessionTimeoutHandler;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private FixedTimeSource timeSource;

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() {
		timeSource = new FixedTimeSource();
		sessionTimeoutHandler = new SessionTimeoutAVPHandler<ApplicationRequest, ApplicationResponse>(timeSource);
		request = new ApplicationRequest(
				new DiameterRequest());
		response = new ApplicationResponse(
				request.getDiameterRequest());
	}

	public class CustomerProfileIsNotPresent {
		@Test
		public void testHandleRequest_ShouldRemoveAllSessionTimeoutAttributes_IfSessionTimeoutIsDisabled_UsingAnyValueLessThanZero() {
			request.setParameter(AAAServerConstants.SESSION_TIMEOUT, AAAServerConstants.SESSION_TIMEOUT_DISABLED);

			response.addAVP(newSessionTimeoutAVP(ANY_VALUE));
			response.addInfoAvp(newSessionTimeoutAVP(ANY_VALUE));
			response.addInfoAvp(newSessionTimeoutAVP(ANY_OTHER_VALUE));

			sessionTimeoutHandler.handleRequest(request, response, null);

			assertNull(response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true));
		}

		@Test
		public void testHandleRequest_ShouldKeepAVPWithMinimumSessionTimeAndRemoveOthers() {
			response.addInfoAvp(newSessionTimeoutAVP(200));
			response.addAVP(newSessionTimeoutAVP(100));
			response.addInfoAvp(newSessionTimeoutAVP(300));

			sessionTimeoutHandler.handleRequest(request, response, null);

			IDiameterAVP sessionTimeout = response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true);
			assertNotNull(sessionTimeout);
			assertEquals(100L, sessionTimeout.getInteger());
		}
		
		@Test
		public void testHandleRequest_ShouldNotAddSessionTimeoutAVP_IfNoSessionTimeoutAVPIsPresentInAnswer() {
			sessionTimeoutHandler.handleRequest(request, response, null);
			
			assertNull(response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true));
		}
	}

	public class CustomerProfileIsPresent {
		AccountData accountData;

		@Before
		public void setUp() {
			accountData = new AccountData();
			request.setAccountData(accountData);
		}

		public class CustomerProfileExpiryPreconditionsAreMet {

			@Before
			public void setUp() {
				accountData.setExpiryDateCheckRequired(true);
				accountData.setGracePeriodApplicable(false);
				accountData.setExpiryDate(anyNonNullDate());
			}

			private Date anyNonNullDate() {
				return Calendar.getInstance().getTime();
			}
			
			@Test
			public void testHandleRequest_ShouldNotAddSessionTimeoutAVP_IfNoSessionTimeoutAVPIsPresentInAnswer() {
				sessionTimeoutHandler.handleRequest(request, response, null);
				
				assertNull(response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true));
			}
			
			@Test
			public void testHandleRequest_ShonullessionTimeoutValueAsMinimumOfSessionTimeoutAVPs_IfItIsTheLowest() {
				accountData.setMaxSessionTime(200);
				setExpiryTimeInSeconds(accountData, 199);

				response.addAVP(newSessionTimeoutAVP(150));
				response.addInfoAvp(newSessionTimeoutAVP(201));
				response.addInfoAvp(newSessionTimeoutAVP(300));

				sessionTimeoutHandler.handleRequest(request, response, null);

				IDiameterAVP sessionTimeout = response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true);
				assertNotNull(sessionTimeout);
				assertEquals(150L, sessionTimeout.getInteger());
			}

			@Test
			public void testHandleRequest_ShouldSetSessionTimeoutValueAsRemainingAccountExpiryTime_IfItIsTheLowest() {
				accountData.setMaxSessionTime(150);
				setExpiryTimeInSeconds(accountData, 130);

				response.addAVP(newSessionTimeoutAVP(200));
				response.addInfoAvp(newSessionTimeoutAVP(300));

				sessionTimeoutHandler.handleRequest(request, response, null);

				IDiameterAVP sessionTimeout = response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true);
				assertNotNull(sessionTimeout);
				assertEquals(130L, sessionTimeout.getInteger());
			}

			@Test
			public void testHandleRequest_ShouldSetSessionTimeoutValueAsMinimumOfSessionTimeoutAVPs_IfItIsTheLowest_AndMaxSessionTimeIsHigherThanRemainingExpiryTime() {
				accountData.setMaxSessionTime(300);
				setExpiryTimeInSeconds(accountData, 200);

				response.addAVP(newSessionTimeoutAVP(300));
				response.addInfoAvp(newSessionTimeoutAVP(100));
				response.addInfoAvp(newSessionTimeoutAVP(400));

				sessionTimeoutHandler.handleRequest(request, response, null);

				IDiameterAVP sessionTimeout = response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true);
				assertNotNull(sessionTimeout);
				assertEquals(100L, sessionTimeout.getInteger());
			}

			@Test
			public void testHandleRequest_ShouldSetMaxSessionTimeValueFromCustomerProfile_IfItIsTheLowest() {
				accountData.setMaxSessionTime(100);
				setExpiryTimeInSeconds(accountData, 200);
				
				response.addAVP(newSessionTimeoutAVP(101));
				response.addInfoAvp(newSessionTimeoutAVP(102));
				response.addInfoAvp(newSessionTimeoutAVP(300));

				sessionTimeoutHandler.handleRequest(request, response, null);

				IDiameterAVP sessionTimeout = response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true);
				assertNotNull(sessionTimeout);
				assertEquals(100L, sessionTimeout.getInteger());
			}

			@Test
			public void testHandleRequest_ShouldNotSendSessionTimeoutAVP_IfMinOfSessionTimeoutAVPsIsZero() {
				accountData.setMaxSessionTime(100);
				
				setExpiryTimeInSeconds(accountData, 200);
				
				response.addAVP(newSessionTimeoutAVP(0));
				response.addInfoAvp(newSessionTimeoutAVP(100));
				response.addInfoAvp(newSessionTimeoutAVP(300));

				sessionTimeoutHandler.handleRequest(request, response, null);

				IDiameterAVP sessionTimeout = response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true);
				assertNull(sessionTimeout);
			}

			@Test
			public void testHandleRequest_ShouldNotConsiderMaxSessionTime_IfItIsLowerThanZero_AndChooseLowestOfOtherTwo() {
				accountData.setMaxSessionTime(-1);
				
				setExpiryTimeInSeconds(accountData, 200);
				
				response.addAVP(newSessionTimeoutAVP(100));
				response.addInfoAvp(newSessionTimeoutAVP(200));
				response.addInfoAvp(newSessionTimeoutAVP(300));

				sessionTimeoutHandler.handleRequest(request, response, null);

				IDiameterAVP sessionTimeout = response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true);
				assertNotNull(sessionTimeout);
				assertEquals(100L, sessionTimeout.getInteger());
			}
			
			@Test
			public void testHandleRequest_ShouldNotConsiderMaxSessionTime_IfItIsEqualToZero_AndChooseLowestOfOtherTwo() {
				accountData.setMaxSessionTime(0);
				
				setExpiryTimeInSeconds(accountData, 200);
				
				response.addAVP(newSessionTimeoutAVP(100));
				response.addInfoAvp(newSessionTimeoutAVP(200));
				response.addInfoAvp(newSessionTimeoutAVP(300));

				sessionTimeoutHandler.handleRequest(request, response, null);

				IDiameterAVP sessionTimeout = response.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true);
				assertNotNull(sessionTimeout);
				assertEquals(100L, sessionTimeout.getInteger());
			}

		}



		private void setExpiryTimeInSeconds(AccountData accountData, int remainingTimeInSeconds) {
			long currentTime = System.currentTimeMillis();
			Date expiryDate = new Date(currentTime);
			accountData.setExpiryDate(expiryDate);

			timeSource.setCurrentTimeInMillis(currentTime - TimeUnit.SECONDS.toMillis(remainingTimeInSeconds));
		}
	}

	private IDiameterAVP newSessionTimeoutAVP(long sessionTimeoutValue) {
		IDiameterAVP sessionTimeoutAVP = DiameterDictionary.getInstance()
			.getAttribute(DiameterAVPConstants.SESSION_TIMEOUT);
		sessionTimeoutAVP.setInteger(sessionTimeoutValue);
		return sessionTimeoutAVP;
	}
}
