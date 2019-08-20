package com.elitecore.elitesm.ws.rest.diameterroutingtable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupRelData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class DiameterRoutingTableController {
	
	private static final String DIAMETER_ROUTING_TABLE = "Diameter Routing Table";


	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterRoutingTableName, @QueryParam(value = "entry") String diameterRoutingTableEntryName) throws DataManagerException {

		DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();
		DiameterRoutingTableData diameterRoutingTable = diameterRoutingTableBLManager.getDiameterRoutingTableByName(diameterRoutingTableName);
		
		if (Strings.isNullOrBlank(diameterRoutingTableEntryName)) {
			if (Collectionz.isNullOrEmpty(diameterRoutingTable.getDiameterRoutingTableEntries()) == false) {
				List<DiameterRoutingConfData> diameterRoutingTableEntries = diameterRoutingTable.getDiameterRoutingTableEntries();
				for (DiameterRoutingConfData diameterRoutingTableEntry : diameterRoutingTableEntries) {
					String TranslationMappingName = null;
					if (diameterRoutingTableEntry.getTransMapConfId() != null && diameterRoutingTableEntry.getCopyPacketMapId() == null) {
						TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
						TranslationMappingName = translationMappingBLManager.getTranslationMappingConfDataById(diameterRoutingTableEntry.getTransMapConfId()).getName();
						diameterRoutingTableEntry.setTranslationMappingName(TranslationMappingName);
					}
					if (diameterRoutingTableEntry.getTransMapConfId() == null && diameterRoutingTableEntry.getCopyPacketMapId() != null) {
						CopyPacketTransMapConfBLManager copyPacketBLManager =  new CopyPacketTransMapConfBLManager();
						TranslationMappingName = copyPacketBLManager.getCopyPacketTransMapConfigDetailDataById(diameterRoutingTableEntry.getCopyPacketMapId()).getName();
						diameterRoutingTableEntry.setTranslationMappingName(TranslationMappingName);
					}
					if (Strings.isNullOrBlank(diameterRoutingTableEntry.getSubsciberMode()) == false) {
						if (diameterRoutingTableEntry.getSubsciberMode().equals(CommonConstants.IMSI_MSISDN)) {
							diameterRoutingTableEntry.setSubscriberRoutingOneName(new IMSIBasedRoutingTableBLManager().getImsiBasedRoutingTableData(diameterRoutingTableEntry.getImsiBasedRoutingTableId()).getRoutingTableName());
							if (diameterRoutingTableEntry.getMsisdnBasedRoutingTableId() != null) {
								diameterRoutingTableEntry.setSubscriberRoutingTwoName(new MSISDNBasedRoutingTableBLManager().getMSISDNBasedRoutingTableData(diameterRoutingTableEntry.getMsisdnBasedRoutingTableId()).getRoutingTableName());
							}
						} else if (diameterRoutingTableEntry.getSubsciberMode().equals(CommonConstants.MSISDN_IMSI)) {
							diameterRoutingTableEntry.setSubscriberRoutingOneName(new MSISDNBasedRoutingTableBLManager().getMSISDNBasedRoutingTableData(diameterRoutingTableEntry.getMsisdnBasedRoutingTableId()).getRoutingTableName());
							if (diameterRoutingTableEntry.getImsiBasedRoutingTableData() != null) {
								diameterRoutingTableEntry.setSubscriberRoutingTwoName(new IMSIBasedRoutingTableBLManager().getImsiBasedRoutingTableData(diameterRoutingTableEntry.getImsiBasedRoutingTableId()).getRoutingTableName());
							}
						}
					}
				}
			}
			return Response.ok(diameterRoutingTable).build();
		} else if (Collectionz.isNullOrEmpty(diameterRoutingTable.getDiameterRoutingTableEntries()) == false) {
			List<DiameterRoutingConfData> diameterRoutingTableEntries = diameterRoutingTable.getDiameterRoutingTableEntries();
			for (DiameterRoutingConfData diameterRoutingTableEntry : diameterRoutingTableEntries) {
				if (diameterRoutingTableEntry.getRoutingTableId().equals(diameterRoutingTable.getRoutingTableId()) && diameterRoutingTableEntry.getName().equals(diameterRoutingTableEntryName.trim())) {
					String TranslationMappingName = null;
					if (diameterRoutingTableEntry.getTransMapConfId() != null && diameterRoutingTableEntry.getCopyPacketMapId() == null) {
						TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
						TranslationMappingName = translationMappingBLManager.getTranslationMappingConfDataById(diameterRoutingTableEntry.getTransMapConfId()).getName();
						diameterRoutingTableEntry.setTranslationMappingName(TranslationMappingName);
					}
					if (diameterRoutingTableEntry.getTransMapConfId() == null && diameterRoutingTableEntry.getCopyPacketMapId() != null) {
						CopyPacketTransMapConfBLManager copyPacketBLManager =  new CopyPacketTransMapConfBLManager();
						TranslationMappingName = copyPacketBLManager.getCopyPacketTransMapConfigDetailDataById(diameterRoutingTableEntry.getCopyPacketMapId()).getName();
						diameterRoutingTableEntry.setTranslationMappingName(TranslationMappingName);
	
					}
					if (Strings.isNullOrBlank(diameterRoutingTableEntry.getSubsciberMode()) == false) {
						if (diameterRoutingTableEntry.getSubsciberMode().equals(CommonConstants.IMSI_MSISDN)) {
							diameterRoutingTableEntry.setSubscriberRoutingOneName(new IMSIBasedRoutingTableBLManager().getImsiBasedRoutingTableData(diameterRoutingTableEntry.getImsiBasedRoutingTableId()).getRoutingTableName());
							if (diameterRoutingTableEntry.getMsisdnBasedRoutingTableId() != null) {
								diameterRoutingTableEntry.setSubscriberRoutingTwoName(new MSISDNBasedRoutingTableBLManager().getMSISDNBasedRoutingTableData(diameterRoutingTableEntry.getMsisdnBasedRoutingTableId()).getRoutingTableName());
							}
						} else if (diameterRoutingTableEntry.getSubsciberMode().equals(CommonConstants.MSISDN_IMSI)) {
							diameterRoutingTableEntry.setSubscriberRoutingOneName(new MSISDNBasedRoutingTableBLManager().getMSISDNBasedRoutingTableData(diameterRoutingTableEntry.getMsisdnBasedRoutingTableId()).getRoutingTableName());
							if (diameterRoutingTableEntry.getImsiBasedRoutingTableData() != null) {
								diameterRoutingTableEntry.setSubscriberRoutingTwoName(new IMSIBasedRoutingTableBLManager().getImsiBasedRoutingTableData(diameterRoutingTableEntry.getImsiBasedRoutingTableId()).getRoutingTableName());
							}
						}
					}
					return Response.ok(diameterRoutingTableEntry).build();
				}
			}
		}
		
		return Response.ok(RestUtitlity.getResponse("Diameter Routing Table Entry " + diameterRoutingTableEntryName + " for Diameter Routing Table " + diameterRoutingTableName + " does not exists", ResultCode.NOT_FOUND)).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String diameterRoutingTableName) throws DataManagerException {
		return getByNameFromQuery(diameterRoutingTableName, null);
	}

	@GET
	@Path("/{name}/{entry}")
	public Response getByNameFromPath(@PathParam(value = "name") String diameterRoutingTableName, @PathParam(value = "entry") String diameterRoutingTableEntryName) throws DataManagerException {
		return getByNameFromQuery(diameterRoutingTableName, diameterRoutingTableEntryName);
	}
	
	
	@POST
	@Path("/routingtable")
	public Response createRoutingTable(@Valid DiameterRoutingTableData diameterRoutingTable) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();
		
		diameterRoutingTableBLManager.createRoutingTable(diameterRoutingTable, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Diameter Routing Table created successfully")).build();

	}
	
	@POST
	@Path("/routingentry")
	public Response createRoutingEntry(@Valid DiameterRoutingConfData diameterRoutingTableEntry) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();
		
		Timestamp timestamp = new Timestamp(new Date().getTime());
		
		diameterRoutingTableEntry.setCreateDate(timestamp);
		diameterRoutingTableEntry.setCreatedByStaffId(staffData.getStaffId());
		diameterRoutingTableEntry.setLastModifiedDate(timestamp);
		diameterRoutingTableEntry.setLastModifiedByStaffId(staffData.getStaffId());
		
		if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterPeerGroupDataSet()) == false) {
			Set<DiameterPeerGroupData> diameterPeerGroupDatas = diameterRoutingTableEntry.getDiameterPeerGroupDataSet();
			for (DiameterPeerGroupData diameterPeerGroupData : diameterPeerGroupDatas) {
				if (Collectionz.isNullOrEmpty(diameterPeerGroupData.getDiameterPeerGroupRelDataSet()) == false) {
					Set<DiameterPeerGroupRelData> diameterPeerGroupRelDatas = diameterPeerGroupData.getDiameterPeerGroupRelDataSet();
					Long orderNumber = 1L;
					for (DiameterPeerGroupRelData diameterPeerGroupRelData : diameterPeerGroupRelDatas) {
						diameterPeerGroupRelData.setPeerName(diameterRoutingTableBLManager.getDiameterPeerNameById(diameterPeerGroupRelData.getPeerUUID()));
						diameterPeerGroupRelData.setOrderNumber(orderNumber++);
					}
				}
			}
		}
			
		diameterRoutingTableBLManager.createRoutingEntry(diameterRoutingTableEntry, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Diameter Routing Table Entry created successfully")).build();

	}
	
	
	@POST
	@Path("/routingtable/bulk")
	public Response createRoutingTables(@Valid ListWrapper<DiameterRoutingTableData> diameterRoutingTableDatas, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();
		
		List<DiameterRoutingTableData> diameterRoutingTables = diameterRoutingTableDatas.getList();
		
		Map<String, List<Status>> responseMap = diameterRoutingTableBLManager.createRoutingTable(diameterRoutingTables, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_ROUTING_TABLE, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	@Path("/routingentry/bulk")
	public Response createRoutingEntries(@Valid ListWrapper<DiameterRoutingConfData> diameterRoutingTableDataEntries, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();
		
		List<DiameterRoutingConfData> diameterRoutingTableEntries = diameterRoutingTableDataEntries.getList();
			
		Timestamp timestamp = new Timestamp(new Date().getTime());
		for (DiameterRoutingConfData diameterRoutingTableEntry : diameterRoutingTableEntries) {
			diameterRoutingTableEntry.setCreateDate(timestamp);
			diameterRoutingTableEntry.setCreatedByStaffId(staffData.getStaffId());
			diameterRoutingTableEntry.setLastModifiedDate(timestamp);
			diameterRoutingTableEntry.setLastModifiedByStaffId(staffData.getStaffId());
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterPeerGroupDataSet()) == false) {
				Set<DiameterPeerGroupData> diameterPeerGroupDatas = diameterRoutingTableEntry.getDiameterPeerGroupDataSet();
				for (DiameterPeerGroupData diameterPeerGroupData : diameterPeerGroupDatas) {
					if (Collectionz.isNullOrEmpty(diameterPeerGroupData.getDiameterPeerGroupRelDataSet()) == false) {
						Set<DiameterPeerGroupRelData> diameterPeerGroupRelDatas = diameterPeerGroupData.getDiameterPeerGroupRelDataSet();
						Long orderNumber = 1L;
						for (DiameterPeerGroupRelData diameterPeerGroupRelData : diameterPeerGroupRelDatas) {
							diameterPeerGroupRelData.setPeerName(diameterRoutingTableBLManager.getDiameterPeerNameById(diameterPeerGroupRelData.getPeerUUID()));
							diameterPeerGroupRelData.setOrderNumber(orderNumber++);
						}
					}
				}
			}
			
		}

		Map<String, List<Status>> responseMap = diameterRoutingTableBLManager.createRoutingEntry(diameterRoutingTableEntries, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_ROUTING_TABLE, "Entry/Entries created successfully",
				responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}

	
	@PUT
	@Path("/routingtable")
	public Response updateRoutingTableByQueryParam(@Valid DiameterRoutingTableData diameterRoutingTable, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterRoutingTableName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();
		
		diameterRoutingTableBLManager.updateRoutingTableByName(diameterRoutingTable, staffData, diameterRoutingTableName);

		return Response.ok(RestUtitlity.getResponse("Diameter Routing Table updated successfully")).build();

	}
	
	@PUT
	@Path("/routingtable/{name}")
	public Response updateRoutingTableByPathParam(@Valid DiameterRoutingTableData diameterRoutingTable, @PathParam(value = "name") String diameterRoutingTableName) throws DataManagerException {
		return updateRoutingTableByQueryParam(diameterRoutingTable, diameterRoutingTableName);
	}
	
	
	@PUT
	@Path("/routingentry")
	public Response updateRoutingEntryByQueryParam(@Valid DiameterRoutingConfData diameterRoutingTableEntry, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterRoutingTableEntryName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();
		
		diameterRoutingTableEntry.setLastModifiedDate(new Timestamp(new Date().getTime()));
		diameterRoutingTableEntry.setLastModifiedByStaffId(staffData.getStaffId());
		
		if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterPeerGroupDataSet()) == false) {
			Set<DiameterPeerGroupData> diameterPeerGroupDatas = diameterRoutingTableEntry.getDiameterPeerGroupDataSet();
			for (DiameterPeerGroupData diameterPeerGroupData : diameterPeerGroupDatas) {
				if (Collectionz.isNullOrEmpty(diameterPeerGroupData.getDiameterPeerGroupRelDataSet()) == false) {
					Set<DiameterPeerGroupRelData> diameterPeerGroupRelDatas = diameterPeerGroupData.getDiameterPeerGroupRelDataSet();
					Long orderNumber = 1L;
					for (DiameterPeerGroupRelData diameterPeerGroupRelData : diameterPeerGroupRelDatas) {
						diameterPeerGroupRelData.setPeerName(diameterRoutingTableBLManager.getDiameterPeerNameById(diameterPeerGroupRelData.getPeerUUID()));
						diameterPeerGroupRelData.setOrderNumber(orderNumber++);
					}
				}
			}
		}
		
		diameterRoutingTableBLManager.updateRoutingEntryByName(diameterRoutingTableEntry, staffData, diameterRoutingTableEntryName);

		return Response.ok(RestUtitlity.getResponse("Diameter Routing Table Entry updated successfully")).build();

	}
	
	@PUT
	@Path("/routingentry/{name}")
	public Response updateRoutingEntryByPathParam(@Valid DiameterRoutingConfData diameterRoutingTableEntry, @PathParam(value = "name") String diameterRoutingTableEntryName) throws DataManagerException {
		return updateRoutingEntryByQueryParam(diameterRoutingTableEntry, diameterRoutingTableEntryName);
	}

	
	@DELETE
	@Path("/routingtable")
	public Response deleteRoutingTableByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterRoutingTableDataNames) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();

		List<String> diameterRoutingTableNames = Arrays.asList(diameterRoutingTableDataNames.split(","));

		diameterRoutingTableBLManager.deleteRoutingTableByName(diameterRoutingTableNames, staffData);

		return Response.ok(RestUtitlity.getResponse("Diameter Routing Table(s) deleted successfully")).build();

	}

	@DELETE
	@Path("/routingtable/{name}")
	public Response deleteRoutingTableByPathParam(@PathParam(value = "name") String diameterRoutingTableNames) throws DataManagerException {
		return deleteRoutingTableByQueryParam(diameterRoutingTableNames);
	}
	
	
	@DELETE
	@Path("/routingentry")
	public Response deleteRoutingEntryByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterRoutingTableEntryDataNames) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterRoutingConfBLManager diameterRoutingTableBLManager = new DiameterRoutingConfBLManager();

		List<String> diameterRoutingTableEntryNames = Arrays.asList(diameterRoutingTableEntryDataNames.split(","));

		diameterRoutingTableBLManager.deleteRoutingEntryByName(diameterRoutingTableEntryNames, staffData);

		return Response.ok(RestUtitlity.getResponse("Diameter Routing Table Entry/Entries deleted successfully")).build();

	}

	@DELETE
	@Path("/routingentry/{name}")
	public Response deleteRoutingEntryByPathParam(@PathParam(value = "name") String diameterRoutingTableNames) throws DataManagerException {
		return deleteRoutingEntryByQueryParam(diameterRoutingTableNames);
	}

	
	@GET
	@Path("/help")
	public Response getDiameterRoutingTableHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_ROUTING_TABLE);
	}

	
}
