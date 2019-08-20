package com.elitecore.netvertex.service.offlinernc.currency;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 
 * @author narendra.pathai
 *
 */
public class IdentityConverter implements CurrencyConverter {

	@Override
	public BigDecimal convert(BigDecimal amount, Timestamp date) throws CurrencyExchangeException {
		return amount;
	}

}
