package com.elitecore.exprlib.parser.expression.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionIsTimeBetween extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	public int getFunctionType() {
		return 0;
	}


	public String getName() {
		return "isTimeBetween";
	}

	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException {
		throw new InvalidTypeCastException("cannot cast a string to integer");
	}

	
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException {
		if(argumentList.size()!=2)
			throw new IllegalArgumentException("Number of parameter mismatch ,isTimeBetween function must has 2 argument ");

		SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd");
		Date min,max;
		try{	
			min = (Date)formatter.parse(argumentList.get(0).getName());
			max = (Date)formatter.parse(argumentList.get(1).getName());
			Date current=new Date();
			return String.valueOf(min.getTime() <= current.getTime() && current.getTime() <= max.getTime());
		}catch (ParseException e) {
			throw new InvalidTypeCastException("The agrument is not in proper date format ( yyyy/MM/dd'T'HH:mm:ss ) , Reason: " + e.getMessage(),e);
		}
	}

}
