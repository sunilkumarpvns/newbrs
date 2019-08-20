package com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.datamanager.diameter.routingconf.data.RoutingAction;

/**
 * 
 * Routing Action Adapter do conversion of Routing Action, Routing Action Name to Routing Action Id and vice versa. <br>
 * It takes Routing Action Name as input and give Routing Action Id as output in unmarshal. <br>
 * It takes Routing Action Id as input and give Routing Action Name as output in marshal. <br>
 * For invalid values it gives null.
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <routing-action>Proxy</routing-action>
 * 
 * }
 * 
 * than output is :
 * 3
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class RoutingActionAdapter extends XmlAdapter<String, Long> {

	@Override
	public Long unmarshal(String name) throws Exception {

		Long routingActionId = null;

		if (RoutingAction.idFromName(name) != null) {
			routingActionId = RoutingAction.idFromName(name);
		}

		return routingActionId;
	}

	@Override
	public String marshal(Long id) throws Exception {

		String routingActionName = null;

		if (RoutingAction.nameFromId(id) != null) {
			routingActionName = RoutingAction.nameFromId(id);
		}

		return routingActionName;
	}

}
