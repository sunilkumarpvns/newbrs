package com.elitecore.elitesm.ws.rest.exception;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.passwordutil.NoSuchEncryptionException;

public class NoSuchEncryptionExceptionMapper implements ExceptionMapper<NoSuchEncryptionException>{

	public NoSuchEncryptionExceptionMapper(){}
	
	@Context
	private HttpServletRequest request;
	
	@Override
	public Response toResponse(NoSuchEncryptionException noSuchEncryptionException) {
		return Response.ok(RestExceptionDetail.getInstance(request, noSuchEncryptionException, ResultCode.INTERNAL_ERROR.responseCode)).build();
	}
}
