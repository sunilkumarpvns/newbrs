package com.elitecore.coreradius.commons.util.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.coreradius.commons.util.dictionary.AttributeModel;
import com.elitecore.coreradius.commons.util.dictionary.AttributeSupportedValueModel;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class AttributeSupportedValueModelTest {

	private AttributeSupportedValueModel attributeSupportedValue;

	@Before
	public void setUp(){
		attributeSupportedValue = new AttributeSupportedValueModel();
	}
	
	@Test
	public void testAttributeSupportedValue_MustHaveDefaultConstructor() throws SecurityException, NoSuchMethodException{
		AttributeSupportedValueModel.class.getDeclaredConstructor(new Class<?>[]{});
	}
	
	@Test
	public void testAttributeSupportedValue_MustHavePublicDefaultConstructor() throws SecurityException, NoSuchMethodException{
		Constructor<?> constructor = AttributeSupportedValueModel.class.getDeclaredConstructor(new Class<?>[]{});
		assertTrue(Modifier.isPublic(constructor.getModifiers()));
	}
	
	@Test
	public void testGetName_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method getNameMethod = AttributeSupportedValueModel.class.getDeclaredMethod("getName", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + getNameMethod.getName() +" method.",getNameMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testGetId_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
		Method getIdMethod = AttributeSupportedValueModel.class.getDeclaredMethod("getId", new Class<?>[]{});
		assertNotNull("@XMLAttribute annotation is missing from: " + getIdMethod.getName() +" method.",getIdMethod.getAnnotation(XmlAttribute.class));
	}
	
	@Test
	public void testGetParent_MustBeTransient() throws SecurityException, NoSuchMethodException{
		Method getIdMethod = AttributeSupportedValueModel.class.getDeclaredMethod("getParent", new Class<?>[]{});
		assertNotNull("@XMLTransient annotation is missing from: " + getIdMethod.getName() +" method. It should always be transient.",getIdMethod.getAnnotation(XmlTransient.class));
	}
	
	@Test
	@Parameters({"1,name1",
				 "2,name2"})
	public void testToString_ShouldContainAllRequiredFields(int id, String name){
		attributeSupportedValue.setId(id);
		attributeSupportedValue.setName(name);
		
		assertTrue(attributeSupportedValue.toString().contains(String.valueOf(id)));
		assertTrue(attributeSupportedValue.toString().contains(name));
	}
	
	@Test
	@Parameters({"1,name1",
	 			 "2,name2"})
	public void testHashcode_Contract(int id, String name){
		attributeSupportedValue.setId(id);
		attributeSupportedValue.setName(name);
		AttributeSupportedValueModel otherSupportedValueModel = new AttributeSupportedValueModel();
		otherSupportedValueModel.setId(id);
		otherSupportedValueModel.setName(name);
		
		assertEquals("Two invocations of hashcode on same instance should return same value",
				attributeSupportedValue.hashCode(), attributeSupportedValue.hashCode());
		assertEquals("Two objects which are equal should return same value for hashcode",
				attributeSupportedValue.hashCode(), otherSupportedValueModel.hashCode());
	}
	
	/*--------- Assertion for the radius dictionary format --------------*/
	@Test
	public void testGetName_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getNameMethod = AttributeSupportedValueModel.class.getDeclaredMethod("getName", new Class<?>[]{});
		XmlAttribute xmlAttribute = getNameMethod.getAnnotation(XmlAttribute.class);
		assertEquals("name", xmlAttribute.name());
	}
	
	@Test
	public void testGetId_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
		Method getIdMethod = AttributeSupportedValueModel.class.getDeclaredMethod("getId", new Class<?>[]{});
		XmlAttribute xmlAttribute = getIdMethod.getAnnotation(XmlAttribute.class);
		assertEquals("id", xmlAttribute.name());
	}
	
	@Test
	public void testEquals_ShouldReturnFalseIfOtherObjectIsNull(){
		assertFalse(attributeSupportedValue.equals(null));
	}
	
	@Test
	public void testEquals_ShouldReturnFalse_WhenTypeOfParameterPassedIsNotOfSameType(){
		assertFalse(attributeSupportedValue.equals(new Object()));
	}
	
	@Test
	public void testEquals_ShouldReturnFalseIfNameFieldOfParameterToEqualsIsNull(){
		attributeSupportedValue.setName("name");
		assertFalse(attributeSupportedValue.equals(new AttributeSupportedValueModel()));
	}
	
	@Test
	public void testEquals_ShouldReturnFalseIfNameFieldOfObjectBeingTestedIsNull(){
		assertFalse(attributeSupportedValue.equals(new AttributeSupportedValueModel(0,"name")));
	}
	
	@Test
	public void testEquals_ShouldReturnTrueForSameInstance(){
		assertTrue(attributeSupportedValue.equals(attributeSupportedValue));
	}
	
	@Test
	@Parameters({"1,name1,1,name1,true",
				 "1,name2,1,name2,true",
				 "1,name1,2,name1,false",
				 "2,name1,2,name2,false",
				 "1,name1,2,name2,false"})
	public void testEquals_ShouldReturnProperResult(int id1, String name1, int id2, String name2, boolean expectedResult){
		attributeSupportedValue.setId(id1);
		attributeSupportedValue.setName(name1);
		
		AttributeSupportedValueModel other = new AttributeSupportedValueModel();
		other.setId(id2);
		other.setName(name2);
		
		assertEquals(expectedResult, attributeSupportedValue.equals(other));
	}
	
	/* --------------- Testing getter/setter compatibility ---------- */
	@Test
	@Parameters({"test","test1"})
	public void testGetSetNameCompatibility(String name) throws SecurityException, NoSuchMethodException{
		attributeSupportedValue.setName(name);
		assertEquals(name, attributeSupportedValue.getName());
	}
	
	@Test
	@Parameters({"1","2"})
	public void testGetSetIdCompatibility(int id) throws SecurityException, NoSuchMethodException{
		attributeSupportedValue.setId(id);
		assertEquals(id, attributeSupportedValue.getId());
	}
	
	@Test
	public void testGetSetParentCompatibility() throws SecurityException, NoSuchMethodException{
		AttributeModel parentAttributeModel = new AttributeModel();
		attributeSupportedValue.setParent(parentAttributeModel);
		
		//should be the same instance
		assertSame(parentAttributeModel, attributeSupportedValue.getParent());
		
		//should not mutate parent in some way
		assertEquals(parentAttributeModel, attributeSupportedValue.getParent());
	}
}
