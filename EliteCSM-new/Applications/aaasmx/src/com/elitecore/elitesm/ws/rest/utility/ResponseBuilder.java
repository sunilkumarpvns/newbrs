package com.elitecore.elitesm.ws.rest.utility;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.elitecore.elitesm.ws.rest.exception.RestWSExceptionDetail;

public class ResponseBuilder {

	private MediaType mediaType;

	public ResponseBuilder(MediaType mediaType) {
		this.mediaType = mediaType;
	}
	
	public final Response generateResponseMessage(String responseMessage) {
		Message message = new Message();
		message.setMessage(responseMessage);
		return Response.ok(message).type(mediaType).build();
	}
	
	public final Response generateResponse(ListWrapper<DBFieldList> resultData) {
		if(resultData == null) {
			String message = Response.Status.NO_CONTENT +"-" + Response.Status.NO_CONTENT.getStatusCode() + " ,Reason: no records found in db.";
			return generateResponseMessage(message);
		}
		return Response.ok(resultData).type(mediaType).build();
	}

	public final Response generateExceptionResponse(Exception e, boolean isDebugEnable) {
		RestWSExceptionDetail detail = new RestWSExceptionDetail();
		if(isDebugEnable) {
			detail.setErrorMessage(e.getMessage());
			detail.setMetadata(e.toString());
		}else {
			detail.setErrorMessage(e.getMessage());
		}
		return Response.ok(detail,mediaType).build();
	}

	public void setResponseMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
}
