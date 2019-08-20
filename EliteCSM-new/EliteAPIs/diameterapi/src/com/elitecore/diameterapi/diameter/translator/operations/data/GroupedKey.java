package com.elitecore.diameterapi.diameter.translator.operations.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class GroupedKey implements Key<List<AttributeMapping>> {
	
	private List<AttributeMapping> attributeMappings;
	private LogicalExpression checkCondition;
	private String parent;
	
	public GroupedKey(List<AttributeMapping> attributeMappings) {
		this(null, null, attributeMappings);
	}
	
	public GroupedKey(String parent, LogicalExpression checkCondition, List<AttributeMapping> attributeMappings) {
		Preconditions.checkNotNull(attributeMappings, "Attribute Mappings can not be null");
		this.attributeMappings = attributeMappings;
		this.parent = parent;
		this.checkCondition = checkCondition;
	}

	@Override
	public KeyTypes getKeyType() {
		return KeyTypes.GROUPED;
	}
	
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		for(AttributeMapping mapping:attributeMappings) {
			out.println(mapping);
		}
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public @Nonnull List<AttributeMapping> getElement() {
		return attributeMappings;
	}

	@Override
	public String getParent() {
		return parent;
	}

	@Override
	public LogicalExpression getCondition() {
		return checkCondition;
	}
	
}
