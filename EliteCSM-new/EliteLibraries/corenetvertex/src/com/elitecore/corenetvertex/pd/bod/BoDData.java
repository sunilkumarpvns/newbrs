package com.elitecore.corenetvertex.pd.bod;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.DoubleToStringAdapter;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.TimestampToStringAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;


@Entity(name = "com.elitecore.corenetvertex.pd.bod.BoDData")
@Table(name = "TBLM_BOD")
public class BoDData extends ResourceData {
    private static final long serialVersionUID = 1L;
    @SerializedName(FieldValueConstants.NAME)private String name;
    @SerializedName(FieldValueConstants.DESCRIPTION)private String description;
    @SerializedName(FieldValueConstants.PRICE) @XmlJavaTypeAdapter(DoubleToStringAdapter.class) private Double price;
    @SerializedName(FieldValueConstants.MODE)private String packageMode;
    @SerializedName(FieldValueConstants.VALIDITIY_PERIOD)private Integer validityPeriod;
    @SerializedName(FieldValueConstants.VALIDITY_PERIOD_UNIT)private String validityPeriodUnit = ValidityPeriodUnit.DAY.displayValue;
    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityStartDate;
    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityEndDate;
    @SerializedName(FieldValueConstants.PARAM1) private String param1;
    @SerializedName(FieldValueConstants.PARAM2)private String param2;
    private transient List<BoDQosMultiplierData> bodQosMultiplierDatas;
    private String applicableQosProfiles;

    public BoDData(){
        bodQosMultiplierDatas = Collectionz.newArrayList();
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
    public String getStatus() {
        return super.getStatus();
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "PACKAGE_MODE")
    public String getPackageMode() {
        return packageMode;
    }

    public void setPackageMode(String packageMode) {
        this.packageMode = packageMode;
    }

    @Column(name="PRICE")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name = "VALIDITY_PERIOD")
    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    @Column(name = "VALIDITY_PERIOD_UNIT")
    public String getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public void setValidityPeriodUnit(String validityPeriodUnit) {
        this.validityPeriodUnit = validityPeriodUnit;
    }

    @Column(name="AVAILABILITY_START_DATE")
    public Timestamp getAvailabilityStartDate() {
        return availabilityStartDate;
    }

    @JsonProperty
    public void setAvailabilityStartDate(Timestamp availabilityStartDate) {
        this.availabilityStartDate = availabilityStartDate;
    }

    @Column(name="AVAILABILITY_END_DATE")
    public Timestamp getAvailabilityEndDate() {
        return availabilityEndDate;
    }

    @JsonProperty
    public void setAvailabilityEndDate(Timestamp availabilityEndDate) {
        this.availabilityEndDate = availabilityEndDate;
    }

    @Column(name="PARAM_1")
    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    @Column(name="PARAM_2")
    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "bodData", orphanRemoval = false)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("fupLevel ASC ")
    public List<BoDQosMultiplierData> getBodQosMultiplierDatas() {
        return bodQosMultiplierDatas;
    }

    public void setBodQosMultiplierDatas(
            List<BoDQosMultiplierData> bodQosMultiplierDatas) {
        this.bodQosMultiplierDatas = bodQosMultiplierDatas;
    }

    @Column(name = "QOS_PROFILE_NAMES")
    public String getApplicableQosProfiles() {
        return applicableQosProfiles;
    }

    public void setApplicableQosProfiles(String applicableQosProfile) {
        this.applicableQosProfiles = applicableQosProfile;
    }

    @Override
    @Column(name="GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Transient
    @JsonIgnore
    public String getValidity() {
        if (validityPeriod != null) {
            return validityPeriod + " " + validityPeriodUnit;
        }
        return "";
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

    @Override
    public String toString() {

        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", name)
                .append("Package Mode", packageMode)
                .append("Validity Period", validityPeriod)
                .append("Validity Period Unit", validityPeriodUnit);

        toStringBuilder.append("\t");

        return toStringBuilder.toString();

    }

    @Override
    public JsonObject toJson() {
        JsonObject bodData = new JsonObject();
        bodData.addProperty("Name",name);
        bodData.addProperty("Description",description);
        bodData.addProperty("Mode",packageMode);
        bodData.addProperty("Validity Period",validityPeriod);
        bodData.addProperty("Validity Period Unit",validityPeriodUnit);
        bodData.addProperty("Groups",getGroups());
        bodData.addProperty("Status",getStatus());
        bodData.addProperty("Applicable QoS Profiles",getApplicableQosProfiles());
        bodData.addProperty( "Price",getPrice());
        bodData.addProperty(FieldValueConstants.AVAILABILITY_START_DATE, Objects.isNull(availabilityStartDate) ? "": availabilityStartDate.toString());
        bodData.addProperty(FieldValueConstants.AVAILABILITY_END_DATE,Objects.isNull(availabilityEndDate) ? "":availabilityEndDate.toString());
        bodData.addProperty("Param1",param1);
        bodData.addProperty("Param2",param2);

        if(Collectionz.isNullOrEmpty(bodQosMultiplierDatas) == false){
            JsonArray jsonArray = new JsonArray();
            for(BoDQosMultiplierData bodQosMultiplierData : bodQosMultiplierDatas){
                jsonArray.add(bodQosMultiplierData.toJson());
            }
            bodData.add("BoD QoS Multiplier ", jsonArray);
        }
        return bodData;
    }

    @Transient
    @JsonIgnore
    public List<String> getApplicableQosProfileNames(){
        List<String> applicableQosProfileNames = Collectionz.newArrayList();
        if(Strings.isNullOrBlank(applicableQosProfiles) == false){
            applicableQosProfileNames = Strings.splitter(CommonConstants.COMMA).trimTokens().split(applicableQosProfiles);
        }
        return applicableQosProfileNames;
    }
}