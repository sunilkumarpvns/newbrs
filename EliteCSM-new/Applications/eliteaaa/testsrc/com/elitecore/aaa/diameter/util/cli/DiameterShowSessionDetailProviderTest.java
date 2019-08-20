package com.elitecore.aaa.diameter.util.cli;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterShowSessionDetailProviderTest {
	
	private static final String SESSION = "session";
	private @Mock SessionFactoryManager sessionFactoryManager;
	private DiameterShowSessionDetailProvider diameterShowSessionDetailProvider;
	
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		diameterShowSessionDetailProvider = spy((new DiameterShowSessionDetailProvider(sessionFactoryManager)));
	}

	@Test
	public void getKeyReturns_session() {
		assertEquals(SESSION, diameterShowSessionDetailProvider.getKey());
	}

	@Test
	public void getHotKeyReturnsHotKeySuggestions() {
		assertEquals("'session':{'count':{},'?':{}}", diameterShowSessionDetailProvider.getHotkeyHelp());
	}
	
	@Test
	public void getDescriptionReturnsDescription() {
		assertEquals("Displays count of diameter sessions", diameterShowSessionDetailProvider.getDescription());
	}
 
	public class HelpMessageIsDisplayed {
		
		@Test
		public void ifGivenArgumentIsInvalidOrUnknown() {
			
			diameterShowSessionDetailProvider.execute(new String[] { "invalid" });
			
			verify(diameterShowSessionDetailProvider, times(1)).getHelpMsg();
		}

		@Test
		public void ifGivenArgumentIsNull() {
			
			diameterShowSessionDetailProvider.execute(null);
			
			verify(diameterShowSessionDetailProvider, times(1)).getHelpMsg();
		}

		@Test
		public void whenGivenArgumentIsHelp() {
			
			diameterShowSessionDetailProvider.execute(new String[] { "-help" });
			
			verify(diameterShowSessionDetailProvider, times(1)).getHelpMsg();
		}
		
	}
	
	@Test
	public void showsSessionCountWhenGivenArgumentIsCount() {
		
		when(sessionFactoryManager.getSessionCount()).thenReturn(5);
		
		String output = diameterShowSessionDetailProvider.execute(new String[] { "count" });
		
		assertEquals("Total Diameter Session count is: " + 5 + "\n", output);
	}

}