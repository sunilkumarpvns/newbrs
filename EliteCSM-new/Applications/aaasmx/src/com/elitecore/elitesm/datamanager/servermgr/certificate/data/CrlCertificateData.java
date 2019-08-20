package com.elitecore.elitesm.datamanager.servermgr.certificate.data;

import java.security.cert.X509CRLEntry;
import java.util.Set;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

import net.sf.json.JSONObject;

@XmlRootElement(name = "certificate-revocation")
@XmlType(propOrder = { "crlCertificateName", "serialNo", "nextUpdate", "lastUpdate", "issuer", "signatureAlgo", "revokedDataList" })
public class CrlCertificateData extends BaseData implements Differentiable {

	private String crlCertificateId;
	
	@NotEmpty(message = "Certificate Revocation Name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 64, message = "Length of Certificate Revocation Name must not greater than 64.")
	private String crlCertificateName;    
	
	private byte[] crlCertificate;        
	private String createdByStaffId;
	private String modifiedByStaffId;
	private java.sql.Timestamp modifiedDate;
	private String certificateFileName;
	
	private Set<X509CRLEntry> revokedList;
	
	private Set<X509CRLEntryData> revokedDataList;
	
	private String serialNo;
	private String lastUpdate;
	private String nextUpdate;
	private String issuer;
	private String signatureAlgo;
	private String strIssuerName;

	@XmlTransient
	public String getCrlCertificateId() {
		return crlCertificateId;
	}
	public void setCrlCertificateId(String crlCertificateId) {
		this.crlCertificateId = crlCertificateId;
	}
	
	@XmlElement(name = "name")
	public String getCrlCertificateName() {
		return crlCertificateName;
	}
	public void setCrlCertificateName(String crlCertificateName) {
		this.crlCertificateName = crlCertificateName;
	}
	
	@XmlTransient
	public byte[] getCrlCertificate() {
		return crlCertificate;
	}
	public void setCrlCertificate(byte[] crlCertificate) {
		this.crlCertificate = crlCertificate;
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
	
	@XmlElement(name = "serial-number")
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	
	@XmlElement(name = "last-update")
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@XmlElement(name = "next-update")
	public String getNextUpdate() {
		return nextUpdate;
	}
	public void setNextUpdate(String nextUpdate) {
		this.nextUpdate = nextUpdate;
	}
	
	@XmlElement(name = "issuer")
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	@XmlElement(name = "signature-algorithm")
	public String getSignatureAlgo() {
		return signatureAlgo;
	}
	public void setSignatureAlgo(String signatureAlgo) {
		this.signatureAlgo = signatureAlgo;
	}
	
	@XmlTransient
	public Set<X509CRLEntry> getRevokedList() {
		return revokedList;
	}
	public void setRevokedList(Set<X509CRLEntry> revokedList) {
		this.revokedList = revokedList;
	}
	
	@XmlElementWrapper(name = "certificate-revocation-details")
	@XmlElement(name = "certificate-revocation-detail")
	public Set<X509CRLEntryData> getRevokedDataList() {
		return revokedDataList;
	}
	public void setRevokedDataList(Set<X509CRLEntryData> revokedDataList) {
		this.revokedDataList = revokedDataList;
	}	
	
	@XmlTransient
	public String getStrIssuerName() {
		if (getIssuer() != null) { 
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
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", crlCertificateName);
		object.put("Certificate Revocation List", certificateFileName);
		return object;
	}

}
