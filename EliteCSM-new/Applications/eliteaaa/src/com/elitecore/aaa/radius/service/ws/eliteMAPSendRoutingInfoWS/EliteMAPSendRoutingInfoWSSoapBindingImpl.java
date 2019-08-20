/**
 * EliteMAPSendRoutingInfoWSSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;

public class EliteMAPSendRoutingInfoWSSoapBindingImpl implements com.elitecore.aaa.radius.service.ws.eliteMAPSendRoutingInfoWS.EliteMAPSendRoutingInfoWS{
    public java.lang.String wsMAPSendRoutingInfo(java.lang.String msisdn) throws java.rmi.RemoteException {
    	return EliteAAAServiceExposerManager.getInstance().requestIMSIForMSISDN(msisdn);
    }

}
