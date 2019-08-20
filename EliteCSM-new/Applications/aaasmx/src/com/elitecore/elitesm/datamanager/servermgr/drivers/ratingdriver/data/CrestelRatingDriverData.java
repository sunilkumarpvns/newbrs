package com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.TranslationMappingNameToIDAdapter;
import com.elitecore.elitesm.ws.rest.security.AuthenticationDetails;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@ValidObject
@XmlType(propOrder = { "transMapConfId", "instanceNumber", "jndiPropValMapList" })
public class CrestelRatingDriverData extends BaseData implements Serializable,Differentiable, Validator{
	
	private static final long serialVersionUID = 1L;
	private String crestelRatingDriverId;
	private String driverInstanceId;
	
	@Expose
	@SerializedName("Translation Mapping Configuration")
	private String transMapConfId;
	
	@Expose
	@SerializedName("Number Of Instance")
	@NotNull(message = "Instance Number must be specified and it should be numeric")
	@Min(value = 0 , message = "Instance Number must be positive number.")
	private Integer instanceNumber;
	
	private TranslationMappingConfData translationMappingConfData;
	
	@Valid
	List<RatingDriverPropsData> jndiPropValMapList= new ArrayList<RatingDriverPropsData>(); 
	
	@XmlElement(name = "number-of-instance")
	public Integer getInstanceNumber() {
		return instanceNumber;
	}
	public void setInstanceNumber(Integer instanceNumber) {
		this.instanceNumber = instanceNumber;
	}
	public String getCrestelRatingDriverId() {
		return crestelRatingDriverId;
	}
	
	@XmlTransient
	public void setCrestelRatingDriverId(String crestelRatingDriverId) {
		this.crestelRatingDriverId = crestelRatingDriverId;
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
	public List<RatingDriverPropsData> getJndiPropValMapList() {
		return jndiPropValMapList;
	}
	public void setJndiPropValMapList(List<RatingDriverPropsData> jndiPropValMapList) {
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
		object.put("Number Of Instance", instanceNumber);
		object.put("Translation Mapping Configuration", EliteSMReferencialDAO.fetchTranslationMappingData(transMapConfId));
		
		if(jndiPropValMapList!=null){
			JSONObject fields = new JSONObject();
			for (RatingDriverPropsData element : jndiPropValMapList) {
				fields.putAll(element.toJson());
			}
			object.put("JNDI Properties", fields);
		}
		return object;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
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
				if (this.transMapConfId == null && customAuthenticationDetails.isCrestelRating()) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Translation Mapping Configuration must be specified and it must be of type Diameter to Crestel Rating");
					
				} else if (this.transMapConfId == null && customAuthenticationDetails.isCrestelRating() == false) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Translation Mapping Configuration must be specified and it must be of type Diameter to Crestel OCSv2");
					
				} else if(customAuthenticationDetails.isCrestelRating() && RestValidationMessages.INVALID.equals(this.transMapConfId) == false){
					translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_RATING, TranslationMappingConfigConstants.DIAMETER);
					for(TranslationMappingConfData translationMappingConfData : translationMappingConfDataList) {
						if(translationMappingConfData.getTranslationMapConfigId().compareTo(this.transMapConfId) == 0) {
							flag = true;
							return true;
						}
						
					}
					if(flag == false) {
						RestUtitlity.setValidationMessage(context, "Translation Mapping should be from Diameter to Crestel Rating type.");
						isValid = false;
						return isValid;
					}
				} else if(customAuthenticationDetails.isCrestelRating() == false && RestValidationMessages.INVALID.equals(this.transMapConfId) == false){
					translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.CRESTEL_OCSv2, TranslationMappingConfigConstants.DIAMETER);
					for(TranslationMappingConfData translationMappingConfData : translationMappingConfDataList) {
						if(translationMappingConfData.getTranslationMapConfigId().compareTo(this.transMapConfId) == 0) {
							flag = true;
							return true;
						}
					}
					if(flag == false) {
						RestUtitlity.setValidationMessage(context, "Translation Mapping type should be from Diameter to Crestel OCSv2.");
						isValid = false;
						return isValid;
					}
				} else {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Translation Mapping Configuration does not exist");
					return isValid;
				} 
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return isValid;
	}		
	
}

