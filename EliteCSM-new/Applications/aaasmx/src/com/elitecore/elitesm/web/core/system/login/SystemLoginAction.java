package com.elitecore.elitesm.web.core.system.login;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.login.LoginBLManager;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.base.BaseSystemAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.Captcha;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.core.system.profilemanagement.ProfileManager;
import com.elitecore.passwordutil.PasswordEncryption;

public class SystemLoginAction extends BaseSystemAction {
	
	private static final String MODULE = "LOGIN ACTION";
	private static final String FAILURE = "failure";
	private static int captchaAttempt=0,ipFlagCount=0,countUser=0;
	private boolean multipleAttempt=false,ipFlag=false;
	private static int noOfAttempts=0,idleIntervalForSession=0;
	
	ConcurrentHashMap<Integer,String> userMap=new ConcurrentHashMap<Integer, String>();
	ConcurrentHashMap<Integer,String> ipMap=new ConcurrentHashMap<Integer, String>();
	ConcurrentHashMap<String,Long> ipHashMap=new ConcurrentHashMap<String, Long>();
	ConcurrentHashMap<Integer,String> captchaHashMap=new ConcurrentHashMap<Integer,String>();
	ConcurrentHashMap<String, Long> userHashMap=new ConcurrentHashMap<String, Long>();
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logTrace(MODULE, "Enter execute method of :"+ getClass().getName());
		String strReturnPath = "loginFailed";
		String strReturnToStarupPage = "loginFailedInitFailed";
		
		if(ConfigManager.isInitCompleted() == false){
			return mapping.findForward(strReturnToStarupPage);
		}
		
		
		try{
			SystemLoginForm radiusLoginForm = (SystemLoginForm) form;
			LoginBLManager loginBLManager = new LoginBLManager();
			noOfAttempts=Integer.parseInt(ConfigManager.get(ConfigConstant.LOGIN_BLOCK_ATTEMPT));
			idleIntervalForSession = Integer.parseInt(ConfigManager.get(ConfigConstant.SESSION_IDLE_TIME));
				
			if( radiusLoginForm != null && radiusLoginForm.getSystemUserName() != null){
				String encryptedOldPassword = PasswordEncryption.getInstance().crypt(radiusLoginForm.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
				boolean isValidUser = loginBLManager.isValidUser(radiusLoginForm.getSystemUserName(), encryptedOldPassword);
					
				if( isValidUser ){
					Logger.logInfo(MODULE, "Validating user - " + radiusLoginForm.getSystemUserName() + " Successfully");
					boolean isValidSession = true;
					boolean isCaptchaCodeExist = isCaptchaCodeExist(request);
					
					if( isCaptchaCodeExist ){
						boolean isValidCaptchaCode = isValidCaptcha(request);
						if( !isValidCaptchaCode ){
							isValidSession = false;
						}
					}
					
					if( isValidSession ){
						String isReuireToGenerateDm = radiusLoginForm.getGenerateDm();
						
						if(isReuireToGenerateDm != null && !(isReuireToGenerateDm.equals("false"))){
							if (isTokenValid(request)) {
								ConcurrentUserAccessDAO.destroyAllSession(radiusLoginForm.getSystemUserName());
							}
						}
						
						boolean isUserSessionActive =  ConcurrentUserAccessDAO.getActiveSession( radiusLoginForm.getSystemUserName(), request);
						
						if( !isUserSessionActive ){
								Logger.logWarn(MODULE, "Login Failed.");
								request.setAttribute("errorCode", "CONCURRENT_USER");
								request.setAttribute("disconnectOldSession", true);
								request.setAttribute("invalid_concurrent_user", radiusLoginForm.getSystemUserName());
								strReturnPath = "loginFailed";
								resetToken(request);
								saveToken(request);
								return mapping.findForward(strReturnPath);
						}	
					}
				}
			}
			
			if(ipHashMap.size()>0){
				for(String key:ipHashMap.keySet()){
					if(request.getRemoteHost().equals(key)){
						Long currentVal=ipHashMap.get(key);
						ipHashMap.remove(key);
						ipHashMap.put(key, currentVal);
						Long elapsedTime=currentVal-System.currentTimeMillis();
						if(elapsedTime<=0){
							ipHashMap.remove(key);
							ipMap.values().removeAll(Collections.singleton(key));
						}else{
							Long minutes = (Long) ((elapsedTime / 1000) / 60);
							request.setAttribute("ElapsedTime", minutes);
							request.setAttribute("IpAddress",request.getRemoteHost());
							ipMap.values().remove(key);
							if(minutes<=0){
								ipHashMap.remove(key);
								ipMap.values().removeAll(Collections.singleton(key));
								multipleAttempt=true;
							}
						}
					}
				}
			}
			
			if(userHashMap.size()>0){
				for(String key:userHashMap.keySet()){
					if(radiusLoginForm.getSystemUserName().equals(key)){
						Long currentVal=userHashMap.get(key);
						userHashMap.remove(key);
						userHashMap.put(key, currentVal);
						Long elapsedTime=currentVal-System.currentTimeMillis();
						if(elapsedTime<=0){
							userHashMap.remove(key);
							userMap.values().removeAll(Collections.singleton(key));
						}else{
							Long minutes = (Long) ((elapsedTime / 1000) / 60);
							request.setAttribute("ElapsedTime", minutes);
							request.setAttribute("Account",radiusLoginForm.getSystemUserName());
							if(minutes<=0){
								userHashMap.remove(key);
								userMap.values().removeAll(Collections.singleton(key));
							}
						}
					}
				}
			}
			
			captchaHashMap.put(captchaAttempt,request.getRemoteHost());
			captchaAttempt++;
			if(radiusLoginForm.getAction()!=null && radiusLoginForm.getAction().equals("login")){
			try
			{
				long startTime = System.currentTimeMillis();
				StaffBLManager staffBLManager = new StaffBLManager();
				HttpSession session = request.getSession();
				
				/*Encrypt User Password*/
				String encryptedOldPassword = PasswordEncryption.getInstance().crypt(radiusLoginForm.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
			    radiusLoginForm.setPassword(encryptedOldPassword);
				
				String userId = loginBLManager.validateLogin(radiusLoginForm.getSystemUserName(), radiusLoginForm.getPassword());
				String captchaText=request.getParameter("captchaText");
				if (userId != null) {
					if(captchaText!=null){
						 String c= (String)session.getAttribute(Captcha.CAPTCHA_KEY) ;
						 if(!captchaText.equals(c) ){
							 	int countCaptcha;	
								countCaptcha=Collections.frequency((Collection<?>) captchaHashMap.values(), request.getRemoteHost());
									if(countCaptcha>=3){
										 request.setAttribute("enabledCaptcha", countCaptcha);
									}
							 Logger.logWarn(MODULE, "Login Failed.");
							 request.setAttribute("errorCode", "INVALIDCAPTCHA");
							 strReturnPath = "loginFailed";
						 }else{
							 Long chkMinutes=(Long) request.getAttribute("ElapsedTime");
							 	if(chkMinutes==null){
							  		chkMinutes=Long.parseLong("0");
							  	}
							 if(chkMinutes>0 && chkMinutes!=null){
								 Logger.logWarn(MODULE, "Login Failed.");
								 request.setAttribute("errorCode", "BLOCKIP");
								 strReturnPath = "loginFailed";
							 }else{
								 
								//If improper session timeout found then set change default session timeout
								 if(session.getMaxInactiveInterval() < 0){
									 session.setMaxInactiveInterval(900);
								 }
								 
								 radiusLoginForm.setUserId(userId);
								
								 HashMap userControlActionMap = staffBLManager.getUserControlAction(radiusLoginForm.getUserId());
								 HashMap modelMap = (HashMap) userControlActionMap.get("modelMap");
								 session.setAttribute("modelMap", modelMap);
								 ProfileManager.setActionMap(userControlActionMap);
		
								 Set<String> actionaliasSet = staffBLManager.getActionAliasSets(userId);
								 session.setAttribute("__action_Alias_Set_", actionaliasSet);
								 strReturnPath = "loginHome";
								 session.setAttribute("radiusLoginForm", radiusLoginForm);
								 request.setAttribute("enabledCaptcha", null);
								 licenseExpiryValidation(request);
								 
								 IStaffData staffData = staffBLManager.getStaffData(userId);
								 Timestamp lastChangePasswordDate = staffData.getLastChangePasswordDate();
								 request.getSession().setAttribute("lastChangePasswordDate", lastChangePasswordDate);
								 
								 ipHashMap.remove(request.getRemoteHost());
								 ipMap.values().removeAll(Collections.singleton(request.getRemoteHost()));
								 captchaHashMap.values().removeAll(Collections.singleton(request.getRemoteHost()));
								 userMap.values().removeAll(Collections.singleton(radiusLoginForm.getSystemUserName()));
								 userHashMap.values().removeAll(Collections.singleton(radiusLoginForm.getSystemUserName()));
								 multipleAttempt=false;
								 ipFlag=true;
								 
								 session.setAttribute("userLoggedIn", true);
								 session.setMaxInactiveInterval(idleIntervalForSession);
							 	}
							 
						 }
					}else{
							Integer param = (Integer) session.getAttribute("enabledCaptcha");
							  if (param != null && param>=3) {
								    Logger.logWarn(MODULE, "Login Failed.");
									request.setAttribute("errorCode", "LOGINFAILED");
									strReturnPath = "loginFailed";
							  }else{
								  Long chkMinutes=(Long) request.getAttribute("ElapsedTime");
								  	if(chkMinutes==null){
								  		chkMinutes=Long.parseLong("0");
								  	}
									 if(chkMinutes>0){
										 Logger.logWarn(MODULE, "Login Failed.");
										 request.setAttribute("errorCode", "BLOCKIP");
										 strReturnPath = "loginFailed";
									 }else{
										
										//If improper session timeout found then set change default session timeout
										 if(session.getMaxInactiveInterval() < 0){
											 session.setMaxInactiveInterval(900);
										 }
										 
										 radiusLoginForm.setUserId(userId);

										 HashMap userControlActionMap = staffBLManager.getUserControlAction(radiusLoginForm.getUserId());
										 HashMap modelMap = (HashMap) userControlActionMap.get("modelMap");
										 session.setAttribute("modelMap", modelMap);
										 ProfileManager.setActionMap(userControlActionMap);
				
										 Set<String> actionaliasSet = staffBLManager.getActionAliasSets(userId);
										 session.setAttribute("__action_Alias_Set_", actionaliasSet);
										 strReturnPath = "loginHome";
										 session.setAttribute("radiusLoginForm", radiusLoginForm);
										 request.setAttribute("enabledCaptcha", null);
										 captchaHashMap.values().removeAll(Collections.singleton(request.getRemoteHost()));
										 licenseExpiryValidation(request);
										 IStaffData staffData = staffBLManager.getStaffData(userId);
										 Timestamp lastChangePasswordDate = staffData.getLastChangePasswordDate();
										 request.getSession().setAttribute("lastChangePasswordDate", lastChangePasswordDate);
										 
										 ipMap.values().removeAll(Collections.singleton(request.getRemoteHost()));
										 ipHashMap.values().removeAll(Collections.singleton(request.getRemoteHost()));
										 userMap.values().removeAll(Collections.singleton(radiusLoginForm.getSystemUserName()));
										 userHashMap.values().removeAll(Collections.singleton(radiusLoginForm.getSystemUserName()));
										 ipFlag=true;
										 multipleAttempt=false;
										 
										 session.setAttribute("userLoggedIn", true);
										 session.setMaxInactiveInterval(idleIntervalForSession);
									}
							  }
						 }
				} else {
					int countCaptcha;
					if(Collections.frequency((Collection<?>) captchaHashMap.values(), request.getRemoteHost())>3){
						boolean validUser=loginBLManager.validateUserName(radiusLoginForm.getSystemUserName());
						if(validUser==true){
							userMap.put(countUser,radiusLoginForm.getSystemUserName());
							countUser++;
						}
					}
					countCaptcha=Collections.frequency((Collection<?>) captchaHashMap.values(), request.getRemoteHost());
					if(countCaptcha>=3){
						 request.setAttribute("enabledCaptcha", countCaptcha);
					}
				    Logger.logWarn(MODULE, "Login Failed.");
					request.setAttribute("errorCode", "LOGINFAILED");
					strReturnPath = "loginFailed";
				}
				Logger.logDebug(MODULE, "Total login time:"+(System.currentTimeMillis()-startTime));
			}catch(DataManagerException dme){
				Logger.logError(MODULE, " Error in login action :" +dme);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
				request.setAttribute("errorDetails", errorElements);
				if(dme.getMessage()!=null && dme.getMessage().equalsIgnoreCase("Cannot open connection")){
					strReturnPath = FAILURE;
					ActionMessage message = new ActionMessage("datasource.exception");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveErrors(request, messages);

				}else{
					strReturnPath = FAILURE;
					ActionMessage message = new ActionMessage("general.error");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveErrors(request, messages);
				}

			}catch(Exception e){
				Logger.logError(MODULE, " Error in login action :" +e);
				strReturnPath = FAILURE;
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			}else if( radiusLoginForm.getAction()!=null && radiusLoginForm.getAction().equals("pwdexpired")){
				try{
				 StaffBLManager staffBLManager = new StaffBLManager();
				 HttpSession session = request.getSession();
				
				 /*Encrypt User Password*/
				 String encryptedOldPassword = PasswordEncryption.getInstance().crypt(radiusLoginForm.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
				 radiusLoginForm.setPassword(encryptedOldPassword);
				 
				 String userId = loginBLManager.validateLogin(radiusLoginForm.getSystemUserName(), radiusLoginForm.getPassword());
				 
				 if (userId != null) {
					 
					//If improper session timeout found then set change default session timeout
					 if(session.getMaxInactiveInterval() < 0){
						 session.setMaxInactiveInterval(900);
					 }
					 
					 radiusLoginForm.setUserId(userId);
		
					 HashMap userControlActionMap = staffBLManager.getUserControlAction(radiusLoginForm.getUserId());
					 HashMap modelMap = (HashMap) userControlActionMap.get("modelMap");
					 session.setAttribute("modelMap", modelMap);
					 ProfileManager.setActionMap(userControlActionMap);
		
					 Set<String> actionaliasSet = staffBLManager.getActionAliasSets(userId);
					 session.setAttribute("__action_Alias_Set_", actionaliasSet);
					 strReturnPath = "changePassword";
					 session.setAttribute("radiusLoginForm", radiusLoginForm);
					 request.setAttribute("enabledCaptcha", null);
					 captchaHashMap.values().removeAll(Collections.singleton(request.getRemoteHost()));
					 licenseExpiryValidation(request);
					 IStaffData staffData = staffBLManager.getStaffData(userId);
					 Timestamp lastChangePasswordDate = staffData.getLastChangePasswordDate();
					 request.getSession().setAttribute("lastChangePasswordDate", lastChangePasswordDate);
					 
					 ipMap.values().removeAll(Collections.singleton(request.getRemoteHost()));
					 ipHashMap.values().removeAll(Collections.singleton(request.getRemoteHost()));
					 userMap.values().removeAll(Collections.singleton(radiusLoginForm.getSystemUserName()));
					 userHashMap.values().removeAll(Collections.singleton(radiusLoginForm.getSystemUserName()));
					 ipFlag=true;
					 multipleAttempt=false;
					 
					 session.setAttribute("userLoggedIn", true);
					 session.setMaxInactiveInterval(idleIntervalForSession);
				 }else{
					 Logger.logWarn(MODULE, "Login Failed.");
					 request.setAttribute("errorCode", "LOGINFAILED");
					 strReturnPath = "loginFailed";
				 }
				 
			}catch(Exception e){
				Logger.logError(MODULE, " Error in login action :" +e);
				strReturnPath = FAILURE;
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
			}else{
				 HttpSession session = request.getSession();
				 String c= (String)session.getAttribute(Captcha.CAPTCHA_KEY);
				 ipFlag=true;
				 int countCaptcha=Collections.frequency((Collection<?>) captchaHashMap.values(), request.getRemoteHost());
					if(countCaptcha>=3){
						 request.setAttribute("enabledCaptcha", countCaptcha);
					}
			}
					if(ipFlag==false){
						ipMap.put(ipFlagCount, request.getRemoteHost());
						ipFlagCount++;
						
						int countIp=Collections.frequency((Collection<?>) ipMap.values(),request.getRemoteHost()),userFlag=0;
						int countUser=Collections.frequency((Collection<?>) userMap.values(),radiusLoginForm.getSystemUserName());
						if(countUser>=noOfAttempts){
							if(countUser==noOfAttempts){
								boolean existUser=userHashMap.containsKey(radiusLoginForm.getSystemUserName());
									if(existUser==false){
										Long blockInterval=Long.parseLong(ConfigManager.get(ConfigConstant.LOGIN_BLOCK_INTERVAL));
										Long sysTime=(System.currentTimeMillis()+(blockInterval*1000));
										userHashMap.put(radiusLoginForm.getSystemUserName(), sysTime);
										request.setAttribute("Account", radiusLoginForm.getSystemUserName());
									}
							}
							Logger.logWarn(MODULE, "Login Failed.");
							request.setAttribute("errorCode", "BLOCKUSER");
							strReturnPath = "loginFailed";
							userFlag=1;
						}
						if(multipleAttempt==true){
							if(countIp>=noOfAttempts){
								if(countIp==noOfAttempts){
									boolean existIp=ipHashMap.containsKey(request.getRemoteHost());
									if(existIp==false){
										Long blockInterval=Long.parseLong(ConfigManager.get(ConfigConstant.LOGIN_BLOCK_INTERVAL));
										Long sysTime=(System.currentTimeMillis()+(blockInterval*1000));
										ipHashMap.put(request.getRemoteHost(), sysTime);
									}
								}
								if(userFlag==1){
									Logger.logWarn(MODULE, "Login Failed.");
									request.setAttribute("errorCode", "BLOCKUSERIP");
									strReturnPath = "loginFailed";
								}else{
								 	Logger.logWarn(MODULE, "Login Failed.");
									request.setAttribute("errorCode", "BLOCKIP");
									strReturnPath = "loginFailed";
								}
							}
						}else{
							if(countIp>=(noOfAttempts+3)){
								if(countIp==(noOfAttempts+3)){
									boolean existIp=ipHashMap.containsKey(request.getRemoteHost());
									if(existIp==false){
										Long blockInterval=Long.parseLong(ConfigManager.get(ConfigConstant.LOGIN_BLOCK_INTERVAL));
										Long sysTime=(System.currentTimeMillis()+(blockInterval*1000));
										ipHashMap.put(request.getRemoteHost(), sysTime);
									}
								}
								if(userFlag==1){
									Logger.logWarn(MODULE, "Login Failed.");
									request.setAttribute("errorCode", "BLOCKUSERIP");
									strReturnPath = "loginFailed";
								}else{
								 	Logger.logWarn(MODULE, "Login Failed.");
									request.setAttribute("errorCode", "BLOCKIP");
									strReturnPath = "loginFailed";
								}
							}
					}
					}else{
						ipMap.remove(request.getRemoteHost());
						ipFlag=false;
					}
					
				Logger.logInfo(MODULE, "Value of  userLoggedIn : " +  request.getSession().getAttribute("userLoggedIn"));	
				resetToken(request);

				if( request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY) != null ){
					request.getSession().setAttribute(Globals.TRANSACTION_TOKEN_KEY, null);
				}
				
				return mapping.findForward(strReturnPath);
	
		}catch(NumberFormatException nfe){
			strReturnPath = "loginFailed";
			Logger.logError(MODULE, " Error in login action :" +nfe);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(nfe);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch(Exception e){
			strReturnPath = "loginFailed";
			Logger.logError(MODULE, " Error in login action :" +e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(strReturnPath);
	}

	private boolean isValidCaptcha(HttpServletRequest request) {
		String captchaText=request.getParameter("captchaText");
		if( captchaText != null ){
			String captchaString= (String)request.getSession().getAttribute(Captcha.CAPTCHA_KEY) ;
			if(captchaText.equals(captchaString) ){
				 return true;
			}
		}else{
			return false;
		}
		return false;
	}

	private boolean isCaptchaCodeExist(HttpServletRequest request) {
		String captchaText=request.getParameter("captchaText");
		if ( captchaText == null) {
			return false;
		}else{
			return true;
		}
	}

	public void licenseExpiryValidation(HttpServletRequest request) {
		boolean bLicenseAlert = false;
		boolean bLicenseAlertForPopup = false;
		boolean bLicenseExpireAlert = false;
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		try{
			List<NetServerInstanceData> netServerListForLicense = netServerBLManager.getNetServerInstanceListForLicense();
			List<NetServerTypeData> netServerTypeList = netServerBLManager.getNetServerTypeList();

			if(netServerListForLicense != null) {
				for(NetServerInstanceData netServerInstanceData : netServerListForLicense) {
					Integer licenseExpireDays = 0;
					if(netServerInstanceData != null) {
						licenseExpireDays = netServerInstanceData.getLicenseExpiryDays();
					}
					if(licenseExpireDays != null) {
						if(licenseExpireDays > 0 && licenseExpireDays <= 30)
							bLicenseAlert = true;
						if(licenseExpireDays < 3)
							bLicenseAlertForPopup = true;
						if(licenseExpireDays < 0)
							bLicenseExpireAlert = true;
					}
				}
			}

			request.getSession().setAttribute("bLicenseAlert", String.valueOf(bLicenseAlert));
			request.getSession().setAttribute("bLicenseAlertForPopup", String.valueOf(bLicenseAlertForPopup));
			request.getSession().setAttribute("bLicenseExpireAlert", String.valueOf(bLicenseExpireAlert));
		}catch (DataManagerException hExp) {
			Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
			Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
		}catch (Exception hExp) {
			Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
			Logger.logTrace(MODULE,hExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(hExp);
			request.setAttribute("errorDetails", errorElements);
		}
	}
}
