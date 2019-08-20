package com.elitecore.diameterapi.diameter.common.session.imdg;

import static com.elitecore.diameterapi.diameter.common.session.imdg.DummyIMDGConfiguration.INSTANCE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.imdg.ImdgInstanceFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.common.session.Session;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class HazelcastSessionFactoryTest {

	private static final String KEY_1 = "Key1";
	private static final String KEY_2 = "Key2";
	private static final String SESSION1_WITH_KEY_1 = "O;1";
	private static final String SESSION2_WITH_KEY_1 = "O;2";
	private static final String SESSION1_WITH_KEY_2 = "R;1";
	
	private HazelcastSessionFactory sessionFactory;
	private HazelcastImdgInstance imdgInstance;
	@Mock private ServerContext context;
	
	@Before
	public void before() throws ImdgInstanceFailedException {
		MockitoAnnotations.initMocks(this);
		imdgInstance = new HazelcastImdgInstance(context, new DummyIMDGConfiguration(), INSTANCE_NAME);
		sessionFactory = new HazelcastSessionFactory(imdgInstance);
		imdgInstance.start();
	}
	
	@Test
	public void remoteAllSessions_DoesNotReleaseAllSessions_AssociatedWithSessionReleaseKey() {
		Session session = sessionFactory.getOrCreateSession(SESSION1_WITH_KEY_1);
		session.setParameter(Session.SESSION_RELEASE_KEY, KEY_1);
		session.update(null);
		
		Session otherSession = sessionFactory.getOrCreateSession(SESSION2_WITH_KEY_1);
		otherSession.setParameter(Session.SESSION_RELEASE_KEY, KEY_1);
		otherSession.update(null);
		
		long releaseCount = sessionFactory.removeAllSessions(KEY_1);
		
		assertEquals(0, releaseCount);
		assertTrue(sessionFactory.hasSession(SESSION1_WITH_KEY_1));
		assertTrue(sessionFactory.hasSession(SESSION2_WITH_KEY_1));
	}

	public class ReadOnlySession {

		@Before
		public void before() {
			Session session = sessionFactory.getOrCreateSession(SESSION1_WITH_KEY_1);
			session.setParameter(KEY_1, KEY_1);
			session.update(null);
		}

		@Test
		public void returnsNullForUnknownSession() {
			assertNull(sessionFactory.readOnlySession(SESSION1_WITH_KEY_2));
		}

		@Test(expected=UnsupportedOperationException.class)
		public void updateOperationNotSupported() {
			ISession readOnlySession = sessionFactory.readOnlySession(SESSION1_WITH_KEY_1);
			readOnlySession.update(null);
		}

		@Test(expected=UnsupportedOperationException.class)
		public void releaseOperationNotSupported() {
			ISession readOnlySession = sessionFactory.readOnlySession(SESSION1_WITH_KEY_1);
			readOnlySession.release();
		}

		@Test(expected=UnsupportedOperationException.class)
		public void setParameterOperationNotSupported() {
			ISession readOnlySession = sessionFactory.readOnlySession(SESSION1_WITH_KEY_1);
			readOnlySession.setParameter(KEY_2, KEY_2);
		}

		@Test(expected=UnsupportedOperationException.class)
		public void removeParameterOperationNotSupported() {
			ISession readOnlySession = sessionFactory.readOnlySession(SESSION1_WITH_KEY_1);
			readOnlySession.removeParameter(KEY_1);
		}

	}
	
	@After
	public void after() {
		imdgInstance.stop();
	}
}
