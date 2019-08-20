package com.elitecore.netvertex.service.offlinernc.currency;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import javax.annotation.Nonnull;

import com.elitecore.corenetvertex.pd.currency.CurrencyData;

/**
 * 
 * @author narendra.pathai
 *
 */
public class CurrencyExchange {

	private Map<String, CurrencyConverter> currencyExchangeCodeToCurrencyConverter;

	public CurrencyExchange(Map<String, CurrencyConverter> currencyExchangeCodeToCurrencyConverter) {
		this.currencyExchangeCodeToCurrencyConverter = currencyExchangeCodeToCurrencyConverter;
	}

	/**
	 * @return converted amount after applying the exchange rate. Returns the same amount in case of
	 * identity conversion i.e. <code>fromIsoCode</code> and <code>toIsoCode</code> are same.
	 * @throws CurrencyExchangeException if currency exchange is not known or no effective current exchange
	 * is found for the requested date.
	 */
	public BigDecimal convert(@Nonnull BigDecimal amount, @Nonnull String fromIsoCode, @Nonnull String toIsoCode, 
			@Nonnull Timestamp date) throws CurrencyExchangeException {
		
		CurrencyConverter converter = currencyExchangeCodeToCurrencyConverter.get(exchangeCode(fromIsoCode, toIsoCode));
		if (converter == null) {
			throw new CurrencyExchangeException("Currency exchange: " + fromIsoCode + " -> " + toIsoCode + " is unknown");
		}
		
		return converter.convert(amount, date);
	}

	static String exchangeKey(CurrencyData currencyData) {
		return exchangeCode(currencyData.getFromIsoCode(), currencyData.getToIsoCode());
	}
	
	static String exchangeCode(String fromIsoCode, String toIsoCode) {
		return fromIsoCode + "-" + toIsoCode;
	}
}
