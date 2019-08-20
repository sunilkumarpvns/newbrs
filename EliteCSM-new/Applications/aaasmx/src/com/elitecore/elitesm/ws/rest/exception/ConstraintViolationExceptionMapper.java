package com.elitecore.elitesm.ws.rest.exception;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.elitesm.ws.rest.constant.ResultCode;

public class ConstraintViolationExceptionMapper implements ExceptionMapper<org.hibernate.exception.ConstraintViolationException> {
	
	@Context
	private HttpServletRequest request;

	@Override
	public Response toResponse(org.hibernate.exception.ConstraintViolationException constraintViolationException) {
		return Response.ok(RestExceptionDetail.getInstance(request, constraintViolationException, ResultCode.INTERNAL_ERROR.responseCode)).build();
	}
	
}
