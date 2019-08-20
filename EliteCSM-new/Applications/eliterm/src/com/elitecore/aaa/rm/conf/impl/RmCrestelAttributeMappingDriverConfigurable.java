package com.elitecore.aaa.rm.conf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "attribute-policy-mapping")		
@ConfigurationProperties(moduleName ="CRESTEL_ATTR_MAPPING_CONFIGURABLE",readWith = XMLReader.class, synchronizeKey ="CRESTEL_ATTRIBUTE_POLICY_MAPPING_DRIVER")
@XMLProperties(name = "crestel-rating-properties", schemaDirectories = {"system","schema"}, configDirectories = {"conf", "services", "drivers"})		
public class RmCrestelAttributeMappingDriverConfigurable extends Configurable {
	
	public static final String ATTRIBUTE_POLICY_MAPPING="attribute-policy-mapping";
	public static final String AUTH_MAPPING="auth-mapping";
	public static final String REAUTH_MAPPING="reauth-mapping";
	public static final String ACCT_MAPPING="acct-mapping";
//	public static final String ACCT_STOP_MAPPING="acct-stop-mapping";
//	public static final String ACCT_INTERIM_MAPPING="acct-interim-mapping";
	public static final String CALL_METHOD="call-method";
	public static final String CHECK_ITEM_RE="check-item-re";
	public static final String MAPPING="mapping";
	public static final String API_DETAIL = "API_DETAIL";
	public static final String RATING_RESPONSE = "RATING_RESPONSE";
	public static final String DUMMY_DRIVER = "DUMMY_DRIVER";
	public static final String CONFIG_GROUP="config-group";
	public static final String INSTANCE_NUMBER="instance-number";
	public static final String RNC_RESPONSE_CODES="rnc-response-codes";
	public static final String CODE="code";
	public static final String RNC_CODE="rnc-code";
	public static final String RADIUS_REPLY_MESSAGE="radius-reply-message";
	public static final String DATASOURCE_NAME="datasource-name";
	public static final String POLICY_ENABLE="policy-enable";
	public static final String PREPAID_CHARGING_SERVICE="prepaidchargingservice";
	public static final String RATING_FIELD_MAPPING = "rating-field-mapping";

	

	private PrepaidSystemParentDetail prePrepaidSystemDetail;
	private RatingFieldMappindDetail ratingFieldMappindDetail;
	private RatingResponseDetail ratingResponseDetail;
	private boolean dummyDriver=true;
	private boolean policyEnabled;

	private String datasourceName;

	private Map configMap;

	public RmCrestelAttributeMappingDriverConfigurable() {
		this.prePrepaidSystemDetail = new PrepaidSystemParentDetail();
		this.ratingFieldMappindDetail = new RatingFieldMappindDetail();
		this.ratingResponseDetail = new RatingResponseDetail();
		this.configMap = new HashMap();
	}

	@PostRead
	public void postReadProcessing() {
		
		
		Map mappingPolicyConfiguration = new HashMap();
		
		if(prePrepaidSystemDetail!=null){
			Map systemDetailMap = new HashMap();
			List<Proprty> properList = this.prePrepaidSystemDetail.getPrepaidSystemDetail().getAPIDetail().getProperties();
			if(properList!=null){
				int numOfProperty = properList.size();
				for(int i=0;i<numOfProperty;i++){
					Proprty proprty = properList.get(i);
					systemDetailMap.put(proprty.getName(), proprty.getValue());
				}
			}
			//systemDetailMap.put(INSTANCE_NUMBER, this.prePrepaidSystemDetail.getPrepaidSystemDetail().getAPIDetail().getInstanceNumber());
			mappingPolicyConfiguration.put(API_DETAIL,systemDetailMap);
		}
		
		if(ratingFieldMappindDetail!=null){
			Map dccaMapping = new HashMap();
			
			AuthMappingDetail tempAuthMappingDetail = ratingFieldMappindDetail.getAuthMappingDetail();
			if(tempAuthMappingDetail!=null){
				List mappingList = new ArrayList();
				List<ConfigDetail>  configList = tempAuthMappingDetail.getConfigList();
				if(configList!=null && configList.size()>0){
					int numOfConfigList = configList.size();
					for(int k=0;k<numOfConfigList;k++){
						ConfigDetail configDetail = configList.get(k);
						Map mappingConfig = new HashMap<String,String>();
						mappingConfig.put(CHECK_ITEM_RE, configDetail.getCheckItem());
						mappingConfig.put(MAPPING, configDetail.getMapping());
						mappingConfig.put(CALL_METHOD, configDetail.getCallMethod());
						mappingList.add(mappingConfig);
					}
					dccaMapping.put(AUTH_MAPPING, mappingList);
				}
				
			}
			
			ReAuthMappingDetail tempReAuthMappingDetail = ratingFieldMappindDetail.getReAuthMappingDetail();
			if(tempReAuthMappingDetail!=null){
				List mappingList = new ArrayList();
				List<ConfigDetail>  configList = tempReAuthMappingDetail.getConfigList();
				if(configList!=null && configList.size()>0){
					int numOfConfigList = configList.size();
					for(int k=0;k<numOfConfigList;k++){
						ConfigDetail configDetail = configList.get(k);
						Map mappingConfig = new HashMap<String,String>();
						mappingConfig.put(CHECK_ITEM_RE, configDetail.getCheckItem());
						mappingConfig.put(MAPPING, configDetail.getMapping());
						mappingConfig.put(CALL_METHOD, configDetail.getCallMethod());
						mappingList.add(mappingConfig);
					}
					dccaMapping.put(REAUTH_MAPPING, mappingList);
				}
				
			}
			
			AccountingMappingDetail tempAccountingMappingDetail = ratingFieldMappindDetail.getAccountingMappingDetail();
			if(tempAccountingMappingDetail!=null){
				List mappingList  = new ArrayList();
				List<ConfigDetail>  configList = tempAccountingMappingDetail.getConfigList();
				if(configList!=null && configList.size()>0){
					int numOfConfigList = configList.size();
					for(int k=0;k<numOfConfigList;k++){
						ConfigDetail configDetail = configList.get(k);
						Map mappingConfig = new HashMap<String,String>();
						mappingConfig.put(CHECK_ITEM_RE, configDetail.getCheckItem());
						mappingConfig.put(MAPPING, configDetail.getMapping());
						mappingConfig.put(CALL_METHOD, configDetail.getCallMethod());
						mappingList.add(mappingConfig);
					}
					dccaMapping.put(ACCT_MAPPING, mappingList);
				}
				
			}
			
			mappingPolicyConfiguration.put(RATING_FIELD_MAPPING, dccaMapping);
		}
		
		if(this.ratingResponseDetail!=null){
			Map ratingResponseMap = new HashMap();
			List<ParamDetail> parameters = ratingResponseDetail.getParameters();
			if(parameters!=null){
				int numOfParameters = parameters.size();
				for(int m=0;m<numOfParameters;m++){
					ParamDetail paramDetail = parameters.get(m);
					String ratingField = paramDetail.getRatingField();
					String ratingValue = paramDetail.getRatingValue();
					if(ratingField!=null && ratingField.length()>0 && ratingValue!=null && ratingValue.length()>0){
						ratingResponseMap.put(ratingField, ratingValue);
					}
				}
			}
			mappingPolicyConfiguration.put(RATING_RESPONSE, ratingResponseMap);
		}
		mappingPolicyConfiguration.put(DUMMY_DRIVER, getIsDummyDriver());
		mappingPolicyConfiguration.put(POLICY_ENABLE, getIsPolicyEnabled());
		mappingPolicyConfiguration.put(DATASOURCE_NAME, getDatasourceName());
		
		this.configMap = mappingPolicyConfiguration;

	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}
	
	@XmlElement(name="datasource-name",type=String.class)
	public String getDatasourceName() {
		return datasourceName;
	}

	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}
	
	@XmlElement(name="dummy-driver",type=boolean.class,defaultValue="true")
	public boolean getIsDummyDriver() {
		return dummyDriver;
	}

	public void setIsDummyDriver(boolean dummyDriver) {
		this.dummyDriver = dummyDriver;
	}
	
	@XmlElement(name="policy-enable",type=boolean.class)
	public boolean getIsPolicyEnabled() {
		return policyEnabled;
	}

	public void setIsPolicyEnabled(boolean policyEnabled) {
		this.policyEnabled = policyEnabled;
	}
	
	@XmlTransient
	public Map getConfigMap() {
		return configMap;
	}

	public void setConfigMap(Map configMap) {
		this.configMap = configMap;
	}
	
	@XmlElement(name="rating-response")
	public RatingResponseDetail getRatingResponseDetail() {
		return ratingResponseDetail;
	}

	public void setRatingResponseDetail(RatingResponseDetail ratingResponseDetail) {
		this.ratingResponseDetail = ratingResponseDetail;
	}
	
	@XmlElement(name="rating-field-mapping")
	public RatingFieldMappindDetail getRatingFieldMappindDetail() {
		return ratingFieldMappindDetail;
	}

	public void setRatingFieldMappindDetail(
			RatingFieldMappindDetail ratingFieldMappindDetail) {
		this.ratingFieldMappindDetail = ratingFieldMappindDetail;
	}

	@XmlElement(name = "prepaid-system")
	public PrepaidSystemParentDetail getPrepaidSystemParentDetail() {
		return prePrepaidSystemDetail;
	}

	public void setPrepaidSystemParentDetail(
			PrepaidSystemParentDetail prePrepaidSystemDetail) {
		this.prePrepaidSystemDetail = prePrepaidSystemDetail;
	}

}

@XmlType(propOrder = {})
class PrepaidSystemDetail {
	private ApiDetails apiDetail;
	

	public PrepaidSystemDetail() {
		apiDetail = new ApiDetails();
	}

	@XmlElement(name = "jndi-properties")
	public ApiDetails getAPIDetail() {
		return apiDetail;
	}

	public void setAPIDetail(ApiDetails apiDetail) {
		this.apiDetail = apiDetail;
	}

}
@XmlType(propOrder = {})
class RatingFieldMappindDetail {
	private AuthMappingDetail authMappingDetail;
	private AccountingMappingDetail accountingMappingDetail;
	private ReAuthMappingDetail reAuthMappingDetail;

	public RatingFieldMappindDetail() {
		this.accountingMappingDetail = new AccountingMappingDetail();
		this.authMappingDetail = new AuthMappingDetail();
		this.reAuthMappingDetail = new ReAuthMappingDetail();
	}

	@XmlElement(name="reauth-mapping")
	public ReAuthMappingDetail getReAuthMappingDetail() {
		return reAuthMappingDetail;
	}

	public void setReAuthMappingDetail(ReAuthMappingDetail reAuthMappingDetail) {
		this.reAuthMappingDetail = reAuthMappingDetail;
	}

	@XmlElement(name = "auth-mapping")
	public AuthMappingDetail getAuthMappingDetail() {
		return authMappingDetail;
	}

	public void setAuthMappingDetail(AuthMappingDetail authMappingDetail) {
		this.authMappingDetail = authMappingDetail;
	}

	@XmlElement(name = "acct-mapping")
	public AccountingMappingDetail getAccountingMappingDetail() {
		return accountingMappingDetail;
	}

	public void setAccountingMappingDetail(
			AccountingMappingDetail accountingMappingDetail) {
		this.accountingMappingDetail = accountingMappingDetail;
	}

}

@XmlType(propOrder = {})
class AuthMappingDetail {
	private List<ConfigDetail> configList;

	public AuthMappingDetail() {
		configList = new ArrayList<ConfigDetail>();
	}
	
	@XmlElement(name="config-group")
	public List<ConfigDetail> getConfigList() {
		return configList;
	}

	public void setConfigList(List<ConfigDetail> configList) {
		this.configList = configList;
	}
	
}
@XmlType(propOrder = {})
class ReAuthMappingDetail {
	private List<ConfigDetail> configList;

	public ReAuthMappingDetail() {
		configList = new ArrayList<ConfigDetail>();
	}
	
	@XmlElement(name="config-group")
	public List<ConfigDetail> getConfigList() {
		return configList;
	}

	public void setConfigList(List<ConfigDetail> configList) {
		this.configList = configList;
	}
	
}


@XmlType(propOrder = {})
class AccountingMappingDetail {
	private List<ConfigDetail> configList;

	public AccountingMappingDetail() {
		configList = new ArrayList<ConfigDetail>();
	}
	
	@XmlElement(name="config-group")
	public List<ConfigDetail> getConfigList() {
		return configList;
	}

	public void setConfigList(List<ConfigDetail> configList) {
		this.configList = configList;
	}

}

@XmlType(propOrder = {})
class ConfigDetail {

	private String checkItem;
	private String mapping;
	private String callMethod;

	public ConfigDetail() {
	}

	@XmlElement(name = "check-item-re", type = String.class)
	public String getCheckItem() {
		return checkItem;
	}

	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}

	@XmlElement(name = "mapping", type = String.class)
	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	@XmlElement(name = "call-method", type = String.class)
	public String getCallMethod() {
		return callMethod;
	}

	public void setCallMethod(String callMethod) {
		this.callMethod = callMethod;
	}

}

@XmlType(propOrder = {})
class RatingResponseDetail{
	private List<ParamDetail> parameters;
	private boolean dummy = true;

	public RatingResponseDetail() {
		this.parameters = new ArrayList<ParamDetail>();
	}
	
	@XmlElement(name="parameter")
	public List<ParamDetail> getParameters() {
		return parameters;
	}

	public void setParameters(List<ParamDetail> parameters) {
		this.parameters = parameters;
	}
	
	@XmlAttribute(name="dummy")
	public boolean getIsDummy() {
		return dummy;
	}

	public void setIsDummy(boolean dummy) {
		this.dummy = dummy;
	}
	
}

@XmlType(propOrder = {})
class ParamDetail{
	private String ratingField;
	private String ratingValue;
	public ParamDetail() {
	}	
	
	@XmlElement(name="rating-field",type =String.class)
	public String getRatingField() {
		return ratingField;
	}
	public void setRatingField(String ratingField) {
		this.ratingField = ratingField;
	}
	@XmlElement(name="rating-value",type =String.class)
	public String getRatingValue() {
		return ratingValue;
	}
	public void setRatingValue(String ratingValue) {
		this.ratingValue = ratingValue;
	}

	
}

@XmlType(propOrder={})
class Proprty {

	private String name;
	private String value;
	
	public Proprty() {
	}
	
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String property) {
		this.name = property;
	}

	@XmlAttribute(name="value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}

@XmlType(propOrder = {})
class ApiDetails{
	private List<Proprty> properties;
	//private String instanceNumber;
	
	public ApiDetails() {
		this.properties = new ArrayList<Proprty>();
	}
	
	/*@XmlElement(name="instance-number",type=String.class)
	public String getInstanceNumber() {
		return instanceNumber;
	}
	public void setInstanceNumber(String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}*/
	
	@XmlElement(name="property")
	public List<Proprty> getProperties() {
		return properties;
	}

	public void setProperties(List<Proprty> properties) {
		this.properties = properties;
	}
}
@XmlType(propOrder={})
class PrepaidSystemParentDetail{

	private PrepaidSystemDetail prepaidSystemDetail ;
	public PrepaidSystemParentDetail() {
	}
	@XmlElement(name = "api-detail")
	public PrepaidSystemDetail getPrepaidSystemDetail() {
		return prepaidSystemDetail;
	}
	public void setPrepaidSystemDetail(PrepaidSystemDetail prepaidSystemDetail) {
		this.prepaidSystemDetail = prepaidSystemDetail;
	}
		
}
