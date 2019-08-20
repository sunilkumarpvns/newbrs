package com.elitecore.nvsmx.system.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 *
 * HTTP security headers�provide yet another layer of security by helping to mitigate attacks and security vulnerabilities. 
 * Implemented this functionality through the headers that can make your more versatile or secure application. <br><br>
 *
 * Whatever parameters are configured in <init-param> tag of PortalSecurityFilter in web.xml; those parameters would be added in response header <br><br>
 *
 * HTTP Header Parameters are : <br/>
 *
 * 1. X-Frame-Option  <br>
 * 2. X-XSS-Protection <br>
 * 3. X-Content-Type-Options<br>
 * 4. Strict-Transport-Security<br>
 *
 * @author aditya.shrivastava
 *
 */

public class PortalSecurityFilter implements Filter {

    FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;

        Enumeration attrs =  filterConfig.getInitParameterNames();

        while(attrs.hasMoreElements()) {

            String headerName = (String) attrs.nextElement();
            String headerValue = filterConfig.getInitParameter(headerName);

            res.setHeader(headerName, headerValue);
        }
        res.setHeader("Cache-control", "no-cache, no-store");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "-1");


        chain.doFilter(request, res);
    }
}
