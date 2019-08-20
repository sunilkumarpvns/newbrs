package com.elitecore.diameterapi.core.common.session;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.imdg.ImdgInstanceFailedException;
import com.elitecore.core.imdg.config.IMDGConfiguration;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.diameter.common.session.InMemorySessionFactory;
import com.elitecore.diameterapi.diameter.common.session.imdg.DummyIMDGConfiguration;
import com.elitecore.diameterapi.diameter.common.session.imdg.HazelcastSession;
import com.elitecore.diameterapi.diameter.common.session.imdg.HazelcastSessionFactory;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier.CC;
import static com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier.NASREQ;
import static com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier.TGPP_GX_29_212_18;
import static com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier.TGPP_SY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class SessionFactoryManagerImplTest {
	
	private static final long UNREGISTERED_APP_ID = 4578;
	private SessionFactoryManager sessionFactoryManager;
	private IDiameterStackContext diameterStackContext;
	private IMDGConfiguration imdgConfiguration = new DummyIMDGConfiguration();
	private final Optional<HazelcastImdgInstance>  ABSENT = Optional.ofNullable(null);
	private FixedTimeSource fixedTimeSource = new FixedTimeSource(10000);
	
	@Mock private ServerContext serverContext;
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@BeforeClass
	public static void before() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		diameterStackContext = new DummyStackContext(null);
		sessionFactoryManager = new SessionFactoryManagerImpl(diameterStackContext, fixedTimeSource);
	}
	
	
	@Test
	public void returnsInMemorySessionFactoryWhenAppIdIsNotRegistered() throws InitializationFailedException{
		sessionFactoryManager.register(NASREQ.applicationId, SessionFactoryType.INMEMORY, ABSENT);
		
		SessionsFactory sessionFactory = sessionFactoryManager.getSessionFactory(UNREGISTERED_APP_ID);
		
		assertThat(sessionFactory, instanceOf(InMemorySessionFactory.class));
	}
	
	@Test
	public void returnsRegisteredSessionFactoryBasedOnAppId() throws InitializationFailedException {
		sessionFactoryManager.register(NASREQ.applicationId, SessionFactoryType.INMEMORY, ABSENT);
		
		SessionsFactory sessionFactory = sessionFactoryManager.getSessionFactory(NASREQ.applicationId);
		
		assertThat(sessionFactory, instanceOf(InMemorySessionFactory.class));
	}
	
	@Test
	public void throwsInitailiztionFailedExceptionIfHazelcastImdgInstanceIsAbsentWhileRegisteringHazelcastSessionFactory() throws InitializationFailedException {
		expectedException.expect(InitializationFailedException.class);
		sessionFactoryManager.register(NASREQ.applicationId, SessionFactoryType.HAZELCAST, ABSENT);
	}
	
	public class Functions {
		Optional<HazelcastImdgInstance> present;
		@Before
		public void setup() throws InitializationFailedException, ImdgInstanceFailedException {
			sessionFactoryManager.register(NASREQ.applicationId, SessionFactoryType.INMEMORY, ABSENT);

			present = Optional.of(new HazelcastImdgInstance(serverContext, imdgConfiguration, DummyIMDGConfiguration.INSTANCE_NAME));
			sessionFactoryManager.register(CC.applicationId, SessionFactoryType.HAZELCAST, present);
			present.get().start();

			sessionFactoryManager.register(TGPP_SY.applicationId, SessionFactoryType.NULLSESSION, ABSENT);
		}
		
		@After
		public void cleanup() {
			present.get().stop();
		}

		@Test
		public void getSessionCountReturnsSumOfSessionsOfEachRegisteredFactories() throws InitializationFailedException, ImdgInstanceFailedException {

			sessionFactoryManager.register(TGPP_GX_29_212_18.applicationId, SessionFactoryType.INMEMORY, ABSENT);
			sessionFactoryManager.register(CC.applicationId, SessionFactoryType.INMEMORY, ABSENT);

			sessionFactoryManager.getSessionFactory(NASREQ.applicationId).getOrCreateSession("session One");
			sessionFactoryManager.getSessionFactory(NASREQ.applicationId).getOrCreateSession("session Two");

			// Hazelcast session is added into map only after session update

			sessionFactoryManager.getSessionFactory(CC.applicationId).getOrCreateSession("session Three").update(ValueProvider.NO_VALUE_PROVIDER);


			sessionFactoryManager.getSessionFactory(CC.applicationId).getOrCreateSession("cc session one");
			sessionFactoryManager.getSessionFactory(TGPP_GX_29_212_18.applicationId).getOrCreateSession("Gx session one");


			Assert.assertEquals(5, sessionFactoryManager.getSessionCount());
		}
		
		@Test
		public void nullSessionFactoryReturnsZeroAsSessionCount() throws InitializationFailedException, ImdgInstanceFailedException {

			sessionFactoryManager.getSessionFactory(TGPP_SY.applicationId).getOrCreateSession("session Four");
			
			Assert.assertEquals(0, sessionFactoryManager.getSessionCount());
		}
		
		@Test
		public void removeIdleSessionRemovesIdleSessionFromAllSessionFactoriesAndReturnsTotal() {
			SessionsFactory inMemorySessionFactory = sessionFactoryManager.getSessionFactory(NASREQ.applicationId);
			inMemorySessionFactory.getOrCreateSession("sessionOne");
			
		
			fixedTimeSource.advance(2000);
			
			inMemorySessionFactory.getOrCreateSession("sessionThree");
			
			assertThat(sessionFactoryManager.removeIdleSession(1000), equalTo(1));
			assertThat(sessionFactoryManager.hasSession("sessionOne", NASREQ.applicationId), is(false));
			assertThat(sessionFactoryManager.hasSession("sessionThree", NASREQ.applicationId), is(true));
		}
	
		@Test
		public void removeAllSessionsRemovesAllSessionsFromAllRegisteredSessionFactoriesAndReturnsCount() throws InterruptedException {
			SessionsFactory inMemorySessionFactory = sessionFactoryManager.getSessionFactory(NASREQ.applicationId);
			inMemorySessionFactory.getOrCreateSession("sessionOne");
			inMemorySessionFactory.getOrCreateSession("sessionTwo");
			
			SessionsFactory hazelcastSessionFactory = sessionFactoryManager.getSessionFactory(CC.applicationId);
			Session sessionThree = hazelcastSessionFactory.getOrCreateSession("sessionThree");
			Session sessionFour = hazelcastSessionFactory.getOrCreateSession("sessionFour");
			
			//So as the 1s lock on the hazelcast sesssion can be released
			hazelcastSessionFactory.update(sessionThree);
			hazelcastSessionFactory.update(sessionFour);
			
			assertThat(sessionFactoryManager.removeAllSessions(), is(4));
			assertThat(sessionFactoryManager.hasSession("sessionOne"), is(false));
			assertThat(sessionFactoryManager.hasSession("sessionTwo"), is(false));
			assertThat(sessionFactoryManager.hasSession("sessionThree"), is(false));
			assertThat(sessionFactoryManager.hasSession("sessionFour"), is(false));
		}
		
		@Test
		public void releaseSessionRemovesTheSessionBasedOnSessionId() {
			SessionsFactory inMemorySessionFactory = sessionFactoryManager.getSessionFactory(NASREQ.applicationId);
			inMemorySessionFactory.getOrCreateSession("sessionOne");
			inMemorySessionFactory.getOrCreateSession("sessionTwo");
			
			sessionFactoryManager.release("sessionTwo");
			
			assertThat(sessionFactoryManager.hasSession("sessionOne"), is(true));
			assertThat(sessionFactoryManager.hasSession("sessionTwo"), is(false));
		}
		
		@Test
		public void hasSessionReturnsTrueIfSessionWithGivenSessionIdExists() {
			SessionsFactory inMemorySessionFactory = sessionFactoryManager.getSessionFactory(NASREQ.applicationId);
			inMemorySessionFactory.getOrCreateSession("sessionOne");
			
			assertThat(sessionFactoryManager.hasSession("sessionOne"), is(true));
		}
		
		@Test
		public void hasSessionReturnsFalseIfSessionWithGivenSessionIdDoesNotExists() {
			SessionsFactory inMemorySessionFactory = sessionFactoryManager.getSessionFactory(NASREQ.applicationId);
			inMemorySessionFactory.getOrCreateSession("sessionOne");
			
			assertThat(sessionFactoryManager.hasSession("sessionTwo"), is(false));
		}
	}
	
	class HazelcastSessionFactoryStub extends HazelcastSessionFactory {

		public HazelcastSessionFactoryStub(HazelcastImdgInstance hazelcastImdgServer) {
			super(hazelcastImdgServer);
		}
		
		@Override
		public Session getOrCreateSession(String sessionId) {
			Session session = sessionIdToSessionMap.get(sessionId);
			if (session == null) {
				session = new HazelcastSession(sessionId, this, fixedTimeSource);
				sessionIdToSessionMap.set(sessionId, session);
			}
			return session;
		}
	}
}
