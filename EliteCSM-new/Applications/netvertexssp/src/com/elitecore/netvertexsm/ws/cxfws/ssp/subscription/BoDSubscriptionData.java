
package com.elitecore.netvertexsm.ws.cxfws.ssp.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for boDSubscriptionData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="boDSubscriptionData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bodEndTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="bodPackageID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="bodPackageName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bodStartTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="bodStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bodSubscriptionID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="bodSubscriptionTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastUpdateTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="rejectReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "boDSubscriptionData", propOrder = {
    "bodEndTime",
    "bodPackageID",
    "bodPackageName",
    "bodStartTime",
    "bodStatus",
    "bodSubscriptionID",
    "bodSubscriptionTime",
    "description",
    "lastUpdateTime",
    "rejectReason"
})
public class BoDSubscriptionData {

    protected Long bodEndTime;
    protected Long bodPackageID;
    protected String bodPackageName;
    protected Long bodStartTime;
    protected String bodStatus;
    protected Long bodSubscriptionID;
    protected Long bodSubscriptionTime;
    protected String description;
    protected Long lastUpdateTime;
    protected String rejectReason;

    /**
     * Gets the value of the bodEndTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBodEndTime() {
        return bodEndTime;
    }

    /**
     * Sets the value of the bodEndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBodEndTime(Long value) {
        this.bodEndTime = value;
    }

    /**
     * Gets the value of the bodPackageID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBodPackageID() {
        return bodPackageID;
    }

    /**
     * Sets the value of the bodPackageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBodPackageID(Long value) {
        this.bodPackageID = value;
    }

    /**
     * Gets the value of the bodPackageName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBodPackageName() {
        return bodPackageName;
    }

    /**
     * Sets the value of the bodPackageName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBodPackageName(String value) {
        this.bodPackageName = value;
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
     * Gets the value of the bodStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBodStatus() {
        return bodStatus;
    }

    /**
     * Sets the value of the bodStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBodStatus(String value) {
        this.bodStatus = value;
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
     * Gets the value of the bodSubscriptionTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBodSubscriptionTime() {
        return bodSubscriptionTime;
    }

    /**
     * Sets the value of the bodSubscriptionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBodSubscriptionTime(Long value) {
        this.bodSubscriptionTime = value;
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

}
