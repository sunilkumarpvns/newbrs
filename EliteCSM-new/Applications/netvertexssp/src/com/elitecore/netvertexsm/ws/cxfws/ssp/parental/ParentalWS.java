package com.elitecore.netvertexsm.ws.cxfws.ssp.parental;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.1
 * 2014-01-09T22:40:24.599+05:30
 * Generated source version: 2.7.1
 * 
 */
@WebService(targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "ParentalWS")
@XmlSeeAlso({ObjectFactory.class})
public interface ParentalWS {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsApplyAccessControlPolicies", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsApplyAccessControlPolicies")
    @WebMethod
    @ResponseWrapper(localName = "wsApplyAccessControlPoliciesResponse", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsApplyAccessControlPoliciesResponse")
    public com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicyResponse wsApplyAccessControlPolicies(
        @WebParam(name = "subscriberID", targetNamespace = "")
        java.lang.String subscriberID,
        @WebParam(name = "parentalPolicy", targetNamespace = "")
        java.util.List<com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicy> parentalPolicy,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsAuthenticate", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsAuthenticate")
    @WebMethod
    @ResponseWrapper(localName = "wsAuthenticateResponse", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsAuthenticateResponse")
    public com.elitecore.netvertexsm.ws.cxfws.ssp.parental.AuthenticationResponse wsAuthenticate(
        @WebParam(name = "userName", targetNamespace = "")
        java.lang.String userName,
        @WebParam(name = "password", targetNamespace = "")
        java.lang.String password,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsGetPolicyGroup", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsGetPolicyGroup")
    @WebMethod
    @ResponseWrapper(localName = "wsGetPolicyGroupResponse", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsGetPolicyGroupResponse")
    public com.elitecore.netvertexsm.ws.cxfws.ssp.parental.PolicyGroupQueryResponse wsGetPolicyGroup(
        @WebParam(name = "subscriberID", targetNamespace = "")
        java.lang.String subscriberID,
        @WebParam(name = "policyGroupName", targetNamespace = "")
        java.lang.String policyGroupName,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsListParentalPolicies", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsListParentalPolicies")
    @WebMethod
    @ResponseWrapper(localName = "wsListParentalPoliciesResponse", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsListParentalPoliciesResponse")
    public com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicyResponse wsListParentalPolicies(
        @WebParam(name = "subscriberID", targetNamespace = "")
        java.lang.String subscriberID,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "wsListChildProfiles", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsListChildProfiles")
    @WebMethod
    @ResponseWrapper(localName = "wsListChildProfilesResponse", targetNamespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", className = "com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsListChildProfilesResponse")
    public com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ChildProfileResponse wsListChildProfiles(
        @WebParam(name = "parentID", targetNamespace = "")
        java.lang.String parentID,
        @WebParam(name = "childProfileCriteria", targetNamespace = "")
        com.elitecore.netvertexsm.ws.cxfws.ssp.parental.WsListChildProfiles.ChildProfileCriteria childProfileCriteria,
        @WebParam(name = "parameter1", targetNamespace = "")
        java.lang.String parameter1,
        @WebParam(name = "parameter2", targetNamespace = "")
        java.lang.String parameter2
    );
}
