package com.elitecore.aaa.radius.policies.servicepolicy.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.data.DBFieldDetail;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.DynAuthServicePolicyConfiguration;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.impl.DynAuthServicePolicyConfigurationData.DBFailureAction.DBFailureActionAdapter;
import com.elitecore.aaa.radius.service.dynauth.handlers.DynAuthHandler;
import com.elitecore.aaa.radius.service.dynauth.handlers.conf.StaticNasCommunicationHandlerData;
import com.elitecore.core.commons.config.core.CaseInsensitiveEnumAdapter;

@XmlType(propOrder={})
public class DynAuthServicePolicyConfigurationData implements DynAuthServicePolicyConfiguration {

	private int eligibleSession = DynAuthHandler.RECENT;
	private String policyId;
	private String radiusRuleSet;	
	private String policyName;
	private String dataSourceName;
	private String responseAttributeStr;
	private String tableName;
	private long eventTimestampValue=60;

	private boolean bValidatePacket;
	
	private String concurrentDBQuery;
	private List<DBFieldDetail> mappingList;
	private StaticNasCommunicationHandlerData data;

	private DBFailureAction dbFailureAction = DBFailureAction.DEFAULT_ACTION; 
	
	public DynAuthServicePolicyConfigurationData() {
		this.mappingList = new ArrayList<DBFieldDetail>();
		data = new StaticNasCommunicationHandlerData(); 
	}
	
	@XmlElement(name = "ruleset",type = String.class)
	public String getRadiusRuleSet() {
		return radiusRuleSet;
	}
	
	public void setRadiusRuleSet(String radiusRuleSet) {
		this.radiusRuleSet = radiusRuleSet;
	}

	@XmlElement(name = "name",type = String.class,required = true,nillable = false)
	public String getPolicyName() {
		return policyName;
	}
	
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	@XmlElement(name = "datasource-name",type = String.class)
	@Override
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	@Override
	@XmlElement(name="response-attributes",type=String.class)
	public String getResponseAttributeStr() {
		return responseAttributeStr;
	}

	public void setResponseAttributeStr(String responseAttributeStr) {
		this.responseAttributeStr = responseAttributeStr;
	}

	@XmlElement(name="eligible-session", type = int.class)
	public int getEligibleSession() {
		return eligibleSession;
	}
	
	public void setEligibleSession(int eligibleSession) {
		this.eligibleSession = eligibleSession;
	}
	
	@Override
	@XmlTransient
	public String getConcurrentDBQuery() {
		return concurrentDBQuery;
	}
	
	public void setConcurrentDBQuery(String concurrentDBQuery) {
		this.concurrentDBQuery = concurrentDBQuery;
	}

	@Override
	@XmlElement(name="event-timestamp",type=long.class,defaultValue="60")
	public long getEventTimestampValue() {
		return eventTimestampValue * 1000;
	}
	
	public void setEventTimestampValue(long eventTimestampValue) {
		this.eventTimestampValue = eventTimestampValue;
	}

	@Override
	@XmlElement(name="validate-packet",type=boolean.class)
	public boolean getIsValidatePacket() {
		return bValidatePacket;
	}
	public void setIsValidatePacket(boolean bValidatePacket) {
		this.bValidatePacket = bValidatePacket;
	}

	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- RAD DynAuth Service Policy Configuration -- ");
		out.println();	
		out.println("	Service Policy Name = " + policyName);
		out.println("	Rule Set            = " + radiusRuleSet);
		out.println("	Packet Validation   = " + bValidatePacket);
		out.println("	Datasource Name     = " + dataSourceName);
		out.println();
		out.close();
		return stringBuffer.toString();
	}

	@Override
	@XmlElementWrapper(name="dynaAuth-field-mapping-details")
	@XmlElement(name="mapping")
	public List<DBFieldDetail> getDbFieldDetailList() {
		return mappingList;
	}
	
	public void setDbFieldDetailList(List<DBFieldDetail> mappingList) {
		this.mappingList = mappingList;
	}
	@Override
	@XmlElement(name="table-name",type=String.class,defaultValue="TBLMCONCURRENTUSERS")
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	@XmlElement(name = "id",type = String.class)
	public String getPolicyId() {
		return policyId;
	}
	
	public void setPolicyId(String policyId) {
		 this.policyId =policyId;
	}

	@Override
	@XmlElement(name = "nas-communication-handler")
	public StaticNasCommunicationHandlerData getCommunicatorData() {
		data.setPolicyName(policyName);
		return data;
	}

	public void setCommunicatorData(StaticNasCommunicationHandlerData data) {
		this.data = data;
	}
	
	@Override
	@XmlJavaTypeAdapter(value = DBFailureActionAdapter.class)
	@XmlElement(name = "db-failure-action", type = String.class)
	public DBFailureAction getDbFailureAction() {
		return dbFailureAction;
	}
	public void setDbFailureAction(DBFailureAction dbFailureBehavior) {
		this.dbFailureAction = dbFailureBehavior;
	}
	
	public enum DBFailureAction {

		NAK,
		DROP;
		
		public static final DBFailureAction DEFAULT_ACTION = NAK;
		
		public static DBFailureAction from(String dbFailureActionString) {
			
			for (DBFailureAction dbFailureAction : values()) {
				if (dbFailureAction.name().equalsIgnoreCase(dbFailureActionString)) {
					return dbFailureAction;
				}
			}
			return DEFAULT_ACTION;
		}
		
		/** This adapter will make case-insensitive conversion from String --> DBFailureAction **/
		public static class DBFailureActionAdapter extends CaseInsensitiveEnumAdapter<DBFailureAction> {

			public DBFailureActionAdapter() {
				super(DBFailureAction.class, DEFAULT_ACTION);
			}
		}
	}
}