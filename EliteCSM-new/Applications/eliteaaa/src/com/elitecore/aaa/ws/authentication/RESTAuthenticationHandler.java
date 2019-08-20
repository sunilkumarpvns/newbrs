package com.elitecore.aaa.ws.authentication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.elitecore.aaa.license.nfv.WebServiceParams;
import com.elitecore.aaa.ws.config.AAARestServer;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

/**
 * <p>A custom authentication provider for authenticating REST request. Only for a valid user the
 * the request will be entertained. For invalid users it will throw an exception. After authentication
 * whether the user is authorized to perform that operation or not will checked.</p>
 * 
 * @author chirag i. prajapati
 */
public class RESTAuthenticationHandler implements AuthenticationProvider {

	private static final String MODULE = "REST-AUTH-HNDLR";

	@Override
	public Authentication authenticate(Authentication authenticationParameters) {

		String userName = authenticationParameters.getName();
		String passWord = authenticationParameters.getCredentials().toString();

		if (Strings.isNullOrBlank(userName)) {
			LogManager.getLogger().debug(MODULE, Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() + ", Authentication parameters are empty. Reason: username does not provided.");
			throw new UsernameNotFoundException(Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() + ", Authentication parameters are empty. Reason: username does not provided.");
		}

		if (Strings.isNullOrBlank(passWord)) {
			LogManager.getLogger().debug(MODULE, Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() + ", Authentication parameters are empty. Reason: password does not provided.");
			throw new BadCredentialsException(Response.Status.UNAUTHORIZED + "-" + Response.Status.UNAUTHORIZED.getStatusCode() + ", Authentication parameters are empty. Reason: password does not provided.");
		}

		/** reading user credential from file in AAA(../system/_smWs) */

		WebServiceParams webServiceParams = new WebServiceParams(AAARestServer.getServerContext().getServerHome() + File.separator + "system");
		try {
			webServiceParams.readDetails();
		} catch (IOException e1) {
			LogManager.getLogger().trace(MODULE, e1);
			throw new AuthenticationCredentialsNotFoundException("Authentication failed for user: " + authenticationParameters.getName() + ", Reason: " + e1.getMessage());
		} catch (NoSuchEncryptionException | DecryptionNotSupportedException e1) {
			LogManager.getLogger().trace(MODULE, e1);
			throw new AuthenticationCredentialsNotFoundException("Authentication failed for user: " + authenticationParameters.getName() + ", due to improper password encryption format.");
		} catch (DecryptionFailedException e1) {
			LogManager.getLogger().trace(MODULE, e1);
			throw new AuthenticationCredentialsNotFoundException("Authentication failed for user: " + authenticationParameters.getName() + ", due to improper decryption.");
		}

		ArrayList<GrantedAuthority> permittedRoleList = new ArrayList<>();
		SimpleGrantedAuthority grantedAuth = null;
		if (userName.equals(webServiceParams.getUserName()) && passWord.equals(webServiceParams.getPassword())) {
			grantedAuth = new SimpleGrantedAuthority("ROLE_USER");
			permittedRoleList.add(grantedAuth);
			return new UsernamePasswordAuthenticationToken(webServiceParams, passWord, permittedRoleList);
		} else {
			LogManager.getLogger().debug(MODULE, "User '"+ userName + "' does not have permission to perform this operation, Reason: " + Response.Status.FORBIDDEN + "-" + Response.Status.FORBIDDEN.getStatusCode() + "," + "ACCESS DENIED");
			throw new AccessDeniedException("User '"+ userName + "' does not have permission to perform this operation, Reason: " + Response.Status.FORBIDDEN + "-" + Response.Status.FORBIDDEN.getStatusCode() + "," + "ACCESS DENIED");
		}

	}


	@Override
	public boolean supports(Class<? extends Object> authenticationToken) {
		return authenticationToken.equals(UsernamePasswordAuthenticationToken.class);
	}
}