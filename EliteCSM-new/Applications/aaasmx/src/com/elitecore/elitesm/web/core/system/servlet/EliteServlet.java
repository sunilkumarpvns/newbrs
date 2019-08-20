package com.elitecore.elitesm.web.core.system.servlet;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;
import org.apache.struts.action.ActionServlet;

import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_CONSTANTS;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_STATUS;

public final class EliteServlet extends ActionServlet {
    
	private static final long serialVersionUID = 1L;
	
	private static final String MODULE = "ELITE SERVLET";
	private static  String userName=null;
    public void init() throws ServletException{
    	super.init();    	
    	try {
    		/**
    		 *  *** SM Initialization Process ***
    		 * 
    		 * It will Initialize EliteSMStartupStatus with PENDING and set Status with Success when completed
    		 * If any status is in Process it will shows PROCESSING..
    		 * After Completing Initializing Process it will Redirect user to Login Page.
    		 * */
    		
    		EliteSMStartupStatus.init();    	
    		EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.SEV, SM_MODULE_STATUS.PROCESSING);
            String contextPath = getServletContext().getRealPath("");
            EliteUtility.setSMHome(contextPath);
            EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.SEV, SM_MODULE_STATUS.SUCCESS);
            Logger.logDebug(MODULE,"contextPath :"+contextPath );       
           
			ConfigManager.init();
			
			EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.SMSPC, SM_MODULE_STATUS.SUCCESS);
			
        } catch(Exception e) {
        	Logger.logInfo(MODULE, "Unable to build hibernate sessionfactory");
        }
        
    }
   
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	
    	
    	if (request.getSession().getAttribute("radiusLoginForm") != null || "1".equals(request.getParameter("loginMode"))) {          
            userName=request.getParameter("systemUserName");
            if (userName != null) {
                request.getSession().setAttribute("systemUserName", userName);
                MDC.put("loginUser",userName);
            } else {
                userName=(String)request.getSession().getAttribute("systemUserName");
            }
        }else if(!"2".equals(request.getParameter("loginMode"))){
        	if(ConfigManager.isInitCompleted()){
        		request.getRequestDispatcher("startup.do?loginMode=2").forward(request,response);
        		return;
        	}
    	}
        
    	MDC.put("remoteaddress", request.getRemoteAddr());
        super.process(request,response);
    
   }
}