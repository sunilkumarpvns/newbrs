
package com.elitecore.netvertexsm.ws.cxfws.subscriberprovisioning;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wsGetSubscriberProfileByIDResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsGetSubscriberProfileByIDResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://subscriberprovisioning.cxfws.ws.netvertexsm.elitecore.com/}subscriberProvisioningResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsGetSubscriberProfileByIDResponse", propOrder = {
    "_return"
})
public class WsGetSubscriberProfileByIDResponse {

    @XmlElement(name = "return")
    protected SubscriberProvisioningResponse _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link SubscriberProvisioningResponse }
     *     
     */
    public SubscriberProvisioningResponse getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriberProvisioningResponse }
     *     
     */
    public void setReturn(SubscriberProvisioningResponse value) {
        this._return = value;
    }

}
