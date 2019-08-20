package com.elitecore.commons.base;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class OptionalTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testIsPresent_ShouldReturnFalseForAbsentOptional() {
		assertFalse(Optional.absent().isPresent());
	}
	
	@Test
	@Parameters(method = "dataFor_testIsPresent_ShouldReturnWhetherReferenceIsNullOrNot")
	public void testIsPresent_ShouldReturnWhetherReferenceIsNullOrNot(Object obj, boolean expectedValue) {
		Optional<Object> optional = Optional.of(obj);
		assertEquals(expectedValue, optional.isPresent());
	}
	
	public Object[] dataFor_testIsPresent_ShouldReturnWhetherReferenceIsNullOrNot() {
		return $(
				$(null,			false),
				$("",			true),
				$(new Object(), true)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testGet_ShouldReturnTheValue_IfIsPresentReturnsTrue")
	public void testGet_ShouldReturnTheValue_IfIsPresentReturnsTrue(Object obj) {
		Optional<Object> optional = Optional.of(obj);
		assertEquals(obj, optional.get());
	}
	
	public Object[] dataFor_testGet_ShouldReturnTheValue_IfIsPresentReturnsTrue() {
		return $(
				$(new Object()),
				$(""),
				$("test"),
				$(1)
		);
	}
	
	@Test
	public void testGet_OnAnAbsentOptional_ShouldThrowIllegalStateException() {
		exception.expect(IllegalStateException.class);
		Optional.absent().get();
	}
	
	@Test
	public void testFromNullable_ShouldReturnAnAbsentValue_IfReferenceIsNull() {
		Optional<Object> optional = Optional.of(null);
		assertEquals(false, optional.isPresent());
		exception.expect(IllegalStateException.class);
		optional.get();
	}
	
	@Test
	public void testOrElse_ShouldThrowNPE_IfTheGivenReferenceIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("orElse reference should not be null. Use orNull instead of orElse");
		
		Optional.of(null)
				.orElse(null);
	}
	
	@Test
	public void testOrNull_ShouldReturnNull_IfOptionalIsAbsent() {
		Object nullReference = null;
		Object result = Optional
			.of(nullReference)
			.orNull();
		
		assertNull(result);
	}
	
	@Test
	public void testOrNull_ShouldReturnThePresentInstanceAndNotNull_IfOptionalIsPresent() {
		Object someNonNullObject = new Object();
		Object result = Optional
			.of(someNonNullObject)
			.orNull();
		
		assertSame(someNonNullObject, result);
	}
	
	@Test
	public void testOrElse_ShouldReturnItsNonNullReference_IfOptionalIsAbsent() {
		Object defaultObj = new Object();
		
		Object result = Optional
			.of(null)
			.orElse(defaultObj);
		
		assertSame(defaultObj, result);
	}
	
	@Test
	public void testOrElse_ShouldReturnThePresentInstance_IfOptionalIsPresent() {
		Object someNonNullObject = new Object();
		Object defaultObj = new Object();
		
		Object result = Optional
			.of(someNonNullObject)
			.orElse(defaultObj);
		
		assertSame(someNonNullObject, result);
	}
	
	
}
