/**
 * Copyright (C) Elitecore Technologies Ltd.
 * SSSSPortalException.java
 * Created on Jul 9, 2007
 * Last Modified on 
 * @author : kaushikvira
 */
package com.elitecore.elitecodegen.exception;

import org.apache.log4j.Logger;

/**
 * @author kaushikvira
 */
public class EliteCodeGenException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3708313510781664148L;
    
    private static Logger     log;
    
    private Exception         exceptionCause;
    
    public EliteCodeGenException( Exception ex ) {
        EliteCodeGenException.log = Logger.getLogger(EliteCodeGenException.class);
        this.exceptionCause = ex;
    }
    
    public EliteCodeGenException( String errormsg ) {
        EliteCodeGenException.log = Logger.getLogger(EliteCodeGenException.class);
        this.exceptionCause = new Exception(errormsg);
    }
    
    public Exception getExceptionCause() {
        return this.exceptionCause;
    }
    
    public void setExceptionCause( Exception exceptionCause ) {
        this.exceptionCause = exceptionCause;
    }
    
}
