
package com.elitecore.netvertexsm.ws.cxfws.ssp.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for usageMonotoringInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="usageMonotoringInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="aggregateKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CUI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="downloadOctets" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="meteringLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="monitoringKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subscriberID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalOctets" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="uploadOctets" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="usageTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "usageMonotoringInformation", propOrder = {
    "aggregateKey",
    "cui",
    "downloadOctets",
    "meteringLevel",
    "monitoringKey",
    "subscriberID",
    "totalOctets",
    "uploadOctets",
    "usageTime"
})
public class UsageMonotoringInformation {

    protected String aggregateKey;
    @XmlElement(name = "CUI")
    protected String cui;
    protected long downloadOctets;
    protected String meteringLevel;
    protected String monitoringKey;
    protected String subscriberID;
    protected long totalOctets;
    protected long uploadOctets;
    protected long usageTime;

    /**
     * Gets the value of the aggregateKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAggregateKey() {
        return aggregateKey;
    }

    /**
     * Sets the value of the aggregateKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAggregateKey(String value) {
        this.aggregateKey = value;
    }

    /**
     * Gets the value of the cui property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUI() {
        return cui;
    }

    /**
     * Sets the value of the cui property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUI(String value) {
        this.cui = value;
    }

    /**
     * Gets the value of the downloadOctets property.
     * 
     */
    public long getDownloadOctets() {
        return downloadOctets;
    }

    /**
     * Sets the value of the downloadOctets property.
     * 
     */
    public void setDownloadOctets(long value) {
        this.downloadOctets = value;
    }

    /**
     * Gets the value of the meteringLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeteringLevel() {
        return meteringLevel;
    }

    /**
     * Sets the value of the meteringLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeteringLevel(String value) {
        this.meteringLevel = value;
    }

    /**
     * Gets the value of the monitoringKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonitoringKey() {
        return monitoringKey;
    }

    /**
     * Sets the value of the monitoringKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonitoringKey(String value) {
        this.monitoringKey = value;
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
     * Gets the value of the totalOctets property.
     * 
     */
    public long getTotalOctets() {
        return totalOctets;
    }

    /**
     * Sets the value of the totalOctets property.
     * 
     */
    public void setTotalOctets(long value) {
        this.totalOctets = value;
    }

    /**
     * Gets the value of the uploadOctets property.
     * 
     */
    public long getUploadOctets() {
        return uploadOctets;
    }

    /**
     * Sets the value of the uploadOctets property.
     * 
     */
    public void setUploadOctets(long value) {
        this.uploadOctets = value;
    }

    /**
     * Gets the value of the usageTime property.
     * 
     */
    public long getUsageTime() {
        return usageTime;
    }

    /**
     * Sets the value of the usageTime property.
     * 
     */
    public void setUsageTime(long value) {
        this.usageTime = value;
    }

}
