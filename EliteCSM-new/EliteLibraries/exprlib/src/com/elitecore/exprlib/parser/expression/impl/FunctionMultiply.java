package com.elitecore.exprlib.parser.expression.impl;




import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;


/**
 * The FunctionMultiply class performs the multiplication operation on the provided arguments as a list. It performs the multiplication  on the each element of a list. 
 * @author Ishani Bhatt
 */

public class FunctionMultiply extends AbstractIntegerFunctionExpression{

	private static final long serialVersionUID = 1L;
	private static final String MULTIPLICATION="mul";
	
	/**
	 * this method will iterate a list and performs the multiplication on the elements.
	 * @author Ishani Bhatt
	 * @param Value provider to get the value of an expression 
	 * @return multiplication of all the elements provided in a list
	 * 
	 */
	
	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		if(argumentList.size() < 2){
			throw new IllegalArgumentException("Number of arguments mismatch ,multiplication function must have atlest 2 arguments ");
		}
		
		long multiply = argumentList.get(0).getLongValue(valueProvider);
		for(int i=1;i < argumentList.size();i++){
			multiply = multiply * argumentList.get(i).getLongValue(valueProvider);
			if(multiply == 0){
				return 0;
			}
		}
		return multiply;
	}

	/**
	 * @return the name of a function operation
	 */
	@Override
	public String getName() {
		return MULTIPLICATION;
	}
}
