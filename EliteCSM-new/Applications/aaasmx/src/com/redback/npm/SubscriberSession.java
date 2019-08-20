/**
 * SubscriberSession.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;

public class SubscriberSession  extends com.redback.npm.SubscriberSessionSummary  implements java.io.Serializable {
    private java.lang.String NASPortId;

    private java.lang.String NASPortType;

    private java.lang.String acctSessionId;

    private java.lang.String callingStationId;

    private java.lang.String context;

    private java.lang.String macAddress;

    private java.lang.String medium;

    private java.lang.String sessionIp;

    private java.lang.String subscriberAccount;

    public SubscriberSession() {
    }

    public SubscriberSession(
           java.lang.String circuitType,
           java.lang.String nasId,
           java.lang.String nasType,
           java.lang.String sessionId,
           java.util.Calendar startTime,
           java.lang.String NASPortId,
           java.lang.String NASPortType,
           java.lang.String acctSessionId,
           java.lang.String callingStationId,
           java.lang.String context,
           java.lang.String macAddress,
           java.lang.String medium,
           java.lang.String sessionIp,
           java.lang.String subscriberAccount) {
        super(
            circuitType,
            nasId,
            nasType,
            sessionId,
            startTime);
        this.NASPortId = NASPortId;
        this.NASPortType = NASPortType;
        this.acctSessionId = acctSessionId;
        this.callingStationId = callingStationId;
        this.context = context;
        this.macAddress = macAddress;
        this.medium = medium;
        this.sessionIp = sessionIp;
        this.subscriberAccount = subscriberAccount;
    }


    /**
     * Gets the NASPortId value for this SubscriberSession.
     * 
     * @return NASPortId
     */
    public java.lang.String getNASPortId() {
        return NASPortId;
    }


    /**
     * Sets the NASPortId value for this SubscriberSession.
     * 
     * @param NASPortId
     */
    public void setNASPortId(java.lang.String NASPortId) {
        this.NASPortId = NASPortId;
    }


    /**
     * Gets the NASPortType value for this SubscriberSession.
     * 
     * @return NASPortType
     */
    public java.lang.String getNASPortType() {
        return NASPortType;
    }


    /**
     * Sets the NASPortType value for this SubscriberSession.
     * 
     * @param NASPortType
     */
    public void setNASPortType(java.lang.String NASPortType) {
        this.NASPortType = NASPortType;
    }


    /**
     * Gets the acctSessionId value for this SubscriberSession.
     * 
     * @return acctSessionId
     */
    public java.lang.String getAcctSessionId() {
        return acctSessionId;
    }


    /**
     * Sets the acctSessionId value for this SubscriberSession.
     * 
     * @param acctSessionId
     */
    public void setAcctSessionId(java.lang.String acctSessionId) {
        this.acctSessionId = acctSessionId;
    }


    /**
     * Gets the callingStationId value for this SubscriberSession.
     * 
     * @return callingStationId
     */
    public java.lang.String getCallingStationId() {
        return callingStationId;
    }


    /**
     * Sets the callingStationId value for this SubscriberSession.
     * 
     * @param callingStationId
     */
    public void setCallingStationId(java.lang.String callingStationId) {
        this.callingStationId = callingStationId;
    }


    /**
     * Gets the context value for this SubscriberSession.
     * 
     * @return context
     */
    public java.lang.String getContext() {
        return context;
    }


    /**
     * Sets the context value for this SubscriberSession.
     * 
     * @param context
     */
    public void setContext(java.lang.String context) {
        this.context = context;
    }


    /**
     * Gets the macAddress value for this SubscriberSession.
     * 
     * @return macAddress
     */
    public java.lang.String getMacAddress() {
        return macAddress;
    }


    /**
     * Sets the macAddress value for this SubscriberSession.
     * 
     * @param macAddress
     */
    public void setMacAddress(java.lang.String macAddress) {
        this.macAddress = macAddress;
    }


    /**
     * Gets the medium value for this SubscriberSession.
     * 
     * @return medium
     */
    public java.lang.String getMedium() {
        return medium;
    }


    /**
     * Sets the medium value for this SubscriberSession.
     * 
     * @param medium
     */
    public void setMedium(java.lang.String medium) {
        this.medium = medium;
    }


    /**
     * Gets the sessionIp value for this SubscriberSession.
     * 
     * @return sessionIp
     */
    public java.lang.String getSessionIp() {
        return sessionIp;
    }


    /**
     * Sets the sessionIp value for this SubscriberSession.
     * 
     * @param sessionIp
     */
    public void setSessionIp(java.lang.String sessionIp) {
        this.sessionIp = sessionIp;
    }


    /**
     * Gets the subscriberAccount value for this SubscriberSession.
     * 
     * @return subscriberAccount
     */
    public java.lang.String getSubscriberAccount() {
        return subscriberAccount;
    }


    /**
     * Sets the subscriberAccount value for this SubscriberSession.
     * 
     * @param subscriberAccount
     */
    public void setSubscriberAccount(java.lang.String subscriberAccount) {
        this.subscriberAccount = subscriberAccount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SubscriberSession)) return false;
        SubscriberSession other = (SubscriberSession) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.NASPortId==null && other.getNASPortId()==null) || 
             (this.NASPortId!=null &&
              this.NASPortId.equals(other.getNASPortId()))) &&
            ((this.NASPortType==null && other.getNASPortType()==null) || 
             (this.NASPortType!=null &&
              this.NASPortType.equals(other.getNASPortType()))) &&
            ((this.acctSessionId==null && other.getAcctSessionId()==null) || 
             (this.acctSessionId!=null &&
              this.acctSessionId.equals(other.getAcctSessionId()))) &&
            ((this.callingStationId==null && other.getCallingStationId()==null) || 
             (this.callingStationId!=null &&
              this.callingStationId.equals(other.getCallingStationId()))) &&
            ((this.context==null && other.getContext()==null) || 
             (this.context!=null &&
              this.context.equals(other.getContext()))) &&
            ((this.macAddress==null && other.getMacAddress()==null) || 
             (this.macAddress!=null &&
              this.macAddress.equals(other.getMacAddress()))) &&
            ((this.medium==null && other.getMedium()==null) || 
             (this.medium!=null &&
              this.medium.equals(other.getMedium()))) &&
            ((this.sessionIp==null && other.getSessionIp()==null) || 
             (this.sessionIp!=null &&
              this.sessionIp.equals(other.getSessionIp()))) &&
            ((this.subscriberAccount==null && other.getSubscriberAccount()==null) || 
             (this.subscriberAccount!=null &&
              this.subscriberAccount.equals(other.getSubscriberAccount())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getNASPortId() != null) {
            _hashCode += getNASPortId().hashCode();
        }
        if (getNASPortType() != null) {
            _hashCode += getNASPortType().hashCode();
        }
        if (getAcctSessionId() != null) {
            _hashCode += getAcctSessionId().hashCode();
        }
        if (getCallingStationId() != null) {
            _hashCode += getCallingStationId().hashCode();
        }
        if (getContext() != null) {
            _hashCode += getContext().hashCode();
        }
        if (getMacAddress() != null) {
            _hashCode += getMacAddress().hashCode();
        }
        if (getMedium() != null) {
            _hashCode += getMedium().hashCode();
        }
        if (getSessionIp() != null) {
            _hashCode += getSessionIp().hashCode();
        }
        if (getSubscriberAccount() != null) {
            _hashCode += getSubscriberAccount().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SubscriberSession.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://npm.redback.com", "SubscriberSession"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NASPortId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NASPortId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NASPortType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NASPortType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acctSessionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acctSessionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callingStationId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "callingStationId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("context");
        elemField.setXmlName(new javax.xml.namespace.QName("", "context"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("macAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "macAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("medium");
        elemField.setXmlName(new javax.xml.namespace.QName("", "medium"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionIp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sessionIp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriberAccount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subscriberAccount"));
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
