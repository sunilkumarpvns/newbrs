package com.elitecore.commons.base;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

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
public class StringsTest {

	private static final String EMPTY_STRING = "";
	private static final Object[] SOME_ARRAY = new Object[]{};
	private static final String SOME_NON_EMPTY_STRING = "SOME_STRING";
	private static final Iterable<?> SOME_ITERABLE = Collections.emptyList();
	private static final Integer ANY_INT = -1;
	private static final Long ANY_LONG = -1L;
	private static final String ANY_NON_NUMBER = "not a number";
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	@Parameters(method = "dataFor_testIsNullOrEmpty")
	public void testIsNullOrEmpty(String inputString, boolean expectedResult){
		boolean result = Strings.isNullOrEmpty(inputString);
		assertEquals(expectedResult, result);
	}
	
	public Object[][] dataFor_testIsNullOrEmpty(){
		return new Object[][]{
				{null, 	true},
				{EMPTY_STRING, 	true},
				{" ", 	false},
				{"\t", 	false},
				{"a",	false},
				{"ab", 	false}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testIsNullOrBlank")
	public void testIsNullOrBlank(String inputString, boolean expectedResult){
		boolean result = Strings.isNullOrBlank(inputString);
		assertEquals(expectedResult, result);
	}
	
	public Object[][] dataFor_testIsNullOrBlank(){
		return new Object[][]{
				{null, 	true},
				{EMPTY_STRING, 	true},
				{" ", 	true},
				{"\t", 	true},
				{"a",	false},
				{"ab", 	false}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testRepeat_ShouldThrowIllegalArgumentException_WhenCountIsLessThanZero")
	public void testRepeat_ShouldThrowIllegalArgumentException_WhenCountIsLessThanZero(String inputString, int count){
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("invalid count: " + count);
		
		Strings.repeat(inputString, count);
	}
	
	public Object[][] dataFor_testRepeat_ShouldThrowIllegalArgumentException_WhenCountIsLessThanZero(){
		return new Object[][]{
				{EMPTY_STRING, 	-1},
				{EMPTY_STRING, 	-2},
				{EMPTY_STRING, 	-3},
		};
	}
	
	@Test
	public void testRepeat_ShouldThrowNullPointerException_WhenInputStringIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("string is null");
		
		Strings.repeat(null, 0);
	}
	
	@Test
	@Parameters(method = "dataFor_testRepeat")
	public void testRepeat(String inputString, int count, String expectedString){
		String actualString = Strings.repeat(inputString, count);
		assertEquals(expectedString, actualString);
	}
	
	public Object[][] dataFor_testRepeat(){
		return new Object[][]{
				{EMPTY_STRING, 	0, 		EMPTY_STRING},
				{EMPTY_STRING, 	1,		EMPTY_STRING},
				{EMPTY_STRING, 	2,		EMPTY_STRING},
				{"a",	0,		EMPTY_STRING},
				{"a",	1,		"a"},
				{"a",	2,		"aa"},
				{"hey",	3,		"heyheyhey"}
		};
	}
	
	@Test
	public void testJoinWithArray_ShouldThrowNullPointerException_WhenSeparatorIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("separator is null");
		
		Strings.join(null, new Object[]{});
	}
	
	@Test
	public void testJoinWithArray_ShouldThrowNullPointerException_WhenPartsAreNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("parts are null");
		
		Strings.join(EMPTY_STRING, (Object[])null);
	}
	
	@Test
	public void testJoinWithIterable_ShouldThrowNullPointerException_WhenSeparatorIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("separator is null");
		
		Strings.join(null, Collections.emptyList());
	}
	
	@Test
	public void testJoinWithIterable_ShouldThrowNullPointerException_WhenPartsAreNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("parts are null");
		
		Strings.join(EMPTY_STRING, (Iterable<?>)null);
	}
	
	@Test
	@Parameters(method = "dataFor_testJoin")
	public void testJoinWithIterable(Object[] parts, String separator, String expectedString){
		assertEquals(expectedString, Strings.join(separator, Arrays.asList(parts)));
	}
	
	@Test
	public void testJoinWithIterableAndFunction_ShouldThrowNullPointerException_IfPartFunctionIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("partFunction is null");
		
		Strings.join(SOME_NON_EMPTY_STRING, SOME_ITERABLE, null);
	}
	
	@Test
	@Parameters(method = "dataFor_testJoinWithSingleQuoteFunction")
	public void testJoinWithIterableAndFunction(Object[] parts, String separator, String expectedString){
		assertEquals(expectedString, Strings.join(separator, Arrays.asList(parts), Strings.WITHIN_SINGLE_QUOTES));
	}
	
	@Test
	@Parameters(method = "dataFor_testJoin")
	public void testJoinWithArray(Object[] parts, String separator, String expectedString){
		assertEquals(expectedString, Strings.join(separator, parts));
	}

	public Object[][] dataFor_testJoin(){
		return new Object[][]{
				{new String[]{},			",", 	EMPTY_STRING},
				{new String[]{EMPTY_STRING,EMPTY_STRING}, 		EMPTY_STRING, 	EMPTY_STRING},
				{new String[]{EMPTY_STRING,EMPTY_STRING}, 		" ", 	" "},
				{new String[]{"a","b"}, 	",", 	"a,b"},
				{new String[]{"a",null}, 	",", 	"a"},
				{new String[]{"a",null,"b"},",", 	"a,b"},
				{new String[]{null,"a","b"},",", 	"a,b"},
		};
	}
	
	@Test
	public void testJoinWithArrayAndFunction_ShouldThrowNullPointerException_IfPartFunctionIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("partFunction is null");
		
		Strings.join(SOME_NON_EMPTY_STRING, SOME_ARRAY, null);
	}
	
	@Test
	@Parameters(method = "dataFor_testJoinWithSingleQuoteFunction")
	public void testJoinWithArrayAndFunction(Object[] parts, String separator, String expectedString){
		assertEquals(expectedString, Strings.join(separator, parts, Strings.WITHIN_SINGLE_QUOTES));
	}
	
	public Object[] dataFor_testJoinWithSingleQuoteFunction() {
		return new Object[][]{
				{new String[]{},			",", 	EMPTY_STRING},
				{new String[]{EMPTY_STRING,EMPTY_STRING}, 		EMPTY_STRING, 	"''''"},
				{new String[]{EMPTY_STRING,EMPTY_STRING}, 		" ", 	"'' ''"},
				{new String[]{"a","b"}, 	",", 	"'a','b'"},
				{new String[]{"a",null}, 	",", 	"'a'"},
				{new String[]{"a",null,"b"},",", 	"'a','b'"},
				{new String[]{null,"a","b"},",", 	"'a','b'"},
		};
	}
	
	@Test
	public void testPadStart_ShouldThrowNullPointerException_IfInputStringIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("string is null");
		
		Strings.padStart(null, 1, '0');
	}
	
	@Test
	@Parameters(method = "dataFor_testPadStart")
	public void testPadStart(String inputString, int minLength, char padChar, String expectedString){
		assertEquals(expectedString, Strings.padStart(inputString, minLength, padChar));
	}
	
	public Object[][] dataFor_testPadStart(){
		return new Object[][]{
				{EMPTY_STRING, 	1, 	'0', 	"0"},
				{"a", 	1, 	'0', 	"a"},
				{"aa", 	1, 	'0', 	"aa"},
				{"a", 	2, 	'0', 	"0a"},
				{EMPTY_STRING, 	2, 	'0', 	"00"},
		};
	}

	@Test
	public void testPadEnd_ShouldThrowNullPointerException_IfInputStringIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("string is null");
		
		Strings.padEnd(null, 1, '0');
	}
	
	@Test
	@Parameters(method = "dataFor_testPadEnd")
	public void testPadEnd(String inputString, int minLength, char padChar, String expectedString){
		assertEquals(expectedString, Strings.padEnd(inputString, minLength, padChar));
	}
	
	public Object[][] dataFor_testPadEnd(){
		return new Object[][]{
				{EMPTY_STRING, 	1, 	'0', 	"0"},
				{"a", 	1, 	'0', 	"a"},
				{"aa", 	1, 	'0', 	"aa"},
				{"a", 	2, 	'0', 	"a0"},
				{EMPTY_STRING, 	2, 	'0', 	"00"},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testSplitter")
	public void testSplitter(String inputString, 
			char splitterChar,  
			String[] expecteds){
		assertArrayEquals(expecteds, 
				Strings.splitter(splitterChar)
				.splitToArray(inputString));
	}
	
	public Object[] dataFor_testSplitter(){
		return $(
			//  Input        |separator| Expected Tokens
				$("a,b,c",      ',',    new String[]{"a", "b", "c"} ),
				$("a,b,c",      '.',    new String[]{"a,b,c"} )
				);
	}
	
	@Test
	public void testIsAbsentOrEmpty_ShouldReturnTrue_WhenOptionalIsAbsent() {
		assertTrue(Strings.isAbsentOrEmpty(Optional.<String>absent()));
	}
	
	@Test
	public void testIsAbsentOrEmpty_ShouldReturnTrue_WhenOptionalIsPresentWithEmptyString() {
		assertTrue(Strings.isAbsentOrEmpty(Optional.of(EMPTY_STRING)));
	}
	
	@Test
	@Parameters(method = "dataFor_testIsAbsentOrEmpty_ShouldNotTrimInputAndJustCheckLength")
	public void testIsAbsentOrEmpty_ShouldNotTrimInputAndJustCheckLength(String input, boolean expectedOutput) {
		assertEquals(expectedOutput, Strings.isAbsentOrEmpty(Optional.of(input)));
	}
	
	public Object[] dataFor_testIsAbsentOrEmpty_ShouldNotTrimInputAndJustCheckLength() {
		return $(
				// input		expected output
				$(" ",			false),
				$(SOME_NON_EMPTY_STRING,	false)
		);
	}
	
	@Test
	public void testIsAbsentOrBlank_ShouldReturnTrue_WhenOptionalIsAbsent() {
		assertTrue(Strings.isAbsentOrBlank(Optional.<String>absent()));
	}
	
	@Test
	public void testIsAbsentOrBlank_ShouldReturnTrue_WhenOptionalIsPresentWithEmptyString() {
		assertTrue(Strings.isAbsentOrBlank(Optional.of(EMPTY_STRING)));
	}
	
	@Test
	@Parameters(method = "dataFor_testIsAbsentOrEmpty_ShouldTrimTheInputStringAndThenCheckForLength")
	public void testIsAbsentOrEmpty_ShouldTrimTheInputStringAndThenCheckForLength(String input, boolean expectedOutput) {
		assertEquals(expectedOutput, Strings.isAbsentOrBlank(Optional.of(input)));
	}
	
	public Object[] dataFor_testIsAbsentOrEmpty_ShouldTrimTheInputStringAndThenCheckForLength() {
		return $(
				// input		expected output
				$(" ",			true),
				$("\t",			true),
				$(SOME_NON_EMPTY_STRING,	false)
		);
	}
	

	@Test
	@Parameters(value = {"a", "ab", "1"})
	public void testNonNullAndNonEmpty_ShouldReturnPredicateReference_ThatEvaluatesToTrueIfStringPassedIsNonNullAndNonEmpty(String input) {
		assertTrue(Strings.nonNullAndNonEmpty().apply(input));
	}
	
	@Test
	public void testNonNullAndNonEmpty_ShouldReturnPredicateReference_ThatEvaluatesToFalseIfStringPassedIsNull() {
		assertFalse(Strings.nonNullAndNonEmpty().apply(null));
	}
	
	@Test
	public void testNonNullAndNonEmpty_ShouldReturnPredicateReference_ThatEvaluatesToFalseIfStringPassedIsEmpty() {
		assertFalse(Strings.nonNullAndNonEmpty().apply(EMPTY_STRING));
	}
	
	@Test
	@Parameters(value = {"a", "ab", "1"})
	public void testNonNullAndNonBlank_ShouldReturnPredicateReference_ThatEvaluatesToTrueIfStringPassedIsNonNullAndNonBlank(String input) {
		assertTrue(Strings.nonNullAndNonBlank().apply(input));
	}
	
	@Test
	public void testNonNullAndNonBlank_ShouldReturnPredicateReference_ThatEvaluatesToFalseIfStringPassedIsNull() {
		assertFalse(Strings.nonNullAndNonBlank().apply(null));
	}
	
	@Test
	@Parameters(value = {" ", "  ", "\t"})
	public void testNonNullAndNonBlank_ShouldReturnPredicateReference_ThatEvaluatesToFalseIfStringPassedIsBlank(String input) {
		assertFalse(Strings.nonNullAndNonBlank().apply(input));
	}
	
	@Test
	public void testFilterNullOrEmpty_ShouldRemoveNullOrEmptyElementsFromCollectionPassed() {
		Collection<String> collection = new ArrayList<String>();
		collection.add(SOME_NON_EMPTY_STRING);
		collection.add(EMPTY_STRING);
		collection.add(null);
		collection.add("test");
		
		Strings.filterNullOrEmpty(collection);
		
		assertEquals(Arrays.asList(SOME_NON_EMPTY_STRING, "test"), collection);
	}
	
	@Test
	public void testFilterNullOrBlank_ShouldRemoveNullOrBlankElementsFromCollectionPassed() {
		Collection<String> collection = new ArrayList<String>();
		collection.add(SOME_NON_EMPTY_STRING);
		collection.add(EMPTY_STRING);
		collection.add(" ");
		collection.add("\t");
		collection.add(null);
		collection.add("test");
		
		Strings.filterNullOrBlank(collection);
		
		assertEquals(Arrays.asList(SOME_NON_EMPTY_STRING, "test"), collection);
	}
	
	@Test
	@Parameters(method = "dataFor_testToBoolean")
	public void testToBoolean(String booleanInString,
			boolean expectedBoolean) {
		assertEquals(expectedBoolean, Strings.toBoolean(booleanInString));
	}
	
	public Object[] dataFor_testToBoolean() {
		return $(
				$("true",		true),
				$("true	",		true),
				$("	true",		true),
				$("	true ",		true),
				$("yes",		true),
				$(" yes ",		true),
				$("false",		false),
				$(" false ",	false),
				$("no",			false),
				$(" no ",		false)
		);
	}
	
	@Test
	public void testToBoolean_ShouldReturnTheFalse_IfBooleanStringIsNull() {
		assertFalse(Strings.toBoolean(null));
	}
	
	@Test
	public void testToInt_ShouldReturnAFunction_ThatConvertsStringToInteger() {
		Function<String, Integer> toInt = Strings.toInt();
		assertEquals(ANY_INT, toInt.apply(String.valueOf(ANY_INT)));
	}
	
	@Test
	public void testToInt_ShouldRethowNumberFormatException_IfStringIsNotAnInteger() {
		exception.expect(NumberFormatException.class);
		
		Strings.toInt().apply(ANY_NON_NUMBER);
	}
	
	@Test
	public void testToLong_ShouldReturnAFunction_ThatConvertsStringToLong() {
		assertEquals(ANY_LONG, Strings.toLong().apply(String.valueOf(ANY_LONG)));
	}
	
	@Test
	public void testToLong_ShouldRethowNumberFormatException_IfStringIsNotALong() {
		exception.expect(NumberFormatException.class);
		
		Strings.toLong().apply(ANY_NON_NUMBER);
	}
	
	@Test
	public void testValueOf_ShouldReturnTheOriginalInputToString_IfInputStringIsNotNull() {
		assertEquals(SOME_NON_EMPTY_STRING, Strings.valueOf(SOME_NON_EMPTY_STRING));
	}
	
	@Test
	public void testValueOf_ShouldReturnBlankString_IfInputStringIsNull() {
		assertEquals("", Strings.valueOf(null));
	}
	
	@Test
	public void testValueOf_WithPlaceholder_ShouldReturnTheOriginalToString_IfInputIsNotNull() {
		assertEquals(SOME_NON_EMPTY_STRING, Strings.valueOf(SOME_NON_EMPTY_STRING, Strings.NOT_APPLICABLE));
	}
	
	@Test
	public void testValueOf_WithPlaceholder_ShouldReturnThePlaceholderString_IfInputIsNull() {
		assertEquals(Strings.NOT_APPLICABLE, Strings.valueOf(null, Strings.NOT_APPLICABLE));
	}
	
	@Test
	public void testValueOf_ThrowsNPE_IfPlaceholderIsNull() {
		exception.expect(NullPointerException.class);
		
		Strings.valueOf(null, null);
	}
}
