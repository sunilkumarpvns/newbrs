package com.elitecore.elitesm.web.cli.server;

import java.util.StringTokenizer;

import com.elitecore.core.util.cli.JMXException;
import com.elitecore.core.util.cli.cmd.JMXCommnadExecutor;
import com.elitecore.elitesm.web.cli.client.CLIService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CLIServiceImpl extends RemoteServiceServlet implements CLIService {

	private final static String MODULE="CLI IMPL"; 
	private final String ACTION_ALIAS="CLI";
	
	private static JMXCommnadExecutor commandExecutor = null;

	public String getResponce(String promptInput) {
		String response="";
		if(!"".equals(promptInput)){									
			StringTokenizer tokenizer = new StringTokenizer(promptInput, " ");
			String strCommand = tokenizer.nextToken();
			String strCommadParameters = "";
			if(promptInput.indexOf(' ') != -1){
				strCommadParameters = promptInput.substring(promptInput.indexOf(' '));
			}
			try {
				response = commandExecutor.execute(strCommand, strCommadParameters.trim());
			} catch (JMXException e) {
				response = "Communication Error";
			}				
		}
		
		return response;
	}
	public void init(String adminHost, Integer adminPort) {
		commandExecutor = new JMXCommnadExecutor(adminHost,adminPort);		
	}
	
	
	
	
	
	
	
	
}
