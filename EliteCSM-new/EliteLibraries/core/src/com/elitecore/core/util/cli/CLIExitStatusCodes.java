package com.elitecore.core.util.cli;

public enum CLIExitStatusCodes {

	EXECUTED_SUCCESSFULLY(0),
	QUIT(0),
	INVALID_HOME_PATH(64),
	ILLEGAL_CLI_ARGUMENT(65),
	COMMAND_EXECUTION_ERROR(66), 
	FILE_ERROR(67), 
	IO_ERROR(68), 
	;
	
	public final int exitStatusCode;
	private CLIExitStatusCodes(int exitStatusCode) {
		this.exitStatusCode = exitStatusCode;
	}
}
