package com.elitecore.aaa.core.plugins.transactionlogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FileLocationParser {
	
	private static final String MODULE = "FILE-LOCATN-PRSR";
	
	private String locationExpression;
	private String moduleName;
	private List<Formatter> formatters;
	private List<SimpleDateFormat> dateFormats;
	private boolean hasDate = false;
	private TimeSource timesource;

	public FileLocationParser(String locationExpression, String moduleName) {
		this(locationExpression, moduleName, TimeSource.systemTimeSource());
	}
	
	@VisibleForTesting
	 FileLocationParser(String locationExpression, String moduleName, TimeSource timesource) {
			this.locationExpression = locationExpression;
			this.moduleName = moduleName;
			this.timesource = timesource;
			this.formatters = new ArrayList<Formatter>();
			this.dateFormats = new ArrayList<SimpleDateFormat>();
	}

	public void init() throws InvalidExpressionException {
		
		if (locationExpression == null || locationExpression.trim().length() < 1 ) {
			LogManager.getLogger().warn(MODULE, "File location not provided for:" + moduleName);
			return;
		}
		
		// If file location does not contain "{" and "}" that means it is a location without timestamp and attribute ID
		if ((locationExpression.contains("{") == false) || (locationExpression.contains("}") == false)) {
			this.formatters.add(new StringFormatter(locationExpression));
			return;
		}
		
		char[] locEx = locationExpression.toCharArray();
		String tmp = "";
		String dateFormateTmp = "";
		boolean isLiteral = false;
		boolean isDate = false;
		for (int i=0; i<locEx.length; i++) {
			switch(locEx[i]) {
			
			case '{' :
				if (locEx[i+1] == '$') {
					isLiteral = true;
					Formatter stringFormatter = new StringFormatter(tmp);
					stringFormatter.init();
					formatters.add(stringFormatter);
					tmp = "";
				} else {
					hasDate = true;
					isDate = true;
					tmp += locEx[i];
				}
				break;

			case '}':
				Formatter formatter = null;
				if (isLiteral) {
					isLiteral = false;
					formatter = new ValueProviderFormatter(tmp);
					formatters.add(formatter);
					tmp = "";
				} else {
					isDate = false;
					tmp += locEx[i];
					this.dateFormats.add(new SimpleDateFormat(dateFormateTmp));
					dateFormateTmp = "";
				}
				break;
				
			case '\\':
				tmp += locEx[++i];
				if (isDate) {
					dateFormateTmp += locEx[++i];
				}
				break;
				
			default :
				tmp += locEx[i];
				if (isDate) {
					dateFormateTmp += locEx[i];
				}
				break;
			}
		}
		Formatter stringFormatter = new StringFormatter(tmp);
		stringFormatter.init();
		formatters.add(stringFormatter);
	}
	
	public String getKey(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		String key = "";
		for (Formatter formatter : this.formatters) {
			key += formatter.process(valueProvider);
		}
		return key;
	}
	
	public String getLocation(String key) {
		if (hasDate == false) {
			return key;
		}
		StringBuilder stringBuilder = new StringBuilder(key);
		int index = 0;
		int start = stringBuilder.indexOf("{");
		int end = stringBuilder.indexOf("}");
		while (start > -1 && end > -1) {
			stringBuilder.replace(start, end+1, this.dateFormats.get(index).format(new Date(timesource.currentTimeInMillis())));
			start = stringBuilder.indexOf("{", end);
			end = stringBuilder.indexOf("}", start);
			index++;
		}
		return stringBuilder.toString();
	}
}
