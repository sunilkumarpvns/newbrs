package com.elitecore.netvertex.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.exprlib.parser.exception.InvalidSymbolException;
import com.elitecore.exprlib.scanner.Scanner;
import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.scanner.impl.ScannerImpl;
import com.elitecore.netvertex.core.bisummary.BICEASummary;
import com.elitecore.netvertex.core.util.exception.TagParsingException;
import com.elitecore.netvertex.core.util.exception.TagValueNotFoundException;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

/**
 * 
 * @author Manjil Purohit
 *
 */
public class TagProcessor {

	private static final String MODULE = "TAG-PROCR";
	public static final String NOT_AVAILABLE = "N/A";
	private Scanner scanner;
	
	public TagProcessor() {
		scanner = new ScannerImpl() {
			@Override
			protected boolean isOperator(char ch) {
				return (ch == '{' || ch == '}');
			}
			
			@Override
			protected boolean isWhitespace(char ch) {
				return false;
			}
			
		};
	}
	
	public String getTemplate(String data, PCRFResponse pcrfResponse) throws TagValueNotFoundException, TagParsingException {
		return getTemplateData(getTemplateStrings(data), pcrfResponse);
	}
	
	private String getTemplateData(List<TemplateString> templateStrings,  PCRFResponse pcrfResponse) throws TagValueNotFoundException {
		StringBuilder stringBuilder = new StringBuilder();
		String value = null;
		for(TemplateString token : templateStrings) {
			if(token.isKey()) {
				if((value = getValue(token.getString(), pcrfResponse)) != null){
					stringBuilder.append(value);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Using default value: "+ NOT_AVAILABLE +". Reason: value not found for tag " + token);
					
					stringBuilder.append(NOT_AVAILABLE);
				}
			} else {
				stringBuilder.append(token.getString());
			}
				
		}
		return stringBuilder.toString();
	}
	
	private List<TemplateString> getTemplateStrings(String template) throws TagParsingException {
		
		if(template == null){
			throw new TagParsingException("Error while parsing template. Reason: Template is null");
		}
			
			
		List<TemplateString> templateStrings = new ArrayList<TemplateString>();
		try {
			List<Symbol> symbols = scanner.getSymbols(template);
			String symbol;
			for(int i=0; i<symbols.size(); i++) {
				symbol = symbols.get(i).getName();
					
				if(symbol.equals("{")) {
					if(i+1 == symbols.size() || i+2 == symbols.size())
						throw new TagParsingException("Error while parsing template. Reason: Tag not properly ended");
					String key = symbols.get(++i).getName().trim();
					String closingBrace = symbols.get(++i).getName();
					if(key.equals("{") || key.equals("}") || !closingBrace.equals("}")) 
						throw new TagParsingException("Error while parsing template. Reason: Tag not properly defined");		
					templateStrings.add(new TemplateString(key,true));
				} else if(symbol.equals("}")) {
					throw new TagParsingException("Error while parsing template. Reason: Tag not properly started");
				} else 	
					templateStrings.add(new TemplateString(symbol, false));	
			}
		} catch (InvalidSymbolException e) {
		}
		return templateStrings;
	}
	
	private class TemplateString {
		private boolean isKey;
		private String string;
		
		private TemplateString(String string, boolean isKey) {
			this.string = string;
			this.isKey = isKey;
		}
		
		public boolean isKey() {
			return isKey;
		}
		
		public String getString() {
			return string;
		}
		
		@Override
		public String toString() {
			StringWriter stringBuffer = new StringWriter();
			PrintWriter out = new PrintWriter(stringBuffer);
			out.println();
			out.println("           Key = " + string);
			out.println("           Is Key = " + isKey);
			out.close();
			return stringBuffer.toString();
		}
	}
	
	private String getValue(String identifier, PCRFResponse response) {
		if(response == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to get value for tag:"+ identifier +". Reason: PCRFRespose provide null");
			return null;
		}
			
		String stringValue = null;
		    	 stringValue=response.getAttribute(identifier);
		     if(stringValue==null)
		    	 stringValue =  BICEASummary.getInstance().getValue(identifier);
		     
		return stringValue;
		
	}	

}
