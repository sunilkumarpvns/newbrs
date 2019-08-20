package com.elitecore.commons.base;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import junitparams.JUnitParamsRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class MapsTest {
	
	@Test
	public void testIsNullOrEmpty_ShouldReturnTrue_IfMapIsNull() {
		assertTrue(Maps.isNullOrEmpty(null));
	}
	
	@Test
	public void testIsNullOrEmpty_ShouldReturnTrue_IfMapIsEmpty() {
		assertTrue(Maps.isNullOrEmpty(new HashMap<Object, Object>()));
	}
	
	@Test
	public void testIsNullOrEmpty_ShouldReturnFalse_IfMapContainsSomeEntries() {
		Map<Object, Object> nonEmptyMap = new HashMap<Object, Object>();
		nonEmptyMap.put(new Object(), new Object());
		
		assertFalse(Maps.isNullOrEmpty(nonEmptyMap));
	}
	
	@Test
	public void testNewHashMap_ShouldReturnANonNullHashMap() {
		assertNotNull(Maps.newHashMap());
	}
	
	@Test
	public void testNewHashMap_ShouldReturnAnEmptyHashMap() {
		assertTrue(Maps.newHashMap().isEmpty());
	}
	
	@Test
	public void testNewHashMap_ShouldReturnNewInstanceOnEachInvocation() {
		assertNotSame(Maps.newHashMap(), Maps.newHashMap());
	}
	
	@Test
	public void testNewLinkedHashMap_ShouldReturnANonNullHashMap() {
		assertNotNull(Maps.newLinkedHashMap());
	}
	
	@Test
	public void testNewLinkedHashMap_ShouldReturnAnEmptyHashMap() {
		assertTrue(Maps.newLinkedHashMap().isEmpty());
	}
	
	@Test
	public void testNewLinkedHashMap_ShouldReturnNewInstanceOnEachInvocation() {
		assertNotSame(Maps.newLinkedHashMap(), Maps.newLinkedHashMap());
	}
}
