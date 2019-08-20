/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   XMLParserNotSupportedException.java                             
 * ModualName                                     
 * Created on Oct 12, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.netvertexsm.datamanager.core.exceptions.environmentsupport;


/**
 * @author kaushikvira
 *
 */
public class XMLParserNotSupportedException extends EnvironmentNotSupportedException {
    
    /**
     * 
     */
    public XMLParserNotSupportedException() {
    }
    
    /**
     * @param message
     */
    public XMLParserNotSupportedException( String message ) {
        super(message);
    }
    
    /**
     * @param message
     * @param sourceField
     */
    public XMLParserNotSupportedException( String message ,
                                           String sourceField ) {
        super(message, sourceField);
    }
    
    /**
     * @param message
     * @param cause
     */
    public XMLParserNotSupportedException( String message ,
                                           Throwable cause ) {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public XMLParserNotSupportedException( Throwable cause ) {
        super(cause);
    }
    
    /**
     * @param message
     * @param sourceField
     * @param cause
     */
    public XMLParserNotSupportedException( String message ,
                                           String sourceField ,
                                           Throwable cause ) {
        super(message, sourceField, cause);
    }
    
}
