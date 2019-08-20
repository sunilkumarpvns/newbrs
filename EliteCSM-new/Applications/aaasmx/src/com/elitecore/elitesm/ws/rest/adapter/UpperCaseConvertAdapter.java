package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
* 
* UpperCaseConvertAdapter do conversion of inputed value in upper case<br>
* It takes inputed value  with any upper or lower case as input and give upper case respectively as output in unmarshalling.<br>
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
* TRUE
* 
* </pre>
* 
* @author @author Tejas P Shah
*
*/

public class UpperCaseConvertAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String value) throws Exception {
		return value.toUpperCase();
	}

	@Override
	public String marshal(String value) throws Exception {
		return value;
	}
	
}
