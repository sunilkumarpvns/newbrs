package com.elitecore.netvertex.core.session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.Nonnull;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl.SQLDialectFactory;
import com.elitecore.core.serverx.sessionx.db.SQLDialect;
import com.elitecore.core.serverx.sessionx.db.impl.DerbyDialectImpl;
import com.elitecore.core.serverx.sessionx.impl.FieldMappingImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupConfiguration;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.session.conf.SessionManagerConfiguration;
import com.elitecore.netvertex.core.util.Maps;
import com.elitecore.netvertex.core.util.Maps.Entry;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;


import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnitParamsRunner.class)
public class NetvertexSessionManagerTest {

	private static final String DS_NAME = "temp";
	private static final String CONNECTION_URL = "jdbc:derby:memory:TestingDB;create=true";
	private static final String CORE_SESSION_ID_VAL = "id";
	private static final String SESSION_MANAGER_ID_VAL = "101";
	private static final DBDataSourceImpl DERBY_DS = new DBDataSourceImpl(
																				"1",
																				DS_NAME,
																				CONNECTION_URL, 
																				"", 
																				"", 
																				1, 
																				5, 
																				0, 
																				10, 
																				10
																			);
	
	private static Connection connection;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		connection = createConnection();
		create_Table(connection);
	}
	
	private static Connection createConnection() throws Exception{		
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		try {
			Connection connection = DriverManager.getConnection(CONNECTION_URL);
			connection = spy(connection);
			return connection;
		} catch (SQLException e) {
			fail("Problem in creating Derby Database Connection, Reason: "+e.getMessage());
		}
		return null;

		
		
	}

	private static void create_Table(Connection connection) throws SQLException {
		CoreSessionTableDetail.createTable(connection);
		SessionRuleTableDetail.createTable(connection);
	}

	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		CoreSessionTableDetail.dropTable(connection);
		SessionRuleTableDetail.dropTable(connection);
		DBUtility.closeQuietly(connection);
		DerbyUtil.closeDerby("TestingDB");
	}

	@After
	public void tearDown() throws Exception {
		CoreSessionTableDetail.truncateTable(connection);
		SessionRuleTableDetail.truncateTable(connection);
	}

	
	
	public Object[][] data_provider_for_getCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID() {
		
		return new Object[][] {
				$("Core-Sessions with ID :" + CORE_SESSION_ID_VAL +" must be found", 
				Arrays.asList(Maps.<PCRFKeyConstants,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID, SESSION_MANAGER_ID_VAL)
						))),
				CORE_SESSION_ID_VAL,
				Maps.<String,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
				))
				),
				
				$("Core-Sessions with ID :otherCoreSessionId must not be found",
				Arrays.asList(Maps.<PCRFKeyConstants,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, SESSION_MANAGER_ID_VAL)
						))),
				"otherCoreSessionId",
				null
				)
		};
		
		
	}

	@Test
	@Parameters(method="data_provider_for_getCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID")
	public void test_getCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID(String message,
																				List<LinkedHashMap<PCRFKeyConstants,String>> data,
																				String coreSessionID,
																				LinkedHashMap<String,String> expectedData) 
																				throws SQLException, Exception {
		CoreSessionTableDetail.insert(data);
		
		NetvertexSessionManager netvertexSessionManager = createNetvertexSessionManager(false);
		
		
		SessionData sessionData = netvertexSessionManager.getSessionLookup().getCoreSessionByCoreSessionID(coreSessionID);
		
		if(sessionData == null && expectedData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData == null){
			Assert.assertTrue(true);
			return;
		}
		
		

		LinkedHashMap<String, String> actualData = new LinkedHashMap<String, String>();
		
		for (String key : sessionData.getKeySet()) {
			actualData.put(key, sessionData.getValue(key));
		}
		
		
			
		ReflectionAssert.assertReflectionEquals(message,expectedData, actualData,ReflectionComparatorMode.LENIENT_ORDER);
		
	}
	
	
	public Object[][] data_provider_for_getCoreSessionByCoreSessionID_should_cache_the_value_once_data_fetched_from_DB() {
		
		return new Object[][] {
				$("Core-Sessions with ID :" + CORE_SESSION_ID_VAL +" must be found", 
				Arrays.asList(Maps.<PCRFKeyConstants,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID, SESSION_MANAGER_ID_VAL)
						))),
				CORE_SESSION_ID_VAL,
				Maps.<String,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
				))
				),
				
				$("Core-Sessions with ID :otherCoreSessionId must not be found",
				Arrays.asList(Maps.<PCRFKeyConstants,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, SESSION_MANAGER_ID_VAL)
						))),
				"otherCoreSessionId",
				null
				)
		};
		
		
	}

	@Test
	@Parameters(method="data_provider_for_getCoreSessionByCoreSessionID_should_cache_the_value_once_data_fetched_from_DB")
	public void test_getCoreSessionByCoreSessionID_should_cache_the_value_once_data_fetched_from_DB(String message,
																				List<LinkedHashMap<PCRFKeyConstants,String>> data,
																				String coreSessionID,
																				LinkedHashMap<String,String> expectedData) 
																				throws SQLException, Exception {
		CoreSessionTableDetail.insert(data);
		
		NetvertexSessionManager netvertexSessionManager = createNetvertexSessionManager(true);
		
	
		netvertexSessionManager.getSessionLookup().getCoreSessionByCoreSessionID(coreSessionID);
		
		CoreSessionTableDetail.truncateTable(connection);
		
		SessionData sessionData = netvertexSessionManager.getSessionLookup().getCoreSessionByCoreSessionID(coreSessionID);
		
		if(sessionData == null && expectedData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData == null){
			Assert.assertTrue(true);
			return;
		}
		
		

		LinkedHashMap<String, String> actualData = new LinkedHashMap<String, String>();
		
		for (String key : sessionData.getKeySet()) {
			actualData.put(key, sessionData.getValue(key));
		}
		
		
			
		ReflectionAssert.assertReflectionEquals(message,expectedData, actualData,ReflectionComparatorMode.LENIENT_ORDER);
		
	}
	
	public Object[][] data_provider_for_deleteCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID() {
		
		return new Object[][] {
				$("Core-Sessions with ID :" + CORE_SESSION_ID_VAL +" must not be found", 
				Arrays.asList(Maps.<PCRFKeyConstants,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID, SESSION_MANAGER_ID_VAL)
						))),
				CORE_SESSION_ID_VAL,
				CORE_SESSION_ID_VAL,
				null
				),
				
				$("Core-Sessions with ID :otherCoreSessionId must be found",
				Arrays.asList(Maps.<PCRFKeyConstants,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID, SESSION_MANAGER_ID_VAL)
						))),
				"otherCoreSessionId",
				CORE_SESSION_ID_VAL,
				Maps.<String,String>newLinkedHashMap(
					Arrays.asList(
							Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
							Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
							Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
							
				)))
				
		};
		
		
	}
	
	
	@Test
	@Parameters(method="data_provider_for_deleteCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID")
	public void test_deleteCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID(String message,
																											List<LinkedHashMap<PCRFKeyConstants,String>> data,
																											String coreSessionID,
																											String selectCriteria,
																											LinkedHashMap<String,String> expectedData) throws SQLException, Exception {
		CoreSessionTableDetail.insert(data);
		NetvertexSessionManager netvertexSessionManager = createNetvertexSessionManager(false);
		netvertexSessionManager.getSessionOperation().deleteCoreSessionByCoreSessionId(coreSessionID);
		
		SessionData sessionData = netvertexSessionManager.getSessionLookup().getCoreSessionByCoreSessionID(selectCriteria);
		
		if(sessionData == null && expectedData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData == null){
			Assert.assertTrue(true);
			return;
		}
		

		LinkedHashMap<String, String> actualData = new LinkedHashMap<String, String>();
		
		for (String key : sessionData.getKeySet()) {
			actualData.put(key, sessionData.getValue(key));
		}
	
		ReflectionAssert.assertReflectionEquals(message,expectedData, actualData,ReflectionComparatorMode.LENIENT_ORDER);
		
		
	}
	
	
	public Object[][] data_provider_for_deleteCoreSessionByCoreSessionID_should_remove_from_cache_even_though_DB_operation_fail() {
		
		Object[][] objects = new Object[][] {
				$("Core-Sessions with ID :" + CORE_SESSION_ID_VAL +" must not be found", 
				Maps.<String,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)),
				CORE_SESSION_ID_VAL,
				CORE_SESSION_ID_VAL,
				null
				),
				
				$("Core-Sessions with ID :otherCoreSessionId must be found",
				Maps.<String,String>newLinkedHashMap(
						Arrays.asList(
								Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
								Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
								Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)),
				"otherCoreSessionId",
				CORE_SESSION_ID_VAL,
				Maps.<String,String>newLinkedHashMap(
					Arrays.asList(
							Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
							Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
							Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
							
				)))
				
		};
		
		Object[][] finalObj = new Object[objects.length][];
		
		
		int index = 0;
		for(Object[] object : objects){
			PCRFResponse pcrfResponse = new PCRFResponseImpl();
			for (java.util.Map.Entry<String, String> entry : ((LinkedHashMap<String, String>)object[1]).entrySet()) {
				pcrfResponse.setAttribute(entry.getKey(), entry.getValue());
			}
			
			finalObj[index++]= new Object[]{object[0],pcrfResponse,object[2],object[3],object[4]};
		}
		
		return finalObj;
		
	}
	
	
	@Test
	@Parameters(method="data_provider_for_deleteCoreSessionByCoreSessionID_should_remove_from_cache_even_though_DB_operation_fail")
	public void test_deleteCoreSessionByCoreSessionID_should_remove_from_cache_even_though_DB_operation_fail(String message,
																											PCRFResponse pcrfResponse,
																											String coreSessionID,
																											String selectCriteria,
																											LinkedHashMap<String,String> expectedData) throws SQLException, Exception {
	
		NetvertexSessionManager netvertexSessionManager = createNetvertexSessionManager(true);
		
		netvertexSessionManager.getSessionOperation().createCoreSession(pcrfResponse);
		CoreSessionTableDetail.dropTable(connection);
		try {
			netvertexSessionManager.getSessionOperation().deleteCoreSessionByCoreSessionId(coreSessionID);	
		} finally {
			CoreSessionTableDetail.createTable(connection);
		}
		
		
		SessionData sessionData = netvertexSessionManager.getSessionLookup().getCoreSessionByCoreSessionID(selectCriteria);
		
		if(sessionData == null && expectedData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData == null){
			Assert.assertTrue(true);
			return;
		}
		

		LinkedHashMap<String, String> actualData = new LinkedHashMap<String, String>();
		
		for (String key : sessionData.getKeySet()) {
			actualData.put(key, sessionData.getValue(key));
		}
	
		ReflectionAssert.assertReflectionEquals(message,expectedData, actualData,ReflectionComparatorMode.LENIENT_ORDER);
		
		
	}
	
	public Object[][] data_provider_for_insertCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID() {
		
		Object [][] objects = new Object[][] {
				$("Core-Sessions with ID :" + CORE_SESSION_ID_VAL +" must not be found", 
				
				Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
						Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
						Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)
				)
				
				),
				
				$("Core-Sessions with ID :otherCoreSessionId must be found",
				
				Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
						Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
						Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)
				)
				)
				
		};
		
		Object[][] finalObj = new Object[objects.length][];
		
		
		int index = 0;
		for(Object[] object : objects){
			PCRFResponse pcrfResponse = new PCRFResponseImpl();
			for (java.util.Map.Entry<String, String> entry : ((LinkedHashMap<String, String>)object[1]).entrySet()) {
				pcrfResponse.setAttribute(entry.getKey(), entry.getValue());
			}
			
			finalObj[index++]= new Object[]{object[0],pcrfResponse,object[1]};
		}
		
		return finalObj;
	}
	
	
	@Test
	@Parameters(method="data_provider_for_insertCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID")
	public void test_insertCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID(String message,PCRFResponse pcrfResponse,
			LinkedHashMap<String, String> expectedData) throws SQLException, Exception {
		
		NetvertexSessionManager netvertexSessionManager = createNetvertexSessionManager(false);
		netvertexSessionManager.getSessionOperation().createCoreSession(pcrfResponse);
		
		SessionData sessionData = netvertexSessionManager.getSessionLookup().getCoreSessionByCoreSessionID(pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
		
		if(sessionData == null && expectedData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData == null){
			Assert.assertTrue(true);
			return;
		}
		

		LinkedHashMap<String, String> actualData = new LinkedHashMap<String, String>();
		
		for (String key : sessionData.getKeySet()) {
			actualData.put(key, sessionData.getValue(key));
		}
	
		ReflectionAssert.assertReflectionEquals(message,expectedData, actualData,ReflectionComparatorMode.LENIENT_ORDER);
		
		
	}
	
	public Object[][] dataprovider_for_test_insertCoreSessionByCoreSessionID_should_cache_value_even_thought_DB_operation_Fail() {
		
		Object [][] objects = new Object[][] {
				$("Core-Sessions with ID :" + CORE_SESSION_ID_VAL +" must not be found", 
				
				Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
						Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
						Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)
				)
				
				),
				
				$("Core-Sessions with ID :otherCoreSessionId must be found",
				
				Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
						Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
						Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)
				)
				)
				
		};
		
		Object[][] finalObj = new Object[objects.length][];
		
		
		int index = 0;
		for(Object[] object : objects){
			PCRFResponse pcrfResponse = new PCRFResponseImpl();
			for (java.util.Map.Entry<String, String> entry : ((LinkedHashMap<String, String>)object[1]).entrySet()) {
				pcrfResponse.setAttribute(entry.getKey(), entry.getValue());
			}
			
			finalObj[index++]= new Object[]{object[0],pcrfResponse,object[1]};
		}
		
		return finalObj;
	}
	
	
	@Test
	@Parameters(method="dataprovider_for_test_insertCoreSessionByCoreSessionID_should_cache_value_even_thought_DB_operation_Fail")
	public void test_insertCoreSessionByCoreSessionID_should_cache_value_even_thought_DB_operation_fail(String message,PCRFResponse pcrfResponse,
			LinkedHashMap<String, String> expectedData) throws SQLException, Exception {
		
		NetvertexSessionManager netvertexSessionManager = createNetvertexSessionManager(true);
		
	
		CoreSessionTableDetail.dropTable(connection);
		try {
			netvertexSessionManager.getSessionOperation().createCoreSession(pcrfResponse);
		} finally {
			CoreSessionTableDetail.createTable(connection);
		}
		
		
		SessionData sessionData = netvertexSessionManager.getSessionLookup().getCoreSessionByCoreSessionID(pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
		
		if(sessionData == null && expectedData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData == null){
			Assert.assertTrue(true);
			return;
		}
		

		LinkedHashMap<String, String> actualData = new LinkedHashMap<String, String>();
		
		for (String key : sessionData.getKeySet()) {
			actualData.put(key, sessionData.getValue(key));
		}
	
		ReflectionAssert.assertReflectionEquals(message,expectedData, actualData,ReflectionComparatorMode.LENIENT_ORDER);
		
		
	}
	

	
	public Object[][] data_provider_for_updateCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID() {
		
		Object [][] objects = new Object[][] {
				$("Core-Sessions with ID :" + CORE_SESSION_ID_VAL +" must not be found", 
				
				Arrays.asList(Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, "otherVal"),
						Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, "1230"),
						Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID, SESSION_MANAGER_ID_VAL)
						)
				)),
				Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
						Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
						Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)
				)
				),
				
				$("Core-Sessions with ID :otherCoreSessionId must be found",
				
				Arrays.asList(Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, "xyz")
						)
				)),
				Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
						Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "12"),
						Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)
				)
				
				)
				
		};
		
		Object[][] finalObj = new Object[objects.length][];
		
		
		int index = 0;
		for(Object[] object : objects){
			PCRFResponse pcrfResponse = new PCRFResponseImpl();
			for (java.util.Map.Entry<String, String> entry : ((LinkedHashMap<String, String>)object[2]).entrySet()) {
				pcrfResponse.setAttribute(entry.getKey(), entry.getValue());
			}
			
			finalObj[index++]= new Object[]{object[0],object[1],pcrfResponse,object[2]};
		}
		
		return finalObj;
	}
	
	
	@Test
	@Parameters(method="data_provider_for_updateCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID")
	public void test_updateCoreSessionByCoreSessionID_should_return_session_data_that_match_coresessionID(String message,List<LinkedHashMap<PCRFKeyConstants,String>> insertData, PCRFResponse pcrfResponse,
			LinkedHashMap<String, String> expectedData) throws SQLException, Exception {
		
		CoreSessionTableDetail.insert(insertData);
		
		NetvertexSessionManager netvertexSessionManager = createNetvertexSessionManager(false);
		netvertexSessionManager.getSessionOperation().updateCoreSession(pcrfResponse);
		
		SessionData sessionData = netvertexSessionManager.getSessionLookup().getCoreSessionByCoreSessionID(pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
		
		if(sessionData == null && expectedData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData == null){
			Assert.assertTrue(true);
			return;
		}
		

		LinkedHashMap<String, String> actualData = new LinkedHashMap<String, String>();
		
		for (String key : sessionData.getKeySet()) {
			actualData.put(key, sessionData.getValue(key));
		}
	
		ReflectionAssert.assertReflectionEquals(message,expectedData, actualData,ReflectionComparatorMode.LENIENT_ORDER);
		
		
	}
	
	public Object[][] data_provider_for_updateCoreSessionByCoreSessionID_should_cache_value_even_thouhg_DB_operation_fail() {
		
		Object [][] objects = new Object[][] {
				$("Core-Sessions with ID :" + CORE_SESSION_ID_VAL +" must not be found", 
				
				
				Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
						Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "123"),
						Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)
				)
				),
				
				$("Core-Sessions with ID :otherCoreSessionId must be found",
				
	
				Maps.newLinkedHashMap(Arrays.asList(
						Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID.val, CORE_SESSION_ID_VAL),
						Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY.val, "12"),
						Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID.val, SESSION_MANAGER_ID_VAL)
						)
				)
				
				)
				
		};
		
		Object[][] finalObj = new Object[objects.length][];
		
		
		int index = 0;
		for(Object[] object : objects){
			PCRFResponse pcrfResponse = new PCRFResponseImpl();
			for (java.util.Map.Entry<String, String> entry : ((LinkedHashMap<String, String>)object[1]).entrySet()) {
				pcrfResponse.setAttribute(entry.getKey(), entry.getValue());
			}
			
			finalObj[index++]= new Object[]{object[0],pcrfResponse,object[1]};
		}
		
		return finalObj;
	}
	
	
	@Test
	@Parameters(method="data_provider_for_updateCoreSessionByCoreSessionID_should_cache_value_even_thouhg_DB_operation_fail")
	public void test_updateCoreSessionByCoreSessionID_should_cache_value_even_thouhg_DB_operation_fail(String message, PCRFResponse pcrfResponse,
			LinkedHashMap<String, String> expectedData) throws SQLException, Exception {
		
		
		NetvertexSessionManager netvertexSessionManager = createNetvertexSessionManager(true);
		CoreSessionTableDetail.dropTable(connection);
		try {
			netvertexSessionManager.getSessionOperation().updateCoreSession(pcrfResponse);
		} finally {
			CoreSessionTableDetail.createTable(connection);
		}
	
		
		
		SessionData sessionData = netvertexSessionManager.getSessionLookup().getCoreSessionByCoreSessionID(pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));
		
		if(sessionData == null && expectedData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData != null){
			Assert.fail(message);
		}
		
		if(expectedData == null && sessionData == null){
			Assert.assertTrue(true);
			return;
		}
		

		LinkedHashMap<String, String> actualData = new LinkedHashMap<String, String>();
		
		for (String key : sessionData.getKeySet()) {
			actualData.put(key, sessionData.getValue(key));
		}
	
		ReflectionAssert.assertReflectionEquals(message,expectedData, actualData,ReflectionComparatorMode.LENIENT_ORDER);
		
		
	}
	
	
	private NetvertexSessionManager createNetvertexSessionManager(boolean isSessionCacheEnabled)
			throws InitializationFailedException {

		DummyNetvertexServerContextImpl netVertexServerContext = new DummyNetvertexServerContextImpl();
		netVertexServerContext.setServerInstanceId(SESSION_MANAGER_ID_VAL);
		SessionManagerConfiguration sessionManagerConfiguration = new DummySessionManagerConfiguration();

		DummyNetvertexServerConfiguration serverConfiguration = netVertexServerContext.getServerConfiguration();
		MiscellaneousConfiguration miscellaneousConfiguration = serverConfiguration.spyMiscConf();
		NetvertexServerGroupConfiguration netvertexServerGroupConfiguration = serverConfiguration.spyGroupConfiguration();
		when(netvertexServerGroupConfiguration.getSessionDS()).thenReturn(DERBY_DS);
		serverConfiguration.setSessionManagerConfiguration(sessionManagerConfiguration);

		when(miscellaneousConfiguration.isSessionCacheEnabled()).thenReturn(isSessionCacheEnabled);

		return new NetvertexSessionManagerFactory().create(netVertexServerContext, new SessionDaoFactoryTestable().create(netVertexServerContext));
	}

	private static class CoreSessionTableDetail {
		private static final String SESSIONMANAGERID = "SESSION_MANAGER_ID";
		private static final String SEQ_MCORESESSIONS = "SEQ_MCORESESSIONS";
		private static final String USERIDENTITY = "USER_IDENTITY";
		private static final String CORESESSIONID = "CORE_SESSION_ID";
		private static final String CSID = "CS_ID";
		private static final String TABLE_NAME = "TBLT_SESSION";
		private static final String START_TIME = "START_TIME";
		private static final String END_TIME = "LAST_UPDATE_TIME";
		
		
		private static final SessionTableFieldMappingUtil FIELD_MAPPING;
		
		
		static {
			FIELD_MAPPING = new SessionTableFieldMappingUtil(Arrays.<Entry<PCRFKeyConstants,String>>asList(
													Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, CORESESSIONID),
													Entry.newEntry(PCRFKeyConstants.CS_USER_IDENTITY, USERIDENTITY),
													Entry.newEntry(PCRFKeyConstants.CS_SESSION_MANAGER_ID, SESSIONMANAGERID)
												),TABLE_NAME);
		}


		public static void createTable(Connection connection) throws SQLException {
			connection.createStatement().execute("create table " + TABLE_NAME + "("
													+ CSID + " VARCHAR(36),"
													+ CORESESSIONID +" VARCHAR(255),"
													+ USERIDENTITY + " VARCHAR(50),"
													+ SESSIONMANAGERID + " VARCHAR(50),"
													+ START_TIME + " TIMESTAMP,"
													+ END_TIME + " TIMESTAMP)");
			
			connection.createStatement().execute("create sequence " + SEQ_MCORESESSIONS + " as INT start with 100");

		}
		
		public static void insert(List<LinkedHashMap<PCRFKeyConstants, String>> data) throws SQLException {
			FIELD_MAPPING.insert(data);
			
			
		}

		private static void dropTable(Connection connection) throws SQLException {
			connection.createStatement().execute("drop table " + TABLE_NAME);
			connection.createStatement().execute("drop sequence " + SEQ_MCORESESSIONS + " RESTRICT");
		}
		
		private static void truncateTable(Connection connection) throws SQLException {
			connection.createStatement().execute("truncate table " + TABLE_NAME);
		}
		
		
		
	}
	
	private static class SessionRuleTableDetail {
		private static final String SRID = "SR_ID";
		private static final String PCCRULES = "PCC_RULES";
		private static final String CORESESSIONID = "CORE_SESSION_ID";
		private static final String START_TIME = "START_TIME";
		private static final String END_TIME = "LAST_UPDATE_TIME";
		
		
		private static final String TABLE_NAME = "TBLT_SUBSESSION";
		
		private static final SessionTableFieldMappingUtil FIELD_MAPPING;
		
		
		static {
			FIELD_MAPPING = new SessionTableFieldMappingUtil(Arrays.<Entry<PCRFKeyConstants,String>>asList(
					Entry.newEntry(PCRFKeyConstants.CS_CORESESSION_ID, CORESESSIONID),
					Entry.newEntry(PCRFKeyConstants.PCC_RULE, PCCRULES)
				),TABLE_NAME);
		}

		public static void insert(List<LinkedHashMap<PCRFKeyConstants, String>> data) throws SQLException {
			FIELD_MAPPING.insert(data);	
		}

		public static void createTable(Connection connection) throws SQLException {
			connection.createStatement().execute("create table " + TABLE_NAME + "(" 
															+ SRID + " VARCHAR(12),"
															+ CORESESSIONID + " VARCHAR(255), "
															+ PCCRULES + " VARCHAR(50),"
															+ START_TIME + " TIMESTAMP,"
															+ END_TIME + " TIMESTAMP)");
			
		}
		
		private static void dropTable(Connection connection) throws SQLException {
			connection.createStatement().execute("drop table " + TABLE_NAME);
		}
		
		private static void truncateTable(Connection connection) throws SQLException {
			connection.createStatement().execute("truncate table " + TABLE_NAME);
		}
	}
	
	
	
	private static class SessionTableFieldMappingUtil{
		
		@Nonnull private final LinkedHashMap<PCRFKeyConstants, String> fieldMapping;
		@Nonnull private final LinkedHashMap<String,PCRFKeyConstants> reverseFieldMapping;
		@Nonnull private final List<FieldMapping> fieldMappings;
		@Nonnull private final String tableName;
		
		public SessionTableFieldMappingUtil(List<Entry<PCRFKeyConstants,String>> mappings,String tableName){
			
			fieldMapping = new LinkedHashMap<PCRFKeyConstants, String>();
			reverseFieldMapping = new LinkedHashMap<String, PCRFKeyConstants>();
			List<FieldMapping> fieldMappings = Collectionz.newArrayList();;
			
			for (Entry<PCRFKeyConstants, String> key : mappings) {
				fieldMapping.put(key.getKey(),key.getValue());
				reverseFieldMapping.put(key.getValue(),key.getKey());
				FieldMapping fieldMapping = new FieldMappingImpl(FieldMapping.STRING_TYPE, key.getKey().val, key.getValue());
				fieldMappings.add(fieldMapping);
			}
			
			this.fieldMappings = Collections.unmodifiableList(fieldMappings);
			this.tableName = tableName;
			
		}



		public List<FieldMapping> fieldMappings(){
			return fieldMappings;
		}
		
		public String field(PCRFKeyConstants pcrfKeyConstants){
			return fieldMapping.get(pcrfKeyConstants);
		}
		
		public Collection<String> fields(){
			return fieldMapping.values();
		}

		public Collection<PCRFKeyConstants> keys(PCRFKeyConstants pcrfKeyConstants){
			return fieldMapping.keySet();
		}
		
		public void insert(List<LinkedHashMap<PCRFKeyConstants, String>> datas) throws SQLException {
			
			
			for (LinkedHashMap<PCRFKeyConstants, String> data : datas) {
				StringBuilder query = new StringBuilder("Insert into ").append(tableName).append(" (");
				
				StringBuilder fields = new StringBuilder();
				StringBuilder value = new StringBuilder();
				int index = 0;
				for (java.util.Map.Entry<PCRFKeyConstants, String> entry : data.entrySet()) {
					if(isFirstIndex(index)){
						fields.append(fieldMapping.get(entry.getKey()));
						value.append('\'').append(entry.getValue()).append('\'');
					} else {
						fields.append(",").append(fieldMapping.get(entry.getKey()));
						value.append(",").append('\'').append(entry.getValue()).append('\'');
					}
					index++;
					
				}
				
				query.append(fields).append(')').append(" values(").append(value).append(')');
				
				System.out.println(query.toString());
				
				connection.createStatement().executeUpdate(query.toString());
			}
			
			connection.commit();
			
			
			
		}

		private static boolean isFirstIndex(int index) {
			return index == 0;
		}
		
	}
	
	
	
	
	
	private class DummySessionManagerConfiguration implements SessionManagerConfiguration {

		@Override
		public List<FieldMapping> getCoreSessionFieldMappings() {
			return CoreSessionTableDetail.FIELD_MAPPING.fieldMappings();
		}

		@Override
		public List<FieldMapping> getSessionRuleFieldMappings() {
			return SessionRuleTableDetail.FIELD_MAPPING.fieldMappings();
		}

		@Override
		public boolean isBatchUpdateEnable() {
			return false;
		}

		@Override
		public long getBatchSize() {
			return 0;
		}

		@Override
		public long getBatchUpdateIntervalInSec() {
			return 0;
		}

		@Override
		public int getBatchQueryTimeout() {
			return 0;
		}

		@Override
		public boolean isSaveInBatch() {
			return false;
		}

		@Override
		public boolean isUpdateInBatch() {
			return false;
		}

		@Override
		public boolean isDeleteInBatch() {
			return false;
		}


		@Override
		public void toString(IndentingToStringBuilder builder) {

		}
	}
	
	private class SessionDaoFactoryTestable extends SessionDaoFactory{
		@Override
		@Nonnull
		protected SQLDialectFactory createDialectFactory() {
			return new SessionConfigurationImpl.SQLDialectFactory(){

				@Override
				public SQLDialect newDialect(
						List<SchemaMapping> schemaMappingList,
						DBVendors dbVendors) {
					return new DerbyDialectImpl(schemaMappingList);
				}

			};
		}
	}
	
	

}
