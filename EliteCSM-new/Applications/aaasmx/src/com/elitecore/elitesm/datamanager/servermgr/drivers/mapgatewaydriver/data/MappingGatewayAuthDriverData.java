package com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.aaa.util.constants.RadiusMapGWAuthDriverConstant;
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
import com.elitecore.elitesm.ws.rest.adapter.FieldTrimmerAdapter;
import com.elitecore.elitesm.ws.rest.security.AuthenticationDetails;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlType(propOrder={"localHostId","localHostPort","localHostIp","remoteHostId","remoteHostPort","remoteHostIp","maxQueryTimeoutCount","mapGwConnPoolSize",
		"requestTimeout","statusCheckDuration","userIdentityAttributes","sendAuthInfo","numberOfTriplets","gatewayFieldList"})
@ValidObject
public class MappingGatewayAuthDriverData extends BaseData implements Differentiable,Validator{
	private String mapGWAuthid;
	private String driverInstanceId;
	
	@Expose
	@SerializedName("Local Host Id")
	@NotEmpty(message = "Local Host Id must be specified.")
	private String localHostId;
	
	@Expose
	@SerializedName("Local Host Port")
	@NotNull(message = "Local Host Port must be specified and it should be numeric.")
	@Min(value = 0, message = " Local Host Port must be positive")
	private Integer localHostPort;
	
	@Expose
	@SerializedName("Local Host Ip")
	@NotEmpty(message = "Local Host Ip must be specified")
	private String localHostIp;
	
	@Expose
	@SerializedName("Remote Host Id")
	@NotEmpty(message = "Remote Host Id must be specified.")
	private String remoteHostId;
	
	@Expose
	@SerializedName("Remote Host Port")
	@NotNull(message = "Remote Host Port must be specified and it should be numeric.")
	@Min(value = 0, message = "Remote Host Port must be positive")
	private Integer remoteHostPort;
	
	@Expose
	@SerializedName("Remote Host Ip")
	@NotEmpty(message = "Remote Host Ip must be specified.")
	private String remoteHostIp;

	@Expose
	@SerializedName("Maximum Query Timeout Count")
	@NotNull(message = "Max Query Timeout must be specified and it should be numeric.")
	@Min(value = 0, message = "Max Query timeout count must be positive")
	private Long maxQueryTimeoutCount;

	@Expose
	@SerializedName("Connection Pool Size")
	@NotNull(message = "Connection Pool Size must be specified and it should be numeric.")
	@Min(value = 0, message = "Connection Pool Size must be positive")
	private Integer mapGwConnPoolSize;

	@Expose
	@SerializedName("Request Timeout(ms)")
	@NotNull(message = "Request Timeout must be specified and it should be numeric.")
	@Min(value = 0, message = "Request Timeout must be positive")
	private Integer requestTimeout;

	@Expose
	@SerializedName("Status Check Duration(sec)")
	@NotNull(message = "Status Check Duration must be specified and it should be numeric.")
	@Min(value = 0, message = "Status Check Duration must be positive")
	private Integer statusCheckDuration;

	@Expose
	@SerializedName("User Identity Attributes")
	private String userIdentityAttributes;
	
	@Expose
	@SerializedName("Send Auth Info")
	@Pattern(regexp = "(?i)(True|False)", message = "Invalid value for Send Auth Info Record. Value could be 'True' or 'False'.")
	private String sendAuthInfo;
	
	@Expose
	@SerializedName("Number Of Triplets")
	private String numberOfTriplets;
	@Valid
	@NotEmpty(message = "At least one mapping must be specified")
	private List<GatewayFieldMapData> gatewayFieldList;
	private boolean checkValidate;
	
	public MappingGatewayAuthDriverData() {
		 this.numberOfTriplets = RadiusMapGWAuthDriverConstant.NUMBER_OF_TRIPLET;
		 this.sendAuthInfo = RadiusMapGWAuthDriverConstant.SEND_AUTH_INFO;
	}
	
	@XmlElement(name = "connection-pool-size")
	public Integer getMapGwConnPoolSize() {
		return mapGwConnPoolSize;
	}
	public void setMapGwConnPoolSize(Integer mapGwConnPoolSize) {
		this.mapGwConnPoolSize = mapGwConnPoolSize;
	}
	
	@XmlElement(name = "request-timeout")
	public Integer getRequestTimeout() {
		return requestTimeout;
	}
	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
	
	@XmlElement(name = "status-check-duration")
	public Integer getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Integer statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	
	@XmlElement(name = "maximum-query-timeout-count")
	public Long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}

	@XmlElementWrapper(name = "map-profile-fields")
	@XmlElement(name = "map-profile-field")
	public List<GatewayFieldMapData> getGatewayFieldList() {
		return gatewayFieldList;
	}
	public void setGatewayFieldList(List<GatewayFieldMapData> gatewayFieldList) {
		this.gatewayFieldList = gatewayFieldList;
	}
	
	@XmlTransient
	public String getMapGWAuthid() {
		return mapGWAuthid;
	}
	public void setMapGWAuthid(String mapGWAuthid) {
		this.mapGWAuthid = mapGWAuthid;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElement(name = "local-host-id")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getLocalHostId() {
		return localHostId;
	}
	public void setLocalHostId(String localHostId) {
		this.localHostId = localHostId;
	}
	
	@XmlElement(name = "local-host-port")
	public Integer getLocalHostPort() {
		return localHostPort;
	}
	public void setLocalHostPort(Integer localHostPort) {
		this.localHostPort = localHostPort;
	}
	
	@XmlElement(name = "local-host-ip")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getLocalHostIp() {
		return localHostIp;
	}
	public void setLocalHostIp(String localHostIp) {
		this.localHostIp = localHostIp;
	}
	
	@XmlElement(name = "remote-host-id")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getRemoteHostId() {
		return remoteHostId;
	}
	public void setRemoteHostId(String remoteHostId) {
		this.remoteHostId = remoteHostId;
	}
	
	@XmlElement(name = "remote-host-port")
	public Integer getRemoteHostPort() {
		return remoteHostPort;
	}
	public void setRemoteHostPort(Integer remoteHostPort) {
		this.remoteHostPort = remoteHostPort;
	}
	
	@XmlElement(name = "remote-host-ip")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getRemoteHostIp() {
		return remoteHostIp;
	}
	public void setRemoteHostIp(String remoteHostIp) {
		this.remoteHostIp = remoteHostIp;
	}
	
	@XmlElement(name = "user-identity-attributes")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}
	
	@XmlElement(name = "send-auth-info")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getSendAuthInfo() {
		return sendAuthInfo;
	}
	public void setSendAuthInfo(String sendAuthInfo) {
		this.sendAuthInfo = sendAuthInfo;
	}
	
	@XmlElement(name = "number-of-triplets")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getNumberOfTriplets() {
		return numberOfTriplets;
	}
	public void setNumberOfTriplets(String numberOfTriplets) {
		this.numberOfTriplets = numberOfTriplets;
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
		object.put("Local Host Id", localHostId);
		object.put("Local Host Ip", localHostIp);
		object.put("Local Host Port", localHostPort);
		object.put("Remote Host Id", remoteHostId);
		object.put("Remote Host Ip", remoteHostIp);
		object.put("Remote Host Port", remoteHostPort);
		object.put("Maximum Query Timeout Count", maxQueryTimeoutCount);
		object.put("Connection Pool Size", mapGwConnPoolSize);
		object.put("Request Timeout(ms)", requestTimeout);
		object.put("Status Check Duration(sec)", statusCheckDuration);
		object.put("User Identity Attributes", userIdentityAttributes);
		object.put("Send Auth Info", sendAuthInfo);
		object.put("Number Of Triplets", numberOfTriplets);
		if(Collectionz.isNullOrEmpty(gatewayFieldList) == false){
			JSONObject fields = new JSONObject();
			for (GatewayFieldMapData element : gatewayFieldList) {
				fields.putAll(element.toJson());
			}
			object.put("MAP Profile Field ", fields);
		}
		return object;
	}
	
	/**
	*Contains code that will check 
	*weather the logical name does exist and unique (except in Multiple allowed logical name) with default logical name.
	*It also validate if the combination of multiple allowed logical name and Profile field value twice then response with proper message.
	*@author Tejas.p.Shah
	*/
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		List<LogicalNameValuePoolData> multipleLogicalNameRelList = null;
		if(Collectionz.isNullOrEmpty(gatewayFieldList) == false && checkValidate == false){
			AuthenticationDetails customAuthenticationDetails =null;
			List<String> logicalNameMultipleAllowList =null;
				DriverBLManager driverBLManager = new DriverBLManager();
				
			try {
				
				if(SecurityContextHolder.getContext().getAuthentication() != null) {
					Object obj = SecurityContextHolder.getContext().getAuthentication().getDetails();
					if(obj instanceof AuthenticationDetails){
						customAuthenticationDetails = (AuthenticationDetails) obj;
					}
					if(customAuthenticationDetails.isDiameter()){
						logicalNameMultipleAllowList = driverBLManager.getLogicalNameDriverRelList(DriverTypeConstants.DIAMETER_MAP_GWAUTH_FORWARD);
						multipleLogicalNameRelList = driverBLManager.getMultipleLogicalNameRelList(DriverTypeConstants.DIAMETER_MAP_GWAUTH_FORWARD);
					}else{
						multipleLogicalNameRelList = driverBLManager.getMultipleLogicalNameRelList(DriverTypeConstants.RADIUS_MAPGATEWAY_AUTH_DRIVER);
						logicalNameMultipleAllowList = driverBLManager.getLogicalValueDriverRelList(DriverTypeConstants.RADIUS_MAPGATEWAY_AUTH_DRIVER);
					}
					
					List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();
					List<String> uniqueLogicalNameList = DriverUtility.getUniqueLogicalMultipleAllowList(multipleLogicalNameRelList, logicalNameList);
					
					Map<String,String> validateMultipleAllowMap = new HashMap<String, String>();
					Set<String> uniqueLogicalNameSet = new HashSet<String>();
					
					StringBuilder duplicateLogicalNames = new StringBuilder();
					StringBuilder invalidLogicalNames = new StringBuilder();
					StringBuilder combinationLogicalName =new StringBuilder();
					
					for(GatewayFieldMapData gatewayFieldMapData : gatewayFieldList) {
						String logicalName = null;String profileField =  null;

						if(Strings.isNullOrBlank(gatewayFieldMapData.getLogicalName()) == false) {
							 logicalName = gatewayFieldMapData.getLogicalName().trim();
						} 
						
						if(Strings.isNullOrBlank(gatewayFieldMapData.getProfileField()) == false) {
							profileField = gatewayFieldMapData.getProfileField().trim();
						}
						
						if(Collectionz.isNullOrEmpty(logicalNameMultipleAllowList) == false && Collectionz.isNullOrEmpty(logicalNameList)  ==false){
							ResultObject resObj = FieldWithLogicalNameValidateUtility.checkFieldWithLogicalNameValidate(validateMultipleAllowMap, uniqueLogicalNameSet,uniqueLogicalNameList, logicalNameMultipleAllowList,
									duplicateLogicalNames, invalidLogicalNames, logicalName, profileField, context);
							
							if(resObj.isError()){
								combinationLogicalName.append(resObj.getErrorMsg() + ", ");
							}
						}
					}
					
					DriverResult driverResult = new DriverResult();
					List<GatewayFieldMapData> convertedList = getConversionOfValueWithLogicalName(context, gatewayFieldList,driverResult, logicalNameList);
					
					if(driverResult.isError()){
						isValid = false;
					}
					
					if(Collectionz.isNullOrEmpty(convertedList) == false) {
						gatewayFieldList.clear();
						gatewayFieldList.addAll(convertedList);
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
					return isValid;
				}
		} catch (DataManagerException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	return isValid;
	}
	
private List<GatewayFieldMapData> getConversionOfValueWithLogicalName(ConstraintValidatorContext context, List<GatewayFieldMapData> gatewayFieldList2, DriverResult driverResult, List<LogicalNameValuePoolData> logicalNameList) {
		
		List<GatewayFieldMapData> convertedList = new ArrayList<GatewayFieldMapData>();
		for(GatewayFieldMapData gateWayFieldMapData : gatewayFieldList2 ){
			
			String logicalName = gateWayFieldMapData.getLogicalName();
			String profileField = gateWayFieldMapData.getProfileField();
			if (Strings.isNullOrEmpty(logicalName) && Strings.isNullOrEmpty(profileField)) {
				 RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name and profile-field must be specified.");
			      driverResult.setError(true);
			  } else if (Strings.isNullOrEmpty(profileField) && Strings.isNullOrEmpty(logicalName) == false) {
				  RestUtitlity.setValidationMessage(context, "In the Mapping List, the profile-field must be specified for logical-name:[" + logicalName + "].");
			      driverResult.setError(true);

			  } else if (Strings.isNullOrEmpty(logicalName) && Strings.isNullOrEmpty(profileField) == false) {
				  RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name must be specified for profile-field:[" + profileField + "].");
			      driverResult.setError(true);
			  }
			
			if(driverResult.isError() == false ) {
				GatewayFieldMapData gatewayMapData = new GatewayFieldMapData();
				for(LogicalNameValuePoolData data: logicalNameList) {
					if(data.getName().equals(logicalName)) {
						gatewayMapData.setLogicalName(data.getValue());
						gatewayMapData.setDefaultValue(gateWayFieldMapData.getDefaultValue());
						gatewayMapData.setMapAuthId(gateWayFieldMapData.getMapAuthId());
						gatewayMapData.setNameValuePoolData(gateWayFieldMapData.getNameValuePoolData());
						gatewayMapData.setProfileField(profileField);
						gatewayMapData.setProfileFieldMapId(gateWayFieldMapData.getProfileFieldMapId());
						gatewayMapData.setProfileFieldValuePoolData(gateWayFieldMapData.getProfileFieldValuePoolData());
						gatewayMapData.setValueMapping(gateWayFieldMapData.getValueMapping());
						convertedList.add(gatewayMapData);
						break;
					}
				}
			}
		}
		return convertedList;
	}
}
