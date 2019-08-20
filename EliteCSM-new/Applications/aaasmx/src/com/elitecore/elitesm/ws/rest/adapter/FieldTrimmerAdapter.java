package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
/**
* 
*FieldTrimmerAdapter do conversion of inputed value in trim less from right hand and left hand side of inputed value<br>
* It converts inputed value in trim less from right hand and left hand side of inputed value while unmarshalling. <br>
* It takes value as input and give as output in marshal. <br>
*  
* <pre>
* 
* for Example:- <br>
* if input is: 
* {@code
* 
* <any-boolean-tag> TrUe </any-boolean-tag>
* 
* }
* 
* than output is :
*TrUe
* 
* </pre>
* 
* @author Tejas P Shah
*
*/
public class FieldTrimmerAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String value) throws Exception {
		return value.trim();
	}

	@Override
	public String marshal(String value) throws Exception {
		return value;
	}

}
