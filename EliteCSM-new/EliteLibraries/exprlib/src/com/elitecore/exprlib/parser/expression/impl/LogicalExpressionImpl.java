package com.elitecore.exprlib.parser.expression.impl;

import java.util.Collections;
import java.util.List;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.symbol.impl.Operator;
import com.elitecore.exprlib.symbol.impl.operator.LogicalOperator;

public class LogicalExpressionImpl implements LogicalExpression {

	private static final long serialVersionUID = 1L;
	private LogicalExpression operand1,operand2;
	private LogicalOperator operator;
	
	public LogicalExpressionImpl(LogicalExpression operand1,LogicalExpression operand2,LogicalOperator operator) {
		this.operand1=operand1;
		this.operand2=operand2;
		this.operator=operator;
	}
	
	public boolean evaluate(ValueProvider valueProvider) {
		boolean result1 = false;
		boolean result2 = false;
		
		result1=operand1.evaluate(valueProvider);
		
		if(operator.getOperatorType()==Operator.AND ){
			if(result1==false)
				return false;
		}else if(operator.getOperatorType()==Operator.OR){
			if(result1==true)
				return true;
		}else if(operator.getOperatorType()==Operator.NOT){
			return !result1;
		}
	
		result2=operand2.evaluate(valueProvider);
		return result2;
	}

	public int getExpressionType() {
		return Expression.LogicalExpression;
	}
	
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException{
		return 0;
	}
	
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException,IllegalArgumentException{
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
		
		if(operator.getOperatorType() == Operator.NOT){
			return "(" + operator.getOperatorType().type + " " + operand1.toString() + ")";
		}
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
