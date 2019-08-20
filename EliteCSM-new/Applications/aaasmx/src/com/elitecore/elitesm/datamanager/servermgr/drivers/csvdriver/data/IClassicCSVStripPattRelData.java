package com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data;

public interface IClassicCSVStripPattRelData {
	
	public Long getId();
	public String getAttributeid() ;
	public String getStrip() ;
	public String getPattern();
	public String getSeparator();
	public long getClassicCsvId();
}
