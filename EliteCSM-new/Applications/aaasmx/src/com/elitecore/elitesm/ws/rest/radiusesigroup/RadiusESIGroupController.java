package com.elitecore.elitesm.ws.rest.radiusesigroup;

import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RadiusESIGroupController {

	private static final String RADIUS_ESI_GROUP = "Radius ESI Group";
	private static final String MODULE = ConfigConstant.RADIUS_ESI_GROUP;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String radiusESIGroupName) throws DataManagerException, JAXBException {

		RadiusESIGroupBLManager radiusESIGroupBLManager = new RadiusESIGroupBLManager();
		RadiusESIGroupData radiusESIGroup = radiusESIGroupBLManager.getRadiusESIGroupByName(radiusESIGroupName);

		return Response.ok(convertXmlToData(radiusESIGroup)).build();
	}

	private RadiusEsiGroupConfigurationData convertXmlToData(RadiusESIGroupData radiusESIGroup) throws JAXBException {

        String xmlDatas = new String(radiusESIGroup.getEsiGroupDataXml());
        StringReader stringReader =new StringReader(xmlDatas.trim());

        RadiusEsiGroupConfigurationData esiConfigurationData = ConfigUtil.deserialize(stringReader, RadiusEsiGroupConfigurationData.class);

        esiConfigurationData.setName(radiusESIGroup.getName());
        esiConfigurationData.setDescription(radiusESIGroup.getDescription());

        esiConfigurationData.setRedundancyMode(esiConfigurationData.getRedundancyMode());
        esiConfigurationData.setEsiType(esiConfigurationData.getEsiType());
        esiConfigurationData.setStateful(esiConfigurationData.getStateful());
        esiConfigurationData.setSwitchBackEnable(esiConfigurationData.getSwitchBackEnable());

        esiConfigurationData.setActivePassiveEsiList(esiConfigurationData.getActivePassiveEsiList());
        esiConfigurationData.setPrimaryEsiList(esiConfigurationData.getPrimaryEsiList());
        esiConfigurationData.setFailOverEsiList(esiConfigurationData.getFailOverEsiList());

        return esiConfigurationData;
    }

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String radiusESIGroupName) throws DataManagerException, JAXBException {
		return getByNameFromQuery(radiusESIGroupName);
	}

	@POST
	public Response create(@Valid RadiusEsiGroupConfigurationData radiusESIGroupData) throws DataManagerException, JAXBException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		RadiusESIGroupBLManager radiusESIGroupBLManager = new RadiusESIGroupBLManager();
		
		radiusESIGroupBLManager.createRadiusESIGroup(convertDataToXml(radiusESIGroupData), staffData);

		return Response.ok(RestUtitlity.getResponse("Radius ESI Group created successfully")).build();
	}

    private RadiusESIGroupData convertDataToXml(RadiusEsiGroupConfigurationData radiusESIGroupData) throws JAXBException {

        RadiusESIGroupData radEsiGrpData = new RadiusESIGroupData();

        radEsiGrpData.setName(radiusESIGroupData.getName());
        radEsiGrpData.setDescription(radiusESIGroupData.getDescription());

        String serialize = ConfigUtil.serialize(RadiusEsiGroupConfigurationData.class, radiusESIGroupData);

        radEsiGrpData.setEsiGroupDataXml(serialize.getBytes());

	    return radEsiGrpData;
    }

    @POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<RadiusEsiGroupConfigurationData> radiusESIGroupDatas, @Context UriInfo uri) throws DataManagerException, JAXBException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		RadiusESIGroupBLManager radiusESIGroupBLManager = new RadiusESIGroupBLManager();
		
		List<RadiusEsiGroupConfigurationData> radiusESIGroups = radiusESIGroupDatas.getList();
		List<RadiusESIGroupData> esiGroupDataList = new ArrayList<>();

		for (RadiusEsiGroupConfigurationData radiusESIGroupData : radiusESIGroups) {
            RadiusESIGroupData radiusESIGrpData = convertDataToXml(radiusESIGroupData);
            esiGroupDataList.add(radiusESIGrpData);
        }
		Map<String, List<Status>> responseMap = radiusESIGroupBLManager.createRadiusESIGroup(esiGroupDataList, staffData, URLInfo.isPartialSuccess(uri));

		return Response.ok(RestUtitlity.getResponse(RADIUS_ESI_GROUP, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isPartialSuccess(uri))).build();
	}

	@PUT
	public Response updateByQueryParam(@Valid RadiusEsiGroupConfigurationData radiusESIGroup, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String radiusESIGroupName) throws DataManagerException, JAXBException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		RadiusESIGroupBLManager radiusESIGroupBLManager = new RadiusESIGroupBLManager();

		radiusESIGroupBLManager.updateRadiusESIGroupByName(convertDataToXml(radiusESIGroup), staffData, radiusESIGroupName);

		return Response.ok(RestUtitlity.getResponse("Radius ESI Group updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid RadiusEsiGroupConfigurationData radiusESIGroup, @PathParam(value = "name") String radiusESIGroupName) throws DataManagerException, JAXBException {
		return updateByQueryParam(radiusESIGroup, radiusESIGroupName);
	}

	@DELETE
	public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String radiusESIGroupName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		RadiusESIGroupBLManager radiusESIGroupBLManager = new RadiusESIGroupBLManager();

		List<String> radiusESIGroupNames = Arrays.asList(radiusESIGroupName.split(","));

		radiusESIGroupBLManager.deleteRadiusESIGroupByName(radiusESIGroupNames, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Radius ESI Group(s) deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String radiusESIGroupName) throws DataManagerException {
		return deleteByQueryParam(radiusESIGroupName);
	}

	@GET
	@Path("/help")
	public Response getRadiusESIGroupHelp() throws IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RADIUS_ESI_GROUP);
	}
}
