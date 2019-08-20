package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
* 
*InitCapCaseAdapter do conversion of inputed value in Init capital case<br>
* It takes inputed value  with any upper or lower case as input and give First Letter Initialize respectively as output in unmarshalling. <br>
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
* True
* 
* </pre>
* 
* @author Tejas P Shah
*
*/

public class InitCapCaseAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String value) throws Exception {
		if (value.isEmpty() == false) {
			return value = value.substring(0, 1).toUpperCase()+ value.substring(1).toLowerCase();
		}
		return value;
	}

	@Override
	public String marshal(String value) throws Exception {
		return value;
	}
}