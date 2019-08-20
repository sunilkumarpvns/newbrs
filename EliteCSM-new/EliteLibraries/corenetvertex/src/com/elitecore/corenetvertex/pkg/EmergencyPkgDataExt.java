package com.elitecore.corenetvertex.pkg;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.validator.EmergencyPkgValidator;
import com.elitecore.corenetvertex.core.validator.QosProfileEmergencyPackageValidator;
import com.elitecore.corenetvertex.pkg.importemergencypkg.EmergencyPkgImportOperation;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.TimestampToStringAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Ishani on 29/11/16.
 */
@Entity
@Table(name = "TBLM_PACKAGE")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name","description","type","status","packageMode","availabilityStartDate","availabilityEndDate","qosProfiles","orderNumber","param1","param2"})
@Import(required = true, validatorClass = EmergencyPkgValidator.class, importClass = EmergencyPkgImportOperation.class)
@XmlRootElement(name="emergencyPkgData")
public class EmergencyPkgDataExt extends ResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    @SerializedName(FieldValueConstants.NAME)private String name;
    @SerializedName(FieldValueConstants.DESCRIPTION)private String description;
    @SerializedName(FieldValueConstants.TYPE)private String type;
    @SerializedName(FieldValueConstants.MODE)private String packageMode;
    @SerializedName(FieldValueConstants.ORDER_NUMBER) private Integer orderNumber;

    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityStartDate;
    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityEndDate;


    private transient List<QosProfileData> qosProfiles;

    @SerializedName(FieldValueConstants.PARAM1) private String param1;
    @SerializedName(FieldValueConstants.PARAM2)private String param2;

    private transient List<PkgGroupOrderData> pkgGroupWiseOrders;

    public EmergencyPkgDataExt() {
        qosProfiles = Collectionz.newArrayList();
        pkgGroupWiseOrders = Collectionz.newLinkedList();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Column(name = "STATUS")
    @XmlElement(name="status")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "TYPE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "PACKAGE_MODE")
    public String getPackageMode() {
        return packageMode;
    }

    public void setPackageMode(String packageMode) {
        this.packageMode = packageMode;
    }

    @Column(name = "AVAILABILITY_START_DATE")
    public Timestamp getAvailabilityStartDate() {
        return this.availabilityStartDate;
    }

    public void setAvailabilityStartDate(Timestamp availabilityStartDate) {
        this.availabilityStartDate = availabilityStartDate;
    }

    @Column(name = "AVAILABILITY_END_DATE")
    public Timestamp getAvailabilityEndDate() {
        return this.availabilityEndDate;
    }

    public void setAvailabilityEndDate(Timestamp availabilityEndDate) {
        this.availabilityEndDate = availabilityEndDate;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "pkgData", orphanRemoval = false)
    @Fetch(FetchMode.SUBSELECT)
    @Where(clause="STATUS != 'DELETED'")
    @org.hibernate.annotations.OrderBy(clause="ORDER_NO asc")
    @XmlElementWrapper(name="qos-profiles")
    @XmlElement(name="qos-profile")
    @Import(required = true, validatorClass = QosProfileEmergencyPackageValidator.class)
    public List<QosProfileData> getQosProfiles() {
        return qosProfiles;
    }

    public void setQosProfiles(List<QosProfileData> qosProfiles) {
        this.qosProfiles = qosProfiles;
    }




    @Override
    public String toString() {

        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", name)
                .append("Type", type)
                .append("Package Mode", packageMode)
                .append("Availibility Start Date", availabilityStartDate)
                .append("Availibility End Date", availabilityEndDate)
                .append("Order Number", orderNumber);

        toStringBuilder.append("\t");
        if (Collectionz.isNullOrEmpty(qosProfiles) == false) {

            for (QosProfileData qosProfileData : qosProfiles) {
                if(qosProfileData != null) {
                    toStringBuilder.append("QoS Profile", qosProfileData);
                }
            }
        }

        return toStringBuilder.toString();

    }

    @Transient
    @Override
    public String getHierarchy() {
        return getId() + "<br>" + name;
    }

    @Override
    public JsonObject toJson(){
        Gson gson =  new GsonBuilder().serializeNulls().create();
        return gson.toJsonTree(this).getAsJsonObject();
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


    public void setDeletedStatus(){
        setStatus(CommonConstants.STATUS_DELETED);
        for(QosProfileData qosProfile:qosProfiles){
            qosProfile.setDeletedStatus(CommonConstants.STATUS_DELETED);
        }
    }

    @Column(name = "ORDER_NO")
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Column(name = "PARAM_1")
    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    @Column(name = "PARAM_2")
    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @XmlTransient
    @Transient
    public List<PkgGroupOrderData> getPkgGroupWiseOrders() {
        return pkgGroupWiseOrders;
    }

    public void setPkgGroupWiseOrders(List<PkgGroupOrderData> pkgGroupWiseOrders) {
        this.pkgGroupWiseOrders = pkgGroupWiseOrders;
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }
}

