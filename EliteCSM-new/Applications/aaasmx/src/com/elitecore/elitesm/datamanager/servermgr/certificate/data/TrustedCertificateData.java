package com.elitecore.elitesm.datamanager.servermgr.certificate.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

import net.sf.json.JSONObject;

@XmlRootElement(name = "trusted-certificate")
@XmlType(propOrder = { "trustedCertificateName", "strSubjectName", "strIssuerName", "validFrom", "validTo", "version", "serialNo", "signatureAlgo", "subject", "issuer", "basicConstraint", "keyUsage" })
public class TrustedCertificateData extends BaseData implements Differentiable {

	private String trustedCertificateId;
	
	@NotEmpty(message = "Trusted Certificate Name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 64, message = "Length of Trusted Certificate Name must not greater than 64.")
	private String trustedCertificateName;    
	
	private byte[] trustedCertificate;        
	private String createdByStaffId;
	private String modifiedByStaffId;
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
	private String strSubjectName;
	private String strIssuerName;
	private int checkExpiryTime;

	@XmlTransient
	public String getTrustedCertificateId() {
		return trustedCertificateId;
	}
	public void setTrustedCertificateId(String trustedCertificateId) {
		this.trustedCertificateId = trustedCertificateId;
	}
	
	@XmlElement(name = "name")
	public String getTrustedCertificateName() {
		return trustedCertificateName;
	}
	public void setTrustedCertificateName(String trustedCertificateName) {
		this.trustedCertificateName = trustedCertificateName;
	}
	
	@XmlTransient
	public byte[] getTrustedCertificate() {
		return trustedCertificate;
	}
	public void setTrustedCertificate(byte[] trustedCertificate) {
		this.trustedCertificate = trustedCertificate;
	}
	
	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
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
	
	@XmlTransient
	public String getCertificateFileName() {
		return certificateFileName;
	}
	public void setCertificateFileName(String certificateFileName) {
		this.certificateFileName = certificateFileName;
	}
	
	@XmlElement(name = "trusted-certificate-detail-issuer")
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	@XmlElement(name = "trusted-certificate-detail-subject")
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
	
	@XmlElement(name = "trusted-certificate-subject")
	public String getStrSubjectName() {
		if(getSubject()!=null){
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
	
	@XmlElement(name = "trusted-certificate-issuer")
	public String getStrIssuerName() {
		if(getIssuer()!=null){
			String[] issuerDetail = getIssuer().split(",");
			for (String str : issuerDetail) {
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
		object.put("Name", trustedCertificateName);
		object.put("Trusted Certificate", certificateFileName);
		return object;
	}
	
}
