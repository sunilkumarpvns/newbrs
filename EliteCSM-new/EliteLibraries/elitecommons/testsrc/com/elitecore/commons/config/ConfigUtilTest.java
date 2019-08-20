package com.elitecore.commons.config;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.elitecore.commons.io.Files;

public class ConfigUtilTest {

	private static final String EXPECTED_XML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<test>\n" +
					"    <value>1</value>\n" +
					"</test>\n";
	private static final ValidTestData EXPECTED_VALID_TEST_DATA = new ValidTestData(1);
	private static final InvalidTestData EXPECTED_INVALID_TEST_DATA = new InvalidTestData();

	private static final String XML_VALID_STRING = 
			"<test>" +
					"<value>" +
						"1" +
					"</value>" +
			"</test>";
	
	private static final String XML_INVALID_STRING = 
			"<test>" +
					"<value>" +
						"1" +
			"</test>";

	@Rule public ExpectedException expectedException = ExpectedException.none();
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	
	@Test
	public void testSerialize_WithStringWriter_ShouldBeSuccessfull() throws JAXBException, IOException {

		StringWriter stringWriter = new StringWriter();
		ConfigUtil.serialize(stringWriter, ValidTestData.class, EXPECTED_VALID_TEST_DATA);
		stringWriter.close();

		assertEquals(EXPECTED_XML, stringWriter.toString());
	}

	@Test
	public void testSerialize_WithStringWriter_ShouldThrowJAXBException_WhenInvalidObjectIsPassed() throws JAXBException, IOException {
		expectedException.expect(JAXBException.class);

		ConfigUtil.serialize(new StringWriter(), InvalidTestData.class, EXPECTED_INVALID_TEST_DATA);
	}

	@Test
	public void testSerialize_WithFile_ShouldWriteDataToAFile() throws JAXBException, IOException {
		File file = folder.newFile("testData.xml");

		ConfigUtil.serialize(file, ValidTestData.class, EXPECTED_VALID_TEST_DATA);

		String writtenXML = new String(Files.readFully(file));

		assertEquals(EXPECTED_XML, writtenXML);
	}

	@Test
	public void testSerialize_WithFile_ShouldThrowJAXBException_WhenInvalidObjectIsPassed() throws JAXBException, IOException {
		expectedException.expect(JAXBException.class);

		File file = folder.newFile("testData.xml");

		ConfigUtil.serialize(file, InvalidTestData.class, EXPECTED_INVALID_TEST_DATA);
	}

	@Test
	public void testSerialize_WithFile_ShouldThrowIOException_WhenFileIsUnavailable() throws JAXBException, IOException {
		expectedException.expect(JAXBException.class);

		File file = folder.newFile("testData.xml");
		String string = file.getAbsolutePath();
		file.delete();

		file = new File(string);

		ConfigUtil.serialize(file, InvalidTestData.class, EXPECTED_INVALID_TEST_DATA);
	}

	@Test
	public void testSerialize_ShouldReturnXmlString() throws JAXBException {
		assertEquals(EXPECTED_XML, ConfigUtil.serialize(ValidTestData.class, EXPECTED_VALID_TEST_DATA));
	}

	@Test
	public void testSerialize_ShouldThrowJAXBException_WhenInvalidObjectIsPassed() throws JAXBException {
		expectedException.expect(JAXBException.class);

		ConfigUtil.serialize(InvalidTestData.class, EXPECTED_INVALID_TEST_DATA);
	}

	@XmlRootElement(name = "test")
	public static class ValidTestData {
		private int value;

		// For JAXB use
		public ValidTestData() {
		}

		public ValidTestData(int value) {
			this.value = value;
		}
		@XmlElement(name = "value", type = int.class)
		public int getValue() {
			return this.value;
		}
		public void setValue(int value) {
			this.value = value;
		}
	}

	public static class InvalidTestData {
		private int value;

		// For JAXB use
		public InvalidTestData() {
		}

		public InvalidTestData(int value) {
			this.value = value;
		}

		@XmlElement(name = "value", type = int.class)
		public int getValue() {
			return this.value;
		}
		public void setValue(int value) {
			this.value = value;
		}
	}


	@Test
	public void testDeseiralize_WithStringReader_ShouldBeSuccessful() throws JAXBException {
		TestData testData = ConfigUtil.deserialize(new StringReader(XML_VALID_STRING), TestData.class);
		TestData expectedTestData = new TestData();
		expectedTestData.setValue(1);
		
		assertEquals(expectedTestData, testData);
	}

	@Test
	public void testDeserialize_WithStringReader_ShouldThrowJAXBException_WhenImproperXMLStringIsPassed() throws JAXBException {
		expectedException.expect(JAXBException.class);
		
		ConfigUtil.deserialize(new StringReader(XML_INVALID_STRING), TestData.class);
	}

	@Test
	public void testDeserialize_WithXMLFile_ShouldBeSuccessful() throws JAXBException, IOException {
		File file = folder.newFile("testData.xml");
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(XML_VALID_STRING);
		fileWriter.close();

		TestData testData = ConfigUtil.deserialize(file, TestData.class);
		TestData expectedTestData = new TestData();
		expectedTestData.setValue(1);
		
		assertEquals(expectedTestData, testData);
	}

	@Test
	public void testDeserialize_WithXMLFile_ShouldThrowJAXBException_WhenImproperXMLStringIsPassed() throws JAXBException, IOException {
		File file = folder.newFile("testData.xml");
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(XML_INVALID_STRING);
		fileWriter.close();

		expectedException.expect(JAXBException.class);
		
		ConfigUtil.deserialize(file, TestData.class);
	}

	@Test
	public void testDeserialize_WithXMLFile_ShouldThrowIOException_WhenPaasedFileIsUnavailable() throws IOException, JAXBException {
		File file = folder.newFile("testData.xml");
		String string = file.getAbsolutePath();
		file.delete();

		file = new File(string);

		expectedException.expect(IOException.class);
		
		ConfigUtil.deserialize(file, TestData.class);
	}

	@Test
	public void testDeserialize_WithString_ShouldBeSuccessful() throws JAXBException, IOException {
		TestData testData = ConfigUtil.deserialize(XML_VALID_STRING, TestData.class);
		TestData expectedTestData = new TestData();
		expectedTestData.setValue(1);
		
		assertEquals(expectedTestData, testData);
	}

	@Test
	public void testDeserialize_WithString_ShouldThrowJAXBException_WhenImproperXMLStringIsPassed() throws JAXBException, IOException {
		expectedException.expect(JAXBException.class);

		ConfigUtil.deserialize(XML_INVALID_STRING, TestData.class);
	}

	@XmlRootElement(name = "test")
	public static class TestData {
		private int value;

		@XmlElement(name = "value", type = int.class)
		public int getValue() {
			return this.value;
		}
		public void setValue(int value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TestData == false) {
				return false;
			}
			TestData that = (TestData) obj;
			return this.value == that.value;
		}
	}
}