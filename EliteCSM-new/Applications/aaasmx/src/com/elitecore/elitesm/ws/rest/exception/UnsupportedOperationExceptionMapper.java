package com.elitecore.elitesm.ws.rest.exception;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.elitesm.ws.rest.constant.ResultCode;
/**
 * Whenever from REST request latch on the server and <br>if it has found unknown or invalid
 * operation found for module it will handle <b>UnsupportedOperationException</b>
 * the purpose of this class to handle UnsupportedOperationException <br> and to response with appropriate
 * <b>message </b>and<b>code.</b> 
 * @author Tejas.P.Shah
 *
 */
public class UnsupportedOperationExceptionMapper implements ExceptionMapper<UnsupportedOperationException>{

	@Context
    private HttpServletRequest request;
	
	@Override
	public Response toResponse(UnsupportedOperationException unsupportedOperationException) {
		return Response.ok(RestExceptionDetail.getInstance(request, unsupportedOperationException, ResultCode.METHOD_NOT_ALLOWED.responseCode)).build();
	}

	

}
