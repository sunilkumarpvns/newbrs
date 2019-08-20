package com.elitecore.core.util.cli.cmd;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.core.util.cli.JMXException;
import com.elitecore.core.util.cli.JMXInterface;

public class JMXCommnadExecutor {
	
	private static String MODULE = "JMXCommand Executor"; 
	private String objectName = null;
	private String methodName = null;
	
	public JMXCommnadExecutor(String hostName,int port){		
		objectName = MBeanConstants.CLI;
		methodName = "executeCLICommand";	
		
		try {
			JMXInterface.getInstance().init(hostName,port);
		} catch (JMXException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "JMXException while initializing JMX Instance.");
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}
	
	public String[] retrieveHotkeyHelp() throws Exception{
		String[] response = null;		
		try {
			response = (String[])JMXInterface.getInstance().makeCall(objectName, "retrieveHotkeyHelp", null, null);
		} catch (JMXException e) {			
			throw e;			
		}
		return response;		
	}

	public String retrieveCommandNames() throws Exception{
		String response = null;		
		try {
			response = (String)JMXInterface.getInstance().makeCall(objectName, "retrieveCommandNames", null, null);
		} catch (JMXException e) {			
			throw e;			
		}
		return response;		
	}

	public String execute(String strCommand,String strCommandPrarmeters) throws JMXException{	
		
		String[] params = new String[2];
		String[] signs = new String[2];
		
		params[0] = strCommand;
		params[1] = strCommandPrarmeters;	
		signs[0] = "java.lang.String";
		signs[1] = "java.lang.String";
		return (String)JMXInterface.getInstance().makeCall(objectName, methodName, params, signs);
		}
	
	public String retriveServerState() throws JMXException {
		return (String)JMXInterface.getInstance().makeCall(MBeanConstants.CONFIGURATION, "ServerState");
	}
}
