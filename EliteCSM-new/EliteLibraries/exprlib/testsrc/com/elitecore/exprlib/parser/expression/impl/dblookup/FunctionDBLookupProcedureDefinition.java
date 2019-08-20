package com.elitecore.exprlib.parser.expression.impl.dblookup;

public class FunctionDBLookupProcedureDefinition {
	
	public static int addNumberFunction(int val1, int val2){
		return(val1+val2);
	}
	
	public static void addNumberProcedure(int val1,int val2,int[] val3){
		val3[0] = val1 + val2;
	}
	
	public static void addNumberProcWithAllINParams(int val1,int val2,int val3){
		val3 = val1 + val2;
	}

	public static void addNumberProcwithAllOUTParams(int[] val1,int[] val2,int[] val3){
	}
	
}
