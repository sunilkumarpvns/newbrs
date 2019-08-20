package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.EnumMap;

public class SPRTestUtil {
    public static SubscriberProfileData createProfile() {
        return createProfile("Chetan");
    }

    public static SubscriberProfileData createProfile(String subscriberIdentity) {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity(subscriberIdentity)
                .withImsi("1234")
                .withMsisdn("9797979797")
                .withUserName("user1")
                .withPassword("user1")
                .withPhone("123456")
                .build();
    }

    public static SPRInfoImpl createSubscriberProfileWithAllField() throws OperationFailedException {
        SPRInfoImpl s = new SPRInfoImpl();
        s.setSubscriberIdentity("97979797");
        s.setPassword("password");
        s.setEncryptionType("encryptiontype");
        s.setUserName("userName");
        s.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        s.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        s.setParam1("param1");
        s.setParam2("param2");
        s.setParam3("param3");
        s.setParam4("param4");
        s.setParam5("param5");
        s.setNasPortId("NASPORT");
        s.setFramedIp("1.1.1.1");
        s.setCallingStationId("callingstationid");
        s.setSyInterface(true);
        s.setPasswordCheck(true);
        s.setStatus("ACTIVE");
        s.setSubscriberLevelMetering(SPRInfo.SubscriberLevelMetering.ENABLE);
        s.setModifiedEui64("modifiedeui64");
        s.setEui64("eui64");
        s.setMac("mac");
        s.setMeid("meid");
        s.setEsn("esn");
        s.setImei("IMEI");
        s.setMsisdn("97979797");
        s.setImsi("IMSI");
        s.setCui("cui");
        s.setArpu(1L);
        s.setCadre("cadre");
        s.setDepartment("department");
        s.setCompany("company");
        s.setRole("role");
        s.setBirthdate(new Timestamp(System.currentTimeMillis()));
        s.setCountry("country");
        s.setParentId("parentId");
        s.setGroupName("groupname");
        s.setCustomerType("custType");
        s.setBillingDate(10);
        s.setExpiryDate(new Timestamp(new Date(2037,10,10).getTime()));
        s.setProductOffer("volt_base");
        s.setProductOffer("RnCPackage");
        s.setImsPackage("imspackage");
        s.setEmail("a@b.com");
        s.setPhone("97979797");
        s.setSipURL("SIPURL");
        s.setArea("area");
        s.setCity("city");
        s.setZone("zone");

        return s;
    }

    public static EnumMap<SPRFields, String> createSPRFieldMap(SPRInfo sprInfo) {

        EnumMap<SPRFields, String> sprFieldMap = new EnumMap<>(SPRFields.class);

        for (SPRFields sprField : SPRFields.values()) {
            /*
             * Timestamp value is converted to millisecond
             */
            if (Types.TIMESTAMP == sprField.type || Types.NUMERIC == sprField.type) {
                sprFieldMap.put(sprField, sprField.getNumericValue(sprInfo) == null ? null : sprField.getNumericValue(sprInfo) + "");
            } else if (Types.VARCHAR == sprField.type) {
                sprFieldMap.put(sprField, sprField.getStringValue(sprInfo));
            }
        }

        return sprFieldMap;
    }
}
