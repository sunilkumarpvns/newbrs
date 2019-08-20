package com.elitecore.netvertex.core.servicepolicy;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.ExecutionContextKey;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.ddf.DDFTable;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.util.Predicates;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.corenetvertex.spr.ddf.CurrencyValidatorPredicate;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import org.apache.logging.log4j.ThreadContext;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ExecutionContext {

	private static final String MODULE = "EXE-CNTX";
	private int currentIndex = 0;
	private EnumMap<ExecutionContextKey, Object> parameters;
	private Calendar calendar;
	private PCRFRequest pcrfRequest;
	private PCRFResponse pcrfResponse;
	private CacheAwareDDFTable cacheAwareDDFTable;
    private String systemCurrency;
    @Nullable private OperationFailedException usageException;
    @Nullable private OperationFailedException balanceException;
	@Nullable private OperationFailedException monetaryBalanceException;
    @Nullable private OperationFailedException subscriptionException;
    @Nullable private OperationFailedException subscriberProfileException;
    private ThreadLocal<Predicate<MonetaryBalance>> currencyValidatorThreadLocal;

	public ExecutionContext(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse, CacheAwareDDFTable cacheAwareDDFTable, String systemCurrency) {
		this.pcrfRequest = pcrfRequest;
		this.pcrfResponse = pcrfResponse;
		this.cacheAwareDDFTable = cacheAwareDDFTable;
        this.systemCurrency = systemCurrency;
        this.calendar = Calendar.getInstance();
        this.currencyValidatorThreadLocal = new CurrencyValidatorThreadLocal();
		
		/* * if AUTHENTICATE event is not exist and Terminate Request THEN
		 * 	remove spr cache
		 */
		if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.AUTHENTICATE) == false && isTerminateRequest(pcrfRequest)) {
			String subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
			if (subscriberIdentity != null) {
				SPRInfo removedSPRInfo = cacheAwareDDFTable.removeCache(subscriberIdentity);

				String alternateIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val);
				if(alternateIdentity != null) {
					cacheAwareDDFTable.removeSecondaryCache(pcrfRequest.getAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val));
				}
				if (removedSPRInfo != null) {
					pcrfRequest.setSPRInfo(removedSPRInfo);
					PCRFPacketUtil.setProfileAttributes(pcrfRequest, pcrfResponse, removedSPRInfo);
				}
			} else {
				getLogger().debug(MODULE, "Skipping SPR cache remove operation. Reason: Subscriber Identity not avaiable in PCRFRequest");
			}
		}
	}
	
	public int incrementAndGetHandlerIndex(){
		return ++currentIndex;
	}
	
	public int getCurrentIndex(){
		return currentIndex;
	}
	
	public void setParameter(ExecutionContextKey key, Object parameter){
		if(parameters == null){
			parameters = new EnumMap<ExecutionContextKey, Object>(ExecutionContextKey.class);
		}
		parameters.put(key, parameter);
	}
	
	public Object getParameter(ExecutionContextKey key){
		if(parameters == null){
			return null;
		}
		return parameters.get(key);
	}

	public SPRInfo getSPR() throws OperationFailedException {

		if (pcrfRequest.getSPRInfo() != null) {
			return pcrfRequest.getSPRInfo();
		}

		if (subscriberProfileException != null) {
			subscriberProfileException.setStackTrace(Thread.currentThread().getStackTrace());
			throw subscriberProfileException;
		}

		try {
			SPRInfo subscriberProfile = cacheAwareDDFTable.getProfile(pcrfRequest);
			pcrfRequest.setSPRInfo(subscriberProfile);
			PCRFPacketUtil.setProfileAttributes(pcrfRequest, pcrfResponse, subscriberProfile);
			ThreadContext.put(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, pcrfRequest.getSPRInfo().getSubscriberIdentity());
			return pcrfRequest.getSPRInfo();
		} catch (OperationFailedException ex) {
			subscriberProfileException =ex;
			throw subscriberProfileException;
		}


	}

	public Calendar getCurrentTime() {
		return calendar;
	}
	
	public ServiceUsage getCurrentUsage() throws OperationFailedException {
		/* null usage means usage is not retrieved yet..
		 * empty usage means usage not available from DB, so DON'T CHECK FOR EMPTY */
		
		if (pcrfResponse.getCurrentUsage() != null) {
			return pcrfResponse.getCurrentUsage();
		}
		
		if (usageException != null) {
		    usageException.setStackTrace(Thread.currentThread().getStackTrace());
			throw usageException;
		}

		try {
			Map<String, Map<String, SubscriberUsage>> currentUsage = getSPR().getCurrentUsage();

			pcrfResponse.setUsageFetchTime(getSPR().getUsageLoadTime());
			pcrfResponse.setUsageReadTime(getSPR().getUsageReadTime());
			ServiceUsage serviceUsage = new ServiceUsage(currentUsage);
			if(getLogger().isInfoLogLevel()) {

				StringWriter writer = new StringWriter();
				IndentingWriter indentingWriter = new IndentingPrintWriter(writer);
				indentingWriter.println("SubscriberUsage: ");
				indentingWriter.incrementIndentation();
				if(Maps.isNullOrEmpty(currentUsage)) {
					indentingWriter.println("No usage found");
				} else {
					serviceUsage.toString(indentingWriter);
				}

				indentingWriter.decrementIndentation();
				getLogger().info(MODULE, writer.toString());
			}

			pcrfResponse.setServiceUsage(serviceUsage);


		} catch (OperationFailedException e) {
			usageException = e;
			throw e;
		}
		
		return pcrfResponse.getCurrentUsage();
	}
	
	public LinkedHashMap<String, Subscription> getSubscriptions() throws OperationFailedException {


		/* null subscription means subscription is not retrieved yet..
		 * empty subscription means subscription not available from DB, so DON'T CHECK FOR EMPTY */
		if (pcrfResponse.getSubscriptions() != null) {
			return pcrfResponse.getSubscriptions();
		}
		
		if (subscriptionException != null) {
			throw subscriptionException;
		}
		
		try {
			SPRInfo sprInfo = getSPR();
			LinkedHashMap<String, Subscription> activeSubscriptions = sprInfo.getActiveSubscriptions(calendar.getTimeInMillis());
			pcrfResponse.setSubscriptionFetchTime(sprInfo.getSubscriptionsLoadTime());
			pcrfResponse.setSubscriptionsReadTime(sprInfo.getSubscriptionsReadTime());
			if(getLogger().isInfoLogLevel()) {
				StringWriter stringWriter = new StringWriter();
				IndentingWriter indentingWriter = new IndentingPrintWriter(stringWriter);
				indentingWriter.incrementIndentation();
				indentingWriter.println("Subscriptions: ");
				if(Maps.isNullOrEmpty(activeSubscriptions)) {
					indentingWriter.println("No subscription found");
				} else {

					for(Subscription subscription : activeSubscriptions.values()) {
						subscription.toString(indentingWriter);
					}
				}

				indentingWriter.decrementIndentation();
				getLogger().info(MODULE, stringWriter.toString());
			}

			pcrfResponse.setSubscriptions(activeSubscriptions);

		} catch(OperationFailedException e) {
			subscriptionException = e;
			throw e;
		}
		
		return pcrfResponse.getSubscriptions();
	}


	
	private boolean isTerminateRequest(PCRFRequest pcrfRequest) {
		return pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP);
	}
	public void replaceUsage(Collection<SubscriberUsage> usages) throws OperationFailedException {
		getSPR().replaceUsage(usages);
	}

	public void addToExistingUsage(Collection<SubscriberUsage> usages) throws OperationFailedException {
		getSPR().addToExistingUsage(usages);
	}

	public void insertNewUsage(Collection<SubscriberUsage> usages) throws OperationFailedException {
		getSPR().insertNewUsage(usages);
	}
	
	public boolean isSessionCreatedToday() {
		return pcrfRequest.isSessionCreatedToday(getCurrentTime());
	}
	
	public boolean isSessionCreatedInCurrentWeek() {
		return pcrfRequest.isSessionCreatedInCurrentWeek(getCurrentTime());
	}

	public SubscriberNonMonitoryBalance getCurrentNonMonetoryBalance() throws OperationFailedException {

		/* null usage means usage is not retrieved yet..
		 * empty usage means usage not available from DB, so DON'T CHECK FOR EMPTY */

        if (pcrfResponse.getCurrentNonMonetoryBalance() != null) {
            return pcrfResponse.getCurrentNonMonetoryBalance();
        }

        if (balanceException != null) {
            balanceException.setStackTrace(Thread.currentThread().getStackTrace());
            throw balanceException;
        }

        try {
            SubscriberNonMonitoryBalance currentUsage = getSPR().getNonMonetaryBalance();
			pcrfResponse.setUsageFetchTime(getSPR().getUsageLoadTime());
			pcrfResponse.setUsageReadTime(getSPR().getUsageReadTime());

			if(getLogger().isInfoLogLevel()) {

                StringWriter writer = new StringWriter();
                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(writer);
                indentingPrintWriter.println("Subscriber Balance: ");
                indentingPrintWriter.incrementIndentation();
                if(Maps.isNullOrEmpty(currentUsage.getPackageBalances())) {
                    indentingPrintWriter.println("No balance found");
                } else {
                    currentUsage.toString(indentingPrintWriter);
                }

                indentingPrintWriter.decrementIndentation();
                getLogger().info(MODULE, writer.toString());
            }

            pcrfResponse.setCurrentNonMonetoryBalance(currentUsage);


        } catch (OperationFailedException e) {
            balanceException = e;
            throw e;
        }

        return pcrfResponse.getCurrentNonMonetoryBalance();
	}
	
	public DDFTable getDDFTable() {
		return cacheAwareDDFTable;
	}

	public SubscriberMonetaryBalance getCurrentMonetaryBalance() throws OperationFailedException {

		if (monetaryBalanceException != null) {
			monetaryBalanceException.setStackTrace(Thread.currentThread().getStackTrace());
			throw monetaryBalanceException;
		}

		if(pcrfResponse.getCurrentMonetaryBalance() != null) {
			return pcrfResponse.getCurrentMonetaryBalance();
		}

		try {
			SubscriberMonetaryBalance monetaryBalance = getSPR().getMonetaryBalance(currencyValidatorThreadLocal.get().and(Predicates.RECENT_MONETARY_BALANCE));

			if(getLogger().isInfoLogLevel()) {
				IndentingToStringBuilder toStringBuilder = new IndentingToStringBuilder();
				toStringBuilder.appendHeading("-- Subscriber Monetary Balance -- ");
				toStringBuilder.incrementIndentation();
				toStringBuilder.appendChildObject("Monetary Balance", monetaryBalance);
				toStringBuilder.decrementIndentation();
				getLogger().info(MODULE, toStringBuilder.toString());
			}

			pcrfResponse.setCurrentMonetaryBalance(monetaryBalance);


		} catch (OperationFailedException e) {
			monetaryBalanceException = e;
			throw e;
		}

		return pcrfResponse.getCurrentMonetaryBalance();
	}

    public SubscriberRnCNonMonetaryBalance getCurrentRnCNonMonetaryBalance() throws OperationFailedException {
		if (pcrfResponse.getCurrentRnCNonMonetaryBalance() != null) {
			return pcrfResponse.getCurrentRnCNonMonetaryBalance();
		}

		if (balanceException != null) {
			balanceException.setStackTrace(Thread.currentThread().getStackTrace());
			throw balanceException;
		}

		try {
			SubscriberRnCNonMonetaryBalance rnCNonMonetaryBalance = getSPR().getRnCNonMonetaryBalance();
			if(getLogger().isInfoLogLevel()) {

				StringWriter writer = new StringWriter();
				IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(writer);
				indentingPrintWriter.println("Subscriber RnC Balance: ");
				indentingPrintWriter.incrementIndentation();
				if(Maps.isNullOrEmpty(rnCNonMonetaryBalance.getPackageBalances())) {
					indentingPrintWriter.println("No balance found");
				} else {
					rnCNonMonetaryBalance.toString(indentingPrintWriter);
				}

				indentingPrintWriter.decrementIndentation();
				getLogger().info(MODULE, writer.toString());
			}

			pcrfResponse.setCurrentRnCNonMonetaryBalance(rnCNonMonetaryBalance);


		} catch (OperationFailedException e) {
			balanceException = e;
			throw e;
		}

		return pcrfResponse.getCurrentRnCNonMonetaryBalance();
    }

    private class CurrencyValidatorThreadLocal extends ThreadLocal<Predicate<MonetaryBalance>> {
        @Override
        protected Predicate<MonetaryBalance> initialValue() {
            return CurrencyValidatorPredicate.create(systemCurrency);
        }
    }
}
