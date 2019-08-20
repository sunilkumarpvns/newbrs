/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   NetServerStartupConfigData.java                 		
 * ModualName server    			      		
 * Created on 19 October, 2007
 * Last Modified on                                     
 * @author :  kaushik vira
 */                                                     
package com.elitecore.elitesm.datamanager.servermgr.data;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
   
public class NetServerStartupConfigData extends BaseData implements INetServerStartupConfigData{

    private String netServerStartupConfigId;
    private String netServerId;
    private String protocol;
    private long communicationPort;
    private String userName;
    private String password;
    private String loginPrompt;
    private String passwordPrompt;
    private String shellPrompt;
    private String failureMsg;
    private long operationTimeOut;
    private java.sql.Timestamp lastModifiedDate;
    private String lastModifiedByStaffId;
    private String commonStatusId;
    private java.sql.Timestamp statusChangeDate;
    private String shell;
    
    



    
    public String getShell() {
        return shell;
    }

    
    public void setShell( String shell ) {
        this.shell = shell;
    }

    public String getNetServerStartupConfigId(){
        return netServerStartupConfigId;
    }

	public void setNetServerStartupConfigId(String netServerStartupConfigId) {
		this.netServerStartupConfigId = netServerStartupConfigId;
	}


    public String getNetServerId(){
        return netServerId;
    }

	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}


    public String getProtocol(){
        return protocol;
    }

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}


    public long getCommunicationPort(){
        return communicationPort;
    }

	public void setCommunicationPort(long communicationPort) {
		this.communicationPort = communicationPort;
	}


    public String getUserName(){
        return userName;
    }

	public void setUserName(String userName) {
		this.userName = userName;
	}


    public String getPassword(){
        return password;
    }

	public void setPassword(String password) {
		this.password = password;
	}


    public String getLoginPrompt(){
        return loginPrompt;
    }

	public void setLoginPrompt(String loginPrompt) {
		this.loginPrompt = loginPrompt;
	}


    public String getPasswordPrompt(){
        return passwordPrompt;
    }

	public void setPasswordPrompt(String passwordPrompt) {
		this.passwordPrompt = passwordPrompt;
	}


    public String getShellPrompt(){
        return shellPrompt;
    }

	public void setShellPrompt(String shellPrompt) {
		this.shellPrompt = shellPrompt;
	}


    public String getFailureMsg(){
        return failureMsg;
    }

	public void setFailureMsg(String failureMsg) {
		this.failureMsg = failureMsg;
	}


    public long getOperationTimeOut(){
        return operationTimeOut;
    }

	public void setOperationTimeOut(long operationTimeOut) {
		this.operationTimeOut = operationTimeOut;
	}


    public java.sql.Timestamp getLastModifiedDate(){
        return lastModifiedDate;
    }

	public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}


    public String getLastModifiedByStaffId(){
        return lastModifiedByStaffId;
    }

	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}


    public String getCommonStatusId(){
        return commonStatusId;
    }

	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}


    public java.sql.Timestamp getStatusChangeDate(){
        return statusChangeDate;
    }

	public void setStatusChangeDate(java.sql.Timestamp statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	
	@Override
        public String toString() {
            StringWriter out = new StringWriter();
            PrintWriter writer = new PrintWriter(out);
            writer.println();
            writer.println("------------NetServerStartupConfigData-----------------");
            writer.println("netServerStartupConfigId=" + netServerStartupConfigId);  
            writer.println("netServerId=" + netServerId);  
            writer.println("protocol=" + protocol);  
            writer.println("communicationPort=" + communicationPort);  
            writer.println("userName=" + userName);  
            writer.println("password=" + password);  
            writer.println("loginPrompt=" + loginPrompt);  
            writer.println("passwordPrompt=" + passwordPrompt);  
            writer.println("shellPrompt=" + shellPrompt);  
            writer.println("failureMsg=" + failureMsg);  
            writer.println("operationTimeOut=" + operationTimeOut);  
            writer.println("lastModifiedDate=" + lastModifiedDate);  
            writer.println("lastModifiedByStaffId=" + lastModifiedByStaffId);  
            writer.println("commonStatusId=" + commonStatusId);  
            writer.println("statusChangeDate=" + statusChangeDate);  
            writer.println("shell=" + shell  );  
            writer.println("----------------------------------------------------");
            writer.close();                                               
            return out.toString();                                        
        }


}
