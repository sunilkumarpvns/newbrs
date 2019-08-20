package com.elitecore.diameterapi.diameter.common.data;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.routerx.SubscriberBasedRoutingTableData;

/**
 * 
 * This interface contains schematic structure 
 * of the methods for Configuration Data of Routing Entry.
 * 
 * @author monica.lulla
 *
 */
public interface RoutingEntryData {
	
	
	/**
	 * @return Routing Entry Name
	 * 
	 */
	public String getRoutingName();
	
	/**
	 * 
	 * @return Destination Realm served by routing entry
	 */
	public String getDestRealm();
	
	/**
	 * 
	 * @return String of Application Ids configured in Routing Entry
	 */
	public String getApplicationIds();
	
	/**
	 * 
	 * @return Origin IP
	 */
	public String getOriginHostIp();
	
	/**
	 * 
	 * @return Origin Realm
	 */
	public String getOriginRealm();
	
	/**
	 * 
	 * @return Ruleset String for Routing entry
	 */
	public String getAdvancedCondition();
	
	/**
	 * 
	 * @return Translation Mapping Name
	 */
	public String getTransMapName();
	
	/**
	 * Local 	--> 0<br />
	 * Relay 	--> 1<br />
	 * Proxy 	--> 2<br />
	 * Redirect	--> 3
	 * 
	 * @return int value of Routing Action
	 */
	public int getRoutingAction();
	
	/**
	 * This method states that Routing to be done in Stateful or Stateless fashion<br />
	 * Stateful Routing Enabled  --> true<br />
	 * Stateful Routing Disabled --> false
	 * 
	 * @return true if Stateful routing enabled else false
	 */
	public boolean getStatefulRouting();
	
	/**
	 * This method states that Redirect Host MUST be Alive or not, 
	 * while sending Redirect Indication
	 * AttachedRedirection Enabled  --> true<br />
	 * AttachedRedirection Disabled --> false
	 * 
	 * @return 
	 */
	public boolean getAttachedRedirection();
	
	/**
	 * Time out for a Request transaction, 
	 * 
	 * This method is useful in failure cases, 
	 * where we want to forward Origin Request to another Peer and 
	 * wants to check whether Request transaction has Timed out or not 
	 * 
	 * @return Transaction Time Out  in milliseconds
	 */
	public long getTransActionTimeOut();
	
	/**
	 * 
	 * @return List of Peer Groups configured.
	 */
	public List<PeerGroupImpl> getPeerGroupList();
	
	/**
	 * 
	 * @return list of Subscriber Routing Table Names
	 */
	public ArrayList<String> getSubscriberRoutingTableNames();
	
	/**
	 * 
	 * @return List of Failure Configurations.
	 */
	public List<DiameterFailoverConfigurationImpl> getFailoverDataList();

	public ArrayList<SubscriberBasedRoutingTableData> getSubscriberBasedRoutingTableDataList();

}

