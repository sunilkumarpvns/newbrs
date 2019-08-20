package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverFieldMapData;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateRadiusHttpAuthDriverForm extends BaseWebForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<LogicalNameValuePoolData> logicalNameList;
	private long maxQueryTimeoutCount = 200;
	private Long httpAuthDriverId;
	private Long statusCheckDuration=120L;
	private Long driverInstanceId;
	private String http_url;
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	private Set httpFeildMapSet;
	private String logicalName;
	private Long responseParamIndex;
	private String action;
	private String expiryDateFormats="MM/dd/yyyy";
	private String defaultValue;
	private String valueMapping;
	private String userIdentityAttributes;
	List<String> logicalNameMultipleAllowList = new ArrayList<String>(0);

	

	
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

	public String getExpiryDateFormats() {
		return expiryDateFormats;
	}

	public void setExpiryDateFormats(String expiryDateFormats) {
		this.expiryDateFormats = expiryDateFormats;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getLogicalName() {
		return logicalName;
	}

	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	public Long getResponseParamIndex() {
		return responseParamIndex;
	}

	public void setResponseParamIndex(Long responseParamIndex) {
		this.responseParamIndex = responseParamIndex;
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

	public List<LogicalNameValuePoolData> getLogicalNameList() {
		return logicalNameList;
	}
	
	public void setLogicalNameList(List<LogicalNameValuePoolData> logicalNameList) {
		this.logicalNameList = logicalNameList;
	}
	
	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	
	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	
	public Long getHttpAuthDriverId() {
		return httpAuthDriverId;
	}
	
	public void setHttpAuthDriverId(Long httpAuthDriverId) {
		this.httpAuthDriverId = httpAuthDriverId;
	}
	
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	public String getHttp_url() {
		return http_url;
	}
	
	public void setHttp_url(String httpUrl) {
		http_url = httpUrl;
	}
	
	public Set getHttpFeildMapSet() {
		return httpFeildMapSet;
	}
	
	public void setHttpFeildMapSet(Set httpFeildMapSet) {
		this.httpFeildMapSet = httpFeildMapSet;
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
	
	public List<HttpAuthDriverFieldMapData> getDefaultmapping(){
		List<HttpAuthDriverFieldMapData> defaultMappingList = new ArrayList<HttpAuthDriverFieldMapData>();
		String[] logicalnm = {"User Name","CUI","User Password","Password Check","Encryption Type","Customer Status","Calling Station ID"
                  		      ,"Authorization Policy","Customer Check Items","Customer Reply Items","Expiry Date","Credit Limit"};
		String[] responseParameterIndex = {"1","2","3","4","5","6","7","8"
                		    ,"9","10","11","12"};
		String[] logicalv = {"User-Name","CUI","User-Password","PasswordCheck","EncryptionType","CustomerStatus","Calling-Station-ID","AuthorizationPolicy"
        					,"CustomerCheckItems","CustomerReplyItems","ExpiryDate","CreditLimit"};
		for(int index = 0 ; index < logicalnm.length ; index++){
			HttpAuthDriverFieldMapData httpAuthDriverFieldMapData = new HttpAuthDriverFieldMapData();
			LogicalNameValuePoolData nameValuePoolData = new LogicalNameValuePoolData();
			nameValuePoolData.setName(logicalnm[index]);
			nameValuePoolData.setValue(logicalv[index]);
			httpAuthDriverFieldMapData.setNameValuePoolData(nameValuePoolData);
			httpAuthDriverFieldMapData.setResponseParamIndex(Long.parseLong(responseParameterIndex[index]));
			//httpAuthDriverFieldMapData.setHttpAuthFieldMapId(Long.parseLong(responseParameterIndex[index]));
			defaultMappingList.add(httpAuthDriverFieldMapData);
		}
		return defaultMappingList;
	}

}
