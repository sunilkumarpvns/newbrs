package com.elitecore.elitesm.web.dashboard.ajaxfunction;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.json.WidgetOrderData;

/**
 * Servlet implementation class FetchWidgetConfiguration
 */
public class GetWidgetOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="CheckPasswordValidity";

   
    public GetWidgetOrder() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strTemplateId = (request.getParameter("templateId")).trim();
		String strDashboardId = (request.getParameter("dashboardid")).trim();
	    response.setContentType("application/json");
	    
		DashboardBLManager blManager = new DashboardBLManager();
		try {
			if(Strings.isNullOrEmpty(strTemplateId) == false && Strings.isNullOrEmpty(strDashboardId) == false){
				
				Long nextOrderNumber=blManager.getWidgetOrder(strTemplateId, strDashboardId);
				
				//Widget Order Data
				
				WidgetOrderData widgetOrderData=new WidgetOrderData();
				widgetOrderData.setColumnNumber(1L);
				widgetOrderData.setDashboardId(strDashboardId);
				widgetOrderData.setLayout(2L);
				widgetOrderData.setOrderNumber(nextOrderNumber);
				
				Long nextOrderId=blManager.addOrderData(widgetOrderData);
			
				String strOrderId = String.valueOf(nextOrderId);
				response.getWriter().write(strOrderId);
			}
			 
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	}
}
