package com.elitecore.corenetvertex.util;

import java.sql.DriverManager;

public class DerbyUtil {
    public static void closeDerby(String testingDB) {
        String connectionURL = "jdbc:derby:memory:"+ testingDB + ";drop=true";
        try {
            DriverManager.getConnection(connectionURL);
        }catch (Exception ex) {
            //during drop we expect exception
        }
    }
}
