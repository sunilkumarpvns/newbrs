package com.elitecore.exprlib.parser.expression.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionCurrentTimestamp extends AbstractStringFunctionExpression {
	
	private static final long serialVersionUID = 1L;
	private static final String NTP_KEYWORD = "NTP";
	
	private static final long msb0baseTime;
	private static final long msb1baseTime;

	static {
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(utcZone);
        calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        msb1baseTime = calendar.getTime().getTime();
        calendar.set(2036, Calendar.FEBRUARY, 7, 6, 28, 16);
        calendar.set(Calendar.MILLISECOND, 0);
        msb0baseTime = calendar.getTime().getTime();
    }
	
	public FunctionCurrentTimestamp() {
	}

	@Override
	public String getName() {
		return "currentTimestamp";
	}
	
	@Override
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}
	
	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		if(argumentList.size() > 2) {
			throw new IllegalArgumentException("Number of parameter mismatch, " + getName() + " can only have two or less arguments.");
		}
		String time = Long.toString(System.currentTimeMillis());
		if (argumentList.size() == 1) {
			String arg = argumentList.get(0).getStringValue(valueProvider); 
			if (NTP_KEYWORD.equalsIgnoreCase(arg)) {
				time = Long.toString(currentNtpTimeInSecs());
			} else {
				time = toFormat(arg, System.currentTimeMillis());
			}
		} else if (argumentList.size() == 2) {
			String arg1 = argumentList.get(0).getStringValue(valueProvider);
			String arg2 = argumentList.get(1).getStringValue(valueProvider);
			
			if (NTP_KEYWORD.equalsIgnoreCase(arg2)) {
				time = toFormat(arg1, currentNtpTimeInSecs());
			} else {
				time = toFormat(arg1, System.currentTimeMillis());
			}
		}
		
		return time;
	}

	private long currentNtpTimeInSecs() {
		long t = System.currentTimeMillis();
        boolean useBase1 = t < msb0baseTime;				    // time < Feb-2036
        long baseTime;
        if (useBase1) {
            baseTime = t - msb1baseTime;						// dates <= Feb-2036
        } else {
            // if base0 needed for dates >= Feb-2036
            baseTime = t - msb0baseTime;
        }

        return baseTime / 1000;
    }
	
	private String toFormat(String format, long timestamp) {
		return new SimpleDateFormat(format).format(new Date(timestamp));
	}
	
}
