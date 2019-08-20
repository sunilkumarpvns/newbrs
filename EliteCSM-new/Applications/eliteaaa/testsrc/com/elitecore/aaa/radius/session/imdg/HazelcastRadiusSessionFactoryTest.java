package com.elitecore.aaa.radius.session.imdg;

import static com.elitecore.diameterapi.diameter.common.session.imdg.DummyIMDGConfiguration.INSTANCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession.IndexField;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.diameterapi.diameter.common.session.imdg.DummyIMDGConfiguration;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class HazelcastRadiusSessionFactoryTest {

	private static final String SESSION1_ID1 = "id1";
	private static final String SESSION2_ID2 = "id2";
	private static final String SESSION3_ID3 = "id3";
	private HazelcastImdgInstance hazelCastInstance;
	private HazelcastRadiusSessionFactory sessionFactory;
	private FixedTimeSource timeSource;
	private static List<ImdgIndexDetail> radiusIndexMappingList;

	@Mock public ServerContext serverContext;

	@Rule public ExpectedException exception = ExpectedException.none();
	private RadiusSessionDataValueProvider sessionDataValueProvider; 

	@BeforeClass 
	public static void setImdgRadiusIndexFieldMappingList() {
		radiusIndexMappingList = new ArrayList<>();
		
		ImdgIndexDetail usernameMapping = new ImdgIndexDetail();
		usernameMapping.setImdgFieldValue(RadiusAttributeConstants.USER_NAME_STR);
		usernameMapping.setAttributeList(new ArrayList<>(Arrays.asList("0:1")));
		usernameMapping.setImdgIndex("index0");
		radiusIndexMappingList.add(usernameMapping);
		
		ImdgIndexDetail framedIpAddressMapping = new ImdgIndexDetail();
		framedIpAddressMapping.setImdgFieldValue(RadiusAttributeConstants.FRAMED_IP_ADDRESS_STR);
		framedIpAddressMapping.setAttributeList(new ArrayList<>(Arrays.asList("0:8")));
		framedIpAddressMapping.setImdgIndex("index1");
		radiusIndexMappingList.add(framedIpAddressMapping);
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		timeSource = new FixedTimeSource(System.currentTimeMillis());
		hazelCastInstance  = new HazelcastImdgInstance(serverContext, new DummyIMDGConfiguration(), INSTANCE_NAME);
		sessionFactory = new HazelcastRadiusSessionFactory(hazelCastInstance, timeSource, radiusIndexMappingList, new ArrayList<>());
		hazelCastInstance.start();
		
		createSessionDataValueProvider();
	}
	
	private void createSessionDataValueProvider() throws InvalidAttributeIdException {
		RadAuthRequest request = new RadAuthRequestBuilder()
					.addAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS_STR,"10.10.10.10")
					.buildRequest();
		RadAuthResponse response = new RadAuthRequestBuilder().buildResponse();
		this.sessionDataValueProvider = new RadiusSessionDataValueProvider(request, response);
	}
	

	@Test
	public void createsSessionUsingSessionId() throws InvalidAttributeIdException {

		sessionFactory.getOrCreateSession(SESSION1_ID1).update(sessionDataValueProvider);

		assertThat(sessionFactory.hasSession(SESSION1_ID1), is(true));
	}
	
	@Test
	public void updateIsRequiredToAddCreatedSessionInIMDG() {

		sessionFactory.getOrCreateSession(SESSION1_ID1).update(sessionDataValueProvider);

		assertThat(sessionFactory.hasSession(SESSION1_ID1), is(true));
		
		sessionFactory.getOrCreateSession(SESSION2_ID2).update(sessionDataValueProvider);

		assertThat(sessionFactory.hasSession(SESSION2_ID2), is(true));
	}

	@Test
	public void returnsTotalNumberOfSessionCount() {
		sessionFactory.getOrCreateSession(SESSION1_ID1).update(sessionDataValueProvider);
		sessionFactory.getOrCreateSession(SESSION2_ID2).update(sessionDataValueProvider);

		assertThat(sessionFactory.getSessionCount(), is(2));
	}

	@Test
	public void sessionCanBeRetrivedUsingValuesOfIndexFields() throws InvalidAttributeIdException {
		ISession session = sessionFactory.getOrCreateSession(SESSION1_ID1);
		session.update(sessionDataValueProvider);

		Collection<String> sessionUsingFramedIpAddress = sessionFactory
				.search(IndexField.INDEX_1.getIndex(), "10.10.10.10");
		assertThat(SESSION1_ID1, is(sessionUsingFramedIpAddress.iterator().next()));
	}
	
	@Test
	public void returnsEmptyCollectionIfSessionDoesNotExistForGivenAttribute() {
		
		Collection<String> emptyCollection = sessionFactory
				.search(IndexField.INDEX_1.getIndex(), "20.20.20.20");
		
		assertTrue(emptyCollection.isEmpty());
	}

	public class SessionGetsRemoved {

		@Test
		public void IfexistInTheMap() {
			ISession createdSession = sessionFactory.getOrCreateSession(SESSION1_ID1);

			sessionFactory.removeSession(createdSession);

			assertThat(sessionFactory.hasSession(SESSION1_ID1), is(false));

		}

		@Test
		public void usingSessionId() {
			sessionFactory.getOrCreateSession(SESSION1_ID1).update(sessionDataValueProvider);
			sessionFactory.getOrCreateSession(SESSION2_ID2).update(sessionDataValueProvider);
			
			sessionFactory.getOrCreateSession(SESSION1_ID1);
			sessionFactory.removeSession(SESSION1_ID1);

			assertThat(sessionFactory.hasSession(SESSION1_ID1), is(false));
			assertThat(sessionFactory.hasSession(SESSION2_ID2), is(true));

		}

		@Test
		public void ifItsLastAccessedTimeIsGreaterThanSessionTimeout() throws InterruptedException {
			int lastAccessedTime= 1001;
			int sessionTimeOut = 1000;
			
			sessionFactory.getOrCreateSession(SESSION1_ID1).update(sessionDataValueProvider);

			assertThat(sessionFactory.hasSession(SESSION1_ID1), is(true));

			timeSource.advance(lastAccessedTime);
			
			sessionFactory.removeIdleSession(sessionTimeOut);

			assertThat(sessionFactory.hasSession(SESSION1_ID1), is(false));
		}
		
		@Test
		public void ifItsLastAccessedTimeIsEqualsToSessionTimeout() throws InterruptedException {
			int lastAccessedTime= 1000;
			int sessionTimeOut = 1000;
			
			sessionFactory.getOrCreateSession(SESSION1_ID1).update(sessionDataValueProvider);

			assertThat(sessionFactory.hasSession(SESSION1_ID1), is(true));

			timeSource.advance(lastAccessedTime);
			
			sessionFactory.removeIdleSession(sessionTimeOut);

			assertThat(sessionFactory.hasSession(SESSION1_ID1), is(false));
		}
		
		@Test
		public void removeAllWillRemovesAllUnlockedSession() throws InvalidAttributeIdException {
			sessionFactory.getOrCreateSession(SESSION1_ID1).update(sessionDataValueProvider);
			sessionFactory.getOrCreateSession(SESSION2_ID2).update(sessionDataValueProvider);
			sessionFactory.getOrCreateSession(SESSION3_ID3).update(sessionDataValueProvider);
			
			sessionFactory.getOrCreateSession(SESSION3_ID3);
			
			assertThat(sessionFactory.removeAllSessions(), is(2));

			assertThat(sessionFactory.getSessionCount(), is(1));

			assertThat(sessionFactory.hasSession(SESSION1_ID1), is(false));
			assertThat(sessionFactory.hasSession(SESSION2_ID2), is(false));

			assertThat(sessionFactory.hasSession(SESSION3_ID3), is(true));

		}

	} 

	@After
	public void after() {
		hazelCastInstance.stop();
	}

}

