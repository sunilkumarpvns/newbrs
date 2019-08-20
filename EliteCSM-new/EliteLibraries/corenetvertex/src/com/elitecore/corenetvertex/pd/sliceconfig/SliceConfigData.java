package com.elitecore.corenetvertex.pd.sliceconfig;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;

@Entity(name = "com.elitecore.corenetvertex.pd.sliceconfig.SliceConfigData")
@Table(name = "TBLM_SLICE_CONFIGURATION")
public class SliceConfigData extends ResourceData implements Serializable {

    private static final long serialVersionUID = -9173903847327724345L;

    private Long monetaryReservation;

    private Integer volumeSlicePercentage;
    private Integer volumeSliceThreshold;
    private Long volumeMinimumSlice;
    private Long volumeMaximumSlice;
    private String volumeMinimumSliceUnit;
    private String volumeMaximumSliceUnit;

    private Integer timeSlicePercentage;
    private Integer timeSliceThreshold;
    private Long timeMinimumSlice;
    private Long timeMaximumSlice;
    private String timeMinimumSliceUnit;
    private String timeMaximumSliceUnit;

    private Boolean dynamicSlicing;

    @Column(name = "MONETARY_RESERVATION")
    public Long getMonetaryReservation() {
        return monetaryReservation;
    }

    public void setMonetaryReservation(Long monetaryReservation) {
        this.monetaryReservation = monetaryReservation;
    }

    @Column(name = "VOLUME_SLICE")
    public Integer getVolumeSlicePercentage() {
        return volumeSlicePercentage;
    }

    public void setVolumeSlicePercentage(Integer volumeSlicePercentage) {
        this.volumeSlicePercentage = volumeSlicePercentage;
    }

    @Column(name = "VOLUME_THRESHOLD")
    public Integer getVolumeSliceThreshold() {
        return volumeSliceThreshold;
    }

    public void setVolumeSliceThreshold(Integer volumeSliceThreshold) {
        this.volumeSliceThreshold = volumeSliceThreshold;
    }

    @Column(name = "VOLUME_MIN_SLICE")
    public Long getVolumeMinimumSlice() {
        return volumeMinimumSlice;
    }

    public void setVolumeMinimumSlice(Long volumeMinimumSlice) {
        this.volumeMinimumSlice = volumeMinimumSlice;
    }

    @Column(name = "VOLUME_MAX_SLICE")
    public Long getVolumeMaximumSlice() {
        return volumeMaximumSlice;
    }

    public void setVolumeMaximumSlice(Long volumeMaximumSlice) {
        this.volumeMaximumSlice = volumeMaximumSlice;
    }

    @Column(name = "VOLUME_MIN_SLICE_UNIT")
    public String getVolumeMinimumSliceUnit() {
        return volumeMinimumSliceUnit;
    }

    public void setVolumeMinimumSliceUnit(String volumeMinimumSliceUnit) {
        this.volumeMinimumSliceUnit = volumeMinimumSliceUnit;
    }

    @Column(name = "VOLUME_MAX_SLICE_UNIT")
    public String getVolumeMaximumSliceUnit() {
        return volumeMaximumSliceUnit;
    }

    public void setVolumeMaximumSliceUnit(String volumeMaximumSliceUnit) {
        this.volumeMaximumSliceUnit = volumeMaximumSliceUnit;
    }

    @Column(name = "TIME_SLICE")
    public Integer getTimeSlicePercentage() {
        return timeSlicePercentage;
    }

    public void setTimeSlicePercentage(Integer timeSlicePercentage) {
        this.timeSlicePercentage = timeSlicePercentage;
    }

    @Column(name = "TIME_THRESHOLD")
    public Integer getTimeSliceThreshold() {
        return timeSliceThreshold;
    }

    public void setTimeSliceThreshold(Integer timeSliceThreshold) {
        this.timeSliceThreshold = timeSliceThreshold;
    }

    @Column(name = "TIME_MIN_SLICE")
    public Long getTimeMinimumSlice() {
        return timeMinimumSlice;
    }

    public void setTimeMinimumSlice(Long timeMinimumSlice) {
        this.timeMinimumSlice = timeMinimumSlice;
    }

    @Column(name = "TIME_MAX_SLICE")
    public Long getTimeMaximumSlice() {
        return timeMaximumSlice;
    }

    public void setTimeMaximumSlice(Long timeMaximumSlice) {
        this.timeMaximumSlice = timeMaximumSlice;
    }

    @Column(name = "TIME_MIN_SLICE_UNIT")
    public String getTimeMinimumSliceUnit() {
        return timeMinimumSliceUnit;
    }

    public void setTimeMinimumSliceUnit(String timeMinimumSliceUnit) {
        this.timeMinimumSliceUnit = timeMinimumSliceUnit;
    }

    @Column(name = "TIME_MAX_SLICE_UNIT")
    public String getTimeMaximumSliceUnit() {
        return timeMaximumSliceUnit;
    }

    public void setTimeMaximumSliceUnit(String timeMaximumSliceUnit) {
        this.timeMaximumSliceUnit = timeMaximumSliceUnit;
    }

    @Column(name = "DYNAMIC_SLICING")
    public Boolean getDynamicSlicing() {
        return dynamicSlicing;
    }

    public void setDynamicSlicing(Boolean dynamicSlicing) {
        this.dynamicSlicing = dynamicSlicing;
    }

    @Override
    @Transient
    public String getResourceName() {
        return "slice";
    }

    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Monetary Reservation", monetaryReservation);
        jsonObject.addProperty("Volume Slice(%)", volumeSlicePercentage);
        jsonObject.addProperty("Volume Slice Threshold(%)", volumeSliceThreshold);
        jsonObject.addProperty("Time Slice(%)", timeSlicePercentage);
        jsonObject.addProperty("Time Slice Threshold(%)", timeSliceThreshold);
        jsonObject.addProperty("Volume Minimum Slice", volumeMinimumSlice);
        jsonObject.addProperty("Volume Maximum Slice", volumeMaximumSlice);
        jsonObject.addProperty("Time Minimum Slice", timeMinimumSlice);
        jsonObject.addProperty("Time Maximum Slice", timeMaximumSlice);
        jsonObject.addProperty("Volume Minimum Slice Unit", volumeMinimumSliceUnit);
        jsonObject.addProperty("Volume Maximum Slice Unit", volumeMaximumSliceUnit);
        jsonObject.addProperty("Time Minimum Slice Unit", timeMinimumSliceUnit);
        jsonObject.addProperty("Time Maximum Slice Unit", timeMaximumSliceUnit);

        return jsonObject;
    }
}
