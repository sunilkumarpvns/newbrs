/**
 * Copyright (C) Elitecore Technologies Ltd. FileName BaseServerException.java
 * ModualName com.elitecore.netvertexsm.datamanager.core.exceptions.server Created
 * on Feb 14, 2008 Last Modified on
 * 
 * @author : kaushikvira
 */

package com.elitecore.netvertexsm.datamanager.core.exceptions.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;

public class BaseServerException extends DataManagerException {
    
    private static final long serialVersionUID = -3188168105619818638L;
    List<String> parameterList = new ArrayList<String>();
    
    public BaseServerException() {}
    
    public BaseServerException( String message ) {
        super(message);
    }
    
    public BaseServerException( Throwable cause ) {
        super(cause);
    }
    
    public BaseServerException( String message , Throwable cause ) {
        super(message, cause);
    }
    
    public BaseServerException( String message , String param1 ) {
        new BaseServerException(message, param1, null, null, null);
    }
    
    public BaseServerException( String message , String param1 , String param2 ) {
        new BaseServerException(message, param1, param2, null, null);
    }
    
    public BaseServerException( String message , String param1 , String param2 , String param3 ) {
        new BaseServerException(message, param1, param2, param3, null);
    }
    
    public BaseServerException( String message , String param1 , Throwable cause ) {
        new BaseServerException(message, param1, null, null, cause);
    }
    
    public BaseServerException( String message , String param1 , String param2 , Throwable cause ) {
        new BaseServerException(message, param1, param2, null, cause);
    }
    
    public BaseServerException( String message , List<String> listOfParam ) {
        new BaseServerException(message, listOfParam, null);
    }
    
    public BaseServerException( String message , String param1 , String param2 , String param3 , Throwable cause ) {
        super(message, cause);
        if (param1 != null)
            this.parameterList.add(param1);
        if (param2 != null)
            this.parameterList.add(param2);
        if (param3 != null)
            this.parameterList.add(param3);
    }
    
    public BaseServerException( String message , List<String> listOfParam , Throwable cause ) {
        super(message, cause);
        this.parameterList = listOfParam;
    }
    
    public List<String> getParameterList( ) {
        return new ArrayList<String>(this.parameterList);
    }
    
    public int getNoOfParameter( ) {
        return this.parameterList.size();
    }
    
    public String getParam1( ) {
        return getParam(1);
    }
    
    public String getParam2( ) {
        return getParam(2);
    }
    
    public String getParam3( ) {
        return getParam(3);
    }
    
    public String getParam( int i ) {
        return parameterList.size() >= i ? parameterList.get(i) : "";
    }
    
    public boolean isParam1Exists( ) {
        return isParamExists(1);
    }
    
    public boolean isParam2Exists( ) {
        return isParamExists(2);
    }
    
    public boolean isParam3Exists( int i ) {
        return isParamExists(3);
    }
    
    public boolean isParamExists( int i ) {
        return parameterList.size() >= i ? true : false;
    }
    
    public String getprintStackTrace() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println();
        out.println("---------------Start " + getClass().getSimpleName() + "-----------------------");
        out.println("msg:- " + getMessage());
        if (getCause() != null)
            out.println("cause msg:- " + getCause().getMessage());
        out.println("no of parameter :- " + parameterList.size());
        Iterator<String> itParamList = parameterList.iterator();
        int i = 1;
        while (itParamList.hasNext())
            out.println("param" + (i++) + " :" + parameterList.size());
        
        out.println("Stack :- ");
        
        printStackTrace(out);
        out.println();
        out.println("Root Stack :- ");
        if(getCause()!=null)
            getCause().printStackTrace(out);
        out.println();
        out.println("---------------End " + getClass().getSimpleName() + "-----------------------");
        out.println("");
        out.close();
        return writer.toString();
    }
   
   
}
