package com.elitecore.core.commons.config.core;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.core.commons.config.core.CaseInsensitiveEnumAdapterTest.TestEnum.TestXMLAdapter;

@RunWith(JUnitParamsRunner.class)
public class CaseInsensitiveEnumAdapterTest {

	@XmlEnum
	enum TestEnum {
		@XmlEnumValue(value = "TEST_VALUE1")
		TEST_VALUE1,
		@XmlEnumValue(value = "TEST_VALUE1")
		TEST_VALUE2,
		@XmlEnumValue(value = "DEFAULT")
		DEFAULT;

		static class TestXMLAdapter extends CaseInsensitiveEnumAdapter<TestEnum> {
			public TestXMLAdapter() {
				super(TestEnum.class, DEFAULT);
			}
		}
	}

	private TestXMLAdapter xmlAdapter;

	@Before
	public void setUp() {
		this.xmlAdapter = new TestEnum.TestXMLAdapter();
	}

	@Test
	@Parameters(method = "dataFor_testUnmarshal_ShouldReturnValueCaseInsensitively")
	public void testUnmarshal_ShouldReturnValueCaseInsensitively(String value, TestEnum expectedResult) throws Exception {
		assertEquals(expectedResult, xmlAdapter.unmarshal(value));
	}

	public Object[] dataFor_testUnmarshal_ShouldReturnValueCaseInsensitively() {
		return $(
				$("TEST_VALUE1",	TestEnum.TEST_VALUE1),
				$("TEST_value1",	TestEnum.TEST_VALUE1),
				$("test_value1",	TestEnum.TEST_VALUE1),
				$("tESt_vAlue1",	TestEnum.TEST_VALUE1),
				$("TEST_VALUE2",	TestEnum.TEST_VALUE2),
				$("TEST_value2",	TestEnum.TEST_VALUE2),
				$("test_value2",	TestEnum.TEST_VALUE2),
				$("tESt_vAlue2",	TestEnum.TEST_VALUE2)
		);
	}

	@Test
	@Parameters(method = "dataFor_testUnmarshal_ShouldReturnDefaultValue_WhenValueIsInvalid")
	public void testUnmarshal_ShouldReturnDefaultValue_WhenValueIsInvalid(String value, TestEnum expectedResult) throws Exception {
		assertEquals(expectedResult, xmlAdapter.unmarshal(value));
	}

	public Object[] dataFor_testUnmarshal_ShouldReturnDefaultValue_WhenValueIsInvalid() {
		return $(
				$("SomeInvalidValue",	TestEnum.DEFAULT),
				$("TEST_V1",			TestEnum.DEFAULT),
				$(null,					TestEnum.DEFAULT),
				$("TEST_VALUE",			TestEnum.DEFAULT)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testUnmarshal_TrimValueAndLocateTheEnumConstant")
	public void testUnmarshal_TrimValueAndLocateTheEnumConstant(String value, TestEnum expectedResult) throws Exception {
		assertEquals(expectedResult, xmlAdapter.unmarshal(value));
	}
	
	public Object[] dataFor_testUnmarshal_TrimValueAndLocateTheEnumConstant() {
		return $(
				$("  TEST_VALUE1",		TestEnum.TEST_VALUE1),
				$("  TEST_VALUE1  ",	TestEnum.TEST_VALUE1)
		);
	}
	
	@Test
	public void testMarshal_ShouldReturnNameOfEnumConstant() throws Exception {
		for(TestEnum testEnum : TestEnum.values()) {
			assertEquals(testEnum.name(), xmlAdapter.marshal(testEnum));
		}
	}
}
