package com.elitecore.core.commons.util;

import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;

public class StringOperationUtility  extends TranslationOperationUtility{
	
	private static final String MODULE = "STRING-OPT-UTILITY";
	
	private static final String RESPLACE_FIRST  = "replacefirst";
	private static final String RESPLACE_ALL    = "replaceall";
	private static final String CONCAT          = "concat";
	private static final String SUB_STRING      = "substring";
	private static final String TO_LOWER_CASE   = "tolowercase";
	private static final String TO_UPPER_CASE   = "touppercase";
	private static final String TRIM            = "trim";
	private static final String STRIP_PREFIX    = "stripprefix";
	private static final String STRIP_SUFFIX    = "stripsuffix";
	public static final String STROP            = "${STROP}";
	
	public static String getValue(String strKeyword, ValueProvider valueProvider,String sourceKeyArg) {
		String resultString = null;
		if(strKeyword!=null && valueProvider!=null && STROP.equalsIgnoreCase(getKeywordName(strKeyword))){
			List<String> tokens = getTokens(strKeyword);
			if(tokens!=null && tokens.size()>0){
				int numOfTokens = tokens.size();
				for(int currentTokensIndex=0;currentTokensIndex<numOfTokens;currentTokensIndex++){
					String currentToken = tokens.get(currentTokensIndex);
					if(currentToken!=null)
						sourceKeyArg = getTokenValue(strKeyword,currentToken,valueProvider,sourceKeyArg); 
				}
			}
		}
		resultString = sourceKeyArg;
		return resultString;
	}
	private static String getTokenValue( String strKeyword ,String currentToken,ValueProvider valueProvider,String sourceKeyArg) {
		String resultString = sourceKeyArg;
		String [] tokenArguments = getTokenArguments(currentToken,strKeyword);
		if(tokenArguments!=null && tokenArguments.length>0){
			String operation = tokenArguments[0];
			String[] operationArguments = getArgument(tokenArguments);
			int numOfOptArg = operationArguments.length;
			
			if(RESPLACE_FIRST.equalsIgnoreCase(operation)){
				if(sourceKeyArg!=null){
					if(numOfOptArg == 1){
						String firstArg =operationArguments[0];
						if(isLiteral(firstArg)){
							firstArg = getValue(firstArg);
						}else {
							firstArg = valueProvider.getStringValue(firstArg);
						}
						if(firstArg!=null)
							resultString = sourceKeyArg.replaceFirst(firstArg, "");
					}else if(numOfOptArg>=2) {
						
						String firstArg =operationArguments[0];
						String secondArg =operationArguments[1];
						if(isLiteral(firstArg)){
							firstArg = getValue(firstArg);
						}else {
							firstArg = valueProvider.getStringValue(firstArg);
						}
						
						if(isLiteral(secondArg)){
							secondArg = getValue(secondArg);
						}else {
							secondArg = valueProvider.getStringValue(secondArg);
						}
						if(firstArg!=null && secondArg!=null)
							resultString = sourceKeyArg.replaceFirst(firstArg, secondArg);
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Can't perform operation : "+RESPLACE_FIRST+" on String :"+sourceKeyArg+" Reason : Regex not configured");
					}
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No String found to perform "+RESPLACE_FIRST+" Operation during evaluating keyword :"+strKeyword);
				}
			}else if (RESPLACE_ALL.equalsIgnoreCase(operation)) {
				if(sourceKeyArg!=null){
					if(numOfOptArg == 1){
						String firstArg =operationArguments[0];
						if(isLiteral(firstArg)){
							firstArg = getValue(firstArg);
						}else {
							firstArg = valueProvider.getStringValue(firstArg);
						}
						if(firstArg!=null)
							resultString = sourceKeyArg.replaceAll(firstArg, "");
					}else if(numOfOptArg>=2) {
						
						String firstArg =operationArguments[0];
						String secondArg =operationArguments[1];
						if(isLiteral(firstArg)){
							firstArg = getValue(firstArg);
						}else {
							firstArg = valueProvider.getStringValue(firstArg);
						}
						
						if(isLiteral(secondArg)){
							secondArg = getValue(secondArg);
						}else {
							secondArg = valueProvider.getStringValue(secondArg);
						}
						if(firstArg!=null && secondArg!=null)
							resultString = sourceKeyArg.replaceAll(firstArg, secondArg);
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Can't perform operation : "+RESPLACE_ALL+" on String :"+sourceKeyArg+" Reason : Regex not configured");
					}
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No String found to perform "+RESPLACE_ALL+" Operation during evaluating keyword :"+strKeyword);
				}
			}else if (CONCAT.equalsIgnoreCase(operation)) {
				String tempString ="";

				for(int i=0;i<numOfOptArg;i++){
					String argument = operationArguments[i];
					if(isLiteral(argument)){
						argument = getValue(argument);
						tempString = tempString+argument;
					}else {
						argument = valueProvider.getStringValue(argument) ;
						if(argument!=null ){
							tempString = tempString+argument;
						}
					}
				}
				String souceKey = sourceKeyArg;
				if(souceKey!=null){
					tempString = souceKey+tempString;
				}
				if(tempString.length()>0){
					resultString = tempString;
				}
			}else if (SUB_STRING.equalsIgnoreCase(operation)) {
				try{
					if(sourceKeyArg!=null){
						if(numOfOptArg == 1){
							String firstArg =operationArguments[0];
							if(isLiteral(firstArg)){
								firstArg = getValue(firstArg);
							}else {
								firstArg = valueProvider.getStringValue(firstArg);
							}
							if(firstArg!=null)
								resultString = sourceKeyArg.substring(Integer.parseInt(firstArg));
						}else if(numOfOptArg>=2){
							String firstArg =operationArguments[0];
							String secondArg =operationArguments[1];
							if(isLiteral(firstArg)){
								firstArg = getValue(firstArg);
							}else {
								firstArg = valueProvider.getStringValue(firstArg);
							}
							
							if(isLiteral(secondArg)){
								secondArg = getValue(secondArg);
							}else {
								secondArg = valueProvider.getStringValue(secondArg);
							}
							if(firstArg!=null && secondArg!=null)
								resultString = sourceKeyArg.substring(Integer.parseInt(firstArg), Integer.parseInt(secondArg));
						}else {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Can't perform operation : "+SUB_STRING+" on String :"+sourceKeyArg+" Reason : BeginIndex not configured");
						}		
					
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "No String found to perform "+SUB_STRING+" Operation during evaluating keyword :"+strKeyword);
					}

				}catch (NumberFormatException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Can't perform operation : "+SUB_STRING+" on String :"+sourceKeyArg+" Reason :"+e.getMessage());
				}catch (IndexOutOfBoundsException  e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Can't perform operation : "+SUB_STRING+" on String :"+sourceKeyArg+" Reason :"+e.getMessage());
				}
			}else if (TO_LOWER_CASE.equalsIgnoreCase(operation)) {
				if(sourceKeyArg!=null){
					resultString = sourceKeyArg.toLowerCase();
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No String found to perform "+TO_LOWER_CASE+" Operation during evaluating keyword :"+strKeyword);
				}
			}else if (TO_UPPER_CASE.equalsIgnoreCase(operation)) {
				if(sourceKeyArg!=null){
					resultString = sourceKeyArg.toUpperCase();
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No String found to perform "+TO_UPPER_CASE+" Operation during evaluating keyword :"+strKeyword);
				}
			}else if (TRIM.equalsIgnoreCase(operation)) {
				if(sourceKeyArg!=null){
					resultString = sourceKeyArg.trim();
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No String found to perform "+TRIM+" Operation during evaluating keyword :"+strKeyword);
				}
			}else if (STRIP_PREFIX.equalsIgnoreCase(operation) || STRIP_SUFFIX.equalsIgnoreCase(operation)) {
				if(sourceKeyArg!=null){
					if(numOfOptArg>=1){
						resultString = stripAttributeValue(operation,numOfOptArg, operationArguments,sourceKeyArg,valueProvider);
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Can't perform "+operation+" on String :"+sourceKeyArg+" Reason : Separator not configured");
					}
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No String found to perform "+operation+" Operation during evaluating keyword :"+strKeyword);
				}	
			}
		}
		
		return resultString;
	}
	
	private static String  stripAttributeValue(String operation,int numOfOptArg,String[] arguments, String sourceKeyArg,ValueProvider valueProvider) {
		String resultString =sourceKeyArg;

		String separator = arguments[0];
		if(isLiteral(separator)){
			separator = getValue(separator);
		}else {
			separator  = valueProvider.getStringValue(separator);
		}
		if(separator!=null && separator.length()>0 ){
			if(sourceKeyArg.contains(separator)){
				String strPosition = "1";
				String includeIdentStr = "0";
				if(numOfOptArg>=3){
					strPosition = arguments[1];
					includeIdentStr = arguments[2];
					
					if(isLiteral(strPosition)){
						strPosition = getValue(strPosition);
					}else {
						String tempPosition = valueProvider.getStringValue(strPosition);
						if(tempPosition!=null){
							strPosition = tempPosition;
						}
					}
					
					if(isLiteral(includeIdentStr)){
						includeIdentStr = getValue(includeIdentStr);
					}else {
						String tempIncludeIdentStr = valueProvider.getStringValue(includeIdentStr);
						if(tempIncludeIdentStr!=null){
							includeIdentStr = tempIncludeIdentStr;
						}
					}
				}else if (numOfOptArg>=2) {
					strPosition = arguments[1];
					if(isLiteral(strPosition)){
						strPosition = getValue(strPosition);
					}else {
						String tempPosition = valueProvider.getStringValue(strPosition);
						if(tempPosition!=null){
							strPosition = tempPosition;
						}
					}
				}
				
			
				
				int position = 1;
				int includeIdent = 0;
				try{
					int tempPosition = Integer.parseInt(strPosition);
					if(!(tempPosition<1))
						position = tempPosition;
				}catch (NumberFormatException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Invalid separator position : "+position+" configured , using default position "+position);
				}
				
				try{
					includeIdent = Integer.parseInt(includeIdentStr);
				}catch (NumberFormatException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Separator will be add to Stripped value , Reason : Invalid value "+includeIdentStr+ "for whether to include separator or not ");
				}
				
				
				String tempResultStr="";

				int index = sourceKeyArg.indexOf(separator);
				
				for(int l=0;l<position;l++){
					index = sourceKeyArg.indexOf(separator, index);
					if(index==-1)
						break;
					if(l<position-1)
						index = index+separator.length();
				}
				
				
				if(index!=-1){
					
					if(STRIP_SUFFIX.equalsIgnoreCase(operation)){
						tempResultStr = sourceKeyArg.substring(0, index);
						if(includeIdent!=0){
							tempResultStr = tempResultStr+separator;
						}
						
					}else {
						tempResultStr = sourceKeyArg.substring(index+separator.length());
						if(includeIdent!=0){
							tempResultStr = separator+tempResultStr;
						}
					}
				}else {
					tempResultStr = sourceKeyArg;
				}
				resultString = tempResultStr;
			}	
			
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Can't perform "+operation+" on String :"+sourceKeyArg+" Reason : Separator not found");
		}	
		
		return resultString;
	}
}
