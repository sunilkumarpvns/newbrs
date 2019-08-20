package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.commons.net.FakeAddressResolver;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.compiler.CompilerFactory;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;


@RunWith(JUnitParamsRunner.class)
public class FunctionNetMatchTest {

	private Compiler compiler;
	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Mock
	private ValueProvider valueProvider;
	@Mock
	public UnknownHostException ex;
	private FakeAddressResolver fakeAddressResolver;

	@Before
	public void setup(){

		MockitoAnnotations.initMocks(this);
		fakeAddressResolver = new FakeAddressResolver()
				.addHost("127.0.0.0", new byte[] {127, 0, 0, 0})
				.addHost("127.0.0.2", new byte[] {127, 0, 0, 2})
				.addHost("127.0.0.3", new byte[] {127, 0, 0, 3})
				.addHost("127.0.0.4", new byte[] {127, 0, 0, 4})
				.addHost("127.0.0.5", new byte[] {127, 0, 0, 5});
		
		compiler = new CompilerFactory().newCompiler();
		compiler.addFunction(new FunctionNetMatch(fakeAddressResolver));
		
	}

	public Object[][] dataFor_TestGetStringValue_ShouldReturnTrueIfGivenIPAddressBelongsToASpecifiedNetworkOtherwiseReturnFalse_WhenValidArgumentIsPassed(){

		return new Object[][]{

				{"netmatch(\"127.0.0.1\",\"127.0.0.0-127.0.0.4\")",true},
				{"netmatch(\"127.0.0.5\",\"127.0.0.1-127.0.0.3\")",false},
				{"netmatch(\"127.0.0.1\",\"127.0.0.1/24\")",true},
				{"netmatch(\"0.0.0.0\",\"127.0.0.1/24\")",false},
				{"netmatch(\"127.0.0.1\",\"127.0.0.1\")",true},
				{"netmatch(\"127.0.0.1\",\"127.0.0.1-\")",false},
				{"netmatch(\"127.0.0.1\",\"127.0.0.1/33\")",false},
				{"netmatch(\"127.0.0.1\",\"127.0.0.1/\")",false},

		};
	}

	@Test
	@Parameters(method = "dataFor_TestGetStringValue_ShouldReturnTrueIfGivenIPAddressBelongsToASpecifiedNetworkOtherwiseReturnFalse_WhenValidArgumentIsPassed")
	public void testGetStringValue_ShouldReturnTrueIfGivenIPAddressBelongsToASpecifiedNetworkOtherwiseReturnFalse_WhenValidArgumentIsPassed(
			String function, String expected) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, 
			MissingIdentifierException{

		Expression expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected, value);

	}

	public Object[][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsNotEqualToTWo(){

		return new Object[][]{

				{"netmatch(\"127.0.0.1\",\"127.0.0.1\",\"127.0.0.1\")"},
				{"netmatch()"}
		};
	}
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsNotEqualToTWo")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsNotEqualToTWo(String function)
			throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException, InvalidExpressionException{

		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameter mismatch ,NetMatch function must have TWO arguements 1)IP Address   "
				+ "2)IP-Range OR NETMASK");
		String value = expression.getStringValue(valueProvider);

	}

	public Object[][] dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenInvalidIPAddressIsPassedAsAnArgument(){

		return new Object[][]{

				{"netmatch(\"1.1.1.1\",\"1.1.1.1\")","1.1.1.1"},
				{"netmatch(\" \",\"1.1.1.1\")"," "},
		};
	}

	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenInvalidIPAddressIsPassedAsAnArgument")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenInvalidIPAddressIsPassedAsAnArgument(
			String function, String arg) throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{

		Expression expression = compiler.parseExpression(function);
		Mockito.when(ex.getMessage()).thenReturn("No address associated with hostname");
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid host configured in Function Reason :"+arg);
		String value =expression.getStringValue(valueProvider);

	}


	@Test
	public void testGetLongValue_ShouldThroughInvalidTypeCastException_WhenAnyArgumentIsPassed() throws 
	InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{

		Expression expression = compiler.parseExpression("netmatch(\"127.0.0.1\",\"127.0.0.0-127.0.0.3\")");
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("cannot cast a string to integer");
		long value = expression.getLongValue(valueProvider);

	}

	public Object[][] dataFor_TestGetStringValue_ShouldThrowInvalidTypeCastException_WhenNetMaskBitsIsNonNumeric(){

		return new Object[][]{

				{"netmatch(\"127.0.0.1\",\"127.0.0.1/1a\")","1a"},
				{"netmatch(\"127.0.0.1\",\"127.0.0.1/ \")"," "},
		};
	}


	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldThrowInvalidTypeCastException_WhenNetMaskBitsIsNonNumeric")
	public void testGetStringValue_ShouldThrowInvalidTypeCastException_WhenNetMaskBitsIsNonNumeric(String function, String arg) throws InvalidExpressionException, InvalidTypeCastException,
	IllegalArgumentException, MissingIdentifierException{

		Expression expression = compiler.parseExpression(function);
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("NetMask Bits :"+arg.trim()+" must be numeric ");
		String value = expression.getStringValue(valueProvider);
	}


	@Test
	public void testGetStringValue_ShouldReturnTrueIfGivenIPAddressBelongsToASpecifiedNetworkOtherwiseReturnFalse_WhenIdentifierIsPassedAsAnArgument(
			) throws InvalidExpressionException, InvalidTypeCastException,
			MissingIdentifierException, IllegalArgumentException{

		Expression expression = compiler.parseExpression("netmatch(0:4,\"127.0.0.0-127.0.0.3\")");
		Mockito.when(valueProvider.getStringValue("0:4")).thenReturn("127.0.0.1");
		String value = expression.getStringValue(valueProvider);
		assertEquals("true", value);

	}


}
