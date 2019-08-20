package com.elitecore.netvertex.service.offlinernc.productspec;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecServicePkgRelData;
import com.elitecore.corenetvertex.pd.pbss.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecData;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.DummyOfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroupFactory;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackage;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackageFactory;

public class ProductSpecTest {

	private static final String SERVICE1_NAME = "Service1";
	private static final String SERVICE1_RNC_PACKAGE_NAME = "Service1RnCPackage";
	private static final String SERVICE2_NAME = "Service2";
	private static final String SERVICE2_RNC_PACKAGE_NAME = "Service2RnCPackage";
	private static final String UNKNOWN = "Unknown";
	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	private static final String AVAILABLE_START_DATE = "02-02-2015 10:00:00";
	private static final String AVAILABLE_END_DATE = "12-02-2015 10:00:00";
	private static final String SESSION_CONNECT_TIME = "05-02-2015 10:00:00";
	
	private DummyOfflineRnCServiceContext serviceContext;
	private ProductSpecFactory productSpecFactory;
	private ProductSpecData productSpecData;
	private ProductSpec productSpec;
	private RnCPackageFactory rncPackageFactory;
	private RnCPackage rncPackageForService1;
	private RnCPackage rncPackageForService2;
	private RnCRequest request;
	private Map<String, RnCPackage> rncPackageNameToRncPackage = new HashMap<>();
	private DummyNetvertexServerConfiguration serverConfiguration;
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(TIME_FORMAT);
	
	@Before
	public void setUp() throws InitializationFailedException, ParseException {
		serverConfiguration = new DummyNetvertexServerConfiguration();
		SystemParameterConfiguration spySystemParam = serverConfiguration.spySystemParameterConf();
		Mockito.when(spySystemParam.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);
		Mockito.when(spySystemParam.getRateSelectionWhenDateChange()).thenReturn(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName());
		
		serviceContext = new DummyOfflineRnCServiceContext();
		
		rncPackageFactory = spy(new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(serviceContext, spySystemParam),spySystemParam)));
		productSpecFactory = new ProductSpecFactory(rncPackageFactory, spySystemParam);
		
		RncPackageData service1RnCPackageData = createRnCPackageData(SERVICE1_RNC_PACKAGE_NAME);
		rncPackageForService1 = createRnCPackage(service1RnCPackageData);

		RncPackageData service2RnCPackageData = createRnCPackageData(SERVICE2_RNC_PACKAGE_NAME);
		rncPackageForService2 = createRnCPackage(service2RnCPackageData);
		
		doAnswer(new Answer<RnCPackage>() {

			@Override
			public RnCPackage answer(InvocationOnMock invocation) throws Throwable {
				return rncPackageNameToRncPackage.get(invocation.getArgumentAt(0, RncPackageData.class).getName());
			}
		}).when(rncPackageFactory).create(Mockito.any(), Mockito.any());
		
		productSpecData = new ProductSpecData();
		productSpecData.setName("TestProductSpec");
		
		Date date = simpleDateFormatThreadLocal.get().parse(AVAILABLE_START_DATE);
		Timestamp availableStartDate = new Timestamp(date.getTime());
		productSpecData.setAvailabilityStartDate(availableStartDate);
		
		addServicePackageRelation(SERVICE1_NAME, service1RnCPackageData);
		addServicePackageRelation(SERVICE2_NAME, service2RnCPackageData);
		
		request = new RnCRequest();
		productSpec = productSpecFactory.create(productSpecData);
		
	}

	@Test
	public void selectsPackageBasedOnServiceName() throws Exception {
		
		request.setAttribute(OfflineRnCKeyConstants.SERVICE_NAME.getName(), SERVICE1_NAME);

		RnCPackage selectedPackage = productSpec.selectPackage(request);
		
		System.out.println(request.getTrace());
		
		assertThat(selectedPackage, is(sameInstance(rncPackageForService1)));
		
		request.setAttribute(OfflineRnCKeyConstants.SERVICE_NAME.getName(), SERVICE2_NAME);
		
		selectedPackage = productSpec.selectPackage(request);
		
		System.out.println(request.getTrace());
		
		assertThat(selectedPackage, is(sameInstance(rncPackageForService2)));
	}
	
	@Test
	public void failsWithPackageNotFoundErrorIfNoPackageForRequestedServiceIsFound() throws Exception {
		
		request.setAttribute(OfflineRnCKeyConstants.SERVICE_NAME.getName(), UNKNOWN);
		
		try {
			productSpec.selectPackage(request);
			
			fail("Expected to throw exception but didn't");
		} catch (OfflineRnCException ex) {
			assertThat(ex.getCode(), is(equalTo(OfflineRnCErrorCodes.PACKAGE_NOT_FOUND)));
			assertThat(ex.getMessage(), is(equalTo(OfflineRnCErrorMessages.PACKAGE_NOT_FOUND.message())));
		}
	}
	
	@Test
	public void bothDatesAreOptionalThenProductSpecIsEligible() throws Exception {
		
		productSpecData.setAvailabilityStartDate(null);
		productSpecData.setAvailabilityEndDate(null);
		productSpecData.setName("ProductSpec");
		productSpec = productSpecFactory.create(productSpecData);
		
		request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SESSION_CONNECT_TIME);
		boolean valid = productSpec.isEligible(request);
		assertTrue(valid);
	}
	
	@Test
	public void givenSessionConnectTimeSameAsStartDateThenProductSpecIsEligible() throws Exception {
		Date date = simpleDateFormatThreadLocal.get().parse(SESSION_CONNECT_TIME);
		Timestamp availableStartDate = new Timestamp(date.getTime());
		productSpecData.setAvailabilityStartDate(availableStartDate);
		productSpecData.setAvailabilityEndDate(null);
		productSpecData.setName("ProductSpec");
		productSpec = productSpecFactory.create(productSpecData);
		
		request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SESSION_CONNECT_TIME);
		boolean valid = productSpec.isEligible(request);
		assertTrue(valid);
	}
	
	@Test
	public void givenSessionConnectTimeAfterStartDateThenProductSpecIsEligible() throws Exception {
		
		productSpecData.setAvailabilityEndDate(null);
		productSpecData.setName("ProductSpec");
		productSpec = productSpecFactory.create(productSpecData);
		
		request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SESSION_CONNECT_TIME);
		boolean valid = productSpec.isEligible(request);
		assertTrue(valid);
	}
	
	@Test
	public void givenSessionConnectTimeIsBetweenStartDateAndEndDateThenProductSpecIsEligible() throws Exception {
		Date endDate = simpleDateFormatThreadLocal.get().parse(AVAILABLE_END_DATE);
		Timestamp availableEndDate = new Timestamp(endDate.getTime());
		productSpecData.setAvailabilityEndDate(availableEndDate);
		productSpecData.setName("ProductSpec");
		
		productSpec = productSpecFactory.create(productSpecData);
		
		request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SESSION_CONNECT_TIME);
		boolean valid = productSpec.isEligible(request);
		assertTrue(valid);
	}
	
	@Test
	public void givenSessionConnectTimeBeforeStartDateThenProductSpecIsNotEligible() throws Exception {
		String SessionStartDate = "01-02-2015 10:00:00";
		
		productSpecData.setAvailabilityEndDate(null);
		productSpecData.setName("ProductSpec");
		
		productSpec = productSpecFactory.create(productSpecData);
		
		request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SessionStartDate);
		boolean valid = productSpec.isEligible(request);
		assertFalse(valid);
	}
	
	private void addServicePackageRelation(String serviceName, RncPackageData rncPackageData) {
		ServiceData service1Data = new ServiceData();
		service1Data.setName(serviceName);
		service1Data.setId(serviceName);
		ProductSpecServicePkgRelData productSpecServicePkgRelData = new ProductSpecServicePkgRelData();
		productSpecServicePkgRelData.setServiceData(service1Data);
		productSpecServicePkgRelData.setRncPackageData(rncPackageData);
		productSpecData.getProductOfferServicePkgRelDataList().add(productSpecServicePkgRelData);
	}

	private RnCPackage createRnCPackage(RncPackageData packageData) {
		RnCPackage rncPackage = spy(new RnCPackage(packageData.getName(), null));
		rncPackageNameToRncPackage.put(packageData.getName(), rncPackage);
		return rncPackage;
	}

	private RncPackageData createRnCPackageData(String name) {
		RncPackageData service1RnCPackageData = new RncPackageData();
		service1RnCPackageData.setName(name);
		return service1RnCPackageData;
	}
}
