package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data;

import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;

public interface IRadiusPolicyParamDetailData {
	public int getDictionaryNo();
	public void setDictionaryNo(int dictionaryNo);
	public String getOperatorId();
	public void setOperatorId(String operatorId);
	public String getName();
	public void setName(String name);
	public String getParameterUsage();
	public void setParameterUsage(String parameterUsage);
	public String getParameterUsageValue();
	public void setParameterUsageValue(String parameterUsageValue);
	public String getDictionaryParameterName();
	public void setDictionaryParameterName(String dictionaryParameterName);
	public String getValue();
	public void setValue(String value);
	public Long getRadiusPolicyId();
	public void setRadiusPolicyId(Long radiusPolicyId);
	public long getRadiusPolicyParamDetailId();
	public void setRadiusPolicyParamDetailId(long radiusPolicyParamDetailId);

	public Integer getVendorParameterId();
	public void setVendorParameterId(Integer vendorParameterId);
	public String getDataTypeName();
	public void setDataTypeName(String dataTypeName);
	public String getDisplayValue();
	public void setDisplayValue(String displayValue);
	public String getVendorParamOverridden();
	public void setVendorParamOverridden(String vendorParamOverridden);
	public long getVendorId();
	public void setVendorId(long vendorId);
	public String getReplyItemType();
	public void setReplyItemType(String replyItemType);
	public long getRadiusRFCDictionaryId();
	public void setRadiusRFCDictionaryId(long radiusRFCDictionaryId);
	public DictionaryData getRadiusDictionary();
	public void setRadiusDictionary(DictionaryData radiusDictionary);
	public OperatorData getRadiusOperator();
	public void setRadiusOperator(OperatorData radiusOperator);
	public String getStatus();
	public void setStatus(String status);
	

	
}
