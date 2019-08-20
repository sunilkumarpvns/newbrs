package com.elitecore.corenetvertex.spr.util;

import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;

import java.sql.Timestamp;
import java.util.EnumMap;

import static java.util.Objects.nonNull;

public class SPRInfoUtil {

    public static SPRInfoImpl createSPRInfoImpl(EnumMap<SPRFields, String> updatedProfile,String subscriberIdentity){
        SPRInfoImpl sprInfo = new SPRInfoImpl();

        if(nonNull(subscriberIdentity)){
            sprInfo.setSubscriberIdentity(subscriberIdentity);
        }else{
            sprInfo.setSubscriberIdentity(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.USERNAME))){
            sprInfo.setUserName(updatedProfile.get(SPRFields.USERNAME));
        }else{
            sprInfo.setUserName(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.SERVICE_INSTANCE_ID))){
            sprInfo.setServiceInstanceId(updatedProfile.get(SPRFields.SERVICE_INSTANCE_ID));
        }else{
            sprInfo.setServiceInstanceId(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.CUSTOMER_TYPE))){
            sprInfo.setCustomerType(updatedProfile.get(SPRFields.CUSTOMER_TYPE));
        }else{
            sprInfo.setCustomerType(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.BILLING_DATE))){
            sprInfo.setBillingDate(Integer.parseInt(updatedProfile.get(SPRFields.BILLING_DATE)));
        }else{
            sprInfo.setBillingDate(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.EXPIRY_DATE))){
            sprInfo.setExpiryDate(new Timestamp(Long.parseLong(updatedProfile.get(SPRFields.EXPIRY_DATE))));
        }else{
            sprInfo.setExpiryDate(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.PRODUCT_OFFER))){
            sprInfo.setProductOffer(updatedProfile.get(SPRFields.PRODUCT_OFFER));
        }else{
            sprInfo.setProductOffer(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.IMSI))){
            sprInfo.setImsi(updatedProfile.get(SPRFields.IMSI));
        }else{
            sprInfo.setImsi(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.MSISDN))){
            sprInfo.setMsisdn(updatedProfile.get(SPRFields.MSISDN));
        }else{
            sprInfo.setMsisdn(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.IMEI))){
            sprInfo.setImei(updatedProfile.get(SPRFields.IMEI));
        }else{
            sprInfo.setImei(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.STATUS))){
            sprInfo.setStatus(updatedProfile.get(SPRFields.STATUS));
        }else{
            sprInfo.setStatus(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.CREATED_DATE))){
            sprInfo.setCreatedDate(new Timestamp(Long.parseLong(updatedProfile.get(SPRFields.CREATED_DATE))));
        }else{
            sprInfo.setCreatedDate(null);
        }

        if(nonNull(updatedProfile.get(SPRFields.MODIFIED_DATE))){
            sprInfo.setModifiedDate(new Timestamp(Long.parseLong(updatedProfile.get(SPRFields.MODIFIED_DATE))));
        }else{
            sprInfo.setModifiedDate(null);
        }

        return sprInfo;
    }
}
