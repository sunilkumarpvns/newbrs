package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.data.IDataTypeData;
import com.elitecore.netvertexsm.util.constants.DictionaryConstant;

public class RadiusDictionaryParamDetailData implements IRadiusDictionaryParamDetailData, Serializable, Comparable {
    
    private long          dictionaryParameterDetailId;
    private Long          parentDetailId;
    private long          dictionaryId;
    private String        alias;
    private String        name;
    private Integer       vendorParameterId;
    private String        predefinedValues;
    private long          radiusRFCDictionaryParameterId;
    private String        dataTypeId;
    private String        vendorParameterOveridden;
    private String        operatorId;
    private long          vendorId;
    private long          dictionaryNumber;
    private String        networkFilterSupport;
    private String        usageType;
    private String        avPair;
    private String		  hasTag;
    private String 		  ignoreCase;
    private Integer       encryptStandard;
    private IDataTypeData dataType;
    private String attributeId;
    
    
    private List<RadiusDictionaryParamDetailData> nestedParameterDetailList;
    private Set<RadiusDictionaryParamDetailData> nestedParameterDetailSet;
    
	public Long getParentDetailId() {
		return parentDetailId;
	}

	public void setParentDetailId(Long parentDetailId) {
		this.parentDetailId = parentDetailId;
	}

	public String getAlias( ) {
        return alias;
    }
    
    public void setAlias( String alias ) {
        this.alias = alias;
    }
    
    public String getAvPair( ) {
        return avPair;
    }
    
    public void setAvPair( String avPair ) {
        this.avPair = avPair;
    }
    
    public String getDataTypeId( ) {
        return dataTypeId;
    }
    
    public void setDataTypeId( String dataTypeId ) {
        this.dataTypeId = dataTypeId;
    }
    
    public long getDictionaryId( ) {
        return dictionaryId;
    }
    
    public void setDictionaryId( long dictionaryId ) {
        this.dictionaryId = dictionaryId;
    }
    
    public long getDictionaryNumber( ) {
        return dictionaryNumber;
    }
    
    public void setDictionaryNumber( long dictionaryNumber ) {
        this.dictionaryNumber = dictionaryNumber;
    }
    
    public long getDictionaryParameterDetailId( ) {
        return dictionaryParameterDetailId;
    }
    
    public void setDictionaryParameterDetailId( long dictionaryParameterDetailId ) {
        this.dictionaryParameterDetailId = dictionaryParameterDetailId;
    }
    
    public String getName( ) {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getNetworkFilterSupport( ) {
        return networkFilterSupport;
    }
    
    public void setNetworkFilterSupport( String networkFilterSupport ) {
        this.networkFilterSupport = networkFilterSupport;
    }
    
    public String getOperatorId( ) {
        return operatorId;
    }
    
    public void setOperatorId( String operatorId ) {
        this.operatorId = operatorId;
    }
    
    public long getRadiusRFCDictionaryParameterId( ) {
        return radiusRFCDictionaryParameterId;
    }
    
    public void setRadiusRFCDictionaryParameterId( long radiusRFCDictionaryParameterId ) {
        this.radiusRFCDictionaryParameterId = radiusRFCDictionaryParameterId;
    }
    
    public String getUsageType( ) {
        return usageType;
    }
    
    public void setUsageType( String usageType ) {
        this.usageType = usageType;
    }
    
    public long getVendorId( ) {
        return vendorId;
    }
    
    public void setVendorId( long vendorId ) {
        this.vendorId = vendorId;
    }
    
    
    
    public Integer getVendorParameterId() {
		return vendorParameterId;
	}

	public void setVendorParameterId(Integer vendorParameterId) {
		this.vendorParameterId = vendorParameterId;
	}

	public String getVendorParameterOveridden( ) {
        return vendorParameterOveridden;
    }
    
    public void setVendorParameterOveridden( String vendorParameterOveridden ) {
        this.vendorParameterOveridden = vendorParameterOveridden;
    }
    
    public void setDataType( IDataTypeData dataType ) {
        this.dataType = dataType;
    }
    
    public IDataTypeData getDataType( ) {
        return dataType;
    }
    
    public int compareTo( Object iDctionaryParameterDetilaData ) {
        
        String paramName = ((RadiusDictionaryParamDetailData) iDctionaryParameterDetilaData).getName();
    	return name.compareTo(paramName);
    }
    
    public String getPredefinedValues( ) {
        return predefinedValues;
    }
    
    public void setPredefinedValues( String predefinedValues ) {
        this.predefinedValues = predefinedValues;
    }
    
    public boolean hasPreDefinedValue( ) {
        
        if (predefinedValues != null && !predefinedValues.equalsIgnoreCase("")) { return true; }
        return false;
    }
    
    public Map getPreDefinedValueMap() {
        Map preDefinedValueMap = new HashMap();
        
        if (!hasPreDefinedValue()) {
            return preDefinedValueMap;
        } else {
            StringTokenizer preDefinedValueToken = new StringTokenizer(predefinedValues, DictionaryConstant.PREDEFINEDVALUES_SEPARATOR);
            while (preDefinedValueToken.hasMoreElements()) {
                StringTokenizer preDefinedKeyValueToken = new StringTokenizer(preDefinedValueToken.nextElement().toString(), DictionaryConstant.PREDEFINEDVALUES_KEYVALUE_SEPARATOR);
                if (preDefinedKeyValueToken.countTokens() == 2) {
                    preDefinedValueMap.put(preDefinedKeyValueToken.nextElement(), preDefinedKeyValueToken.nextElement());
                }
            }
         }
        return preDefinedValueMap;
    }

	public String getHasTag() {
		
		return this.hasTag;
	}

	public void setHasTag(String hasTag) {
		// TODO Auto-generated method stub
		this.hasTag = hasTag;
	}
    
	public String getIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(String ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public Integer getEncryptStandard() {
		return encryptStandard;
	}

	public void setEncryptStandard(Integer encryptStandard) {
		this.encryptStandard = encryptStandard;
	}

	/**
	 * @return the attributeId
	 */
	public String getAttributeId() {
		return attributeId;
	}

	/**
	 * @param attributeId the attributeId to set
	 */
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public List<RadiusDictionaryParamDetailData> getNestedParameterDetailList() {
		return nestedParameterDetailList;
	}

	public void setNestedParameterDetailList(
			List<RadiusDictionaryParamDetailData> nestedParameterDetailList) {
		this.nestedParameterDetailList = nestedParameterDetailList;
	}

	public Set<RadiusDictionaryParamDetailData> getNestedParameterDetailSet() {
		return nestedParameterDetailSet;
	}

	public void setNestedParameterDetailSet(Set<RadiusDictionaryParamDetailData> nestedParameterDetailSet) {
		this.nestedParameterDetailSet = nestedParameterDetailSet;
	}
	
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ DictionaryParameterDetailData --------------");
		writer.println("dictionaryParameterDetailId 	              :"+dictionaryParameterDetailId);
		writer.println("parentDetailId                                :"+parentDetailId);           
		writer.println("dictionaryId                                  :"+dictionaryId);
		writer.println("alias                       	`			  :"+alias);         
		writer.println("name                   						  :"+name);
		writer.println("vendorParameterId	           		          :"+vendorParameterId);   
		writer.println("predefinedValues                              :"+predefinedValues);
		writer.println("radiusRFCDictionaryParameterId                :"+radiusRFCDictionaryParameterId);
		writer.println("dataTypeId   	                              :"+dataTypeId);          
		writer.println("vendorParameterOveridden			          :"+vendorParameterOveridden);	
		writer.println("operatorId			                          :"+operatorId);	
		writer.println("vendorId		                              :"+vendorId);	
		writer.println("dictionaryNumber			                  :"+dictionaryNumber);
		writer.println("networkFilterSupport	                      :"+networkFilterSupport);
		writer.println("usageType	  					              :"+usageType);
		writer.println("avPair	                                      :"+avPair);
		writer.println("hasTag	 			                          :"+hasTag);
		writer.println("dataType                                      :"+dataType);
		writer.println("attributeId                                   : "+attributeId);
		writer.println("----------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
		
		
	}
    
	
}
