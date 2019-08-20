
package com.elitecore.netvertexsm.ws.cxfws.ssp.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wsGetAddOnSubscriptionRequestResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsGetAddOnSubscriptionRequestResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://subscription.ssp.cxfws.ws.netvertexsm.elitecore.com/}addOnApprovalReqResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsGetAddOnSubscriptionRequestResponse", propOrder = {
    "_return"
})
public class WsGetAddOnSubscriptionRequestResponse {

    @XmlElement(name = "return")
    protected AddOnApprovalReqResponse _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link AddOnApprovalReqResponse }
     *     
     */
    public AddOnApprovalReqResponse getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddOnApprovalReqResponse }
     *     
     */
    public void setReturn(AddOnApprovalReqResponse value) {
        this._return = value;
    }

}