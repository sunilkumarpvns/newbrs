package com.elitecore.exprlib.parser.expression.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * @author milan paliwal
 */

public class FunctionDateFormat extends AbstractStringFunctionExpression {
	
	private static final long serialVersionUID = 1L;
	public FunctionDateFormat(){
	}	
	
	public int getFunctionType() {	
		return 0;
	}
	
	@Override
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()!=2)
			throw new IllegalArgumentException("Number of parameter mismatch ,Date Format function must have 2 argument ");
		
		String date=argumentList.get(0).getStringValue(valueProvider).trim();
		Calendar calendar=Calendar.getInstance();
		
		String format=argumentList.get(1).getStringValue(valueProvider).trim();
		Date formatDate;
		String returnDate="";
		SimpleDateFormat formatter=new SimpleDateFormat(format);
		formatter.setLenient(false);
		try{
			formatDate=(Date)formatter.parse(date);
			calendar.setTime(formatDate);
			
			returnDate=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
			return returnDate;
		}catch (ParseException e) {
			throw new InvalidTypeCastException("The agrument is not in proper date format ( "+format+" ) , Reason: " + e.getMessage(),e);
		}
	}

	public String getName() {
		return "dateFormat";
	}
	
}
