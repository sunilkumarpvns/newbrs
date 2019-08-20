package com.elitecore.elitesm.ws.rest.diameterpeers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
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

import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;


public class DiameterPeersController {

	private static final String DIAMETER_PEER = "Diameter Peer";
	private static final String MODULE = ConfigConstant.DIAMETER_PEER;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPeerName) throws DataManagerException {

		DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
		DiameterPeerData diameterPeer = diameterPeerBLManager.getDiameterPeerByName(diameterPeerName);

		return Response.ok(diameterPeer).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String diameterPeerName) throws DataManagerException {
		return getByNameFromQuery(diameterPeerName);
	}

	@POST
	public Response create(@Valid DiameterPeerData diameterPeer) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
		
		diameterPeer.setCreateDate(new Timestamp(new Date().getTime()));
		diameterPeer.setCreatedByStaffId(staffData.getStaffId());
		diameterPeer.setLastModifiedDate(new Timestamp(new Date().getTime()));
		diameterPeer.setLastModifiedByStaffId(staffData.getStaffId());
			
		diameterPeerBLManager.createDiameterPeer(diameterPeer, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Diameter Peer created successfully")).build();

	}

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DiameterPeerData> diameterPeerDatas, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
		
		List<DiameterPeerData> diameterPeers = diameterPeerDatas.getList();
		
		for (DiameterPeerData diameterPeer : diameterPeers) {
			
			diameterPeer.setCreateDate(new Timestamp(new Date().getTime()));
			diameterPeer.setCreatedByStaffId(staffData.getStaffId());
			diameterPeer.setLastModifiedDate(new Timestamp(new Date().getTime()));
			diameterPeer.setLastModifiedByStaffId(staffData.getStaffId());
			
		}

		Map<String, List<Status>> responseMap = diameterPeerBLManager.createDiameterPeer(diameterPeers, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_PEER, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();

	}

	@PUT
	public Response updateByQueryParam(@Valid DiameterPeerData diameterPeer, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPeerName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
		
		diameterPeer.setLastModifiedDate(new Timestamp(new Date().getTime()));
		diameterPeer.setLastModifiedByStaffId(staffData.getStaffId());
			
		diameterPeerBLManager.updateDiameterPeerByName(diameterPeer, staffData, diameterPeerName);

		LogManager.getLogger().info(MODULE, diameterPeer.toString());

		return Response.ok(RestUtitlity.getResponse("Diameter Peer updated successfully")).build();

	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DiameterPeerData diameterPeer, @PathParam(value = "name") String diameterPeerName) throws DataManagerException {
		return updateByQueryParam(diameterPeer, diameterPeerName);
	}

	@DELETE
	public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPeerNames) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();

		List<String> listDiameterPeerName = Arrays.asList(diameterPeerNames.split(","));

		diameterPeerBLManager.deleteDiameterPeerByName(listDiameterPeerName, staffData);

		return Response.ok(RestUtitlity.getResponse("Diameter Peer(s) deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String diameterPeerName) throws DataManagerException {
		return deleteByQueryParam(diameterPeerName);
	}

	@GET
	@Path("/help")
	public Response getDiameterPeersHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_PEERS);
	}
	
}
