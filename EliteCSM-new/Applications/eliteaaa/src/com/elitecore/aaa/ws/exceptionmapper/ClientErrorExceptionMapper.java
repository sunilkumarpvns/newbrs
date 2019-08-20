package com.elitecore.aaa.ws.exceptionmapper;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.aaa.ws.config.EliteResponse;

public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException>{
	
	@Override
	public Response toResponse(ClientErrorException arg0) {
		return Response.ok(new EliteResponse(arg0.getMessage() ,String.valueOf(arg0.getResponse().getStatusInfo().getStatusCode()))).build();
	}
}
