package com.elitecore.exprlib.parser.expression.impl;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.IdentifierExpression;
import com.elitecore.exprlib.parser.expression.LiteralExpression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.symbol.impl.Operator;
import com.elitecore.exprlib.symbol.impl.operator.ComparisionOperator;


public class ComparisionExpressionImpl implements LogicalExpression {
	
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "CompExpr";
	private Expression operand1,operand2;
	private ComparisionOperator operator;
	
	public ComparisionExpressionImpl(Expression operand1,Expression operand2,ComparisionOperator operator) {
		this.operand1=operand1;
		this.operand2=operand2;
		this.operator=operator;
	}
	
	public int getExpressionType() {
		return Expression.ComparisionExpression;
	}
	
	/**
	 * @author milancsm
	 * The Expression can be of two type 
	 * 1)Which return single value e.g.  Name = "milan*"
	 * 2)Which return multiple value e.g. SessionID* = "AV123"						
	 */
	public boolean evaluate(ValueProvider valueProvider){
		Operator operatorType=operator.getOperatorType();
		boolean result = false;
		LogManager.getLogger().debug(MODULE, operand1.getName()+operator+operand2.getName());
		try{
			if(operatorType == Operator.EQUALS){
				if(operand1.hasWildCardCharacter()){
					return evaluate(operand1.getStringValue(valueProvider).toCharArray(), operand2, valueProvider);
				}else if(operand2.hasWildCardCharacter()){
					return evaluate(operand2.getStringValue(valueProvider).toCharArray(), operand1, valueProvider);
				}else if(operand1.isRegularExpression()){
					return evaluate(((LiteralExpression)operand1).getPattern(), operand2, valueProvider);
				}else if(operand2.isRegularExpression()){
					return evaluate(((LiteralExpression)operand2).getPattern(), operand1, valueProvider);
				}else{
					return evaluateString(valueProvider);
	}
			}else{
				return evaluateNumber(valueProvider, operatorType);
	}
		}catch(InvalidTypeCastException e){
			LogManager.getLogger().debug(MODULE, e.getMessage());
		}catch(IllegalArgumentException e){
			LogManager.getLogger().debug(MODULE, e.getMessage());
		}catch (MissingIdentifierException e) {
			// If the identifier is missing than check whether the identifier is optional or not 
			LogManager.getLogger().debug(MODULE, e.getMessage());
			result=e.isOptional();
		}
		return result;
	}
	
	
	/**
	 * Method will evaluate the NUMERIC expression
	 * Operator can be <  or   >  , 
	 * @param valueProvider
	 */
	private boolean evaluateNumber(ValueProvider valueProvider,Operator operatorType) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{

		if(!operand1.returnsMutipleValue() && !operand2.returnsMutipleValue()){
			return evaluate(operand1.getLongValue(valueProvider), operand2.getLongValue(valueProvider), operatorType);
		}else if (!operand1.returnsMutipleValue()){
			return evaluate(operand1.getLongValue(valueProvider), operand2, operatorType, valueProvider);
		}else{
			List<Long> values = ((IdentifierExpression)operand1).getLongValues(valueProvider);
			for(Long value1 : values){
				if(evaluate(value1, operand2, operatorType, valueProvider))
					return true;
						}
		}
						return false;
					}
	
	
	/**
	 * Method will Compare the NUMERIC Expression based on operator can be < , >  
	 * @param value1 : Long value to compare
	 * @param operand2 : Contains Multiple long value to compare 
	 * @param op :  Can be < or >
	 * @param valueProvider
	 */
	private boolean evaluate(long value1, Expression operand2,Operator op, ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		if(operand2.returnsMutipleValue()){
		List<Long> values = ((IdentifierExpression)operand2).getLongValues(valueProvider);
			for(long value2 : values){
				if(evaluate(value1,value2,op))
				return true;
				}
		}else{
			long value2=operand2.getLongValue(valueProvider);
			return evaluate(value1,value2,op);
		}
		return false;
	}

	/**
	 * Evaluate Numeric Expression ,eg: UploadQuota < 10000
	 * @param value1 : long value to compare
	 * @param value2 : long value to compare
	 * @param op : Can be < or >
	 */
	private boolean evaluate(long value1,long value2,Operator op){
		switch (op) {
			case LESSTHAN:
			return value1 < value2;
			case GREATERTHAN:
			return value1 > value2;
		default:
			return false;
			}
	}
			
	
	/**
	 * Method will evaluate the String expression
	 * IF Operator is = than 
	 * @param valueProvider
	*/
	private boolean evaluateString(ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		if(!operand1.returnsMutipleValue() && !operand2.returnsMutipleValue() ){
			return operand1.getStringValue(valueProvider).equals(operand2.getStringValue(valueProvider));
		}else if( operand1.returnsMutipleValue() && !(operand2.returnsMutipleValue()) ){
			return evaluate(operand2.getStringValue(valueProvider),operand1,valueProvider);
		}else if( operand2.returnsMutipleValue() && !(operand1.returnsMutipleValue()) ){
			return evaluate(operand1.getStringValue(valueProvider),operand2,valueProvider);
		}else{
			List<String> values = ((IdentifierExpression)operand1).getStringValues(valueProvider);
			for(String value : values){
				if(evaluate(value, operand2,valueProvider))
					return true;
		}
		}
		return false;
	}
	
	
	/**
	 * Method will Compare the Equality of String Expression ,eg: sessionID = "1234"
	 * @param value1 : String value to compare
	 * @param operand2 : Contains Multiple value to compare
	 * @param valueProvider
	 */
	private boolean evaluate(String value1, Expression operand2, ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		List<String> values = ((IdentifierExpression)operand2).getStringValues(valueProvider);
		for(String value : values){
			if(value1.equals(value))
				return true;
		}
		return false;
	}

	
	/**
	 * Method evaluate the Regular expression 
	 * One Expression may returns multiple value and other is Regx  e.g. ~abc11
	 * @param pattern : Pattern To compare the String
	 * @param operand2 : Contains Multiple or single String value to compare in Pattern
	 * @param valueProvider
	 */
	private boolean evaluate(Pattern pattern, Expression operand2, ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		if(operand2.returnsMutipleValue()){
			List<String> values = ((IdentifierExpression)operand2).getStringValues(valueProvider);
			for(String value : values){
				if(matches(value, pattern))
					return true;
	}
		}else{
			return matches(operand2.getStringValue(valueProvider), pattern);
	}
		return false;
	}

	
	/**
	 * Method Evaluate the Regular expression  e.g. ~abc11
	 * @param value : String value to match with Pattern
	 * @param pattern : Pattern to compare with String
	 * @return
	 */
	private boolean matches(String value,Pattern pattern){
		return pattern.matcher(value).matches();
	}
	
	
	/**
	 * The method evaluate String having WildCardCharacter  
	 * One Expression may returns multiple value and other contains WildCardChar e.g. abc11* or abc11?
	 * @param value1 : String that contains WildCardCharacter like * or ?
	 * @param operand2 : Contains Multiple or single String value to compare
	 * @param valueProvider
	 */
	private boolean evaluate(char[] value1, Expression operand2, ValueProvider valueProvider) throws InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		if(operand2.returnsMutipleValue()){
			List<String> values = ((IdentifierExpression)operand2).getStringValues(valueProvider);
			for(String value : values){
				if(matches(value,value1))
					return true;
			}
		}else{
			return matches(operand2.getStringValue(valueProvider),value1);
		}
		return false;
	}
	
	/**
	 * @author milan
	 * @param source String string to be compare
	 * @param pattern String that contains Wild Card Character like * or ?
	 * compare two expression if one expression contains * or ?
	 */
	private static boolean matches(String sourceString, char[] pattern){
		int stringOffset = 0;
		char[] stringCharArray = sourceString.toCharArray();
		final int stringLen = stringCharArray.length;
		final int patternLen = pattern.length;
		int currentPos=0;
		try{
			for(currentPos=0;currentPos<patternLen;currentPos++,stringOffset++){
				if(stringOffset == stringLen){
					while(currentPos < patternLen){
						if(pattern[currentPos] != '*')
							return false;
						currentPos++;
					}
					return true;
				}
				
				
				if(pattern[currentPos]!=stringCharArray[stringOffset]){
					if(pattern[currentPos]=='\\'){
						currentPos++;
						if(pattern[currentPos]!=stringCharArray[stringOffset])
							return false;
						else
							continue;
					}
					if(pattern[currentPos]=='*'){
						boolean bStar = true;
						currentPos++;
						if(currentPos == patternLen)
							return true;
						while(bStar){
							int tmpCurrentPos = currentPos;
							//go to first matching occurrence
							while(stringCharArray[stringOffset]!=pattern[tmpCurrentPos]){
								stringOffset++;
								if(stringOffset == stringLen){
									while(tmpCurrentPos < patternLen){
										if(pattern[tmpCurrentPos] != '*')
											return false;
										tmpCurrentPos++;
									}
									return true;
								}
							}
							//match whole string until next * come
							while(tmpCurrentPos < patternLen){
								if(pattern[tmpCurrentPos] != stringCharArray[stringOffset]){
									if(pattern[tmpCurrentPos] == '*'){
										bStar = false;
										currentPos = tmpCurrentPos - 1;
										stringOffset--;
										break;
									}else if(pattern[tmpCurrentPos] != '?'){
										break;
									}
								}
								tmpCurrentPos++;
								stringOffset++;
								if(stringOffset == stringLen){
									while(tmpCurrentPos < patternLen){
										if(pattern[tmpCurrentPos] != '*'){
											return false;
										}
										tmpCurrentPos++;
									}
									return true;
								}
							}
							if(stringOffset == stringLen && tmpCurrentPos == patternLen)
								return true;
						}
						continue;
					}
					if(pattern[currentPos]=='?'){
						continue;
					}
					return false;
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
		if(currentPos < patternLen){
			while(pattern[currentPos] == '*')
				currentPos++;
		}
		return(currentPos == patternLen && stringOffset == stringLen);
	}
	
	@Override
	public long getLongValue(ValueProvider valueProvider)	throws InvalidTypeCastException {
		return 0;
}
	@Override
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean returnsMutipleValue() {
		return false;
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
		return "(" + operand1.toString() + " " + operator.getOperatorType().type + " " + operand2.toString() + ")";
	}

	@Override
	public List<String> getStringValues(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return Collections.emptyList();
	}

	@Override
	public List<Long> getLongValues(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return Collections.emptyList();
	}
	
}
