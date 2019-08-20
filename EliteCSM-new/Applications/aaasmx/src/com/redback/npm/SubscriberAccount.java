/**
 * SubscriberAccount.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redback.npm;

import java.util.ArrayList;

public class SubscriberAccount  implements java.io.Serializable {
    private boolean activated;

    private com.redback.npm.CircuitAttribute[] circuitAttributes;

    private java.util.Calendar creationDate;

    private java.lang.String creditControlExternalId;

    private java.lang.String creditControlExternalType;

    private java.lang.String externalField1;

    private java.lang.String externalField2;

    private java.lang.String externalField3;

    private java.lang.String externalField4;

    private java.lang.String externalField5;

    private java.lang.String externalId;

    private boolean local;

    private java.lang.String locationLock;

    private java.lang.String name;

    private java.lang.String password;

    private ArrayList subscriptionIds;

    private com.sun.java.jax_rpc_ri.internal.ArrayList subscriptions;

    public SubscriberAccount() {
    }

    public SubscriberAccount(
           boolean activated,
           com.redback.npm.CircuitAttribute[] circuitAttributes,
           java.util.Calendar creationDate,
           java.lang.String creditControlExternalId,
           java.lang.String creditControlExternalType,
           java.lang.String externalField1,
           java.lang.String externalField2,
           java.lang.String externalField3,
           java.lang.String externalField4,
           java.lang.String externalField5,
           java.lang.String externalId,
           boolean local,
           java.lang.String locationLock,
           java.lang.String name,
           java.lang.String password,
           ArrayList subscriptionIds,
           com.sun.java.jax_rpc_ri.internal.ArrayList subscriptions) {
           this.activated = activated;
           this.circuitAttributes = circuitAttributes;
           this.creationDate = creationDate;
           this.creditControlExternalId = creditControlExternalId;
           this.creditControlExternalType = creditControlExternalType;
           this.externalField1 = externalField1;
           this.externalField2 = externalField2;
           this.externalField3 = externalField3;
           this.externalField4 = externalField4;
           this.externalField5 = externalField5;
           this.externalId = externalId;
           this.local = local;
           this.locationLock = locationLock;
           this.name = name;
           this.password = password;
           this.subscriptionIds = subscriptionIds;
           this.subscriptions = subscriptions;
    }


    /**
     * Gets the activated value for this SubscriberAccount.
     * 
     * @return activated
     */
    public boolean isActivated() {
        return activated;
    }


    /**
     * Sets the activated value for this SubscriberAccount.
     * 
     * @param activated
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }


    /**
     * Gets the circuitAttributes value for this SubscriberAccount.
     * 
     * @return circuitAttributes
     */
    public com.redback.npm.CircuitAttribute[] getCircuitAttributes() {
        return circuitAttributes;
    }


    /**
     * Sets the circuitAttributes value for this SubscriberAccount.
     * 
     * @param circuitAttributes
     */
    public void setCircuitAttributes(com.redback.npm.CircuitAttribute[] circuitAttributes) {
        this.circuitAttributes = circuitAttributes;
    }


    /**
     * Gets the creationDate value for this SubscriberAccount.
     * 
     * @return creationDate
     */
    public java.util.Calendar getCreationDate() {
        return creationDate;
    }


    /**
     * Sets the creationDate value for this SubscriberAccount.
     * 
     * @param creationDate
     */
    public void setCreationDate(java.util.Calendar creationDate) {
        this.creationDate = creationDate;
    }


    /**
     * Gets the creditControlExternalId value for this SubscriberAccount.
     * 
     * @return creditControlExternalId
     */
    public java.lang.String getCreditControlExternalId() {
        return creditControlExternalId;
    }


    /**
     * Sets the creditControlExternalId value for this SubscriberAccount.
     * 
     * @param creditControlExternalId
     */
    public void setCreditControlExternalId(java.lang.String creditControlExternalId) {
        this.creditControlExternalId = creditControlExternalId;
    }


    /**
     * Gets the creditControlExternalType value for this SubscriberAccount.
     * 
     * @return creditControlExternalType
     */
    public java.lang.String getCreditControlExternalType() {
        return creditControlExternalType;
    }


    /**
     * Sets the creditControlExternalType value for this SubscriberAccount.
     * 
     * @param creditControlExternalType
     */
    public void setCreditControlExternalType(java.lang.String creditControlExternalType) {
        this.creditControlExternalType = creditControlExternalType;
    }


    /**
     * Gets the externalField1 value for this SubscriberAccount.
     * 
     * @return externalField1
     */
    public java.lang.String getExternalField1() {
        return externalField1;
    }


    /**
     * Sets the externalField1 value for this SubscriberAccount.
     * 
     * @param externalField1
     */
    public void setExternalField1(java.lang.String externalField1) {
        this.externalField1 = externalField1;
    }


    /**
     * Gets the externalField2 value for this SubscriberAccount.
     * 
     * @return externalField2
     */
    public java.lang.String getExternalField2() {
        return externalField2;
    }


    /**
     * Sets the externalField2 value for this SubscriberAccount.
     * 
     * @param externalField2
     */
    public void setExternalField2(java.lang.String externalField2) {
        this.externalField2 = externalField2;
    }


    /**
     * Gets the externalField3 value for this SubscriberAccount.
     * 
     * @return externalField3
     */
    public java.lang.String getExternalField3() {
        return externalField3;
    }


    /**
     * Sets the externalField3 value for this SubscriberAccount.
     * 
     * @param externalField3
     */
    public void setExternalField3(java.lang.String externalField3) {
        this.externalField3 = externalField3;
    }


    /**
     * Gets the externalField4 value for this SubscriberAccount.
     * 
     * @return externalField4
     */
    public java.lang.String getExternalField4() {
        return externalField4;
    }


    /**
     * Sets the externalField4 value for this SubscriberAccount.
     * 
     * @param externalField4
     */
    public void setExternalField4(java.lang.String externalField4) {
        this.externalField4 = externalField4;
    }


    /**
     * Gets the externalField5 value for this SubscriberAccount.
     * 
     * @return externalField5
     */
    public java.lang.String getExternalField5() {
        return externalField5;
    }


    /**
     * Sets the externalField5 value for this SubscriberAccount.
     * 
     * @param externalField5
     */
    public void setExternalField5(java.lang.String externalField5) {
        this.externalField5 = externalField5;
    }


    /**
     * Gets the externalId value for this SubscriberAccount.
     * 
     * @return externalId
     */
    public java.lang.String getExternalId() {
        return externalId;
    }


    /**
     * Sets the externalId value for this SubscriberAccount.
     * 
     * @param externalId
     */
    public void setExternalId(java.lang.String externalId) {
        this.externalId = externalId;
    }


    /**
     * Gets the local value for this SubscriberAccount.
     * 
     * @return local
     */
    public boolean isLocal() {
        return local;
    }


    /**
     * Sets the local value for this SubscriberAccount.
     * 
     * @param local
     */
    public void setLocal(boolean local) {
        this.local = local;
    }


    /**
     * Gets the locationLock value for this SubscriberAccount.
     * 
     * @return locationLock
     */
    public java.lang.String getLocationLock() {
        return locationLock;
    }


    /**
     * Sets the locationLock value for this SubscriberAccount.
     * 
     * @param locationLock
     */
    public void setLocationLock(java.lang.String locationLock) {
        this.locationLock = locationLock;
    }


    /**
     * Gets the name value for this SubscriberAccount.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this SubscriberAccount.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the password value for this SubscriberAccount.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this SubscriberAccount.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the subscriptionIds value for this SubscriberAccount.
     * 
     * @return subscriptionIds
     */
    public ArrayList getSubscriptionIds() {
        return subscriptionIds;
    }


    /**
     * Sets the subscriptionIds value for this SubscriberAccount.
     * 
     * @param subscriptionIds
     */
    public void setSubscriptionIds(ArrayList subscriptionIds) {
        this.subscriptionIds = subscriptionIds;
    }


    /**
     * Gets the subscriptions value for this SubscriberAccount.
     * 
     * @return subscriptions
     */
    public com.sun.java.jax_rpc_ri.internal.ArrayList getSubscriptions() {
        return subscriptions;
    }


    /**
     * Sets the subscriptions value for this SubscriberAccount.
     * 
     * @param subscriptions
     */
    public void setSubscriptions(com.sun.java.jax_rpc_ri.internal.ArrayList subscriptions) {
        this.subscriptions = subscriptions;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SubscriberAccount)) return false;
        SubscriberAccount other = (SubscriberAccount) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.activated == other.isActivated() &&
            ((this.circuitAttributes==null && other.getCircuitAttributes()==null) || 
             (this.circuitAttributes!=null &&
              java.util.Arrays.equals(this.circuitAttributes, other.getCircuitAttributes()))) &&
            ((this.creationDate==null && other.getCreationDate()==null) || 
             (this.creationDate!=null &&
              this.creationDate.equals(other.getCreationDate()))) &&
            ((this.creditControlExternalId==null && other.getCreditControlExternalId()==null) || 
             (this.creditControlExternalId!=null &&
              this.creditControlExternalId.equals(other.getCreditControlExternalId()))) &&
            ((this.creditControlExternalType==null && other.getCreditControlExternalType()==null) || 
             (this.creditControlExternalType!=null &&
              this.creditControlExternalType.equals(other.getCreditControlExternalType()))) &&
            ((this.externalField1==null && other.getExternalField1()==null) || 
             (this.externalField1!=null &&
              this.externalField1.equals(other.getExternalField1()))) &&
            ((this.externalField2==null && other.getExternalField2()==null) || 
             (this.externalField2!=null &&
              this.externalField2.equals(other.getExternalField2()))) &&
            ((this.externalField3==null && other.getExternalField3()==null) || 
             (this.externalField3!=null &&
              this.externalField3.equals(other.getExternalField3()))) &&
            ((this.externalField4==null && other.getExternalField4()==null) || 
             (this.externalField4!=null &&
              this.externalField4.equals(other.getExternalField4()))) &&
            ((this.externalField5==null && other.getExternalField5()==null) || 
             (this.externalField5!=null &&
              this.externalField5.equals(other.getExternalField5()))) &&
            ((this.externalId==null && other.getExternalId()==null) || 
             (this.externalId!=null &&
              this.externalId.equals(other.getExternalId()))) &&
            this.local == other.isLocal() &&
            ((this.locationLock==null && other.getLocationLock()==null) || 
             (this.locationLock!=null &&
              this.locationLock.equals(other.getLocationLock()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.subscriptionIds==null && other.getSubscriptionIds()==null) || 
             (this.subscriptionIds!=null &&
              this.subscriptionIds.equals(other.getSubscriptionIds()))) &&
            ((this.subscriptions==null && other.getSubscriptions()==null) || 
             (this.subscriptions!=null &&
              this.subscriptions.equals(other.getSubscriptions())));
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
        _hashCode += (isActivated() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getCircuitAttributes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCircuitAttributes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCircuitAttributes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCreationDate() != null) {
            _hashCode += getCreationDate().hashCode();
        }
        if (getCreditControlExternalId() != null) {
            _hashCode += getCreditControlExternalId().hashCode();
        }
        if (getCreditControlExternalType() != null) {
            _hashCode += getCreditControlExternalType().hashCode();
        }
        if (getExternalField1() != null) {
            _hashCode += getExternalField1().hashCode();
        }
        if (getExternalField2() != null) {
            _hashCode += getExternalField2().hashCode();
        }
        if (getExternalField3() != null) {
            _hashCode += getExternalField3().hashCode();
        }
        if (getExternalField4() != null) {
            _hashCode += getExternalField4().hashCode();
        }
        if (getExternalField5() != null) {
            _hashCode += getExternalField5().hashCode();
        }
        if (getExternalId() != null) {
            _hashCode += getExternalId().hashCode();
        }
        _hashCode += (isLocal() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getLocationLock() != null) {
            _hashCode += getLocationLock().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getSubscriptionIds() != null) {
            _hashCode += getSubscriptionIds().hashCode();
        }
        if (getSubscriptions() != null) {
            _hashCode += getSubscriptions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SubscriberAccount.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://npm.redback.com", "SubscriberAccount"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activated");
        elemField.setXmlName(new javax.xml.namespace.QName("", "activated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("circuitAttributes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "circuitAttributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://npm.redback.com", "CircuitAttribute"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creationDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "creationDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditControlExternalId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "creditControlExternalId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditControlExternalType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "creditControlExternalType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalField1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "externalField1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalField2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "externalField2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalField3");
        elemField.setXmlName(new javax.xml.namespace.QName("", "externalField3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalField4");
        elemField.setXmlName(new javax.xml.namespace.QName("", "externalField4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalField5");
        elemField.setXmlName(new javax.xml.namespace.QName("", "externalField5"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "externalId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("local");
        elemField.setXmlName(new javax.xml.namespace.QName("", "local"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("locationLock");
        elemField.setXmlName(new javax.xml.namespace.QName("", "locationLock"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriptionIds");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subscriptionIds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://java.sun.com/jax-rpc-ri/internal", "arrayList"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriptions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subscriptions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://java.sun.com/jax-rpc-ri/internal", "arrayList"));
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
