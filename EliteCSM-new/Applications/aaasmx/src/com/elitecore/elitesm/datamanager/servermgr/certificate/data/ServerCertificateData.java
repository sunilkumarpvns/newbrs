package com.elitecore.elitesm.datamanager.servermgr.certificate.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.sslcertificates.servercertificateprofile.PrivateKeyAlgorithmAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.sf.json.JSONObject;

@XmlRootElement(name = "server-certificate-profile")
@XmlType(propOrder = { "serverCertificateName", "strSubjectName", "strIssuerName", "validFrom", "validTo", "version", "serialNo", "signatureAlgo", "subject", "issuer", "basicConstraint", "keyUsage", "privateKeyAlgorithm", "privateKeyPassword" })
public class ServerCertificateData extends BaseData implements Differentiable {

	private String serverCertificateId;
	
	@Expose
	@SerializedName("Name")
	@NotEmpty(message = "Server Certificate Name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 64, message = "Length of Server Certificate Name must not greater than 64.")
	private String serverCertificateName;    
	
	@Expose
	@SerializedName("Subject")
	private String strSubjectName;
	
	@Expose
	@SerializedName("Issuer")
	private String strIssuerName;
	
	@Expose
	@SerializedName("Valid From")
	private String validFrom;
	
	@Expose
	@SerializedName("Expiry Date")
	private String validTo;
	
	private String issuer;
	private String subject;
	
	private byte[] certificate;
	
	private byte[] privateKey;
	private String privateKeyPassword;
	
	@NotNull(message = "Private Key Algorithm must be specified. Value could be 'RSA' or 'DHA' or 'DSA'.")
	@Pattern(regexp = "RSA|DiffieHellman|DSA" , message = "Invalid value of Private Key Algorithm. Value could be 'RSA' or 'DHA' or 'DSA'.")
	private String privateKeyAlgorithm;    
	
	private String createdByStaffId;
	private String modifiedByStaffId;
	private java.sql.Timestamp modifiedDate;
	private String certificateFileName;
	private String privateKeyFileName;	

	private String version;
	private String serialNo;
	private String signatureAlgo;
	
	
	private String publicKey;
	private String basicConstraint;
	private String subjectUniqueID;
	private String keyUsage;
	private String subjectAltName;
	private String issuerAltName;
	private String privateKeyData;
	
	
	private int checkExpiryTime;

	@XmlTransient
	public String getCertificateFileName() {
		return certificateFileName;
	}
	public void setCertificateFileName(String certificateFileName) {
		this.certificateFileName = certificateFileName;
	}
	
	@XmlTransient
	public byte[] getCertificate() {
		return certificate;
	}
	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	@XmlTransient
	public byte[] getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}   

	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}

	@XmlTransient
	public String getServerCertificateId() {
		return serverCertificateId;
	}
	public void setServerCertificateId(String serverCertificateId) {
		this.serverCertificateId = serverCertificateId;
	}
	
	@XmlElement(name = "name")
	public String getServerCertificateName() {
		return serverCertificateName;
	}
	public void setServerCertificateName(String name) {
		this.serverCertificateName = name;
	}	
	
	@XmlElement(name = "private-key-password")
	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}
	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}
	
	@XmlElement(name = "private-key-algorithm")
	@XmlJavaTypeAdapter(PrivateKeyAlgorithmAdapter.class)
	public String getPrivateKeyAlgorithm() {
		return privateKeyAlgorithm;
	}
	public void setPrivateKeyAlgorithm(String privateKeyAlgorithm) {
		this.privateKeyAlgorithm = privateKeyAlgorithm;
	}

	@XmlTransient
	public String getModifiedByStaffId() {
		return modifiedByStaffId;
	}
	public void setModifiedByStaffId(String modifiedByStaffId) {
		this.modifiedByStaffId = modifiedByStaffId;
	}

	@XmlTransient
	public java.sql.Timestamp getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(java.sql.Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	@XmlElement(name = "version")
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	@XmlElement(name = "serial-number")
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	
	@XmlElement(name = "signature-algorithm")
	public String getSignatureAlgo() {
		return signatureAlgo;
	}
	public void setSignatureAlgo(String signatureAlgo) {
		this.signatureAlgo = signatureAlgo;
	}

	@XmlElement(name = "public-certificate-issuer")
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	@XmlElement(name = "public-certificate-subject")
	public String getSubject() {
	  return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@XmlElement(name = "valid-from")
	public String getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}

	@XmlElement(name = "expiry-date")
	public String getValidTo() {
		return validTo;
	}
	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}

	@XmlTransient
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@XmlElement(name = "basic-constraint")
	public String getBasicConstraint() {
		return basicConstraint;
	}
	public void setBasicConstraint(String basicConstraint) {
		this.basicConstraint = basicConstraint;
	}

	@XmlTransient
	public String getSubjectUniqueID() {
		return subjectUniqueID;
	}
	public void setSubjectUniqueID(String subjectUniqueID) {
		this.subjectUniqueID = subjectUniqueID;
	}
	
	@XmlElement(name = "key-usage")
	public String getKeyUsage() {
		return keyUsage;
	}
	public void setKeyUsage(String keyUsage) {
		this.keyUsage = keyUsage;
	}
	
	@XmlTransient
	public String getSubjectAltName() {
		return subjectAltName;
	}
	public void setSubjectAltName(String subjectAltName) {
		this.subjectAltName = subjectAltName;
	}
	
	@XmlTransient
	public String getIssuerAltName() {
		return issuerAltName;
	}
	public void setIssuerAltName(String issuerAltName) {
		this.issuerAltName = issuerAltName;
	}

	@XmlTransient
	public String getPrivateKeyData() {
		return privateKeyData;
	}
	public void setPrivateKeyData(String privateKeyData) {
		this.privateKeyData = privateKeyData;
	}

	@XmlTransient
	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}
	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}
	
	@XmlElement(name = "server-certificate-issuer")
	public String getStrIssuerName() {
		if (getIssuer() != null) { 
			 String[] subjectDetail = getIssuer().split(",");
			 	for (String str : subjectDetail) {
			 			if (str.contains("CN=")) { 
			 				strIssuerName=str.split("CN=")[1];
			 			} 
			 	}
			} 
		return strIssuerName;
	}
	public void setStrIssuerName(String strIssuerName) {
		this.strIssuerName = strIssuerName;
	}
	
	@XmlElement(name = "server-certificate-subject")
	public String getStrSubjectName() {
		if (getSubject() != null) { 
		 String[] subjectDetail = getSubject().split(",");
		 	for (String str : subjectDetail) {
		 			if (str.contains("CN=")) { 
		 				strSubjectName=str.split("CN=")[1];
		 			} 
		 	}
		} 
		return strSubjectName;
	}
	public void setStrSubjectName(String strSubjectName) {
		this.strSubjectName = strSubjectName;
	}

	@XmlTransient
	public int getCheckExpiryTime() {
			SimpleDateFormat formatter=new SimpleDateFormat("dd MMM yyyy kk:mm:ss ");
			try {
				Date parsedDate = formatter.parse(getValidTo());
				Date currentDate = new Date();
				int daysDifference=(int)( (parsedDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));
				checkExpiryTime=daysDifference;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return checkExpiryTime;
	}
	public void setCheckExpiryTime(int checkExpiryTime) {
		this.checkExpiryTime = checkExpiryTime;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Profile Name", serverCertificateName);
		object.put("Public Certificate", certificateFileName);
		object.put("Private Key", privateKeyFileName);
		object.put("Private Key Password", privateKeyPassword);
		object.put("Private Key Algorithm", privateKeyAlgorithm);
		return object;
	}
	 
}
