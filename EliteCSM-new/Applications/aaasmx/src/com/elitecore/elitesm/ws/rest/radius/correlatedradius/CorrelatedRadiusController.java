package com.elitecore.elitesm.ws.rest.radius.correlatedradius;

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.radius.correlatedradius.CorrelatedRadiusBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.radius.correlatedradius.data.CorrelatedRadiusData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Path("/")
public class CorrelatedRadiusController {

    private static final String CORRELATED_RADIUS = "Correlated Radius";

    @GET
    public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER)
                                       @QueryParam(value = "name") String correlatedRadiusName) throws DataManagerException {

        CorrelatedRadiusBLManager correlatedRadiusBLManager = new CorrelatedRadiusBLManager();
        CorrelatedRadiusData correlatedRadiusData = correlatedRadiusBLManager.getCorrelatedRadiusDataByName(correlatedRadiusName);
        return Response.ok(correlatedRadiusData).build();
    }

    @GET
    @Path("/{name}")
    public Response getByNameFromPath(@PathParam(value = "name") String correlatedRadiusName) throws DataManagerException {
        return getByNameFromQuery(correlatedRadiusName);
    }

    @POST
    public Response create(@Valid CorrelatedRadiusData correlatedRadiusData) throws DataManagerException {

        StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CorrelatedRadiusBLManager correlatedRadiusBlManager = new CorrelatedRadiusBLManager();

        ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();

        correlatedRadiusData.setAuthEsiId(externalSystemInterfaceBLManager.getRadiusESIIdByName(correlatedRadiusData.getAuthEsiName()));

        correlatedRadiusData.setAcctEsiId(externalSystemInterfaceBLManager.getRadiusESIIdByName(correlatedRadiusData.getAcctEsiName()));

        correlatedRadiusBlManager.create(correlatedRadiusData, staffData);

        return Response.ok(RestUtitlity.getResponse("Correlated Radius created successfully")).build();

    }

    @POST
    @Path("/bulk")
    public Response create(@Valid ListWrapper<CorrelatedRadiusData> correlatedRadiusDatas, @Context UriInfo uri) throws DataManagerException {

        StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CorrelatedRadiusBLManager correlatedRadiusBlManager = new CorrelatedRadiusBLManager();

        ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();

        for (CorrelatedRadiusData correlatedRadiusData : correlatedRadiusDatas.getList()) {

            correlatedRadiusData.setAuthEsiId(externalSystemInterfaceBLManager.getRadiusESIIdByName(correlatedRadiusData.getAuthEsiName()));

            correlatedRadiusData.setAcctEsiId(externalSystemInterfaceBLManager.getRadiusESIIdByName(correlatedRadiusData.getAcctEsiName()));
        }

        Map<String, List<Status>> responseMap = correlatedRadiusBlManager.createCorrelatedRadius(correlatedRadiusDatas.getList(), staffData, URLInfo.isPartialSuccess(uri));

        return Response.ok(RestUtitlity.getResponse(CORRELATED_RADIUS, "(s) created successfully", responseMap,
                URLInfo.isPartialSuccess(uri), URLInfo.isPartialSuccess(uri))).build();
    }

    @PUT
    public Response updateByQueryParam(@Valid CorrelatedRadiusData correlatedRadiusData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String radiusESIGroupName) throws DataManagerException {

        StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CorrelatedRadiusBLManager correlatedRadiusBlManager = new CorrelatedRadiusBLManager();

        ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();

        correlatedRadiusData.setAuthEsiId(externalSystemInterfaceBLManager.getRadiusESIIdByName(correlatedRadiusData.getAuthEsiName()));

        correlatedRadiusData.setAcctEsiId(externalSystemInterfaceBLManager.getRadiusESIIdByName(correlatedRadiusData.getAcctEsiName()));

        correlatedRadiusBlManager.updateCorrelatedRadiusByName(correlatedRadiusData, staffData, radiusESIGroupName);

        return Response.ok(RestUtitlity.getResponse("Correlated Radius updated successfully")).build();

    }

    @PUT
    @Path("/{name}")
    public Response updateByPathParam(@Valid CorrelatedRadiusData correlatedRadiusData, @PathParam(value = "name") String radiusESIGroupName) throws DataManagerException {
        return updateByQueryParam(correlatedRadiusData, radiusESIGroupName);
    }

    @DELETE
    public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String correlatedRadiusName) throws DataManagerException {

        StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CorrelatedRadiusBLManager correlatedRadiusBlManager = new CorrelatedRadiusBLManager();

        List<String> correlatedRadiusNames = Arrays.asList(correlatedRadiusName.split(","));

        correlatedRadiusBlManager.deleteCorrelatedRadiusByName(correlatedRadiusNames, staffData);

        return Response.ok(RestUtitlity.getResponse("Correlated Radius(s) deleted successfully")).build();

    }

    @DELETE
    @Path("/{name}")
    public Response deleteByPathParam(@PathParam(value = "name") String correlatedRadiusName) throws DataManagerException {
        return deleteByQueryParam(correlatedRadiusName);
    }

    @GET
    @Path("/help")
    public Response getRadiusESIGroupHelp() throws IllegalArgumentException, IOException {
        return RestUtitlity.getHelp(RestHelpConstant.CORRELATED_RADIUS);
    }
}
