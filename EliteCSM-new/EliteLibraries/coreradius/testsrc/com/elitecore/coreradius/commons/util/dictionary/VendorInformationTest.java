package com.elitecore.coreradius.commons.util.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.coreradius.commons.util.DictionaryParseException;

/**
 *
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class VendorInformationTest {

    private VendorInformation vendorInformation;

    @Before
    public void setUp(){
        vendorInformation = new VendorInformation();
    }

    @Test
    public void testConstructor_MustHaveADefaultConstructorRequiredForJAXB() throws SecurityException, NoSuchMethodException{
        VendorInformation.class.getDeclaredConstructor(new Class<?>[]{});
    }

    @Test
    public void testConstructor_MustBePublic() throws SecurityException, NoSuchMethodException{
        Constructor<?> constructor = VendorInformation.class.getDeclaredConstructor(new Class<?>[]{});
        assertTrue(Modifier.isPublic(constructor.getModifiers()));
    }

    @Test
    public void testConstructor_ShouldInitializeDictionaryModelListToNullNullValue(){
        assertNotNull(vendorInformation.getAttributeModels());
    }

    @Test
    public void testConstructor_ShouldInitializeDictionaryModelListToEmptyValue(){
        assertTrue(vendorInformation.getAttributeModels().isEmpty());
    }

    @Test
    public void testGetVendorName_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
        Method getVendorNameMethod = VendorInformation.class.getDeclaredMethod("getVendorName", new Class<?>[]{});
        assertNotNull("@XMLAttribute annotation is missing from: " + getVendorNameMethod.getName() +" method.",getVendorNameMethod.getAnnotation(XmlAttribute.class));
    }

    @Test
    public void testGetVendorId_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
        Method getVendorIdMethod = VendorInformation.class.getDeclaredMethod("getVendorId", new Class<?>[]{});
        assertNotNull("@XMLAttribute annotation is missing from: " + getVendorIdMethod.getName() +" method.",getVendorIdMethod.getAnnotation(XmlAttribute.class));
    }

    @Test
    public void testGetAttributeModels_MustHaveXMLElementAnnotation() throws SecurityException, NoSuchMethodException{
        Method getAttributeModelsMethod = VendorInformation.class.getDeclaredMethod("getAttributeModels", new Class<?>[]{});
        assertNotNull("@XMLElement annotation is missing from: " + getAttributeModelsMethod.getName() +" method.",getAttributeModelsMethod.getAnnotation(XmlElement.class));
    }

    @Test
    public void testGetAvPairSeparator_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
        Method getAvPairSeparatorMethod = VendorInformation.class.getDeclaredMethod("getAvPairSeparator", new Class<?>[]{});
        assertNotNull("@XMLAttribute annotation is missing from: " + getAvPairSeparatorMethod.getName() +" method.",getAvPairSeparatorMethod.getAnnotation(XmlAttribute.class));
    }


    @Test
    public void testGetFormat_MustHaveXMLAttributeAnnotation() throws SecurityException, NoSuchMethodException{
        Method getFormatMethod = VendorInformation.class.getDeclaredMethod("getFormat", new Class<?>[]{});
        assertNotNull("@XMLAttribute annotation is missing from: " + getFormatMethod.getName() +" method.",getFormatMethod.getAnnotation(XmlAttribute.class));
    }

    /*--------- Assertion for the radius dictionary format --------------*/
    @Test
    public void testGetVendorName_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
        Method getVendorNameMethod = VendorInformation.class.getDeclaredMethod("getVendorName", new Class<?>[]{});
        XmlAttribute xmlAttribute = getVendorNameMethod.getAnnotation(XmlAttribute.class);
        assertEquals("vendor-name", xmlAttribute.name());
    }

    @Test
    public void testGetVendorId_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
        Method getVendorIdMethod = VendorInformation.class.getDeclaredMethod("getVendorId", new Class<?>[]{});
        XmlAttribute xmlAttribute = getVendorIdMethod.getAnnotation(XmlAttribute.class);
        assertEquals("vendorid", xmlAttribute.name());
    }

    @Test
    public void testAttributeModels_MustHaveXMLElementAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
        Method getAttributeModelsMethod = VendorInformation.class.getDeclaredMethod("getAttributeModels", new Class<?>[]{});
        XmlElement xmlElement = getAttributeModelsMethod.getAnnotation(XmlElement.class);
        assertEquals("attribute", xmlElement.name());
    }

    @Test
    public void testAvPairSeparator_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
        Method getAvPairSeparatorMethod = VendorInformation.class.getDeclaredMethod("getAvPairSeparator", new Class<?>[]{});
        XmlAttribute xmlAttribute = getAvPairSeparatorMethod.getAnnotation(XmlAttribute.class);
        assertEquals("avpair-separator", xmlAttribute.name());
    }

    @Test
    public void testGetFormat_MustHaveXMLAttributeAnnotationWithNameConfirmingToRadiusDictionary() throws SecurityException, NoSuchMethodException{
        Method getFormatMethod = VendorInformation.class.getDeclaredMethod("getFormat", new Class<?>[]{});
        XmlAttribute xmlAttribute = getFormatMethod.getAnnotation(XmlAttribute.class);
        assertEquals("format", xmlAttribute.name());
    }

    @Test
    public void testGetAvPairSeparator_MustHaveDefaultValueOfSpace(){
        assertEquals(" ", vendorInformation.getAvPairSeparator());
    }

    @Test
    public void testGetFormat_MustHaveDefaultValueOf1_1(){
        assertEquals("1,1", vendorInformation.getFormat());
    }

	/* ---------- Testing Getter/Setter compatibility --------- */

    @Test
    @Parameters({"test","test1"})
    public void testGetSetVendorNameCompatibility(String vendorName) throws SecurityException, NoSuchMethodException{
        vendorInformation.setVendorName(vendorName);
        assertEquals(vendorName, vendorInformation.getVendorName());
    }

    @Test
    @Parameters({"1","2"})
    public void testGetSetVendorIdCompatibility(int vendorId) throws SecurityException, NoSuchMethodException{
        vendorInformation.setVendorId(vendorId);
        assertEquals(vendorId, vendorInformation.getVendorId());
    }

    @Test
    @Parameters({"@","@@"})
    public void testGetSetAvPairSeparatorCompatibility(String avPairSeparator) throws SecurityException, NoSuchMethodException{
        vendorInformation.setAvPairSeparator(avPairSeparator);
        assertEquals(avPairSeparator, vendorInformation.getAvPairSeparator());
    }

    @Test
    @Parameters({"1\\,1","1\\,2"})
    public void testGetSetFormatCompatibility(String format) throws SecurityException, NoSuchMethodException{
        vendorInformation.setFormat(format);
        assertEquals(format, vendorInformation.getFormat());
    }


    @Test
    public void testEquals_ShouldReturnFalseIfOtherObjectIsNull(){
        assertFalse(vendorInformation.equals(null));
    }

    @Test
    public void testEquals_ShouldReturnFalse_WhenTypeOfParameterPassedIsNotOfSameType(){
        assertFalse(vendorInformation.equals(new Object()));
    }

    @Test
    public void testEquals_ShouldReturnTrueForSameInstance(){
        assertTrue(vendorInformation.equals(vendorInformation));
    }

    @Test
    @Parameters({
            "1,1,true",
            "1,2,false",
            "2,2,true",
            "-1,-2,false",
            "-2,-2,true"
    })
    public void testEquals_ShouldReturnExpectedValueForIdEquality(int thisId, int thatId, boolean expectedResult){
        vendorInformation.setVendorId(thisId);

        VendorInformation thatVendorInformation = new VendorInformation();
        thatVendorInformation.setVendorId(thatId);

        assertEquals(expectedResult, vendorInformation.equals(thatVendorInformation));
    }


    @Test
    @Parameters({
            "name1,name1,true",
            "name1,name2,false",
            "name2,name2,true"})
    public void testEquals_ShouldReturnExpectedValueForNameEquality(String thisName, String thatName, boolean expectedResult){
        vendorInformation.setVendorName(thisName);

        VendorInformation thatVendorInformation = new VendorInformation();
        thatVendorInformation.setVendorName(thatName);

        assertEquals(expectedResult, vendorInformation.equals(thatVendorInformation));
    }

    @Test
    @Parameters(method = "dataFor_testEquals_ShouldReturnProperResult")
    public void testEquals_ShouldReturnProperResult(int id, String name, String avPair, Set<AttributeModel> attributeModels,
                                                    int otherId, String otherName, String otherAvPair, Set<AttributeModel> otherAttributeModels, boolean result){
        vendorInformation.setVendorId(id);
        vendorInformation.setVendorName(name);
        vendorInformation.setAvPairSeparator(avPair);
        vendorInformation.getAttributeModels().addAll(attributeModels);

        VendorInformation otherVendorInformation = new VendorInformation();
        otherVendorInformation.setVendorId(otherId);
        otherVendorInformation.setVendorName(otherName);
        otherVendorInformation.setAvPairSeparator(otherAvPair);
        otherVendorInformation.getAttributeModels().addAll(otherAttributeModels);

        boolean equals = vendorInformation.equals(otherVendorInformation);
        assertEquals(result, equals);
    }

    @Test(expected = DictionaryParseException.class)
    @Parameters(method = "dataFor_testPostRead_ShouldThrowDictionaryParseException_WhenAttributeFormatIsInvalid")
    public void testPostRead_ShouldThrowDictionaryParseException_WhenAttributeFormatIsInvalid(String format) throws DictionaryParseException{
        vendorInformation.setFormat(format);

        vendorInformation.postRead();
    }

    public Object[][] dataFor_testPostRead_ShouldThrowDictionaryParseException_WhenAttributeFormatIsInvalid(){
        return new Object[][]{
                {"1.1,1"},
                {"1,1.1"},
                {"-1,1"},
                {"1,-1,"},
                {"a,1"},
                {"1,a"},
                {"a,a"},
                {"0,0"},
                {"1,0"},
                {"0,1"},
                {"00"},
                {"0,0,0"},
                {"1,1,1"}
        };
    }

    public Object[][] dataFor_testEquals_ShouldReturnProperResult(){
        Set<AttributeModel> am1 = new HashSet<AttributeModel>();
        am1.add(new AttributeModel());
        Set<AttributeModel> am2 = new HashSet<AttributeModel>();
        AttributeModel attributeModel = new AttributeModel();
        attributeModel.setId(1);
        am1.add(attributeModel);

        return new Object[][]{
                {1,"name1","@",am1,1,"name1","@",am1,true},
                {1,"name1","@",am1,2,"name1","@",am1,false},
                {1,"name1","@",am1,1,"name2","@",am1,false},
                {1,"name1","@",am1,1,"name1","@@",am1,true}, //will not consider av-pair separator
                {1,"name1","@",am1,1,"name1","@",am2,true}, //will not consider attributes

        };
    }

    @Test
    @Parameters(method = "dataFor_testGetFormatBytesCount_ShouldReturnByteCountFromFormat_AfterPostReadIsCalled")
    public void testGetFormatBytesCountForIdentifier_ShouldReturnByteCountForIdentifierFromFormat_AfterPostReadIsCalled(String formatString, int expectedByteCountForIdentifier, int unused) throws DictionaryParseException{
        vendorInformation.setFormat(formatString);

        vendorInformation.postRead();

        assertEquals(expectedByteCountForIdentifier, vendorInformation.getFormatBytesCountForIdentifier());
    }


    @Test
    @Parameters(method = "dataFor_testGetFormatBytesCount_ShouldReturnByteCountFromFormat_AfterPostReadIsCalled")
    public void testGetFormatBytesCountForLength_ShouldReturnByteCountForIdentifierFromFormat_AfterPostReadIsCalled(String formatString, int unused, int expectedByteCountForLength) throws DictionaryParseException{
        vendorInformation.setFormat(formatString);

        vendorInformation.postRead();

        assertEquals(expectedByteCountForLength, vendorInformation.getFormatBytesCountForLength());
    }

    public Object[][] dataFor_testGetFormatBytesCount_ShouldReturnByteCountFromFormat_AfterPostReadIsCalled(){
        return new Object[][]{
                {"1,1", 1, 1},
                {"1,2", 1, 2},
                {"2,1", 2, 1},
                {"2,2", 2, 2},
        };
    }

    @Test
    public void testGetAttributeModelForId_ShouldReturnAttributeModelBasedOnAttributeId_AfterPostReadIsCalled() throws DictionaryParseException{
        AttributeModel model = new AttributeModel();
        model.setId(1);
        AttributeModel model2 = new AttributeModel();
        model2.setId(2);

        vendorInformation.getAttributeModels().add(model);
        vendorInformation.getAttributeModels().add(model2);

        vendorInformation.postRead();

        assertNotNull(vendorInformation.getAttributeModelForId(1));
        assertNotNull(vendorInformation.getAttributeModelForId(2));
        assertSame(model, vendorInformation.getAttributeModelForId(1));
        assertSame(model2, vendorInformation.getAttributeModelForId(2));
    }


    @Test
    public void testHashCode_Contract(){
        vendorInformation.setVendorId(1);
        vendorInformation.setVendorName("name");

        VendorInformation otherVendorInformation = new VendorInformation();
        otherVendorInformation.setVendorId(1);
        otherVendorInformation.setVendorName("name");

        assertEquals("Two invocations of hashcode on same instance should return same value",
                Integer.valueOf(vendorInformation.hashCode()), Integer.valueOf(vendorInformation.hashCode()));
        assertEquals("Two objects which are equal should return same value for hashcode",
                Integer.valueOf(vendorInformation.hashCode()), Integer.valueOf(otherVendorInformation.hashCode()));
    }

    @Test
    public void testGetAttributeModels_ShouldNotContainTwoEquivalentAttributeModels(){
        AttributeModel attributeModel1 = new AttributeModel();
        attributeModel1.setId(1);
        attributeModel1.setName("name");

        AttributeModel attributeModel2 = new AttributeModel();
        attributeModel2.setId(1);
        attributeModel2.setName("name");

        vendorInformation.getAttributeModels().add(attributeModel1);
        vendorInformation.getAttributeModels().add(attributeModel2);

        //must only contain one of the attribute models as they are equal
        assertEquals(1, vendorInformation.getAttributeModels().size());
    }
}
