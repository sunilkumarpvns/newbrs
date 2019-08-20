package com.elitecore.netvertex.cli;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.util.CacheStatistics;
import com.elitecore.corenetvertex.util.PartitioningCache;

@RunWith(JUnitParamsRunner.class)
public class SessionCacheStatisticsDetailProviderTest {

	private SessionCacheStatisticsDetailProvider sessionCacheStatisticsDetailProvider;
	private CacheStatistics cacheStatistics;
	
	@Before
	public void setUp() {
		
		PartitioningCache<String, String> cache = new PartitioningCache.CacheBuilder<String, String>(Mockito.mock(com.elitecore.corenetvertex.util.TaskScheduler.class)).build();
		cacheStatistics =  Mockito.spy(cache.statistics());
		sessionCacheStatisticsDetailProvider = new SessionCacheStatisticsDetailProvider("test", cacheStatistics);
		sessionCacheStatisticsDetailProvider = Mockito.spy(sessionCacheStatisticsDetailProvider);
		
	}
	
	@Test
	@Parameters({"?", "-HELP", "-Help", "-help", "-hELP"})
	public void test_session_cache_must_call_getHelpMessage_when_help_parameter_is_given(String parameters) throws RegistrationFailedException{
		
		sessionCacheStatisticsDetailProvider.execute(new String[]{parameters});
		Mockito.verify(sessionCacheStatisticsDetailProvider).getHelpMsg();
		
	}
	
	@Test
	@Parameters({"-", "abc", "pqw abc", "temp", "aaa", "-xzzz", "abc", "pqr", "XYZ","","	","    "})
	public void test_session_cache_must_call_getHelpMessage_when_invalid_parameter_is_given(String parameters) throws RegistrationFailedException{
		
		sessionCacheStatisticsDetailProvider.execute(new String[]{parameters});
		Mockito.verify(sessionCacheStatisticsDetailProvider).getHelpMsg();
		
	}
	
	@Test
	public void test_session_cache_must_call_statistics_getters_when_no_parameters_given() throws RegistrationFailedException {
		
		sessionCacheStatisticsDetailProvider.execute(new String[] {});
		Mockito.verify(cacheStatistics).getRequestCount();
		Mockito.verify(cacheStatistics).getHitCount();
		Mockito.verify(cacheStatistics).getMissCount();
		Mockito.verify(cacheStatistics).getAverageLoadPanelty();
		Mockito.verify(cacheStatistics).getLoadCount();
		Mockito.verify(cacheStatistics).getEvictionCount();
		
	}

	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Test(expected=RegistrationFailedException.class)
	public void test_Registration_fail_for_detailProivder_when_key_is_null() throws RegistrationFailedException{			
		sessionCacheStatisticsDetailProvider.registerDetailProvider(new DummyDetailProvider(null, "help", null));
	}
	
	@Test
	public void test_Registration_fail_for_detailProivder_when_registerd_twice() throws RegistrationFailedException{
		sessionCacheStatisticsDetailProvider.registerDetailProvider(new DummyDetailProvider("-any", "help", null));
		
		expectedException.expect(RegistrationFailedException.class);
		sessionCacheStatisticsDetailProvider.registerDetailProvider(new DummyDetailProvider("-any", "help", null));
	}
	
	@Test
	public void test_getKey_returns_valid_key() {
		
		Assert.assertSame("test", sessionCacheStatisticsDetailProvider.getKey());
	}
	private class DummyDetailProvider extends DetailProvider {

		String key;
		String helpMessage;
		Map<String , DetailProvider> reloadDetailProviderMap;

		public DummyDetailProvider(String key, String helpMessage,
				Map<String, DetailProvider> map){
			this.key = key;
			this.helpMessage = helpMessage;
			this.reloadDetailProviderMap = map;
		}

		@Override
		public String execute(String[] parameters) {
			return "Dummy Detail Provider's Execute Called";
		}
 
		@Override
		public String getHelpMsg() {
			return helpMessage;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public HashMap<String, DetailProvider> getDetailProviderMap() {
			return (HashMap<String, DetailProvider>) reloadDetailProviderMap;
		}

		@Override
		public String getHotkeyHelp() {
			return key;
		}

		@Override
		public String getDescription() {
			return "Describes Detail Proivder";
		}

	}
}
