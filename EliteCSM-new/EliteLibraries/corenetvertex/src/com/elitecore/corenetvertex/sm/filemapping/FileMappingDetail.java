package com.elitecore.corenetvertex.sm.filemapping;

import java.io.Serializable;

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
 * Used to manage File Mapping Details related information with DB Created by
 * Seekarla.Krishna on 14/12/17.
 */
@Entity(name = "com.elitecore.corenetvertex.filemapping.FileMappingDetail")
@Table(name = "TBLM_FILE_MAPPING_DETAIL")
public class FileMappingDetail implements Serializable {

	private static final long serialVersionUID = 3150700294614190378L;
	private String id;
	private String sourceKey;
	private String destinationKey;
	private String valueMapping;
	private String defaultValue;
	private transient FileMappingData fileMappingData;

	
	

	@Id
	@GeneratedValue(generator = "eliteSequenceGenerator")
	@Column(name = "ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "SOURCE_KEY")
	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	@Column(name = "DESTINATION_KEY")
	public String getDestinationKey() {
		return destinationKey;
	}

	public void setDestinationKey(String destinationKey) {
		this.destinationKey = destinationKey;
	}
	
	@Column(name = "VALUE_MAPPING")
	public String getValueMapping() {
		return valueMapping;
	}

	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}

	@Column(name = "DEFAULT_VALUE")
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FILE_MAPPING_ID")
	@JsonIgnore
	public FileMappingData getFileMappingData() {
		return fileMappingData;
	}

	public void setFileMappingData(FileMappingData fileMappingData) {
		this.fileMappingData = fileMappingData;
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.SOURCE_KEY, sourceKey);
		jsonObject.addProperty(FieldValueConstants.DESTINATION_KEY, destinationKey);
		jsonObject.addProperty(FieldValueConstants.DEFAULT_VALUE, defaultValue);
		return jsonObject;
	}
}
