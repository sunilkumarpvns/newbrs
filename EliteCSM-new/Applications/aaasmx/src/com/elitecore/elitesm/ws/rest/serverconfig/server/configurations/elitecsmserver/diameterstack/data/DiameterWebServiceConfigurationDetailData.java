package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;


import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder={"reAuthTranslationMappingDetail","abortSessionTranslationMappingDetail","genericTranslationMappingDetail"})
@ValidObject
public class DiameterWebServiceConfigurationDetailData implements Validator{
	
	private ReAuthTranslationMappingDetailData reAuthTranslationMappingDetail;
	private AbortSessionTranslationMappingDetailData abortSessionTranslationMappingDetail;
	private GenericTranslationMappingDetailData genericTranslationMappingDetail;

	public DiameterWebServiceConfigurationDetailData() {
		this.reAuthTranslationMappingDetail = new ReAuthTranslationMappingDetailData();
		this.abortSessionTranslationMappingDetail = new AbortSessionTranslationMappingDetailData();
		this.genericTranslationMappingDetail = new GenericTranslationMappingDetailData();
	}

	@XmlElement(name="diameter-re-auth")
	@Valid
	public ReAuthTranslationMappingDetailData getReAuthTranslationMappingDetail() {
		return reAuthTranslationMappingDetail;
	}

	public void setReAuthTranslationMappingDetail(
			ReAuthTranslationMappingDetailData reAuthTranslationMappingDetail) {
		this.reAuthTranslationMappingDetail = reAuthTranslationMappingDetail;
	}
	@XmlElement(name="diameter-abort-session")
	@Valid
	public AbortSessionTranslationMappingDetailData getAbortSessionTranslationMappingDetail() {
		return abortSessionTranslationMappingDetail;
	}

	public void setAbortSessionTranslationMappingDetail(
			AbortSessionTranslationMappingDetailData abortSessionTranslationMappingDetail) {
		this.abortSessionTranslationMappingDetail = abortSessionTranslationMappingDetail;
	}

	@XmlElement(name="diameter-generic-request")
	@Valid
	public GenericTranslationMappingDetailData getGenericTranslationMappingDetail() {
		return genericTranslationMappingDetail;
	}

	public void setGenericTranslationMappingDetail(
			GenericTranslationMappingDetailData genericTranslationMappingDetail) {
		this.genericTranslationMappingDetail = genericTranslationMappingDetail;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		//diameter to web service translation mapping
		List<String> tranlationMappingNames = new ArrayList<String>();
		TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
		try{
			List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.WEB_SERVICE);
			
			if(Collectionz.isNullOrEmpty(translationMappingConfDataList) == false){
				for(TranslationMappingConfData translationMappingConfData : translationMappingConfDataList){
					tranlationMappingNames.add(translationMappingConfData.getName());
				}
			}
			
		}catch(Exception e){
			RestUtitlity.setValidationMessage(context, "Failed to retrive list of Alert listeer names");
			return false;
		}
		
		String reAuthTranlationMappingName = reAuthTranslationMappingDetail.getTranslationMapping();
		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(reAuthTranlationMappingName) == false && Strings.isNullOrBlank(reAuthTranlationMappingName) ==false){
			if(tranlationMappingNames.contains(reAuthTranlationMappingName) == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Configured "+reAuthTranlationMappingName+" reAuth Translation Mapping does not exist or It does not have from Web-service type to Diameter type");
				return isValid;
			}
		}
		String abortSessionTranslationMappingName = abortSessionTranslationMappingDetail.getTranslationMapping();
		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(abortSessionTranslationMappingName) == false  && Strings.isNullOrBlank(abortSessionTranslationMappingName) ==false){
			if(tranlationMappingNames.contains(abortSessionTranslationMappingName) == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Configured "+abortSessionTranslationMappingName+" Abort Session Translation Mapping does not exist or It does not have from Web-service type to Diameter type");
				return isValid;
			}
		}
		
		String genericTranslationMappingName = genericTranslationMappingDetail.getTranslationMapping();
		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(genericTranslationMappingName) == false   && Strings.isNullOrBlank(genericTranslationMappingName) ==false){
			if(tranlationMappingNames.contains(genericTranslationMappingName) == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Configured "+genericTranslationMappingName+" Generic Translation Mapping does not exist or It does not have from Web-service type to Diameter type");
				return isValid;
			}
		}
		return isValid;
	}
	
}

