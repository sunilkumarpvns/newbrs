package com.elitecore.aaa.exprlib.function;

import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.AbstractStringFunctionExpression;

public class FunctionFirstOf extends AbstractStringFunctionExpression {
	private static final String MODULE = "FUNCTION-FIRSTOF";
	private static final String FIRSTOF="firstof";
	
	@Override
	public String getName() {
		return FIRSTOF;
	}

	@Override
	public List<String> getStringValues(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {

		if(Collectionz.isNullOrEmpty(argumentList)){
			throw new IllegalArgumentException("Expression list can not be empty");
		}
		
		for (Expression expression : argumentList) {
			try {
				List<String> result = expression.getStringValues(valueProvider);
				if (Collectionz.isNullOrEmpty(result) == false) {
					return result;
				}
			} catch(MissingIdentifierException missingIdentifierException) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, missingIdentifierException.getMessage());
				}
			}
		}
		throw new MissingIdentifierException("Can not find value for expression: " + this.toString());
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return getStringValues(valueProvider).get(0);
	}

}
