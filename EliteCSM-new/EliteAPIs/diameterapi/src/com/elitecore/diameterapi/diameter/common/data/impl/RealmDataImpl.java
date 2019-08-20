package com.elitecore.diameterapi.diameter.common.data.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.data.DiameterFailoverConfiguration;
import com.elitecore.diameterapi.diameter.common.data.RealmData;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

@XmlType(propOrder={})
public class RealmDataImpl implements RealmData {
	
	private String destRealm;	
	private Set<ApplicationEnum> localApplicationIds;
	private String strLocalApplicationIds;


	private RoutingActions routingAction;	
	private long transActionTimeOut =3000;
	private short statefulRouting = STATEFUL_ROUTING_ENABLED;
	protected List<PeerGroupImpl> peerGroups;
	private LogicalExpression advancedCondition = null;
	private String advancedConditionStr;
	private String originatorHostIp;
	private String transMapName;
	private String routingName;
	private String originRealm;
	private int routingActionInt;
	private int hashCode;
	private List<DiameterFailoverConfigurationImpl> failoverDataList;
	private Map<Integer, DiameterFailoverConfiguration> errorCodeMap;
	private boolean attachedRedirection;
	
	public RealmDataImpl(String destRealm, RoutingActions routingAction, Set<ApplicationEnum> localApplications,List<PeerGroupImpl> peerGroups) {
		this.localApplicationIds = new HashSet<ApplicationEnum>();
		this.routingAction =  RoutingActions.PROXY;
		this.advancedConditionStr = null;
		this.originatorHostIp = null;
		this.transMapName = "";
		this.destRealm="*";
		this.originRealm = "*";
		if(destRealm!=null)
			this.destRealm = destRealm;
		if(localApplications!=null)
			localApplicationIds = localApplications;
		if(routingAction!=null)
			this.routingAction = routingAction;
		if (peerGroups != null){
			this.peerGroups = peerGroups;
		} else {
			this.peerGroups = new ArrayList<PeerGroupImpl>();
		}
		this.errorCodeMap = new HashMap<Integer, DiameterFailoverConfiguration>();
		this.failoverDataList = new ArrayList<DiameterFailoverConfigurationImpl>();
	}
	
	public RealmDataImpl() {
	}
	
	public void setDestRealm(String destRealm) {
		this.destRealm = destRealm;
	}

	public void setAttachedRedirection(boolean attachedRedirection) {
		this.attachedRedirection = attachedRedirection;
	}
	
	@Override
	@XmlElement(name="attached-redirection",type=boolean.class)
	public boolean getAttachedRedirection() {
		return attachedRedirection;
	}
	public void setApplications(
			Set<ApplicationEnum> localApplicationIds) {
		this.localApplicationIds = localApplicationIds;
	}

	public void setPeerGroupList(List<PeerGroupImpl> peerGroups) {
		this.peerGroups = peerGroups;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public void setErrorCodeMap(
			Map<Integer, DiameterFailoverConfiguration> errorCodeMap) {
		this.errorCodeMap = errorCodeMap;
	}


	public void setRoutinAction(RoutingActions localAction){
		this.routingAction = localAction;
	}

	@XmlTransient
	public Set<ApplicationEnum> getApplications() {
		return localApplicationIds;
	}

	public void setStatefulRouting(short statefullRouting){
		this.statefulRouting = statefullRouting;
	}
	
	@Override
	@XmlElement(name="destination-realm",type=String.class)
	public String getDestRealm() {
		return destRealm;
	}

	@Override
	@XmlTransient
	public RoutingActions getRoutinAction() {
		return routingAction;
	}

	@XmlElementWrapper(name="peer-groups")
	@XmlElement(name="peer-group")
	public List<PeerGroupImpl> getPeerGroupList() {
		return peerGroups;
	}

	public void setAdvancedCondition(LogicalExpression logicalExpression) {
		this.advancedCondition = logicalExpression;
	}

	public void setRoutingName(String routingName){
		this.routingName=routingName;
	}
	@XmlElement(name="name",type=String.class)
	public String getRoutingName(){
		return routingName;
	}
	public void setOriginatorHostIp(String originHostIp) {
		this.originatorHostIp = originHostIp;
	}
	
	@XmlTransient
	public LogicalExpression getAdvancedCondition() {
		return advancedCondition;
	}

	@XmlElement(name ="origin-host",type =String.class)
	public String getOriginatorHostIp() {
		return originatorHostIp;
	}
	@XmlElement(name ="ruleset",type =String.class)
	public String getAdvancedConditionStr() {
		return advancedConditionStr;
	}
	public void setAdvancedConditionStr(String advancedConditionStr){

		this.advancedConditionStr = advancedConditionStr;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null){
			RealmData realmData = (RealmData) obj;
			if (realmData.getRoutingName().equals(this.routingName)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		if (hashCode == 0) {
			hashCode = (destRealm + advancedConditionStr).hashCode();
		}
		return hashCode;
	}
	
	public String toString(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("\tRoutingConfiguration Name      ="  + routingName);
		out.println("\tDestination Realm              = " + destRealm);
		out.println("\tOrigin Host-Ip                 = " + originatorHostIp);
		out.println("\tOrigin Realm                   = " + originRealm);
		out.println("\tTransaction Timeout            = " + transActionTimeOut);
		out.println("\tStateful Routing               = " + (statefulRouting==1?"Enabled":"Disabled"));
		out.println("\tAttached Redirection           = " + (attachedRedirection==true?"Enabled":"Disabled"));
		if(transMapName.length() > 0)
			out.println("\tTranslation Mapping Name   = " + transMapName);
		else
			out.println("\tTranslation Mapping Not Configured");
		out.println("\tAdvanced Condition             = " + (advancedCondition != null? advancedConditionStr : "*"));
		out.println("\tRouting Action                 = " + routingAction);
		Iterator<ApplicationEnum> iter  = localApplicationIds.iterator();
		while(iter.hasNext()){
			out.println("\tApplications Ids           = " +iter.next().toString());
		}
		int failoverListSize = failoverDataList.size();
		for(int i=0 ; i<failoverListSize ; i++){
			out.println("\t" + failoverDataList.get(i).toString());
		}
		int peerGroupLen =  peerGroups.size();
		for(int i = 0 ;i <peerGroupLen ; i++){
			out.print("\t\tDiameter Peer Group:\t" + peerGroups.get(i).toString());
		}	return stringWriter.toString();
	}

	@XmlElement(name="translation-mapping",type=String.class)
	public String getTransMapName() {
		return transMapName;
	}
	public void setTransMapName(String transMapName){
		this.transMapName = transMapName;
	}

	@Override
	@XmlElement(name="transaction-time-out",type=long.class,defaultValue="3000")
	public long getTransActionTimeOut() {
		return transActionTimeOut;
	}	
	public void setTransActionTimeOut(long transActionTimeOut){
		this.transActionTimeOut = transActionTimeOut;
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
	@XmlTransient
	public Map<Integer, DiameterFailoverConfiguration> getFailoverDataMap() {
		return errorCodeMap;
	}

	public void setErrorCodeMap(HashMap<Integer,DiameterFailoverConfiguration> errorCodeMap) {
		if(errorCodeMap != null)
			this.errorCodeMap = errorCodeMap;
	}

	@Override
	@XmlElement(name="stateful-routing",type=short.class,defaultValue="1")
	public short getStatefulRouting() {
		return this.statefulRouting;
	}
	
	public void setOriginRealm(String realm){
		this.originRealm = realm;
	}
	
	@Override
	@XmlElement(name="origin-realm",type=String.class)
	public String getOriginRealm() {
		return this.originRealm;
	}
	@XmlElement(name="routing-action",type=int.class,defaultValue="0")
	public int getRoutingActionInt() {
		return routingActionInt;
	}

	public void setRoutingActionInt(int routingActionInt) {
		this.routingActionInt = routingActionInt;
	}
	
	@XmlElement(name="application-ids",type=String.class)
	public String getStrLocalApplicationIds() {
		return strLocalApplicationIds;
	}

	public void setStrLocalApplicationIds(String strLocalApplicationIds) {
		this.strLocalApplicationIds = strLocalApplicationIds;
	}
	
}
