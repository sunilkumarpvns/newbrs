package com.elitecore.aaa.ws.controller.session.diameter;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.elitecore.aaa.ws.controller.session.SessionData;
import com.elitecore.aaa.ws.provider.SessionCount;

/**
 * <p>This is responsible for serving REST call for diameter session's search using key, total no of active sessions,
 * update session, remove session operations.</p>
 * @author chirag i prajapati
 */

@Path("/")
public class DiameterSessionController {
	
	@GET
	@Path("search/{fieldName}/{value}")
	public Response getSessionBySubscriberIdentity(@PathParam("fieldName") String fieldName,@PathParam("value") String value) {
		return Response.ok(new SessionData(fieldName, value, "DIAMETER")).build();
	}
	
	@GET
	@Path("count")
	public Response getTotalSessionCount() {
		SessionCount sessionCount = new SessionCount();
		sessionCount.setTotalCount(1);
		return Response.ok(sessionCount).build();
	}
	
	@POST
	@Path("update")
	public Response updateSession(SessionData sessionDetail) {
		return Response.ok("Okay updated").build();
	}
	
	
	@DELETE
	@Path("remove")
	public Response removeSession() {
		return Response.ok("Okay Deleted").build();
	}
}
