package com.elitecore.core.util.cli.parser;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.util.cli.CLIUtility;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;

/**
 * A Static Parser that will parse Arguments passed to cli.
 * 
 * @author monica.lulla
 *
 */
public class CLIArgumentsParser {

	private static final String COMMAND_START = "{";
	private static final String COMMAND_END = "}";
	/**
	 *
	 * @param cliArgs 
	 * @return {@link CLIArguments}
	 * 
	 * Note: if -h host:port1 and -p port2 both are passed --> last encountered port will be selected.
	 * 
	 * @throws ParserException on any Invalid Parsing Condition/Argument.
	 */
	public static CLIArguments parse(String [] cliArgs) throws ParserException{

		List<String> commandList = new ArrayList<String>();
		String host = null;
		int port = -1;
		FileWriter fileWriter = null;

		for(int index = 0; index < cliArgs.length; index++){
			CLIArgumentOptions cliArgOption = CLIArgumentOptions.formCLIArgumentOption(cliArgs[index]);
			if(cliArgOption == null){
				throw new ParserException("option `" + cliArgs[index] + "' not recognized.");
			}

			switch (cliArgOption) {
			case HOST:
				index++;
				if(isArgumentAvailable(cliArgs, index)){
					throw new ParserException("option requires an argument -- '" 
							+ CLIArgumentOptions.HOST.name + "'");
				}
				try {
					URLData urlData = URLParser.parse(cliArgs[index]);
					host = urlData.getHost();

					if(urlData.getPort() > 0){
						port = urlData.getPort();
					}
				} catch (InvalidURLException e) {
					throw new ParserException(e.getMessage());
				}

				break;
			case PORT:
				index++;
				if(isArgumentAvailable(cliArgs, index)){
					throw new ParserException("option requires an argument -- '" 
							+ CLIArgumentOptions.PORT.name + "'");
				}
				try{
					port = Integer.parseInt(cliArgs[index]);
				}catch (NumberFormatException e) {
					throw new ParserException("Invalid port " + cliArgs[index]);
				}
				break;
			case COMMAND:

				//As these tokens come from CMD these are space separated.
				//Acceptable Command String --> {abc pqr xyz}  OR { abc pqr xyz }  OR abc pqr xyz 
				index++;
				if(isArgumentAvailable(cliArgs, index)){
					throw new ParserException("option requires an argument -- '" 
							+ CLIArgumentOptions.COMMAND.name + "'");
				}
				StringBuffer fullCommandArg = new StringBuffer();
				String commandArg = cliArgs[index];
				//Process token Starting with { token --> {   OR   {abc 
				if(commandArg.startsWith(COMMAND_START)){

					if(commandArg.length() > 1){
						//Take token --> {abc and Strip token --> abc
						cliArgs[index] = commandArg.substring(1);
					}else{
						//Skip token --> {
						index++;
						if(isArgumentAvailable(cliArgs, index)){
							throw new ParserException("option requires an argument -- '" 
									+ CLIArgumentOptions.COMMAND.name + "'");
						}
					}

					while(true){
						//get next token
						commandArg = cliArgs[index];
						//Process last token --> xyz} OR  }
						if(commandArg.endsWith(COMMAND_END)){
							if(commandArg.length() > 1){
								//Strip } and take token --> xyz
								fullCommandArg.append(commandArg.substring(0, commandArg.length() -1) + " ");
							}
							break;
						}
						//Take token --> pqr
						fullCommandArg.append(commandArg + " ");
						index++;
						if(isArgumentAvailable(cliArgs, index)){
							throw new ParserException("incomplete argument for -- '" 
									+ CLIArgumentOptions.COMMAND.name + "'");
						}
					}
				}else{
					//Process all tokens upto end
					while(index < cliArgs.length){
						fullCommandArg.append(cliArgs[index] + " ");
						index++;
					}
				}
				commandArg = fullCommandArg.toString().trim();
				if(commandArg.length() > 0)
					commandList.add(commandArg);
				break;
			case INPUT_FILE:

				index++;
				if(isArgumentAvailable(cliArgs, index)){
					throw new ParserException(CLIArgumentOptions.INPUT_FILE.name 
							+ ": missing file operand");
				}

				String inputFile = cliArgs[index];
				try {
					commandList.addAll(CLIUtility.readCommandsFromFile(inputFile));
				} catch (FileNotFoundException e) {
					throw new ParserException(CLIArgumentOptions.INPUT_FILE.name + 
							": cannot execute: " + e.getMessage());
				} catch (IOException e) {
					throw new ParserException(CLIArgumentOptions.INPUT_FILE.name + 
							": unable to read ‘" + inputFile + "’");
				}

				break;
			case OUTPUT:

				index++;
				if(isArgumentAvailable(cliArgs, index)){
					throw new ParserException(CLIArgumentOptions.OUTPUT.name 
							+ ": missing file operand");
				}
				try {
					fileWriter = CLIUtility.getOutputFile(cliArgs[index]);
				} catch (IOException e) {
					throw new ParserException(CLIArgumentOptions.OUTPUT.name + 
							": unable to write : " + e.getMessage());
				}
				break;
			default:
				// Option cliArgs[i] is recognized but not handled
				throw new ParserException("option `" + cliArgs[index] + "' not supported.");
			}
		}		
		return new CLIArguments(host, port, commandList, fileWriter);
	}
	private static boolean isArgumentAvailable(String[] cliArgs, int index) {
		return index >= cliArgs.length;
	}
	
	private enum CLIArgumentOptions {
		
		HOST("-h"),
		PORT("-p"),
		COMMAND("-c"),
		INPUT_FILE("-f"),
		OUTPUT("-o");

		public String name;
		private CLIArgumentOptions(String name) {
			this.name = name;
		}

		/**
		 * get CLIArgCommandTypes with the help of its String value
		 * 
		 * @param CLIArgCommandTypesStr is String value of CLIArgCommandTypes
		 * @return CLIArgCommandTypes
		 */
		public static CLIArgumentOptions formCLIArgumentOption(String CLIArgCommandTypesStr) {
			if(HOST.name.equals(CLIArgCommandTypesStr)){
				return CLIArgumentOptions.HOST;
			}else if(PORT.name.equals(CLIArgCommandTypesStr)){
				return CLIArgumentOptions.PORT;
			}else if(COMMAND.name.equals(CLIArgCommandTypesStr)){
				return CLIArgumentOptions.COMMAND;
			}else if(INPUT_FILE.name.equals(CLIArgCommandTypesStr)){
				return CLIArgumentOptions.INPUT_FILE;
			}else if(OUTPUT.name.equals(CLIArgCommandTypesStr)){
				return CLIArgumentOptions.OUTPUT;
			}
			return null;
		}
	}

}
