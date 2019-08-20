package com.elitecore.elitesm.datamanager.servermgr.certificate.data;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "certificate-revocation-detail")
@XmlType(propOrder = { "serialNumber", "revocationDate" })
public class X509CRLEntryData {
	
	BigInteger serialNumber;
	String revocationDate;
	
	@XmlElement(name = "serial-number")
	public BigInteger getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(BigInteger serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	@XmlElement(name = "revocation-date")
	public String getRevocationDate() {
		return revocationDate;
	}
	public void setRevocationDate(String revocationDate) {
		this.revocationDate = revocationDate;
	}
	
}
