package com.elitecore.elitesm.web.core.system.security;


public class ServerManagerSecurityException extends Exception   {
    
    public ServerManagerSecurityException(){
        super("Security Violation Exception");    
    }
    
    public ServerManagerSecurityException(String message){
        super(message);
    }
}