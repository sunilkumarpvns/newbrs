package com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.elitesm.datamanager.diameter.routingconf.data.FailureAction;

/**
 * 
 * Failure Action Adapter do conversion of Failure Action, Failure Action Name to Failure Action Id and vice versa. <br>
 * It takes Failure Action Name as input and give Failure Action Id as output in unmarshal. <br>
 * It takes Failure Action Id as input and give Failure Action Name as output in marshal. <br>
 * For invalid values it gives null.
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <failure-action>Passthrough</failure-action>
 * 
 * }
 * 
 * than output is :
 * 4
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class FailureActionAdapter extends XmlAdapter<String, Short> {

	@Override
	public Short unmarshal(String name) throws Exception {

		Short failureActionId = null;

		if (FailureAction.idFromName(name) != null) {
			failureActionId = FailureAction.idFromName(name);
		}

		return failureActionId;
	}

	@Override
	public String marshal(Short id) throws Exception {

		String failureActionName = null;

		if (FailureAction.nameFromId(id) != null) {
			failureActionName = FailureAction.nameFromId(id);
		}

		return failureActionName;
	}

}
