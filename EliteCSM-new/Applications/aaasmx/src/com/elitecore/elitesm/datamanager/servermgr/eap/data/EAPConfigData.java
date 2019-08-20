package com.elitecore.elitesm.datamanager.servermgr.eap.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
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
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.ws.rest.adapter.NumericAdapter;
import com.elitecore.elitesm.ws.rest.adapter.eapconfig.EAPAuthMethodsAdapter;
import com.elitecore.elitesm.ws.rest.adapter.eapconfig.EAPDefaultNegotiationMethodAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlRootElement(name="eap-configuration")
@ValidObject
@XmlType(propOrder ={"name","description","sessionCleanupInterval","sessionDurationForCleanup","sessionTimeout","mskRevalidationTime",
		"treatInvalidPacketAsFatal","defaultNegiotationMethod","notificationSuccess","notificationFailure","maxEapPacketSize","enabledAuthMethods","eaptlsConfigData","simConfigData",
		"akaConfigData","akaPrimeConfigData"})
public class EAPConfigData extends BaseData implements IEAPConfigData,Serializable,Differentiable,Validator{
	
	private static final long serialVersionUID = 1L;
	private String eapId;
	
	@Expose
	@SerializedName("Name")
	@NotEmpty(message="Name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX,message=RestValidationMessages.NAME_INVALID)
	private String name;
	
	@Expose
	@SerializedName("Description")
	private String description;
	
	@Expose
	@SerializedName("Default Negotiation Method")
	@NotNull(message="Default Negotiation Method must be specified")
	private Long defaultNegiotationMethod;
	
	@Expose
	@SerializedName("Enabled Auth Methods")
	@NotEmpty(message="Enable Auth method must be specified")
	private String enabledAuthMethods;

	@Expose
	@SerializedName("Session Cleanup Interval")
	@NotNull(message="Session Cleanup Interval must be specified")
	@Min(value=0,message="Session Cleanup Interval must be Numeric")
	private Long sessionCleanupInterval;
	
	@Expose
	@SerializedName("Session Duration for Cleanup")
	@NotNull(message="Session Duration for Cleanup must be specified")
	@Min(value=0,message="Session Duration for Cleanup must be Numeric")
	private Long sessionDurationForCleanup;
	
	@Expose
	@SerializedName("Session Timeout")
	@NotNull(message="Session Timeout must be specified")
	@Min(value=0,message="Session Timeout must be Numeric")
	private Long sessionTimeout;
	
	@Expose
	@SerializedName("MSK Revalidation Time")
	@Min(value=0,message="MSK Revalidation Time must be Numeric")
	private Long mskRevalidationTime;
	
	@Expose
	@SerializedName("Treat Invalid Packet as fatal")
	@NotEmpty(message="Treat Invalid Packet as fatal must be specified")
	@Pattern(regexp=RestValidationMessages.TRUE_FALSE_WITH_EMPTY,message="Enter valid Treat Invalid Packet as fatal(true/false)")
	private String treatInvalidPacketAsFatal;
	
	@Expose
	@SerializedName("Max EAP Packet Size")
	@NotNull(message="Max EAP Packet Size must be specified")
	@Min(value=0,message="Max EAP Packet Size must be Numeric")
	private Long maxEapPacketSize;
	
	@Expose
	@SerializedName("Notification Success")
	@NotEmpty(message="Notification Success must be specified")
	@Pattern(regexp=RestValidationMessages.TRUE_FALSE_WITH_EMPTY,message="Enter valid Notification Success(true/false)")
	private String notificationSuccess;
	
	@Expose
	@SerializedName("Notification Failure")
	@NotEmpty(message="Notification Failure must be specified")
	@Pattern(regexp=RestValidationMessages.TRUE_FALSE_WITH_EMPTY,message="Enter valid Notification Failure(true/false)")
	private String notificationFailure;
	
	private String peapVersion;
	private String eapPeapCertificateRequest;

	private String eapTtlsCertificateRequest;
	private Integer peapNegotiationMethod;
	private Integer ttlsNegotiationMethod;
	private String createdByStaffId;
	private Timestamp createDate;
	private String lastModifiedByStaffId;
	private Timestamp lastModifiedDate;
	private Set<EAPTLSConfigData> eapTlsConfigSet;
	private EAPTLSConfigData eaptlsConfigData;
	
	private EAPSimAkaConfigData simConfigData;
	private EAPSimAkaConfigData akaConfigData;
	private EAPSimAkaConfigData akaPrimeConfigData;
	
	private List<String> checkedEnabledMethodsArray;
	private List<String> unCkeckedEnabledMethodsArray;
    
	private String auditUId;
	
	public EAPConfigData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	@XmlElement(name="enable-auth-methods")
	@XmlJavaTypeAdapter(value=EAPAuthMethodsAdapter.class)
	public String getEnabledAuthMethods() {
		return enabledAuthMethods;
	}
	public void setEnabledAuthMethods(String enabledAuthMethods) {
		this.enabledAuthMethods = enabledAuthMethods;
	}
	
	@XmlElement(name="tls-configuration")
	@Valid
	public EAPTLSConfigData getEaptlsConfigData() {
		return eaptlsConfigData;
	}
	public void setEaptlsConfigData(EAPTLSConfigData eaptlsConfigData) {
		this.eaptlsConfigData = eaptlsConfigData;
	}
	
	@XmlTransient
	public String getEapId() {
		return eapId;
	}
	public void setEapId(String eapId) {
		this.eapId = eapId;
	}
	
	@XmlElement(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name="default-negotiation-method")
	@XmlJavaTypeAdapter(value=EAPDefaultNegotiationMethodAdapter.class)
	@Min(value=1,message="Invalid Default Negotiation Method")
	public Long getDefaultNegiotationMethod() {
		return defaultNegiotationMethod;
	}
	public void setDefaultNegiotationMethod(Long defaultNegiotationMethod) {
		this.defaultNegiotationMethod = defaultNegiotationMethod;
	}
	
	@XmlElement(name="treat-invalid-packet-as-fatal")
	public String getTreatInvalidPacketAsFatal() {
		return treatInvalidPacketAsFatal;
	}
	public void setTreatInvalidPacketAsFatal(String treatInvalidPacketAsFatal) {
		this.treatInvalidPacketAsFatal = treatInvalidPacketAsFatal.toLowerCase();
	}
	
	@XmlElement(name="notification-success")
	public String getNotificationSuccess() {
		return notificationSuccess;
	}
	public void setNotificationSuccess(String notificationSuccess) {
		this.notificationSuccess = notificationSuccess.toLowerCase();
	}
	
	@XmlElement(name="notification-failure")
	public String getNotificationFailure() {
		return notificationFailure;
	}
	public void setNotificationFailure(String notificationFailure) {
		this.notificationFailure = notificationFailure.toLowerCase();
	}
	
	@XmlElement(name="max-eap-packet-size")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	public Long getMaxEapPacketSize() {
		return maxEapPacketSize;
	}
	public void setMaxEapPacketSize(Long maxEapPacketSize) {
		this.maxEapPacketSize = maxEapPacketSize;
	}
	
	@XmlElement(name="session-cleanup-interval")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	public Long getSessionCleanupInterval() {
		return sessionCleanupInterval;
	}
	public void setSessionCleanupInterval(Long sessionCleanupInterval) {
		this.sessionCleanupInterval = sessionCleanupInterval;
	}
	
	@XmlElement(name="session-duration-for-cleanup")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	public Long getSessionDurationForCleanup() {
		return sessionDurationForCleanup;
	}
	public void setSessionDurationForCleanup(Long sessionDurationForCleanup) {
		this.sessionDurationForCleanup = sessionDurationForCleanup;
	}
	
	@XmlElement(name="session-timeout")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	public Long getSessionTimeout() {
		return sessionTimeout;
	}
	public void setSessionTimeout(Long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	
	@XmlTransient
	public String getEapTtlsCertificateRequest() {
		return eapTtlsCertificateRequest;
	}
	public void setEapTtlsCertificateRequest(String eapTtlsCertificateRequest) {
		this.eapTtlsCertificateRequest = eapTtlsCertificateRequest;
	}
	
	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	
	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	
	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	@XmlTransient
	public Set<EAPTLSConfigData> getEapTlsConfigSet() {
		return eapTlsConfigSet;
	}
	public void setEapTlsConfigSet(Set<EAPTLSConfigData> eapTlsConfigSet) {
		this.eapTlsConfigSet = eapTlsConfigSet;
	}
	
	@XmlTransient
	public List<String> getCheckedEnabledMethodsArray() {
		return checkedEnabledMethodsArray;
	}
	public void setCheckedEnabledMethodsArray(
			List<String> checkedEnabledMethodsArray) {
		this.checkedEnabledMethodsArray = checkedEnabledMethodsArray;
	}
	
	@XmlTransient
	public List<String> getUnCkeckedEnabledMethodsArray() {
		return unCkeckedEnabledMethodsArray;
	}
	public void setUnCkeckedEnabledMethodsArray(
			List<String> unCkeckedEnabledMethodsArray) {
		this.unCkeckedEnabledMethodsArray = unCkeckedEnabledMethodsArray;
	}
	
	@XmlTransient
	public String getPeapVersion() {
		return peapVersion;
	}
	public void setPeapVersion(String peapVersion) {
		this.peapVersion = peapVersion;
	}
	
	@XmlTransient
	public String getEapPeapCertificateRequest() {
		return eapPeapCertificateRequest;
	}
	public void setEapPeapCertificateRequest(String eapPeapCertificateRequest) {
		this.eapPeapCertificateRequest = eapPeapCertificateRequest;
	}
	
	@XmlElement(name="sim-configuration")
	@Valid
	public EAPSimAkaConfigData getSimConfigData() {
		return simConfigData;
	}
	public void setSimConfigData(EAPSimAkaConfigData simConfigData) {
		this.simConfigData = simConfigData;
	}
	
	@XmlElement(name="aka-configuration")
	@Valid
	public EAPSimAkaConfigData getAkaConfigData() {
		return akaConfigData;
	}
	public void setAkaConfigData(EAPSimAkaConfigData akaConfigData) {
		this.akaConfigData = akaConfigData;
	}
	
	@XmlTransient
	public Integer getPeapNegotiationMethod() {
		return peapNegotiationMethod;
	}
	public void setPeapNegotiationMethod(Integer peapNegotiationMethod) {
		this.peapNegotiationMethod = peapNegotiationMethod;
	}
	
	@XmlTransient
	public Integer getTtlsNegotiationMethod() {
		return ttlsNegotiationMethod;
	}
	public void setTtlsNegotiationMethod(Integer ttlsNegotiationMethod) {
		this.ttlsNegotiationMethod = ttlsNegotiationMethod;
	}
	
	@XmlElement(name="msk-revalidation-time")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	public Long getMskRevalidationTime() {
		return mskRevalidationTime;
	}
	public void setMskRevalidationTime(Long mskRevalidationTime) {
		this.mskRevalidationTime = mskRevalidationTime;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Session Cleanup Interval(secs)", sessionCleanupInterval);
		object.put("Session Duration for Cleanup(secs)", sessionDurationForCleanup);
		object.put("Session Timeout (secs)", sessionTimeout);
		object.put("MSK Revalidation Time (sec)", mskRevalidationTime);
		
		if( treatInvalidPacketAsFatal != null && treatInvalidPacketAsFatal.equalsIgnoreCase("true")){
			object.put("Treat Invalid Packet as fatal", "True");
		}else if( treatInvalidPacketAsFatal != null && treatInvalidPacketAsFatal.equalsIgnoreCase("false") ){
			object.put("Treat Invalid Packet as fatal", "False");
		}
		
		if( notificationSuccess != null && notificationSuccess.equalsIgnoreCase("true")){
			object.put("Notification Success", "True");
		}else if( notificationSuccess != null && notificationSuccess.equalsIgnoreCase("false") ){
			object.put("Notification Success", "False");
		}
		
		if( notificationFailure != null && notificationFailure.equalsIgnoreCase("true")){
			object.put("Notification Failure", "True");
		}else if( notificationFailure != null && notificationFailure.equalsIgnoreCase("false") ){
			object.put("Notification Failure", "False");
		}
		
		object.put("Max EAP Packet Size", maxEapPacketSize);
		
		//Upadate Supported Method
		object.put("Supported Method", getSupportedMethod());
		
		List<String> enableAuthMethodsLst = Arrays.asList(enabledAuthMethods.split(","));
		//Update TLS Configuration
		if(enableAuthMethodsLst.contains(EAPConfigConstant.TLS_STR) && eaptlsConfigData!=null){
			object.put("TLS Configuration", eaptlsConfigData.toJson());
		}
		
		//Update TTLS Configuration
		if(enableAuthMethodsLst.contains(EAPConfigConstant.TTLS_STR)){
			object.put("TTLS Configuration", getTTLSConfiguration());
		}
		
		//Update PEAP Configuration
		if(enableAuthMethodsLst.contains(EAPConfigConstant.PEAP_STR)){
			object.put("PEAP Configuration", getPEAPConfiguration());
		}
		
		//Update EAP SIM Configuration
		if(enableAuthMethodsLst.contains(EAPConfigConstant.SIM_STR) && simConfigData!=null){
			object.put("EAP SIM Configuration", simConfigData.toJson());
		}
		
		//Update EAP AKA Configuration
		if(enableAuthMethodsLst.contains(EAPConfigConstant.AKA_STR) && akaConfigData!=null){
			object.put("EAP AKA Configuration", akaConfigData.toJson());
		}
		
		//Update EAP AKA' Configuration
		if(enableAuthMethodsLst.contains(EAPConfigConstant.AKA_PRIME_STR) &&akaPrimeConfigData!=null){
			object.put("EAP AKA Prime Configuration", akaPrimeConfigData.toJson());
		}
		return object;
	}
	
	private JSONObject getSupportedMethod() {
		JSONObject object = new JSONObject();
		object.put("Default Negotiation Method", getDefaultNegotiationMethod(defaultNegiotationMethod));
		object.put("Enabled Auth Methods",getEnabledAuthMethodNames(enabledAuthMethods));
		return object;
	}	
	
	private String getEnabledAuthMethodNames( String enabledAuthMethodsNames ) {
		String enabledAuthMethodNames = "";
		String [] authMethods = enabledAuthMethodsNames.split(",");
		
		for( String authMethodDigit :  authMethods ){
			 if( authMethodDigit != null){
				 authMethodDigit =  authMethodDigit.trim();
				 if(authMethodDigit.equals("013")){
					 enabledAuthMethodNames += "TLS"; 
				 }else if(authMethodDigit.equals("021")){
					 enabledAuthMethodNames += "TTLS"; 
				 }else if(authMethodDigit.equals("004")){
					 enabledAuthMethodNames += "MD5"; 
				 }else if(authMethodDigit.equals("025")){
					 enabledAuthMethodNames += "PEAP"; 
				 }else if(authMethodDigit.equals("018")){
					 enabledAuthMethodNames += "SIM"; 
				 }else if(authMethodDigit.equals("023")){
					 enabledAuthMethodNames += "AKA"; 
				 }else if(authMethodDigit.equals("006")){
					 enabledAuthMethodNames += "GTC"; 
				 }else if(authMethodDigit.equals("026")){
					 enabledAuthMethodNames += "MS-CHAPv2"; 
				 }else if(authMethodDigit.equals("050")){
					 enabledAuthMethodNames += "AKA'"; 
				 }
				 enabledAuthMethodNames +=",";
			 }
		}
		enabledAuthMethodNames = enabledAuthMethodNames.substring(0, enabledAuthMethodNames.length()-1);
		return enabledAuthMethodNames;
	}
	private Object getTTLSConfiguration() {
		JSONObject object = new JSONObject();
		
		object.put("TTLS Certificate Request", eapTtlsCertificateRequest);
		object.put("TTLS Negotiation Method", getTTLSNegotiationMethodName(ttlsNegotiationMethod));
		return object;
	}
	
	private Object getPEAPConfiguration() {
		JSONObject object = new JSONObject();
		
		object.put("PEAP Certificate Request", eapPeapCertificateRequest);
		object.put("PEAP Version", peapVersion);
		object.put("PEAP Negotiation Method", getPEAPNegotiationMethodName(peapNegotiationMethod));
		return object;
	}
	
	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	@XmlElement(name="aka-prime-configuration")
	@Valid
	public EAPSimAkaConfigData getAkaPrimeConfigData() {
		return akaPrimeConfigData;
	}
	public void setAkaPrimeConfigData(EAPSimAkaConfigData akaPrimeConfigData) {
		this.akaPrimeConfigData = akaPrimeConfigData;
	}
	
	private String getDefaultNegotiationMethod(Long defaultNegiotationMethodValue) {
		String defaultNegotiationMethodName = "";
		
		if( defaultNegiotationMethodValue == 4L ) {
			defaultNegotiationMethodName = "MD5-Challenge";
		}else if (defaultNegiotationMethodValue == 6L ){
			defaultNegotiationMethodName = "GTC";
		}else if( defaultNegiotationMethodValue == 13L ){
			defaultNegotiationMethodName = "TLS";
		}else if( defaultNegiotationMethodValue == 18L ){
			defaultNegotiationMethodName = "SIM";
		}else if( defaultNegiotationMethodValue == 21L ){
			defaultNegotiationMethodName = "TTLS";
		}else if( defaultNegiotationMethodValue == 23L ){
			defaultNegotiationMethodName = "AKA";
		}else if( defaultNegiotationMethodValue == 50L ){
			defaultNegotiationMethodName = "AKA'";
		}else if( defaultNegiotationMethodValue == 25L ){
			defaultNegotiationMethodName = "PEAP";
		}else if( defaultNegiotationMethodValue == 26L ){
			defaultNegotiationMethodName = "MS-CHAPv2";
		}
		
		return defaultNegotiationMethodName;
	}
	
	private String getTTLSNegotiationMethodName(Integer ttlsNegotiationMethodValue) {
		String ttlsNegotiationMethodName = "";
		if( ttlsNegotiationMethodValue != null ){
			if( ttlsNegotiationMethodValue == 4){
				ttlsNegotiationMethodName = "EAP-MD5";
			}else if( ttlsNegotiationMethodValue == 6 ){
				ttlsNegotiationMethodName = "EAP-GTC";
			}else if( ttlsNegotiationMethodValue == 26 ){
				ttlsNegotiationMethodName = "EAP-MsCHAPv2";
			}
		}
		return ttlsNegotiationMethodName;
	}
	
	private String getPEAPNegotiationMethodName(Integer peapNegotiationMethodValue) {
		String peapNegotiationMethodName = "";
		if( peapNegotiationMethodValue != null ){
			if( peapNegotiationMethodValue == 4){
				peapNegotiationMethodName = "EAP-MD5";
			}else if( peapNegotiationMethodValue == 6 ){
				peapNegotiationMethodName = "EAP-GTC";
			}else if( peapNegotiationMethodValue == 26 ){
				peapNegotiationMethodName = "EAP-MsCHAPv2";
			}
		}
		return peapNegotiationMethodName;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if(Strings.isNullOrBlank(enabledAuthMethods) == false){
			
			List<String> enableAuthMethodsLst = Arrays.asList(enabledAuthMethods.split(","));
			
			//check for invalid enable auth method
			Set<String> invalidEnableAuthMethods = getInvalidEnableAuthMethod(enabledAuthMethods);
			
			if(Collectionz.isNullOrEmpty(invalidEnableAuthMethods) == false){
				RestUtitlity.setValidationMessage(context, "Invalid Enable Auth Methods: "+invalidEnableAuthMethods);
				isValid = false;
			}
			
			
			if(defaultNegiotationMethod != null){
				
				String defaultNegotiationMethodName = EAPConfigUtils.convertDefaultNegotiationMethodToLabel(defaultNegiotationMethod);
				
				String defaultNegotiationMethodValueStr = EAPConfigUtils.convertEnableAuthMethodToAuthTypeId(defaultNegotiationMethodName, true);
				
				if(Strings.isNullOrBlank(defaultNegotiationMethodValueStr)== false){
					if(enableAuthMethodsLst.contains(defaultNegotiationMethodValueStr) == false){
						RestUtitlity.setValidationMessage(context, "Please Provide "+defaultNegotiationMethodName+" Enabled Auth Method");
						isValid = false;
					}
				}
				
			}
			
		}
		
				
		return isValid;
	}
	
	
	private Set<String> getInvalidEnableAuthMethod(String enableAuthMethods){
		Set<String> invalidEnableAuthMethodSet = new TreeSet<String>();
		List<String> enableAuthMethodLst = Arrays.asList(enableAuthMethods.split(","));
		
		for(String enableAuthMethod : enableAuthMethodLst){
			if(Strings.isNullOrBlank(enableAuthMethod) == false){
				String enableAuthMethodName = EAPConfigUtils.convertEnableAuthMethodToLabelString(enableAuthMethod);
				if(Strings.isNullOrBlank(enableAuthMethodName)){
					invalidEnableAuthMethodSet.add(enableAuthMethod);
				}
			}
		}
		return invalidEnableAuthMethodSet;
	}
}
