
package com.elitecore.netvertexsm.ws.cxfws.ssp.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wsListAddOnSubscriptionsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsListAddOnSubscriptionsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://subscription.ssp.cxfws.ws.netvertexsm.elitecore.com/}addOnSubscriptionResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsListAddOnSubscriptionsResponse", propOrder = {
    "_return"
})
public class WsListAddOnSubscriptionsResponse {

    @XmlElement(name = "return")
    protected AddOnSubscriptionResponse _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link AddOnSubscriptionResponse }
     *     
     */
    public AddOnSubscriptionResponse getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddOnSubscriptionResponse }
     *     
     */
    public void setReturn(AddOnSubscriptionResponse value) {
        this._return = value;
    }

}