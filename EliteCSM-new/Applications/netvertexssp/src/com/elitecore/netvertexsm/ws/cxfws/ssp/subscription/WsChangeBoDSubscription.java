
package com.elitecore.netvertexsm.ws.cxfws.ssp.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wsChangeBoDSubscription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsChangeBoDSubscription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscriberID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bodSubscriptionID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="subscriptionStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rejectReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bodStartTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="validityPeriod" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="validityPeriodUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parameter1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parameter2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsChangeBoDSubscription", propOrder = {
    "subscriberID",
    "bodSubscriptionID",
    "subscriptionStatus",
    "rejectReason",
    "bodStartTime",
    "validityPeriod",
    "validityPeriodUnit",
    "parameter1",
    "parameter2"
})
public class WsChangeBoDSubscription {

    protected String subscriberID;
    protected Long bodSubscriptionID;
    protected String subscriptionStatus;
    protected String rejectReason;
    protected Long bodStartTime;
    protected Long validityPeriod;
    protected String validityPeriodUnit;
    protected String parameter1;
    protected String parameter2;

    /**
     * Gets the value of the subscriberID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriberID() {
        return subscriberID;
    }

    /**
     * Sets the value of the subscriberID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriberID(String value) {
        this.subscriberID = value;
    }

    /**
     * Gets the value of the bodSubscriptionID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBodSubscriptionID() {
        return bodSubscriptionID;
    }

    /**
     * Sets the value of the bodSubscriptionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBodSubscriptionID(Long value) {
        this.bodSubscriptionID = value;
    }

    /**
     * Gets the value of the subscriptionStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    /**
     * Sets the value of the subscriptionStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionStatus(String value) {
        this.subscriptionStatus = value;
    }

    /**
     * Gets the value of the rejectReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * Sets the value of the rejectReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRejectReason(String value) {
        this.rejectReason = value;
    }

    /**
     * Gets the value of the bodStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBodStartTime() {
        return bodStartTime;
    }

    /**
     * Sets the value of the bodStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBodStartTime(Long value) {
        this.bodStartTime = value;
    }

    /**
     * Gets the value of the validityPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * Sets the value of the validityPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setValidityPeriod(Long value) {
        this.validityPeriod = value;
    }

    /**
     * Gets the value of the validityPeriodUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    /**
     * Sets the value of the validityPeriodUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidityPeriodUnit(String value) {
        this.validityPeriodUnit = value;
    }

    /**
     * Gets the value of the parameter1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameter1() {
        return parameter1;
    }

    /**
     * Sets the value of the parameter1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameter1(String value) {
        this.parameter1 = value;
    }

    /**
     * Gets the value of the parameter2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameter2() {
        return parameter2;
    }

    /**
     * Sets the value of the parameter2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameter2(String value) {
        this.parameter2 = value;
    }

}
