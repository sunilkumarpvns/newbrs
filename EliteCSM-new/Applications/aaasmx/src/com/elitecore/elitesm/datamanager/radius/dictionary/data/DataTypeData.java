package com.elitecore.elitesm.datamanager.radius.dictionary.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class DataTypeData extends BaseData implements IDataTypeData {

    private String dataTypeId;
    private String alias;
    private String name;    
    private String description;
    private String systemGenerated;    
    
    private String javaScriptRegex;
    private String javaRegex;
    private String errorMessage;
    private String dictionaryType;
    
    public String getDataTypeId() {
        return dataTypeId;
    }
    public void setDataTypeId(String dataTypeId) {
        this.dataTypeId = dataTypeId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSystemGenerated() {
        return systemGenerated;
    }
    public void setSystemGenerated(String systemGenerated) {
        this.systemGenerated = systemGenerated;
    }
    

    public String getJavaScriptRegex() {
        return javaScriptRegex;
    }
    public void setJavaScriptRegex(String javaScriptRegex) {
        this.javaScriptRegex = javaScriptRegex;
    }
    public String getJavaRegex() {
        return javaRegex;
    }
    public void setJavaRegex(String javaRegex) {
        this.javaRegex = javaRegex;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
	/**
	 * @return the dictionaryType
	 */
	public String getDictionaryType() {
		return dictionaryType;
	}
	/**
	 * @param dictionaryType the dictionaryType to set
	 */
	public void setDictionaryType(String dictionaryType) {
		this.dictionaryType = dictionaryType;
	}
    

}
