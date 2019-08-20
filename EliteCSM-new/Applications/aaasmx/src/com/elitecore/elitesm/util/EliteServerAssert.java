/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   EliteServerAssert.java                            
 * ModualName com.elitecore.elitesm.util                                      
 * Created on Apr 11, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util;

import com.elitecore.elitesm.datamanager.core.exceptions.server.ServerConfigurationNotFoundException;

public class EliteServerAssert {
     
    public static void notNull( Object object , String message ) throws ServerConfigurationNotFoundException {
        if (object == null) {
            throw new ServerConfigurationNotFoundException("[Assertion failed] -" + message +" Internal System Error.");
        }
    }

    public static void notNull( Object object ) throws ServerConfigurationNotFoundException {
        notNull(object, "");
    }

}
