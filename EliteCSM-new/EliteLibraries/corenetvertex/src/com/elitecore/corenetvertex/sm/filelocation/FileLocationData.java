package com.elitecore.corenetvertex.sm.filelocation;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.filemapping.FileMappingData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;
/**
 * Used to manage file location data related information with DB
 * 
 * Created by Ajay Pandey on 16/12/17.
 */

@Entity(name= "com.elitecore.corenetvertex.sm.filelocation.FileLocationData")
@Table(name = "TBLM_FILE_LOCATION")
public class FileLocationData extends DefaultGroupResourceData implements Serializable {
	
	private static final long serialVersionUID = 5518534212348970291L;
	private String name;
	private String description;
	private String ratingType;
	private String sortingType;
	private String sortingCriteria;
	private String inputPath;
	private String errorPath;
	private String archivePath;
	private String tableName;
	
	private DatabaseData databaseData;
	private FileMappingData fileMappingData;
	private List<FileOutputConfigurationData> fileOutputConfigurationData;
	private List<ColumnMappingData> columnMappingData;
	
	public FileLocationData() {
		fileOutputConfigurationData = Collectionz.newArrayList();
		columnMappingData = Collectionz.newArrayList();
	}

	@JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FILE_MAPPING_ID")
	public FileMappingData getFileMappingData() {
		return fileMappingData;
	}

	public void setFileMappingData(FileMappingData fileMappingData) {
		this.fileMappingData = fileMappingData;
	}
	
	@OneToMany(fetch = FetchType.EAGER,mappedBy = "fileLocationData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
	public List<FileOutputConfigurationData> getFileOutputConfigurationData() {
		return fileOutputConfigurationData;
	}

	public void setFileOutputConfigurationData(List<FileOutputConfigurationData> fileOutputConfigurationData) {
		this.fileOutputConfigurationData = fileOutputConfigurationData;
	}

	@OneToMany(fetch = FetchType.EAGER,mappedBy = "fileLocationData" ,cascade = {CascadeType.ALL},orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
	public List<ColumnMappingData> getColumnMappingData() {
		return columnMappingData;
	}

	public void setColumnMappingData(List<ColumnMappingData> columnMappingData) {
		this.columnMappingData = columnMappingData;
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
			FileMappingData mappingData = new FileMappingData();
			mappingData.setId(fileMappingId);
			this.fileMappingData = mappingData;
		}
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

	
	@Column(name = "RATING_TYPE")
	public String getRatingType() {
		return ratingType;
	}

	public void setRatingType(String ratingType) {
		this.ratingType = ratingType;
	}

	@Column(name = "SORTING_TYPE")
	public String getSortingType() {
		return sortingType;
	}

	public void setSortingType(String sortingType) {
		this.sortingType = sortingType;
	}
	
	@Column(name = "SORTING_CRITERIA")
	public String getSortingCriteria() {
		return sortingCriteria;
	}

	public void setSortingCriteria(String sortingCriteria) {
		this.sortingCriteria = sortingCriteria;
	}
	
	@Column(name = "INPUT_PATH")
	public String getInputPath() {
		return inputPath;
	}
	
	
	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}
	
	@Column(name = "ERROR_PATH")
	public String getErrorPath() {
		return errorPath;
	}

	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}

	@Column(name = "ARCHIVE_PATH")
	public String getArchivePath() {
		return archivePath;
	}

	public void setArchivePath(String archivePath) {
		this.archivePath = archivePath;
	}

	
	@JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DB_DATASOURCE")
	public DatabaseData getDatabaseData() {
		return databaseData;
	}

	public void setDatabaseData(DatabaseData databaseData) {
		this.databaseData = databaseData;
	}
	
	@Transient
	public String getDatabaseId() {
		if (this.getDatabaseData() != null) {
			return getDatabaseData().getId();
		}
		return null;
	}

	public void setDatabaseId(String databaseId) {
		if (Strings.isNullOrBlank(databaseId) == false) {
			DatabaseData dbData = new DatabaseData();
			dbData.setId(databaseId);
			this.databaseData = dbData;
		}
	}

	@Column(name = "TABLE_NAME")
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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
		jsonObject.addProperty(FieldValueConstants.RATING_TYPE, ratingType);
		jsonObject.addProperty(FieldValueConstants.SORTING_TYPE, sortingType);
		jsonObject.addProperty(FieldValueConstants.SORTING_CRITERIA, sortingCriteria);
		jsonObject.addProperty(FieldValueConstants.INPUT_PATH, inputPath);
		jsonObject.addProperty(FieldValueConstants.ERROR_PATH, errorPath);
		jsonObject.addProperty(FieldValueConstants.ARCHIVE_PATH, archivePath);
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
		
		if (fileOutputConfigurationData != null) {
			JsonArray jsonArray = new JsonArray();
			for (FileOutputConfigurationData fileOutputConfigurationDatas : fileOutputConfigurationData) {
				jsonArray.add(fileOutputConfigurationDatas.toJson());
			}
			jsonObject.add("File Output Configuration", jsonArray);
		}
		
		if(getDatabaseData() != null) {
            jsonObject.addProperty(FieldValueConstants.DB_DATASOURCE,databaseData.getName());
        }
		jsonObject.addProperty(FieldValueConstants.TABLE_NAME, tableName);
		
		if (columnMappingData != null) {
			JsonArray jsonArray = new JsonArray();
			for (ColumnMappingData columnMappingDatas : columnMappingData) {
				jsonArray.add(columnMappingDatas.toJson());
			}
			jsonObject.add("Column Mapping", jsonArray);
		}

		return jsonObject;
	}

	//FIXME - complete this code. Committing to remove compilation error
	@Transient
	public String getOutputPath() {
		return fileOutputConfigurationData.get(0).getLocation();
	}

}
