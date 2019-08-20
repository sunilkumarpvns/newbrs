package com.elitecore.elitesm.web.servermgr.eap.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.VendorSpecificCertificateData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateEAPTLSDetailsForm extends BaseWebForm {
	
	private String eaptlsId;
	private String eapId;
	private String certificateRequest="false";
	private long sessionResumptionLimit=2;
	private long sessionResumptionDuration=5000;
	private String defaultCompressionMethod;
	private String certificateTypesList;
	private String ciphersuiteList;
	private String minTlsVersion;
	private String maxTlsVersion;
	private List<String> cipherSuitList;
	private String serverCertificateId;
	private String cipherSuites;
	private String[] listCipherSuites;
	private String serverCertificateProfileName;
	private List<ServerCertificateData> serverCertificateDataList;
	private List<String> lstCipherSuitesList;
    private String serverCertificateName;
    private List<String> lstTLSVersion;
    private List<String> remainingCipherList;
    
    private String expiryDate;
	private String revokedCertificate;
	private String missingClientCertificate;
	private String macValidation;
	
	private List<VendorSpecificCertificateData> vendorSpecificList = new ArrayList<VendorSpecificCertificateData>();
	//attribute of vendor spec
	private long id;
	
	private String oui="000000*";
	private java.lang.Long serverCerticateIdForVSC;
	
	//ttls certificate request
	private String ttlscertificateRequest="false";
	private Integer ttlsNegotiationMethod;
	private String peapCertificateRequest="false";
	private Integer peapNegotiationMethod;
	private String peapVersion="0";
	//certificate type list
	private boolean dss;
	private boolean dss_dh;
	private boolean rsa=true;
	private boolean rsa_dh;
	
   // tls/ttls type
   
   private String typeVal;
   private String action;
   private String enabledAuthMethods;
   private String auditUId;
   private String name;
   
	public String getTtlscertificateRequest() {
		return ttlscertificateRequest;
	}
	public void setTtlscertificateRequest(String ttlscertificateRequest) {
		this.ttlscertificateRequest = ttlscertificateRequest;
	}
	private int itemIndex;
	private String checkAction;
	
	
	
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	
	public String getCheckAction() {
		return checkAction;
	}
	public void setCheckAction(String checkAction) {
		this.checkAction = checkAction;
	}
	public String getEaptlsId() {
		return eaptlsId;
	}
	public void setEaptlsId(String eaptlsId) {
		this.eaptlsId = eaptlsId;
	}
	public String getEapId() {
		return eapId;
	}
	public void setEapId(String eapId) {
		this.eapId = eapId;
	}
	public String getCertificateRequest() {
		return certificateRequest;
	}
	public void setCertificateRequest(String certificateRequest) {
		this.certificateRequest = certificateRequest;
	}
	public long getSessionResumptionLimit() {
		return sessionResumptionLimit;
	}
	public void setSessionResumptionLimit(long sessionResumptionLimit) {
		this.sessionResumptionLimit = sessionResumptionLimit;
	}
	public long getSessionResumptionDuration() {
		return sessionResumptionDuration;
	}
	public void setSessionResumptionDuration(long sessionResumptionDuration) {
		this.sessionResumptionDuration = sessionResumptionDuration;
	}
	public String getDefaultCompressionMethod() {
		return defaultCompressionMethod;
	}
	public void setDefaultCompressionMethod(String defaultCompressionMethod) {
		this.defaultCompressionMethod = defaultCompressionMethod;
	}
	public String getCertificateTypesList() {
		return certificateTypesList;
	}
	public void setCertificateTypesList(String certificateTypesList) {
		this.certificateTypesList = certificateTypesList;
	}
	public String getCiphersuiteList() {
		return ciphersuiteList;
	}
	public void setCiphersuiteList(String ciphersuiteList) {
		this.ciphersuiteList = ciphersuiteList;
	}
	public List<VendorSpecificCertificateData> getVendorSpecificList() {
		return vendorSpecificList;
	}
	public void setVendorSpecificList(
			List<VendorSpecificCertificateData> vendorSpecificList) {
		this.vendorSpecificList = vendorSpecificList;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getOui() {
		return oui;
	}
	public void setOui(String oui) {
		this.oui = oui;
	}
	public boolean isDss() {
		return dss;
	}
	public void setDss(boolean dss) {
		this.dss = dss;
	}
	public boolean isDss_dh() {
		return dss_dh;
	}
	public void setDss_dh(boolean dss_dh) {
		this.dss_dh = dss_dh;
	}
	public boolean isRsa() {
		return rsa;
	}
	public void setRsa(boolean rsa) {
		this.rsa = rsa;
	}
	public boolean isRsa_dh() {
		return rsa_dh;
	}
	public void setRsa_dh(boolean rsa_dh) {
		this.rsa_dh = rsa_dh;
	}
	
	public String getTypeVal() {
		return typeVal;
	}
	public void setTypeVal(String typeVal) {
		this.typeVal = typeVal;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getEnabledAuthMethods() {
		return enabledAuthMethods;
	}
	public void setEnabledAuthMethods(String enabledAuthMethods) {
		this.enabledAuthMethods = enabledAuthMethods;
	}
	
	public String getPeapCertificateRequest() {
		return peapCertificateRequest;
	}
	public void setPeapCertificateRequest(String peapCertificateRequest) {
		this.peapCertificateRequest = peapCertificateRequest;
	}
	public String getPeapVersion() {
		return peapVersion;
	}
	public void setPeapVersion(String peapVersion) {
		this.peapVersion = peapVersion;
	}
	public Integer getTtlsNegotiationMethod() {
		return ttlsNegotiationMethod;
	}
	public void setTtlsNegotiationMethod(Integer ttlsNegotiationMethod) {
		this.ttlsNegotiationMethod = ttlsNegotiationMethod;
	}
	public Integer getPeapNegotiationMethod() {
		return peapNegotiationMethod;
	}
	public void setPeapNegotiationMethod(Integer peapNegotiationMethod) {
		this.peapNegotiationMethod = peapNegotiationMethod;
	}
	public String getMinTlsVersion() {
		return minTlsVersion;
	}
	public void setMinTlsVersion(String minTlsVersion) {
		this.minTlsVersion = minTlsVersion;
	}
	public String getMaxTlsVersion() {
		return maxTlsVersion;
	}
	public void setMaxTlsVersion(String maxTlsVersion) {
		this.maxTlsVersion = maxTlsVersion;
	}
	public String getServerCertificateId() {
		return serverCertificateId;
	}
	public void setServerCertificateId(String serverCertificateId) {
		this.serverCertificateId = serverCertificateId;
	}
	public String getCipherSuites() {
		return cipherSuites;
	}
	public void setCipherSuites(String cipherSuites) {
		this.cipherSuites = cipherSuites;
	}
	public String[] getListCipherSuites() {
		return listCipherSuites;
	}
	public void setListCipherSuites(String[] listCipherSuites) {
		this.listCipherSuites = listCipherSuites;
	}
	public String getServerCertificateProfileName() {
		return serverCertificateProfileName;
	}
	public void setServerCertificateProfileName(String serverCertificateProfileName) {
		this.serverCertificateProfileName = serverCertificateProfileName;
	}
	public List<ServerCertificateData> getServerCertificateDataList() {
		return serverCertificateDataList;
	}
	public void setServerCertificateDataList(
			List<ServerCertificateData> serverCertificateDataList) {
		this.serverCertificateDataList = serverCertificateDataList;
	}
	public java.lang.Long getServerCerticateIdForVSC() {
		return serverCerticateIdForVSC;
	}
	public void setServerCerticateIdForVSC(java.lang.Long serverCerticateIdForVSC) {
		this.serverCerticateIdForVSC = serverCerticateIdForVSC;
	}
	public List<String> getLstCipherSuitesList() {
		return lstCipherSuitesList;
	}
	public void setLstCipherSuitesList(List<String> lstCipherSuitesList) {
		this.lstCipherSuitesList = lstCipherSuitesList;
	}
	public String getServerCertificateName() {
		return serverCertificateName;
	}
	public void setServerCertificateName(String serverCertificateName) {
		this.serverCertificateName = serverCertificateName;
	}
	public List<String> getLstTLSVersion() {
		return lstTLSVersion;
	}
	public void setLstTLSVersion(List<String> lstTLSVersion) {
		this.lstTLSVersion = lstTLSVersion;
	}
	public List<String> getCipherSuitList() {
		return cipherSuitList;
	}
	public void setCipherSuitList(List<String> cipherSuitList) {
		this.cipherSuitList = cipherSuitList;
	}
	public List<String> getRemainingCipherList() {
		return remainingCipherList;
	}
	public void setRemainingCipherList(List<String> remainingCipherList) {
		this.remainingCipherList = remainingCipherList;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getRevokedCertificate() {
		return revokedCertificate;
	}
	public void setRevokedCertificate(String revokedCertificate) {
		this.revokedCertificate = revokedCertificate;
	}
	public String getMissingClientCertificate() {
		return missingClientCertificate;
	}
	public void setMissingClientCertificate(String missingClientCertificate) {
		this.missingClientCertificate = missingClientCertificate;
	}
	public String getMacValidation() {
		return macValidation;
	}
	public void setMacValidation(String macValidation) {
		this.macValidation = macValidation;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
