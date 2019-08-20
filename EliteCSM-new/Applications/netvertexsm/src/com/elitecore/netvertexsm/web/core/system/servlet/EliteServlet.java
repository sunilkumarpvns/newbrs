package com.elitecore.netvertexsm.web.core.system.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionServlet;

import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.conf.NetvertexSMConfiguration;
import com.elitecore.netvertexsm.util.driver.CDRDriverManager;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;

public final class EliteServlet extends ActionServlet {

	private static final String MODULE = "ELITE SERVLET";

	private NetvertexSMConfiguration configuration;
	private boolean isInitFailed = false;
	private String cdrInitFailedMessage = null;

	public void init() throws ServletException {
		super.init();

		try {
			
			/*
			 * String contextPath = getServletContext().getRealPath("");
			 * EliteUtility.setSMHome(contextPath);
			 * HibernateSessionFactory.setDBPropsLocation
			 * (contextPath+"/WEB-INF/database.properties");
			 */

			configuration = new NetvertexSMConfiguration();
			configuration.readConfiguration();
			ConfigManager.init();
			
			//FIXME temporary disable Web service configuration as no CDR datasource will be configured with WS -- ishani.bhatt
			/*try {

				CDRDriverManager.init(configuration.getDriverConfigurations());

			} catch (Throwable ex) {
				Logger.logError(MODULE, "Error while initializing CDRDriverManager configuration. Reason: " + ex.getMessage());				
				Logger.logTrace(MODULE,ex);
				cdrInitFailedMessage = "CDR-Driver Initialization failed<br>&nbsp;&nbsp;<span style='color:red;'>Reason:</span> " + ex.getMessage();
				isInitFailed = true;
				return;
			}*/
			// Initializing memory informer to fetch server memory usae
			// information for dashboard
			// ServerMemoryInformer.getInstance().init();
			
			//FIXME temporary stop the initializing PolicyGroup caching --ishani.bhatt
			//PolicyGroupCache.getInstance().init();

		} catch (Exception e) {
			Logger.logError(MODULE, "Problem while Reading configuration : "
					+ e.getMessage());
			Logger.logTrace(MODULE, e);
		}
	}

	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		if (isInitFailed == true) {
			request.getSession().setAttribute("errorDetails", cdrInitFailedMessage);
			request.getRequestDispatcher("/jsp/core/response/InitError.jsp").forward(request, response);
		} else {
			EliteUtility.setClientIP(request);
			if (request.getSession().getAttribute("radiusLoginForm") != null || (request.getParameter("loginMode") != null && request .getParameter("loginMode").equals("1"))) {
				EliteUtility.setCurrentUserId(request);
				super.process(request, response);
			} else {
				/* request.setAttribute("errorCode", "SESSIONEXPIRED"); */
				request.getRequestDispatcher("/Login.jsp").forward(request, response);
			}
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		CDRDriverManager.stop();
		//PolicyGroupCache.getInstance().stop();
	}
}