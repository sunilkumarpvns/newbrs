package com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data;

public class CSVStripFieldMapData {
	private Long csvStripID;
	private String pcrfKey;
	private String pattern;
	private String separator;
	private Long csvDriverID;
	
	public Long getCsvStripID() {
		return csvStripID;
	}
	
	public void setCsvStripID(Long csvStripID) {
		this.csvStripID = csvStripID;
	}
	
	public String getPcrfKey() {
		return pcrfKey;
	}
	
	public void setPcrfKey(String pcrfKey) {
		this.pcrfKey = pcrfKey;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public String getSeparator() {
		return separator;
	}
	
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	public Long getCsvDriverID() {
		return csvDriverID;
	}
	
	public void setCsvDriverID(Long csvDriverID) {
		this.csvDriverID = csvDriverID;
	}
}