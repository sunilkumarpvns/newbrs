package com.elitecore.diameterapi.core.common.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.session.InMemorySessionFactory;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class InMemorySessionFactoryTest {
	
	private static final String KEY_1 = "Key1";
	private static final String KEY_2 = "Key2";
	private static final String SESSION1_WITH_KEY_1 = "O;1";
	private static final String SESSION2_WITH_KEY_1 = "O;2";
	private static final String SESSION1_WITH_KEY_2 = "R;1";
	private static final String SESSION2_WITH_KEY_2 = "R;2";
	
	private static final IDiameterStackContext STACK_CONTEXT = new DummyStackContext(null) {
		
		public int getMaxWorkerThreads() {
			return 1;
		};
	};
	
	private SessionsFactory sessionFactory;
	
	@Before
	public void before() {
		sessionFactory = new InMemorySessionFactory(STACK_CONTEXT, TimeSource.systemTimeSource());
	}
	
	@Test
	public void remoteAllSessions_ShouldReleaseAllSessions_AssociatedWithSessionReleaseKey() {
		Session session = sessionFactory.getOrCreateSession(SESSION1_WITH_KEY_1);
		session.setParameter(Session.SESSION_RELEASE_KEY, KEY_1);
		
		Session otherSession = sessionFactory.getOrCreateSession(SESSION2_WITH_KEY_1);
		otherSession.setParameter(Session.SESSION_RELEASE_KEY, KEY_1);
		
		long releaseCount = sessionFactory.removeAllSessions(KEY_1);
		
		assertEquals(2, releaseCount);
		assertFalse(sessionFactory.hasSession(SESSION1_WITH_KEY_1));
		assertFalse(sessionFactory.hasSession(SESSION2_WITH_KEY_1));
	}
	
	@Test
	public void remoteAllSessions_ShouldNotReleaseAnySession_NotAssociatedWithSessionReleaseKey() {
		Session session1 = sessionFactory.getOrCreateSession(SESSION1_WITH_KEY_1);
		session1.setParameter(DiameterSession.SESSION_RELEASE_KEY, KEY_1);
		
		Session session2 = sessionFactory.getOrCreateSession(SESSION2_WITH_KEY_1);
		session2.setParameter(DiameterSession.SESSION_RELEASE_KEY, KEY_1);
		
		Session session3 = sessionFactory.getOrCreateSession(SESSION1_WITH_KEY_2);
		session3.setParameter(DiameterSession.SESSION_RELEASE_KEY, KEY_2);
		
		Session session4 = sessionFactory.getOrCreateSession(SESSION2_WITH_KEY_2);
		session4.setParameter(DiameterSession.SESSION_RELEASE_KEY, KEY_2);
		
		sessionFactory.removeAllSessions(KEY_1);
		
		assertTrue(sessionFactory.hasSession(SESSION1_WITH_KEY_2));
		assertTrue(sessionFactory.hasSession(SESSION2_WITH_KEY_2));
	}
	
	public class ReadOnlySession {
		
		@Before
		public void before() {
			Session session = sessionFactory.getOrCreateSession(SESSION1_WITH_KEY_1);
			session.setParameter(KEY_1, KEY_1);
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
}
