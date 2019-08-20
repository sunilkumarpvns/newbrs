package com.elitecore.netvertex.service.offlinernc.currency;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.elitecore.corenetvertex.pd.currency.CurrencyData;
import com.elitecore.corenetvertex.pm.HibernateReader;

/**
 * 
 * @author narendra.pathai
 *
 */
public class CurrencyExchangeFactory {

	@Nonnull private SessionFactory sessionFactory;

	public CurrencyExchangeFactory(@Nonnull SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return an instance of {@link CurrencyExchangeFactory} using hibernate session factory.
	 */
	public CurrencyExchange create() {
		Session session = sessionFactory.openSession();
		
		List<CurrencyData> currencies = HibernateReader.readAll(CurrencyData.class, session);
		
		return create(currencies);
	}

	/**
	 * @param currencies
	 * @return an instance of {@link CurrencyExchangeFactory} using list of currency exchange rates
	 */
	public CurrencyExchange create(List<CurrencyData> currencies) {
		Map<String, CurrencyConverter> currencyExchangeCodeToCurrencySet = new ConcurrentHashMap<>();
		
		for (CurrencyData currencyData : currencies) {
			((DefaultConverter) currencyExchangeCodeToCurrencySet.computeIfAbsent(CurrencyExchange.exchangeKey(currencyData), k -> new DefaultConverter(currencyData.getFromIsoCode(),
					currencyData.getToIsoCode())))
				.addRate(currencyData.getRate(), currencyData.getEffectiveDate());
			
			currencyExchangeCodeToCurrencySet.computeIfAbsent(CurrencyExchange.exchangeCode(currencyData.getFromIsoCode(),
					currencyData.getFromIsoCode()), k -> new IdentityConverter());
			
			currencyExchangeCodeToCurrencySet.computeIfAbsent(CurrencyExchange.exchangeCode(currencyData.getToIsoCode(),
					currencyData.getToIsoCode()), k -> new IdentityConverter());
		}
		
		return new CurrencyExchange(currencyExchangeCodeToCurrencySet);
	}
}
