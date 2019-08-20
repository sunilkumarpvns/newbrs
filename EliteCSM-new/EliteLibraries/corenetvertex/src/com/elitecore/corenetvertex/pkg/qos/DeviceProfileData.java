package com.elitecore.corenetvertex.pkg.qos;

import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.LongToStringAdapter;
import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

@Entity
@Table(name = "TBLM_DEVICE_PROFILE")
public class DeviceProfileData extends ResourceData implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private transient QosProfileData qosProfile;
	private Long tac;
	private String brand;
	private String model;
	private String hardwareType;
	private String os;
	private Integer year;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "QOS_PROFILE_ID")
	@XmlTransient
	@Transient
	public QosProfileData getQosProfile() {
		return qosProfile;
	}

	public void setQosProfile(QosProfileData qosProfile) {
		this.qosProfile = qosProfile;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	public void setStatus(String status) {
		super.setStatus(status);
	}
	
	
	@Column(name = "TAC")
	@XmlJavaTypeAdapter(LongToStringAdapter.class)
	public Long getTac() {
		return tac;
	}

	public void setTac(Long tac) {
		this.tac = tac;
	}

	@Column(name = "BRAND")
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Column(name = "MODEL")
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(name = "HARDWARE_TYPE")
	public String getHardwareType() {
		return hardwareType;
	}

	public void setHardwareType(String hardwareType) {
		this.hardwareType = hardwareType;
	}

	@Column(name = "OS")
	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	@Column(name = "YEAR")
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public JsonObject toJson() {
		return null;
	}


	@Transient
	public String getName() {
		return getModel();
	}

	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}


	@Override
	@Transient
	public String getHierarchy() {
		return getId() + "<br>" + getName();
	}


	public DeviceProfileData deepClone() throws CloneNotSupportedException {
		DeviceProfileData newData = (DeviceProfileData) this.clone();
		newData.qosProfile = qosProfile;
		return newData;
	}

	public DeviceProfileData copyModel() {
		DeviceProfileData newData = new DeviceProfileData();
		newData.tac = this.tac;
		newData.brand = this.brand;
		newData.model = this.model;
		newData.hardwareType = this.hardwareType;
		newData.os = this.os;
		newData.year = this.year;
		return newData;
	}
}
