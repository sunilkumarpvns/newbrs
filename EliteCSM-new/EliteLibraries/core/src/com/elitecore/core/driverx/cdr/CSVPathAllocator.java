package com.elitecore.core.driverx.cdr;

import com.elitecore.core.driverx.ValueProviderExt;


public interface CSVPathAllocator{
	
	public String getPath(ValueProviderExt valueProvider);

	public String getBasePath();
}
