package com.elitecore.core.util.cli.parser;

import java.io.FileWriter;
import java.util.List;
/**
 * This will provide you Commands passed in CLI Arguments. 
 * 
 * @author monica.lulla
 *
 */
public class CLIArguments {
	
	private String host;
	private int port = -1;
	private List<String> commandList;
	private FileWriter outputFileWriter;
	
	public CLIArguments(String host, int port,	
			List<String> commandList, FileWriter outputFileWriter) {
		this.host = host;
		this.port = port;
		if(commandList != null && !commandList.isEmpty()){
			this.commandList = commandList;
		}
		this.outputFileWriter = outputFileWriter;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public List<String> getCommandList() {
		return commandList;
	}
	
	public FileWriter getOutputFileWriter() {
		return outputFileWriter;
	}

	
}
