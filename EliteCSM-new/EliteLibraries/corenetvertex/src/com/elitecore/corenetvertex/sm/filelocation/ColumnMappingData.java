package com.elitecore.corenetvertex.sm.filelocation;

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
 * Used to manage File Output Configuration related information with DB
 * 
 * Created by Saket Kumar on 01/02/18.
 */

@Entity(name= "com.elitecore.corenetvertex.sm.filelocation.ColumnMappingData")
@Table(name = "TBLM_FILE_LOCATION_COL_MAPPING")
public class ColumnMappingData implements Serializable{

	private static final long serialVersionUID = -8138888130215312099L;
	
	private String id;
	private String sourceKey;
	private String columnName;
	private String valueMapping;
	private String defaultValue;
	private transient FileLocationData fileLocationData;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FILE_LOCATION_ID")
    @JsonIgnore
	public FileLocationData getFileLocationData() {
		return fileLocationData;
	}

	public void setFileLocationData(FileLocationData fileLocationData) {
		this.fileLocationData = fileLocationData;
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
	
	@Column(name =  "SOURCE_KEY")
	public String getSourceKey() {
		return sourceKey;
	}
	
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	
	@Column(name =  "COLUMN_NAME")
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	@Column(name =  "VALUE_MAPPING")
	public String getValueMapping() {
		return valueMapping;
	}
	
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	
	@Column(name =  "DEFAULT_VALUE")
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.SOURCE_KEY, sourceKey);
		jsonObject.addProperty(FieldValueConstants.COLUMN_NAME, columnName);
		jsonObject.addProperty(FieldValueConstants.VALUE_MAPPING, valueMapping);
		jsonObject.addProperty(FieldValueConstants.DEFAULT_VALUE, defaultValue);

		return jsonObject;
	}
	
}
