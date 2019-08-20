/**
 * SessionFilter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;

public class SessionFilter  implements java.io.Serializable {
    private java.lang.String callingStationId;

    private java.lang.String networkCircuitId;

    private java.lang.String sessionName;

    public SessionFilter() {
    }

    public SessionFilter(
           java.lang.String callingStationId,
           java.lang.String networkCircuitId,
           java.lang.String sessionName) {
           this.callingStationId = callingStationId;
           this.networkCircuitId = networkCircuitId;
           this.sessionName = sessionName;
    }


    /**
     * Gets the callingStationId value for this SessionFilter.
     * 
     * @return callingStationId
     */
    public java.lang.String getCallingStationId() {
        return callingStationId;
    }


    /**
     * Sets the callingStationId value for this SessionFilter.
     * 
     * @param callingStationId
     */
    public void setCallingStationId(java.lang.String callingStationId) {
        this.callingStationId = callingStationId;
    }


    /**
     * Gets the networkCircuitId value for this SessionFilter.
     * 
     * @return networkCircuitId
     */
    public java.lang.String getNetworkCircuitId() {
        return networkCircuitId;
    }


    /**
     * Sets the networkCircuitId value for this SessionFilter.
     * 
     * @param networkCircuitId
     */
    public void setNetworkCircuitId(java.lang.String networkCircuitId) {
        this.networkCircuitId = networkCircuitId;
    }


    /**
     * Gets the sessionName value for this SessionFilter.
     * 
     * @return sessionName
     */
    public java.lang.String getSessionName() {
        return sessionName;
    }


    /**
     * Sets the sessionName value for this SessionFilter.
     * 
     * @param sessionName
     */
    public void setSessionName(java.lang.String sessionName) {
        this.sessionName = sessionName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SessionFilter)) return false;
        SessionFilter other = (SessionFilter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.callingStationId==null && other.getCallingStationId()==null) || 
             (this.callingStationId!=null &&
              this.callingStationId.equals(other.getCallingStationId()))) &&
            ((this.networkCircuitId==null && other.getNetworkCircuitId()==null) || 
             (this.networkCircuitId!=null &&
              this.networkCircuitId.equals(other.getNetworkCircuitId()))) &&
            ((this.sessionName==null && other.getSessionName()==null) || 
             (this.sessionName!=null &&
              this.sessionName.equals(other.getSessionName())));
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
        if (getCallingStationId() != null) {
            _hashCode += getCallingStationId().hashCode();
        }
        if (getNetworkCircuitId() != null) {
            _hashCode += getNetworkCircuitId().hashCode();
        }
        if (getSessionName() != null) {
            _hashCode += getSessionName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SessionFilter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://npm.redback.com", "SessionFilter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callingStationId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "callingStationId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("networkCircuitId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "networkCircuitId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sessionName"));
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

}
