package com.elitecore.coreradius.commons.util.dictionary;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.elitecore.coreradius.commons.util.DictionaryParseException;

/**
 *
 * @author narendra.pathai
 *
 */
public class DictionaryModelFactory{

	public DictionaryModel newModelfrom(Reader reader) throws DictionaryParseException{
		VendorInformation vendorInformation = read(reader);
		vendorInformation.postRead();
		Map<Long, VendorInformation> idToVendorInformation = new HashMap<>();
		idToVendorInformation.put(vendorInformation.getVendorId(), vendorInformation);
		return new DictionaryModel(idToVendorInformation);
	}

	//presently schema validation is off
	protected VendorInformation read(Reader reader) throws DictionaryParseException{
		try{
			JAXBContext context = JAXBContext.newInstance(VendorInformation.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return  (VendorInformation) unmarshaller.unmarshal(reader);
		}catch (JAXBException e) {
			throw new DictionaryParseException(e);
		}
	}
}
