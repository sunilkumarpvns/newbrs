package com.elitecore.exprlib.parser.expression.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionGetDateDifference extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;


	public int getFunctionType() {
		return 0;
	}


	public String getName() {
		return "getDateDifference";
	}

	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		if(argumentList.size()==0 || argumentList.size() > 2)
			throw new IllegalArgumentException("Number of parameter mismatch ,Datedifference function must has 1 or 2 argument ");
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd");
		formatter.setLenient(false);
		Date date1,date2;
		try {
			date1 = (Date)formatter.parse(argumentList.get(0).getStringValue(valueProvider));
			if(argumentList.size()>1)
				date2= (Date)formatter.parse(argumentList.get(1).getStringValue(valueProvider));
			else
				date2= new Date();

			long difference=Math.abs(date1.getTime()-date2.getTime());
			int days=(int)(difference / (24 * 60 * 60 * 1000));
			return days;

		} catch (ParseException e) {
			throw new IllegalArgumentException("The agrument is not in proper date format ( yyyy/MM/dd ) , Reason: " + e.getMessage(),e);
		}
	}

	
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException {
		return String.valueOf(getLongValue(valueProvider));
	}

}
