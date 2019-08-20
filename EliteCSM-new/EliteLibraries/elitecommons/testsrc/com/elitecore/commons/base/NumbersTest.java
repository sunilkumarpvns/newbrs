package com.elitecore.commons.base;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
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
public class NumbersTest {
	private static final int ANY_INT = -1;
	private static final Integer ANY_BOXED_INT = -1;
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	@Parameters(method = "dataFor_testToByteArrayWithInt")
	public void testToByteArrayWithInt(int value, int noOfBytes, byte[] expectedBytes){
		byte[] actualBytes = Numbers.toByteArray(value, noOfBytes);
		assertArrayEquals(expectedBytes, actualBytes);
	}
	
	public Object[][] dataFor_testToByteArrayWithInt() {
		return new Object[][] {
				{123, 1, new byte[] {123}},
				
				{891, 3, new byte[] {0, 3, 123}},
				
				{456, 4, new byte[] {0, 0, 1, -56}},
				
				{-1, 4, new byte[] {-1, -1, -1, -1}},
				
				{65537, 4, new byte[] {0, 1, 0, 1}},
				
				{Integer.MIN_VALUE, 4 , new byte[] {-128, 0, 0, 0}}
		};
	}
	
	@Test
	@Parameters({"1,-1","1,-2"})
	public void testToByteArrayWithInt_ShouldThrowIllegalArgumentException_IfNoOfBytesIsLessThanZero(int value, int noOfBytes){
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("noOfBytes cannot be negative");
		
		Numbers.toByteArray(value, noOfBytes);
	}
	
	@Test
	public void testToByteArrayWithInt_ShouldReturnAnEmptyByteArray_WhenNoOfBytesIsZero(){
		int ZERO = 0;
		assertArrayEquals(new byte[]{}, Numbers.toByteArray(1, ZERO));
	}
	
	@Test
	@Parameters(method = "dataFor_testParseInt_ShouldReturnParsedIntegerValue_IfParseableAsInteger")
	public void testParseInt_ShouldReturnParsedIntegerValue_IfParseableAsInteger(String value, int defaultValue, int expectedValue){
		assertEquals(expectedValue, Numbers.parseInt(value, defaultValue));
	}
	
	public Object[][] dataFor_testParseInt_ShouldReturnParsedIntegerValue_IfParseableAsInteger() {
		return new Object[][] {
				//value								default		expected
				{String.valueOf(Integer.MIN_VALUE),		0,	Integer.MIN_VALUE},
				{"-100",								0,	-100},
				{"0",									0,	0},
				{"100",									0,	100},
				{String.valueOf(Integer.MAX_VALUE),		0,	Integer.MAX_VALUE}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParseInt_WithBoxedInteger_ShouldReturnParsedIntegerValue_IfParseableAsInteger")
	public void testParseInt_WithBoxedInteger_ShouldReturnParsedIntegerValue_IfParseableAsInteger(String value, Integer defaultValue, Integer expectedValue){
		assertEquals(expectedValue, Numbers.parseInt(value, defaultValue));
	}
	
	public Object[][] dataFor_testParseInt_WithBoxedInteger_ShouldReturnParsedIntegerValue_IfParseableAsInteger() {
		return new Object[][] {
				//value								default		expected
				{String.valueOf(Integer.MIN_VALUE),		0,		Integer.MIN_VALUE},
				{"-100",								0,		-100},
				{"0",									0,		0},
				{"100",									0,		100},
				{String.valueOf(Integer.MAX_VALUE),		0,		Integer.MAX_VALUE},
				{String.valueOf(Integer.MAX_VALUE),		null,	Integer.MAX_VALUE}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParseInt_ShouldReturnDefaultValue_IfNonParseableValueIsPassed")
	public void testParseInt_ShouldReturnDefaultValue_IfNonParseableValueIsPassed(String value, int defaultValue, int expectedValue){
		assertEquals(expectedValue, Numbers.parseInt(value, defaultValue));
	}
	
	public Object[][] dataFor_testParseInt_ShouldReturnDefaultValue_IfNonParseableValueIsPassed() {
		int DEFAULT_VALUE = -255;
		return new Object[][] {
				//value								default				expected
				{"-100.1",								DEFAULT_VALUE,	DEFAULT_VALUE},
				{"a",									DEFAULT_VALUE,	DEFAULT_VALUE},
				{"1111111111111111111111111",			DEFAULT_VALUE,	DEFAULT_VALUE}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_WithBoxedInteger_testParseInt_ShouldReturnDefaultValue_IfNonParseableValueIsPassed")
	public void testParseInt_WithBoxedInteger_ShouldReturnDefaultValue_IfNonParseableValueIsPassed(String value, Integer defaultValue, Integer expectedValue){
		assertEquals(expectedValue, Numbers.parseInt(value, defaultValue));
	}
	
	public Object[][] dataFor_WithBoxedInteger_testParseInt_ShouldReturnDefaultValue_IfNonParseableValueIsPassed() {
		int DEFAULT_VALUE = -255;
		return new Object[][] {
				//value								default				expected
				{"-100.1",							DEFAULT_VALUE,	DEFAULT_VALUE},
				{"a",								DEFAULT_VALUE,	DEFAULT_VALUE},
				{"1111111111111111111111111",		DEFAULT_VALUE,	DEFAULT_VALUE},
				{"-100.1",							null,			null}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testToByteArrayWithLong")
	public void testToByteArrayWithLong(long value, int noOfBytes, byte[] expectedBytes){
		assertArrayEquals(expectedBytes, Numbers.toByteArray(value, noOfBytes));
	}
	
	public Object[][] dataFor_testToByteArrayWithLong() {
		return new Object[][] {
				{123, 1, new byte[] {123}},
				
				{891, 4, new byte[] {0, 0, 3, 123}},
				
				{456, 8, new byte[] {0, 0, 0, 0, 0, 0, 1, -56}},
				
				{-1, 8, new byte[] {-1, -1, -1, -1, -1, -1, -1, -1}},
				
				{65537, 8, new byte[] {0, 0, 0, 0, 0, 1, 0, 1}},
				
				{Long.MIN_VALUE, 8 , new byte[] {-128, 0, 0, 0, 0, 0, 0, 0}}
		};
	}
	
	@Test
	@Parameters({"1,-1","1,-2"})
	public void testToByteArrayWithLong_ShouldThrowIllegalArgumentException_IfNoOfBytesIsLessThanZero(long value, int noOfBytes){
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("noOfBytes cannot be negative");
		
		Numbers.toByteArray(value, noOfBytes);
	}
	
	@Test
	public void testToByteArrayWithLong_ShouldReturnAnEmptyByteArray_WhenNoOfBytesIsZero(){
		int ZERO = 0;
		assertArrayEquals(new byte[]{}, Numbers.toByteArray((long)1, ZERO));
	}
	
	@Test
	@Parameters(method = "dataFor_testParseLong_ShouldReturnParsedIntegerValue_IfParseableAsInteger")
	public void testParseLong_ShouldReturnParsedIntegerValue_IfParseableAsInteger(String value, long defaultValue, long expectedValue){
		assertEquals(expectedValue, Numbers.parseLong(value, defaultValue));
	}
	
	public Object[][] dataFor_testParseLong_ShouldReturnParsedIntegerValue_IfParseableAsInteger() {
		return new Object[][] {
				//value								default		expected
				{String.valueOf(Long.MIN_VALUE),		(long)0,	Long.MIN_VALUE},
				{"-100",								(long)0,	-100},
				{"0",									(long)0,	0},
				{"100",									(long)0,	100},
				{String.valueOf(Long.MAX_VALUE),		(long)0,	Long.MAX_VALUE}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParseInt_ShouldReturnDefaultValue_IfNonParseableValueIsPassed")
	public void testParseLong_ShouldReturnDefaultValue_IfNonParseableValueIsPassed(String value, long defaultValue, long expectedValue){
		assertEquals(expectedValue, Numbers.parseLong(value, defaultValue));
	}
	
	public Object[][] dataFor_testParseLong_ShouldReturnDefaultValue_IfNonParseableValueIsPassed() {
		int DEFAULT_VALUE = -255;
		return new Object[][] {
				//value								default				expected
				{"-100.1",								DEFAULT_VALUE,	DEFAULT_VALUE},
				{"a",									DEFAULT_VALUE,	DEFAULT_VALUE},
				{"111111111111111111111111111111",		DEFAULT_VALUE,	DEFAULT_VALUE}
		};
	}

	@Test
	@Parameters(method = "dataFor_testParseInt_WithPredicate_ShouldReturnParsedInteger_IfParsedIntegerSatisfiesThePredicate")
	public void testParseInt_WithPredicate_ShouldReturnParsedInteger_IfParsedIntegerSatisfiesThePredicate(String input, 
			int expectedOutput) {
		assertEquals(expectedOutput, Numbers.parseInt(input, Numbers.POSITIVE_INT, ANY_INT));
	}
	
	public Object[] dataFor_testParseInt_WithPredicate_ShouldReturnParsedInteger_IfParsedIntegerSatisfiesThePredicate() {
		return $(
				// input							exptected output
				$("1",								1),
				$("10",								10),
				$(String.valueOf(Integer.MAX_VALUE), Integer.MAX_VALUE)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testParseInt_WithPredicate_ShouldReturnValueOtherwiseParameter_IfParsedIntegerDoesNotSatisfyThePredicate")
	public void testParseInt_WithPredicate_ShouldReturnValueOtherwiseParameter_IfParsedIntegerDoesNotSatisfyThePredicate(String input, 
			int valueOtherwise, int expectedOutput) {
		assertEquals(expectedOutput, Numbers.parseInt(input, Numbers.POSITIVE_INT, valueOtherwise));
	}
	
	public Object[] dataFor_testParseInt_WithPredicate_ShouldReturnValueOtherwiseParameter_IfParsedIntegerDoesNotSatisfyThePredicate() {
		return $(
				// input						value otherwise					exptected output
				$("0",								1,							1),
				$("-1",								1,							1),
				$(String.valueOf(Integer.MIN_VALUE), Integer.MAX_VALUE,			Integer.MAX_VALUE)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testParseInt_WithPredicate_ShouldReturnValueOtherwise_IfStringIsNotAnInteger")
	public void testParseInt_WithPredicate_ShouldReturnValueOtherwise_IfStringIsNotAnInteger(String input,
			int valueOtherwise, int expectedOutput) {
		assertEquals(expectedOutput, Numbers.parseInt("1.1", Predicates.<Integer>alwaysTrue(), valueOtherwise));
	}
	
	public Object[] dataFor_testParseInt_WithPredicate_ShouldReturnValueOtherwise_IfStringIsNotAnInteger() {
		return $(
				$("1.1",		1,				1),
				$("true",		1,				1),
				$("abcd",		1,				1)
		);
	}

	@Test
	@Parameters(method = "dataFor_testParseInt_Boxed_WithPredicate_ShouldReturnParsedInteger_IfParsedIntegerSatisfiesThePredicate")
	public void testParseInt_Boxed_WithPredicate_ShouldReturnParsedInteger_IfParsedIntegerSatisfiesThePredicate(String input, 
			Integer expectedOutput) {
		assertEquals(expectedOutput, Numbers.parseInt(input, Numbers.POSITIVE_INT, ANY_BOXED_INT));
	}
	
	public Object[] dataFor_testParseInt_Boxed_WithPredicate_ShouldReturnParsedInteger_IfParsedIntegerSatisfiesThePredicate() {
		return $(
				// input							exptected output
				$("1",								1),
				$("10",								10),
				$(String.valueOf(Integer.MAX_VALUE), Integer.MAX_VALUE)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testParseInt_Boxed_WithPredicate_ShouldReturnValueOtherwiseParameter_IfParsedIntegerDoesNotSatisfyThePredicate")
	public void testParseInt_Boxed_WithPredicate_ShouldReturnValueOtherwiseParameter_IfParsedIntegerDoesNotSatisfyThePredicate(String input, 
			Integer valueOtherwise, Integer expectedOutput) {
		assertEquals(expectedOutput, Numbers.parseInt(input, Numbers.POSITIVE_INT, valueOtherwise));
	}
	
	public Object[] dataFor_testParseInt_Boxed_WithPredicate_ShouldReturnValueOtherwiseParameter_IfParsedIntegerDoesNotSatisfyThePredicate() {
		return $(
				// input						value otherwise					expected output
				$("0",								1,							1),
				$("-1",								1,							1),
				$(String.valueOf(Integer.MIN_VALUE), Integer.MAX_VALUE,			Integer.MAX_VALUE)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testParseInt_Boxed_WithPredicate_ShouldReturnValueOtherwise_IfStringIsNotAnInteger")
	public void testParseInt_Boxed_WithPredicate_ShouldReturnValueOtherwise_IfStringIsNotAnInteger(String input,
			Integer valueOtherwise, Integer expectedOutput) {
		assertEquals(expectedOutput, Numbers.parseInt("1.1", Predicates.<Integer>alwaysTrue(), valueOtherwise));
	}
	
	public Object[] dataFor_testParseInt_Boxed_WithPredicate_ShouldReturnValueOtherwise_IfStringIsNotAnInteger() {
		return $(
				// input	value otherwise		expected output
				$(null,			1,				1),
				$("1.1",		1,				1),
				$("true",		1,				1),
				$("abcd",		1,				1)
		);
	}
	
	@Test
	public void testPOSITIVE_INT_Predicate_ShouldReturnFalse_IfIntegerIsNull() {
		assertFalse(Numbers.POSITIVE_INT.apply(null));
	}
	
	public Object[] dataProviderFor_testPOSITIVE_LONG_Predicate_ShouldReturnFalse_IfLongIsNullOrNotPositiveNumber() {
		return $(
				// input	result
				$(1l, true),
				$(0l, false),
				$(-1l, false),
				$(Long.MIN_VALUE, false),
				$(Long.MAX_VALUE, true)
		);
	}
	
	@Test
	@Parameters(method="dataProviderFor_testPOSITIVE_LONG_Predicate_ShouldReturnFalse_IfLongIsNullOrNotPositiveNumber")
	public void testPOSITIVE_LONG_Predicate_ShouldReturnFalse_IfLongIsNullOrNotPositiveNumber(Long value, boolean result) {
		assertSame(result,Numbers.POSITIVE_LONG.apply(value));
	}
	
	@Test
	public void testPOSITIVE_LONG_Predicate_ShouldReturnFalse_IfLongIsNull() {
		assertFalse(Numbers.POSITIVE_LONG.apply(null));
	}
}
