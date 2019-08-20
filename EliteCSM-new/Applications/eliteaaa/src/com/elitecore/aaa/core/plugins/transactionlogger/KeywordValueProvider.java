package com.elitecore.aaa.core.plugins.transactionlogger;

import java.util.List;

import com.elitecore.core.commons.exprlib.AttributeValueProvider;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;

public class KeywordValueProvider implements AttributeValueProvider {

	private final AttributeValueProvider request;
	private final AttributeValueProvider response;
	private AttributeValueProvider attributeValueProvider;

	public KeywordValueProvider(AttributeValueProvider request,
			AttributeValueProvider response) {
		this.request = request;
		this.response = response;
		this.attributeValueProvider = this.request;
	}

	@Override
	public String getStringValue(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		identifier = parse(identifier);
		return this.attributeValueProvider.getStringValue(identifier);
	}

	@Override
	public long getLongValue(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		identifier = parse(identifier);
		return this.attributeValueProvider.getLongValue(identifier);
	}

	@Override
	public List<String> getStringValues(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		identifier = parse(identifier);
		return this.attributeValueProvider.getStringValues(identifier);
	}

	@Override
	public List<Long> getLongValues(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		identifier = parse(identifier);
		return this.attributeValueProvider.getLongValues(identifier);
	}

	private String parse(String identifier) {
		identifier = identifier.trim();
		if (identifier.startsWith("$RES:")) {
			this.attributeValueProvider = this.response;
			identifier = removeKeyword(identifier);
		} else if (identifier.startsWith("$REQ:")) {
			this.attributeValueProvider = this.request;
			identifier = removeKeyword(identifier);
		} else {
			this.attributeValueProvider = this.request;
		}
		return identifier;
	}

	private String removeKeyword(String identifier) {
		return identifier.substring(5);
	}

	@Override
	public Object getValue(String key) {
		return request.getValue(key);
	}

	@Override
	public String getDictionaryKey(String identifier)
			throws InvalidTypeCastException, MissingIdentifierException {
		return this.attributeValueProvider.getDictionaryKey(identifier);
	}
}