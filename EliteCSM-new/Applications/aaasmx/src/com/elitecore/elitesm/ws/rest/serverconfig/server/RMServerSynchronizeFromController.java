package com.elitecore.elitesm.ws.rest.serverconfig.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

public class RMServerSynchronizeFromController {

	@PUT
	public Response rmSynchrnonizeFrom(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverInstanceName)
			throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException, ConnectException{
		
		String responseMessage = SyncToAndSyncFromUtil.synchronizFromData(serverInstanceName,SyncToAndSyncFromUtil.ELITERMSERVER);	
		if( Strings.isNullOrBlank(responseMessage)== false){
			return Response.ok(RestUtitlity.getResponse(responseMessage, ResultCode.INVALID_INPUT_PARAMETER)).build();
		}else {
			return Response.ok(RestUtitlity.getResponse("RM Server Synchronization From successfully completed")).build();
		}
	}
	
	@PUT
	@Path("/{name}")
	public Response rmSynchrnonizeFromPathParams(@PathParam(value = "name") String serverInstanceName)
			throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException, ConnectException{
		return rmSynchrnonizeFrom(serverInstanceName);
	}
	
	@GET
	@Path("/help/")
	public Response getRMSynchronizeToHelp() throws FileNotFoundException,IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.ELITERMSERVER_SYNCHRONIZE_FROM);
	}
}
