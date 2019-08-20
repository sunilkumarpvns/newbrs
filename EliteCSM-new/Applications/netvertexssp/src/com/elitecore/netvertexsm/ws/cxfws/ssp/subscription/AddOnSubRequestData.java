
package com.elitecore.netvertexsm.ws.cxfws.ssp.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addOnSubRequestData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addOnSubRequestData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="addOnID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="addOnSubReqID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="createTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="lastUpdateTime" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="parentIdentity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rejectReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscriberIdentity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addOnSubRequestData", propOrder = {
    "addOnID",
    "addOnSubReqID",
    "createTime",
    "lastUpdateTime",
    "parentIdentity",
    "rejectReason",
    "status",
    "subscriberIdentity"
})
public class AddOnSubRequestData {

    protected Long addOnID;
    protected Long addOnSubReqID;
    protected Long createTime;
    protected Long lastUpdateTime;
    protected String parentIdentity;
    protected String rejectReason;
    protected String status;
    protected String subscriberIdentity;

    /**
     * Gets the value of the addOnID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAddOnID() {
        return addOnID;
    }

    /**
     * Sets the value of the addOnID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAddOnID(Long value) {
        this.addOnID = value;
    }

    /**
     * Gets the value of the addOnSubReqID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAddOnSubReqID() {
        return addOnSubReqID;
    }

    /**
     * Sets the value of the addOnSubReqID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAddOnSubReqID(Long value) {
        this.addOnSubReqID = value;
    }

    /**
     * Gets the value of the createTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * Sets the value of the createTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCreateTime(Long value) {
        this.createTime = value;
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
     * Gets the value of the parentIdentity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentIdentity() {
        return parentIdentity;
    }

    /**
     * Sets the value of the parentIdentity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentIdentity(String value) {
        this.parentIdentity = value;
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
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the subscriberIdentity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriberIdentity() {
        return subscriberIdentity;
    }

    /**
     * Sets the value of the subscriberIdentity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriberIdentity(String value) {
        this.subscriberIdentity = value;
    }

}
