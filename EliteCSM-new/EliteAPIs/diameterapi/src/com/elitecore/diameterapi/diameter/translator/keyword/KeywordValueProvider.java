package com.elitecore.diameterapi.diameter.translator.keyword;

import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * Allows the values to fetched from Value Provider.
 * Here Value Provider can be of desired Type and is left on Implementation. 
 * 
 * @author monica.lulla
 *
 */
public abstract class KeywordValueProvider {

	private String agrgument;

	public KeywordValueProvider(String argument) {
		this.agrgument = argument;
	}  

	/**
	 * @param params Translator Params containing Packet Object
	 * @return Value Provider based on Packet
	 * @throws MissingIdentifierException when Unable to form Value Provider from Packet
	 */
	protected abstract @Nonnull ValueProvider getValueProvider(TranslatorParams params) throws MissingIdentifierException ;

	public String getStringValue(TranslatorParams params)
			throws InvalidTypeCastException, MissingIdentifierException {
		
		return getValueProvider(params).getStringValue(agrgument);
	}

	public long getLongValue(TranslatorParams params)
			throws InvalidTypeCastException, MissingIdentifierException {
		return getValueProvider(params).getLongValue(agrgument);
	}

	public List<String> getStringValues(TranslatorParams params)
			throws InvalidTypeCastException, MissingIdentifierException {
		return getValueProvider(params).getStringValues(agrgument);
	}

	public List<Long> getLongValues(TranslatorParams params)
			throws InvalidTypeCastException, MissingIdentifierException {
		return getValueProvider(params).getLongValues(agrgument);
	}
}
