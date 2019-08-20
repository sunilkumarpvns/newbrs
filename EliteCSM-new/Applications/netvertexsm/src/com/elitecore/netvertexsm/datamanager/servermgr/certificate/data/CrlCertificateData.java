package com.elitecore.netvertexsm.datamanager.servermgr.certificate.data;

import java.security.cert.X509CRLEntry;
import java.sql.Timestamp;
import java.util.Set;

public class CrlCertificateData {

	private java.lang.Long crlCertificateId;
	private String crlCertificateName;    
	private byte[] crlCertificate;        
	private java.lang.Long createdByStaffId;
	private java.lang.Long modifiedByStaffId;
	private Timestamp modifiedDate;
	private String certificateFileName;
	
	private Set<X509CRLEntry> revokedList;
	private String serialNo;
	private String lastUpdate;
	private String nextUpdate;
	private String issuer;
	private String signatureAlgo;
	private Timestamp createDate;
	

	
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public java.lang.Long getCrlCertificateId() {
		return crlCertificateId;
	}
	public void setCrlCertificateId(java.lang.Long crlCertificateId) {
		this.crlCertificateId = crlCertificateId;
	}
	public String getCrlCertificateName() {
		return crlCertificateName;
	}
	public void setCrlCertificateName(String crlCertificateName) {
		this.crlCertificateName = crlCertificateName;
	}
	public byte[] getCrlCertificate() {
		return crlCertificate;
	}
	public void setCrlCertificate(byte[] crlCertificate) {
		this.crlCertificate = crlCertificate;
	}
	public java.lang.Long getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(java.lang.Long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public java.lang.Long getModifiedByStaffId() {
		return modifiedByStaffId;
	}
	public void setModifiedByStaffId(java.lang.Long modifiedByStaffId) {
		this.modifiedByStaffId = modifiedByStaffId;
	}
	public Timestamp getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getCertificateFileName() {
		return certificateFileName;
	}
	public void setCertificateFileName(String certificateFileName) {
		this.certificateFileName = certificateFileName;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public String getNextUpdate() {
		return nextUpdate;
	}
	public void setNextUpdate(String nextUpdate) {
		this.nextUpdate = nextUpdate;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getSignatureAlgo() {
		return signatureAlgo;
	}
	public void setSignatureAlgo(String signatureAlgo) {
		this.signatureAlgo = signatureAlgo;
	}
	public Set<X509CRLEntry> getRevokedList() {
		return revokedList;
	}
	public void setRevokedList(Set<X509CRLEntry> revokedList) {
		this.revokedList = revokedList;
	}	
}
