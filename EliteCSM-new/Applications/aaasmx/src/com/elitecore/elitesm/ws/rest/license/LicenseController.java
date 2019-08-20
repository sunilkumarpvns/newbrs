package com.elitecore.elitesm.ws.rest.license;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.elitecore.elitesm.blmanager.core.system.license.SMLicenseBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.nfv.CentralizedLicenseCoordinator;
import com.elitecore.license.nfv.RequestData;
import com.elitecore.license.nfv.ResponseData;
import com.google.gson.Gson;

/**
 * Controller class for handling license allocation and license validation web service calls.
 * 
 * <p>
 * Web service calls having url 
 * <br>{SM-IP}:{SM-port}/{context-path}/cxfservices/restful/v1/server/elitecsmserver/license</br>
 * will be served by this controller class.
 * 
 * </p>
 * 
 * @author vicky.singh
 *
 */
@Path("/")
public class LicenseController {

	@POST
	@Path("/validate")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response validateLicenseAuthneticity(String data) {
		RequestData requestData = new Gson().fromJson(data, RequestData.class);
		ResponseData responseData = new ResponseData();

		CentralizedLicenseCoordinator centralizedLicenseHelper = 
				new CentralizedLicenseCoordinator(new SMLicenseBLManager());
			
		responseData = centralizedLicenseHelper.validate(requestData);
		return Response.ok(new Gson().toJson(responseData, ResponseData.class)).build();

	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response downLoadLicenseByPost(String data) {
		RequestData requestData = new Gson().fromJson(data, RequestData.class);
		
		ResponseData responseData = null;
		try {
			if (verifyInstance(requestData) == false) {
				responseData = new ResponseData(ResponseData.UNKNOWN_INSTANCE, ResponseData.UNKNOWN_INSTANCE_MSG);
				return Response.ok(new Gson().toJson(responseData, ResponseData.class)).build();
			}
		} catch (DataManagerException e) {
			responseData = new ResponseData(ResponseData.UNKNOWN_INSTANCE, e.getMessage());
			return Response.ok(new Gson().toJson(responseData, ResponseData.class)).build();
		}
		
		CentralizedLicenseCoordinator centralizedLicenseHelper = null;
		centralizedLicenseHelper = new CentralizedLicenseCoordinator(new SMLicenseBLManager());
		try {
			responseData = centralizedLicenseHelper.getLicense(EliteUtility.getSMHome(), 
					requestData.getVersion(), requestData);
		} catch (InvalidLicenseKeyException e) {
			responseData = new ResponseData(ResponseData.INVALID_LICENSE, e.getMessage());
			return Response.ok(new Gson().toJson(responseData, ResponseData.class)).build();
		}
		
		return Response.ok(new Gson().toJson(responseData, ResponseData.class)).build();
	}

	private boolean verifyInstance(RequestData requestData) throws DataManagerException {
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(requestData.getServerName());
		if (netServerInstanceData != null) {
			if(netServerInstanceData.getNetServerCode().equals(requestData.getServerId()) == false) {
				return false;
			} else  {
				return true;
			}
		} else {
			throw new DataManagerException("Server instance not found");
		}
	}
}

