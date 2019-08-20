package com.elitecore.commons.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.io.Writer;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class IndentingPrintWriterTest {

	@Rule public ExpectedException exception = ExpectedException.none();

	private IndentingWriter indentingWriter;
	private StringWriter stringWriter;

	@Before
	public void setUp(){
		stringWriter = new StringWriter();
		indentingWriter = new IndentingPrintWriter(stringWriter);
	}
	
	@SuppressWarnings("resource")
	@Test
	public void testConstructor_ShouldThrowNPE_WhenWriterArgumentIsNull(){
		exception.expect(NullPointerException.class);
		
		new IndentingPrintWriter((Writer)null);
	}
	
	@Test
	@Parameters(method = "dataFor_testIncrementIndentation_ShouldPrependTabsEqualToCurrentIndentationCount")
	public void testIncrementIndentation_ShouldPrependTabsEqualToCurrentIndentationCount(int indentationCount, String stringToAppend, String expectedString){
		incrementIndentation(indentationCount);
		
		indentingWriter.print(stringToAppend);
		
		assertEquals(expectedString, stringWriter.toString());
	}
	
	public Object[][] dataFor_testIncrementIndentation_ShouldPrependTabsEqualToCurrentIndentationCount() {
		return new Object[][] {
				{0, 	"any", 		"any"},
				{1, 	"any",		"\tany"},
				{2,		"any",		"\t\tany"}
		};
	}

	@Test
	public void testDecrementIndentation_ShouldThrowIllegalStateException_IfIndentationCountGetsNegative(){
		exception.expect(IllegalStateException.class);
		exception.expectMessage("indentation cannot be negative");
		
		indentingWriter.decrementIndentation();
	}
	
	@Test
	@Parameters(method = "dataFor_testDecrementIndentation_ShouldDecrementTabsPrinted")
	public void testDecrementIndentation_ShouldDecrementTabsPrinted(int incrementCount, int decrementCount, String stringToAppend, String expectedString){
		incrementIndentation(incrementCount);
		
		decrementIndentation(decrementCount);
		
		indentingWriter.print(stringToAppend);
		
		assertEquals(expectedString, stringWriter.toString());
	}
	private void decrementIndentation(int decrementCount) {
		for(int i = 0; i < decrementCount; i++){
			indentingWriter.decrementIndentation();
		}
	}
	private void incrementIndentation(int incrementCount) {
		for(int i = 0; i < incrementCount; i++){
			indentingWriter.incrementIndentation();
		}
	}
	
	public Object[][] dataFor_testDecrementIndentation_ShouldDecrementTabsPrinted() {
		return new Object[][] {
				{1,	1,	"any",	"any"},
				{2,	1,	"any",	"\tany"},
		};
	}
	
	
	@Test
	@Parameters(method = "dataFor_testPrintWithString")
	public void testPrintWithString(int indentation, String stringToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(stringToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithString() {
		return new Object[][] {
				{0,	"any",	"any"},
				{1,	"any",	"\tany"},
				{2,	"any",	"\t\tany"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testPrintWithBoolean")
	public void testPrintWithBoolean(int indentation, boolean booleanToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(booleanToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithBoolean() {
		return new Object[][] {
				{0,	false,	"false"},
				{1,	true,	"\ttrue"},
				{2,	true,	"\t\ttrue"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testPrintWithChar")
	public void testPrintWithChar(int indentation, char charToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(charToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithChar() {
		return new Object[][] {
				{0,	'a',	"a"},
				{1,	'a',	"\ta"},
				{2,	'a',	"\t\ta"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testPrintWithInt")
	public void testPrintWithInt(int indentation, int intToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(intToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithInt() {
		return new Object[][] {
				{0,	1,	"1"},
				{1,	1,	"\t1"},
				{2,	1,	"\t\t1"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testPrintWithFloat")
	public void testPrintWithFloat(int indentation, float floatToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(floatToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithFloat() {
		return new Object[][] {
				{0,	(float)1.1,	"1.1"},
				{1,	(float)1.1,	"\t1.1"},
				{2,	(float)1.1,	"\t\t1.1"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testPrintWithDouble")
	public void testPrintWithDouble(int indentation, double doubleToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(doubleToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithDouble() {
		return new Object[][] {
				{0,	1.1,	"1.1"},
				{1,	1.1,	"\t1.1"},
				{2,	1.1,	"\t\t1.1"}
		};
	}
	
	
	@Test
	@Parameters(method = "dataFor_testPrintWithByte")
	public void testPrintWithByte(int indentation, byte byteToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(byteToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithByte() {
		return new Object[][] {
				{0,	(byte)1,	"1"},
				{1,	(byte)1,	"\t1"},
				{2,	(byte)1,	"\t\t1"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testPrintWithByte")
	public void testPrintWithLong(int indentation, long longToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(longToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithLong() {
		return new Object[][] {
				{0,	(long)1,	"1"},
				{1,	(long)1,	"\t1"},
				{2,	(long)1,	"\t\t1"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testPrintWithCharArray")
	public void testPrintWithCharArray(int indentation, char[] charsToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(charsToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithCharArray() {
		return new Object[][] {
				{0,	new char[]{'a'},	"a"},
				{1,	new char[]{'a'},	"\ta"},
				{2,	new char[]{'a'},	"\t\ta"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testPrintWithObject")
	public void testPrintWithObject(int indentation, Object objToAppend, String expectedString){
		incrementIndentation(indentation);
		
		indentingWriter.print(objToAppend);
		
		assertTrue(stringWriter.toString().equals(expectedString));
	}
	
	public Object[][] dataFor_testPrintWithObject() {
		return new Object[][] {
				{0,	(Object)"any",	"any"},
				{1,	(Object)"any",	"\tany"},
				{2,	(Object)"any",	"\t\tany"}
		};
	}
}
