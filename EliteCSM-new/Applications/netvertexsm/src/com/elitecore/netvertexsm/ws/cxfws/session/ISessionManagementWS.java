package com.elitecore.netvertexsm.ws.cxfws.session;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.elitecore.netvertexsm.ws.cxfws.session.response.SessionResponse;

/**
 * @author kirpalsinh.raj
 *
 */

@WebService(name="SessionManagementWS")
public interface ISessionManagementWS {
	
	@WebMethod(operationName="wsGetSessionByIP")
	SessionResponse wsGetSessionByIP(
			@WebParam(name="sessionIP")String sessionIP,			
			@WebParam(name="sessionType")String sessionType			
	);
   
	@WebMethod(operationName="wsReAuthBySessionByIP")
	SessionResponse wsReAuthBySessionByIP(
			@WebParam(name="sessionIP")String sessionIP			
	);
	
	@WebMethod(operationName="wsReAuthBySubscriberIdentity")
	SessionResponse wsReAuthBySubscriberIdentity(
			@WebParam(name="subscriberIdentity")String subscriberIdentity			
	);
	
	@WebMethod(operationName="wsReAuthByCoreSessionId")
	SessionResponse wsReAuthByCoreSessionId(
			@WebParam(name="coreSessionId")String coreSessionId			
	);

	
	
	
}
