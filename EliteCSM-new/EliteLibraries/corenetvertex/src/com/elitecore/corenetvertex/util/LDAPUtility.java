package com.elitecore.corenetvertex.util;

import netscape.ldap.LDAPException;

import com.elitecore.corenetvertex.constants.CommonConstants;

public class LDAPUtility {

    private static final int LDAP_SERVER_DOWN = 81;
    private static final int CONNECT_ERROR = 91;
    private static final int LDAP_TIMEOUT = 85;
    private static final int TIME_LIMIT_EXCEEDED = 3;

    /**
     * 
     * @param ex
     *            LDAPException
     * @return boolean return true if LDAPException is for Server
     *         down,connection error,LDAP time out ,time limit exceed and false
     *         if not
     */
    public static boolean isLDAPDownException(LDAPException ex) {
	int errorCode = ex.getLDAPResultCode();
	switch (errorCode) {
	    case LDAP_SERVER_DOWN:
	    case CONNECT_ERROR:
	    case LDAP_TIMEOUT:
	    case TIME_LIMIT_EXCEEDED:
		return true;
	    default:
		return false;
	}
    }

    public static boolean isRFC2254LDAPFilter(char ch) {
	return ch == CommonConstants.BACKSLASH || ch == CommonConstants.ASTERISK
		|| ch == CommonConstants.OPENING_PARENTHESES || ch == CommonConstants.CLOSING_PARENTHESES
		|| ch == CommonConstants.NUL;
    }
}
