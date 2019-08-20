
package com.elitecore.netvertexsm.ws.cxfws.ssp.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addOnPackage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addOnPackage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="addOnPackageID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="addOnPackageName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="addOnPackageType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BSSCorrelationID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hsqValue" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="OCSCorrelationID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="offerEndDate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="subscriberID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="usageResetInterval" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="validityPeriod" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="validityPeriodUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addOnPackage", propOrder = {
    "addOnPackageID",
    "addOnPackageName",
    "addOnPackageType",
    "bssCorrelationID",
    "description",
    "hsqValue",
    "ocsCorrelationID",
    "offerEndDate",
    "price",
    "priority",
    "subscriberID",
    "usageResetInterval",
    "validityPeriod",
    "validityPeriodUnit"
})
public class AddOnPackage {

    protected Long addOnPackageID;
    protected String addOnPackageName;
    protected String addOnPackageType;
    @XmlElement(name = "BSSCorrelationID")
    protected String bssCorrelationID;
    protected String description;
    protected Long hsqValue;
    @XmlElement(name = "OCSCorrelationID")
    protected String ocsCorrelationID;
    protected Long offerEndDate;
    protected Long price;
    protected Integer priority;
    protected String subscriberID;
    protected Long usageResetInterval;
    protected Long validityPeriod;
    protected String validityPeriodUnit;

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
     * Gets the value of the addOnPackageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddOnPackageType() {
        return addOnPackageType;
    }

    /**
     * Sets the value of the addOnPackageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddOnPackageType(String value) {
        this.addOnPackageType = value;
    }

    /**
     * Gets the value of the bssCorrelationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBSSCorrelationID() {
        return bssCorrelationID;
    }

    /**
     * Sets the value of the bssCorrelationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBSSCorrelationID(String value) {
        this.bssCorrelationID = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the hsqValue property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getHsqValue() {
        return hsqValue;
    }

    /**
     * Sets the value of the hsqValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHsqValue(Long value) {
        this.hsqValue = value;
    }

    /**
     * Gets the value of the ocsCorrelationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOCSCorrelationID() {
        return ocsCorrelationID;
    }

    /**
     * Sets the value of the ocsCorrelationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOCSCorrelationID(String value) {
        this.ocsCorrelationID = value;
    }

    /**
     * Gets the value of the offerEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOfferEndDate() {
        return offerEndDate;
    }

    /**
     * Sets the value of the offerEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOfferEndDate(Long value) {
        this.offerEndDate = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPrice(Long value) {
        this.price = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPriority(Integer value) {
        this.priority = value;
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
     * Gets the value of the usageResetInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUsageResetInterval() {
        return usageResetInterval;
    }

    /**
     * Sets the value of the usageResetInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUsageResetInterval(Long value) {
        this.usageResetInterval = value;
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

}
