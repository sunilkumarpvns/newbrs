package com.elitecore.core.util.cli.cmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.tests.PrintMethodRule;
import com.hazelcast.core.IMap;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ImdgMapStatisticsDetailProviderTest {


	private static final String MAP = "map";
	private ImdgMapStatisticsDetailProvider imdgMapStatisticsDetailProvider;
	@Mock private IMap<Object, Object> map;
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		imdgMapStatisticsDetailProvider = Mockito.spy(new ImdgMapStatisticsDetailProvider());
	}


	@Test
	public void getKeyReturns_map() {
		assertEquals(MAP, imdgMapStatisticsDetailProvider.getKey());
	}

	@Test
	public void getDescriptionReturnsDescription() {
		assertEquals("Diplays IMDG map statistics of the given map name", imdgMapStatisticsDetailProvider.getDescription());
	}

	public class HelpMessageIsDisplayed {

		private static final String HELP_OPTION = "-help";
		private static final String HELP = "?";

		@Test
		public void whenGivenArgumentIsHelp() {
			assertTrue(imdgMapStatisticsDetailProvider.execute(new String[] { HELP }).contains("\nUsage 	  : show imdg statistics map <option>"));
		}

		@Test
		public void whenGivenArgumentIsHelpOption() {
			assertTrue(imdgMapStatisticsDetailProvider.execute(new String[] { HELP_OPTION }).contains("\nUsage 	  : show imdg statistics map <option>"));
		}

	}

	public class AllMapStatisticsAreDisplayed {

		@Test
		public void whenGivenArgumentIsInvalidOrUnknown() {

			imdgMapStatisticsDetailProvider.execute(new String[] { "invalid" });

			verify(imdgMapStatisticsDetailProvider).showAllMapStatistics();
		}

		@Test
		public void whenGivenArgumentIsNull() {

			imdgMapStatisticsDetailProvider.execute(null);

			verify(imdgMapStatisticsDetailProvider).showAllMapStatistics();
		}


	}

	class ParticularMapStatisticsAreDisplayed {

		private final String SOMEMAPNAME[] ={ "MAPNAME" };

		@Before
		public void setUp() {
			imdgMapStatisticsDetailProvider.registerMapDetailProvider(map);
		}

		@Test
		public void whenMapDetailsIsRegistered() {
			assertTrue(imdgMapStatisticsDetailProvider.execute(SOMEMAPNAME).contains("MAPNAME"));

		}
	}

}
