package com.elitecore.aaa.core.data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class UserFileAccountData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	AccountData accountData = null;
	Map<String,String> authAttributesMap = new LinkedHashMap<String,String>();
	LogicalExpression expression;
	String condition;
	
	public void addAuthAttributes(String authAttributeId,String authAttributeValue) throws InvalidExpressionException {
		authAttributesMap.put(authAttributeId,authAttributeValue);
		if(condition == null)
			condition = authAttributeId + " = " + "\"" + authAttributeValue + "\"";
		else
			condition += " AND " + authAttributeId + " = " + "\"" + authAttributeValue + "\"";
			expression = Compiler.getDefaultCompiler().parseLogicalExpression(condition);
	}
	
	public Map<String,String> getAuthAttributesMap() {
		return authAttributesMap;
	}
	public void setAccountData(AccountData accountData){
		this.accountData = accountData;
	}
	public AccountData getAccountData(){
		return accountData;
	}
	
	public LogicalExpression getExpression(){
		return expression;
	}
}
	
		
