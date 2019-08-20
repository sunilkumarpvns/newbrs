/**
 * File :-ConnectionFactory.java 
 * Project :- Assignment2
 * package :- com.elitecore.Assignment2
 * Description :- Creating connection class for Project.
 * Auther :- kaushikvira
 * Time of Creation:- 11:01:35 AM 2007
 */

package com.elitecore.elitecodegen.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.log4j.Logger;

import com.elitecore.elitecodegen.controller.form.DbResourceForm;
import com.elitecore.elitecodegen.exception.EliteCodeGenException;

public class ConnectionFactory {
    
    private static Logger log = Logger.getLogger(ConnectionFactory.class);
    
    /* To store file name which contain DB resouce properties */

    public static Connection Connect( DbResourceForm dbResource ) throws EliteCodeGenException {
        
        ConnectionFactory.log.info("ConnectionFactory.connect() called");
        ConnectionFactory.log.info("Connecting to...");
        ConnectionFactory.log.info("url :" + dbResource.getStrJdbcUrl());
        ConnectionFactory.log.info("driver :" + dbResource.getStrDriverName());
        ConnectionFactory.log.info("userName :" + dbResource.getStrUserName());
        ConnectionFactory.log.info("password :" + dbResource.getStrPassword());
        try {
            Class.forName(dbResource.getStrDriverName()).newInstance();
            ConnectionFactory.log.info("Driver instance is created...");
            
            /* Create a Connection object */
            Connection conn = DriverManager.getConnection(dbResource.getStrJdbcUrl(), dbResource.getStrUserName(), dbResource.getStrPassword());
            ConnectionFactory.log.info("Connected...");
            return conn;
        }
        catch (Exception e) {
            ConnectionFactory.log.info("Exception", e);
            EliteCodeGenException elietEx = new EliteCodeGenException(e);
            throw elietEx;
        }
    }
}
