package com.elitecore.elitesm.web.expressionbuilder.client.core;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;

public class StateMachine {
	
	public static StringBuffer currentExpr = new StringBuffer();
	public static String currentState;
	/*
	 * INPUT      	INITIAL STATE 		FINAL STATE 		ACTION
	 * digit[0-9]	s0					s0					Populate list according to the prefix
	 * :			s0					s1					list of the nested attributes based on the prefix
	 * digit		s1					s0					Populate list according to the prefix
	 * = | !=		s0					s2					Populate the supported values or nothing
	 * "			s2					s3					Nothing
	 * char			s3					s3					Nothing
	 * "			s3					s4					Logical operators
	 * & 			s4					s5					&
	 * |			s4					s6					|
	 * & 			s5					s0					Do as above
	 * |			s6					s0					Do as above
	 * 															 
	 */
	
	
	
	
	
	public static String getCurrentState(String prefixData){
		currentExpr = new StringBuffer();
		prefixData = prefixData.trim();
		String cState= "s0";
		for(int i=0;i<prefixData.length();i++){
			if(isChar(prefixData.charAt(i)) && !cState.equalsIgnoreCase("s3")){
				cState = "s1";
				currentExpr.append(prefixData.charAt(i));
			}
			else if(isOperator(prefixData,i)){
				cState = "s2";
				currentExpr.append(prefixData.charAt(i));
			}else if(cState == "s2" && (prefixData.charAt(i) == '"' || prefixData.charAt(i) == '\'') ){
				cState = "s3";
				currentExpr.append(prefixData.charAt(i));
			}else if(prefixData.charAt(i) == '"' && cState == "s3"){
				cState = "s4";
				currentExpr = new StringBuffer();
				//currentExpr.append(prefixData.charAt(i));
			}else if(cState == "s4"){
				try{
					StringBuffer buffer = new StringBuffer();
					buffer.append(prefixData.charAt(i));
					
					if((i+1)<=(prefixData.length()-1)){
						
						buffer.append(prefixData.charAt(i+1));
						if(isLogicalOperator(buffer.toString())){
							cState = "s0";
							i++;
							//currentExpr = new StringBuffer();
						}
					}else{
						currentExpr = new StringBuffer();
						currentExpr.append(buffer.toString());
					}
				}catch(Exception e){
					cState = "error";
				}
			}else if(cState == "s3"){
				cState = "s3";
				currentExpr.append(prefixData.charAt(i));
			}else if(prefixData.charAt(i)==' '){

			}else{

				cState = "error";
			}
		}
		Log.debug("currentState is:"+cState);
		currentState=cState;
		return cState;

	}
	
	
	
	
	
	private static boolean isChar(char charAt) {
		String regx = "[0-9a-zA-Z-.]";
		StringBuffer buffer = new StringBuffer();
		buffer.append(charAt);
		return buffer.toString().matches(regx);
	}

	
	private static boolean isOperator(String prefixData, int i) {
	    List<String> operatorItems=ListUtility.getOperatorList();
		for (int j = 0; j < operatorItems.size(); j++) {
			
			String opr=operatorItems.get(j);
			StringBuffer buffer = new StringBuffer();
			
			if(opr.length()== 1){
               buffer.append(prefixData.charAt(i));
				if(buffer.toString().equalsIgnoreCase(opr))
					return true;
				
			}else if(opr.length() == 2 && prefixData.length()-1>=(i+1)){
				buffer.append(prefixData.charAt(i));
				buffer.append(prefixData.charAt(i+1));
				if(buffer.toString().equalsIgnoreCase(opr))
					return true;
			}
			
		}
		return false;
	}


	private static boolean isIntNumber(char num){
	    try{
	    	StringBuffer buffer = new StringBuffer();
	    	buffer.append(num);
	        Integer.parseInt(buffer.toString());
	    } catch(NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	private static boolean isLogicalOperator(String opr){
		boolean result = false;
		List<String>logicalOperators=ListUtility.getLogicalOperatorList();
		for(String oper:logicalOperators){
			if(oper.equalsIgnoreCase(opr)){
				result = true;
				break;
			}
		}
		return result;
	}

	
	
	
}





/*
 *       
			if(isIntNumber(prefixData.charAt(i))){
				currentState = "s0";
				currentExpr.append(prefixData.charAt(i));
			}else if(prefixData.charAt(i) == ':'){
				currentState = "s1";
				currentExpr.append(prefixData.charAt(i));
			}else if(prefixData.charAt(i) == '='){
				currentState = "s2";
				currentExpr.append(prefixData.charAt(i));
			}else if(currentState == "s2" && (prefixData.charAt(i) == '"' || prefixData.charAt(i) == '\'') ){
				currentState = "s3";
				currentExpr.append(prefixData.charAt(i));
			}else if(prefixData.charAt(i) == '"' && currentState == "s3"){
				currentState = "s4";
			}else if(currentState == "s4"){
				try{
				StringBuffer buffer = new StringBuffer();
				buffer.append(prefixData.charAt(i));
				buffer.append(prefixData.charAt(i+1));
				if(isLogicalOperator(buffer.toString())){
					currentState = "s0";
					i++;
					//currentExpr = new StringBuffer();
				}
				}catch(Exception e){
					currentState = "error";
				}
			}else if(currentState == "s3"){
				currentState = "s3";
				currentExpr.append(prefixData.charAt(i));
			}else{
				currentState = "error";
			}
		
 * 
 */

