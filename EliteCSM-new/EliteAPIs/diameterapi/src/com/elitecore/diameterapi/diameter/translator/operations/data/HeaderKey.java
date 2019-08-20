package com.elitecore.diameterapi.diameter.translator.operations.data;

import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class HeaderKey<P> implements Key<HeaderFields<P>> {

	private HeaderFields<P> headerField;

	public HeaderKey(HeaderFields<P> headerFeild) {
		this.headerField = headerFeild;
	}

	@Override
	public KeyTypes getKeyType() {
		return KeyTypes.HEADER;
	}

	@Override
	public HeaderFields<P> getElement() {
		return headerField;
	}

	@Override
	public String getParent() {
		return null;
	}

	@Override
	public LogicalExpression getCondition() {
		return null;
	}

}
