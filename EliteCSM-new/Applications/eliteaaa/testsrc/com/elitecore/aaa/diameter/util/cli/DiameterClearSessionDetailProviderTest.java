package com.elitecore.aaa.diameter.util.cli;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManagerImpl;
import com.elitecore.diameterapi.core.common.session.SessionFactoryType;
import com.elitecore.diameterapi.core.common.session.SessionsFactory;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterClearSessionDetailProviderTest {
	private static final String SESSION = "session";
	private static final String ALL = "all";
	private static final String TIME = "time";
	private static final String ID = "id";
	private static final String ID_OR_TIME = "5";
	
	private SessionFactoryManager sessionFactoryManager;
	private DiameterClearSessionDetailProvider diameterClearSessionDetailProvider;

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		sessionFactoryManager = spy(new SessionFactoryManagerImpl(new DummyStackContext(null)));
		diameterClearSessionDetailProvider = spy((new DiameterClearSessionDetailProvider(sessionFactoryManager)));
	}
	
	@Test
	public void getKeyReturns_session() {
		assertEquals(SESSION, diameterClearSessionDetailProvider.getKey());
	}
	
	@Test
	public void getHotKeyReturnsHotKeySuggestions() {
		assertEquals("'"+SESSION+"':{'"+ALL+"':{},'"+TIME+"':{},'"+ID+"':{},'?':{}}", diameterClearSessionDetailProvider.getHotkeyHelp());
	}
	
	@Test
	public void getDescriptionReturnsDescription() {
		assertEquals("Flushes Diameter Sessions", diameterClearSessionDetailProvider.getDescription());
	}
	
	public class HelpMessageIsDisplayed {
		
		@Test
		public void ifGivenArgumentIsInvalidOrUnknown() {
			
			diameterClearSessionDetailProvider.execute(new String[] { "invalid" });
			
			verify(diameterClearSessionDetailProvider, times(1)).getHelpMsg();
		}

		@Test
		public void ifGivenArgumentIsNull() {
			
			diameterClearSessionDetailProvider.execute(null);
			
			verify(diameterClearSessionDetailProvider, times(1)).getHelpMsg();
		}

		@Test
		public void whenGivenArgumentIsHelp() {
			
			diameterClearSessionDetailProvider.execute(new String[] { "-help" });
			
			verify(diameterClearSessionDetailProvider, times(1)).getHelpMsg();
		}
		
	}
	
	public class SessionIsCleared {
		
		private static final long REGISTERED_APP_ID = 0;

		@Test
		public void allSessionsWhenArgumentIsAll() {
			
			when(sessionFactoryManager.removeAllSessions()).thenReturn(5);
			
			String output = diameterClearSessionDetailProvider.execute(new String[] { ALL });
			
			assertEquals("Total number of flushed Diameter Sessions are: " + 5 + "\n", output);
		}
		
		@Test
		public void whenIdArgumentProvidedIfSessionIdMatch() throws InitializationFailedException {
			sessionFactoryManager.register(REGISTERED_APP_ID, SessionFactoryType.INMEMORY, Optional.ofNullable(null));
			SessionsFactory sessionFactory = sessionFactoryManager.getSessionFactory(REGISTERED_APP_ID);
			sessionFactory.getOrCreateSession(ID_OR_TIME);
			String output = diameterClearSessionDetailProvider.execute(new String[] { ID , ID_OR_TIME });
			assertEquals("Session for Session-ID: " + 5 + " succesfully removed\n", output);
		}
		
		@Test
		public void ifArgumentIsTimeAndSessionsIsOlderThanGivenTime() {
			
			when(sessionFactoryManager.removeIdleSession(5 * 60000)).thenReturn(5);
			
			String output = diameterClearSessionDetailProvider.execute(new String[] { TIME , ID_OR_TIME });
			
			assertEquals("Total number of flushed sessions are: " + 5 + "\n", output);
		}
	}
	
	
	public class SessionIsNotCleared {
		
		@Test
		public void whenArgumentIsIdButNoMatchingSessionIdFound() {
			
			when(sessionFactoryManager.hasSession(ID_OR_TIME)).thenReturn(false);
			
			String output = diameterClearSessionDetailProvider.execute(new String[] { ID , ID_OR_TIME });
			
			assertEquals("Session with Session-ID: " + 5 + " does not exist\n", output);
		}
		
		
		@Test
		public void whenArgumentIsTimeButInputIsInvalid() {
			
			when(sessionFactoryManager.removeIdleSession(5 * 60000)).thenReturn(5);
			
			String output = diameterClearSessionDetailProvider.execute(new String[] { TIME , "invalidtime" });
			
			assertEquals("Invalid time. Please give time in Number of Minutes.", output);
		}
	}
}
