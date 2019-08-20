package com.elitecore.elitesm.ws.rest.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Some additional parameters are required for authorization, those parameter are part of HTTP request.
 * In order to get those parameters form the request, <b>WebAuthenticationDetailsSource</b> is extended 
 * to a custom class
 * @author animesh christie
 */

public class RESTAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails>{

	@Override
	public AuthenticationDetails buildDetails(HttpServletRequest context) {
		return new AuthenticationDetails(context);
	}
	
}
