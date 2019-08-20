package com.elitecore.elitesm.ws.rest.prioritytable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.diameter.prioritytable.PriorityTableConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.diameter.prioritytable.data.PriorityTable;
import com.elitecore.elitesm.datamanager.diameter.prioritytable.data.PriorityTableData;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class PriorityTableController {

	@GET
	public Response get() throws DataManagerException {

		PriorityTableConfBLManager priorityTableBLManager = new PriorityTableConfBLManager();
		
		List<PriorityTableData> priorityTables = priorityTableBLManager.getPriorityTableList();
		
		if (!Collectionz.isNullOrEmpty(priorityTables)) {
			PriorityTable priorityTable = new PriorityTable();
			priorityTable.setPriorityTables(priorityTables);
			return Response.ok(priorityTable).build();
		} else {
			return Response.ok(RestUtitlity.getResponse("Priority Table not found.", ResultCode.NOT_FOUND)).build();
		}
		
	}

	@POST
	public Response create(@Valid PriorityTable priorityTableData) throws DataManagerException {
		return update(priorityTableData);
	}
	

	@PUT
	public Response update(@Valid PriorityTable priorityTableData) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		PriorityTableConfBLManager priorityTableBLManager = new PriorityTableConfBLManager();

		List<PriorityTableData> priorityTables = priorityTableData.getPriorityTables();
		
		priorityTableBLManager.updatePriorityTable(new ArrayList<PriorityTableData>(priorityTables), staffData);
		
		return Response.ok(RestUtitlity.getResponse("Priority Table updated successfully")).build();

	}

	@GET
	@Path("/help")
	public Response getPriorityTableHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.PRIORITY_TABLE);
	}

}
