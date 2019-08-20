package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver.CSVLineBuilder;

public interface CSVDataBuilder<T> {

	public String getHeader();
	public CSVLineBuilder<T> getLineBuilder(TimeSource timeSource);
}
