package com.elitecore.exprlib.scanner.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.exprlib.scanner.Scanner;
import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.symbol.impl.IdentifierSymbol;
import com.elitecore.exprlib.symbol.impl.operator.GreaterThan;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class ScannerImplTest {

	private Scanner scanner;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	@Before
	public void setUp() {
		scanner = new ScannerImpl();
	}

	public Object[][] dataProviderFor_isWhitespace_should_return_boolean() {
		return new Object[][]{
				{
					' ', true				
				},
				{
					'\t', true				
				},
				{
					'\r', true				
				},
				{
					'\f', true				
				},
				{
					'\n', true				
				},
				{
					',', true				
				},
				{
					'a', false				
				},
				{
					';', false				
				},
				{
					'.', false				
				},
				{
					'^', false				
				},
				{
					'`', false				
				},
		};
	}
	
	
	@Test
	@Parameters(method="dataProviderFor_isWhitespace_should_return_boolean")
	public void test_isWhitespace_should_return_boolean(char ch, boolean result) {
		TempScanner scanner = new TempScanner();
		assertEquals(result, scanner.isWhitespace(ch));
	}
	
	class TempScanner extends ScannerImpl{		
		public boolean isWhitespace(char ch) {
			return super.isWhitespace(ch);
		}
	}
	
	public void setUpFor_scanned_with_defined_whitespace(List<Character> whitespaces, List<Character> operators) {
		scanner = new ScannerImpl(whitespaces, operators);
	}
	
	public Object[][] dataProvideFor_test_scanner(){
		return new Object[][]{
				
//					Expression 		whitespace				operators								expected Symbol list
				{
					" A > B ",		Arrays.asList(' '),		Arrays.asList('(',')','>'),	 			Arrays.asList(new IdentifierSymbol("A"),
																												new GreaterThan(">"),
																												new IdentifierSymbol("B"))
				},
				{
					" A > B ", 		Collections.<Character>emptyList(),	Arrays.asList('(',')','>'),	Arrays.asList(new IdentifierSymbol(" A "),
																												new GreaterThan(">"),
																												new IdentifierSymbol(" B "))
				},
				{
					"A>,B",			Arrays.asList(','),		Arrays.asList('(',')'), 				Arrays.asList(new IdentifierSymbol("A>"),
																												new IdentifierSymbol("B"))
				},
				{
					" A >, B ",		Arrays.asList(','),		Collections.<Character>emptyList(), 	Arrays.asList(new IdentifierSymbol(" A >"),
																												new IdentifierSymbol(" B "))
				},
				{
					" A > B ",		Collections.<Character>emptyList(),		Collections.<Character>emptyList(), 	Arrays.asList(new IdentifierSymbol(" A > B "))
				}
		};
	}
	
	/**
	 * @param expression 
	 * @param whitespaceList
	 * @param operators
	 * @param expectedSymbols
	 * @throws Exception
	 */
	@Test
	@Parameters(method="dataProvideFor_test_scanner")
	public void test_scanner(
			String expression,
			List<Character> whitespaceList,
			List<Character> operators,
			List<Symbol> expectedSymbols) throws Exception{
		
		setUpFor_scanned_with_defined_whitespace(whitespaceList, operators);
		List<Symbol> actulSymbols = scanner.getSymbols(expression);
		assertEquals(expectedSymbols, actulSymbols);
	}
	
}
