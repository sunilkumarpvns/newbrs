package com.elitecore.corenetvertex.pd.ratecard;

import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * manage Monetary Rate Card related information.
 * Created by ishani.dave on 17/12/17.
 */

@Entity(name = "com.elitecore.corenetvertex.pd.ratecard.NonMonetaryRateCardData")
@Table(name = "TBLM_NON_MONETARY_RATE_CARD")
public class NonMonetaryRateCardData implements Serializable{

    private static final long serialVersionUID = -9173903847327724345L;
    private String id;
    private String pulseUom;
    private String timeUom;
    private Long pulse;
    private Long time;
    private Long event;
    private String renewalIntervalUnit;
    private Integer renewalInterval;
    private Boolean proration = CommonStatusValues.DISABLE.isBooleanValue();
    private transient RateCardData rateCardData;
    private transient RncPackageData rncPackageData;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name="PULSE_UOM")
    public String getPulseUom() {
        return pulseUom;
    }

    public void setPulseUom(String pulseUom) {
        this.pulseUom = pulseUom;
    }

    @Column(name="TIME_UOM")
    public String getTimeUom() {
        return timeUom;
    }

    public void setTimeUom(String timeUom) {
        this.timeUom = timeUom;
    }

    @Column(name="PULSE")
    public Long getPulse() {
        return pulse;
    }

    public void setPulse(Long pulse) {
        this.pulse = pulse;
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

    @Column(name = "TIME")
    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Column(name = "EVENT")
    public Long getEvent() {
        return event;
    }

    public void setEvent(Long event) {
        this.event = event;
    }

    @Transient
    public RncPackageData getRncPackageData() {
        return rncPackageData;
    }

    public void setRncPackageData(RncPackageData rncPackageData) {
        this.rncPackageData = rncPackageData;
    }

    @Column(name = "RENEWAL_INTERVAL_UNIT")
    public String getRenewalIntervalUnit() {
        return renewalIntervalUnit;
    }

    public void setRenewalIntervalUnit(String renewalIntervalUnit) {
        this.renewalIntervalUnit = renewalIntervalUnit;
    }

    @Column(name = "RENEWAL_INTERVAL")
    public Integer getRenewalInterval() {
        return renewalInterval;
    }

    public void setRenewalInterval(Integer renewalInterval) {
        this.renewalInterval = renewalInterval;
    }

    @Column(name = "PRORATION")
    public Boolean getProration() {
        return proration;
    }

    public void setProration(Boolean proration) {
        this.proration = proration;
    }

    public Object copyModel(){
        NonMonetaryRateCardData newnonMonetaryRateCardData = new NonMonetaryRateCardData();
        newnonMonetaryRateCardData.setId(null);
        newnonMonetaryRateCardData.setEvent(this.event);
        newnonMonetaryRateCardData.setPulseUom(this.pulseUom);
        newnonMonetaryRateCardData.setPulse(this.pulse);
        newnonMonetaryRateCardData.setTime(this.time);
        newnonMonetaryRateCardData.setTimeUom(this.timeUom);
        newnonMonetaryRateCardData.setRenewalInterval(this.renewalInterval);
        newnonMonetaryRateCardData.setRenewalIntervalUnit(this.renewalIntervalUnit);
        newnonMonetaryRateCardData.setProration(this.proration);
        return newnonMonetaryRateCardData;
    }


    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.PULSE, pulse);
        jsonObject.addProperty(FieldValueConstants.PULSEUOM, pulseUom);
        jsonObject.addProperty(FieldValueConstants.TIME, time);
        jsonObject.addProperty(FieldValueConstants.TIMEUOM, timeUom);
        jsonObject.addProperty(FieldValueConstants.EVENT, event);
        jsonObject.addProperty(FieldValueConstants.RENEWAL_INTERVAL, renewalInterval);
        jsonObject.addProperty(FieldValueConstants.RENEWAL_INTERVAL_UNIT, renewalIntervalUnit);
        jsonObject.addProperty(FieldValueConstants.PRORATION,proration);
        return jsonObject;
    }


}
