package com.elitecore.exprlib.parser.expression.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.LiteralExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.symbol.impl.LiteralSymbol;

public class LiteralExpressionImpl implements LiteralExpression {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private LiteralSymbol expression;
	private Pattern regx;
	private String expressionStr;
	private boolean isRegularExpression;
	private boolean hasWildCardCharacter;
	
	
	public LiteralExpressionImpl(LiteralSymbol expression) {
		this.expression=expression;
		expressionStr=expression.getName();
		isRegularExpression=hasWildCardCharacter=false;
		checkRegularExpression();
		checkWildCardCharacter();
	}
	
	private void checkRegularExpression(){
		try{
			if(expressionStr.length() > 1 && expressionStr.startsWith("~")){
				this.isRegularExpression=true;
				String regxPattern = expressionStr.substring(1);
				regx = Pattern.compile(regxPattern);
			}
		}catch (PatternSyntaxException ex) {
			throw new java.lang.IllegalArgumentException(ex);
		}
	}
	
	private void checkWildCardCharacter(){
		char[] experssionArray=expressionStr.toCharArray();
		char ch;
		for(int index=0;index<experssionArray.length;index++){
			ch=experssionArray[index];
			if(ch=='*' || ch=='?'){
				hasWildCardCharacter=true;
				return;
			}
			if( ch=='\\')
				index++;
		}
	}
	
	public String getName(){
		return this.expressionStr;
	}
	
	public int getExpressionType() {
		return Expression.LiteralExpression;
	}

	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException{
		try{
			return Long.parseLong(getName());
		}catch(NumberFormatException e){
			throw new InvalidTypeCastException("Configured literal contains a nonnumeric value : " + this.getName());
		}
		
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException {
		return getName();
	}

	public Pattern getPattern(){
		return this.regx;
	}

	@Override
	public boolean returnsMutipleValue() {
		return false;
}

	@Override
	public boolean isRegularExpression() {
		return isRegularExpression;
	}

	@Override
	public boolean hasWildCardCharacter() {
		return hasWildCardCharacter;
	}

	@Override
	public String toString() {
		return "\"" + expressionStr + "\"";
	}

	@Override
	public List<String> getStringValues(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		List<String> lstStringValues = new ArrayList<String>(2);
		lstStringValues.add(getStringValue(valueProvider));
		return lstStringValues;
	}

	@Override
	public List<Long> getLongValues(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		List<Long> lstLongValues = new ArrayList<Long>(2);
		long value = getLongValue(valueProvider);
		lstLongValues.add(value);
		return lstLongValues;
	}
}
