package com.elitecore.aaa.diameter.translators.policy.data;

import java.util.Map;

import com.elitecore.exprlib.parser.expression.LogicalExpression;

public interface ParsedMapping {
	public LogicalExpression getExpression();
	public Map<String,String> getMappingNodes();
	public Map<String,String> getValueNodes();
	public Map<String,String> getDefaultValuesMap();
	public String getStrExpression();
}
