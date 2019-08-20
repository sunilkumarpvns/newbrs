package com.elitecore.corenetvertex.sm.device;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity(name = "com.elitecore.corenetvertex.sm.device.DeviceData")
@Table(name = "TBLM_DEVICE_PROFILE")
public class DeviceData extends DefaultGroupResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DELIMITER = ",";

    @SerializedName(FieldValueConstants.TAC)
    private String tac;
    @SerializedName(FieldValueConstants.BRAND)
    private String brand;
    @SerializedName(FieldValueConstants.DEVICE_MODEL)
    private String deviceModel;
    @SerializedName(FieldValueConstants.HARDWARE_TYPE)
    private String hardwareType;
    @SerializedName(FieldValueConstants.OS)
    private String os;
    @SerializedName(FieldValueConstants.YEAR)
    private String year;
    @SerializedName(FieldValueConstants.ADDITIONAL_INFO)
    private String additionalInformation;

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Column(name = "TAC")
    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
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
    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
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
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.TAC, tac);
        jsonObject.addProperty(FieldValueConstants.BRAND, brand);
        jsonObject.addProperty(FieldValueConstants.DEVICE_MODEL, deviceModel);
        jsonObject.addProperty(FieldValueConstants.OS, os);
        jsonObject.addProperty(FieldValueConstants.HARDWARE_TYPE, hardwareType);
        jsonObject.addProperty(FieldValueConstants.YEAR, year);
        jsonObject.addProperty(FieldValueConstants.ADDITIONAL_INFO, additionalInformation);
        return jsonObject;
    }


	@Transient
	@JsonIgnore
	public String getName() {
		return getDeviceModel();
	}

    @Override
    @Transient
    public String getResourceName() {
        if (getTac() != null)
            return getTac();
        else
            return null;
    }


    @Override
    @Transient
    public String getHierarchy() {
        return getId() + "<br>" + getTac();
    }


    @Column(name = "ADDITIONAL_INFORMATION")
    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Transient
    public String generateCSVData() {
        StringBuilder stringBuilder = new StringBuilder();
        if (tac != null) {
            stringBuilder.append(tac);
        }
        stringBuilder.append(DELIMITER);
        if (Strings.isNullOrBlank(brand) == false) {
            stringBuilder.append(brand);
        }
        stringBuilder.append(DELIMITER);
        if (Strings.isNullOrBlank(deviceModel) == false) {
            stringBuilder.append(deviceModel);
        }
        stringBuilder.append(DELIMITER);
        if (Strings.isNullOrBlank(hardwareType) == false) {
            stringBuilder.append(hardwareType);
        }
        stringBuilder.append(DELIMITER);
        if (Strings.isNullOrBlank(os) == false) {
            stringBuilder.append(os);
        }
        stringBuilder.append(DELIMITER);
        if (year != null) {
            stringBuilder.append(year);
        }
        stringBuilder.append(DELIMITER);
        if (Strings.isNullOrBlank(additionalInformation) == false) {
            stringBuilder.append(additionalInformation);
        }
        stringBuilder.append(DELIMITER + "\n");
        return stringBuilder.toString();
    }

}
