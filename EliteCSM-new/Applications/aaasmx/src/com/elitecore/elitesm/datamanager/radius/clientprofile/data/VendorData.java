package com.elitecore.elitesm.datamanager.radius.clientprofile.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

@XmlRootElement(name="vendor")
public class VendorData extends BaseData implements Serializable, Comparable<VendorData>{

	private static final long serialVersionUID = 1L;

	private String vendorInstanceId; 
	private long vendorId;
	private String vendorName;
	
	@XmlTransient
	public String getVendorInstanceId() {
		return vendorInstanceId;
	}
	public void setVendorInstanceId(String vendorInstanceId) {
		this.vendorInstanceId = vendorInstanceId;
	}
	
	@XmlTransient
	public long getVendorId() {
		return vendorId;
	}
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}

	@XmlElement(name = "vendor-name")
	@NotEmpty(message = "Vendor Name must be specified for supported vendors")
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	
	@Override
	public int compareTo(VendorData vendorData) {
		return vendorName.compareTo(vendorData.getVendorName());
	}
}
