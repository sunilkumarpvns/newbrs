package com.elitecore.core.commons.utilx.cli.cmd;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.util.cli.cmd.ICommand;

public abstract class EliteBaseCommand implements ICommand {
	
	public final static String HELP_OPTION  = "-help";
	public final static String HELP     = "?";
	public final static String CSV = "-csv";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	
	public EliteBaseCommand(){
		
	}
	
	public final static String fillChar(String input, int length){
		return fillChar(input, length, ' ');
	}

	public final static String fillChar(String input, int length, char chr){
		
		if (input == null)
			input = "";

		StringBuilder stringBuffer = new StringBuilder();
		stringBuffer.append(input);
		for(int i = input.length(); i<length; i++){
			stringBuffer.append(chr);
		}
		return stringBuffer.toString();
	}

	protected final String fillCharBeforeString(String input, int length, char chr){
		
		if (input == null)
			input = "";

		StringBuilder stringBuffer = new StringBuilder();
		for(int i = input.length(); i<length; i++){
			stringBuffer.append(chr);
		}
		stringBuffer.append(input);
		return stringBuffer.toString();
	}
	protected final String fillCharBeforeString(String input, int length){
		return fillCharBeforeString(input, length, ' ');
	}
	
	protected final String convertDateToString(Date date) {
		if (date != null) {
			return sdf.format(date);
		}
		return "-";
	}
	
	protected final String[] parseArgumentString(String parameters){
		String[] params = null;
		
		if (parameters != null){
			params = ParserUtility.splitString(parameters, ' ');
			}
		return params;
	}	
	
	public String getHelpMsg() {
		return "";
	}

	/**
	 * checks whether the parameter is help option or not.
	 * 
	 * NOTE: Case insensitive check
	 * @param parameter The String to compare for help option
	 * @return true if parameter is help option, false if parameter is null or not help option
	 */	
	public static boolean isHelpParameter(String parameter) {
		return HELP_OPTION.equalsIgnoreCase(parameter) || HELP.equals(parameter);
	}
}
