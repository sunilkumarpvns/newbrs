package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.commons.base.TimeRange;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pd.partner.PartnerData;
import com.elitecore.corenetvertex.pd.pbss.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.pbss.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecData;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.DummyOfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.PartnerFactory;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.partner.Partner;
import com.elitecore.netvertex.service.offlinernc.productspec.ProductSpec;
import com.elitecore.netvertex.service.offlinernc.productspec.ProductSpecFactory;
import com.elitecore.netvertex.service.offlinernc.productspec.ProductSpecService;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroup;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroupFactory;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackage;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackageFactory;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class RatingHandlerTest {

	private static final String CALL_SERVICE = "Call";
	private static final String CURRENCY = "INR";
	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	private static final String PACKAGE_NAME = "TestRnCPackage";
	private static final String ACCOUNT_NAME = "Airtel_41";
	private static final String PARTNER_NAME = "Vodafone";
	private static final String SESSION_CONNECT_TIME = "10-04-2018 10:00:00";
	
	private RnCRequest request;
	private RnCResponse response;
	
	private Map<String, RateCardGroup> nameToMockRcg = new HashMap<>();
	private Map<String, RnCPackage> rncPackageNameToRncPackage = new HashMap<>();
	private Map<String, ProductSpec> productSpecNameToProductSpec = new HashMap<>();
	private RnCPackage rncPackage;

	private ProductSpec mockProductSpec;
	private RateCardGroup mockRCG2WithOrder2;
	private RateCardGroup mockRCG1WithOrder1;
	private RateCardGroup mockRCG3WithOrder3;
	private DummyNetvertexServerConfiguration serverConfiguration;
	private DummyOfflineRnCServiceContext serviceContext = new DummyOfflineRnCServiceContext();

	private PartnerData partnerData;
	private ProductSpecData productSpecificationData;
	private LobData lob;
	private Partner partner;
	private RatingHandler handler;

	private RateCardGroupFactory mockRateCardGroupFactory;
	private RnCPackageFactory rncPackageFactory;
	private PartnerFactory partnerFactory;
	private ProductSpecFactory productSpecFactory;
	private ProductSpecService productSpecService;
	private SystemParameterConfiguration spySystemParams;
	private TimeRange timeRange;

	@Before
	public void setUp() throws InitializationFailedException, OfflineRnCException, ParseException {
		serverConfiguration = new DummyNetvertexServerConfiguration();
		spySystemParams = serverConfiguration.spySystemParameterConf();
		Mockito.when(spySystemParams.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);
		Mockito.when(spySystemParams.getRateSelectionWhenDateChange()).thenReturn("SESSION-CONNECT-TIME");

		mockRateCardGroupFactory = spy(
				new RateCardGroupFactory(new RateCardFactory(serviceContext, spySystemParams), spySystemParams));

		RateCardGroupData mockRCG1Data = createMockRCGData("mockRCGData1", 1);
		RateCardGroupData mockRCG2Data = createMockRCGData("mockRCGData2", 2);
		RateCardGroupData mockRCG3Data = createMockRCGData("mockRCGData3", 3);

		mockRCG1WithOrder1 = createMockRCG("RateCardGroupDemo1");
		nameToMockRcg.put(mockRCG1Data.getName(), mockRCG1WithOrder1);
		mockRCG2WithOrder2 = createMockRCG("RateCardGroupDemo2");
		nameToMockRcg.put(mockRCG2Data.getName(), mockRCG2WithOrder2);
		mockRCG3WithOrder3 = createMockRCG("RateCardGroupDemo3");
		nameToMockRcg.put(mockRCG3Data.getName(), mockRCG3WithOrder3);

		doAnswer(new Answer<RateCardGroup>() {
			@Override
			public RateCardGroup answer(InvocationOnMock invocation) throws Throwable {
				return nameToMockRcg.get(invocation.getArgumentAt(0, RateCardGroupData.class).getName());
			}
		}).when(mockRateCardGroupFactory).create(Mockito.any(), Mockito.any(), Mockito.any());

		RncPackageData rncPackageData = new RncPackageData();
		rncPackageData.setName(PACKAGE_NAME);
		rncPackageData.setRateCardGroupData(Arrays.asList(mockRCG1Data, mockRCG2Data, mockRCG3Data));

		rncPackageFactory = spy(new RnCPackageFactory(mockRateCardGroupFactory));
		rncPackage = spy(rncPackageFactory.create(rncPackageData, CURRENCY));
		rncPackageNameToRncPackage.put("RnCPackage-1", rncPackage);
		
		partnerData = new PartnerData();
		productSpecificationData = new ProductSpecData();
		lob = new LobData();

		productSpecificationData.setName("productSpecDataDemo");
		productSpecService = new ProductSpecService(CALL_SERVICE, CALL_SERVICE, rncPackage);
		
		timeRange = TimeRange.open();

		mockProductSpec = createMockProductSpec(productSpecificationData.getName(),productSpecService,timeRange,spySystemParams);
		productSpecNameToProductSpec.put(mockProductSpec.getName(), mockProductSpec);
		
		productSpecFactory = spy(new ProductSpecFactory(rncPackageFactory,spySystemParams));
		partnerFactory = new PartnerFactory(null, productSpecFactory);
		
		doAnswer(new Answer<ProductSpec>() {
			@Override
			public ProductSpec answer(InvocationOnMock invocation) throws Throwable {
				return productSpecNameToProductSpec.get(invocation.getArgumentAt(0, ProductSpecData.class).getName());
			}
		}).when(productSpecFactory).create(Mockito.any());
		
		AccountData accountData = new AccountData();
		accountData.setPartnerData(partnerData);
		accountData.setName(ACCOUNT_NAME);
		accountData.setProductSpecification(productSpecificationData);
		accountData.setLob(lob);

		partnerData.setAccountData(Arrays.asList(accountData));
		partnerData.setName(PARTNER_NAME);
		partnerData.setPartnerLegalName(PARTNER_NAME);

		CountryData countryData = new CountryData();
		countryData.setId("091");
		countryData.setName("India");
		countryData.setCode("91");

		RegionData regionData = new RegionData();
		regionData.setName("South East");
		regionData.setCountryData(countryData);

		CityData cityData = new CityData();
		cityData.setCountryId(countryData.getId());
		cityData.setName("Ahmedabad");
		cityData.setRegionData(regionData);

		partnerData.setCity(cityData);
		partner = partnerFactory.createPartner(partnerData);
		serviceContext.addPartner(partner);
		
		request = new RnCRequest();
		request.setAttribute(OfflineRnCKeyConstants.PARTNER_NAME.getName(), PARTNER_NAME);
		request.setAttribute(OfflineRnCKeyConstants.ACCOUNT_ID.getName(), ACCOUNT_NAME);
		request.setAttribute(OfflineRnCKeyConstants.SERVICE_NAME.getName(), CALL_SERVICE);
		request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SESSION_CONNECT_TIME);
		response = RnCResponse.of(request);
	}
	
	@Test
	public void rncPackageAppliedSuccessfullyWhenAnyOfTheRCGsReturnTrue() throws OfflineRnCException, ParseException {
		doReturn(false).when(mockRCG1WithOrder1).apply(request, response);
		doReturn(false).when(mockRCG2WithOrder2).apply(request, response);
		doReturn(true).when(mockRCG3WithOrder3).apply(request, response);
		
		handler = new RatingHandler(serviceContext);
		handler.handleRequest(request, response, null);
		
		Mockito.verify(rncPackage).apply(Mockito.any(), Mockito.any());
	}
	
	
	public class failsWithInvalidEdrMissingKey {
		
		@Test
		public void partnerNameErrorMessageWhenPartnerNameIsNotThereInRequest() throws OfflineRnCException, ParseException {
			request.setAttribute(OfflineRnCKeyConstants.PARTNER_NAME.getName(), null);
			doReturn(false).when(mockRCG1WithOrder1).apply(request, response);
			doReturn(false).when(mockRCG2WithOrder2).apply(request, response);
			doReturn(true).when(mockRCG3WithOrder3).apply(request, response);
			
			handler = new RatingHandler(serviceContext);
			try {
				handler.handleRequest(request, response, null);
			}
			catch(OfflineRnCException ex) {
				assertThat(ex.getCode(), is(equalTo(OfflineRnCErrorCodes.INVALID_EDR)));
				assertThat(ex.getMessage(), is(equalTo(OfflineRnCErrorMessages.INVALID_EDR.message()+"-"+OfflineRnCErrorMessages.MISSING_KEY+"-"+OfflineRnCKeyConstants.PARTNER_NAME.getName())));
			}
		}
		
		@Test
		public void accountIDErrorMessageWhenAccountIDIsNotThereInRequest() throws OfflineRnCException, ParseException {
			request.setAttribute(OfflineRnCKeyConstants.ACCOUNT_ID.getName(), null);
			doReturn(false).when(mockRCG1WithOrder1).apply(request, response);
			doReturn(false).when(mockRCG2WithOrder2).apply(request, response);
			doReturn(true).when(mockRCG3WithOrder3).apply(request, response);
			handler = new RatingHandler(serviceContext);
			
			try {
				handler.handleRequest(request, response, null);
			}
			catch(OfflineRnCException ex) {
				assertThat(ex.getCode(), is(equalTo(OfflineRnCErrorCodes.INVALID_EDR)));
				assertThat(ex.getMessage(), is(equalTo(OfflineRnCErrorMessages.INVALID_EDR.message()+"-"+OfflineRnCErrorMessages.MISSING_KEY+"-"+OfflineRnCKeyConstants.ACCOUNT_ID.getName())));
			}
		}
		
		@Test
		public void serviceNameErrorMessageWhenServiceIsNotThereInRequest() throws OfflineRnCException, ParseException {
			request.setAttribute(OfflineRnCKeyConstants.SERVICE_NAME.getName(), null);
			doReturn(false).when(mockRCG1WithOrder1).apply(request, response);
			doReturn(false).when(mockRCG2WithOrder2).apply(request, response);
			doReturn(true).when(mockRCG3WithOrder3).apply(request, response);
			
			handler = new RatingHandler(serviceContext);
			try {
				handler.handleRequest(request, response, null);
			}
			catch(OfflineRnCException ex) {
				assertThat(ex.getCode(), is(equalTo(OfflineRnCErrorCodes.INVALID_EDR)));
				assertThat(ex.getMessage(), is(equalTo(OfflineRnCErrorMessages.INVALID_EDR.message()+"-"+OfflineRnCErrorMessages.MISSING_KEY+"-"+OfflineRnCKeyConstants.SERVICE_NAME.getName())));
			}
		}
	}
	
	private ProductSpec createMockProductSpec(String name, ProductSpecService service, TimeRange timeRange, SystemParameterConfiguration spySystemParams) {
		return new ProductSpec(name, Arrays.asList(service), timeRange, spySystemParams);
	}

	private RateCardGroupData createMockRCGData(String name, int orderNo) {
		RateCardGroupData mockRCG1Data = new RateCardGroupData();
		mockRCG1Data.setName(name);
		mockRCG1Data.setOrderNo(orderNo);
		return mockRCG1Data;
	}

	private RateCardGroup createMockRCG(String name) {
		return spy(new RateCardGroup(name, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
	}
}
