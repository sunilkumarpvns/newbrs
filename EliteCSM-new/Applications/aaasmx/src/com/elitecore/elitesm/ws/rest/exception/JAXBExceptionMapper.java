package com.elitecore.elitesm.ws.rest.exception;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.xml.bind.JAXBException;

import com.elitecore.elitesm.ws.rest.constant.ResultCode;


public class JAXBExceptionMapper implements ExceptionMapper<JAXBException> {

	public JAXBExceptionMapper(){};
	
	@Context
	private HttpServletRequest request;
	
	@Override
	public Response toResponse(JAXBException jaxbException) {
		return Response.ok(RestExceptionDetail.getInstance(request, jaxbException, ResultCode.INTERNAL_ERROR.responseCode)).build();
	}
}
