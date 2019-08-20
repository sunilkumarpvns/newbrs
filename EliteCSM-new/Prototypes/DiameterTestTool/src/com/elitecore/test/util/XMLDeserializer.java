package com.elitecore.test.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLDeserializer {
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(File file, Class<T> claze) throws JAXBException, IOException{
		JAXBContext context = JAXBContext.newInstance(claze);
		 Unmarshaller um = context.createUnmarshaller();
		 FileReader fileReader = new FileReader(file);
		 T t =  (T) um.unmarshal(fileReader);
		 
		 fileReader.close();
		 return t;
	}
	

	

}
