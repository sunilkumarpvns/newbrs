package com.elitecore.elitesm.web.dashboard.ajaxfunction;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigData;

/**
 * Servlet implementation class RetriveCipherSuit
 */
public class WidgetConfiguration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="CheckPasswordValidity";

   
    public WidgetConfiguration() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strWidgetId = (request.getParameter("widgetId")).trim();
		String jsonData = (request.getParameter("jsonData")).trim();
		Long widgetId=Long.parseLong(strWidgetId);
		response.setContentType("text/plain");
		try {
			if(widgetId != null && jsonData != null){
				JSONArray jsonArray=JSONArray.fromObject(jsonData);
				for(int i=0;i<jsonArray.size();i++){
						DashboardBLManager blManager = new DashboardBLManager();
					
						JSONObject json_data = jsonArray.getJSONObject(i);
						
						WidgetConfigData widgetConfigData=new WidgetConfigData();
						String parameterId = json_data.getString("parameterId");
						String parameterKey= json_data.getString("parameterKey");
						String paramWidgetId= json_data.getString("widgetId");
						String parameterValue=json_data.getString("parameterValue");
						
						
						widgetConfigData.setParameterId(parameterId);
						widgetConfigData.setParameterKey(parameterKey);
						widgetConfigData.setWidgetId(paramWidgetId);
						widgetConfigData.setParameterValue(parameterValue);
						
						blManager.updateWidgetConfig(widgetConfigData);
					}
				}
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	
	}
}
