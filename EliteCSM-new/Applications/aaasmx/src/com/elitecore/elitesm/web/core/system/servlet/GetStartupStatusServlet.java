package com.elitecore.elitesm.web.core.system.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Maps;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_CONSTANTS;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_STATUS;

/**
 * Servlet implementation class GetStartupStatusServlet
 */
public class GetStartupStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = GetStartupStatusServlet.class.getSimpleName();
	
    public GetStartupStatusServlet() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LinkedHashMap<SM_MODULE_CONSTANTS, SM_MODULE_STATUS> eliteSMStartupStatusMap = EliteSMStartupStatus.getModuleStatusMap();
		 JSONObject eliteSMStartupStatusJson = new JSONObject();
		if(Maps.isNullOrEmpty(eliteSMStartupStatusMap) == false){
			for(SM_MODULE_CONSTANTS key : eliteSMStartupStatusMap.keySet()){
				String label =key.label;
				String status =eliteSMStartupStatusMap.get(key).status;
				eliteSMStartupStatusJson.put(label, status);
			}
		}
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(eliteSMStartupStatusJson);
		out.close();
	}

}
