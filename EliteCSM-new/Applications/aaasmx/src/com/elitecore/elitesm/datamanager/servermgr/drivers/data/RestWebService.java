package com.elitecore.elitesm.datamanager.servermgr.drivers.data;


import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class RestWebService {

	public static String getXML(Object obj) throws JAXBException{
		StringWriter stringWriter = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(obj,stringWriter);
		return stringWriter.toString();
	}
	
	
	public static String getXML(Object obj,Class<?> clazz) throws JAXBException{
		StringWriter stringWriter = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass(),clazz);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(obj,stringWriter);
		return stringWriter.toString();
	}
	
	public static String getJSON(Object obj) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		StringWriter stringWriter = new StringWriter();
		mapper.writeValue(stringWriter, obj);
		String jsonInString = mapper.writeValueAsString(obj);
		return jsonInString;
	}
}


