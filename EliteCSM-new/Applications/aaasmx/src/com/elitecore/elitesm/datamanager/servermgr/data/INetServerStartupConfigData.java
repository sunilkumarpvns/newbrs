/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   INetServerStartupConfigData.java                 		
 * ModualName server    			      		
 * Created on 19 October, 2007
 * Last Modified on                                     
 * @author :  kaushik vira
 */                                                     
package com.elitecore.elitesm.datamanager.servermgr.data;

public interface INetServerStartupConfigData{



    public String getShell();
    public void setShell( String shell );
    public String getNetServerStartupConfigId();
    public void setNetServerStartupConfigId(String netServerStartupConfigId);

    public String getNetServerId();
    public void setNetServerId(String netServerId);

    public String getProtocol();
    public void setProtocol(String protocol);

    public long getCommunicationPort();
    public void setCommunicationPort(long communicationPort);

    public String getUserName();
    public void setUserName(String userName);

    public String getPassword();
    public void setPassword(String password);

    public String getLoginPrompt();
    public void setLoginPrompt(String loginPrompt);

    public String getPasswordPrompt();
    public void setPasswordPrompt(String passwordPrompt);

    public String getShellPrompt();
    public void setShellPrompt(String shellPrompt);

    public String getFailureMsg();
    public void setFailureMsg(String failureMsg);

    public long getOperationTimeOut();
    public void setOperationTimeOut(long operationTimeOut);

    public java.sql.Timestamp getLastModifiedDate();
    public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate);

    public String getLastModifiedByStaffId();
    public void setLastModifiedByStaffId(String lastModifiedByStaffId);

    public String getCommonStatusId();
    public void setCommonStatusId(String commonStatusId);

    public java.sql.Timestamp getStatusChangeDate();
    public void setStatusChangeDate(java.sql.Timestamp statusChangeDate);
    public String toString();

}
