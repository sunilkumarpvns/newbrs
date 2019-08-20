package com.elitecore.elitesm.ws.rest.adapter.prioritytable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.diameterapi.core.common.transport.priority.Priority;

/**
 * 
 * Priority Adapter do conversion of Priority, Priority Name to Priority Id and vice versa. <br>
 * It takes Priority Name as input and give Priority Id as output in unmarshal. <br>
 * It takes Priority Id as input and give Priority Name as output in marshal. <br>
 * For invalid values it gives null.
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <priority>Medium</priority>
 * 
 * }
 * 
 * than output is :
 * 2
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class PriorityAdapter extends XmlAdapter<String, Integer> {

	@Override
	public Integer unmarshal(String value) throws Exception {

		Integer priorityName = null;

		if (Priority.fromPriority(value) != null) {
			priorityName = Priority.fromPriority(value).val;
		}

		return priorityName;
	}

	@Override
	public String marshal(Integer value) throws Exception {

		String priorityId = null;

		if (Priority.fromPriority(value) != null) {
			priorityId = Priority.fromPriority(value).priority;
		}

		return priorityId;
	}

}
