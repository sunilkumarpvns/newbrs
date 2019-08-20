package com.elitecore.exprlib.parser.expression.impl.dblookup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import junitparams.JUnitParamsRunner;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.StringFunctionExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.ConnectionProvider;
import com.elitecore.exprlib.parser.expression.impl.FunctionDBLookup;

@RunWith(JUnitParamsRunner.class)
public class FunctionDBLookUpUnitTest {

	@Mock private ValueProvider dbLookupValueProvider;
	private Compiler compiler;
	private static ConnectionProvider testConnectionProvider;
	private final static String TESTINGDB = "TestingDB";

	@BeforeClass
	public static void setUpBeforeClass() throws SQLException, ClassNotFoundException{
		testConnectionProvider = new TestConnectionProvider();
		createTableAndInsertData();
	}
	
	@Before
	public void setUp() throws ClassNotFoundException, SQLException{
		MockitoAnnotations.initMocks(this);
		compiler = Compiler.getDefaultCompiler();
		compiler.addFunction(new FunctionDBLookup(testConnectionProvider));
	}

	private static void createTableAndInsertData() throws SQLException,ClassNotFoundException{
		
		Connection conn = testConnectionProvider.getConnection(TESTINGDB);

		conn.prepareCall("CREATE TABLE TEST (Username VARCHAR(25),AcctSessionID VARCHAR(10),NasIPAddress VARCHAR(20))").execute();
		conn.prepareCall("CREATE TABLE TESTwithNULL (Username VARCHAR(25),AcctSessionID VARCHAR(10),NasIPAddress VARCHAR(20))").execute();

		String[] dataToInsert ={
				"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('CSM','200','0.0.0.0')",
				"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('AAA','201','0.0.0.1')",
				"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('AAADEV','202','0.0.0.2')",
				"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('AAAPSG','203','0.0.0.3')",
				"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('NV','204','0.0.0.4')",
				"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('NVDEV','205','0.0.0.5')",
				"Insert into TEST (Username,AcctSessionID,NasIPAddress) values ('NVPSG','206','0.0.0.6')",
				"Insert into TESTwithNULL (Username,AcctSessionID,NasIPAddress) values ('CSM1','200',null)",
				"Insert into TESTwithNULL (Username,AcctSessionID,NasIPAddress) values ('CSM1','201','0.0.0.10')",	
		};

		for (String data : dataToInsert) {
			conn.prepareCall(data).execute();
		}
		
		conn.prepareCall("CREATE FUNCTION addNumberFunction(no1 int, no2 int) RETURNS int PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA EXTERNAL NAME 'com.elitecore.exprlib.parser.expression.impl.dblookup.FunctionDBLookupProcedureDefinition.addNumberFunction'").execute();
		conn.prepareCall("CREATE procedure addNumberProcWithAllINParams( IN no1 int, IN no2 int, IN no3 int) PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA EXTERNAL NAME 'com.elitecore.exprlib.parser.expression.impl.dblookup.FunctionDBLookupProcedureDefinition.addNumberProcWithAllINParams'").execute();
		conn.prepareCall("CREATE procedure addNumberProcwithAllOUTParams( OUT no1 int, OUT no2 int, OUT no3 int) PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA EXTERNAL NAME 'com.elitecore.exprlib.parser.expression.impl.dblookup.FunctionDBLookupProcedureDefinition.addNumberProcwithAllOUTParams'").execute();
		conn.prepareCall("CREATE procedure addNumberProcedure(IN no1 int, IN no2 int, OUT no3 int) PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA EXTERNAL NAME 'com.elitecore.exprlib.parser.expression.impl.dblookup.FunctionDBLookupProcedureDefinition.addNumberProcedure'").execute();
	}

	@AfterClass
	public static void tearDown() throws SQLException{
		testConnectionProvider.getConnection(TESTINGDB).prepareCall("DROP TABLE TEST").execute();
		testConnectionProvider.getConnection(TESTINGDB).prepareCall("DROP TABLE TESTwithNULL").execute();
		testConnectionProvider.getConnection(TESTINGDB).prepareCall("DROP FUNCTION addNumberFunction").execute();
		testConnectionProvider.getConnection(TESTINGDB).prepareCall("DROP PROCEDURE addNumberProcWithAllINParams").execute();
		testConnectionProvider.getConnection(TESTINGDB).prepareCall("DROP PROCEDURE addNumberProcwithAllOUTParams").execute();
		testConnectionProvider.getConnection(TESTINGDB).prepareCall("DROP PROCEDURE addNumberProcedure").execute();
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_ShouldThrowNullPointerException_WhenConnectionProviderIsNull(){
		new FunctionDBLookup(null);
	}

	@Test(expected=ClassCastException.class)
	public void testGetStringValue_ShouldThrowClassCastException_WhenFunctionNameIsWrong() throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		StringFunctionExpression dbLookup = (StringFunctionExpression)compiler.parseExpression("DLookup()");
		dbLookup.getStringValue(dbLookupValueProvider);
	}
	
	
	public Object[][] dataFor_dbLookUp_WhenNumberOfArgumentsAreLessThanRequiredArguments(){
		return new Object[][]{

			
				/**
				 * Invalid function Signature.
				 * No. of Arguments < Minimum Arguments = 2.
				 */
				//Only Function is Configured.
				{"DBLookup()",IllegalArgumentException.class},

				//Invalid no of Argument i.e Data Source not given
				{"DBLookup(\"select * from TEST\")",com.elitecore.exprlib.parser.exception.IllegalArgumentException.class},
		};
	}

	@Test
	@junitparams.Parameters(method="dataFor_dbLookUp_WhenNumberOfArgumentsAreLessThanRequiredArguments")
	public void testGetStringValue_ShouldThrowExpectedException_WhenNumberOfArgumentsAreLessThanRequiredArguments(String expressionString,Class<? extends Exception> expectedException){
		try {
			StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
			dbLookup.getStringValue(dbLookupValueProvider);
			fail("Expected Exception: "+expectedException+" does not thrown.");
		} catch (Throwable t) {
			assertEquals("Expected Exception is " + expectedException + " but getting " + t, expectedException, t.getClass());
		}
	}

	public Object[][] dataFor_dbLookUp_WhenIlleagalArgumentsArePassed(){
		return new Object[][]{

				/**
				 * Illegal Argument Configuration
				 * i.e. DBSource not given,Query not Configured,Inputs parameter not specify etc.
				 */
				
				// Invalid Method Signature, comma is missing between Query & INParameter
				{"DBLookup(\"TestingDB\",\"select * from TAB where Username = ?\" 'AAA'\")",InvalidExpressionException.class,null},

				//Empty dbSource and query statement
				{"DBLookup(\" \",\" \")",IllegalArgumentException.class,"Query should not be empty."},
				
				// Empty dbSource
				{"DBLookup(\" \",\"select * from TEST\")",IllegalArgumentException.class,"Data Source name should not be empty."},

				//Empty Query Statement
				{"DBLookup(\"TestingDB\",\" \")",IllegalArgumentException.class,"Query should not be empty."},

				{"DBLookup(\"TestingDB\",\"\")",IllegalArgumentException.class,"Query should not be empty."},

				// Illegal operation, select instead select
				{"DBLookup(\"TestingDB\",\"selct * from TEST\")",IllegalArgumentException.class,null},
				
				// Table Does not Exist.
				{"DBLookup(\"TestingDB\",\"select * from TEST1\")",IllegalArgumentException.class,null},
				
				//Invalid Data source is specified												
				{"DBLookup(\"TtinDB2\",\"select * from TEST \")",IllegalArgumentException.class,null},

				//Invalid Column Name in Query, NasIAddress instead of NasIPAddress.
				{"DBLookup(\"TestingDB\",\"select NasIAddress from TEST\")",IllegalArgumentException.class,null},

				// Missing IN parameter for NasIPAddress --> ?
				{"DBLookup(\"TestingDB\",\"select AcctSessionID from TEST where NasIPAddress = ?\")",IllegalArgumentException.class,null},

				// Missing IN parameter for 2nd column, i.e. parameters NasIPAddress
				{"DBLookup(\"TestingDB\",\"select AcctSessionID from TEST where Username = ? or NasIPAddress = ?\",\"NV\")",IllegalArgumentException.class,null},

				// No column mapping for 3rd IN Parameter Mapping 200
				{"DBLookup(\"TestingDB\",\"select AcctSessionID from TEST where Username = ? or NasIPAddress = ?\",\"AAAPSG\",\"0.0.0.2\",\"200\")",IllegalArgumentException.class,null},

				// Invalid Type Cast as 0:1 is String and abs function evaluated on numeric values
				{"DBLookup(\"TestingDB\",\"select * from TEST where Username = ?\",abs(\"AAA\"))",InvalidTypeCastException.class,null},
				
				
				// No IN Parameters in Expression
				{"DBLookup(\"TestingDB\",\"{?=call addNumberFunction(?,?)}\")",IllegalArgumentException.class,null},
				
				// 2nd parameter was not set
				{"DBLookup(\"TestingDB\",\"{?=call addNumberFunction(?,?)}\",\"3\")",IllegalArgumentException.class,null},
				
				// all are IN Parameter for procedure but due to no IN Parameters in expression will 
				// register all Procedure parameters as all out parameter.
				{"DBLookup(\"TestingDB\",\"{call addNumberProcWithAllINParams(?,?,?)}\")",IllegalArgumentException.class,null},
				
				// As all are IN Parameter for Procedure but due to one less argument in IN Parameter
				// of expression will register third argument of procedure as OUT Parameter.
				{"DBLookup(\"TestingDB\",\"{call addNumberProcWithAllINParams(?,?,?)}\",\"3\",\"7\")",IllegalArgumentException.class,null},
				
				// all are OUT Parameter of procedure but due to IN Parameters in expression will 
				// register it as IN Parameter.
				{"DBLookup(\"TestingDB\",\"{call addNumberProcwithAllOUTParams(?,?,?)}\",\"3\",\"7\",\"4\")",IllegalArgumentException.class,null},
				
				{"DBLookup(\"TestingDB\",\"{call addNumberProcwithAllOUTParams(?,?,?)}\",\"3\",\"7\")",IllegalArgumentException.class,null}
		};
	}

	@Test
	@junitparams.Parameters(method = "dataFor_dbLookUp_WhenIlleagalArgumentsArePassed")
	public void testGetStringValue_ShouldThrowExpectedException_WhenIlleagalArgumentsArePassed(String expressionString,Class<? extends Exception> expectedException,String reasonString){
		try {
			StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
			dbLookup.getStringValue(dbLookupValueProvider);
			fail("Expected Exception: "+expectedException+" does not thrown.");
		} catch (Throwable t) {
			assertEquals("Expected Exception is " + expectedException + " but getting " + t,expectedException, t.getClass());
			if(reasonString != null){
				assertEquals(reasonString, t.getLocalizedMessage());
			}
		}
	}

	@Test(expected = MissingIdentifierException.class)
	public void testGetStringValue_ShouldLookUpValueProviderAndThrowMisssingIdentifierException_WhenRequestedIDisMissing() throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
		//as 0:1 is not provide to valueprovider using mock so 0:1 is marked as missing parameter and
		//MissingIdentifierException will be thrown.
		
		//Mockito.when(valueProvider.getStringValue("0:1")).thenReturn("CSM");
		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ?\",0:1)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		dbLookup.getStringValue(dbLookupValueProvider);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetStringValue_ShouldLookUpValueProviderAndThrowIllegalArgumentException_WhenInvalidFunctionSignatureIsConfigured() throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{

		// Invalid function signature abs(arg1,arg2) is specify, actual abs(arg1) 
		String expressionString = "DBLookup(\"TestingDB\",\"select * from TEST where Username = ?\",abs(0:1,0:4))";
		Mockito.when(dbLookupValueProvider.getStringValue("0:1")).thenReturn("CSM");
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		dbLookup.getStringValue(dbLookupValueProvider);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetStringValue_ShouldThrowIllegalStateException_WhenPreparedStatementIsNull() throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		String expressionString = "DBLookup(\"NullPreparedStatement\",\"select * from TEST\")";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		dbLookup.getStringValue(dbLookupValueProvider);
		fail("Prepared statement is null, getStringValue must throw exception");
	}

	public Object[][] dataFor_dbLookUp_WhenQueryIsNotSelectQuery(){
		return new Object[][]{

				/***
				 * Invalid Operation is configured. 
				 * i.e. Insert Statement,Update Statement,Drop Statement,Delete Statement
				 */
				{"DBLookup(\"TestingDB\",\"insert into TEST (Username,AcctSessionID,NasIPAddress) values ('admin','207','0.0.0.7')\")",IllegalArgumentException.class},

				{"DBLookup(\"TestingDB\",\"update TEST set Username='admin-admin' where Username = ?\",\"admin\")",IllegalArgumentException.class},

				{"DBLookup(\"TestingDB\",\"delete from TEST where Username = ? \",\"admin-admin\")",IllegalArgumentException.class},

				{"DBLookup(\"TestingDB\",\"drop table TEST \")",IllegalArgumentException.class}
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_dbLookUp_WhenQueryIsNotSelectQuery")
	public void testGetStringValue_ShouldExecutedQuery_WhenQueryIsNotSelectQuery(String expressionString,Class<? extends Exception> expectedException){
		try {
			StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
			dbLookup.getStringValue(dbLookupValueProvider);
			fail("Expected Exception: "+expectedException+" does not thrown.");
		} catch (Throwable t) {
			assertEquals("Expected Exception is " + expectedException + " but getting " + t,expectedException, t.getClass());
		}
	}

	public Object[][] dataFor_dbLookUp_WhenValidArgumentsArePassed(){
		return new Object[][]{

				/**
				 * Success Cases
				 */
				
				// Expression without IN parameters but taking query with whiteSpace.
				{"DBLookup(\"TestingDB\",\"      select acctSessionID from TEST where Username = 'CSM'      \")","200"},

				// Expression without IN parameters.
				{"DBLookup(\"TestingDB\",\"select acctSessionID from TEST where Username = 'CSM'\")","200"},

				// IN parameter with literal value
				{"DBLookup(\"TestingDB\",\"select AcctSessionID from TEST where Username = ?\",\"CSM\")","200"},

				// Multiple IN parameters with literal values
				{"DBLookup(\"TestingDB\",\"select AcctSessionID from TEST where Username = ? and NasIPAddress = ?\",\"AAA\",\"0.0.0.1\")","201"},

				// Pattern Matching Query Result = AAA as operation is fetch whole table and first Column will username.
				{"DBLookup(\"TestingDB\",\"select * from TEST where Username like 'A%'\")","AAA"},

				/*If o/p of query = multiple rows containing Null as well as some values,than return result
				 will be first non null value. here this query gives two nasIpAddress first one is null 
				 and second will contain some value, so Final Result = Second value of NasIpAddress.*/
				{"DBLookup(\"TestingDB\",\"select NasIPAddress from TESTwithNULL where Username='CSM1'\")","0.0.0.10"},

				// Oracle Aggregate Functions
				{"DBLookup(\"TestingDB\",\"SELECT count(*) from TEST where Username like '%'\")","7"},

				{"DBLookup(\"TestingDB\",\"select count(NasIPAddress) from TEST where Username = ?\",\"AAA\")","1"},

				/* In this parser for expression will only consider the token
				 * if it is like "SomeValue"/Identifier before Comma (,)
				 * e.g "arg1", here arg1 is some literal that followed by Comma(,) is
				 * consider. if  ,,, than no token will be generated. */ 
				{"DBLookup(\"TestingDB\",,,,\"select Username from TEST\")","CSM"},
				
				{"DBLookup(\"TestingDB\",\"{?=call addNumberFunction(?,?)}\",\"3\",\"7\")","10"},
				
				{"DBLookup(\"TestingDB\",\"{?=call addNumberFunction(3,7)}\")","10"},
				/*As the IN parameters of expression string will matches
				the required Parameters of procedure, so final result will be 
				the sum of given parameters. and result will be in OUT Parameter --> i.e. no3 */
				
				{"DBLookup(\"TestingDB\",\"{call addNumberProcedure(?,?,?)}\",\"3\",\"7\")","10"},
				
				{"DBLookup(\"TestingDB\",\"{call addNumberProcedure(3,7,?)}\")","10"},				
				
				// As No of IN Parameters in expression == No of IN Parameters of procedure
				// so, all set as IN Parameter and final result is NULL
				{"DBLookup(\"TestingDB\",\"{call addNumberProcWithAllINParams(?,?,?)}\",\"3\",\"7\",\"10\")",null},
				
				// As no IN parameters in expression so all OUT Parameter will set as OUT Parameters
				{"DBLookup(\"TestingDB\",\"{ call addNumberProcwithAllOUTParams(?,?,?)}\")","0"}
				
		};
	}

	@Test
	@junitparams.Parameters(method = "dataFor_dbLookUp_WhenValidArgumentsArePassed")
	public void testGetStringValue_ShouldGetSuccess_WhenValidArgumentsArePassed(String expressionString,String result) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		assertEquals(result, dbLookup.getStringValue(dbLookupValueProvider));
	}

	@Test
	public void testGetStringValue_ShouldLookUpValueProvider_WhenSingleIdentifierIsGivenINParamters() throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
		String expectedValue = "200";
		Mockito.when(dbLookupValueProvider.getStringValue("0:1")).thenReturn("CSM");

		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ?\", 0:1)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		String returnValue = dbLookup.getStringValue(dbLookupValueProvider);

		assertEquals("Expected value is " + expectedValue + " but getting " + returnValue,expectedValue, returnValue);
	}

	@Test
	public void testGetStringValue_ShouldLookUpValueProvider_WhenMultipleIdentifierAreGivenAsINParamters() throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
		String expectedValue = "201";
		Mockito.when(dbLookupValueProvider.getStringValue("0:1")).thenReturn("AAA");
		Mockito.when(dbLookupValueProvider.getStringValue("0:4")).thenReturn("0.0.0.1");

		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ? and NasIPAddress = ?\",0:1,0:4)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		String returnValue = dbLookup.getStringValue(dbLookupValueProvider);

		assertEquals("Expected value is " + expectedValue + " but getting " + returnValue,expectedValue, returnValue);
	}

	@Test
	public void testGetStringValue_ShouldLookUpValueProvider_WhenOtherValidFunctionOperationIsPerformdOnINParamters() throws InvalidTypeCastException, MissingIdentifierException, InvalidExpressionException, IllegalArgumentException{
		String expectedValue = null;
		Mockito.when(dbLookupValueProvider.getStringValue("0:1")).thenReturn("CSM");

		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ?\", toLowerCase(0:1))";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		String returnValue = dbLookup.getStringValue(dbLookupValueProvider);

		assertEquals("Expected value is " + expectedValue + " but getting " + returnValue,expectedValue, returnValue);
	}

	@Test
	public void testGetLongValue_ShouldAlwaysReturnZero_WhenCalledWithValueProvider() throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ? AND NasIPAddress=?\", 0:1,0:4)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		assertEquals(0, dbLookup.getLongValue(dbLookupValueProvider));
	}

	@Test
	public void testGetLongValue_ShouldAlwaysReturnZero_WhenCalledWithNULLValueProvider() throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ? AND NasIPAddress=?\", 0:1,0:4)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		assertEquals(0, dbLookup.getLongValue(null));
	}
	
	@Test
	public void testReturnsMultipleValues_ShouldAlwaysReturnFalse_WhenCalled() throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ? AND NasIPAddress=?\", 0:1,0:4)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		assertEquals(false, dbLookup.returnsMutipleValue());
	}

	@Test
	public void testHasWildCardCharacter_ShouldAlwaysReturnFalse_WhenCalled() throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ? AND NasIPAddress=?\", 0:1,0:4)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		assertEquals(false,dbLookup.hasWildCardCharacter());
	}

	@Test
	public void testIsRegularExpression_ShouldAlwaysReturnFalse_WhenCalled() throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{
		boolean expectedResult = false;
		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ? AND NasIPAddress=?\", 0:1,0:4)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		boolean actualResult = dbLookup.isRegularExpression();
		assertEquals("Expected value is " + expectedResult + " but getting " + actualResult,expectedResult,actualResult);
	}

	@Test
	public void testGetFunctionType_ShouldAlwaysReturnFunctionExpression_WhenCalled() throws InvalidExpressionException{
		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ? AND NasIPAddress=?\", 0:1,0:4)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		assertEquals(Expression.FunctionExpression, dbLookup.getFunctionType());
	}

	@Test
	public void testGetExpressionType_ShouldAlwaysReturnFunctionExpressionValue_WhenCalled() throws InvalidExpressionException{
		String expressionString = "DBLookup(\"TestingDB\",\"select acctsessionid from TEST where Username = ? AND NasIPAddress=?\", 0:1,0:4)";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		assertEquals(Expression.FunctionExpression, dbLookup.getExpressionType());
	}

	@Test
	public void testGetStringValue_MustCallGetConnectionOnProvider_WhenConnectionProviderIsNonNull() throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, SQLException{

		ConnectionProvider mockConnectionProvider = Mockito.mock(ConnectionProvider.class);

		compiler.addFunction(new FunctionDBLookup(mockConnectionProvider));
		String expressionString = "DBLookup(\"TestingDB\",\"select * from tab\")";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		try{
			dbLookup.getStringValue(dbLookupValueProvider);
			fail("IllegalArgument not thrown even when connection is null");
		}catch(IllegalArgumentException ex){
			//this must come
		}

		Mockito.verify(mockConnectionProvider).getConnection("TestingDB");
	}

	@Test
	public void testGetStringValue_ShouldEatSQLException_WhenExceptionOccursWhileClosingConnection() throws SQLException, InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{
		Connection mockConnection = Mockito.mock(Connection.class);
		Mockito.doThrow(SQLException.class).when(mockConnection).close();

		ConnectionProvider mockConnectionProvider = Mockito.mock(ConnectionProvider.class);
		Mockito.when(mockConnectionProvider.getConnection(TESTINGDB)).thenReturn(mockConnection);
		CallableStatement mockCallableStatement = Mockito.mock(CallableStatement.class);
		Mockito.when(mockConnection.prepareCall(Mockito.anyString())).thenReturn(mockCallableStatement);

		ResultSet mockResultSet = Mockito.mock(ResultSet.class);
		Mockito.when(mockCallableStatement.executeQuery()).thenReturn(mockResultSet);
		
		compiler.addFunction(new FunctionDBLookup(mockConnectionProvider));
		String expressionString = "DBLookup(\"TestingDB\",\"select * from tab\")";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);
		dbLookup.getStringValue(dbLookupValueProvider);
	}

	@Test
	public void testGetStringValue_ShouldEatSQLException_WhenExceptionOccursWhileClosingPrepareStatement() throws SQLException, InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{

		Connection mockConnection = Mockito.mock(Connection.class);

		ConnectionProvider mockConnectionProvider = Mockito.mock(ConnectionProvider.class);
		Mockito.when(mockConnectionProvider.getConnection(TESTINGDB)).thenReturn(mockConnection);

		CallableStatement mockCallableStatement = Mockito.mock(CallableStatement.class);

		Mockito.when(mockConnection.prepareCall(Mockito.anyString())).thenReturn(mockCallableStatement);
		Mockito.doThrow(SQLException.class).when(mockCallableStatement).close();

		ResultSet mockResultSet = Mockito.mock(ResultSet.class);
		Mockito.when(mockCallableStatement.executeQuery()).thenReturn(mockResultSet);
		
		compiler.addFunction(new FunctionDBLookup(mockConnectionProvider));
		String expressionString = "DBLookup(\"TestingDB\",\"select * from TEST\")";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);

		dbLookup.getStringValue(dbLookupValueProvider);
	}

	@Test
	public void testGetStringValue_ShouldEatSQLException_WhenExceptionOccursWhileClosingResultSet() throws SQLException, InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{

		ConnectionProvider mockConnectionProvider = Mockito.mock(ConnectionProvider.class);

		Connection mockConnection = Mockito.mock(Connection.class);
		Mockito.when(mockConnectionProvider.getConnection(TESTINGDB)).thenReturn(mockConnection);

		CallableStatement mockCallableStatement = Mockito.mock(CallableStatement.class);
		Mockito.when(mockConnection.prepareCall(Mockito.anyString())).thenReturn(mockCallableStatement);

		ResultSet mockResultSet = Mockito.mock(ResultSet.class);
		Mockito.when(mockCallableStatement.executeQuery()).thenReturn(mockResultSet);
		Mockito.doThrow(SQLException.class).when(mockResultSet).close();

		compiler.addFunction(new FunctionDBLookup(mockConnectionProvider));
		String expressionString = "DBLookup(\"TestingDB\",\"select * from TEST\")";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);

		dbLookup.getStringValue(dbLookupValueProvider);
	}

	@Test
	public void testGetStringValue_MustCloseConnection_WhenConnectionIsNonNull() throws SQLException, InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{
		Connection mockConnection = Mockito.mock(Connection.class);

		ConnectionProvider mockConnectionProvider = Mockito.mock(ConnectionProvider.class);

		Mockito.when(mockConnectionProvider.getConnection(TESTINGDB)).thenReturn(mockConnection);

		compiler.addFunction(new FunctionDBLookup(mockConnectionProvider));

		String expressionString = "DBLookup(\"TestingDB\",\"select * from tab\")";

		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);

		try{
			dbLookup.getStringValue(dbLookupValueProvider);
		}catch(IllegalStateException ex){
			// must come as preparestatement will be null
		}

		Mockito.verify(mockConnection).close();
	}

	@Test
	public void testGetStringValue_MustClosePreparedStatement_WhenPreparedStatementIsNonNull() throws SQLException, InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException{
		Connection mockConnection = Mockito.mock(Connection.class);
		CallableStatement mockCallableStatement = Mockito.mock(CallableStatement.class);
		Mockito.when(mockConnection.prepareCall(Mockito.anyString())).thenReturn(mockCallableStatement);

		ConnectionProvider mockConnectionProvider = Mockito.mock(ConnectionProvider.class);

		Mockito.when(mockConnectionProvider.getConnection(TESTINGDB)).thenReturn(mockConnection);
		
		compiler.addFunction(new FunctionDBLookup(mockConnectionProvider));
		String expressionString = "DBLookup(\"TestingDB\",\"select * from tab\")";
		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);

		try{
			dbLookup.getStringValue(dbLookupValueProvider);
		}catch(IllegalArgumentException ex){
			//must come as resultset will be null
		}

		Mockito.verify(mockCallableStatement).close();
	}

	@Test
	public void testGetStringValue_MustCloseResultSet_WhenResultSetIsNonNull() throws SQLException, InvalidExpressionException, InvalidTypeCastException, MissingIdentifierException, IllegalArgumentException{
		Connection mockConnection = Mockito.mock(Connection.class);
		CallableStatement st = Mockito.mock(CallableStatement.class);
		ResultSet rs = Mockito.mock(ResultSet.class);

		Mockito.when(st.executeQuery()).thenReturn(rs);
		Mockito.when(mockConnection.prepareCall(Mockito.anyString())).thenReturn(st);

		ConnectionProvider mockConnectionProvider = Mockito.mock(ConnectionProvider.class);

		Mockito.when(mockConnectionProvider.getConnection(TESTINGDB)).thenReturn(mockConnection);

		compiler.addFunction(new FunctionDBLookup(mockConnectionProvider));

		String expressionString = "DBLookup(\"TestingDB\",\"select * from tab\")";

		StringFunctionExpression dbLookup = (StringFunctionExpression) compiler.parseExpression(expressionString);

		dbLookup.getStringValue(dbLookupValueProvider);

		Mockito.verify(rs).close();
	}


	/* ------------------------------STUBS -------------------------*/
	
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
				}else if("NullPreparedStatement".equals(dbSourceName)){
					Connection connectionMock = Mockito.mock(Connection.class);
					Mockito.when(connectionMock.prepareStatement(Mockito.anyString())).thenReturn(null);
					conn = connectionMock;
				}
			} catch (SQLException e) {
				fail("Problem in creating Derby Database Connection, Reason: "+e.getMessage());
			}
			return conn;
		}
	}
}

