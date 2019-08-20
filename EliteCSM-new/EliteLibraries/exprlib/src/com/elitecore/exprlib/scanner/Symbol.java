package com.elitecore.exprlib.scanner;

import java.io.Serializable;

/**
 * Defines the Symbol interface, which is used to represent all terminals
 * and nonterminals while parsing.
 * 
 * @author Subhash Punani
 */
public interface Symbol extends Serializable{
	int keyword=1;
	int operator=2;
	int literal=3;
	int function=4;
	int identifier=5;

		/**
		 * 
		 * It returns the type of the symbol 
		 */
	public int getType();
	public String getName();
}
