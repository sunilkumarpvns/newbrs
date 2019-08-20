
package com.elitecore.netvertexsm.ws.cxfws.ssp.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addOnSubscriptionData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addOnSubscriptionData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="addOnPackageID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="addOnPackageName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addOnSubscriptionID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="lastUpdateTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="parentID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rejectReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscriberID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscriptionEndTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="subscriptionStartTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="subscriptionStatusName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscriptionStatusValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscriptionTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addOnSubscriptionData", propOrder = {
    "addOnPackageID",
    "addOnPackageName",
    "addOnSubscriptionID",
    "lastUpdateTime",
    "parentID",
    "rejectReason",
    "subscriberID",
    "subscriptionEndTime",
    "subscriptionStartTime",
    "subscriptionStatusName",
    "subscriptionStatusValue",
    "subscriptionTime"
})
public class AddOnSubscriptionData {

    protected Long addOnPackageID;
    protected String addOnPackageName;
    protected Long addOnSubscriptionID;
    protected Long lastUpdateTime;
    protected String parentID;
    protected String rejectReason;
    protected String subscriberID;
    protected Long subscriptionEndTime;
    protected Long subscriptionStartTime;
    protected String subscriptionStatusName;
    protected String subscriptionStatusValue;
    protected Long subscriptionTime;

    /**
     * Gets the value of the addOnPackageID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAddOnPackageID() {
        return addOnPackageID;
    }

    /**
     * Sets the value of the addOnPackageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAddOnPackageID(Long value) {
        this.addOnPackageID = value;
    }

    /**
     * Gets the value of the addOnPackageName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddOnPackageName() {
        return addOnPackageName;
    }

    /**
     * Sets the value of the addOnPackageName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddOnPackageName(String value) {
        this.addOnPackageName = value;
    }

    /**
     * Gets the value of the addOnSubscriptionID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAddOnSubscriptionID() {
        return addOnSubscriptionID;
    }

    /**
     * Sets the value of the addOnSubscriptionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAddOnSubscriptionID(Long value) {
        this.addOnSubscriptionID = value;
    }

    /**
     * Gets the value of the lastUpdateTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Sets the value of the lastUpdateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLastUpdateTime(Long value) {
        this.lastUpdateTime = value;
    }

    /**
     * Gets the value of the parentID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * Sets the value of the parentID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentID(String value) {
        this.parentID = value;
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
     * Gets the value of the subscriptionEndTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSubscriptionEndTime() {
        return subscriptionEndTime;
    }

    /**
     * Sets the value of the subscriptionEndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSubscriptionEndTime(Long value) {
        this.subscriptionEndTime = value;
    }

    /**
     * Gets the value of the subscriptionStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSubscriptionStartTime() {
        return subscriptionStartTime;
    }

    /**
     * Sets the value of the subscriptionStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSubscriptionStartTime(Long value) {
        this.subscriptionStartTime = value;
    }

    /**
     * Gets the value of the subscriptionStatusName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionStatusName() {
        return subscriptionStatusName;
    }

    /**
     * Sets the value of the subscriptionStatusName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionStatusName(String value) {
        this.subscriptionStatusName = value;
    }

    /**
     * Gets the value of the subscriptionStatusValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionStatusValue() {
        return subscriptionStatusValue;
    }

    /**
     * Sets the value of the subscriptionStatusValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionStatusValue(String value) {
        this.subscriptionStatusValue = value;
    }

    /**
     * Gets the value of the subscriptionTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSubscriptionTime() {
        return subscriptionTime;
    }

    /**
     * Sets the value of the subscriptionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSubscriptionTime(Long value) {
        this.subscriptionTime = value;
    }

}
