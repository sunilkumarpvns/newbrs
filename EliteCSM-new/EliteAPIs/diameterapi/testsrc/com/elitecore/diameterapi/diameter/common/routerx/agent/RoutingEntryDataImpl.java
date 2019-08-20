package com.elitecore.diameterapi.diameter.common.routerx.agent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.data.impl.DiameterFailoverConfigurationImpl;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerGroupImpl;
import com.elitecore.diameterapi.diameter.common.routerx.SubscriberBasedRoutingTableData;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

/**
 * It is a Configuration Class that holds 
 * properties and methods for Routing Entry Configuration.
 * 
 * @author monica.lulla
 *
 */

@XmlType(propOrder={})
public class RoutingEntryDataImpl implements RoutingEntryData {
	
	private String routingName;
	private String destRealm;	
	private String applicationIds;
	private String originHostIp;
	private String originRealm;
	private String advancedConditionStr;
	private String transMapName;
	private int routingAction = RoutingActions.PROXY.routingAction;
	private boolean statefulRouting = true;
	private boolean attachedRedirection;
	private long transactionTimeOut = 3000;	
	private List<PeerGroupImpl> peerGroupsList;
	private List<DiameterFailoverConfigurationImpl> failoverDataList;
	private int hashCode;

	public RoutingEntryDataImpl(String destRealm, RoutingActions routingAction, String applications,List<PeerGroupImpl> peerGroups) {
		this.destRealm = destRealm;
		this.routingAction = routingAction.routingAction;
		this.applicationIds = applications;
		this.peerGroupsList = peerGroups;
	}
	
	public RoutingEntryDataImpl() {
	}

	@Override
	@XmlElement(name="name",type=String.class)
	public String getRoutingName(){
		return routingName;
	}
	
	public void setRoutingName(String routingName){
		this.routingName=routingName;
	}
	
	@Override
	@XmlElement(name="destination-realm",type=String.class)
	public String getDestRealm() {
		return destRealm;
	}
	
	public void setDestRealm(String destRealm) {
		this.destRealm = destRealm;
	}
	
	@Override
	@XmlElement(name="application-ids",type=String.class)
	public String getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(String applicationIds) {
		this.applicationIds = applicationIds;
	}
	
	@Override
	@XmlElement(name ="origin-host",type =String.class)
	public String getOriginHostIp() {
		return originHostIp;
	}
	
	public void setOriginHostIp(String originHostIp) {
		this.originHostIp = originHostIp;
	}
	
	@Override
	@XmlElement(name="origin-realm",type=String.class)
	public String getOriginRealm() {
		return this.originRealm;
	}
	
	public void setOriginRealm(String realm){
		this.originRealm = realm;
	}

	@Override
	@XmlElement(name ="ruleset",type =String.class)
	public String getAdvancedCondition() {
		return advancedConditionStr;
	}
	
	public void setAdvancedCondition(String advancedConditionStr){
		this.advancedConditionStr = advancedConditionStr;
	}
	
	@Override
	@XmlElement(name="translation-mapping",type=String.class)
	public String getTransMapName() {
		return transMapName;
	}
	
	public void setTransMapName(String transMapName){
		this.transMapName = transMapName;
	}
	
	@Override
	@XmlElement(name="routing-action",type=int.class,defaultValue="2")
	public int getRoutingAction() {
		return routingAction;
	}

	public void setRoutingAction(int routingActionInt) {
		this.routingAction = routingActionInt;
	}
	
	@Override
	@XmlElement(name="stateful-routing",type=boolean.class,defaultValue="true")
	public boolean getStatefulRouting() {
		return this.statefulRouting;
	}
	
	public void setStatefulRouting(boolean statefullRouting){
		this.statefulRouting = statefullRouting;
	}
	
	@Override
	@XmlElement(name="attached-redirection",type=boolean.class)
	public boolean getAttachedRedirection() {
		return attachedRedirection;
	}
	
	public void setAttachedRedirection(boolean attachedRedirection) {
		this.attachedRedirection = attachedRedirection;
	}
	
	@Override
	@XmlElement(name="transaction-time-out",type=long.class,defaultValue="3000")
	public long getTransActionTimeOut() {
		return transactionTimeOut;
	}	
	
	public void setTransActionTimeOut(long transActionTimeOut){
		this.transactionTimeOut = transActionTimeOut;
	}

	@Override
	@XmlElementWrapper(name="peer-groups")
	@XmlElement(name="peer-group")
	public List<PeerGroupImpl> getPeerGroupList() {
		return peerGroupsList;
	}
	
	public void setPeerGroupList(List<PeerGroupImpl> peerGroupsList) {
		this.peerGroupsList = peerGroupsList;
	}

	/**
	 * Setter and Getter for the Diameter FailOver Map.
	 */

	@Override
	@XmlElementWrapper(name="failure-actions")
	@XmlElement(name="failure-action")
	public List<DiameterFailoverConfigurationImpl> getFailoverDataList() {
		return failoverDataList;
	}

	public void setFailoverDataList(List<DiameterFailoverConfigurationImpl> failoverDataList) {
		if(failoverDataList != null)
			this.failoverDataList = failoverDataList;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null){
			RoutingEntryData routingEntryData = (RoutingEntryData) obj;
			if (routingEntryData.getRoutingName().equals(this.routingName)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		if (hashCode == 0) {
			hashCode = routingName.hashCode();
		}
		return hashCode;
	}
	
	@Override
	public String toString(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("\tRouting Entry Name		      = " + routingName);
		out.println("\tDestination Realm              = " + destRealm);
		out.println("\tOrigin Host-Ip                 = " + originHostIp);
		out.println("\tOrigin Realm                   = " + originRealm);
		out.println("\tTransaction Timeout            = " + transactionTimeOut);
		out.println("\tStateful Routing               = " + (statefulRouting==true?"Enabled":"Disabled"));
		out.println("\tAttached Redirection           = " + (attachedRedirection==true?"Enabled":"Disabled"));
		if(transMapName != null && transMapName.length() > 0)
			out.println("\tTranslation Mapping Name   = " + transMapName);
		else
			out.println("\tTranslation Mapping Not Configured");
		out.println("\tAdvanced Condition             = " + (advancedConditionStr != null? advancedConditionStr : "*"));
		out.println("\tRouting Action                 = " + RoutingActions.getActionString(routingAction));
		out.println("\tApplication IDs				  = " + applicationIds);
		int failoverListSize = failoverDataList.size();
		for(int i=0 ; i<failoverListSize ; i++){
			out.println("\t" + failoverDataList.get(i).toString());
		}
		int peerGroupLen =  peerGroupsList.size();
		for(int i = 0 ;i < peerGroupLen ; i++){
			out.print("\t\tDiameter Peer Group:\t" + peerGroupsList.get(i).toString());
		}	
		return stringWriter.toString();
	}

	@Override
	public ArrayList<String> getSubscriberRoutingTableNames() {
		return null;
	}

	@Override
	public ArrayList<SubscriberBasedRoutingTableData> getSubscriberBasedRoutingTableDataList() {
		return null;
	}
}

