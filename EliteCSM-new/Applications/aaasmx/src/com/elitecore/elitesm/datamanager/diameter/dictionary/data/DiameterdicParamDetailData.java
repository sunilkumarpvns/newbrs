/**
 * 
 */
package com.elitecore.elitesm.datamanager.diameter.dictionary.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpRule;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDataTypeData;

/**
 * @author pratik.chauhan
 *
 */
public class DiameterdicParamDetailData extends BaseData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String diameterdciParamDetailId;
	private String dictionaryId;
	private String alias;
	private String name;
	private Integer vendorParameterId;
	private String predefinedValues;
	
	private String dataTypeId;
	private String vendorParameterOveridden;
	private Long dictionaryNumber;
	private String networkFilterSupport;
	private Long vendorId;
	private String description;
	private String encryption;
	private String mandatory;
	private String strProtected;
	private String attributeId;
	private IDataTypeData dataType;
	private Map<String,AvpRule> fixedGroupedAttribute= new HashMap<String, AvpRule>();
	private Map<String,AvpRule> requiredGroupedAttribute= new HashMap<String, AvpRule>();
	private Map<String,AvpRule> optionalGroupedAttribute= new HashMap<String, AvpRule>();
	
	/**
	 * @return the diameterdciParamDetailId
	 */
	public String getDiameterdciParamDetailId() {
		return diameterdciParamDetailId;
	}
	/**
	 * @param diameterdciParamDetailId the diameterdciParamDetailId to set
	 */
	public void setDiameterdciParamDetailId(String diameterdciParamDetailId) {
		this.diameterdciParamDetailId = diameterdciParamDetailId;
	}
	/**
	 * @return the dictionaryId
	 */
	public String getDictionaryId() {
		return dictionaryId;
	}
	/**
	 * @param dictionaryId the dictionaryId to set
	 */
	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}
	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}
	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the vendorParameterId
	 */
	public Integer getVendorParameterId() {
		return vendorParameterId;
	}
	/**
	 * @param vendorParameterId the vendorParameterId to set
	 */
	public void setVendorParameterId(Integer vendorParameterId) {
		this.vendorParameterId = vendorParameterId;
	}
	/**
	 * @return the predefinedValues
	 */
	public String getPredefinedValues() {
		return predefinedValues;
	}
	/**
	 * @param predefinedValues the predefinedValues to set
	 */
	public void setPredefinedValues(String predefinedValues) {
		this.predefinedValues = predefinedValues;
	}
	
	/**
	 * @return the dataTypeId
	 */
	public String getDataTypeId() {
		return dataTypeId;
	}
	/**
	 * @param dataTypeId the dataTypeId to set
	 */
	public void setDataTypeId(String dataTypeId) {
		this.dataTypeId = dataTypeId;
	}
	/**
	 * @return the vendorParameterOveridden
	 */
	public String getVendorParameterOveridden() {
		return vendorParameterOveridden;
	}
	/**
	 * @param vendorParameterOveridden the vendorParameterOveridden to set
	 */
	public void setVendorParameterOveridden(String vendorParameterOveridden) {
		this.vendorParameterOveridden = vendorParameterOveridden;
	}
	/**
	 * @return the dictionaryNumber
	 */
	public Long getDictionaryNumber() {
		return dictionaryNumber;
	}
	/**
	 * @param dictionaryNumber the dictionaryNumber to set
	 */
	public void setDictionaryNumber(Long dictionaryNumber) {
		this.dictionaryNumber = dictionaryNumber;
	}
	/**
	 * @return the networkFilterSupport
	 */
	public String getNetworkFilterSupport() {
		return networkFilterSupport;
	}
	/**
	 * @param networkFilterSupport the networkFilterSupport to set
	 */
	public void setNetworkFilterSupport(String networkFilterSupport) {
		this.networkFilterSupport = networkFilterSupport;
	}
	/**
	 * @return the vendorId
	 */
	public Long getVendorId() {
		return vendorId;
	}
	/**
	 * @param vendorId the vendorId to set
	 */
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the encryption
	 */
	public String getEncryption() {
		return encryption;
	}
	/**
	 * @param encryption the encryption to set
	 */
	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}
	/**
	 * @return the mandatory
	 */
	public String getMandatory() {
		return mandatory;
	}
	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}
	/**
	 * @return the strProtected
	 */
	public String getStrProtected() {
		return strProtected;
	}
	/**
	 * @param strProtected the strProtected to set
	 */
	public void setStrProtected(String strProtected) {
		this.strProtected = strProtected;
	}
	/**
	 * @return the fixedGroupedAttribute
	 */
	public Map<String, AvpRule> getFixedGroupedAttribute() {
		return fixedGroupedAttribute;
	}
	/**
	 * @param fixedGroupedAttribute the fixedGroupedAttribute to set
	 */
	public void setFixedGroupedAttribute(Map<String, AvpRule> fixedGroupedAttribute) {
		this.fixedGroupedAttribute = fixedGroupedAttribute;
	}
	/**
	 * @return the requiredGroupedAttribute
	 */
	public Map<String, AvpRule> getRequiredGroupedAttribute() {
		return requiredGroupedAttribute;
	}
	/**
	 * @param requiredGroupedAttribute the requiredGroupedAttribute to set
	 */
	public void setRequiredGroupedAttribute(
			Map<String, AvpRule> requiredGroupedAttribute) {
		this.requiredGroupedAttribute = requiredGroupedAttribute;
	}
	/**
	 * @return the optionalGroupedAttribute
	 */
	public Map<String, AvpRule> getOptionalGroupedAttribute() {
		return optionalGroupedAttribute;
	}
	/**
	 * @param optionalGroupedAttribute the optionalGroupedAttribute to set
	 */
	public void setOptionalGroupedAttribute(
			Map<String, AvpRule> optionalGroupedAttribute) {
		this.optionalGroupedAttribute = optionalGroupedAttribute;
	}
	
	
	/**
	 * @return the dataType
	 */
	public IDataTypeData getDataType() {
		return dataType;
	}
	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(IDataTypeData dataType) {
		this.dataType = dataType;
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
	
	
	public String toString(){
		
		StringWriter out = new StringWriter();
		PrintWriter writer=new PrintWriter(out);
        
		writer.println();
		writer.println("------------ Diameter Dictionary Data --------------");
		writer.println("Attribute Name       :"+name);
		writer.println("----------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
		
		
	}


}
