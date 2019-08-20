package com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
 * Stateful Routing Adapter do conversion of Stateful Routing, Stateful Routing Name to Stateful Routing Id and vice versa. <br>
 * It takes Stateful Routing Name as input and give Stateful Routing Id as output in unmarshal. <br>
 * It takes Stateful Routing Id as input and give Stateful Routing Name as output in marshal. <br>
 * For invalid values it gives null.
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <stateful-routing>Enabled</stateful-routing>
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

public class StatefulRoutingAdapter extends XmlAdapter<String, Long> {

	@Override
	public Long unmarshal(String name) throws Exception {

		Long statefulRoutingId = null;

		if (name != null) {
			if ("Enabled".equalsIgnoreCase(name)) {
				statefulRoutingId = 1L;
			} else if ("Disabled".equalsIgnoreCase(name)) {
				statefulRoutingId = 0L;
			} else {
				statefulRoutingId = null;
			}
		}

		return statefulRoutingId;
	}

	@Override
	public String marshal(Long id) throws Exception {

		String statefulRoutingName = null;

		if (id != null) {
			if (1 == id) {
				statefulRoutingName = "Enabled";
			} else if (0 == id) {
				statefulRoutingName = "Disabled";
			} else {
				statefulRoutingName = null;
			}
		}

		return statefulRoutingName;
	}

}
