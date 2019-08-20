package com.elitecore.netvertexsm.web.core.util;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.elitecore.netvertexsm.util.constants.SystemLoginConstants;

/**
 * @author aneri.chavda
 * This filter is used to redirect the user back to Change Password Page,
 * if the user doesn't change the password on first login.
 *
 */
public class LoginValidatorFilter implements Filter {

	FilterConfig filterConfig = null;
	public void destroy() { }

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try{
				HttpServletRequest httpRequest = (HttpServletRequest)request;
				String skipPasswordCheck = httpRequest.getParameter("skipPasswordCheck");
		
				String changePwdOnFirstLogin = (String) httpRequest.getSession().getAttribute(SystemLoginConstants.CHANGEPWDONFIRSTLOGIN);
		 
        		if(skipPasswordCheck != null && skipPasswordCheck.equalsIgnoreCase("1") ){
        			chain.doFilter(request,response);
        		}else{
        			
        			Timestamp passwordChangeDate =(Timestamp) httpRequest.getSession().getAttribute("passwordChangeDate");
        			
        			if(("true".equalsIgnoreCase(changePwdOnFirstLogin) && passwordChangeDate==null)){
        				/* If changePasswordOnFirstLogin flag value is true and user is logging first time 
        				 * then take user to change password jsp page until user change the password */        				
        				httpRequest.getRequestDispatcher("/initChangePassword.do").forward(request,response);
        			} 
        			
        			String PASSWORD_EXPIRY_FLAG = (String)httpRequest.getSession().getAttribute("PASSWORD_EXPIRY_FLAG");
        			if(SystemLoginConstants.YES.equalsIgnoreCase(PASSWORD_EXPIRY_FLAG)){
        				/* If password is expired then take the user to the change password jsp page */
        				httpRequest.getRequestDispatcher("/initChangePassword.do").forward(request,response);
        			} else {
        				
        				chain.doFilter(request,response);
        			}
        		}
		}catch(Exception ex){}
	}
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

}
