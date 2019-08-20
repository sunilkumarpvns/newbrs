package com.elitecore.elitesm.web.dashboard.ajaxfunction;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.blmanager.core.system.login.LoginBLManager;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigData;
import com.elitecore.passwordutil.PasswordEncryption;
import com.google.gson.Gson;

/**
 * Servlet implementation class FetchWidgetConfiguration
 */
public class ChangeWidgetOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="CheckPasswordValidity";

   
    public ChangeWidgetOrder() {
        super();
    }
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String strColumnName = (request.getParameter("columnId")).trim();
		String strSortArray = (request.getParameter("sortOrderArray").trim());
		
		Long columnId=null;
		response.setContentType("text/plain");
		
		try {
			if(strColumnName !=null && strSortArray !=null){
				if(strColumnName.equals("first")){
					columnId=1L;
				}else{
					columnId=2L;
				}
				
				JSONArray jsonArray=JSONArray.fromObject(strSortArray);
				Long orderNumber=1L;
				for(int i=0;i<jsonArray.size();i++){
					String jsonData=jsonArray.get(i).toString();
					if(jsonData != null &&  jsonData.length() > 0){
						String widgetId=jsonData;
						DashboardBLManager blManager = new DashboardBLManager();
						blManager.changeWidgetOrder(widgetId,columnId,orderNumber);
						orderNumber++;
					}
					
				}
				
			}
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	
	}
}
