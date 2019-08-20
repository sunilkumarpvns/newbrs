package com.elitecore.netvertexsm.util;

import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.corenetvertex.constants.CommonStatus;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.SystemLoginConstants;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

/**
 * Servlet implementation class RetriveCipherSuit
 */
public class CheckPasswordValidity extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE="CheckPasswordValidity";

	public CheckPasswordValidity() {
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.logDebug(MODULE, "Method called doGet()");
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.logDebug(MODULE, "Method called doPost()");
		
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		SystemLoginForm radiusLoginForm = null;
		
		if( userName == null && password == null ){
			
			/*
			 * If user hit/refresh the Url into the browser bar then username and possword will be null. so taking the username,password from the 
			 * radiusLoginForm available in the session scope.
			 * */
			
			Logger.logDebug(MODULE, "User has hit/Refresh URL");
			radiusLoginForm = (SystemLoginForm) request.getSession().getAttribute("radiusLoginForm");
			
			if( radiusLoginForm != null ){
				Logger.logDebug(MODULE, "Proceeding with RadiusLoginForm");
				userName = radiusLoginForm.getUserName();
				password = radiusLoginForm.getPassword();			
			}else{
				Logger.logDebug(MODULE, "RadiusLoginForm not found");
			}
		}
		
		response.setContentType("text/plain");
		String responseString = null;

		RequestDispatcher rd = null;
		try {
			
			Logger.logDebug(MODULE, "user '"+userName+"' is trying to login");	
			if(userName != null || password != null){
				 
				StaffBLManager staffBLManager = new StaffBLManager();
				
				StaffData staffData = staffBLManager.validateLoginUser(userName.trim(), password.trim());
				String userId = null;

				if(staffData!=null) {
					if(CommonStatus.ACTIVE.statusId.equalsIgnoreCase(staffData.getCommonStatusId()) ==false){
						Logger.logError(MODULE, "User Account is Inactive for user: "+staffData.getUserName());
						request.setAttribute("errorCode", "LOGINFAILED");
						rd = request.getRequestDispatcher("/Login.jsp");
						rd.forward(request, response);
						return;
					} else if(staffData.getPassword().equals(PasswordUtility.getEncryptedPassword(password)) || (staffData.getPasswordChangeDate() == null && staffData.getPassword().equals(password))) {
						userId =  String.valueOf(staffData.getStaffId());
					} else {
						Logger.logInfo(MODULE, "UserId not found for given Username and Password");
					}
				}
				
				if(userId != null){

					if(staffData.getPassword().equals(PasswordUtility.getEncryptedPassword(password)) == false &&(staffData.getPasswordChangeDate() == null && staffData.getPassword().equals(password)))  {
						Logger.logInfo(MODULE, "Last Password Changed date is Null. User should change Password.");
						request.getSession().setAttribute(SystemLoginConstants.CHANGEPWDONFIRSTLOGIN, "true");
						responseString = SystemLoginConstants.FIRSTLOGIN;
					} else {
						PasswordSelectionPolicyBLManager passwordBLManager = new PasswordSelectionPolicyBLManager();
						PasswordPolicyConfigData passwordPolicySelectionData = passwordBLManager.getPasswordSelectionPolicy();
						String changePwdOnFirstLogin = passwordPolicySelectionData.getChangePwdOnFirstLogin();
						request.getSession().setAttribute(SystemLoginConstants.CHANGEPWDONFIRSTLOGIN, changePwdOnFirstLogin);
						if(changePwdOnFirstLogin.equalsIgnoreCase("true")){
							if(staffBLManager.getLastChangedPwdDate(userName) == null){
								Logger.logInfo(MODULE, "User is loging First Time. Its good to change Password on First Login.");
								responseString = SystemLoginConstants.FIRSTLOGIN;
							}
						}
						if(staffBLManager.getLastChangedPwdDate(userName) != null ){
							Date lastmodifiedDate = staffBLManager.getLastChangedPwdDate(userName);
							Date currentDate = new Date();

							if(passwordPolicySelectionData.getPasswordValidity() != null){
								int validityDays = passwordPolicySelectionData.getPasswordValidity();

								if(validityDays == 0){
									Logger.logInfo(MODULE, "Password Validity days are Zero(0)");
									responseString = SystemLoginConstants.NO;
								}else{
									long modifiedDate = lastmodifiedDate.getTime();
									long curDate = currentDate.getTime();
									long day = 1000 * 60 * 60 * 24; // milliseconds in a day
									int remainingDays = (int) ((curDate - modifiedDate) / day);
									if((validityDays) < remainingDays){
										Logger.logInfo(MODULE, "Password Expired, User Required to change Password.");
										responseString = SystemLoginConstants.YES;
										request.getSession().setAttribute(SystemLoginConstants.PASSWORD_EXPIRY_FLAG,SystemLoginConstants.YES);
									}else{
										Logger.logInfo(MODULE, "User doesn't need to change Password.");
										responseString = SystemLoginConstants.NO;
									}
								}
							}
						}else{
							if(staffBLManager.getLastChangedPwdDate(userName) == null){
								if(passwordPolicySelectionData.getPasswordValidity() != null){
									int validityDays = passwordPolicySelectionData.getPasswordValidity();
									if(validityDays == 0){
										Logger.logInfo(MODULE, "Password Validity days are Zero(0) and Last Password Changed date is Null.");
										responseString = SystemLoginConstants.NO;
									}else{
										Logger.logInfo(MODULE, "Last Password Changed date is Null. User should change Password.");
										responseString = SystemLoginConstants.CHANGE_PWD;
									}
								}
							}
						}
					}

				}else{
					Logger.logInfo(MODULE, "Invalid user found. UserId is Null.");
					responseString = SystemLoginConstants.INVALID;
				}
			}else{
				Logger.logInfo(MODULE, "Invalid user. Empty username and Password.");
				responseString = SystemLoginConstants.INVALID;
			}
			

			if(responseString.equalsIgnoreCase(SystemLoginConstants.FIRSTLOGIN)){
				//This is FIRST LOGIN user required to change password on your first login
				Logger.logInfo(MODULE, "Its First Login. Forwarding to Password-Expired page");
				rd = request.getRequestDispatcher("/passwordExpired.do");
				
			}else if(responseString.equalsIgnoreCase(SystemLoginConstants.YES)){
				// User's Password is expired , User required to change Password.
				Logger.logInfo(MODULE, "User should change Password. Forwarding to Password-Expired page");
				rd = request.getRequestDispatcher("/passwordExpired.do");

			}else if(responseString.equalsIgnoreCase(SystemLoginConstants.INVALID)){
				// Invalid User
				Logger.logInfo(MODULE, "Invalid User. Forwarding to Login page");
				request.setAttribute("errorCode", "LOGINFAILED");
				rd = request.getRequestDispatcher("/Login.jsp");
				
			}else {
				Logger.logInfo(MODULE, "Forwarding to Login Action");
				rd = request.getRequestDispatcher("/login.do");
			}
			 
			rd.forward(request, response);
			 
		}catch(Exception ex){
			Logger.logError(MODULE, "Error while Checking Password Validity. Reason: "+ex.getMessage());
			Logger.logTrace(MODULE,ex);
			request.setAttribute("errorCode", "LOGINFAILED");
			response.sendRedirect("/Login.jsp");
		}
	}
}
