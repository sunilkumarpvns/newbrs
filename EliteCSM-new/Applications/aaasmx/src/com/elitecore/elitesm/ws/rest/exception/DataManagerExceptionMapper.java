package com.elitecore.elitesm.ws.rest.exception;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class DataManagerExceptionMapper implements ExceptionMapper<DataManagerException> {

	public DataManagerExceptionMapper(){}
	
	@Context
	private HttpServletRequest request;

	@Override
	public Response toResponse(DataManagerException dataManagerException) {
		return Response.ok(RestExceptionDetail.getInstance(request, dataManagerException, RestUtitlity.getResultCode(dataManagerException))).build();
	}
}
