package com.elitecore.netvertex.service.offlinernc.core;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class RncRequestValueProvider implements ValueProvider {

	private RnCRequest rncPacket;

	public RncRequestValueProvider(RnCRequest rncPacket) {
		this.rncPacket = rncPacket;
	}
	
	@Override
	public String getStringValue(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		String val = rncPacket.getAttribute(identifier);
		if (val == null) {
			throw new MissingIdentifierException("Configured identifier not found: " + identifier);
		}
		return val;
	}

	@Override
	public long getLongValue(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		String val = getStringValue(identifier);
		try {
			return Long.parseLong(val);
		} catch (NumberFormatException nfe) {
			throw new InvalidTypeCastException("Invalid Numeric value found for Identifier '" + identifier + "' Value: " + val);
		}
	}

	@Override
	public List<String> getStringValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		String value = getStringValue(identifier);
		List<String> values = new ArrayList<>(1);
		values.add(value);
		return values;
	}

	@Override
	public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
		Long value = getLongValue(identifier);
		List<Long> values = new ArrayList<>(1);
		values.add(value);
		return values;
	}

	@Override
	public Object getValue(String key) {
		return null;
	}

}
