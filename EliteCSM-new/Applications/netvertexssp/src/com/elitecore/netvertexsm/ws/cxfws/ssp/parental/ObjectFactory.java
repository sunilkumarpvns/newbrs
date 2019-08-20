
package com.elitecore.netvertexsm.ws.cxfws.ssp.parental;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.elitecore.netvertexsm.ws.cxfws.ssp.parental package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _WsListParentalPoliciesResponse_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsListParentalPoliciesResponse");
    private final static QName _WsGetPolicyGroupResponse_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsGetPolicyGroupResponse");
    private final static QName _WsApplyAccessControlPolicies_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsApplyAccessControlPolicies");
    private final static QName _WsListChildProfiles_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsListChildProfiles");
    private final static QName _WsListParentalPolicies_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsListParentalPolicies");
    private final static QName _WsListChildProfilesResponse_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsListChildProfilesResponse");
    private final static QName _WsApplyAccessControlPoliciesResponse_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsApplyAccessControlPoliciesResponse");
    private final static QName _WsAuthenticate_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsAuthenticate");
    private final static QName _WsGetPolicyGroup_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsGetPolicyGroup");
    private final static QName _WsAuthenticateResponse_QNAME = new QName("http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", "wsAuthenticateResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.elitecore.netvertexsm.ws.cxfws.ssp.parental
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WsListChildProfiles }
     * 
     */
    public WsListChildProfiles createWsListChildProfiles() {
        return new WsListChildProfiles();
    }

    /**
     * Create an instance of {@link WsListChildProfiles.ChildProfileCriteria }
     * 
     */
    public WsListChildProfiles.ChildProfileCriteria createWsListChildProfilesChildProfileCriteria() {
        return new WsListChildProfiles.ChildProfileCriteria();
    }

    /**
     * Create an instance of {@link WsApplyAccessControlPolicies }
     * 
     */
    public WsApplyAccessControlPolicies createWsApplyAccessControlPolicies() {
        return new WsApplyAccessControlPolicies();
    }

    /**
     * Create an instance of {@link WsListParentalPolicies }
     * 
     */
    public WsListParentalPolicies createWsListParentalPolicies() {
        return new WsListParentalPolicies();
    }

    /**
     * Create an instance of {@link WsGetPolicyGroupResponse }
     * 
     */
    public WsGetPolicyGroupResponse createWsGetPolicyGroupResponse() {
        return new WsGetPolicyGroupResponse();
    }

    /**
     * Create an instance of {@link WsListParentalPoliciesResponse }
     * 
     */
    public WsListParentalPoliciesResponse createWsListParentalPoliciesResponse() {
        return new WsListParentalPoliciesResponse();
    }

    /**
     * Create an instance of {@link WsAuthenticateResponse }
     * 
     */
    public WsAuthenticateResponse createWsAuthenticateResponse() {
        return new WsAuthenticateResponse();
    }

    /**
     * Create an instance of {@link WsAuthenticate }
     * 
     */
    public WsAuthenticate createWsAuthenticate() {
        return new WsAuthenticate();
    }

    /**
     * Create an instance of {@link WsGetPolicyGroup }
     * 
     */
    public WsGetPolicyGroup createWsGetPolicyGroup() {
        return new WsGetPolicyGroup();
    }

    /**
     * Create an instance of {@link WsListChildProfilesResponse }
     * 
     */
    public WsListChildProfilesResponse createWsListChildProfilesResponse() {
        return new WsListChildProfilesResponse();
    }

    /**
     * Create an instance of {@link WsApplyAccessControlPoliciesResponse }
     * 
     */
    public WsApplyAccessControlPoliciesResponse createWsApplyAccessControlPoliciesResponse() {
        return new WsApplyAccessControlPoliciesResponse();
    }

    /**
     * Create an instance of {@link PolicyGroup }
     * 
     */
    public PolicyGroup createPolicyGroup() {
        return new PolicyGroup();
    }

    /**
     * Create an instance of {@link SubscriberProfile }
     * 
     */
    public SubscriberProfile createSubscriberProfile() {
        return new SubscriberProfile();
    }

    /**
     * Create an instance of {@link ParentalPolicy }
     * 
     */
    public ParentalPolicy createParentalPolicy() {
        return new ParentalPolicy();
    }

    /**
     * Create an instance of {@link ParentalPolicyResponse }
     * 
     */
    public ParentalPolicyResponse createParentalPolicyResponse() {
        return new ParentalPolicyResponse();
    }

    /**
     * Create an instance of {@link ChildProfileResponse }
     * 
     */
    public ChildProfileResponse createChildProfileResponse() {
        return new ChildProfileResponse();
    }

    /**
     * Create an instance of {@link PolicyGroupQueryResponse }
     * 
     */
    public PolicyGroupQueryResponse createPolicyGroupQueryResponse() {
        return new PolicyGroupQueryResponse();
    }

    /**
     * Create an instance of {@link AuthenticationResponse }
     * 
     */
    public AuthenticationResponse createAuthenticationResponse() {
        return new AuthenticationResponse();
    }

    /**
     * Create an instance of {@link WsListChildProfiles.ChildProfileCriteria.Entry }
     * 
     */
    public WsListChildProfiles.ChildProfileCriteria.Entry createWsListChildProfilesChildProfileCriteriaEntry() {
        return new WsListChildProfiles.ChildProfileCriteria.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsListParentalPoliciesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsListParentalPoliciesResponse")
    public JAXBElement<WsListParentalPoliciesResponse> createWsListParentalPoliciesResponse(WsListParentalPoliciesResponse value) {
        return new JAXBElement<WsListParentalPoliciesResponse>(_WsListParentalPoliciesResponse_QNAME, WsListParentalPoliciesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsGetPolicyGroupResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsGetPolicyGroupResponse")
    public JAXBElement<WsGetPolicyGroupResponse> createWsGetPolicyGroupResponse(WsGetPolicyGroupResponse value) {
        return new JAXBElement<WsGetPolicyGroupResponse>(_WsGetPolicyGroupResponse_QNAME, WsGetPolicyGroupResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsApplyAccessControlPolicies }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsApplyAccessControlPolicies")
    public JAXBElement<WsApplyAccessControlPolicies> createWsApplyAccessControlPolicies(WsApplyAccessControlPolicies value) {
        return new JAXBElement<WsApplyAccessControlPolicies>(_WsApplyAccessControlPolicies_QNAME, WsApplyAccessControlPolicies.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsListChildProfiles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsListChildProfiles")
    public JAXBElement<WsListChildProfiles> createWsListChildProfiles(WsListChildProfiles value) {
        return new JAXBElement<WsListChildProfiles>(_WsListChildProfiles_QNAME, WsListChildProfiles.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsListParentalPolicies }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsListParentalPolicies")
    public JAXBElement<WsListParentalPolicies> createWsListParentalPolicies(WsListParentalPolicies value) {
        return new JAXBElement<WsListParentalPolicies>(_WsListParentalPolicies_QNAME, WsListParentalPolicies.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsListChildProfilesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsListChildProfilesResponse")
    public JAXBElement<WsListChildProfilesResponse> createWsListChildProfilesResponse(WsListChildProfilesResponse value) {
        return new JAXBElement<WsListChildProfilesResponse>(_WsListChildProfilesResponse_QNAME, WsListChildProfilesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsApplyAccessControlPoliciesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsApplyAccessControlPoliciesResponse")
    public JAXBElement<WsApplyAccessControlPoliciesResponse> createWsApplyAccessControlPoliciesResponse(WsApplyAccessControlPoliciesResponse value) {
        return new JAXBElement<WsApplyAccessControlPoliciesResponse>(_WsApplyAccessControlPoliciesResponse_QNAME, WsApplyAccessControlPoliciesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsAuthenticate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsAuthenticate")
    public JAXBElement<WsAuthenticate> createWsAuthenticate(WsAuthenticate value) {
        return new JAXBElement<WsAuthenticate>(_WsAuthenticate_QNAME, WsAuthenticate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsGetPolicyGroup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsGetPolicyGroup")
    public JAXBElement<WsGetPolicyGroup> createWsGetPolicyGroup(WsGetPolicyGroup value) {
        return new JAXBElement<WsGetPolicyGroup>(_WsGetPolicyGroup_QNAME, WsGetPolicyGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsAuthenticateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/", name = "wsAuthenticateResponse")
    public JAXBElement<WsAuthenticateResponse> createWsAuthenticateResponse(WsAuthenticateResponse value) {
        return new JAXBElement<WsAuthenticateResponse>(_WsAuthenticateResponse_QNAME, WsAuthenticateResponse.class, null, value);
    }

}
