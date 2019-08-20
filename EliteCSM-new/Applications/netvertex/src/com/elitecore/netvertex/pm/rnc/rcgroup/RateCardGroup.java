package com.elitecore.netvertex.pm.rnc.rcgroup;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCard;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RoPolicyContextImpl;
import com.elitecore.netvertex.rnc.RnCNonMonetaryOperationUtils;
import com.elitecore.netvertex.service.pcrf.util.PCRFValueProvider;

import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RateCardGroup extends com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup{

	private static final String MODULE = "RateCardGroup";

	public RateCardGroup(String id, String name, String description, LogicalExpression advancedCondition, RateCard peakDaysRateCard, RateCard offPeakRateCard, String rncPackageId, String rncPackageName, int order, AccessTimePolicy accessTimePolicy, List<TimeSlotRelationData> timeSlotRelationDatas) {
		super(id, name, description, advancedCondition, peakDaysRateCard, offPeakRateCard, rncPackageId,
				rncPackageName, order, accessTimePolicy, timeSlotRelationDatas);
	}

	public boolean checkForApplicability(RoPolicyContextImpl policyContext, Subscription subscription) {
		return apply(policyContext, new QuotaReservation(), subscription);
	}

	public boolean apply(RoPolicyContextImpl policyContext, QuotaReservation quotaReservation, Subscription subscription){

		if (LogManager.getLogger().isInfoLogLevel()) {
			policyContext.getTraceWriter().println();
			policyContext.getTraceWriter().incrementIndentation();
			policyContext.getTraceWriter().println();
			policyContext.getTraceWriter().print("[ " + MODULE + " ] : " + getName());
			policyContext.getTraceWriter().incrementIndentation();
			policyContext.getTraceWriter().println();
		}

		try {
			if(Objects.nonNull(getAdvancedCondition()) &&
					getAdvancedCondition().evaluate(policyContext.getValueProvider()) == false) {

				if (LogManager.getLogger().isInfoLogLevel()) {
					policyContext.getTraceWriter().println();
					policyContext.getTraceWriter().print("Advanced condition: " + getAdvancedCondition() + " not satisfied");
				}



				return false;
			}

			if (LogManager.getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().println("Advanced condition: " + getAdvancedCondition() + " satisfied");
			}

			if (Objects.nonNull(getOffPeakRateCard())) {
				RnCNonMonetaryOperationUtils.RateCardResults ratecardResult = applyOffPeakRateCardPeakRateCard(policyContext, quotaReservation, subscription);

				if(ratecardResult == RnCNonMonetaryOperationUtils.RateCardResults.SUCCESS){
					return true;
				}

				if(ratecardResult == RnCNonMonetaryOperationUtils.RateCardResults.NO_BALANCE){
					return false;
				}

			}

			return applyPeakRateCard(policyContext, quotaReservation, subscription);
		} finally {
			if (LogManager.getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().decrementIndentation();
				policyContext.getTraceWriter().decrementIndentation();
			}

		}
	}

	private boolean applyPeakRateCard(RoPolicyContextImpl policyContext, QuotaReservation quotaReservation, Subscription subscription){

		if(RateCardType.NON_MONETARY == getPeakRateCard().getType()){
			return  ((com.elitecore.netvertex.pm.rnc.ratecard.NonMonetaryRateCard)getPeakRateCard()).apply(policyContext, quotaReservation, subscription);
		}else{
			return  ((com.elitecore.netvertex.pm.rnc.ratecard.MonetaryRateCard)getPeakRateCard()).apply(policyContext, quotaReservation, subscription);
		}

	}

	private RnCNonMonetaryOperationUtils.RateCardResults applyOffPeakRateCardPeakRateCard(RoPolicyContextImpl policyContext, QuotaReservation quotaReservation, Subscription subscription){

		RateCard offPeakRateCard = getOffPeakRateCard();

		if(getAccessTimePolicy() != null && getAccessTimePolicy().applyPolicy() == AccessTimePolicy.FAILURE){
			if(getLogger().isInfoLogLevel()) {
				policyContext.getTraceWriter().println("Off Peak Rate Validation fail for timebase condition for RateCardGroup : " + getName());
			}

			return RnCNonMonetaryOperationUtils.RateCardResults.FAILURE;
		}

		if(RateCardType.NON_MONETARY == offPeakRateCard.getType()){
			return  ((com.elitecore.netvertex.pm.rnc.ratecard.NonMonetaryRateCard)offPeakRateCard).apply(policyContext, quotaReservation, subscription)?
					RnCNonMonetaryOperationUtils.RateCardResults.SUCCESS:RnCNonMonetaryOperationUtils.RateCardResults.NO_BALANCE;
		}else{
			return  ((com.elitecore.netvertex.pm.rnc.ratecard.MonetaryRateCard)offPeakRateCard).apply(policyContext, quotaReservation, subscription)?
					RnCNonMonetaryOperationUtils.RateCardResults.SUCCESS:RnCNonMonetaryOperationUtils.RateCardResults.NO_BALANCE;
		}

	}
}
