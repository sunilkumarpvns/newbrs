package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UpdateNetServerAdminInterfaceDetailForm extends BaseWebForm{
	private Long netServerId;
	private String adminInterfaceIP;
	private int adminInterfacePort;
	private String action;
        private String chkUpdateStarupConf;
        
        
        private String netServerStartupConfigId;
        private String protocol;
        private long   communicationPort;
        private String userName;
        private String password;
        private String loginPrompt;
        private String passwordPrompt;
        private String shellPrompt;
        private String failureMsg;
        private long   operationTimeOut;
        private String shell;
        
        
        public String getShell() {
            return shell;
        }
        
        public void setShell( String shell ) {
            this.shell = shell;
        }
        
        public String getNetServerStartupConfigId() {
            return netServerStartupConfigId;
        }
        
        public void setNetServerStartupConfigId( String netServerStartupConfigId ) {
            this.netServerStartupConfigId = netServerStartupConfigId;
        }
        
        public Long getNetServerId() {
            return netServerId;
        }
        
        public void setNetServerId( Long netServerId ) {
            this.netServerId = netServerId;
        }
        
        public String getProtocol() {
            return protocol;
        }
        
        public void setProtocol( String protocol ) {
            this.protocol = protocol;
        }
        
        public long getCommunicationPort() {
            return communicationPort;
        }
        
        public void setCommunicationPort( long communicationPort ) {
            this.communicationPort = communicationPort;
        }
        
        public String getUserName() {
            return userName;
        }
        
        public void setUserName( String userName ) {
            this.userName = userName;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword( String password ) {
            this.password = password;
        }
        
        public String getLoginPrompt() {
            return loginPrompt;
        }
        
        public void setLoginPrompt( String loginPrompt ) {
            this.loginPrompt = loginPrompt;
        }
        
        public String getPasswordPrompt() {
            return passwordPrompt;
        }
        
        public void setPasswordPrompt( String passwordPrompt ) {
            this.passwordPrompt = passwordPrompt;
        }
        
        public String getShellPrompt() {
            return shellPrompt;
        }
        
        public void setShellPrompt( String shellPrompt ) {
            this.shellPrompt = shellPrompt;
        }
        
        public String getFailureMsg() {
            return failureMsg;
        }
        
        public void setFailureMsg( String failureMsg ) {
            this.failureMsg = failureMsg;
        }
        
        public long getOperationTimeOut() {
            return operationTimeOut;
        }
        
        public void setOperationTimeOut( long operationTimeOut ) {
            this.operationTimeOut = operationTimeOut;
        }
       
        public String getChkUpdateStarupConf( ) {
            return chkUpdateStarupConf;
        }
        
        public void setChkUpdateStarupConf( String chkUpdateStarupConf ) {
            this.chkUpdateStarupConf = chkUpdateStarupConf;
        }
        
        public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAdminInterfaceIP() {
		return adminInterfaceIP;
	}
	public void setAdminInterfaceIP(String adminInterfaceIP) {
		this.adminInterfaceIP = adminInterfaceIP;
	}
	
	public int getAdminInterfacePort() {
		return adminInterfacePort;
	}
	public void setAdminInterfacePort(int adminInterfacePort) {
		this.adminInterfacePort = adminInterfacePort;
	}
	
}
