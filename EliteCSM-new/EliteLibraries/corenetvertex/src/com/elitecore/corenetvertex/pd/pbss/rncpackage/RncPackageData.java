package com.elitecore.corenetvertex.pd.pbss.rncpackage;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.pbss.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;

@Entity(name = "com.elitecore.corenetvertex.pd.pbss.rncpackage.RncPackageData")
@Table(name = "TBLM_RNC_PACKAGE")
public class RncPackageData extends DefaultGroupResourceData implements Serializable {

	private static final long serialVersionUID = 1710049796365131632L;
	private String name;
	private String description;
	private String scope;

	private transient List<RateCardData> rateCardData;
	private transient List<RateCardGroupData> rateCardGroupData;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "rncPackageData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	public List<RateCardData> getRateCardData() {
		return rateCardData;
	}





	public void setRateCardData(List<RateCardData> rateCardData) {
		this.rateCardData = rateCardData;
	}
	
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "rncPackageData", orphanRemoval = false)
	@Fetch(FetchMode.SUBSELECT)
	public List<RateCardGroupData> getRateCardGroupData() {
		return rateCardGroupData;
	}

	public void setRateCardGroupData(List<RateCardGroupData> rateCardGroupData) {
		this.rateCardGroupData = rateCardGroupData;
	}
	
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "SCOPE")
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@JsonIgnore
	@Transient
	@Override
	public String getResourceName() {
		return getName();
	}
	
	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.SCOPE, scope);
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
		
		return jsonObject;
	}
}
