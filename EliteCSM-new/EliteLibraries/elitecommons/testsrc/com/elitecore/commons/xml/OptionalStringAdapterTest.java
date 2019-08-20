package com.elitecore.commons.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.elitecore.commons.base.Optional;

@RunWith(JUnitParamsRunner.class)
public class OptionalStringAdapterTest {
	
	private static final String ABSENT = null;
	private OptionalStringAdapter adapter;
	
	@Before
	public void setUp() {
		adapter = new OptionalStringAdapter();
	}
	
	@Test
	public void testMarshal_ShouldReturnNull_IfOptionalIsAbsent() throws Exception {
		assertEquals(ABSENT, adapter.marshal(Optional.<String>absent()));
	}
	
	@Test
	public void testMarshal_ShouldReturnNull_IfOptionalIsNull() throws Exception {
		assertEquals(ABSENT, adapter.marshal(null));
	}
	
	@Test
	@Parameters(value = {"1", "12", "0"})
	public void testMarshal_ShouldReturnTheValue_IfOptionalIsPresentWithValue(String value) throws Exception {
		assertEquals(value, adapter.marshal(Optional.of(value)));
	}
	@Test
	public void testUnmarshal_ShouldReturnOptionalAbsent_IfValueIsNotPresent() throws Exception {
		assertFalse(adapter.unmarshal(ABSENT).isPresent());
	}
	
	@Test
	@Parameters(value = {"1", "12", "123"})
	public void testUnmarshal_ShouldReturnValueWrappedInOptional_WhenValueIsNotNull(String val) throws Exception {
		assertEquals(val, adapter.unmarshal(val).get());
	}

}