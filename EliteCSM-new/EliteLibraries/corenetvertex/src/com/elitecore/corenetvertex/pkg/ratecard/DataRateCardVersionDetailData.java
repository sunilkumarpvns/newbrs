package com.elitecore.corenetvertex.pkg.ratecard;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.BigDecimalToStringGsonAdapter;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Data Monetary Rate Card Version Detail
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionDetailData")
@Table(name = "TBLM_DATA_RATE_CARD_VRSN_DTL")
public class DataRateCardVersionDetailData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String labelKey1;
    private String labelKey2;
    private Long slab1;
    private Long slab2;
    private Long slab3;
    private Long pulse1;
    private Long pulse2;
    private Long pulse3;
    @JsonAdapter(BigDecimalToStringGsonAdapter.class)
    private BigDecimal rate1;
    private BigDecimal rate2;
    private BigDecimal rate3;
    private String rateType;
    private Integer orderNumber;
    private transient DataRateCardVersionRelationData dataRateCardVersionRelationData;
    private RevenueDetailData revenueDetail;

    public DataRateCardVersionDetailData() {
        revenueDetail = new RevenueDetailData();
    }

    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @XmlElement(name="id")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name="label-key-1")
    @Column(name =  "LABEL_KEY_1")
    public String getLabelKey1() {
        return labelKey1;
    }

    public void setLabelKey1(String labelKey1) {
        this.labelKey1 = labelKey1;
    }

    @XmlElement(name="label-key-2")
    @Column(name =  "LABEL_KEY_2")
    public String getLabelKey2() {
        return labelKey2;
    }

    public void setLabelKey2(String labelKey2) {
        this.labelKey2 = labelKey2;
    }

    @XmlElement(name="slab-1")
    @Column(name="SLAB_1")
    public Long getSlab1() {
        return slab1;
    }

    public void setSlab1(Long slab1) {
        this.slab1 = slab1;
    }

    @XmlElement(name="slab-2")
    @Column(name="SLAB_2")
    public Long getSlab2() {
        return slab2;
    }


    public void setSlab2(Long slab2) {
        this.slab2 = slab2;
    }

    @XmlElement(name="slab-3")
    @Column(name="SLAB_3")
    public Long getSlab3() {
        return slab3;
    }

    public void setSlab3(Long slab3) {
        this.slab3 = slab3;
    }

    @XmlElement(name="pulse-1")
    @Column(name="PULSE_1")
    public Long getPulse1() {
        return pulse1;
    }

    public void setPulse1(Long pulse1) {
        this.pulse1 = pulse1;
    }

    @XmlElement(name="pulse-2")
    @Column(name="PULSE_2")
    public Long getPulse2() {
        return pulse2;
    }

    public void setPulse2(Long pulse2) {
        this.pulse2 = pulse2;
    }

    @XmlElement(name="pulse-3")
    @Column(name="PULSE_3")
    public Long getPulse3() {
        return pulse3;
    }

    public void setPulse3(Long pulse3) {
        this.pulse3 = pulse3;
    }

    @XmlElement(name="rate-1")
    @Column(name="RATE_1")
    public BigDecimal getRate1() {
        return rate1;
    }

    public void setRate1(BigDecimal rate1) {
        this.rate1 = rate1;
    }

    @XmlElement(name="rate-2")
    @Column(name="RATE_2")
    public BigDecimal getRate2() {
        return rate2;
    }

    public void setRate2(BigDecimal rate2) {
        this.rate2 = rate2;
    }

    @XmlElement(name="rate-3")
    @Column(name="RATE_3")
    public BigDecimal getRate3() {
        return rate3;
    }

    public void setRate3(BigDecimal rate3) {
        this.rate3 = rate3;
    }

    @XmlElement(name="rate-type")
    @Column(name="RATE_TYPE")
    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VER_REL_ID")
    @JsonIgnore
    @XmlTransient
    public DataRateCardVersionRelationData getDataRateCardVersionRelationData() {
        return dataRateCardVersionRelationData;
    }

    public void setDataRateCardVersionRelationData(DataRateCardVersionRelationData dataRateCardVersionRelationData) {
        this.dataRateCardVersionRelationData = dataRateCardVersionRelationData;
    }

    @XmlElement(name="oder-number")
    @Column(name = "ORDER_NUMBER")
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "REVENUE_DETAIL")
    public RevenueDetailData getRevenueDetail() {
        return revenueDetail;
    }

    public void setRevenueDetail(RevenueDetailData revenueDetail) {
        this.revenueDetail = revenueDetail;
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Label Key 1", labelKey1);
        jsonObject.addProperty("Label Kay 2", labelKey2);
        jsonObject.addProperty(FieldValueConstants.SLAB_ONE, slab1);
        jsonObject.addProperty(FieldValueConstants.SLAB_TWO, slab2);
        jsonObject.addProperty(FieldValueConstants.SLAB_THREE, slab3);
        jsonObject.addProperty(FieldValueConstants.PULSE_ONE, pulse1);
        jsonObject.addProperty(FieldValueConstants.PULSE_TWO, pulse2);
        jsonObject.addProperty(FieldValueConstants.PULSE_THREE, pulse3);
        jsonObject.addProperty(FieldValueConstants.RATE_ONE, rate1);
        jsonObject.addProperty(FieldValueConstants.RATE_TWO, rate2);
        jsonObject.addProperty(FieldValueConstants.RATE_THREE, rate3);
        jsonObject.addProperty("Rate Type", rateType);
        jsonObject.addProperty(FieldValueConstants.ORDER_NUMBER, orderNumber);
        if(Objects.nonNull(revenueDetail)){
            jsonObject.addProperty(FieldValueConstants.REVENUE_DETAIL, revenueDetail.getName());
        }
        return jsonObject;
    }

    public DataRateCardVersionDetailData copyModel() {
        DataRateCardVersionDetailData  newData = new DataRateCardVersionDetailData();
        newData.labelKey1 = this.labelKey1;
        newData.labelKey2 = this.labelKey2;
        newData.orderNumber = this.orderNumber;
        newData.pulse1 = this.pulse1;
        newData.pulse2 = this.pulse2;
        newData.pulse3 = this.pulse3;
        newData.rate1 = this.rate1;
        newData.rate2 = this.rate2;
        newData.rate3 = this.rate3;
        newData.rateType = this.rateType;
        newData.slab1 = this.slab1;
        newData.slab2 = this.slab2;
        newData.slab3 = this.slab3;
        newData.revenueDetail = this.revenueDetail;
        return newData;
    }
}