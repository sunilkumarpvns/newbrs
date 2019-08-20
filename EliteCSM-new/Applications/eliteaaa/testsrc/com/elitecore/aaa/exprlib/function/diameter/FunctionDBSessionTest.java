package com.elitecore.aaa.exprlib.function.diameter;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.exprlib.function.FunctionBaseValue;
import com.elitecore.aaa.exprlib.function.diameter.FunctionDBSession.FunctionWhere;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

@RunWith(JUnitParamsRunner.class)
public class FunctionDBSessionTest {

	private Compiler compiler;
	private FunctionDBSession dbSession;
	private FunctionWhere where;
	
	private DiameterRequest diameterRequest = new DiameterRequest();
	private DiameterAVPValueProvider valueProvider ;
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@BeforeClass
	public static void loadDictionary() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp(){
		compiler = Compiler.getDefaultCompiler();
		dbSession = new FunctionDBSession();
		compiler.addFunction(dbSession);
		where = new FunctionWhere();
		compiler.addFunction(where);
		
		diameterRequest.addAvp(DiameterAVPConstants.CALLING_STATION_ID, "elite");
		diameterRequest.addAvp(DiameterAVPConstants.CLASS, "pdpType");
		diameterRequest.addAvp(DiameterAVPConstants.USER_NAME, "elite");
		diameterRequest.addAvp(DiameterAVPConstants.SERVICE_TYPE, "1");
		diameterRequest.setLocatedSessionData(createSessionData());
		valueProvider = new DiameterAVPValueProvider(diameterRequest);
	}


	@Test
	@Parameters (method ="dataFor_ThrowsExceptionWhenNumberOfArgumentsIsLessThanOneOrMoreThanTwo")
	public void throwsIllegalArgumentExceptionWhenNumberOfArgumentsIsLessThanOneOrMoreThanTwo(String function)
			throws Exception {
		
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Number of arguments mismatch, DBSESSION function must have atleast 1 and atmost 2 arguments");

		expression.getStringValues(valueProvider);
	}

	public List<String> dataFor_ThrowsExceptionWhenNumberOfArgumentsIsLessThanOneOrMoreThanTwo() {
		return Arrays.asList(
				"dbsession()",
				"dbsession(param1,param2,param3)"
		);
	}
	
	@Test
	public void throwsMissingIdentifierExceptionWhenLocatedSessionDataIsEmpty()
			throws Exception {
		valueProvider.setValue(DiameterRequest.LOCATED_SESSION_DATA, Collections.emptyList());
		Expression expression = compiler.parseExpression("dbsession(\"pdpType\", where(groupName = \"elite\"))");
		expectedException.expect(MissingIdentifierException.class);
		expectedException.expectMessage("No session found");
		expression.getStringValues(valueProvider);
	}
	
	@Test
	public void throwsMissingIdentifierExceptionWhenLocatedSessionDataIsNull()
			throws Exception {
		valueProvider.setValue(DiameterRequest.LOCATED_SESSION_DATA, null);
		Expression expression = compiler.parseExpression("dbsession(\"pdpType\", where(groupName = \"elite\"))");
		expectedException.expect(MissingIdentifierException.class);
		expectedException.expectMessage("No session found");
		expression.getStringValues(valueProvider);
	}

	@Test 
	public void givenSingleArgument_ReturnsNumberOfSessionsWhenTheArgumentIsAsterisk()
			throws Exception {
		Expression expression = compiler.parseExpression("dbsession(\"*\")");
		String actual = expression.getStringValue(valueProvider);
		
		assertEquals(String.valueOf(4), actual);
	}

	@Test
	public void givenSingleArgument_EvaluatesArgumentToFindColumnNameAndReturnsValuesOfThatColumnFromAllLocatedSessions()
			throws Exception {
		Expression expression = compiler.parseExpression("dbsession(0:25)");
		assertEquals(Arrays.asList("1","12","8","4"), expression.getStringValues(valueProvider));
	}
	
		
	@Test
	public void givenTwoArguments_ThrowsMissingIdentifierExpressionIfSelectorColumnNameIsInvalidOrNotMapped ()
			throws Exception {
		Expression expression = compiler.parseExpression("dbsession(\"pdpType\", where(invalidSelectorColumn = 0:31))");
		expectedException.expect(MissingIdentifierException.class);
		expectedException.expectMessage("No session found with matching expression");
		expression.getStringValues(valueProvider);
	}

	@Test
	public void givenTwoArguments_ThrowsMissingIdentifierExpressionWhenSelectorExpressionDoesNotMatchForAnySession()
			throws Exception {
		Expression expression = compiler.parseExpression("dbsession(\"sessionStatus\", where(groupName = \"neon\" AND sessionStatus=\"active\"))");
		expectedException.expect(MissingIdentifierException.class);
		expectedException.expectMessage("No session found with matching expression");
		expression.getStringValue(valueProvider);
	}
	
	@Test
	@Parameters (method = "dataFor_givenTwoArguments_ReturnsSessionsForWhichTheSelectorExpressionMatches")
	public void givenTwoArguments_ReturnsSessionsForWhichTheSelectorExpressionMatches(String function, List<String> expected) throws Exception {
		Expression expression = compiler.parseExpression(function);
		assertEquals(expected, expression.getStringValues(valueProvider));
	}

	public Object[] dataFor_givenTwoArguments_ReturnsSessionsForWhichTheSelectorExpressionMatches() {
		return $(
				$ ( "dbsession(\"pdpType\", where(groupName = \"elite\"))" , Arrays.asList("1","12")),
				$ ( "dbsession(\"pdpType\", where(pdpType > \"5\"))" , Arrays.asList("12","8")),
				$ ( "dbsession(\"sessionStatus\", where(NOT(sessionStatus = \"active\")))" , Arrays.asList("inactive","deleted")),
				$ ( "dbsession(\"pdpType\", where(groupName IN(\"elite\" , \"neon\")))", Arrays.asList("1","12","4"))
		);
	}
	
	@Test 
	@Parameters(method = "dataFor_givenTwoArgumentsAndFristArgumentIsAsterisk_ReturnsNumberOfSessionsThatMatchTheSelectorExpression")
	public void givenTwoArgumentsAndFristArgumentIsAsterisk_ReturnsNumberOfSessionsThatMatchTheSelectorExpression(String function, String expectedCount)
			throws Exception {
		Expression expression = compiler.parseExpression(function);
		String actual = expression.getStringValue(valueProvider);
		
		assertEquals(expectedCount, actual);
	}
	
	public Object[] dataFor_givenTwoArgumentsAndFristArgumentIsAsterisk_ReturnsNumberOfSessionsThatMatchTheSelectorExpression() {
		return $(
				$ ( "dbsession(\"*\", where(groupName = \"elite\"))" , "2"),
				$ ( "dbsession(\"*\", where(groupName = 0:1))" , "2")
		);			
	}

	@Test
	@Parameters (method = "dataFor_GivenTwoArguments_ThrowsInvalidTypeCastExceptionIfSecondArgumentIsOtherThanWhereFunction")
	public void givenTwoArguments_ThrowsInvalidTypeCastExceptionIfSecondArgumentIsOtherThanWhereFunction(String function)
			throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(InvalidTypeCastException.class);
		expectedException.expectMessage("Second Argument must be where function");
		expression.getStringValues(valueProvider);
	}

	public List<String> dataFor_GivenTwoArguments_ThrowsInvalidTypeCastExceptionIfSecondArgumentIsOtherThanWhereFunction() {
		return Arrays.asList(
				"dbsession(\"pdpType\", groupName = \"elite\")" ,
				"dbsession(\"pdpType\",add(\"1\",\"2\"))"
		);
	}
	
	@Test
	public void returnsFirstSessionIfComposedWithinFunctionGetFirst()
			throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression("getfirst(dbsession(\"pdpType\", where(groupName = \"elite\")))");
		assertEquals("1", expression.getStringValue(valueProvider) );
	}

	@Test
	public void returnsLastSessionIfComposedWithinFunctionGetLast()
			throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException {
		Expression expression = compiler.parseExpression("getlast(dbsession(\"pdpType\", where(groupName = \"elite\")))");
		assertEquals("12",expression.getStringValue(valueProvider) );
	}

	@Test
	@Parameters(method = "dataFor_ThrowsMissingIdentifierExpressionIfColumnNameIsInvalid")
	public void throwsMissingIdentifierExpressionIfColumnNameIsInvalidOrNotMapped (String function)
			throws Exception {
		Expression expression = compiler.parseExpression(function);
		expectedException.expect(MissingIdentifierException.class);
		expectedException.expectMessage("Column name is invalid or not mapped: invalidColumName");
		expression.getStringValues(valueProvider);
	}
	
	public List<String> dataFor_ThrowsMissingIdentifierExpressionIfColumnNameIsInvalid() {
		return  Arrays.asList(
				"dbsession(\"invalidColumName\")",
				"dbsession(\"invalidColumName\", where(groupName = \"elite\"))"
		);
	}

	@Test
	public void dBSessionInLogicalExpression()
			throws Exception {
		FunctionBaseValue baseValue = new FunctionBaseValue();
		compiler.addFunction(baseValue);
		LogicalExpression expression = (LogicalExpression) compiler.parseExpression("baseValue(0:6) = dbsession(\"pdpType\", where(groupName = \"elite\"))");
		assertTrue(expression.evaluate(valueProvider));
	}
	
	public List<SessionData> createSessionData() {
		List<SessionData> sessionData = new ArrayList<SessionData>();

		String sessionStatus = "sessionStatus";
		String pdpType = "pdpType";
		String groupName = "groupName";
		SessionDataImpl session1 = new SessionDataImpl("schema", "1", Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
		session1.addValue(sessionStatus, "active");
		session1.addValue(pdpType, "1");
		session1.addValue(groupName, "elite");
		sessionData.add(session1);

		SessionDataImpl session2 = new SessionDataImpl("schema", "2", Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
		session2.addValue(sessionStatus, "inactive");
		session2.addValue(pdpType, "12");
		session2.addValue(groupName, "elite");
		sessionData.add(session2);

		SessionDataImpl session3 = new SessionDataImpl("schema","3", Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
		session3.addValue(sessionStatus, "active");
		session3.addValue(pdpType, "8");
		sessionData.add(session3);

		SessionDataImpl session4 = new SessionDataImpl("schema","4", Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
		session4.addValue(sessionStatus, "deleted");
		session4.addValue(pdpType, "4");
		session4.addValue(groupName, "neon");
		sessionData.add(session4);

		return sessionData;

	}

}
