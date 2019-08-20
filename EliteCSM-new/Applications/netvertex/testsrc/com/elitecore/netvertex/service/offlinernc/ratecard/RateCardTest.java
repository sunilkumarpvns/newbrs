package com.elitecore.netvertex.service.offlinernc.ratecard;

import static com.elitecore.commons.base.Strings.join;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST1;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST2;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST3;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTING_EFFECT;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.CALLED_NAME;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.CALLING_NAME;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.CHARGE_PER_UOM;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.LINE_OF_BUSINESS;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.PARTNER_CURRENCY_ISO_CODE;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.RATE1;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.RATE2;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.RATE3;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.SERVICE_NAME;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.SESSION_CONNECT_TIME;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.SESSION_TIME;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.SESSION_TOTAL_DATA_TRANSFER;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.SYSTEM_CURRENCY_ACCOUNTED_COST;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.TOTAL_PULSE1;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.TOTAL_PULSE2;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.TOTAL_PULSE3;
import static com.elitecore.netvertex.service.offlinernc.ratecard.RnCResponseAssertion.assertThat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.currency.CurrencyData;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionDetail;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionRelation;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.DummyOfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchange;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchangeException;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchangeFactory;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class RateCardTest {
	private static final String FLAT = "FLAT";
	public static final String INCREMENTAL = "INCREMENTAL";
	private static final String RATE_SEPARATOR = ":";
	private static final String INR_ISO_CODE = "INR";
	private static final String USD_ISO_CODE = "USD";
	private static final OfflineRnCKeyConstants RATING_KEY = OfflineRnCKeyConstants.SESSION_CONNECT_TIME;
	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	private static final String CURRENCY_INR = "INR";
	private static final String CURRENCY_DNR = "DNR";
	private static final String CURRENCY_USD = "USD";
	private static final String ACCOUNT_EFFECT_CR = "CR";
	private static final String ACCOUNT_EFFECT_DR = "DR";


	private final Timestamp fromDate = Timestamp.valueOf("2018-01-16 10:10:10");
	private final String dateAfterFromDate = "17-01-2018 10:00:00";
	private final String dateBeforeFromDate = "1-1-2016 10:00:00";
	private final String labelOneValue = "india";
	private final String labelTwoValue = "uk";

	private RateCard rateCard;
	private RnCRequest request;
	private RnCResponse response;

	private RateCardFactory rateCardFactory;

	private RateCardData rateCardData;
	private RateCardVersionRelation rateCardVersionRelation;
	private RateCardVersionDetail rateCardVersionDetail;
	private DummyOfflineRnCServiceContext offlineRnCServiceContext;
	private DummyNetvertexServerContextImpl netvertexServerContext;
	private DummyNetvertexServerConfiguration serverConfiguration;
	private CurrencyExchangeFactory currencyExchangeFactory;

	@Mock private SessionFactory sessionFactory;

	@Rule public PrintMethodRule rule = new PrintMethodRule();

	@Rule public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		serverConfiguration = new DummyNetvertexServerConfiguration();
		SystemParameterConfiguration spySystemParam = serverConfiguration.spySystemParameterConf();
		Mockito.when(spySystemParam.getSystemCurrency()).thenReturn(INR_ISO_CODE);
		Mockito.when(spySystemParam.getMultiValueSeparator()).thenReturn(RATE_SEPARATOR);
		Mockito.when(spySystemParam.getRateSelectionWhenDateChange()).thenReturn(RATING_KEY.getName());
		Mockito.when(spySystemParam.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);


		netvertexServerContext = new DummyNetvertexServerContextImpl();
		netvertexServerContext.setNetvertexServerConfiguration(serverConfiguration);

		offlineRnCServiceContext = new DummyOfflineRnCServiceContext();
		offlineRnCServiceContext.setServerContext(netvertexServerContext);

		currencyExchangeFactory = new CurrencyExchangeFactory(sessionFactory);
		List<CurrencyData> currencies = new ArrayList<>();
		CurrencyData currencyData = new CurrencyData();
		currencyData.setFromIsoCode(USD_ISO_CODE);
		currencyData.setToIsoCode(INR_ISO_CODE);
		currencyData.setRate(10.0);
		currencyData.setEffectiveDate(fromDate);
		currencies.add(currencyData);
		CurrencyExchange currencyExchange = currencyExchangeFactory.create(currencies);
		offlineRnCServiceContext.setCurrencyExchange(currencyExchange);
		rateCardFactory = new RateCardFactory(offlineRnCServiceContext, spySystemParam);
	}

	@After
	public void showLogs() {
		LogManager.getLogger().debug("RATE-CARD-TEST", request.getTrace());
	}
	public class whileChargingBasedOnTime {

		public class LimitedSlab {

			@Before
			public void setup() {
				flatLimitedSetup();
			}

			@Test
			public void givenSessionDisconnectTimeIsPriorToVersionFromDateRateIsNotApplied() throws OfflineRnCException, InitializationFailedException {
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), dateBeforeFromDate);
				request.setAttribute(SESSION_TIME.getName(), "60");

				Assert.assertFalse(rateCard.apply(request, response));
			}

			@Test
			public void givenPulseUomIsSecondCalculatesAndAddsTotalNumberOfPulses() throws OfflineRnCException, InitializationFailedException {
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "60");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "1");
			}

			@Test
			public void givenPulseUomIsSecondAndSessionTimeIsNotExactMultipleCalculatesTotalNumberOfPulseByRoundingUp() throws OfflineRnCException, InitializationFailedException {
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2");
			}

			@Test
			public void givenPulseUomIsMinuteCalculatesAndAddsTotalNumberOfPulses() throws OfflineRnCException, InitializationFailedException {
				rateCardData.setPulseUom(Uom.MINUTE.getValue());
				rateCardVersionDetail.setPulse1(1.0);
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "120");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2");
			}

			@Test
			public void givenPulseUomIsHourCalculatesAndAddsTotalNumberOfPulses() throws OfflineRnCException, InitializationFailedException {
				rateCardData.setPulseUom(Uom.HOUR.getValue());
				rateCardVersionDetail.setPulse1(2.0);
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "7500");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2");
			}

			@Test
			public void addsCalculatedRateInResponse() throws OfflineRnCException, InitializationFailedException {
				rateCardData.setPulseUom(Uom.SECOND.getValue());
				rateCardVersionDetail.setPulse1(20.0);
				rateCardData.setRateUom(Uom.SECOND.getValue());
				rateCardVersionDetail.setRate1(5.0);

				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "4")
						.containsAttributeWithValue(OfflineRnCKeyConstants.RATE1, "100.000000");

			}

			@Test
			public void addsAccountingEffectInResponseGivenEdrIsSuccessfullyRated() throws OfflineRnCException, InitializationFailedException {
				rateCardData.setPulseUom(Uom.SECOND.getValue());
				rateCardVersionDetail.setPulse1(20.0);
				rateCardData.setRateUom(Uom.SECOND.getValue());
				rateCardVersionDetail.setRate1(5.0);

				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_DR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "4")
						.containsAttributeWithValue(OfflineRnCKeyConstants.RATE1, "100.000000");

				assertThat(response).containsAttributeWithValue(ACCOUNTING_EFFECT, "DR");

			}

			@Test
			public void ratePerUomOfRateCardIsPresentInResponseGivenRequestIsSuccessfullyRated() throws OfflineRnCException, InitializationFailedException {

				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2")
						.containsAttributeWithValue(ACCOUNTED_COST1, "60.000000")
						.containsAttributeWithValue(ACCOUNTED_COST, "60.000000")
						.containsAttributeWithValue(CHARGE_PER_UOM, "0.500000");

			}

			@Test
			public void summationOfAccountedCostsIsPresentInAccountedCostKey() throws OfflineRnCException, InitializationFailedException {
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2")
						.containsAttributeWithValue(ACCOUNTED_COST1, "60.000000")
						.containsAttributeWithValue(ACCOUNTED_COST, "60.000000");
			}

			@Test
			public void ratingFailsIfCalculatedCostCannotBeConvertedToSystemCurrency() throws InitializationFailedException, OfflineRnCException {
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_DNR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				expectedException.expect(OfflineRnCException.class);
				expectedException.expectCause(CoreMatchers.instanceOf(CurrencyExchangeException.class));
				expectedException.expectMessage(OfflineRnCErrorMessages.EXCHANGE_RATE_NOT_FOUND.message());
				rateCard.apply(request, response);
			}

			@Test
			public void totalCostIsConvertedAndAddedInSystemCurrencyAccountedCostKey() throws OfflineRnCException, InitializationFailedException {
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_USD, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2")
						.containsAttributeWithValue(ACCOUNTED_COST1, "60.000000")
						.containsAttributeWithValue(ACCOUNTED_COST, "60.000000")
						.containsAttributeWithValue(SYSTEM_CURRENCY_ACCOUNTED_COST, "600.000000");
			}

			@Test
			public void configuredCurrencyIsoCodeIsPresentInResponseGivenRequestIsSuccessfullyRated() throws OfflineRnCException, InitializationFailedException {
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_USD, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2")
						.containsAttributeWithValue(PARTNER_CURRENCY_ISO_CODE, CURRENCY_USD);

			}

			public class RateUomIsSameAsPulseUom {

				@Test
				public void ratesRequestBasedOnRate() throws OfflineRnCException, InitializationFailedException {
					rateCardVersionDetail.setRate1(0.5);
					rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
					request.setAttribute(SESSION_TIME.getName(), "70");

					rateCard.apply(request, response);

					assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2")
							.containsAttributeWithValue(ACCOUNTED_COST1, "60.000000");
				}
			}

			 class RateUomIsDifferentFromPulseUom {


				@Test
				public void ratesRequestBasedOnRate() throws OfflineRnCException   {
					rateCardVersionDetail.setPulse1(1.0);
					rateCardVersionDetail.setRate1(10.0);
					rateCardData.setPulseUom(Uom.MINUTE.getValue());
					rateCardData.setRateUom(Uom.SECOND.getValue());

					request.setAttribute(SESSION_TIME.getName(), "70");

					rateCard.apply(request, response);

					assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2").
							containsAttributeWithValue(ACCOUNTED_COST1, "1200.000000").
							containsAttributeWithValue(ACCOUNTED_COST, "1200.000000");
				}
			}
		}

		public class UnlimitedSlab {

			@Before
			public void setup() {
				request = RnCPacketBuilder.requestBuilder().
						with(LINE_OF_BUSINESS, "International interconnect").
						with(SERVICE_NAME, "Voice").
						with(CALLING_NAME, labelOneValue).
						with(CALLED_NAME, labelTwoValue).
						with(OfflineRnCKeyConstants.SESSION_CONNECT_TIME, dateAfterFromDate).
						build();

				response = RnCResponse.of(request);

				rateCardData = new RateCardData();
				rateCardData.setName("Dummy rate card");

				rateCardData.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
				rateCardData.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());
				rateCardData.setRateUom(Uom.SECOND.getValue());
				rateCardData.setPulseUom(Uom.SECOND.getValue());

				rateCardVersionRelation = new RateCardVersionRelation();
				rateCardVersionRelation.setOrderNumber(1);
				rateCardVersionRelation.setVersionName("version one");
				rateCardVersionDetail = new RateCardVersionDetail();
				rateCardVersionDetail.setFromDate(fromDate);
				rateCardVersionDetail.setLabel1(labelOneValue);
				rateCardVersionDetail.setLabel2(labelTwoValue);
				rateCardVersionDetail.setOrderNumber(1);
				rateCardVersionDetail.setTierRateType(FLAT);

				rateCardVersionDetail.setPulse1(60.0);
				rateCardVersionDetail.setRate1(60.0);
				rateCardVersionDetail.setSlab1(1000.0);

				rateCardVersionDetail.setPulse2(60.0);
				rateCardVersionDetail.setRate2(60.0);
				rateCardVersionDetail.setSlab2(-1.0);

				List<RateCardVersionDetail> rateCardVersionDetails = new ArrayList<>();
				rateCardVersionDetails.add(rateCardVersionDetail);

				rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);

				List<RateCardVersionRelation> rateCardVersionRelations = new ArrayList<>();
				rateCardVersionRelations.add(rateCardVersionRelation);

				rateCardData.setRateCardVersionRelation(rateCardVersionRelations);
			}

			@Test
			public void unlimitedPulseIsSatisfiedForAnyValueOfAccountedUsageGivenMultipleSlabsAreConfigured() throws OfflineRnCException, InitializationFailedException {
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "6000");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "100");
			}

			@Test
			public void givenOnlyUnlimitedPulseIsConfiguredItIsApplied() throws OfflineRnCException, InitializationFailedException {
				rateCardVersionDetail.setSlab1(-1.0);
				rateCardVersionDetail.setRate1(60.0);
				rateCardData.setRateUom(Uom.MINUTE.getValue());
				rateCardVersionDetail.setPulse1(1.0);
				rateCardData.setPulseUom(Uom.SECOND.getValue());

				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);

				request.setAttribute(SESSION_TIME.getName(), "50");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "50")
						.containsAttributeWithValue(RATE1, "1.000000");


			}
		}
	}

	public class WhileChargingBasedOnData {

		@Before
		public void setup() {
			request = RnCPacketBuilder.requestBuilder().
					with(LINE_OF_BUSINESS, "International interconnect").
					with(SERVICE_NAME, "Data").
					with(CALLING_NAME, labelOneValue).
					with(CALLED_NAME, labelTwoValue).
					with(OfflineRnCKeyConstants.SESSION_CONNECT_TIME, dateAfterFromDate).
					build();

			response = RnCResponse.of(request);

			rateCardData = new RateCardData();
			rateCardData.setName("Dummy rate card");

			rateCardData.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
			rateCardData.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());
			rateCardData.setRateUom(Uom.BYTE.getValue());
			rateCardData.setPulseUom(Uom.BYTE.getValue());

			rateCardVersionRelation = new RateCardVersionRelation();
			rateCardVersionRelation.setOrderNumber(1);
			rateCardVersionRelation.setVersionName("version one");
			rateCardVersionDetail = new RateCardVersionDetail();
			rateCardVersionDetail.setFromDate(fromDate);
			rateCardVersionDetail.setLabel1(labelOneValue);
			rateCardVersionDetail.setLabel2(labelTwoValue);
			rateCardVersionDetail.setOrderNumber(1);
			rateCardVersionDetail.setTierRateType(FLAT);
			rateCardVersionDetail.setPulse1(1024.0);
			rateCardVersionDetail.setRate1(1.0);
			rateCardVersionDetail.setSlab1(10000.0);

			List<RateCardVersionDetail> rateCardVersionDetails = new ArrayList<>();
			rateCardVersionDetails.add(rateCardVersionDetail);

			rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);

			List<RateCardVersionRelation> rateCardVersionRelations = new ArrayList<>();
			rateCardVersionRelations.add(rateCardVersionRelation);

			rateCardData.setRateCardVersionRelation(rateCardVersionRelations);
		}

		@Test
		public void addsNumberOfPulseInResponse() throws OfflineRnCException, InitializationFailedException   {

			rateCardVersionDetail.setPulse1(1024.0);
			rateCardVersionDetail.setRate1(1.50);
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);

			request.setAttribute(SESSION_TOTAL_DATA_TRANSFER.getName(), "2048");

			rateCard.apply(request, response);

			assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "2");
		}

		@Test
		public void givenUsageIsNotExactMultipleCalculatesTotalNumberOfPulseByRoundingUp() throws OfflineRnCException, InitializationFailedException   {

			rateCardVersionDetail.setPulse1(1024.0);
			rateCardVersionDetail.setRate1(1.50);
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);

			request.setAttribute(OfflineRnCKeyConstants.SESSION_TOTAL_DATA_TRANSFER.getName(), "194");

			rateCard.apply(request, response);

			assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "1");
		}

		@Test
		public void addsRatePerPulseInResponse() throws OfflineRnCException, InitializationFailedException   {
			rateCardVersionDetail.setPulse1(1024.0);
			rateCardVersionDetail.setRate1(1.50);
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);

			request.setAttribute(SESSION_TOTAL_DATA_TRANSFER.getName(), "194");

			rateCard.apply(request, response);

			assertThat(response).containsAttributeWithValue(RATE1, "1536.000000");
		}

		public class RateUomIsSameAsPulseUom {

			@Test
			public void ratesRequestBasedOnRate() throws OfflineRnCException, InitializationFailedException   {
				rateCardVersionDetail.setPulse1(1024.0);
				rateCardVersionDetail.setRate1(1.50);
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);

				request.setAttribute(SESSION_TOTAL_DATA_TRANSFER.getName(), "194");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "1").
						containsAttributeWithValue(ACCOUNTED_COST1, "1536.000000").
						containsAttributeWithValue(ACCOUNTED_COST, "1536.000000");
			}
		}

		public class RateUomIsDifferentFromPulseUom {

			@Test
			public void ratesRequestBasedOnRate() throws OfflineRnCException, InitializationFailedException   {
				rateCardVersionDetail.setPulse1(1024.0);
				rateCardVersionDetail.setRate1(1.50);
				rateCardData.setPulseUom(Uom.MB.getValue());
				rateCardData.setRateUom(Uom.GB.getValue());
				rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);

				request.setAttribute(SESSION_TOTAL_DATA_TRANSFER.getName(), "194");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE1, "1").
						containsAttributeWithValue(ACCOUNTED_COST1, "1.500000").
						containsAttributeWithValue(ACCOUNTED_COST, "1.500000");
			}
		}
	}

	public class IncrementalRatingBehavior {

		@Before
		public void setup() {
			incrementalLimtedSetup();
		}

		@Test
		public void appliesIncrementalSlab() throws OfflineRnCException, InitializationFailedException {
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
			request.setAttribute(SESSION_TIME.getName(), "1400");

			rateCard.apply(request, response);

			assertThat(response).containsAttributeWithValue(TOTAL_PULSE1,
					Strings.join(RATE_SEPARATOR, new String[]{"10","15","3"}))
					.containsAttributeWithValue(ACCOUNTED_COST1,
							"2910.000000")
					.containsAttributeWithValue(RATE1,
							join(RATE_SEPARATOR, new String[]{"30.000000", "120.000000", "270.000000"}));
		}

		@Test
		public void ratePerUomOfRateCardIsPresentInResponseGivenRequestIsSuccessfullyRated() throws OfflineRnCException, InitializationFailedException {
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
			request.setAttribute(SESSION_TIME.getName(), "1400");

			rateCard.apply(request, response);

			assertThat(response).containsAttributeWithValue(TOTAL_PULSE1,
					Strings.join(RATE_SEPARATOR, new String[]{"10","15","3"}))
					.containsAttributeWithValue(ACCOUNTED_COST1
							, "2910.000000")
					.containsAttributeWithValue(RATE1
							, join(RATE_SEPARATOR, new String[]{"30.000000", "120.000000", "270.000000"}))
					.containsAttributeWithValue(CHARGE_PER_UOM
							, join(RATE_SEPARATOR, new String[]{"1.000000", "2.000000", "3.000000"}));
		}

		@Test
		public void configuredCurrencyIsoCodeIsPresentInResponseGivenRequestIsSuccessfullyRated() throws OfflineRnCException, InitializationFailedException {
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_USD, ACCOUNT_EFFECT_CR);
			request.setAttribute(SESSION_TIME.getName(), "1400");

			rateCard.apply(request, response);

			assertThat(response).containsAttributeWithValue(TOTAL_PULSE1,
					Strings.join(RATE_SEPARATOR, new String[]{"10","15","3"}))
					.containsAttributeWithValue(ACCOUNTED_COST1
							, "2910.000000")
					.containsAttributeWithValue(RATE1
							, join(RATE_SEPARATOR, new String[]{"30.000000", "120.000000", "270.000000"}))
					.containsAttributeWithValue(OfflineRnCKeyConstants.PARTNER_CURRENCY_ISO_CODE, CURRENCY_USD);
		}


		@Test
		public void ratePerPulseForEachSlabAppliedIsPresentInRateKey() throws OfflineRnCException, InitializationFailedException {
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
			request.setAttribute(SESSION_TIME.getName(), "1400");

			rateCard.apply(request, response);

			assertThat(response).containsAttributeWithValue(TOTAL_PULSE1,
					Strings.join(RATE_SEPARATOR, new String[]{"10","15","3"}))
					.containsAttributeWithValue(RATE1,
							join(RATE_SEPARATOR, new String[]{"30.000000", "120.000000", "270.000000"}));
		}

		@Test
		public void summationOfAllAccountedCostsIsPresentInAccountedCostKey() throws OfflineRnCException, InitializationFailedException {
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
			request.setAttribute(SESSION_TIME.getName(), "1400");

			rateCard.apply(request, response);

			assertThat(response).containsAttributeWithValue(TOTAL_PULSE1,
					Strings.join(RATE_SEPARATOR, new String[]{"10","15","3"}))
					.containsAttributeWithValue(ACCOUNTED_COST1,
							"2910.000000")
					.containsAttributeWithValue(RATE1,
							join(RATE_SEPARATOR, new String[]{"30.000000", "120.000000", "270.000000"}))
					.containsAttributeWithValue(ACCOUNTED_COST, "2910.000000");
		}

		@Test
		public void totalCostIsConvertedAndAddedInSystemCurrencyAccountedCostKey() throws OfflineRnCException, InitializationFailedException {
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_USD, ACCOUNT_EFFECT_CR);
			request.setAttribute(SESSION_TIME.getName(), "1400");

			rateCard.apply(request, response);

			assertThat(response).containsAttributeWithValue(TOTAL_PULSE1,
					Strings.join(RATE_SEPARATOR, new String[]{"10","15","3"}))
					.containsAttributeWithValue(ACCOUNTED_COST1,
							"2910.000000")
					.containsAttributeWithValue(RATE1,
							join(RATE_SEPARATOR, new String[]{"30.000000", "120.000000", "270.000000"}))
					.containsAttributeWithValue(ACCOUNTED_COST, "2910.000000")
					.containsAttributeWithValue(SYSTEM_CURRENCY_ACCOUNTED_COST, "29100.000000");
		}

		@Test
		public void ratingFailsIfCalculatedCostCannotBeConvertedToSystemCurrency() throws OfflineRnCException, InitializationFailedException {
			rateCard = rateCardFactory.createRateCard1(rateCardData, CURRENCY_DNR, ACCOUNT_EFFECT_CR);
			request.setAttribute(SESSION_TIME.getName(), "1400");

			expectedException.expect(OfflineRnCException.class);
			expectedException.expectCause(CoreMatchers.instanceOf(CurrencyExchangeException.class));
			expectedException.expectMessage(OfflineRnCErrorMessages.EXCHANGE_RATE_NOT_FOUND.message());
			rateCard.apply(request, response);
		}
	}

	public class WhenRateCardTwoIsApplied {


		public class WithFlatSlab {

			@Before
			public void setup() {
				flatLimitedSetup();
			}

			@Test
			public void enrichesKeysRelatedTwoRateCardTwoKeys() throws InitializationFailedException, OfflineRnCException {
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard2(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE2, "2")
						.containsAttributeWithValue(ACCOUNTED_COST2, "60.000000");
			}

			@Test
			public void addsToAccountedCostIfItAlreadyExists() throws InitializationFailedException, OfflineRnCException {
				response.setAttribute(ACCOUNTED_COST, "100.00");
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard2(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE2, "2")
						.containsAttributeWithValue(ACCOUNTED_COST, "160.000000");
			}

			@Test
			public void addsToSystemCurrencyAccountedCostIfItAlreadyExists() throws InitializationFailedException, OfflineRnCException {
				response.setAttribute(SYSTEM_CURRENCY_ACCOUNTED_COST, "100.00");
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard2(rateCardData, CURRENCY_USD, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE2, "2")
						.containsAttributeWithValue(SYSTEM_CURRENCY_ACCOUNTED_COST, "700.000000");
			}
		}

		public class WithIncrementalSlab {

			@Before
			public void setup() {
				incrementalLimtedSetup();
			}

			@Test
			public void enrichesKeysRelatedTwoRateCardTwoKeys() throws InitializationFailedException, OfflineRnCException {
				rateCard = rateCardFactory.createRateCard2(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "1400");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE2,
						Strings.join(RATE_SEPARATOR, new String[]{"10","15","3"}))
						.containsAttributeWithValue(ACCOUNTED_COST2,
								"2910.000000")
						.containsAttributeWithValue(RATE2,
								join(RATE_SEPARATOR, new String[]{"30.000000", "120.000000", "270.000000"}));
			}
		}
	}

	public class WhenRateCardThreeIsApplied {


		public class WithFlatSlab {

			@Before
			public void setup() {
				flatLimitedSetup();


			}

			@Test
			public void enrichesKeysRelatedToRateCardThreeKeys() throws InitializationFailedException, OfflineRnCException {
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard3(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE3, "2")
						.containsAttributeWithValue(ACCOUNTED_COST3, "60.000000");
			}

			@Test
			public void addsToAccountedCostIfItAlreadyExists() throws InitializationFailedException, OfflineRnCException {
				response.setAttribute(ACCOUNTED_COST, "100.00");
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard3(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE3, "2")
						.containsAttributeWithValue(ACCOUNTED_COST, "160.000000");
			}

			@Test
			public void addsToSystemCurrencyAccountedCostIfItAlreadyExists() throws InitializationFailedException, OfflineRnCException {
				response.setAttribute(SYSTEM_CURRENCY_ACCOUNTED_COST, "100.00");
				rateCardVersionDetail.setRate1(0.5);
				rateCard = rateCardFactory.createRateCard3(rateCardData, CURRENCY_USD, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "70");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE3, "2")
						.containsAttributeWithValue(SYSTEM_CURRENCY_ACCOUNTED_COST, "700.000000");
			}
		}

		public class WithIncrementalSlab {

			@Before
			public void setup() {
				incrementalLimtedSetup();
			}

			@Test
			public void enrichesKeysRelatedToRateCardThreeKeys() throws InitializationFailedException, OfflineRnCException {
				rateCard = rateCardFactory.createRateCard3(rateCardData, CURRENCY_INR, ACCOUNT_EFFECT_CR);
				request.setAttribute(SESSION_TIME.getName(), "1400");

				rateCard.apply(request, response);

				assertThat(response).containsAttributeWithValue(TOTAL_PULSE3,
						Strings.join(RATE_SEPARATOR, new String[]{"10","15","3"}))
						.containsAttributeWithValue(ACCOUNTED_COST3,
								"2910.000000")
						.containsAttributeWithValue(RATE3,
								join(RATE_SEPARATOR, new String[]{"30.000000", "120.000000", "270.000000"}));
			}
		}
	}

	private void flatLimitedSetup() {
		request = RnCPacketBuilder.requestBuilder().
				with(LINE_OF_BUSINESS, "International interconnect").
				with(SERVICE_NAME, "Voice").
				with(CALLING_NAME, labelOneValue).
				with(CALLED_NAME, labelTwoValue).
				with(SESSION_CONNECT_TIME, dateAfterFromDate).
				build();

		response = RnCResponse.of(request);

		rateCardData = new RateCardData();
		rateCardData.setName("Dummy rate card");

		rateCardData.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		rateCardData.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());
		rateCardData.setRateUom(Uom.SECOND.getValue());
		rateCardData.setPulseUom(Uom.SECOND.getValue());

		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setOrderNumber(1);
		rateCardVersionRelation.setVersionName("version one");
		rateCardVersionDetail = new RateCardVersionDetail();
		rateCardVersionDetail.setFromDate(fromDate);
		rateCardVersionDetail.setLabel1(labelOneValue);
		rateCardVersionDetail.setLabel2(labelTwoValue);
		rateCardVersionDetail.setOrderNumber(1);
		rateCardVersionDetail.setTierRateType(FLAT);
		rateCardVersionDetail.setPulse1(60.0);
		rateCardVersionDetail.setRate1(60.0);
		rateCardVersionDetail.setSlab1(10000.0);

		List<RateCardVersionDetail> rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetails.add(rateCardVersionDetail);

		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);

		List<RateCardVersionRelation> rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelations.add(rateCardVersionRelation);

		rateCardData.setRateCardVersionRelation(rateCardVersionRelations);
	}

	private void incrementalLimtedSetup() {
		request = RnCPacketBuilder.requestBuilder().
				with(LINE_OF_BUSINESS, "International interconnect").
				with(SERVICE_NAME, "Data").
				with(CALLING_NAME, labelOneValue).
				with(CALLED_NAME, labelTwoValue).
				with(SESSION_CONNECT_TIME, dateAfterFromDate).
				build();

		response = RnCResponse.of(request);

		rateCardData = new RateCardData();
		rateCardData.setName("Incremental Rate card");

		rateCardData.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		rateCardData.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());
		rateCardData.setRateUom(Uom.SECOND.getValue());
		rateCardData.setPulseUom(Uom.SECOND.getValue());

		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setOrderNumber(1);
		rateCardVersionRelation.setVersionName("version one");
		rateCardVersionDetail = new RateCardVersionDetail();
		rateCardVersionDetail.setFromDate(fromDate);
		rateCardVersionDetail.setLabel1(labelOneValue);
		rateCardVersionDetail.setLabel2(labelTwoValue);
		rateCardVersionDetail.setOrderNumber(1);
		rateCardVersionDetail.setTierRateType(INCREMENTAL);

		rateCardVersionDetail.setPulse1(30.0);
		rateCardVersionDetail.setRate1(1.0);
		rateCardVersionDetail.setSlab1(300.0);

		rateCardVersionDetail.setPulse2(60.0);
		rateCardVersionDetail.setRate2(2.0);
		rateCardVersionDetail.setSlab2(900.0);

		rateCardVersionDetail.setPulse3(90.0);
		rateCardVersionDetail.setRate3(3.0);
		rateCardVersionDetail.setSlab3(1500.0);


		List<RateCardVersionDetail> rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetails.add(rateCardVersionDetail);

		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);

		List<RateCardVersionRelation> rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelations.add(rateCardVersionRelation);

		rateCardData.setRateCardVersionRelation(rateCardVersionRelations);
	}

}