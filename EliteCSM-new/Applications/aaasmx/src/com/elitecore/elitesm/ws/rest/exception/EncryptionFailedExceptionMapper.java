package com.elitecore.elitesm.ws.rest.exception;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.passwordutil.EncryptionFailedException;

public class EncryptionFailedExceptionMapper implements ExceptionMapper<EncryptionFailedException> {
	
	public EncryptionFailedExceptionMapper(){};
	
	@Context
	private HttpServletRequest request;
	
	@Override
	public Response toResponse(EncryptionFailedException encryptionFailedException) {
		return Response.ok(RestExceptionDetail.getInstance(request, encryptionFailedException, ResultCode.INTERNAL_ERROR.responseCode)).build();
	}
}
