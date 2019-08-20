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

public class IncrementalRating implements RatingBehavior {

	private static final String MODULE = "INCR-RATING-BEHAVIOR";
	private final List<RateSlab> incrementalSlabs;
	private final Timestamp fromDate;
	private final String key;
	private final String rateCardCurrencyIsoCode;
	private final String systemCurrencyIsoCode;
	private final CurrencyExchange currencyExchange;
	private final String multiValueSeparator;
	private final String ratingKey;
	private final String timeFormat;
	private final BigDecimalFormatter bigDecimalFormatter;
	
	public IncrementalRating(String key, Timestamp fromDate, List<RateSlab> incrementalSlabs,
			String rateCardCurrencyIsoCode, String systemCurrencyIsoCode, String multiValueSeparator, 
			CurrencyExchange currencyExchange, String ratingKey, String timeFormat, BigDecimalFormatter bigDecimalFormatter) {

		this.key = key;
		this.fromDate = fromDate;
		this.incrementalSlabs = incrementalSlabs;
		this.rateCardCurrencyIsoCode = rateCardCurrencyIsoCode;
		this.systemCurrencyIsoCode = systemCurrencyIsoCode;
		this.currencyExchange = currencyExchange;
		this.multiValueSeparator = multiValueSeparator;
		this.ratingKey = ratingKey;
		this.timeFormat = timeFormat;
		this.bigDecimalFormatter = bigDecimalFormatter;
	}

	@Override
	public boolean apply(RatingRequest ratingRequest, RnCResponse response) throws OfflineRnCException {
		StringBuilder totalPulse = new StringBuilder();
		StringBuilder ratePerPulse = new StringBuilder();
		StringBuilder chargePerUom = new StringBuilder();
		
		BigDecimal accountedCost = new BigDecimal("0");
		BigDecimal totalCostInSystemCurrency = new BigDecimal("0");
		
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().print("[ " + MODULE + " ] Rating mode: INCREMENTAL");
		ratingRequest.getRncRequest().getTraceWriter().println();
		ratingRequest.getRncRequest().getTraceWriter().incrementIndentation();
		
		int previousAccountedUsage = 0;
		ratingRequest.setPreviousAccountedUsage(previousAccountedUsage);
		for (RateSlab rateSlab : incrementalSlabs) {
			boolean applied = rateSlab.apply(ratingRequest, response);
			assert applied == true;
			
			
			BigDecimal acctCostSystemCurrency;
			try {
				acctCostSystemCurrency = currencyExchange.convert(ratingRequest.getSlabOneCost(), 
						rateCardCurrencyIsoCode,
						systemCurrencyIsoCode,
						OFCSFileHelper.timeStampOf(checkKeyNotNull(ratingRequest.getRncRequest() , ratingKey), timeFormat));

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
			
			totalPulse.append(ratingRequest.getSlabOnePulse()).append(multiValueSeparator);
			ratePerPulse.append(bigDecimalFormatter.format(ratingRequest.getSlabOneRatePerPulse())).append(multiValueSeparator);
			chargePerUom.append(bigDecimalFormatter.format(ratingRequest.getChargePerUom())).append(multiValueSeparator);
			accountedCost = accountedCost.add(ratingRequest.getSlabOneCost());
			totalCostInSystemCurrency = totalCostInSystemCurrency.add(acctCostSystemCurrency);
			
			if (ratingRequest.isRatingCompleted()) {
				break;
			}
		}
		
		if (ratingRequest.isRatingCompleted()) {

			totalPulse.deleteCharAt(totalPulse.length() - 1);
			ratePerPulse.deleteCharAt(ratePerPulse.length() - 1);
			chargePerUom.deleteCharAt(chargePerUom.length() - 1);
		
			response.setParameter(TOTAL_PULSE1.getName(), totalPulse.toString());
			response.setParameter(RATE1.getName(), ratePerPulse.toString());
			response.setParameter(CHARGE_PER_UOM.getName(), chargePerUom.toString());
			response.setParameter(ACCOUNTED_COST1.getName(), bigDecimalFormatter.format(accountedCost));
			response.setParameter(SYSTEM_CURRENCY_ACCOUNTED_COST.getName(), bigDecimalFormatter.format(totalCostInSystemCurrency));
			response.setParameter(PARTNER_CURRENCY_ISO_CODE.getName(), rateCardCurrencyIsoCode);
			
			ratingRequest.getRncRequest().getTraceWriter().decrementIndentation();
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- Pulse: " + totalPulse.toString());
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- Charge Per UOM: " + chargePerUom.toString());
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- Rate per pulse: " + ratePerPulse.toString());
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- Accounted Cost: " + bigDecimalFormatter.format(accountedCost));
			ratingRequest.getRncRequest().getTraceWriter().println();
			ratingRequest.getRncRequest().getTraceWriter().print("- Rate applied");
			
		} else {
			ratingRequest.reset();
			ratingRequest.getRncRequest().getTraceWriter().print("Failed to apply rate, Reason: No suitable slab found in this entry");
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