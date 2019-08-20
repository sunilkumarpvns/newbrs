package com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data;

public class CSVFieldMapData {
	
	private Long fieldMapId;
	private String header;
	private String pcrfKey;
	private Long csvDriverId;
	private Integer orderNumber;

	public Long getFieldMapId() {
		return fieldMapId;
	}
	public void setFieldMapId(Long fieldMapId) {
		this.fieldMapId = fieldMapId;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getPcrfKey() {
		return pcrfKey;
	}
	public void setPcrfKey(String pcrfKey) {
		this.pcrfKey = pcrfKey;
	}
	public Long getCsvDriverId() {
		return csvDriverId;
	}
	public void setCsvDriverId(Long csvDriverId) {
		this.csvDriverId = csvDriverId;
	}
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}


}
