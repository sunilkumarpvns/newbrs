package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.elitecore.elitesm.util.constants.SystemLoginConstants;
import com.sun.java.jax_rpc_ri.internal.ArrayList;

/**
 * @author aneri.chavda
 * This filter is used to redirect the user back to Change Password Page,
 * if the user doesn't change the password on first login.
 *
 */
public class LoginValidatorFilter implements Filter {

	private String MODULE = "LOGIN-VALIDATOR-FILTER";
	FilterConfig filterConfig = null;
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		
		String changePwdOnFirstLogin = (String) httpRequest.getSession().getAttribute(SystemLoginConstants.CHANGEPWDONFIRSTLOGIN);
		
		if(changePwdOnFirstLogin == null || changePwdOnFirstLogin.equalsIgnoreCase("false") ){
			chain.doFilter(request, response);
		}else{
			String skipPasswordCheck = httpRequest.getParameter("skipPasswordCheck");
			if(skipPasswordCheck != null && skipPasswordCheck.equalsIgnoreCase("yes") ){
				chain.doFilter(request,response);
			}else{
				Timestamp lastChangePasswordDate = (Timestamp) httpRequest.getSession().getAttribute("lastChangePasswordDate");
				if(lastChangePasswordDate == null ){
					httpRequest.getRequestDispatcher("/initChangePassword.do").forward(request,response);
					return;
				}else{
					chain.doFilter(request,response);
				}
			}
		}
	}
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

}
