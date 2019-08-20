package com.elitecore.aaa.rm.conf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.drivers.conf.CrestelAttributePolicyMappingDictator;
import com.elitecore.aaa.radius.drivers.conf.CrestelAttributePolicyMappingWrapper;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "attribute-policy-mapping")
@ConfigurationProperties(moduleName ="RM_CRESTEL_ATTR_MAPPING_CONFIGURABLE",readWith = XMLReader.class, synchronizeKey ="CRESTEL_ATTRIBUTE_POLICY_MAPPING")
@XMLProperties(name = "crestel-attribute-mapping", schemaDirectories = {"system","schema"}, configDirectories = {"conf", "services", "drivers"})		
public class RMCrestelAttributeMappingConfigurable  extends Configurable{

	public static final String ATTRIBUTE_IDS = "ATTRIBUTE_IDS";
	public static final String ATTRIBUTE = "ATTRIBUTE";
	public static final String RATING_FIELD = "RATING_FIELD";
	public static final String DEFAULT_VALUE = "DEFAULT_VALUE";
	public static final String DUMMY_DRIVER = "DUMMY_DRIVER";
	public static final String VENDOR_ID = "VENDOR_ID";
	public static final String REQUEST_PARAMETERS = "REQUEST_PARAMETERS";
	public static final String RESPONSE_PARAMETERS = "RESPONSE_PARAMETERS";
	public static final String ATTRIBUTE_LIST = "attribute-list";
	public static final String RATING_RESPONSE = "RATING_RESPONSE";
	public static final String API_DETAIL = "API_DETAIL";
	public static final String JNDI_PROPERTIES = "JNDI_PROPERTIES";
	public static final String PROPERTIES = "PROPERTIES";
	public static final String PROPERTY_NAME = "NAME";
	public static final String PROPERTY_VALUE = "VALUE";
	public static final String VALUE_MAPPING = "VALUE_MAPPING";
	public static final String ATTRIBUTES_POLICY_MAPPING_CONFIGURATION = "attribute-policy-mapping-configuration";
	public static final String RESPONSE_ERROR_MAPPING="response-error-mapping";
	public static final String ATTRIBUTE_VALUE="attribute-value";
	public static final String RATING_FIELD_CHECKS = "RATING_FIELD_CHECKS";
	public static final String CHECKED_ATTRIBUTE_LIST = "CHECKED_ATTRIBUTE_lIST";
	public static final String CHECKED_MAPPING_LIST = "checked-mapping-list";
	
	
	private List<MappingPolicyDetail> mappingPolicyDetail;
	private Map<String, Object> configMap;

	public RMCrestelAttributeMappingConfigurable() {
		mappingPolicyDetail = new ArrayList<MappingPolicyDetail>();
		this.configMap = new HashMap<String, Object>();
	}

	@XmlElement(name ="mapping-policy")
	public List<MappingPolicyDetail> getMappingPolicyDetail() {
		return mappingPolicyDetail;
	}

	public void setMappingPolicyDetail(List<MappingPolicyDetail> mappingPolicyDetail) {
		this.mappingPolicyDetail = mappingPolicyDetail;
	}



	@PostRead
	public void postReadProcessing() {

		Map<String, Object> localHashMap = new HashMap<String, Object>();
		
		try{
			
			CrestelAttributePolicyMappingDictator attrPolicyMappingDictator = new CrestelAttributePolicyMappingDictator();
			
			if(mappingPolicyDetail!=null){
				int numOfPolicy = mappingPolicyDetail.size();
				for(int i=0;i<numOfPolicy;i++){
					MappingPolicyDetail policy = mappingPolicyDetail.get(i);
					getAttributePolicyMappingDictator(policy.getId(), attrPolicyMappingDictator,policy);
				}
			}
			
			localHashMap.put(ATTRIBUTES_POLICY_MAPPING_CONFIGURATION, attrPolicyMappingDictator);
			
			this.configMap = localHashMap;
			
		}catch(Exception e){
		}
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}
	

	private ArrayList<?> parseJsonObject(JSONObject object,String mapValue){
		ArrayList<Object> subAvpList = new ArrayList<Object>();
		Set<?> set = object.keySet();
		Iterator<?> iterator = set.iterator();
		while(iterator.hasNext()){
			String attrId = (String)iterator.next();
			HashMap<String, Object> avpDetailMap = new HashMap<String, Object>();			
			if(attrId.contains(":")){
				StringTokenizer strTokenizer = new StringTokenizer(attrId, ":");
				avpDetailMap.put(VENDOR_ID, strTokenizer.nextToken());
				avpDetailMap.put(ATTRIBUTE_IDS, strTokenizer.nextToken());
			}else{
				avpDetailMap.put(VENDOR_ID, "0");
				avpDetailMap.put(ATTRIBUTE_IDS, attrId);
			}
			
			if(object.get(attrId) instanceof JSONObject){
				avpDetailMap.put(ATTRIBUTE_LIST,parseJsonObject(object.getJSONObject(attrId), mapValue));
			}else{
				avpDetailMap.put(mapValue, object.get(attrId));
			}
			subAvpList.add(avpDetailMap);
		}		
		return subAvpList;
	}
	
	
	private ArrayList<?> parseJsonObject(JSONObject object,String mapValue,ArrayList<?> subAvpList){
		ArrayList<Object> tempSubAvpList = new ArrayList<Object>();
		tempSubAvpList.addAll(subAvpList);
		Iterator<?> subAvpIter = tempSubAvpList.iterator();
		Set<?> set = object.keySet();
		Iterator<?> iterator = set.iterator();
		while(iterator.hasNext()){
			String attrId = (String)iterator.next();
			HashMap avpDetailMap = null;
			while(subAvpIter.hasNext()){
				HashMap hashMap = (HashMap)subAvpIter.next();
				String attributeId = (String) hashMap.get(ATTRIBUTE_IDS);
				String vendorId = (String) hashMap.get(VENDOR_ID);
				if(((vendorId+":"+attributeId).equals(attrId))||(attributeId).equals(attributeId)){
					avpDetailMap = hashMap;
					break;
				}
			}		
			if(object.get(attrId) instanceof JSONObject){
				ArrayList  avpList = (ArrayList)avpDetailMap.get(ATTRIBUTE_LIST);
				avpDetailMap.put(ATTRIBUTE_LIST,parseJsonObject(object.getJSONObject(attrId), mapValue,avpList));
			}else{
				avpDetailMap.put(mapValue, object.get(attrId));
			}
		}
		return subAvpList;
	}
	
	private Map<String, Object> getAttributeInfoMap(String strAttributeId,String ratingField,String defaultValue,List<ValueMappingDetail>  valueMappingList){
		
		Map<String, Object>  attributeMap = new HashMap<String, Object> ();
		
		if(strAttributeId!=null){
			attributeMap.put(ATTRIBUTE_IDS, strAttributeId);
			if(strAttributeId.contains(":")){
				attributeMap.put(VENDOR_ID, strAttributeId.substring(0,strAttributeId.indexOf(":")).trim());
				attributeMap.put(ATTRIBUTE_IDS, strAttributeId.substring(strAttributeId.indexOf(":")+1).trim());
			}else{
				attributeMap.put(VENDOR_ID, "0");
				attributeMap.put(ATTRIBUTE_IDS, strAttributeId.trim());
			}
		}
		if(ratingField!=null){
			if(ratingField.startsWith("{")){	
				JSONObject jsonObject = JSONObject.fromObject(ratingField.trim());
				attributeMap.put(ATTRIBUTE_LIST,parseJsonObject(jsonObject,RATING_FIELD));
			}else{
				attributeMap.put(RATING_FIELD, ratingField.trim());
			}
		}
		if(defaultValue!=null){
			if(defaultValue.startsWith("{")){
				JSONObject jsonObject = JSONObject.fromObject(defaultValue.trim());					
				attributeMap.put(ATTRIBUTE_LIST,parseJsonObject(jsonObject,DEFAULT_VALUE,(ArrayList)attributeMap.get(ATTRIBUTE_LIST)));					
			}else{
				attributeMap.put(DEFAULT_VALUE, defaultValue);
			}
		}
		if(valueMappingList!=null){
			Map diameterRatingValueMap = new HashMap();
			
			for(int k=0;k<valueMappingList.size();k++){
				ValueMappingDetail valueMappingDetail  = valueMappingList.get(k);
				String radiusValue = valueMappingDetail.getAttributeValue();
				String ratingValue = valueMappingDetail.getRating();
				if(radiusValue!=null && radiusValue.trim().length()>0 && ratingValue!=null && ratingValue.trim().length()>0){
					diameterRatingValueMap.put(radiusValue, ratingValue);
				}
			}
			
			attributeMap.put(VALUE_MAPPING, diameterRatingValueMap);
		}
		
		return attributeMap;
	}
	
	
	private void getAttributePolicyMappingDictator( String key, CrestelAttributePolicyMappingDictator attrPolicyMappingDictator,MappingPolicyDetail policy)throws InitializationFailedException {
		
		CrestelAttributePolicyMappingWrapper attrPolicyMappingWrapper = new CrestelAttributePolicyMappingWrapper();
		if(policy.getRequestParamsDetail()!=null){
			List<Map<String, Object>> requestParameterList = new ArrayList<Map<String,Object>>();
			RequestParamsDetail requestParamsDetail = policy.getRequestParamsDetail();
			
			List<AttributeDetailsForRequest> attributeList = requestParamsDetail.getAttributeList();
			if(attributeList!=null){
				for(int j=0;j<attributeList.size();j++){
					AttributeDetailsForRequest attributeDetailsForRequest = attributeList.get(j);
					
					requestParameterList.add(getAttributeInfoMap(attributeDetailsForRequest.getAttributeId(),attributeDetailsForRequest.getRatingField(),attributeDetailsForRequest.getDefaultValue(),attributeDetailsForRequest.getValueMappingList()));
				}
			}
			
			attrPolicyMappingWrapper.setRequestParameterMappingList(requestParameterList);
		}
		if(policy.getResponseParamsDetail()!=null){
			
			List<Map<String, Object>> responseParameterList = new ArrayList<Map<String,Object>>();
			
			ResponseParamsDetail responseParamsDetail = policy.getResponseParamsDetail();
			
			List<AttributeDetailsForResponse>  responseAttributeList = responseParamsDetail.getAttributeList();
			if(responseAttributeList!=null){
				for(int m=0;m<responseAttributeList.size();m++){
					AttributeDetailsForResponse attributeDetailsForResponse = responseAttributeList.get(m);
					responseParameterList.add(getAttributeInfoMap(attributeDetailsForResponse.getAttributeId(),attributeDetailsForResponse.getRatingField(),attributeDetailsForResponse.getDefaultValue(),attributeDetailsForResponse.getValueMappingList()));
				}
			}
			List<CheckedMappingDetail>  responseChekList = responseParamsDetail.getCheckedMappingList();
			if(responseChekList!=null){
				for(int n=0;n<responseChekList.size();n++){
					CheckedMappingDetail checkedMappingDetail = responseChekList.get(n);
					List fieldCheckList = new ArrayList();
					List checkedAttributeList = new ArrayList();
					
					RatingFieldCheckDetail radFieldCheckDetail = checkedMappingDetail.getRatingFieldChecks().getRatingFieldCheckDetail();
					if(radFieldCheckDetail!=null){
						List<FieldDetail>  fieldList = radFieldCheckDetail.getFieldDetailList();
						if(fieldList!=null){
							
							for(int m=0;m<fieldList.size();m++){
								FieldDetail fieldDetail = fieldList.get(m);
								
								String name = fieldDetail.getName();
								String value = fieldDetail.getValue();
								if(name!=null && name.length()>0 && value!=null && value.length()>0){
									HashMap fieldCheckMap = new HashMap();
									fieldCheckMap.put(name, value);
									fieldCheckList.add(fieldCheckMap);
								}
							}
						}
						
					}
					List<AttributeDetailsForResponse>  checkdResponseList = checkedMappingDetail.getAttributeDetailList();
					if(checkdResponseList!=null){
						if(checkdResponseList!=null){
							for(int m=0;m<checkdResponseList.size();m++){
								AttributeDetailsForResponse attributeDetailsForResponse = checkdResponseList.get(m);
								checkedAttributeList.add(getAttributeInfoMap(attributeDetailsForResponse.getAttributeId(),attributeDetailsForResponse.getRatingField(),attributeDetailsForResponse.getDefaultValue(),attributeDetailsForResponse.getValueMappingList()));
							}
						}
						
					}
					
					attrPolicyMappingWrapper.addCheckedMapping(fieldCheckList, checkedAttributeList);
				}
			}
			
			attrPolicyMappingWrapper.setResponseParameterMappingList(responseParameterList);
		}
		
		attrPolicyMappingDictator.addAttributePolicyMappingConfiguration(key, attrPolicyMappingWrapper);
	}

	@XmlTransient
	public Map<String, Object> getConfigMap() {
		return configMap;
	}
	
	public void setConfigMap(Map<String, Object> configMap) {
		this.configMap = configMap;
	}

}
@XmlType(propOrder={})
class MappingPolicyDetail{

	private String id;
	private RequestParamsDetail requestParamsDetail;
	private ResponseParamsDetail responseParamsDetail;

	public MappingPolicyDetail() {
		// TODO Auto-generated constructor stub
		requestParamsDetail = new RequestParamsDetail();
		responseParamsDetail = new ResponseParamsDetail();
	}
	@XmlAttribute(name ="id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlElement(name ="request-parameters")
	public RequestParamsDetail getRequestParamsDetail() {
		return requestParamsDetail;
	}
	public void setRequestParamsDetail(RequestParamsDetail requestParamsDetail) {
		this.requestParamsDetail = requestParamsDetail;
	}
	@XmlElement(name ="response-parameters")
	public ResponseParamsDetail getResponseParamsDetail() {
		return responseParamsDetail;
	}
	public void setResponseParamsDetail(ResponseParamsDetail responseParamsDetail) {
		this.responseParamsDetail = responseParamsDetail;
	}
}
@XmlType(propOrder={})
class RequestParamsDetail{
	private List<AttributeDetailsForRequest> attributeList;

	public RequestParamsDetail() {
		attributeList = new ArrayList<AttributeDetailsForRequest>();
	}
	@XmlElementWrapper(name ="attribute-list")
	@XmlElement(name ="attribute")
	public List<AttributeDetailsForRequest> getAttributeList() {
		return attributeList;
	}
	public void setAttributeList(List<AttributeDetailsForRequest> attributeList) {
		this.attributeList = attributeList;
	}
}

@XmlType(propOrder={})
class ResponseParamsDetail{
	
	private List<AttributeDetailsForResponse> attributeList;
	private List<CheckedMappingDetail> checkedMappingList;

	public ResponseParamsDetail() {
		attributeList = new ArrayList<AttributeDetailsForResponse>();
		checkedMappingList = new ArrayList<CheckedMappingDetail>();

	}
	@XmlElementWrapper(name ="checked-mapping-list")
	@XmlElement(name ="checked-mapping")
	public List<CheckedMappingDetail> getCheckedMappingList() {
		return checkedMappingList;
	}
	public void setCheckedMappingList(List<CheckedMappingDetail> checkedMappingList) {
		this.checkedMappingList = checkedMappingList;
	}
	@XmlElementWrapper(name ="attribute-list")
	@XmlElement(name ="attribute")
	public List<AttributeDetailsForResponse> getAttributeList() {
		return attributeList;
	}
	public void setAttributeList(List<AttributeDetailsForResponse> attributeList) {
		this.attributeList = attributeList;
	}
}

@XmlType(propOrder={})
class AttributeDetailsForRequest{

	private String attributeId;
	private String ratingField;
	private String defaultValue;
	private List<ValueMappingDetail> valueMappingList;

	public AttributeDetailsForRequest() {
		valueMappingList = new ArrayList<ValueMappingDetail>();
	}

	@XmlElementWrapper(name ="value-mapping")
	@XmlElement(name ="value")
	public List<ValueMappingDetail> getValueMappingList() {
		return valueMappingList;
	}
	public void setValueMappingList(List<ValueMappingDetail> valueMappingList) {
		this.valueMappingList = valueMappingList;
	}
	@XmlElement(name ="attribute-ids",type =String.class)
	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	@XmlElement(name ="rating-field",type =String.class)
	public String getRatingField() {
		return ratingField;
	}

	public void setRatingField(String ratingField) {
		this.ratingField = ratingField;
	}
	@XmlElement(name ="default-value",type =String.class)
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}

@XmlType(propOrder={})
class ValueMappingDetail{

	private String attributeValue;
	private String rating;

	public ValueMappingDetail() {
		// TODO Auto-generated constructor stub
	}
	@XmlElement(name ="attribute-value",type = String.class)
	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	@XmlElement(name ="rating",type =String.class)
	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

}

@XmlType(propOrder={})
class CheckedMappingDetail{
	
	private List<AttributeDetailsForResponse>attributeDetailList;
	private RatingFieldChecks ratingFieldChecks;



	public CheckedMappingDetail() {
		// TODO Auto-generated constructor stub
	}

	@XmlElementWrapper(name ="attribute-list")
	@XmlElement(name ="attribute")
	public List<AttributeDetailsForResponse> getAttributeDetailList() {
		return attributeDetailList;
	}

	public void setAttributeDetailList(
			List<AttributeDetailsForResponse> attributeDetailList) {
		this.attributeDetailList = attributeDetailList;
	}
	@XmlElement(name="rating-field-checks")
	public RatingFieldChecks getRatingFieldChecks() {
		return ratingFieldChecks;
	}

	public void setRatingFieldChecks(
			RatingFieldChecks ratingFieldChecks) {
		this.ratingFieldChecks = ratingFieldChecks;
	}
}

@XmlType(propOrder={})
class RatingFieldCheckDetail{
	private List<FieldDetail> fieldDetailList;

	public RatingFieldCheckDetail() {
		this.fieldDetailList = new ArrayList<FieldDetail>();
	}
	@XmlElement(name ="field")
	public List<FieldDetail> getFieldDetailList() {
		return fieldDetailList;
	}

	public void setFieldDetailList(List<FieldDetail> fieldDetailList) {
		this.fieldDetailList = fieldDetailList;
	}

}

@XmlType(propOrder={})
class FieldDetail{
	private String name;
	private String value;

	public FieldDetail() {

	}

	@XmlElement(name ="field-name",type =String.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name ="value",type =String.class)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

@XmlType(propOrder={})
class AttributeDetailsForResponse{

	private String attributeId;
	private String ratingField;
	private String defaultValue;
	private List<ValueMappingDetail> valueMappingList;

	public AttributeDetailsForResponse() {
		this.valueMappingList = new ArrayList<ValueMappingDetail>();
	}
	@XmlElement(name ="attribute-ids",type =String.class)
	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	@XmlElement(name ="rating-field",type =String.class)
	public String getRatingField() {
		return ratingField;
	}

	public void setRatingField(String ratingField) {
		this.ratingField = ratingField;
	}
	@XmlElement(name ="default-value",type =String.class)
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@XmlElementWrapper(name ="value-mapping")
	@XmlElement(name ="value")
	public List<ValueMappingDetail> getValueMappingList() {
		return valueMappingList;
	}
	public void setValueMappingList(List<ValueMappingDetail> valueMappingList) {
		this.valueMappingList = valueMappingList;
	}
}
class RatingFieldChecks{
	private RatingFieldCheckDetail ratingFieldCheckDetail;
	public RatingFieldChecks() {
		ratingFieldCheckDetail= new RatingFieldCheckDetail();
		// TODO Auto-generated constructor stub
	}
	@XmlElement(name ="field-list")
	public RatingFieldCheckDetail getRatingFieldCheckDetail() {
		return ratingFieldCheckDetail;
	}

	public void setRatingFieldCheckDetail(
			RatingFieldCheckDetail ratingFieldCheckDetail) {
		this.ratingFieldCheckDetail = ratingFieldCheckDetail;
	}

}