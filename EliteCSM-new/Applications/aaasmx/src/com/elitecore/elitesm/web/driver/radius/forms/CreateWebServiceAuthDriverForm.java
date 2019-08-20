package com.elitecore.elitesm.web.driver.radius.forms;



import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyMapRelData;
import com.elitecore.elitesm.util.constants.AccountFieldConstants;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

@SuppressWarnings("serial")
public class CreateWebServiceAuthDriverForm extends BaseWebForm{
	
	private long wsAuthDriverId;	
	private long driverInstanceId;
	private String serviceAddress;
	private String action;
	private String imsiAttribute="0:1";
	///request Parameters
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	private String logicalName;
	private String wsMethodKey;
	private String defaultValue;
	private String valueMapping;
	private Long maxQueryTimeoutCnt=50L;
	private Long statusChkDuration=120L;
	private List<LogicalNameValuePoolData> logicalNameList;
	private List<WebMethodKeyData> webMethodKeyList;
	private String userIdentityAttributes;

    List<String> logicalNameMultipleAllowList = new ArrayList<String>(0);

	

	
	public Long getMaxQueryTimeoutCnt() {
		return maxQueryTimeoutCnt;
	}
	public void setMaxQueryTimeoutCnt(Long maxQueryTimeoutCnt) {
		this.maxQueryTimeoutCnt = maxQueryTimeoutCnt;
	}
	public Long getStatusChkDuration() {
		return statusChkDuration;
	}
	public void setStatusChkDuration(Long statusChkDuration) {
		this.statusChkDuration = statusChkDuration;
	}
	public long getWsAuthDriverId() {
		return wsAuthDriverId;
	}
	public void setWsAuthDriverId(long wsAuthDriverId) {
		this.wsAuthDriverId = wsAuthDriverId;
	}
	public long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverDesp() {
		return driverDesp;
	}
	public void setDriverDesp(String driverDesp) {
		this.driverDesp = driverDesp;
	}
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getWsMethodKey() {
		return wsMethodKey;
	}
	public void setWsMethodKey(String wsMethodKey) {
		this.wsMethodKey = wsMethodKey;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	public String getImsiAttribute() {
		return imsiAttribute;
	}
	public void setImsiAttribute(String imsiAttribute) {
		this.imsiAttribute = imsiAttribute;
	}
	public List<LogicalNameValuePoolData> getLogicalNameList() {
		return logicalNameList;
	}
	public void setLogicalNameList(List<LogicalNameValuePoolData> logicalNameList) {
		this.logicalNameList = logicalNameList;
	}
	public List<WebMethodKeyData> getWebMethodKeyList() {
		return webMethodKeyList;
	}
	public void setWebMethodKeyList(List<WebMethodKeyData> webMethodKeyList) {
		this.webMethodKeyList = webMethodKeyList;
	}

	public List<String> getLogicalNameMultipleAllowList() {
		return logicalNameMultipleAllowList;
	}
	public void setLogicalNameMultipleAllowList(
			List<String> logicalNameMultipleAllowList) {
		this.logicalNameMultipleAllowList = logicalNameMultipleAllowList;
	}

	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}

	public List<WebMethodKeyMapRelData> getDefaultmapping(){
		List<WebMethodKeyMapRelData> defaultMappingList = new ArrayList<WebMethodKeyMapRelData>();
		String[] logicalnm = {AccountFieldConstants.MSISDN,
				AccountFieldConstants.IMSI,
				AccountFieldConstants.IMEI,
				AccountFieldConstants.CREDIT_LIMIT,
				AccountFieldConstants.PASSWORD_CHECK,
				AccountFieldConstants.USER_PASSWORD,
				AccountFieldConstants.PARAM1,
				AccountFieldConstants.CUSTOMER_STATUS,
				AccountFieldConstants.CUSTOMER_TYPE };
		String[] wsMethodKeys = {AccountFieldConstants.MSISDN,
				AccountFieldConstants.IMSI,
				AccountFieldConstants.IMEI,
				AccountFieldConstants.CREDIT_LIMIT,
				AccountFieldConstants.PASSWORD_CHECK,
				AccountFieldConstants.USER_PASSWORD,
				AccountFieldConstants.CUSTOMER_SERVICES,
				AccountFieldConstants.CUSTOMER_STATUS,
				AccountFieldConstants.CUSTOMER_TYPE};
		String[] logicalv = {AccountFieldConstants.MSISDN,
				AccountFieldConstants.IMSI,
				AccountFieldConstants.IMEI,
				AccountFieldConstants.CREDIT_LIMIT,
				AccountFieldConstants.PASSWORD_CHECK,
				AccountFieldConstants.USER_PASSWORD,
				AccountFieldConstants.PARAM1,
				AccountFieldConstants.CUSTOMER_STATUS,
				AccountFieldConstants.CUSTOMER_TYPE };
		String [] defaultValues = {"","","","1","no","test","","","prepaid"};
		String [] valueMappings = {"","","","","","","","","GSM Prepaid=prepaid,GSM Postpaid=postpaid"};
		for(int index = 0 ; index < logicalnm.length ; index++){
			WebMethodKeyMapRelData webMethodKeyMapRelData = new WebMethodKeyMapRelData();
			LogicalNameValuePoolData nameValuePoolData = new LogicalNameValuePoolData();
			nameValuePoolData.setName(logicalnm[index]);
			nameValuePoolData.setValue(logicalv[index]);
			webMethodKeyMapRelData.setNameValuePoolData(nameValuePoolData);
			webMethodKeyMapRelData.setWebMethodKey(wsMethodKeys[index]);
			webMethodKeyMapRelData.setDefaultValue(defaultValues[index]);
			webMethodKeyMapRelData.setValueMapping(valueMappings[index]);
			defaultMappingList.add(webMethodKeyMapRelData);
		}
		return defaultMappingList;
	}	
	
}
