package com.elitecore.core.driverx.cdr;


public interface CSVBuilder {
	
	public CSVLineBuilder buildLineBuilder() throws Exception;
	public CSVPathAllocator buildPathAllocator(String serverHome) throws Exception;

}
