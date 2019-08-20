package com.elitecore.exprlib.compiler;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.exprlib.parser.Parser;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidSymbolException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.FunctionExpression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.impl.FunctionAbs;
import com.elitecore.exprlib.parser.expression.impl.FunctionAdd;
import com.elitecore.exprlib.parser.expression.impl.FunctionCeil;
import com.elitecore.exprlib.parser.expression.impl.FunctionConcat;
import com.elitecore.exprlib.parser.expression.impl.FunctionCurrentTimestamp;
import com.elitecore.exprlib.parser.expression.impl.FunctionDateFormat;
import com.elitecore.exprlib.parser.expression.impl.FunctionDecrypt;
import com.elitecore.exprlib.parser.expression.impl.FunctionDivision;
import com.elitecore.exprlib.parser.expression.impl.FunctionEncrypt;
import com.elitecore.exprlib.parser.expression.impl.FunctionFloor;
import com.elitecore.exprlib.parser.expression.impl.FunctionGetDateDifference;
import com.elitecore.exprlib.parser.expression.impl.FunctionGetFirst;
import com.elitecore.exprlib.parser.expression.impl.FunctionGetHour;
import com.elitecore.exprlib.parser.expression.impl.FunctionGetLast;
import com.elitecore.exprlib.parser.expression.impl.FunctionGetMonth;
import com.elitecore.exprlib.parser.expression.impl.FunctionGetWeekDay;
import com.elitecore.exprlib.parser.expression.impl.FunctionGetYear;
import com.elitecore.exprlib.parser.expression.impl.FunctionIsTimeBetween;
import com.elitecore.exprlib.parser.expression.impl.FunctionLength;
import com.elitecore.exprlib.parser.expression.impl.FunctionLog10;
import com.elitecore.exprlib.parser.expression.impl.FunctionLoge;
import com.elitecore.exprlib.parser.expression.impl.FunctionLowerCase;
import com.elitecore.exprlib.parser.expression.impl.FunctionMac2Tgpp;
import com.elitecore.exprlib.parser.expression.impl.FunctionMax;
import com.elitecore.exprlib.parser.expression.impl.FunctionMin;
import com.elitecore.exprlib.parser.expression.impl.FunctionModulo;
import com.elitecore.exprlib.parser.expression.impl.FunctionMultiply;
import com.elitecore.exprlib.parser.expression.impl.FunctionNetMatch;
import com.elitecore.exprlib.parser.expression.impl.FunctionNow;
import com.elitecore.exprlib.parser.expression.impl.FunctionPercentage;
import com.elitecore.exprlib.parser.expression.impl.FunctionPower;
import com.elitecore.exprlib.parser.expression.impl.FunctionRandom;
import com.elitecore.exprlib.parser.expression.impl.FunctionReplace;
import com.elitecore.exprlib.parser.expression.impl.FunctionReplaceAll;
import com.elitecore.exprlib.parser.expression.impl.FunctionReplaceFirst;
import com.elitecore.exprlib.parser.expression.impl.FunctionRound;
import com.elitecore.exprlib.parser.expression.impl.FunctionStrip;
import com.elitecore.exprlib.parser.expression.impl.FunctionSubString;
import com.elitecore.exprlib.parser.expression.impl.FunctionSubtract;
import com.elitecore.exprlib.parser.expression.impl.FunctionTrim;
import com.elitecore.exprlib.parser.expression.impl.FunctionUpperCase;
import com.elitecore.exprlib.parser.impl.OperatorPrecedenceParserImpl;
import com.elitecore.exprlib.scanner.Scanner;
import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.scanner.impl.ScannerImpl;

public class Compiler{
	private Scanner scanner;
	private Parser parser;
	private static Compiler compiler;
	private static Map<String, File> encryptionKeyFileMap;
	
	static {
		compiler = new Compiler();
		compiler.addFunctions();
	}
	
	Compiler() {
		scanner = new ScannerImpl();
		parser = new OperatorPrecedenceParserImpl();
		encryptionKeyFileMap = new ConcurrentHashMap<String, File>(1);
	}
	
	private void addFunctions() {
		addStringFunction();
		addIntegerFunction();
	}

	private void addStringFunction(){
		addFunction(new FunctionTrim());
		addFunction(new FunctionIsTimeBetween());
		addFunction(new FunctionConcat());
		addFunction(new FunctionDateFormat());
		addFunction(new FunctionNow());
		addFunction(new FunctionStrip());
		addFunction(new FunctionReplace());
		addFunction(new FunctionReplaceFirst());
		addFunction(new FunctionReplaceAll());
		addFunction(new FunctionSubString());
		addFunction(new FunctionUpperCase());
		addFunction(new FunctionLowerCase());
		addFunction(new FunctionNetMatch());
		addFunction(new FunctionMac2Tgpp());
		addFunction(new FunctionEncrypt());
		addFunction(new FunctionDecrypt());
		addFunction(new FunctionCurrentTimestamp());
		addFunction(new FunctionGetFirst());
		addFunction(new FunctionGetLast());
	}
	
	private void addIntegerFunction(){
		addFunction(new FunctionAdd());
		addFunction(new FunctionGetWeekDay());
		addFunction(new FunctionGetHour());
		addFunction(new FunctionGetMonth());
		addFunction(new FunctionGetYear());
		addFunction(new FunctionGetDateDifference());
		addFunction(new FunctionAbs());
		addFunction(new FunctionCeil());
		addFunction(new FunctionFloor());
		addFunction(new FunctionRound());
		addFunction(new FunctionMax());
		addFunction(new FunctionMin());
		addFunction(new FunctionRandom());
		addFunction(new FunctionLength());
		addFunction(new FunctionSubtract());
		addFunction(new FunctionMultiply());
		addFunction(new FunctionDivision());
		addFunction(new FunctionPercentage());
		addFunction(new FunctionModulo());
		addFunction(new FunctionLog10());
		addFunction(new FunctionLoge());
		addFunction(new FunctionPower());
	}
	
	public static Compiler getDefaultCompiler(){
		return compiler;
	}
	
	public  LogicalExpression parseLogicalExpression(String expression)throws InvalidExpressionException{
		try{
			List<Symbol> symbols = scanner.getSymbols(expression);
			return (LogicalExpression) parser.parse(symbols);
		}catch(ClassCastException e ){
			throw new InvalidExpressionException("Invalid Expression: " + expression 
					+ ", Reason: " + e.getMessage(),e);
		}catch(InvalidSymbolException e){
			throw new InvalidExpressionException("Invalid Expression: " + expression 
					+ ", Reason: " + e.getMessage(),e);
		}
	}
	
	public  Expression parseExpression(String expression)throws InvalidExpressionException{
		try{
			List<Symbol> symbols = scanner.getSymbols(expression);
			return  parser.parse(symbols);
		}catch(Exception e){
			throw new InvalidExpressionException("Invalid Expression: " + expression 
					+ ", Reason: " + e.getMessage(),e);
		}
	}
	
	public void addFunction(FunctionExpression expression){
		scanner.addFunction(expression.getName());
		parser.addFunction(expression.getName(),expression);
	}
	
	public File getEncryptionKeyFile(String fileName){
		return encryptionKeyFileMap.get(fileName);
	}
	
	public void registerEncryptionKeyFile(File encryptionKeyFile){
		if(encryptionKeyFile != null && encryptionKeyFile.exists()){
			encryptionKeyFileMap.put(encryptionKeyFile.getName(), encryptionKeyFile);
		}
	}
}
