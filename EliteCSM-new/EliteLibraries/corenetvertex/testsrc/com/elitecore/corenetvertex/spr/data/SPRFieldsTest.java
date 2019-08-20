package com.elitecore.corenetvertex.spr.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SPRFieldsTest {



    @Test
    public void SPRFields() {

        List sprFieldsList = Arrays.asList(("SUBSCRIBER_IDENTITY,USERNAME,PASSWORD,CUSTOMER_TYPE,STATUS," +
                "PRODUCT_OFFER,IMS_PACKAGE,EXPIRY_DATE,BILLING_DATE,AREA," +
                "CITY,PARAM1,PARAM2,PARAM3,PARAM4,PARAM5,ZONE,COUNTRY,BIRTH_DATE,ROLE," +
                "COMPANY,DEPARTMENT,ARPU,CADRE,EMAIL,PHONE,SIP_URL,CUI,IMSI,MSISDN,MAC," +
                "EUI64,MODIFIED_EUI64,ENCRYPTION_TYPE,ESN,MEID,PARENT_ID,GROUP_NAME,IMEI," +
                "CALLING_STATION_ID,NAS_PORT_ID,FRAMED_IP,SUBSCRIBER_LEVEL_METERING,PASSWORD_CHECK," +
                "SY_INTERFACE,PAYG_INTL_DATA_ROAMING,BILLING_ACCOUNT_ID,SERVICE_INSTANCE_ID," +
                "NEXT_BILL_DATE,BILL_CHANGE_DATE,CREATED_DATE,MODIFIED_DATE").split(","));

        for (SPRFields sprFields : SPRFields.values()) {

            Assert.assertTrue("If Add new field in SPRFields or change the order it will" +
                    "impact the volt",sprFieldsList.contains(sprFields.name()));

        }

    }}