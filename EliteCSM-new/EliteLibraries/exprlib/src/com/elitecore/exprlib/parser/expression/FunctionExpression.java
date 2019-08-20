package com.elitecore.exprlib.parser.expression;

import java.util.List;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;

public interface FunctionExpression  extends Expression ,Cloneable{
	public int StringFunction = 1;
	public int IntegerFunction = 2;
	public int FloatingPointFunction = 3;
	public int getFunctionType();
	public void addArgument(Expression expression);
	public Object clone();
	public String getName();
	public List<String> getStringValues(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException;
	public List<Long> getLongValues(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException;
}
