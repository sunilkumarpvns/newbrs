
package com.elitecore.exprlib.symbol;

import com.elitecore.exprlib.symbol.impl.Operator;

/**
 * @author milan paliwal
 *
 */
public interface SymbolFactoryConfiguration {
	
	/**
	 * @param symbol 
	 * @return the type of the symbol
	 */
	int getSymbolType(String symbol);
	
	/**
	 * @param symbol
	 * @return the type of the operator
	 */
	Operator getOperatorType(String symbol);	
}
