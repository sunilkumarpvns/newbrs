package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class DiameterGroupedAttributeData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long groupedAttrId;
	private Long attributeRuleId;
	private Long parameterDetailId;
	private String name;
	private String maximum;
	private String minimum;
	private String groupedAttributeType;
	private Long diameterdicId;
	private Integer vendorId;
	private Integer attributeId;
	
	
	
	
	/**
	 * @return the groupedAttrId
	 */
	public Long getGroupedAttrId() {
		return groupedAttrId;
	}
	/**
	 * @param groupedAttrId the groupedAttrId to set
	 */
	public void setGroupedAttrId(Long groupedAttrId) {
		this.groupedAttrId = groupedAttrId;
	}
	/**
	 * @return the attributeRuleId
	 */
	public Long getAttributeRuleId() {
		return attributeRuleId;
	}
	/**
	 * @param attributeRuleId the attributeRuleId to set
	 */
	public void setAttributeRuleId(Long attributeRuleId) {
		this.attributeRuleId = attributeRuleId;
	}
	/**
	 * @return the parameterDetailId
	 */
	public Long getParameterDetailId() {
		return parameterDetailId;
	}
	/**
	 * @param parameterDetailId the parameterDetailId to set
	 */
	public void setParameterDetailId(Long parameterDetailId) {
		this.parameterDetailId = parameterDetailId;
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
	 * @return the maximum
	 */
	public String getMaximum() {
		return maximum;
	}
	/**
	 * @param maximum the maximum to set
	 */
	public void setMaximum(String maximum) {
		this.maximum = maximum;
	}
	/**
	 * @return the minimum
	 */
	public String getMinimum() {
		return minimum;
	}
	/**
	 * @param minimum the minimum to set
	 */
	public void setMinimum(String minimum) {
		this.minimum = minimum;
	}
	/**
	 * @return the groupedAttributeType
	 */
	public String getGroupedAttributeType() {
		return groupedAttributeType;
	}
	/**
	 * @param groupedAttributeType the groupedAttributeType to set
	 */
	public void setGroupedAttributeType(String groupedAttributeType) {
		this.groupedAttributeType = groupedAttributeType;
	}
	
	
	
   /**
	 * @return the diameterdicId
	 */
	public Long getDiameterdicId() {
		return diameterdicId;
	}
	/**
	 * @param diameterdicId the diameterdicId to set
	 */
	public void setDiameterdicId(Long diameterdicId) {
		this.diameterdicId = diameterdicId;
	}
	
	public Integer getVendorId() {
		return vendorId;
	}
	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}
	public Integer getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(Integer attributeId) {
		this.attributeId = attributeId;
	}
public String toString(){
	   
	    StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------Group Attribute Data --------------");
		writer.println("attributeRuleId 	             :"+attributeRuleId);
		writer.println("parameterDetailId                :"+parameterDetailId);
		writer.println("vendorId                         :"+vendorId);
		writer.println("attributeId                      :"+attributeId);
		writer.println("name                   			 :"+name);
		writer.println("maximum                        	 :"+maximum);         
		writer.println("minimum                          :"+minimum);
		writer.println("groupedAttributeType	         :"+groupedAttributeType);   
		writer.println("----------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	   
   }


}
