package com.elitecore.diameterapi.diameter.common.data.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.diameterapi.diameter.common.data.PeerGroup;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

@XmlType(propOrder={"peerList", "advancedConditionStr"})
public class PeerGroupImpl implements PeerGroup {

	private LogicalExpression ruleSet = null;
	private List<PeerInfoImpl> peerInfoList;
	private String advancedConditionStr = "";
	
	public PeerGroupImpl(){
		peerInfoList = new ArrayList<PeerInfoImpl>();
	}
	public void setPeerAdvancedCondition(LogicalExpression ruleSet){
		this.ruleSet = ruleSet;
	}
	public void setPeerInfoList(List<PeerInfoImpl> peerInfoList){
		if(peerInfoList!= null)
			this.peerInfoList = peerInfoList;
	}
	
	@XmlTransient
	public LogicalExpression getRuleSet() {
		return ruleSet;
	}
	@XmlElement(name="peer")
	public List<PeerInfoImpl> getPeerList() {
		return peerInfoList;
	}
	@XmlElement(name="ruleset",type=String.class)
	public String getAdvancedConditionStr() {
		return advancedConditionStr;
	}
	public void setAdvancedConditionStr(String advancedConditionStr){
		this.advancedConditionStr = advancedConditionStr;
	}
	public String toString(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println("\n\t\t\tAdvanced Condition = " + ((advancedConditionStr != null && advancedConditionStr.trim().length() >0)? advancedConditionStr : "*"));
		if(peerInfoList.size() == 0){
			out.println("\t\t\tNo Peer is Defined For this  Peer Group.");
		} else {
			out.println("\t\t\tPeers: "+ peerInfoList);
		}
		return stringWriter.toString();
	}
}
