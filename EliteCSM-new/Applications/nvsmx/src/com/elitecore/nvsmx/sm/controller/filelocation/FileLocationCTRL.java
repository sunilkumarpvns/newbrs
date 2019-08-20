package com.elitecore.nvsmx.sm.controller.filelocation;

import static com.opensymphony.xwork2.Action.SUCCESS;

import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.HttpHeaders;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.filelocation.ColumnMappingData;
import com.elitecore.corenetvertex.sm.filelocation.FileLocationData;
import com.elitecore.corenetvertex.sm.filelocation.FileOutputConfigurationData;
import com.elitecore.corenetvertex.sm.filemapping.FileMappingData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

/**
 * @author Ajay Pandey on 16/12/17
 */

@ParentPackage(value = "sm")
@Namespace("/sm/filelocation")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "file-location" }),
})
public class FileLocationCTRL extends RestGenericCTRL<FileLocationData> {

	private static final long serialVersionUID = -2977797594729321317L;

	private List<FileMappingData> fileMappingDataList = Collectionz.newArrayList();
	private List<FileMappingData> fileMappingList = Collectionz.newArrayList();
	private List<DatabaseData> dbDataSourceList = Collectionz.newArrayList();

	@Override
	public ACLModules getModule() {
		return ACLModules.FILELOCATION;
	}
	
	 @Override
	    public HttpHeaders create() {
	        if(LogManager.getLogger().isDebugLogLevel()){
	            LogManager.getLogger().debug(getLogModule(),"Method called create()");
	        }
	        FileLocationData fileLocationData = (FileLocationData) getModel();
	        
	        
	        setFileLocationDataToFileOutputConfiguration(fileLocationData);
	        setFileLocationDataToColumnMapping(fileLocationData);
	        return super.create();
	    }

	    @Override
	    public HttpHeaders update() {
	        if(LogManager.getLogger().isDebugLogLevel()){
	            LogManager.getLogger().debug(getLogModule(),"Method called update()");
	        }
	        
	        FileLocationData fileLocationData = (FileLocationData) getModel();
	        setFileLocationDataToFileOutputConfiguration(fileLocationData);
	        setFileLocationDataToColumnMapping(fileLocationData);
	        
	        return super.update();
	    }


	    private void setFileLocationDataToFileOutputConfiguration(FileLocationData fileLocationData) {
	        filterEmptyFileOutputConfiguration(fileLocationData.getFileOutputConfigurationData());
	        fileLocationData.getFileOutputConfigurationData().forEach(fileLocationDatas -> fileLocationDatas.setFileLocationData(fileLocationData));
	    }

	    private void filterEmptyFileOutputConfiguration(List<FileOutputConfigurationData> fileOutputConfigurationDatas) {

	        Collectionz.filter(fileOutputConfigurationDatas, fileOutputConfigurationData -> {
	            if (fileOutputConfigurationData != null) {
	                return !Strings.isNullOrBlank(fileOutputConfigurationData.getLocation());
	            } else {
	                return false;
	            }
	    });
	    }

	    private void setFileLocationDataToColumnMapping(FileLocationData fileLocationData) {
	    	filterEmptyColumnMapping(fileLocationData.getColumnMappingData());
	    	fileLocationData.getColumnMappingData().forEach(fileLocationDatas -> fileLocationDatas.setFileLocationData(fileLocationData));
	    }

	    private void filterEmptyColumnMapping(List<ColumnMappingData> columnMappingDatas) {

	        Collectionz.filter(columnMappingDatas, columnMappingData -> {
	            if (columnMappingData != null) {
	                return !Strings.isNullOrBlank(columnMappingData.getColumnName());
	            } else {
	                return false;
	            }
	    });
	    }
	    
	    public String getFileOutputConfigDataListAsJson() {
            Gson gson = GsonFactory.defaultInstance();
            FileLocationData fileLocationData = (FileLocationData) getModel();
            JsonArray modelJson = gson.toJsonTree(fileLocationData.getFileOutputConfigurationData(),new TypeToken<List<FileOutputConfigurationData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }
	    
	    public String getColumnMappingDataListAsJson() {
            Gson gson = GsonFactory.defaultInstance();
            FileLocationData fileLocationData = (FileLocationData) getModel();
        JsonArray modelJson = gson.toJsonTree(fileLocationData.getColumnMappingData(),new TypeToken<List<ColumnMappingData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }
	
	@SkipValidation
	@Override
	public void prepareValuesForSubClass() throws Exception {
		List<FileMappingData> fileMappingDatas = CRUDOperationUtil.findAll( FileMappingData.class);
		for(FileMappingData fileMappingData : fileMappingDatas){
			if(fileMappingData.getType().equals(NVSMXCommonConstants.FILE_MAPPING_TYPE)){
				fileMappingList.add(fileMappingData);
			}
		}
        setFileMappingList(fileMappingList);
        setFileMappingDataList(CRUDOperationUtil.findAll( FileMappingData.class));
		setDbDataSourceList(CRUDOperationUtil.findAll(DatabaseData.class));
	}

	@Override
	public FileLocationData createModel() {
		return new FileLocationData();
	}
	
	public List<FileMappingData> getFileMappingList() {
		return fileMappingList;
	}

	public void setFileMappingList(List<FileMappingData> fileMappingList) {
		this.fileMappingList = fileMappingList;
	}

	public List<DatabaseData> getDbDataSourceList() {
		return dbDataSourceList;
	}

	public void setDbDataSourceList(List<DatabaseData> dbDataSourceList) {
		this.dbDataSourceList = dbDataSourceList;
	}
	
	public List<FileMappingData> getFileMappingDataList() {
		return fileMappingDataList;
	}

	public void setFileMappingDataList(List<FileMappingData> fileMappingDataList) {
		this.fileMappingDataList = fileMappingDataList;
	}
}
