package com.elitecore.aaa.ws.exceptionmapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.xml.bind.JAXBException;

import com.elitecore.aaa.ws.config.EliteResponse;
import com.elitecore.aaa.ws.config.ResultCode;

/**
 * This handles JAXBException related to JAXB parsing, wrap exception message in custom response object and send to end client 
 * @author chirag i. prajapati
 */
public class JaxbExceptionMapper implements ExceptionMapper<JAXBException>{

	@Override
	public Response toResponse(JAXBException arg0) {
		return Response.ok(new EliteResponse(arg0.getMessage(),ResultCode.INTERNAL_ERROR.responseCode)).build();
	}

}
