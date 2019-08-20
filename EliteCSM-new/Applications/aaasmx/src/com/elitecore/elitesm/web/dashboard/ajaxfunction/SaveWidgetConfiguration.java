package com.elitecore.elitesm.web.dashboard.ajaxfunction;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigData;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigValueData;

/**
 * Servlet implementation class RetriveCipherSuit
 */
public class SaveWidgetConfiguration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="CheckPasswordValidity";

   
    public SaveWidgetConfiguration() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String widgetId=request.getParameter("widgetid");
		response.setContentType("text/html");
		DashboardBLManager blManager = new DashboardBLManager();
		try {
			blManager.deleteWidgetConfiguration(widgetId);
			
			Map params = request.getParameterMap();
			Iterator i = params.keySet().iterator();

			while ( i.hasNext() )
			  {
			    String key = (String) i.next();
			    String value = ((String[]) params.get( key ))[ 0 ];
			    if(key.trim().equals("ELITEAAAINSTANCES")){
			    	String[] strArray = (String[]) params.get(key);
			    	value = "";
			    	for(String str : strArray){
			    		if(str != null && str.length() > 0){
			    			if(value.length() > 0){
			    				value = value + "," + str;
				    		}else{
				    			value = str;
				    		}
			    		}
			    		
			    	}
			    }
			   
			    if(!(key.equals("widgetid"))){
			    	blManager.saveWidgetConfig(key,value,widgetId);
			    }
			  }
			response.getWriter().write("save");
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	
	}
}
