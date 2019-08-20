package com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.1
 * 2014-01-09T15:00:11.018+05:30
 * Generated source version: 2.7.1
 * 
 */
@WebService(targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "SubscriberProvisioningWS")
@XmlSeeAlso({ObjectFactory.class})
public interface SubscriberProvisioningWS {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsAddSubscriberBulk", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsAddSubscriberBulk")
    @WebMethod
    @ResponseWrapper(localName = "wsAddSubscriberBulkResponse", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsAddSubscriberBulkResponse")
    public com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProvisioningResponse wsAddSubscriberBulk(
        @WebParam(name = "subscriberProfile", targetNamespace = "")
        java.util.List<com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile> subscriberProfile,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsGetSubscriberProfileByID", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsGetSubscriberProfileByID")
    @WebMethod
    @ResponseWrapper(localName = "wsGetSubscriberProfileByIDResponse", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsGetSubscriberProfileByIDResponse")
    public com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProvisioningResponse wsGetSubscriberProfileByID(
        @WebParam(name = "subscriberID", targetNamespace = "")
        java.lang.String subscriberID,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsDeleteSubscriberProfile", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsDeleteSubscriberProfile")
    @WebMethod
    @ResponseWrapper(localName = "wsDeleteSubscriberProfileResponse", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsDeleteSubscriberProfileResponse")
    public com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProvisioningResponse wsDeleteSubscriberProfile(
        @WebParam(name = "subscriberID", targetNamespace = "")
        java.lang.String subscriberID,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsUpdateSubscriberProfile", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsUpdateSubscriberProfile")
    @WebMethod
    @ResponseWrapper(localName = "wsUpdateSubscriberProfileResponse", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsUpdateSubscriberProfileResponse")
    public com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProvisioningResponse wsUpdateSubscriberProfile(
        @WebParam(name = "subscriberProfile", targetNamespace = "")
        com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsUpdateSubscriberProfile.SubscriberProfile subscriberProfile,
        @WebParam(name = "subscriberID", targetNamespace = "")
        java.lang.String subscriberID,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsAddSubscriberProfile", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsAddSubscriberProfile")
    @WebMethod
    @ResponseWrapper(localName = "wsAddSubscriberProfileResponse", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsAddSubscriberProfileResponse")
    public com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProvisioningResponse wsAddSubscriberProfile(
        @WebParam(name = "subscriberProfile", targetNamespace = "")
        com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsAddSubscriberProfile.SubscriberProfile subscriberProfile,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsListSubscriberProfiles", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsListSubscriberProfiles")
    @WebMethod
    @ResponseWrapper(localName = "wsListSubscriberProfilesResponse", targetNamespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsListSubscriberProfilesResponse")
    public com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProvisioningResponse wsListSubscriberProfiles(
        @WebParam(name = "subscriberProfileCriteria", targetNamespace = "")
        com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.WsListSubscriberProfiles.SubscriberProfileCriteria subscriberProfileCriteria,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );
}
