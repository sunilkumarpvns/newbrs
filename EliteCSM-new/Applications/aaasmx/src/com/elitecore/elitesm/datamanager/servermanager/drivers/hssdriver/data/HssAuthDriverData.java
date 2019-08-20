package com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data;

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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.aaa.util.constants.HSSAuthDriverConstant;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverResult;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.util.DriverUtility;
import com.elitecore.elitesm.util.FieldWithLogicalNameValidateUtility;
import com.elitecore.elitesm.util.ResultObject;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.security.AuthenticationDetails;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "hss-auth-driver")
@XmlType(propOrder = { "applicationid", "commandCode", "requesttimeout", "userIdentityAttributes", "noOfTriplets", "additionalAttributes", 
		"diameterPeerRelDataList", "hssAuthFieldMapList" })
@ValidObject
public class HssAuthDriverData extends BaseData implements IHssAuthDriverData,Serializable,Differentiable,Validator{
	
	private static final long serialVersionUID = 1L;
	private String hssauthdriverid;
	
	@Expose
	@SerializedName("3GPP Application Id")
	@NotEmpty(message = "3gpp Application Id must be specified.")
	private String applicationid;

	@Expose
	@SerializedName("Command Code")
	@NotNull(message = "Command Code must be specified and it should be numeric.")
	private Long commandCode;
	
	@Expose
	@SerializedName("Request Timeout")
	private Long requesttimeout;

	@Expose
	@SerializedName("User Identity Attributes")
	private String userIdentityAttributes;

	@Expose
	@SerializedName("No of Triplets")
	private Long noOfTriplets;

	@Expose
	@SerializedName("Additional Attributes")
	private String additionalAttributes;
	
	private String driverInstanceId;
	
	
	@Valid
	@NotEmpty(message = "At least one Diameter Peer must be specified.")
    private List<DiameterPeerRelData> diameterPeerRelDataList;
    
	@Valid
	private List<HssAuthDriverFieldMapData> hssAuthFieldMapList;
	private boolean checkValidate;
	
	public HssAuthDriverData() {
		requesttimeout = HSSAuthDriverConstant.REQUEST_TIMEOUT;
		userIdentityAttributes = HSSAuthDriverConstant.USER_IDENTITY_ATTRIBUTES;
		noOfTriplets = HSSAuthDriverConstant.NO_OF_TRIPLETS;
    }
	
	@XmlTransient
	public String getHssauthdriverid() {
		return hssauthdriverid;
	}

	public void setHssauthdriverid(String hssauthdriverid) {
		this.hssauthdriverid = hssauthdriverid;
	}

	@XmlElement(name = "tgpp-application-id")
	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	@XmlElement(name = "request-timeout")
	public Long getRequesttimeout() {
		return requesttimeout;
	}

	public void setRequesttimeout(Long requesttimeout) {
		this.requesttimeout = requesttimeout;
	}

	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}

	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}

	@XmlElement(name = "user-identity-attributes")
	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}

	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}

	@XmlElement(name = "command-code")
	public Long getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(Long commandCode) {
		this.commandCode = commandCode;
	}
	
	@Valid
	@XmlElementWrapper(name = "hss-driver-field-mappings")
	@XmlElement(name = "hss-driver-field-mapping")
	public List<HssAuthDriverFieldMapData> getHssAuthFieldMapList() {
		return hssAuthFieldMapList;
	}

	public void setHssAuthFieldMapList(List<HssAuthDriverFieldMapData> hssAuthFieldMapList) {
		this.hssAuthFieldMapList = hssAuthFieldMapList;
	}
	
	@XmlTransient
	public boolean isCheckValidate() {
		return checkValidate;
	}
	public void setCheckValidate(boolean checkValidate) {
		this.checkValidate = checkValidate;
	}
	
	@Valid
	@XmlElementWrapper(name = "hss-peer-configuration")
	@XmlElement(name = "diameter-peer")
	public List<DiameterPeerRelData> getDiameterPeerRelDataList() {
		return diameterPeerRelDataList;
	}

	public void setDiameterPeerRelDataList(List<DiameterPeerRelData> diameterPeerRelDataList) {
		this.diameterPeerRelDataList = diameterPeerRelDataList;
	}
	
	@XmlElement(name = "no-of-triplets")
	public Long getNoOfTriplets() {
		return noOfTriplets;
	}

	public void setNoOfTriplets(Long noOfTriplets) {
		this.noOfTriplets = noOfTriplets;
	}
	
	@XmlElement(name = "additional-attributes")
	public String getAdditionalAttributes() {
		return additionalAttributes;
	}

	public void setAdditionalAttributes(String additionalAttributes) {
		this.additionalAttributes = additionalAttributes;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		List<LogicalNameValuePoolData> multipleLogicalNameRelList = null;
		try {
			if(Collectionz.isNullOrEmpty(hssAuthFieldMapList) == false  && checkValidate == false){
				if(SecurityContextHolder.getContext().getAuthentication() != null) {
					Object obj = SecurityContextHolder.getContext().getAuthentication().getDetails();
					AuthenticationDetails customAuthenticationDetails =null;
					DriverBLManager driverBLManager = new DriverBLManager();
					
					List<String> logicalNameMultipleAllowList =null;
					
					if(obj instanceof AuthenticationDetails){
						customAuthenticationDetails = (AuthenticationDetails) obj;
					}
					if(customAuthenticationDetails.isDiameter()){
						logicalNameMultipleAllowList = driverBLManager.getLogicalNameDriverRelList(DriverTypeConstants.DIAMETER_HSS_AUTH_DRIVER);
						multipleLogicalNameRelList = driverBLManager.getMultipleLogicalNameRelList(DriverTypeConstants.DIAMETER_HSS_AUTH_DRIVER);
					}else{
						logicalNameMultipleAllowList = driverBLManager.getLogicalNameDriverRelList(DriverTypeConstants.RADIUS_HSS_AUTH_DRIVER);
						multipleLogicalNameRelList = driverBLManager.getMultipleLogicalNameRelList(DriverTypeConstants.RADIUS_HSS_AUTH_DRIVER);
					}
					
					List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();
					List<String> uniqueLogicalNameList = DriverUtility.getUniqueLogicalMultipleAllowList(multipleLogicalNameRelList, logicalNameList);
					
					Map<String,String> validateMultipleAllowMap = new HashMap<String, String>();
					Set<String> uniqueLogicalNameSet = new HashSet<String>();
					
					StringBuilder duplicateLogicalNames = new StringBuilder();
					StringBuilder invalidLogicalNames = new StringBuilder();
					StringBuilder combinationLogicalName =new StringBuilder();
					
					for(HssAuthDriverFieldMapData gatewayFieldMapData : hssAuthFieldMapList) {
						String logicalName = gatewayFieldMapData.getLogicalName();
						String responseParamIndex = Strings.valueOf(gatewayFieldMapData.getAttributeIds());
						if(Strings.isNullOrEmpty(logicalName) == false && Strings.isNullOrBlank(responseParamIndex) ==false){
							
							ResultObject resObj = FieldWithLogicalNameValidateUtility.checkFieldWithLogicalNameValidate(validateMultipleAllowMap, uniqueLogicalNameSet,uniqueLogicalNameList, logicalNameMultipleAllowList,
									duplicateLogicalNames, invalidLogicalNames, logicalName.trim(), responseParamIndex.trim(), context);
							
							if(resObj.isError()){
								combinationLogicalName.append(resObj.getErrorMsg() + ", ");
							}
						}
					}
					
					DriverResult driverResult = new DriverResult();
					List<HssAuthDriverFieldMapData> convertedList = getConversionOfValueWithLogicalName(context, hssAuthFieldMapList,driverResult, logicalNameList);
					
					if(driverResult.isError()){
						isValid = false;
					}
					
					if(Collectionz.isNullOrEmpty(convertedList) == false) {
						hssAuthFieldMapList.clear();
						hssAuthFieldMapList.addAll(convertedList);
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


	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("3GPP Application Id", applicationid);
		object.put("Command Code", commandCode);
		object.put("Request Timeout", requesttimeout);
		object.put("User Identity Attributes", userIdentityAttributes);
		object.put("No of Triplets", noOfTriplets);
		object.put("Additional Attributes", additionalAttributes);
	   
		if (Collectionz.isNullOrEmpty(diameterPeerRelDataList) == false) {
			JSONArray array = new JSONArray();
			for (DiameterPeerRelData element : diameterPeerRelDataList) {
				array.add(EliteSMReferencialDAO.fetchDiameterPeerData(element.getPeerUUID()) + "-W-" + element.getWeightage());
			}
			object.put("HSS Peer Configuration", array);
		}
		if (Collectionz.isNullOrEmpty(hssAuthFieldMapList) == false) {
			JSONObject fields = new JSONObject();
			for (HssAuthDriverFieldMapData element : hssAuthFieldMapList) {
				fields.putAll(element.toJson());
			}
			object.put("Diameter HSS Driver Field Mappings", fields);
		}
		return object;
	}
	
private List<HssAuthDriverFieldMapData> getConversionOfValueWithLogicalName(ConstraintValidatorContext context, List<HssAuthDriverFieldMapData> hssFieldMapList, DriverResult driverResult, List<LogicalNameValuePoolData> logicalNameList) {
		
		List<HssAuthDriverFieldMapData> convertedList = new ArrayList<HssAuthDriverFieldMapData>();
		for(HssAuthDriverFieldMapData hssAuthDriverFieldMapData : hssFieldMapList ){
			
			 String logicalName = hssAuthDriverFieldMapData.getLogicalName();
			 String attributeIds = hssAuthDriverFieldMapData.getAttributeIds();
			
			if (Strings.isNullOrEmpty(logicalName) && Strings.isNullOrEmpty(attributeIds)) {
				 RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name and attribute-id must be specified.");
				 driverResult.setError(true);
			  } else if (Strings.isNullOrEmpty(attributeIds) && Strings.isNullOrEmpty(logicalName) == false) {
				  RestUtitlity.setValidationMessage(context, "In the Mapping List, the attribute-id must be specified for logical-name:[" + logicalName + "].");
				  driverResult.setError(true);
			  } else if (Strings.isNullOrEmpty(logicalName) && Strings.isNullOrEmpty(attributeIds) == false) {
				  RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name must be specified for attribute-id:[" + attributeIds + "].");
				  driverResult.setError(true);
			  }
			
			if(driverResult.isError() == false ) {
				HssAuthDriverFieldMapData hssAuthData = new HssAuthDriverFieldMapData();
				for(LogicalNameValuePoolData data: logicalNameList) {
					if(data.getName().equals(logicalName)) {
						hssAuthData.setAttributeIds(attributeIds);
						hssAuthData.setDbFieldMapId(hssAuthDriverFieldMapData.getDbFieldMapId());
						hssAuthData.setDefaultValue(hssAuthDriverFieldMapData.getDefaultValue());
						hssAuthData.setHssauthdriverid(hssAuthDriverFieldMapData.getHssauthdriverid());
						hssAuthData.setLogicalName(data.getValue());
						hssAuthData.setValueMapping(hssAuthDriverFieldMapData.getValueMapping());
						hssAuthData.setNameValuePoolData(hssAuthDriverFieldMapData.getNameValuePoolData());
						
						convertedList.add(hssAuthData);
						break;
					}
				}
			}
		}
		return convertedList;
	}
}
