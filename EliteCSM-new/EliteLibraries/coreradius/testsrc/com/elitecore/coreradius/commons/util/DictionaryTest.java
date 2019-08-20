package com.elitecore.coreradius.commons.util;

import static com.elitecore.coreradius.commons.util.RadiusMatchers.TypeMatcher.ofType;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.dictionary.*;
import junitparams.JUnitParamsRunner;

import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.ByteAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.TextAttribute;
import com.elitecore.coreradius.commons.attributes.UnknownAttribute;
import com.elitecore.coreradius.commons.util.constants.LengthFormat;
import com.elitecore.coreradius.commons.util.constants.PaddingType;

@RunWith(JUnitParamsRunner.class)
public class DictionaryTest{

	private VendorInformation vendorInformation;
	private DictionaryModel dictionaryModel;

	@Before
	public void setUp() throws Exception {
		setUpExpectedDictionaryModel();
		Dictionary.getInstance().load(dictionaryModel, null);
	}

	private void setUpExpectedDictionaryModel() throws DictionaryParseException {
		vendorInformation = new VendorInformation();
		vendorInformation.setVendorName("test");
		vendorInformation.setVendorId(1);
		vendorInformation.setAvPairSeparator("@@");
		vendorInformation.setFormat("1,1");

		AttributeModel attributeModel = new AttributeModel();
		attributeModel.setId(1);
		attributeModel.setName("Attrib1");
		attributeModel.setType("string");
		attributeModel.setTagged(true);
		attributeModel.setAvPair(false);
		attributeModel.setEncryptStandard(1);
		attributeModel.setIgnoreCase(true);
		attributeModel.setLengthFormat(LengthFormat.TLV);
		attributeModel.setPaddingType(PaddingType.NONE);
		vendorInformation.getAttributeModels().add(attributeModel);

		attributeModel = new AttributeModel();
		attributeModel.setId(2);
		attributeModel.setName("Attrib2");
		attributeModel.setType("integer");
		attributeModel.setTagged(false);
		attributeModel.setAvPair(true);
		attributeModel.setEncryptStandard(2);
		attributeModel.setEncryptStandard(2);
		attributeModel.setIgnoreCase(false);
		attributeModel.setLengthFormat(LengthFormat.VALUE);
		attributeModel.setPaddingType(PaddingType.DHCP);
		vendorInformation.getAttributeModels().add(attributeModel);

		attributeModel = new AttributeModel();
		attributeModel.setId(5);
		attributeModel.setName("Attrib5");
		attributeModel.setType("integer");
		attributeModel.setTagged(true);
		vendorInformation.getAttributeModels().add(attributeModel);


		attributeModel = new AttributeModel();
		attributeModel.setId(3);
		attributeModel.setName("Attrib3");
		attributeModel.setType("integer");
		attributeModel.getSupportedValues().add(new AttributeSupportedValueModel(1, "sv1"));
		attributeModel.getSupportedValues().add(new AttributeSupportedValueModel(2, "sv2"));
		vendorInformation.getAttributeModels().add(attributeModel);

		attributeModel = new AttributeModel();
		attributeModel.setId(4);
		attributeModel.setName("Attrib4");
		attributeModel.setType("grouped");

		AttributeModel subAttributeModel = new AttributeModel();
		subAttributeModel.setId(1);
		subAttributeModel.setName("Param1");
		subAttributeModel.setType("string");
		attributeModel.getSubAttributes().add(subAttributeModel);

		subAttributeModel = new AttributeModel();
		subAttributeModel.setId(2);
		subAttributeModel.setName("Param2");
		subAttributeModel.setType("octets");
		attributeModel.getSubAttributes().add(subAttributeModel);

		subAttributeModel = new AttributeModel();
		subAttributeModel.setId(3);
		subAttributeModel.setName("Param3");
		subAttributeModel.setType("byte");
		attributeModel.getSubAttributes().add(subAttributeModel);

		vendorInformation.getAttributeModels().add(attributeModel);

		vendorInformation.postRead();

		Map<Long, VendorInformation> idToVendorInformation = new HashMap<>();
		idToVendorInformation.put(vendorInformation.getVendorId(), vendorInformation);
		dictionaryModel = new DictionaryModel(idToVendorInformation);
	}

	@Test
	public void testGetVendorName_ShouldReturnVendorNameBasedOnId_AfterDictionaryIsLoaded() throws Exception{
		System.out.println(vendorInformation);
		assertEquals(vendorInformation.getVendorName(), Dictionary.getInstance().getVendorName(vendorInformation.getVendorId()));
	}


	@Test
	public void testGetVendorID_ShouldReturnVendorIdBasedOnName_AfterDictionaryIsLoaded() throws Exception{
		assertEquals(String.valueOf(vendorInformation.getVendorId()), Dictionary.getInstance().getVendorID(vendorInformation.getVendorName()));
	}

	@Test
	public void testGetVendorAVPairSeparator_ShouldReturnAvpairSeparatorBasedOnVendorId_AfterDictionaryIsLoaded() throws Exception{
		String actualAVPairSeperator = Dictionary.getInstance().getVendorAVPairSeparator(vendorInformation.getVendorId());
		assertEquals(vendorInformation.getAvPairSeparator(), actualAVPairSeperator);
	}


	@Test
	public void testGetVendorAVPairSeparator_ShouldReturnAvpairSeparatorBasedOnVendorName_AfterDictionaryIsLoaded() throws Exception{
		String actualAVPairSeperator = Dictionary.getInstance().getVendorAVPairSeparator(vendorInformation.getVendorName());
		assertEquals(vendorInformation.getAvPairSeparator(), actualAVPairSeperator);
	}


	@Test
	public void testGetAttributeNameBasedOnAttributeIdString_ShouldReturnAttributeName_AfterDictionaryIsLoaded() throws Exception{
		String expectedAttributeName = vendorInformation.getAttributeModelForId(1).getName();
		String actualAttributeName = Dictionary.getInstance().getAttributeName("1:1");

		assertEquals(expectedAttributeName, actualAttributeName);


		expectedAttributeName = vendorInformation.getAttributeModelForId(4).getSubAttributeModel(1).getName();
		actualAttributeName = Dictionary.getInstance().getAttributeName("1:4:1");

		assertEquals(expectedAttributeName, actualAttributeName);

	}


	@Test
	public void testGetAttributeNameBasedOnVendorAttributeId_ShouldReturnAttributeNameForThatVendor_AfterDictionaryIsLoaded() throws Exception{
		String expectedAttributeName = vendorInformation.getAttributeModelForId(4).getSubAttributeModel(1).getName();
		String actualAttributeName = Dictionary.getInstance().getAttributeName(1,4,1);

		assertEquals(expectedAttributeName, actualAttributeName);
	}


	@Test
	public void testGetAttributeId_ShouldReturnAttributeIdForThatAttribute_AfterDictionaryIsLoaded() throws Exception{
		AttributeId attributeId = Dictionary.getInstance().getAttributeId("1:4:1");

		AttributeModel expectedAttributeModel = vendorInformation.getAttributeModelForId(4).getSubAttributeModel(1);

		assertNotNull(attributeId);
		assertEquals(expectedAttributeModel.getId(), attributeId.getAttrId());
	}

	@Test
	public void testGetKeyFromValue_ShouldReturnKey_AfterDictionaryIsLoaded(){
		long expectedKey = vendorInformation.getAttributeModelForId(3).getSupportedValueModel(1).getId();
		long actualKey = Dictionary.getInstance().getKeyFromValue(1, 3, "sv1");

		assertEquals(expectedKey, actualKey);
	}

	@Test
	public void testGetValueFromKey_ShouldReturnValue_AfterDictionaryIsLoaded(){
		String expectedValue = vendorInformation.getAttributeModelForId(3).getSupportedValueModel(1).getName();
		String actualValue = Dictionary.getInstance().getValueFromKey(1, 3, 1);

		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testGetKnownAttribute_ShouldReturnNull_IfAttributeIsUnknown(){
		assertNull(Dictionary.getInstance().getKnownAttribute(1, 6));
	}

	@Test
	public void testGetKnownAttribute_ShouldReturnAttribute_AfterDictionaryIsLoaded(){
		assertNotNull(Dictionary.getInstance().getKnownAttribute(1, 1));
		assertNotNull(Dictionary.getInstance().getKnownAttribute(1, 4, 1));
	}

	@Test
	public void testGetKnownAttribute_ShouldReturnAttributeOfTypeBasedOnAttributeType_AfterDictionaryIsLoaded(){
		assertEquals(TextAttribute.class, Dictionary.getInstance().getKnownAttribute(1, 1).getClass());
		assertEquals(ByteAttribute.class, Dictionary.getInstance().getKnownAttribute(1, 4, 3).getClass());
	}


	@Test
	public void testGetAttribute_ShouldReturnUnknownAttribute_IfAttributeIsUnknown(){
		IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(1, 20);

		assertNotNull(attribute);
		assertEquals(UnknownAttribute.class, attribute.getClass());
	}

	@Test
	public void testGetAttribute_ShouldReturnAttributeOfTypeBasedOnAttributeType_AfterDictionaryIsLoadedAndAttributeIsKnown(){
		assertEquals(TextAttribute.class, Dictionary.getInstance().getAttribute(1, 1).getClass());
		assertEquals(ByteAttribute.class, Dictionary.getInstance().getAttribute(1, 4, 3).getClass());
	}

	@Test
	public void licenseValidation(){
		assertTrue(Dictionary.getInstance().doLicenseValidation(dictionaryModel, vendorId -> true));
	}

	public Object[][] data_provider_for_readDictionaryCreateAttributeAccordinToItsType() {


		Object[][] data = new Object[DictionaryAttributeTypeConstant.values().length][];

		int id = 0;
		for(DictionaryAttributeTypeConstant type : DictionaryAttributeTypeConstant.values()) {
			int index = id;
			Set<AttributeModel> attrbuteModels = new HashSet<>();
			AttributeModel attributeModel = new AttributeModel(id++, UUID.randomUUID().toString(), type.name(), Collections.emptySet(), Collections.emptySet());

			vendorInformation = new VendorInformation(RadiusConstants.STANDARD_VENDOR_ID, "test", attrbuteModels);
			attrbuteModels.add(attributeModel);

			Map<Long, VendorInformation> idToVedorInformation = Stream.of(vendorInformation).collect(toMap(VendorInformation::getVendorId, Function.identity()));
			DictionaryModel dictionaryModel = new DictionaryModel(idToVedorInformation);
			data[index] = new Object[]{dictionaryModel, attributeModel};
		}

		return data;
	}

	@Test
	@Parameters(method="data_provider_for_readDictionaryCreateAttributeAccordinToItsType")
	public void loadDictionaryCreateAttributeAccordinToStandardVendorId(DictionaryModel dictionaryModel, AttributeModel attributeModel) throws Exception{
		Dictionary.getInstance().load(dictionaryModel, null);
		IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(RadiusConstants.STANDARD_VENDOR_ID, attributeModel.getId());
		assertNotNull(attribute);
		assertEquals(attributeModel.getId(), attribute.getID());
		assertThat(attribute, ofType(DictionaryAttributeTypeConstant.from(attributeModel.getType()), RadiusConstants.STANDARD_VENDOR_ID));

	}

	@Test
	@Parameters(method="data_provider_for_readDictionaryCreateAttributeAccordinToItsType")
	public void loadDictionaryCreateAttributeAccordinToWimaxVendorId(DictionaryModel dictionaryModel, AttributeModel attributeModel) throws Exception{
		dictionaryModel.getIdToVendorInformation().get(RadiusConstants.STANDARD_VENDOR_ID).setVendorId(RadiusConstants.WIMAX_VENDOR_ID);
		Dictionary.getInstance().load(dictionaryModel, null);
		IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(RadiusConstants.WIMAX_VENDOR_ID, attributeModel.getId());
		assertNotNull(attribute);
		assertEquals(attributeModel.getId(), attribute.getID());
		assertThat(attribute, ofType(DictionaryAttributeTypeConstant.from(attributeModel.getType()), RadiusConstants.WIMAX_VENDOR_ID));

	}

	@Test
	@Parameters(method="data_provider_for_readDictionaryCreateAttributeAccordinToItsType")
	public void loadDictionaryCreateAttributeAccordinToOtherVendorId(DictionaryModel dictionaryModel, AttributeModel attributeModel) throws Exception{
		dictionaryModel.getIdToVendorInformation().get(RadiusConstants.STANDARD_VENDOR_ID).setVendorId(RadiusAttributeConstants.VENDOR_SPECIFIC);
		Dictionary.getInstance().load(dictionaryModel, null);
		IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC, attributeModel.getId());
		assertNotNull(attribute);
		assertEquals(attributeModel.getId(), attribute.getID());
		assertThat(attribute, ofType(DictionaryAttributeTypeConstant.from(attributeModel.getType()), RadiusAttributeConstants.VENDOR_SPECIFIC));

	}
}
