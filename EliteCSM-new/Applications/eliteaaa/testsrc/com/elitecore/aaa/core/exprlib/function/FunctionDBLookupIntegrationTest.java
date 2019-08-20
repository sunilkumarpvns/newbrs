package com.elitecore.aaa.core.exprlib.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import junitparams.JUnitParamsRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.StringFunctionExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.ConnectionProvider;
import com.elitecore.exprlib.parser.expression.impl.FunctionDBLookup;

@RunWith(JUnitParamsRunner.class)
public class FunctionDBLookupIntegrationTest {

	private ValueProvider dbLookupValueProvider;
	private ConnectionProvider testConnectionProvider;
	private Compiler compiler;
	private static RadiusPacket requestPacket;
	private final static String TESTINGDB = "TestingDB";
	
	static {

		
		// FIXME change the dictionary implementation in Version 6.6 using RadiusHarnessTest
		String standard_dictionary = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

			+"<attribute-list vendorid=\"0\" vendor-name=\"standard\">"

				+"<attribute id=\"1\" name=\"User-Name\" type=\"string\"/>"
				+"<attribute id=\"4\" name=\"NAS-IP-Address\" type=\"ipaddr\"/>"
				+"<attribute id=\"30\" name=\"Called-Station-Id\"  type=\"string\"/>"        
				+"<attribute id=\"31\" name=\"Calling-Station-Id\" type=\"string\"/>"
				+"<attribute id=\"44\" name=\"Acct-Session-Id\" type=\"string\"/>"

			+"</attribute-list>"
			;

		StringReader stndrd_dict = new StringReader(standard_dictionary);
		try {
			Dictionary.getInstance().load(stndrd_dict);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		requestPacket = new RadiusPacket();

		requestPacket.setPacketType(RadiusConstants.ACCOUNTING_MESSAGE);

		IRadiusAttribute userName = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.USER_NAME);
		userName.setStringValue("CSM");
		requestPacket.addAttribute(userName);

		IRadiusAttribute nasIp = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
		nasIp.setStringValue("0.0.0.1");
		requestPacket.addAttribute(nasIp);

		IRadiusAttribute acctID = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.ACCT_SESSION_ID);
		acctID.setStringValue("200");
		requestPacket.addAttribute(acctID);

		requestPacket.refreshInfoPacketHeader();
		requestPacket.refreshPacketHeader();
	}
	
	@Before
	public void setUp() throws ClassNotFoundException, Exception{
		testConnectionProvider = new TestConnectionProvider();
		dbLookupValueProvider = new DBLookupValueProvider(); 
		
		compiler = Compiler.getDefaultCompiler();
		compiler.addFunction(new FunctionDBLookup(testConnectionProvider));
		
		createTableAndInsertData();
	}

	private void createTableAndInsertData() throws SQLException{
		
		java.sql.Connection conn = testConnectionProvider.getConnection(TESTINGDB);

		conn.prepareStatement("CREATE TABLE TEST (Username VARCHAR(25),AcctSessionID VARCHAR(10),NasIPAddress VARCHAR(20))").execute();
		conn.prepareStatement("CREATE TABLE TESTwithNULL (Username VARCHAR(25),AcctSessionID VARCHAR(10),NasIPAddress VARCHAR(20))").execute();
		
		String[] dataToInsert ={
			"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('CSM','200','0.0.0.0')",
			"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('AAA','201','0.0.0.1')",
			"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('AAADEV','202','0.0.0.2')",
			"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('AAAPSG','203','0.0.0.3')",
			"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('NV','204','0.0.0.4')",
			"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('NVDEV','205','0.0.0.5')",
			"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('NVPSG','206','0.0.0.6')",
			"Insert into TESTwithNULL (Username,AcctSessionID,NasIPAddress) values ('CSM1','200',null)",
			"Insert into TESTwithNULL (Username,AcctSessionID,NasIPAddress) values ('CSM1','201','0123456789')",
			
		};
		
		for(int dataIndx = 0;dataIndx < dataToInsert.length; dataIndx++){
			conn.prepareStatement(dataToInsert[dataIndx]).execute();
		}
	}
	
	@After
	public void tearDown() throws SQLException{
		testConnectionProvider.getConnection(TESTINGDB).prepareStatement("DROP TABLE TEST").execute();
		testConnectionProvider.getConnection(TESTINGDB).prepareStatement("DROP TABLE TESTwithNULL").execute();
	}
	
	public Object[][] dataFor_dbLookUp_ShouldThrowSomeException_WhenImproperRequestParametersArePassed(){
		return new Object[][]{
				
			/**
			 * Request Packet parameters
			 */
			
			// Missing Identifier 0:31 as not into request
			{"DBLookup(\"TestingDB\",\"select * from TEST where Username = ? and NasIPAddress = ?\",0:1,0:31)",MissingIdentifierException.class},
			
			// Invalid Type Cast as 0:1 is String and abs function evaluated on numeric values
			{"DBLookup(\"TestingDB\",\"select * from TEST where Username = ?\",abs(0:1))",InvalidTypeCastException.class},
			
			// Invalid Function configuration for function abs(),need only one arguments
			{"DBLookup(\"TestingDB\",\"select * from TEST where Username = ?\",abs(0:1,0:31))",com.elitecore.exprlib.parser.exception.IllegalArgumentException.class},
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_dbLookUp_ShouldThrowSomeException_WhenImproperRequestParametersArePassed")
	public void testGetStringValue_ShouldThrowExpectedException_WhenProperRequestParametersArePassed(String expressionString,Class<? extends Exception> expectedException){
		try {
			StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
			dbLookup.getStringValue(dbLookupValueProvider);
		} catch (Throwable t) {
			assertEquals("Expected Exception is " + expectedException + " but getting " + t,expectedException, t.getClass());
		}
	}
	
	public Object[][] dataFor_dbLookUp_ShouldGetSuccess_WhenValidRequestParametersArePassed(){
		return new Object[][]{
				
			/**
			 * Success Cases.
			 * Radius Packet Parameters.
			 */
			
			{"DBLookup(\"TestingDB\",\"select AcctSessionID from TEST where Username = ?\",0:1)","200"},
			
			{"DBLookup(\"TestingDB\",\"select * from TEST where Username = ? and NasIPAddress = ?\",0:1,0:4)",null},

			{"DBLookup(\"TestingDB\",\"select * from TEST where Username = ?\",toLowerCase(0:1))",null},
			
			{"DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ?\", toLowerCase(0:1))",null}
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_dbLookUp_ShouldGetSuccess_WhenValidRequestParametersArePassed")
	public void testGetStringValue_ShouldGetSuccess_WhenValidRequestParametersArePassed(String expressionString,String resultString) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		assertEquals(resultString,dbLookup.getStringValue(dbLookupValueProvider));
	}

	private static class DBLookupValueProvider implements ValueProvider{

		@Override
		public String getStringValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			if(identifier != null){
				try{
					return requestPacket.getRadiusAttribute(identifier).getStringValue();
				}catch(Exception e){
					throw new MissingIdentifierException(e.getMessage());
				}
			}
			return null;
		}

		@Override
		public long getLongValue(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			if(identifier != null){
				return requestPacket.getRadiusAttribute(identifier).getLongValue();
			}
			return 0;
		}

		@Override
		public List<String> getStringValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return null;
		}

		@Override
		public List<Long> getLongValues(String identifier)
				throws InvalidTypeCastException, MissingIdentifierException {
			return null;
		}

		@Override
		public Object getValue(String key) {
			return null;
		}
	}
		
	private static class TestConnectionProvider implements ConnectionProvider{

		public TestConnectionProvider() throws ClassNotFoundException {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		}

		@Override
		public Connection getConnection(String dbSourceName) {
			Connection conn = null;
			try {
				if(TESTINGDB.equals(dbSourceName)){
					conn = DriverManager.getConnection("jdbc:derby:memory:TestingDB;create=true");
				}
			} catch (SQLException e) {
				fail("Problem in creating Derby Database Connection, Reason: "+e.getMessage());
			}
			return conn;
		}
	}
}