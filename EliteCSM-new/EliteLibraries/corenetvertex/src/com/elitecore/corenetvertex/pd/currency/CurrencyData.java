package com.elitecore.corenetvertex.pd.currency;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;

@Entity(name = "com.elitecore.corenetvertex.pd.currency.CurrencyData")
@Table(name = "TBLM_CURRENCY")
public class CurrencyData extends DefaultGroupResourceData implements Serializable {

	private static final long serialVersionUID = -1266655570105019791L;
	private String fromIsoCode;
	private String toIsoCode;
	private Double rate ;
	private Timestamp effectiveDate;


	@Column(name="RATE")
	public Double getRate() {
		return rate;
	}

	
	public void setRate(Double rate) {
		this.rate = rate;
	}
	
	@Column(name = "FROM_ISO_CODE")
	public String getFromIsoCode() {
		return fromIsoCode;
	}

	public void setFromIsoCode(String fromIsoCode) {
		this.fromIsoCode = fromIsoCode;
	}

	@Column(name = "TO_ISO_CODE")
	public String getToIsoCode() {
		return toIsoCode;
	}

	public void setToIsoCode(String toIsoCode) {
		this.toIsoCode = toIsoCode;
	}

	
	
	@Column(name = "EFFECTIVE_DATE")
	public Timestamp getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	@Transient
	@XmlTransient
	public String getHierarchy() {
		return getId() + "<br>" + rate;
	}

	@Transient
	@Override
	@JsonIgnore
	public String getResourceName() {
		return String.valueOf(getRate());
	}
	
	
	@Override
    @Column(name="STATUS")
    public String getStatus() {
        return super.getStatus();
    }
	

	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.FROMISOCODE, fromIsoCode);
		jsonObject.addProperty(FieldValueConstants.TOISOCODE, toIsoCode);
		jsonObject.addProperty(FieldValueConstants.RATE, rate);
		jsonObject.addProperty(FieldValueConstants.EFFECTIVE_DATE, String.valueOf(effectiveDate));
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		return jsonObject;
	}

}
