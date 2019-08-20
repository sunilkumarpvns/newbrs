package com.elitecore.exprlib.symbol;

import com.elitecore.exprlib.scanner.Symbol;

/**
 * @author milan paliwal
 * This factory creates the symbol
 */
public interface SymbolFactory {
	
	/**
	 * 
	 * @param symbol it is a String of which symbol is to be made
	 * @return returns the related symbol object
	 */
	Symbol createSymbol(String symbol);

}
