package com.elitecore.exprlib.scanner;

import java.util.List;
import com.elitecore.exprlib.parser.exception.InvalidSymbolException;
import com.elitecore.exprlib.scanner.Symbol;

/**
 * Defines the Scanner interface, which uses in the default implementation.  
 * Integration of scanners implementing <code>Scanner</code> is facilitated.
 * 
 * 
 * @author Subhash Punani
 */
public interface Scanner {
	/**
	 * Returns list of <code>String</code>
	 * @return List
	 */
	List<Symbol> getSymbols(String expression)throws InvalidSymbolException; 
	
	void addFunction(String functionName);
	
}
