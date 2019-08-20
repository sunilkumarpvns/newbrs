package com.elitecore.netvertex.service.offlinernc.currency;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 
 * @author narendra.pathai
 *
 */
public interface CurrencyConverter {
	BigDecimal convert(BigDecimal amount, Timestamp date) throws CurrencyExchangeException;
}
