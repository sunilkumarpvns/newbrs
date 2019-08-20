package com.elitecore.corenetvertex.pd.ratecard;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.TierRateType;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * manage RateCard Version wise information.
 * Created by ishani on 17/12/17.
 */

@Entity(name="com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersionDetailData")
@Table(name="TBLM_RATE_CARD_VERSION_DETAIL")
public class  MonetaryRateCardVersionDetail implements Serializable{

    private static final long serialVersionUID = -9173903847327724345L;

    private String id;
    private String label1;
    private String label2;
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
    private Integer discount;
    private RevenueDetailData revenueDetail;
    private String rateType = TierRateType.FLAT.name();
    private Integer orderNumber;
    private transient MonetaryRateCardVersion monetaryRateCardVersion;

    public MonetaryRateCardVersionDetail() {
        revenueDetail = new RevenueDetailData();
    }

    public MonetaryRateCardVersionDetail copyModel(){
        MonetaryRateCardVersionDetail newMonetaryRateCardVersionDetail=new MonetaryRateCardVersionDetail();
        newMonetaryRateCardVersionDetail.setId(null);
        newMonetaryRateCardVersionDetail.setLabel1(this.label1);
        newMonetaryRateCardVersionDetail.setLabel2(this.label2);
        newMonetaryRateCardVersionDetail.setSlab1(this.slab1);
        newMonetaryRateCardVersionDetail.setSlab2(this.slab2);
        newMonetaryRateCardVersionDetail.setSlab3(this.slab3);
        newMonetaryRateCardVersionDetail.setPulse1(this.pulse1);
        newMonetaryRateCardVersionDetail.setPulse2(this.pulse2);
        newMonetaryRateCardVersionDetail.setPulse3(this.pulse3);
        newMonetaryRateCardVersionDetail.setRate1(this.rate1);
        newMonetaryRateCardVersionDetail.setRate2(this.rate2);
        newMonetaryRateCardVersionDetail.setRate3(this.rate3);
        newMonetaryRateCardVersionDetail.setDiscount(this.discount);
        newMonetaryRateCardVersionDetail.setRevenueDetail(this.revenueDetail);
        newMonetaryRateCardVersionDetail.setRateType(this.getRateType());
        newMonetaryRateCardVersionDetail.setOrderNumber(this.getOrderNumber());
        return newMonetaryRateCardVersionDetail;
    }


    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name="LABEL_1")
    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    @Column(name="LABEL_2")
    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    @Column(name="SLAB_1")
    public Long getSlab1() {
        return slab1;
    }

    public void setSlab1(Long slab1) {
        this.slab1 = slab1;
    }

    @Column(name="SLAB_2")
    public Long getSlab2() {
        return slab2;
    }


    public void setSlab2(Long slab2) {
        this.slab2 = slab2;
    }

    @Column(name="SLAB_3")
    public Long getSlab3() {
        return slab3;
    }

    public void setSlab3(Long slab3) {
        this.slab3 = slab3;
    }

    @Column(name="PULSE_1")
    public Long getPulse1() {
        return pulse1;
    }

    public void setPulse1(Long pulse1) {
        this.pulse1 = pulse1;
    }

    @Column(name="PULSE_2")
    public Long getPulse2() {
        return pulse2;
    }

    public void setPulse2(Long pulse2) {
        this.pulse2 = pulse2;
    }

    @Column(name="PULSE_3")
    public Long getPulse3() {
        return pulse3;
    }

    public void setPulse3(Long pulse3) {
        this.pulse3 = pulse3;
    }

    @Column(name="RATE_1")
    public BigDecimal getRate1() {
        return rate1;
    }

    public void setRate1(BigDecimal rate1) {
        this.rate1 = rate1;
    }

    @Column(name="RATE_2")
    public BigDecimal getRate2() {
        return rate2;
    }

    public void setRate2(BigDecimal rate2) {
        this.rate2 = rate2;
    }

    @Column(name="RATE_3")
    public BigDecimal getRate3() {
        return rate3;
    }

    public void setRate3(BigDecimal rate3) {
        this.rate3 = rate3;
    }


    @Column(name="DISCOUNT")
    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    @Column(name="RATE_TYPE")
    public String getRateType() {
        return rateType;
    }


    public void setRateType(String rateType) {
        if(Strings.isNullOrBlank(rateType)){
            rateType = TierRateType.FLAT.name();
        }
        this.rateType = rateType;
    }

    @Column(name = "ORDER_NUMBER")
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    @ManyToOne
    @JoinColumn(name = "RATE_CARD_VERSION_ID")
    @JsonIgnore
    public MonetaryRateCardVersion getMonetaryRateCardVersion() {
        return monetaryRateCardVersion;
    }

    public void setMonetaryRateCardVersion(MonetaryRateCardVersion monetaryRateCardVersion) {
        this.monetaryRateCardVersion = monetaryRateCardVersion;
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
        jsonObject.addProperty(FieldValueConstants.LABEL_ONE, label1);
        jsonObject.addProperty(FieldValueConstants.LABEL_TWO, label2);
        jsonObject.addProperty(FieldValueConstants.SLAB_ONE, slab1);
        jsonObject.addProperty(FieldValueConstants.SLAB_TWO, slab2);
        jsonObject.addProperty(FieldValueConstants.SLAB_THREE, slab3);
        jsonObject.addProperty(FieldValueConstants.PULSE_ONE, pulse1);
        jsonObject.addProperty(FieldValueConstants.PULSE_TWO, pulse2);
        jsonObject.addProperty(FieldValueConstants.PULSE_THREE, pulse3);
        jsonObject.addProperty(FieldValueConstants.RATE_ONE, rate1);
        jsonObject.addProperty(FieldValueConstants.RATE_TWO, rate2);
        jsonObject.addProperty(FieldValueConstants.RATE_THREE, rate3);
        jsonObject.addProperty(FieldValueConstants.DISCOUNT,discount);
        if(Objects.nonNull(revenueDetail)) {
            jsonObject.addProperty(FieldValueConstants.REVENUE_DETAIL, revenueDetail.getName());
        }
        jsonObject.addProperty(FieldValueConstants.TIER_RATE_TYPE, rateType);

        return jsonObject;
    }
}
