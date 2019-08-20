package com.elitecore.coreeap.data;

import java.util.List;

import com.elitecore.coreeap.util.constants.EapTypeConstants;


public interface ICustomerAccountInfoProvider {
	public ICustomerAccountInfo getCustomerAccountInfo() throws AccountInfoProviderException;
	public ICustomerAccountInfo getCustomerAccountInfo(String strUserIdentity) throws AccountInfoProviderException;
	public ICustomerAccountInfo getCustomerAccountInfo(String strUserIdentity, EapTypeConstants constant) throws AccountInfoProviderException;
	public List<String> getClientUserIdentityAttributeList();
	
	/**
	 * Checks whether LDAP bind authentication should be performed on this customer.
	 * This method <b>MUST</b> be called before calling {@link #doLdapBindAuthentication(String, ICustomerAccountInfo)}
	 * @param accountData
	 * @return
	 */
	public boolean checkLDAPBindAuthenticationRequired(ICustomerAccountInfo accountData);
	
	/**
	 * Uses LDAP Bind integration mechanism to bind to server using the DN of the customer.
	 * {@link #checkLDAPBindAuthenticationRequired(ICustomerAccountInfo)} <b>MUST</b> be called before calling this method
	 * @param decodedPassword Decoded password based on encryption standard
	 * @param accountData user profile
	 * @return true if bind authentication was successful false otherwise
	 */
	public boolean doLdapBindAuthentication(String decodedPassword, ICustomerAccountInfo accountData);
}
