
package com.elitecore.netvertexsm.ws.cxfws.ssp.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wsListUsageMonitoringInformationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsListUsageMonitoringInformationResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://subscription.ssp.cxfws.ws.netvertexsm.elitecore.com/}usageMonotoringQueryResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsListUsageMonitoringInformationResponse", propOrder = {
    "_return"
})
public class WsListUsageMonitoringInformationResponse {

    @XmlElement(name = "return")
    protected UsageMonotoringQueryResponse _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link UsageMonotoringQueryResponse }
     *     
     */
    public UsageMonotoringQueryResponse getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link UsageMonotoringQueryResponse }
     *     
     */
    public void setReturn(UsageMonotoringQueryResponse value) {
        this._return = value;
    }

}
