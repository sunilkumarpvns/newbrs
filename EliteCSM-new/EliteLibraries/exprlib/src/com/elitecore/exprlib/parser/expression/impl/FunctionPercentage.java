package com.elitecore.exprlib.parser.expression.impl;


import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;


/**
 * The FunctionPercentage class finds the percentage of the elements passed as arguments. The first argument will be the numerator and the second argument will be the denominator.
 * The percentage will be calculated on the based of a formula (numerator*100)/denominator.   
 * @author Ishani Bhatt
 */

public class FunctionPercentage extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String PERCENTAGE="percentage";
	
	/**
	 * 
	 * @author Ishani Bhatt
	 * @param Value provider to get the value of an expression 
	 * @return percentage  of the two elements of the list where the first element of the list will be numerator and the second element of the list will be denominator.
	 * 
	 */
	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		if(argumentList.size() != 2){
			throw new IllegalArgumentException("Number of arguments mismatch ,percentage function must have only 2 arguments ");
		}
		long percentage;
		if(argumentList.get(0).getLongValue(valueProvider) < 0 || argumentList.get(1).getLongValue(valueProvider) < 0 ){
			throw new IllegalArgumentException("Numerator or Denominator value for obtaining percentage can not be negative");
		}
		if(argumentList.get(1).getLongValue(valueProvider) == 0){
			throw new IllegalArgumentException("Denominator can not be zero. Divide By Zero exception is thrown");
		}
			percentage = (argumentList.get(0).getLongValue(valueProvider)*100)/(argumentList.get(1).getLongValue(valueProvider));
		return percentage;
	}

	/**
	 * @return the name of a function operation
	 */
	@Override
	public String getName() {
		return PERCENTAGE;
	}
}
