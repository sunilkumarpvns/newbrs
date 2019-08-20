package com.elitecore.netvertex.gateway.diameter.utility.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="application")
public class ApplicationData {
	
	private Integer id;
	private Integer diameterResultCode;
	private Integer vendorId;
	private Integer experimentalResultCode;

	public ApplicationData() {
        // Intentionally Left blank for JAXB Unmarshalling
	}

	@XmlElement(name="id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlElement(name="diameter-result-code")
	public Integer getDiameterResultCode() {
		return diameterResultCode;
	}

	public void setDiameterResultCode(Integer diameterResultCode) {
		this.diameterResultCode = diameterResultCode;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	@XmlElement(name="vendor-id")
	public Integer getVendorId() {
		return vendorId;
	}

	public void setExperimentalResultCode(Integer experimentalResultCode) {
		this.experimentalResultCode = experimentalResultCode;
	}

	@XmlElement(name="experimental-result-code")
	public Integer getExperimentalResultCode() {
		return experimentalResultCode;
	}
}
