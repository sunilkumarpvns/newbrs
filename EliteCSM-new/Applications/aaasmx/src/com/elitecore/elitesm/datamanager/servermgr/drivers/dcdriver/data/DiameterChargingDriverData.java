package com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data;

import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
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
@XmlRootElement(name = "diameter-charging-driver")
@ValidObject
public class DiameterChargingDriverData extends BaseData implements Differentiable,Validator{
	
	private String dcDriverId;
	private String driverInstanceId;
	
	@Expose
	@SerializedName("Translation Mapping Configuration")
	@NotEmpty(message = "Translation Mapping Configuration must be specified.")
	private String transMapConfId;
	
	@Expose
	@SerializedName("Disconnect URL")
	@NotEmpty(message = "Disconnect URL must be specified.")
	private String disConnectUrl;
	
	private TranslationMappingConfData translationMappingConfData;
	
	@XmlTransient
	public String getDcDriverId() {
		return dcDriverId;
	}
	public void setDcDriverId(String dcDriverId) {
		this.dcDriverId = dcDriverId;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElement(name = "translation-mapping-configuration")
	@XmlJavaTypeAdapter(TranslationMappingNameToIDAdapter.class)
	public String getTransMapConfId() {
		return transMapConfId;
	}
	public void setTransMapConfId(String transMapConfId) {
		this.transMapConfId = transMapConfId;
	}
	
	@XmlElement(name = "disconnect-url")
	public String getDisConnectUrl() {
		return disConnectUrl;
	}
	public void setDisConnectUrl(String disConnectUrl) {
		this.disConnectUrl = disConnectUrl;
	}
	
	@XmlTransient
	public TranslationMappingConfData getTranslationMappingConfData() {
		return translationMappingConfData;
	}
	public void setTranslationMappingConfData(
			TranslationMappingConfData translationMappingConfData) {
		this.translationMappingConfData = translationMappingConfData;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Disconnect URL", disConnectUrl);
		object.put("Translation Mapping Configuration", EliteSMReferencialDAO.fetchTranslationMappingData(transMapConfId));
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
				boolean flag =  false;
				if(Strings.isNullOrBlank(this.transMapConfId) == false && RestValidationMessages.INVALID.equals(this.transMapConfId) == false) {
					translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.RADIUS);
					for(TranslationMappingConfData translationMappingConfData : translationMappingConfDataList) {
						if(translationMappingConfData.getTranslationMapConfigId().equals(this.transMapConfId)) {
							flag = true;
							break;
						}
					}
					
					if(flag == false) {
						RestUtitlity.setValidationMessage(context, "Translation Mapping Configuration must be from Radius to Diameter type");
						return flag;
					}
				}else{
					if(Strings.isNullOrBlank(this.transMapConfId) == false ){
						isValid = false;
						RestUtitlity.setValidationMessage(context, "Translation Mapping Configuration does not exist.");
					}
				}
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		} 
		return isValid;
	}
}