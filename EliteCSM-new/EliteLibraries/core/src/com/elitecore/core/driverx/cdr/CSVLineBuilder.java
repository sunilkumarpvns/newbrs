package com.elitecore.core.driverx.cdr;

import java.util.List;

import com.elitecore.core.driverx.ValueProviderExt;

public interface CSVLineBuilder {
	
	public List<String> getCSVRecords(ValueProviderExt request);
	
}
