package com.elite.auth;

import java.util.Map;

import com.elite.exception.CommunicationException;

/**
 * The interface that each Authentication class must implement.
 *
 * Contains the methods that are used for authenticating the user.
 */
public interface AuthenticationInterface {

	public static final String LDAP = "LDAP";

	public static final String AAA = "AAA";


	/**
	 * Authenticates the user by verifying the username and password and
	 * returns true if user is valid, false otherwise.
	 *
	 * @param user user name
	 * @param password password
	 * @return true if user is valid, else false.
	 * @throws CommunicationException if error occurs while retrieving the the details
	 * from the server.
	 */
	public boolean authenticate(String user, String password) throws CommunicationException;
	public boolean changePassword(String user, String oldPassword , String newPassword) throws CommunicationException;
	

}
