package com.elitecore.core.commons.util.string;

import java.io.IOException;

import junitparams.JUnitParamsRunner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.core.util.string.BoundedStringBuilder;

@RunWith(JUnitParamsRunner.class)
public class BoundedStringBuilderTest {
	
	private static final int MAX_LIMIT = 10;
	private BoundedStringBuilder boundedStringBuilder;
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		boundedStringBuilder = new BoundedStringBuilder(MAX_LIMIT);
	}
	
	@Test
	public void test_IsAccomodableOfStr_ShouldReturnTrue_WhenStringOfSizeLessThanMaxLimitIsPassed() {
		boolean acutalValue = boundedStringBuilder.isAccomodable("ABCD");
		Assert.assertEquals(true, acutalValue);
	}
	
	@Test
	public void test_IsAccomodableOfStr_ShouldReturnTrue_WhenStringOfSizeEqualToMaxLimitIsPassed() {
		boolean acutalValue = boundedStringBuilder.isAccomodable("1234567890");
		Assert.assertEquals(true, acutalValue);
	}
	
	@Test
	public void test_IsAccomodableOfStr_ShouldReturnFalse_WhenStringOfSizeGreaterThanMaxLimitIsPassed() {
		boolean acutalValue = boundedStringBuilder.isAccomodable("12345678901");
		Assert.assertEquals(false, acutalValue);
	}
	
	@Test
	public void test_IsAccomodableOfStr_ShouldReturnFalse_WhenStringAreAppendedInPartsAndTotalLengthExceedsMaxLimit() {
		boundedStringBuilder.append("123");
		boundedStringBuilder.append("45");
		boolean acutalValue = boundedStringBuilder.isAccomodable("123456");
		Assert.assertEquals(false, acutalValue);
	}
	
	@Test
	public void test_IsAccomodableOfStr_ShouldThrowNullPointerException_WhenStrPassedIsNull() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("str is null");
		String str = null;
		boundedStringBuilder.isAccomodable(str);
	}
	
	@Test
	public void test_IsAccomodableOfCsq_ShouldReturnTrue_WhenStringOfSizeLessThanMaxLimitIsPassed() {
		CharSequence csq = new String("ABCD");
		boolean acutalValue = boundedStringBuilder.isAccomodable(csq);
		Assert.assertEquals(true, acutalValue);
	}
	
	@Test
	public void test_IsAccomodableOfCsq_ShouldReturnTrue_WhenStringOfSizeEqualToMaxLimitIsPassed() {
		CharSequence csq = new String("1234567890");
		boolean acutalValue = boundedStringBuilder.isAccomodable(csq);
		Assert.assertEquals(true, acutalValue);
	}
	
	@Test
	public void test_IsAccomodableOfCsq_ShouldReturnFalse_WhenStringOfSizeGreaterThanMaxLimitIsPassed() {
		CharSequence csq = new String("12345678901");
		boolean acutalValue = boundedStringBuilder.isAccomodable(csq);
		Assert.assertEquals(false, acutalValue);
	}
	
	@Test
	public void test_IsAccomodableOfCsq_ShouldReturnFalse_WhenStringAreAppendedInPartsAndTotalLengthExceedsMaxLimit() throws IOException {
		CharSequence csq = new String("123");
		boundedStringBuilder.append(csq);
		boundedStringBuilder.append("45");
		csq = new String("123456");
		boolean acutalValue = boundedStringBuilder.isAccomodable(csq);
		Assert.assertEquals(false, acutalValue);
	}
	
	@Test
	public void test_IsAccomodableOfCsq_ShouldThrowNullPointerException_WhenCsqPassedIsNull() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("csq is null");
		CharSequence csq = null;
		boundedStringBuilder.isAccomodable(csq);
	}
}
