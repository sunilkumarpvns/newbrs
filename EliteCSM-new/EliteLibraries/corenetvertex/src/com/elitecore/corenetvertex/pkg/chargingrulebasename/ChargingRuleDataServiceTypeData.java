package com.elitecore.corenetvertex.pkg.chargingrulebasename;

import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.LongToStringAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

/**
 * Created by kirpalsinh on 19/1/17.
 */

@Entity
@Table(name="TBLM_CHRG_RULE_DATA_SRV_TYPE")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargingRuleDataServiceTypeData implements Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "dataServiceType") private DataServiceTypeData dataServiceTypeData;
    private String monitoringKey;

    transient private ChargingRuleBaseNameData chargingRuleBaseName;

    private String id;

    @XmlJavaTypeAdapter(LongToStringAdapter.class)
    private Long    sliceTotal;
    private String  sliceTotalUnit;

    @XmlJavaTypeAdapter(LongToStringAdapter.class)
    private Long    sliceUpload;
    private String  sliceUploadUnit;

    @XmlJavaTypeAdapter(LongToStringAdapter.class)
    private Long    sliceDownload;
    private String  sliceDownloadUnit;

    @XmlJavaTypeAdapter(LongToStringAdapter.class)
    private Long    sliceTime;
    private String  sliceTimeUnit;

    @XmlTransient
    private String sliceTotalValue;/* Used for only display purpose in datatable*/

    @XmlTransient
    private String sliceDownloadValue;/* Used for only display purpose in datatable*/

    @XmlTransient
    private String sliceUploadValue;/* Used for only display purpose in datatable*/

    @XmlTransient
    private String sliceTimeValue;/* Used for only display purpose in datatable*/

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ManyToOne( fetch = FetchType.EAGER )
    @JoinColumn( name = "DATA_SERVICE_TYPE_ID")
    public DataServiceTypeData getDataServiceTypeData() {
        return dataServiceTypeData;
    }

    public void setDataServiceTypeData(DataServiceTypeData dataServiceTypeData) {
        this.dataServiceTypeData = dataServiceTypeData;
    }

    @Column ( name = "MONITORING_KEY" )
    public String getMonitoringKey() {
        return monitoringKey;
    }

    public void setMonitoringKey(String monitoringKey) {
        this.monitoringKey = monitoringKey;
    }

    @ManyToOne ( fetch  = FetchType.EAGER )
    @JoinColumn( name = "CHARGING_RULE_BASE_NAME_ID" )
    @XmlTransient
    public ChargingRuleBaseNameData getChargingRuleBaseName() {
        return chargingRuleBaseName;
    }

    public void setChargingRuleBaseName(ChargingRuleBaseNameData chargingRuleBaseName) {
        this.chargingRuleBaseName = chargingRuleBaseName;
    }

    @Column ( name = "SLICE_TOTAL" )
    public Long getSliceTotal() {
        return sliceTotal;
    }

    public void setSliceTotal(Long sliceTotal) {
        this.sliceTotal = sliceTotal;
    }

    @Column ( name = "SLICE_TOTAL_UNIT" )
    public String getSliceTotalUnit() {
        return sliceTotalUnit;
    }

    public void setSliceTotalUnit(String sliceTotalUnit) {
        this.sliceTotalUnit = sliceTotalUnit;
    }

    @Column ( name = "SLICE_UPLOAD" )
    public Long getSliceUpload() {
        return sliceUpload;
    }

    public void setSliceUpload(Long sliceUpload) {
        this.sliceUpload = sliceUpload;
    }

    @Column ( name = "SLICE_UPLOAD_UNIT" )
    public String getSliceUploadUnit() {
        return sliceUploadUnit;
    }

    public void setSliceUploadUnit(String sliceUploadUnit) {
        this.sliceUploadUnit = sliceUploadUnit;
    }

    @Column ( name = "SLICE_DOWNLOAD" )
    public Long getSliceDownload() {
        return sliceDownload;
    }

    public void setSliceDownload(Long sliceDownload) {
        this.sliceDownload = sliceDownload;
    }

    @Column ( name = "SLICE_DOWNLOAD_UNIT" )
    public String getSliceDownloadUnit() {
        return sliceDownloadUnit;
    }

    public void setSliceDownloadUnit(String sliceDownloadUnit) {
        this.sliceDownloadUnit = sliceDownloadUnit;
    }

    @Column ( name = "SLICE_TIME" )
    public Long getSliceTime() {
        return sliceTime;
    }

    public void setSliceTime(Long sliceTime) {
        this.sliceTime = sliceTime;
    }

    @Column ( name = "SLICE_TIME_UNIT" )
    public String getSliceTimeUnit() {
        return sliceTimeUnit;
    }

    public void setSliceTimeUnit(String sliceTimeUnit) {
        this.sliceTimeUnit = sliceTimeUnit;
    }

    @Transient
    @XmlTransient
    public String getSliceTotalValue() {
        return sliceTotalValue;
    }

    public void setSliceTotalValue(String sliceTotalValue) {
        this.sliceTotalValue = sliceTotalValue;
    }

    @Transient
    @XmlTransient
    public String getSliceDownloadValue() {
        return sliceDownloadValue;
    }

    public void setSliceDownloadValue(String sliceDownloadValue) {
        this.sliceDownloadValue = sliceDownloadValue;
    }

    @Transient
    @XmlTransient
    public String getSliceUploadValue() {
        return sliceUploadValue;
    }

    public void setSliceUploadValue(String sliceUploadValue) {
        this.sliceUploadValue = sliceUploadValue;
    }

    @Transient
    @XmlTransient
    public String getSliceTimeValue() {
        return sliceTimeValue;
    }

    public void setSliceTimeValue(String sliceTimeValue) {
        this.sliceTimeValue = sliceTimeValue;
    }

    public ChargingRuleDataServiceTypeData deepClone() throws CloneNotSupportedException {
        ChargingRuleDataServiceTypeData newData = (ChargingRuleDataServiceTypeData) this.clone();
        newData.id = id;
        newData.monitoringKey = monitoringKey;
        newData.chargingRuleBaseName = chargingRuleBaseName;
        newData.dataServiceTypeData = dataServiceTypeData;
        newData.sliceTotal = sliceTotal;
        newData.sliceUpload = sliceUpload;
        newData.sliceDownload = sliceDownload;
        newData.sliceTime = sliceTime;
        return newData;
    }
}
