package com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverResult;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.LDAPDatasourceNameAdapter;
import com.elitecore.elitesm.ws.rest.adapter.PasswordDecryptAdapter;
import com.elitecore.elitesm.ws.rest.adapter.SearchScopeAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "ldap-auth-driver")
@XmlType(propOrder={"ldapDsId","expiryDatePattern","passwordDecryptType","searchScope","queryMaxExecTime",
		"maxQueryTimeoutCount","userIdentityAttributes","searchFilter","ldapAuthDriverFieldMapList"})
@ValidObject
public class LDAPAuthDriverData extends BaseData implements Serializable,ILDAPAuthDriverData,Differentiable,Validator{
	
	private static final long serialVersionUID = 1L;
	
	@Expose
	@SerializedName("LDAP Datasource")
	@NotEmpty(message = "LDAP Datasource must be specified")
	private String ldapDsId;

	private String ldapDriverId;
	
	@Expose
	@SerializedName("Expiry Date Pattern")
	@NotNull(message = "Expiry Date Pattern must be specified.")
	@Pattern(regexp = "(?i)(MM/dd/yyyy)", message = "Invalid value for Expiry Date Pattern. Value could be 'MM/dd/yyyy'.")		
	private String expiryDatePattern;	
	
	@Expose
	@SerializedName("Password Decrypt Type")
	@NotNull(message = "Password Decrypt must be specified.")
	@Min(value = 0, message = "Invalid value for Password Decrypt Type. Value could be '0'.")	
	private Long passwordDecryptType;
	
	@Expose
	@SerializedName("Search Scope")
	@NotNull(message = "Search Scope must be specified.")
	@Pattern(regexp = "(?i)(0|1|2)", message = "Invalid value for Search Scope Record. Value could be 'SCOPE_BASE' or 'SCOPE_ONE' or 'SCOPE_SUB'.")
	private String searchScope;
	
	@Expose
	@SerializedName("Query Maximum Execution Time")
	@NotNull(message = "Query Max Exec Time must be specified and it should be numeric.")
	private Long queryMaxExecTime;
	
	@Expose
	@SerializedName("Maximum Query Timeout Count")
	private Long maxQueryTimeoutCount;
	
	@Expose
	@SerializedName("User Identity Attributes")
	private String userIdentityAttributes; 
	
	@Expose
	@SerializedName("Search Filter")
	private String searchFilter;
	
	private String driverInstanceId;	
	
	@NotEmpty(message = "At least one mapping must be specified.")
	private List<LDAPAuthFieldMapData> ldapAuthDriverFieldMapList;
	
	private String auditUId;
	
//This property is check for weather to check
	private boolean checkValidate;
	
	public LDAPAuthDriverData(){
		this.maxQueryTimeoutCount = 100l;
	}
	
	@XmlElement(name = "maximum-query-timeout-count")
	public Long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	
	@XmlTransient
	public String getLdapDriverId() {
		return ldapDriverId;
	}
	public void setLdapDriverId(String ldapDriverId) {
		this.ldapDriverId = ldapDriverId;
	}
	/*public long getDsStatusCheckInterval() {
		return dsStatusCheckInterval;
	}
	public void setDsStatusCheckInterval(long dsStatusCheckInterval) {
		this.dsStatusCheckInterval = dsStatusCheckInterval;
	}*/
	@XmlElement(name = "expiry-date-pattern")
	public String getExpiryDatePattern() {
		return expiryDatePattern;
	}
	public void setExpiryDatePattern(String expiryDatePattern) {
		this.expiryDatePattern = expiryDatePattern;
	}
	
	@XmlElement(name = "password-decrypt-type")
	@XmlJavaTypeAdapter(value = PasswordDecryptAdapter.class)
	public Long getPasswordDecryptType() {
		return passwordDecryptType;
	}
	public void setPasswordDecryptType(Long passwordDecryptType) {
		this.passwordDecryptType = passwordDecryptType;
	}
	
	@XmlElement(name = "query-maximum-execution-time")
	public Long getQueryMaxExecTime() {
		return queryMaxExecTime;
	}
	public void setQueryMaxExecTime(Long queryMaxExecTime) {
		this.queryMaxExecTime = queryMaxExecTime;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElement(name = "ldap-datasource")
	@XmlJavaTypeAdapter(value = LDAPDatasourceNameAdapter.class)
	public String getLdapDsId() {
		return ldapDsId;
	}
	public void setLdapDsId(String ldapDsId) {
		this.ldapDsId = ldapDsId;
	}
	
	@Valid
	@XmlElementWrapper(name = "ldap-field-mappings")
	@XmlElement(name = "ldap-field-mapping")
	public List<LDAPAuthFieldMapData> getLdapAuthDriverFieldMapList() {
		return ldapAuthDriverFieldMapList;
	}
	public void setLdapAuthDriverFieldMapList(
			List<LDAPAuthFieldMapData> ldapAuthDriverFieldMapList) {
		this.ldapAuthDriverFieldMapList = ldapAuthDriverFieldMapList;
	}
	
	@XmlElement(name = "user-identity-attributes")
	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}	
	
	@XmlElement(name = "search-scope")
	@XmlJavaTypeAdapter(value = SearchScopeAdapter.class)
	public String getSearchScope() {
		return searchScope;
	}
	public void setSearchScope(String searchScope) {
		this.searchScope = searchScope;
	}
	
	@XmlElement(name = "search-filter")
	public String getSearchFilter() {
		return searchFilter;
	}
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	
	@XmlTransient
	public boolean isCheckValidate() {
		return checkValidate;
	}
	public void setCheckValidate(boolean checkValidate) {
		this.checkValidate = checkValidate;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("LDAP Datasource", EliteSMReferencialDAO.fetchLDAPDatasourceData(ldapDsId));
		object.put("Expiry Date Pattern", expiryDatePattern);
		object.put("Query Maximum Execution Time(ms)", queryMaxExecTime);
		object.put("Password Decrypt Type", passwordDecryptType);
		
		
		if( searchScope != null){
			if( searchScope.equals("1")){
				object.put("Search Scope", "SCOPE_ONE");
			}else if( searchScope.equals("2")){
				object.put("Search Scope", "SCOPE_SUB");
			}else if( searchScope.equals("0")){
				object.put("Search Scope", "SCOPE_BASE");
			}
		}
		
		object.put("Maximum Query Timeout Count", maxQueryTimeoutCount);
		object.put("User Identity Attributes", userIdentityAttributes);
		object.put("Search Filter", searchFilter);
		if(Collectionz.isNullOrEmpty(ldapAuthDriverFieldMapList) == false){
			JSONObject fields = new JSONObject();
			for (LDAPAuthFieldMapData element : ldapAuthDriverFieldMapList) {
				fields.putAll(element.toJson());
			}
			object.put("Ldap Field Mapping", fields);
		}
		return object;
	}
	
	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if(RestValidationMessages.INVALID.equals(this.ldapDsId)){
			isValid = false;
			RestUtitlity.setValidationMessage(context,"Invalid LDAP Datasource name.");
		}
		
		if(Collectionz.isNullOrEmpty(ldapAuthDriverFieldMapList) == false && checkValidate == false){
			
			StringBuilder duplicateLogicalNames = new StringBuilder();
			StringBuilder invalidLogicalNames = new StringBuilder();
			Set<String> duplicateLogicalNameSet = new HashSet<String>();
			DriverBLManager driverBLManager = new DriverBLManager();
			try {
				List<String> logicalNameList = new ArrayList<String>();
				List<LogicalNameValuePoolData> logicalNameValueDataList = driverBLManager.getLogicalNameValuePoolList();
				if(Collectionz.isNullOrEmpty(logicalNameValueDataList) == false){
					for(LogicalNameValuePoolData logicalNameValuePoolData : logicalNameValueDataList){
						logicalNameList.add(logicalNameValuePoolData.getName());
					}
					
					for(LDAPAuthFieldMapData authFieldMapData : ldapAuthDriverFieldMapList){
						String logicalName = null;
						if(Strings.isNullOrEmpty(authFieldMapData.getLogicalName()) == false) {
							logicalName = authFieldMapData.getLogicalName().trim();
						}
						if(logicalNameList.contains(logicalName)){
							boolean flag = duplicateLogicalNameSet.add(logicalName);
							if(flag == false){
								isValid = false;
								duplicateLogicalNames.append(logicalName+", ");
							}
						}else if (Strings.isNullOrEmpty(logicalName) == false){
							invalidLogicalNames.append(logicalName+", ");
						}
					}
					DriverResult driverResult = new DriverResult();
					List<LDAPAuthFieldMapData> convertedList = getConversionOfValueWithLogicalName(context, ldapAuthDriverFieldMapList,driverResult, logicalNameValueDataList);
					
					if(driverResult.isError()){
						isValid = false;
					}
					
					if(Collectionz.isNullOrEmpty(convertedList) == false) {
						ldapAuthDriverFieldMapList.clear();
						ldapAuthDriverFieldMapList.addAll(convertedList);	
					}
				}
				if(Strings.isNullOrEmpty(duplicateLogicalNames.toString()) == false ){
					duplicateLogicalNames.insert(0, "Duplicate Logical Name(s) : ");
					duplicateLogicalNames.setLength(duplicateLogicalNames.length() - 2);
					isValid = false;
					RestUtitlity.setValidationMessage(context,duplicateLogicalNames.toString());
				}
				if(Strings.isNullOrEmpty(invalidLogicalNames.toString()) == false){
					invalidLogicalNames.insert(0, "Invalid Logical Name(s) : ");
					invalidLogicalNames.setLength(invalidLogicalNames.length() - 2);
					isValid = false;
					RestUtitlity.setValidationMessage(context,invalidLogicalNames.toString());
				}
				return isValid;
			} catch (DataManagerException e) {
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	return isValid;
	}
	
private List<LDAPAuthFieldMapData> getConversionOfValueWithLogicalName(ConstraintValidatorContext context, List<LDAPAuthFieldMapData> ldapAuthDriverFieldMapList, DriverResult driverResult, List<LogicalNameValuePoolData> logicalNameValueDataList) {
		
		List<LDAPAuthFieldMapData> convertedList = new ArrayList<LDAPAuthFieldMapData>();
		for(LDAPAuthFieldMapData ldapAuthFieldMapData : ldapAuthDriverFieldMapList ){
			
		  String ldapAttribute = ldapAuthFieldMapData.getLdapAttribute();
		  
	      if (Strings.isNullOrEmpty(ldapAuthFieldMapData.getLogicalName()) && Strings.isNullOrEmpty(ldapAttribute)) {
		      RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name and ldap attribute must be specified.");
		      driverResult.setError(true);
		  } else if (Strings.isNullOrEmpty(ldapAttribute) && Strings.isNullOrEmpty(ldapAuthFieldMapData.getLogicalName()) == false) {
		      RestUtitlity.setValidationMessage(context, "In the Mapping List, the ldap attribute must be specified for logical-name:[" + ldapAuthFieldMapData.getLogicalName()+ "].");
		      driverResult.setError(true);

		  } else if (Strings.isNullOrEmpty(ldapAuthFieldMapData.getLogicalName()) && Strings.isNullOrEmpty(ldapAttribute) == false) {
		      RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name must be specified for ldap attribute:[" +ldapAttribute+ "].");
		      driverResult.setError(true);
		  }
			
	      if(driverResult.isError() == false) {
	    	  LDAPAuthFieldMapData ldapAuthData = new LDAPAuthFieldMapData();
	    	  for(LogicalNameValuePoolData data: logicalNameValueDataList) {
	    		  if(data.getName().equals(ldapAuthFieldMapData.getLogicalName())) {
	    			  ldapAuthData.setLogicalName(data.getValue());
	    			  ldapAuthData.setDefaultValue(ldapAuthFieldMapData.getDefaultValue());
	    			  ldapAuthData.setNameValuePoolData(ldapAuthFieldMapData.getNameValuePoolData());
	    			  ldapAuthData.setValueMapping(ldapAuthFieldMapData.getValueMapping());
	    			  ldapAuthData.setLdapAttribute(ldapAuthFieldMapData.getLdapAttribute());
	    			  ldapAuthData.setLdapDriverId(ldapAuthFieldMapData.getLdapDriverId());
	    			  ldapAuthData.setLdapFieldMapId(ldapAuthFieldMapData.getLdapFieldMapId());
	    			  convertedList.add(ldapAuthData);
	    			  break;
	    		  }
	    	  }
			}
		}
		return convertedList;
	}
	
}