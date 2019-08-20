package com.elitecore.ssp.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;
import org.apache.struts.action.ActionServlet;

import com.elitecore.ssp.util.EliteUtility;
import com.elitecore.ssp.util.constants.SessionAttributeKeyConstant;
import com.elitecore.ssp.util.logger.Logger;

/**
 * Servlet implementation class NetvertexSSPServlet
 */
public final class EliteActionServlet extends ActionServlet {
	
	private static final String MODULE =EliteActionServlet.class.getSimpleName();
	
	public void init() throws ServletException{
    	super.init();
    	   	try {
                String contextPath = getServletContext().getRealPath("");
                EliteUtility.setSSPHome(contextPath);
                System.out.println("-----------------------------------------------------------------------------------------------------------------");
                System.out.println("                                                                                                                 ");
                System.out.println("                                      NetVertex Service Selection Portal                                         ");
                System.out.println("                                                                                                                 ");
                System.out.println("-----------------------------------------------------------------------------------------------------------------");
                Logger.logDebug(MODULE,"contextPath :"+contextPath );
            } catch(Exception e) {
            	Logger.logTrace(MODULE, e);
            }
               
    }
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	MDC.put("remoteaddress", request.getRemoteAddr());
    	Logger.logDebug(MODULE, "Enter in Process method of EliteActionServlet");
    	if(request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER) != null || (request.getParameter("loginMode")!=null && request.getParameter("loginMode").equals("1"))){    		
    		super.process(request,response);
    	}else{
        	request.getRequestDispatcher("/jsp/login.jsp").forward(request,response);
    	}
    }
}
