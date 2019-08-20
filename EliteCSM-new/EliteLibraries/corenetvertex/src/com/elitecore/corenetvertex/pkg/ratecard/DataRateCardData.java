package com.elitecore.corenetvertex.pkg.ratecard;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.validator.DataRateCardVersionRelationValidator;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

/**
 * Data Monetary Rate Card
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData")
@Table(name = "TBLM_DATA_RATE_CARD")
public class DataRateCardData extends ResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private String pulseUnit;
    private String rateUnit;
    private String labelKey1;
    private String labelKey2;
    private transient PkgData pkgData;
    private List<DataRateCardVersionRelationData> dataRateCardVersionRelationData;
    private transient List<QosProfileData> qoSProfiles;
    private String pkgId;

    public DataRateCardData() {
        dataRateCardVersionRelationData = Collectionz.newArrayList();
    }

    @XmlElement(name="name")
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="description")
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name="pulse-unit")
    @Column(name = "PULSE_UNIT")
    public String getPulseUnit() {
        return pulseUnit;
    }

    public void setPulseUnit(String pulseUnit) {
        this.pulseUnit = pulseUnit;
    }

    @XmlElement(name="rate-unit")
    @Column(name = "RATE_UNIT")
    public String getRateUnit() {
        return rateUnit;
    }

    public void setRateUnit(String rateUnit) {
        this.rateUnit = rateUnit;
    }

    @XmlElement(name="label-key-1")
    @Column(name = "LABEL_KEY_1")
    public String getLabelKey1() {
        return labelKey1;
    }

    public void setLabelKey1(String labelKey1) {
        this.labelKey1 = labelKey1;
    }

    @XmlElement(name="label-key-2")
    @Column(name = "LABEL_KEY_2")
    public String getLabelKey2() {
        return labelKey2;
    }

    public void setLabelKey2(String labelKey2) {
        this.labelKey2 = labelKey2;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PACKAGE_ID")
    @XmlTransient
    @JsonIgnore
    public PkgData getPkgData() {
        return pkgData;
    }

    public void setPkgData(PkgData pkgData) {
        if(pkgData != null){
            setPkgId(pkgData.getId());
        }
        this.pkgData = pkgData;
    }

    @XmlElementWrapper(name="data-rate-card-versions")
    @XmlElement(name="data-rate-card-version")
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "dataRateCardData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @Import(required = true, validatorClass = DataRateCardVersionRelationValidator.class)
    public List<DataRateCardVersionRelationData> getDataRateCardVersionRelationData() {
        return dataRateCardVersionRelationData;
    }

    public void setDataRateCardVersionRelationData(List<DataRateCardVersionRelationData> dataRateCardVersionRelationData) {
        this.dataRateCardVersionRelationData = dataRateCardVersionRelationData;
    }



    @OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="rateCardData")
    @Fetch(FetchMode.SUBSELECT)
    @Where(clause="STATUS != 'DELETED'")
    @XmlTransient
    public List<QosProfileData> getQoSProfiles() {
        return qoSProfiles;
    }

    public void setQoSProfiles(List<QosProfileData> qoSProfiles) {
        this.qoSProfiles = qoSProfiles;
    }


    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
        if(StringUtils.isNotBlank(status) && CommonConstants.STATUS_DELETED.equalsIgnoreCase(status)){
            for(DataRateCardVersionRelationData dataRateCardVersionRelationData:dataRateCardVersionRelationData){
                dataRateCardVersionRelationData.removeRevenueDetail();
            }
        }

    }

    @Override
    @Transient
    public String getGroups() {
        return pkgData.getGroups();
    }

    @Transient
    @Override
    public String getResourceName() {
        return getName();
    }

    @Transient
    public String getPkgId() {
        return pkgId;
    }

    public void setPkgId(String pkgId) {
        this.pkgId = pkgId;
    }


    @Override
    @Transient
    public String getAuditableId() {
        return pkgData.getId();
    }

    @Override
    @Transient
    public ResourceData getAuditableResource() {
        return pkgData;
    }

    @Override
    @Transient
    public String getHierarchy() {
        return pkgData.getHierarchy() +"<br>"+ getId() + "<br>"+ name;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty("Pulse Unit", pulseUnit);
        jsonObject.addProperty("Rate Unit", rateUnit);
        jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
        jsonObject.addProperty(FieldValueConstants.LABEL_KEY_ONE, labelKey1);
        jsonObject.addProperty(FieldValueConstants.LABEL_KEY_TWO, labelKey2);
        if(dataRateCardVersionRelationData != null){
            JsonObject jsonArray = new JsonObject();
            for(DataRateCardVersionRelationData dataRateCardVersionRelationData : dataRateCardVersionRelationData){
                if(dataRateCardVersionRelationData != null)
                    jsonArray.add(dataRateCardVersionRelationData.getVersionName(),dataRateCardVersionRelationData.toJson());
            }
            jsonObject.add("Versions", jsonArray);
        }
        return jsonObject;
    }

    public DataRateCardData copyModel() {
        DataRateCardData newData = new DataRateCardData();
        newData.description = this.description;
        newData.labelKey1 = this.labelKey1;
        newData.labelKey2 = this.labelKey2;
        newData.name = this.name;
        newData.pulseUnit = this.pulseUnit;
        newData.rateUnit = this.rateUnit;
        List<DataRateCardVersionRelationData> dataRateCardVersionRelationDatas = Collectionz.newArrayList();
        for(DataRateCardVersionRelationData dataRateCardVersionRelationData : this.dataRateCardVersionRelationData){
            DataRateCardVersionRelationData dataRateCardVersionRelationDataCopy = dataRateCardVersionRelationData.copyModel();
            dataRateCardVersionRelationDataCopy.setId(null);
            dataRateCardVersionRelationDataCopy.setDataRateCardData(newData);
            dataRateCardVersionRelationDatas.add(dataRateCardVersionRelationDataCopy);
        }
        newData.setDataRateCardVersionRelationData(dataRateCardVersionRelationDatas);
        return newData;
    }
}
