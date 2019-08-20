package com.elitecore.elitesm.datamanager.core.exceptions.auhorization;

import org.springframework.security.core.AuthenticationException;

/**
 * When client send HTTP request to the system and same time if the Session Factory is not getting up,
 * In such a case client will get "System is not ready to accept your request" in HTTP Response
 * 
 * @author nayana.rathod
 *
 */

public class SystemNotReadyException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public SystemNotReadyException(String msg) {
		super(msg);
	}
	   
}
