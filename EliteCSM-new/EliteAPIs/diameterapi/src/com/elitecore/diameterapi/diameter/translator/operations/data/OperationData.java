package com.elitecore.diameterapi.diameter.translator.operations.data;

import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class OperationData {
	
	private LogicalExpression checkExp;
	private AttributeMapping attributeMapping;
	
	public OperationData(LogicalExpression checkExp,
			AttributeMapping attributeMapping) {

		this.checkExp = checkExp;
		this.attributeMapping = attributeMapping;
	}
	
	public LogicalExpression getCheckExpression() {
		return checkExp;
	}
	public AttributeMapping getAttributeMapping() {
		return attributeMapping;
	}
}
