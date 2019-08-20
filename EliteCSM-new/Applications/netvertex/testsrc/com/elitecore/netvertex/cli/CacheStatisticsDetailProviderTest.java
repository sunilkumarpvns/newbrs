package com.elitecore.netvertex.cli;

import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import junit.framework.Assert;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

@RunWith(JUnitParamsRunner.class)
public class CacheStatisticsDetailProviderTest {

	private CacheStatisticsDetailProvider cacheStatisticsDetailProvider = CacheStatisticsDetailProvider.getInstance();
	private DummyDetailProvider dummyDetailProvider;
	
	@Before
	public void setUp() throws RegistrationFailedException {
		cacheStatisticsDetailProvider = Mockito.spy(cacheStatisticsDetailProvider);
		dummyDetailProvider = new DummyDetailProvider("-temp", "Calling dummy detail provider's help",new HashMap<String, DetailProvider>(4,1));
		dummyDetailProvider = Mockito.spy(dummyDetailProvider);
	}
	
	@Test
	@Parameters({"?", "-HELP", "-Help", "-help", "-hELP", ""})
	public void test_cache_must_call_getHelpMessage_when_help_parameter_is_given(String parameters) throws RegistrationFailedException{
		
		cacheStatisticsDetailProvider.execute(new String[]{parameters});
		Mockito.verify(cacheStatisticsDetailProvider).getHelpMsg();
	}
	
	@Test
	@Parameters({ "-session-cache", "abc", "pqw abc", "temp", "aaa", "-xzzz", "abc", "pqr", "XYZ","", " ", "   ","		"})
	public void test_session_cache_must_call_getHelpMessage_when_invalid_parameter_is_given(String parameters) throws RegistrationFailedException{
		
		cacheStatisticsDetailProvider.execute(new String[]{parameters});
		Mockito.verify(cacheStatisticsDetailProvider).getHelpMsg();
	}
	
	
	@Test
	public void test_cache_must_call_getHelpMessage_when_no_parameter_is_given() throws RegistrationFailedException{
		cacheStatisticsDetailProvider.execute(new String[0]);
		Mockito.verify(cacheStatisticsDetailProvider).getHelpMsg();
	}
	
	@Test
	public void test_cache_must_call_child_helpMessage_when_helpMethod_called() throws RegistrationFailedException{
		cacheStatisticsDetailProvider.registerDetailProvider(dummyDetailProvider);
		cacheStatisticsDetailProvider.getHelpMsg();
		Mockito.verify(dummyDetailProvider).getDescription();
	
	}
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Test(expected=RegistrationFailedException.class)
	public void test_Registration_fail_for_detailProivder_when_key_is_null() throws RegistrationFailedException{			
		cacheStatisticsDetailProvider.registerDetailProvider(new DummyDetailProvider(null, "help", null));
	}
	
	@Test
	public void test_Registration_fail_for_detailProivder_when_registerd_twice() throws RegistrationFailedException{
		cacheStatisticsDetailProvider.registerDetailProvider(new DummyDetailProvider("-any", "help", null));
		
		expectedException.expect(RegistrationFailedException.class);
		cacheStatisticsDetailProvider.registerDetailProvider(new DummyDetailProvider("-any", "help", null));
	}
	
	public Object[][] data_for_execute_should_call_detailProvider_based_on_argument(){
		
		return new Object[][]{
				
				{
					new DummyDetailProvider("temp1", "help1", null), 
					"temp1",
					new String[]{}
				},
				{
					new DummyDetailProvider("temp2", "help2", null),
					"temp2",
					new String[]{}
				},
				{
					new DummyDetailProvider("temp3", "help3", null),
					"temp3 -HELP",
					new String[]{"-HELP"}
				},
				{
					new DummyDetailProvider("temp4", "help4", null),
					"temp4 -help",
					new String[]{"-help"}
				},
				{
					new DummyDetailProvider("temp5", "help5", null),
					"temp5 xyz abc",
					new String[]{"xyz","abc"}
				},
				{
					new DummyDetailProvider("temp6", "help6", null),
					"temp6 xyz",
					new String[]{"xyz"}
				},
		};
	}
	
	@Test
	@Parameters(method="data_for_execute_should_call_detailProvider_based_on_argument")
	public void test_execute_must_call_detailProvide_based_on_argument(DummyDetailProvider detailProvider, String parameter,String[] detailProviderArgs) throws RegistrationFailedException{
		detailProvider = Mockito.spy(detailProvider);
		cacheStatisticsDetailProvider.registerDetailProvider(detailProvider);
		cacheStatisticsDetailProvider.execute(parameter.split(" "));
		Mockito.verify(detailProvider).execute(detailProviderArgs);
	}
	
	@Test
	public void test_getKey_returns_valid_key() {
		
		Assert.assertSame("cache", cacheStatisticsDetailProvider.getKey());
	}
	private class DummyDetailProvider extends DetailProvider {

		String key;
		String helpMessage;
		Map<String , DetailProvider> detailProviderMap;

		public DummyDetailProvider(String key, String helpMessage,
				Map<String, DetailProvider> map){
			this.key = key;
			this.helpMessage = helpMessage;
			this.detailProviderMap = map;
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
			return (HashMap<String, DetailProvider>) detailProviderMap;
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
