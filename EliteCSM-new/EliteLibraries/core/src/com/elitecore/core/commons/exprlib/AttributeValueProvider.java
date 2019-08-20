package com.elitecore.core.commons.exprlib;

import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * This class is used when you need control over using raw/base value of attributes that are of
 * enumerated type.
 * 
 * @author khushbu.chauhan
 * @author malav.desai
 *
 */
public interface AttributeValueProvider extends ValueProvider {
	boolean USE_DICTIONARY_VALUE = true;
	boolean USE_BASE_VALUE = false;
	
	/**
	 * Returns key of the {@code identifier} passed. For instance if 0:40 is an attribute having raw value 1, which is mapped in dictionary
	 * with name {@literal "}Start{@literal "}, then the return value should be the raw value 1.
	 * <br/>
	 * NOTE: If the attribute identified by the {@code identifier} is not of enumerated type then the raw value must be returned.
	 * 
	 * @param identifier identifier of the attribute
	 * @throws MissingIdentifierException when identifier is not present in packet.
	 */
	String getDictionaryKey(String identifier) throws InvalidTypeCastException, MissingIdentifierException;
}
