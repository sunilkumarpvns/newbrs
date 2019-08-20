package com.elitecore.exprlib.symbol.impl;

import java.util.HashMap;
import java.util.Map;

public enum Operator{
	
	AND("AND","<operand> AND <operand>"),
	OR("OR","<operand> OR <operand>"),
	NOT("NOT","NOT <operand>"),
	IN("IN","<operand> IN(<operand>,..)"),
	OPENINGBRACES("(",null),
	CLOSINGBRACES(")",null),
	LESSTHAN("<","<operand> < <operand>"),
	GREATERTHAN(">","<operand> > <operand>"),
	EQUALS("=","<operand> = <operand>"),
	HASH("#",null);
	
	public final String type;
	public final String syntax;
	
	private Operator(String operatorType, String syntax){
		this.type=operatorType;
		this.syntax = syntax;
	}
	
	private static final Operator[] types = values();
	private static final Map<String, Operator> objectMap;
	
	static {
		objectMap = new HashMap<String,Operator>();
		for (Operator type : types){
			objectMap.put(type.type, type);
		}
		objectMap.put("&&",AND);
		objectMap.put("||",OR);
	}

	public static Operator getOperatorType(String operatorType){
		return objectMap.get(operatorType);
	}
	
	public String getOperatorType(){
		return type;
	}
}
