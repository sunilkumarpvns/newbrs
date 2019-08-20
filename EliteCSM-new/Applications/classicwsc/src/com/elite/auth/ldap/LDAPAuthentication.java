package com.elite.auth.ldap;

import java.util.Enumeration;
import java.util.Map;

import com.elite.auth.DefaultAuthenticationInterface;
import com.elite.config.LDAPConfig;
import com.elite.context.Context_Manager;
import com.elite.context.WSCContext;
import com.elite.exception.CommunicationException;
import com.opensymphony.xwork2.ActionContext;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPModification;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPv2;



public class LDAPAuthentication extends DefaultAuthenticationInterface
{

		public boolean authenticate(String strUserName, String password) throws CommunicationException 
		{
			LDAPConnection ldapConnection = null;
			String strLDAPDataSourceName = null;
	
			boolean isAuthenticated = false;
			LDAPSearchResults results = null;
	
			try
			{
				strLDAPDataSourceName = "LDAP_AUTH_DS1";
				ldapConnection = LDAPConnectionManager.getInstance(strLDAPDataSourceName).getConnection();
				LDAPConfig ldap_config = (LDAPConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ldap");
	
				String[] ldapEntryAttrs = { ldap_config.getUser_password() };
				results = ldapConnection.search(ldap_config.getSearch_base_dn(),LDAPv2.SCOPE_SUB,ldap_config.getUser_base_dn()+strUserName,ldapEntryAttrs,false);
				String nextValue = null;
				if(results != null && results.hasMoreElements()){
					LDAPEntry myEntry = null;
					myEntry = results.next();
					LDAPAttributeSet entryAttrs = myEntry.getAttributeSet();
					
					Enumeration attrsInSet = entryAttrs.getAttributes();
					while (attrsInSet.hasMoreElements()) {
						LDAPAttribute nextAttr = (LDAPAttribute) attrsInSet.nextElement();
						String attrName = nextAttr.getName();
						if(ldap_config.getPassword() != null){
							if (attrName.equalsIgnoreCase(ldap_config.getUser_password())) {
								Enumeration valsInAttr = nextAttr.getStringValues();
								while(valsInAttr.hasMoreElements()) {
									nextValue = (String) valsInAttr.nextElement();
									if(nextValue != null){
										nextValue = nextValue.trim();
										if(nextValue.equals(password))
										{
											((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).setAttribute("dn", myEntry.getDN());
											isAuthenticated = true;
										}
									}
								}
							}
						}
					}
				}
			}catch(Exception e) {
				throw new CommunicationException(e.getMessage());
			}
			return isAuthenticated;
	}
	public boolean changePassword(String user, String oldPassword,String newPassword) throws CommunicationException {
		LDAPConnection ldapConnection = null;
		String strLDAPDataSourceName = "LDAP_AUTH_DS1";
		try
		{
			ldapConnection = LDAPConnectionManager.getInstance(strLDAPDataSourceName).getConnection();
			LDAPConfig ldap_config = (LDAPConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ldap");
			LDAPAttribute ldapatt = new LDAPAttribute("password",newPassword);
			LDAPModification ldapmodi = new LDAPModification(LDAPModification.REPLACE,ldapatt);
			ldapConnection.modify((String)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("dn"),ldapmodi);
			return true;
		}
		catch (Exception e) {
			throw new CommunicationException(e.getMessage());
		}
		
	}

}
