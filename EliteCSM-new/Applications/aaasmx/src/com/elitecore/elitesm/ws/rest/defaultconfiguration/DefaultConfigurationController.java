package com.elitecore.elitesm.ws.rest.defaultconfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.form.EliteAAASetupData;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.model.EliteAAADefaultModel;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.util.DefaultSetupUtility;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

@Path("/")
public class DefaultConfigurationController {
	
	@POST
	public Response createAAASetup(@Valid EliteAAASetupData eliteAAASetupData) throws DataManagerException {
		EliteAAADefaultModel model = new EliteAAADefaultModel();

		if (Collectionz.isNullOrEmpty(DefaultSetupUtility.verifyAndCollectDuplicateModule()) == false) {
			return Response.ok(RestUtitlity.getResponse("Failed to create Default configuration, Reason: " + DefaultSetupUtility.verifyAndCollectDuplicateModule() + " modules are already created.", ResultCode.ALREADY_EXIST)).build();
		} else if (Collectionz.isNullOrEmpty(DefaultSetupUtility.checkForXmlFiles()) == false) {
			return Response.ok(RestUtitlity.getResponse("Failed to create Default configuration, Reason: XML File does not exist of : " + DefaultSetupUtility.checkForXmlFiles() + " Module(s).", ResultCode.INTERNAL_ERROR)).build();
		} else { 
			model.createDefaultConfigurationSetup(eliteAAASetupData);
		}

		return Response.ok(RestUtitlity.getResponse("EliteCSM Default Configuration created successfully")).build();
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DEFAULT_CONFIGURATION);
	}
}
