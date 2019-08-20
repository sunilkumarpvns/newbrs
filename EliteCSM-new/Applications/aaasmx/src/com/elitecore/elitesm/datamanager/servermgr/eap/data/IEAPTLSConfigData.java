/**
 * 
 */
package com.elitecore.elitesm.datamanager.servermgr.eap.data;

import java.util.List;

/**
 * @author pratikchauhan
 *
 */
public interface IEAPTLSConfigData {
	
	public String getEaptlsId() ;
	public void setEaptlsId(String eaptlsId);
	public String getEapId();
	public void setEapId(String eapId);
	public String getCertificateRequest();
	public void setCertificateRequest(String certificateRequest);
	public Long getSessionResumptionLimit();
	public void setSessionResumptionLimit(Long sessionResumptionLimit);
	public Long getSessionResumptionDuration();
	public void setSessionResumptionDuration(Long sessionResumptionDuration);
	public String getDefaultCompressionMethod();
	public void setDefaultCompressionMethod(String defaultCompressionMethod);
	public String getCertificateTypesList();
	public void setCertificateTypesList(String certificateTypesList);
	public String getCiphersuiteList();
	public void setCiphersuiteList(String ciphersuiteList);
	public List<VendorSpecificCertificateData> getVendorSpecificList();
	public void setVendorSpecificList(List<VendorSpecificCertificateData> vendorSpecificList);
	public String getMinTlsVersion();
	public void setMinTlsVersion(String minTlsVersion);
	public String getMaxTlsVersion();
	public void setMaxTlsVersion(String maxTlsVersion);
	public String getServerCertificateId();
	public void setServerCertificateId(String serverCertificateId);
	public String getServerCertificateProfileName();
	public void setServerCertificateProfileName(String serverCertificateProfileName);
	public String getExpiryDate();
	public void setExpiryDate(String expiryDate);
	public String getRevokedCertificate();
	public void setRevokedCertificate(String revokedCertificate);
	public String getMissingClientCertificate();
	public void setMissingClientCertificate(String missingClientCertificate);
	public String getMacValidation();
	public void setMacValidation(String macValidation);
}
