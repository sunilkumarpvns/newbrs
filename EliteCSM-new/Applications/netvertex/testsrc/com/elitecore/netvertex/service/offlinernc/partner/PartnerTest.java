package com.elitecore.netvertex.service.offlinernc.partner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pd.partner.PartnerData;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecData;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.DummyOfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.OfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.PartnerFactory;
import com.elitecore.netvertex.service.offlinernc.account.Account;
import com.elitecore.netvertex.service.offlinernc.productspec.ProductSpecFactory;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroupFactory;
import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackageFactory;

public class PartnerTest {

	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	private static final Timestamp REGISTRATION_TIMESTAMP = new Timestamp(System.currentTimeMillis());
	private static final String ACCOUNT_NAME_ONE = "Airtel_1";
	private static final String ACCOUNT_NAME_TWO = "Airtel_2";
	private static final String ACCOUNT_NAME_THREE = "Airtel_3";
	private static final String PARTNER_NAME = "Voda";
	private static final String PARTNER_LEGAL_NAME = "Vodafone";
	private static final String UNKNOWN_ACCOUNT = "abc";

	private OfflineRnCServiceContext serviceContext = new DummyOfflineRnCServiceContext();
	private PartnerFactory partnerFactory;
	private Partner partner;

	@Before
	public void setUp() throws InitializationFailedException {
		DummyNetvertexServerConfiguration serverConfiguration = new DummyNetvertexServerConfiguration();
		SystemParameterConfiguration spySystemParam = serverConfiguration.spySystemParameterConf();
		Mockito.when(spySystemParam.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);
		
		partnerFactory = new PartnerFactory(null, new ProductSpecFactory(
				new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(serviceContext, spySystemParam), spySystemParam)), spySystemParam));
		
		PartnerData partnerData = new PartnerData();
		ProductSpecData productSpecification = new ProductSpecData();

		LobData lob = new LobData();
		AccountData accountData1 = new AccountData();
		accountData1.setPartnerData(partnerData);
		accountData1.setName(ACCOUNT_NAME_ONE);
		accountData1.setProductSpecification(productSpecification);
		accountData1.setLob(lob);
		
		AccountData accountData2 = new AccountData();
		accountData2.setPartnerData(partnerData);
		accountData2.setName(ACCOUNT_NAME_TWO);
		accountData2.setProductSpecification(productSpecification);
		accountData2.setLob(lob);

		AccountData accountData3 = new AccountData();
		accountData3.setPartnerData(partnerData);
		accountData3.setName(ACCOUNT_NAME_THREE);
		accountData3.setProductSpecification(productSpecification);
		accountData3.setLob(lob);

		partnerData.setAccountData(Arrays.asList(accountData1, accountData2, accountData3));
		
		partnerData.setName(PARTNER_NAME);
		partnerData.setPartnerLegalName(PARTNER_LEGAL_NAME);
		partnerData.setRegistraionDate(REGISTRATION_TIMESTAMP);

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
	}

	@Test
	public void returnsAccountBasedOnAccountName() {
		Account account = partner.selectAccount(ACCOUNT_NAME_TWO);
		
		assertThat(ACCOUNT_NAME_TWO, is(equalTo(account.getName())));
		
		account = partner.selectAccount(ACCOUNT_NAME_THREE);
		
		assertThat(account.getName(), is(equalTo(ACCOUNT_NAME_THREE)));
	}
	
	@Test
	public void returnsNullIfNoAccountIsFoundForGivenAccountName() {
		Account account = partner.selectAccount(UNKNOWN_ACCOUNT);
		
		assertThat(account, is(nullValue()));
	}
	
	@Test
	public void toStringOfAccountContainsNamePartnerLegalNameRegistrationDateAndCityName() {
		assertThat(partner.toString(), containsString("name=Voda"));
		assertThat(partner.toString(), containsString("partnerLegalName=Vodafone"));
		assertThat(partner.toString(), containsString("registrationDate=" + REGISTRATION_TIMESTAMP));
		assertThat(partner.toString(), containsString("city=Ahmedabad"));
	}
}
