package com.elitecore.netvertex.core.servicepolicy;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfo.SubscriberLevelMetering;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

@RunWith(JUnitParamsRunner.class)
public class ExecutionContextGetSPRTest {


    private static final String SUBSCRIBER_IDENTITY_VAL = "1020";
    public static final String INR = "INR";
    @Mock private CacheAwareDDFTable ddfTable;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	public Object[][] dataProvider_removeCacheWhenTerminateRequestArrivesAndAuthenticationNotOccur() {
		return new Object[][] {
				{
					Arrays.asList(PCRFEvent.SESSION_STOP)
				},
				{
					Arrays.asList(PCRFEvent.USAGE_REPORT, PCRFEvent.SESSION_STOP)
				}
		};
	}

	@Test
	@Parameters(method="dataProvider_removeCacheWhenTerminateRequestArrivesAndAuthenticationNotOccur")
	public void removeCacheWhenTerminateRequestArrivesAndAuthenticationNotOccur(
			List<PCRFEvent> events) throws Exception {
		
		PCRFRequest pcrfRequest = createPCRFRequest(SUBSCRIBER_IDENTITY_VAL,events);
		
		ExecutionContext executionContext = new ExecutionContext( pcrfRequest, mock(PCRFResponse.class), ddfTable, INR);
		String subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

		when(ddfTable.getProfile(pcrfRequest)).thenReturn(getExpectedSPRInfo());
		executionContext.getSPR();
		
	}
	
	public static @Nonnull PCRFRequest createPCRFRequest(String userIdentity, List<PCRFEvent> events) {
		PCRFRequestImpl request = new PCRFRequestImpl();
		request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, userIdentity);
		request.setSessionFound(true);
		Set<PCRFEvent> pcrfEvents = new HashSet<PCRFEvent>(2,1);
		pcrfEvents.addAll(events);
		request.setPCRFEvents(pcrfEvents);
		return request;
	}
	
	private @Nonnull SPRInfoImpl getExpectedSPRInfo() {
		SPRInfoImpl sprInfo = new SPRInfoImpl();
		sprInfo.setUserName("user2");
		sprInfo.setPassword("user2");
		sprInfo.setImei("12345");
		sprInfo.setSubscriberIdentity(SUBSCRIBER_IDENTITY_VAL);
		sprInfo.setMsisdn("9797979798");
		sprInfo.setPhone("123456");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2030);
		sprInfo.setExpiryDate(new Timestamp(calendar.getTime().getTime()));
		return sprInfo; 
	}
	
	private Object[][] dataProvider_for_setProfileRelatedAttributeToPCRFRequestAndResponse() {
		Timestamp birthdate = new Timestamp(System.currentTimeMillis());
		Timestamp expiryDate = new Timestamp(System.currentTimeMillis() + 10000);
		return new Object[][] {
				
				$(
					new SPRInfoImpl.SPRInfoBuilder().
					withArea("ahm").withArpu(1l).withBillingDate(10).withBirthdate(birthdate)
					.withCadre("1").withCity("ahmedabad").withCompany("elitecore").withCountry("india").withCui("100")
					.withCustomerType("pospaid").withDepartment("CSM").withEmail("abc@elitecore.com").withEncryptionType("hash")
					.withESN("1").withEUI64("10").withExpiryDate(expiryDate).withGroupName("CSM").withImei("9").withImsi("1")
					.withMac("1").withMEID("1").withModifiedEUI64("1").withMsisdn("1").withParam1("param1").withParam2("param1")
					.withParam3("param1").withParam4("param1").withParam5("param1").withParentId("1").withPassword("1").withPhone("1")
					.withRole("software engineer").withSipURL("1").withSubscriberIdentity(SUBSCRIBER_IDENTITY_VAL)
					.withProductOffer("package").withSubscriberLevelMetering(SubscriberLevelMetering.ENABLE).withUserName("avc").withZone("ahmedabad").build()
				),
				
				$(
					new SPRInfoImpl.SPRInfoBuilder().
					withArea("ahm").withArpu(1l).withBillingDate(10).withBirthdate(birthdate)
					.withCadre("1").withCity("ahmedabad").withCompany("elitecore").withCountry("india").withCui("100")
					.withCustomerType("pospaid").withDepartment("CSM").withEmail("abc@elitecore.com").withEncryptionType("hash")
					.withESN("1").withEUI64("10").withExpiryDate(expiryDate).withGroupName("CSM").withImei("9").withImsi("1")
					.withMac("1").withMEID("1").withModifiedEUI64("1").withMsisdn("1").withParam1("param1").withParam2("param1")
					.withParam3("param1").withParam4("param1").withParam5("param1").withParentId("1").withPassword("1").withPhone("1")
					.withRole("software engineer").withSipURL("1").withSubscriberIdentity(SUBSCRIBER_IDENTITY_VAL)
					.withProductOffer("package").withSubscriberLevelMetering(SubscriberLevelMetering.ENABLE).withUserName("avc").withZone("ahmedabad").build()
				),
				
				$(
					new SPRInfoImpl.SPRInfoBuilder().
					withArea("ahm").withArpu(1l).withBillingDate(10).withBirthdate(birthdate)
					.withCadre("1").withCity("ahmedabad").withCompany("elitecore").withCountry("india").withCui("100")
					.withDepartment("CSM").withEmail("abc@elitecore.com").withEncryptionType("hash")
					.withESN("1").withEUI64("10").withExpiryDate(expiryDate).withGroupName("CSM").withImei("9").withImsi("1")
					.withParam3("param1").withParam4("param1").withParam5("param1").withParentId("1").withPassword("1").withPhone("1")
					.withRole("software engineer").withSipURL("1").withSubscriberIdentity(SUBSCRIBER_IDENTITY_VAL)
					.withProductOffer("package").withSubscriberLevelMetering(SubscriberLevelMetering.ENABLE).withUserName("avc").withZone("ahmedabad").build()
				),
				
				$(
					new SPRInfoImpl.SPRInfoBuilder().
					withArea("ahm").withArpu(1l).withBillingDate(10).withBirthdate(birthdate)
					.withCadre("1").withCity("ahmedabad").withCompany("elitecore").withCountry("india").withCui("100")
					.withRole("software engineer").withSipURL("1").withSubscriberIdentity(SUBSCRIBER_IDENTITY_VAL)
					.withProductOffer("package").withSubscriberLevelMetering(SubscriberLevelMetering.ENABLE).withUserName("avc").withZone("ahmedabad").build()
				)
				
				
		};
	};
	
	@Parameters(method = "dataProvider_for_setProfileRelatedAttributeToPCRFRequestAndResponse")
	@Test
	public void setProfileRelatedAttributeToPCRFRequestAndResponse(SPRInfoImpl sprInfo) throws Exception {
		
		PCRFRequest pcrfRequest = createPCRFRequest(SUBSCRIBER_IDENTITY_VAL, Arrays.asList(PCRFEvent.AUTHENTICATE, PCRFEvent.SESSION_UPDATE));
		
		sprInfo.setSubscriberIdentity(SUBSCRIBER_IDENTITY_VAL);
		when(ddfTable.getProfile(pcrfRequest)).thenReturn(sprInfo);
		PCRFResponse pcrfResponse = new PCRFResponseImpl();

		ExecutionContext executionContext = new ExecutionContext( pcrfRequest, pcrfResponse, ddfTable, INR);
		
		executionContext.getSPR();
		
		for(SPRFields sprField : SPRFields.values()){
			
			if(sprField.pcrfKey != null) {
				assertEquals(sprField.getStringValue(sprInfo), pcrfRequest.getAttribute(sprField.pcrfKey.val));
				assertEquals(sprField.getStringValue(sprInfo), pcrfResponse.getAttribute(sprField.pcrfKey.val));				
			}
		}
	}
	
	@Test
	public void NotFetchProfileFromRepositoryIfExistInRequest() throws Exception {
		PCRFRequest pcrfRequest = createPCRFRequest(SUBSCRIBER_IDENTITY_VAL, Arrays.asList(PCRFEvent.AUTHENTICATE, PCRFEvent.SESSION_START));
		PCRFResponse pcrfResponse = new PCRFResponseImpl();
		
		SPRInfo expectedSPR = getExpectedSPRInfo();
		
		pcrfRequest.setSPRInfo(expectedSPR);
		
		ExecutionContext executionContext = new ExecutionContext( pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR);
		
		SPRInfo actualSPR = executionContext.getSPR();
		
		assertReflectionEquals(expectedSPR, actualSPR, ReflectionComparatorMode.LENIENT_ORDER);
		verifyZeroInteractions(ddfTable);
	}

	
	@Test
	public void getProfileFromRepositoryIfNotFoundFromRequest() throws Exception {
		SPRInfo expectedProfile = getExpectedSPRInfo();
		PCRFRequestImpl request = new PCRFRequestImpl();
		when(ddfTable.getProfile(request)).thenReturn(expectedProfile);
		ExecutionContext executionContext = new ExecutionContext(request, mock(PCRFResponse.class), ddfTable, INR);
		
		SPRInfo actualProfile = executionContext.getSPR();
		
		verify(ddfTable, times(1)).getProfile(request);
		
		assertReflectionEquals(expectedProfile, actualProfile, ReflectionComparatorMode.LENIENT_ORDER);
	}



}

