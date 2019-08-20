package com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;

/**
 * 
 * Routing Table Name To Id Adapter do conversion of Routing Table Name to Routing Table Id and vice versa. <br>
 * It takes Routing Table Name as input and gives Routing Table Id as output in unmarshal. <br>
 * It takes Routing Table Id as input and gives Routing Table Name as output in marshal. <br>
 * For invalid value of input, it gives null. 
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <routing-table-name>routing_table</routing-table-name>
 * 
 * }
 * 
 * than output is :
 * 101
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */

public class RoutingTableNameToIdAdapter extends XmlAdapter<String, String> {
	
	DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();

	@Override
	public String unmarshal(String routingTableName) throws Exception {
		
		String routingTableId = null;
		
		if (Strings.isNullOrBlank(routingTableName) == false) {
			try {
				routingTableId = diameterRoutingTableBLManager.getDiameterRoutingTableByName(routingTableName).getRoutingTableId();
			} catch (Exception e) {
				routingTableId = null;
			}
		}
		
		return routingTableId;
	}

	@Override
	public String marshal(String routingTableId) throws Exception {
		
		String routingTableName;
		routingTableName = diameterRoutingTableBLManager.getDiameterRoutingTableById(routingTableId).getRoutingTableName();
		return routingTableName;
		
	}
	
}
