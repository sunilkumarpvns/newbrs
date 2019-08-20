package com.elitecore.corenetvertex.sm.filelocation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.filemapping.FileMappingData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Used to manage File Output Configuration related information with DB
 * 
 * Created by Saket Kumar on 01/02/18.
 */

@Entity(name= "com.elitecore.corenetvertex.sm.filelocation.FileOutputConfigurationData")
@Table(name = "TBLM_FILE_OUTPUT_CONFIG")
public class FileOutputConfigurationData implements Serializable{

	private static final long serialVersionUID = 7631365040615970598L;
	
	private String id;
	private FileMappingData fileMappingData;
	private String extension;
	private String fieldSeparator;
	private String location;
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
	
	@JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MAPPING")
	public FileMappingData getFileMappingData() {
		return fileMappingData;
	}

	public void setFileMappingData(FileMappingData fileMappingData) {
		this.fileMappingData = fileMappingData;
	}
	
	@Transient
	public String getFileMappingId() {
		if (this.getFileMappingData() != null) {
			return getFileMappingData().getId();
		}
		return null;
	}

	public void setFileMappingId(String fileMappingId) {
		if (Strings.isNullOrBlank(fileMappingId) == false) {
			FileMappingData fileMappingData = new FileMappingData();
			fileMappingData.setId(fileMappingId);
			this.fileMappingData = fileMappingData;
		}
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
	
	@Column(name =  "EXTENSION")
	public String getExtension() {
		return extension;
	}
	
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	 @Column(name =  "FIELD_SEPARATOR")
	public String getFieldSeparator() {
		return fieldSeparator;
	}
	
	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}
	
	@Column(name =  "LOCATION")
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		if(getFileMappingData() != null) {
            jsonObject.addProperty(FieldValueConstants.FILE_MAPPING,fileMappingData.getName());
        }
		jsonObject.addProperty(FieldValueConstants.EXTENSION, extension);
		jsonObject.addProperty(FieldValueConstants.FIELD_SEPARATOR, fieldSeparator);
		jsonObject.addProperty(FieldValueConstants.LOCATION, location);

		return jsonObject;
	}
	
}
