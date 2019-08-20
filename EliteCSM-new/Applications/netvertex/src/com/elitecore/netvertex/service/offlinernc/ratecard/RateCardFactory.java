package com.elitecore.netvertex.service.offlinernc.ratecard;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionDetail;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionRelation;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.OfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.util.BigDecimalFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST1;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST2;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST3;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.CHARGE_PER_UOM;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.PARTNER_CURRENCY_ISO_CODE;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.RATE1;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.RATE2;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.RATE3;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.SYSTEM_CURRENCY_ACCOUNTED_COST;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.TOTAL_PULSE1;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.TOTAL_PULSE2;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.TOTAL_PULSE3;
import static com.elitecore.corenetvertex.constants.TierRateType.FLAT;
import static com.elitecore.corenetvertex.constants.TierRateType.INCREMENTAL;
import static com.elitecore.corenetvertex.constants.TierRateType.fromVal;

public class RateCardFactory {

	private static final int SLAB_UNLIMITED = -1;
	static final String KEY_SEPERATOR = "--";
	
	private OfflineRnCServiceContext serviceContext;
	private SystemParameterConfiguration systemParameterConfiguration;
	private BigDecimalFormatter bigDecimalFormatter;
	
	public RateCardFactory(OfflineRnCServiceContext serviceContext, SystemParameterConfiguration systemParameterConfiguration) {
		this.serviceContext = serviceContext ;
		this.systemParameterConfiguration = systemParameterConfiguration;
		this.bigDecimalFormatter = serviceContext.getBigDecimalFormatter();
	}

	public RateCard createRateCard1(RateCardData data, String currency, String accountEffect) throws InitializationFailedException {
		final RateCard rateCard = createRateCard(data, currency, accountEffect);
		return new RateCardImpl(rateCard, TOTAL_PULSE1.getName(), RATE1.getName(), CHARGE_PER_UOM.getName(), ACCOUNTED_COST1.getName(), PARTNER_CURRENCY_ISO_CODE.getName());
	}
	
	public RateCard createRateCard2(RateCardData data, String currency, String accountEffect) throws InitializationFailedException {
		final RateCard rateCard = createRateCard(data, currency, accountEffect);
		return new RateCardImpl(rateCard, TOTAL_PULSE2.getName(), RATE2.getName(), null, ACCOUNTED_COST2.getName(), null);
	}

	public RateCard createRateCard3(RateCardData data, String currency, String accountEffect) throws InitializationFailedException {
		final RateCard rateCard = createRateCard(data, currency, accountEffect);
		return new RateCardImpl(rateCard, TOTAL_PULSE3.getName(), RATE3.getName(), null, ACCOUNTED_COST3.getName(), null);
	}

	private RateCard createRateCard(RateCardData data, String currency, String accountEffect) {
		String systemCurrencyIsoCode = systemParameterConfiguration.getSystemCurrency();
		String multiValueSeparator = systemParameterConfiguration.getMultiValueSeparator();

		String sessionTime = systemParameterConfiguration.getRateSelectionWhenDateChange();

		String timeFormat = systemParameterConfiguration.getEdrDateTimeStampFormat();
		

		List<RateCardVersionRelation> rateCardVersionRelation = data.getRateCardVersionRelation();
		
		rateCardVersionRelation.sort((o1, o2) -> {
			if (o1.getOrderNumber() < o2.getOrderNumber()) {
				return 1;
			} else if (o1.getOrderNumber() > o2.getOrderNumber()) {
				return SLAB_UNLIMITED;
			} else {
				return 0;
			}
		});
		
		List<RateCardVersion> rateCardVersions = new ArrayList<>();
		
		RateCard rateCard = null;
		OfflineRnCKeyConstants ratingKey = null;
		Uom rateUom = Uom.valueOf(data.getRateUom());
		Preconditions.checkNotNull(rateUom, "Rate-UOM is null" );
		
		if (rateUom.equals(Uom.SECOND) || rateUom.equals(Uom.MINUTE) || rateUom.equals(Uom.HOUR)) {
			rateCard = new VoiceRateCard(data.getName(), 
					accountEffect,
					data.getLabelKey1(),
					data.getLabelKey2(),
					rateCardVersions);
			ratingKey = OfflineRnCKeyConstants.SESSION_TIME;
		} else if (rateUom.equals(Uom.BYTE) || rateUom.equals(Uom.KB) || rateUom.equals(Uom.MB) || rateUom.equals(Uom.GB)){
			rateCard = new DataRateCard(data.getName(),
					accountEffect,
					data.getLabelKey1(),
					data.getLabelKey2(),
					rateCardVersions);
			ratingKey = OfflineRnCKeyConstants.SESSION_TOTAL_DATA_TRANSFER;
		} else {
			throw new AssertionError("Unknown rate UoM: " + rateUom);
		}
		
		for (RateCardVersionRelation versionRelation: rateCardVersionRelation) {
			RatingBehavior ratingBehavior;
			List<RatingBehavior> ratingBehaviors = new ArrayList<>();
			List<RateSlab> rateSlabs = new ArrayList<>();
			
			List<RateCardVersionDetail> rateCardVersionDetails = versionRelation.getRateCardVersionDetail();
			for (RateCardVersionDetail rateCardVersionDetail : rateCardVersionDetails) {
				Preconditions.checkNotNull(rateCardVersionDetail.getTierRateType(), "Rate card tier type is null for rate card " + data.getName());
				if (FLAT.equals(fromVal(rateCardVersionDetail.getTierRateType()))) {
					createAndAddFlatSlabs(rateSlabs,
							rateCardVersionDetail,
							data.getPulseUom(),
							data.getRateUom(),
							ratingKey);
				} else if (INCREMENTAL.equals(fromVal(rateCardVersionDetail.getTierRateType()))) {
					createAndAddIncrementalSlabs(rateSlabs,
							rateCardVersionDetail,
							data.getPulseUom(),
							data.getRateUom(),
							ratingKey);
				} else {
					throw new AssertionError("Invalid tier type: " + rateCardVersionDetail.getTierRateType());
				}
				
				String keyValueOne = rateCardVersionDetail.getLabel1();
				String keyValueTwo = rateCardVersionDetail.getLabel2();
				keyValueOne = (Strings.isNullOrBlank(keyValueOne) ? "" : keyValueOne);
				keyValueTwo = (Strings.isNullOrBlank(keyValueTwo) ? "" : keyValueTwo);
			
				if (FLAT.equals(fromVal(rateCardVersionDetail.getTierRateType()))) {
					ratingBehavior = new FlatRating(keyValueOne + KEY_SEPERATOR + keyValueTwo, 
							rateCardVersionDetail.getFromDate(),
							rateSlabs,
							currency,
							systemCurrencyIsoCode, 
							serviceContext.getCurrencyExchange(), sessionTime, timeFormat, bigDecimalFormatter);

				} else if (INCREMENTAL.equals(fromVal(rateCardVersionDetail.getTierRateType()))) {
					ratingBehavior = new IncrementalRating(keyValueOne + KEY_SEPERATOR + keyValueTwo, 
							rateCardVersionDetail.getFromDate(),
							rateSlabs,
							currency,
							systemCurrencyIsoCode,
							multiValueSeparator,
							serviceContext.getCurrencyExchange(), sessionTime, timeFormat, bigDecimalFormatter);
				} else {
					throw new AssertionError("Invalid tier type: " + rateCardVersionDetail.getTierRateType());
				}
				
				ratingBehaviors.add(ratingBehavior);
			}
			rateCardVersions.add(new RateCardVersion(versionRelation.getVersionName(), sessionTime, ratingBehaviors));
		}
		return rateCard;
	}
	
	private void createAndAddIncrementalSlabs(List<RateSlab> rateSlabs, RateCardVersionDetail rateCardVersionDetail, String pulseUom, String rateUom, OfflineRnCKeyConstants ratingKey) {
		RateSlab rateSlabOne;
		RateSlab rateSlabTwo;
		RateSlab rateSlabThree;
		
		if (rateCardVersionDetail.getSlab1() != null) {

			rateSlabOne = new IncrementalSlab(rateCardVersionDetail.getSlab1(),
					BigDecimal.valueOf(rateCardVersionDetail.getPulse1()),
					BigDecimal.valueOf(rateCardVersionDetail.getRate1()),
					ratingKey,
					Uom.valueOf(pulseUom),
					calculateRatePerPulse(BigDecimal.valueOf(rateCardVersionDetail.getPulse1())
							, Uom.valueOf(pulseUom)
							, BigDecimal.valueOf(rateCardVersionDetail.getRate1())
							, Uom.valueOf(rateUom)
							, ratingKey), bigDecimalFormatter);
			
			rateSlabs.add(rateSlabOne);
		}

		if (rateCardVersionDetail.getSlab2() != null) {
				rateSlabTwo = new IncrementalSlab(rateCardVersionDetail.getSlab2(),
						BigDecimal.valueOf(rateCardVersionDetail.getPulse2()),
						BigDecimal.valueOf(rateCardVersionDetail.getRate2()),
						ratingKey,
						Uom.valueOf(pulseUom),
						calculateRatePerPulse(BigDecimal.valueOf(rateCardVersionDetail.getPulse2())
								, Uom.valueOf(pulseUom)
								, BigDecimal.valueOf(rateCardVersionDetail.getRate2())
								, Uom.valueOf(rateUom)
								, ratingKey), bigDecimalFormatter);

			rateSlabs.add(rateSlabTwo);
		}

		if (rateCardVersionDetail.getSlab3() != null) {

			rateSlabThree = new IncrementalSlab(rateCardVersionDetail.getSlab3(),
					BigDecimal.valueOf(rateCardVersionDetail.getPulse3()),
					BigDecimal.valueOf(rateCardVersionDetail.getRate3()),
					ratingKey,
					Uom.valueOf(pulseUom),
					calculateRatePerPulse(BigDecimal.valueOf(rateCardVersionDetail.getPulse3())
							, Uom.valueOf(pulseUom)
							, BigDecimal.valueOf(rateCardVersionDetail.getRate3())
							, Uom.valueOf(rateUom)
							, ratingKey), bigDecimalFormatter);

			rateSlabs.add(rateSlabThree);
		}
	}

	private void createAndAddFlatSlabs(List<RateSlab> rateSlabs, RateCardVersionDetail rateCardVersionDetail, String pulseUom, String rateUom, OfflineRnCKeyConstants ratingKey) {
		RateSlab rateSlabOne;
		RateSlab rateSlabTwo;
		RateSlab rateSlabThree;

		if (rateCardVersionDetail.getSlab1() != null) {

			rateSlabOne = new FlatSlab(rateCardVersionDetail.getSlab1()
					, BigDecimal.valueOf(rateCardVersionDetail.getPulse1())
					, BigDecimal.valueOf(rateCardVersionDetail.getRate1())
					, ratingKey
					, Uom.valueOf(pulseUom)
					, calculateRatePerPulse(BigDecimal.valueOf(rateCardVersionDetail.getPulse1())
							, Uom.valueOf(pulseUom)
							, BigDecimal.valueOf(rateCardVersionDetail.getRate1())
							, Uom.valueOf(rateUom)
							, ratingKey), bigDecimalFormatter);

			rateSlabs.add(rateSlabOne);
		}

		if (rateCardVersionDetail.getSlab2() != null) {
			rateSlabTwo = new FlatSlab(rateCardVersionDetail.getSlab2(),
					BigDecimal.valueOf(rateCardVersionDetail.getPulse2()),
					BigDecimal.valueOf(rateCardVersionDetail.getRate2()),
					ratingKey,
					Uom.valueOf(pulseUom),
					calculateRatePerPulse(BigDecimal.valueOf(rateCardVersionDetail.getPulse2()),
							Uom.valueOf(pulseUom),
							BigDecimal.valueOf(rateCardVersionDetail.getRate2()),
							Uom.valueOf(rateUom),
							ratingKey), bigDecimalFormatter);

			rateSlabs.add(rateSlabTwo);
		}

		if (rateCardVersionDetail.getSlab3() != null) {

			rateSlabThree = new FlatSlab(rateCardVersionDetail.getSlab3(),
					BigDecimal.valueOf(rateCardVersionDetail.getPulse3()),
					BigDecimal.valueOf(rateCardVersionDetail.getRate3()),
					ratingKey,
					Uom.valueOf(pulseUom),
					calculateRatePerPulse(BigDecimal.valueOf(rateCardVersionDetail.getPulse3())
							, Uom.valueOf(pulseUom)
							, BigDecimal.valueOf(rateCardVersionDetail.getRate3())
							, Uom.valueOf(rateUom)
							, ratingKey), bigDecimalFormatter);

			rateSlabs.add(rateSlabThree);
		}
	}

	private BigDecimal calculateRatePerPulse(BigDecimal pulse, Uom pulseUom, BigDecimal rate, Uom rateUom, OfflineRnCKeyConstants ratingKey) {
		if (ratingKey == OfflineRnCKeyConstants.SESSION_TIME) {
			return calculateRatePerPulseVoice(pulse, pulseUom, rate, rateUom);
		} else {
			return calculateRatePerPulseData(pulse, pulseUom, rate, rateUom);
		}
	}
	
	private BigDecimal calculateRatePerPulseData(BigDecimal pulse, Uom pulseUom, BigDecimal rate, Uom rateUom) {
		BigDecimal localRatePerPulse = null;

		if (rateUom == pulseUom) {
			localRatePerPulse = rate.multiply(pulse);
		} else {

			if (Uom.BYTE == pulseUom) {
				BigDecimal ratePerByte = null;
				if (Uom.KB == rateUom) {
					ratePerByte = rate.divide(BigDecimal.valueOf(DataUnit.KB.toBytes(1)), 20,  RoundingMode.HALF_EVEN);
				} else if (Uom.MB == rateUom) {
					ratePerByte = rate.divide(BigDecimal.valueOf(DataUnit.MB.toBytes(1)), 20, RoundingMode.HALF_EVEN);
				} else if (Uom.GB == rateUom) {
					ratePerByte = rate.divide(BigDecimal.valueOf(DataUnit.GB.toBytes(1)), 20, RoundingMode.HALF_EVEN); 
				} 
				localRatePerPulse = pulse.multiply(ratePerByte);
			} else if (Uom.KB == pulseUom) {
				BigDecimal ratePerKb = null;
				if (Uom.BYTE == rateUom) {
					ratePerKb = rate.multiply(BigDecimal.valueOf(DataUnit.KB.toBytes(1)));
				} else if (Uom.MB == rateUom) {
					ratePerKb = rate.divide(BigDecimal.valueOf(DataUnit.MB.toKB(1)), 20, RoundingMode.HALF_EVEN);
				} else if (Uom.GB == rateUom) {
					ratePerKb = rate.divide(BigDecimal.valueOf(DataUnit.GB.toKB(1)), 20, RoundingMode.HALF_EVEN);
				}
				localRatePerPulse = pulse.multiply(ratePerKb);
			} else if (Uom.MB == pulseUom) {
				BigDecimal ratePerMb = null;
				if (Uom.BYTE == rateUom) {
					ratePerMb = rate.multiply(BigDecimal.valueOf(DataUnit.MB.toBytes(1)));
				} else if (Uom.KB == rateUom) {
					ratePerMb = rate.multiply(BigDecimal.valueOf(DataUnit.MB.toKB(1)));
				} else if (Uom.GB == rateUom) {
					ratePerMb = rate.divide(BigDecimal.valueOf(DataUnit.GB.toMB(1)), 20, RoundingMode.HALF_EVEN);
				}
				localRatePerPulse = pulse.multiply(ratePerMb);
			} else {
				BigDecimal ratePerGb = null;
				if (Uom.BYTE == rateUom) {
					ratePerGb = rate.divide(BigDecimal.valueOf(DataUnit.GB.toBytes(1)), 20, RoundingMode.HALF_EVEN);
				} else if (Uom.KB == rateUom) {
					ratePerGb = rate.divide(BigDecimal.valueOf(DataUnit.GB.toKB(1)), 20, RoundingMode.HALF_EVEN);
				} else if (Uom.MB == rateUom) {
					ratePerGb = rate.divide(BigDecimal.valueOf(DataUnit.GB.toMB(1)), 20, RoundingMode.HALF_EVEN);
				}
				localRatePerPulse = pulse.multiply(ratePerGb);
			}
		}

		return bigDecimalFormatter.scale(localRatePerPulse);
	}
	
	private BigDecimal calculateRatePerPulseVoice(BigDecimal pulse, Uom pulseUom, BigDecimal rate, Uom rateUom) {
		BigDecimal localRatePerPulse = null;

		if (rateUom == pulseUom) {
			localRatePerPulse = rate.multiply(pulse);
		} else {
			if (Uom.SECOND == pulseUom) {
				BigDecimal ratePerSecond = null;
				if (Uom.HOUR == rateUom) {
					ratePerSecond = rate.divide(BigDecimal.valueOf(TimeUnit.HOUR.toSeconds(1)), 20, RoundingMode.HALF_EVEN);
				} else if (Uom.MINUTE == rateUom) {
					ratePerSecond = rate.divide(BigDecimal.valueOf(TimeUnit.MINUTE.toSeconds(1)), 20, RoundingMode.HALF_EVEN); 
				}

				localRatePerPulse = pulse.multiply(ratePerSecond);
			} else if(Uom.MINUTE == pulseUom) {
				BigDecimal ratePerMinute = null;
				if (Uom.SECOND == rateUom) {
					ratePerMinute = rate.multiply(BigDecimal.valueOf(TimeUnit.MINUTE.toSeconds(1)));
				} else if (Uom.HOUR == rateUom) {
					ratePerMinute = rate.divide(BigDecimal.valueOf(60), 20, RoundingMode.HALF_EVEN); 
				}

				localRatePerPulse = pulse.multiply(ratePerMinute);
			} else {
				BigDecimal ratePerHour = null;
				if (Uom.SECOND == rateUom) {
					ratePerHour = rate.multiply(BigDecimal.valueOf(TimeUnit.HOUR.toSeconds(1)));
				} else if(Uom.MINUTE == rateUom) {
					ratePerHour = rate.multiply(BigDecimal.valueOf(TimeUnit.MINUTE.toSeconds(1)));
				}

				localRatePerPulse = pulse.multiply(ratePerHour);
			}
		}
		return bigDecimalFormatter.scale(localRatePerPulse);
	}

	private class RateCardImpl implements RateCard {

		private final RateCard rateCard;
		private String totalPulse;
		private String rate;
		private String chargePerUom;
		private String accountedCost;
		private String isoCode;

		public RateCardImpl(RateCard rateCard, String totalPulse, String rate, String chargePerUom, String accountedCost, String isoCode) {
			this.rateCard = rateCard;
			this.totalPulse = totalPulse;
			this.rate = rate;
			this.chargePerUom = chargePerUom;
			this.accountedCost = accountedCost;
			this.isoCode = isoCode;
		}

		@Override
        public String getName() {
            return rateCard.getName();
        }

		@Override
        public boolean apply(RnCRequest request, RnCResponse response) throws OfflineRnCException {
            boolean applied = rateCard.apply(request, response);
            if (applied) {
                response.setAttribute(totalPulse, (String)response.getParameter(TOTAL_PULSE1.getName()));
                response.setAttribute(rate, (String)response.getParameter(RATE1.getName()));
                if(chargePerUom != null) {
					response.setAttribute(chargePerUom, (String) response.getParameter(CHARGE_PER_UOM.getName()));
				}
                response.setAttribute(accountedCost, (String)response.getParameter(ACCOUNTED_COST1.getName()));
                if(isoCode != null) {
					response.setAttribute(isoCode, (String) response.getParameter(PARTNER_CURRENCY_ISO_CODE.getName()));
				}

                updateAccountedCost(response);
                updateSystemCurrencyAccountedCost(response);
                displayEnrichedKeys(request, response);
            }
            return applied;
        }

		private void displayEnrichedKeys(RnCRequest request, RnCResponse response) {
            request.getTraceWriter().println();
            request.getTraceWriter().println(totalPulse + response.getAttribute(totalPulse));
            request.getTraceWriter().println(rate + response.getAttribute(rate));
            if(chargePerUom != null) {
				request.getTraceWriter().println(chargePerUom + response.getAttribute(chargePerUom));
			}
			request.getTraceWriter().println(accountedCost + response.getAttribute(accountedCost));
            request.getTraceWriter().println("- Accounted-Cost: " + response.getAttribute(ACCOUNTED_COST));
            request.getTraceWriter().println("- Rate Card " +  getName() + " applied");
        }

		private void updateSystemCurrencyAccountedCost(RnCResponse response) {
			String prevSystemCurrencyAccountedCostStr = response.getAttribute(SYSTEM_CURRENCY_ACCOUNTED_COST);
			if (Strings.isNullOrBlank(prevSystemCurrencyAccountedCostStr)) {
				response.setAttribute(SYSTEM_CURRENCY_ACCOUNTED_COST.getName(), (String)response.getParameter(SYSTEM_CURRENCY_ACCOUNTED_COST.getName()));
			} else {
				BigDecimal prevSystemCurrencyAccountedCost = new BigDecimal(prevSystemCurrencyAccountedCostStr);
				BigDecimal toBeAdded = new BigDecimal((String)response.getParameter(SYSTEM_CURRENCY_ACCOUNTED_COST.getName()));
				BigDecimal newSystemCurrencyAccountedCost = prevSystemCurrencyAccountedCost.add(toBeAdded);
				response.setAttribute(SYSTEM_CURRENCY_ACCOUNTED_COST.getName(), bigDecimalFormatter.format(newSystemCurrencyAccountedCost));
			}
		}

		private void updateAccountedCost(RnCResponse response) {
			String prevAccountedCostStr = response.getAttribute(ACCOUNTED_COST);
			if (Strings.isNullOrBlank(prevAccountedCostStr)) {
				response.setAttribute(ACCOUNTED_COST.getName(), (String)response.getParameter(ACCOUNTED_COST1.getName()));
			} else {
				BigDecimal prevAccountedCost = new BigDecimal(prevAccountedCostStr);
				BigDecimal toBeAdded = new BigDecimal((String)response.getParameter(ACCOUNTED_COST1.getName()));
				BigDecimal newAccountedCost = prevAccountedCost.add(toBeAdded);
				response.setAttribute(ACCOUNTED_COST.getName(), bigDecimalFormatter.format(newAccountedCost));
			}
		}
	}
}
