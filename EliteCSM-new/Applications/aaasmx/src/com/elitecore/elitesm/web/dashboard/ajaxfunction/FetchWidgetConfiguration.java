package com.elitecore.elitesm.web.dashboard.ajaxfunction;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.util.logger.Logger;
import com.google.gson.Gson;

/**
 * Servlet implementation class FetchWidgetConfiguration
 */
public class FetchWidgetConfiguration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="CheckPasswordValidity";

   
    public FetchWidgetConfiguration() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strWidgetId = (request.getParameter("widgetId")).trim();
	    response.setContentType("application/json");
		DashboardBLManager blManager = new DashboardBLManager();
		try {
			if(Strings.isNullOrEmpty(strWidgetId) == false){
				PageList widgetConfigList=blManager.getAllConfigurationList(strWidgetId);
				String json = new Gson().toJson(widgetConfigList.getListData());
				response.getWriter().write(json);
				request.setAttribute("widgetList", widgetConfigList.getListData());
			}
			 
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	}
}
