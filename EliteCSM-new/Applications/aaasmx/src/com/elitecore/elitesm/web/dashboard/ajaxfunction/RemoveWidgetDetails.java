package com.elitecore.elitesm.web.dashboard.ajaxfunction;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigData;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigValueData;

/**
 * Servlet implementation class RetriveCipherSuit
 */
public class RemoveWidgetDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="CheckPasswordValidity";

   
    public RemoveWidgetDetails() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strWidgetId = (request.getParameter("widgetId")).trim();
		response.setContentType("text/plain");
		try {
			if(Strings.isNullOrEmpty(strWidgetId) == false){
				DashboardBLManager blManager = new DashboardBLManager();
				blManager.deleteWidget(strWidgetId);
			}
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	
	}
}
