package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.google.gson.Gson;

/**
 * Servlet implementation class GetTGPPDriverDataServlet
 */
public class GetTGPPDriverDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="GetTGPPDriverDataServlet";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTGPPDriverDataServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DriverBLManager driverBlManager = new DriverBLManager();
		List<DriverInstanceData> driverList = null;
		response.setContentType("text/plain");
		
		try {
			driverList = driverBlManager.getDriverInstanceList(ServiceTypeConstants.NAS_ACCT_APPLICATION);
			String json = new Gson().toJson(driverList);

		    response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(json);
			
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}
	}
}
