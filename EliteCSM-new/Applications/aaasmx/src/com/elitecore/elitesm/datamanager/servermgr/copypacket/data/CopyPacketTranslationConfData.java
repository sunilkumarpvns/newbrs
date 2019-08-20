package com.elitecore.elitesm.datamanager.servermgr.copypacket.data;

import java.io.Serializable;
import java.util.ArrayList;
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
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.ws.rest.adapter.FromTranslationAdapter;
import com.elitecore.elitesm.ws.rest.serverconfig.copypacketconfig.DefaultMappingParameter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Contains;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "copy-packet-config")
@ValidObject
@XmlType(propOrder = {"name", "description", "script", "transFromType", "transToType", "copyPacketTransMapData", "defaultMapping", "dummyParameterData"})
public class CopyPacketTranslationConfData extends BaseData implements Serializable, Differentiable, Validator {
	
	private static final long serialVersionUID = 1L;
	private String copyPacketTransConfId;
	
	@Expose
	@SerializedName("Name")
	@NotEmpty(message = "Copy Packet configuration name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	
	@Expose
	@SerializedName("Description")
	private String description;
	
	@Expose
	@SerializedName("Script")
	private String script;
	
	@NotEmpty(message = "From Translation Type must be specified. It can be DIAMETER or RADIUS.")
	@Contains(allowedValues = {TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.RADIUS}, invalidMessage =  "Invalid From Translation Type. It can be DIAMETER or RADIUS.")
	private String transFromType;
	
	@NotEmpty(message = "To Translation Type must be specified. It can be DIAMETER or RADIUS.")
	@Contains(allowedValues = {TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.RADIUS}, invalidMessage = "Invalid To Translation Type. It can be DIAMETER or RADIUS.")
	private String transToType;
	private TranslatorTypeData translatorTypeTo;
	private TranslatorTypeData translatorTypeFrom;
	private String auditUid;
	
	@Valid
	private List<CopyPacketTranslationMapData> copyPacketTransMapData;
	@Valid
	private List<CopyPacketDummyResponseParameterData> dummyParameterData = new ArrayList<CopyPacketDummyResponseParameterData>();
	
	/**
	 * To achieve XML readability for REST
	 */
	@Valid
	private DefaultMappingParameter defaultMapping = new DefaultMappingParameter();
	
	public CopyPacketTranslationConfData() {
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
	public String getCopyPacketTransConfId() {
		return copyPacketTransConfId;
	}
	public void setCopyPacketTransConfId(String copyPacketTransConfId) {
		this.copyPacketTransConfId = copyPacketTransConfId;
	}
	
	@XmlElement(name = "name")
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
	
	@XmlElement(name = "from-translation-type")
	@XmlJavaTypeAdapter(FromTranslationAdapter.class)
	public String getTransFromType() {
		return transFromType;
	}
	public void setTransFromType(String transFromType) {
		this.transFromType = transFromType;
	}
	
	@XmlElement(name = "to-translation-type")
	@XmlJavaTypeAdapter(FromTranslationAdapter.class)
	public String getTransToType() {
		return transToType;
	}
	public void setTransToType(String transToType) {
		this.transToType = transToType;
	}
	
	@XmlElementWrapper(name = "mappings")
	@XmlElement(name = "mapping")
	public List<CopyPacketTranslationMapData> getCopyPacketTransMapData() {
		return copyPacketTransMapData;
	}
	public void setCopyPacketTransMapData(
			List<CopyPacketTranslationMapData> copyPacketTransMapData) {
		this.copyPacketTransMapData = copyPacketTransMapData;
	}
	
	@XmlElementWrapper(name = "dummy-response-parameters")
	@XmlElement(name = "dummy-response-parameter")
	public List<CopyPacketDummyResponseParameterData> getDummyParameterData() {
		return dummyParameterData;
	}
	public void setDummyParameterData(List<CopyPacketDummyResponseParameterData> dummyParameterData) {
		this.dummyParameterData = dummyParameterData;
	}
	
	@XmlElement(name = "script")
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
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
	
	@XmlTransient
	public String getAuditUid() {
		return auditUid;
	}
	public void setAuditUid(String auditUid) {
		this.auditUid = auditUid;
	}
	
	@Override
	public JSONObject toJson(){
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("To Type", transToType);
		object.put("From Type", transFromType);
		object.put("script", script);
		JSONObject fields = new JSONObject();
		if(copyPacketTransMapData != null){
			for (CopyPacketTranslationMapData element : copyPacketTransMapData) {	
				fields.putAll(element.toJson());
			}	
			object.put("Mappings", fields);
		}
		if(dummyParameterData != null && dummyParameterData.size() > 0){
			fields = new JSONObject();
			for (CopyPacketDummyResponseParameterData element : dummyParameterData) {
				fields.putAll(element.toJson());
			}
			object.put("Dummy Response Parameters", fields);
		}
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		isValid = validateToFromTranslationCombination(context, this.transFromType, this.transToType);
		if (isValid) {
			isValid = validateTranslationMappingList(context, this.copyPacketTransMapData);
		}
		return isValid;
	}

	private boolean validateTranslationMappingList(ConstraintValidatorContext context, List<CopyPacketTranslationMapData> copyPacketMappingData) {
		ArrayList<String> nameList = new ArrayList<String>();
		ArrayList<String> inMessage = new ArrayList<String>();

		if (Collectionz.isNullOrEmpty(copyPacketMappingData) == false) {
			for(CopyPacketTranslationMapData data : copyPacketMappingData) {
				nameList.add(data.getMappingName());
				inMessage.add(data.getInExpression());
			}
			return ValidateMappingNameAndInMessage(context, nameList, inMessage);
		} else {
			RestUtitlity.setValidationMessage(context, "Mapping must be specified.");
			return false;
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
		int noOfMappingName = nameList.size();
		for(int i=0; i<noOfMappingName;i++) {
			for(int j = i+1;j<noOfMappingName;j++) {
				if (Strings.isNullOrEmpty(nameList.get(i)) == false) {
					if (nameList.get(i).equals(nameList.get(j))) {
						RestUtitlity.setValidationMessage(context, "Mapping name should be unique for each Copy Packet Mapping.");
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean checkForInMessage(ConstraintValidatorContext context, ArrayList<String> inMessage) {
		int noOfInMessages = inMessage.size();
		for(int i=0; i<noOfInMessages;i++) {
			for(int j = i+1;j<noOfInMessages;j++) {
				if (Strings.isNullOrEmpty(inMessage.get(i)) == false) {
					if (inMessage.get(i).equals(inMessage.get(j))) {
						RestUtitlity.setValidationMessage(context, "In Expression should be unique for each Copy Packet Mapping.");
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean validateToFromTranslationCombination(ConstraintValidatorContext context, String fromType, String toType) {

		if ( fromType != null && toType != null && TranslationMappingConfigConstants.INVALID_VALUE.equals(fromType) == false && TranslationMappingConfigConstants.INVALID_VALUE.equals(toType) == false) {
			if (fromType.equals(toType) == false) {
				RestUtitlity.setValidationMessage(context, "Copy Packet Translation Mapping only supports DIAMETER-DIAMETER and RADIUS-RADIUS packet translation.");
				return false;
			} }
		return true; 
	}
	}
