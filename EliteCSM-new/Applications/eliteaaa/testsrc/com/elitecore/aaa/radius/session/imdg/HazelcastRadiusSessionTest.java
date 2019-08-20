package com.elitecore.aaa.radius.session.imdg;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.base.RadAcctRequestBuilder;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.session.SessionEventListener;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession.SessionStatus;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class HazelcastRadiusSessionTest {

	private static final int RADIUS_SERIALIZATION_ID = 3;
	private static final String SESSION_ID = "session1";
	private static IMap<String, HazelcastRadiusSession> radiusSessionMap;
	private static HazelcastInstance hazelcastInstance;
	private HazelcastRadiusSession createdSession;
	private RadAuthRequest authRequest;
	private RadAcctRequest acctRequest;
	private RadAuthResponse authResponse;
	private RadAcctResponse acctResponse;

	@BeforeClass
	public static void setUpBeforeClass() {
		Config config = new Config();
		config.getSerializationConfig().addDataSerializableFactory(HazelcastRadiusSessionSerializationFactory.FACTORY_ID, 
				new HazelcastRadiusSessionSerializationFactory(null,new ArrayList<>(), new ArrayList<>()));
		hazelcastInstance = Hazelcast.newHazelcastInstance(config);
		radiusSessionMap = hazelcastInstance.getMap("default");
		
	}
	
	@Before
	public void setUp() {
		createdSession = new HazelcastRadiusSession(SESSION_ID, TimeSource.systemTimeSource(), null, new ArrayList<>(), new ArrayList<>());
	}

	
	@Test
	public void serializeAndDeserializeSessionWithoutParameters() {
		
		radiusSessionMap.put(createdSession.getSessionId(), createdSession);

		HazelcastRadiusSession deserializedSession = radiusSessionMap.get(createdSession.getSessionId());

		assertReflectionEquals(deserializedSession, createdSession);
	}

	@Test
	public void serializeAndDeserializeSessionWithParameters() {
		HazelcastRadiusSession originalSession = new HazelcastRadiusSession(SESSION_ID, TimeSource.systemTimeSource(), null, new ArrayList<>(), new ArrayList<>());
		originalSession.setParameter("key1", "value1");
		originalSession.setParameter("key2", 10000);
		originalSession.setParameter("key3", System.nanoTime()); // just to add a long value

		radiusSessionMap.put(originalSession.getSessionId(), originalSession);

		HazelcastRadiusSession deserializedSession = radiusSessionMap.get(originalSession.getSessionId());

		assertReflectionEquals(deserializedSession, originalSession);
	}
	
	@Test
	public void serializationIdIsThree() {
		HazelcastRadiusSession originalSession = new HazelcastRadiusSession(null, TimeSource.systemTimeSource(), null, new ArrayList<>(), new ArrayList<>());
		assertThat(RADIUS_SERIALIZATION_ID, is(originalSession.getId()));
		
	}

	@After
	public void cleanUp() {
		radiusSessionMap.remove(SESSION_ID);
	}

	public class RadiusSessionStatus {
		
		@Before
		public void setUP() {
			createdSession = new HazelcastRadiusSession(SESSION_ID, TimeSource.systemTimeSource(), new DummySessionEventListener(), new ArrayList<>(), new ArrayList<>());
		}

		@Test
		public void IsInactiveWhenPacketTypeIsAccessRequest() throws InvalidAttributeIdException {
			setPacketAsAccessRequest();
			setAuthResponse();
			createdSession.update(new RadiusSessionDataValueProvider(authRequest, authResponse));
			assertThat(createdSession.getSessionStatus(), is(SessionStatus.INACTIVE));
		}

		public void setPacketAsAccessRequest() throws InvalidAttributeIdException {
			authRequest = new RadAuthRequestBuilder()
					.addAttribute("5535:44", "session2")
					.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test")
					.packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE)
					.build();
		}
		
		public void setAuthResponse() {
			authResponse = new RadAuthRequestBuilder().buildResponse();
		}

		@Test
		public void IsActiveWhenPacketTypeIsNotAccessRequest() throws InvalidAttributeIdException, UnknownHostException {
			setPacketAsAccountingRequest();
			setAcctResponse();
			createdSession.update(new RadiusSessionDataValueProvider(acctRequest, acctResponse));
			assertThat(createdSession.getSessionStatus(), is(SessionStatus.ACTIVE));
		}

		public void setPacketAsAccountingRequest() throws InvalidAttributeIdException {
			acctRequest = new RadAcctRequestBuilder()
					.addAttribute("5535:44", "session2")
					.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test")
					.packetType(RadiusConstants.ACCOUNTING_REQUEST_MESSAGE)
					.build();

		}
		
		public void setAcctResponse() throws UnknownHostException {
			acctResponse = new RadAcctRequestBuilder().buildResponse(acctRequest);
		}

	}
	
	private class DummySessionEventListener implements SessionEventListener {

		@Override
		public boolean removeSession(ISession session) {
			return false;
		}

		@Override
		public void update(ISession session) {
			LogManager.getLogger().info("HazelcastRadiusSessionTest", "Session updated succesfully");
		}
		
	}
	
	@AfterClass
	public static void afterTest() {
		hazelcastInstance.shutdown();
	}

}
