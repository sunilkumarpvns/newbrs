/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   TelnetServerOperation_impl.java                             
 * ModualName                                     
 * Created on Oct 18, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.communicationmanager.remoteconnection.baseoperation.impl.telnet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.net.telnet.TelnetClient;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.elitesm.datamanager.core.exceptions.communication.InvalidLoginException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerStartupConfigData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData;
import com.elitecore.elitesm.util.client.BaseTelnetClient;
import com.elitecore.elitesm.util.communicationmanager.remoteconnection.baseoperation.IRemoteOperartionManager;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * @author kaushikvira
 */
public class TelnetServerOperation_impl extends BaseTelnetClient implements IRemoteOperartionManager {
    
    public static final String     PROTOCOL               = "Telnet";
    
    public static final String     MODULE                 = "TELNET COMMUNICATION UTIL";
    
    public static final String     ELITEAAA_HOME            = "ELITEAAA_HOME";
    
    public int                     readTimeout            = 5000;
    
    private INetServerInstanceData iNetServerInstanceData = null;
    private INetServerTypeData  iNetServerTypeData=null;
    
    public TelnetServerOperation_impl( INetServerInstanceData iNetServerInstanceData, INetServerTypeData iNetServerTypeData) {
        this.iNetServerInstanceData = iNetServerInstanceData;
        this.iNetServerTypeData=iNetServerTypeData;
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
        
    public void startServer( ) throws CommunicationException {
        
        if (telnetClient == null){
            connect();
        }
        
        if (!telnetClient.isConnected()){
        	connect();
        }
        
        if (!telnetClient.isConnected()){
            throw new CommunicationException("Telnet Client is not Connected.");
        }
        
        String outputString;
        StringBuffer communicationOutput = null;
        PrintWriter out = null;
        try {
            communicationOutput = new StringBuffer();
            INetServerStartupConfigData serverStartupConfig = iNetServerInstanceData.getStartupConfig();
            out = new PrintWriter(new BufferedOutputStream(telnetClient.getOutputStream()));
            
            setServerEnv();
            
            outputString = readUntil(serverStartupConfig.getLoginPrompt(), this.readTimeout);
            outputString = replaceTabSpaceChars(outputString, "");
            communicationOutput.append(outputString);
            communicationOutput.append(serverStartupConfig.getUserName() + '\n');
            
            out.println(serverStartupConfig.getUserName());
            out.flush();
            outputString = readUntil(serverStartupConfig.getPasswordPrompt(), this.readTimeout);
            outputString = replaceTabSpaceChars(outputString, "");
            communicationOutput.append(outputString);
            communicationOutput.append("xxxxxxxx" + '\n');
            
            out.println(PasswordEncryption.getInstance().decrypt(serverStartupConfig.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT));
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            outputString = replaceTabSpaceChars(outputString, "");
            communicationOutput.append(outputString);
            
            if (outputString.indexOf(INVALID_LOGIN_MSG) != -1) { throw new InvalidLoginException("Loging faild"); }
           
            
            communicationOutput.append(serverStartupConfig.getShell() + '\n');
            
            out.println(serverStartupConfig.getShell());
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            outputString = replaceTabSpaceChars(outputString, "");
            communicationOutput.append(outputString);
            
            communicationOutput.append("stty column 200" + "\n");
            out.println("stty columns 200");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            outputString = replaceTabSpaceChars(outputString, "");
            communicationOutput.append(outputString);
            
            communicationOutput.append("stty rows 40" + "\n");
            out.println("stty rows 40");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            outputString = replaceTabSpaceChars(outputString, "");
            communicationOutput.append(outputString);
      
            communicationOutput.append("cd " + SERVER_HOME  +"\n");
            out.println("cd " + SERVER_HOME);
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            outputString = replaceTabSpaceChars(outputString, "");
            communicationOutput.append(outputString);
            
            
            communicationOutput.append("cd bin" +"\n");
            out.println("cd bin");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            outputString = replaceTabSpaceChars(outputString, "");
            communicationOutput.append(outputString);
            
            communicationOutput.append("ls" + '\n');
            out.println("ls");
            out.flush();
            outputString = readUntil(PROMPT + " ", this.readTimeout);
            outputString = replaceTabSpaceChars(outputString, "");
            communicationOutput.append(outputString);
            
            if(outputString.contains(STARTUP_SCRIPT_NAME+".sh")){
            	communicationOutput.append("sh "+STARTUP_SCRIPT_NAME+".sh " + '\n');
            	out.println("sh "+STARTUP_SCRIPT_NAME+".sh");
            	out.flush();
            	outputString = readUntil(PROMPT + " ", this.readTimeout);
            	communicationOutput.append(outputString);
            	outputString = readUntil(PROMPT + " ", this.readTimeout);
            }else{
            	throw new DataManagerException("Can not find "+STARTUP_SCRIPT_NAME +".sh file in bin directory.");
            }
            if(outputString !=null && outputString.trim().equals(STARTUP_SCRIPT_NAME+".sh: ../logs/"+STARTUP_SCRIPT_NAME+"-server.log: cannot create")){
            	throw new CommunicationException("User ["+serverStartupConfig.getUserName()+"] is not accessible to start the server.");
            }
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
    private  String replaceTabSpaceChars(String text, String replacement) {
    	String replace = "[\t]";
    	return text.replaceAll(replace, replacement);
    }
    private void setServerEnv( ) {
        setServerHome(iNetServerInstanceData.getServerHome());
        setJavahome(iNetServerInstanceData.getJavaHome());
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
        else if (serverTypeId.equalsIgnoreCase(ServermgrConstant.AAA_SERVER))
            return ELITEAAA_HOME;
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
            if (telnetClient == null){
            	connect();
            }
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
            communicationOutput.append(" "+serverStartupConfig.getUserName() + '\n');
            out.println(serverStartupConfig.getUserName());
            out.flush();
            outputString = readUntil(serverStartupConfig.getPasswordPrompt(), this.readTimeout);
            communicationOutput.append(outputString.trim());
            communicationOutput.append(" xxxxxx" + '\n');
            
            out.println(PasswordEncryption.getInstance().decrypt(serverStartupConfig.getPassword(), PasswordEncryption.ELITE_PASSWORD_CRYPT));
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
