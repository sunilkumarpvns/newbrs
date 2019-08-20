package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl.SPRInfoBuilder;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import static org.junit.Assert.assertEquals;

public class SPRInfoBuilderTest {

    
    @Test
    public void test_builder_should_give_SPRInfo_with_default_value_when_value_not_provided() throws Exception {

        SPRInfo actual = new SPRInfoBuilder().build();


        SPRInfoImpl expected = new SPRInfoImpl();
        expected.setStatus(SubscriberStatus.ACTIVE.name());
        ReflectionAssert.assertReflectionEquals(expected, actual);
    }

    
    @Test
    public void test_builder_should_build_SPRInfo_with_provided_value() {
	
	//input data
	SubscriberProfileData data = getProfileData();

	SPRInfo info = new SPRInfoBuilder()
		.withSubscriberIdentity(data.getSubscriberIdentity())
		.withUserName(data.getUserName())
		.withPassword(data.getPassword())
		.withCustomerType(data.getCustomerType())
		.withProductOffer(data.getSubscriberPackage())
		.withExpiryDate(data.getExpiryDate())
		.withArea(data.getArea())
		.withCity(data.getCity())
		.withParam1(data.getParam1())
		.withParam2(data.getParam2())
		.withParam3(data.getParam3())
		.withParam4(data.getParam4())
		.withParam5(data.getParam5())
		.withZone(data.getZone())
		.withCountry(data.getCountry())
		.withBirthdate(data.getBirthdate())
		.withRole(data.getRole())
		.withCompany(data.getCompany())
		.withDepartment(data.getDepartment())
		.withCadre(data.getCadre())
		.withEmail(data.getEmail())
		.withPhone(data.getPhone())
		.withSipURL(data.getSipURL())
		.withCui(data.getCui())
		.withImsi(data.getImsi())
		.withMsisdn(data.getMsisdn())
		.withImei(data.getImei())
		.withMac(data.getMac())
		.withEUI64(data.geteUI64())
		.withModifiedEUI64(data.getmODIFIED_EUI64())
		.withESN(data.geteSN())
		.withMEID(data.getmEID())
		.withParentId(data.getParentId())
		.withGroupName(data.getGroupName())
		.withEncryptionType(data.getEncryptionType())
		.build();

	assertEquals(-1, info.getAge());
	assertEquals(null, info.getArpu());
	assertEquals(null, info.getBillingDate());
	assertEquals(data.getSubscriberIdentity() , info.getSubscriberIdentity());
	assertEquals(data.getUserName(), info.getUserName());
	assertEquals(data.getPassword(), info.getPassword());
	assertEquals(data.getCustomerType(), info.getCustomerType());
	assertEquals(data.getStatus(), info.getStatus());
	assertEquals(data.getSubscriberPackage(), info.getProductOffer());
	assertEquals(data.getExpiryDate(), info.getExpiryDate());
	assertEquals(data.getArea(), info.getArea());
	assertEquals(data.getCity(), info.getCity());
	assertEquals(data.getParam1(), info.getParam1());
	assertEquals(data.getParam2(), info.getParam2());
	assertEquals(data.getParam3(), info.getParam3());
	assertEquals(data.getParam4(), info.getParam4());
	assertEquals(data.getParam5(), info.getParam5());
	assertEquals(data.getZone(), info.getZone());
	assertEquals(data.getCountry(), info.getCountry());
	assertEquals(data.getBirthdate(), info.getBirthdate());
	assertEquals(data.getRole(), info.getRole());
	assertEquals(data.getCompany(), info.getCompany());
	assertEquals(data.getDepartment(), info.getDepartment());
	assertEquals(data.getCadre(), info.getCadre());
	assertEquals(data.getEmail(), info.getEmail());
	assertEquals(data.getPhone(), info.getPhone());
	assertEquals(data.getSipURL(), info.getSipURL());
	assertEquals(data.getCui(), info.getCui());
	assertEquals(data.getImsi(), info.getImsi());
	assertEquals(data.getMsisdn(), info.getMsisdn());
	assertEquals(data.getImei(), info.getImei());
	assertEquals(data.getMac(), info.getMac());
	assertEquals(data.geteUI64(), info.getEui64());
	assertEquals(data.getmODIFIED_EUI64(), info.getModifiedEui64());
	assertEquals(data.geteSN(), info.getEsn());
	assertEquals(data.getmEID(), info.getMeid());
	assertEquals(data.getParentId(), info.getParentId());
	assertEquals(data.getGroupName(), info.getGroupName());
	assertEquals(data.getEncryptionType(), info.getEncryptionType());
    }

    private SubscriberProfileData getProfileData() {
	return new SubscriberProfileData.SubscriberProfileDataBuilder()
		.withSubscriberIdentity("101")
		.withImsi("1234")
		.withMsisdn("9797979797")
		.withUserName("user1")
		.withPassword("user1")
		.withPhone("123456")
		.withSubscriberPackage("package1")
		.build();
    }
}
