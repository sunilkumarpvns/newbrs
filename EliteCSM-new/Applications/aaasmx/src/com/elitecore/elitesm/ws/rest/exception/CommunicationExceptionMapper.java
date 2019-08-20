package com.elitecore.elitesm.ws.rest.exception;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;


public class CommunicationExceptionMapper implements ExceptionMapper<CommunicationException> {

	public CommunicationExceptionMapper(){};
	
	@Context
	private HttpServletRequest request;
	
	@Override
	public Response toResponse(CommunicationException communicationException) {
		return Response.ok(RestExceptionDetail.getInstance(request, communicationException, ResultCode.INTERNAL_ERROR.responseCode)).build();
	}
}
