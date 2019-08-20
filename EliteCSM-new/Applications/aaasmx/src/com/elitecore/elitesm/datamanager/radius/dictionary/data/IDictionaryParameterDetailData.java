package com.elitecore.elitesm.datamanager.radius.dictionary.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IOperatorData;

public interface IDictionaryParameterDetailData {

    public String getAlias() ;
    public void setAlias(String alias) ;
    public String getAvPair() ;
    public void setAvPair(String avPair) ;
    public String getDataTypeId() ;
    public void setDataTypeId(String dataTypeId) ;
    public String getDictionaryId() ;
    public void setDictionaryId(String dictionaryId) ;
    public long getDictionaryNumber() ;
    public void setDictionaryNumber(long dictionaryNumber) ;
    public String getDictionaryParameterDetailId() ;
    public void setDictionaryParameterDetailId(String dictionaryParameterDetailId) ;
    public String getName() ;
    public void setName(String name) ;
    public String getNetworkFilterSupport() ;
    public void setNetworkFilterSupport(String networkFilterSupport) ;
    public String getOperatorId() ;
    public void setOperatorId(String operatorId) ;
    public String getPredefinedValues() ;
    public void setPredefinedValues(String predefinedValues) ;
    public long getRadiusRFCDictionaryParameterId() ;
    public void setRadiusRFCDictionaryParameterId(long id);
    public String getUsageType() ;
    public void setUsageType(String usageType) ;
    public long getVendorId() ;
    public void setVendorId(long vendorId) ;
    public Integer getVendorParameterId();
	public void setVendorParameterId(Integer vendorParameterId);
	public String getVendorParameterOveridden() ;
    public void setVendorParameterOveridden(String vendorParameterOveridden) ;
    public void setDictionaryOperator(IOperatorData operatorData);
    public IOperatorData getDictionaryOperator() ;
    public void setDataType(IDataTypeData operatorData);
    public IDataTypeData getDataType() ;
    public Map getPreDefinedValueMap();
    public boolean hasPreDefinedValue();
    public String getHasTag();
    public void setHasTag(String hasTag);
    public String getIgnoreCase();
	public void setIgnoreCase(String ignoreCase);
	public Integer getEncryptStandard();
	public void setEncryptStandard(Integer encryptStandard);
    public String getParentDetailId();
	public void setParentDetailId(String parentDetailId);
	public String getLengthFormat();
	public void setLengthFormat(String lengthFormat);
	public String getPaddingType();
	public void setPaddingType(String paddingType);
	
	public String getAttributeId();
	public void setAttributeId(String attributeId);
    
	public List<DictionaryParameterDetailData> getNestedParameterDetailList();
	public void setNestedParameterDetailList(List<DictionaryParameterDetailData> nestedParameterDetailList);
	
	public Set<DictionaryParameterDetailData> getNestedParameterDetailSet();
	public void setNestedParameterDetailSet(Set<DictionaryParameterDetailData> nestedParameterDetailSet);

}
