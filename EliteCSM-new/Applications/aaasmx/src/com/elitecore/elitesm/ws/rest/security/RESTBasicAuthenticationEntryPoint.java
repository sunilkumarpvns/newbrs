package com.elitecore.elitesm.ws.rest.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
/**
 * Once the BASIC authentication is failed due to wrong username and password, it 
 * throws AuthenticationException from Authentication Handler.
 * 
 * The RESTBasicAuthenticationEntryPoint will fetch the <b>AuthenticationException</b> like <br><br>
 * 
 * <b>UsernameNotFoundException</b><br>
 * <b>BadCredentialsException</b><br>
 * <b>AuthenticationCredentialsNotFoundException</b><br>
 * <b>ProviderNotFoundException</b><br><br>
 * 
 *  <code>
 *  public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
 *  </code> <br><br>
 * 
 *  
 *  This method will fetch AuthenticationException based on exception and send response to the user.<br><br>
 * 
 *  <b>Note:</b> This will indicate that the users credentials are no longer
 *  authorized
 * 
 *  @author nayana.rathod
 *
 */
public class RESTBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
	
	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
		
		/* Authentication failed, send error response */
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         
        PrintWriter writer = response.getWriter();
        writer.println(authException.getMessage());
		
	}
	
	@Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("Spring Security Application");
        super.afterPropertiesSet();
    }
}
