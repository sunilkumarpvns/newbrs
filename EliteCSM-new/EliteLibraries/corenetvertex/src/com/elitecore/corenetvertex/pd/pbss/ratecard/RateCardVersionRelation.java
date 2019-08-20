package com.elitecore.corenetvertex.pd.pbss.ratecard;

import java.io.Serializable;
import java.util.List;

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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.google.gson.JsonObject;

/**
 * manage RateCard Version information.
 * Created by Saket on 17/12/17.
 */

@Entity(name="com.elitecore.corenetvertex.pd.ratecard.RateCardVersionRelation")
@Table(name="TBLM_RATECARD_VERSION_REL")

public class RateCardVersionRelation implements Serializable{

	private static final long serialVersionUID = -8548509093019357582L;
	
	private String id;
	private Integer orderNumber;
	private transient RateCardData rateCardData;
	private List<RateCardVersionDetail> rateCardVersionDetail;
	private String versionName;
	
	public RateCardVersionRelation() {
		rateCardVersionDetail = Collectionz.newArrayList();
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
	
	@Column(name =  "ORDER_NUMBER")
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RATECARD_ID")
    @JsonIgnore
	public RateCardData getRateCardData() {
		return rateCardData;
	}

	public void setRateCardData(RateCardData rateCardData) {
		this.rateCardData = rateCardData;
	}
	
	@Column(name =  "VERSION_NAME")
	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
	@OneToMany(fetch = FetchType.EAGER,mappedBy = "rateCardVersionRelation" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
	public List<RateCardVersionDetail> getRateCardVersionDetail() {
		return rateCardVersionDetail;
	}

	public void setRateCardVersionDetail(List<RateCardVersionDetail> rateCardVersionDetail) {
		this.rateCardVersionDetail = rateCardVersionDetail;
	}

	public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.ORDER_NUMBER, orderNumber);
        jsonObject.addProperty(FieldValueConstants.VERSION_NAME, versionName);
        return jsonObject;
    }
}
