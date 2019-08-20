package com.elitecore.elitesm.ws.rest.adapter.prioritytable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry;

/**
 * 
 * Diameter Session Adapter do conversion of Diameter Session, Diameter Session Name to Diameter Session Id and vice versa. <br>
 * It takes Diameter Session Name as input and give Diameter Session Id as output in unmarshal. <br>
 * It takes Diameter Session Id as input and give Diameter Session Name as output in marshal. <br>
 * For invalid values it gives null.
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <diameter-session>New</diameter-session>
 * 
 * }
 * 
 * than output is :
 * 1
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class DiameterSessionAdapter extends XmlAdapter<String, Integer> {
	
	@Override
	public Integer unmarshal(String value) throws Exception {

		Integer sessionName = null;

		if (PriorityEntry.DiameterSessionTypes.fromType(value) != null) {
			sessionName = PriorityEntry.DiameterSessionTypes.fromType(value).val;
		}

		return sessionName;
	}

	@Override
	public String marshal(Integer value) throws Exception {

		String sessionId = null;

		if (PriorityEntry.DiameterSessionTypes.fromVal(value) != null) {
			sessionId = PriorityEntry.DiameterSessionTypes.fromVal(value).type;
		}

		return sessionId;
	}

}
