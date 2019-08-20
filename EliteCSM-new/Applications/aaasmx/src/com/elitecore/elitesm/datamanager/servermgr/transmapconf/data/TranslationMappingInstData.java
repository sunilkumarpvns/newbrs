package com.elitecore.elitesm.datamanager.servermgr.transmapconf.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
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
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.ws.rest.adapter.TrueFalseAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.Contains;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
@XmlRootElement(name = "mapping")
@ValidObject
@XmlType(propOrder = {"mappingName", "inMessage", "outMessage", "dummyResponse", "defaultMapping", "requestParameter", "responseParameter" })
public class TranslationMappingInstData extends BaseData implements  Serializable, Differentiable, Validator {
	

	private static final long serialVersionUID = 1L;
	
	private  String mappingInstanceId;
	@NotEmpty(message = "Mapping Name must be specified.")
	private String mappingName;
	@NotEmpty(message = "In Message must be specified.")
	private String inMessage;
	private String outMessage;
	private String translationMapConfigId;
	@Contains(allowedValues = { TranslationMappingConfigConstants.NO, TranslationMappingConfigConstants.YES }, invalidMessage = "Invalid default mapping. Value of default-mapping must be True or False.", isNullable = true)
	private String defaultMapping;
	@Contains(allowedValues = { TranslationMappingConfigConstants.TRUE, TranslationMappingConfigConstants.False }, invalidMessage = "Invalid dummy reponse. Value of dummy reponse must be True or False." , isNullable = true)
	private String dummyResponse;
	private List<TranslationMappingInstDetailData> translationMappingInstDetailDataList = new  ArrayList<TranslationMappingInstDetailData>();
	private Integer orderNumber ;
	
	public TranslationMappingInstData() {
		setDefaultMapping("Y");
		setDummyResponse("false");
	}
	
	/**
	 * To achieve XML readability for REST, it is a list of request and response parameter from each mapping
	 */
	private List<TranslationMappingInstDetailData> requestParameter = new ArrayList<TranslationMappingInstDetailData>(); 
	private List<TranslationMappingInstDetailData> responseParameter = new ArrayList<TranslationMappingInstDetailData>();
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@XmlElementWrapper(name = "request-parameters")
	@XmlElement(name = "request-parameter")
	public List<TranslationMappingInstDetailData> getRequestParameter() {
		return requestParameter;
	}
	
	public void setRequestParameter(List<TranslationMappingInstDetailData> requestParameter) {
		this.requestParameter = requestParameter;
	}
	
	@XmlElementWrapper(name = "response-parameters")
	@XmlElement(name = "response-parameter")
	public List<TranslationMappingInstDetailData> getResponseParameter() {
		return responseParameter;
	}
	
	public void setResponseParameter(List<TranslationMappingInstDetailData> responseParameter) {
		this.responseParameter = responseParameter;
	}
	
	@XmlTransient
	public String getMappingInstanceId() {
		return mappingInstanceId;
	}
	
	public void setMappingInstanceId(String mappingInstanceId) {
		this.mappingInstanceId = mappingInstanceId;
	}
	
	@XmlElement(name = "mapping-name")
	public String getMappingName() {
		return mappingName;
	}
	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}
	
	@XmlElement(name = "in-message")
	public String getInMessage() {
		return inMessage;
	}
	
	public void setInMessage(String inMessage) {
		this.inMessage = inMessage;
	}
	
	@XmlElement(name = "out-message")
	public String getOutMessage() {
		return outMessage;
	}
	
	public void setOutMessage(String outMessage) {
		this.outMessage = outMessage;
	}
	
	@XmlTransient
	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}
	
	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}
	
	@XmlElement(name = "default-mapping")
	@XmlJavaTypeAdapter(TrueFalseAdapter.class)
	public String getDefaultMapping() {
		return defaultMapping;
	}
	
	public void setDefaultMapping(String defaultMapping) {
		this.defaultMapping = defaultMapping;
	}
	
	@XmlElement(name = "dummy-response")
	public String getDummyResponse() {
		return dummyResponse;
	}
	
	public void setDummyResponse(String dummyResponse) {
		this.dummyResponse = dummyResponse;
	}
	
	@XmlTransient
	public List<TranslationMappingInstDetailData> getTranslationMappingInstDetailDataList() {
		return translationMappingInstDetailDataList;
	}

	public void setTranslationMappingInstDetailDataList(
			List<TranslationMappingInstDetailData> translationMappingInstDetailDataList) {
		this.translationMappingInstDetailDataList = translationMappingInstDetailDataList;
	}
	
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ TranslationMappingInstData --------------");
		writer.println("mappingInstanceId 	     :"+mappingInstanceId);
		writer.println("inMessage                :"+inMessage);           
		writer.println("outMessage               :"+outMessage);
		writer.println("defaultMapping           :"+defaultMapping);         
		writer.println("translationMapConfigId   :"+translationMapConfigId);
		writer.println("-------------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	}
	
	public JSONObject toJson(){
		JSONObject object =  new JSONObject();
		object.put("In Message", inMessage);
		object.put("Out Message", outMessage);
		object.put("Dummy Response", dummyResponse);
		object.put("Default Mapping", defaultMapping);
		JSONObject requestParameter = new JSONObject();
		JSONObject responseParameter = new JSONObject();
		
		if (Collectionz.isNullOrEmpty(translationMappingInstDetailDataList) == false) {
			for (TranslationMappingInstDetailData element : translationMappingInstDetailDataList) {
				if("TMI0002".equals(element.getMappingTypeId())){
					responseParameter.putAll(element.toJson());
				}else{
					requestParameter.putAll(element.toJson());
				}
			}
			object.put("Request Parameters", requestParameter);
			object.put("Response Parameters", responseParameter);
		}
		
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {

		boolean isValid = true;
		String defaultStatusValue = this.defaultMapping;
		List<TranslationMappingInstDetailData> requestResponseList = this.translationMappingInstDetailDataList; 
		List<TranslationMappingInstDetailData> requestParameterList = this.requestParameter; 
		List<TranslationMappingInstDetailData> responseParameterList = this.responseParameter; 

		if (TranslationMappingConfigConstants.YES.equalsIgnoreCase(defaultStatusValue)) {

			if (Collectionz.isNullOrEmpty(requestResponseList) && Collectionz.isNullOrEmpty(requestParameterList) && Collectionz.isNullOrEmpty(responseParameterList)) {
				return isValid;
			}
			else if ((Collectionz.isNullOrEmpty(requestResponseList) && Collectionz.isNullOrEmpty(requestParameterList) == false) || (Collectionz.isNullOrEmpty(requestResponseList) && Collectionz.isNullOrEmpty(responseParameterList) == false)) {
				RestUtitlity.setValidationMessage(context, "Default mapping is enabled, so the request and response parameters are not required.");
				isValid = false;
			}
		}
		return isValid;
	
	}
}
