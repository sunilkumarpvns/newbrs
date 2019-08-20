package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

/**
 * 
 * Access Time Adapter do conversion, String to Integer and vice versa. <br>
 * It takes String as input and give Integer as output in unmarshal. <br>
 * It takes Integer as input and give String as output in marshal. <br>
 * For invalid values it gives String invalid. 
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <start-hour>a12b</start-hour>
 * 
 * }
 * 
 * than output is :
 * invalid
 * 
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */


public class AccessTimeAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String value) throws Exception {

		String returnValue = null;

		if (Strings.isNullOrBlank(value) == false) {
			
			if (value.matches("-?[0-9]+")) {
				
				returnValue = value;
				
			} else {
				
				returnValue = "invalid";
				
			}
			
		}

		return returnValue;
	}

	@Override
	public String marshal(String value) throws Exception {
		
		String returnValue = "";
		
		if (value != null) {
			
			returnValue = value;
			
		}
		
		return returnValue;
	}

}
