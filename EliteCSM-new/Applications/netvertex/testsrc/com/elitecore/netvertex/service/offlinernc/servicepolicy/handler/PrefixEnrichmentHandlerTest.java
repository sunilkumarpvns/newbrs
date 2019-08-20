package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pd.account.AccountPrefixMasterRelationData;
import com.elitecore.corenetvertex.pd.account.PrefixListMasterData;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pd.partner.PartnerData;
import com.elitecore.corenetvertex.pd.prefixes.PrefixesData;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecData;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.DummyOfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.OfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.PartnerFactory;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchange;
import com.elitecore.netvertex.service.offlinernc.partner.Partner;
import com.elitecore.netvertex.service.offlinernc.prefix.conf.PrefixConfiguration;
import com.elitecore.netvertex.service.offlinernc.productspec.ProductSpecFactory;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroupFactory;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackageFactory;
import com.elitecore.netvertex.service.offlinernc.util.BigDecimalFormatter;

public class PrefixEnrichmentHandlerTest {
	
	private static final String PARTNER_NAME = "Vodafone";

	private static final String ACCOUNT_NAME = "Airtel_41";

	private static final String UNKNOWN_NUMBER = "9112312";
	
	private static final String CALLED_STATION_ID_1 = "918128961877";
	private static final String CALLING_STATION_ID_1 = "91432121212";
	
	private static final String CALLED_STATION_ID_2 = "91822323232";
	private static final String CALLING_STATION_ID_2 = "9144212122";
	
	private static final String CALLED_NAME_DATA_1 = "India";
	private static final String CALLED_PREFIX_DATA_1 = "9181";
	private static final Integer CALLED_COUNTRY_DATA_1 = 91;
	private static final Integer CALLED_AREA_DATA_1 = 81;

	private static final String CALLING_NAME_DATA_1 = "United Kingdom";
	private static final String CALLING_PREFIX_DATA_1 = "9143";
	private static final Integer CALLING_COUNTRY_DATA_1 = 91;
	private static final Integer CALLING_AREA_DATA_1 = 43;

	private static final String CALLED_NAME_DATA_2 = "Rusia";
	private static final String CALLED_PREFIX_DATA_2 = "9182";
	private static final Integer CALLED_COUNTRY_DATA_2 = 91;
	private static final Integer CALLED_AREA_DATA_2 = 82;

	private static final String CALLING_NAME_DATA_2 = "USA";
	private static final String CALLING_PREFIX_DATA_2 = "9144";
	private static final Integer CALLING_COUNTRY_DATA_2 = 91;
	private static final Integer CALLING_AREA_DATA_2 = 44;
	
	private PrefixEnrichmentHandler prefixEnrichmentHandler;
	private RnCRequest request;
	private PrefixConfiguration calledPrefixGlobalConfiguration;
	private PrefixConfiguration callingPrefixGlobalConfiguration;
	private List<PrefixConfiguration> prefixConfigurations;
	private RnCResponse response;
	private PacketOutputStream out;
	
	@Rule
	public PrintMethodRule printMethodRule = new PrintMethodRule();
	
	private PartnerFactory partnerFactory;
	private PartnerData partnerData;
	private AccountData accountData;
	private AccountPrefixMasterRelationData accountPrefixMasterRelationData;
	private PrefixListMasterData prefixListMasterData;
	private List<PrefixesData> prefixesList;
	private PrefixesData calledPrefixAcctLevelData;
	private PrefixesData callingPrefixAcctLevelData;
	private ProductSpecData productSpecification;
	private LobData lob;
	private CityData cityData;
	private RegionData regionData;
	private CountryData countryData;
	private OfflineRnCServiceContext serviceContext;
	private DummyNetvertexServerConfiguration serverConfiguration;
	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	
	
	@Before
	public void setUp() throws InitializationFailedException {
		
		serverConfiguration = new DummyNetvertexServerConfiguration();
		SystemParameterConfiguration spySystemParams = serverConfiguration.spySystemParameterConf();
		Mockito.when(spySystemParams.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);
		
		partnerFactory = new PartnerFactory(null, new ProductSpecFactory(new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new DummyOfflineRnCServiceContext(), spySystemParams), spySystemParams)), spySystemParams));

		partnerData =  new PartnerData();
		accountData = new AccountData();
		prefixListMasterData = new PrefixListMasterData();
		prefixesList = new ArrayList<>();
		calledPrefixAcctLevelData = new PrefixesData();
		callingPrefixAcctLevelData = new PrefixesData();
		accountPrefixMasterRelationData = new AccountPrefixMasterRelationData();
		productSpecification = new ProductSpecData();
		
		lob = new LobData();
		cityData = new CityData();
		regionData = new RegionData();
		countryData = new CountryData();
		
		calledPrefixAcctLevelData.setCountryCode(CALLED_COUNTRY_DATA_2);
		calledPrefixAcctLevelData.setAreaCode(CALLED_AREA_DATA_2);
		calledPrefixAcctLevelData.setPrefix(CALLED_PREFIX_DATA_2);
		calledPrefixAcctLevelData.setName(CALLED_NAME_DATA_2);
		
		callingPrefixAcctLevelData.setCountryCode(CALLING_COUNTRY_DATA_2);
		callingPrefixAcctLevelData.setAreaCode(CALLING_AREA_DATA_2);
		callingPrefixAcctLevelData.setPrefix(CALLING_PREFIX_DATA_2);
		callingPrefixAcctLevelData.setName(CALLING_NAME_DATA_2);
		
		prefixesList.add(calledPrefixAcctLevelData);
		prefixesList.add(callingPrefixAcctLevelData);
		
		prefixListMasterData.setPrefixesList(prefixesList);
		
		accountPrefixMasterRelationData.setPrefixListMasterData(prefixListMasterData);
		accountPrefixMasterRelationData.setAccountData(accountData);
		
		cityData.setRegionData(regionData);
		
		regionData.setCountryData(countryData);
		
		accountData.setPartnerData(partnerData);
		accountData.setName(ACCOUNT_NAME);
		accountData.setAccountPrefixMasterRelation(accountPrefixMasterRelationData);
		accountData.setProductSpecification(productSpecification);
		accountData.setLob(lob);
		
		partnerData.setAccountData(Arrays.asList(accountData));
		partnerData.setPartnerLegalName(PARTNER_NAME);
		partnerData.setCity(cityData);

		Partner partner = partnerFactory.createPartner(partnerData);
		
		serviceContext = new OfflineRnCServiceContext() {
			
			@Override
			public ServerContext getServerContext() {
				return null;
			}
			
			@Override
			public Partner getPartner(String partnerName) {
				return partner;
			}

			@Override
			public CurrencyExchange getCurrencyExchange() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public BigDecimalFormatter getBigDecimalFormatter() {
				// TODO Auto-generated method stub
				return null;
			}

			
		};
		
		
		prefixConfigurations = new ArrayList<>();
		calledPrefixGlobalConfiguration = new PrefixConfiguration();
		calledPrefixGlobalConfiguration.setPrefixName(CALLED_NAME_DATA_1);
		calledPrefixGlobalConfiguration.setPrefixCode(CALLED_PREFIX_DATA_1);
		calledPrefixGlobalConfiguration.setCountryCode(CALLED_COUNTRY_DATA_1);
		calledPrefixGlobalConfiguration.setAreaCode(CALLED_AREA_DATA_1);
		
		prefixConfigurations.add(calledPrefixGlobalConfiguration);
		
		callingPrefixGlobalConfiguration = new PrefixConfiguration();
		callingPrefixGlobalConfiguration.setPrefixName(CALLING_NAME_DATA_1);
		callingPrefixGlobalConfiguration.setPrefixCode(CALLING_PREFIX_DATA_1);
		callingPrefixGlobalConfiguration.setCountryCode(CALLING_COUNTRY_DATA_1);
		callingPrefixGlobalConfiguration.setAreaCode(CALLING_AREA_DATA_1);
		
		prefixConfigurations.add(callingPrefixGlobalConfiguration);
		
		prefixEnrichmentHandler = new PrefixEnrichmentHandler(prefixConfigurations, serviceContext);
		prefixEnrichmentHandler.init();
		
		request = new RnCRequest();
		request.setAttribute(OfflineRnCKeyConstants.CALLED_STATION_ID.getName() , CALLED_STATION_ID_1);
		request.setAttribute(OfflineRnCKeyConstants.CALLING_STATION_ID.getName() , CALLING_STATION_ID_1);
		response = RnCResponse.of(request);
		response.setAttribute(OfflineRnCKeyConstants.PARTNER_NAME, PARTNER_NAME);
		response.setAttribute(OfflineRnCKeyConstants.ACCOUNT_ID,ACCOUNT_NAME);
	}

	@Test
	public void enrichesResponseWithCalledAndCallingPartyPrefixInformation() throws Exception {
		prefixEnrichmentHandler.handleRequest(request, response, out);
	
		assertCallingKeysEnriched(callingPrefixGlobalConfiguration);
		assertCalledKeysEnriched(calledPrefixGlobalConfiguration);
	}

	@Test
	public void enrichesResponseWithCalledAndCallingPartyPrefixInformationWhenPrefixDefinedAtAccountLevel() throws Exception {
		
		request.setAttribute(OfflineRnCKeyConstants.CALLED_STATION_ID.getName(), CALLED_STATION_ID_2);
		request.setAttribute(OfflineRnCKeyConstants.CALLING_STATION_ID.getName(), CALLING_STATION_ID_2);

		prefixEnrichmentHandler.handleRequest(request, response, out);
		
		assertCalledKeysEnriched(calledPrefixAcctLevelData);
		assertCallingKeysEnriched(callingPrefixAcctLevelData);
	}
	
	@Test
	public void preferenceIsGivenToAccountLevelPrefixConfigurationOverGlobalPrefixConfiguration() throws Exception {
		
		request.setAttribute(OfflineRnCKeyConstants.CALLED_STATION_ID.getName(), CALLED_STATION_ID_2);
		request.setAttribute(OfflineRnCKeyConstants.CALLING_STATION_ID.getName(), CALLING_STATION_ID_2);
		
		calledPrefixGlobalConfiguration.setPrefixName(CALLED_NAME_DATA_1);
		calledPrefixGlobalConfiguration.setAreaCode(CALLED_AREA_DATA_2);
		calledPrefixGlobalConfiguration.setCountryCode(CALLED_COUNTRY_DATA_2);
		calledPrefixGlobalConfiguration.setPrefixCode(CALLED_PREFIX_DATA_2);
		
		prefixEnrichmentHandler = new PrefixEnrichmentHandler(prefixConfigurations, serviceContext);
		prefixEnrichmentHandler.init();
		
		prefixEnrichmentHandler.handleRequest(request, response, out);
		
		assertCalledKeysEnriched(calledPrefixAcctLevelData);
		assertCallingKeysEnriched(callingPrefixAcctLevelData);
	}
	
	@Test
	public void calledPrefixEnrichedFromGlobalLevelAndCallingPrefixEnrichedFromAccountLevel() throws Exception {
		
		request.setAttribute(OfflineRnCKeyConstants.CALLED_STATION_ID.getName(), CALLED_STATION_ID_1);
		request.setAttribute(OfflineRnCKeyConstants.CALLING_STATION_ID.getName(), CALLING_STATION_ID_2);
		
		prefixEnrichmentHandler.handleRequest(request, response, out);
		
		assertCalledKeysEnriched(calledPrefixGlobalConfiguration);
		assertCallingKeysEnriched(callingPrefixAcctLevelData);
	}
	
	@Test
	public void calledPrefixEnrichedFromAccountLevelAndCallingPrefixEnrichedFromGlobalLevel() throws Exception {
		
		request.setAttribute(OfflineRnCKeyConstants.CALLED_STATION_ID.getName(), CALLED_STATION_ID_2);
		request.setAttribute(OfflineRnCKeyConstants.CALLING_STATION_ID.getName(), CALLING_STATION_ID_1);
		
		prefixEnrichmentHandler.handleRequest(request, response, out);
		
		assertCalledKeysEnriched(calledPrefixAcctLevelData);
		assertCallingKeysEnriched(callingPrefixGlobalConfiguration);
	}
	
	
	@Test
	public void skipsCalledPartyEnrichmentIfPrefixInformationNotFound() throws Exception {
		request.setAttribute(OfflineRnCKeyConstants.CALLED_STATION_ID.getName(), UNKNOWN_NUMBER);
		
		prefixEnrichmentHandler.handleRequest(request, response, out);
		
		assertCalledKeysMissing();
		assertCallingKeysEnriched(callingPrefixGlobalConfiguration);
	}

	@Test
	public void skipsCallingPartyEnrichmentIfPrefixInformationNotFound() throws Exception {
		request.setAttribute(OfflineRnCKeyConstants.CALLING_STATION_ID.getName(), UNKNOWN_NUMBER);
		
		prefixEnrichmentHandler.handleRequest(request, response, out);
		
		assertCalledKeysEnriched(calledPrefixGlobalConfiguration);
		assertCallingKeysMissing();
	}

	@Test
	public void skipsCallingAndCalledPartyEnrichmentIfPrefixInformationNotFound() throws Exception {
		request.setAttribute(OfflineRnCKeyConstants.CALLED_STATION_ID.getName() , UNKNOWN_NUMBER);
		request.setAttribute(OfflineRnCKeyConstants.CALLING_STATION_ID.getName() , UNKNOWN_NUMBER);
		
		prefixEnrichmentHandler.handleRequest(request, response, out);
		
		assertCallingKeysMissing();
		assertCalledKeysMissing();
	}
	
	private void assertCalledKeysEnriched(PrefixConfiguration prefixConfiguration) {
		assertCalledPrefix(is(equalTo(prefixConfiguration.getPrefixCode())));
		assertCalledCountryCode(is(equalTo(String.valueOf(prefixConfiguration.getCountryCode()))));
		assertCalledAreaCode(is(equalTo(String.valueOf(prefixConfiguration.getAreaCode()))));
		assertCalledName(is(equalTo(prefixConfiguration.getPrefixName())));
	}
	
	private void assertCallingKeysEnriched(PrefixConfiguration prefixConfiguration) {
		assertCallingPrefix(is(equalTo(prefixConfiguration.getPrefixCode())));
		assertCallingCountryCode(is(equalTo(String.valueOf(prefixConfiguration.getCountryCode()))));
		assertCallingAreaCode(is(equalTo(String.valueOf(prefixConfiguration.getAreaCode()))));
		assertCallingName(is(equalTo(prefixConfiguration.getPrefixName())));
	}
	
	private void assertCalledKeysEnriched(PrefixesData prefixesData) {
		assertCalledPrefix(is(equalTo(prefixesData.getPrefix())));
		assertCalledCountryCode(is(equalTo(String.valueOf(prefixesData.getCountryCode()))));
		assertCalledAreaCode(is(equalTo(String.valueOf(prefixesData.getAreaCode()))));
		assertCalledName(is(equalTo(prefixesData.getName())));
	}
	
	private void assertCallingKeysEnriched(PrefixesData prefixesData) {
		assertCallingPrefix(is(equalTo(prefixesData.getPrefix())));
		assertCallingCountryCode(is(equalTo(String.valueOf(prefixesData.getCountryCode()))));
		assertCallingAreaCode(is(equalTo(String.valueOf(prefixesData.getAreaCode()))));
		assertCallingName(is(equalTo(prefixesData.getName())));
	}

	private void assertCalledKeysMissing() {
		assertCalledPrefix(is(nullValue()));
		assertCalledCountryCode(is(nullValue()));
		assertCalledAreaCode(is(nullValue()));
		assertCalledName(is(nullValue()));
	}
	
	private void assertCallingKeysMissing() {
		assertCallingPrefix(is(nullValue()));
		assertCallingCountryCode(is(nullValue()));
		assertCallingAreaCode(is(nullValue()));
		assertCallingName(is(nullValue()));
	}
	
	private void assertCalledName(Matcher<? super String> matcher) {
		assertThat(response.getAttribute(OfflineRnCKeyConstants.CALLED_NAME.getName()), matcher);
	}
	
	private void assertCallingName(Matcher<? super String> matcher) {
		assertThat(response.getAttribute(OfflineRnCKeyConstants.CALLING_NAME.getName()), matcher);
	}

	private void assertCallingCountryCode(Matcher<? super String> matcher) {
		assertThat(response.getAttribute(OfflineRnCKeyConstants.CALLING_COUNTRY_CODE.getName()), matcher);
	}

	private void assertCallingAreaCode(Matcher<? super String> matcher) {
		assertThat(response.getAttribute(OfflineRnCKeyConstants.CALLING_AREA_CODE.getName()), matcher);
	}

	private void assertCallingPrefix(Matcher<? super String> matcher) {
		assertThat(response.getAttribute(OfflineRnCKeyConstants.CALLING_PREFIX.getName()), matcher);
	}
	
	private void assertCalledCountryCode(Matcher<? super String> matcher) {
		assertThat(response.getAttribute(OfflineRnCKeyConstants.CALLED_COUNTRY_CODE.getName()), matcher);
	}

	private void assertCalledAreaCode(Matcher<? super String> matcher) {
		assertThat(response.getAttribute(OfflineRnCKeyConstants.CALLED_AREA_CODE.getName()), matcher);
	}

	private void assertCalledPrefix(Matcher<? super String> matcher) {
		assertThat(response.getAttribute(OfflineRnCKeyConstants.CALLED_PREFIX.getName()), matcher);
	}
}
