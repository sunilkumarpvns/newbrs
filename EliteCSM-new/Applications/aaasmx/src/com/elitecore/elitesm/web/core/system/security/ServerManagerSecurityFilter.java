package com.elitecore.elitesm.web.core.system.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.util.logger.Logger;

public class ServerManagerSecurityFilter implements Filter{

	static final String MODULE = "[AAASMX-SECURITY-FILTER]";
	FilterConfig filterConfig = null;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		 Logger.logDebug(MODULE, "Filter Applied.");
		 this.filterConfig = filterConfig;
		
	}
	
	@Override
	public void destroy() {
		this.filterConfig = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req= (HttpServletRequest) request;
			if(req.getParameter("isRequestFromBacktoHome") != null ){
				 if( req.getParameter("isRequestFromBacktoHome").trim().equals("true")){
					ESAPISecurityPlugin.elapsedTimeMap.clear();
					ESAPISecurityPlugin.blockIpList.clear();
				 }
			}
			
		chain.doFilter(new RequestWrapper((HttpServletRequest) request, (HttpServletResponse) response), response);
	}

	

}
