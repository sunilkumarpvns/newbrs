/**
 *	Copyright (C) Elitecore Technologies Ltd.
 *  FileName RadiusConstant.java
 *  ModualName sm
 *  Created on Sep 15, 2007
 *  Last Modified 
 *  @author ukaushikvira
 */
package com.elitecore.elitesm.util.constants;

import java.util.HashMap;
import java.util.Map;


/**
 * @author kaushikvira
 *
 */
public class RadiusConstant extends BaseConstant {

/* LDAP Constants */
    
    public static final String LDAPSCHEMA_REQURIED_ATTRIBUTE = "Y";
    public static final String LDAPSCHEMA_NOTREQURIED_ATTRIBUTE = "N";
    public static final String LDAPSCHEMA_OBJECTCLASS_ATTRIBUTE = "O";
    public static final String LDAPSCHEMA_ALL_ATTRIBUTE = "ALL";
    public static final String ACCOUNTING_SERVICE = "SCR0022";
    public static final String AUTHENTICATION_SERVICE = "SCR0021";
    public static final int ACCOUNTING_REQUEST_START = 2;
    public static final int ACCOUNTING_REQUEST_STOP = 3;
    public static final int ACCOUNTING_REQUEST_UPDTAE = 4;
    public static Map LDAPERRORCODEMAP ;
    static{
        LDAPERRORCODEMAP = new HashMap();
        LDAPERRORCODEMAP.put("1","datasource.ldap.errorcode.1");   
        LDAPERRORCODEMAP.put("2","datasource.ldap.errorcode.2");   
        LDAPERRORCODEMAP.put("3","datasource.ldap.errorcode.3");   
        LDAPERRORCODEMAP.put("4","datasource.ldap.errorcode.4");   
        LDAPERRORCODEMAP.put("5","datasource.ldap.errorcode.5");   
        LDAPERRORCODEMAP.put("6","datasource.ldap.errorcode.6");   
        LDAPERRORCODEMAP.put("7","datasource.ldap.errorcode.7");   
        LDAPERRORCODEMAP.put("8","datasource.ldap.errorcode.8");   
        LDAPERRORCODEMAP.put("9","datasource.ldap.errorcode.9");   
        LDAPERRORCODEMAP.put("10","datasource.ldap.errorcode.10"); 
        LDAPERRORCODEMAP.put("11","datasource.ldap.errorcode.11"); 
        LDAPERRORCODEMAP.put("12" ,"datasource.ldap.errorcode.12");
        LDAPERRORCODEMAP.put("13","datasource.ldap.errorcode.13"); 
        LDAPERRORCODEMAP.put("14","datasource.ldap.errorcode.14"); 
        LDAPERRORCODEMAP.put("16","datasource.ldap.errorcode.16");  
        LDAPERRORCODEMAP.put("17","datasource.ldap.errorcode.17"); 
        LDAPERRORCODEMAP.put("18","datasource.ldap.errorcode.18"); 
        LDAPERRORCODEMAP.put("19","datasource.ldap.errorcode.19"); 
        LDAPERRORCODEMAP.put("20","datasource.ldap.errorcode.20"); 
        LDAPERRORCODEMAP.put("21","datasource.ldap.errorcode.21"); 
        LDAPERRORCODEMAP.put("32","datasource.ldap.errorcode.32"); 
        LDAPERRORCODEMAP.put("33","datasource.ldap.errorcode.33"); 
        LDAPERRORCODEMAP.put("34","datasource.ldap.errorcode.34"); 
        LDAPERRORCODEMAP.put("35","datasource.ldap.errorcode.35"); 
        LDAPERRORCODEMAP.put("36","datasource.ldap.errorcode.36"); 
        LDAPERRORCODEMAP.put("48","datasource.ldap.errorcode.48"); 
        LDAPERRORCODEMAP.put("49","datasource.ldap.errorcode.49"); 
        LDAPERRORCODEMAP.put("50","datasource.ldap.errorcode.50"); 
        LDAPERRORCODEMAP.put("51","datasource.ldap.errorcode.51"); 
        LDAPERRORCODEMAP.put("52","datasource.ldap.errorcode.52"); 
        LDAPERRORCODEMAP.put("53","datasource.ldap.errorcode.53"); 
        LDAPERRORCODEMAP.put("54","datasource.ldap.errorcode.54"); 
        LDAPERRORCODEMAP.put("64","datasource.ldap.errorcode.64"); 
        LDAPERRORCODEMAP.put("65","datasource.ldap.errorcode.65"); 
        LDAPERRORCODEMAP.put("66","datasource.ldap.errorcode.66"); 
        LDAPERRORCODEMAP.put("67","datasource.ldap.errorcode.67"); 
        LDAPERRORCODEMAP.put("68","datasource.ldap.errorcode.68"); 
        LDAPERRORCODEMAP.put("69","datasource.ldap.errorcode.69"); 
        LDAPERRORCODEMAP.put("71","datasource.ldap.errorcode.71"); 
        LDAPERRORCODEMAP.put("80","datasource.ldap.errorcode.80"); 
        LDAPERRORCODEMAP.put("81","datasource.ldap.errorcode.81"); 
        LDAPERRORCODEMAP.put("85","datasource.ldap.errorcode.85"); 
        LDAPERRORCODEMAP.put("89","datasource.ldap.errorcode.89"); 
        LDAPERRORCODEMAP.put("91","datasource.ldap.errorcode.91"); 
        LDAPERRORCODEMAP.put("92","datasource.ldap.errorcode.92"); 
        LDAPERRORCODEMAP.put("93","datasource.ldap.errorcode.93"); 
        LDAPERRORCODEMAP.put("94","datasource.ldap.errorcode.94"); 
        LDAPERRORCODEMAP.put("95","datasource.ldap.errorcode.95"); 
        LDAPERRORCODEMAP.put("96","datasource.ldap.errorcode.96"); 
        LDAPERRORCODEMAP.put("97","datasource.ldap.errorcode.97"); 
  }
    
    public static String getErrorKey(String errorCode)
    {
        String errorKey = (String) LDAPERRORCODEMAP.get(errorCode);
        
        if(errorKey ==null)
       return "datasource.ldap.errorcode.80";
       else
        return errorKey;
        
    }
    
    
    
}
