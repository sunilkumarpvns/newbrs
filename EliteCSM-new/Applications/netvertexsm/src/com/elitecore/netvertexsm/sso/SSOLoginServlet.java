package com.elitecore.netvertexsm.sso;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.blmanager.core.system.login.LoginBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.core.system.profilemanagement.ProfileManager;
/**
 * Servlet implementation class SSOLoginServlet
 */
public class SSOLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SSOLoginServlet";

    public SSOLoginServlet() {
	super();
    }

    public void init(ServletConfig config) throws ServletException {
	super.init(config);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	Logger.logDebug(MODULE, "Method called doGet()");
	
	String strFirstLoginPath ="/serverGroupManagement.do?method=initSearch";
	SystemLoginForm systemLoginForm = new SystemLoginForm();
	
	ServletContext sctx = ((HttpServletRequest) request).getSession().getServletContext();
	
	String pdContextPath = sctx.getInitParameter("pdContextPath");
	
	if( Strings.isNullOrBlank(pdContextPath) == false ){
		ServletContext pdContext = ((HttpServletRequest) request).getSession().getServletContext().getContext("/"+pdContextPath);
		if(pdContext!=null){
			Logger.logDebug(MODULE, "PolicyDesigner's context found");
			String ssoUsername = (String) pdContext.getAttribute(ServermgrConstant.SSO_USERNAME);
			String ssoPassword = (String) pdContext.getAttribute(ServermgrConstant.SSO_PASSWORD);
			systemLoginForm.setUserName(ssoUsername);
			systemLoginForm.setPassword(ssoPassword);
			Logger.logDebug(MODULE, "Attributes successfully set in SystemLoginForm for user '"+ssoUsername+"'");
		}else{
			Logger.logDebug(MODULE, "PolicyDesigner's Context is Null");	
		}
	}else{
		Logger.logDebug(MODULE, "PolicyDesigner's context path is Null");
	}
	
	
	try
	{
		long startTime = System.currentTimeMillis();
		LoginBLManager loginBLManager = new LoginBLManager();
		StaffBLManager staffBLManager = new StaffBLManager();
		HttpSession session = request.getSession();
		String userId = loginBLManager.validateLogin(systemLoginForm.getUserName(), systemLoginForm.getPassword());
		
		if (userId != null) {
			systemLoginForm.setUserId(userId);

			HashMap userControlActionMap = staffBLManager.getUserControlAction(systemLoginForm.getUserId());
			HashMap modelMap = (HashMap) userControlActionMap.get("modelMap");
			session.setAttribute("modelMap", modelMap);
			ProfileManager.setActionMap(userControlActionMap);

			Set<String> actionaliasSet = staffBLManager.getActionAliasSets(userId);
			session.setAttribute("__action_Alias_Set_", actionaliasSet);
			IStaffData staffData = staffBLManager.getStaffData(userId);
			if(staffData.getPasswordChangeDate() == null){
				strFirstLoginPath = "/initChangePassword.do";
			}
			Timestamp systemLoginTime =  new Timestamp(new Date().getTime());;
			Timestamp passwordChangeDate = staffData.getPasswordChangeDate();
			session.setAttribute("passwordChangeDate", passwordChangeDate);
			session.setAttribute("strFirstLoginPath",strFirstLoginPath);
			session.setAttribute("radiusLoginForm", systemLoginForm);
			session.setAttribute("systemLoginTime",systemLoginTime );
			
			String pdSSOURL = "http://"+request.getServerName()+":"+request.getServerPort()+"/"+pdContextPath+"/commons/login/Login/ssoLogin";
			sctx.setAttribute(ServermgrConstant.SSO_USERNAME, systemLoginForm.getUserName());
			sctx.setAttribute(ServermgrConstant.SSO_PASSWORD, systemLoginForm.getPassword());		
			sctx.setAttribute(ServermgrConstant.PD_SSO_URL, pdSSOURL);							  
			
			Logger.logDebug(MODULE, "Attributes successfully set in ServerManager's Context for user '"+systemLoginForm.getUserName()+"'");
			Logger.logDebug(MODULE, "Total login time:"+(System.currentTimeMillis()-startTime));
		} else {		    
			
		    Logger.logDebug(MODULE, "Login Failed. UserId is Null");
		    request.setAttribute("errorCode", "LOGINFAILED");
		    request.getRequestDispatcher("/Login.jsp").forward(request, response);
		    return;
		}
		
	}catch(DataManagerException dme){
		Logger.logError(MODULE, "DataManager Error while SSO Login. Reason: "+dme.getMessage());
		Logger.logTrace(MODULE, dme);
		request.getRequestDispatcher("/Login.jsp").forward(request, response);
		return;
	}catch(Exception e){
		Logger.logError(MODULE, "Exception while SSO Login. Reason: "+e.getMessage());
		Logger.logTrace(MODULE, e);
	    request.getRequestDispatcher("/Login.jsp").forward(request, response);
	    return;
	}	
	request.getRequestDispatcher("/jsp/core/system/login/LoginHome.jsp").forward(request, response);
	
    }
}
