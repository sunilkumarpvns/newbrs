package com.elitecore.corenetvertex.pd.pbss.ratecard;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.google.gson.JsonObject;

/**
 * manage RateCard Version wise information.
 * Created by Saket on 17/12/17.
 */

@Entity(name="com.elitecore.corenetvertex.pd.ratecard.RateCardVersionDetail")
@Table(name="TBLM_VERSION_DETAIL")
public class RateCardVersionDetail implements Serializable{
	
	private static final long serialVersionUID = -9173903847327724345L;

	private String id;
	private String label1;
	private String label2;
	private Double slab1;
	private Double slab2;
	private Double slab3;
	private Double pulse1;
	private Double pulse2;
	private Double pulse3;
	private Double rate1;
	private Double rate2;
	private Double rate3;
	private String tierRateType;
	private Timestamp fromDate;
	private Integer orderNumber;
	private transient RateCardVersionRelation rateCardVersionRelation;
	
	
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
	public Double getSlab1() {
		return slab1;
	}

	public void setSlab1(Double slab1) {
		this.slab1 = slab1;
	}

	@Column(name="SLAB_2")
	public Double getSlab2() {
		return slab2;
	}


	public void setSlab2(Double slab2) {
		this.slab2 = slab2;
	}

	@Column(name="SLAB_3")
	public Double getSlab3() {
		return slab3;
	}

	public void setSlab3(Double slab3) {
		this.slab3 = slab3;
	}

	@Column(name="PULSE_1")
	public Double getPulse1() {
		return pulse1;
	}

	public void setPulse1(Double pulse1) {
		this.pulse1 = pulse1;
	}

	@Column(name="PULSE_2")
	public Double getPulse2() {
		return pulse2;
	}

	public void setPulse2(Double pulse2) {
		this.pulse2 = pulse2;
	}

	@Column(name="PULSE_3")
	public Double getPulse3() {
		return pulse3;
	}

	public void setPulse3(Double pulse3) {
		this.pulse3 = pulse3;
	}
	
	@Column(name="RATE_1")
	public Double getRate1() {
		return rate1;
	}

	public void setRate1(Double rate1) {
		this.rate1 = rate1;
	}

	@Column(name="RATE_2")
	public Double getRate2() {
		return rate2;
	}

	public void setRate2(Double rate2) {
		this.rate2 = rate2;
	}

	@Column(name="RATE_3")
	public Double getRate3() {
		return rate3;
	}

	public void setRate3(Double rate3) {
		this.rate3 = rate3;
	}

	@Column(name="TIER_RATE_TYPE")
	public String getTierRateType() {
		return tierRateType;
	}


	public void setTierRateType(String tierRateType) {
		this.tierRateType = tierRateType;
	}

	@Column(name="FROM_DATE")
	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATECARD_VERSION_REL_ID")
    @JsonIgnore
	public RateCardVersionRelation getRateCardVersionRelation() {
		return rateCardVersionRelation;
	}

	public void setRateCardVersionRelation(RateCardVersionRelation rateCardVersionRelation) {
		this.rateCardVersionRelation = rateCardVersionRelation;
	}

	@Column(name = "ORDER_NUMBER")
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
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
        jsonObject.addProperty(FieldValueConstants.TIER_RATE_TYPE, tierRateType);
        jsonObject.addProperty(FieldValueConstants.FROM_DATE, String.valueOf(fromDate));
        jsonObject.addProperty(FieldValueConstants.ORDER_NUMBER, orderNumber);
        return jsonObject;
    }
}
