package com.elitecore.exprlib.parser.expression;

import java.io.Serializable;
import java.util.List;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;



public interface Expression extends Serializable{
	public int LogicalExpression = 1;
	public int ComparisionExpression=2;
	public int FunctionExpression = 3;
	public int IdentifierExpression = 4;
	public int LiteralExpression = 5;
	public abstract int getExpressionType();
	
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException;
	
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException;
	
	public List<String> getStringValues(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException;
	public List<Long> getLongValues(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException;
	
	public String getName();
	
	/**
	 * @author milan
	 * @return whether the Expression returns the Multiple value or not	
	 */
	public boolean returnsMutipleValue();
	
	/**
	 * @author milan
	 * @return whether the Expression is RegularExpression	
	 */
	public boolean isRegularExpression();
	
	/**
	 * @author milan
	 * @return whether the Expression has WildCard Character	
	 */
	public boolean hasWildCardCharacter();
	
}
