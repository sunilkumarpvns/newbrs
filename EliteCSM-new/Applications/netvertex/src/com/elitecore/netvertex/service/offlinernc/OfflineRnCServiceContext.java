package com.elitecore.netvertex.service.offlinernc;

import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchange;
import com.elitecore.netvertex.service.offlinernc.partner.Partner;
import com.elitecore.netvertex.service.offlinernc.util.BigDecimalFormatter;

public interface OfflineRnCServiceContext extends ServiceContext {

	Partner getPartner(String partnerName);

	CurrencyExchange getCurrencyExchange();
	
	BigDecimalFormatter getBigDecimalFormatter(); 
}
