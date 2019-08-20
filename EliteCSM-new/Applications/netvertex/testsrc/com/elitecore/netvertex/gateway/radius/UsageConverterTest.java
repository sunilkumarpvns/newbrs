package com.elitecore.netvertex.gateway.radius;

import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.netvertex.core.*;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.radius.conf.RadiusListenerConfiguration;
import com.elitecore.netvertex.gateway.radius.scripts.RadiusGroovyScript;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.usagemetering.ServiceUnit;
import com.elitecore.netvertex.usagemetering.UMLevel;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;
import com.elitecore.netvertex.usagemetering.factory.UMInfoFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class UsageConverterTest {
	
	public UsageConverterExt usageConverter;
	public RadiusGatewayControllerContext gatewayControllerContext;
	public PCRFRequest pcrfRequest = new PCRFRequestImpl();
	
	@Before
	public void setUp(){
		final DummyNetvertexServerContextImplExt serverContext = new DummyNetvertexServerContextImplExt();
		
		RadiusListenerConfiguration radiusListenerConfiguration = Mockito.mock(RadiusListenerConfiguration.class);
		
		Mockito.when(radiusListenerConfiguration.getMaximumThread()).thenReturn(1);

		DummyNetvertexServerConfiguration netvertexServerConfiguration = Mockito.mock(DummyNetvertexServerConfiguration.class);
		
		Mockito.when(netvertexServerConfiguration.getRadiusGatewayEventListenerConfiguration()).thenReturn(radiusListenerConfiguration);
		
		serverContext.setNetvertexServerConfiguration(netvertexServerConfiguration);
		
		this.gatewayControllerContext = new RadiusGatewayControllerContext() {
			
			@Override
			public NetVertexServerContext getServerContext() {
				return serverContext;
			}
			
			@Override
			public List<RadiusGroovyScript> getRadiusGroovyScripts(String ipAddress) {
				return null;
			}
			
			@Override
			public RadiusGateway getRadiusGateway(String ipAddress) {
				return null;
			}
			
		};

		MSCC mscc = new MSCC();
		GyServiceUnits serviceUnits = new GyServiceUnits();
		serviceUnits.setVolume(RandomUtils.nextInt(1,Integer.MAX_VALUE));
		serviceUnits.setTime(RandomUtils.nextInt(1,Integer.MAX_VALUE));
		mscc.setUsedServiceUnits(serviceUnits);
		pcrfRequest.setReportedMSCCs(Arrays.asList(mscc));
	}


	public Object[][] data_provider_for_usage_converter_should_provider_usage_after_deducting_previous_usage(){


		final String SESSION_A = "SessionA";
		final String SESSION_B = "SessionB";

		/*
		 * ARGUMENTS :
		 * 			1) Message: print when test cases is fail
		 * 			2) Reported Usage : Usage reported by GATEWAY in Request/Response
		 * 			3) Converted Usage : Converted usage used by PCRFService for Usage Metering
		 */


		Object [][] object = new Object [][] {
				{
						"Usage converter should provide reported usage with 1 in all type of usage",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(201).withTime(401).build()),
						new ServiceUnit.ServiceUnitBuilder().withInputOctets(1).withOutputOctets(1).withTotalOctets(1).withTime(1).build()
				},

				{
						"Usage converter should provide reported usage with input is 1",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(100).withTotalOctets(200).withTime(400).build()),
						new ServiceUnit.ServiceUnitBuilder().withInputOctets(1).withOutputOctets(0).withTotalOctets(0).withTime(0).build()
				},

				{
						"Usage converter should provide reported usage with output is 1",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(101).withTotalOctets(200).withTime(400).build()),
						new ServiceUnit.ServiceUnitBuilder().withInputOctets(0).withOutputOctets(1).withTotalOctets(0).withTime(0).build()
				},
				{
						"Usage converter should provide reported usage with total is 1",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(201).withTime(400).build()),
						new ServiceUnit.ServiceUnitBuilder().withInputOctets(0).withOutputOctets(0).withTotalOctets(1).withTime(0).build()
				},
				{
						"Usage converter should provide reported usage with time is 1",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(401).build()),
						new ServiceUnit.ServiceUnitBuilder().withInputOctets(0).withOutputOctets(0).withTotalOctets(0).withTime(1).build()
				},
		};

		Object [][] finalObject = new Object[object.length][];

		int i = 0;
		for(Object [] innerObject : object){

			Map<String, ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
			previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(400).build());
			previousUsage.put(SESSION_B, new ServiceUnit.ServiceUnitBuilder().withInputOctets(100).withOutputOctets(200).withTotalOctets(300).withTime(500).build());

			Validation<ServiceUnit> validation = new ValidationImpl<ServiceUnit>((String)innerObject[0], (ServiceUnit)innerObject[2]);
			finalObject[i++] = new Object []{
					previousUsage,
					innerObject[1],
					new ValidatorImpl<ServiceUnit>(Arrays.asList(validation))
			};
		}


		return finalObject;
	}
	
	@Test
	@Parameters(method="data_provider_for_usage_converter_should_provider_usage_after_deducting_previous_usage")
	public void test_usage_converter_should_provide_usage_after_deducting_previous_usage(Map<String, ServiceUnit> previousUsage,
																						  UsageMonitoringInfo usageMonitoringInfo,
																						  Validator<ServiceUnit> validator) throws Exception{
		pcrfRequest.setReportedUsageInfoList(Arrays.asList(usageMonitoringInfo));
		pcrfRequest.setPCRFEvents(EnumSet.noneOf(PCRFEvent.class));
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, usageMonitoringInfo.getMonitoringKey());
		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		usageConverter.convert(pcrfRequest);
		validator.validate(pcrfRequest.getReportedUsageInfoList().get(0).getUsedServiceUnit());

		assertThat(pcrfRequest.getReportedUsageInfoList(), hasSameOctects(pcrfRequest.getReportedMSCCs()));
	}

	private TypeSafeDiagnosingMatcher<List<UsageMonitoringInfo>> hasSameOctects(List<MSCC> reportedMSCCs) {
		return new TypeSafeDiagnosingMatcher<List<UsageMonitoringInfo>>() {

			@Override
			public void describeTo(Description description) {
			}

			@Override
			protected boolean matchesSafely(List<UsageMonitoringInfo> reportedUsage,
											Description description) {

				if (reportedMSCCs == null) {
					assertNull(reportedUsage);
					return true;
				}

				assertEquals(reportedUsage.size(), reportedMSCCs.size());

				if (reportedMSCCs.isEmpty()) {
					return true;
				}

				ServiceUnit usage = reportedUsage.get(0).getUsedServiceUnit();
				GyServiceUnits quota = reportedMSCCs.get(0).getUsedServiceUnits();

				assertEquals(usage.getTotalOctets(), quota.getVolume());
				assertEquals(usage.getTime(), quota.getTime());
				return true;
			}
		};
	}


	public Object[][] data_provider_for_usage_converter_remove_usage_If_usage_isNot_Positive() {


		final String SESSION_A = "SessionA";

		/*
		 * ARGUMENTS :
		 * 			1) Message: print when test cases is fail
		 * 			2) Reported Usage : Usage reported by GATEWAY in Request/Response
		 * 			3) Converted Usage : Converted usage used by PCRFService for Usage Metering
		 */


		Object[][] object = new Object[][]{

				{
						"Usage converter should provide reported usage with 0 in all type of usage",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(400).build()),
				},
				{
						"Usage converter should provide reported usage with -1 in all type of usage",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(49).withOutputOctets(99).withTotalOctets(199).withTime(399).build()),
				},
				{
						"Usage converter should provide reported usage with input is -1 in all type of usage",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(49).withOutputOctets(101).withTotalOctets(201).withTime(401).build()),
				},
				{
						"Usage converter should provide reported usage with output is -1 in all type of usage",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(99).withTotalOctets(201).withTime(401).build()),
				},
				{
						"Usage converter should provide reported usage with total is -1 in all type of usage",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(199).withTime(401).build()),
				},
				{
						"Usage converter should provide reported usage with time is -1 in all type of usage",
						UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(201).withTime(399).build()),
				}
		};

		Object[][] finalObject = new Object[object.length][];

		int i = 0;
		for (Object[] innerObject : object) {

			Map<String, ServiceUnit> previousUsage = new HashMap<>();
			previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(400).build());


			finalObject[i++] = new Object[]{
					previousUsage,
					innerObject[1]
			};
		}


		return finalObject;
	}

	@Test
	@Parameters(method = "data_provider_for_usage_converter_remove_usage_If_usage_isNot_Positive")
	public void test_usage_converter_remove_usage_If_usage_isNot_Positive(Map<String, ServiceUnit> previousUsage, UsageMonitoringInfo usageMonitoringInfo) throws Exception {
		pcrfRequest.setReportedUsageInfoList(Arrays.asList(usageMonitoringInfo));
		pcrfRequest.setPCRFEvents(EnumSet.noneOf(PCRFEvent.class));
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, usageMonitoringInfo.getMonitoringKey());
		usageConverter = new UsageConverterExt(gatewayControllerContext, previousUsage);
		usageConverter.convert(pcrfRequest);
		assertNull(pcrfRequest.getReportedUsageInfoList());
		assertNull(pcrfRequest.getReportedMSCCs());
	}
	
	@Test
	public void test_convert_will_consider_reported_usage_as_previous_and_discard_reported_usage_when_previous_usage_not_found() throws Exception {

		Map<String, ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
		String SESSION_A = "sessionA";
		String SESSION_B = "sessionB";

		previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200)
				.withTime(400).build());
		previousUsage.put(SESSION_B, new ServiceUnit.ServiceUnitBuilder().withInputOctets(100).withOutputOctets(200).withTotalOctets(300)
				.withTime(500).build());

		ServiceUnit usedServiceUnit = new ServiceUnit.ServiceUnitBuilder()
				.withInputOctets(49).withOutputOctets(99).withTotalOctets(199).withTime(399).build();
		UsageMonitoringInfo usageMonitoringInfo = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace("NEW SESSION", usedServiceUnit);
		
		pcrfRequest.setReportedUsageInfoList(Arrays.asList(usageMonitoringInfo));
		pcrfRequest.setPCRFEvents(EnumSet.noneOf(PCRFEvent.class));
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, usageMonitoringInfo.getMonitoringKey());
		usageConverter = new UsageConverterExt(gatewayControllerContext, previousUsage);
		usageConverter.convert(pcrfRequest);
		
		assertNull(pcrfRequest.getReportedUsageInfoList());
		assertNull(pcrfRequest.getReportedMSCCs());
		ReflectionAssert.assertReflectionEquals(usedServiceUnit,usageConverter.getPreviousUsageFromCache(usageMonitoringInfo.getMonitoringKey()), ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	public Object[][] data_provider_for_updated_usage_should_be_set_in_CS_USAGE_key(){
	
		
		
		final String SESSION_A = "SessionA";
		final String SESSION_B = "SessionB";
		
		/*
		 * ARGUMENTS : 
		 * 			1) Message: print when test cases is fail
		 * 			2) Reported Usage : Usage reported by GATEWAY in Request/Response
		 * 			3) Converted Usage in JSON : Cumulative usage in JSON Format 
		 * 			4) Previous Usage in JSON : previous usage stored in session as JSON Format 
		 */
		
		Object [][] object = new Object [][] {
				{
					
					"Usage converter should provide reported usage with 1 in all type of usage, use in,out and time usage found in session ",
					new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(152).withTime(402).build(),
					"{\"in\":\"51\",\"out\":\"101\",\"time\":\"402\"}",
					"{\"in\":\"51\",\"out\":\"100\",\"time\":\"401\"}"
					
				},
				
				{
					"Usage converter should provide reported usage with 1 in all type of usage, use time usage found in session, in and out usage is taken from in-memory ",
					new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(152).withTime(402).build(),
					"{\"in\":\"51\",\"out\":\"101\",\"time\":\"402\"}",
					"{\"in\":\"25\",\"out\":\"101\",\"time\":\"401\"}"
				},
				{
					"Usage converter should provide reported usage with 1 in all type of usage, use time usage found in session, in and out usage is taken from in-memory ",
					new ServiceUnit.ServiceUnitBuilder().withInputOctets(152).withTotalOctets(152).withTime(402).build(),
					"{\"in\":\"152\",\"out\":\"0\",\"time\":\"402\"}",
					"{\"in\":\"25\",\"time\":\"400\"}"
				},
				
				{
					"Usage converter should provide reported usage with 1 in all type of usage, use out usage found in session, time usage is taken from in-memory ",
					new ServiceUnit.ServiceUnitBuilder().withOutputOctets(152).withTotalOctets(152).withTime(402).build(),
					"{\"in\":\"0\",\"out\":\"152\",\"time\":\"402\"}",
					"{\"out\":\"151\"}"
				},
				{
					"Usage converter should provide reported usage with 1 in all type of usage, use time usage found in session, in and out usage is taken from in-memory ",
					new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(152).withTime(402).build(),
					"{\"in\":\"51\",\"out\":\"101\",\"time\":\"402\"}",
					"{\"time\":\"401\"}"
				},
		};
		
		Object [][] finalObject = new Object[object.length][];
		
		int i = 0;
		for(Object [] innerObject : object){
			
			Map<String,ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
			previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(150).withTime(400).build());
			previousUsage.put(SESSION_B, new ServiceUnit.ServiceUnitBuilder().withInputOctets(100).withOutputOctets(200).withTotalOctets(300).withTime(500).build());
			
			Validation<String> validation = new ValidationImpl<String>((String)innerObject[0], (String)innerObject[2]);
			finalObject[i++] = new Object []{
				previousUsage,
				innerObject[1],
				(String)innerObject[3],
				new ValidatorImpl<String>(Arrays.asList(validation))
			};
		}
		
		
		
		return finalObject;
	}
	
	
	@Test
	@Parameters(method="data_provider_for_updated_usage_should_be_set_in_CS_USAGE_key")
	public void test_updated_usage_should_be_set_in_CS_USAGE_key(
				Map<String,ServiceUnit> previousUsage,
				ServiceUnit reportedServiceUnit,
				String usageInJson,
				Validator<String> validator) throws Exception{
		
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_SESSION_USAGE.val, usageInJson);
		pcrfRequest.setReportedUsageInfoList(Arrays.asList(UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace("SESSION_A", reportedServiceUnit)));
		pcrfRequest.setPCRFEvents(EnumSet.noneOf(PCRFEvent.class));
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, "SESSION_A");
		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		usageConverter.convert(pcrfRequest);
		validator.validate(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_USAGE.val));
		assertThat(pcrfRequest.getReportedUsageInfoList(), hasSameOctects(pcrfRequest.getReportedMSCCs()));
	}
	
	
	@Test
	public void test_usage_converter_skip_processing_if_core_session_id_not_found(){
		final String SESSION_A = "SessionA";
		final String SESSION_B = "SessionB";
		
		Map<String,ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
		previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(400).build());
		previousUsage.put(SESSION_B, new ServiceUnit.ServiceUnitBuilder().withInputOctets(100).withOutputOctets(200).withTotalOctets(300).withTime(500).build());
		
		pcrfRequest = Mockito.spy(pcrfRequest);
		pcrfRequest.setPCRFEvents(EnumSet.noneOf(PCRFEvent.class));
		
		Mockito.when(pcrfRequest.getReportedUsageInfoList()).thenThrow(new NullPointerException());
		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		usageConverter.convert(pcrfRequest);
		
		Mockito.verify(pcrfRequest,Mockito.times(1)).getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
		Mockito.reset(pcrfRequest);
	}
	
	
	public Object[][] data_provider_for_usage_converter_flush_cached_usage_on_session_stop_and_session_reset(){
		
		
		final String SESSION_A = "SessionA";
		final String SESSION_B = "SessionB";
		
		Map<String,ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
		previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(400).build());
		previousUsage.put(SESSION_B, new ServiceUnit.ServiceUnitBuilder().withInputOctets(100).withOutputOctets(200).withTotalOctets(300).withTime(500).build());
		
		return new Object[][]{
				{
					previousUsage,
					new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(201).withTime(401).build(),
					PCRFEvent.SESSION_STOP,
					SESSION_A
				},
				{
					previousUsage,
					new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(201).withTime(401).build(),
					PCRFEvent.SESSION_STOP,
					SESSION_A
					
				},
				{
					previousUsage,
					new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(201).withTime(401).build(),
					PCRFEvent.SESSION_RESET,
					SESSION_B
				},
				{
					previousUsage,
					new ServiceUnit.ServiceUnitBuilder().withInputOctets(51).withOutputOctets(101).withTotalOctets(201).withTime(401).build(),
					PCRFEvent.SESSION_RESET,
					SESSION_A
				},
				{
					previousUsage,
					null,
					PCRFEvent.SESSION_RESET,
					null
				}
		};
		
	}
	
	@Test
	@Parameters(method="data_provider_for_usage_converter_flush_cached_usage_on_session_stop_and_session_reset")
	public void test_usage_converter_flush_cached_usage_on_session_stop_and_session_reset(
			Map<String,ServiceUnit> previousUsage,
			ServiceUnit usageMonitoringInfo,
			PCRFEvent pcrfEvent,
			String sessionid){
		Set<PCRFEvent> pcrfEvents =  EnumSet.of(pcrfEvent);
		pcrfRequest.setPCRFEvents(pcrfEvents);
		String coreSessionid = sessionid == null ? "sess" : sessionid;
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionid);
		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		usageConverter.convert(pcrfRequest);
		
		Assert.assertNull(usageConverter.getPreviousUsageFromCache(coreSessionid));
	}
	
	
	
	@Test
	@Parameters(value={"null","empty"})
	public void test_convert_when_usage_not_reported_and_previous_usage_found_then_skipp_processing(String option){
		
		final String SESSION_A = "SessionA";
		
		Map<String,ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
		previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(400).build());
		PCRFRequest pcrfRequest = new PCRFRequestImpl();
		pcrfRequest.setPCRFEvents(EnumSet.noneOf(PCRFEvent.class));
		if(option.equals("null")){
			pcrfRequest.setReportedUsageInfoList(null);
			pcrfRequest.setReportedMSCCs(null);
		} else {
			pcrfRequest.setReportedUsageInfoList(Collections.<UsageMonitoringInfo>emptyList());
			pcrfRequest.setReportedMSCCs(Collections.<MSCC>emptyList());
		}
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, SESSION_A);
		
		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		usageConverter.convert(pcrfRequest);
		
		ReflectionAssert.assertLenientEquals(previousUsage.get(SESSION_A), usageConverter.getPreviousUsageFromCache(SESSION_A));
		if (option.equals("null")) {
			assertNull(pcrfRequest.getReportedUsageInfoList());
			assertNull(pcrfRequest.getReportedUsageInfoList());

		} else {
			assertTrue(pcrfRequest.getReportedUsageInfoList().isEmpty());
			assertTrue(pcrfRequest.getReportedMSCCs().isEmpty());
		}
	}
	
	@Test
	@Parameters(value={"null","empty"})
	public void test_convert_when_usage_not_reported_and_previous_usage_not_found_then_zero_usage_should_be_created(String option){
		
		final String SESSION_A = "SessionA";
		
		Map<String,ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
		PCRFRequest pcrfRequest = new PCRFRequestImpl();
		pcrfRequest.setPCRFEvents(EnumSet.noneOf(PCRFEvent.class));
		if(option.equals("null")){
			pcrfRequest.setReportedUsageInfoList(null);
			pcrfRequest.setReportedMSCCs(null);
		} else {
			pcrfRequest.setReportedUsageInfoList(Collections.<UsageMonitoringInfo>emptyList());
			pcrfRequest.setReportedMSCCs(Collections.<MSCC>emptyList());
		}
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, SESSION_A);
		
		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		usageConverter.convert(pcrfRequest);
		
		ServiceUnit expectedUsage = new ServiceUnit();
		
		ReflectionAssert.assertLenientEquals(expectedUsage, usageConverter.getPreviousUsageFromCache(SESSION_A));
		if(option.equals("null")){
			assertNull(pcrfRequest.getReportedUsageInfoList());
			assertNull(pcrfRequest.getReportedMSCCs());
		} else {
			assertTrue(pcrfRequest.getReportedUsageInfoList().isEmpty());
			assertTrue(pcrfRequest.getReportedMSCCs().isEmpty());
		}
	}
	
	@Test
	public void test_usage_converter_flush_the_usage_after_second_call_to_cache_cleaner(){
		
		final String SESSION_A = "SessionA";
		final String SESSION_B = "SessionB";
		
		Map<String,ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
		previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(400).build());
		previousUsage.put(SESSION_B, new ServiceUnit.ServiceUnitBuilder().withInputOctets(100).withOutputOctets(200).withTotalOctets(300).withTime(500).build());

		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		
		((DummyNetvertexServerContextImplExt)gatewayControllerContext.getServerContext()).intervalTask.execute(null);
		for(String monitoringKey : previousUsage.keySet()){
			Assert.assertSame(previousUsage.get(monitoringKey), usageConverter.getPreviousUsageFromCache(monitoringKey));
		}
		
		((DummyNetvertexServerContextImplExt)gatewayControllerContext.getServerContext()).intervalTask.execute(null);
		
		for(String monitoringKey : previousUsage.keySet()){
			Assert.assertNull(usageConverter.getPreviousUsageFromCache(monitoringKey));
		}
		
	}
	
	@Test
	public void test_usage_converter_should_not_update_time_of_cached_UM_info_when_session_start_time_and_used_time_is_zero() throws Exception{
		
		final String SESSION_A = "SessionA";

		
		Map<String, ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
		UsageMonitoringInfo previousUM = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(400).build());
		previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().withInputOctets(50).withOutputOctets(100).withTotalOctets(200).withTime(400).build());
		
		ServiceUnit serviceUnit = new ServiceUnit.ServiceUnitBuilder().build();
		UsageMonitoringInfo usageMonitoringInfo = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, serviceUnit);
		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		
		
		PCRFRequest pcrfRequest = new PCRFRequestImpl();		
		
		pcrfRequest.setPCRFEvents(EnumSet.noneOf(PCRFEvent.class));
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, usageMonitoringInfo.getMonitoringKey());
		pcrfRequest.setReportedUsageInfoList(Arrays.asList(usageMonitoringInfo));
		
		usageConverter.convert(pcrfRequest);

		Validation<ServiceUnit> validation = new ValidationImpl<ServiceUnit>("Usage metering time should be updated to " + 100, new ServiceUnit.ServiceUnitBuilder().withInputOctets(0).withOutputOctets(0).withTotalOctets(0).build());
		validation.validate(serviceUnit);
		
		Validation<ServiceUnit> cachedUMValidation = new ValidationImpl<ServiceUnit>("Cached Usage metering time should be not be updated, value remain at " + previousUM.getUsedServiceUnit().getTime(), new ServiceUnit.ServiceUnitBuilder().withTime(previousUM.getUsedServiceUnit().getTime()).build());
		cachedUMValidation.validate(usageConverter.getPreviousUsageFromCache(usageMonitoringInfo.getMonitoringKey()));
	}
	@Test
	public void test_usage_converter_should_user_session_start_time_when_used_time_is_zero() throws Exception{
		
		final String SESSION_A = "SessionA";

		
		Map<String, ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
		ServiceUnit usedServiceUnit = new ServiceUnit.ServiceUnitBuilder().build();
		UsageMonitoringInfo usageMonitoringInfo = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(SESSION_A, usedServiceUnit);
		previousUsage.put(SESSION_A, new ServiceUnit.ServiceUnitBuilder().build());
		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		
		
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val, usageMonitoringInfo.getMonitoringKey());
		pcrfRequest.setSessionStartTime(new Date(TimeUnit.SECONDS.toMillis(100)));
		pcrfRequest.setPCRFEvents(EnumSet.noneOf(PCRFEvent.class));
		pcrfRequest.setReportedUsageInfoList(Arrays.asList(usageMonitoringInfo));
		
		usageConverter.convert(pcrfRequest);
		
		Validation<ServiceUnit> validation = new ValidationImpl<ServiceUnit>("Usage metering time should be updated to " + pcrfRequest.getSessionStartTime(), new ServiceUnit.ServiceUnitBuilder().withTime(100).build());
		validation.validate(usedServiceUnit);
		validation.validate(usageConverter.getPreviousUsageFromCache(usageMonitoringInfo.getMonitoringKey()));

		assertThat(pcrfRequest.getReportedUsageInfoList(), hasSameOctects(pcrfRequest.getReportedMSCCs()));
	}
	
	@Test
	public void test_usage_converter_should_never_throw_exception(){
		Map<String,ServiceUnit> previousUsage = new HashMap<String, ServiceUnit>();
		PCRFRequest pcrfRequest = Mockito.spy(new PCRFRequestImpl());
		Mockito.when(pcrfRequest.getReportedUsageInfoList()).thenThrow(new NullPointerException());
		Mockito.when(pcrfRequest.getAttribute(Mockito.anyString())).thenThrow(new NullPointerException());
		gatewayControllerContext = Mockito.spy(gatewayControllerContext);
		usageConverter = new UsageConverterExt(gatewayControllerContext,previousUsage);
		usageConverter.convert(pcrfRequest);
	}
	
	private static class UsageConverterExt extends UsageConverter{
		public long currentTime = TimeUnit.SECONDS.toMillis(200);
		public UsageConverterExt(RadiusGatewayControllerContext serverContext,
				Map<String, ServiceUnit> previousUsage) {
			super(serverContext, previousUsage);
		}
		
		public ServiceUnit getPreviousUsageFromCache(String monitoringKey){
			return super.getPreviousUsageFromCache(monitoringKey);
		}
		
		@Override
		public long currentTimeInMillis(){
			return currentTime;
		}
		
	}
	
	private static class DummyNetvertexServerContextImplExt extends DummyNetvertexServerContextImpl{
		public IntervalBasedTask intervalTask = null;
	
	
		
		@Override
		public TaskScheduler getTaskScheduler() {
			return new TaskScheduler() {
				
				@Override
				public Future<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
					return null;
				}
				
				@Override
				public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
					intervalTask = task;
					return null;
				}

				@Override
				public void execute(Runnable command) {
					
				}
			};
		};
		
	}


	public static void main(String args[]) {
		
		List<PkgData> pkgDatas = new ArrayList<PkgData>();
		PkgData pkgData1 = new PkgData();pkgData1.setName("pkg1");
		PkgData pkgData2 = new PkgData();pkgData2.setName("pkg2");
		pkgDatas.add(pkgData1);
		pkgDatas.add(pkgData2);
		
		for (PkgData pkgData : pkgDatas) {
			
			PkgData newPkgData = new PkgData();newPkgData.setName("newPkg");
			pkgData = newPkgData;
			System.out.println(pkgData.getName());
		}
		
		String a = "a";
		String b = "b";
		String c = "c";
		
		a = c;
		System.out.println(a);
		
		
		System.out.println(pkgDatas);
	}
}
