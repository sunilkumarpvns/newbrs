package com.elitecore.elitesm.ws.rest.exception;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.elitecore.elitesm.ws.rest.constant.ResultCode;

public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException>{

    public NotFoundExceptionMapper(){}
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public Response toResponse(NotFoundException notFoundException) {
        return Response.ok(RestExceptionDetail.getInstance(request, notFoundException, ResultCode.NOT_FOUND.responseCode)).build();
    }
}

