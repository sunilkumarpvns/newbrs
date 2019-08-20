package com.elitecore.elitesm.ws.rest.utility;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.elitesm.ws.rest.security.AuthenticationDetails;

/**
 * Interceptor performs the following tasks<br>
 * 
 * <b>1)</b> Validates the version of request, only the version specified in "restVersions" property of 
 * "com.elitecore.elitesm.util.constants.ConfigConstant" are valid, any other version URL will result into 404<br>
 * <b>2)</b> Intercept the incoming message and based on the <b>operation</b> parameter in request, changes HTTP method 
 * from POST to the required method.
 * 
 *<pre>for Example:
 *  if <b>operation</b> = "search", convert to <b>GET</b>
 *  if <b>operation</b> = "update", convert to <b>PUT</b>
 *  if <b>operation</b> = "delete", convert to <b>DELETE</b>
 *  if <b>operation</b> = "create", no changes will be made in HTTP method
 *</pre>
 * @author nayana.rathod
 */
public class MessageInInterceptor extends AbstractPhaseInterceptor<Message>{

	public MessageInInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		
		Object obj = SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		if(obj instanceof AuthenticationDetails){
			AuthenticationDetails customAuthenticationDetails = (AuthenticationDetails) obj;
			
			if(customAuthenticationDetails.getRestACLParameter().equals("invalidurl")){
				throw new NotFoundException("Invalid URL");
			} else if(customAuthenticationDetails.getRestACLParameter().equals("invalidoperation")){
				throw new UnsupportedOperationException("Invalid Operation");
			}
		}
		
		String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
		
		HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
		
		if("POST".equalsIgnoreCase(httpMethod)){
			
			String operation = request.getParameter("operation");
			
			if("search".equalsIgnoreCase(operation)){
				message.put(Message.HTTP_REQUEST_METHOD, "GET");
			} else if("create".equalsIgnoreCase(operation)){
				message.put(Message.HTTP_REQUEST_METHOD, "POST");
			} else if("delete".equalsIgnoreCase(operation)){
				message.put(Message.HTTP_REQUEST_METHOD, "DELETE");
			} else if("update".equalsIgnoreCase(operation)){
				message.put(Message.HTTP_REQUEST_METHOD, "PUT");
			} else {
				//TODO Intentionally kept blank
			}
		}
		
		//Remote address for auditing
		MDC.put("restremoteaddress", request.getRemoteAddr());
	}
}
