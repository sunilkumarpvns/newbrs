
package com.elitecore.netvertexsm.ws.cxfws.ssp.parental;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wsListChildProfilesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsListChildProfilesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://parental.ssp.cxfws.ws.netvertexsm.elitecore.com/}childProfileResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsListChildProfilesResponse", propOrder = {
    "_return"
})
public class WsListChildProfilesResponse {

    @XmlElement(name = "return")
    protected ChildProfileResponse _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link ChildProfileResponse }
     *     
     */
    public ChildProfileResponse getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChildProfileResponse }
     *     
     */
    public void setReturn(ChildProfileResponse value) {
        this._return = value;
    }

}
