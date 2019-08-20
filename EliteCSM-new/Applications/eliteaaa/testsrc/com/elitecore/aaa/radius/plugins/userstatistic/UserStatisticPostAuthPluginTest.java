package com.elitecore.aaa.radius.plugins.userstatistic;

import static com.elitecore.core.CoreLibMatchers.ESCommunicatorMatchers.isAlive;
import static com.elitecore.core.CoreLibMatchers.ESCommunicatorMatchers.isDead;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.impl.DatabaseDSConfigurationImpl;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl.AttributeDetail;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DBConnectionManagerSpy;
import com.elitecore.core.commons.util.db.DerbyConnectionProvider;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
/***
 * 
 * @author soniya
 */
@RunWith(HierarchicalContextRunner.class)
public class UserStatisticPostAuthPluginTest {

	private static final String SERVICE_TYPE_ATTRIBUTE = "0:6";
	private static final String FRAMED_USER = "2";
	private static final String DICTIONARY_VALUE_FOR_FRAMED_USER = "Framed-User";
	private static final String DEFAULTVALUE_CALLINGSTATION_ID = "10.20.30.40";

	private static final PluginCallerIdentity DUMMY_IDENTITY = null;
	private static final String DUMMY_ARGUMENT = "";
	private TableUserStatisticsSchema userStatisticsTable;

	private UserStatisticPostAuthPluginStub userStatisticPostAuthPlugin;
	private UserStatisticPostAuthPluginConfigurationImpl userStatsticsPluginData;
	private FakeTaskScheduler fakeTaskScheduler;
	private DerbyConnectionProvider derbyConnectionProvider;
	private DBConnectionManagerSpy dbConnectionManagerSpy;
	private RadAuthRequestBuilder radAuthBuilder;
	private RadAuthRequest request;

	private RadAuthResponse response;
	
	@Mock private DatabaseDSConfigurationImpl databaseDSConfigutation;
	@Mock private ServerContext serverContext;

	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		fakeTaskScheduler = new FakeTaskScheduler();
		derbyConnectionProvider = new DerbyConnectionProvider(fakeTaskScheduler);
		dbConnectionManagerSpy = DBConnectionManagerSpy.create(derbyConnectionProvider.getConnectionManager());
		createPluginData();
		createPlugin();
		createRequestAndResponse();
		request = radAuthBuilder.build();
		createTableSchema();
	}
	
	@Test
	public void isOnlyEligibleForPostRequestExecution() throws Exception {

		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage("User Statistic Auth Post Plugin does not applicable for pre operation.");

		userStatisticPostAuthPlugin.handlePreRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
	}
	
	@Test
	public void isNotApplicableForAccessChallengeMessage() throws Exception {
		response.setPacketType(RadiusConstants.ACCESS_CHALLENGE_MESSAGE);

		userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
		
		executeBatch(); 
		
		assertNoStatisticsRecorded();
	}

	@Test
	public void pluginInitializationFailsIfConfiguredDatasourceFailsToInitialize() throws Exception {
		dbConnectionManagerSpy.simulateDatabaseInitializationFailure();
		
		exception.expect(InitializationFailedException.class);

		createPlugin();
	}
	
	@Test
	public void pluginInitializationFailsIfConfiguredDatasourceTypeIsUnsupported() throws Exception {
		dbConnectionManagerSpy.simulateDatabaseTypeUnsupported();
		
		exception.expect(InitializationFailedException.class);

		createPlugin();
	}
	
	@Test
	public void recordUserInformationInBatch() throws Exception {
		userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

		assertThat(recordedStatisticsCount(), is(equalTo(0)));
		
		executeBatch();

		assertThat(recordedStatisticsCount(), is(equalTo(1)));
	}

	@Test
	public void recordsDefaultAttributeValueFromMappingIfAttributeNotFoundInConfiguredPacketType() throws Exception {

		AttributeDetail requestMapping = dbFieldMapping()
				.attributeId("0:31").dbField("CALLING_STATION_ID")
				.fromRequestPacket().dataType("String")
				.defaultValue(DEFAULTVALUE_CALLINGSTATION_ID)
				.attributeDetail;
		AttributeDetail responseMapping = dbFieldMapping()
				.attributeId("0:27").dbField("SESSION_TIMEOUT")
				.fromResponsePacket().dataType("String")
				.defaultValue("100")
				.attributeDetail;
		
		addDBFieldMapping(requestMapping);
		addDBFieldMapping(responseMapping);

		createPlugin();

		userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

		executeBatch();

		assertThat(valueOf(requestMapping.getDbField()), is(equalTo(DEFAULTVALUE_CALLINGSTATION_ID)));
		assertThat(valueOf(responseMapping.getDbField()), is(equalTo("100")));
	}

	@Test
	public void recordsDictionaryValueForEnumeratedTypeIfEnableInDBFieldMappingAndAttributeExist() throws Exception {

		request = radAuthBuilder.addAttribute(SERVICE_TYPE_ATTRIBUTE, FRAMED_USER).build();

		AttributeDetail attributeDetail = dbFieldMapping().attributeId(SERVICE_TYPE_ATTRIBUTE)
				.dbField("SERVICE_TYPE").useDictionaryValue().
				fromRequestPacket().dataType("String")
				.attributeDetail;
		
		addDBFieldMapping(attributeDetail);

		createPlugin();

		userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);

		executeBatch();

		assertEquals(DICTIONARY_VALUE_FOR_FRAMED_USER, valueOf(attributeDetail.getDbField()));		
	}
	
	@Test
	public void marksTransactionFactoryAsDeadIfDbDownSQLExceptionOccursWhileExecutingBatch() throws Exception {
		userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
		
		dbConnectionManagerSpy.simulateDBDownDatasourceExceptionFromGetConnection();
	
		executeBatch();

		assertThat(dbConnectionManagerSpy.getSpiedInstance().getTransactionFactory(), isDead());
	}
	
	@Test
	public void clearsPendingStatisticsIfDbDownSQLExceptionOccursWhileExecutingBatch() throws Exception {
		userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
		
		dbConnectionManagerSpy.simulateDBDownDatasourceExceptionFromGetConnection();
	
		executeBatch();

		assertNoStatisticsRecorded();
	}
	
	public class QueryTimeout {

		@Before
		public void setUp() throws SQLException, InitializationFailedException {
			userStatsticsPluginData.setMaxQueryTimeoutCount(2);
			createPlugin();
			dbConnectionManagerSpy.simulateQueryTimeout();	
		}
		
		@Test
		public void doesNotMarkTransactionFactoryAsDeadIfMaxQueryTimeoutCountIsNotReached() throws Exception {
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();

			assertThat(dbConnectionManagerSpy.getSpiedInstance().getTransactionFactory(), isAlive());
		}
		
		@Test
		public void marksTransactionFactoryAsDeadIfMaxQueryTimeoutCountIsReached() throws Exception {
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();
			
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();
			
			assertThat(dbConnectionManagerSpy.getSpiedInstance().getTransactionFactory(), isDead());
		}
		
		@Test
		public void allPreviousStatisticsAreClearedIfMaxQueryTimeoutCountIsReached() throws Exception {
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();
			
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();
			
			assertNoStatisticsRecorded();	
		}
	}
	
	public class UnknownDBError {
		
		@Before
		public void setUp() throws SQLException, InitializationFailedException {
			userStatsticsPluginData.setMaxQueryTimeoutCount(2);
			createPlugin();
			dbConnectionManagerSpy.simulateSQLRecoverableExceptionFromPreparedStatement();
		}
		
		@Test
		public void doesNotMarkTransactionFactoryAsDeadIfMaxQueryTimeoutCountIsNotReached() throws Exception {
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();
			
			assertThat(dbConnectionManagerSpy.getSpiedInstance().getTransactionFactory(), isAlive());
		}
		
		@Test
		public void marksTransactionFactoryAsDeadIfMaxQueryTimeoutCountIsReached() throws Exception {
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();
			
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();
			
			assertThat(dbConnectionManagerSpy.getSpiedInstance().getTransactionFactory(), isDead());
		}
		
		@Test
		public void allPreviousStatisticsAreClearedIfMaxQueryTimeoutCountIsReached() throws Exception {
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();
			
			userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
			
			executeBatch();
			
			assertNoStatisticsRecorded();	
		}
	}
	
	@Test
	public void userInformationWillNotRecordedIfTransactionFactoryIsDead() throws Exception {
		dbConnectionManagerSpy.getSpiedInstance().getTransactionFactory().markDead();
		dbConnectionManagerSpy.simulateDatasourceExceptionFromGetConnection();
		
		userStatisticPostAuthPlugin.handlePostRequest(request, response, DUMMY_ARGUMENT, DUMMY_IDENTITY, ISession.NO_SESSION);
		
		executeBatch();
		
		assertNoStatisticsRecorded();	
	}

	private void assertNoStatisticsRecorded() throws Exception {
		assertThat(recordedStatisticsCount(), is(equalTo(0)));
	}

	private int recordedStatisticsCount() throws SQLException {
		return userStatisticsTable.count();
	}
	
	private String valueOf(String dbField) throws Exception {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dbConnectionManagerSpy.getSpiedInstance().getConnection();
			preparedStatement = connection.prepareStatement("select "+ dbField +" from TBLUSERSTATISTICS");
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			return resultSet.getString(dbField);
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}

	private void executeBatch() {
		fakeTaskScheduler.tick();
	}
	
	private void createRequestAndResponse() throws Exception {

		radAuthBuilder = new RadAuthRequestBuilder()
		.addAttribute("0:1", "usename")
		.packetType(2);

		response = new RadAuthRequestBuilder()
		.addAttribute("0:18", "access accept")
		.packetType(2)
		.buildResponse();
	}

	private void createTableSchema() throws Exception {
		System.out.println("Creating table schema for TBLUSERSTATISTICS");
		userStatisticsTable = new TableUserStatisticsSchema(derbyConnectionProvider.getConnectionManager());
		userStatisticsTable.create();
	}

	private void setUserStatisticsPluginData() {
		userStatsticsPluginData = new UserStatisticPostAuthPluginConfigurationImpl();
		userStatsticsPluginData.setName("User_staistic_post_auth_plugin_test");
		userStatsticsPluginData.setStatus("CST01");
		userStatsticsPluginData.setTableName("TBLUSERSTATISTICS");
		userStatsticsPluginData.setDbQueryTimeoutInMs(1);
		userStatsticsPluginData.setMaxQueryTimeoutCount(1);
		userStatsticsPluginData.setBatchUpdateIntervalInMs(1000);
		userStatsticsPluginData.setDataSourceName("FakeDatasource");

		addDBFieldMapping(dbFieldMapping().attributeId("0:1")
				.dbField("USER_NAME").fromRequestPacket()
				.dataType("String").attributeDetail);

		addDBFieldMapping(dbFieldMapping().attributeId("0:18")
				.dbField("REPLY_MESSAGE").fromResponsePacket()
				.dataType("String").attributeDetail);
		
		addDBFieldMapping(dbFieldMapping().attributeId("0:89")
				.dbField("PARAM_DATE0").fromRequestPacket()
				.dataType("Date").defaultValue("120").attributeDetail);

	}

	private void createPluginData() {
		setUserStatisticsPluginData();
		userStatsticsPluginData.postRead();
	}

	private void createPlugin() throws InitializationFailedException {
		userStatsticsPluginData.postRead();

		PluginContext pluginContext = new PluginContext() {

			@Override
			public ServerContext getServerContext() {
				return serverContext;
			}

			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return null;
			}
		};
		
		userStatisticPostAuthPlugin = new UserStatisticPostAuthPluginStub(pluginContext, databaseDSConfigutation, userStatsticsPluginData, dbConnectionManagerSpy.getSpiedInstance());
		
		Mockito.doReturn(derbyConnectionProvider.getDataSource())
		.when(databaseDSConfigutation).getDataSourceByName(Mockito.anyString());

		Mockito.doReturn(fakeTaskScheduler)
		.when(serverContext).getTaskScheduler();

		userStatisticPostAuthPlugin.init();

	}


	@After
	public void dropSchema() throws SQLException, Exception {
		System.out.println("Dropping table schema for TBLUSERSTATISTICS");
		userStatisticsTable.drop();
	}

	class UserStatisticPostAuthPluginStub extends UserStatisticPostAuthPlugin {

		UserStatisticPostAuthPluginStub(PluginContext pluginContext,
				DatabaseDSConfigurationImpl databaseDSConfiguration,
				UserStatisticPostAuthPluginConfigurationImpl configuration,
				DBConnectionManager connectionManager) {
			super(pluginContext, databaseDSConfiguration, configuration, connectionManager);
		}

		@Override
		void commitBatch(Connection connection) throws SQLException {
			connection.commit();
		}

	}
	
	public void addDBFieldMapping(AttributeDetail attributeDetail) {
		userStatsticsPluginData.getAttributeList().add(attributeDetail);
	}
	
	public static AttributeDetailBuilder dbFieldMapping() {
		return new AttributeDetailBuilder();
		
	}
	
	public static class AttributeDetailBuilder {
		public AttributeDetail attributeDetail = new AttributeDetail();
		
		public AttributeDetailBuilder() {
			attributeDetail.setUseDictionaryValue("false");
		}

		public AttributeDetailBuilder defaultValue(String defaultValue) {
			attributeDetail.setDefaultValue(defaultValue);
			return this;
		}

		public AttributeDetailBuilder attributeId(String attributeId) {
			attributeDetail.setAttributeId(attributeId);
			return this;
		}
		
		public AttributeDetailBuilder dataType(String dataType) {
			attributeDetail.setDataType(dataType);
			return this;
		}
		
		public AttributeDetailBuilder dbField(String dbField) {
			attributeDetail.setDbField(dbField);
			return this;
		}
		
		public AttributeDetailBuilder fromRequestPacket() {
			attributeDetail.setPacketType("0");
			return this;
		}
		
		public AttributeDetailBuilder fromResponsePacket() {
			attributeDetail.setPacketType("1");
			return this;
		}
		
		public AttributeDetailBuilder useDictionaryValue() {
			attributeDetail.setUseDictionaryValue("true");
			return this;
		}
		
		
	}

}
