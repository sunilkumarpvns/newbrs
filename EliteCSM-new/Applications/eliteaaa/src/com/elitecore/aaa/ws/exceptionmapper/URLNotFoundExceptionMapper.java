package com.elitecore.aaa.ws.exceptionmapper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.aaa.ws.config.EliteResponse;
import com.elitecore.aaa.ws.config.ResultCode;

/**
 * This handles NotFoundException when URL not found, wrap exception message in custom response object and send to end client 
 * @author chirag i. prajapati
 *
 */
public class URLNotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
	
	@Override
	public Response toResponse(NotFoundException arg0) {
		return Response.ok(new EliteResponse("Invalid URL",ResultCode.NOT_FOUND.responseCode)).build();
	}

}
