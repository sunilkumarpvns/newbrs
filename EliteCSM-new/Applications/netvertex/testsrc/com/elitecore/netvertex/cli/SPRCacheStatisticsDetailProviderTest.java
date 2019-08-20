package com.elitecore.netvertex.cli;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.util.CacheStatistics;
import com.elitecore.corenetvertex.util.PartitioningCache;



/**
 * @author chetan.sankhala
 */

@RunWith(value = junitparams.JUnitParamsRunner.class)
public class SPRCacheStatisticsDetailProviderTest {

	private SPRCacheStatisticsDetailProvider provider;
	private CacheStatistics statistics;
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setUp() {
		PartitioningCache<String, String> cache = new PartitioningCache.CacheBuilder<String, String>(mock(com.elitecore.corenetvertex.util.TaskScheduler.class)).build();
		statistics = spy(cache.statistics());
		provider = new SPRCacheStatisticsDetailProvider(statistics);
		provider = spy(provider);
		
	}
	
	@Test
	@Parameters({"?","-HELP","-Help","-help","-hELP"})
	public void test_helpMessage_should_call_when_help_parameter_is_passed(String parameters){
		
		provider.execute(new String[]{parameters});
		
		verify(provider).getHelpMsg();
	}
	
	public Object[][] dataProviderFor_test_hotkeyhelp_for_should_show_childDetail_parameters_if_registered(){
		
		String prefix = "'spr-cache':{'-help':{}";
		String suffix = "}";
		
		return new Object[][] {
				{
					new DetailProvider[]{new DummyDetailProvider("-param1","Help For param1", new HashMap<String, DetailProvider>(4,1))},
					prefix + ",'-param1':{}" + suffix
				},
				{
					new DetailProvider[]{},
					prefix + "" + suffix
				}
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_hotkeyhelp_for_should_show_childDetail_parameters_if_registered")
	public void test_hotkeyhelp_for_should_show_childDetail_parameters_if_registered(
			DetailProvider[] dummyDetailProvider,
			String expectedHotKeyMsg ) throws RegistrationFailedException{
		
		for(DetailProvider dummy : dummyDetailProvider){
			provider.registerDetailProvider(dummy);
		}
		
		assertEquals(expectedHotKeyMsg, provider.getHotkeyHelp());
	}
	
	public Object[][] dataProviderFor_test_execute_should_call_getHelpMsg_when_provided_detail_provider_not_exist(){
		
		final String NOT_REGISTERED_PARAM = "param3";
		
		return new Object[][] {
				{
					new DetailProvider[]{new DummyDetailProvider("param1","Help For param1", new HashMap<String, DetailProvider>(4,1))},
					new String[]{NOT_REGISTERED_PARAM}
				},
				{//two detail providers
					new DetailProvider[]{new DummyDetailProvider("param1","Help For param2", new HashMap<String, DetailProvider>(4,1)), new DummyDetailProvider("param2","Help For param2", new HashMap<String, DetailProvider>(4,1))},
					new String[]{NOT_REGISTERED_PARAM},
				},
				{
					new DetailProvider[]{},
					new String[]{NOT_REGISTERED_PARAM},
				}
		};
	}
	
	
	@Test
	@Parameters(method =  "dataProviderFor_test_execute_should_call_getHelpMsg_when_provided_detail_provider_not_exist")
	public void test_execute_should_call_getHelpMsg_when_provided_detail_provider_not_exist(
			DetailProvider[] detailProviders,
			String[] parameters) throws Exception {
		
		registerDetailProviders(detailProviders);
		
		provider.execute(parameters);
		verify(provider, times(1)).getHelpMsg();
	}
	
	private void registerDetailProviders(DetailProvider[] detailProviders) throws Exception {
		for(DetailProvider detailProvider : detailProviders){
			provider.registerDetailProvider(detailProvider);
		}
	}

	@Test
	public void test_execute_should_call_execute_of_registered_detail_provided() throws Exception {

		DetailProvider mockedDetailProvider = registerMockedDetailProvider();
		
		provider.execute(new String[] { "dummy" });
		
		verify(mockedDetailProvider, times(1)).execute(new String[]{});
	}
	
	private DetailProvider registerMockedDetailProvider() throws Exception {
		
		DetailProvider detailProvider = mock(DetailProvider.class);
		when(detailProvider.getKey()).thenReturn("dummy");
		
		provider.registerDetailProvider(detailProvider);
		
		return detailProvider;
	}
	
	@Test
	public void test_execute_should_fetch_counters_from_provided_statistics() throws Exception {
		
		provider.execute(new String[] {});
		
		verify(statistics, times(1)).getRequestCount();
		verify(statistics, times(1)).getHitCount();
		verify(statistics, times(1)).getEvictionCount();
		verify(statistics, times(1)).getAverageLoadPanelty();
		verify(statistics, times(1)).getLoadCount();
		verify(statistics, times(1)).getMissCount();
	}
	
	@Test
	public void test_getKey_should_give_name_of_registered_detail_provider() throws Exception {
		assertEquals("spr-cache", provider.getKey());
	}
	
	
	@Test
	public void test_registerDetailProvider_should_throw_RegistrationFailedException_when_detail_provider_with_null_key_passed() throws Exception {
		
		//detail provider for null key
		DummyDetailProvider detailProvider = new DummyDetailProvider(null, "help", new HashMap<String, DetailProvider>());
		
		exception.expect(RegistrationFailedException.class);
		exception.expectMessage("Failed to register detail provider. Reason : key is not specified.");
		
		provider.registerDetailProvider(detailProvider);
	}
	
	@Test
	public void test_registerDetailProvider_should_throw_RegistrationFailedException_when_detail_provider_already_exist() throws Exception {
		DummyDetailProvider detailProvider1 = new DummyDetailProvider("dummy1", "help", new HashMap<String, DetailProvider>());
		DummyDetailProvider detailProvider2 = new DummyDetailProvider("dummy1", "help", new HashMap<String, DetailProvider>());
		
		provider.registerDetailProvider(detailProvider1);

		exception.expect(RegistrationFailedException.class);
		exception.expectMessage("Failed to register detail provider. Reason : Detail Provider already contains detail provider with Key : " + detailProvider2.getKey());
		
		provider.registerDetailProvider(detailProvider2);
	}

	private class DummyDetailProvider extends DetailProvider {

		private String key;
		private String helpMessage;
		private HashMap<String, DetailProvider> detailProviderMap;
		
		
		public DummyDetailProvider(String key, String helpMessage,
				HashMap<String, DetailProvider> map){
			this.key = key;
			this.helpMessage = helpMessage;
			this.detailProviderMap = map;
		}
		
		
		@Override
		public String execute(String[] parameters) {
			return key + " executed";
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
			return detailProviderMap;
		}
		
	}
}
