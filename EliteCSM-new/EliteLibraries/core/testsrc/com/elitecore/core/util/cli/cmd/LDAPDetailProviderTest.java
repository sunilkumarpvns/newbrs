package com.elitecore.core.util.cli.cmd;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import com.elitecore.core.commons.utilx.ldap.LDAPConnectionManager;
import com.elitecore.core.commons.utilx.ldap.data.LDAPDataSource;
import com.elitecore.core.util.cli.cmd.DefaultLDAPConnectionProvider;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.LDAPConnectionManagerProvider;
import com.elitecore.core.util.cli.cmd.LDAPDetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;

import junitparams.JUnitParamsRunner;
import netscape.ldap.LDAPConnection;

@RunWith(JUnitParamsRunner.class)
public class LDAPDetailProviderTest {

	private static final String HELP = "-help";
	private static final String LDAP_CONFIGURATION_NOT_FOUND = "LDAP CONFIGURATION NOT FOUND";
	private static final String VIEW = "-view";
	private static final String DATASOURCENAME1 = "DummyDatasource1";
	private static final String DATASOURCENAME2 = "DummyDatasource2";
	private LDAPConnectionManagerProvider connectionManagerProvider;
	private Map<String, LDAPDataSource> dataSourceMap;

	LDAPDetailProvider ldapDetailProvider ;
	LDAPDataSource dummyDataSource1 = new LDAPDatasourceImpl(DATASOURCENAME1);
	LDAPDataSource dummyDataSource2 = new LDAPDatasourceImpl(DATASOURCENAME2);
	

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		dataSourceMap = new HashMap<String, LDAPDataSource>();
		dataSourceMap.put(dummyDataSource1.getDataSourceName(), dummyDataSource1);
		dataSourceMap.put(dummyDataSource2.getDataSourceName(), dummyDataSource2);
		connectionManagerProvider = new DefaultLDAPConnectionProvider();
		ldapDetailProvider = new DummyLDAPDetailProvider(connectionManagerProvider, dataSourceMap); 
		ldapDetailProvider = spy(ldapDetailProvider);
	}

	@Test
	public void test_DataSourceMap_Is_NULL(){
		ldapDetailProvider = new DummyLDAPDetailProvider(null);
		assertEquals(LDAP_CONFIGURATION_NOT_FOUND, ldapDetailProvider.execute(new String[]{"jhsffka"}));
	}

	@Test
	public void test_DataSourceMap_Is_Empty(){
		ldapDetailProvider =new DummyLDAPDetailProvider(Collections.<String , LDAPDataSource>emptyMap());
		assertEquals(LDAP_CONFIGURATION_NOT_FOUND, ldapDetailProvider.execute(new String[]{"jhsffka"}));
	}

	@Test
	@junitparams.Parameters({"",HELP,"?","-Help","-HElp","-heLp" , "-helP", "-HElp", "-heLP", "-hELp", "-HelP", "-hElP", "-HeLp"})
	public void test_execute_ForHelpMsg_with0DetailProviders(String args) throws RegistrationFailedException{
		ldapDetailProvider.execute(new String[]{args});
		verify(ldapDetailProvider, atLeastOnce()).getHelpMsg();
	}

	@Test
	@junitparams.Parameters({"-h", "123", "abc", "1a2b" , "*" , "!avh"})
	public void test_execute_When_Wrong_Parameters_Are_Passed(String args){
		when(ldapDetailProvider.getHelpMsg()).thenReturn(HELP);
		assertEquals("Invalid Option"+ HELP, ldapDetailProvider.execute(new String[]{args}));
	}

	public  Object[][] list_Of_Params_For_View() {

		return new Object[][]{
				{new String[]{VIEW, "DummyDatasource1", "DummyDatasource2"} , new String[]{"DummyDatasource1","DummyDatasource2"} },
				{new String[]{VIEW},  new String[]{"DummyDatasource1","DummyDatasource2"} },
				{new String[]{"-VIEW"},  new String[]{"DummyDatasource1","DummyDatasource2"} },
				{new String[]{"-View"},  new String[]{"DummyDatasource1","DummyDatasource2"} },
				{new String[]{"-VIew"},  new String[]{"DummyDatasource1","DummyDatasource2"} },
				{new String[]{"-viEW"},  new String[]{"DummyDatasource1","DummyDatasource2"} },
				{new String[]{"-ViEw"},  new String[]{"DummyDatasource1","DummyDatasource2"} },
				{new String[]{VIEW},  new String[]{"DummyDatasource1","DummyDatasource2"} },
				{new String[]{VIEW , "DummyDatasource1", "wrongDatsource"} , new String[]{"DummyDatasource1"} },
				{new String[]{VIEW , "DummyDatasource1", "wrongDatasource1","DummyDatasource2" },  new String[]{"DummyDatasource1","DummyDatasource2"} },
				{new String[]{VIEW , "DummyDatasource1" , "DummyDatasource1"}, new String[]{"DummyDatasource1"} },
		};
	}



	@Test
	@junitparams.Parameters(method = "list_Of_Params_For_View")
	public void test_execute_View_When_Partially_Correct_Datasource_Provided(String[] args, String[] correctDatasources){



		Map<String, LDAPDataSource > dbDatasourceMapPramaters = new HashMap<String, LDAPDataSource>();
		for( String datasource : correctDatasources){
			dbDatasourceMapPramaters.put(datasource, dataSourceMap.get(datasource));
		}
		ldapDetailProvider.execute(args);
		verify(ldapDetailProvider).ldapFormat(dbDatasourceMapPramaters);
	}


	@Test
	public void test_execute_when_Param_Is_NULL(){
		ldapDetailProvider.execute(null);
		verify(ldapDetailProvider).getHelpMsg();
	}

	@Test
	public void test_helpMessage_should_Call_GetDecription(){
		ldapDetailProvider.getHelpMsg();
		verify(ldapDetailProvider).getDescription();
		verify(ldapDetailProvider).getKey();
	}

	public void test_getHelpMsg_shouldCallDescriptionOfChildDetailProviders() throws RegistrationFailedException{
		DetailProvider dummyLdapChild = new DummyDetailProvider("Ldap Help", "Dummy_Ldap", null);
		dummyLdapChild = spy(dummyLdapChild);
		ldapDetailProvider.registerDetailProvider(dummyLdapChild);
		ldapDetailProvider.getHelpMsg();
		verify(dummyLdapChild).getHelpMsg();
	}



	public Object[][] listOfParams(){
		return new Object[][]{
				{ new DetailProvider[] { new DummyDetailProvider(null,"LDAP1",null), new DummyDetailProvider(null,"LDAP2", null) },
					"'-l':{'-help':{}, '-view':{ '-help':{}, 'DummyDatasource1':{} , 'DummyDatasource2':{} },LDAP1,LDAP2}"

				},
				{ new DetailProvider[] { new DummyDetailProvider(null,"LDAP1", null) } ,
					"'-l':{'-help':{}, '-view':{ '-help':{}, 'DummyDatasource1':{} , 'DummyDatasource2':{} },LDAP1}"
				},
				{ new DetailProvider[] {} ,
					"'-l':{'-help':{}, '-view':{ '-help':{}, 'DummyDatasource1':{} , 'DummyDatasource2':{} }}"

				}

		};

	}

	@Test
	@junitparams.Parameters(method = "listOfParams")
	public void test_hotKeyHelp(DetailProvider[] detailProviders , String expectedOutput ) throws RegistrationFailedException{
		
		for(DetailProvider detailProvider : detailProviders){
			ldapDetailProvider.registerDetailProvider(detailProvider);
		}
		assertEquals(expectedOutput, ldapDetailProvider.getHotkeyHelp());
	}
	
	@Test
	public void test_fetchedConnectionShouldBeClosed() throws Exception {
		LDAPConnectionManagerProvider spyConnectionManagerProvider = spy(connectionManagerProvider);
		LDAPConnectionManager connectionManager = mock(LDAPConnectionManager.class);
		LDAPConnection ldapConnection = mock(LDAPConnection.class);
		setUpConnectionProvider(spyConnectionManagerProvider, connectionManager, ldapConnection);
		
		ldapDetailProvider = new LDAPDetailProvider(spyConnectionManagerProvider) {

			@Override
			protected Map<String, LDAPDataSource> getLDAPDatasourceMap() {
				return dataSourceMap;
			}
		};
		
		ldapDetailProvider.execute(new String[]{VIEW, DATASOURCENAME1});
		
		verify(connectionManager).closeConnection(ldapConnection);
	}
	
	private void setUpConnectionProvider(LDAPConnectionManagerProvider spyConnectionManagerProvider, LDAPConnectionManager connectionManager, LDAPConnection ldapConnection) throws Exception {
		doReturn(ldapConnection).when(connectionManager).getConnection();
		doReturn(true).when(connectionManager).isInitialize();
		doReturn(connectionManager).when(spyConnectionManagerProvider).getConnectionManager(DATASOURCENAME1);
	}

	private class DummyDetailProvider extends DetailProvider{


		private String helpMsg;
		private String key;
		private HashMap<String,DetailProvider> detailProviderMap;



		public DummyDetailProvider(String helpMsg, String key,
				HashMap<String, DetailProvider> detailProviderMap) {
			this.helpMsg = helpMsg;
			this.key = key;
			this.detailProviderMap = detailProviderMap;
		}

		@Override
		public String execute(String[] parameters) {
			if(parameters != null) return parameters[0];
			else{
				return null;
			}
		}

		@Override
		public String getHelpMsg() {
			// TODO Auto-generated method stub
			return helpMsg;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public HashMap<String, DetailProvider> getDetailProviderMap() {
			return detailProviderMap;
		}

		@Override
		public String getHotkeyHelp() {
			return key;
		}
	}

	private class DummyLDAPDetailProvider extends LDAPDetailProvider{

		private Map<String, LDAPDataSource> dataSourceMap;
		public DummyLDAPDetailProvider(LDAPConnectionManagerProvider connectionManagerProvider, Map<String, LDAPDataSource> dataSourceMap) {
			super(connectionManagerProvider);
			this.dataSourceMap = dataSourceMap;
		}
		
		public DummyLDAPDetailProvider(Map<String, LDAPDataSource> dataSourceMap) {
			super(new DefaultLDAPConnectionProvider());
			this.dataSourceMap = dataSourceMap;
		}

		@Override
		protected Map<String, LDAPDataSource> getLDAPDatasourceMap() {
			return dataSourceMap;
		}

		public String ldapFormat(Map<String, LDAPDataSource> datasourceParametersMap) {
			return "InsideLDAPFromat";
		};

	}
	
	private class LDAPDatasourceImpl implements LDAPDataSource{
		
		private String name;

		public LDAPDatasourceImpl(String name) {
			this.name = name;
		}

		@Override
		public String getStrIpAddress() {
			return null;
		}

		@Override
		public String getAdministrator() {
			return null;
		}

		@Override
		public int getVersion() {
			return 0;
		}

		@Override
		public String getDataSourceId() {
			return null;
		}

		@Override
		public String getDataSourceName() {
			return name;
		}

		@Override
		public String getIpAddress() {
			return null;
		}

		@Override
		public long getLdapSizeLimit() {
			return 0;
		}

		@Override
		public int getMaxPoolSize() {
			return 0;
		}

		@Override
		public int getMinPoolSize() {
			return 0;
		}

		@Override
		public String getPassword() {
			return null;
		}

		@Override
		public String getPlainTextPassword() {
			return null;
		}

		@Override
		public int getPort() {
			return 0;
		}

		@Override
		public int getTimeout() {
			return 0;
		}

		@Override
		public String getUserPrefix() {
			return null;
		}

		@Override
		public ArrayList<String> getSearchBaseDnList() {
			return null;
		}
		
	}

}

