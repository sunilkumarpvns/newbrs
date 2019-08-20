package com.elitecore.corenetvertex.pkg.rnc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
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
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Rating And Charging Based Quota Profile Data
 * Created by dhyani on 19/7/17.
 */
@Entity
@Table(name = "TBLM_RnC_QUOTA_PROFILE")
public class RncProfileData extends ResourceData implements Serializable{

    private String name;
    private String description;
    private String quotaType;
    private String unitType;
    private transient PkgData pkgData;
    private List<RncProfileDetailData> rncProfileDetailDatas;
    private List<Integer> fupLevel;
    private transient List<QosProfileData> qosProfiles;
    private String balanceLevel;
    private Integer  renewalInterval;
    private String renewalIntervalUnit;
    private Boolean proration = CommonStatusValues.DISABLE.isBooleanValue();
    private Boolean carryForward = CommonStatusValues.DISABLE.isBooleanValue();

    private static final Comparator<RncProfileDetailData> RNC_PROFILE_DETAIL_DATA_COMPARATOR = Comparator.comparing(RncProfileDetailData::getFupLevel)
            .thenComparing(rncProfileDetailData -> rncProfileDetailData.getDataServiceTypeData().getName());
    private transient List<QuotaNotificationData> quotaNotificationDatas;

	private String pccProfileName;

    public RncProfileData() {
        rncProfileDetailDatas = Collectionz.newArrayList();
        fupLevel = Collectionz.newArrayList();
        quotaNotificationDatas = Collectionz.newArrayList();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name =  "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PACKAGE_ID")
    @XmlTransient
    public PkgData getPkgData() {
        return pkgData;
    }

    public void setPkgData(PkgData pkgData) {
        this.pkgData = pkgData;
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "rncProfileData",cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    public List<RncProfileDetailData> getRncProfileDetailDatas() {
        return rncProfileDetailDatas;
    }

    public void setRncProfileDetailDatas(List<RncProfileDetailData> rncProfileDetailDatas) {
        this.rncProfileDetailDatas = rncProfileDetailDatas;
    }

    @Override
    @Transient
    public String getGroups() {
        return pkgData.getGroups();
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
            for(RncProfileDetailData rncProfileDetailData : rncProfileDetailDatas){
                rncProfileDetailData.setRevenueDetail(null);
            }
        }

    }

    @Transient
    @Override
    public ResourceData getAuditableResource() {
        return pkgData;
    }

    @Override
    @Transient
    public String getHierarchy() {
        return pkgData.getHierarchy() +"<br>"+ getId() + "<br>"+ name;
    }


    @Transient
    @Override
    public String getAuditableId() {
        return pkgData.getId();
    }

    @Transient
    @XmlTransient
    public List<Integer> getFupLevel() {
        fupLevel = Collectionz.newArrayList();
        fupLevel.add(0);
        fupLevel.add(1);
        fupLevel.add(2);
        return fupLevel;
    }

    public void setFupLevel(List<Integer> fupLevel) {
        this.fupLevel = fupLevel;
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

    @OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="rncProfileData")
    @Fetch(FetchMode.SUBSELECT)
    @Where(clause="STATUS != 'DELETED'")
    @XmlTransient
    public List<QosProfileData> getQosProfiles() {
        return qosProfiles;
    }
    public void setQosProfiles(List<QosProfileData> qosProfiles) {
        this.qosProfiles = qosProfiles;
    }

    @Column(name = "QUOTA_TYPE")
    public String getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    @Column(name = "UNIT_TYPE")
    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    @Column(name = "RENEWAL_INTERVAL")
    @XmlElement(name = "renewal-interval")
    public Integer getRenewalInterval() {
        return renewalInterval;
    }

    public void setRenewalInterval(Integer renewalInterval) {
        this.renewalInterval = renewalInterval;
    }

    @Column(name="RENEWAL_INTERVAL_UNIT")
    @XmlElement(name="renewal-interval-unit")
    public String getRenewalIntervalUnit() {
        return renewalIntervalUnit;
    }

    public void setRenewalIntervalUnit(String renewalIntervalUnit) {
        this.renewalIntervalUnit = renewalIntervalUnit;
    }

    @Column(name = "BALANCE_LEVEL")
    public String getBalanceLevel() {
        return balanceLevel;
    }

    public void setBalanceLevel(String balanceLevel) {
        this.balanceLevel = balanceLevel;
    }

    @OneToMany(cascade={CascadeType.ALL},fetch=FetchType.LAZY,mappedBy="quotaProfile",orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @XmlTransient
    public List<QuotaNotificationData> getQuotaNotificationDatas() {
        return quotaNotificationDatas;
    }
    public void setQuotaNotificationDatas(List<QuotaNotificationData> quotaNotificationData) {
        this.quotaNotificationDatas = quotaNotificationData;
    }

    @Column(name="PRORATION")
    public Boolean getProration() {
        return proration;
    }

    public void setProration(Boolean proration) {
        this.proration = proration;
    }

    @Column(name = "CARRY_FORWARD")
    @XmlElement(name="carry-forward")
    public Boolean getCarryForward() {
        return carryForward;
    }

    public void setCarryForward(Boolean carryForward) {
        this.carryForward = carryForward;
    }

    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name", name);
        jsonObject.addProperty("Description", description);
        jsonObject.addProperty("Quota Type", quotaType!= null? QuotaUsageType.fromQuotaUsageType(quotaType).getValue():null);
        jsonObject.addProperty("Unit Type", unitType!=null? VolumeUnitType.fromVolumeUnitType(unitType).getValue():null);
        jsonObject.addProperty("Balance Level", balanceLevel != null ? BalanceLevel.valueOf(balanceLevel).getDisplayVal():null);
        jsonObject.addProperty("Renewal Interval", renewalInterval);
        jsonObject.addProperty("Renewal Interval Unit", renewalIntervalUnit!=null? RenewalIntervalUnit.fromRenewalIntervalUnit(renewalIntervalUnit).displayValue:null);
        jsonObject.addProperty("Proration",proration);
        jsonObject.addProperty("Carry Forward",carryForward);

        if(Collectionz.isNullOrEmpty(rncProfileDetailDatas) == false){
            Collections.sort(rncProfileDetailDatas, RNC_PROFILE_DETAIL_DATA_COMPARATOR);
            JsonObject fupLvlJsonObject = null;
            JsonArray fupLevelJsonArray  = new JsonArray();
            Integer fupLevel = null;
            JsonArray serviceJsonArray = new JsonArray();
            for(RncProfileDetailData rncProfileDetailDataTemp : rncProfileDetailDatas){
                if(fupLevel == null || fupLevel != rncProfileDetailDataTemp.getFupLevel()){
                    if(fupLevel != null){
                        fupLvlJsonObject.add((fupLevel > 0 ? ("FUP" + fupLevel) : "HSQ"), serviceJsonArray);
                        fupLevelJsonArray.add(fupLvlJsonObject);
                        serviceJsonArray = new JsonArray();
                    }
                    fupLevel = rncProfileDetailDataTemp.getFupLevel();
                    fupLvlJsonObject = new JsonObject();
                }
                serviceJsonArray.add(rncProfileDetailDataTemp.toJson());
            }
            if(fupLvlJsonObject!=null){
                fupLvlJsonObject.add((fupLevel > 0 ? ("FUP" + fupLevel) : "HSQ"), serviceJsonArray);
            }
            fupLevelJsonArray.add(fupLvlJsonObject);
            jsonObject.add("QuotaProfile Details", fupLevelJsonArray);
        }
        return jsonObject;
    }

	public void setPccProfileName(String pccProfileName) {
		this.pccProfileName = pccProfileName;
	}

	@Transient
	public String getPccProfileName() {
		return pccProfileName;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RncProfileData other = (RncProfileData) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public RncProfileData copyModel() {
        RncProfileData newData = new RncProfileData();
        newData.name = this.name;
        newData.balanceLevel = this.balanceLevel;
        newData.description = this.description;
        newData.fupLevel = this.fupLevel;
        newData.quotaType = this.quotaType;
        newData.renewalInterval = this.renewalInterval;
        newData.renewalIntervalUnit = this.renewalIntervalUnit;
        newData.unitType = this.unitType;
        newData.proration = this.proration;
        newData.carryForward = this.carryForward;

        List<RncProfileDetailData> rncProfileDetailDataList = Collectionz.newArrayList();
        for (RncProfileDetailData rncProfileDetailData : this.rncProfileDetailDatas) {
            RncProfileDetailData rncProfileDetailDataCopy = rncProfileDetailData.copyModel();
            rncProfileDetailDataCopy.setId(null);
            rncProfileDetailDataCopy.setRncProfileData(newData);
            rncProfileDetailDataList.add(rncProfileDetailDataCopy);
        }
        newData.setRncProfileDetailDatas(rncProfileDetailDataList);
        return newData;
    }

    /**
     * @return 1 if only HSQ is configured, 2 if FUP1 is configured, 3 if FUP2 is configured
     */
    @Transient
    public int getFupCountConfigured(){
        if(rncProfileDetailDatas==null){
            return 0;
        }

        int result = 0;
        for(RncProfileDetailData rncProfileDetailData : rncProfileDetailDatas){
            if(rncProfileDetailData.getFupLevel()>result){
                result = rncProfileDetailData.getFupLevel();
            }
        }
        return result+1;
    }

}
