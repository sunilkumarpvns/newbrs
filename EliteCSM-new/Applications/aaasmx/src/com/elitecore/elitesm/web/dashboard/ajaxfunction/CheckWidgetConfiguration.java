package com.elitecore.elitesm.web.dashboard.ajaxfunction;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.util.logger.Logger;

/**
 * Servlet implementation class FetchWidgetConfiguration
 */
public class CheckWidgetConfiguration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="CheckPasswordValidity";

   
    public CheckWidgetConfiguration() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strWidgetId = (request.getParameter("widgetId")).trim();
		response.setContentType("application/text");
		DashboardBLManager blManager = new DashboardBLManager();
		try {
			if( Strings.isNullOrEmpty(strWidgetId) == false){
				String isExist=blManager.checkWidgetConfigurations(strWidgetId);
				response.getWriter().write(isExist.toString());
			}
			 
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	}
}
