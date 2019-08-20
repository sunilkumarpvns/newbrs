package com.elitecore.elitesm.ws.rest.security;

import java.util.ArrayList;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.SystemNotReadyException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestACLConstant;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * A custom authentication provider for authenticating REST request. Only for a valid user the
 * the request will be entertained. For invalid users it will throw an exception. After authentication
 * whether the user is authorized to perform that operation or not will checked.
 * @author animesh christie
 */
public class AuthenticationHandler implements AuthenticationProvider{

	private static final String MODULE = "CUSTOM-AUTH-PRVDR";
	private static final String USER_NAME = "username";
	

	@Override
	public Authentication authenticate(Authentication authenticationParameters)	throws AuthenticationException {

		if (ConfigManager.isInitCompleted()) {
			
			String userName = authenticationParameters.getName();
			String passWord = authenticationParameters.getCredentials().toString();
	
			if(Strings.isNullOrBlank(userName)) {
				if(Logger.getLogger().isLogLevel(LogLevel.WARN)) {
					Logger.logWarn(MODULE, Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() +", Authentication parameters are empty. Reason: username does not provided.");
				}
				throw new UsernameNotFoundException(Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() +", Authentication parameters are empty. Reason: username does not provided.");
			}
			
			if(Strings.isNullOrBlank(passWord)) {
				if(Logger.getLogger().isLogLevel(LogLevel.WARN)){
					Logger.logWarn(MODULE, Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() +", Authentication parameters are empty. Reason: password does not provided.");
				}
				throw new BadCredentialsException(Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() +", Authentication parameters are empty. Reason: password does not provided.");
			}
	
			Session session = HibernateSessionFactory.createSession();
			
			if (session == null) {
				return authenticationParameters;
			}
			
			Criteria criteria = session.createCriteria(StaffData.class).add(Restrictions.eq(USER_NAME, userName));
			StaffData staffData = (StaffData) criteria.uniqueResult();
			
			session.close();
	
			if(staffData != null) {
	
				String plainTextPwd = null;
	
				try {
					plainTextPwd = PasswordEncryption.getInstance().decrypt(staffData.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
				} catch (NoSuchEncryptionException e) {
					throw new AuthenticationCredentialsNotFoundException(Response.Status.UNAUTHORIZED.name() + ",Reason: Authentication failed for user: "+ authenticationParameters.getName() +", due to unsupported encryption.");
				}catch (DecryptionNotSupportedException e) {
					throw new AuthenticationCredentialsNotFoundException("Authentication failed for user: "+ authenticationParameters.getName() +", due to improper password encryption format.");
				} catch (DecryptionFailedException e) {
					throw new AuthenticationCredentialsNotFoundException("Authentication failed for user: "+ authenticationParameters.getName() +", due to exception in decryption.");
				}
	
				if(passWord.equals(plainTextPwd) == false) {
					if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)){
						Logger.logDebug(MODULE, Response.Status.EXPECTATION_FAILED + "-" + Response.Status.EXPECTATION_FAILED.getStatusCode() +", Invalid Password is provided in Authencation parameters for user: "+userName);
					}
					throw new BadCredentialsException("Invalid Password is provided in Authencation parameters for user: "+userName);
				}
	
				ArrayList<GrantedAuthority> permittedRoleList = new ArrayList<GrantedAuthority>();
				SimpleGrantedAuthority grantedAuth = null;
				try {
					Set<String> actionAlias = getActionAlias(staffData.getStaffId());
	
					Object obj = authenticationParameters.getDetails();
					
					if(obj instanceof AuthenticationDetails){
						AuthenticationDetails customAuthenticationDetails = (AuthenticationDetails) obj;
						String restACLVal = customAuthenticationDetails.getRestACLParameter();
						
						try{
							if(restACLVal.equals("wadl") || actionAlias.contains(RestACLConstant.valueOf(restACLVal).getConfigConstantString())) {
								grantedAuth = new SimpleGrantedAuthority(ConfigConstant.ROLE_WEBSERVICE);
							}
						} catch (IllegalArgumentException e){
							/*
							 * Note : Whenever enum constant for RestACLParameter will not be available in RestACLConstant class
							 * IllegalArgumentException is thrown, in order to handle that this exception catch block is added.
							 * 
							 * Here default role will be given to the user, so that spring authentication will be 
							 * successful and CXF will return 404 as the URL is not registered.
							 */
							if(Logger.getLogger().isLogLevel(LogLevel.ERROR)) {
								Logger.logError(MODULE, e.getMessage());
							}
							if(customAuthenticationDetails.getRestACLParameter().equals("invalidoperation")) {
								customAuthenticationDetails.setRestACLParameter("invalidoperation");
							}else if (customAuthenticationDetails.getRestACLParameter().equals("invalidversion")) {
								customAuthenticationDetails.setRestACLParameter("invalidurl");
							}
							grantedAuth = new SimpleGrantedAuthority(ConfigConstant.ROLE_WEBSERVICE);
						}
					}
				} catch (DataManagerException e) {
					if(Logger.getLogger().isLogLevel(LogLevel.ERROR)) {
						Logger.logError(MODULE, e.getMessage());
					}
					Logger.logTrace(MODULE, e);
					throw new ProviderNotFoundException(Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() + "," + e.getMessage());
				}
				
				if( grantedAuth != null ){
					permittedRoleList.add(grantedAuth);
					return new UsernamePasswordAuthenticationToken(staffData, passWord,permittedRoleList);
				}else{
					throw new AccessDeniedException(Response.Status.FORBIDDEN + "-" + Response.Status.FORBIDDEN.getStatusCode() + "," + "ACCESS DENIED : You do not have permission to perform this operation");
				}
		
			} else {
				if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
					Logger.logDebug(MODULE, Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() +", User: "+userName + " doesn't exist.");
				}
				throw new BadCredentialsException(Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() +", User: "+userName + " doesn't exist.");
			}
		}else{
			throw new SystemNotReadyException("System is not ready to process request");
		}
	}

	/**
	 * A set of string will be returned based on Staff Id. The set contains all the actions that a particular
	 * user has rights to perform, based on this list the user/staff will be authorized.  
	 * @param staffId Staff Id of user/staff
	 * @return Set<String> Set of string actions
	 * @throws DataManagerException
	 */
	private Set<String> getActionAlias(String staffId) throws DataManagerException {
		StaffBLManager staffBLManager = new StaffBLManager();
		StaffData staffData = new StaffData();
		staffData.setStaffId(staffId);
		Set<String> actionAliasSets = staffBLManager.getActionAliasSets(String.valueOf(staffId));
		return actionAliasSets;
	}

	@Override
	public boolean supports(Class<? extends Object> authenticationToken) {
		return authenticationToken.equals(UsernamePasswordAuthenticationToken.class);
	}
}