package com.elitecore.elitesm.ws.rest.adapter.diameterpeers;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;

/**
 * 
 * Retransmission Count Adapter do conversion, String to Long and vice versa. <br>
 * It takes String as input and give Long as output in unmarshal. <br>
 * It takes Long as input and give String as output in marshal. <br>
 * For invalid values it gives null. <br>
 * For empty string input, it gives 0 default value in output. 
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <retransmission-count>a12b</retransmission-count>
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

public class RetransmissionCountAdapter extends XmlAdapter<String, Long>  {

	@Override
	public Long unmarshal(String value) throws Exception {

		Long returnValue = 0L;

		if (Strings.isNullOrBlank(value) == false) {
			
			if (value.matches("-?[0-9]+")) {
				
				returnValue = Long.parseLong(value);
				
			} else {
				
				returnValue = null;
				
			}
			
		}

		return returnValue;
		
	}

	@Override
	public String marshal(Long value) throws Exception {
		
		return String.valueOf(value);
		
	}

}
