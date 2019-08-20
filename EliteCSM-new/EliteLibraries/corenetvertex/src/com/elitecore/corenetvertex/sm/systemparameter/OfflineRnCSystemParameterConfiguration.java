package com.elitecore.corenetvertex.sm.systemparameter;

import java.util.List;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class OfflineRnCSystemParameterConfiguration  extends DefaultGroupResourceData {

	/**
	 * @author vijayrajsinh
	 */
	private static final long serialVersionUID = 1L;
	
	private List<SystemParameterData> fileSystemParameters = Collectionz.newArrayList();
    private List<SystemParameterData> ratingSystemParameters = Collectionz.newArrayList();
	
    
    public List<SystemParameterData> getFileSystemParameters() {
		return fileSystemParameters;
	}
    
	public void setFileSystemParameters(List<SystemParameterData> fileSystemParameters) {
		this.fileSystemParameters = fileSystemParameters;
	}
	
	public List<SystemParameterData> getRatingSystemParameters() {
		return ratingSystemParameters;
	}
	
	public void setRatingSystemParameters(List<SystemParameterData> ratingSystemParameters) {
		this.ratingSystemParameters = ratingSystemParameters;
	}
	
    @Override
    @JsonIgnore
    //We don't have any id in SystemParameter. It works on key value formation. Where key is alias.
    public String getId() {
        return CommonConstants.EMPTY_STRING;
    }

    @Override
    @JsonIgnore
    public String getGroupNames() {
        return super.getGroupNames();
    }

    @Override
    @JsonIgnore
    public String getStatus() {
        return super.getStatus();
    }
	
	@Override
    @Transient
	public String getResourceName() {
		return "Offline-RnC-System-Parameter-Configuration";
	}
	
	@Override
	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
        if (Collectionz.isNullOrEmpty(fileSystemParameters) == false) {
            JsonArray jsonArray = new JsonArray();
            for (SystemParameterData systemParameterData : fileSystemParameters) {
                jsonArray.add(systemParameterData.toJson());
            }
            jsonObject.add("File System Parameters ", jsonArray);
        }

        if (Collectionz.isNullOrEmpty(ratingSystemParameters) == false) {
            JsonArray jsonArray = new JsonArray();
            for (SystemParameterData systemParameterData : ratingSystemParameters) {
                jsonArray.add(systemParameterData.toJson());
            }
            jsonObject.add("Rating System Parameters ", jsonArray);
        }
        return jsonObject;
	}
}
