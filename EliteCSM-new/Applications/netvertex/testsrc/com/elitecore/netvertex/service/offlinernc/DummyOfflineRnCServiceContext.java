package com.elitecore.netvertex.service.offlinernc;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchange;
import com.elitecore.netvertex.service.offlinernc.partner.Partner;
import com.elitecore.netvertex.service.offlinernc.util.BigDecimalFormatter;
import com.elitecore.netvertex.service.offlinernc.util.RoundingModeTypes;

public class DummyOfflineRnCServiceContext implements OfflineRnCServiceContext {

	private ServerContext dummyNetvertexServerContext;
	private Map<String, Partner> partners = new HashMap<>();
	private CurrencyExchange currencyExchange;
	private BigDecimalFormatter bigDecimalFormatter = new BigDecimalFormatter(6, RoundingModeTypes.TRUNCATE);
	@Override
	public ServerContext getServerContext() {
		return dummyNetvertexServerContext;
	}
	
	public void setServerContext(ServerContext dummyNetVertexServerContext) {
		this.dummyNetvertexServerContext = dummyNetVertexServerContext;
	}

	@Override
	public Partner getPartner(String partnerName) {
		return partners.get(partnerName);
	}
	
	public void addPartner(Partner partner) {
		partners.put(Preconditions.checkNotNull(partner.getName(), "Partner name is null"), partner);
	}

	@Override
	public CurrencyExchange getCurrencyExchange() {
		return currencyExchange;
	}
	
	public void setCurrencyExchange(CurrencyExchange currencyExchange) {
		this.currencyExchange = currencyExchange;
	}

	@Override
	public BigDecimalFormatter getBigDecimalFormatter() {
		return bigDecimalFormatter;
	}
	
	public void setBigDecimalFormatter(BigDecimalFormatter bigDecimalFormatter) {
		this.bigDecimalFormatter = bigDecimalFormatter;
	}
}
