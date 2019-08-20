package com.elitecore.elitesm.ws.rest.serverconfig.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.bind.annotation.RequestBody;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

/**
 * 
 * @author Tejas.P.Shah
 *
 */

@Path("/")
public class EliteCLIController {

	@POST
	public Response executeCLICommandByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@QueryParam(value = "name") String serverName, @RequestBody @NotEmpty(message = RestValidationMessages.COMMAND_NOT_FOUND_IN_REQUEST_BODY) String command) throws ConnectException, DataManagerException {
		
		IRemoteCommunicationManager remoteCommunicationManager = null;
		remoteCommunicationManager = CommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);
		
		int index = command.indexOf(" ");
		String strCommand = "";
		String strCommadParameters = "";
		if(index != -1){
			strCommand = command.substring(0, index);
			strCommadParameters = command.substring(index + 1, command.length());
		} else {
			strCommand = command.replaceAll(RestValidationMessages.REMOVE_EXTRA_LINES, "");
		}
		
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		
		NetServerInstanceData serverInstanceData =  netServerBLManager.getNetServerInstanceByName(serverName);
		remoteCommunicationManager.init(serverInstanceData.getAdminHost(), serverInstanceData.getAdminPort(), null, false);
		
		Object[] wsArgValues = {strCommand,strCommadParameters};
        String[] wsArgTypes = {"java.lang.String","java.lang.String"};
        
        long timeElapsed = System.currentTimeMillis();
        
        String commandResult = (String) remoteCommunicationManager.execute(MBeanConstants.CLI,"executeCLICommand",wsArgValues,wsArgTypes);
        
        timeElapsed = System.currentTimeMillis() - timeElapsed;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		
        String responseTime = "\nExecuted On: " + sdf.format(new Date()) + " -- Time Taken: "+ timeElapsed + "ms";
        
        remoteCommunicationManager.close();
		
        return Response.ok(RestUtitlity.getResponseTime(commandResult,responseTime)).build();
	}
	
	@POST
	@Path("/{name}")
	public Response executeCLICommandByPathParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
	@PathParam(value = "name") String serverName, @RequestBody @NotEmpty(message = RestValidationMessages.COMMAND_NOT_FOUND_IN_REQUEST_BODY) String command) throws ConnectException, DataManagerException {
		return executeCLICommandByQueryParam(serverName,command);
	}
		
	@GET
	@Path("/help/")
	public Response getServerInstanceHelp() throws FileNotFoundException, IllegalArgumentException,IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITECLI_COMMAND);
	}
}
