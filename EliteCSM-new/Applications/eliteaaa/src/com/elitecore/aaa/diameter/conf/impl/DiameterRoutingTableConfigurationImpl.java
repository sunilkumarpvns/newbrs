package com.elitecore.aaa.diameter.conf.impl;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.diameter.conf.DiameterRoutingTableConfiguration;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

@XmlType(propOrder={})
public class DiameterRoutingTableConfigurationImpl implements DiameterRoutingTableConfiguration{
	
	private RealmEntries realmEntries;
	private String tableName = "";
	private OverloadAction overloadAction;
	private int resultCode;
	private String scriptName;
	
	@Deprecated
	public DiameterRoutingTableConfigurationImpl(ServerContext serverContext) {
		this.realmEntries = new RealmEntries();
		overloadAction = OverloadAction.DROP;
		resultCode = ResultCode.DIAMETER_TOO_BUSY.code;
	}
	public DiameterRoutingTableConfigurationImpl() {
		this.realmEntries = new RealmEntries();
		overloadAction = OverloadAction.DROP;
		resultCode = ResultCode.DIAMETER_TOO_BUSY.code;
	}
	

	@XmlElement(name="routing-entries")
	public RealmEntries getRealmEntries() {
		return realmEntries;
	}

	public void setRealmEntries(RealmEntries realmEntries) {
		this.realmEntries = realmEntries;
	}

	@XmlElement(name="table-name",type=String.class,defaultValue="")
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	@XmlTransient
	public List<RoutingEntryDataImpl> getRoutingEntryDataList() {
		return realmEntries.getRealmEntryList();
	}

	@XmlElement(name="result-code")
	public int getResultCode() {
		return resultCode;
	}
	
	@XmlElement(name="script")
	public String getScriptName() {
		return scriptName;
	}
	
	public void setScriptName(String scriptInstance) {
		this.scriptName = scriptInstance;
	}
	
	public void setResultCode(int resultCode) {
		
		/*
		 * RFC 6733
		 * Result code starts from 1001 so only Result code greater than 1000 is allowed.
		 * Default Result code is : 3004 (set in constructor)
		 */
		if(resultCode > 1000){
			this.resultCode = resultCode;
		}
	}
	
	@XmlElement(name="overload-action",type=OverloadAction.class)
	public OverloadAction getOverloadAction() {
		return overloadAction;
	}
	
	public void setOverloadAction(OverloadAction overloadAction) {
		if(overloadAction != null){
			this.overloadAction = overloadAction;
		}
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.println();
		out.println(" ----Diameter Routing Table Configuration----");
		out.println("\t Table Name 		: " + tableName);
		out.println("\t Overload Action : " + overloadAction.val);
		out.println("\t Result Code 	: " + resultCode);
		out.println(" ----Diameter Routing Entry Configuration----");
		List<RoutingEntryDataImpl> routingEntryDataList = getRoutingEntryDataList();
		int routingListSize = routingEntryDataList.size();
		for(int i=0 ; i<routingListSize ; i++){
			out.println("\t" + routingEntryDataList.get(i).toString());
		}
		return stringBuffer.toString();
	}
	
}
