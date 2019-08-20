package com.elitecore.aaa.radius.policies.servicepolicy.conf;

import java.util.List;

import com.elitecore.aaa.core.data.DBFieldDetail;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.impl.DynAuthServicePolicyConfigurationData.DBFailureAction;
import com.elitecore.aaa.radius.service.dynauth.handlers.conf.StaticNasCommunicationHandlerData;

public interface DynAuthServicePolicyConfiguration {
	
	public String getRadiusRuleSet();

	public String getPolicyName();
	public String getPolicyId();
	
	public String getDataSourceName();
	
	public int getEligibleSession() ;

	public List<DBFieldDetail> getDbFieldDetailList() ;
	
	public String getConcurrentDBQuery() ;
	
	public long getEventTimestampValue() ;
	
	public boolean getIsValidatePacket();

	public String getTableName() ;
	public String getResponseAttributeStr() ;

	public StaticNasCommunicationHandlerData getCommunicatorData();

	/**
	 * 
	 * @return default behavior to apply in case of database failure 
	 */
	DBFailureAction getDbFailureAction();
}
