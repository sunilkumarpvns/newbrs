package com.elitecore.core.driverx;

import java.util.List;

public interface ValueExpression {
	
	public String getValue(ValueProviderExt valueProvider);
	public List<String> getValues(ValueProviderExt valueProvider);
	
}
