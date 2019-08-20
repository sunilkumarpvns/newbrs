package com.elitecore.aaa.ws.controller.session.radius;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.ws.common.AAARestConstants;
import com.elitecore.aaa.ws.config.EliteResponse;
import com.elitecore.aaa.ws.config.ResultCode;
import com.elitecore.aaa.ws.controller.session.SessionData;
import com.elitecore.aaa.ws.provider.RadiusSessionDataProvider;
import com.elitecore.aaa.ws.provider.SessionCount;

/**
 * This is end point which serves REST API call for radius session's search using key, total no of active sessions,
 * update session, remove session operations.
 * @author chirag i prajapati
 */

@Path("/")
public class RadiusSessionController {
	
	private AAAServerContext serverContext;
	
	public RadiusSessionController(AAAServerContext serverContext) {
		this.serverContext = serverContext;
	}
	
	private RadiusSessionDataProvider radiusSessionProvider = new RadiusSessionDataProvider(serverContext);
	
	@GET
	@Path("search/{fieldName}/{value}")
	public Response getSessionBySubscriberIdentity(@PathParam("fieldName") String fieldName, @PathParam("value") String value) {
		
		if (fieldName != null && AAARestConstants.FRAMED_IP_ADDRESS.equalsIgnoreCase(fieldName)) {
			return Response.ok(radiusSessionProvider.getAllSessionUsingKey(AAARestConstants.FRAMED_IP_ADDRESS, value)).build();
		}
		
		return Response.ok(new EliteResponse("Key not found", ResultCode.NOT_FOUND.responseCode)).build();
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
