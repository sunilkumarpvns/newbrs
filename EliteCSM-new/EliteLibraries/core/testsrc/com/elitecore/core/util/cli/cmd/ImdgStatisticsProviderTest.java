package com.elitecore.core.util.cli.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.tests.PrintMethodRule;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ImdgStatisticsProviderTest {

	private static final String STATISTICS = "statistics";
	private ImdgStatisticsProvider imdgStatisticsProvider;
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		imdgStatisticsProvider = Mockito.spy(new ImdgStatisticsProvider());
	}


	@Test
	public void getKeyReturns_statistics() {
		assertEquals(STATISTICS, imdgStatisticsProvider.getKey());
	}

	@Test
	public void getDescriptionReturnsDescription() {
		assertEquals("Display details for given parameter", imdgStatisticsProvider.getDescription());
	}

	public class HelpMessageIsDisplayed {

		private static final String HELP = "-help";

		@Test
		public void whenGivenArgumentIsInvalidOrUnknown() {
			assertTrue(imdgStatisticsProvider.execute(new String[] { "invalid" }).contains("Usage 	  : show imdg statistics <options>"));
		}

		@Test
		public void whenGivenArgumentIsNull() {
			assertTrue(imdgStatisticsProvider.execute(null).contains("Usage 	  : show imdg statistics <options>"));
		}

		@Test
		public void whenGivenArgumentIsHelp() {
			assertTrue(imdgStatisticsProvider.execute(new String[] { HELP }).contains("Usage 	  : show imdg statistics <options>"));
		}

	}
	

}
