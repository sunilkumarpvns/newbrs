/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   BaseTelnetClient.java                             
 * ModualName                                     
 * Created on Dec 12, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.netvertexsm.util.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.OperationFailedException;

/**
 * @author kaushikvira
 */
public abstract class BaseTelnetClient extends BaseCommunicationClient {
    
    protected static final String PROTOCOL = "Telnet";
    
    protected static final String MODULE   = "BASE TELNET CLIENT";
    
    public String                 SERVER_HOME;
    
    public String                 SERVER_EXPORT;
    
    public String                 JAVA_HOME;
    
    public String                 INVALID_LOGIN_MSG;
    
    public String                 PROMPT;
    
    public String                 STARTUP_SCRIPT_NAME;
    
    protected TelnetClient        telnetClient;
    
    public void connect( String remoteIp ,
                         int remotePort ) throws CommunicationException {
        try {
            if (telnetClient == null) {
                telnetClient = new TelnetClient();
            }
            telnetClient.connect(remoteIp, remotePort);
        }
        catch (SocketException socketEx) {
            throw new CommunicationException("Not able to get Connection");
        }
        catch (IOException ioException) {
            throw new CommunicationException("Not able to get Connection");
        }
        catch (Exception e) {
            throw new CommunicationException("Not able to get Connection");
        }
    }
    
    public void connect( String remoteIp ) throws CommunicationException {
        try {
            if (telnetClient == null) {
                telnetClient = new TelnetClient();
            }
            telnetClient.connect(remoteIp, 23);
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
    
    public void disconnect( ) {
        try {
            
            if (telnetClient != null)
                telnetClient.disconnect();
        }
        catch (Exception e) {}
        finally {
            if (telnetClient != null) 
                telnetClient = null;
        }
    }
    
    public void setDefaultTimeout( int timeout ) {
        telnetClient.setDefaultTimeout(timeout);
    }
    
    public int getDefaultTimeout( ) {
        return telnetClient.getDefaultTimeout();
    }
    
    public boolean isAlive( long timeout ) throws OperationFailedException {
         try {
            return telnetClient.sendAYT(timeout);
        }
        catch (IOException ioException) {
            throw new OperationFailedException("isAlive Operation Faild");
        }
        catch (InterruptedException intException) {
            throw new OperationFailedException("isAlive Operation Faild");
        }
        catch (Exception e) {
            throw new OperationFailedException("isAlive Operation Faild");
        }
        
    }
    
    public InetAddress getRemoteAddress( ) {
        return telnetClient.getRemoteAddress();
    }
    
    public int getRemotePort( ) {
        return telnetClient.getRemotePort();
    }
    
    public InetAddress getLocalAddress( ) {
        return telnetClient.getLocalAddress();
    }
    
    public int getLocalPort( ) {
        return telnetClient.getLocalPort();
    }
    
    public String getProtocol( ) {
        return PROTOCOL;
    }
    
    public boolean isConnected( ) {
        return telnetClient.isConnected();
    }
    
    public String readUntil( String pattern ,
                             int timeout ) {
        char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuffer sb = new StringBuffer(" ");
        char ch;
        long startTime = System.currentTimeMillis();
        
        try {
            while (true) {
                if (System.currentTimeMillis() - startTime > timeout)
                    return sb.toString();
                
                if (telnetClient.getInputStream().available() != 0) {
                    ch = (char) telnetClient.getInputStream().read();
                    sb.append(ch);
                    if (ch == lastChar) {
                        if (sb.toString().endsWith(pattern)) 
                            return sb.toString(); 
                    }
                }
            }
        }
        catch (Exception e) {
            return sb.toString();
        }
        
    }
    
    public String getInvalidLoginMsg( ) {
        return INVALID_LOGIN_MSG;
    }
    
    public void setInvalidLoginMsg( String invalidLoginMsg ) {
        this.INVALID_LOGIN_MSG = invalidLoginMsg;
    }
    
    public String getPrompt( ) {
        return PROMPT;
    }
    
    public void setPrompt( String prompt ) {
        this.PROMPT = prompt;
    }
    
    public String getStartupScriptName() {
		return STARTUP_SCRIPT_NAME;
	}

	public void setStartupScriptName(String startupScriptName) {
		STARTUP_SCRIPT_NAME = startupScriptName;
	}
    
}
