package com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotBlank;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.sf.json.JSONObject;
@XmlRootElement(name = "user-file-auth-driver-data")
@XmlType(propOrder = {"fileLocations" , "expiryDateFormat" })
public class UserFileAuthDriverData extends BaseData implements IUserFileAuthDriverData,Differentiable{
	
	private String userFileDriverId;
	
	@Expose
	@SerializedName("File Location")
	private String fileLocations;
	
	@Expose
	@SerializedName("Expiry Date Format")
	private String expiryDateFormat;
	
	private String driverInstanceId;
	
	@XmlTransient
	public String getUserFileDriverId() {
		return userFileDriverId;
	}
	public void setUserFileDriverId(String userFileDriverId) {
		this.userFileDriverId = userFileDriverId;
	}
	@XmlElement(name = "file-locations")
	@NotBlank(message = "Driver File Location must be specified")
	public String getFileLocations() {
		return fileLocations;
	}
	public void setFileLocations(String fileLocations) {
		this.fileLocations = fileLocations;
	}
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	@XmlElement(name = "expiry-date-formats")
	@NotBlank(message = "Driver Expiry date must be specified")
	public String getExpiryDateFormat() {
		return expiryDateFormat;
	}
	public void setExpiryDateFormat(String expiryDateFormat) {
		this.expiryDateFormat = expiryDateFormat;
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("File Locations", fileLocations);
		object.put("Expiry Date Formats", expiryDateFormat);
		return object;
	}
}

