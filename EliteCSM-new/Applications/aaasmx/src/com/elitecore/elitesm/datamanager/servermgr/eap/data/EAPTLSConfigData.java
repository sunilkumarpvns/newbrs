/**
 * 
 */
package com.elitecore.elitesm.datamanager.servermgr.eap.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.EAPConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.eapconfig.EAPConfigUtils;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.NumericAdapter;
import com.elitecore.elitesm.ws.rest.adapter.eapconfig.EAPCertificateTypeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.eapconfig.EAPServerCertificateAdapter;
import com.elitecore.elitesm.ws.rest.adapter.eapconfig.TLSCipherSuitesAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author pratikchauhan
 *
 */
@ValidObject
@XmlType(propOrder ={"minTlsVersion","maxTlsVersion","serverCertificateId","certificateRequest","ciphersuiteList","certificateValidation","sessionResumptionLimit","sessionResumptionDuration","defaultCompressionMethod",
		"certificateTypesList","ttlsConfiguration","peapConfiguration","vendorSpecificList"})
public class EAPTLSConfigData extends BaseData implements IEAPTLSConfigData,Serializable,Differentiable,Validator{
	
	private String eaptlsId;
	private String eapId;
	
	@NotEmpty(message="TLS Certificate Request must be specified")
	private String certificateRequest;
	
	@NotNull(message="Session Resumption Limit must be specified")
	private Long sessionResumptionLimit;
	
	@NotNull(message="Session Resumption Duration must be specified")
	private Long sessionResumptionDuration;
	
	private String defaultCompressionMethod;
	
	@NotEmpty(message="At least one Certificate Type must be specified")
	private String certificateTypesList;
	
	private String ciphersuiteList;
	private List<VendorSpecificCertificateData> vendorSpecificList;
	
	@NotEmpty(message="Minimum TLS version must be specified")
	private String minTlsVersion;
	@NotEmpty(message="Maximum TLS version must be specified")
	private String maxTlsVersion;
	
	private String serverCertificateId;
	private String serverCertificateProfileName;
	
	private String expiryDate;
	private String revokedCertificate;
	private String missingClientCertificate;
	private String macValidation;
	
	private TLSCertificateValidation certificateValidation;
	
	private EAPTTLSConfiguration ttlsConfiguration;
	
	private EAPPeapConfiguration peapConfiguration;
	
	private static Set<String> tlsversions = new TreeSet<String>();
	static {
		tlsversions.add(EAPConfigConstant.TLSv_1);
		tlsversions.add(EAPConfigConstant.TLSv_1_1);
		tlsversions.add(EAPConfigConstant.TLSv_1_2);
	}
	
	@XmlElement(name="ttls-configuration")
	@Valid
	public EAPTTLSConfiguration getTtlsConfiguration() {
		return ttlsConfiguration;
	}
	public void setTtlsConfiguration(EAPTTLSConfiguration ttlsConfiguration) {
		this.ttlsConfiguration = ttlsConfiguration;
	}
	
	@XmlElement(name="peap-configuration")
	@Valid
	public EAPPeapConfiguration getPeapConfiguration() {
		return peapConfiguration;
	}
	public void setPeapConfiguration(EAPPeapConfiguration peapConfiguration) {
		this.peapConfiguration = peapConfiguration;
	}
	@XmlElement(name="certificate-validations")
	@Valid
	public TLSCertificateValidation getCertificateValidation() {
		return certificateValidation;
	}
	public void setCertificateValidation(TLSCertificateValidation certificateValidation) {
		this.certificateValidation = certificateValidation;
	}
	
	@XmlElementWrapper(name="vendor-specific-certificates")
	@XmlElement(name="vendor-specific-certificate")
	@Valid
	public List<VendorSpecificCertificateData> getVendorSpecificList() {
		return vendorSpecificList;
	}
	public void setVendorSpecificList(
			List<VendorSpecificCertificateData> vendorSpecificList) {
		this.vendorSpecificList = vendorSpecificList;
	}
	
	@XmlTransient
	public String getEaptlsId() {
		return eaptlsId;
	}
	public void setEaptlsId(String eaptlsId) {
		this.eaptlsId = eaptlsId;
	}
	
	@XmlTransient
	public String getEapId() {
		return eapId;
	}
	public void setEapId(String eapId) {
		this.eapId = eapId;
	}
	
	@XmlElement(name="tls-certificate-request")
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX,message="Enter valid TLS Certificate Request(true/false)")
	public String getCertificateRequest() {
		return certificateRequest;
	}
	public void setCertificateRequest(String certificateRequest) {
		this.certificateRequest = certificateRequest.toLowerCase();
	}
	
	@XmlElement(name="session-resumption-limit")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	@Min(value=0,message="Session Resumption Limit must be Numeric")
	public Long getSessionResumptionLimit() {
		return sessionResumptionLimit;
	}
	public void setSessionResumptionLimit(Long sessionResumptionLimit) {
		this.sessionResumptionLimit = sessionResumptionLimit;
	}
	
	@XmlElement(name="session-resumption-duration")
	@XmlJavaTypeAdapter(value=NumericAdapter.class)
	@Min(value=0,message="Session Resumption Duration must be Numeric")
	public Long getSessionResumptionDuration() {
		return sessionResumptionDuration;
	}
	public void setSessionResumptionDuration(Long sessionResumptionDuration) {
		this.sessionResumptionDuration = sessionResumptionDuration;
	}
	
	@XmlElement(name="default-compression-method")
	public String getDefaultCompressionMethod() {
		return defaultCompressionMethod;
	}
	public void setDefaultCompressionMethod(String defaultCompressionMethod) {
		this.defaultCompressionMethod = defaultCompressionMethod;
	}
	
	@XmlElement(name="certificate-types")
	@XmlJavaTypeAdapter(value = EAPCertificateTypeAdapter.class)
	public String getCertificateTypesList() {
		return certificateTypesList;
	}
	public void setCertificateTypesList(String certificateTypesList) {
		this.certificateTypesList = certificateTypesList;
	}
	
	@XmlElement(name="ciphersuites")
	@XmlJavaTypeAdapter(value=TLSCipherSuitesAdapter.class)
	public String getCiphersuiteList() {
		return ciphersuiteList;
	}
	public void setCiphersuiteList(String ciphersuiteList) {
		this.ciphersuiteList = ciphersuiteList;
	}
	
	@XmlElement(name="min-tls-version")
	@Pattern (regexp = RestValidationMessages.REGEX_TLS_VERSION,message="Enter valid Minimum TLS version(TLSv1,TLSv1.1,TLSv1.2)")
	public String getMinTlsVersion() {
		return minTlsVersion;
	}
	public void setMinTlsVersion(String minTlsVersion) {
		this.minTlsVersion = minTlsVersion;
	}
	
	@XmlElement(name="max-tls-version")
	@Pattern (regexp = RestValidationMessages.REGEX_TLS_VERSION,message="Enter valid Maximum TLS version(TLSv1,TLSv1.1,TLSv1.2)")
	public String getMaxTlsVersion() {
		return maxTlsVersion;
	}
	public void setMaxTlsVersion(String maxTlsVersion) {
		this.maxTlsVersion = maxTlsVersion;
	}
	
	@XmlElement(name="server-certificate-profile")
	@XmlJavaTypeAdapter(value=EAPServerCertificateAdapter.class)
	public String getServerCertificateId() {
		return serverCertificateId;
	}
	public void setServerCertificateId(String serverCertificateId) {
		this.serverCertificateId = serverCertificateId;
	}
	
	@XmlTransient
	public String getServerCertificateProfileName() {
		return serverCertificateProfileName;
	}
	public void setServerCertificateProfileName(String serverCertificateProfileName) {
		this.serverCertificateProfileName = serverCertificateProfileName;
	}
	
	@XmlTransient
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@XmlTransient
	public String getRevokedCertificate() {
		return revokedCertificate;
	}
	public void setRevokedCertificate(String revokedCertificate) {
		this.revokedCertificate = revokedCertificate;
	}
	
	@XmlTransient
	public String getMissingClientCertificate() {
		return missingClientCertificate;
	}
	public void setMissingClientCertificate(String missingClientCertificate) {
		this.missingClientCertificate = missingClientCertificate;
	}
	
	@XmlTransient
	public String getMacValidation() {
		return macValidation;
	}
	public void setMacValidation(String macValidation) {
		this.macValidation = macValidation;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Minimum TLS Version", minTlsVersion);
		object.put("Maximum TLS Version", maxTlsVersion);
		object.put("Server Certificate Profile", ( serverCertificateId == null ) ? "NONE" : EliteSMReferencialDAO.fetchServerCertificateDetails(serverCertificateId));
		object.put("TLS Certificate Request", certificateRequest);
		object.put("CipherSuite List", EliteSMReferencialDAO.fetchCipherSuitList(ciphersuiteList));
		
		JSONObject certificateValidation = new JSONObject();
		certificateValidation.put("Expiry Date", expiryDate);
		certificateValidation.put("Revoked Certificate", revokedCertificate);
		certificateValidation.put("Missing Client Certificate", missingClientCertificate);
		certificateValidation.put("MAC Validation", macValidation);
		object.put("Certificate Validation", certificateValidation);
		
		object.put("Session Resumption Limit", sessionResumptionLimit);
		object.put("Session Resumption Duration", sessionResumptionDuration);
		object.put("Certificate Type", getServerCertificateTypeNames(certificateTypesList));
		if(vendorSpecificList!=null){
			JSONArray array = new JSONArray();
			for (VendorSpecificCertificateData element : vendorSpecificList) {
				array.add(element.toJson());
			}
			object.put("Vendor Specific Certificate", array);
		}
		return object;
	}
	
	private String getServerCertificateTypeNames(String certificateTypesListNames) {
		String serverCertificateTypeList = "";
		String [] serverTypes = certificateTypesListNames.split(",");
		for(String serverType :  serverTypes ){
			if(serverType.equalsIgnoreCase("1")){
				serverCertificateTypeList += "RSA";
			}else if( serverType.equalsIgnoreCase("2")){
				serverCertificateTypeList += "DSS";
			}else if( serverType.equalsIgnoreCase("3")){
				serverCertificateTypeList += "RSA-DH";
			}else if( serverType.equalsIgnoreCase("4")){
				serverCertificateTypeList += "DSS-DH";
			}
			serverCertificateTypeList += ",";
		}
		serverCertificateTypeList = serverCertificateTypeList.substring(0, serverCertificateTypeList.length()-1);
		return serverCertificateTypeList;
		
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		Boolean isValid = true;
		
		if("0".equals(this.serverCertificateId)){
			this.serverCertificateId = null;
		}
		
		if(RestValidationMessages.INVALID.equals(this.serverCertificateId)){
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Invalid Server Certificate Profile name");
		}
		
		if(Strings.isNullOrBlank(this.minTlsVersion) == false && Strings.isNullOrBlank(maxTlsVersion) == false){
			
			if(tlsversions.contains(this.minTlsVersion) && tlsversions.contains(this.maxTlsVersion)){
				Boolean isValidTlsVersionSelection = EAPConfigUtils.isMaxTLSVersionGreaterThanOrEqualToMinTLSVersion(this.minTlsVersion, this.maxTlsVersion);
				if(isValidTlsVersionSelection == false){
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Maximum TLS Version must be greater than or equals to Minimum TLS Version");
				} else{
					//validate cipher suite based in Minimum and Maximum TLS Version
					if(Strings.isNullOrBlank(ciphersuiteList) == false){
						TreeSet<String> invalidCipherSuites = getTLSVersionSpecificUnsupportedCipherSuiteList(this.minTlsVersion,this.maxTlsVersion,this.ciphersuiteList);
						if(Collectionz.isNullOrEmpty(invalidCipherSuites) == false){
							isValid = false	;
							RestUtitlity.setValidationMessage(context, "Invalid Cipher Suites :"+invalidCipherSuites);
						}
					}
				}
			}
		
		}
		
		//check if duplicate vendor identifier in vendor specified certificate
		if(Collectionz.isNullOrEmpty(vendorSpecificList) == false){
			List<String> duplicateVendorIdentifers = isDuplicateVendorIdentierInVendorSpecifiedCertificate(vendorSpecificList);
			if(Collectionz.isNullOrEmpty(duplicateVendorIdentifers) == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Vendor Identifier must be unique, Duplicate Vendor Identifiers : "+duplicateVendorIdentifers);
			}
		}
		
		//check invalid certificate type list
		if(Strings.isNullOrBlank(certificateTypesList) == false){
			Set<String> invalidCertificateTypes = getInvalidCertificateTypes(certificateTypesList);
			if(Collectionz.isNullOrEmpty(invalidCertificateTypes) == false){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Invalid Certificate Types: "+ invalidCertificateTypes);
			}
		}
		
		return isValid;
	}
	
	private List<String> isDuplicateVendorIdentierInVendorSpecifiedCertificate(List<VendorSpecificCertificateData> vendorSpecificCertificateDatas){
		Set<String> vendorIdentifiers = new LinkedHashSet<String>();
		List<String> duplicateVendorIdentifiers = new ArrayList<String>();
		for(VendorSpecificCertificateData vendorSpecificCertificateData : vendorSpecificCertificateDatas){
			if(vendorSpecificCertificateData != null) {
				String ouiValue = vendorSpecificCertificateData.getOui();
				if(Strings.isNullOrBlank(ouiValue) == false){
					boolean isAdded = vendorIdentifiers.add(ouiValue);
					
					if(isAdded == false){
						//duplicate entry
						duplicateVendorIdentifiers.add(ouiValue);
					}
				}
			}
		}
		return duplicateVendorIdentifiers;
	}
	
	
	private Set<String> getInvalidCertificateTypes(String certificateTypes){
		List<String> certificateTypesLst = Arrays.asList(certificateTypes.split(","));
		Set<String> invalidCertificateType = new TreeSet<String>();
		
		for(String certificateType : certificateTypesLst){
			if(Strings.isNullOrBlank(certificateType) == false){
				String CertificateTypeName = EAPConfigUtils.getCertificateTypeName(certificateType);
				if(Strings.isNullOrBlank(CertificateTypeName)){
					invalidCertificateType.add(certificateType);
				}
			}
		}
		
		return invalidCertificateType;
	}
	private  TreeSet<String> getTLSVersionSpecificUnsupportedCipherSuiteList(String minTlsVersionValue, String maxTlsVersionValue, String cipherSuiteStr){
		ProtocolVersion minTLSVer=ProtocolVersion.fromVersion(minTlsVersionValue);
		ProtocolVersion maxTLSVer=ProtocolVersion.fromVersion(maxTlsVersionValue);
		List<CipherSuites> cipherSuitesList=new ArrayList<CipherSuites>(CipherSuites.getSupportedCipherSuites(minTLSVer,maxTLSVer));
		return getInvalidCipherSuites(cipherSuiteStr, getValidCipherSuiteCodes(cipherSuitesList));
	}
	public static List<String> getValidCipherSuiteCodes(List<CipherSuites> cipherSuites){
		List<String> validCipherSuitesCodesLst = new LinkedList<String>();
		if(Collectionz.isNullOrEmpty(cipherSuites)== false){
			for(CipherSuites cipherSuite : cipherSuites){
				validCipherSuitesCodesLst.add(String.valueOf(cipherSuite.code));
			}
		}
		return validCipherSuitesCodesLst;
	}
	
	private  TreeSet<String> getInvalidCipherSuites(String cipherSuitesStr,List<String> ValidCipherSuiteCodes){
		TreeSet<String> invalidCipherSuite = new TreeSet<String>();
		List<String> cipherSuiteLst = Arrays.asList(cipherSuitesStr.split(","));
		
		if(Collectionz.isNullOrEmpty(cipherSuiteLst) == false){
			for(String cipherSuite : cipherSuiteLst){
				if(EliteUtility.isNumeric(cipherSuite.trim())){
					if(Strings.isNullOrBlank(cipherSuite) == false){
						if(ValidCipherSuiteCodes.contains(cipherSuite.trim()) == false){
							//get cipher suite name from code
							String cipherSuiteName = getCipherSuitesNameByKey(Integer.parseInt(cipherSuite));
							invalidCipherSuite.add(cipherSuiteName);
						}
					}
				}else{
					invalidCipherSuite.add(cipherSuite.trim());
				}
			}
		}
		return invalidCipherSuite;
	}
	
	private String getCipherSuitesNameByKey(Integer cipherSuiteCode){
		CipherSuites[] cipherSuites= CipherSuites.values();
			for(CipherSuites cipherSuit:cipherSuites){
				if(cipherSuit.code == cipherSuiteCode){
					return cipherSuit.name();
				}
		}
		return "";
	}
	
	public static String convertCipherSuitesNamesToCipherSuiteCodes(String cipherSuiteNames){
		Map<String,Integer> cipherSuiteDetails = getCipherSuiteDetailMap();
		List<String> cipherSuiteList;
		StringBuilder cipherSuiteIds =new StringBuilder();
		if(Strings.isNullOrBlank(cipherSuiteNames) == false){
			cipherSuiteList = Arrays.asList(cipherSuiteNames.split(","));
			
			for(String cipherSuiteName : cipherSuiteList){
				if(Strings.isNullOrBlank(cipherSuiteName)== false){
					if(cipherSuiteDetails.containsKey(cipherSuiteName.trim())){
						cipherSuiteIds.append(cipherSuiteDetails.get(cipherSuiteName.trim())+",");
					}else{
						cipherSuiteIds.append(cipherSuiteName.trim()+",");
					}
				}
			}
		}
		String finalCipherSuites = cipherSuiteIds.substring(0,cipherSuiteIds.length()-1);
		return finalCipherSuites;
	}
	
	public static String convertCipherSuiteCodeToCipherSuiteName(String cipherSuiteCodes){
		CipherSuites[] cipherSuites= CipherSuites.values();
		List<String> cipherSuiteList;
		StringBuilder cipherSuiteNames =new StringBuilder();
		if(Strings.isNullOrBlank(cipherSuiteCodes) == false){
			cipherSuiteList = Arrays.asList(cipherSuiteCodes.split(","));
			
			for(String cipherSuiteCode : cipherSuiteList){
				if(Strings.isNullOrBlank(cipherSuiteCode)== false){
					for(CipherSuites cipherSuit:cipherSuites){
						
						if((cipherSuit.code == Integer.parseInt(cipherSuiteCode))){
							cipherSuiteNames.append(cipherSuit.name()+",");
						}
						
					}
				}
			}
			
		}
		String finalCipherSuites = cipherSuiteNames.substring(0,cipherSuiteNames.length()-1);
		return finalCipherSuites;
	}
	
	public static Map<String,Integer> getCipherSuiteDetailMap(){
		CipherSuites[] cipherSuites= CipherSuites.values();
		HashMap<String,Integer> cipherSuiteDetails = new HashMap<String,Integer>();
		for(CipherSuites cipherSuit:cipherSuites){
			cipherSuiteDetails.put(cipherSuit.name(),cipherSuit.code);
		}
		return cipherSuiteDetails;
	}
	
	public static Integer getCipherSuiteKeyByName(String value){
		
		CipherSuites[] cipherSuites= CipherSuites.values();
		for(CipherSuites cipherSuit:cipherSuites){
			if(cipherSuit.name().equals(value)){
				return cipherSuit.code;
			}
		}
		return 0;
	}
}
