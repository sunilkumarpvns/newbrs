package com.elite.model;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elite.auth.db.DBAuthentication;
import com.elite.auth.db.DBConnectionManager;
import com.elite.auth.ldap.LDAPAuthentication;
import com.elite.config.AAAConfig;
import com.elite.config.ORACLEConfig;
import com.elite.config.WSCConfig;
import com.elite.context.Context_Manager;
import com.elite.context.WSCContext;
import com.elite.exception.CommunicationException;
import com.elite.user.UserOtherDetail;
import com.elite.user.Userbean;
import com.elitecore.radius.client.RadiusClient;
import com.opensymphony.xwork2.ActionContext;

public class LoginModal {
	Logger logger = Logger.getLogger("wsc");
	public boolean authenticate(String user, String password) throws Exception {
		boolean isAuthenticate = false;
		String auth_type = ((WSCConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("wsc")).getAuth_type();
		logger.info("Authentication Type is "+auth_type);
		if( auth_type.equals("LDAP")) {
			LDAPAuthentication authentication = new LDAPAuthentication();
			try {
				isAuthenticate = authentication.authenticate(user,password);
			}catch(Exception e) {
				throw new CommunicationException("Error authenticating user, reason: " + e.getMessage());
			}
		}else if(auth_type.equals("AAA")) {
			AAAConfig aaaconfig = (AAAConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("aaa");
			String ip = aaaconfig.getIp();
			int port = aaaconfig.getPort();
			String sharedSecret = aaaconfig.getShared_secret();
			int timeout = aaaconfig.getTimeout();
			InetAddress address;
			try {
				address = InetAddress.getByName(ip);
			}catch(Exception e){
				throw new CommunicationException("Unknowd Host IP.");
			}

			RadiusClient client = new RadiusClient(address, port, sharedSecret, timeout);
			try {
				isAuthenticate = client.authenticateAAAUser(user,password);
			}catch(Exception e) {
				throw new CommunicationException("Error occured while authenticating user: " + user+" " +e);
			}

		}else if( auth_type.equals("ORACLEDB")) {
			DBAuthentication authentication = new DBAuthentication();
			try {
				isAuthenticate = authentication.authenticate(user,password);
			}catch(Exception e) {
				throw new CommunicationException("Error authenticating user, reason: " + e.getMessage());
			}
		}
		return isAuthenticate;

	}
	
	public boolean changePassword(String username, String oldpassword, String newpassword) throws Exception
	{
		boolean ismodified = false;
		
		String auth_type = ((WSCConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("wsc")).getAuth_type();
		logger.info("Authentication Type is "+auth_type);
		if( auth_type.equals("LDAP")) {
			LDAPAuthentication authentication = new LDAPAuthentication();
			try {
				ismodified = authentication.changePassword(username, oldpassword, newpassword);
			}catch(Exception e) {
				throw new CommunicationException("Error changing Password, reason: " + e.getMessage());
			}
		}else if(auth_type.equals("AAA")) {
			ismodified = false;
		}else if( auth_type.equals("ORACLEDB")) {
			DBAuthentication authentication = new DBAuthentication();
			try {
				ismodified = authentication.changePassword(username, oldpassword, newpassword);
			}catch(Exception e) {
				throw new CommunicationException("Error changing Password, reason: " + e.getMessage());
			}
		}
		return ismodified;
	}
	
	public UserOtherDetail getdetail(String strUserName) throws CommunicationException 
	{
		Connection oracleConnection = null;
		UserOtherDetail userotherdetails = new UserOtherDetail();
		try
		{ 
			String strDataSourceName = ((ORACLEConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ora")).getDatasourcename();
			oracleConnection = DBConnectionManager.getInstance(strDataSourceName).getConnection();
			Statement stat = oracleConnection.createStatement();
			String sql = "select * from customeraccount where customeridentifier = '"+ strUserName+ "'";
			ResultSet rs =  stat.executeQuery(sql);
			if(rs.next())
			{
				
				userotherdetails.setCustomername( (rs.getString("customername") == null)?"-": rs.getString("customername"));
				userotherdetails.setServicetype((rs.getString("servicetype") == null)?"-": rs.getString("servicetype"));
				userotherdetails.setCustomertype((rs.getString("customertype") == null)?"-": rs.getString("customertype"));
				userotherdetails.setCustomersubtype((rs.getString("customersubtype") == null)?"-": rs.getString("customersubtype"));
				userotherdetails.setKeyquestion((rs.getString("keyquestion") == null)?"-": rs.getString("keyquestion"));
				userotherdetails.setKeyanswer((rs.getString("keyanswer") == null)?"-": rs.getString("keyanswer"));
				userotherdetails.setEmailaddress((rs.getString("emailaddress") == null)?"-": rs.getString("emailaddress"));
				userotherdetails.setMobilephone((rs.getString("mobilephone") == null)?"-": rs.getString("mobilephone"));
				userotherdetails.setAddressline1((rs.getString("addressline1") == null)?"-": rs.getString("addressline1"));
				userotherdetails.setAddressline2((rs.getString("addressline2") == null)?"-": rs.getString("addressline2"));
				userotherdetails.setPostalcode((rs.getString("postalcode") == null)?"-": rs.getString("postalcode"));
				userotherdetails.setHomephone((rs.getString("homephone") == null)?"-": rs.getString("homephone"));
				userotherdetails.setCity((rs.getString("city") == null)?"-": rs.getString("city"));
				userotherdetails.setState((rs.getString("state") == null)?"-": rs.getString("state"));
				userotherdetails.setCountry((rs.getString("country") == null)?"-": rs.getString("country"));
				userotherdetails.setAccountstatus((rs.getString("accountstatus") == null)?"-": rs.getString("accountstatus"));
				userotherdetails.setCreateddate((rs.getString("createddate") == null)?"-": rs.getString("createddate"));
				sql = "select packagename from packagedefinition where packageid = '"+ rs.getString("packageid")+ "'";
				rs =  stat.executeQuery(sql);
				if (rs.next())
				{
					userotherdetails.setPackaage(rs.getString("packagename")); 
				}
			}
		}
		catch (Exception e) {
			throw new CommunicationException(e.getMessage()+ e);
		}
		return userotherdetails;
	}
	
	public long getTotalUsage(Userbean user) throws CommunicationException
	{
		String sql = "" ;
		Connection oracleConnection = null;
		ResultSet rs = null;
		try
		{ 
			String strDataSourceName = ((ORACLEConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ora")).getDatasourcename();
			oracleConnection = DBConnectionManager.getInstance(strDataSourceName).getConnection();
			Statement stat = oracleConnection.createStatement();
			
			if(user.getUserotherdetail().getServicetype().equals("DATA"))
			{
				sql = "select sum(volume)/1024 from cdrchargetable where customeridentifier = '"+ user.getUsername()+ "'";
				rs =  stat.executeQuery(sql);
				if (rs.next())
				{
					return rs.getLong(1);
				}
			}
			else if(user.getUserotherdetail().getServicetype().equals("VOIP"))
			{
				sql = "select sum(sessiontime) from cdrchargetable where customeridentifier = '"+ user.getUsername()+ "'";
				rs =  stat.executeQuery(sql);
				if (rs.next())
				{
					return rs.getLong(1);
				}
			}
			logger.info(sql);
				
		}
		catch (Exception e) {
			throw new CommunicationException(e.getMessage()+ e);
			
		}
		return 0;
		
		
	}
	public boolean profileUpdate(UserOtherDetail userdetail, Userbean user) throws CommunicationException 
	{
		Connection oracleConnection = null;
		try
		{ 
			String strDataSourceName = ((ORACLEConfig)((WSCContext)ActionContext.getContext().getApplication().get("wsc_context")).getAttribute("ora")).getDatasourcename();
			oracleConnection = DBConnectionManager.getInstance(strDataSourceName).getConnection();
			Statement stat = oracleConnection.createStatement();
			String sql = "update customeraccount set emailaddress = '"+ userdetail.getEmailaddress()+"'," +
					" keyquestion = '"+ userdetail.getKeyquestion()+"'," +
					" keyanswer = '"+ userdetail.getKeyanswer()+"'," +
					" mobilephone = '"+ userdetail.getMobilephone()+"'," +
					" addressline1 = '"+ userdetail.getAddressline1()+"'," +
					" addressline2 = '"+ userdetail.getAddressline2()+"'," +
					" postalcode = '"+ userdetail.getPostalcode()+"'," +
					" homephone = '"+ userdetail.getHomephone()+"' " +
					"where customeridentifier = '"+ user.getUsername()+ "'";
			stat.executeUpdate(sql);
			user.setUserotherdetail( getdetail(user.getUsername()));
			
		}
		catch (Exception e) {
			throw new CommunicationException(e.getMessage()+ e);
		}
		return true;
	}
	
	
	public boolean processLoginAttempt(String username, String password)
	{
		if ("admin".equals(username) && "admin".equals(password)) 
        {
        	Userbean user = new Userbean(username, password, new Date());
        	Map session = ActionContext.getContext().getSession();
        	session.put("user",user);
            return true;
        }
		else if ("nirav@eliteaaa.in".equals(username) && "elitecore".equals(password)) 
        {
        	Userbean user = new Userbean(username, password, new Date());
        	Map session = ActionContext.getContext().getSession();
        	session.put("user",user);
            return true;
        }
		 else
		 {
			 return false;
		 }
		 
	}

	
}
