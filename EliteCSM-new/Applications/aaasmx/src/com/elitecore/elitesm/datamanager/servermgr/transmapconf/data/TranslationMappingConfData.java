package com.elitecore.elitesm.datamanager.servermgr.transmapconf.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.BaseTranslationMappingAdapter;
import com.elitecore.elitesm.ws.rest.adapter.FromTranslationAdapter;
import com.elitecore.elitesm.ws.rest.adapter.ToTranslationAdapter;
import com.elitecore.elitesm.ws.rest.serverconfig.translationmapping.DefaultMappingParameter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Contains;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name="translation-mapping-config")
@XmlType(propOrder = {"name", "description", "script", "fromType", "toType", "baseTranslationMapConfigId", "translationMappingInstDataList", "defaultMapping", "dummyResponseParameterDataList" })
@ValidObject
public class TranslationMappingConfData extends BaseData implements Serializable, Differentiable, Validator {

	private static final long serialVersionUID = 1L;

	private String translationMapConfigId;

	@Expose
	@SerializedName("Name")
	@NotEmpty(message = "Translation Mapping name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;

	@Expose
	@SerializedName("Description")
	private String description;

	@Expose
	@SerializedName("Script")
	private String script;

	private String dummyResponse;

	@NotEmpty(message = "To Translation Type must be specified. It can be DIAMETER, CRESTEL-RATING, CRESTEL-OCSv2 or RADIUS.")
	@Contains(allowedValues = {TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.RADIUS, TranslationMappingConfigConstants.CRESTEL_RATING, TranslationMappingConfigConstants.CRESTEL_OCSv2}, invalidMessage = "Invalid To Translation Type. It can be DIAMETER, CRESTEL-RATING, CRESTEL-OCSv2 or RADIUS.")
	private String toType;

	@NotEmpty(message = "From Translation Type must be specified. It can be DIAMETER, WEB-SERVICE or RADIUS.")
	@Contains(allowedValues = {TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.WEB_SERVICE, TranslationMappingConfigConstants.RADIUS}, invalidMessage = "Invalid From Translation Type. It can be DIAMETER, WEB-SERVICE or RADIUS.")
	private String fromType;

	private TranslatorTypeData translatorTypeTo;
	private TranslatorTypeData translatorTypeFrom;
	private String auditUid;

	private String baseTranslationMapConfigId;

	@Valid
	private List<TranslationMappingInstData>  translationMappingInstDataList;
	private List<TranslationMappingInstDetailData> defaultTranslationMappingDetailDataList = new LinkedList<TranslationMappingInstDetailData>();

	private List<TranslationMappingInstDetailData> translationMappingList;
	private List<DummyResponseParameterData> dummyResponseParameterDataList;
	
	/**
	 * To achieve XML readability for REST
	 */
	private DefaultMappingParameter defaultMapping;
	
	public TranslationMappingConfData() {
		description = RestUtitlity.getDefaultDescription();
	}

	@XmlElement(name = "default-mapping")
	public DefaultMappingParameter getDefaultMapping() {
		return defaultMapping;
	}

	public void setDefaultMapping(DefaultMappingParameter defaultMapping) {
		this.defaultMapping = defaultMapping;
	}

	@XmlTransient
	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}

	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}

	@XmlElement(name  = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlTransient
	public String getDummyResponse() {
		return dummyResponse;
	}
	public void setDummyResponse(String dummyResponse) {
		this.dummyResponse = dummyResponse;
	}

	@XmlElement(name = "to-translation-type")
	@XmlJavaTypeAdapter(ToTranslationAdapter.class)
	public String getToType() {
		return toType;
	}
	public void setToType(String toType) {
		this.toType = toType;
	}

	@XmlElement(name = "from-translation-type")
	@XmlJavaTypeAdapter(FromTranslationAdapter.class)
	public String getFromType() {
		return fromType;
	}
	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	@XmlElement(name = "base-translation-mapping")
	@XmlJavaTypeAdapter(BaseTranslationMappingAdapter.class)
	public String getBaseTranslationMapConfigId() {
		return baseTranslationMapConfigId;
	}

	public void setBaseTranslationMapConfigId(String baseTranslationMapConfigId) {
		this.baseTranslationMapConfigId = baseTranslationMapConfigId;
	}

	@XmlElementWrapper(name = "mappings")
	@XmlElement(name = "mapping")
	public List<TranslationMappingInstData> getTranslationMappingInstDataList() {
		return translationMappingInstDataList;
	}

	public void setTranslationMappingInstDataList(List<TranslationMappingInstData> translationMappingInstDataList) {
		this.translationMappingInstDataList = translationMappingInstDataList;
	}

	@XmlTransient
	public List<TranslationMappingInstDetailData> getDefaultTranslationMappingDetailDataList() {
		return defaultTranslationMappingDetailDataList;
	}

	public void setDefaultTranslationMappingDetailDataList(List<TranslationMappingInstDetailData> defaultTranslationMappingDetailDataList) {
		this.defaultTranslationMappingDetailDataList = defaultTranslationMappingDetailDataList;
	}

	@XmlElementWrapper(name = "dummy-response-parameters")
	@XmlElement(name = "dummy-response-parameter")
	public List<DummyResponseParameterData> getDummyResponseParameterDataList() {
		return dummyResponseParameterDataList;
	}

	public void setDummyResponseParameterDataList(List<DummyResponseParameterData> dummyResponseParameterDataList) {
		this.dummyResponseParameterDataList = dummyResponseParameterDataList;
	}

	@XmlTransient
	public TranslatorTypeData getTranslatorTypeTo() {
		return translatorTypeTo;
	}

	public void setTranslatorTypeTo(TranslatorTypeData translatorTypeTo) {
		this.translatorTypeTo = translatorTypeTo;
	}

	@XmlTransient
	public TranslatorTypeData getTranslatorTypeFrom() {
		return translatorTypeFrom;
	}

	public void setTranslatorTypeFrom(TranslatorTypeData translatorTypeFrom) {
		this.translatorTypeFrom = translatorTypeFrom;
	}

	@XmlElement(name = "script")
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public JSONObject toJson(){
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("To Type", toType);
		object.put("From Type", fromType);
		object.put("Base Translation Mapping", EliteSMReferencialDAO.fetchTranslationMappingData(baseTranslationMapConfigId));
		object.put("script", script);
		JSONObject fields = new JSONObject();
		if(translationMappingInstDataList != null){
			for (TranslationMappingInstData element : translationMappingInstDataList) {
				object.put(element.getMappingName(), element.toJson());
			}
		}
		if (Collectionz.isNullOrEmpty(defaultTranslationMappingDetailDataList) == false) {
			JSONObject defaultMapping = new JSONObject();
			JSONObject requestParameter = new JSONObject();
			JSONObject responseParameter = new JSONObject();
			for (TranslationMappingInstDetailData element : defaultTranslationMappingDetailDataList) {
				if("TMI0002".equals(element.getMappingTypeId())){
					responseParameter.putAll(element.toJson());
				}else{
					requestParameter.putAll(element.toJson());
				}
			}
			defaultMapping.put("Request Parameters", requestParameter);
			defaultMapping.put("Response Parameters", responseParameter);
			object.put("Default Mapping", defaultMapping);
		}
		if (Collectionz.isNullOrEmpty(dummyResponseParameterDataList) == false) {
			fields = new JSONObject();
			for (DummyResponseParameterData element : dummyResponseParameterDataList) {
				fields.putAll(element.toJson());
			}
			object.put("Dummy Response Parameters", fields);
		}
		return object;
	}

	@XmlTransient
	public String getAuditUid() {
		return auditUid;
	}

	public void setAuditUid(String auditUid) {
		this.auditUid = auditUid;
	}
	
	@XmlTransient
	public List<TranslationMappingInstDetailData> getTranslationMappingList() {
		return translationMappingList;
	}

	public void setTranslationMappingList(List<TranslationMappingInstDetailData> translationMappingList) {
		this.translationMappingList = translationMappingList;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		String fromType = this.fromType;
		String toType = this.toType;
		
		String baseTranslationMappingId = this.baseTranslationMapConfigId;
		List<TranslationMappingInstData> translationMappingList = this.translationMappingInstDataList;

		isValid = validateToFromTranslationCombination(context, fromType, toType);
		if (isValid) {
			if (validateBaseTransaltionMapping(context, fromType, toType, baseTranslationMappingId)) {
				isValid = validateTranslationMappingList(context, translationMappingList);
			} else {
				isValid = false;
			}
		}
		return isValid;
	}

	private boolean validateTranslationMappingList(ConstraintValidatorContext context, List<TranslationMappingInstData> transMappingList) {
		ArrayList<String> nameList = new ArrayList<String>();
		ArrayList<String> inMessage = new ArrayList<String>();

		if (Collectionz.isNullOrEmpty(transMappingList) == false) {
			for(TranslationMappingInstData data : transMappingList) {
				nameList.add(data.getMappingName());
				inMessage.add(data.getInMessage());
			}
			return ValidateMappingNameAndInMessage(context, nameList, inMessage);
		} else {
			return true;
		}
	}

	private boolean ValidateMappingNameAndInMessage(ConstraintValidatorContext context, ArrayList<String> nameList, ArrayList<String> inMessage) {
		if (checkForMappingName(context, nameList)) {
			if(checkForInMessage(context, inMessage)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean checkForMappingName(ConstraintValidatorContext context, ArrayList<String> nameList) {
		for(int i=0; i<nameList.size();i++) {
			for(int j = i+1;j<nameList.size();j++) {
				if (Strings.isNullOrEmpty(nameList.get(i)) == false) {
				if (nameList.get(i).equals(nameList.get(j))) {
					RestUtitlity.setValidationMessage(context, "Mapping name should be unique for each Translation Mapping.");
					return false;
				}
			}
			}
		}
		return true;
	}

	private boolean checkForInMessage(ConstraintValidatorContext context, ArrayList<String> inMessage) {
		for(int i=0; i<inMessage.size();i++) {
			for(int j = i+1;j<inMessage.size();j++) {
				if (Strings.isNullOrEmpty(inMessage.get(i)) == false) {
				if (inMessage.get(i).equals(inMessage.get(j))) {
					RestUtitlity.setValidationMessage(context, "In Message should be unique for each Translation Mapping.");
					return false;
				}
				}
			}
		}
		return true;
	}


	private boolean validateBaseTransaltionMapping(ConstraintValidatorContext context, String fromValue, String toValue, String baseTranslationMappingId) {
		if (baseTranslationMappingId == null || "0".equals(baseTranslationMappingId)) {
			return true;
		} else if (RestValidationMessages.INVALID.equals(baseTranslationMappingId)) {
			RestUtitlity.setValidationMessage(context, "Base Translation Mapping does not exist.");
			return false;
		} else {
			TranslationMappingConfBLManager blManager = new TranslationMappingConfBLManager();
			TranslationMappingConfData baseTranslationMappingData = null;
			try {
				baseTranslationMappingData = blManager.getTranslationMappingConfDataById(baseTranslationMappingId);
			} catch (DataManagerException e) {
				e.printStackTrace();
			}
			return validateBaseTranslationMappingType(fromValue, toValue, baseTranslationMappingData, context);
		}
	}

	private boolean validateBaseTranslationMappingType(String fromValue, String toValue,
			TranslationMappingConfData baseTranslationMappingData, ConstraintValidatorContext context) {

		if (fromType.equals(baseTranslationMappingData.getFromType()) && toType.equals(baseTranslationMappingData.getToType())) {
			this.baseTranslationMapConfigId = baseTranslationMappingData.getTranslationMapConfigId();
			return true;
		}
		RestUtitlity.setValidationMessage(context, "Base Translation Mapping Type does not match with the Translation Mapping Type that is being created/updated.");
		return false;
	}

	private boolean validateToFromTranslationCombination(ConstraintValidatorContext context, String fromValue, String toValue) {

		if (TranslationMappingConfigConstants.WEB_SERVICE.equalsIgnoreCase(fromValue)) {
			if (TranslationMappingConfigConstants.DIAMETER.equalsIgnoreCase(toValue) == false) {
				RestUtitlity.setValidationMessage(context, "When the 'From' Translation Type is 'WEB-SERVICE then 'TO' Translation Type must be 'DIAMETER' only.");
				return false;
			}
		}
		return true;
	}
}
