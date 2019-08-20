package com.elitecore.core.commons.util;

import java.text.DecimalFormat;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;

public class MathOperationUtility extends TranslationOperationUtility {

	private static final String MODULE = "MATH-OP";
	private static final String ADDITION = "add";
	private static final String SUBTRACTION = "sub";
	private static final String MULTIPLICATION = "mul";
	private static final String DIVISION = "div";
	private static final String PERCENTAGE = "per";
	private static final String MODULO = "mod";
	private static final String POWER = "pow";
	private static final String LOGE = "loge";
	private static final String LOG10 = "log10";
	public static final String MATHOP = "${MATHOP}";
	
	private static final DecimalFormatThreadLocal decimalFormat = new DecimalFormatThreadLocal();
	
	private static class DecimalFormatThreadLocal extends ThreadLocal<DecimalFormat> {
		@Override
		protected DecimalFormat initialValue() {
			return new DecimalFormat("#.##");
		}
	}
	
	public static String getValue(String strKeyword, ValueProvider valueProvider,String sourceKeyArg) {
		if(strKeyword!=null && valueProvider!=null && MATHOP.equalsIgnoreCase(getKeywordName(strKeyword))){
			List<String> tokens = getTokens(strKeyword);
			if(tokens!=null){
				for(String currentToken: tokens){
					if(currentToken!=null)
						sourceKeyArg = getTokenValue(strKeyword,currentToken,valueProvider,sourceKeyArg); 
				}
			}
		}
		return sourceKeyArg;
	}
	
	
	private static String getTokenValue( String strKeyword ,String currentToken,ValueProvider valueProvider,String sourceKeyArg) {
		String resultString = sourceKeyArg;
		String [] tokenArguments = getTokenArguments(currentToken,strKeyword);
		
		DecimalFormat formatter = decimalFormat.get();
		
		if(tokenArguments!=null && tokenArguments.length>0){
			String operation = tokenArguments[0];
			String[] operationArguments = getArgument(tokenArguments);
			int numOfOptArg = operationArguments.length;
			
			if(ADDITION.equalsIgnoreCase(operation)){
				if (operationArguments != null && numOfOptArg > 0){
					try {
						String strOper1 = null;
						if (!isLiteral(operationArguments[0])){
							strOper1 = valueProvider.getStringValue(operationArguments[0]);
						} else {
							strOper1 = getValue(operationArguments[0]);
						}
						
						if (strOper1 != null && strOper1.trim().length() > 0 && sourceKeyArg!= null && sourceKeyArg.trim().length() > 0 ){
							double oper1 = Double.parseDouble(strOper1);
							double oper2 = Double.parseDouble(sourceKeyArg);
							double result = oper1 + oper2;
							resultString = formatter.format(result);

						}
					} catch (NumberFormatException e){
						if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, "Could not execute Add operation on " + operationArguments[0] + " and " + sourceKeyArg);
						}
					}
				}
			} else if(SUBTRACTION.equalsIgnoreCase(operation)){
				try {
					String strOper1 = null;
					if (!isLiteral(operationArguments[0])){
						strOper1 = valueProvider.getStringValue(operationArguments[0]);
					} else {
						strOper1 = getValue(operationArguments[0]);
					}
					if (strOper1 != null && strOper1.trim().length() > 0 && sourceKeyArg!= null && sourceKeyArg.trim().length() > 0 ){
						double oper1 = Double.parseDouble(strOper1);
						double oper2 = Double.parseDouble(sourceKeyArg);
						double result = oper2 - oper1;
						resultString = formatter.format(result);
					}
				} catch (NumberFormatException e){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Could not execute Subtraction operation on " + operationArguments[0] + " and " + sourceKeyArg);
					}
				}
			} else if(DIVISION.equalsIgnoreCase(operation)){
				try {
					String strOper1 = null;
					if (!isLiteral(operationArguments[0])){
						strOper1 = valueProvider.getStringValue(operationArguments[0]);
					} else {
						strOper1 = getValue(operationArguments[0]);
					}
					if (strOper1 != null && strOper1.trim().length() > 0 && sourceKeyArg!= null && sourceKeyArg.trim().length() > 0 ){
						double result;
						double oper1 = Double.parseDouble(strOper1);
						double oper2 = Double.parseDouble(sourceKeyArg);
						if (oper1 != 0) { //NOSONAR - Reason: Floating point numbers should not be tested for equality
							result = oper2 / oper1;
							resultString = formatter.format(result);
							/*
							 * below code converts the result to integer form,
							 * (truncating the floating point ) 
							 * if both operands are integers  
							 */
							int indexOfDecimal = resultString.indexOf(".");
							if (indexOfDecimal > 0 && strOper1.indexOf(".") == -1 && sourceKeyArg.indexOf(".") == -1){
								resultString = resultString.substring(0, indexOfDecimal);
							}							
						} else { 
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().info(MODULE, "Not executing Division operation because second operator is zero");
							}
						}
					}
				} catch (NumberFormatException e){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Could not execute Division operation on " + operationArguments[0] + " and " + sourceKeyArg);
					}
				}
			} else if(MULTIPLICATION.equalsIgnoreCase(operation)){
				try {
					String strOper1 = null;
					if (!isLiteral(operationArguments[0])){
						strOper1 = valueProvider.getStringValue(operationArguments[0]);
					} else {
						strOper1 = getValue(operationArguments[0]);
					}
					if (strOper1 != null && strOper1.trim().length() > 0 && sourceKeyArg!= null && sourceKeyArg.trim().length() > 0 ){
						double result;
						double oper1 = Double.parseDouble(strOper1);
						double oper2 = Double.parseDouble(sourceKeyArg);
						result = oper1 * oper2;
						resultString = formatter.format(result);
					}
				} catch (NumberFormatException e){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Could not execute Multiplication operation on " + operationArguments[0] + " and " + sourceKeyArg);
					}
				}
			} else if(PERCENTAGE.equalsIgnoreCase(operation)){
				try {
					String strOper1 = null;
					if (!isLiteral(operationArguments[0])){
						strOper1 = valueProvider.getStringValue(operationArguments[0]);
					} else {
						strOper1 = getValue(operationArguments[0]);
					}
					if (strOper1 != null && strOper1.trim().length() > 0 && sourceKeyArg!= null && sourceKeyArg.trim().length() > 0 ){
						double result;
						double oper1 = Double.parseDouble(strOper1);
						double oper2 = Double.parseDouble(sourceKeyArg);
						result = (double) (oper2 * ( oper1 / 100));
						resultString = formatter.format(result);
						/*
						 * below code converts the result to integer form,
						 * (truncating the floating point ) 
						 * if both operands are integers  
						 */
						int indexOfDecimal = resultString.indexOf(".");
						if (indexOfDecimal > 0 && strOper1.indexOf(".") == -1 && sourceKeyArg.indexOf(".") == -1){
							resultString = resultString.substring(0, indexOfDecimal);
						}
					}
				} catch (NumberFormatException e){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Could not execute percentage operation on " + operationArguments[0] + " and " + sourceKeyArg);
					}
				}
			} else if (MODULO.equalsIgnoreCase(operation)) {
				try {
					String strOper1 = null;
					if (!isLiteral(operationArguments[0])){
						strOper1 = valueProvider.getStringValue(operationArguments[0]);
					} else {
						strOper1 = getValue(operationArguments[0]);
					}
					if (Strings.isNullOrBlank(strOper1) == false && 
							Strings.isNullOrBlank(sourceKeyArg) == false) {
						
						double oper1 = Double.parseDouble(strOper1);
						
						if (oper1 != 0) { //NOSONAR - Reason: Floating point numbers should not be tested for equality 

							double oper2 = Double.parseDouble(sourceKeyArg);
							double result = oper2 % oper1;
							resultString = formatter.format(result);
						}
					}
				} catch (NumberFormatException e){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Could not execute " + MODULO 
								+ " operation on " + operationArguments[0] 
								+ " and " + sourceKeyArg);
					}
				}
			} else if (POWER.equalsIgnoreCase(operation)) {
				try {
					String strOper1 = null;
					if (!isLiteral(operationArguments[0])){
						strOper1 = valueProvider.getStringValue(operationArguments[0]);
					} else {
						strOper1 = getValue(operationArguments[0]);
					}
					if (Strings.isNullOrBlank(strOper1) == false && 
							Strings.isNullOrBlank(sourceKeyArg) == false){
						
						double oper1 = Double.parseDouble(strOper1);
						double oper2 = Double.parseDouble(sourceKeyArg);
						double result = Math.pow(oper2, oper1);
						resultString = formatter.format(result);
					}
				} catch (NumberFormatException e){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Could not execute " + POWER 
								+ " operation on " + operationArguments[0] 
								+ " and " + sourceKeyArg);
					} 
				}
			} else if (LOGE.equalsIgnoreCase(operation)) {
				try {
					if (Strings.isNullOrBlank(sourceKeyArg) == false){
						
						double operand = Double.parseDouble(sourceKeyArg);
						
						if(operand > 0) {
							double result = Math.log(operand);
							resultString = formatter.format(result);
						}
					}
				} catch (NumberFormatException e){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Could not execute " + LOGE 
								+ " operation on " + sourceKeyArg);
					} 
				}
			} else if (LOG10.equalsIgnoreCase(operation)) {
				try {
					if (Strings.isNullOrBlank(sourceKeyArg) == false){
						
						double operand = Double.parseDouble(sourceKeyArg);
						if (operand > 0) {
							double result = Math.log10(operand);
							resultString = formatter.format(result);
						}
					}
				} catch (NumberFormatException e){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Could not execute " + LOG10 
								+ " operation on " + sourceKeyArg);
					} 
				}
			} 
		}
		return resultString;
	}
}