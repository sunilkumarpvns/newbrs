package com.elitecore.core.commons.fileio.loactionalloactor;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;


public class FileLocationAllocatorFactory {

	public static final String MODULE = "FILE_LOCATION_ALLOCATION_FACTORY";
	
	private static FileLocationAllocatorFactory fileLocationAllocatorFactory;
	
	public static final String FTP = "ftp";
	public static final String FTPS = "ftps";
	public static final String HTTP = "http";
	public static final String SMTP = "smtp";
	public static final String COMMAND = "command";
	public static final String LOCAL = "local";
	
	private FileLocationAllocatorFactory() {
		super();
	}

	public static FileLocationAllocatorFactory getInstance(){
		if(fileLocationAllocatorFactory==null){
			synchronized (FileLocationAllocatorFactory.class) {
			fileLocationAllocatorFactory = new FileLocationAllocatorFactory();
			}
		}
		return fileLocationAllocatorFactory;
	}
	
	
	public IFileLocationAllocater getFileLocationAllocater(String type){
		if(FTP.equalsIgnoreCase(type)){return new FTPFileAllocator();}
		else if(FTPS.equalsIgnoreCase(type)){return new FTPSFileAllocator();}
		else if(HTTP.equalsIgnoreCase(type)){return new HTTPFileLocationAllocator();}
		else if(SMTP.equalsIgnoreCase(type)){return new SMTPFileAllocator();}
		else if(LOCAL.equalsIgnoreCase(type)){return new LocalFileAllocator();}
		//else if(COMMAND.equalsIgnoreCase(type)){return new CommandExecutorAllocator();}
		else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "No Suitable File Location Allocator Found for configured protocol : "+type);
			return null;
		}
	}
}
