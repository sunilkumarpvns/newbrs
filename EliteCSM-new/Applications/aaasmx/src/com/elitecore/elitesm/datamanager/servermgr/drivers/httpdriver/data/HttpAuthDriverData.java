package com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data;

import java.io.Serializable;
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
import org.springframework.security.core.context.SecurityContextHolder;

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
import com.elitecore.elitesm.ws.rest.security.AuthenticationDetails;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "http-auth-driver")
@XmlType(propOrder = { "http_url", "statusCheckDuration", "maxQueryTimeoutCount", "expiryDateFormat", "userIdentityAttributes", "httpFieldMapList" })
@ValidObject
public class HttpAuthDriverData extends BaseData implements Differentiable,Serializable,Validator{
	private static final long serialVersionUID = 1L;
	private String httpAuthDriverId;
	private String driverInstanceId;
	
	@Expose
	@SerializedName("Http Url")
	@NotEmpty(message = "Http Url Name must be specified.")
	private String http_url;
	
	@Expose
	@SerializedName("Status Check Duration")
	@NotNull(message = "Status Check Duration must be specified and it should be numeric.")
	private Long statusCheckDuration;

	@Expose
	@SerializedName("Maximum Query Timeout Count")
	@NotNull(message = "Max query timeout count must be specified and it should be numeric.")
	private Long maxQueryTimeoutCount;

	@Expose
	@SerializedName("Expiry Date Formats")
	@NotEmpty(message = "Expiry Date Formats must be specified.")
	private String expiryDateFormat;

	@Expose
	@SerializedName("User Identity Attributes")
	private String userIdentityAttributes;
	
	private List<HttpAuthDriverFieldMapData> httpFieldMapList;
	private boolean checkValidate;
	
	@XmlElement(name = "expiry-date-formats")
	public String getExpiryDateFormat() {
		return expiryDateFormat;
	}

	public void setExpiryDateFormat(String expiryDateFormat) {
		this.expiryDateFormat = expiryDateFormat;
	}

	@XmlTransient
	public String getHttpAuthDriverId() {
		return httpAuthDriverId;
	}
	
	public void setHttpAuthDriverId(String httpAuthDriverId) {
		this.httpAuthDriverId = httpAuthDriverId;
	}
	
	@XmlElement(name = "status-check-duration")
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	
	@XmlElement(name="maximum-query-timeout-count")
	public Long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElement(name = "http-url")
	public String getHttp_url() {
		return http_url;
	}
	
	public void setHttp_url(String httpUrl) {
		http_url = httpUrl;
	}
	
	@Valid
	@NotEmpty(message ="At least one mapping must be specified.")
	@XmlElementWrapper(name = "http-response-mappings")
	@XmlElement(name = "http-response-mapping")
	public List<HttpAuthDriverFieldMapData> getHttpFieldMapList() {
		return httpFieldMapList;
	}

	public void setHttpFieldMapList(List<HttpAuthDriverFieldMapData> httpFieldMapList) {
		this.httpFieldMapList = httpFieldMapList;
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
		object.put("Http Url", http_url);
		object.put("Status Check Duration (Sec.)", statusCheckDuration);
		object.put("Maximum Query Timeout Count", maxQueryTimeoutCount);
		object.put("Expiry Date Formats", expiryDateFormat);
		object.put("User Identity Attributes", userIdentityAttributes);
		if(Collectionz.isNullOrEmpty(httpFieldMapList) == false){
			JSONObject fields = new JSONObject();
			for (HttpAuthDriverFieldMapData element : httpFieldMapList) {
				fields.putAll(element.toJson());
			}
			object.put("Http Response Mapping", fields);
		}
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		List<LogicalNameValuePoolData> multipleLogicalNameRelList = null;
		try {
			if(Collectionz.isNullOrEmpty(httpFieldMapList) == false && checkValidate == false){
				if(SecurityContextHolder.getContext().getAuthentication() != null) {
					Object obj = SecurityContextHolder.getContext().getAuthentication().getDetails();
					AuthenticationDetails customAuthenticationDetails =null;
					DriverBLManager driverBLManager = new DriverBLManager();
					
					List<String> logicalNameMultipleAllowList =null;
					
					if(obj instanceof AuthenticationDetails){
						customAuthenticationDetails = (AuthenticationDetails) obj;
					}
					if(customAuthenticationDetails.isDiameter()){
						logicalNameMultipleAllowList = driverBLManager.getLogicalNameDriverRelList(DriverTypeConstants.DIAMETER_HTTP_AUTH_FORWARD);
						multipleLogicalNameRelList = driverBLManager.getMultipleLogicalNameRelList(DriverTypeConstants.DIAMETER_HTTP_AUTH_FORWARD);
					}else{
						logicalNameMultipleAllowList = driverBLManager.getLogicalNameDriverRelList(DriverTypeConstants.RADIUS_HTTP_AUTH_DRIVER);
						multipleLogicalNameRelList = driverBLManager.getMultipleLogicalNameRelList(DriverTypeConstants.RADIUS_HTTP_AUTH_DRIVER);
					}
					
					List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();
					List<String> uniqueLogicalNameList = DriverUtility.getUniqueLogicalMultipleAllowList(multipleLogicalNameRelList, logicalNameList);
					
					Map<String,String> validateMultipleAllowMap = new HashMap<String, String>();
					Set<String> uniqueLogicalNameSet = new HashSet<String>();
					
					StringBuilder duplicateLogicalNames = new StringBuilder();
					StringBuilder invalidLogicalNames = new StringBuilder();
					StringBuilder combinationLogicalName =new StringBuilder();
					
					for(HttpAuthDriverFieldMapData gatewayFieldMapData : httpFieldMapList) {
						String logicalName = gatewayFieldMapData.getLogicalName();
						String responseParamIndex = Strings.valueOf(gatewayFieldMapData.getResponseParamIndex());
						if(Strings.isNullOrEmpty(logicalName) == false && Strings.isNullOrBlank(responseParamIndex) ==false){
							
							ResultObject resObj = FieldWithLogicalNameValidateUtility.checkFieldWithLogicalNameValidate(validateMultipleAllowMap, uniqueLogicalNameSet,uniqueLogicalNameList, logicalNameMultipleAllowList,
									duplicateLogicalNames, invalidLogicalNames, logicalName.trim(), responseParamIndex.trim(), context);
							
							if(resObj.isError()){
								combinationLogicalName.append(resObj.getErrorMsg() + ", ");
							}
						}
					}
					DriverResult driverResult = new DriverResult();
					List<HttpAuthDriverFieldMapData> convertedList = getConversionOfValueWithLogicalName(context, httpFieldMapList,driverResult, logicalNameList);
					
					if(driverResult.isError()){
						isValid = false;
					}
					
					if(Collectionz.isNullOrEmpty(convertedList) == false) {
						httpFieldMapList.clear();
						httpFieldMapList.addAll(convertedList);
					}
					
					if(Strings.isNullOrEmpty(duplicateLogicalNames.toString()) == false ){
						duplicateLogicalNames.insert(0, "Duplicate Logical Name(s) : ");
						duplicateLogicalNames.setLength(duplicateLogicalNames.length() - 2);
						isValid = false;
						RestUtitlity.setValidationMessage(context,duplicateLogicalNames.toString());
					}
					if(Strings.isNullOrEmpty(invalidLogicalNames.toString()) == false){
						invalidLogicalNames.insert(0, "Invalid Logical Name(s) : ");
						invalidLogicalNames.setLength(invalidLogicalNames.length() - 2);
						isValid = false;
						RestUtitlity.setValidationMessage(context,invalidLogicalNames.toString());
					}
					
					if(Strings.isNullOrEmpty(combinationLogicalName.toString()) == false){
						isValid = false;
						RestUtitlity.setValidationMessage(context, combinationLogicalName.toString());
					}
				}
				}
		}catch (DataManagerException e) {
			e.printStackTrace();
		} catch(Exception e){
		e.printStackTrace();
		}
		
		return isValid;
	}
	private List<HttpAuthDriverFieldMapData> getConversionOfValueWithLogicalName(ConstraintValidatorContext context, List<HttpAuthDriverFieldMapData> httpFieldMapList, DriverResult driverResult, List<LogicalNameValuePoolData> logicalNameList) {

		List<HttpAuthDriverFieldMapData> convertedList = new ArrayList<HttpAuthDriverFieldMapData>();
		for(HttpAuthDriverFieldMapData httpAuthDriverFieldMapData : httpFieldMapList ){

			Long responseParamIndex = httpAuthDriverFieldMapData.getResponseParamIndex();
			String logicalName = httpAuthDriverFieldMapData.getLogicalName();
			String defaultValue = httpAuthDriverFieldMapData.getDefaultValue();
			String valueMapping = httpAuthDriverFieldMapData.getValueMapping();
			LogicalNameValuePoolData nameValuePoolData = httpAuthDriverFieldMapData.getNameValuePoolData();
			String httpAuthDrvId = httpAuthDriverFieldMapData.getHttpAuthDriverId();
			String httpAuthFieldMapId = httpAuthDriverFieldMapData.getHttpAuthFieldMapId();
			
			if (Strings.isNullOrEmpty(logicalName) && responseParamIndex == null) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name and response-parameter-index must be specified.");
				driverResult.setError(true);
			} else if (responseParamIndex == null && Strings.isNullOrEmpty(logicalName) == false) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the response-parameter-index must be specified for logical-name:[" + logicalName + "].");
				driverResult.setError(true);

			} else if (Strings.isNullOrEmpty(logicalName) && responseParamIndex != null) {
				RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name must be specified for response-parameter-index:[" + responseParamIndex + "].");
				driverResult.setError(true);
			}

			if(driverResult.isError() == false ) {
				HttpAuthDriverFieldMapData httpAuthDriverData = new HttpAuthDriverFieldMapData();
				for(LogicalNameValuePoolData data: logicalNameList) {
					if(data.getName().equals(logicalName)) {
						httpAuthDriverData.setLogicalName(data.getValue());
						httpAuthDriverData.setDefaultValue(defaultValue);
						httpAuthDriverData.setHttpAuthDriverId(httpAuthDrvId);
						httpAuthDriverData.setHttpAuthFieldMapId(httpAuthFieldMapId);
						httpAuthDriverData.setNameValuePoolData(nameValuePoolData);
						httpAuthDriverData.setResponseParamIndex(responseParamIndex);
						httpAuthDriverData.setValueMapping(valueMapping);
						convertedList.add(httpAuthDriverData);
						break;
					}
				}
			}
			
		}
		return convertedList;
	}
}