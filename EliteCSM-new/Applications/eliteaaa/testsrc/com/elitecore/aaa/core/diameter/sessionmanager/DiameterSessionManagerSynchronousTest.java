package com.elitecore.aaa.core.diameter.sessionmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.DatabaseDSConfiguration;
import com.elitecore.aaa.core.config.DBDataSourceImpl;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.sessionmanager.DiameterSessionManagerConfigurable;
import com.elitecore.aaa.diameter.conf.sessionmanager.FieldMappingImpl;
import com.elitecore.aaa.diameter.conf.sessionmanager.SessionDataMapping;
import com.elitecore.aaa.diameter.conf.sessionmanager.SessionScenarioDetails;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl.SQLDialectFactory;
import com.elitecore.core.serverx.sessionx.db.SQLDialect;
import com.elitecore.core.serverx.sessionx.db.impl.DerbyDialectImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/***
 * 
 * @author malav.desai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class DiameterSessionManagerSynchronousTest {
	
	private DiameterSessionManagerConfigurable configurable = new DiameterSessionManagerConfigurable();
	
	private static final String BATCH_SYSTEM_PROPERTY = "sql.transaction.batch";
	private static final String NOWAIT_SYSTEM_PROPERTY = "sql.transaction.nowait";
	private final static String TESTINGDB = "TestingDB";
	private final static String TABLE_NAME = "tblmsessionmanager";
	private final static String SEQ_NAME = "SEQMSESSIONMANAGER";
	private final static long NONE_OPERATION = 0;
	private final static long INSERT_OPERATION = 1;
	private final static long DELETE_OPERATION = 2;
	private final static long UPDATE_OPERATION = 3;
	private final static String USERNAME_DB_PARAM = "param1";
	private final static String USER_PASSWORD_DB_PARAM = "param2";
	
	private DBConnectionManager dbConnectionManager;
	@Mock private AAAServerContext serverContext;
	@Mock private AAAServerConfiguration aaaServerConfig;
	@Mock private DatabaseDSConfiguration databaseDSConfiguration;
	private DBDataSource dataSource = new DBDataSourceImpl(null, "", "jdbc:derby:memory:TestingDB;create=true", 
			"", "",	2, 5, 10, 3000);
	
	static{
		DummyDiameterDictionary.getInstance();
		System.setProperty(BATCH_SYSTEM_PROPERTY, "false");
		System.setProperty(NOWAIT_SYSTEM_PROPERTY, "false");
	}
	
	class DiaSessionManagerStub extends DiameterSessionManager {
		
		private static final String ID_PARAM = "CONCUSERID";

		public DiaSessionManagerStub(AAAServerContext serverContext,
				DiameterSessionManagerConfigurable sessionManagerConfigurable) {
			super(serverContext, sessionManagerConfigurable);
		} 

		@Override
		protected SessionConfigurationImpl createSessionConfiguration() {
			return new SessionConfigurationImpl(serverContext, new DerbyDialectFactory());
		}
		
	}
	
	class DerbyDialectFactory implements SQLDialectFactory {

		@Override
		public SQLDialect newDialect(List<SchemaMapping> schemaMappingList,
				DBVendors dbVendors) {
			return new DerbyDialectImpl(schemaMappingList);
		}
		
	}
	
	@Before
	public void before() throws Exception{
		URL url = DiameterSessionManagerSynchronousTest.class.getClassLoader().getResource("session-manager-synchronous.xml");
		File file = new File(url.getFile());
		JAXBContext jaxbContext = JAXBContext.newInstance(DiameterSessionManagerConfigurable.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		configurable = (DiameterSessionManagerConfigurable) jaxbUnmarshaller.unmarshal(file);
		configurable.postReadProcessing();
		MockitoAnnotations.initMocks(this);
		Mockito.when(serverContext.getServerConfiguration()).thenReturn(aaaServerConfig);
		Mockito.when(aaaServerConfig.getDatabaseDSConfiguration()).thenReturn(databaseDSConfiguration);
		Mockito.when(databaseDSConfiguration.getDataSource(TESTINGDB)).thenReturn(dataSource);
		Mockito.when(serverContext.getTaskScheduler()).thenReturn(Mockito.mock(TaskScheduler.class));
		createTable();
	}

	private void createTable() throws Exception{
		this.dbConnectionManager = DBConnectionManager.getInstance(TESTINGDB);
		Connection conn = null;
		try{
			if(this.dbConnectionManager.isInitilized() == true){
				conn = dbConnectionManager.getConnection();
				conn.prepareCall("TRUNCATE TABLE " + TABLE_NAME).execute();
			} else {
				dbConnectionManager.init(dataSource, serverContext.getTaskScheduler());
				conn = dbConnectionManager.getConnection();
				conn.prepareCall("CREATE TABLE " + TABLE_NAME  + " (" + DiaSessionManagerStub.ID_PARAM + " INT, " + AAAServerConstants.PROTOCOL_TYPE_FIELD + " VARCHAR(30),  starttime TIMESTAMP, updatetime TIMESTAMP, param1 VARCHAR(25), param2 VARCHAR(25), param3 VARCHAR(25), param4 VARCHAR(25), param5 VARCHAR(25), param6 VARCHAR(25), param7 VARCHAR(25), session_status VARCHAR(10))").execute();
				conn.prepareCall("CREATE SEQUENCE " + SEQ_NAME + " INCREMENT BY 1 START WITH 1 NO MAXVALUE NO MINVALUE NO CYCLE").execute();
			}
			conn.commit();
		} finally {
			DBUtility.closeQuietly(conn);
		}
	}
	
	private void insertData() throws Exception {
		Connection conn = null;
		try{
			conn = dbConnectionManager.getConnection();
			conn.prepareCall(getInsertQuery("malav", "kuldeep", "kuldeep")).execute();
			conn.prepareCall(getInsertQuery("malav", "parag", "parag")).execute();
			conn.prepareCall(getInsertQuery("malav", "monica", "monica")).execute();
			conn.prepareCall(getInsertQuery("malav", "kuldeep", "malav")).execute();
			conn.commit();
		} finally {
			DBUtility.closeQuietly(conn);
		}
	}
	
//	@Test
//	public void testLocate_WhenLocatorIsDisabledFromConfiguration_ShouldReturnEmptyList() throws Exception {
//		insertData();
//		configurable.getScenarioDetails().get(0).setSessionLocator(false);
//		configurable.postReadProcessing();
//		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
//		diaSessionManager.init();
//
//		DiameterRequest diameterRequest = new DiameterRequest();
//		diameterRequest.setHop_by_hopIdentifier(1);
//		diameterRequest.setEnd_to_endIdentifier(1);
//		
//		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
//		avp.setStringValue("malav");
//		diameterRequest.addAvp(avp);
//		
//		assertArrayEquals("Expected empty list", Collections.EMPTY_LIST.toArray(), diaSessionManager.locate(diameterRequest).toArray());
//	}

	@Test
	public void testLocate_WhenLocatorIsEnabledFromConfiguration_ShouldReturnAllSessionDatasWithTheSelectedUserName() throws Exception {
		insertData();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);
		
		List<SessionData> ans = diaSessionManager.locate(diameterRequest, null);
		
		assertEquals("expected list size : 4, but Got : " + ans.size(), 4, ans.size());
		for (SessionData sessionData : ans) {
			assertEquals("expected value does not match with passed value in criteria" ,"malav", sessionData.getValue(USERNAME_DB_PARAM));
		}
	}
	
	@Test
	public void testLocate_WhenLocatorIsEnabledAndHaveMultipleCriteria_ShouldReturnAllSessionDatasWithTheSelectedCriteria() throws Exception {
		insertData();
		configurable.getScenarioDetails().get(0).setCriteria("param1,param2");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
		avp.setStringValue("kuldeep");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);
		
		List<SessionData> ans= diaSessionManager.locate(diameterRequest, null);
		assertEquals("expected list size : 2 but Got : " + ans.size(), 2, ans.size());
		for (SessionData sessionData : ans) {
			assertEquals("expected value does not match with passed value in criteria" ,"malav", sessionData.getValue(USERNAME_DB_PARAM));
			assertEquals("expected value does not match with passed value in criteria" ,"kuldeep", sessionData.getValue(USER_PASSWORD_DB_PARAM));
		}
	}

	@Test
	public void testLocate_WhenAllConfiguredCriteriaAVPsAreNotRecievedInTheRequestOrEmpty_ShouldRejectTheResponse() throws Exception {
		insertData();
		configurable.getScenarioDetails().get(0).setCriteria("param3");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
		avp.setStringValue("kuldeep");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);
		
		diaSessionManager.locate(diameterRequest, diameterAnswer);
		
		DiameterAssertion.assertThat(diameterAnswer)
		.hasResultCode(ResultCode.DIAMETER_MISSING_AVP)
		.containsAVP(DiameterAVPConstants.ERROR_MESSAGE,"Session Manager Operation Failed due to Missing AVP/s.")
		.containsAVP("0:279.0:280","");
	}
	

	@Test
	public void testLocate_WithDifferentDelimeterThanDefault_ShouldReturnAllSessionDatasWithTheSelectedCriteria() throws Exception {
		insertData();
		configurable.setMultiValueDelimeter(";");
		configurable.getScenarioDetails().get(0).setCriteria("param1;param2");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
		avp.setStringValue("kuldeep");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);
		
		List<SessionData> ans= diaSessionManager.locate(diameterRequest, null);
		assertEquals("expected list size : 2 but Got : " + ans.size(), 2, ans.size());
		for (SessionData sessionData : ans) {
			assertEquals("expected value does not match with passed value in criteria" ,"malav", sessionData.getValue(USERNAME_DB_PARAM));
			assertEquals("expected value does not match with passed value in criteria" ,"kuldeep", sessionData.getValue(USER_PASSWORD_DB_PARAM));
		}
	}
	
	@Test
	public void testLocate_WhenDelimeterInCriteriaIsDifferentFromProvidedDelimeterInConfiguration_ShouldReturnNull() throws Exception {
		insertData();
		configurable.setMultiValueDelimeter(";");
		configurable.getScenarioDetails().get(0).setCriteria("param1,param2");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
		avp.setStringValue("kuldeep");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);
		
		List<SessionData> ans= diaSessionManager.locate(diameterRequest, diameterAnswer);
		assertNull("Expected null",ans);
	}
	
	public static Object[][] dataFor_testSave_ShouldInsertData_IfExpressionIsSatisfied(){
		return new Object[][]{
				{new String[]{"malav", "malav1", "malav2","1000"}},
				{new String[]{"malav", "malav1", "malav2", "kailash","1000"}},
				{new String[]{"malav", "kailash", "malav2","1000"}},
				{new String[]{"malav", "kailash", "malav","1000"}},
				{new String[]{"abc", "123", "xyz", "789","1000"}}
		};
	}
	
	@Test
	@Parameters(method="dataFor_testSave_ShouldInsertData_IfExpressionIsSatisfied")
	public void testSave_ShouldInsertData_IfExpressionIsSatisfied(String...strings) throws Exception {
		configurable.getScenarioDetails().get(0).setExpression(DiameterAVPConstants.USER_NAME + "=\"malav*\"");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		
		
		for (String string : strings) {
			DiameterRequest diameterRequest = new DiameterRequest();
			diameterRequest.setHop_by_hopIdentifier(1);
			diameterRequest.setEnd_to_endIdentifier(1);
			
			IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.PROXY_HOST);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			DiameterAnswer diameterAnswer = new DiameterAnswer();
			

			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);

			diameterAnswer.setHop_by_hopIdentifier(1);
			diameterAnswer.setEnd_to_endIdentifier(1);
			
			if(string.startsWith("malav")){
				assertEquals(1, diaSessionManager.save(diameterRequest, diameterAnswer));
			} else {
				assertEquals(-1, diaSessionManager.save(diameterRequest, diameterAnswer));
			}
		}

		Connection connection = dbConnectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME); 
			resultSet = preparedStatement.executeQuery();
			for (String string : strings) {
				if(string.startsWith("malav")){
					resultSet.next();
					assertEquals("", string, resultSet.getString("param1"));
					assertEquals("", string, resultSet.getString("param2"));
					assertEquals("", string, resultSet.getString("param3"));
				}
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@Test
	public void testSave_ShouldInsertDefaultValuesProvided_WhenIdentifierAttributeIsNotAvailableInThePacket() throws Exception {
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);
			
		DiameterAnswer diameterAnswer = new DiameterAnswer();

		diameterAnswer.setHop_by_hopIdentifier(1);
		diameterAnswer.setEnd_to_endIdentifier(1);
		
		assertEquals(1, diaSessionManager.save(diameterRequest, diameterAnswer));

		Connection connection = dbConnectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME); 
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			assertEquals("", "malav", resultSet.getString("param1"));
			assertEquals("", "hello2", resultSet.getString("param2"));
			assertEquals("", "hello3", resultSet.getString("param3"));
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	public static Object[][] dataFor_testSave_ShouldTryToGetValuesFromAttributesSequentially_WhenMultipleAttributesAreConfiguredInRefferringAttributes(){
		return new Object[][]{
				{"password",			DiameterAVPConstants.USER_PASSWORD + "," + DiameterAVPConstants.ACCT_MULTI_SESSION_ID + "," + DiameterAVPConstants.PROXY_HOST,
					new String[]{DiameterAVPConstants.USER_PASSWORD, "password"}},
					
				{"session id",			DiameterAVPConstants.USER_PASSWORD + "," + DiameterAVPConstants.ACCT_MULTI_SESSION_ID + "," + DiameterAVPConstants.PROXY_HOST,
					new String[]{DiameterAVPConstants.ACCT_MULTI_SESSION_ID, "session id"}},
					
				{"proxy host",			DiameterAVPConstants.USER_PASSWORD + "," + DiameterAVPConstants.ACCT_MULTI_SESSION_ID + "," + DiameterAVPConstants.PROXY_HOST,
					new String[]{DiameterAVPConstants.PROXY_HOST, "proxy host"}},
					
				{"password", 			DiameterAVPConstants.USER_PASSWORD + "," + DiameterAVPConstants.ACCT_MULTI_SESSION_ID + "," + DiameterAVPConstants.PROXY_HOST,
					new String[]{DiameterAVPConstants.USER_PASSWORD, "password", DiameterAVPConstants.ACCT_MULTI_SESSION_ID, "session id", DiameterAVPConstants.PROXY_HOST, "proxy host"}},
					
				{"session id",			DiameterAVPConstants.USER_PASSWORD + "," + DiameterAVPConstants.ACCT_MULTI_SESSION_ID + "," + DiameterAVPConstants.PROXY_HOST,
					new String[]{DiameterAVPConstants.ACCT_MULTI_SESSION_ID, "session id", DiameterAVPConstants.PROXY_HOST, "proxy host"}},
		};
	}
	
	@Test
	@Parameters(method="dataFor_testSave_ShouldTryToGetValuesFromAttributesSequentially_WhenMultipleAttributesAreConfiguredInRefferringAttributes")
	public void testSave_ShouldTryToGetValuesFromAttributesSequentially_WhenMultipleAttributesAreConfiguredInRefferringAttributes(
			String expectedValue, String refferingAttributes, String...attributeString) throws Exception {
		configurable.getScenarioDetails().get(0).setFieldMappingName("list2");
				List<SessionDataMapping> sessionDatas = new ArrayList<SessionDataMapping>();
				SessionDataMapping mappingList = new SessionDataMapping();
				mappingList.setName("list2");
					List<FieldMappingImpl> fieldMappings = new ArrayList<FieldMappingImpl>();
					fieldMappings.add(new FieldMappingImpl("param1", DiameterAVPConstants.USER_NAME, FieldMapping.STRING_TYPE, "hello1"));
					fieldMappings.add(new FieldMappingImpl("param2", refferingAttributes, FieldMapping.STRING_TYPE, "hello2"));
				mappingList.setFeildMappings(fieldMappings);
			sessionDatas.add(mappingList);
			sessionDatas.add(configurable.getSessionDatas().get(0));
		configurable.setSessionDatas(sessionDatas);
		configurable.postReadProcessing();
		
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		IDiameterAVP sessionIdAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		sessionIdAVP.setStringValue("1000");
		diameterRequest.addAvp(sessionIdAVP);
		
		for (int i=0; i<attributeString.length;) {
			avp = DiameterDictionary.getInstance().getKnownAttribute(attributeString[i++]);
			avp.setStringValue(attributeString[i++]);
			diameterRequest.addAvp(avp);
		}
			
		DiameterAnswer diameterAnswer = new DiameterAnswer();

		diameterAnswer.setHop_by_hopIdentifier(1);
		diameterAnswer.setEnd_to_endIdentifier(1);
		
		assertEquals(1, diaSessionManager.save(diameterRequest, diameterAnswer));

		Connection connection = dbConnectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			assertEquals("", "malav", resultSet.getString("param1"));
			assertEquals("", expectedValue, resultSet.getString("param2"));
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@Test
	@Parameters(method="dataFor_testSave_ShouldInsertData_IfExpressionIsSatisfied")
	public void testUpdate_ShouldUpdateData_IfExpressionIsSatisfied(String...strings) throws Exception {
		configurable.getScenarioDetails().get(0).setExpression(DiameterAVPConstants.USER_NAME + "=\"malav*\"");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		
		Connection connection = null;
		try{
			connection = dbConnectionManager.getConnection();
			for(String string : strings){
				connection.prepareCall("INSERT INTO " + TABLE_NAME + " (param1, param2, param3) VALUES ('" + string + "', '" + string + "', '" + string + "')").execute();
			}
			connection.commit();
		} finally {
			DBUtility.closeQuietly(connection);
		}
		
		
		for (String string : strings) {
			DiameterRequest diameterRequest = new DiameterRequest();
			diameterRequest.setHop_by_hopIdentifier(1);
			diameterRequest.setEnd_to_endIdentifier(1);
			
			IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
			avp.setStringValue(string + " updated");
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.PROXY_HOST);
			avp.setStringValue(string + " updated");
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
			avp.setStringValue(string + " updated");
			diameterRequest.addAvp(avp);
			
			DiameterAnswer diameterAnswer = new DiameterAnswer();
			diameterAnswer.setHop_by_hopIdentifier(1);
			diameterAnswer.setEnd_to_endIdentifier(1);
			
			if(string.startsWith("malav")){
				assertNotSame( -1, diaSessionManager.update(diameterRequest, diameterAnswer));
			}else {
				assertEquals( -1, diaSessionManager.update(diameterRequest, diameterAnswer));
			}
		}

		connection = dbConnectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME);
			resultSet = preparedStatement.executeQuery();
			for (String string : strings) {
					resultSet.next();
					assertEquals("", string, resultSet.getString("param1"));
					if(string.startsWith("malav")){
						string += " updated";
					}
					assertEquals("", string, resultSet.getString("param2"));
					assertEquals("", string, resultSet.getString("param3"));
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@Test
	public void testUpdate_ShouldNotUpdateDefaultValuesProvided_WhenIdentifierAttributeIsNotAvailableInThePacket() throws Exception {
		Connection connection = null;
		try{
			connection = dbConnectionManager.getConnection();
			connection.prepareCall("INSERT INTO " + TABLE_NAME + " (param1, param2, param3) VALUES ('malav', 'malav', 'malav')").execute();
			connection.commit();
		} finally {
			DBUtility.closeQuietly(connection);
		}
		
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
		avp.setStringValue("desai");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);
			
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		diameterAnswer.setHop_by_hopIdentifier(1);
		diameterAnswer.setEnd_to_endIdentifier(1);
		
		assertNotSame( -1, diaSessionManager.update(diameterRequest, diameterAnswer));

		connection = dbConnectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			assertEquals("", "malav", resultSet.getString("param1"));
			assertEquals("", "desai", resultSet.getString("param2"));
			assertEquals("", "malav", resultSet.getString("param3"));
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@Test
	@Parameters(method="dataFor_testSave_ShouldTryToGetValuesFromAttributesSequentially_WhenMultipleAttributesAreConfiguredInRefferringAttributes")
	public void testUpdate_ShouldTryToGetValuesFromAttributesSequentially_WhenMultipleAttributesAreConfiguredInRefferringAttributes(
			String valueToBeUpdated, String refferingAttributes, String...attributeString) throws Exception {
		configurable.getScenarioDetails().get(0).setFieldMappingName("list2");
				List<SessionDataMapping> sessionDatas = new ArrayList<SessionDataMapping>();
				SessionDataMapping mappingList = new SessionDataMapping();
				mappingList.setName("list2");
					List<FieldMappingImpl> fieldMappings = new ArrayList<FieldMappingImpl>();
					fieldMappings.add(new FieldMappingImpl("param1", DiameterAVPConstants.USER_NAME, FieldMapping.STRING_TYPE, "hello1"));
					fieldMappings.add(new FieldMappingImpl("param2", refferingAttributes, FieldMapping.STRING_TYPE, "hello2"));
				mappingList.setFeildMappings(fieldMappings);
			sessionDatas.add(mappingList);
			sessionDatas.add(configurable.getSessionDatas().get(0));
		configurable.setSessionDatas(sessionDatas);
		configurable.postReadProcessing();
		
		Connection connection = null;
		try{
			connection = dbConnectionManager.getConnection();
			connection.prepareCall("INSERT INTO " + TABLE_NAME + " (param1, param2) VALUES ('malav','" + valueToBeUpdated + "')").execute();
			connection.commit();
		} finally {
			DBUtility.closeQuietly(connection);
		}
		
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);
		
		for (int i=0; i<attributeString.length;) {
			avp = DiameterDictionary.getInstance().getKnownAttribute(attributeString[i++]);
			avp.setStringValue(attributeString[i++] + " updated");
			diameterRequest.addAvp(avp);
		}
			
		DiameterAnswer diameterAnswer = new DiameterAnswer();

		diameterAnswer.setHop_by_hopIdentifier(1);
		diameterAnswer.setEnd_to_endIdentifier(1);
		
		assertNotSame( -1, diaSessionManager.update(diameterRequest, diameterAnswer));

		connection = dbConnectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			assertEquals("", "malav", resultSet.getString("param1"));
			assertEquals("", valueToBeUpdated + " updated", resultSet.getString("param2"));
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@Test
	@Parameters(method="dataFor_testSave_ShouldInsertData_IfExpressionIsSatisfied")
	public void testDelete_ShouldDeleteDataUsingCriteria_IfExpressionIsSatisfied(String...strings) throws Exception {
		configurable.getScenarioDetails().get(0).setExpression(DiameterAVPConstants.USER_NAME + "=\"malav*\"");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		
		Connection connection = null;
		try{
			connection = dbConnectionManager.getConnection();
			for(String string : strings){
				connection.prepareCall("INSERT INTO " + TABLE_NAME + " (param1, param2, param3) VALUES ('" + string + "', '" + string + "', '" + string + "')").execute();
			}
			connection.commit();
		} finally {
			DBUtility.closeQuietly(connection);
		}
		
		
		for (String string : strings) {
			DiameterRequest diameterRequest = new DiameterRequest();
			diameterRequest.setHop_by_hopIdentifier(1);
			diameterRequest.setEnd_to_endIdentifier(1);
			
			IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.PROXY_HOST);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			DiameterAnswer diameterAnswer = new DiameterAnswer();
			diameterAnswer.setHop_by_hopIdentifier(1);
			diameterAnswer.setEnd_to_endIdentifier(1);
			
			diameterRequest.setLocatedSessionData(diaSessionManager.locate(diameterRequest, null));
			
			if(string.startsWith("malav")){
				assertNotSame( -1, diaSessionManager.delete(diameterRequest, diameterAnswer));
			}else {
				assertEquals( -1, diaSessionManager.delete(diameterRequest, diameterAnswer));
			}
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		for (String string : strings) {
			try{
				connection = dbConnectionManager.getConnection();
				preparedStatement = connection.prepareStatement("select " + DiaSessionManagerStub.ID_PARAM + " from " + TABLE_NAME + " where param1 = '" + string + "'");
				resultSet = preparedStatement.executeQuery();
				boolean expectedResultSet = true;
				if(string.startsWith("malav")){
					expectedResultSet = false;
				}
				assertEquals(expectedResultSet, resultSet.next());
			}finally {
				DBUtility.closeQuietly(resultSet);
				DBUtility.closeQuietly(preparedStatement);
				DBUtility.closeQuietly(connection);
			}
		}
	}
	
	@Test
	@Parameters(method="dataFor_testSave_ShouldInsertData_IfExpressionIsSatisfied")
	public void testDelete_ShouldDeleteDataByProvidingSessionData_IfExpressionIsSatisfied(String...strings) throws Exception {
		configurable.getScenarioDetails().get(0).setExpression(DiameterAVPConstants.USER_NAME + "=\"malav*\"");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		
		Connection connection = null;
		try{
			connection = dbConnectionManager.getConnection();
			for(String string : strings){
				connection.prepareCall("INSERT INTO " + TABLE_NAME + " (param1, param2, param3) VALUES ('" + string + "', '" + string + "', '" + string + "')").execute();
			}
			connection.commit();
		} finally {
			DBUtility.closeQuietly(connection);
		}
		
		List<SessionData> sessionData = new ArrayList<SessionData>();
		for (String string : strings) {
			DiameterRequest diameterRequest = new DiameterRequest();
			diameterRequest.setHop_by_hopIdentifier(1);
			diameterRequest.setEnd_to_endIdentifier(1);
			
			IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.PROXY_HOST);
			avp.setStringValue(string);
			diameterRequest.addAvp(avp);
			
			DiameterAnswer diameterAnswer = new DiameterAnswer();
			diameterAnswer.setHop_by_hopIdentifier(1);
			diameterAnswer.setEnd_to_endIdentifier(1);
			
			sessionData.addAll(diaSessionManager.locate(diameterRequest, null));
		}
		assertNotSame( -1, diaSessionManager.delete(sessionData));

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		for (String string : strings) {
			try{
				connection = dbConnectionManager.getConnection();
				preparedStatement = connection.prepareStatement("select " + DiaSessionManagerStub.ID_PARAM + " from " + TABLE_NAME + " where param1 = '" + string + "'");
				resultSet = preparedStatement.executeQuery();
				boolean expectedResultSet = true;
//				if(string.startsWith("malav")){
//					expectedResultSet = false;
//				}
				assertEquals(expectedResultSet, resultSet.next());
			}finally {
				DBUtility.closeQuietly(resultSet);
				DBUtility.closeQuietly(preparedStatement);
				DBUtility.closeQuietly(connection);
			}
		}
	}
	
	@Test
	public void testDelete_ShouldReturnMinus1_WhenNoScenarioIsAvailable() throws Exception{
		insertData();
		configurable.setScenarioDetails(new ArrayList<SessionScenarioDetails>());
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		List<SessionData> sessionData = diaSessionManager.locate(diameterRequest,null);
		assertEquals(-1, diaSessionManager.delete(sessionData));
	}
	
	@Test
	public void testTruncate_ShouldTrucateTheTable() throws Exception{
		insertData();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		
		assertNotSame( -1, diaSessionManager.truncate());
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			connection = dbConnectionManager.getConnection();
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next()){
				fail("truncation has failed");
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@Test
	public void testTruncate_ShouldReturnMinus1_WhenNoScenarioIsAvailable() throws Exception{
		insertData();
		configurable.setScenarioDetails(new ArrayList<SessionScenarioDetails>());
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		
		assertEquals(-1, diaSessionManager.truncate());
	}
	
	@Test
	public void testSave_ShouldSaveUserPasswordFromDiameterAnswerPacket_WhenRefferingAttributeContainsUserPasswordResponseMapping() throws Exception{
				List<SessionDataMapping> sessionDatas = new ArrayList<SessionDataMapping>();
				SessionDataMapping mappingList = new SessionDataMapping();
				mappingList.setName("list1");
					List<FieldMappingImpl> fieldMappings = new ArrayList<FieldMappingImpl>();
					fieldMappings.add(new FieldMappingImpl("param1", DiameterAVPConstants.USER_NAME, FieldMapping.STRING_TYPE, "hello1"));
					fieldMappings.add(new FieldMappingImpl("param2", "$RES(" + DiameterAVPConstants.USER_PASSWORD + ")", FieldMapping.STRING_TYPE, "hello2"));
					fieldMappings.add(new FieldMappingImpl("param3", "$REQ(" + DiameterAVPConstants.PROXY_HOST + ")", FieldMapping.STRING_TYPE, "hello3"));
				mappingList.setFeildMappings(fieldMappings);
			sessionDatas.add(mappingList);
		configurable.setSessionDatas(sessionDatas);
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);
		
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
		avp.setStringValue("req pass");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.PROXY_HOST);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);
		
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		diameterAnswer.setHop_by_hopIdentifier(1);
		diameterAnswer.setEnd_to_endIdentifier(1);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
		avp.setStringValue("res pass");
		diameterAnswer.addAvp(avp);
		
		if(diaSessionManager.save(diameterRequest, diameterAnswer) < 0){
			fail("session manager operation failed but intend to success");
		}
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			connection = dbConnectionManager.getConnection();
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME);
			resultSet = preparedStatement.executeQuery();
			int resultSetSize = 0;
			while (resultSet.next()) {
				resultSetSize++;
				assertEquals("malav", resultSet.getString("param1"));
				assertEquals("res pass", resultSet.getString("param2"));
				assertEquals("malav", resultSet.getString("param3"));
			}
			if(resultSetSize != 1){
				fail("fail while saving values from response packet");
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	public static Object[][] dataFor_testSessionOverrideScenario_ShouldOverrideAskedDatabaseAction_AsPerValueProvidedInECSessionActionAVP_WhenDiameterPacketContainsECSessionActionAVP(){
		return new Object[][]{
				
				{INSERT_OPERATION,		NONE_OPERATION,			getInsertQuery("malav", "malav", "malav"),
					"malav",	"avpValue",		"malav",	 	1},
				{INSERT_OPERATION,		INSERT_OPERATION,		"values 1",
					"malav", 	"avpValue", 	"avpValue",		1},
				{INSERT_OPERATION,		UPDATE_OPERATION,		getInsertQuery("malav", "malav", "malav"),
					"malav", 	"avpValue2",	"avpValue2",	1},
				{INSERT_OPERATION,		DELETE_OPERATION,		getInsertQuery("malav", "malav", "malav"),
					"malav", 	"avpValue",		"avpValue",		0},
				{INSERT_OPERATION,		8,						"values 1",
					"malav", 	"avpValue",		"avpValue",		1},
				
				{UPDATE_OPERATION,		NONE_OPERATION,			getInsertQuery("malav", "malav", "malav"),
					"malav",	"avpValue",		"malav",	 	1},
				{UPDATE_OPERATION,		INSERT_OPERATION,		"values 1",
					"malav", 	"avpValue", 	"avpValue",		1},
				{UPDATE_OPERATION,		UPDATE_OPERATION,		getInsertQuery("malav", "malav", "malav"),
					"malav", 	"avpValue2",	"avpValue2",	1},
				{UPDATE_OPERATION,		DELETE_OPERATION,		getInsertQuery("malav", "malav", "malav"),
					"malav", 	"avpValue",		"avpValue",		0},
				{UPDATE_OPERATION,		11,						getInsertQuery("malav", "malav", "malav"),
					"malav", 	"avpValue",		"avpValue",		1},

				{DELETE_OPERATION,		NONE_OPERATION,			getInsertQuery("malav", "malav", "malav"),
					"malav",	"avpValue",		"malav",	 	1},
				{DELETE_OPERATION,		INSERT_OPERATION,		"values 1",
					"malav", 	"avpValue", 	"avpValue",		1},
				{DELETE_OPERATION,		UPDATE_OPERATION,		getInsertQuery("malav", "malav", "malav"),
					"malav", 	"avpValue2",	"avpValue2",	1},
				{DELETE_OPERATION,		DELETE_OPERATION,		getInsertQuery("malav", "malav", "malav"),
					"malav", 	"avpValue",		"avpValue",		0},
				{DELETE_OPERATION,		4,						getInsertQuery("malav", "malav", "malav"),
					"malav", 	"avpValue",		"avpValue",		0},
		};
	}
	
	@Test
	@Parameters(method="dataFor_testSessionOverrideScenario_ShouldOverrideAskedDatabaseAction_AsPerValueProvidedInECSessionActionAVP_WhenDiameterPacketContainsECSessionActionAVP")
	public void testSessionOverrideScenario_ShouldOverrideAskedDatabaseAction_AsPerValueProvidedInECSessionActionAVP_WhenDiameterPacketContainsECSessionActionAVP(
			long defaultAction, long sessionAction, String queryString, String username, String avpValue, String expectedAvpValueFromDb, long expectedResultSetSize) throws Exception {
		configurable.getScenarioDetails().get(0).setExpression(DiameterAVPConstants.USER_NAME + "=\"malav*\"");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		diaSessionManager.init();
		
		Connection connection = null;
		try{
			connection = dbConnectionManager.getConnection();
			connection.prepareCall(queryString).execute();
			connection.commit();
		} finally {
			DBUtility.closeQuietly(connection);
		}

		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);

		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue(username);
		diameterRequest.addAvp(avp);

		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_PASSWORD);
		avp.setStringValue(avpValue);
		diameterRequest.addAvp(avp);

		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.PROXY_HOST);
		avp.setStringValue(avpValue);
		diameterRequest.addAvp(avp);
		
		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.EC_SESSION_ACTION);
		avp.setInteger(sessionAction);
		diameterRequest.addAvp(avp);

		avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_ID);
		avp.setStringValue("1000");
		diameterRequest.addAvp(avp);

		DiameterAnswer diameterAnswer = new DiameterAnswer();
		diameterAnswer.setHop_by_hopIdentifier(1);
		diameterAnswer.setEnd_to_endIdentifier(1);

		diameterRequest.setLocatedSessionData(diaSessionManager.locate(diameterRequest, null));
		
		int result = -1;
		if(defaultAction == INSERT_OPERATION) {
			result = diaSessionManager.save(diameterRequest, diameterAnswer);
		} else if(defaultAction == UPDATE_OPERATION) {
			result = diaSessionManager.update(diameterRequest, diameterAnswer);
		} else if(defaultAction == DELETE_OPERATION) {
			result = diaSessionManager.delete(diameterRequest, diameterAnswer);
		}
		if(result < 0){
			fail("session manager operation failed but intend to success");
		}

		connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			connection = dbConnectionManager.getConnection();
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME + " where param1 = '" + username + "'");
			resultSet = preparedStatement.executeQuery();
			int resultSetSize = 0;
			while (resultSet.next()) {
				resultSetSize++;
				assertEquals("param1", username, resultSet.getString("param1"));
				assertEquals("param2", expectedAvpValueFromDb, resultSet.getString("param2"));
				assertEquals("param3", expectedAvpValueFromDb, resultSet.getString("param3"));
			}
			if(resultSetSize != expectedResultSetSize){
				fail("actual result set size: " + resultSetSize + " does not match expected size: " + expectedResultSetSize);
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
//	public static Object[][] dataFor_testInit_ShouldNotCreateSessionScenario_WhenFieldMappingNameIsNotProper(){
//		return new Object[][]{
//				{null},
//				{""},
//				{"wrongFieldMappingName"},
//		};
//	}
//	
//	@Test
//	@Parameters(method="dataFor_testInit_ShouldNotCreateSessionScenario_WhenFieldMappingNameIsNotProper")
//	public void testInit_ShouldNotCreateSessionScenario_WhenFieldMappingNameIsNotProper(String fieldMappingName) throws Exception{
//		insertData();
//		configurable.getScenarioDetails().get(0).setFieldMappingName(fieldMappingName);
//		DiameterSessionManager diaSessionManager = new DiameterSessionManager(serverContext, configurable);
//		diaSessionManager.init();
//		
//		DiameterRequest diameterRequest = new DiameterRequest();
//		diameterRequest.setHop_by_hopIdentifier(1);
//		diameterRequest.setEnd_to_endIdentifier(1);
//
//		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
//		avp.setStringValue("malav");
//		diameterRequest.addAvp(avp);
//
//		DiameterAnswer diameterAnswer = new DiameterAnswer();
//		diameterAnswer.setHop_by_hopIdentifier(1);
//		diameterAnswer.setEnd_to_endIdentifier(1);
//		
//		if(diaSessionManager.delete(diameterRequest, diameterAnswer) >= 0){
//			fail("session manager operation succeed but intend to fail");
//		}
//		
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//		try{
//			connection = dbConnectionManager.getConnection();
//			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME + " where param1 = 'malav'");
//			resultSet = preparedStatement.executeQuery();
//			int resultSetSize = 0;
//			while (resultSet.next()) {
//				resultSetSize++;
//				assertEquals("param1", "malav", resultSet.getString("param1"));
//			}
//			if(resultSetSize != 4){
//				fail("fail while saving values from response packet");
//			}
//		} finally {
//			DBUtility.closeQuietly(resultSet);
//			DBUtility.closeQuietly(preparedStatement);
//			DBUtility.closeQuietly(connection);
//		}
//	}
	
	@Test
	public void testInit_ShouldNotCreateSessionScenario_WhenCriteriaIsEmptyString() throws Exception{
		insertData();
		configurable.getScenarioDetails().get(0).setCriteria("");
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiameterSessionManager(serverContext, configurable);
		diaSessionManager.init();
		
		DiameterRequest diameterRequest = new DiameterRequest();
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);

		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
		avp.setStringValue("malav");
		diameterRequest.addAvp(avp);

		DiameterAnswer diameterAnswer = new DiameterAnswer();
		diameterAnswer.setHop_by_hopIdentifier(1);
		diameterAnswer.setEnd_to_endIdentifier(1);
		
		if(diaSessionManager.delete(diameterRequest, diameterAnswer) >= 0){
			fail("session manager operation succeed but intend to fail");
		}
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			connection = dbConnectionManager.getConnection();
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME + " where param1 = 'malav'");
			resultSet = preparedStatement.executeQuery();
			int resultSetSize = 0;
			while (resultSet.next()) {
				resultSetSize++;
				assertEquals("param1", "malav", resultSet.getString("param1"));
			}
			if(resultSetSize != 4){
				fail("fail while saving values from response packet");
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@Test(expected=NullPointerException.class)
	public void testInit_ShouldThrowNullPointerException_WhenCriteriaIsNull() throws InitializationFailedException{
		configurable.getScenarioDetails().get(0).setCriteria(null);
		configurable.postReadProcessing();
		DiameterSessionManager diaSessionManager = new DiameterSessionManager(serverContext, configurable);
		diaSessionManager.init();
	}
	
	@Test(expected=NullPointerException.class)
	public void testInit_ShouldThrowNullPointerException_WhenSessionManagerConfigurableIsNull() throws InitializationFailedException{
		DiameterSessionManager diaSessionManager = new DiameterSessionManager(serverContext, null);
		diaSessionManager.init();
	}
	
	@Test(expected=NullPointerException.class)
	public void testInit_ShouldThrowNullPointerException_WhenServerContextIsNull() throws InitializationFailedException{
		DiameterSessionManager diaSessionManager = new DiameterSessionManager(null, configurable);
		diaSessionManager.init();
	}
	
	private static String getInsertQuery(String username, String passwd, String proxy){
		return "INSERT INTO " + TABLE_NAME + " (param1, param2, param3) VALUES ('" + username + "', '" + passwd + "', '" + proxy + "')";
	}
	
	@Test
	public void byDefaultCreatesAnActiveSession() throws InitializationFailedException, SQLException {
		DiameterSessionManager diameterSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		DiameterRequest diameterRequest = new DiameterRequest();
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		setUpRequest(diameterRequest, diameterAnswer );

		diameterSessionManager.init();
		diameterSessionManager.save(diameterRequest, diameterAnswer);

		Connection connection = dbConnectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME); 
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			assertEquals(AAAServerConstants.SESSION_STATUS_ACTIVE, resultSet.getString("session_status"));
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
}

	}
	
	@Test
	public void usesValueOfSessionStatusAvpIfPresentInRequest() throws InitializationFailedException, SQLException {
		DiameterSessionManager diameterSessionManager = new DiaSessionManagerStub(serverContext, configurable);
		DiameterRequest diameterRequest = new DiameterRequest();
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		setUpRequest(diameterRequest, diameterAnswer );
		diameterRequest.addAvp(DiameterAVPConstants.EC_SESSION_STATUS,"INACTIVE");
		
		diameterSessionManager.init();
		diameterSessionManager.save(diameterRequest, diameterAnswer);

		Connection connection = dbConnectionManager.getConnection();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			preparedStatement = connection.prepareStatement("select * from " + TABLE_NAME); 
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			assertEquals(AAAServerConstants.SESSION_STATUS_INACTIVE, resultSet.getString("session_status"));
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	private void setUpRequest(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) {
		diameterRequest.setHop_by_hopIdentifier(1);
		diameterRequest.setEnd_to_endIdentifier(1);

		diameterRequest.addAvp(DiameterAVPConstants.USER_NAME, "malav");
		diameterRequest.addAvp(DiameterAVPConstants.USER_PASSWORD,"malav");
		diameterRequest.addAvp(DiameterAVPConstants.PROXY_HOST,"test");
		diameterRequest.addAvp(DiameterAVPConstants.SESSION_ID,"session1");

	}
}
