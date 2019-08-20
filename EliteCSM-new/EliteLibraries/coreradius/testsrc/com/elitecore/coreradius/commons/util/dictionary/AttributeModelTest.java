package com.elitecore.coreradius.commons.util.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.coreradius.commons.util.constants.LengthFormat;
import com.elitecore.coreradius.commons.util.constants.PaddingType;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class AttributeModelTest {

	private AttributeModel attributeModel;

	@Before
	public void setUp(){
		attributeModel = new AttributeModel();
	}
	
	@Test
	public void testAttributeModel_MustHaveADefaultConstructorRequiredForJAXB() throws SecurityException, NoSuchMethodException{
		AttributeModel.class.getDeclaredConstructor(new Class<?>[]{});
	}
	
	@Test
	public void testAttributeModel_ConstructorMustBePublic() throws SecurityException, NoSuchMethodException{
		Constructor<?> constructor = AttributeModel.class.getDeclaredConstructor(new Class<?>[]{});
		assertTrue(Modifier.isPublic(constructor.getModifiers()));
	}
	
	@Test
	public void testDefaultConstructor_SupportedValuesShouldNotBeNull(){
		assertNotNull(attributeModel.getSupportedValues());
	}
	
	@Test
	public void testDefaultConstructor_SubAttributesShouldNotBeNull(){
		assertNotNull(attributeModel.getSubAttributes());
	}
	
	@Test
	public void testGetName_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method getNameMethod = AttributeModel.class.getDeclaredMethod("getName", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + getNameMethod.getName() +" method.",getNameMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testGetId_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method getIdMethod = AttributeModel.class.getDeclaredMethod("getId", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + getIdMethod.getName() +" method.",getIdMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testGetType_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method getTypeMethod = AttributeModel.class.getDeclaredMethod("getType", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + getTypeMethod.getName() +" method.",getTypeMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testGetSupportedValues_MustHaveXMLElementWrapperAnnotation() throws SecurityException, NoSuchMethodException{
		Method getSupportedValuesMethod = AttributeModel.class.getDeclaredMethod("getSupportedValues", new Class<?>[]{});
		assertNotNull("@XMLElementWrapper annotation is missing from: " + getSupportedValuesMethod.getName() +" method.",getSupportedValuesMethod.getAnnotation(XmlElementWrapper.class));
	}
	
	@Test
	public void testGetSupportedValues_MustHaveXMLElementAnnotation() throws SecurityException, NoSuchMethodException{
		Method getSupportedValuesMethod = AttributeModel.class.getDeclaredMethod("getSupportedValues", new Class<?>[]{});
		assertNotNull("@XMLElement annotation is missing from: " + getSupportedValuesMethod.getName() +" method.",getSupportedValuesMethod.getAnnotation(XmlElement.class));
	}
	
	@Test
	public void testGetSubAttributes_MustHaveXMLElementAnnotation() throws SecurityException, NoSuchMethodException{
		Method getSubAttributesMethod = AttributeModel.class.getDeclaredMethod("getSubAttributes", new Class<?>[]{});
		assertNotNull("@XMLElement annotation is missing from: " + getSubAttributesMethod.getName() +" method.",getSubAttributesMethod.getAnnotation(XmlElement.class));
	}
	
	@Test
	public void testIsTagged_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method isTaggedMethod = AttributeModel.class.getDeclaredMethod("isTagged", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + isTaggedMethod.getName() +" method.",isTaggedMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testIsIgnoreCase_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method isIgnoreCaseMethod = AttributeModel.class.getDeclaredMethod("isIgnoreCase", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + isIgnoreCaseMethod.getName() +" method.",isIgnoreCaseMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testIsAvPair_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method isAvPairMethod = AttributeModel.class.getDeclaredMethod("isAvPair", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + isAvPairMethod.getName() +" method.",isAvPairMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testGetEncryptStandard_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method getEncryptStandardMethod = AttributeModel.class.getDeclaredMethod("getEncryptStandard", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + getEncryptStandardMethod.getName() +" method.",getEncryptStandardMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testGetPaddingType_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method getPaddingTypeMethod = AttributeModel.class.getDeclaredMethod("getPaddingType", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + getPaddingTypeMethod.getName() +" method.",getPaddingTypeMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testGetLengthFormat_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method getLengthFormatMethod = AttributeModel.class.getDeclaredMethod("getLengthFormat", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + getLengthFormatMethod.getName() +" method.",getLengthFormatMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testGetParent_MustBeXMLTransient() throws SecurityException, NoSuchMethodException{
		Method getParentMethod = AttributeModel.class.getDeclaredMethod("getParent", new Class<?>[]{});
		assertNotNull("@XMLTransient annotation is missing from: " + getParentMethod.getName() +" method. It should always be transient.",getParentMethod.getAnnotation(XmlTransient.class));
	}
	
	@Test
	public void testNewIdFormatter_ShouldReturnNonNullInstanceOfFormatter(){
		assertNotNull(attributeModel.newIdFormatter());
	}
	
	@Test
	public void testNewIdFormatter_ShouldReturnNewInstanceOfFormatter_OnEachInvocation(){
		assertNotSame("A newly constructed instance should be returned on each invocation",
				attributeModel.newIdFormatter(), attributeModel.newIdFormatter());
	}
	
	/*--------- Assertion for the radius dictionary format --------------*/
	@Test
	public void testGetName_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getNameMethod = AttributeModel.class.getDeclaredMethod("getName", new Class<?>[]{});
		XmlAttribute xmlAttribute = getNameMethod.getAnnotation(XmlAttribute.class);
		assertEquals("name", xmlAttribute.name());
	}
	
	@Test
	public void testGetId_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getIdMethod = AttributeModel.class.getDeclaredMethod("getId", new Class<?>[]{});
		XmlAttribute xmlAttribute = getIdMethod.getAnnotation(XmlAttribute.class);
		assertEquals("id", xmlAttribute.name());
	}
	
	@Test
	public void testGetType_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getTypeMethod = AttributeModel.class.getDeclaredMethod("getType", new Class<?>[]{});
		XmlAttribute xmlAttribute = getTypeMethod.getAnnotation(XmlAttribute.class);
		assertEquals("type", xmlAttribute.name());
	}
	
	@Test
	public void testGetSupportedValues_MustHaveXMLElementWrapperAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getSupportedValuesMethod = AttributeModel.class.getDeclaredMethod("getSupportedValues", new Class<?>[]{});
		XmlElementWrapper xmlElementWrapper = getSupportedValuesMethod.getAnnotation(XmlElementWrapper.class);
		assertEquals("supported-values", xmlElementWrapper.name());
	}
	
	@Test
	public void testGetSupportedValues_MustHaveXMLElementAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getSupportedValuesMethod = AttributeModel.class.getDeclaredMethod("getSupportedValues", new Class<?>[]{});
		XmlElement xmlElement = getSupportedValuesMethod.getAnnotation(XmlElement.class);
		assertEquals("value", xmlElement.name());
	}
	
	@Test
	public void testGetSubAttributes_MustHaveXMLElementAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getSubAttributesMethod = AttributeModel.class.getDeclaredMethod("getSubAttributes", new Class<?>[]{});
		XmlElement xmlElement = getSubAttributesMethod.getAnnotation(XmlElement.class);
		assertEquals("attribute", xmlElement.name());
	}
	
	@Test
	public void testIsTagged_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method isTaggedMethod = AttributeModel.class.getDeclaredMethod("isTagged", new Class<?>[]{});
		XmlAttribute xmlAttribute = isTaggedMethod.getAnnotation(XmlAttribute.class);
		assertEquals("has-tag", xmlAttribute.name());
	}
	
	@Test
	public void testIsIgnoreCase_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method isIgnoreCaseMethod = AttributeModel.class.getDeclaredMethod("isIgnoreCase", new Class<?>[]{});
		XmlAttribute xmlAttribute = isIgnoreCaseMethod.getAnnotation(XmlAttribute.class);
		assertEquals("ignore-case", xmlAttribute.name());
	}
	
	@Test
	public void testIsAvPair_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method isAvPairMethod = AttributeModel.class.getDeclaredMethod("isAvPair", new Class<?>[]{});
		XmlAttribute xmlAttribute = isAvPairMethod.getAnnotation(XmlAttribute.class);
		assertEquals("avpair", xmlAttribute.name());
	}
	
	@Test
	public void testGetEncryptStandard_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getEncryptStandardMethod = AttributeModel.class.getDeclaredMethod("getEncryptStandard", new Class<?>[]{});
		XmlAttribute xmlAttribute = getEncryptStandardMethod.getAnnotation(XmlAttribute.class);
		assertEquals("encrypt-standard", xmlAttribute.name());
	}
	
	@Test
	public void testGetPaddingType_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getPaddingTypeMethod = AttributeModel.class.getDeclaredMethod("getPaddingType", new Class<?>[]{});
		XmlAttribute xmlAttribute = getPaddingTypeMethod.getAnnotation(XmlAttribute.class);
		assertEquals("padding-type", xmlAttribute.name());
	}
	
	@Test
	public void testGetLengthFormat_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getLengthFormatMethod = AttributeModel.class.getDeclaredMethod("getLengthFormat", new Class<?>[]{});
		XmlAttribute xmlAttribute = getLengthFormatMethod.getAnnotation(XmlAttribute.class);
		assertEquals("length-format", xmlAttribute.name());
	}
	
	@Test
	public void testToString_ShouldNotThrowAnyExpcetion_WhenCalledOnEmptyObject(){
		attributeModel.toString();
	}
	
	/* ------- Testing getter/setter compatibility ------*/
	@Test
	@Parameters({"test","test1"})
	public void testGetSetNameCompatibility(String name) throws SecurityException, NoSuchMethodException{
		attributeModel.setName(name);
		assertEquals(name, attributeModel.getName());
	}
	
	@Test
	@Parameters({"1","2"})
	public void testGetSetIdCompatibility(int id) throws SecurityException, NoSuchMethodException{
		attributeModel.setId(id);
		assertEquals(id, attributeModel.getId());
	}
	
	@Test
	@Parameters({"test","test1"})
	public void testGetSetTypeCompatibility(String type) throws SecurityException, NoSuchMethodException{
		attributeModel.setType(type);
		assertEquals(type, attributeModel.getType());
	}
	
	@Test
	@Parameters({"false","true"})
	public void testIsSetTaggedCompatibility(Boolean isTagged) throws SecurityException, NoSuchMethodException{
		attributeModel.setTagged(isTagged);
		assertEquals(isTagged, attributeModel.isTagged());
	}
	
	@Test
	@Parameters({"false","true"})
	public void testIsSetIgnoreCaseCompatibility(boolean isIgnoreCase) throws SecurityException, NoSuchMethodException{
		attributeModel.setIgnoreCase(isIgnoreCase);
		assertEquals(isIgnoreCase, attributeModel.isIgnoreCase());
	}
	
	@Test
	@Parameters({"false","true"})
	public void testIsSetAvPairCompatibility(boolean isAvPair) throws SecurityException, NoSuchMethodException{
		attributeModel.setAvPair(isAvPair);
		assertEquals(isAvPair, attributeModel.isAvPair());
	}
	
	@Test
	@Parameters({"1","2","-1"})
	public void testGetSetEncryptStandardCompatibility(int encryptStandard) throws SecurityException, NoSuchMethodException{
		attributeModel.setEncryptStandard(encryptStandard);
		assertEquals(encryptStandard, attributeModel.getEncryptStandard());
	}
	
	@Test
	@Parameters({"none","dhcp"})
	public void testGetSetPaddingTypeCompatibility(String paddingType) throws SecurityException, NoSuchMethodException{
		attributeModel.setPaddingType(PaddingType.fromName(paddingType));
		assertEquals(PaddingType.fromName(paddingType), attributeModel.getPaddingType());
	}
	
	@Test
	@Parameters({"value","tlv"})
	public void testGetSetLengthFormatCompatibility(String lengthFormat) throws SecurityException, NoSuchMethodException{
		attributeModel.setLengthFormat(LengthFormat.fromName(lengthFormat));
		assertEquals(LengthFormat.fromName(lengthFormat), attributeModel.getLengthFormat());
	}
	
	@Test
	@Parameters({"1,name1","2,name2"})
	public void testHashCode_Contract(int id, String name){
		attributeModel.setId(id);
		attributeModel.setName(name);
		AttributeModel otherModel = new AttributeModel();
		otherModel.setId(id);
		otherModel.setName(name);
		
		assertEquals("Two invocations of hashcode on same instance should return same value",
				attributeModel.hashCode(), attributeModel.hashCode());
		assertEquals("Two objects which are equal should return same value for hashcode",
				attributeModel.hashCode(), otherModel.hashCode());
	}
	
	@Test
	public void testGetSubAttributeModel_ShouldReturnSubAttributeModelBasedOnId_AfterPostReadIsCalled(){
		attributeModel.setId(2);
		AttributeModel subAttributeModel = new AttributeModel();
		subAttributeModel.setId(1);
		subAttributeModel.setName("subname");
		attributeModel.getSubAttributes().add(subAttributeModel);
		
		attributeModel.postRead();
		
		assertNotNull(attributeModel.getSubAttributeModel(1));
		assertSame(subAttributeModel, attributeModel.getSubAttributeModel(1));
	}
	
	
	@Test
	public void testGetSupportedValueModel_ShouldReturnModelBasedOnId_AfterPostReadIsCalled(){
		attributeModel.setId(2);
		AttributeSupportedValueModel supportedValueModel = new AttributeSupportedValueModel();
		supportedValueModel.setId(1);
		supportedValueModel.setName("subname");
		attributeModel.getSupportedValues().add(supportedValueModel);
		
		attributeModel.postRead();
		
		assertNotNull(attributeModel.getSupportedValueModel(1));
		assertSame(supportedValueModel, attributeModel.getSupportedValueModel(1));
	}
	
	
	
	@Test
	public void testEquals_ShouldReturnFalseIfOtherObjectIsNull(){
		assertFalse(attributeModel.equals(null));
	}
	
	@Test
	public void testEquals_ShouldReturnFalse_WhenTypeOfParameterPassedIsNotOfSameType(){
		assertFalse(attributeModel.equals(new Object()));
	}
	
	@Test
	public void testEquals_ShouldReturnTrueForSameInstance(){
		assertTrue(attributeModel.equals(attributeModel));
	}
	
	@Test
	@Parameters({"1,name,1,name,true",
				 "1,name,2,name,false",
				 "1,name1,1,name2,false",
				 "1,name1,2,name2,false"})
	public void testEquals_ShouldCalculateEqualityBasedOnIdAndName_WhenOtherAttributeModelIsPassed(int thisId, String thisName, int thatId, String thatName, boolean expectedResult){
		attributeModel.setId(thisId);
		attributeModel.setName(thisName);
		
		AttributeModel otherAttributeModel = new AttributeModel();
		otherAttributeModel.setId(thatId);
		otherAttributeModel.setName(thatName);
		
		assertEquals(expectedResult, attributeModel.equals(otherAttributeModel));
		
	}
	
	@Test
	public void testDefaultValueOfPaddingTypeShouldBeNone(){
		assertEquals(PaddingType.NONE, attributeModel.getPaddingType());
	}
	
	@Test
	public void testDefaultValueOfLengthFormatShouldBeTLV(){
		assertEquals(LengthFormat.TLV, attributeModel.getLengthFormat());
	}
	
	@Test
	public void testPostRead_ShouldSetParentAttributeModelInSubAttributes(){
		attributeModel.setId(2);
		AttributeModel subAttributeModel = new AttributeModel();
		subAttributeModel.setId(1);
		subAttributeModel.setName("subname");
		attributeModel.getSubAttributes().add(subAttributeModel);
		
		attributeModel.postRead();
		
		assertNotNull(subAttributeModel.getParent());
	}
	
	@Test
	public void testPostRead_ShouldSameInstanceOfParentAttributeModelInSubAttributes(){
		attributeModel.setId(2);
		AttributeModel subAttributeModel = new AttributeModel();
		subAttributeModel.setId(1);
		subAttributeModel.setName("subname");
		attributeModel.getSubAttributes().add(subAttributeModel);
		
		attributeModel.postRead();
		
		assertEquals(attributeModel, subAttributeModel.getParent());
	}
	
	@Test
	public void testPostRead_ShouldSetParentAttributeModelInSupportedValues(){
		attributeModel.setId(2);
		AttributeSupportedValueModel supportedValue = new AttributeSupportedValueModel();
		supportedValue.setId(1);
		supportedValue.setName("subname");
		attributeModel.getSupportedValues().add(supportedValue);
		
		attributeModel.postRead();
		
		assertNotNull(supportedValue.getParent());
	}
	
	@Test
	public void testPostRead_ShouldSameInstanceOfParentAttributeModelInSupportedValues(){
		attributeModel.setId(2);
		AttributeSupportedValueModel supportedValue = new AttributeSupportedValueModel();
		supportedValue.setId(1);
		supportedValue.setName("subname");
		attributeModel.getSupportedValues().add(supportedValue);
		
		attributeModel.postRead();
		
		assertEquals(attributeModel, supportedValue.getParent());
	}
	
	@Test
	public void testGetParent_ShouldReturnParentAttributeModelIfPresent_AfterPostReadIsCalled(){
		attributeModel.setId(2);
		AttributeModel subAttributeModel = new AttributeModel();
		subAttributeModel.setId(1);
		subAttributeModel.setName("subname");
		attributeModel.getSubAttributes().add(subAttributeModel);
		
		attributeModel.postRead();
		
		assertNotNull(subAttributeModel.getParent());
		assertSame(attributeModel, subAttributeModel.getParent());
	}
	
	@Test
	public void testFormatId_ShouldReturnRadiusFormattedAttributeId(){
		attributeModel.setId(2);
		AttributeModel subAttributeModel = new AttributeModel();
		subAttributeModel.setId(1);
		subAttributeModel.setName("subname");
		attributeModel.getSubAttributes().add(subAttributeModel);
		
		AttributeModel subSubAttributeModel = new AttributeModel();
		subSubAttributeModel.setId(4);
		subAttributeModel.getSubAttributes().add(subSubAttributeModel);
		
		attributeModel.postRead();
		
		AttributeIdFormatter formatter = subSubAttributeModel.newIdFormatter();
		String formatIDString = formatter.formatIdAsString();
		assertEquals("2:1:4", formatIDString);
	}
	
	@Test
	public void testSetLengthFormat_ShouldUseDefualtValueTLV_IfLengthFormatArgumentIsNull(){
		attributeModel.setLengthFormat(null);
		
		assertEquals(LengthFormat.TLV, attributeModel.getLengthFormat());
	}
	
	@Test
	public void testSetPaddingType_ShouldUseDefualtValueNone_IfPaddingTypeArgumentIsNull(){
		attributeModel.setPaddingType(null);
		
		assertEquals(PaddingType.NONE, attributeModel.getPaddingType());
	}
}
