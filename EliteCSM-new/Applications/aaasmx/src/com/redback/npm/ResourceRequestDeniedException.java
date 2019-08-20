/**
 * ResourceRequestDeniedException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;

public class ResourceRequestDeniedException  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private java.lang.String congestionPointId;

    private java.lang.String congestionPointLevelName;

    private java.lang.String subscriberCongestion;

    private java.lang.String message1;

    public ResourceRequestDeniedException() {
    }

    public ResourceRequestDeniedException(
           java.lang.String congestionPointId,
           java.lang.String congestionPointLevelName,
           java.lang.String subscriberCongestion,
           java.lang.String message1) {
        this.congestionPointId = congestionPointId;
        this.congestionPointLevelName = congestionPointLevelName;
        this.subscriberCongestion = subscriberCongestion;
        this.message1 = message1;
    }


    /**
     * Gets the congestionPointId value for this ResourceRequestDeniedException.
     * 
     * @return congestionPointId
     */
    public java.lang.String getCongestionPointId() {
        return congestionPointId;
    }


    /**
     * Sets the congestionPointId value for this ResourceRequestDeniedException.
     * 
     * @param congestionPointId
     */
    public void setCongestionPointId(java.lang.String congestionPointId) {
        this.congestionPointId = congestionPointId;
    }


    /**
     * Gets the congestionPointLevelName value for this ResourceRequestDeniedException.
     * 
     * @return congestionPointLevelName
     */
    public java.lang.String getCongestionPointLevelName() {
        return congestionPointLevelName;
    }


    /**
     * Sets the congestionPointLevelName value for this ResourceRequestDeniedException.
     * 
     * @param congestionPointLevelName
     */
    public void setCongestionPointLevelName(java.lang.String congestionPointLevelName) {
        this.congestionPointLevelName = congestionPointLevelName;
    }


    /**
     * Gets the subscriberCongestion value for this ResourceRequestDeniedException.
     * 
     * @return subscriberCongestion
     */
    public java.lang.String getSubscriberCongestion() {
        return subscriberCongestion;
    }


    /**
     * Sets the subscriberCongestion value for this ResourceRequestDeniedException.
     * 
     * @param subscriberCongestion
     */
    public void setSubscriberCongestion(java.lang.String subscriberCongestion) {
        this.subscriberCongestion = subscriberCongestion;
    }


    /**
     * Gets the message1 value for this ResourceRequestDeniedException.
     * 
     * @return message1
     */
    public java.lang.String getMessage1() {
        return message1;
    }


    /**
     * Sets the message1 value for this ResourceRequestDeniedException.
     * 
     * @param message1
     */
    public void setMessage1(java.lang.String message1) {
        this.message1 = message1;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResourceRequestDeniedException)) return false;
        ResourceRequestDeniedException other = (ResourceRequestDeniedException) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.congestionPointId==null && other.getCongestionPointId()==null) || 
             (this.congestionPointId!=null &&
              this.congestionPointId.equals(other.getCongestionPointId()))) &&
            ((this.congestionPointLevelName==null && other.getCongestionPointLevelName()==null) || 
             (this.congestionPointLevelName!=null &&
              this.congestionPointLevelName.equals(other.getCongestionPointLevelName()))) &&
            ((this.subscriberCongestion==null && other.getSubscriberCongestion()==null) || 
             (this.subscriberCongestion!=null &&
              this.subscriberCongestion.equals(other.getSubscriberCongestion()))) &&
            ((this.message1==null && other.getMessage1()==null) || 
             (this.message1!=null &&
              this.message1.equals(other.getMessage1())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCongestionPointId() != null) {
            _hashCode += getCongestionPointId().hashCode();
        }
        if (getCongestionPointLevelName() != null) {
            _hashCode += getCongestionPointLevelName().hashCode();
        }
        if (getSubscriberCongestion() != null) {
            _hashCode += getSubscriberCongestion().hashCode();
        }
        if (getMessage1() != null) {
            _hashCode += getMessage1().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResourceRequestDeniedException.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://npm.redback.com", "ResourceRequestDeniedException"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("congestionPointId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "congestionPointId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("congestionPointLevelName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "congestionPointLevelName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriberCongestion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subscriberCongestion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }


    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, this);
    }
}
