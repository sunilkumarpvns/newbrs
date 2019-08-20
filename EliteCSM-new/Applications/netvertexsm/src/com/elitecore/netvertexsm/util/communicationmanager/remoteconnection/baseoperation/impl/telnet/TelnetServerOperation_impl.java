/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   TelnetServerOperation_impl.java                             
 * ModualName                                     
 * Created on Oct 18, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.util.communicationmanager.remoteconnection.baseoperation.impl.telnet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.InvalidLoginException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerStartupConfigData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.netvertexsm.util.client.BaseTelnetClient;
import com.elitecore.netvertexsm.util.communicationmanager.remoteconnection.baseoperation.IRemoteOperartionManager;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.logger.Logger;

/**
 * @author kaushikvira
 */
public class TelnetServerOperation_impl extends BaseTelnetClient implements IRemoteOperartionManager {
    
    private static final String    PATH_SEP               = "/";
    
    public static final String     PROTOCOL               = "Telnet";
    
    public static final String     MODULE                 = "TELNET COMMUNICATION UTIL";
    
    public static final String     NETVERTEX_HOME            = "NETVERTEX_HOME";
    
    public int                     readTimeout            = 5000;
    
    private INetServerInstanceData iNetServerInstanceData = null;
    private INetServerTypeData  iNetServerTypeData = null;
    
    public TelnetServerOperation_impl( INetServerInstanceData iNetServerInstanceData, INetServerTypeData iNetServerTypeData) {
        this.iNetServerInstanceData = iNetServerInstanceData;
        this.iNetServerTypeData = iNetServerTypeData;
    }
    
    public void connect( ) throws CommunicationException {
        try {
            if (telnetClient == null) {
                telnetClient = new TelnetClient();
            }
            telnetClient.connect(iNetServerInstanceData.getAdminHost(), (int) iNetServerInstanceData.getStartupConfig().getCommunicationPort());
        }
        catch (SocketException socketEx) {
            throw new CommunicationException("Not able to get Connection", socketEx);
        }
        catch (IOException ioException) {
            throw new CommunicationException("Not able to get Connection", ioException);
        }
        catch (Exception e) {
            throw new CommunicationException("Not able to get Connection", e);
        }
    }
    
    public void cdServerSytemDir( ) throws CommunicationException {
        
        if (telnetClient == null)
            connect();
        
        if (!telnetClient.isConnected())
            connect();
        
        if (!telnetClient.isConnected())
            throw new CommunicationException("Telnet Client is not Connected.");
        
        String outputString = null;
        StringBuffer communicationOutput = null;
        PrintWriter out = null;
        try {
            communicationOutput = new StringBuffer();
            INetServerStartupConfigData serverStartupConfig = iNetServerInstanceData.getStartupConfig();
            out = new PrintWriter(new BufferedOutputStream(telnetClient.getOutputStream()));
            
            setServerEnv();
            
            outputString = readUntil(serverStartupConfig.getLoginPrompt(), this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append(serverStartupConfig.getUserName() + '\n');
            
            out.println(serverStartupConfig.getUserName());
            out.flush();
            outputString = readUntil(serverStartupConfig.getPasswordPrompt(), this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append("xxxxxx" + '\n');
            
            out.println(serverStartupConfig.getPassword());
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            if (outputString.indexOf(INVALID_LOGIN_MSG) != -1) { throw new InvalidLoginException("Loging faild"); }
            
            communicationOutput.append("cd " + SERVER_HOME + PATH_SEP + "bin" + '\n');
            
            out.println("cd " + SERVER_HOME + PATH_SEP + "system");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append("cd bin");
            
            out.println("cd bin");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append("pwd" + '\n');
            
            out.println("pwd");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            if (outputString.indexOf(SERVER_HOME + PATH_SEP + "system") == -1) { throw new DataManagerException(); }
            outputString = readUntil(PROMPT + " ", this.readTimeout);
        }
        
        catch (InvalidLoginException e) {
            out.close();
            Logger.logDebug(MODULE, communicationOutput.toString());
            throw e;
        }
        catch (CommunicationException e) {
            out.close();
            Logger.logDebug(MODULE, communicationOutput.toString());
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            out.close();
            Logger.logDebug(MODULE, communicationOutput.toString());
            throw new CommunicationException("Operation unsuccessfull", e);
        }
        finally {
            if (out != null) {
                out = null;
            }
        }
    }
    
    public String getServerState( ) throws CommunicationException {
        if (telnetClient == null)
            telnetClient = new TelnetClient();
        
        if (!telnetClient.isConnected()) {
            connect();
            cdServerSytemDir();
        }
        
        if (!telnetClient.isConnected())
            throw new CommunicationException("Telnet Client is not Connected.");
        
        String outputString = null;
        StringBuffer communicationOutput = null;
        PrintWriter out = null;
        try {
            communicationOutput = new StringBuffer();
            out = new PrintWriter(new BufferedOutputStream(telnetClient.getOutputStream()));
            
            out.println("cat _sys.state");
            communicationOutput.append("cat _sys.state");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            return outputString;
        }
        catch (Exception e) {
            Logger.logDebug(MODULE, "getServerState() Failed. Reason :" + e.getMessage());
            Logger.logTrace(MODULE, e);
        }
        finally {
            if (out != null) {
                out = null;
            }
        }
        return null;
    }
    
    public void startServer( ) throws CommunicationException {
        
        if (telnetClient == null)
            connect();
        
        if (!telnetClient.isConnected())
            connect();
        
        if (!telnetClient.isConnected())
            throw new CommunicationException("Telnet Client is not Connected.");
        
        String outputString;
        StringBuffer communicationOutput = null;
        PrintWriter out = null;
        try { 
            communicationOutput = new StringBuffer();
            INetServerStartupConfigData serverStartupConfig = iNetServerInstanceData.getStartupConfig();
            out = new PrintWriter(new BufferedOutputStream(telnetClient.getOutputStream()));
            
            setServerEnv();
            
            outputString = readUntil(serverStartupConfig.getLoginPrompt(), this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append(serverStartupConfig.getUserName() + '\n');
            
            out.println(serverStartupConfig.getUserName());
            out.flush();
            outputString = readUntil(serverStartupConfig.getPasswordPrompt(), this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append("xxxxxx" + '\n');
            
            out.println(serverStartupConfig.getPassword());
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            if (outputString.indexOf(INVALID_LOGIN_MSG) != -1) { throw new InvalidLoginException("Loging faild"); }
            communicationOutput.append(serverStartupConfig.getShell() + '\n');
            
            out.println(serverStartupConfig.getShell());
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString + '\n');
            
            communicationOutput.append("export JAVA_HOME=" + JAVA_HOME + '\n');
            
            out.println("export JAVA_HOME=" + JAVA_HOME);
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append("echo $JAVA_HOME" + '\n');
            out.println("echo $JAVA_HOME");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            
            if (outputString.indexOf(JAVA_HOME) == -1) { throw new DataManagerException("JAVA_HOME NOT SET"); }
            communicationOutput.append("export " + getServerExport(iNetServerInstanceData.getNetServerTypeId()) + "=" + SERVER_HOME + '\n');
            out.println("export " + getServerExport(iNetServerInstanceData.getNetServerTypeId()) + "=" + SERVER_HOME);
            out.flush();
            outputString = readUntil(PROMPT + "", this.readTimeout);
            communicationOutput.append(outputString + '\n');
            communicationOutput.append("echo $" + getServerExport(iNetServerInstanceData.getNetServerTypeId()) + "" + '\n');
            
            out.println("echo $" + getServerExport(iNetServerInstanceData.getNetServerTypeId()) + "");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            if (outputString.indexOf(SERVER_HOME) == -1) { throw new DataManagerException("" + getServerExport(iNetServerInstanceData.getNetServerTypeId()) + " NOT SET"); }
            communicationOutput.append("cd $" + getServerExport(iNetServerInstanceData.getNetServerTypeId()) + PATH_SEP + "bin" + '\n');
            
            out.println("cd $" + getServerExport(iNetServerInstanceData.getNetServerTypeId()) + PATH_SEP + "bin");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append("cd bin");
            
            out.println("cd bin");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append("pwd" + '\n');
            
            out.println("pwd");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            
            if (outputString.indexOf(SERVER_HOME + PATH_SEP + "bin") == -1) { throw new DataManagerException(); }
            communicationOutput.append("sh "+STARTUP_SCRIPT_NAME+".sh " + '\n');
            out.println("sh "+STARTUP_SCRIPT_NAME+".sh");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            
            if(outputString !=null && outputString.trim().equals(STARTUP_SCRIPT_NAME+".sh: ../logs/"+STARTUP_SCRIPT_NAME+"-server.log: cannot create")){
            	throw new CommunicationException("User ["+serverStartupConfig.getUserName()+"] is not accessible to start the server.");
            }
            
            communicationOutput.append(outputString);
            out.close();
            Logger.logDebug(MODULE, communicationOutput.toString());
        }
        catch (InvalidLoginException e) {
            out.close();
            Logger.logDebug(MODULE, communicationOutput.toString());
            throw e;
        }
        catch (CommunicationException e) {
            out.close();
            Logger.logDebug(MODULE, communicationOutput.toString());
            throw e;
        }
        catch (Exception e) {
            out.close();
            Logger.logDebug(MODULE, communicationOutput.toString());
            throw new CommunicationException("Operation unsuccessfull", e);
        }
        finally {
            if (out != null) {
                out = null;
            }
        }
        
    }
    
    private void setServerEnv( ) {
        setServerHome(iNetServerInstanceData.getServerHome());
        String javaHome = iNetServerInstanceData.getJavaHome();
		if(javaHome.toLowerCase().indexOf("jre")>1){
			javaHome=javaHome.substring(0,javaHome.toLowerCase().indexOf("jre")-1);
		}
        setJavahome(javaHome);
        setInvalidLoginMsg(iNetServerInstanceData.getStartupConfig().getFailureMsg());
        setPrompt(iNetServerInstanceData.getStartupConfig().getShellPrompt());
        setStartupScriptName(iNetServerTypeData.getStartupScriptName());
    }
    
    public String getServerHome( ) {
        return SERVER_HOME;
    }
    
    public void setServerHome( String serverHome ) {
        this.SERVER_HOME = serverHome;
    }
    
    public String getServerExport( String serverTypeId ) {
        
        if (serverTypeId == null)
            return null;
        else if (serverTypeId.equalsIgnoreCase(ServermgrConstant.NETVERTEX_SERVER))
            return NETVERTEX_HOME;
        else return null;
    }
    
    public String getJavahome( ) {
        return JAVA_HOME;
    }
    
    public void setJavahome( String java_home ) {
        JAVA_HOME = java_home;
    }
    
    public void disconnect( ) {
        try {
            Logger.logDebug(MODULE, "Disconnect called.");
            if (telnetClient != null && telnetClient.isConnected())
                telnetClient.disconnect();
        }
        catch (Exception e) {
            Logger.logTrace(MODULE, e);
        }
        finally {
            Logger.logDebug(MODULE, "Telnet Client set to null.");
            if (telnetClient != null) {
                telnetClient = null;
            }
        }
    }
    
    public boolean isConnectionPossible( ) {
        try {
            if (telnetClient == null)
                connect();
            
            if (!telnetClient.isConnected())
                connect();
            
            if (!telnetClient.isConnected())
                throw new CommunicationException("Telnet Client Unable to Connect.");
            
            String outputString = null;
            StringBuffer communicationOutput = null;
            PrintWriter out = null;
            
            communicationOutput = new StringBuffer();
            INetServerStartupConfigData serverStartupConfig = iNetServerInstanceData.getStartupConfig();
            out = new PrintWriter(new BufferedOutputStream(telnetClient.getOutputStream()));
            
            setServerEnv();
            
            outputString = readUntil(serverStartupConfig.getLoginPrompt(), this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append(serverStartupConfig.getUserName() + '\n');
            
            out.println(serverStartupConfig.getUserName());
            out.flush();
            outputString = readUntil(serverStartupConfig.getPasswordPrompt(), this.readTimeout);
            communicationOutput.append(outputString);
            communicationOutput.append("xxxxxx" + '\n');
            
            out.println(serverStartupConfig.getPassword());
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            communicationOutput.append(outputString);
            if (outputString.indexOf(INVALID_LOGIN_MSG) != -1) { throw new InvalidLoginException("Loging faild"); }
            
        }
        catch (InvalidLoginException e) {
            Logger.logDebug(MODULE, "Not able to Login :" + "Server id :" + iNetServerInstanceData.getNetServerId() + " User :-" + iNetServerInstanceData.getStartupConfig().getUserName());
            return false;
        }
        catch (CommunicationException e) {
            Logger.logDebug(MODULE, "Telnet Communication Not Possible");
            return false;
        }
        catch (Exception e) {
            Logger.logDebug(MODULE, "Telnet Communication Not Possible");
            Logger.logTrace(MODULE, e);
            return false;
        }
        finally {
            disconnect();
        }
        return true;
    }
    
}
