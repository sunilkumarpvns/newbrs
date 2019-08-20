package com.elitecore.elitesm.ws.rest.exception;

import java.net.ConnectException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.elitesm.ws.rest.constant.ResultCode;

public class ConnectExceptionMapper implements ExceptionMapper<ConnectException>{

	public ConnectExceptionMapper(){};
	
	@Context
	private HttpServletRequest request;
	
	@Override
	public Response toResponse(ConnectException connectionException) {
		return Response.ok(RestExceptionDetail.getInstance(request, connectionException, ResultCode.INTERNAL_ERROR.responseCode)).build();
	}

}
