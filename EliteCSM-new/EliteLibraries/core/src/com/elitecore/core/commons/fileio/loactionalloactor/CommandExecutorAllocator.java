package com.elitecore.core.commons.fileio.loactionalloactor;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class CommandExecutorAllocator extends BaseCommonFileAllocator {
	
	public static final String MODULE = "SCRIPT_EXECUTOR_ALLOCATOR";
	
	private String[] commands;
	
	public void initialize(String user, String password, String address, String destinationLocation, int port, String postOperatoin,String folderSepretor,String archiveLocation,String originalExtension) throws FileAllocatorException{
		super.initialize(user, password, address, destinationLocation, port, postOperatoin, folderSepretor, archiveLocation, originalExtension);
		if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "As COMMAND EXECUTION FILE LOCATION ALLOCATOR is selected, risk of execution of the command will be solely deopendent upon the Command Execution Environment.");
		address = address.trim();
		StringTokenizer tokenizer  = new StringTokenizer(address,",;");
		commands = new String[tokenizer.countTokens()];
		int i= 0;
		while(tokenizer.hasMoreTokens()){
			commands[i++] = tokenizer.nextToken();
		}
		if(commands.length<1){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "No Commands Configured to execute.");
		}
	}

	public boolean disconnect() {
		return true;
	}

	public boolean connect() throws FileAllocatorException {
		return true;
	}

	public boolean getPermission() {
		return true;
	}

	public File transferFile(File file) throws FileAllocatorException {
		File fileToTransfer = manageExtension(file, BaseCommonFileAllocator.UIP_EXTENSION, originalExtension, null);
		if(fileToTransfer==null){
			return null;
		}
		for(String command : getCommands()){
			Process process = null;
			try {
				process = Runtime.getRuntime().exec(command+" "+fileToTransfer.getName());
				int result = process.waitFor();
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Command : "+command+", Status : "+result);
			} catch (IOException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Error in executing command.");
				LogManager.getLogger().trace(MODULE, e);
			} catch (InterruptedException e) {
			}
		}
		return fileToTransfer;
	}
	
	
	private String[] getCommands(){
		return commands;
	}
	

}
