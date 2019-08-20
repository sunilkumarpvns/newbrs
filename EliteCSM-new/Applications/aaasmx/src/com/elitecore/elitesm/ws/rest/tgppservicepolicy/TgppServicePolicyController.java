package com.elitecore.elitesm.ws.rest.tgppservicepolicy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.TGPPServerPolicyData;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class TgppServicePolicyController {

	private static final String TGPP_SERVICE_POLICIES = "Tgpp Service Policies ";

	@GET
	public Response get(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
			throws DataManagerException {
		
		TGPPAAAPolicyBLManager policyBLManager = new TGPPAAAPolicyBLManager();
		TGPPAAAPolicyData data = policyBLManager.getTGPPAAAPolicyDataByName(name);
		TGPPServerPolicyData policyData;
		try {
			policyData = (TGPPServerPolicyData)ConfigUtil.deserialize(new String(data.getTgppAAAPolicyXml()),TGPPServerPolicyData.class);
			policyData.setStatus(RestUtitlity.getStatus(data.getStatus()));
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		}
		return Response.ok(policyData).build();
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<TGPPServerPolicyData> policyData,@Context UriInfo uri) throws DataManagerException, JAXBException {
		
		List<TGPPAAAPolicyData> tgppaaaPolicyDatas = new ArrayList<TGPPAAAPolicyData>();
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TGPPAAAPolicyBLManager policyBLManager = new TGPPAAAPolicyBLManager();
		List<TGPPServerPolicyData> policiesToCreate = policyData.getList();
		for (TGPPServerPolicyData tgppServerPolicyData : policiesToCreate) {
			TGPPAAAPolicyData data = convertToTgppPolicyDataBeanFrom(tgppServerPolicyData);
			tgppaaaPolicyDatas.add(data);
		}
		Map<String, List<Status>> responseMap = policyBLManager.createTGPPAAAPolicyData(tgppaaaPolicyDatas,staffData,URLInfo.isPartialSuccess(uri));
		return Response.ok(RestUtitlity.getResponse(TGPP_SERVICE_POLICIES,"created successfully", responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid TGPPServerPolicyData policyData) throws DataManagerException, JAXBException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TGPPAAAPolicyData data = convertToTgppPolicyDataBeanFrom(policyData);
		TGPPAAAPolicyBLManager policyBLManager = new TGPPAAAPolicyBLManager();
		policyBLManager.createTGPPAAAPolicyData(data,staffData);
		return Response.ok(RestUtitlity.getResponse("Tgpp Service Policy created successfully")).build();
	}
	
	@GET
	@Path("/{name}")
	public Response getByPathParam(@PathParam("name") String name) throws DataManagerException {
		return get(name);
	}
	
	@DELETE
	public Response delete(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
			throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TGPPAAAPolicyBLManager policyBLManager = new TGPPAAAPolicyBLManager();
		policyBLManager.deleteTGPPAAAPolicyByName(Arrays.asList(name.split(",")), staffData);
		return Response.ok(RestUtitlity.getResponse("Tgpp Service Policy deleted successfully")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam("name") String name) throws DataManagerException {
		return delete(name);
	}

	@PUT
	public Response update(
			@Valid TGPPServerPolicyData policyData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
			throws DataManagerException {
		
		StaffData staffData = (StaffData) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		TGPPAAAPolicyBLManager policyBLManager = new TGPPAAAPolicyBLManager();
		TGPPAAAPolicyData data = convertToTgppPolicyDataBeanFrom(policyData);
		policyBLManager.updateTgppAAAPolicyByName(data, name.trim(), staffData);
		return Response.ok(RestUtitlity.getResponse("Tgpp Service Policy updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(
			@Valid TGPPServerPolicyData policyData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value = "name") String name)
			throws DataManagerException {
		return update(policyData, name);
	}
	
	private TGPPAAAPolicyData convertToTgppPolicyDataBeanFrom(TGPPServerPolicyData policyData) throws DataManagerException {
		TGPPAAAPolicyData data = new TGPPAAAPolicyData();

		data.setName(policyData.getName());
		data.setDescription(policyData.getDescription());
		data.setStatus(policyData.getStatus());
		data.setRuleset(policyData.getRuleSet());
		data.setSessionManagement(Strings.valueOf(policyData.getSessionManagementEnabled()));
		data.setUserIdentity(policyData.getUserIdentity());
		data.setDefaultResponseBehaviorArgument(policyData.getDefaultResponseBehaviorParameter());
		data.setDefaultResponseBehaviour(policyData.getDefaultResponseBehaviorType().name());
		data.setCui(Strings.isNullOrBlank(policyData.getStatus()) ? policyData.getCui() : "");
		
		try {
			byte[] convertToXml = convertToXml(policyData);
			data.setTgppAAAPolicyXml(convertToXml);
		} catch(JAXBException e) {
			throw new DataManagerException(e.getMessage(), e);
		}
		
		return data;
	}

	private byte[] convertToXml(TGPPServerPolicyData policyData) throws JAXBException {
		String serialize = ConfigUtil.serialize(TGPPServerPolicyData.class, policyData);
		return serialize.getBytes();
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.TGPP_SERVICE_POLICY);
	}
}
