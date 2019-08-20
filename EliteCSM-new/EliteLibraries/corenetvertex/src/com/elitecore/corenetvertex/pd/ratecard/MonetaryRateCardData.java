package com.elitecore.corenetvertex.pd.ratecard;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * manage Monitory Rate Card related information.
 * Created by ishani.dave on 17/12/17.
 */

@Entity(name = "com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData")
@Table(name = "TBLM_MONETARY_RATE_CARD")
public class MonetaryRateCardData implements Serializable{

    private static final long serialVersionUID = -9173903847327724345L;
    private String id;
    private String rateUnit;
    private String pulseUnit;
    private String labelKey1;
    private String labelKey2;
    private List<MonetaryRateCardVersion> monetaryRateCardVersions;
    private transient RateCardData rateCardData;

    public MonetaryRateCardData() {
        monetaryRateCardVersions = Collectionz.newArrayList();
    }

    public MonetaryRateCardData copyModel(){
        MonetaryRateCardData newmonetaryRateCardData= new MonetaryRateCardData();
        newmonetaryRateCardData.setId(null);
        newmonetaryRateCardData.setRateUnit(this.rateUnit);
        newmonetaryRateCardData.setPulseUnit(this.pulseUnit);
        newmonetaryRateCardData.setLabelKey1(this.labelKey1);
        newmonetaryRateCardData.setLabelKey2(this.labelKey2);

        List<MonetaryRateCardVersion> newmonetaryRateCardVersions=new ArrayList<>();
        for (MonetaryRateCardVersion monetaryRateCardVersion: this.getMonetaryRateCardVersions()) {
            MonetaryRateCardVersion monetaryRateCardVersionCopy=monetaryRateCardVersion.copyModel();
            monetaryRateCardVersionCopy.setMonetaryRateCardData(newmonetaryRateCardData);
            newmonetaryRateCardVersions.add(monetaryRateCardVersionCopy);
        }

        newmonetaryRateCardData.monetaryRateCardVersions=newmonetaryRateCardVersions;
        return newmonetaryRateCardData;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "RATE_UNIT")
    public String getRateUnit() {
        return rateUnit;
    }

    public void setRateUnit(String rateUnit) {
        this.rateUnit = rateUnit;
    }

    @Column(name = "PULSE_UNIT")
    public String getPulseUnit() {
        return pulseUnit;
    }

    public void setPulseUnit(String pulseUnit) {
        this.pulseUnit = pulseUnit;
    }

    @Column(name =  "LABEL_KEY_1")
    public String getLabelKey1() {
        return labelKey1;
    }

    public void setLabelKey1(String labelKey1) {
        this.labelKey1 = labelKey1;
    }

    @Column(name =  "LABEL_KEY_2")
    public String getLabelKey2() {
        return labelKey2;
    }

    public void setLabelKey2(String labelKey2) {
        this.labelKey2 = labelKey2;
    }

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "monetaryRateCardData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    public List<MonetaryRateCardVersion> getMonetaryRateCardVersions() {
        return monetaryRateCardVersions;
    }

    public void setMonetaryRateCardVersions(List<MonetaryRateCardVersion> monetaryRateCardVersions) {
        this.monetaryRateCardVersions = monetaryRateCardVersions;
    }

    @OneToOne
    @JoinColumn(name = "RATE_CARD_ID",updatable = false)
    @XmlTransient
    @JsonIgnore
    public RateCardData getRateCardData() {
        return rateCardData;
    }

    public void setRateCardData(RateCardData rateCardData) {
        this.rateCardData = rateCardData;
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.RATEUOM, rateUnit);
        jsonObject.addProperty(FieldValueConstants.PULSEUOM, pulseUnit);
        jsonObject.addProperty(FieldValueConstants.LABEL_KEY_ONE,labelKey1);
        jsonObject.addProperty(FieldValueConstants.LABEL_KEY_TWO,labelKey2);
        if (Collectionz.isNullOrEmpty(monetaryRateCardVersions) == false) {
            JsonObject monetaryRateCardVersionJson = new JsonObject();
            for (MonetaryRateCardVersion monetaryRateCardVersion : monetaryRateCardVersions) {
                monetaryRateCardVersionJson.add(monetaryRateCardVersion.getName(), monetaryRateCardVersion.toJson());
            }
            jsonObject.add(FieldValueConstants.VERSION_INFO, monetaryRateCardVersionJson);
        }
        return jsonObject;
    }
}
