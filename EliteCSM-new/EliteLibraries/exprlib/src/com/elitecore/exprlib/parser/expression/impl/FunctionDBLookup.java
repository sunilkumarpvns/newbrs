package com.elitecore.exprlib.parser.expression.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
/**
 * <pre>
 * <strong>Usage</strong>: This Function is used for Database Look up.
 * 
 * <strong>Precondition</strong>: Minimum two arguments are Required
 * 
 * <strong>Syntax</strong>: <code>DBLookup("DataSourceName","QueryStatement/Function/Procedure","Arg1","Arg2",......,"ArgN")</code>
 * 
 * 
 * The Expression can be configured in two different ways:
 * 
 * 1) Without Arguments
 * 2) With Arguments
 * 
 * 
 * <strong>Expression Arguments Description</strong>.
 * 
 * <strong>1) DataSourceName</strong>-	
 * 
 * Indicates the identity of the Database Data source for look up.
 * This DataSource is configured in AAA, ESI ---> DatabaseDataSource.
 * 
 * PreCondition - DataSource given in function must be attached with at least one Driver i.e. <b>DataSource must be initialize.</b> 
 * 
 * <strong>2) QueryStatement/Function/Procedure</strong> - 
 * 
 * Indicates the Operation to be performed on given DataSource.
 * Query/Function/Procedure can be configured in two different ways.
 * 
 * 1) Query/Function/Procedure without Arguments (Query/Function/Procedure will allow with direct values for Inputs).
 * 2) Query/Function/Procedure with Arguments (Query/Function/Procedure with '?' symbols where Input is Required).				
 * 
 * Function Definition for expression is like {? = call function_name(Arg1,...,ArgN)}, no space will be allowed between "{?" symbol.
 * 
 * Procedure Definition for expression is like {call procedure_name(Arg1,...,ArgN)}.
 * 
 * <strong>3) Arguments</strong>(Optional)-	
 * 
 * Arguments are used to set the IN parameters of Query.Multiple Arguments can be specified  
 * in comma separated form like <b>"Arg1","Arg2",......,"ArgN"</b>
 * 		
 * If number of Arguments in Expression is greater then numbers of IN parameters of Query/Function/Procedure, 
 * then the expression will not be executed and it will raise the IllegalArgumentException.
 *  
 *  
 * Following are the cases: 
 *  
 *  <strong>case 1:</strong>
 *   
 *   <code>DBLookup("TestingDB","select column_name from table where InParam1 = ? or/and InParam2 = ? ",arg1,arg2,arg3)</code>
 *   Query requires 2 IN Parameter i.e Arg1,Arg2 but number of Arguments of expression is more.
 *   
 *  <strong>case 2:</strong>
 *   
 *   <code>DBLookup("TestingDB","{? = call function_name(InParam1,InParam2,InParam3)",arg1,arg2,arg3,arg4,arg5)</code>
 *   Function require 3 IN Parameter i.e InParam1,InParam2,InParam3 but number of Arguments of expression is more.
 *  
 *  <strong>case 3:</strong>
 *   
 *   <code>DBLookup("TestingDB","{call procedure_name(InParam1,InParam2,InParam3)",arg1,arg2,arg3,arg4,arg5)</code>
 *   Procedure require 3 IN Parameter i.e InParam1,InParam2,InParam3 but number of Arguments of expression is more.
 *   
 *   
 * <strong>Possible values for Arguments are as follows:</strong>
 * 
 * 1) Literal value.
 *    <code>DBLookup("TestingDB","select * from AccountData where Username=?,"CSM")</code>   
 * 
 * 2) Attribute-Id received in Request
 *    <code>DBLookup("TestingDB","select * from AccountData where Username=?,0:1)</code>
 * 
 *   
 * Following are the different configuration of function DBLookup,
 * 
 * <b>Example 1: Expression Without Arguments.</b>
 * 
 * <code>DBLookup("TestingDB","select Username from AccountData")</code>
 * 	 
 *  Arguments:
 * 	
 *  "TestingDB": Name of the DataSource which will be used for Database look up.
 *  "select Username from AccountData": Query statement to be executed on TestingDB.
 * 
 * 
 * <b>Example 2: Expression with Arguments.</b>
 * 
 *  <code>DBLookup("TestingDB","select calling_station_id from Accountdata where username = ?","XYZ")</code>
 * 
 *  					OR
 * 
 *  <code>DBLookup("TestingDB","select calling_station_id from Accountdata where username = ?",0:1)</code>
 * 
 * 
 * <b>Example 3: Expression with Function.</b>
 * 
 *  <code>DBLookup("TestingDB","{? = call function_name(InParam1,InParam2,..,InParamN)",arg1,...argN)</code>
 * 
 *    Here the First "?" will be used for OUT parameter as function must return some value.
 *    So First "?" will always be used as OUT Parameter for function.
 *    The Arguments of expression will be used to set the InParameters of the function.
 * 
 * 
 * <b>Example 4: Expression with Procedure.</b>
 * 
 *    <code>DBLookup("TestingDB","{call procedure_name(InParam1,InParam2,..,InParamN)",arg1,...argN)</code>
 * 
 *    As the procedure doesn't return any value, so IN as well OUT parameters will be specified in the procedure.
 *    The Arguments of the expression will be used to set InParameters of the procedure in following manner.
 * 
 *    <strong>case 1:</strong> expression without Arguments,
 * 
 *     <code>DBLookup("TestingDB","{call procedure_name(InParam1,InParam2,...,InParamN)")</code>
 * 
 *     If the Arguments are not given in expression then all the InParameters of procedure will be set as OUT Parameters.
 * 
 *  -----------------------------------------
 * 
 *    <strong>case 2:</strong> Number of Arguments in expression = Number of Procedure InParameters
 *
 *     <code>DBLookup("TestingDB","{call procedure_name(InParam1,InParam2)",Arg1,Arg2)</code>
 * 
 *     If the number of Arguments are equal to number of InParameters of procedure,then all Arguments of 
 *     expression will be set as InParameter of the procedure.
 * 
 *  -----------------------------------------
 * 
 *    <strong>case 3:</strong> Number of Arguments in expression < Number of InParameters of Procedure
 * 
 *     <code>DBLookup("TestingDB","{call procedure_name(InParam1,InParam2,InParam3)",Arg1,Arg2)</code>
 * 
 *     Here, both the Arguments will be set in first two InParameters of procedure and remaining 3rd InParameter 
 *     of the procedure will be set as OUT Parameter. 
 * 
 *  </pre>
 *  
 *  @author sanjay.dhamelia
 */
public class FunctionDBLookup extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "DB-LOOK-UP";

	private static final int INDEX_OF_DATASOURCE_NAME = 0;
	private static final int INDEX_OF_QUERY = 1;
	private static final int MIN_ARG_LENGTH = 2;

	private static final int FIRST_COLUMN = 1;
	private static final int FUCTION_OUT_PARAM_INDEX = 1;
	private static final String FUNCTION_START_SYMBOL = "{?";
	private static final String PROCEDURE_START_SYMBOL = "{";
	private static final char IN_PARAM_SYMBOL = '?';
	
	transient private ConnectionProvider connectionProvider;
	
	public FunctionDBLookup(ConnectionProvider connectionProvider) {
		if(connectionProvider == null){
			throw new NullPointerException("Connection Provider is null.");
		}
		this.connectionProvider = connectionProvider;
	}

	@Override
	public int getFunctionType() {
		return Expression.FunctionExpression;
	}
	
	@Override
	public String getName() {
		return "DBLookup";
	}

	/**
	 * This method takes ValueProvider as arguments and returns the result using ValueProvider (if the configured expression is valid).
	 * <pre>
	 * <strong>Example</strong>: will return the AcctSessionID of the user = CSM
	 * 
	 * <code>DBLookup("TestingDB","select AcctSessionID from TEST where Username = 'CSM' ")</code>
	 * </pre>
	 * @throws IllegalStateException
	 * 
	 *  Following are the possible scenarios with this exception.
	 * 
	 *  <ul><li>When PreparedStatement for Database is unavailable</li><ul>
	 * 
	 * @throws IllegalArgumentException
	 *  Following are the scenarios which will throw this exception.
	 *   <ul><li>Numbers of arguments < MIN_ARGUMENTS(2)</li>                        
	 *   <li>When arguments of the expression is greater than INParameters of Query/Procedure/Function</li>           
	 *
	 * <pre>    Any other exception except(MissingIdentifierException,IllegalArgumentException,InvalidTypeCastException) 
	 *    will be wrapped as IllegalArgumentException like,</pre>
	 *   <ul><li>SQLException
	 *   <li>SQLSyntaxException</ul>
	 *   
	 * @throws MissingIdentifierException
	 *  This exception will be raised when the identifier configured in expression is missing.
	 * 	<pre>
	 *   Suppose,
	 *   IN Parameter takes the argument from Request parameter value.However ,if the request doesn't 
	 *   contains the Argument(As an Attribute) specified in the expression,It will raise an exception.
	 *  
	 *  <strong>Example</strong>:<code>DBLookup("TestingDB","select Username from TEST where calling_station_id = ?",0:31)</code>
	 *  
	 *   if Attribute ID = 0:31 is not received in the request,then an Exception will arise.
	 *  </pre>
	 *  
	 * @throws InvalidTypeCastException
	 *   This exception will raise when Invalid Casting Operation is performed.
	 * <pre>
	 *  <strong>Example</strong>:<code>DBLookup("TestingDB","select * from TEST where Username = ?",abs(0:1))</code>
	 *  
	 *   The function <b>abs(argument1)</b> always takes the numeric value as an argument and 
	 *   here casting from non-numeric value to numeric will raise InvalidTypeCastException.
	 * </pre>
	 * 
	 *</pre>
	 */
	@Override
	public String getStringValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		
		if(argumentList == null || argumentList.size() < MIN_ARG_LENGTH){
			throw new IllegalArgumentException("No. of Argument provided for expression is invalid. Reason: Minimum "+ MIN_ARG_LENGTH
					+" arguments must be required. like: DBLookup(\"DataSourceName\",\"select * from tab\")");
		}
		
		String strQuery = argumentList.get(INDEX_OF_QUERY).getStringValue(valueProvider);
		 
		strQuery = strQuery.trim();
		
		if(strQuery.trim().length() == 0)
			throw new IllegalArgumentException("Query should not be empty.");
		
		int inParametersOfQuery = calculateNoOfINParameters(strQuery);
		validateParameterCountInQuery(inParametersOfQuery,valueProvider);
		
		Connection dbConnection = null;
		CallableStatement callableStmt = null;
		
		try{
			String dbSourceName = argumentList.get(INDEX_OF_DATASOURCE_NAME).getStringValue(valueProvider);

			if(dbSourceName.trim().isEmpty()){
				throw new IllegalArgumentException("Data Source name should not be empty.");
			}
			
			dbConnection = connectionProvider.getConnection(dbSourceName);
			callableStmt = dbConnection.prepareCall(strQuery);
			
			if(callableStmt == null){
				throw new IllegalStateException("Prepare statement for datassh crource: "+dbSourceName+" is null.");
			}

			return executeStatement(inParametersOfQuery,valueProvider, callableStmt);

		}catch (MissingIdentifierException e) {
			throw e;
		}catch (InvalidTypeCastException e) {
			throw e;
		}catch (IllegalArgumentException e) {
			throw e;
		}catch (IllegalStateException e) {
			throw e;
		}catch (Exception e) {
			throw new IllegalArgumentException(e);
		}finally{
			
			if(callableStmt != null){
				try{
					callableStmt.close();
				}catch(SQLException e){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().debug(MODULE, e.getMessage());
				}
			}
			
			if(dbConnection != null){
				try{
					dbConnection.close();
				}catch(SQLException e){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().debug(MODULE, e.getMessage());
				}
			}
		}
	}

	private int calculateNoOfINParameters(String strQuery) {
		int inParamsOfQuery = 0;
		for(int i = 0;i<strQuery.length();i++){
			if(strQuery.charAt(i) == IN_PARAM_SYMBOL){
				inParamsOfQuery++;
			}
		}
		return inParamsOfQuery;
	}

	/**
	 * 
	 * @param valueProvider
	 * @param callableStmt
	 * @throws SQLException
	 * @throws InvalidTypeCastException
	 * @throws IllegalArgumentException
	 * @throws MissingIdentifierException
	 */
	private final String executeStatement(int inParametersOfQuery,ValueProvider valueProvider, CallableStatement callableStmt) throws SQLException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		
		String strQuery = argumentList.get(INDEX_OF_QUERY).getStringValue(valueProvider);
		
		if(strQuery.startsWith(FUNCTION_START_SYMBOL)){
			return fillAndExecuteFunction(valueProvider,callableStmt);
		}else if(strQuery.startsWith(PROCEDURE_START_SYMBOL)){
			return fillAndExecuteProcedure(inParametersOfQuery,valueProvider,callableStmt);
		}else {
			return fillAndExecuteQuery(valueProvider, callableStmt);
		}
	}
	
	/**
	 * This method will set the IN parameters of Query/Function/Procedure.
	 * 
	 * @param callbleStmtArgIndex
	 * @param valueProvider
	 * @param callableStmt
	 * @throws SQLException
	 * @throws InvalidTypeCastException
	 * @throws IllegalArgumentException
	 * @throws MissingIdentifierException
	 */
	private final void fillStatementFromIndex(int callbleStmtArgIndex, ValueProvider valueProvider,CallableStatement callableStmt) throws SQLException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		if(argumentList.size() > MIN_ARG_LENGTH){
			for(int inParamIndex = MIN_ARG_LENGTH;inParamIndex < argumentList.size();inParamIndex++,callbleStmtArgIndex++){
				callableStmt.setString(callbleStmtArgIndex, argumentList.get(inParamIndex).getStringValue(valueProvider));
			}
		}
	}
	/**
	 * This method will be executed for the SQL Statements Provided in the expression configuration.
	 * 
	 * @param valueProvider
	 * @param callableStmt
	 * @throws SQLException
	 * @throws InvalidTypeCastException
	 * @throws IllegalArgumentException
	 * @throws MissingIdentifierException
	 */
	private final String fillAndExecuteQuery(ValueProvider valueProvider,CallableStatement callableStmt) throws SQLException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		ResultSet resultSet = null;
		try{
			int callableStmtArgIndex = 1;
			
			fillStatementFromIndex(callableStmtArgIndex,valueProvider, callableStmt);
			
			String finalResult = null;
			resultSet = callableStmt.executeQuery();
			while(finalResult == null && resultSet.next()){
				finalResult = resultSet.getString(FIRST_COLUMN);
			}
			return finalResult;
		}finally{
			if(resultSet != null){
				try {
					resultSet.close();
				} catch (SQLException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().debug(MODULE, e.getMessage());
				}
			}
		}
	}
	
	/**
	 * This method will be executed for the SQL Function Provided in expression configuration. 
	 * @param valueProvider
	 * @param callableStmt
	 * @throws SQLException
	 * @throws InvalidTypeCastException
	 * @throws IllegalArgumentException
	 * @throws MissingIdentifierException
	 */
	private final String fillAndExecuteFunction(ValueProvider valueProvider, CallableStatement callableStmt) throws SQLException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		int callableStmtArgIndex = FUCTION_OUT_PARAM_INDEX;
		callableStmt.registerOutParameter(callableStmtArgIndex++, Types.VARCHAR);
		
		fillStatementFromIndex(callableStmtArgIndex, valueProvider, callableStmt);
		
		callableStmt.execute();
		return callableStmt.getString(FIRST_COLUMN);
	}

	/**
	 * This method will be executed for the SQL Statements Provided in expression configuration.
	 * 
	 * @param valueProvider
	 * @param callableStmt
	 * @throws SQLException
	 * @throws InvalidTypeCastException
	 * @throws IllegalArgumentException
	 * @throws MissingIdentifierException
	 */
	private final String fillAndExecuteProcedure(int inParametersOfQuery,ValueProvider valueProvider, CallableStatement callableStmt) throws SQLException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		
		int callableStmtArgIndex = 1;
		int outParameterStartIndexForProc = 1;

		int noOfArgmentsInExpression = argumentList.size() - MIN_ARG_LENGTH;
		
		if(noOfArgmentsInExpression == 0){
			
			registerOutParameterForProcedure(callableStmtArgIndex, inParametersOfQuery, callableStmt);
			callableStmt.execute();
			return callableStmt.getString(outParameterStartIndexForProc);
			
		}else if(inParametersOfQuery == noOfArgmentsInExpression){
			
			fillStatementFromIndex(callableStmtArgIndex, valueProvider, callableStmt);
			callableStmt.execute();
			return null;
			
		}else {
			int noOfOutParameters = inParametersOfQuery - noOfArgmentsInExpression;
			outParameterStartIndexForProc = noOfArgmentsInExpression + 1;
			
			fillStatementFromIndex(callableStmtArgIndex, valueProvider, callableStmt);
			registerOutParameterForProcedure(outParameterStartIndexForProc, noOfOutParameters, callableStmt);

			callableStmt.execute();
			return callableStmt.getString(outParameterStartIndexForProc);
		}
	}
	
	private final void registerOutParameterForProcedure(int callableStmtArgIndex,int noOfOutParametersToRegister,CallableStatement callableStatement) throws SQLException{
		for(int indexOfOutParams = 0;indexOfOutParams<noOfOutParametersToRegister;indexOfOutParams++,callableStmtArgIndex++){
			callableStatement.registerOutParameter(callableStmtArgIndex, Types.VARCHAR);
		}
	}
	
	private final void validateParameterCountInQuery(int inParamsOfQuery,ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		if(inParamsOfQuery < argumentList.size() - MIN_ARG_LENGTH){
			LogManager.getLogger().warn(MODULE, "Provided IN Parameters for Expression DBLookup is greater than the Query parameters.");
			throw new IllegalArgumentException("Provided IN Parameters for Expression DBLookup is greater than the Query parameters.");
		}
	}

	public long getLongValue(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException{
		return 0;
	}
}