package com.elitecore.elitesm.web.dashboard.ajaxfunction;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.json.WidgetData;

/**
 * Servlet implementation class FetchWidgetConfiguration
 */
public class AddNewWidget extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="CheckPasswordValidity";

   
    public AddNewWidget() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strOrderId=(request.getParameter("widgetOrderid")).trim();
		String templateId = (request.getParameter("templateId")).trim();
		String dashboardId = (request.getParameter("dashboardid")).trim();
	    response.setContentType("application/json");
	    
		DashboardBLManager blManager = new DashboardBLManager();
		try {
			if(Strings.isNullOrEmpty(templateId) == false && Strings.isNullOrEmpty(dashboardId) == false 
					&& Strings.isNullOrEmpty(strOrderId) == false){
				
				WidgetData widgetData=new WidgetData();
				widgetData.setDashboardId(dashboardId);
				widgetData.setOrderId(strOrderId);
				widgetData.setWidgetTemplateId(templateId);
				
				String widgetId=blManager.addNewWidget(widgetData);
				response.getWriter().write(widgetId);
			}
			 
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	}
}
