package com.elitecore.netvertexsm.datamanager.servermgr.certificate.data;

import java.sql.Timestamp;

public class TrustedCertificateData {

	private java.lang.Long trustedCertificateId;
	private String trustedCertificateName;    
	private byte[] trustedCertificate;        
	private java.lang.Long createdByStaffId;
	private java.lang.Long modifiedByStaffId;
	private java.sql.Timestamp modifiedDate;
	private String certificateFileName;
	
	private String version;
	private String serialNo;
	private String signatureAlgo;
	private String issuer;
	private String subject;
	private String validFrom;
	private String validTo;
	private String publicKey;
	private String basicConstraint;
	private String subjectUniqueID;
	private String keyUsage;
	private String subjectAltName;
	private String issuerAltName;
	private Timestamp createDate;
	

	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	public java.lang.Long getTrustedCertificateId() {
		return trustedCertificateId;
	}
	public void setTrustedCertificateId(java.lang.Long trustedCertificateId) {
		this.trustedCertificateId = trustedCertificateId;
	}
	public String getTrustedCertificateName() {
		return trustedCertificateName;
	}
	public void setTrustedCertificateName(String trustedCertificateName) {
		this.trustedCertificateName = trustedCertificateName;
	}
	public byte[] getTrustedCertificate() {
		return trustedCertificate;
	}
	public void setTrustedCertificate(byte[] trustedCertificate) {
		this.trustedCertificate = trustedCertificate;
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
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}
	public String getValidTo() {
		return validTo;
	}
	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getSignatureAlgo() {
		return signatureAlgo;
	}
	public void setSignatureAlgo(String signatureAlgo) {
		this.signatureAlgo = signatureAlgo;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getBasicConstraint() {
		return basicConstraint;
	}
	public void setBasicConstraint(String basicConstraint) {
		this.basicConstraint = basicConstraint;
	}
	public String getSubjectUniqueID() {
		return subjectUniqueID;
	}
	public void setSubjectUniqueID(String subjectUniqueID) {
		this.subjectUniqueID = subjectUniqueID;
	}
	public String getKeyUsage() {
		return keyUsage;
	}
	public void setKeyUsage(String keyUsage) {
		this.keyUsage = keyUsage;
	}
	public String getSubjectAltName() {
		return subjectAltName;
	}
	public void setSubjectAltName(String subjectAltName) {
		this.subjectAltName = subjectAltName;
	}
	public String getIssuerAltName() {
		return issuerAltName;
	}
	public void setIssuerAltName(String issuerAltName) {
		this.issuerAltName = issuerAltName;
	}
}
