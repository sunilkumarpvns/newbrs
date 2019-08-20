
package com.elitecore.netvertexsm.ws.cxfws.ssp.parental;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for parentalPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="parentalPolicy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="addOnPackageID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="daysOfTheWeek" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timePeriod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parentalPolicy", propOrder = {
    "addOnPackageID",
    "daysOfTheWeek",
    "timePeriod"
})
public class ParentalPolicy {

    protected Long addOnPackageID;
    protected String daysOfTheWeek;
    protected String timePeriod;

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
     * Gets the value of the daysOfTheWeek property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDaysOfTheWeek() {
        return daysOfTheWeek;
    }

    /**
     * Sets the value of the daysOfTheWeek property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDaysOfTheWeek(String value) {
        this.daysOfTheWeek = value;
    }

    /**
     * Gets the value of the timePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimePeriod() {
        return timePeriod;
    }

    /**
     * Sets the value of the timePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimePeriod(String value) {
        this.timePeriod = value;
    }

}
