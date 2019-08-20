package com.elitecore.corenetvertex.pkg.chargingrulebasename;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.validator.ChargingRuleBaseNameImportValidator;
import com.elitecore.corenetvertex.pkg.importpkg.ChargingRuleBaseNameImportOperation;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by kirpalsinh on 19/1/17.
 */

@Entity
@Table(name = "TBLM_CHARGING_RULE_BASE_NAME")
@XmlAccessorType(XmlAccessType.FIELD)
@Import(required = true, validatorClass = ChargingRuleBaseNameImportValidator.class, importClass = ChargingRuleBaseNameImportOperation.class)
@XmlRootElement
public class ChargingRuleBaseNameData extends ResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String description;

    @XmlElement(name = "chargingRuleDataServiceTypes")
    private List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatas;
    private transient List<QosProfileDetailData> qosProfileDetails;

    public ChargingRuleBaseNameData(){
        chargingRuleDataServiceTypeDatas = Collectionz.newArrayList();
        qosProfileDetails = Collectionz.newArrayList();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    @Column(name="GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Override
    public void setGroups(String groups) {
        super.setGroups(groups);
    }

    @OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="chargingRuleBaseName",orphanRemoval=true)
    @Fetch(FetchMode.SUBSELECT)
    @XmlTransient
    public List<ChargingRuleDataServiceTypeData> getChargingRuleDataServiceTypeDatas() {
        return chargingRuleDataServiceTypeDatas;
    }

    public void setChargingRuleDataServiceTypeDatas(List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatas) {
        this.chargingRuleDataServiceTypeDatas = chargingRuleDataServiceTypeDatas;
    }

    @ManyToMany
    @JoinTable(name = "TBLM_QOS_PROFILE_CRBN_REL", joinColumns = { @JoinColumn(name = "CHARGING_RULE_BASE_NAME_ID", nullable = false) },
            inverseJoinColumns = { @JoinColumn(name = "QOS_PROFILE_DETAIL_ID", nullable = false) })
    @XmlTransient
    public List<QosProfileDetailData> getQosProfileDetails() {
        return qosProfileDetails;
    }

    public void setQosProfileDetails(List<QosProfileDetailData> qosProfileDetail) {
        this.qosProfileDetails = qosProfileDetail;
    }


    @Transient
    @Override
    public String getHierarchy() {
        return getId() + "<br>" + name;
    }

    @Transient
    @XmlTransient
    public String getChargingRuleServiceTypeDataInJsonString(){
        try{
            Gson gson = GsonFactory.defaultInstance();
            for(ChargingRuleDataServiceTypeData data : chargingRuleDataServiceTypeDatas){
                if(data.getSliceUpload() != null) {
                    data.setSliceUploadValue(String.valueOf(data.getSliceUpload()) + " " + data.getSliceUploadUnit());
                }

                if(data.getSliceTotal() != null) {
                    data.setSliceTotalValue(String.valueOf(data.getSliceTotal()) + " " + data.getSliceTotalUnit());
                }

                if(data.getSliceDownload() != null) {
                    data.setSliceDownloadValue(String.valueOf(data.getSliceDownload()) + " " + data.getSliceDownloadUnit());
                }

                if(data.getSliceTime() != null) {
                    data.setSliceTimeValue(String.valueOf(data.getSliceTime()) + " " + data.getSliceTimeUnit());
                }
            }
            return gson.toJsonTree(this.chargingRuleDataServiceTypeDatas).getAsJsonArray().toString();
        }catch(Exception e){
            getLogger().error("CHARGING_RULE_BASE_NAME_DATA", "Failed to get DefaultServiceDataFlows for data service type. Reason: "+e.getMessage());
            getLogger().trace(e);
            return "";
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME,        name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.GROUPS,      getGroups());

        if(chargingRuleDataServiceTypeDatas != null){
            JsonArray chargingRuleServiceTypesJsonArray = new JsonArray();

            for(ChargingRuleDataServiceTypeData serviceTypeData : chargingRuleDataServiceTypeDatas){
                JsonObject object = new JsonObject();
                object.addProperty(FieldValueConstants.DATA_SERVICE_TYPE, serviceTypeData.getDataServiceTypeData().getName());
                object.addProperty(FieldValueConstants.MONITORING_KEY, serviceTypeData.getMonitoringKey());

                object.addProperty(FieldValueConstants.SLICE_TOTAL, serviceTypeData.getSliceTotal()==null ? 0 : serviceTypeData.getSliceTotal());
                object.addProperty(FieldValueConstants.SLICE_TOTAL_UNIT, serviceTypeData.getSliceTotalUnit());

                object.addProperty(FieldValueConstants.SLICE_DOWNLOAD, serviceTypeData.getSliceDownload()==null ? 0 : serviceTypeData.getSliceDownload());
                object.addProperty(FieldValueConstants.SLICE_DOWNLOAD_UNIT, serviceTypeData.getSliceDownloadUnit());

                object.addProperty(FieldValueConstants.SLICE_UPLOAD, serviceTypeData.getSliceUpload() == null ? 0 : serviceTypeData.getSliceUpload());
                object.addProperty(FieldValueConstants.SLICE_UPLOAD_UNIT, serviceTypeData.getSliceUploadUnit());

                object.addProperty(FieldValueConstants.SLICE_TIME, serviceTypeData.getSliceTime() == null ? 0 : serviceTypeData.getSliceTime());
                object.addProperty(FieldValueConstants.SLICE_TIME_UNIT, serviceTypeData.getSliceTimeUnit());

                chargingRuleServiceTypesJsonArray.add(object);
            }
            jsonObject.add(FieldValueConstants.CHARGING_RULE_SERVICE_TYPES, chargingRuleServiceTypesJsonArray);
        }

        return jsonObject;
    }

    public ChargingRuleBaseNameData deepClone() throws CloneNotSupportedException {

        ChargingRuleBaseNameData newData = (ChargingRuleBaseNameData) this.clone();

        newData.qosProfileDetails = qosProfileDetails;
        newData.chargingRuleDataServiceTypeDatas = Collectionz.newArrayList();

        if(chargingRuleDataServiceTypeDatas.isEmpty() == false) {
            for (ChargingRuleDataServiceTypeData serviceDataFlow : chargingRuleDataServiceTypeDatas) {
                ChargingRuleDataServiceTypeData clonedServiceTypeData = serviceDataFlow.deepClone();
                clonedServiceTypeData.setChargingRuleBaseName(newData);
                newData.chargingRuleDataServiceTypeDatas.add(clonedServiceTypeData);
            }
        }
        return newData;
    }

    @Override
    public String toString() {
        return "ChargingRuleBaseNameData{" +
                "name='" + name + '\'' +
                ", description='" + description  ;
    }
    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }
}
