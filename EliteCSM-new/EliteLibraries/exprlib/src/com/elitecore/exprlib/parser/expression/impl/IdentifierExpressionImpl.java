package com.elitecore.exprlib.parser.expression.impl;

import java.util.List;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.IdentifierExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.symbol.impl.IdentifierSymbol;


public class IdentifierExpressionImpl implements IdentifierExpression {
	
	private static final long serialVersionUID = 1L;
	private IdentifierSymbol expression;
	private String expressionStr;
	private boolean isOptional;
	private boolean returnsMultiple;
	
	
	public IdentifierExpressionImpl(IdentifierSymbol expression) {
		this.expression=expression;
		isOptional=returnsMultiple=false;
		expressionStr=expression.getName();
		if(expressionStr.charAt(0)=='*'){
			returnsMultiple=true;
			expressionStr=expressionStr.substring(1);
	}
		if(expressionStr.charAt(0)=='[' && expressionStr.charAt(expressionStr.length()-1)==']'){
			isOptional=true;
			expressionStr=expressionStr.substring(1,expressionStr.length()-1);
		}
	}
	
	
	public String getName(){
		return this.expressionStr;
	}
	
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException{
		Long value;
		try{
			value=valueProvider.getLongValue(this.getName());
			
		}catch(MissingIdentifierException e){
			 e.setIsOptional(isOptional);
			 throw e;
		}
		catch(NumberFormatException i){
			throw new InvalidTypeCastException("Configured identifier contains a nonnumeric value : " + this.getName());
		}
		return value;
	}

	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		try{
		String value=valueProvider.getStringValue(this.getName());
		if(value==null)
				throw new MissingIdentifierException("Configured identifier not found: " + this.getName(),isOptional);
		else
			return value;
		}catch(MissingIdentifierException e){
			e.setIsOptional(isOptional);
			throw e;
	}
	}

	public int getExpressionType() {
		return Expression.IdentifierExpression;
	}

	@Override
	public IdentifierSymbol getIdentifierSymbol() {
		return (IdentifierSymbol)expression;
}

	@Override
	public List<String> getStringValues(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		try{
			List<String> stringValues=valueProvider.getStringValues(this.getName());
			if(stringValues==null)
				throw new MissingIdentifierException("Configured identifier not found: " + this.getName(),isOptional);
			else
				return stringValues;
		}catch(MissingIdentifierException e){
			e.setIsOptional(isOptional);
			throw e;
		}
	}


	@Override
	public List<Long> getLongValues(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException,MissingIdentifierException {
		List<Long> longValues;
		try{
			longValues=valueProvider.getLongValues(this.getName());
			if(longValues==null)
				throw new MissingIdentifierException("Configured identifier not found: " + this.getName(),isOptional);
		}catch(MissingIdentifierException e){
			e.setIsOptional(isOptional);
			throw e;
		}catch(NumberFormatException i){
			throw new InvalidTypeCastException("Configured identifier contains a nonnumeric value : " + this.getName());
		}
		return longValues; 
	}

	@Override
	public boolean returnsMutipleValue(){
		return returnsMultiple;
	}


	@Override
	public boolean isRegularExpression() {
		return false;
	}


	@Override
	public boolean hasWildCardCharacter() {
		return false;
	}


	@Override
	public String toString() {
		return expressionStr;
	}
}
