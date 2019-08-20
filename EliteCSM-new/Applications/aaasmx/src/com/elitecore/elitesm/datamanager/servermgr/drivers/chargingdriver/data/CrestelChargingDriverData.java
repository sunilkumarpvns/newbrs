package com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.StringToIntegerAdapter;
import com.elitecore.elitesm.ws.rest.adapter.TranslationMappingNameToIDAdapter;
import com.elitecore.elitesm.ws.rest.security.AuthenticationDetails;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@ValidObject
@XmlRootElement(name = "crestel-charging-driver")
@XmlType(propOrder = {"transMapConfId", "instanceNumber", "jndiPropValMapList"})
public class CrestelChargingDriverData extends BaseData implements Serializable,Differentiable,Validator{
	
	private static final long serialVersionUID = 1L;
	private String crestelChargingDriverId;
	private String driverInstanceId;
	
	@Expose
	@SerializedName("Translation Mapping Configuration")
	private String transMapConfId;
	
	@Expose
	@SerializedName("Number Of Instance")
	@NotNull(message = "Instance Number must be specified and it should be numeric.")
	private Integer instanceNumber;
	
	private TranslationMappingConfData translationMappingConfData;
	@Valid
	List<CrestelChargingDriverPropsData> jndiPropValMapList= new ArrayList<CrestelChargingDriverPropsData>(); 
	
	@XmlTransient
	public String getCrestelChargingDriverId() {
		return crestelChargingDriverId;
	}

	public void setCrestelChargingDriverId(String crestelChargingDriverId) {
		this.crestelChargingDriverId = crestelChargingDriverId;
	}

	@XmlElement(name = "number-of-instance")
	@XmlJavaTypeAdapter(StringToIntegerAdapter.class)
	public Integer getInstanceNumber() {
		return instanceNumber;
	}
	
	public void setInstanceNumber(Integer instanceNumber) {
		this.instanceNumber = instanceNumber;
	}
	
	@XmlElement(name = "translation-mapping-configuration")
	@XmlJavaTypeAdapter(TranslationMappingNameToIDAdapter.class)
	public String getTransMapConfId() {
		return transMapConfId;
	}
	
	public void setTransMapConfId(String transMapConfId) {
		this.transMapConfId = transMapConfId;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElementWrapper(name = "jndi-properties")
	@XmlElement(name = "jndi-property")
	public List<CrestelChargingDriverPropsData> getJndiPropValMapList() {
		return jndiPropValMapList;
	}

	public void setJndiPropValMapList(
			List<CrestelChargingDriverPropsData> jndiPropValMapList) {
		this.jndiPropValMapList = jndiPropValMapList;
	}

	@XmlTransient
	public TranslationMappingConfData getTranslationMappingConfData() {
		return translationMappingConfData;
	}
	
	public void setTranslationMappingConfData(TranslationMappingConfData translationMappingConfData) {
		this.translationMappingConfData = translationMappingConfData;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Translation Mapping Configuration", EliteSMReferencialDAO.fetchTranslationMappingData(transMapConfId));
		object.put("Number Of Instance", instanceNumber);
		if(Collectionz.isNullOrEmpty(jndiPropValMapList) == false){
			JSONObject fields = new JSONObject();
			for (CrestelChargingDriverPropsData element : jndiPropValMapList) {
				fields.putAll(element.toJson());
			}
			object.put("JNDI Properties", fields);
		}
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {

		boolean isValid = true;
		
		if (this.instanceNumber != null && this.instanceNumber <= 0) {
			RestUtitlity.setValidationMessage(context, "Instance Number must be positive number.");
			isValid = false;
		}
		
		try{
			if(SecurityContextHolder.getContext().getAuthentication() != null) {
				Object obj = SecurityContextHolder.getContext().getAuthentication().getDetails();
				AuthenticationDetails customAuthenticationDetails = null;
				
				if(obj instanceof AuthenticationDetails){
					customAuthenticationDetails = (AuthenticationDetails) obj;
				}
				
				TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
				List<TranslationMappingConfData> translationMappingConfDataList = null;
				boolean flag = false;
				if (this.transMapConfId == null) {
					RestUtitlity.setValidationMessage(context, "Translation Mapping Configuration must be specified.");
					isValid = false;
				}else if(customAuthenticationDetails.isCrestelCharging() && RestValidationMessages.INVALID.equals(this.transMapConfId) == false){
					translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_RATING, TranslationMappingConfigConstants.RADIUS);
					for(TranslationMappingConfData translationMappingConfData : translationMappingConfDataList) {
						if(translationMappingConfData.getTranslationMapConfigId().equals(this.transMapConfId)) {
							flag = true;
						}
					}
					
					if(flag == false) {
						RestUtitlity.setValidationMessage(context, "Translation Mapping Configuration must be from Radius to Crestel Rating");
						isValid = false;
					}
				} else if(customAuthenticationDetails.isCrestelCharging() == false && RestValidationMessages.INVALID.equals(this.transMapConfId) == false){
					translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_OCSv2, TranslationMappingConfigConstants.RADIUS);
					for(TranslationMappingConfData translationMappingConfData : translationMappingConfDataList) {
						if(translationMappingConfData.getTranslationMapConfigId().equals(this.transMapConfId)) {
							flag = true;
						}
					}
					
					if(flag == false) {
						RestUtitlity.setValidationMessage(context, "Translation Mapping Configuration must be from Radius to Crestel OCS V2");
						isValid = false;
					}
				} else {
					RestUtitlity.setValidationMessage(context, "Translation Mapping Configuration does not exist.");
					isValid = false;
				}
			}
		}catch (DataManagerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	return isValid;
  }	
}
