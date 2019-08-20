package com.elitecore.commons.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.elitecore.commons.io.Closeables;

public class ConfigUtil {
	
	/**
	 * 
	 * <p>
	 * Converts the serialized data from reader into object.
	 * </p>
	 * <p>
	 * Note: Does not support JAXB schema validation.
	 * </p>
	 * 
	 * @param reader containing java object in serialized form
	 * @param classes java class to be deserialized
	 * @return the newly created clazz type object
	 * @throws JAXBException thrown by JAXB
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(Reader reader, Class<?>... classes) throws JAXBException {
		return (T) JAXBContext.newInstance(classes).createUnmarshaller().unmarshal(reader);
	}
	
	/**
	 * <p>
	 * Converts the serialized data from file into object.
	 * </p>
	 * <p>
	 * Note: Does not support JAXB schema validation.
	 * </p>
	 * 
	 * @param file containing java object in serialized form
	 * @param classes java class to be deserialized
	 * @return the newly created clazz type object
	 * @throws JAXBException thrown by JAXB
	 * @throws FileNotFoundException if the file does not exist, is a directory rather than 
	 * 			a regular file, or for some other reason cannot be opened for reading 
	 */
	public static <T> T deserialize(File file, Class<?>... classes) 
			throws JAXBException, FileNotFoundException {
		return deserialize(new FileReader(file), classes);
	}
	
	/**
	 * <p>
	 * Converts the serialized data from String into object.
	 * </p>
	 * <p>
	 * Note: Does not support JAXB schema validation.
	 * </p>
	 * 
	 * @param sring serialized java object as String
	 * @param classes java class to be deserialized
	 * @return the newly created clazz type object
	 * @throws JAXBException thrown by JAXB
	 */
	public static <T> T deserialize(String sring, Class<?>... classes) throws JAXBException {
		return deserialize(new StringReader(sring), classes);
	}
	
	/**
	 * <p>
	 * Converts the data object into serialized form to file
	 * </p>
	 * 
	 * 
	 * @param file in which serialized data is written 
	 * @param clazz java class to serialized
	 * @param dataObject from which serialized data is generated. must be of the clazz type.
	 * @throws JAXBException thrown by JAXB
	 * @throws IOException if the file does not exist, is a directory rather than 
	 * 			a regular file, or for some other reason cannot be opened for writing
	 */
	public static <T> void serialize(File file, Class<T> clazz, T dataObject) 
			throws JAXBException, IOException {
		FileWriter fileWriter = null;
		try { 
			fileWriter = new FileWriter(file);
			serialize(fileWriter, clazz, dataObject);
		} finally {
			Closeables.closeQuietly(fileWriter);
		}
	}
	
	/**
	 * <p>
	 * Converts the data object into serialized form to writer
	 * </p>
	 * 
	 * @param writer in which serialized data is written 
	 * @param clazz java class to serialized
	 * @param dataObject from which serialized data is generated. must be of the clazz type.
	 * @throws JAXBException thrown by JAXB
	 */
	public static <T> void serialize(Writer writer, Class<T> clazz, T dataObject) 
			throws JAXBException {
		Marshaller jaxbMarshaller = JAXBContext.newInstance(clazz).createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(dataObject,writer);
	}
	
	/**
	 * <p>
	 * Returns the data object into serialized form
	 * </p>
	 * 
	 * @param clazz java class to serialized
	 * @param dataObject from which serialized data is generated. must be of the clazz type.
	 * @return serialized data as String 
	 * @throws JAXBException thrown by JAXB
	 */
	public static <T> String serialize(Class<T> clazz, T dataObject) 
			throws JAXBException {
		StringWriter stringWriter = new StringWriter();
		serialize(stringWriter, clazz, dataObject);
		return stringWriter.toString();
	}

}
