package com.elitecore.core.commons.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class TranslationOperationUtility {
	
	private static final String MODULE = "TRAN-OP-UTIL";

	private static final String OPERATION_ARGUMENT_SEPARATOR  = "-";
	private static final int END_OF_STRING = -1;

	public final static String[] getTokenArguments(String currentToken,String strKeyword) {

		int firstIndex = currentToken.indexOf("(");
		int lastIndex = currentToken.lastIndexOf(")");
		if(firstIndex!=-1 && lastIndex!=-1 && lastIndex>firstIndex){
			String tempString = currentToken.substring(firstIndex+1, lastIndex);
			return splitString(strKeyword,tempString);
		}
		return splitString(strKeyword,currentToken);

	}
	
	public final static String[] getArgument(String[] arguments) {
		String[] operationArguments = new String[arguments.length-1];
		int j=1;
		for(int i=0;i<operationArguments.length;i++){
			operationArguments[i] = arguments[j];
			j = j+1;
		}
		return operationArguments;
	}
	
	public final static String  getKeywordName(String strKeyword){
		String keywordName = null;
		 if(strKeyword!=null){
			 int index = -1;
			 index = strKeyword.indexOf("}");
			 if(index!=-1){
				 String tempString  = strKeyword.substring(0,index+1).trim();
				 index  = tempString.indexOf(OPERATION_ARGUMENT_SEPARATOR);
				 if(index!=-1){
					 keywordName = tempString.substring(0,index)+"}";
				 }else {
					 keywordName = tempString;
				}
			 }	 
		 }
		 return keywordName;
	}
	
	public final static List<String> getTokens(String strKeyword){
		int index = strKeyword.indexOf(OPERATION_ARGUMENT_SEPARATOR);
		List<String> list = null;
		if(index!=-1){
			list = new ArrayList<String>();
			String expression = strKeyword.substring(index+1,strKeyword.indexOf("}"));
			StringBuilder literal = new StringBuilder();
			StringReader stringReader = new StringReader(expression);
			
			int currentChar ;
			try {
				currentChar = stringReader.read();
				while(currentChar != END_OF_STRING){
					if((char)currentChar == '('){
						list.add(readToken(strKeyword,stringReader));
						currentChar = stringReader.read();

						while(currentChar!=END_OF_STRING && isWhitespace((char)currentChar)){
							currentChar = stringReader.read();
						}
						if(currentChar!=END_OF_STRING && (char)currentChar!=','){
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
								LogManager.getLogger().debug(MODULE, "Can't perform String operation : "+strKeyword+" Reason :\",\" missing in "+strKeyword);
						}
						return null;
					}
						
					}else {
						literal.append((char)currentChar);
					}
					currentChar = stringReader.read();
				}
				if(literal.toString().length()>0){
					list.add(literal.toString());
				}
			} catch (IOException e1) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Can't perform String operation : "+strKeyword+" Reason :"+e1.getMessage());
				}
				return null;
			}
		}
		return list;
	}
	
	
	private final static String readToken(String strKeyword,StringReader stringReader)throws IOException{
		int currentChar = END_OF_STRING;
		StringBuilder tokenReader = new StringBuilder();
		tokenReader.append('(');
		currentChar = stringReader.read();
		while((char)currentChar!=')' && currentChar != END_OF_STRING){
			tokenReader.append((char)currentChar);
			currentChar = stringReader.read();
		}
		if((char)currentChar == ')')
			tokenReader.append(')');
		return tokenReader.toString(); 
	}
	
	public final static boolean isLiteral(String val){
		if(val!=null && (("\"".equals(String.valueOf(val.charAt(0)))) && ("\"".equals(String.valueOf(val.charAt(val.length()-1))))))
			return true;
		else
			return false;
	}
	
	public final static String getValue(String val){
		return val.replaceFirst("\"", "").substring(0, val.length()-2);
	}
	
	private static String[]  splitString(String strKeyword,String strAttribute){
		if(strAttribute == null)
			return null;
		ArrayList<String> splittedExpression = new ArrayList<String>();
		StringBuilder currentSymbol = new StringBuilder();
		StringReader stringReader = new StringReader(strAttribute);

		int currentChar = END_OF_STRING;
		try {
			currentChar = stringReader.read();
			while(currentChar != END_OF_STRING){
				if((char)currentChar == '"'){
					splittedExpression.add(readLiteral(stringReader));
					currentChar = stringReader.read();
					if(currentChar!=END_OF_STRING && (char)currentChar!=','){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
							LogManager.getLogger().debug(MODULE, "Can't perform operation : "+strAttribute+" in keyword : "+strKeyword+" Reason :\",\" missing in "+strAttribute);
						}	
						return null;
					}
				}else if(currentChar == ',') {
					if(currentSymbol.toString().length()>0){
						splittedExpression.add(currentSymbol.toString());
						currentSymbol = new StringBuilder();
					}	
				}else {
					currentSymbol.append((char)currentChar);
				}
				currentChar = stringReader.read();
			}
		} catch (IOException e1) {
			LogManager.getLogger().debug(MODULE, "Can't perform operation : "+strAttribute+" in keyword : "+strKeyword+" Reason :\",\" missing in "+strAttribute);
			return null;
		}



		if(currentSymbol!=null && currentSymbol.toString().length()>0)
			splittedExpression.add(currentSymbol.toString());
		return splittedExpression.toArray(new String[splittedExpression.size()]);

	}
	private static String readLiteral(StringReader stringReader) throws IOException{
		int currentChar = END_OF_STRING;
		StringBuilder literal = new StringBuilder();
		literal.append('"');
		currentChar = stringReader.read();

		while((char)currentChar!='"' && currentChar != END_OF_STRING){
			literal.append((char)currentChar);
			currentChar = stringReader.read();
		}
		if((char)currentChar == '"')
			literal.append('"');
		return literal.toString(); 
	}
	private static boolean isWhitespace(char ch) {
        return (ch == ' ' || ch == '\t');
    }
}
