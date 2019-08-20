package com.elitecore.elitesm.datamanager.radius.dictionary.data;

public interface IDataTypeData {

    public String getDataTypeId() ;
    public void setDataTypeId(String dataTypeId) ;
    
    public String getAlias() ;
    public void setAlias(String alias) ;

    public String getName() ;
    public void setName(String name) ;

    public String getDescription() ;
    public void setDescription(String description) ;

    public String getSystemGenerated() ;
    public void setSystemGenerated(String systemGenerated) ;

    public String getJavaScriptRegex() ;
    public void setJavaScriptRegex(String javaScriptRegex) ;

    public String getJavaRegex() ;
    public void setJavaRegex(String javaRegex) ;

    public String getErrorMessage() ;
    public void setErrorMessage(String errorMessage) ;
    
    public String getDictionaryType();
	public void setDictionaryType(String dictionaryType);
}
