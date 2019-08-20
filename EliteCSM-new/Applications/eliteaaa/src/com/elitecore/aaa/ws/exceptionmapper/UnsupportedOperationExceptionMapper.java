package com.elitecore.aaa.ws.exceptionmapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.aaa.ws.config.EliteResponse;
import com.elitecore.aaa.ws.config.ResultCode;

/**
 * <p>Whenever from REST request latch on the server and <br>if it has found unknown or invalid
 * operation found for module it will handle <b>UnsupportedOperationException</b>
 * the purpose of this class to handle UnsupportedOperationException <br> and to response with appropriate
 * <b>message </b>and<b>code.</b></p> 
 * @author chirag i. prajapati
 *
 */
public class UnsupportedOperationExceptionMapper implements ExceptionMapper<UnsupportedOperationException>{

	
	@Override
	public Response toResponse(UnsupportedOperationException unsupportedOperationException) {
		return Response.ok(new EliteResponse(unsupportedOperationException.getMessage(), ResultCode.OPERATION_NOT_SUPPORTED.responseCode)).build();
	}

}
