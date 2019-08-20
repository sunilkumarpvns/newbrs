package com.elitecore.coreradius.commons.util.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Set;

import com.elitecore.coreradius.commons.util.Dictionary;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.coreradius.commons.util.DictionaryParseException;
import com.elitecore.coreradius.commons.util.constants.LengthFormat;
import com.elitecore.coreradius.commons.util.constants.PaddingType;

/**
 *
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class VendorInformationFactoryTest {

    String dictionaryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<attribute-list vendorid=\"0\" vendor-name=\"test\" avpair-separator=\"@@\" format=\"1,1\">"
            + "</attribute-list>";

    //end tag is missing
    String dictionaryStringInImproperXMLFormat = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<attribute-list vendorid=\"0\" vendor-name=\"test\" avpair-separator=\"@@\" format=\"1,1\">";

    private DictionaryModelFactory factory;

    @Before
    public void setUp(){
        factory = new DictionaryModelFactory();
    }

    @Test
    public void testNewModelfrom_ShouldCallPostReadOnNewlyCreatedDictionaryModel() throws DictionaryParseException{
        VendorInformationFactoryStub instance = new VendorInformationFactoryStub();
        DictionaryModel model = instance.newModelfrom(new StringReader(dictionaryString));
        //verify(model).postRead();
    }

    @Test(expected = DictionaryParseException.class)
    public void testNewModelfrom_ShouldRethrowDictionaryParseException_IfDictionaryIsImproper() throws DictionaryParseException{
        factory.newModelfrom(new StringReader(dictionaryStringInImproperXMLFormat));
    }

    @Test
    public void testNewModelfrom_ShouldProvideUnderlyingReason_IfParsingExceptionOccurs(){
        try {
            factory.newModelfrom(new StringReader(dictionaryStringInImproperXMLFormat));
        } catch (DictionaryParseException e) {
            assertNotNull(e.getCause());
        }
    }

    @Test
    @Parameters(method = "dataFor_PositiveTestCasesReading")
    public void testNewModelfrom_ShouldReturnProperlyReadDictionaryModel_WhenReadUsingReader(String dictionaryString, VendorInformation expecteVendorInformation) throws DictionaryParseException{
        for(VendorInformation actualVendorInformation: factory.newModelfrom(new StringReader(dictionaryString)).getIdToVendorInformation().values()) {
            VendorInformationAssert.assertDictionaryModelEquals(expecteVendorInformation, actualVendorInformation);
        }
    }

    public Object[][] dataFor_PositiveTestCasesReading(){
        VendorInformation vendorInformation = new VendorInformation();
        vendorInformation.setVendorName("test");
        vendorInformation.setVendorId(0);
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
        attributeModel.setIgnoreCase(false);
        attributeModel.setLengthFormat(LengthFormat.VALUE);
        attributeModel.setPaddingType(PaddingType.DHCP);
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

        subAttributeModel.setId(2);
        subAttributeModel.setName("Param2");
        subAttributeModel.setType("string");
        attributeModel.getSubAttributes().add(subAttributeModel);

        subAttributeModel.setId(3);
        subAttributeModel.setName("Param3");
        subAttributeModel.setType("string");
        attributeModel.getSubAttributes().add(subAttributeModel);

        vendorInformation.getAttributeModels().add(attributeModel);


        return new Object[][]{
                {	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<attribute-list vendorid=\"0\" vendor-name=\"test\" avpair-separator=\"@@\" format=\"1,1\">"
                        + "<attribute id=\"1\" name=\"Attrib1\" type=\"string\" encrypt-standard=\"1\" has-tag=\"true\" ignore-case=\"true\" length-format=\"tlv\" padding-type=\"none\" avpair=\"false\"/>"
                        + "<attribute id=\"2\" name=\"Attrib2\" type=\"integer\" encrypt-standard=\"2\" has-tag=\"false\" ignore-case=\"false\" length-format=\"value\" padding-type=\"dhcp\" avpair=\"true\"/>"
                        + "<attribute id=\"3\" name=\"Attrib3\" type=\"integer\">"
                        + "<supported-values>"
                        +"<value id=\"1\" name=\"sv1\"/>"
                        +"<value id=\"2\" name=\"sv2\"/>"
                        + "</supported-values>"
                        + "</attribute>"
                        + "<attribute id=\"4\" name=\"Attrib4\"  type=\"grouped\">"
                        +"<attribute id=\"1\" name=\"Param1\"  type=\"string\"/>"
                        + "<attribute id=\"2\" name=\"Param2\"  type=\"string\"/>"
                        + "<attribute id=\"3\" name=\"Param3\"  type=\"string\"/>"
                        + "</attribute>"
                        + "</attribute-list>", vendorInformation}
        };
    }

    public static class VendorInformationAssert {
        public static void assertDictionaryModelEquals(VendorInformation expectedVendorInformation, VendorInformation actualVendorInformation) {
            assertEquals(expectedVendorInformation.getVendorId(), actualVendorInformation.getVendorId());
            assertEquals(expectedVendorInformation.getVendorName(), actualVendorInformation.getVendorName());
            assertEquals(expectedVendorInformation.getAvPairSeparator(), actualVendorInformation.getAvPairSeparator());
            assertEquals(expectedVendorInformation.getFormat(), actualVendorInformation.getFormat());
            assertAttributeModelSetEquals(expectedVendorInformation.getAttributeModels(), actualVendorInformation.getAttributeModels());
        }


        public static void assertAttributeModelSetEquals(Set<AttributeModel> expectedAttributeModelSet, Set<AttributeModel> actualAttributeModelSet){
            Iterator<AttributeModel> iterator = expectedAttributeModelSet.iterator();
            while(iterator.hasNext()){
                AttributeModel expectedAttributeModel = iterator.next();
                Iterator<AttributeModel> actualIterator = actualAttributeModelSet.iterator();
                while(actualIterator.hasNext()){
                    AttributeModel actualAttributeModel = actualIterator.next();
                    if(expectedAttributeModel.equals(actualAttributeModel)){
                        assertAttributeModelEquals(expectedAttributeModel, actualAttributeModel);
                    }
                }
            }
        }

        public static void assertAttributeModelEquals(AttributeModel expectedAttributeModel, AttributeModel actualAttributeModel) {
            assertEquals(expectedAttributeModel.getId(), actualAttributeModel.getId());
            assertEquals(expectedAttributeModel.getLengthFormat(), actualAttributeModel.getLengthFormat());
            assertEquals(expectedAttributeModel.getEncryptStandard(), actualAttributeModel.getEncryptStandard());
            assertEquals(expectedAttributeModel.getName(), actualAttributeModel.getName());
            assertEquals(expectedAttributeModel.getPaddingType(), actualAttributeModel.getPaddingType());
            assertEquals(expectedAttributeModel.getType(), actualAttributeModel.getType());
            assertEquals(expectedAttributeModel.isAvPair(), actualAttributeModel.isAvPair());
            assertEquals(expectedAttributeModel.isIgnoreCase(), actualAttributeModel.isIgnoreCase());
            assertEquals(expectedAttributeModel.isTagged(), actualAttributeModel.isTagged());
            assertAttributeModelSetEquals(expectedAttributeModel.getSubAttributes(), actualAttributeModel.getSubAttributes());
            assertSupportedValuesSetEquals(expectedAttributeModel.getSupportedValues(), actualAttributeModel.getSupportedValues());
        }

        public static void assertSupportedValuesSetEquals(Set<AttributeSupportedValueModel> expectedSupportedValues, Set<AttributeSupportedValueModel> actualSupportedValues) {
            Iterator<AttributeSupportedValueModel> iterator = expectedSupportedValues.iterator();
            while(iterator.hasNext()){
                AttributeSupportedValueModel expectedAttributeSupportedValue = iterator.next();
                Iterator<AttributeSupportedValueModel> actualIterator = actualSupportedValues.iterator();
                while(actualIterator.hasNext()){
                    AttributeSupportedValueModel actualAttributeSupportedValue = actualIterator.next();
                    if(expectedAttributeSupportedValue.equals(actualAttributeSupportedValue)){
                        assertAttributeSupportedValueEquals(expectedAttributeSupportedValue, actualAttributeSupportedValue);
                    }
                }
            }
        }

        public static void assertAttributeSupportedValueEquals(AttributeSupportedValueModel expectedAttributeSupportedValue,AttributeSupportedValueModel actualAttributeSupportedValue) {
            assertEquals(expectedAttributeSupportedValue.getId(), actualAttributeSupportedValue.getId());
            assertEquals(expectedAttributeSupportedValue.getName(), actualAttributeSupportedValue.getName());
        }
    }

    class VendorInformationFactoryStub extends DictionaryModelFactory{
        @Override
        protected VendorInformation read(Reader reader) throws DictionaryParseException {
            return Mockito.spy(super.read(reader));
        }
    }
}
