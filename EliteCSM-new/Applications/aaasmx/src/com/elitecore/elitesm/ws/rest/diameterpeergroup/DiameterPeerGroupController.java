package com.elitecore.elitesm.ws.rest.diameterpeergroup;

import java.io.IOException;
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

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerRelationWithPeerGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class DiameterPeerGroupController {

	private static final String DIAMETER_PEER_GROUP = "Diameter Peer Group";
	private static final String MODULE = ConfigConstant.DIAMETER_PEER_GROUP;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPeerGroupName) throws DataManagerException {

		DiameterPeerGroupBLManager diameterPeerGroupBLManager = new DiameterPeerGroupBLManager();
	
		DiameterPeerGroup diameterPeerGroup = diameterPeerGroupBLManager.getDiameterPeerGroupByName(diameterPeerGroupName);
		
		if( Strings.isNullOrBlank(diameterPeerGroup.getGeoRedunduntGroup()) == false){
			DiameterPeerGroup diameterPeerGroupData = diameterPeerGroupBLManager.getDiameterPeerGroupById(diameterPeerGroup.getGeoRedunduntGroup());
			diameterPeerGroup.setGeoRedunduntGroup(diameterPeerGroupData.getPeerGroupName());
		}
		
		return Response.ok(diameterPeerGroup).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String diameterPeerGroupName) throws DataManagerException {
		return getByNameFromQuery(diameterPeerGroupName);
	}

	@POST
	public Response create(@Valid DiameterPeerGroup diameterPeerGroup) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerGroupBLManager diameterPeerGroupBLManager = new DiameterPeerGroupBLManager();
		
		diameterPeerGroupBLManager.createDiameterPeerGroup(diameterPeerGroup, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Diameter Peer Group created successfully")).build();

	}

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DiameterPeerGroup> diameterPeerGroup, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerGroupBLManager diameterPeerGroupBLManager = new DiameterPeerGroupBLManager();
		
		List<DiameterPeerGroup> diameterPeerGroupDatas = diameterPeerGroup.getList();

		Map<String, List<Status>> responseMap = diameterPeerGroupBLManager.createDiameterPeerGroup(diameterPeerGroupDatas, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_PEER_GROUP, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	public Response updateByQueryParam(@Valid DiameterPeerGroup diameterPeerGroup, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPeerGroupName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerGroupBLManager diameterPeerGroupBLManager = new DiameterPeerGroupBLManager();
		
		DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();

		List<DiameterPeerRelationWithPeerGroup> peerList = diameterPeerGroup.getPeerList();
		
		for(DiameterPeerRelationWithPeerGroup peerDetail:peerList) {
			peerDetail.setPeerId(diameterPeerBLManager.getDiameterPeerIdByName(peerDetail.getPeerName()));
		}
		
		diameterPeerGroup.setPeerList(peerList);

		diameterPeerGroupBLManager.updateDiameterPeerGroupByName(diameterPeerGroup, staffData, diameterPeerGroupName);

		LogManager.getLogger().info(MODULE, diameterPeerGroup.toString());

		return Response.ok(RestUtitlity.getResponse("Diameter Peer Group updated successfully")).build();

	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DiameterPeerGroup diameterPeerGroup, @PathParam(value = "name") String diameterPeerGroupName) throws DataManagerException {
		return updateByQueryParam(diameterPeerGroup, diameterPeerGroupName);
	}

	@DELETE
	public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPeerGroupName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerGroupBLManager diameterPeerGroupBLManager = new DiameterPeerGroupBLManager();
		
		List<String> listDiameterPeerGroups=Arrays.asList(diameterPeerGroupName.split(","));
		
		diameterPeerGroupBLManager.deleteDiameterPeerGroupByName(listDiameterPeerGroups, staffData);

		return Response.ok(RestUtitlity.getResponse("Diameter Peer Group(s) deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String diameterPeerGroupName) throws DataManagerException {
		return deleteByQueryParam(diameterPeerGroupName);
	}

	@GET
	@Path("/help")
	public Response getDiameterPeerGroupHelp() throws IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_PEER_GROUP);
	}
	
}
