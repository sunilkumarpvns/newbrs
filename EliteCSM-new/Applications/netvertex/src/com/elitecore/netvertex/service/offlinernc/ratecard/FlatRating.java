
package com.elitecore.netvertex.service.offlinernc.ratecard;

import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST1;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.CHARGE_PER_UOM;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.PARTNER_CURRENCY_ISO_CODE;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.RATE1;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.SYSTEM_CURRENCY_ACCOUNTED_COST;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.TOTAL_PULSE1;
import static com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory.KEY_SEPERATOR;
import static com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions.checkKeyNotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.elitecore.netvertex.gateway.file.util.OFCSFileHelper;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchange;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchangeException;
import com.elitecore.netvertex.service.offlinernc.util.BigDecimalFormatter;
import com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions;

public class FlatRating implements RatingBehavior {

	private static final String MODULE = "FLAT-RATING-BEHAVIOR";
	private final List<RateSlab> slabs;
	private final Timestamp fromDate;
	private final String key;
	private final String rateCardCurrencyIsoCode;
	private final CurrencyExchange currencyExchange;
	private final String systemCurrencyIsoCode;
	private final String ratingKey;
	private final String timeFormat;
	private final BigDecimalFormatter bigDecimalFormatter;

	public FlatRating(String key, Timestamp fromDate, List<RateSlab> slabs, String rateCardCurrencyIsoCode,
			String systemCurrencyIsoCode, CurrencyExchange currencyExchange, String ratingKey, String timeFormat, BigDecimalFormatter bigDecimalFormatter) {
		this.key = key;
		this.fromDate = fromDate;
		this.slabs = slabs;
		this.rateCardCurrencyIsoCode = rateCardCurrencyIsoCode;
		this.systemCurrencyIsoCode = systemCurrencyIsoCode;
		this.currencyExchange = currencyExchange;
		this.ratingKey = ratingKey;
		this.timeFormat = timeFormat;
		this.bigDecimalFormatter = bigDecimalFormatter;
	}

	@Override
	public boolean apply(RatingRequest ratingRequest, RnCResponse response) throws OfflineRnCException {
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().print("[ " + MODULE + " ] Rating mode: FLAT");
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().incrementIndentation();
		
		for (RateSlab rateSlab : slabs) {
			if (rateSlab.apply(ratingRequest, response) && ratingRequest.isRatingCompleted()) {
				break;
			}
		}
		if (ratingRequest.isRatingCompleted()) {
			
			BigDecimal acctCostSystemCurrency;
			try {
				acctCostSystemCurrency = currencyExchange.convert(ratingRequest.getSlabOneCost(), 
						rateCardCurrencyIsoCode,
						systemCurrencyIsoCode,
						OFCSFileHelper.timeStampOf(checkKeyNotNull(ratingRequest.getRncRequest(), ratingKey), timeFormat));
			} catch (ParseException e) {
				ratingRequest.getRncRequest().getTraceWriter().println();
				ratingRequest.getRncRequest().getTraceWriter().print("Invalid date format configured.");
				throw new OfflineRnCException(OfflineRnCErrorCodes.INVALID_EDR, OfflineRnCErrorMessages.INVALID_EDR,
						OfflineRnCErrorMessages.INVALID_DATE_FORMAT + "-" + ratingKey);
			} catch (CurrencyExchangeException e) { //NOSONAR
				ratingRequest.getRncRequest().getTraceWriter().println();
				ratingRequest.getRncRequest().getTraceWriter().print("Couldn't convert acocunted cost to system currency, Reason: "
						+ e.getMessage());
				throw new OfflineRnCException(OfflineRnCErrorCodes.EXCHANGE_RATE_NOT_FOUND, OfflineRnCErrorMessages.EXCHANGE_RATE_NOT_FOUND, e);
			}
			
			response.setParameter(TOTAL_PULSE1.getName(), ratingRequest.getSlabOnePulse().toPlainString());
			response.setParameter(RATE1.getName(), bigDecimalFormatter.format(ratingRequest.getSlabOneRatePerPulse()));
			response.setParameter(CHARGE_PER_UOM.getName(), bigDecimalFormatter.format(ratingRequest.getChargePerUom()));
			response.setParameter(ACCOUNTED_COST1.getName(), bigDecimalFormatter.format(ratingRequest.getSlabOneCost()));
			response.setParameter(SYSTEM_CURRENCY_ACCOUNTED_COST.getName(), bigDecimalFormatter.format(acctCostSystemCurrency));
			response.setParameter(PARTNER_CURRENCY_ISO_CODE.getName(), rateCardCurrencyIsoCode);
			
		} else {
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- Failed to apply rate, Reason: No suitable slab found in this entry");
		}
		return ratingRequest.isRatingCompleted();
	}

	public boolean isApplicable(RnCRequest request, RnCResponse response, String keyOne, String keyTwo) throws OfflineRnCException {
		String ratingKeyValue = RnCPreConditions.checkKeyNotNull(request, ratingKey);
		Date ratingKeyDate;
		try {
			ratingKeyDate  = OFCSFileHelper.dateOf(ratingKeyValue, timeFormat);
		} catch (ParseException e) {
			throw new OfflineRnCException(OfflineRnCErrorCodes.INVALID_EDR, OfflineRnCErrorMessages.INVALID_EDR,
					OfflineRnCErrorMessages.INVALID_DATE_FORMAT + "-" + ratingKey);
		}
		
		String receivedkey = RnCPreConditions.checkKeyNotNull(response, keyOne)
				+ KEY_SEPERATOR 
				+ RnCPreConditions.checkKeyNotNull(response, keyTwo);
		return ((receivedkey.equals(key)) && (ratingKeyDate.compareTo(fromDate) >= 0));
	}

	@Override
	public String getKey() {
		return key;
	}
}
