package com.elitecore.exprlib.symbol.impl;

import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.symbol.SymbolFactory;
import com.elitecore.exprlib.symbol.SymbolFactoryConfiguration;
import com.elitecore.exprlib.symbol.impl.operator.AndOperator;
import com.elitecore.exprlib.symbol.impl.operator.ClosingBraces;
import com.elitecore.exprlib.symbol.impl.operator.HashOperator;
import com.elitecore.exprlib.symbol.impl.operator.Equals;
import com.elitecore.exprlib.symbol.impl.operator.GreaterThan;
import com.elitecore.exprlib.symbol.impl.operator.InOperator;
import com.elitecore.exprlib.symbol.impl.operator.LessThan;
import com.elitecore.exprlib.symbol.impl.operator.NotOperator;
import com.elitecore.exprlib.symbol.impl.operator.OROperator;
import com.elitecore.exprlib.symbol.impl.operator.OpeningBraces;


public class SymbolFactoryImpl implements SymbolFactory {

	SymbolFactoryConfiguration config;
	
	public SymbolFactoryImpl(SymbolFactoryConfiguration config) {
		this.config=config;
	}
	
	public Symbol createSymbol(String symbol) {
		
		int type=config.getSymbolType(symbol);
		
		switch (type) {
			case Symbol.function:
				FunctionSymbol function=new FunctionSymbol(symbol);
				return function;
				
			case Symbol.literal:
				/*
				 *remove double quotes from start/end of string
				 *eg. "abc" is converted to abc 
				 */
				symbol = symbol.substring(1,symbol.length()-1);
				LiteralSymbol literal = new LiteralSymbol(symbol);
				return literal;
				
			case Symbol.operator:
				Operator operatorType=config.getOperatorType(symbol);
				//if the symbol is of operator type this switch evaluates which operator it is
					switch (operatorType) {
					case AND:
						AndOperator and=new AndOperator(symbol);
						return and;
					case OR:
						OROperator or=new OROperator(symbol);
						return or;
					case IN:
						InOperator in=new InOperator(symbol);
						return in;
					case NOT:
						NotOperator not=new NotOperator(symbol);
						return not;
					case OPENINGBRACES:
						OpeningBraces openingBraces=new OpeningBraces(symbol);
						return openingBraces;
					case CLOSINGBRACES:
						ClosingBraces closingBraces=new ClosingBraces(symbol);
						return closingBraces;
					case LESSTHAN:
						LessThan lessThan=new LessThan(symbol);
						return lessThan;
					case GREATERTHAN:
						GreaterThan greaterThan=new GreaterThan(symbol);
						return greaterThan;
					case EQUALS:
						Equals equals=new Equals(symbol);
						return equals;
					case HASH:
						HashOperator hash=new HashOperator(symbol);
						return hash;
					}
				break;
				
			default:
				IdentifierSymbol identifier=new IdentifierSymbol(symbol);
				return identifier;
		}
		return null;
	}
}

