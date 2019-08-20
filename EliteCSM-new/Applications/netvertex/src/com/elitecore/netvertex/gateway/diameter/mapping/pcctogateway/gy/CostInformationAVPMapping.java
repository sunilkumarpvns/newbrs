package com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gy;

import java.util.Currency;
import javax.annotation.Nonnull;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.AvpAccumalator;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.PCRFToDiameterPacketMapping;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class CostInformationAVPMapping implements PCRFToDiameterPacketMapping {

	private static final String MODULE = "COST-INFO-MAPPING";


	/**
	 * <PRE>
	 * Cost-Information ::= < AVP Header: 423 >
	 * { Unit-Value }
	 * { Currency-Code }
	 * [ Cost-Unit ]  /// minute/seconds/MB
	 *
	 * Unit-Value ::= < AVP Header: 445 >
	 * { Value-Digits }
	 * [ Exponent ]
	 *
	 * </PRE>
	 */
	@Override
	public void apply(@Nonnull DiameterPacketMappingValueProvider valueProvider, @Nonnull AvpAccumalator accumalator) {

		PCRFResponse response = valueProvider.getPcrfResponse();

		String valueDigit = response.getAttribute(PCRFKeyConstants.CI_VALUE_DIGIT.val);

		if (Strings.isNullOrBlank(valueDigit)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping Cost-Information AVP. Reason: PCRF Key: " + PCRFKeyConstants.CI_VALUE_DIGIT.val + " not set for this response");
			}
			return;
		}

		String systemCurrency = valueProvider.getControllerContext().getServerContext().getServerConfiguration().getSystemParameterConfiguration().getSystemCurrency();
		Currency currency = Currency.getInstance(systemCurrency);
		int currencyCode = currency.getNumericCode();
		int exponent = currency.getDefaultFractionDigits();

		//Creating Unit-Value AVP
		AvpGrouped unitValueAVP = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.UNIT_VALUE);
		unitValueAVP.addSubAvp(DiameterAVPConstants.VALUE_DIGITS, valueDigit);
		if (exponent > 0) {
			unitValueAVP.addSubAvp(DiameterAVPConstants.EXPONENT, Math.negateExact(exponent));
		}

		//Creating Cost Information AVP
		AvpGrouped costInformationAVP = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.COST_INFORMATION);
		costInformationAVP.addSubAvp(unitValueAVP);
		costInformationAVP.addSubAvp(DiameterAVPConstants.CURRENCY_CODE, currencyCode);

		String costUnit = response.getAttribute(PCRFKeyConstants.CI_COST_UNIT.val);
		if (Strings.isNullOrBlank(costUnit) == false) {
			costInformationAVP.addSubAvp(DiameterAVPConstants.COST_UNIT, costUnit);
		}

		accumalator.add(costInformationAVP);
	}
}
