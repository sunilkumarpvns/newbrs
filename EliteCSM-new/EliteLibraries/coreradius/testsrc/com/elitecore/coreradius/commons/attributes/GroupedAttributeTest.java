package com.elitecore.coreradius.commons.attributes;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.coreradius.commons.util.RadiusDictionaryTestHarness;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import org.unitils.reflectionassert.ReflectionAssert;

import static org.junit.Assert.*;

/**
 * 
 * @author narendra.pathai
 *
 */
public class GroupedAttributeTest {
	
	private static final String JSON_FORMAT = "{'%d':'%s','%d':'%s'}";
	private static final String ANY_OTHER_VALUE = "value2";
	private static final String ANY_VALUE = "value";

	private GroupedAttribute attribute;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RadiusDictionaryTestHarness.getInstance();
	}
	
	@Before
	public void setUp() {
		attribute = newGroupedAttribute();
	}
	
	private GroupedAttribute newGroupedAttribute() {
		return (GroupedAttribute) Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);
	}
	
	@Test
	public void testSetStringValue_ShouldAcceptJSONFormat_AndAddSameNumberOfSubAttributesAsProvidedInString() {
		String jsonValue = String.format(JSON_FORMAT,
				RadiusAttributeConstants.ELITE_PARAM1,
				ANY_VALUE,
				RadiusAttributeConstants.ELITE_PARAM2,
				ANY_OTHER_VALUE);
		
		attribute.setStringValue(jsonValue);
		
		assertEquals(2, attribute.getAttributes().size());
	}

	@Test
	public void testSetStringValue_ShouldAcceptJSONFormat_AndAddSameAllSubAttributesWithProvidedAttributeIds() {
		String jsonValue = String.format(JSON_FORMAT,
				RadiusAttributeConstants.ELITE_PARAM1,
				ANY_VALUE,
				RadiusAttributeConstants.ELITE_PARAM2,
				ANY_OTHER_VALUE);
		
		attribute.setStringValue(jsonValue);

		assertNotNull(attribute.getAttribute(RadiusAttributeConstants.ELITE_PARAM1));
		assertNotNull(attribute.getAttribute(RadiusAttributeConstants.ELITE_PARAM2));
	}
	
	@Test
	public void testSetStringValue_ShouldAcceptJSONFormat_AndAddSameAllSubAttributesWithProvidedStringValues() {
		String jsonValue = String.format(JSON_FORMAT,
				RadiusAttributeConstants.ELITE_PARAM1,
				ANY_VALUE,
				RadiusAttributeConstants.ELITE_PARAM2,
				ANY_OTHER_VALUE);
		
		attribute.setStringValue(jsonValue);

		assertEquals(ANY_VALUE, attribute.getAttribute(RadiusAttributeConstants.ELITE_PARAM1).getStringValue());
		assertEquals(ANY_OTHER_VALUE, attribute.getAttribute(RadiusAttributeConstants.ELITE_PARAM2).getStringValue());
	}

	@Test
	public void test_addSubAttributeForString_works() {
		attribute.addsubAttribute("21067:117:2", "1");
		List<IRadiusAttribute> attributeList = attribute.getSubAttributes(2);
		List<IRadiusAttribute> expectedAttrList= new ArrayList<>();

		assertNotNull(attributeList);
		assertFalse(attributeList.isEmpty());
		assertEquals(1, attributeList.size());
		assertEquals("1", attributeList.get(0).getStringValue());
	}

	@Test
	public void test_addSubAttributeForInt_works() {
		attribute.addsubAttribute("21067:211:1", 1);
		List<IRadiusAttribute> attributeList = attribute.getSubAttributes(1);
		List<IRadiusAttribute> expectedAttrList= new ArrayList<>();

		assertNotNull(attributeList);
		assertFalse(attributeList.isEmpty());
		assertEquals(1, attributeList.size());
		assertEquals(1, attributeList.get(0).getIntValue());
	}

	@Test
	public void test_addSubAttributeForLong_works() {
		attribute.addsubAttribute("21067:211:1", 1l);
		List<IRadiusAttribute> attributeList = attribute.getSubAttributes(1);
		List<IRadiusAttribute> expectedAttrList= new ArrayList<>();

		assertNotNull(attributeList);
		assertFalse(attributeList.isEmpty());
		assertEquals(1, attributeList.size());
		assertEquals("Request", attributeList.get(0).getStringValue());
	}
}
