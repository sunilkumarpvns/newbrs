package com.elitecore.elitesm.ws.rest.exception;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;

public class UnidentifiedServerInstanceExceptionMapper implements ExceptionMapper<UnidentifiedServerInstanceException>{

	public UnidentifiedServerInstanceExceptionMapper(){};
	
	@Context
	private HttpServletRequest request;
	
	@Override
	public Response toResponse(UnidentifiedServerInstanceException unidentifiedServerInstanceException) {
		return Response.ok(RestExceptionDetail.getInstance(request, unidentifiedServerInstanceException, ResultCode.INTERNAL_ERROR.responseCode)).build();
	}
}
