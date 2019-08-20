package com.elitecore.core.util.cli.cmd;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.commons.tests.PrintMethodRule;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ImdgDetailProviderTest {

	private static final String IMDG = "imdg";
	private ImdgDetailProvider imdgDetailProvider;
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	@Before
	public void setUp() {
		imdgDetailProvider = Mockito.spy(new ImdgDetailProvider());
	}


	@Test
	public void getKeyReturns_imdg() {
		assertEquals(IMDG, imdgDetailProvider.getKey());
	}

	@Test
	public void getDescriptionReturnsDescription() {
		assertEquals("Display details for given parameter", imdgDetailProvider.getDescription());
	}

	public class HelpMessageIsDisplayed {

		private static final String HELP = "-help";

		@Test
		public void whenGivenArgumentIsInvalidOrUnknown() {
			org.junit.Assert.assertTrue(imdgDetailProvider.execute(new String[] { "invalid" }).contains("Usage 	  : show imdg <options>"));
		}

		@Test
		public void whenGivenArgumentIsNull() {
			org.junit.Assert.assertTrue(imdgDetailProvider.execute(null).contains("Usage 	  : show imdg <options>"));
		}

		@Test
		public void whenGivenArgumentIsHelp() {
			org.junit.Assert.assertTrue(imdgDetailProvider.execute(new String[] { HELP }).contains("Usage 	  : show imdg <options>"));
		}

	}
	
}

