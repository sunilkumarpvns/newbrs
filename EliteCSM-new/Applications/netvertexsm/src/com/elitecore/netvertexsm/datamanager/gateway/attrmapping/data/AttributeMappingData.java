package com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data;

public class AttributeMappingData {
	private long attributeMapId;
	private String attribute;
	private String policyKey;
	private String defaultValue;
	private String valueMapping;
	private long packetMapId;
	private Integer orderNumber;
		
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	public long getAttributeMapId() {
		return attributeMapId;
	}
	public void setAttributeMapId(long attributeMapId) {
		this.attributeMapId = attributeMapId;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getPolicyKey() {
		return policyKey;
	}
	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	public long getPacketMapId() {
		return packetMapId;
	}
	public void setPacketMapId(long packetMapId) {
		this.packetMapId = packetMapId;
	}
}
