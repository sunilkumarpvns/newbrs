/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   BaseConnectionManager_impl.java                             
 * ModualName                                     
 * Created on Nov 27, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.util.communicationmanager.remotecommunication.impl;

import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.FormatInvalidException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;

/**
 * @author kaushikvira
 */
public class BaseConnectionManager_impl {

    protected static final String MODULE = "BASE_REMOTECONNECTION_IMPL";

    final protected void validateRemoteIp( String ipAddress ) throws DataValidationException {
        if (EliteGenericValidator.isBlankOrNull(ipAddress) && EliteGenericValidator.isIPV4Address(ipAddress)) { throw new InvalidValueException("Invalid Ip Address"); }
    }

    final protected void validateRemotePort( String port ) throws DataValidationException {
        if (!EliteGenericValidator.isLong(port)) { throw new FormatInvalidException("Invalid port Formate;"); }
        long port_long = ((Long) Long.parseLong(port)).longValue();
        if (!EliteGenericValidator.isInRange(port_long, 0L, 65536L)) { throw new InvalidValueException("Invalid Port Address"); }
    }

}
