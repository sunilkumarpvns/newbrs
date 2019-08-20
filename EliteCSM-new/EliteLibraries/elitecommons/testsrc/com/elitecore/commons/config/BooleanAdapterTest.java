package com.elitecore.commons.config;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class BooleanAdapterTest {
	
	private BooleanAdapter adapter;

	@Before
	public void setUp(){
		adapter = new BooleanAdapter();
	}
	
	@Test
	@Parameters(method = "dataFor_testUnmarshal")
	public void testUnmarshal(String value, boolean expectedResult) throws Exception{
		assertEquals(expectedResult, adapter.unmarshal(value));
	}

	public Object[] dataFor_testUnmarshal(){
		return $(
				$(null, false),
				$("True", true),
				$("TRuE", true),
				$("true", true),
				$("false", false),
				$("False", false),
				$("", false),
				$("yes", true),
				$("YEs", true),
				$("No", false),
				$("no", false)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testMarshal")
	public void testMarshal(Boolean value, String expectedResult) throws Exception{
		assertEquals(expectedResult, adapter.marshal(value));
	}
	
	public Object[] dataFor_testMarshal(){
		return $(
				$(true, "true"),
				$(false, "false"),
				$(null, "false")
		);
	}
}