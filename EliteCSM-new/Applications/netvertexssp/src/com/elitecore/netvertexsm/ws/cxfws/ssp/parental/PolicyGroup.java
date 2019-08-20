
package com.elitecore.netvertexsm.ws.cxfws.ssp.parental;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for policyGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="policyGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hsqValue" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="policyGroupID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="policyGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "policyGroup", propOrder = {
    "description",
    "hsqValue",
    "policyGroupID",
    "policyGroupName"
})
public class PolicyGroup {

    protected String description;
    protected Long hsqValue;
    protected Long policyGroupID;
    protected String policyGroupName;

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
     * Gets the value of the policyGroupID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPolicyGroupID() {
        return policyGroupID;
    }

    /**
     * Sets the value of the policyGroupID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPolicyGroupID(Long value) {
        this.policyGroupID = value;
    }

    /**
     * Gets the value of the policyGroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyGroupName() {
        return policyGroupName;
    }

    /**
     * Sets the value of the policyGroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyGroupName(String value) {
        this.policyGroupName = value;
    }

}
