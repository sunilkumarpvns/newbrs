package com.elitecore.corenetvertex.sm.filemapping;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Used to manage File Mapping Data related information with DB Created by
 * Seekarla.Krishna on 14/12/17.
 */
@Entity(name = "com.elitecore.corenetvertex.sm.filemapping.FileMappingData")
@Table(name = "TBLM_FILE_MAPPING")
public class FileMappingData extends DefaultGroupResourceData implements Serializable {

	private static final long serialVersionUID = 4350243648111030672L;
	private String name;
	private String description;
	private String type;
	private Boolean preserveOtherKeys;
	private List<FileMappingDetail> fileMappingDetail;

	public FileMappingData() {
		fileMappingDetail = Collectionz.newArrayList();
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

	@Column(name = "FILE_MAPPING_TYPE")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "PRESERVE_OTHER_KEYS")
	public Boolean getPreserveOtherKeys() {
		return preserveOtherKeys;
	}

	public void setPreserveOtherKeys(Boolean preserveOtherKeys) {
		this.preserveOtherKeys = preserveOtherKeys;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fileMappingData", cascade = {
			CascadeType.ALL }, orphanRemoval = true)
	@Fetch(FetchMode.SUBSELECT)
	public List<FileMappingDetail> getFileMappingDetail() {
		return fileMappingDetail;
	}

	public void setFileMappingDetail(List<FileMappingDetail> fileMappingDetail) {
		this.fileMappingDetail = fileMappingDetail;
	}

	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	@Transient
	@XmlTransient
	public String getHierarchy() {
		return getId() + "<br>" + name;
	}

	@Override
	@Transient
	@JsonIgnore
	public String getResourceName() {
		return getName();
	}

	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.NAME, name);
		jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
		jsonObject.addProperty(FieldValueConstants.TYPE, type);
		jsonObject.addProperty(FieldValueConstants.PRESERVE_OTHER_KEYS, preserveOtherKeys);
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
		if (fileMappingDetail != null) {
			JsonArray jsonArray = new JsonArray();
			for (FileMappingDetail fileMappingDetails : fileMappingDetail) {
				jsonArray.add(fileMappingDetails.toJson());
			}
			jsonObject.add("Mapping Data", jsonArray);
		}

		return jsonObject;
	}
}
