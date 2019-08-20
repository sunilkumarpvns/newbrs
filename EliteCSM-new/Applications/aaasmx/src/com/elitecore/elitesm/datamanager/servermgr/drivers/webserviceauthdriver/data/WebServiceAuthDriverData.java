package com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverResult;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.util.DriverUtility;
import com.elitecore.elitesm.util.FieldWithLogicalNameValidateUtility;
import com.elitecore.elitesm.util.ResultObject;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlType(propOrder = { "serviceAddress", "maxQueryTimeoutCnt", "statusChkDuration", "imsiAttribute", "userIdentityAttributes", "webMethodKeyDataList"})
@XmlRootElement(name = "web-service-auth-driver")
@ValidObject
public class WebServiceAuthDriverData extends BaseData implements Differentiable,Validator{

	private String wsAuthDriverId;	
	private String driverInstanceId;
	
	@Expose
	@SerializedName("Service Address")
	@NotEmpty(message = "Service Address must be specified.")
	private String serviceAddress;
	
	@Expose
	@SerializedName("Maximum Query Timeout Count(Sec.)")
	@NotNull(message = "Max Query Timeout Count must be specified and it should be numeric.")
	private Long maxQueryTimeoutCnt;
	
	@Expose
	@SerializedName("Status Check Duration(Sec.)")
	@NotNull(message = "Status Check Duration must be specified and it should be numeric.")
	private Long statusChkDuration;
	
	@NotEmpty(message = "IMSI Attribute must be specified.")
	private String imsiAttribute;
	
	@Expose
	@SerializedName("User Identity Attributes")
	private String userIdentityAttributes;
	@Valid
	@NotEmpty(message = "At least one mapping must be specified.")
	private List<WebMethodKeyMapRelData> webMethodKeyDataList;
	private boolean checkValidate;
	
	@XmlElement(name = "maximum-query-timeout-count")
	public Long getMaxQueryTimeoutCnt() {
		return maxQueryTimeoutCnt;
	}
	public void setMaxQueryTimeoutCnt(Long maxQueryTimeoutCnt) {
		this.maxQueryTimeoutCnt = maxQueryTimeoutCnt;
	}
	
	@XmlElement(name = "status-check-duration")
	public Long getStatusChkDuration() {
		return statusChkDuration;
	}
	public void setStatusChkDuration(Long statusChkDuration) {
		this.statusChkDuration = statusChkDuration;
	}
	
	@XmlTransient
	public String getWsAuthDriverId() {
		return wsAuthDriverId;
	}
	public void setWsAuthDriverId(String wsAuthDriverId) {
		this.wsAuthDriverId = wsAuthDriverId;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElement(name = "service-address")
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	
	@XmlElement(name = "imsi-attribute")
	public String getImsiAttribute() {
		return imsiAttribute;
	}
	public void setImsiAttribute(String imsiAttribute) {
		this.imsiAttribute = imsiAttribute;
	}
	
	@XmlElementWrapper(name = "web-method-key-mappings")
	@XmlElement(name = "web-method-key-mapping")
	public List<WebMethodKeyMapRelData> getWebMethodKeyDataList() {
		return webMethodKeyDataList;
	}
	public void setWebMethodKeyDataList(List<WebMethodKeyMapRelData> webMethodKeyDataList) {
		this.webMethodKeyDataList = webMethodKeyDataList;
	}
	
	@XmlElement(name = "user-identity-attributes")
	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}
	
	@XmlTransient
	public boolean isCheckValidate() {
		return checkValidate;
	}
	public void setCheckValidate(boolean checkValidate) {
		this.checkValidate = checkValidate;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Service Address", serviceAddress);
		object.put("IMSI Attribute", imsiAttribute);
		object.put("Maximum Query Timeout Count(Sec.)", maxQueryTimeoutCnt);
		object.put("Status Check Duration(Sec.)", statusChkDuration);
		object.put("User Identity Attributes", userIdentityAttributes);
		if(Collectionz.isNullOrEmpty(webMethodKeyDataList) == false){
			JSONObject fields = new JSONObject();
			for (WebMethodKeyMapRelData element : webMethodKeyDataList) {
				fields.putAll(element.toJson());
			}
			object.put("Webservice Method Key Mapping", fields);
		}
		return object;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		List<LogicalNameValuePoolData> multipleLogicalNameRelList;
		List<String> logicalNameValues = new ArrayList<String>();
		List<String> webMethodKeyList = new ArrayList<String>();
		
		if(Collectionz.isNullOrEmpty(webMethodKeyDataList) == false  && checkValidate == false){
			
			StringBuilder duplicateLogicalNames = new StringBuilder();
			StringBuilder invalidLogicalNames = new StringBuilder();
			StringBuilder combinationLogicalName =new StringBuilder();
			
			DriverBLManager driverBLManager = new DriverBLManager();
			try {
				List<String> logicalNameMultipleAllowList = driverBLManager.getLogicalNameDriverRelList(DriverTypeConstants.RADIUS_MAPGATEWAY_AUTH_DRIVER);
				multipleLogicalNameRelList = driverBLManager.getMultipleLogicalNameRelList(DriverTypeConstants.RADIUS_MAPGATEWAY_AUTH_DRIVER);
				List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();
				List<String> uniqueLogicalNameList = DriverUtility.getUniqueLogicalMultipleAllowList(multipleLogicalNameRelList, logicalNameList);
				
				for (LogicalNameValuePoolData nameValuePool : logicalNameList) {
					 logicalNameValues.add(nameValuePool.getName());
				}
				
				for (LogicalNameValuePoolData nameValuePool : logicalNameList) {
					webMethodKeyList.add(nameValuePool.getValue());
				}
				
				Map<String,String> validateMultipleAllowMap = new HashMap<String, String>();
				Set<String> uniqueLogicalNameSet = new HashSet<String>();
				
				for(WebMethodKeyMapRelData gatewayFieldMapData : webMethodKeyDataList) {
					String logicalName = gatewayFieldMapData.getLogicalName();
					String webMethodKey = gatewayFieldMapData.getWebMethodKey();
					
					if(Strings.isNullOrEmpty(logicalName) == false && Strings.isNullOrEmpty(webMethodKey) == false){
						ResultObject resObj = FieldWithLogicalNameValidateUtility.checkFieldWithLogicalNameValidate(validateMultipleAllowMap, uniqueLogicalNameSet,uniqueLogicalNameList, logicalNameMultipleAllowList,
								duplicateLogicalNames, invalidLogicalNames, logicalName.trim(), webMethodKey.trim(), context);
						
						if(resObj.isError()){
							combinationLogicalName.append(resObj.getErrorMsg() + ", ");
						}
					}
				}

				DriverResult driverResult = new DriverResult();
				List<WebMethodKeyMapRelData> convertedList = getConversionOfValueWithLogicalName(context, webMethodKeyDataList,driverResult, logicalNameList);
				
				if(driverResult.isError()){
					isValid = false;
				}

				if(Strings.isNullOrEmpty(invalidLogicalNames.toString()) == false){
					for(WebMethodKeyMapRelData webMethodKeyMapRelData : webMethodKeyDataList ){
						String logicalName = webMethodKeyMapRelData.getLogicalName();
						String webMethodKey = webMethodKeyMapRelData.getWebMethodKey();

						if(logicalNameMultipleAllowList.contains(logicalName) == false){
							if(logicalNameValues.contains(logicalName) == false || webMethodKeyList.contains(webMethodKey) == false){
								if(logicalNameValues.contains(logicalName) == false){
									logicalName = "";
								}
								
								if(webMethodKeyList.contains(webMethodKey) == false){
									webMethodKey = "";
								}
								
								if (Strings.isNullOrBlank(logicalName) && Strings.isNullOrBlank(webMethodKey)) {
									RestUtitlity.setValidationMessage(context, "In the Mapping List, invalid logical-name and web-method-key is specified.");
									isValid = false;
								} else if (Strings.isNullOrBlank(webMethodKey) && Strings.isNullOrBlank(logicalName) == false) {
									RestUtitlity.setValidationMessage(context, "In the Mapping List, invalid web-method-key specified for logical-name:[" + logicalName + "].");
									isValid = false;
								} else if (Strings.isNullOrBlank(logicalName) && Strings.isNullOrBlank(webMethodKey) == false) {
									RestUtitlity.setValidationMessage(context, "In the Mapping List, invalid logical-name specified for web-method-key:[" + webMethodKey + "].");
									isValid = false;
								}
							}
						}
					}
				}
				
				if(Collectionz.isNullOrEmpty(convertedList) == false) {
					webMethodKeyDataList.clear();
					webMethodKeyDataList.addAll(convertedList);
				}
				
				if(Strings.isNullOrEmpty(duplicateLogicalNames.toString()) == false ){
					duplicateLogicalNames.insert(0, "Duplicate Logical Name(s) : ");
					duplicateLogicalNames.setLength(duplicateLogicalNames.length() - 2);
					isValid = false;
					RestUtitlity.setValidationMessage(context,duplicateLogicalNames.toString());
				}

				
				if(Strings.isNullOrEmpty(combinationLogicalName.toString()) == false){
					isValid = false;
					RestUtitlity.setValidationMessage(context, combinationLogicalName.toString());
				}
				return isValid;
			} catch (DataManagerException e) {
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		return isValid;
	}
	
private List<WebMethodKeyMapRelData> getConversionOfValueWithLogicalName(ConstraintValidatorContext context, List<WebMethodKeyMapRelData> webMethodKeyDataList, DriverResult driverResult, List<LogicalNameValuePoolData> logicalNameList) {
		
		List<WebMethodKeyMapRelData> convertedList = new ArrayList<WebMethodKeyMapRelData>();
		
		for(WebMethodKeyMapRelData webMethodKeyMapRelData : webMethodKeyDataList ){
			 String logicalName = webMethodKeyMapRelData.getLogicalName();
			 String webMethodKey = webMethodKeyMapRelData.getWebMethodKey();
			 
				 if (Strings.isNullOrBlank(logicalName) && Strings.isNullOrBlank(webMethodKey)) {
					 RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name and web-method-key must be specified.");
					 driverResult.setError(true);
				 } else if (Strings.isNullOrBlank(webMethodKey) && Strings.isNullOrBlank(logicalName) == false) {
					 RestUtitlity.setValidationMessage(context, "In the Mapping List, the web-method-key must be specified for logical-name:[" + logicalName + "].");
					 driverResult.setError(true);
				 } else if (Strings.isNullOrBlank(logicalName) && Strings.isNullOrBlank(webMethodKey) == false) {
					 RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name must be specified for web-method-key:[" + webMethodKey + "].");
					 driverResult.setError(true);
				 }
				if(driverResult.isError() == false) {
					WebMethodKeyMapRelData webMethodKeyData = new WebMethodKeyMapRelData();
					for(LogicalNameValuePoolData data: logicalNameList) {
						if(data.getName().equals(webMethodKeyMapRelData.getLogicalName())) {
							webMethodKeyData.setLogicalName(data.getValue());
							webMethodKeyData.setDefaultValue(webMethodKeyMapRelData.getDefaultValue());
							webMethodKeyData.setNameValuePoolData(webMethodKeyMapRelData.getNameValuePoolData());
							webMethodKeyData.setValueMapping(webMethodKeyMapRelData.getValueMapping());
							webMethodKeyData.setKeyMapId(webMethodKeyMapRelData.getKeyMapId());
							webMethodKeyData.setWebMethodKey(webMethodKeyMapRelData.getWebMethodKey());
							webMethodKeyData.setWsAuthDriverId(webMethodKeyMapRelData.getWsAuthDriverId());
							
							convertedList.add(webMethodKeyData);
							break;
						}
					}
				}
		}
		return convertedList;
	}
}
