package com.elitecore.netvertex.service.offlinernc.currency;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 
 * @author narendra.pathai
 *
 */
public class DefaultConverter implements CurrencyConverter {
	
	private String fromIsoCode;
	private String toIsoCode;
	private TreeMap<Timestamp, Double> effectiveDateWiseRate;
	private static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(2, RoundingMode.HALF_EVEN);
	
	public DefaultConverter(String fromIsoCode, String toIsoCode) {
		this.fromIsoCode = fromIsoCode;
		this.toIsoCode = toIsoCode;
		this.effectiveDateWiseRate = new TreeMap<>();
	}

	@Override
	public BigDecimal convert(BigDecimal amount, Timestamp date) throws CurrencyExchangeException {
		Entry<Timestamp, Double> effectiveEntry = effectiveDateWiseRate.floorEntry(date);
		if (effectiveEntry == null) {
			throw new CurrencyExchangeException("No currency exchange: " + fromIsoCode + " -> " + toIsoCode
					+ " valid for date: " + date);
		}

		BigDecimal rate = BigDecimal.valueOf(effectiveEntry.getValue());

		return amount.multiply(rate, DEFAULT_MATH_CONTEXT);
		
	}
	
	void addRate(Double rate, Timestamp startDate) {
		effectiveDateWiseRate.put(startDate, rate);
	}
}
