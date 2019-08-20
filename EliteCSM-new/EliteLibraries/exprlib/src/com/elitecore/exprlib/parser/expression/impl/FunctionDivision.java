package com.elitecore.exprlib.parser.expression.impl;


import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
/**
 * The FunctionDivision class performs the division operation on the provided arguments as a list. It performs the division on the elements of a list. 
 * @author Ishani Bhatt
 */
public class FunctionDivision extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;
	private static final String DIV="div";
	
	/**
	 * this method will iterate a list and performs the division on the elements.
	 * @author Ishani Bhatt
	 * @param Value provider to get the value of an expression 
	 * @return division of all the elements provided in a list
	 * 
	 */
	
	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
			if(argumentList.size() < 2){
				throw new  IllegalArgumentException("Number of arguments mismatch ,division function must have atlest 2 arguments ");
			}
			long division=argumentList.get(0).getLongValue(valueProvider);
			
			for(int i=1;i < argumentList.size() ; i++){
				long tempDivisionVale = argumentList.get(i).getLongValue(valueProvider); 
				if( tempDivisionVale == 0){
					throw new IllegalArgumentException("Can not divide a value by Zero");
					
				} 
				division = division / tempDivisionVale;
				if(division == 0){
					return 0;
				}
				
			}
			return division;
	}
	/**
	 * @return the name of a function operation
	 */
	@Override
	public String getName() {
		return DIV;
	}

}
