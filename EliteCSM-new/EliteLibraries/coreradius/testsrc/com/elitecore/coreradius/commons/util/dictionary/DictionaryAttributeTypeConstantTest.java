package com.elitecore.coreradius.commons.util.dictionary;

import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.BYTE;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.CISCO_COMMAND_CODE;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.DATE;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.EUI;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.EUI64;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.EVLANID;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.GROUPED;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.INTEGER;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.IPADDR;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.IPV6PREFIX;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.LONG;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.OCTETS;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.PREPAIDTLV;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.SHORT;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.STRING;
import static com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant.USER_LOCATION_INFO;
import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertSame;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class DictionaryAttributeTypeConstantTest {

	public Object[][] dataFor_testDictionaryAttributeTypeConstants(){
		return new Object[][]{
				{"string", STRING},
				{"octets", OCTETS},
				{"long", LONG},
				{"integer", INTEGER},
				{"short",SHORT},
				{"byte", BYTE},
				{"ipaddr", IPADDR},
				{"ipv6prefix", IPV6PREFIX},
				{"date", DATE},
				{"prepaidTLV", PREPAIDTLV},
				{"EUI64", EUI64},
				{"EUI", EUI},
				{"grouped", GROUPED},
				{"evlanid", EVLANID},
				{"UserLocationInfo", USER_LOCATION_INFO},
				{"CiscoCommandCode", CISCO_COMMAND_CODE}
		};
	}
	
	public Object[] dataFor_testFrom_ShouldReturnType_IgnoringCase() {
		return $(
				$("STRING", STRING),
				$("String", STRING),
				$("string", STRING),
				$("INTEGER", INTEGER),
				$("Integer", INTEGER),
				$("InTeger", INTEGER),
				$("integer", INTEGER)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testDictionaryAttributeTypeConstants")
	public void testFrom_ShouldReturnDictionaryAttributeTypeConstantBasedOnTypeString(String typeString, DictionaryAttributeTypeConstant expectedAttributeType){
		assertSame(expectedAttributeType, DictionaryAttributeTypeConstant.from(typeString));
	}
	
	@Test
	@Parameters(method = "dataFor_testFrom_ShouldReturnType_IgnoringCase")
	public void testFrom_ShouldReturnType_IgnoringCase(String type, DictionaryAttributeTypeConstant expectedType) {
		assertSame(expectedType, DictionaryAttributeTypeConstant.from(type));
	}
	
	@Test
	public void testFrom_ShouldReturnUnknownType_IfTypeStringPassedIsNull() {
		assertSame(DictionaryAttributeTypeConstant.UNKNOWN, DictionaryAttributeTypeConstant.from(null));
	}
	
	@Test
	@Parameters(value = {
			"unknown",
			"int",
			"garbage"
	})
	public void testFrom_ShouldReturnUnknonwnType_IfTypeIsUnknown(String typeString) {
		assertSame(DictionaryAttributeTypeConstant.UNKNOWN, DictionaryAttributeTypeConstant.from(typeString));
	}
	
	@Test
	@Parameters(value = {
			" integer",
			"  integer",
			"integer   ",
			"  integer  "
	})
	public void testFrom_ShouldTrimTypeString_BeforeLocatingTheType(String typeStringContainingSpaces) {
		assertSame(INTEGER, DictionaryAttributeTypeConstant.from(typeStringContainingSpaces));
	}
}
