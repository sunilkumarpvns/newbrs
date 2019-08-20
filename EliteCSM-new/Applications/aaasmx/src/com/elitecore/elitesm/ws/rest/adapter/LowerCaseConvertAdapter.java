package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

/**
* 
*LowerCaseConvertAdapter do conversion of inputed value in lower case<br>
* It takes inputed value  with any upper or lower case as input and give lower case respectively as output in unmarshalling. <br>
* It takes value as input and give as output in marshal. <br>
*  
* <pre>
* 
* for Example:- <br>
* if input is: 
* {@code
* 
* <any-boolean-tag>TrUe</any-boolean-tag>
* 
* }
* 
* than output is :
* true
* 
* </pre>
* 
* @author Tejas P Shah
*
*/

public class LowerCaseConvertAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String value) throws Exception {
		if (Strings.isNullOrBlank(value) == false) {
			return value.toLowerCase();
		} else {
			return null;
		}
	}

	@Override
	public String marshal(String value) throws Exception {
		if (Strings.isNullOrBlank(value) == false) {
			return value.toLowerCase();
		} else {
			return null;
		}
	}
	
}
