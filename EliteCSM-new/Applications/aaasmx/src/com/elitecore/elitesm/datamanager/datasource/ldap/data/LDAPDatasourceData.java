package com.elitecore.elitesm.datamanager.datasource.ldap.data;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.LDAPDatasourceVersionAdapter;
import com.elitecore.elitesm.ws.rest.adapter.NumericAdapter;
import com.elitecore.elitesm.ws.rest.adapter.StringToLongAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@XmlRootElement(name="ldap-datasource")
@XmlType(propOrder = {"name","address","administrator","password","timeout","statusCheckDuration","minimumPool","maximumPool","userDNPrefix",
		"ldapSizeLimit","ldapVersion","searchDnDetailList"})
@ValidObject
public class LDAPDatasourceData extends BaseData implements ILDAPDatasourceData,Cloneable,Differentiable,Validator{
	
	private static final java.util.regex.Pattern IPV4_IPV6_REGEX = java.util.regex.Pattern.compile(RestValidationMessages.IPV4_IPV6_REGEX);
	
	private String ldapDsId;
	
	@Expose
	@SerializedName("Name")
	@NotEmpty(message="LDAP Datasource Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX,message = RestValidationMessages.NAME_INVALID)
	private String name;
	
	@Expose
	@SerializedName("Address")
	@NotEmpty(message="Address must be specified")
	private String address;
	
	@Expose
	@SerializedName("Administrator")
	@NotEmpty(message="Administrator must be specified")
	private String administrator;
	
	@Expose
	@SerializedName("Timeout(ms)")
	@NotNull(message="Timeout must be specified")
	@Min(value=0,message="Timeout value must be numeric")
	@XmlJavaTypeAdapter(value = NumericAdapter.class)
	private Long timeout;
	
	@Expose
	@SerializedName("Status Check Duration (Sec.)")
	@NotNull(message = "Status Check Duration must be specified")
	@Min(value=0,message="Status Check Duration must be numeric")
	private Long statusCheckDuration;
	
	@Expose
	@SerializedName("Minimum Connection")
	@NotNull(message="Minimum pool must be specified")
	@Min(value=0,message="Minimum Pool value must be valid")
	private Long minimumPool;
	
	@Expose
	@SerializedName("Maximum Connection")
	@NotNull(message="Maximum pool must be specified")
	@Min(value=0,message="Maximum Pool value must be valid")
	private Long maximumPool;
	
	@Expose
	@SerializedName("User DN Prefix")
	@NotEmpty(message="User DN Prefix must be specified")
	private String userDNPrefix;
	
	@Expose
	@SerializedName("LDAP Size Limit")
	@NotNull(message="LDAP Size Limit must be specified")
	@Min(value=0,message="LDAP Size Limit value must be numeric")
	@XmlJavaTypeAdapter(value = StringToLongAdapter.class)
	private Long ldapSizeLimit;
	
	@Expose
	@SerializedName("LDAP Version")
	@NotNull(message = "LDAP version value must be specified")
	private Integer ldapVersion;
	
	private String password;
	
	private String commonStatusId;
	private String lastModifiedByStaffId;
	private String createdByStaffId;
	private Timestamp lastModifiedDate;
	private Timestamp createDate;
	private Set ldapBaseDnDetail;
	
	@NotEmpty(message = "Atleast one DN must be specified")
	private List searchDnDetailList;
	
	private String auditUId;
	
	@XmlElement(name = "address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@XmlElement(name = "administrator",type = String.class,defaultValue ="admin")
	public String getAdministrator() {
		return administrator;
	}
	
	@XmlTransient
	public String getCommonStatusId() {
		return commonStatusId;
	}
	
	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}
	
	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	
	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	
	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	@XmlElement(name = "ldap-version")
	@Min(value = 0 , message = "LDAP version value must be valid")
	@XmlJavaTypeAdapter(value = LDAPDatasourceVersionAdapter.class)
	public Integer getLdapVersion() {
		return ldapVersion;
	}
	public void setLdapVersion(Integer ldapVersion) {
		this.ldapVersion = ldapVersion;
	}
	
	@XmlElement(name = "ldap-size-limit")
	public Long getLdapSizeLimit() {
		return ldapSizeLimit;
	}
	
	@XmlElement(name = "maximum-pool-size",defaultValue ="5")
	public Long getMaximumPool() {
		return maximumPool;
	}
	
	@XmlElement(name = "minimum-pool-size",defaultValue ="2")
	public Long getMinimumPool() {
		return minimumPool;
	}
	
	@XmlElement(name = "name",type = String.class,defaultValue ="admin")
	public String getName() {
		return name;
	}
	
	@NotEmpty(message = "password must be specified")
	public String getPassword() {
		return password;
	}	
	
	@XmlElement(name = "timeout")
	public Long getTimeout() {
		return timeout;
	}
	
	@XmlElement(name = "user-dn-prefix",type = String.class,defaultValue ="uid=")
	public String getUserDNPrefix() {
		return userDNPrefix;
	}
	
	@XmlTransient
	public String getLdapDsId() {
		return ldapDsId;		
	}
	
	@XmlTransient
	public Set getLdapBaseDnDetail() {
		return ldapBaseDnDetail;
	}
	public void setAdministrator(String administrator) {
		this.administrator = administrator;		
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;		
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;		
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;		
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
		
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;		
	}
	public void setLdapSizeLimit(Long ldapSizeLimit) {
		this.ldapSizeLimit = ldapSizeLimit;	
	}
	public void setMaximumPool(Long maximumPool) {
		this.maximumPool = maximumPool;		
	}
	public void setMinimumPool(Long minimumPool) {
		this.minimumPool = minimumPool;		
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPassword(String password) {
		this.password = password;		
	}	
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
		
	}
	public void setUserDNPrefix(String userDNPrefix) {
		this.userDNPrefix = userDNPrefix;	
	}
	public void setLdapDsId(String ldapDsId) {
		this.ldapDsId = ldapDsId;
	}
	
	public void setLdapBaseDnDetail(Set ldapBaseDnDetail) {	
		this.ldapBaseDnDetail = ldapBaseDnDetail;
	}
	
	@XmlElement(name="ldap-base-dn-details",type=LDAPBaseDnDetailData.class)
	public List getSearchDnDetailList() {
		return searchDnDetailList;
	}

	public void setSearchDnDetailList(List searchDnDetailList) {
		this.searchDnDetailList = searchDnDetailList;
	}
	
	@XmlElement(name = "status-check-duration", defaultValue="120")
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	
	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	@Override
    public String toString() {
        StringWriter out = new StringWriter();
        TabbedPrintWriter writer = new TabbedPrintWriter(out);
        writer.print(StringUtility.fillChar("", 30, '-'));
        writer.print(this.getClass().getName());
        writer.println(StringUtility.fillChar("", 30, '-'));
        writer.incrementIndentation();
        writer.println("ldapDsId               :"+ldapDsId);
        writer.println("Name                 :" + name);
        writer.println("timeout      :" + timeout);
        writer.println("ldapSizeLimit :" + ldapSizeLimit);
        writer.println("ldapVersion       :" + ldapVersion);
        writer.println("administrator    :" + administrator);
        writer.println("password                   :"+password);
        writer.println("userDNPrefix               :" + userDNPrefix);
        writer.println("minimumPool               :" + minimumPool);
        writer.println("maximumPool               :" + maximumPool);
        writer.println("commonStatusId               :"+commonStatusId);
        writer.println("statusCheckDuration       :" + statusCheckDuration);
        writer.println("address    :" + address);
        writer.println("password                   :"+password);
        writer.println("userDNPrefix               :" + userDNPrefix);
        writer.println("minimumPool               :" + minimumPool);
        writer.println("maximumPool               :" + maximumPool);
        
        writer.decrementIndentation();
        writer.println(StringUtility.fillChar("", 80, '-'));
        writer.close();
        return out.toString();
    }

	@Override
	public JSONObject toJson(){
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Address", address);
		object.put("Administrator", administrator);
		object.put("Timeout (ms)", timeout);
		object.put("Status Check Duration (Sec.)", statusCheckDuration);
		object.put("Minimum Connection", minimumPool);
		object.put("Maximum Connection", maximumPool);
		object.put("User DN Prefix", userDNPrefix);
		object.put("LDAP Size Limit", ldapSizeLimit);
		object.put("LDAP Version", ldapVersion);
		if(searchDnDetailList != null){
			JSONArray array = new JSONArray();
			for (Object element : searchDnDetailList) {
				array.add(((LDAPBaseDnDetailData)element).getSearchBaseDn());
			}
			object.put("BaseDn Name", array);
		}
		return object;
		
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {

		if(ldapVersion != null ){
			if(ldapVersion < 2 && ldapVersion > -1){
				RestUtitlity.setValidationMessage(context, "minimum ldap version value is 2");
				return false;
			} else if(ldapVersion >3 && ldapVersion > -1){
				RestUtitlity.setValidationMessage(context, "maximum ldap version value is 3");
				return false;
			}
		}

		if(minimumPool !=null && maximumPool !=null && minimumPool > maximumPool && minimumPool > -1 && maximumPool > -1){
			RestUtitlity.setValidationMessage(context, "Maximum Pool value must be greater then Minimum Pool value");
			return false;
		}

		if(Strings.isNullOrBlank(address) == false){
			if(! validateIP(address)){
				RestUtitlity.setValidationMessage(context, "Please Enter Valid Address (HOST:PORT)");
				return false;
			}
		}
		return true;
	}
	
	private boolean validateIP(String address){
		
		String[] ipAddress = address.split(":");
			if(Strings.isNullOrBlank(ipAddress[0])){
				return false;
			} else if(ipAddress.length > 1 && Strings.isNullOrBlank(ipAddress[1])){
				return false;
			} else if(IPV4_IPV6_REGEX.matcher(ipAddress[0]).matches()){
				if(validatePort(ipAddress[1])){
					return true;
				}
			}
		return false;
	}
	
	private boolean validatePort(String port){
		
		if(isNumeric(port) == true){
			if(Integer.parseInt(port) >= 0 && Integer.parseInt(port)<=65535){
				return true;
			}
		}
		return false;
	}	
	
	public static boolean isNumeric(String s) {  
	    return s != null && s.matches("[+]?\\d+");  
	}
}