package com.elitecore.corenetvertex.pkg.ratecard;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.TimestampToStringAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Data Monetary Rate Card Version Relation Data
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionRelationData")
@Table(name = "TBLM_DATA_RATE_CARD_VRSN_REL")
public class DataRateCardVersionRelationData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private transient DataRateCardData dataRateCardData;
    private transient List<DataRateCardVersionDetailData> dataRateCardVersionDetailDataList;
    private String versionName;
    private Timestamp effectiveFromDate;

    public DataRateCardVersionRelationData() {
        dataRateCardVersionDetailDataList = Collectionz.newArrayList();
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @XmlElement(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RATE_CARD_ID")
    @JsonIgnore
    @XmlTransient
    public DataRateCardData getDataRateCardData() {
        return dataRateCardData;
    }

    public void setDataRateCardData(DataRateCardData dataRateCardData) {
        this.dataRateCardData = dataRateCardData;
    }

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "dataRateCardVersionRelationData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @XmlElementWrapper(name="data-rate-card-version-details")
    @XmlElement(name="data-rate-card-version-detail")
    public List<DataRateCardVersionDetailData> getDataRateCardVersionDetailDataList() {
        return dataRateCardVersionDetailDataList;
    }

    public void setDataRateCardVersionDetailDataList(List<DataRateCardVersionDetailData> dataRateCardVersionDetailDataList) {
        this.dataRateCardVersionDetailDataList = dataRateCardVersionDetailDataList;
    }

    @XmlElement(name="version-name")
    @Column(name = "VERSION_NAME")
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @XmlJavaTypeAdapter(TimestampToStringAdapter.class)
    @XmlElement(name="effective-from-date")
    @Column(name = "EFFECTIVE_FROM_DATE")
    public Timestamp getEffectiveFromDate() {
        return effectiveFromDate;
    }

    public void setEffectiveFromDate(Timestamp effectiveFromDate) {
        this.effectiveFromDate = effectiveFromDate;
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        if(dataRateCardVersionDetailDataList != null){
            for(DataRateCardVersionDetailData dataRateCardVersionDetailData : getDataRateCardVersionDetailDataList()){
                jsonObject.add("Version Details_"+dataRateCardVersionDetailData.getId(), dataRateCardVersionDetailData.toJson());
            }
        }
        return jsonObject;
    }

    public DataRateCardVersionRelationData copyModel() {
        DataRateCardVersionRelationData newData = new DataRateCardVersionRelationData();

        newData.effectiveFromDate = this.effectiveFromDate;
        newData.versionName = this.versionName;
        List<DataRateCardVersionDetailData> dataRateCardVersionDetailDataList = Collectionz.newArrayList();
        for(DataRateCardVersionDetailData dataRateCardVersionDetailData : this.dataRateCardVersionDetailDataList){
            DataRateCardVersionDetailData dataRateCardVersionDetailDataCopy = dataRateCardVersionDetailData.copyModel();
            dataRateCardVersionDetailDataCopy.setId(null);
            dataRateCardVersionDetailDataCopy.setDataRateCardVersionRelationData(newData);
            dataRateCardVersionDetailDataList.add(dataRateCardVersionDetailDataCopy);
        }
        newData.setDataRateCardVersionDetailDataList(dataRateCardVersionDetailDataList);
        return newData;
    }

    public void removeRevenueDetail() {
        for(DataRateCardVersionDetailData dataRateCardVersionRelationData :  dataRateCardVersionDetailDataList){
            dataRateCardVersionRelationData.setRevenueDetail(null);
        }
    }
}
