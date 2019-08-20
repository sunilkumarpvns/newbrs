package com.elitecore.elitesm.datamanager.servermgr.copypacket.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.util.constants.CopyPacketConstant;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
@ValidObject
@XmlType(propOrder = {"mappingName", "inExpression", "dummyResponse", "isDefaultMapping", "requestParameter", "responseParameter"})
public class CopyPacketTranslationMapData extends BaseData implements Serializable, Differentiable, Validator {
	private static final long serialVersionUID = 1L;
	
	private String copyPacketMappingId;
	@NotEmpty(message = "Mapping Name must be specified.")
	private String mappingName;
	@NotEmpty(message = "In Expression must be specified.")
	private String inExpression;
	@Pattern(regexp = "" + CopyPacketConstant.TRUE + "|" + CopyPacketConstant.False + "", message = "Invalid dummy reponse. Value of dummy reponse must be true or false.")
	private String dummyResponse;
	@Pattern(regexp = "" + CopyPacketConstant.TRUE + "|" + CopyPacketConstant.False + "", message = "Invalid default mapping. Value of default-mapping must be true or false.")
	private String isDefaultMapping;
	@Valid
	private String copyPacketTransConfId;
	private List<CopyPacketTranslationMapDetailData> copyPacketTransMapDetail = new ArrayList<CopyPacketTranslationMapDetailData>();
	
	private Integer orderNumber;

	/**
	 * To achieve XML readability for REST, it is a list of request and response parameter from each mapping
	 */
	@Valid
	private List<CopyPacketTranslationMapDetailData> requestParameter = new ArrayList<CopyPacketTranslationMapDetailData>();
	@Valid
	private List<CopyPacketTranslationMapDetailData> responseParameter = new ArrayList<CopyPacketTranslationMapDetailData>();
	
	public CopyPacketTranslationMapData() {
		this.setIsDefaultMapping(CopyPacketConstant.False);
		this.setDummyResponse(CopyPacketConstant.False);
	}

	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@XmlElementWrapper(name = "request-parameters")
	@XmlElement(name = "request-parameter")
	public List<CopyPacketTranslationMapDetailData> getRequestParameter() {
		return requestParameter;
	}
	
	public void setRequestParameter(List<CopyPacketTranslationMapDetailData> requestParameter) {
		this.requestParameter = requestParameter;
	}
	
	@XmlElementWrapper(name = "response-parameters")
	@XmlElement(name = "response-parameter")
	public List<CopyPacketTranslationMapDetailData> getResponseParameter() {
		return responseParameter;
	}
	
	public void setResponseParameter(List<CopyPacketTranslationMapDetailData> responseParameter) {
		this.responseParameter = responseParameter;
	}
	
	@XmlTransient
	public String getCopyPacketTransConfId() {
		return copyPacketTransConfId;
	}
	public void setCopyPacketTransConfId(String copyPacketTransConfId) {
		this.copyPacketTransConfId = copyPacketTransConfId;
	}
	
	@XmlTransient
	public String getCopyPacketMappingId() {
		return copyPacketMappingId;
	}
	public void setCopyPacketMappingId(String copyPacketMappingId) {
		this.copyPacketMappingId = copyPacketMappingId;
	}
	
	@XmlElement(name = "mapping-name")
	public String getMappingName() {
		return mappingName;
	}
	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}
	
	@XmlElement(name = "in-expression")
	public String getInExpression() {
		return inExpression;
	}
	public void setInExpression(String inExpression) {
		this.inExpression = inExpression;
	}
	
	@XmlElement(name = "dummy-response")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getDummyResponse() {
		return dummyResponse;
	}
	public void setDummyResponse(String dummyResponse) {
		this.dummyResponse = dummyResponse;
	}
	
	@XmlElement(name = "default-mapping")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getIsDefaultMapping() {
		return isDefaultMapping;
	}
	public void setIsDefaultMapping(String isDefaultMapping) {
		this.isDefaultMapping = isDefaultMapping;
	}
	
	@XmlTransient
	public List<CopyPacketTranslationMapDetailData> getCopyPacketTransMapDetail() {
		return copyPacketTransMapDetail;
	}

	public void setCopyPacketTransMapDetail(
			List<CopyPacketTranslationMapDetailData> copyPacketTransMapDetail) {
		this.copyPacketTransMapDetail = copyPacketTransMapDetail;
	}

	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ CopyPacketMappingInstanceData --------------");
		writer.println("CopyPacketMappingInstanceId 	     :"+copyPacketMappingId);
		writer.println("inMessage                :"+inExpression);           
		writer.println("DummyResponse           :"+dummyResponse); 
		writer.println("DefaultMapping           :"+isDefaultMapping); 
		writer.println("CopyPacketMappingTransConfigId   :"+copyPacketTransConfId);
		writer.println("-------------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	}
	
	public JSONObject toJson(){
		
		JSONObject outerObject = new JSONObject();
		JSONObject object =  new JSONObject();
		object.put("In Expression", inExpression);
		object.put("Dummy Response", dummyResponse);
		object.put("Default Mapping", isDefaultMapping);
		JSONObject requestParameter = new JSONObject();
		JSONObject responseParameter = new JSONObject();
		
		if(copyPacketTransMapDetail != null && copyPacketTransMapDetail.size()  > 0){
			for (CopyPacketTranslationMapDetailData element : copyPacketTransMapDetail) {
				if("TMI0002".equals(element.getMappingTypeId())){
					responseParameter.putAll(element.toJson());
				}else{
					requestParameter.putAll(element.toJson());
				}
			}
			object.put("Request Parameters", requestParameter);
			object.put("Response Parameters", responseParameter);
		}
		if(mappingName!=null){
			outerObject.put(mappingName, object);
		}
		return outerObject;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {

		boolean isValid = true;

		if (TranslationMappingConfigConstants.TRUE.equalsIgnoreCase(this.isDefaultMapping)) {

			if (Collectionz.isNullOrEmpty(this.copyPacketTransMapDetail) && Collectionz.isNullOrEmpty(this.requestParameter) && Collectionz.isNullOrEmpty(this.responseParameter)) {
				return isValid;
			} else if ((Collectionz.isNullOrEmpty(this.copyPacketTransMapDetail) && Collectionz.isNullOrEmpty(this.requestParameter) == false) || 
					(Collectionz.isNullOrEmpty(this.copyPacketTransMapDetail) && Collectionz.isNullOrEmpty(this.responseParameter) == false)) {
				RestUtitlity.setValidationMessage(context, "Default mapping is enabled, so the request and response parameters are not required for mapping: " + this.mappingName + ".");
				isValid = false;
			}
		}
		return isValid;

	}
	
}
