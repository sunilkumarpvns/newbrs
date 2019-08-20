package com.elitecore.exprlib.compiler;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.symbol.impl.Operator;
@RunWith(JUnitParamsRunner.class)
public class CompilerTest {
	
	private Compiler compiler;
	public @Rule ExpectedException expectedException = ExpectedException.none();
	private static final String ERROR_MESSAGE_PREFIX = "Invalid Expression: %s, Reason: ";
	private static final String HASH_IS_NOT_ALLOWED = "Hash Operator is not allowed in expression";
	private static final String OPERATOR_MISSING = "Operator missing";
	private static final String LHS_IS_NOT_LOGICAL_EXPRESSION = "LHS operand is not a logical expression";
	private static final String LHS_IS_LOGICAL_EXPRESSION = "LHS operand is logical expression";
	private static final String RHS_IS_NOT_LOGICAL_EXPRESSION = "RHS operand is not a logical expression";
	private static final String RHS_IS_LOGICAL_EXPRESSION = "RHS operand is logical expression";
	private static final String ONLY_LOGICAL_EXPRESSION_SHOULD_BE_WITH_NOT = "Only logical expression should be used with 'NOT' operator";
	private static final String LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN = "Logical Expression should not be used with 'IN' operator";
	private static final String BRACKET_MAY_NOT_CLOSED_PROPERLY = "Bracket may not closed properly, recheck expression";
	@Before
	public void setUp() {
		compiler = Compiler.getDefaultCompiler();
	}
	
	public Object[][] dataProviderFor_parseExperssion_should_not_throw_exception() {
		
		return new Object[][]{
				/*
				*  To differentiate Test between Literal and Identifier, 
				*  	in toString(), LiteralSymbol value is wrapped value is double quotes.
				* 
				*/
				{
					"hi\\\"hello\\\"do", "hi\"hello\"do"
				},
				{
					"abc", "abc"
				},
				{
					"CS.UserName", "CS.UserName"
				},
				{
					"abc+a/a", "abc+a/a"	
				},
				{
					"\"a\"=\"b\"" , "(\"a\" = \"b\")"	
				},
				{
					"(A < B) || C > D", "((A < B) OR (C > D))"		
				},
				{	
					"a < b ", "(a < b)"				
				},
				{
					"(( a < b)) ", "(a < b)"			
				},
				{
					"(a < b)AND c > d ", "((a < b) AND (c > d))"			
				},
				{
					"((a < b) AND (c>d)) ", "((a < b) AND (c > d))"	
				},
				{
					"A < B || B > C", "((A < B) OR (B > C))" 		
				},
				{
					"A < B || B > C && B = D", "(((A < B) OR (B > C)) AND (B = D))"
				},
				{
					"((A < B || B > C) && B = D)", "(((A < B) OR (B > C)) AND (B = D))" 
				},
				{
					"((A < B || B > C) && (B = D))", "(((A < B) OR (B > C)) AND (B = D))" 
				},
//				{
//					"\"A\"=\"B\t\"", "(\"A\" = \"B\t\")"			
//				},
//				{
//					"\"      a \" = \"b\" ", "(\"      a \" = \"b\")" 
//				},
				{
					"abc\\def", "abcdef" 
				},
				{
					"A IN (A,B,C,D)", "((((A = A) OR (A = B)) OR (A = C)) OR (A = D))"		
				},
				{
					"A IN ((A,B))", "((A = A) OR (A = B))"			
				},
				{
					"A<B AND A>D", "((A < B) AND (A > D))"			
				},		
				{
					"((A<B) AND (A>D))", "((A < B) AND (A > D))"			
				},		
				{
					"A<B OR A>D", "((A < B) OR (A > D))"			
				},		
				{
					"NOT(A<B)", "(NOT (A < B))"				
				},
				{
					"NOT((A<B))", "(NOT (A < B))"				
				},
				{
					"A<B AND B<C", "((A < B) AND (B < C))"
				},
				{
					"(A<B) AND B<C","((A < B) AND (B < C))"
				},
				{
					"A<B OR (B<C)", "((A < B) OR (B < C))"
				},
				{
					"A<B AND B<C","((A < B) AND (B < C))"
				},
				{
					"A<B AND (B<C)","((A < B) AND (B < C))"	
				},
				{
					"NOT(A<B)","(NOT (A < B))"			
				},
				{
					"NOT(A<B AND B<C)","(NOT ((A < B) AND (B < C)))"
				},
				{
					"NOT(A = B)","(NOT (A = B))"
				},
				{
					"NOT(A<B AND B<C OR B<D)","(NOT (((A < B) AND (B < C)) OR (B < D)))"	
				},				
				{
					"A = B","(A = B)" 
				},
				{
					"A < B","(A < B)"
				},
				{
					"A > B","(A > B)"
				},
				{
					"\"A\" > B","(\"A\" > B)"
				},	
				
				//FIXME add toString in Functions
			/*	{	"add(A,B)", ""				},
				{	"add(A)", ""				},
				{	"concat(A,B,C)",""			},
				{	"abs(A)",""					},
				{	"ceil(A)",""				},
				{	"floor(A)",""				},
				{	"getYear()",""				},
				{	"getWeekDay()",""			},
				{	"getHour()",""				},
				{	"getDateDifference(A,B)",""	},
				{	"strlen(C)" ,""				},
				{	"max(A,B,C,D,E)","" 			},
				{	"max(min(A,B,C,D),B,C,D,E)","" },
				{	"max(A,B,C,D,E)" ,""			},
				{	"min(A,B,C,D,E)","" 			},*/
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_parseExperssion_should_not_throw_exception")
	public void test_parseExperssion_should_not_throw_exception(String expression, String expectedExpression) throws Exception {
			Expression expression2 = compiler.parseExpression(expression);
			Assert.assertEquals(expectedExpression, expression2.toString());
	}
	
	public Object[][] dataProviderFor_parseExpression_should_throw_InvalidExpressionException(){
		return new Object[][]{
				/*{	"A IN (A<B,B>C)", 		ERROR_MESSAGE_PREFIX},//FIXME 		
				{	"a < b (,)",			ERROR_MESSAGE_PREFIX},				
				{	", , a < b",			ERROR_MESSAGE_PREFIX},// skipping whitespace				
				{	"a , ,", 				ERROR_MESSAGE_PREFIX},// skipping whitespace		
				{	", ,a ,,",				ERROR_MESSAGE_PREFIX},// skipping whitespace	
				{	"A IN (10, ,20)",		ERROR_MESSAGE_PREFIX}, // skipping whitespace, taking as 2 argument
				{	"A IN (10 20 30 40)", 	ERROR_MESSAGE_PREFIX}, // should fail, taking 4 argument
*/				
				{	"ABC\\",				"unexpected token at end of expression : '\'"} , 
				{	"",						"Symbol can't be generated from empty expression string"}, 		
				{	"ABC\"asd",				"Literal not properly ended"},		
				{	"\"abc\"AND",			"Argument missing for 'AND' operator, Syntax: " + Operator.AND.syntax},		
				{	"AND ()",				"Argument missing for 'AND' operator, Syntax: " + Operator.AND.syntax},				
				{	"OR",					"Argument missing for 'OR' operator, Syntax: " + Operator.OR.syntax},				
				{	"(A<B OR) B<C",			"Argument missing for 'OR' operator, Syntax: " + Operator.OR.syntax},
				{	"()",					"No valid operand or operator found in expression"}, 
				{	"A$BC",					OPERATOR_MISSING},  
				{	"A {10,20,20}",			OPERATOR_MISSING},
				{	"$1000",				OPERATOR_MISSING},			
				{	"ABC\"asd\"",			OPERATOR_MISSING},		
				{	"ABC\"a\\sd\"",			OPERATOR_MISSING},		
				{	"\"asd\"ABC",			OPERATOR_MISSING},		
				{	"ABC\"def\"GHI",		OPERATOR_MISSING},		
				{	"\"abc\"DEF\"ghi\"",	OPERATOR_MISSING},		
				{	"A NOT(A < B)",			OPERATOR_MISSING},
				{	"(A )( B)", 			OPERATOR_MISSING},
				{	"(A > B)( B < D)",		OPERATOR_MISSING},
				{	"(\"A\" > \"B\")( B < D)", OPERATOR_MISSING},
				{	"((A > B)AND(A < B))(A < B)" ,OPERATOR_MISSING},
				{	"A + A",				OPERATOR_MISSING},
				{	"xyz(100,10,32)",		OPERATOR_MISSING},
				{	"A A",					OPERATOR_MISSING},		
				{	"\"a\" \"b\"",			OPERATOR_MISSING},
				{	"A B= B ",				OPERATOR_MISSING}, 
				{	"A ++ B" ,				OPERATOR_MISSING},				
				{	"{ab == a}",			OPERATOR_MISSING},
				{	">abc",					"Argument missing for '>' operator, Syntax: " + Operator.GREATERTHAN.syntax	},
				{	"><",					"Argument missing for '>' operator, Syntax: " + Operator.GREATERTHAN.syntax	},
				{	"\"ab\"==\"a\"", 		"Argument missing for '=' operator, Syntax: " + Operator.EQUALS.syntax	},	
				{	"A AND", 				"Argument missing for 'AND' operator, Syntax: " + Operator.AND.syntax	},				
				{	"AND B", 				"Argument missing for 'AND' operator, Syntax: " + Operator.AND.syntax	},
				{	"(A OR  )", 			"Argument missing for 'OR' operator, Syntax: " + Operator.OR.syntax	},				
				{	"A && B || (C > D ",	BRACKET_MAY_NOT_CLOSED_PROPERLY},
				{	"(A OR  AND ",			BRACKET_MAY_NOT_CLOSED_PROPERLY},				
				{	"(A AND ()", 			BRACKET_MAY_NOT_CLOSED_PROPERLY},				
				{	"(A OR B NOT(C)",		BRACKET_MAY_NOT_CLOSED_PROPERLY},				
				{	"(A > B",				BRACKET_MAY_NOT_CLOSED_PROPERLY},				
				{	"((",					BRACKET_MAY_NOT_CLOSED_PROPERLY},	
				{	"(A OR B AND )", 		LHS_IS_NOT_LOGICAL_EXPRESSION	},
				{	"(A + B) || C > D", 	LHS_IS_NOT_LOGICAL_EXPRESSION},				
				{	"(A OR B AND ())",		LHS_IS_NOT_LOGICAL_EXPRESSION},				
				{	"(A AND B)(C AND D)", 	LHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"(A OR B C) AND B",		LHS_IS_NOT_LOGICAL_EXPRESSION},	
				{	"(A OR B) B AND B", 	LHS_IS_NOT_LOGICAL_EXPRESSION},				
				{	"(A AND B C)", 			LHS_IS_NOT_LOGICAL_EXPRESSION},	
				{	"A AND B",				LHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"A AND B<D",			LHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"A AND (B AND C)",		LHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"(B AND C) AND B",		LHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"A OR B",				LHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"A OR B<D",				LHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"A OR (B OR C)",		LHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"(B OR C) OR B",		LHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"A<B AND B",			RHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"A<B OR B",				RHS_IS_NOT_LOGICAL_EXPRESSION},
				{	"NOT(A)",				ONLY_LOGICAL_EXPRESSION_SHOULD_BE_WITH_NOT},
				{	"NOT(\"A\")",			ONLY_LOGICAL_EXPRESSION_SHOULD_BE_WITH_NOT},
				{	"NOT(f3#%5d5)",			ONLY_LOGICAL_EXPRESSION_SHOULD_BE_WITH_NOT},
				{	"NOT()",				"Argument missing for 'NOT' operator, Syntax: " + Operator.NOT.syntax},
				{	"A NOT()",				"Argument missing for 'NOT' operator, Syntax: " + Operator.NOT.syntax},
				{	"NOT(A,B)",				"Argument missing for 'NOT' operator, Syntax: " + Operator.NOT.syntax}, 
				{	"A NOT(",				BRACKET_MAY_NOT_CLOSED_PROPERLY	},
				{	"NOT A",				"'(' missing for 'NOT' operator, Syntax: " + Operator.NOT.syntax},		
				{	"A NOT",				"'(' missing for 'NOT' operator, Syntax: " + Operator.NOT.syntax},
				{	"A IN()",					"Argument missing for 'IN' operator, Syntax: " + Operator.IN.syntax},
				{	"IN(A,B,C,D)",				"Argument missing for 'IN' operator, Syntax: " + Operator.IN.syntax},
				{	"A IN < A,B,C,D",		"'(' missing for 'IN' operator, Syntax: " + Operator.IN.syntax},				
				{	"A IN A,B,C,D",			"'(' missing for 'IN' operator, Syntax: " + Operator.IN.syntax},		
				{	"A IN(A,IN(A,B,C),C,D)",LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN},
				{	"A<B IN (A,B,C)",		LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN},
				{	"B IN (A<B)",	 		LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN}, 
				{	"A IN(A<B AND B<C)",	LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN},
				{	"A IN(A<B OR D>B)",		LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN},
				{	"A IN(A IN(D,E))",		LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN},
				{	"A IN (A<B)",			LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN},
				{	"A IN (A,B>C)",			LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN},
				{	"A IN (A,B=C)",			LOGICAL_EXPRESSION_IS_NOT_ALLOWED_WITH_IN},
				{	"A > B < C",			LHS_IS_LOGICAL_EXPRESSION},
				{	"(B < C) < A",			LHS_IS_LOGICAL_EXPRESSION},
				{	"(A < B) > C < B",		LHS_IS_LOGICAL_EXPRESSION},
				{	"A > (B<C)",			RHS_IS_LOGICAL_EXPRESSION},
				{	"A > (B<C",				BRACKET_MAY_NOT_CLOSED_PROPERLY},
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_parseExpression_should_throw_InvalidExpressionException")
	public void test_parseExpression_should_throw_InvalidExpressionException(String expression, 
			String reason) throws Exception {
		expectedException.expect(InvalidExpressionException.class);
		expectedException.expectMessage(String.format(ERROR_MESSAGE_PREFIX, expression) + reason);
		compiler.parseExpression(expression);
	}
	
	public Object[][] dataProviderFor_test_hash_token_not_in_double_quotes_must_throw_InvalidExpressionException() {
		return new Object[][]{
				{	"(A AND B)(#"           },				
				{	"(A AND B #)"           },				
				{	"#\"A\"=\"B\""          },
				{	"# \"A\"=\"B\""         },
				{	"\"A\"=\"B\" #"         },
				{	"\"A\"=#\"B\""          },
				{	"(\"A\"=#\"B\")"        },
				{	"(\"A\"<\"B\") AND (#)" },
				{	"(\"A\" AND #"          },
				{	"A # B"                 },
				{	"(\"A\" # \"B\""        },
				{	"(\"A\" # \"B\" # \"C\""}
		};
	}
	
	/**
	 * '#' symbol is only allowed in double quotes otherwise throw  InvalidExpressionException
	 * 
	 * @param expression // String expression
	 * @param isException // whether expression throws InvalidExpressionException
	 * @param expectesMsg // if isException is true, what message it throw
	 * @param expectedExpression // if isException is false, expected expression
	 * 
	 * @author chetan.sankhala
	 */
	@Test
	@Parameters(method="dataProviderFor_test_hash_token_not_in_double_quotes_must_throw_InvalidExpressionException")
	public void test_hash_token_not_in_double_quotes_must_throw_InvalidExpressionException(
			String expression) throws Exception {	
		expectedException.expect(InvalidExpressionException.class);
		expectedException.expectMessage(String.format(ERROR_MESSAGE_PREFIX, expression) + HASH_IS_NOT_ALLOWED);
		compiler.parseExpression(expression);
	}
	
	public Object[][] dataProviderFor_test_hash_token_allowed_only_in_double_quotes_should_not_throw_exception() {
		return new Object[][]{
				{	"\"abc#\"",	"\"abc#\""		},
				{	"\"#\"", 	"\"#\""			},
				{	"\"#.df\"",	"\"#.df\""		}
		};
	}
	
	
	/**
	 * '#' symbol is only allowed in double quotes otherwise throw  InvalidExpressionException
	 * 
	 * @param expression // String expression
	 * @param isException // whether expression throws InvalidExpressionException
	 * @param expectesMsg // if isException is true, what message it throw
	 * @param expectedExpression // if isException is false, expected expression
	 * 
	 * @author chetan.sankhala
	 */
	@Test
	@Parameters(method="dataProviderFor_test_hash_token_allowed_only_in_double_quotes_should_not_throw_exception")
	public void test_hash_token_allowed_only_in_double_quotes_should_not_throw_exception(
			String expression, String expectedExpression) throws Exception {	

		Expression expression2 = compiler.parseExpression(expression);
		
		//if successful parsed, then verify
		Assert.assertEquals(expectedExpression, expression2.toString());
	}
	
	/*public Object[][] dataProviderFor_parserExpression_should_thow_InvalidExpression_when_FunctionExpression_is_invalid() {
		return new Object[][]{
				
				//not done for function expression yet
				{	"add(A,,C)"			},
				{	"add(,B,C)"			},
				{	"add(A,B,)"			},
				{	"add(A,B,C C)"		},
				{	"add(A B,C)"		},
				{	"add(,,C)"			},
				{	"add(10,IN(10))"	}, //this is taken as add(10 IN (10)) one argument
				{	"add()"				},
				{	"add(<A)"			},
				{	"add(A IN())"			},
				{	"add(A IN(A AND D))"	},
				{	"add(A<)"			},
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_parserExpression_should_thow_InvalidExpression_when_FunctionExpression_is_invalid")
	public void test_parserExpression_should_thow_InvalidExpression_when_FunctionExpression_is_invalid(String expression) throws Exception{
		expectedException.expect(InvalidExpressionException.class);
		try {
			compiler.parseExpression(expression);
		} catch (Exception e) {
			System.out.println( expression + " : " + e.getMessage());
			throw e;
		}
	}*/
}