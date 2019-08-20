package com.elitecore.elitesm.ws.rest.utility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.elitecore.elitesm.ws.logger.Logger;

public class MessageWriter implements MessageBodyWriter<ListWrapper<DBFieldList>> {

	private static final String MODULE = "REST-MSG-WRITER";

	@Override
	public long getSize(ListWrapper<DBFieldList> listWrapper, Class<?> arg1, Type arg2,Annotation[] arg3, MediaType mediaType) {
		return listWrapper.getDatalist().size();
	}

	/**
	 * Need to register the class in jaxb.index file whenever any new class 
	 * will introduces for JAXB marshaling.
	 * 
	 * or need to change in the package declaration of JAXBContext when classes
	 * of JAXB moved out of the package also change {@link jaxb.index} accordingly. 
	 */
	@Override
	public void writeTo(ListWrapper<DBFieldList> listWrapper, Class<?> arg1, Type arg2,Annotation[] arg3, MediaType arg4,MultivaluedMap<String, Object> multivaluedMap, OutputStream entityStream)
			throws IOException, WebApplicationException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(entityStream));
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(listWrapper.getClass().getPackage().getName());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			jaxbMarshaller.marshal(listWrapper, bw);
		} catch (JAXBException ex) {
			Logger.logError(MODULE, "Error while marshalling the response.");
			Logger.logTrace(MODULE, ex);
		}
	}

	@Override
	public boolean isWriteable(Class<?> type, Type arg1, Annotation[] arg2,MediaType mediaType) {
		return mediaType.equals(MediaType.APPLICATION_XML) || mediaType.equals(MediaType.TEXT_XML);
	}
}
