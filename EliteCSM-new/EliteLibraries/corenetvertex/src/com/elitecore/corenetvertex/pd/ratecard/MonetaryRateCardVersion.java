package com.elitecore.corenetvertex.pd.ratecard;

import com.elitecore.commons.base.Collectionz;
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
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * manage RateCard Version information.
 * Created by ishani on 17/12/17.
 */

@Entity(name="com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersionData")
@Table(name="TBLM_RATE_CARD_VERSION")

public class MonetaryRateCardVersion implements Serializable{

	private static final long serialVersionUID = -8548509093019357582L;

	private String id;
	private transient MonetaryRateCardData monetaryRateCardData;
	private List<MonetaryRateCardVersionDetail> monetaryRateCardVersionDetail;
	private Timestamp effectiveFromDate;
	private String name;

	public MonetaryRateCardVersion() {
		monetaryRateCardVersionDetail = Collectionz.newArrayList();
	}


	public MonetaryRateCardVersion copyModel(){
		MonetaryRateCardVersion newmonetaryRateCardVersion = new MonetaryRateCardVersion();
		newmonetaryRateCardVersion.setId(null);
		newmonetaryRateCardVersion.setEffectiveFromDate(this.effectiveFromDate);
		newmonetaryRateCardVersion.setName(this.name);

		List<MonetaryRateCardVersionDetail> newmonetaryRateCardVersionDetail = new ArrayList<>();
		for (MonetaryRateCardVersionDetail monetaryRateCardVrsDetail : this.monetaryRateCardVersionDetail) {
			MonetaryRateCardVersionDetail monetaryRateCardVersionDetailCopy = monetaryRateCardVrsDetail.copyModel();
			monetaryRateCardVersionDetailCopy.setId(null);
			monetaryRateCardVersionDetailCopy.setMonetaryRateCardVersion(newmonetaryRateCardVersion);
			newmonetaryRateCardVersionDetail.add(monetaryRateCardVersionDetailCopy);
		}
		newmonetaryRateCardVersion.monetaryRateCardVersionDetail = newmonetaryRateCardVersionDetail;
		return newmonetaryRateCardVersion;
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
	

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RATE_CARD_ID")
    @JsonIgnore
	public MonetaryRateCardData getMonetaryRateCardData() {
		return monetaryRateCardData;
	}

	public void setMonetaryRateCardData(MonetaryRateCardData monetaryRateCardData) {
		this.monetaryRateCardData = monetaryRateCardData;
	}
	
	@Column(name =  "NAME")
	public String getName() {
		return name;
	}

	public void setName(String versionName) {
		this.name = versionName;
	}

	@Column(name = "EFFECTIVE_FROM_DATE")
	public Timestamp getEffectiveFromDate() {
		return effectiveFromDate;
	}

	public void setEffectiveFromDate(Timestamp effectiveFromDate) {
		this.effectiveFromDate = effectiveFromDate;
	}
	
	@OneToMany(fetch = FetchType.EAGER,mappedBy = "monetaryRateCardVersion" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
	@org.hibernate.annotations.OrderBy(clause="ORDER_NUMBER asc")
	public List<MonetaryRateCardVersionDetail> getMonetaryRateCardVersionDetail() {
		return monetaryRateCardVersionDetail;
	}

	public void setMonetaryRateCardVersionDetail(List<MonetaryRateCardVersionDetail> monetaryRateCardVersionDetail) {
		this.monetaryRateCardVersionDetail = monetaryRateCardVersionDetail;
	}

	public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
		if(Collectionz.isNullOrEmpty(monetaryRateCardVersionDetail) == false){
			for (MonetaryRateCardVersionDetail monetaryRateCardVersionDetailData : monetaryRateCardVersionDetail) {
				jsonObject.add("Version Details_"+ monetaryRateCardVersionDetailData.getId(), monetaryRateCardVersionDetailData.toJson());
			}
		}
	    return jsonObject;
    }


}
