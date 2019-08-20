/**
 * SubscriberSessionSummary.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;


public class SubscriberSessionSummary  implements java.io.Serializable {
    private java.lang.String circuitType;

    private java.lang.String nasId;

    private java.lang.String nasType;

    private java.lang.String sessionId;

    private java.util.Calendar startTime;

    public SubscriberSessionSummary() {
    }

    public SubscriberSessionSummary(
           java.lang.String circuitType,
           java.lang.String nasId,
           java.lang.String nasType,
           java.lang.String sessionId,
           java.util.Calendar startTime) {
           this.circuitType = circuitType;
           this.nasId = nasId;
           this.nasType = nasType;
           this.sessionId = sessionId;
           this.startTime = startTime;
    }


    /**
     * Gets the circuitType value for this SubscriberSessionSummary.
     * 
     * @return circuitType
     */
    public java.lang.String getCircuitType() {
        return circuitType;
    }


    /**
     * Sets the circuitType value for this SubscriberSessionSummary.
     * 
     * @param circuitType
     */
    public void setCircuitType(java.lang.String circuitType) {
        this.circuitType = circuitType;
    }


    /**
     * Gets the nasId value for this SubscriberSessionSummary.
     * 
     * @return nasId
     */
    public java.lang.String getNasId() {
        return nasId;
    }


    /**
     * Sets the nasId value for this SubscriberSessionSummary.
     * 
     * @param nasId
     */
    public void setNasId(java.lang.String nasId) {
        this.nasId = nasId;
    }


    /**
     * Gets the nasType value for this SubscriberSessionSummary.
     * 
     * @return nasType
     */
    public java.lang.String getNasType() {
        return nasType;
    }


    /**
     * Sets the nasType value for this SubscriberSessionSummary.
     * 
     * @param nasType
     */
    public void setNasType(java.lang.String nasType) {
        this.nasType = nasType;
    }


    /**
     * Gets the sessionId value for this SubscriberSessionSummary.
     * 
     * @return sessionId
     */
    public java.lang.String getSessionId() {
        return sessionId;
    }


    /**
     * Sets the sessionId value for this SubscriberSessionSummary.
     * 
     * @param sessionId
     */
    public void setSessionId(java.lang.String sessionId) {
        this.sessionId = sessionId;
    }


    /**
     * Gets the startTime value for this SubscriberSessionSummary.
     * 
     * @return startTime
     */
    public java.util.Calendar getStartTime() {
        return startTime;
    }


    /**
     * Sets the startTime value for this SubscriberSessionSummary.
     * 
     * @param startTime
     */
    public void setStartTime(java.util.Calendar startTime) {
        this.startTime = startTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SubscriberSessionSummary)) return false;
        SubscriberSessionSummary other = (SubscriberSessionSummary) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.circuitType==null && other.getCircuitType()==null) || 
             (this.circuitType!=null &&
              this.circuitType.equals(other.getCircuitType()))) &&
            ((this.nasId==null && other.getNasId()==null) || 
             (this.nasId!=null &&
              this.nasId.equals(other.getNasId()))) &&
            ((this.nasType==null && other.getNasType()==null) || 
             (this.nasType!=null &&
              this.nasType.equals(other.getNasType()))) &&
            ((this.sessionId==null && other.getSessionId()==null) || 
             (this.sessionId!=null &&
              this.sessionId.equals(other.getSessionId()))) &&
            ((this.startTime==null && other.getStartTime()==null) || 
             (this.startTime!=null &&
              this.startTime.equals(other.getStartTime())));
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
        if (getCircuitType() != null) {
            _hashCode += getCircuitType().hashCode();
        }
        if (getNasId() != null) {
            _hashCode += getNasId().hashCode();
        }
        if (getNasType() != null) {
            _hashCode += getNasType().hashCode();
        }
        if (getSessionId() != null) {
            _hashCode += getSessionId().hashCode();
        }
        if (getStartTime() != null) {
            _hashCode += getStartTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SubscriberSessionSummary.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://npm.redback.com", "SubscriberSessionSummary"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("circuitType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "circuitType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nasId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nasId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nasType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nasType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sessionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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

}
