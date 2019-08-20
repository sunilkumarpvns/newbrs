package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.DatabaseDSConfiguration;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.server.axixserver.IDiameterWSRequestHandler;
import com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable;
import com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.DBFailureActions;
import com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.DiameterConcurrencyConfigurationData;
import com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.SessionOverrideActions;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.radius.sessionx.ConcurrentPolicyConstants;
import com.elitecore.aaa.radius.sessionx.conf.ConcurrentLoginPolicyConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyConfigurable;
import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyData;
import com.elitecore.aaa.radius.sessionx.conf.impl.ServceWiseLogin;
import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.Session;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionFactory;
import com.elitecore.core.serverx.sessionx.criterion.Criterion;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;


@RunWith(HierarchicalContextRunner.class)
public class DiameterConcurrencyHandlerTest {

	private static final String SESSION_ID_VALUE = "s1";


	private static final String INVALID_MODE = "INVALID_MODE";
	private static final String USERNAME_VALUE = "someuser";


	@Mock private DiameterServiceContext serviceContext;
	@Mock private AAAServerConfiguration aaaServerConfiguration;
	@Mock private ConcurrentLoginPolicyConfiguration concurrentLoginPolicyConfiguration;
	@Mock private DatabaseDSConfiguration databaseDSConfiguration;
	@Mock private AAAServerContext aaaServerContext;
	@Mock private IDiameterWSRequestHandler diameterWSRequestHandler;
	@Mock private EliteAAAServiceExposerManager eliteAAAServiceExposerManager;

	private DiameterConcurrencyConfigurable configurable = new DiameterConcurrencyConfigurable();
	private FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
	private DiameterConcurrencyConfigurationData diameterConcurrencyConfigurationData;
	private SessionFactory mockSessionFactory;
	private Session session;
	private DiameterRequest diameterRequest;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterConcurrencyHandlerSessionFactoryInjectingStub diameterConcurrencyHandler;
	private List<ConcurrentLoginPolicyData> policyData;
	private Criteria criteria;


	private ArrayList<Criterion> criterions;


	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws JAXBException, FileNotFoundException, ClassNotFoundException, DataSourceException, DatabaseInitializationException, DatabaseTypeNotSupportedException, UnsupportedEncodingException {
		MockitoAnnotations.initMocks(this);

		readConcurrencyConfigurationData();

		readPolicyData();

		Mockito.when(serviceContext.getServerContext()).thenReturn(aaaServerContext);
		Mockito.when(aaaServerContext.getServerConfiguration()).thenReturn(aaaServerConfiguration);
		Mockito.when(aaaServerConfiguration.getConcurrentLoginPolicyConfiguration()).thenReturn(concurrentLoginPolicyConfiguration);
		Mockito.when(aaaServerConfiguration.getDiameterConcurrencyConfigurable()).thenReturn(configurable);
		Mockito.when(concurrentLoginPolicyConfiguration.getConcurrentLoginPolicy(Mockito.anyString())).thenReturn(policyData.get(0));
		Mockito.when(aaaServerConfiguration.getDatabaseDSConfiguration()).thenReturn(databaseDSConfiguration);

		createSessionFactory();
		setUpRequestAndResponse();

		diameterConcurrencyHandler = new DiameterConcurrencyHandlerSessionFactoryInjectingStub(serviceContext);
	}

	private void setUpRequestAndResponse() {
		diameterRequest = new DiameterRequest();
		diameterRequest.addAvp(DiameterAVPConstants.EC_CONCURRENT_LOGIN_POLICY_NAME, "dia_con");
		diameterRequest.addAvp(DiameterAVPConstants.SESSION_ID, SESSION_ID_VALUE);
		diameterRequest.addAvp(DiameterAVPConstants.USER_NAME, USERNAME_VALUE);
		diameterRequest.addAvp(DiameterAVPConstants.EC_PROFILE_USER_GROUP,"elite");
		diameterRequest.addAvp(DiameterAVPConstants.SERVICE_TYPE, "1");
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
	}

	private void createSessionFactory() {
		SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
		session = Mockito.mock(Session.class);
		criteria = Mockito.mock(Criteria.class);
		Mockito.when(session.createCriteria(Mockito.anyString())).thenReturn(criteria);
		Mockito.when(sessionFactory.getSession()).thenReturn(session);
		Mockito.when(session.list(criteria)).thenReturn(getSessionData());
		this.mockSessionFactory = sessionFactory;
		this.criterions = new ArrayList<Criterion>();
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				criterions.add(invocation.getArgumentAt(0, Criterion.class));
				return null;
			}
		}).when(criteria).add(any(Criterion.class));
	}

	private void readPolicyData() throws FileNotFoundException, JAXBException, UnsupportedEncodingException {
		File policyDataFile = new File(ClasspathResource.at("system/_sys.concurrentloginpolicy").getAbsolutePath());
		ConcurrentLoginPolicyConfigurable policyConfigurable = ConfigUtil.deserialize(policyDataFile, ConcurrentLoginPolicyConfigurable.class);
		policyConfigurable.postRead();
		policyData = policyConfigurable.getConcurrentLoginPolicies();
	}

	private void readConcurrencyConfigurationData() throws JAXBException, UnsupportedEncodingException, FileNotFoundException{
		File file = new File(ClasspathResource.at("conf/db/diameter-concurrency-conf.xml").getAbsolutePath());
		configurable = ConfigUtil.deserialize(file, DiameterConcurrencyConfigurable.class);
		configurable.postReadProcessing();
		diameterConcurrencyConfigurationData = configurable.getConfigurationList().get(0);
	}

	private void setUpSessionData() {
		Mockito.when(session.list(Mockito.<Criteria>any())).thenReturn(getSessionData());
	}


	@Test
	public void rejectsResponseWhenPolicyDataNotFound() throws Exception {
		diameterConcurrencyHandler.init();

		when(concurrentLoginPolicyConfiguration.getConcurrentLoginPolicy("dia_con")).thenReturn(null);

		diameterConcurrencyHandler.handleRequest(request, response, null);

		assertThat(response.getDiameterAnswer())
		.hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED)
		.containsAVP(DiameterAVPConstants.ERROR_MESSAGE, DiameterErrorMessageConstants.CONCURRENT_LOGIN_POLICY_NOT_FOUND);
	}

	@Test
	public void rejectsResponseWhenPolicyDataModeIsInvalid() throws Exception {
		setPolicyMode(INVALID_MODE);
		diameterConcurrencyHandler.init();

		diameterConcurrencyHandler.handleRequest(request, response, null);

		assertThat(response.getDiameterAnswer())
		.hasResultCode(ResultCode.DIAMETER_MISSING_AVP)
		.containsAVP(DiameterAVPConstants.ERROR_MESSAGE, DiameterErrorMessageConstants.CONCURRENCY_FAILED);
	}

	public class GeneralPolicy {

		@Before
		public void setUp() {
			setPolicyMode(ConcurrentPolicyConstants.GENERAL_POLICY);
			setUpSessionData();
		}

		public class WithIndividualType {

			@Before
			public void setUp() {
				setPolicyType(ConcurrentPolicyConstants.INDIVIDUAL_POLICY);
			}
			
			@Test
			public void rejectsResponseWhenMaxLoginLimitReached() throws Exception {
				setMaximumLoginLimit(1);

				diameterConcurrencyHandler.init();
				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertConcurrentLimitReachedResponse();
			}

			@Test
			public void leavesResponseUnchangedIfMaximumLoginLimitIsNotReached() throws Exception {
				setMaximumLoginLimit(2);

				diameterConcurrencyHandler.init();

				byte[] expected = response.getDiameterAnswer().getBytes();
				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertArrayEquals(expected, response.getDiameterAnswer().getBytes());
			}
		}

		public class WithGroupType {

			@Before
			public void setUp() {
				setPolicyType(ConcurrentPolicyConstants.GROUP_POLICY);
			}

			@Test 
			public void addsClassAVPWithValueGroupNameToResponse() throws Exception {
				setMaximumLoginLimit(1);

				diameterConcurrencyHandler.init();

				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer()).containsAVP(DiameterAVPConstants.CLASS,"concr_grpname=elite");

			}

			@Test
			public void rejectResponseWhenMaxLoginLimitReached() throws Exception {
				setMaximumLoginLimit(1);

				diameterConcurrencyHandler.init();

				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer())
				.hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED)
				.containsAVP(DiameterAVPConstants.ERROR_MESSAGE, DiameterErrorMessageConstants.MAX_LOGIN_LIMIT_REACHED);
			}

			@Test
			public void doesnNotRejectResponse() throws Exception {
				setMaximumLoginLimit(2);

				diameterConcurrencyHandler.init();
				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer())
				.doesNotContainAVP(DiameterAVPConstants.ERROR_MESSAGE);
			}
		}

	}

	private void assertConcurrentLimitReachedResponse() {
		assertThat(response.getDiameterAnswer())
		.hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED)
		.containsAVP(DiameterAVPConstants.ERROR_MESSAGE, DiameterErrorMessageConstants.MAX_LOGIN_LIMIT_REACHED);
	}

	public class ServiceWisePolicy {

		ServceWiseLogin serviceWiseLogin;

		@Before
		public void setUp() {
			setUpSessionData();
			Mockito.when(concurrentLoginPolicyConfiguration.getConcurrentLoginPolicy(Mockito.anyString())).thenReturn(policyData.get(0));
			setPolicyMode(ConcurrentPolicyConstants.SERVICE_WISE_POLICY);
			setMaximumLoginLimit(2);
			setServiceWiseConfiguration();
		}

		private void setServiceWiseConfiguration() {
			serviceWiseLogin = new ServceWiseLogin();
			policyData.get(0).setServiceType(DiameterAVPConstants.SERVICE_TYPE);
			serviceWiseLogin.setMaxServiceWiseSessions(1l);
			serviceWiseLogin.setServiceTypeValue("1");
			List<ServceWiseLogin> data = new ArrayList<ServceWiseLogin>();
			data.add(serviceWiseLogin);
			policyData.get(0).setServiceWiseLoginList(data);
		}

		public class WithInidividualType {

			@Before
			public void setUp() {
				setPolicyType(ConcurrentPolicyConstants.INDIVIDUAL_POLICY);
			}

			@Test
			public void rejectResponseWhenReachesToMaxLoginLimitForTheConfiguredService() throws Exception {
				diameterConcurrencyHandler.init();
				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer())
				.hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED)
				.containsAVP(DiameterAVPConstants.ERROR_MESSAGE, DiameterErrorMessageConstants.MAX_LOGIN_LIMIT_REACHED)
				.containsAVP(DiameterAVPConstants.CLASS, "concr_grpname=someuser" + ";loginpolicy=" + policyData.get(0).getName());
			}

			@Test
			public void doesnNotRejectResponseMaximumLoginLimitIsNotReached() throws Exception {
				setMaximumLoginLimit(2);
				serviceWiseLogin.setMaxServiceWiseSessions(3L);

				diameterConcurrencyHandler.init();
				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer()).containsAVP(DiameterAVPConstants.CLASS,"concr_grpname=someuser;loginpolicy=con_login_policy")
				.doesNotContainAVP(DiameterAVPConstants.ERROR_MESSAGE);
			}
		}


		public class WithGroupType {

			@Before
			public void setUp() {
				setPolicyType(ConcurrentPolicyConstants.GROUP_POLICY);
			}
			
			@Test
			public void addsClassAVPWithValueGroupNameAndLogingPolicyToResponse() throws Exception {
				diameterConcurrencyHandler.init();
				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer())
				.containsAVP(DiameterAVPConstants.CLASS, "concr_grpname=elite" + ";loginpolicy=" + policyData.get(0).getName());

			}

			@Test 
			public void rejectsResponseWhenReachesToMaxLoginLimitForTheConfiguredService() throws Exception {				getSessionData().get(0).addValue("0:6","elite");
				Mockito.when(session.list(Mockito.<Criteria>any())).thenReturn(getSessionData());

				diameterConcurrencyHandler.init();
				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer())
				.hasResultCode(ResultCode.DIAMETER_AUTHORIZATION_REJECTED)
				.containsAVP(DiameterAVPConstants.ERROR_MESSAGE, DiameterErrorMessageConstants.MAX_LOGIN_LIMIT_REACHED)
				.containsAVP(DiameterAVPConstants.CLASS, "concr_grpname=elite" + ";loginpolicy=" + policyData.get(0).getName());
			}

			@Test
			public void doesNotRejectTheResponseWhenMaximumLoginLimitIsNotReached() throws Exception {
				setMaximumLoginLimit(5);
				serviceWiseLogin.setMaxServiceWiseSessions(3L);

				diameterConcurrencyHandler.init();
				diameterConcurrencyHandler.handleRequest(request, response, null);

				assertThat(response.getDiameterAnswer()).containsAVP(DiameterAVPConstants.CLASS,"concr_grpname=elite;loginpolicy=con_login_policy")
				.doesNotContainAVP(DiameterAVPConstants.ERROR_MESSAGE);
			}
		}
	}

	public class SessionOverrideEnabled {

		private SettableFuture<DiameterAnswer> futureResponse;
		private DiameterAnswer diameterASA;

		@Before
		public void setUpDataForSessionOverriding() throws Exception {
			futureResponse = SettableFuture.create();
			diameterConcurrencyConfigurationData.setSessionOverrideAction(SessionOverrideActions.GENREATE_ASR);
			diameterConcurrencyConfigurationData.setSessionOverrideFields("SUBSCRIBER_ID");
			Mockito.doReturn(futureResponse).when(eliteAAAServiceExposerManager).sendSessionDisconnect(Mockito.any(ProtocolType.class),Mockito.anyString(), Mockito.any(DiameterRequest.class));
			Mockito.when(serviceContext.getServerContext().getTaskScheduler()).thenReturn(taskScheduler);
		}

		@Test 
		public void generatesASRWhenUserSessionExists() throws Exception {
			diameterConcurrencyHandler.init();

			diameterConcurrencyHandler.handleRequest(request, response, null);

			verify(eliteAAAServiceExposerManager).sendSessionDisconnect(eq(ProtocolType.DIAMETER), anyString(), any(DiameterRequest.class));
		}

		@Test
		public void removesTheSessionWhenNegativeAnswerIsRecievedForASR() throws Exception {
			diameterConcurrencyHandler.init();
			diameterConcurrencyHandler.handleRequest(request, response, null);

			onNegativeResponse();

			verify(session).delete(Mockito.any(Criteria.class));
		}

		private void onNegativeResponse() {
			setNegativeASA();
			futureResponse.set(diameterASA);
		}

		@Test
		public void doesNotRemoveTheSessionWhenSuccessAnswerIsRecievedForASR() throws com.elitecore.core.commons.InitializationFailedException, InterruptedException, ExecutionException {
			diameterConcurrencyHandler.init();
			diameterConcurrencyHandler.handleRequest(request, response, null);

			onPositiveAnswer();

			verify(session, never()).delete(any(Criteria.class));
		}

		private void onPositiveAnswer() {
			setPostiveASA();
			futureResponse.set(diameterASA);
		}

		private void setPostiveASA() {
			diameterASA = new DiameterAnswer();
			diameterASA.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_SUCCESS.code);
			diameterASA.addAvp(DiameterAVPConstants.SESSION_ID, SESSION_ID_VALUE);
		}

		private void setNegativeASA() {
			diameterASA = new DiameterAnswer();
			diameterASA.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_UNKNOWN_SESSION_ID.code);
			diameterASA.addAvp(DiameterAVPConstants.SESSION_ID,SESSION_ID_VALUE);
		}
	}

	public class DBFailure {

		@Test
		public void ignoresRequestWhenDbFailureActionIsIgnore() throws com.elitecore.core.commons.InitializationFailedException {
			ResultCode expectedResultCode = ResultCode.DIAMETER_SUCCESS;

			response.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, expectedResultCode.code);

			setDBFailureAction(DBFailureActions.IGNORE);

			diameterConcurrencyHandler.init();

			dbGoesDown();

			diameterConcurrencyHandler.handleRequest(request, response, null);

			assertThat(response.getDiameterAnswer()).hasResultCode(expectedResultCode);
		}

		@Test
		public void rejectsRequestWhenDbFailureActionIsReject() throws InitializationFailedException, com.elitecore.core.commons.InitializationFailedException{
			setDBFailureAction(DBFailureActions.REJECT);
			diameterConcurrencyHandler.init();

			dbGoesDown();

			diameterConcurrencyHandler.handleRequest(request, response, null);

			assertRequestIsRejected();
		}

		private void assertRequestIsRejected() {
			assertFalse(response.isFurtherProcessingRequired());
			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_AUTHENTICATION_REJECTED);
			assertTrue(response.isProcessingCompleted());
		}

		@Test
		public void hintsSystemToDropRequestWhenDbFailureActionIsDrop() throws com.elitecore.core.commons.InitializationFailedException {
			setDBFailureAction(DBFailureActions.DROP);
			diameterConcurrencyHandler.init();

			dbGoesDown();

			diameterConcurrencyHandler.handleRequest(request, response, null);

			requestIsDroped();
		}

		private void requestIsDroped() {
			assertFalse(response.isFurtherProcessingRequired());
			assertTrue(response.isMarkedForDropRequest());
		}

		private void setDBFailureAction(DBFailureActions action) {
			diameterConcurrencyConfigurationData.setDBFailureAction(action);
		}

		private void dbGoesDown() {
			Mockito.when(session.list(Mockito.<Criteria>any())).thenReturn(null);
		}
	}

	private List<SessionData> getSessionData() {
		SessionData sessionData = new SessionDataImpl("diaconcurrencysessiondata");
		sessionData.addValue("21067:65539","1");
		sessionData.addValue("0:6", "1");
		sessionData.addValue("0:1", USERNAME_VALUE);
		sessionData.addValue("GROUPNAME", "elite");
		return Arrays.asList(sessionData);
	}

	private void setPolicyType(String type) {
		policyData.get(0).setPolicyType(type);
	}

	private void setPolicyMode(String mode) {
		policyData.get(0).setPolicyMode(mode);
	}

	private void setMaximumLoginLimit(long limit) {
		policyData.get(0).setMaxLogins(limit);
	}

	private class DiameterConcurrencyHandlerSessionFactoryInjectingStub extends DiameterConcurrencyHandler<ApplicationRequest, ApplicationResponse> {

		private static final String DIAMETER_CONFIG_ID = "20";

		public DiameterConcurrencyHandlerSessionFactoryInjectingStub(
				DiameterServiceContext serviceContext) {
			super(serviceContext, DIAMETER_CONFIG_ID, eliteAAAServiceExposerManager );
		}

		@Override
		protected SessionFactory createSessionFactory()
				throws com.elitecore.core.commons.InitializationFailedException {
			return mockSessionFactory;
		}
	}
}