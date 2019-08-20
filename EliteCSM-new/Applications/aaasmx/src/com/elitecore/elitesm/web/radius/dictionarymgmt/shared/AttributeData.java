package com.elitecore.elitesm.web.radius.dictionarymgmt.shared;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AttributeData implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String          dictionaryParameterDetailId;
	private String          parentDetailId;
	private String          dictionaryId;
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
    private int           encryptStandard;
    private String lengthFormat;
    private String paddingType;
	//private IOperatorData operatorData;
	private DataTypeData dataType;

	private Collection<AttributeData> childAttributeList=new ArrayList<AttributeData>();

	public AttributeData(){
		
	}

	public Collection<AttributeData> getChildAttributeList() {
		
		return childAttributeList;
	}


	public void setChildAttributeList(Collection<AttributeData> childAttributeList) {
		this.childAttributeList = childAttributeList;
	}



	public String getDictionaryParameterDetailId() {
		return dictionaryParameterDetailId;
	}


	public void setDictionaryParameterDetailId(String dictionaryParameterDetailId) {
		this.dictionaryParameterDetailId = dictionaryParameterDetailId;
	}


	public String getParentDetailId() {
		return parentDetailId;
	}


	public void setParentDetailId(String parentDetailId) {
		this.parentDetailId = parentDetailId;
	}


	public String getDictionaryId() {
		return dictionaryId;
	}


	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
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


	public Integer getVendorParameterId() {
		return vendorParameterId;
	}

	public void setVendorParameterId(Integer vendorParameterId) {
		this.vendorParameterId = vendorParameterId;
	}

	public String getPredefinedValues() {
		return predefinedValues;
	}


	public void setPredefinedValues(String predefinedValues) {
		this.predefinedValues = predefinedValues;
	}


	public long getRadiusRFCDictionaryParameterId() {
		return radiusRFCDictionaryParameterId;
	}


	public void setRadiusRFCDictionaryParameterId(
			long radiusRFCDictionaryParameterId) {
		this.radiusRFCDictionaryParameterId = radiusRFCDictionaryParameterId;
	}


	public String getDataTypeId() {
		return dataTypeId;
	}


	public void setDataTypeId(String dataTypeId) {
		this.dataTypeId = dataTypeId;
	}


	public String getVendorParameterOveridden() {
		return vendorParameterOveridden;
	}


	public void setVendorParameterOveridden(String vendorParameterOveridden) {
		this.vendorParameterOveridden = vendorParameterOveridden;
	}


	public String getOperatorId() {
		return operatorId;
	}


	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}


	public long getVendorId() {
		return vendorId;
	}


	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}


	public long getDictionaryNumber() {
		return dictionaryNumber;
	}


	public void setDictionaryNumber(long dictionaryNumber) {
		this.dictionaryNumber = dictionaryNumber;
	}


	public String getNetworkFilterSupport() {
		return networkFilterSupport;
	}


	public void setNetworkFilterSupport(String networkFilterSupport) {
		this.networkFilterSupport = networkFilterSupport;
	}


	public String getUsageType() {
		return usageType;
	}


	public void setUsageType(String usageType) {
		this.usageType = usageType;
	}


	public String getAvPair() {
		return avPair;
	}


	public void setAvPair(String avPair) {
		this.avPair = avPair;
	}


	public String getHasTag() {
		return hasTag;
	}


	public void setHasTag(String hasTag) {
		this.hasTag = hasTag;
	}

	public String getIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(String ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public int getEncryptStandard() {
		return encryptStandard;
	}

	public void setEncryptStandard(int encryptStandard) {
		this.encryptStandard = encryptStandard;
	}
	public String getLengthFormat() {
		return lengthFormat;
	}

	public void setLengthFormat(String lengthFormat) {
		this.lengthFormat = lengthFormat;
	}

	public String getPaddingType() {
		return paddingType;
	}

	public void setPaddingType(String paddingType) {
		this.paddingType = paddingType;
	}

	public DataTypeData getDataType() {
		return dataType;
	}

	public void setDataType(DataTypeData dataType) {
		this.dataType = dataType;
		if(dataType!=null){
			this.dataTypeId=dataType.getDataTypeId();
		}
	}
	public String toString(){
		return name;
	}
}
