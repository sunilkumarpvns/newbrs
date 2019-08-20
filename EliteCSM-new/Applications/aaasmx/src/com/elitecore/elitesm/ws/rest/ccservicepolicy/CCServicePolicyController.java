package com.elitecore.elitesm.ws.rest.ccservicepolicy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.CreditControlPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCPolicyPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class CCServicePolicyController {

	private static final String CREDIT_CONTROL_SERVICE = "Credit Control Service ";

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<CreditControlPolicyData> data, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, List<Status>> responseMap = null;
		if (Collectionz.isNullOrEmpty(data.getList()) == false) {
			
			for (CreditControlPolicyData  policyData : data.getList()) {
				
				List<CCPolicyPluginConfig> prePlugins = policyData.getPrePlugins();
				for (CCPolicyPluginConfig ccPolicyPluginConfig : prePlugins) {
					ccPolicyPluginConfig.setPluginType(PolicyPluginConstants.IN_PLUGIN);
					policyData.getCcPolicyPluginConfigList().add(ccPolicyPluginConfig);
				}
				
				List<CCPolicyPluginConfig> postPlugins = policyData.getPostPlugins();
				for (CCPolicyPluginConfig ccPolicyPluginConfig : postPlugins) {
					ccPolicyPluginConfig.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
					policyData.getCcPolicyPluginConfigList().add(ccPolicyPluginConfig);
				}
			}
			CreditControlPolicyBLManager policyDataManager = new CreditControlPolicyBLManager();
			responseMap = policyDataManager.create(data.getList(), staffData, URLInfo.isPartialSuccess(uri));
		}
		return Response.ok(RestUtitlity.getResponse(CREDIT_CONTROL_SERVICE,"Policies created successfully.", responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid CreditControlPolicyData data) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<CCPolicyPluginConfig> prePlugins = data.getPrePlugins();
		for (CCPolicyPluginConfig ccPolicyPluginConfig : prePlugins) {
			ccPolicyPluginConfig.setPluginType(PolicyPluginConstants.IN_PLUGIN);
			data.getCcPolicyPluginConfigList().add(ccPolicyPluginConfig);
		}

		List<CCPolicyPluginConfig> postPlugins = data.getPostPlugins();
		for (CCPolicyPluginConfig ccPolicyPluginConfig : postPlugins) {
			ccPolicyPluginConfig.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
			data.getCcPolicyPluginConfigList().add(ccPolicyPluginConfig);
		}

		CreditControlPolicyBLManager policyDataManager = new CreditControlPolicyBLManager();
		policyDataManager.create(data, staffData);
		return Response.ok(RestUtitlity.getResponse("Credit Control Service Policy created successfully.")).build();	
	}
	
	@PUT
	public Response update(@Valid CreditControlPolicyData data, @NotEmpty(message = "policy name is missing") @QueryParam(value = "name") String policyToUpdate) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		List<CCPolicyPluginConfig> prePlugins = data.getPrePlugins();
		for (CCPolicyPluginConfig ccPolicyPluginConfig : prePlugins) {
			ccPolicyPluginConfig.setPluginType(PolicyPluginConstants.IN_PLUGIN);
			data.getCcPolicyPluginConfigList().add(ccPolicyPluginConfig);
		}
		
		List<CCPolicyPluginConfig> postPlugins = data.getPostPlugins();
		for (CCPolicyPluginConfig ccPolicyPluginConfig : postPlugins) {
			ccPolicyPluginConfig.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
			data.getCcPolicyPluginConfigList().add(ccPolicyPluginConfig);
		}
		
		CreditControlPolicyBLManager policyBlManager = new CreditControlPolicyBLManager();
		policyBlManager.update(data, staffData, policyToUpdate);
		return Response.ok(RestUtitlity.getResponse("Credit Control Service Policy updated successfully.")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParameter(@Valid CreditControlPolicyData data, @PathParam(value = "name") String policyToUpdate) throws DataManagerException {
		return update(data, policyToUpdate);
	}
	
	@DELETE
	public Response delete(@NotEmpty(message = "policy name is missing") @QueryParam(value = "name") String policyName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CreditControlPolicyBLManager policyBlManager = new CreditControlPolicyBLManager();
		String[] policiedToBeDelete = policyName.split(",");
		policyBlManager.deleteByName(policiedToBeDelete, staffData);
		return Response.ok(RestUtitlity.getResponse("Credit Control Service Policy/Policies deleted successfully.")).build();
	}
	
	@GET
	public Response get(@NotEmpty(message = "policy name is missing") @QueryParam("name") String policyName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CreditControlPolicyBLManager policyBlManager = new CreditControlPolicyBLManager();
		CreditControlPolicyData policyData = policyBlManager.getCCServicePolicyByName(policyName, staffData);
		
		List<CCPolicyPluginConfig> pluginDetails = policyData.getCcPolicyPluginConfigList();
		for (CCPolicyPluginConfig plugins : pluginDetails) {
			if (PolicyPluginConstants.IN_PLUGIN.equalsIgnoreCase(plugins.getPluginType())) {
				policyData.getPrePlugins().add(plugins);
			} else if (PolicyPluginConstants.OUT_PLUGIN.equalsIgnoreCase(plugins.getPluginType())) {
				policyData.getPostPlugins().add(plugins);
			}
		}
		return Response.ok(policyData).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByNameUsingPathParameter(@NotEmpty(message = "policy name is missing") @PathParam(value = "name") String policyName) throws JAXBException, DataManagerException {
		return get(policyName);
	}
	
	@DELETE
	@Path("/{name}")
	public Response deleteByNamesFromPath(@NotEmpty(message = "policy name is missing") @PathParam(value = "name") String policyName) throws DataManagerException {
		return delete(policyName);
	}
	
	@GET
	@Path("/help/")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.CREDIT_CONTROL_POLICY);
	}
}