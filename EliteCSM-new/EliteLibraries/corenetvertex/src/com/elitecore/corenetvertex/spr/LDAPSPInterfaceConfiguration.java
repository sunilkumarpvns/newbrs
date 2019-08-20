package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;

import java.util.ArrayList;

/**
 * This interface provide LDAP configuration information for subscriber profile repository
 * 
 * @author Harsh Patel
 * 
 */
public interface LDAPSPInterfaceConfiguration extends SPInterfaceConfiguration {


	LDAPDataSource getLdapDataSource();

	/**
	 * This method returns array of expiryDatePatterns
	 * 
	 * @return SimpleDateFormat
	 */

	public String getExpiryDatePattern();

	/**
	 * This method returns passwordDescryptType
	 * 
	 * @return int
	 */

	public int getPasswordDecryptType();

	public String getUserPrefix();

    public ArrayList<String> getSearchBaseDnList();

}
