package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.data.IDataTypeData;



public interface IRadiusDictionaryParamDetailData {

    public String getAlias() ;
    public void setAlias(String alias) ;
    public String getAvPair() ;
    public void setAvPair(String avPair) ;
    public String getDataTypeId() ;
    public void setDataTypeId(String dataTypeId) ;
    public long getDictionaryId() ;
    public void setDictionaryId(long dictionaryId) ;
    public long getDictionaryNumber() ;
    public void setDictionaryNumber(long dictionaryNumber) ;
    public long getDictionaryParameterDetailId() ;
    public void setDictionaryParameterDetailId(long dictionaryParameterDetailId) ;
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
    public void setDataType(IDataTypeData operatorData);
    public IDataTypeData getDataType() ;
    public Map getPreDefinedValueMap();
    public boolean hasPreDefinedValue();
    public String getHasTag();
    public void setHasTag(String hasTag);
    public Long getParentDetailId();
	public void setParentDetailId(Long parentDetailId);
	public String getIgnoreCase();
	public void setIgnoreCase(String ignoreCase);
	public Integer getEncryptStandard();
	public void setEncryptStandard(Integer encryptStandard);
	public String getAttributeId();
	public void setAttributeId(String attributeId);
    
	public List<RadiusDictionaryParamDetailData> getNestedParameterDetailList();
	public void setNestedParameterDetailList(List<RadiusDictionaryParamDetailData> nestedParameterDetailList);
	
	public Set<RadiusDictionaryParamDetailData> getNestedParameterDetailSet();
	public void setNestedParameterDetailSet(Set<RadiusDictionaryParamDetailData> nestedParameterDetailSet);

}
