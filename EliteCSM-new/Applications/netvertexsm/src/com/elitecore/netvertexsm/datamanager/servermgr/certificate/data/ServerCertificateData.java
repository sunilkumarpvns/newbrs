package com.elitecore.netvertexsm.datamanager.servermgr.certificate.data;

import java.sql.Timestamp;
import java.util.Arrays;

public class ServerCertificateData {

	private java.lang.Long serverCertificateId;
	private String serverCertificateName;    
	private byte[] certificate;
	private byte[] privateKey;
	private String privateKeyPassword;
	private String privateKeyAlgorithm;    
	private java.lang.Long createdByStaffId;
	private java.lang.Long modifiedByStaffId;
	private java.sql.Timestamp modifiedDate;
	private String certificateFileName;
	private String privateKeyFileName;	

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
	private String privateKeyData;
	private Timestamp createDate;

	
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public String getCertificateFileName() {
		return certificateFileName;
	}
	public void setCertificateFileName(String certificateFileName) {
		this.certificateFileName = certificateFileName;
	}
	public byte[] getCertificate() {
		return certificate;
	}
	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}
	public byte[] getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}   

	public java.lang.Long getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(java.lang.Long createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}	
	public java.lang.Long getServerCertificateId() {
		return serverCertificateId;
	}
	public void setServerCertificateId(java.lang.Long serverCertificateId) {
		this.serverCertificateId = serverCertificateId;
	}
	public String getServerCertificateName() {
		return serverCertificateName;
	}
	public void setServerCertificateName(String name) {
		this.serverCertificateName = name;
	}	
	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}
	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}
	public String getPrivateKeyAlgorithm() {
		return privateKeyAlgorithm;
	}
	public void setPrivateKeyAlgorithm(String privateKeyAlgorithm) {
		this.privateKeyAlgorithm = privateKeyAlgorithm;
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
	public String getPrivateKeyData() {
		return privateKeyData;
	}
	public void setPrivateKeyData(String privateKeyData) {
		this.privateKeyData = privateKeyData;
	}
	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}
	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}
	
}
