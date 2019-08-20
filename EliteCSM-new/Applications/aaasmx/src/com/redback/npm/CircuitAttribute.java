/**
 * CircuitAttribute.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;

public class CircuitAttribute  implements java.io.Serializable {
    private java.lang.String NASIdentifier;

    private java.lang.String NASPortId;

    private java.lang.String framedRoute;

    private boolean preAuthenticated;

    private java.lang.String qosReference;

    private com.redback.npm.SessionFilter sessionFilter;

    private java.lang.String staticIPAddress;

    public CircuitAttribute() {
    }

    public CircuitAttribute(
           java.lang.String NASIdentifier,
           java.lang.String NASPortId,
           java.lang.String framedRoute,
           boolean preAuthenticated,
           java.lang.String qosReference,
           com.redback.npm.SessionFilter sessionFilter,
           java.lang.String staticIPAddress) {
           this.NASIdentifier = NASIdentifier;
           this.NASPortId = NASPortId;
           this.framedRoute = framedRoute;
           this.preAuthenticated = preAuthenticated;
           this.qosReference = qosReference;
           this.sessionFilter = sessionFilter;
           this.staticIPAddress = staticIPAddress;
    }


    /**
     * Gets the NASIdentifier value for this CircuitAttribute.
     * 
     * @return NASIdentifier
     */
    public java.lang.String getNASIdentifier() {
        return NASIdentifier;
    }


    /**
     * Sets the NASIdentifier value for this CircuitAttribute.
     * 
     * @param NASIdentifier
     */
    public void setNASIdentifier(java.lang.String NASIdentifier) {
        this.NASIdentifier = NASIdentifier;
    }


    /**
     * Gets the NASPortId value for this CircuitAttribute.
     * 
     * @return NASPortId
     */
    public java.lang.String getNASPortId() {
        return NASPortId;
    }


    /**
     * Sets the NASPortId value for this CircuitAttribute.
     * 
     * @param NASPortId
     */
    public void setNASPortId(java.lang.String NASPortId) {
        this.NASPortId = NASPortId;
    }


    /**
     * Gets the framedRoute value for this CircuitAttribute.
     * 
     * @return framedRoute
     */
    public java.lang.String getFramedRoute() {
        return framedRoute;
    }


    /**
     * Sets the framedRoute value for this CircuitAttribute.
     * 
     * @param framedRoute
     */
    public void setFramedRoute(java.lang.String framedRoute) {
        this.framedRoute = framedRoute;
    }


    /**
     * Gets the preAuthenticated value for this CircuitAttribute.
     * 
     * @return preAuthenticated
     */
    public boolean isPreAuthenticated() {
        return preAuthenticated;
    }


    /**
     * Sets the preAuthenticated value for this CircuitAttribute.
     * 
     * @param preAuthenticated
     */
    public void setPreAuthenticated(boolean preAuthenticated) {
        this.preAuthenticated = preAuthenticated;
    }


    /**
     * Gets the qosReference value for this CircuitAttribute.
     * 
     * @return qosReference
     */
    public java.lang.String getQosReference() {
        return qosReference;
    }


    /**
     * Sets the qosReference value for this CircuitAttribute.
     * 
     * @param qosReference
     */
    public void setQosReference(java.lang.String qosReference) {
        this.qosReference = qosReference;
    }


    /**
     * Gets the sessionFilter value for this CircuitAttribute.
     * 
     * @return sessionFilter
     */
    public com.redback.npm.SessionFilter getSessionFilter() {
        return sessionFilter;
    }


    /**
     * Sets the sessionFilter value for this CircuitAttribute.
     * 
     * @param sessionFilter
     */
    public void setSessionFilter(com.redback.npm.SessionFilter sessionFilter) {
        this.sessionFilter = sessionFilter;
    }


    /**
     * Gets the staticIPAddress value for this CircuitAttribute.
     * 
     * @return staticIPAddress
     */
    public java.lang.String getStaticIPAddress() {
        return staticIPAddress;
    }


    /**
     * Sets the staticIPAddress value for this CircuitAttribute.
     * 
     * @param staticIPAddress
     */
    public void setStaticIPAddress(java.lang.String staticIPAddress) {
        this.staticIPAddress = staticIPAddress;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CircuitAttribute)) return false;
        CircuitAttribute other = (CircuitAttribute) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.NASIdentifier==null && other.getNASIdentifier()==null) || 
             (this.NASIdentifier!=null &&
              this.NASIdentifier.equals(other.getNASIdentifier()))) &&
            ((this.NASPortId==null && other.getNASPortId()==null) || 
             (this.NASPortId!=null &&
              this.NASPortId.equals(other.getNASPortId()))) &&
            ((this.framedRoute==null && other.getFramedRoute()==null) || 
             (this.framedRoute!=null &&
              this.framedRoute.equals(other.getFramedRoute()))) &&
            this.preAuthenticated == other.isPreAuthenticated() &&
            ((this.qosReference==null && other.getQosReference()==null) || 
             (this.qosReference!=null &&
              this.qosReference.equals(other.getQosReference()))) &&
            ((this.sessionFilter==null && other.getSessionFilter()==null) || 
             (this.sessionFilter!=null &&
              this.sessionFilter.equals(other.getSessionFilter()))) &&
            ((this.staticIPAddress==null && other.getStaticIPAddress()==null) || 
             (this.staticIPAddress!=null &&
              this.staticIPAddress.equals(other.getStaticIPAddress())));
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
        if (getNASIdentifier() != null) {
            _hashCode += getNASIdentifier().hashCode();
        }
        if (getNASPortId() != null) {
            _hashCode += getNASPortId().hashCode();
        }
        if (getFramedRoute() != null) {
            _hashCode += getFramedRoute().hashCode();
        }
        _hashCode += (isPreAuthenticated() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getQosReference() != null) {
            _hashCode += getQosReference().hashCode();
        }
        if (getSessionFilter() != null) {
            _hashCode += getSessionFilter().hashCode();
        }
        if (getStaticIPAddress() != null) {
            _hashCode += getStaticIPAddress().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CircuitAttribute.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://npm.redback.com", "CircuitAttribute"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NASIdentifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NASIdentifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NASPortId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NASPortId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("framedRoute");
        elemField.setXmlName(new javax.xml.namespace.QName("", "framedRoute"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("preAuthenticated");
        elemField.setXmlName(new javax.xml.namespace.QName("", "preAuthenticated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qosReference");
        elemField.setXmlName(new javax.xml.namespace.QName("", "qosReference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionFilter");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sessionFilter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://npm.redback.com", "SessionFilter"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("staticIPAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "staticIPAddress"));
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
