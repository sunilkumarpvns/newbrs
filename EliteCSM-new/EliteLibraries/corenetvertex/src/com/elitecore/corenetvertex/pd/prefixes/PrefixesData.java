package com.elitecore.corenetvertex.pd.prefixes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.account.PrefixListMasterData;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;

/**
 * Used to prefixes related information with DB Created by 
 * Ajay Pandey on
 * 17/12/17.
 */

@Entity(name = "com.elitecore.corenetvertex.pd.prefixes.PrefixesData")
@Table(name = "TBLM_PREFIXES")
public class PrefixesData extends DefaultGroupResourceData implements Serializable {

	private static final long serialVersionUID = 5518534212348970291L;
	private String prefix;
	private String name;
	private String description;
	private Integer countryCode;
	private Integer areaCode;
	private String masterPrefixId;
	
	private transient PrefixListMasterData prefixMaster;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PREFIX_LIST_MASTER_ID")
	@Fetch(FetchMode.JOIN)
	@XmlTransient
	@JsonIgnore
	public PrefixListMasterData getPrefixMaster() {
		return prefixMaster;
	}

	public void setPrefixMaster(PrefixListMasterData prefixMaster) {
		this.prefixMaster = prefixMaster;
	}
	
	@Transient
	public String getMasterPrefixId() {
		if(prefixMaster != null){
			this.masterPrefixId = this.prefixMaster.getId();
		}
		return masterPrefixId;
	}

	public void setMasterPrefixId(String masterPrefixId) {
		if(Strings.isNullOrBlank(masterPrefixId) == false){
			PrefixListMasterData prefixListMasterData = new PrefixListMasterData();
			prefixListMasterData.setId(masterPrefixId);
			this.prefixMaster = prefixListMasterData;
		}
		this.masterPrefixId = masterPrefixId;
	}

	@Column(name = "PREFIX")
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
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

	@Column(name = "COUNTRY_CODE")
	public Integer getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}

	@Column(name = "AREA_CODE")
	public Integer getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(Integer areaCode) {
		this.areaCode = areaCode;
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
		jsonObject.addProperty(FieldValueConstants.PREFIX, prefix);
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.COUNTRY_CODE, countryCode);
		jsonObject.addProperty(FieldValueConstants.AREA_CODE, areaCode);
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
		return jsonObject;
	}

}