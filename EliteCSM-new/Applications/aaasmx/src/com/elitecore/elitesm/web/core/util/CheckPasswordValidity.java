package com.elitecore.elitesm.web.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.elitesm.blmanager.core.system.login.LoginBLManager;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.elitesm.util.constants.SystemLoginConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.passwordutil.PasswordEncryption;

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
		String userName = request.getParameter("userName");
		String password=request.getParameter("pwd");
		response.setContentType("text/plain");
		String responseString = null;
		PrintWriter out = response.getWriter();
		StaffBLManager staffBLManager = new StaffBLManager();
		PasswordSelectionPolicyBLManager passwordBLManager=new PasswordSelectionPolicyBLManager();
		LoginBLManager loginBLManager = new LoginBLManager();
		try {
			if(userName != null || password != null){
				String encryptedOldPassword = PasswordEncryption.getInstance().crypt(password.trim(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
				password=encryptedOldPassword;

				String userId = loginBLManager.validateLogin(userName.trim(), password.trim());

				/*Encrypt User Password*/
				if(userId != null){

					PasswordPolicyConfigData passwordPolicySelectionData=passwordBLManager.getPasswordSelectionPolicy();
					String changePwdOnFirstLogin = passwordPolicySelectionData.getChangePwdOnFirstLogin();
					request.getSession().setAttribute(SystemLoginConstants.CHANGEPWDONFIRSTLOGIN, changePwdOnFirstLogin);
					if(changePwdOnFirstLogin.equalsIgnoreCase("true")){
						if(staffBLManager.getLastChangedPwdDate(userName) == null){
							responseString = SystemLoginConstants.FIRSTLOGIN;
							out.println(responseString);
							return;
						}
					}
					if(staffBLManager.getLastChangedPwdDate(userName) != null ){
						Date lastmodifiedDate=staffBLManager.getLastChangedPwdDate(userName);
						Date currentDate=new Date();


						if(passwordPolicySelectionData.getPasswordValidity() != null){
							int validityDays=passwordPolicySelectionData.getPasswordValidity();
							if(validityDays == 0){
								responseString = SystemLoginConstants.NO;
							}else{
								long modifiedDate = lastmodifiedDate.getTime();
								long curDate = currentDate.getTime();
								long day = 1000 * 60 * 60 * 24; // milliseconds in a day
								int remainingDays= (int) ((curDate - modifiedDate) / day);

								if((validityDays) < remainingDays){
									responseString = SystemLoginConstants.YES;
								}else{
									responseString = SystemLoginConstants.NO;
								}
							}
						}
					}else{
						if(staffBLManager.getLastChangedPwdDate(userName) == null){
							if(passwordPolicySelectionData.getPasswordValidity() != null){
								int validityDays=passwordPolicySelectionData.getPasswordValidity();
								if(validityDays == 0){
									responseString = SystemLoginConstants.NO;
								}else{
									responseString = SystemLoginConstants.YES;
								}
							}
						}
					}
				}else{
					responseString = SystemLoginConstants.INVALID;
				}
			}else{
				responseString = SystemLoginConstants.INVALID;
			}

			out.println(responseString);	
		}catch(Exception e){
			Logger.logError(MODULE, "Error while retriving fields : "+e.getMessage());
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}
