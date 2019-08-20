package com.elitecore.netvertex.service.offlinernc.currency;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.corenetvertex.pd.currency.CurrencyData;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


import static com.elitecore.netvertex.service.offlinernc.util.TimeUtility.dayAfter;
import static com.elitecore.netvertex.service.offlinernc.util.TimeUtility.dayBefore;
import static com.elitecore.netvertex.service.offlinernc.util.TimeUtility.daysAfter;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * 
 * @author narendra.pathai
 *
 */
public class CurrencyExchangeTest {

	private static final double EXCHANGE_RATE_ON_FIRST_DATE = 63.62;
	private static final Double EXCHANGE_RATE_ON_SECOND_DATE = 60.12;
	private static final Double EXCHANGE_RATE_ON_THIRD_DATE = 59.22;
	private static final String FIRST_DATE = "01-01-2018 10:00:00";
	private static final String USD = "USD";
	private static final String INR = "INR";
	private static final BigDecimal ANY_AMOUNT = new BigDecimal(10);
	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	
	private final SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(TIME_FORMAT);
	private CurrencyExchangeFactory currencyExchangeFactory;
	private CurrencyExchange currencyExchange;
	private Timestamp firstDate;
	private Timestamp secondDate;
	private Timestamp thirdDate;
	private MathContext mathContext = new MathContext(2, RoundingMode.HALF_EVEN);
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setUp() throws ParseException {
		firstDate = new Timestamp(simpleDateFormatThreadLocal.get().parse(FIRST_DATE).getTime());
		secondDate = daysAfter(firstDate, 4);
		thirdDate = daysAfter(firstDate, 10);
		
		currencyExchangeFactory = new CurrencyExchangeFactory(mock(SessionFactory.class));
		CurrencyData dollarToInrOnFirstDate = new CurrencyData();
		dollarToInrOnFirstDate.setFromIsoCode(USD);
		dollarToInrOnFirstDate.setToIsoCode(INR);
		dollarToInrOnFirstDate.setEffectiveDate(firstDate);
		dollarToInrOnFirstDate.setRate(EXCHANGE_RATE_ON_FIRST_DATE);
		
		CurrencyData dollarToInrOnSecondDate = new CurrencyData();
		dollarToInrOnSecondDate.setFromIsoCode(USD);
		dollarToInrOnSecondDate.setToIsoCode(INR);
		dollarToInrOnSecondDate.setEffectiveDate(secondDate);
		dollarToInrOnSecondDate.setRate(EXCHANGE_RATE_ON_SECOND_DATE);
		
		CurrencyData dollarToInrOnThirdDate = new CurrencyData();
		dollarToInrOnThirdDate.setFromIsoCode(USD);
		dollarToInrOnThirdDate.setToIsoCode(INR);
		dollarToInrOnThirdDate.setEffectiveDate(thirdDate);
		dollarToInrOnThirdDate.setRate(EXCHANGE_RATE_ON_THIRD_DATE);
		
		currencyExchange = currencyExchangeFactory.create(Arrays.asList(dollarToInrOnFirstDate, 
				dollarToInrOnSecondDate, dollarToInrOnThirdDate));
	}
	
	@Test
	public void appliesExchangeRateIfDateIsSameAsEffectiveDate() throws Exception {
		BigDecimal expected = new BigDecimal(10).multiply(new BigDecimal(EXCHANGE_RATE_ON_FIRST_DATE), mathContext);
		
		assertThat(currencyExchange.convert(BigDecimal.TEN, USD, INR, firstDate), is(equalTo(expected)));
	}
	
	@Test
	public void doesNotApplyExchangeRateIfDateIsBeforeEffectiveDate() throws Exception {
		exception.expect(CurrencyExchangeException.class);
		exception.expectMessage(containsString("No currency exchange: USD -> INR valid for date"));

		currencyExchange.convert(BigDecimal.ONE, USD, INR, dayBefore(firstDate));
	}
	
	@Test
	public void appliesExchangeRateIfDateIsAfterEffectiveDate() throws Exception {
		BigDecimal expected = new BigDecimal(10).multiply(new BigDecimal(EXCHANGE_RATE_ON_FIRST_DATE), mathContext);
		
		assertThat(currencyExchange.convert(BigDecimal.TEN, USD, INR, dayAfter(firstDate)), is(equalTo(expected)));
	}
	
	@Test
	public void appliesExchangeRateOfLargestDateLessThanRequestedDate() throws Exception {
		BigDecimal expected = new BigDecimal(10).multiply(new BigDecimal(EXCHANGE_RATE_ON_SECOND_DATE), mathContext);
		
		assertThat(currencyExchange.convert(BigDecimal.TEN, USD, INR, dayAfter(secondDate)), is(equalTo(expected)));
	}
	
	@Test
	public void appliesExchangeRateOfLargestDateEqualToRequestedDate() throws Exception {
		BigDecimal expected = new BigDecimal(10).multiply(new BigDecimal(EXCHANGE_RATE_ON_SECOND_DATE), mathContext);
		
		assertThat(currencyExchange.convert(BigDecimal.TEN, USD, INR, secondDate), is(equalTo(expected)));
	}
	
	@Test
	public void doesNotApplyExchangeRateIfExchangeIsUnknown() throws Exception {
		exception.expect(CurrencyExchangeException.class);
		exception.expectMessage(containsString("Currency exchange: INR -> USD is unknown"));
		
		currencyExchange.convert(ANY_AMOUNT, INR, USD, firstDate);
	}
	
	@Test
	public void alwaysSupportsIdentityExchangeIrrespectiveOfDateAndAmountRemainsUnchanged() throws Exception {
		assertThat(currencyExchange.convert(BigDecimal.TEN, USD, USD, firstDate), is(equalTo(BigDecimal.TEN)));
		assertThat(currencyExchange.convert(BigDecimal.TEN, USD, USD, secondDate), is(equalTo(BigDecimal.TEN)));
		assertThat(currencyExchange.convert(BigDecimal.TEN, USD, USD, thirdDate), is(equalTo(BigDecimal.TEN)));
		
		assertThat(currencyExchange.convert(BigDecimal.TEN, INR, INR, firstDate), is(equalTo(BigDecimal.TEN)));
		assertThat(currencyExchange.convert(BigDecimal.TEN, INR, INR, secondDate), is(equalTo(BigDecimal.TEN)));
		assertThat(currencyExchange.convert(BigDecimal.TEN, INR, INR, thirdDate), is(equalTo(BigDecimal.TEN)));
	}
	
	@Test
	public void appliesLatestExchangeRateIfSameCurrencyExchangeWithDifferentRateIsAdded() throws Exception {
		CurrencyData dollarToInrOnFirstDate = new CurrencyData();
		dollarToInrOnFirstDate.setFromIsoCode(USD);
		dollarToInrOnFirstDate.setToIsoCode(INR);
		dollarToInrOnFirstDate.setEffectiveDate(firstDate);
		dollarToInrOnFirstDate.setRate(EXCHANGE_RATE_ON_FIRST_DATE);
		
		CurrencyData dollarToInrOnFirstDateButDifferentDate = new CurrencyData();
		dollarToInrOnFirstDateButDifferentDate.setFromIsoCode(USD);
		dollarToInrOnFirstDateButDifferentDate.setToIsoCode(INR);
		dollarToInrOnFirstDateButDifferentDate.setEffectiveDate(firstDate);
		dollarToInrOnFirstDateButDifferentDate.setRate(EXCHANGE_RATE_ON_SECOND_DATE);
		
		currencyExchange = currencyExchangeFactory.create(Arrays.asList(dollarToInrOnFirstDate, 
				dollarToInrOnFirstDateButDifferentDate));
		
		BigDecimal expected = new BigDecimal(10).multiply(new BigDecimal(EXCHANGE_RATE_ON_SECOND_DATE), mathContext);
		
		assertThat(currencyExchange.convert(BigDecimal.TEN, USD, INR, secondDate), is(equalTo(expected)));
	}
}