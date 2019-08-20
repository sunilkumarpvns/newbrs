package com.elitecore.corenetvertex.sm.routing.network;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity(name="com.elitecore.corenetvertex.sm.routing.network.NetworkData")
@Table(name = "TBLM_NETWORK")
public class NetworkData extends DefaultGroupResourceData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private Integer mcc;
	private Integer mnc;
	private String technology;	
	private CountryData countryData;
	private OperatorData operatorData;
	private BrandData brandData;

	@OneToOne
	@JoinColumn(name = "BRAND_ID", nullable = false)
	@JsonIgnore
	public BrandData getBrandData() {
		return brandData;
	}

	public void setBrandData(BrandData brandData) {
		this.brandData = brandData;
	}

	@Column(name = "TECHNOLOGY")
	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	@OneToOne
	@JoinColumn(name = "COUNTRY_ID", nullable = false)
	@JsonIgnore
	public CountryData getCountryData() {
		return countryData;
	}

	public void setCountryData(CountryData countryData) {
		this.countryData = countryData;
	} 

	@OneToOne
	@JoinColumn(name = "OPERATOR_ID", nullable = false)
	@JsonIgnore
	public OperatorData getOperatorData() {
		return operatorData;
	}
	
	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}
	
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "MCC")
	public Integer getMcc() {
		return mcc;
	}
	
	public void setMcc(Integer mcc) {
		this.mcc = mcc;
	}
	
	@Column(name = "MNC")
	public Integer getMnc() {
		return mnc;
	}
	
	public void setMnc(Integer mnc) {
		this.mnc = mnc;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	@Transient
	public String getResourceName() {
		return name;
	}

	@Transient
	public String getCountryId() {
		if (this.getCountryData() != null) {
			return getCountryData().getId();
		}
		return null;
	}

	public void setCountryId(String countryId) {
		if (Strings.isNullOrBlank(countryId) == false) {
			CountryData countryData = new CountryData();
			countryData.setId(countryId);
			this.countryData = countryData;
		}
	}

	@Transient
	public String getOperatorId() {
		if (this.getOperatorData() != null) {
			return getOperatorData().getId();
		}
		return null;
	}

	public void setOperatorId(String operatorId) {
		if (Strings.isNullOrBlank(operatorId) == false) {
			OperatorData operatorData = new OperatorData();
			operatorData.setId(operatorId);
			this.operatorData = operatorData;
		}
	}

	@Transient
	public String getBrandId() {
		if (this.getBrandData() != null) {
			return getBrandData().getId();
		}
		return null;
	}

	public void setBrandId(String brandId) {
		if (Strings.isNullOrBlank(brandId) == false) {
			BrandData brandData = new BrandData();
			brandData.setId(brandId);
			this.brandData = brandData;
		}
	}

	@Override
    public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.MCC, mcc);
		jsonObject.addProperty(FieldValueConstants.MNC, mnc);
		jsonObject.addProperty(FieldValueConstants.TECHNOLOGY, technology);
		jsonObject.addProperty(FieldValueConstants.BRAND_DATA, brandData.getName());
		jsonObject.addProperty(FieldValueConstants.COUNTRY_DATA, countryData.getName());
		jsonObject.addProperty(FieldValueConstants.OPERATOR_DATA, operatorData.getName());
		return jsonObject;
	}
}
