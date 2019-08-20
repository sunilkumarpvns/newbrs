/**
 * Copyright (C) Elitecore Technologies Ltd.
 * FileName EliteAssert.java
 * ModualName com.elitecore.elitesm.util.exception
 * Created on Feb 23, 2008
 * Last Modified on
 * 
 * @author : kaushikvira
 */

package com.elitecore.elitesm.util;

import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidArrguementsException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidHttpSessionException;

public abstract class EliteAssert {
    
    public static void isTrue( boolean expression , String message ) throws InvalidArrguementsException {
        if (!expression) {
            throw new InvalidArrguementsException("[Assertion failed] - " + message + " This expression must be true.");
        }
    }
    
    public static void isTrue( boolean expression ) throws InvalidArrguementsException {
        isTrue(expression, "");
    }
    
    public static void isNull( Object object , String message ) throws InvalidArrguementsException {
        if (object != null) {
            throw new InvalidArrguementsException("[Assertion failed] -" + message + " The object argument must be null.");
        }
    }
    
    public static void isNull( Object object ) throws InvalidArrguementsException {
        isNull(object, "");
    }
    
    public static void notNull( Object object , String message ) throws InvalidArrguementsException {
        if (object == null) {
            throw new InvalidArrguementsException("[Assertion failed] -" + message +" This argument is required; it must not be null.");
        }
    }
    
    public static void notNull( Object object ) throws InvalidArrguementsException {
        notNull(object, "");
    }
    
    public static void hasLength( String text , String message ) throws InvalidArrguementsException {
        if (!(text != null && text.length() > 0)) {
            throw new InvalidArrguementsException("[Assertion failed] - " + message + " This String argument must have length; it must not be null or empty");
        }
    }
    
    public static void hasLength( String text ) throws InvalidArrguementsException {
        hasLength(text, "");
    }
    
    public static void isInstanceOf( Class clazz , Object obj ) throws InvalidArrguementsException {
        isInstanceOf(clazz, obj, "");
    }
    
    public static void isInstanceOf( Class type , Object obj , String message ) throws InvalidArrguementsException {
        notNull(type,"[Assertion failed] -"+ message + " Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new InvalidArrguementsException("[Assertion failed] -"+ message + " Object of class [" + (obj != null ? obj.getClass().getName() : "null") + "] must be an instance of " + type);
        }
    }
    
    public static void notEmpty( Object[] array , String message ) throws InvalidArrguementsException {
        if (isEmpty(array)) {
            throw new InvalidArrguementsException("[Assertion failed] - "+  message + " This array must not be empty: it must contain at least 1 element");
        }
    }
    
    public static void notEmpty( Object[] array ) throws InvalidArrguementsException {
        notEmpty(array,"");
    }
    
    public static void noNullElements( Object[] array , String message ) throws InvalidArrguementsException {
        if (array != null) {
            for ( int i = 0; i < array.length; i++ ) {
                if (array[i] == null) {
                    throw new InvalidArrguementsException("[Assertion failed] - "+message+" This array must not contain any null elements.");
                }
            }
        }
    }
    
    public static void noNullElements( Object[] array )throws InvalidArrguementsException {
        noNullElements(array, "");
    }
    
    public static void notEmpty( Collection collection , String message ) throws InvalidArrguementsException{
        if (isEmpty(collection)) {
            throw new InvalidArrguementsException("[Assertion failed] - "+ message +" This collection must not be empty: it must contain at least 1 element");
        }
    }
    
    public static void notEmpty( Collection collection ) throws InvalidArrguementsException {
        notEmpty(collection,"");
    }
    
    public static void notEmpty( Map map , String message ) throws InvalidArrguementsException {
        if (isEmpty(map)) {
            throw new InvalidArrguementsException("[Assertion failed] - "+ message +" This map must not be empty; it must contain at least one entry");
        }
    }
    
    public static void notEmpty( Map map ) throws InvalidArrguementsException{
        notEmpty(map,"");
    }
    
    public static void valiedWebSession(HttpServletRequest request) throws InvalidHttpSessionException{
        if (request == null)
            throw new InvalidHttpSessionException("[Assertion failed] - Invalid Session Found,Reason:- HttpServletRequest Object is null");

        if (request.getSession(false) == null)
            throw new InvalidHttpSessionException("[Assertion failed] - Invalid Session Found,Reason:- HttpSession Object is null");
    }
    
    /* Following Methods are Must be private.*/     
    private static boolean isEmpty( Collection collection ) {
        return (collection == null || collection.isEmpty());
    }
    
    private static boolean isEmpty( Map map ) {
        return (map == null || map.isEmpty());
    }
    
    private static boolean isEmpty( Object[] array ) {
        return (array == null || array.length == 0);
    }
    public static void greaterThanZero(String longValue) throws InvalidArrguementsException{
    	if(Strings.isNullOrBlank(longValue)){
        	throw new InvalidArrguementsException("[Assertion failed] - "+" The argument must be greater than zero.");
        }
    }
    public static void greaterThanZero(String longValue,String message) throws InvalidArrguementsException{
        if(Strings.isNullOrBlank(longValue)){
        	throw new InvalidArrguementsException("[Assertion failed] - "+message+" The argument must be greater than zero.");
        }
    }
}
