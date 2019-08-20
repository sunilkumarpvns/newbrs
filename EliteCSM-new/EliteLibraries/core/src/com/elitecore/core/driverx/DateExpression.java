package com.elitecore.core.driverx;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class DateExpression implements ValueExpression{

	private String identifier;
	public DateExpression(String identifier){
		this.identifier = identifier;
	}
	@Override
	public String getValue(ValueProviderExt valueProvider) {
		SimpleDateFormat sdf = new SimpleDateFormat(identifier);
		Date d = new Date();
		return sdf.format(d);
	}
	@Override
	public List<String> getValues(ValueProviderExt valueProvider) {
		return Collections.emptyList();
	}



	
}