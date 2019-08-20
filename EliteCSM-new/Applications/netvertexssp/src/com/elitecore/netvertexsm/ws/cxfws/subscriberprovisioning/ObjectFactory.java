
package com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning package. 
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

    private final static QName _WsAddSubscriberBulk_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsAddSubscriberBulk");
    private final static QName _WsAddSubscriberProfile_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsAddSubscriberProfile");
    private final static QName _WsUpdateSubscriberProfile_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsUpdateSubscriberProfile");
    private final static QName _WsListSubscriberProfiles_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsListSubscriberProfiles");
    private final static QName _WsUpdateSubscriberProfileResponse_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsUpdateSubscriberProfileResponse");
    private final static QName _WsGetSubscriberProfileByIDResponse_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsGetSubscriberProfileByIDResponse");
    private final static QName _WsAddSubscriberBulkResponse_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsAddSubscriberBulkResponse");
    private final static QName _WsDeleteSubscriberProfileResponse_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsDeleteSubscriberProfileResponse");
    private final static QName _WsGetSubscriberProfileByID_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsGetSubscriberProfileByID");
    private final static QName _WsListSubscriberProfilesResponse_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsListSubscriberProfilesResponse");
    private final static QName _WsAddSubscriberProfileResponse_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsAddSubscriberProfileResponse");
    private final static QName _WsDeleteSubscriberProfile_QNAME = new QName("http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", "wsDeleteSubscriberProfile");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WsAddSubscriberProfile }
     * 
     */
    public WsAddSubscriberProfile createWsAddSubscriberProfile() {
        return new WsAddSubscriberProfile();
    }

    /**
     * Create an instance of {@link WsAddSubscriberProfile.SubscriberProfile }
     * 
     */
    public WsAddSubscriberProfile.SubscriberProfile createWsAddSubscriberProfileSubscriberProfile() {
        return new WsAddSubscriberProfile.SubscriberProfile();
    }

    /**
     * Create an instance of {@link WsUpdateSubscriberProfile }
     * 
     */
    public WsUpdateSubscriberProfile createWsUpdateSubscriberProfile() {
        return new WsUpdateSubscriberProfile();
    }

    /**
     * Create an instance of {@link WsUpdateSubscriberProfile.SubscriberProfile }
     * 
     */
    public WsUpdateSubscriberProfile.SubscriberProfile createWsUpdateSubscriberProfileSubscriberProfile() {
        return new WsUpdateSubscriberProfile.SubscriberProfile();
    }

    /**
     * Create an instance of {@link WsListSubscriberProfiles }
     * 
     */
    public WsListSubscriberProfiles createWsListSubscriberProfiles() {
        return new WsListSubscriberProfiles();
    }

    /**
     * Create an instance of {@link WsListSubscriberProfiles.SubscriberProfileCriteria }
     * 
     */
    public WsListSubscriberProfiles.SubscriberProfileCriteria createWsListSubscriberProfilesSubscriberProfileCriteria() {
        return new WsListSubscriberProfiles.SubscriberProfileCriteria();
    }

    /**
     * Create an instance of {@link WsUpdateSubscriberProfileResponse }
     * 
     */
    public WsUpdateSubscriberProfileResponse createWsUpdateSubscriberProfileResponse() {
        return new WsUpdateSubscriberProfileResponse();
    }

    /**
     * Create an instance of {@link WsGetSubscriberProfileByIDResponse }
     * 
     */
    public WsGetSubscriberProfileByIDResponse createWsGetSubscriberProfileByIDResponse() {
        return new WsGetSubscriberProfileByIDResponse();
    }

    /**
     * Create an instance of {@link WsAddSubscriberBulk }
     * 
     */
    public WsAddSubscriberBulk createWsAddSubscriberBulk() {
        return new WsAddSubscriberBulk();
    }

    /**
     * Create an instance of {@link WsAddSubscriberBulkResponse }
     * 
     */
    public WsAddSubscriberBulkResponse createWsAddSubscriberBulkResponse() {
        return new WsAddSubscriberBulkResponse();
    }

    /**
     * Create an instance of {@link WsGetSubscriberProfileByID }
     * 
     */
    public WsGetSubscriberProfileByID createWsGetSubscriberProfileByID() {
        return new WsGetSubscriberProfileByID();
    }

    /**
     * Create an instance of {@link WsDeleteSubscriberProfileResponse }
     * 
     */
    public WsDeleteSubscriberProfileResponse createWsDeleteSubscriberProfileResponse() {
        return new WsDeleteSubscriberProfileResponse();
    }

    /**
     * Create an instance of {@link WsListSubscriberProfilesResponse }
     * 
     */
    public WsListSubscriberProfilesResponse createWsListSubscriberProfilesResponse() {
        return new WsListSubscriberProfilesResponse();
    }

    /**
     * Create an instance of {@link WsDeleteSubscriberProfile }
     * 
     */
    public WsDeleteSubscriberProfile createWsDeleteSubscriberProfile() {
        return new WsDeleteSubscriberProfile();
    }

    /**
     * Create an instance of {@link WsAddSubscriberProfileResponse }
     * 
     */
    public WsAddSubscriberProfileResponse createWsAddSubscriberProfileResponse() {
        return new WsAddSubscriberProfileResponse();
    }

    /**
     * Create an instance of {@link com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile }
     * 
     */
    public com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile createSubscriberProfile() {
        return new com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.SubscriberProfile();
    }

    /**
     * Create an instance of {@link SubscriberProvisioningResponse }
     * 
     */
    public SubscriberProvisioningResponse createSubscriberProvisioningResponse() {
        return new SubscriberProvisioningResponse();
    }

    /**
     * Create an instance of {@link com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.Entry }
     * 
     */
    public com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.Entry createEntry() {
        return new com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning.Entry();
    }

    /**
     * Create an instance of {@link WsAddSubscriberProfile.SubscriberProfile.Entry }
     * 
     */
    public WsAddSubscriberProfile.SubscriberProfile.Entry createWsAddSubscriberProfileSubscriberProfileEntry() {
        return new WsAddSubscriberProfile.SubscriberProfile.Entry();
    }

    /**
     * Create an instance of {@link WsUpdateSubscriberProfile.SubscriberProfile.Entry }
     * 
     */
    public WsUpdateSubscriberProfile.SubscriberProfile.Entry createWsUpdateSubscriberProfileSubscriberProfileEntry() {
        return new WsUpdateSubscriberProfile.SubscriberProfile.Entry();
    }

    /**
     * Create an instance of {@link WsListSubscriberProfiles.SubscriberProfileCriteria.Entry }
     * 
     */
    public WsListSubscriberProfiles.SubscriberProfileCriteria.Entry createWsListSubscriberProfilesSubscriberProfileCriteriaEntry() {
        return new WsListSubscriberProfiles.SubscriberProfileCriteria.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsAddSubscriberBulk }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsAddSubscriberBulk")
    public JAXBElement<WsAddSubscriberBulk> createWsAddSubscriberBulk(WsAddSubscriberBulk value) {
        return new JAXBElement<WsAddSubscriberBulk>(_WsAddSubscriberBulk_QNAME, WsAddSubscriberBulk.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsAddSubscriberProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsAddSubscriberProfile")
    public JAXBElement<WsAddSubscriberProfile> createWsAddSubscriberProfile(WsAddSubscriberProfile value) {
        return new JAXBElement<WsAddSubscriberProfile>(_WsAddSubscriberProfile_QNAME, WsAddSubscriberProfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsUpdateSubscriberProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsUpdateSubscriberProfile")
    public JAXBElement<WsUpdateSubscriberProfile> createWsUpdateSubscriberProfile(WsUpdateSubscriberProfile value) {
        return new JAXBElement<WsUpdateSubscriberProfile>(_WsUpdateSubscriberProfile_QNAME, WsUpdateSubscriberProfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsListSubscriberProfiles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsListSubscriberProfiles")
    public JAXBElement<WsListSubscriberProfiles> createWsListSubscriberProfiles(WsListSubscriberProfiles value) {
        return new JAXBElement<WsListSubscriberProfiles>(_WsListSubscriberProfiles_QNAME, WsListSubscriberProfiles.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsUpdateSubscriberProfileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsUpdateSubscriberProfileResponse")
    public JAXBElement<WsUpdateSubscriberProfileResponse> createWsUpdateSubscriberProfileResponse(WsUpdateSubscriberProfileResponse value) {
        return new JAXBElement<WsUpdateSubscriberProfileResponse>(_WsUpdateSubscriberProfileResponse_QNAME, WsUpdateSubscriberProfileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsGetSubscriberProfileByIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsGetSubscriberProfileByIDResponse")
    public JAXBElement<WsGetSubscriberProfileByIDResponse> createWsGetSubscriberProfileByIDResponse(WsGetSubscriberProfileByIDResponse value) {
        return new JAXBElement<WsGetSubscriberProfileByIDResponse>(_WsGetSubscriberProfileByIDResponse_QNAME, WsGetSubscriberProfileByIDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsAddSubscriberBulkResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsAddSubscriberBulkResponse")
    public JAXBElement<WsAddSubscriberBulkResponse> createWsAddSubscriberBulkResponse(WsAddSubscriberBulkResponse value) {
        return new JAXBElement<WsAddSubscriberBulkResponse>(_WsAddSubscriberBulkResponse_QNAME, WsAddSubscriberBulkResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsDeleteSubscriberProfileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsDeleteSubscriberProfileResponse")
    public JAXBElement<WsDeleteSubscriberProfileResponse> createWsDeleteSubscriberProfileResponse(WsDeleteSubscriberProfileResponse value) {
        return new JAXBElement<WsDeleteSubscriberProfileResponse>(_WsDeleteSubscriberProfileResponse_QNAME, WsDeleteSubscriberProfileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsGetSubscriberProfileByID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsGetSubscriberProfileByID")
    public JAXBElement<WsGetSubscriberProfileByID> createWsGetSubscriberProfileByID(WsGetSubscriberProfileByID value) {
        return new JAXBElement<WsGetSubscriberProfileByID>(_WsGetSubscriberProfileByID_QNAME, WsGetSubscriberProfileByID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsListSubscriberProfilesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsListSubscriberProfilesResponse")
    public JAXBElement<WsListSubscriberProfilesResponse> createWsListSubscriberProfilesResponse(WsListSubscriberProfilesResponse value) {
        return new JAXBElement<WsListSubscriberProfilesResponse>(_WsListSubscriberProfilesResponse_QNAME, WsListSubscriberProfilesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsAddSubscriberProfileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsAddSubscriberProfileResponse")
    public JAXBElement<WsAddSubscriberProfileResponse> createWsAddSubscriberProfileResponse(WsAddSubscriberProfileResponse value) {
        return new JAXBElement<WsAddSubscriberProfileResponse>(_WsAddSubscriberProfileResponse_QNAME, WsAddSubscriberProfileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WsDeleteSubscriberProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/", name = "wsDeleteSubscriberProfile")
    public JAXBElement<WsDeleteSubscriberProfile> createWsDeleteSubscriberProfile(WsDeleteSubscriberProfile value) {
        return new JAXBElement<WsDeleteSubscriberProfile>(_WsDeleteSubscriberProfile_QNAME, WsDeleteSubscriberProfile.class, null, value);
    }

}
