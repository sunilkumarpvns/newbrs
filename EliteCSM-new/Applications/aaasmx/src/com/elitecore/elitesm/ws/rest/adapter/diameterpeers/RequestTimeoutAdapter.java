package com.elitecore.elitesm.ws.rest.adapter.diameterpeers;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

/**
 * 
 * Request Timeout Adapter do conversion, String to Long and vice versa. <br>
 * It takes String as input and give Long as output in unmarshal. <br>
 * It takes Long as input and give String as output in marshal. <br>
 * For invalid values it gives null. <br>
 * For empty string input, it gives null. <br>
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <request-timeout>a12b</request-timeout>
 * 
 * }
 * 
 * than output is :
 * null
 * 
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class RequestTimeoutAdapter extends XmlAdapter<String, Long>  {

	@Override
	public Long unmarshal(String value) throws Exception {

		Long returnValue = null;

		if (Strings.isNullOrBlank(value) == false) {
			
			if (value.matches("-?[0-9]+")) {
				
				returnValue = Long.parseLong(value);
				
			}
			
		}

		return returnValue;
		
	}

	@Override
	public String marshal(Long value) throws Exception {
		
		return String.valueOf(value);
		
	}

}
