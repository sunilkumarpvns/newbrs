package com.elitecore.exprlib.parser.expression.impl;



import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * The FunctionSubtract class performs the minus operation on the provided arguments as a list. It performs the subtraction on the each element of a list. 
 * @author Ishani Bhatt
 */
public class FunctionSubtract extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String SUB="sub";
	
	/**
	 * this method will iterate a list and performs the subtraction on the elements.
	 * @author Ishani Bhatt
	 * @param Value provider to get the value of an expression 
	 * @return subtraction of all the elements provided in a list
	 * 
	 */
	
	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		if(argumentList.size() < 2){
			throw new IllegalArgumentException("Number of arguments mismatch ,subtraction function must have atlest 2 arguments ");
		}
		Long subtract=argumentList.get(0).getLongValue(valueProvider);
		for(int i=1 ;i< argumentList.size() ;i++){
			subtract = subtract - argumentList.get(i).getLongValue(valueProvider);
		}
		return subtract;
	}
	
	/**
	 * @return the name of a function operation
	 */
	@Override
	public String getName() {
		return SUB;
	}

}
