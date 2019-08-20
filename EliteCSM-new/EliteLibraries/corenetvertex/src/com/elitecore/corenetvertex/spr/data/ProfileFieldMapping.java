package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.ArrayList;

public class ProfileFieldMapping implements ToStringable {

    private String fieldForGroupName = null;
    private String fieldForParentId = null;
    private String fieldForSubscriberIdentity = null;
    private String fieldForUserName = null;
    private String fieldForPassword = null;
    private String fieldForEncryptionType = null;
    private String fieldForStatus = null;
    private String fieldForProductOffer = null;
    private String fieldForExpiryDate = null;
    private String fieldForCustomerType = null;
    private String fieldForBillingDate = null;
    private String fieldForParam1 = null;
    private String fieldForParam2 = null;
    private String fieldForParam3 = null;
    private String fieldForParam4 = null;
    private String fieldForParam5 = null;
    private String fieldForArea = null;
    private String fieldForCity = null;
    private String fieldForCountry = null;
    private String fieldForBirthdate = null;
    private String fieldForRole = null;
    private String fieldForCompany = null;
    private String fieldForDepartment = null;
    private String fieldForZone = null;
    private String fieldForCrade = null;
    private String fieldForARPU = null;
    private String fieldForEmail = null;
    private String fieldForPhone = null;
    private String fieldForSIPURL = null;
    private String fieldForCUI = null;

    private String fieldForIMSI = null;
    private String fieldForMSISDN = null;
    private String fieldForIMEI = null;
    private String fieldForMAC = null;
    private String fieldForEUI64 = null;
    private String fieldForModifiedEui64 = null;
    private String fieldForESN = null;
    private String fieldForMEID = null;

    private String fieldForIMSPackage = null;
    private String fieldForSyInterface = null;
    private String fieldForHSQMultiplier = null;
    private String fieldForFUPMultiplier = null;
    private String fieldForPasswordCheck = null;


    private ArrayList<String> strAttributeList = new ArrayList<String>();

    /**
     * This method set logical filed name with database filed name
     *
     * @param logicalName
     * @param fieldName
     */

    public void setFieldMapping(String logicalName, String fieldName) {
        SPRFields sprField = SPRFields.fromSPRFields(logicalName);

        if (sprField == null) {
            return;
        }

        switch (sprField) {
            case SUBSCRIBER_IDENTITY:
                this.fieldForSubscriberIdentity = fieldName;
                break;
            case USERNAME:
                this.fieldForUserName = fieldName;
                break;
            case PASSWORD:
                this.fieldForPassword = fieldName;
                break;
            case CUSTOMER_TYPE:
                this.fieldForCustomerType = fieldName;
                break;
            case STATUS:
                this.fieldForStatus = fieldName;
                break;
            case PRODUCT_OFFER:
                this.fieldForProductOffer = fieldName;
                break;
            case EXPIRY_DATE:
                this.fieldForExpiryDate = fieldName;
                break;
            case BILLING_DATE:
                this.fieldForBillingDate = fieldName;
                break;
            case AREA:
                this.fieldForArea = fieldName;
                break;
            case CITY:
                this.fieldForCity = fieldName;
                break;
            case PARAM1:
                this.fieldForParam1 = fieldName;
                break;
            case PARAM2:
                this.fieldForParam2 = fieldName;
                break;
            case PARAM3:
                this.fieldForParam3 = fieldName;
                break;
            case PARAM4:
                this.fieldForParam4 = fieldName;
                break;
            case PARAM5:
                this.fieldForParam5 = fieldName;
                break;
            case ZONE:
                this.fieldForZone = fieldName;
                break;
            case COUNTRY:
                this.fieldForCountry = fieldName;
                break;
            case BIRTH_DATE:
                this.fieldForBirthdate = fieldName;
                break;
            case ROLE:
                this.fieldForRole = fieldName;
                break;
            case COMPANY:
                this.fieldForCompany = fieldName;
                break;
            case DEPARTMENT:
                this.fieldForDepartment = fieldName;
                break;
            case ARPU:
                this.fieldForARPU = fieldName;
                break;
            case CADRE:
                this.fieldForCrade = fieldName;
                break;
            case EMAIL:
                this.fieldForEmail = fieldName;
                break;
            case PHONE:
                this.fieldForPhone = fieldName;
                break;
            case SIP_URL:
                this.fieldForSIPURL = fieldName;
                break;
            case CUI:
                this.fieldForCUI = fieldName;
                break;
            case IMSI:
                this.fieldForIMSI = fieldName;
                break;
            case MSISDN:
                this.fieldForMSISDN = fieldName;
                break;
            case MAC:
                this.fieldForMAC = fieldName;
                break;
            case EUI64:
                this.fieldForEUI64 = fieldName;
                break;
            case MODIFIED_EUI64:
                this.fieldForModifiedEui64 = fieldName;
                break;
            case ENCRYPTION_TYPE:
                this.fieldForEncryptionType = fieldName;
                break;
            case ESN:
                this.fieldForESN = fieldName;
                break;
            case MEID:
                this.fieldForMEID = fieldName;
                break;
            case PARENT_ID:
                this.fieldForParentId = fieldName;
                break;
            case GROUP_NAME:
                this.fieldForGroupName = fieldName;
                break;
            case IMEI:
                this.fieldForIMEI = fieldName;
                break;
            case SY_INTERFACE:
                this.fieldForSyInterface = fieldName;
                break;
            case IMS_PACKAGE:
                this.fieldForIMSPackage = fieldName;
                break;
            case PASSWORD_CHECK:
                this.fieldForPasswordCheck = fieldName;
                break;
            default:
                break;
        }
        this.strAttributeList.add(fieldName);
    }

    /**
     * This method returns username filed
     *
     * @return String
     */

    public String getFieldForUserName() {
        return fieldForUserName;
    }

    /**
     * This method returns password field
     *
     * @return String
     */
    public String getFieldForPassword() {
        return fieldForPassword;
    }

    public String getFieldForEncryptionType() {
        return fieldForEncryptionType;
    }

    /**
     * This method returns pckage field
     *
     * @return String
     */

    public String getFieldForProductOffer() {
        return fieldForProductOffer;
    }

    /**
     * This method returns CUI field
     *
     * @return String
     */
    public String getFieldForCUI() {
        return fieldForCUI;
    }

    /**
     * This method returns account status field
     *
     * @return String
     */
    public String getFieldForStatus() {
        return fieldForStatus;
    }

    /**
     * This method returns expiry data field
     *
     * @return String
     */
    public String getFieldForExpiryDate() {
        return fieldForExpiryDate;
    }

    /**
     * This method returns billing date field
     *
     * @return String
     */
    public String getFieldForBillingDate() {
        return fieldForBillingDate;
    }

    /**
     * This method returns customer type field
     *
     * @return String
     */
    public String getFieldForCustomerType() {
        return fieldForCustomerType;
    }

    /**
     * This method returns first parameter field
     *
     * @return String
     */
    public String getFieldForParam1() {
        return fieldForParam1;
    }

    /**
     * This method returns second parameter field
     *
     * @return String
     */
    public String getFieldForParam2() {
        return fieldForParam2;
    }

    /**
     * This method returns third parameter field
     *
     * @return String
     */
    public String getFieldForParam3() {
        return fieldForParam3;
    }

    /**
     * This method returns forth parameter field
     *
     * @return String
     */
    public String getFieldForParam4() {
        return fieldForParam4;
    }

    /**
     * This method returns fifth parameter field
     *
     * @return String
     */
    public String getFieldForParam5() {
        return fieldForParam5;
    }

    /**
     * This method returns the attribute list
     *
     * @return
     */
    public ArrayList<String> getAttributeList() {
        return this.strAttributeList;
    }

    public String getFieldForArea() {
        return fieldForArea;
    }

    public String getFieldForCity() {
        return fieldForCity;
    }

    public String getFieldForCountry() {
        return fieldForCountry;
    }

    public String getFieldForBirthdate() {
        return fieldForBirthdate;
    }

    public String getFieldForRole() {
        return fieldForRole;
    }

    public String getFieldForCompany() {
        return fieldForCompany;
    }

    public String getFieldForDepartment() {
        return fieldForDepartment;
    }

    public String getFieldForZone() {
        return fieldForZone;
    }

    public String getFieldForARPU() {
        return fieldForARPU;
    }

    public String getFieldForCadre() {
        return fieldForCrade;
    }

    public String getFieldForEmail() {
        return fieldForEmail;
    }

    public String getFieldForPhone() {
        return fieldForPhone;
    }

    public String getFieldForSIPURL() {
        return fieldForSIPURL;
    }

    public String getFieldForIMSI() {
        return fieldForIMSI;
    }

    public String getFieldForMSISDN() {
        return fieldForMSISDN;
    }

    public String getFieldForIMEI() {
        return fieldForIMEI;
    }

    public String getFieldForMAC() {
        return fieldForMAC;
    }

    public String getFieldForEUI64() {
        return fieldForEUI64;
    }

    public String getFieldForModifiedEui64() {
        return fieldForModifiedEui64;
    }

    public String getFieldForESN() {
        return fieldForESN;
    }

    public String getFieldForMEID() {
        return fieldForMEID;
    }

    public String getFieldForSubscriberIdentity() {
        return fieldForSubscriberIdentity;
    }

    public String getFieldForParentId() {
        return fieldForParentId;
    }

    public String getFieldForGroupName() {
        return fieldForGroupName;
    }


    public String getFieldForIMSPackage() {
        return fieldForIMSPackage;
    }

    public String getFieldForPasswordCheck() {
        return fieldForPasswordCheck;
    }


    public String getFieldForSyInterface() {
        return fieldForSyInterface;
    }

    public String getFieldForHSQMultiplier() {
        return fieldForHSQMultiplier;
    }

    public String getFieldForFUPMultiplier() {
        return fieldForFUPMultiplier;
    }

    public String getFieldMappingForKey(SPRFields key) {

        switch (key) {
            case SUBSCRIBER_IDENTITY:
                return fieldForSubscriberIdentity;
            case USERNAME:
                return fieldForUserName;
            case PASSWORD:
                return fieldForPassword;
            case CUSTOMER_TYPE:
                return fieldForCustomerType;
            case STATUS:
                return fieldForStatus;
            case PRODUCT_OFFER:
                return fieldForProductOffer;
            case EXPIRY_DATE:
                return fieldForExpiryDate;
            case BILLING_DATE:
                return fieldForBillingDate;
            case AREA:
                return fieldForArea;
            case CITY:
                return fieldForCity;
            case PARAM1:
                return fieldForParam1;
            case PARAM2:
                return fieldForParam2;
            case PARAM3:
                return fieldForParam3;
            case PARAM4:
                return fieldForParam4;
            case PARAM5:
                return fieldForParam5;
            case ZONE:
                return fieldForZone;
            case COUNTRY:
                return fieldForCountry;
            case BIRTH_DATE:
                return fieldForBirthdate;
            case ROLE:
                return fieldForRole;
            case COMPANY:
                return fieldForCompany;
            case DEPARTMENT:
                return fieldForDepartment;
            case ARPU:
                return fieldForARPU;
            case CADRE:
                return fieldForCrade;
            case EMAIL:
                return fieldForEmail;
            case PHONE:
                return fieldForPhone;
            case SIP_URL:
                return fieldForSIPURL;
            case CUI:
                return fieldForCUI;
            case IMSI:
                return fieldForIMSI;
            case MSISDN:
                return fieldForMSISDN;
            case MAC:
                return fieldForMAC;
            case EUI64:
                return fieldForEUI64;
            case MODIFIED_EUI64:
                return fieldForModifiedEui64;
            case ENCRYPTION_TYPE:
                return fieldForEncryptionType;
            case ESN:
                return fieldForESN;
            case MEID:
                return fieldForMEID;
            case PARENT_ID:
                return fieldForParentId;
            case GROUP_NAME:
                return fieldForGroupName;
            case IMEI:
                return fieldForIMEI;
            case SY_INTERFACE:
                return fieldForSyInterface;
            case IMS_PACKAGE:
                return fieldForIMSPackage;
            case PASSWORD_CHECK:
                return fieldForPasswordCheck;
            default:
                return null;
        }

    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        for (SPRFields sprField : SPRFields.values()) {
            String fieldMapping = getFieldMappingForKey(sprField);
            if (fieldMapping == null) {
                continue;
            }
            builder.append(sprField.name(), fieldMapping);
        }
    }
}
