package com.elitecore.diameterapi.diameter.translator.operations.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;


public class NonGroupedKey implements Key<Expression> {

	private Expression expression;
	private String defaultValue;
	private Map<String,String> valueMappings ;
	private String parent;
	private LogicalExpression checkCondition;
	private Map<String, KeywordValueProvider> packetExtractorMap;
	
	public NonGroupedKey(Expression value) {
		this(null, null, value);
	}
	
	public NonGroupedKey(String parent, LogicalExpression checkCondition, Expression value) {
		this(parent, checkCondition, value, null, null);
	}
	
	public NonGroupedKey(String defaultValue) {
		this(null, null, null, defaultValue, null, null);
	}
	
	public NonGroupedKey(String parent, LogicalExpression checkCondition, Expression value,String defaultValue,Map<String,String> valueMappings) {
		this(parent, checkCondition, value, defaultValue, valueMappings, null);
	}
	
	public NonGroupedKey(String parent, LogicalExpression checkCondition, Expression value,String defaultValue,Map<String,String> valueMappings, Map<String, KeywordValueProvider> packetExtractorMap) {
		this.parent = parent;
		this.checkCondition = checkCondition;
		this.expression = value;
		this.defaultValue = defaultValue;
		this.valueMappings = valueMappings;
		this.packetExtractorMap = packetExtractorMap;
	}
	
	public String getDefaultValue(){
		return defaultValue;
	}
	
	public String getValue(String key){
		String value;
		if(valueMappings != null && (value = valueMappings.get(key))!=null)
			return value;
		else
			return key;
	}
	
	@Override
	public KeyTypes getKeyType() {
		return KeyTypes.NONGROUPED;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.print(this.expression);
		
		if(this.defaultValue != null){
			out.print(" (");
			out.print(this.defaultValue);
			out.print(") ");
		}
		if (valueMappings != null && !valueMappings.isEmpty()) {
		
			out.print("[ ");
			
			for(Map.Entry<String, String> entry: valueMappings.entrySet()) {
				out.print(entry.getKey());
				out.print(" = ");
				out.print(entry.getValue());
				out.print(" ");
			}
			out.print(" ]");
		}
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public Expression getElement() {
		return expression;
	}

	@Override
	public String getParent() {
		return parent;
	}

	@Override
	public LogicalExpression getCondition() {
		return checkCondition;
	}
	
	public Map<String, KeywordValueProvider> getPacketExtractorMap() {
		return packetExtractorMap;
	}

	public void setPacketExtractorMap(
			Map<String, KeywordValueProvider> packetExtractorMap) {
		this.packetExtractorMap = packetExtractorMap;
	}
	
	
}
