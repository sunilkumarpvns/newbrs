package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.ActionType;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionDetail;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.exceptions.DBOperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.util.SubscriptionUtil;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.elitecore.commons.base.DBUtility.closeQuietly;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.IMPORT_SUBSCRIBER;
import static com.elitecore.corenetvertex.constants.CommonConstants.SUBSCRIBE_ADDON_PRODUCT_OFFER;
import static com.elitecore.corenetvertex.constants.CommonConstants.SUBSCRIBE_BOD;
import static com.elitecore.corenetvertex.spr.util.SPRUtil.toStringSQLException;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SubscriptionOperationImpl implements SubscriptionOperation {

	private static final String MODULE = "SUBSCRIPTION-OPR";
	private static final String TABLE_TBLT_SUBSCRIPTION = "TBLT_SUBSCRIPTION";
	private static final String SEVERITY = "";
	private static final String TABLE_TBLM_SUBSCRIBER = "TBLM_SUBSCRIBER";

	//queries
	private final String queryForSubscribeAddon;
	private final String queryForUpdateSubscriptionStatusBySubscriptionId;
	private final String queryForUpdateSubscriptionDetailBySubscriptionId;
	private final String queryForSelectSubscriptionBySubscriptionId;
	private final String queryForUnsubscribeBySubscriberId;
	private final String queryForChangeBillDay;
	private final String queryForSubscribeBod;

	private AtomicInteger totalQueryTimeoutCount;
	private AlertListener alertListener;
	private PolicyRepository policyRepository;
	private UMOperation umOperation;
	private ABMFOperation abmfOperation;
	private RnCABMFOperation rnCABMFOperation;
	private MonetaryABMFOperationImpl monetaryABMFOperationImpl;
	private TopUpSubscriptionOperation topUpSubscriptionOperation;
	private EDRListener balanceEDRListener;

	public SubscriptionOperationImpl(AlertListener alertListener,
									 PolicyRepository policyRepository,
									 UMOperation umOperation,
									 ABMFOperation abmfOperation,
									 RnCABMFOperation rnCABMFOperation,
									 MonetaryABMFOperationImpl monetaryABMFOperationImpl,
									 EDRListener balanceEDRListener) {
		this.alertListener = alertListener;
		this.policyRepository = policyRepository;
		this.umOperation = umOperation;
		this.abmfOperation = abmfOperation;
		this.rnCABMFOperation = rnCABMFOperation;
		this.monetaryABMFOperationImpl = monetaryABMFOperationImpl;
		this.totalQueryTimeoutCount = new AtomicInteger(0);
		this.queryForSubscribeAddon = buildQueryForSubscriberAddOn();
		this.queryForUpdateSubscriptionStatusBySubscriptionId = buildQueryForUpdateSubscriptionStatusBySubscriptionId();
		this.queryForUpdateSubscriptionDetailBySubscriptionId = buildQueryForUpdateSubscriptionDetailBySubscriptionId();
		this.queryForSelectSubscriptionBySubscriptionId = builQueryForSelectSubscriptionBySubscriptionId();
		this.queryForUnsubscribeBySubscriberId = buildQueryForUnsubscribeBySubscriberId();
		this.balanceEDRListener = balanceEDRListener;
		topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener, policyRepository, this.abmfOperation,this.monetaryABMFOperationImpl, this.balanceEDRListener);
		this.queryForChangeBillDay = buildQueryForChangeBillDay();
		this.queryForSubscribeBod =buildQueryForSubscribeBod();
	}

	private String buildQueryForUnsubscribeBySubscriberId() {
		return "UPDATE " + TABLE_TBLT_SUBSCRIPTION + " SET STATUS='" + SubscriptionState.UNSUBSCRIBED.state
				+ "', LAST_UPDATE_TIME=CURRENT_TIMESTAMP" + " WHERE SUBSCRIBER_ID=?";
	}

	private String builQueryForSelectSubscriptionBySubscriptionId() {
		return "SELECT * FROM " + TABLE_TBLT_SUBSCRIPTION + " WHERE SUBSCRIPTION_ID=?";
	}

	private String buildQueryForUpdateSubscriptionDetailBySubscriptionId() {
		return "UPDATE " + TABLE_TBLT_SUBSCRIPTION + " SET START_TIME=?, END_TIME=?, PRIORITY=?,LAST_UPDATE_TIME=CURRENT_TIMESTAMP," +
				" METADATA = ? WHERE SUBSCRIPTION_ID=?";
	}

	private String buildQueryForUpdateSubscriptionStatusBySubscriptionId() {
		return "UPDATE " + TABLE_TBLT_SUBSCRIPTION + " SET STATUS=?, REJECT_REASON=?, LAST_UPDATE_TIME=CURRENT_TIMESTAMP" +
				" WHERE SUBSCRIPTION_ID=?";
	}

	private String buildQueryForSubscriberAddOn() {
		return new StringBuilder("INSERT INTO ")
				.append(TABLE_TBLT_SUBSCRIPTION)
				.append("(SUBSCRIPTION_ID, PACKAGE_ID, PRODUCT_OFFER_ID, PARENT_IDENTITY, SUBSCRIBER_ID, SUBSCRIPTION_TIME, SERVER_INSTANCE_ID,")
				.append(" START_TIME, END_TIME, LAST_UPDATE_TIME, STATUS, PRIORITY, PARAM1, PARAM2, TYPE, METADATA)")
				.append(" VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?)")
				.toString();
	}

	private String buildQueryForChangeBillDay() {
		return "UPDATE " + TABLE_TBLM_SUBSCRIBER + " SET NEXTBILLDATE=?, BILLCHANGEDATE=? WHERE SUBSCRIBERIDENTITY=?";
	}

	public PolicyRepository getPolicyRepository() {
		return policyRepository;
	}

	/**
	 * Subscribes Addon by Addon Id
	 * 
	 * 
	 *
	 * @param subscriptionParameter
	 * @param transactionFactory
	 * @return
	 * @throws OperationFailedException
	 */
	public SubscriptionResult subscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter, TransactionFactory transactionFactory, String requestIpAddress) throws OperationFailedException {

		String subscriberIdentity = subscriptionParameter.getSprInfo().getSubscriberIdentity();
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscribing package(" + subscriptionParameter.getProductOfferId() + ") for subscriber ID: " + subscriberIdentity);
		}

		ProductOffer addOnProductOffer = policyRepository.getProductOffer().byId(subscriptionParameter.getProductOfferId());
		if (addOnProductOffer == null) {
			throw new OperationFailedException("Unable to subscribe addOnProductOffer(" + subscriptionParameter.getProductOfferId() + ") for subscriber ID: " + subscriberIdentity
					+ ". Reason: Package not found for ID: " + subscriptionParameter.getProductOfferId(), ResultCode.NOT_FOUND);
		}

		if (addOnProductOffer.getPolicyStatus() == PolicyStatus.FAILURE) {
			throw new OperationFailedException("Unable to subscribe addOnProductOffer(" + subscriptionParameter.getProductOfferId() + ") for subscriber ID: " + subscriberIdentity
					+ ". Reason: AddOn(" + addOnProductOffer.getName() + ") is failed addOnProductOffer", ResultCode.INVALID_INPUT_PARAMETER);
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "AddOn Product Offer(" + addOnProductOffer.getName() + ") found for ID: " + addOnProductOffer.getId());
		}
		return subscribeAddOnProductOffer(addOnProductOffer,
				subscriptionParameter.getSprInfo(),
				subscriptionParameter.getParentId(),
				subscriptionParameter.getSubscriptionStatusValue(),
				subscriptionParameter.getStartTime(),
				subscriptionParameter.getEndTime(),
				subscriptionParameter.getPriority(),
				subscriptionParameter.getParam1(),
				subscriptionParameter.getParam2(),
				null,
				subscriptionParameter.getBillDay(),
				subscriptionParameter.getSubscriptionPrice(),
				subscriptionParameter.getMonetaryBalance(),
				subscriptionParameter.getMetadata(),
				transactionFactory,
				requestIpAddress);
	}

	private void validateStartTimeAndEndTime(long startTimeMs, long endTimeMs, long currentTimeMs) throws OperationFailedException {
		if (endTimeMs <= currentTimeMs) {
			throw new OperationFailedException("End time(" + new Timestamp(endTimeMs).toString() + ") is less or equal to current time", ResultCode.INVALID_INPUT_PARAMETER);
		}

		if (startTimeMs >= endTimeMs) {
			throw new OperationFailedException("Start time(" + new Timestamp(startTimeMs).toString() + ") is more or equal to end time("
					+ new Timestamp(endTimeMs).toString() + ")", ResultCode.INVALID_INPUT_PARAMETER);
		}
	}

	/**
	 * IF subscription state is not received THEN take SubscriptionState.STARTED
	 * 
	 * IF invalid state received THEN throws OperationFailedException
	 * 
	 * @param subscriberIdentity
	 * @param subscriptionStatusValue
	 * @return SubscriptionState
	 * @throws OperationFailedException
	 */
	public SubscriptionState getSubscriptionStateFromValue(String subscriberIdentity, @Nullable Integer subscriptionStatusValue)
			throws OperationFailedException {
		if (subscriptionStatusValue == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Considering Subscription Status: " + SubscriptionState.STARTED
						+ " for Subscriber ID: " + subscriberIdentity
						+ ". Reason: Subscription status not received");
			}

			return SubscriptionState.STARTED;
		}

		SubscriptionState subscriptionState = SubscriptionState.fromValue(subscriptionStatusValue.intValue());
		if (subscriptionState == null) {
			throw new OperationFailedException("Invalid subscription status value: " + subscriptionStatusValue + " received", ResultCode.INVALID_INPUT_PARAMETER);
		}

		if (subscriptionState != SubscriptionState.STARTED) {
			throw new OperationFailedException("Invalid subscription status: " + subscriptionState.name + " received", ResultCode.INVALID_INPUT_PARAMETER);
		}
		return subscriptionState;
	}

	private SubscriptionResult subscribeAddOnProductOffer(ProductOffer addOnProductOffer, SPRInfo sprInfo,
														  String parentId, Integer subscriptionStatusValue,
														  Long startTime, Long endTime, Integer priority,
														  String param1, String param2, @Nullable String subscriptionId,
														  Integer billDay, double subscriptionPrice,
														  MonetaryBalance monetaryBalance, SubscriptionMetadata metadata,
														  TransactionFactory transactionFactory,
														  String requestIpAddress)
			throws OperationFailedException {

		SubscriptionResult subscriptionResult = new SubscriptionResult();

		long currentTimeMS = System.currentTimeMillis();

		startTime = Numbers.POSITIVE_LONG.apply(startTime) ? startTime.longValue() : currentTimeMS;

		endTime = Numbers.POSITIVE_LONG.apply(endTime) ? endTime.longValue()
				: addTime(startTime, addOnProductOffer.getValidityPeriod(), addOnProductOffer.getValidityPeriodUnit());

		validateStartTimeAndEndTime(startTime, endTime, currentTimeMS);


		if(nonNull(priority) && priority < 1) {
			throw new OperationFailedException("Unable to subscribe addOn(ID:" + addOnProductOffer.getId() + ", Name: " + addOnProductOffer.getName()
					+ ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: Invalid priority:" + priority);
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to subscribe addOn(ID:" + addOnProductOffer.getId() + ", Name: " + addOnProductOffer.getName()
					+ ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();


		if (transaction == null) {
			throw new OperationFailedException("Unable to subscribe addOn(ID:" + addOnProductOffer.getId() + ", Name: " + addOnProductOffer.getName()
					+ ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		if(isNull(priority)) {
			priority = CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY;
		}
		
		try {
			transaction.begin();

			if(addOnProductOffer.getDataServicePkgData() != null) {

			Subscription addOnSubscription = addSubscriptionDetails(addOnProductOffer, addOnProductOffer.getDataServicePkgData().getId(), sprInfo.getSubscriberIdentity()
					,parentId, subscriptionStatusValue, startTime, endTime, priority, transaction, param1, param2, subscriptionId, SubscriptionType.ADDON);

				if (addOnProductOffer.getDataServicePkgData().getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
					List<SubscriberUsage> subscriberUsages = new ArrayList<>();
					for (QuotaProfile quotaProfile : addOnProductOffer.getDataServicePkgData().getQuotaProfiles()) {
						Set<String> serviceTosubscriberUsage = new HashSet<>(4);

						for (QuotaProfileDetail quotaProfileDetail : quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().values()) {
							addUsage(addOnProductOffer, sprInfo.getSubscriberIdentity(), subscriberUsages, quotaProfile, serviceTosubscriberUsage, quotaProfileDetail.getServiceId(), addOnSubscription);
						}

						getServiceWiseQuotaProfileUsage(addOnProductOffer, sprInfo.getSubscriberIdentity(), addOnSubscription, subscriberUsages, quotaProfile, serviceTosubscriberUsage);
					}
					if (Collectionz.isNullOrEmpty(subscriberUsages) == false) {
						umOperation.insert(sprInfo.getSubscriberIdentity(), subscriberUsages, transaction);
						subscriptionResult.addSubscription(addOnSubscription, subscriberUsages);
					} else {
						subscriptionResult.addSubscription(addOnSubscription);
					}
					prepareUsageList(addOnProductOffer, sprInfo.getSubscriberIdentity(), subscriptionResult, transaction, addOnSubscription, subscriberUsages);
				} else if (addOnProductOffer.getDataServicePkgData().getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
					SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance = abmfOperation.addBalance(sprInfo.getSubscriberIdentity(), addOnSubscription, addOnProductOffer, transaction, billDay);
					subscriptionResult.addSubscription(addOnSubscription, subscriptionNonMonitoryBalance);
				} else {
					subscriptionResult.addSubscription(addOnSubscription);
				}
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Package(" + addOnProductOffer.getName() + ") subscription successful for subscriber ID: "
							+ sprInfo.getSubscriberIdentity() + " with subscription ID(" + addOnSubscription.getId() + ")");
				}

			}

			if(Collectionz.isNullOrEmpty(addOnProductOffer.getProductOfferServicePkgRelDataList()) == false){
				String subscriptionMetadata = SubscriptionUtil.createMetaString(metadata,subscriptionId,MODULE);
				for(ProductOfferServicePkgRel productOfferServicePkgRel : addOnProductOffer.getProductOfferServicePkgRelDataList()){

						Subscription rncAddOnSubscription = addRncSubscriptionDetails(addOnProductOffer,
								productOfferServicePkgRel.getRncPackageData(), sprInfo.getSubscriberIdentity()
								, parentId, subscriptionStatusValue, startTime, endTime, priority,
								transaction, param1, param2, subscriptionId, subscriptionMetadata);

					if(productOfferServicePkgRel.getRncPackageData().getPkgType() == RnCPkgType.NON_MONETARY_ADDON){
						SubscriptionRnCNonMonetaryBalance subscriptionRnCNonMonetaryBalance = rnCABMFOperation.addBalance(sprInfo.getSubscriberIdentity(), rncAddOnSubscription, productOfferServicePkgRel.getRncPackageData(), transaction, addOnProductOffer.getId(),billDay);

						if(isNull(subscriptionRnCNonMonetaryBalance)) {
							subscriptionResult.addSubscription(rncAddOnSubscription);
						} else {
							subscriptionResult.addSubscription(rncAddOnSubscription, subscriptionRnCNonMonetaryBalance);
						}
					} else {
						subscriptionResult.addSubscription(rncAddOnSubscription);
					}
				}
			}

			if(subscriptionPrice > 0) {
				monetaryABMFOperationImpl.directDebitBalance(sprInfo.getSubscriberIdentity(), monetaryBalance.getId(), subscriptionPrice, transaction);
				if(nonNull(balanceEDRListener)) {
					MonetaryBalance previousMonetaryBalance = monetaryBalance.copy();
					monetaryBalance.setAvailBalance(subscriptionPrice*-1);
					balanceEDRListener.updateMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, previousMonetaryBalance, sprInfo, SUBSCRIBE_ADDON_PRODUCT_OFFER, ActionType.UPDATE.name(),null),
							requestIpAddress, SUBSCRIBE_ADDON_PRODUCT_OFFER);
				}
			}


			return subscriptionResult;


		} catch (SQLException e) {
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;
			if (isQueryTimeoutException(e)) {
				if (isTotalQueryTimeOutCountExceed()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "total number of query timeouts(" + totalQueryTimeoutCount.intValue()
								+ ") exceeded then configured max number "
								+ "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
					}
					transaction.markDead();
				}
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while subscribing package from DB with data source name: "
								+ transaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;

			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			
			transaction.rollback();
			throw new OperationFailedException(toStringSQLException("subscribing package: " + addOnProductOffer.getName(), sprInfo.getSubscriberIdentity(), e), resultCode, e);
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;
		
			if (isConnectionNotFoundException(e)) {
		
				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to subscribe package for subscriber ID: " + sprInfo.getSubscriberIdentity() + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			
			transaction.rollback();
			throw new OperationFailedException("Unable to subscribe package(" + addOnProductOffer.getName() + ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (OperationFailedException e) {
			
			transaction.rollback();
			
			throw new OperationFailedException("Unable to subscribe package(" + addOnProductOffer.getName() + ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: " + e.getMessage(), e.getErrorCode(), e);
		} finally {
			
			endTransaction(transaction);
		}
		
	}

	private void prepareUsageList(ProductOffer addOnProductOffer, String subscriberIdentity, SubscriptionResult subscriptionResult, Transaction transaction, Subscription addOnSubscription, List<SubscriberUsage> subscriberUsages) throws TransactionException, OperationFailedException {
		for (QuotaProfile quotaProfile : addOnProductOffer.getDataServicePkgData().getQuotaProfiles()) {
            Set<String> serviceTosubscriberUsage = new HashSet<>(4);

            for (QuotaProfileDetail quotaProfileDetail : quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails().values()) {
                addUsage(addOnProductOffer, subscriberIdentity, subscriberUsages, quotaProfile, serviceTosubscriberUsage, quotaProfileDetail.getServiceId(), addOnSubscription);
            }

            for (int level = 1; quotaProfile.getServiceWiseQuotaProfileDetails(level) != null; level++) {
                Map<String, QuotaProfileDetail> quotaProfileDetails = quotaProfile.getServiceWiseQuotaProfileDetails(level);

                if (Maps.isNullOrEmpty(quotaProfileDetails)) {
                    continue;
                }

                for (QuotaProfileDetail quotaProfileDetail : quotaProfileDetails.values()) {
                    addUsage(addOnProductOffer, subscriberIdentity, subscriberUsages, quotaProfile, serviceTosubscriberUsage, quotaProfileDetail.getServiceId(), addOnSubscription);
                }
            }
        }
		if (Collectionz.isNullOrEmpty(subscriberUsages) == false) {
            umOperation.insert(subscriberIdentity, subscriberUsages, transaction);
            subscriptionResult.addSubscription(addOnSubscription, subscriberUsages);
        } else {
            subscriptionResult.addSubscription(addOnSubscription);
        }
	}

	private void getServiceWiseQuotaProfileUsage(ProductOffer addOnProductOffer, String subscriberIdentity, Subscription addOnSubscription, List<SubscriberUsage> subscriberUsages, QuotaProfile quotaProfile, Set<String> serviceTosubscriberUsage) {
		for (int level = 1; quotaProfile.getServiceWiseQuotaProfileDetails(level) != null; level++) {
			Map<String, QuotaProfileDetail> quotaProfileDetails = quotaProfile.getServiceWiseQuotaProfileDetails(level);

			if (Maps.isNullOrEmpty(quotaProfileDetails)) {
				continue;
			}

			for (QuotaProfileDetail quotaProfileDetail : quotaProfileDetails.values()) {
				addUsage(addOnProductOffer, subscriberIdentity, subscriberUsages, quotaProfile, serviceTosubscriberUsage, quotaProfileDetail.getServiceId(), addOnSubscription);
			}
		}
	}

	private void addUsage(ProductOffer productOffer, String subscriberIdentity, List<SubscriberUsage> subscriberUsages, QuotaProfile quotaProfile,
			Set<String> serviceTosubscriberUsage, String serviceId, Subscription addOnSubscription) {
		
		if (serviceTosubscriberUsage.add(serviceId) == true) {
			SubscriberUsage.SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder(SubscriberUsage.NEW_ID, subscriberIdentity, serviceId, quotaProfile
					.getId(),productOffer.getDataServicePkgId(), productOffer.getId())
					.withSubscriptionId(addOnSubscription.getId());

			if(quotaProfile.getRenewalInterval()!=0 && quotaProfile.getRenewalIntervalUnit()!=null){
				long resetTime = quotaProfile.getRenewalIntervalUnit().addTime(addOnSubscription.getStartTime().getTime(), quotaProfile.getRenewalInterval());

				if(resetTime>addOnSubscription.getEndTime().getTime()){
					resetTime = addOnSubscription.getEndTime().getTime();
				}
				subscriberUsageBuilder.withBillingCycleResetTime(resetTime);
				subscriberUsageBuilder.withCustomResetTime(resetTime);
			} else {
				subscriberUsageBuilder.withBillingCycleResetTime(addOnSubscription.getEndTime().getTime());
				subscriberUsageBuilder.withCustomResetTime(addOnSubscription.getEndTime().getTime());
			}

			subscriberUsages.add(subscriberUsageBuilder.build());
		}
	}

	@VisibleForTesting
	String getNextSubscriptionId() {
		return UUID.randomUUID().toString();
	}

	private boolean isConnectionNotFoundException(TransactionException e) {
		return e.getErrorCode() == TransactionErrorCode.CONNECTION_NOT_FOUND;
	}

	private boolean isTotalQueryTimeOutCountExceed() {
		boolean result = false;
		if (totalQueryTimeoutCount.incrementAndGet() > CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT) {
			result = true;
			resetTotalQueryTimeoutCount();
		}
		return result;
	}

	private boolean isQueryTimeoutException(SQLException e) {
		return e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE;
	}

	/*
	 * gives future time (time + validity period according to
	 * validityPeriodUnit)
	 */
	private long addTime(long startTime, int validityPeriod, ValidityPeriodUnit validityPeriodUnit) {
		return validityPeriodUnit.addTime(startTime, validityPeriod);
	}

	/**
	 * <PRE>
	 * 	if no addOn subscription found then
	 * 		 returns EMPTY_LIST
	 * </PRE>
	 * 
	 * @throws DBOperationFailedException
	 */

	public LinkedHashMap<String, Subscription> getSubscriptions(
			SPRInfo sprInfo, TransactionFactory transactionFactory) throws OperationFailedException {
		String subscriberIdentity = sprInfo.getSubscriberIdentity();

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Fetching active subscriptions for subscriber ID: " + subscriberIdentity);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to fetch active subscription for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
		if (dbTransaction == null) {
			throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		
		try {
			dbTransaction.begin();
			preparedStatement = dbTransaction.prepareStatement(getQueryForSubscriptions());
			preparedStatement.setString(1, subscriberIdentity);
			preparedStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			resultSet = preparedStatement.executeQuery();
			stopwatch.stop();
			long queryExecutionTime = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);
			sprInfo.setSubscriptionsLoadTime(queryExecutionTime);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while fetching active subscription. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isLogLevel(LogLevel.WARN)) {
					getLogger().warn(MODULE, "DB Query execution time is high while fetching active subscription. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}

			long queryReadStartTime = getCurrentTime();
			LinkedHashMap<String,Subscription> subscriptions = createSubscriptions(subscriberIdentity, resultSet);
			long queryReadTime = getCurrentTime() - queryReadStartTime;
			sprInfo.setSubscriptionsReadTime(queryReadTime);
			resetTotalQueryTimeoutCount();

			if ((subscriptions == null || subscriptions.isEmpty()) == false) {
				return subscriptions;
			}
		} catch (SQLException e) {
			handleAddonException(e, subscriberIdentity, dbTransaction);
		} catch (TransactionException e) {
			handleAddonTransactionException(e, subscriberIdentity, dbTransaction);
		} catch (Exception e) {
			throw new OperationFailedException("Error while fetching addOn subscription for subscriber: " + subscriberIdentity
					+ ". Reason. " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			endTransaction(dbTransaction);
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Active Subscriptions are not exist with subscriber ID: " + subscriberIdentity);
		}

		return new LinkedHashMap<>();
	}

	private LinkedHashMap<String, Subscription> createSubscriptions(String subscriberIdentity, ResultSet resultSet) throws SQLException {
			long currentTime = getCurrentTime();
		LinkedHashMap<String, Subscription> subscriptions = null;
			while (resultSet.next()) {
				String packageId = resultSet.getString("PACKAGE_ID");
				String productOfferId = resultSet.getString("PRODUCT_OFFER_ID");
				String subscriptionId = resultSet.getString("SUBSCRIPTION_ID");
				Timestamp startTime = resultSet.getTimestamp("START_TIME");
				Timestamp endtime = resultSet.getTimestamp("END_TIME");
				int status = resultSet.getInt("STATUS");
				int priority = resultSet.getInt("PRIORITY");
				String type = resultSet.getString("TYPE");
				String metadata = resultSet.getString("METADATA");

				SubscriptionState state = SubscriptionState.fromValue(status);

				ProductOffer subscribedPackage = policyRepository.getProductOffer().byId(productOfferId);

				if (subscribedPackage == null) {
					QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(packageId);

					if (quotaTopUp == null) {
						BoDPackage boDPackage = policyRepository.getBoDPackage().byId(packageId);
						if(boDPackage == null) {
							if (getLogger().isErrorLogLevel()) {
								getLogger().error(MODULE, "Skipping subscriptions: " + subscriptionId
										+ ", subscriberId: " + subscriberIdentity
										+ ". Reason: Addon or Quota TopUp not found for id: " + packageId);
							}
							continue;
						}
					}
				}


				if (state == null) {
					getLogger().error(MODULE, "Error while getting active subscriptions:" + subscriptionId
							+ ", id:" + productOfferId + ", subscriberId:" + subscriberIdentity
							+ ". Reason: Illegle status determined, state: " + status);
					continue;
				}
            // FIXME need to improve this validation checks --chetan
				if (endtime == null) {
					if (state == SubscriptionState.APPROVAL_PENDING) {
						if (getLogger().isDebugLogLevel()) {
							getLogger().debug(MODULE, "Approval pending for subscription:" + subscriptionId
									+ ", id:" + productOfferId + ", subscriberId:" + subscriberIdentity);
						}
					} else if (state == SubscriptionState.REJECTED) {
						if (getLogger().isDebugLogLevel()) {
							getLogger().debug(MODULE, "Subscription:" + subscriptionId + " rejected for id: "
									+ productOfferId + ", subscriberId: " + subscriberIdentity);
						}
					} else {
						if (getLogger().isWarnLogLevel()) {
							getLogger().warn(MODULE, "Endtime is not provided for subscription:" + subscriptionId
									+ ", addon:" + productOfferId + ", subscriberId: " + subscriberIdentity);
						}

					}
					continue;
				}

			if (endtime.getTime() <= currentTime) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Skipping subscription:" + subscriptionId
							+ ", id:" + productOfferId + " for subscriberId:" + subscriberIdentity
							+ ". Reason:  subscription is expired on Endtime:" + endtime + " ");
				}
				continue;
			}

			if (state == SubscriptionState.UNSUBSCRIBED) {
				if (getLogger().isLogLevel(LogLevel.INFO))
					getLogger().info(MODULE, "Skipping subscription:" + subscriptionId
							+ ", id:" + productOfferId + " for subscriberId:" + subscriberIdentity
							+ ". Reason: Subscription is Unsubscribed");
				continue;
			}

			if(isNull(type)) {
				if (getLogger().isLogLevel(LogLevel.INFO))
					getLogger().info(MODULE, "Skipping subscription:" + subscriptionId
							+ ", id:" + productOfferId + " for subscriberId:" + subscriberIdentity
							+ ". Reason: Subscription Type not found from subscription");
				continue;
			}

			if(isNull(SubscriptionType.fromVal(type))) {
				if (getLogger().isLogLevel(LogLevel.INFO))
					getLogger().info(MODULE, "Skipping subscription:" + subscriptionId
							+ ", id:" + productOfferId + " for subscriberId:" + subscriberIdentity
							+ ". Reason: Unknown Subscription Type:" + type);

				continue;
			}

			String param1 = resultSet.getString("PARAM1");
			String param2 = resultSet.getString("PARAM2");

			if (subscriptions == null) {
				subscriptions = new LinkedHashMap<>();
			}

				subscriptions.put(subscriptionId,
						new Subscription(subscriptionId, subscriberIdentity, packageId, productOfferId, startTime,
								endtime, state, priority, SubscriptionType.fromVal(type),
								SubscriptionUtil.createMetaData(metadata,subscriptionId,MODULE), param1, param2));
			}
		return subscriptions;
	}

	private LinkedHashMap<String, Subscription> createSubscriptionsForBod(String subscriberIdentity, ResultSet resultSet) throws SQLException {
		long currentTime = getCurrentTime();
		LinkedHashMap<String, Subscription> bodSubscriptions = null;
		while (resultSet.next()) {
			String bodId = resultSet.getString("PACKAGE_ID");
			String productOfferId = resultSet.getString("PRODUCT_OFFER_ID");
			String bodSubscriptionId = resultSet.getString("SUBSCRIPTION_ID");
			Timestamp startTime = resultSet.getTimestamp("START_TIME");
			Timestamp endtime = resultSet.getTimestamp("END_TIME");
			int status = resultSet.getInt("STATUS");
			int priority = resultSet.getInt("PRIORITY");
			SubscriptionState state = SubscriptionState.fromValue(status);

			BoDPackage subscribeBod = policyRepository.getBoDPackage().byId(bodId);

			if (subscribeBod == null) {
				if (getLogger().isErrorLogLevel()) {
					getLogger().error(MODULE, "Skipping subscriptions: " + bodSubscriptionId
							+ ", subscriberId: " + subscriberIdentity
							+ ". Reason: BoD not found for id: " + bodId);
				}
				continue;
			}

			if (state == null) {
				getLogger().error(MODULE, "Error while getting active subscriptions:" + bodSubscriptionId
						+ ", id:" + bodId + ", subscriberId:" + subscriberIdentity
						+ ". Reason: Illegle status determined, state: " + status);
				continue;
			}
			if (endtime == null) {
				if (state == SubscriptionState.APPROVAL_PENDING) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Approval pending for subscription:" + bodSubscriptionId
								+ ", id:" + bodId + ", subscriberId:" + subscriberIdentity);
					}
				} else if (state == SubscriptionState.REJECTED) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Subscription:" + bodSubscriptionId + " rejected for id: "
								+ bodId + ", subscriberId: " + subscriberIdentity);
					}
				} else {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "Endtime is not provided for subscription:" + bodSubscriptionId
								+ ", addon:" + bodId + ", subscriberId: " + subscriberIdentity);
					}
				}
				continue;
			}

			if (endtime.getTime() <= currentTime) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Skipping subscription:" + bodSubscriptionId
							+ ", id:" + bodId + " for subscriberId:" + subscriberIdentity
							+ ". Reason:  subscription is expired on Endtime:" + endtime + " ");
				}
				continue;
			}

			if (state == SubscriptionState.UNSUBSCRIBED) {
				if (getLogger().isLogLevel(LogLevel.INFO))
					getLogger().info(MODULE, "Skipping subscription:" + bodSubscriptionId
							+ ", id:" + bodId + " for subscriberId:" + subscriberIdentity
							+ ". Reason: Subscription is Unsubscribed");
				continue;
			}

			String param1 = resultSet.getString("PARAM1");
			String param2 = resultSet.getString("PARAM2");

			if (bodSubscriptions == null) {
				bodSubscriptions = new LinkedHashMap<String, Subscription>();
			}

			bodSubscriptions.put(bodSubscriptionId, new Subscription(bodSubscriptionId, subscriberIdentity, subscribeBod.getId(), productOfferId, startTime, endtime, state, priority, SubscriptionType.BOD, param1, param2));
		}
		return bodSubscriptions;
	}



	public LinkedHashMap<String, Subscription> getSubscriptions(
			String subscriberIdentity,
			TransactionFactory transactionFactory) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Fetching active subscriptions for subscriber ID: " + subscriberIdentity);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to fetch active subscription for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
		if (dbTransaction == null) {
			throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			dbTransaction.begin();
			preparedStatement = dbTransaction.prepareStatement(getQueryForSubscriptions());
			preparedStatement.setString(1, subscriberIdentity);
			preparedStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			resultSet = preparedStatement.executeQuery();
			stopwatch.stop();
			long queryExecutionTime = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);

			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while fetching active subscription. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isLogLevel(LogLevel.WARN)) {
					getLogger().warn(MODULE, "DB Query execution time is high while fetching active subscription. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}

			LinkedHashMap<String, Subscription> subscriptions = createSubscriptions(subscriberIdentity, resultSet);

			resetTotalQueryTimeoutCount();

			if ((subscriptions == null || subscriptions.isEmpty()) == false) {
				return subscriptions;
			}
		} catch (SQLException e) {
			handleAddonException(e, subscriberIdentity, dbTransaction);
		} catch (TransactionException e) {
			handleAddonTransactionException(e, subscriberIdentity, dbTransaction);
		} catch (Exception e) {
			throw new OperationFailedException("Error while fetching subscription for subscriber: " + subscriberIdentity
					+ ". Reason. " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			endTransaction(dbTransaction);
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Active Subscriptions are not exist with subscriber ID: " + subscriberIdentity);
		}

		return new LinkedHashMap<>();
	}

	private void handleAddonException(SQLException e, String subscriberIdentity, DBTransaction dbTransaction)
			throws OperationFailedException {

		ResultCode resultCode = ResultCode.INTERNAL_ERROR;

		if (isQueryTimeoutException(e)) {
			if (isTotalQueryTimeOutCountExceed()) {
				if (getLogger().isWarnLogLevel()) {
					getLogger()
							.warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts, so System marked as DEAD");
				}
				dbTransaction.markDead();
			}
			alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
					"DB Query Timeout while fetching Addon subscription from table name: "
							+ TABLE_TBLT_SUBSCRIPTION, 0, "fetching data from table name: " + TABLE_TBLT_SUBSCRIPTION);

			resultCode = ResultCode.SERVICE_UNAVAILABLE;
		} else if (dbTransaction.isDBDownSQLException(e)) {
			dbTransaction.markDead();
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Database with data source name: " + dbTransaction.getDataSourceName()
						+ " is Down, System marked as dead. Reason: " + e.getMessage());
			}
			resultCode = ResultCode.SERVICE_UNAVAILABLE;
		}

		throw new OperationFailedException(toStringSQLException("fetching addOn subscription", subscriberIdentity, e), resultCode, e);
	}

	private void handleBoDException(SQLException e, String subscriberIdentity, DBTransaction dbTransaction)
			throws OperationFailedException {

		ResultCode resultCode = ResultCode.INTERNAL_ERROR;

		if (isQueryTimeoutException(e)) {
			if (isTotalQueryTimeOutCountExceed()) {
				if (getLogger().isWarnLogLevel()) {
					getLogger()
							.warn(MODULE, "total number of query timeouts exceeded then configured max number of query timeouts, so System marked as DEAD");
				}
				dbTransaction.markDead();
			}
			alertListener.generateSystemAlert("", Alerts.QUERY_TIME_OUT, MODULE,
					"DB Query Timeout while fetching BoD subscription from table name: "
							+ TABLE_TBLT_SUBSCRIPTION, 0, "fetching data from table name: " + TABLE_TBLT_SUBSCRIPTION);

			resultCode = ResultCode.SERVICE_UNAVAILABLE;
		} else if (dbTransaction.isDBDownSQLException(e)) {
			dbTransaction.markDead();
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Database with data source name: " + dbTransaction.getDataSourceName()
						+ " is Down, System marked as dead. Reason: " + e.getMessage());
			}
			resultCode = ResultCode.SERVICE_UNAVAILABLE;
		}

		throw new OperationFailedException(toStringSQLException("fetching bod subscription", subscriberIdentity, e), resultCode, e);
	}

	private void endTransaction(@Nullable DBTransaction transaction) {
		try {
			if (transaction != null) {
				transaction.end();
			}
		} catch (TransactionException e) {
			getLogger().trace(MODULE, e);
		}
	}

	protected long getCurrentTime() {
		return System.currentTimeMillis();
	}

	private static String getQueryForSubscriptions() {
		return "SELECT PACKAGE_ID, PRODUCT_OFFER_ID, SUBSCRIPTION_ID, START_TIME, END_TIME, PRIORITY, STATUS, PARAM1, PARAM2, TYPE, METADATA" +
				" FROM " + TABLE_TBLT_SUBSCRIPTION +
				" WHERE SUBSCRIBER_ID = ?";
	}

	private void handleAddonTransactionException(TransactionException e, String subscriberIdentity, DBTransaction transaction)
			throws OperationFailedException {

		ResultCode resultCode = ResultCode.INTERNAL_ERROR;
		if (isConnectionNotFoundException(e)) {

			alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
					"Unable to get Add On subscription from " + transaction.getDataSourceName() +
							" database. Reason: Connection not available");
			resultCode = ResultCode.SERVICE_UNAVAILABLE;
		}

		throw new OperationFailedException("Error while fetching AddOn subscription for subscriber: " + subscriberIdentity + ". Reason. " + e.getMessage(), resultCode, e);
	}

	private void handleBoDTransactionException(TransactionException e, String subscriberIdentity, DBTransaction transaction)
			throws OperationFailedException {

		ResultCode resultCode = ResultCode.INTERNAL_ERROR;
		if (isConnectionNotFoundException(e)) {

			alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
					"Unable to get BoD subscription from " + transaction.getDataSourceName() +
							" database. Reason: Connection not available");
			resultCode = ResultCode.SERVICE_UNAVAILABLE;
		}

		throw new OperationFailedException("Error while fetching BoD subscription for subscriber: " + subscriberIdentity + ". Reason. " + e.getMessage(), resultCode, e);
	}

	private void resetTotalQueryTimeoutCount() {
		totalQueryTimeoutCount.set(0);
	}

	/**
	 * 
	 *
	 * @param subscriptionParameter
	 */
	public SubscriptionResult subscribeProductOfferByName(SubscriptionParameter subscriptionParameter, TransactionFactory transactionFactory) throws OperationFailedException {
		String subscriberIdentity = subscriptionParameter.getSprInfo().getSubscriberIdentity();
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Subscribing productOffer(" + subscriptionParameter.getProductOfferName() + ") for subscriber ID: " + subscriberIdentity);
		}

		ProductOffer productOffer = policyRepository.getProductOffer().byName(subscriptionParameter.getProductOfferName());
		if (productOffer == null) {
			throw new OperationFailedException("Unable to subscribe productOffer(" + subscriptionParameter.getProductOfferName() + ") for subscriber ID: " + subscriberIdentity
					+ ". Reason: Product Offer not found for Name: " + subscriptionParameter.getProductOfferName(), ResultCode.NOT_FOUND);
		}

		if (productOffer.getPolicyStatus() == PolicyStatus.FAILURE) {
			throw new OperationFailedException("Unable to subscribe productOffer(" + subscriptionParameter.getProductOfferName() + ") for subscriber ID: " + subscriberIdentity
					+ ". Reason: AddOn(" + subscriptionParameter.getProductOfferName() + ") is failed productOffer", ResultCode.INVALID_INPUT_PARAMETER);
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "AddOn(" + productOffer.getName() + ") found");
		}

		return subscribeAddOnProductOffer(productOffer, subscriptionParameter.getSprInfo(), subscriptionParameter.getParentId(),
				subscriptionParameter.getSubscriptionStatusValue(), subscriptionParameter.getStartTime(),
				subscriptionParameter.getEndTime(), subscriptionParameter.getPriority(),
				subscriptionParameter.getParam1(), subscriptionParameter.getParam2(), null,
				subscriptionParameter.getBillDay(), subscriptionParameter.getSubscriptionPrice(),
				subscriptionParameter.getMonetaryBalance(), subscriptionParameter.getMetadata(), transactionFactory, null);

	}

	/***
	 *
	 *
	 * @param subscriptionId
	 *            , required
	 * @param subscriptionStatusValue
	 *            , required
	 * @param startTime
	 * @param endTime
	 * @param priority
	 * @param rejectReason
	 * @param transactionFactory
	 *
	 * @return
	 * @throws OperationFailedException
	 */

	public Subscription updateSubscription(
			String subscriberIdentity,
			String subscriptionId,
			Integer subscriptionStatusValue,
			Long startTime,
			Long endTime,
			Integer priority, String rejectReason,
			TransactionFactory transactionFactory
	) throws OperationFailedException {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Updating subscription(" + subscriptionId + ")");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to update subscription for subscription ID: " + subscriptionId
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		long currentTimeMS = System.currentTimeMillis();

		if (subscriptionStatusValue == null) {
			throw new OperationFailedException("Unable to update subscription for subscription ID: " + subscriptionId
					+ ". Reason: Subscription status value not found", ResultCode.INVALID_INPUT_PARAMETER);
		}

		SubscriptionState newSubscriptionState = SubscriptionState.fromValue(subscriptionStatusValue.intValue());
		if (newSubscriptionState == null) {
			throw new OperationFailedException("Unable to update subscription for subscription ID: " + subscriptionId
					+ ". Reason: Invalid subscription status value: " + subscriptionStatusValue + " received", ResultCode.INVALID_INPUT_PARAMETER);
		}

		try {
			Subscription subscription = getActiveSubscriptionBySubscriptionId(subscriptionId, currentTimeMS, transactionFactory);

			if (subscription == null) {
				throw new OperationFailedException("Active subscription not found with ID: " + subscriptionId, ResultCode.NOT_FOUND);
			}

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "subscription(" + subscriptionId + ") updated successfully");
			}

			if (subscription.getSubscriberIdentity().equals(subscriberIdentity) == false) {
				throw new OperationFailedException("Unable to update subscription. Reason: SubscriberId(" + subscriberIdentity + ") " +
						"and susbcriptionId(" + subscription.getId() + ") are not related");
			}

			return updateSubscription(subscription, newSubscriptionState, startTime, endTime, priority, rejectReason, currentTimeMS, transactionFactory);

		} catch (OperationFailedException e) {
			throw new OperationFailedException("Unable to update subscription for subscription ID: " + subscriptionId
					+ ". Reason: " + e.getMessage(), e.getErrorCode(), e);
		}
	}

	private Subscription updateSubscription(Subscription subscription, SubscriptionState newSubscriptionState, Long startTime, Long endTime, Integer newPriority,
											String rejectReason, long currentTimeMS, TransactionFactory transactionFactory) throws OperationFailedException {

		Subscription addOnSubscription = null;
		if (subscription.getStatus() == SubscriptionState.STARTED) {
			if (newSubscriptionState == SubscriptionState.STARTED) {
				
				/*
				 *  These checks are added for scenario when either start time or end time change required, so previous value should be maintained. 
				 */
				if (startTime == null) {
					startTime = subscription.getStartTime().getTime();
				} else if (Numbers.POSITIVE_LONG.apply(startTime) == false) {
					throw new OperationFailedException("Start time(" + startTime + ") should not be negative", ResultCode.INVALID_INPUT_PARAMETER);
				} else {
					if(isFutureSubscription(subscription, currentTimeMS) == false && startTime > currentTimeMS) {
						throw new OperationFailedException("Start time(" + new Timestamp(startTime).toString() + ") is greater or equal to current time for already activated addOn", ResultCode.INVALID_INPUT_PARAMETER);
					}
				}
				
				if (endTime == null) {
					endTime = subscription.getEndTime().getTime();
				} else if (Numbers.POSITIVE_LONG.apply(endTime) == false) {
					throw new OperationFailedException("End time(" + endTime + ") should not be negative", ResultCode.INVALID_INPUT_PARAMETER);
				}
				
				validateStartTimeAndEndTime(startTime, endTime, currentTimeMS);

				int priority = nonNull(newPriority)? newPriority : subscription.getPriority();

				if(priority < 1) {
					throw new OperationFailedException("Invalid priority value:"+ priority +", priority shoudld be greater than 1", ResultCode.INVALID_INPUT_PARAMETER);
				}
				
				if (changeSubscriptionDetails(subscription.getId(), startTime, endTime, transactionFactory, priority, subscription.getMetadata()) == 0) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "No rows updated for susbcription. Please provide valid susbcriptionId(" + subscription.getId() + ")");
					}
					return null;
				}


				
				addOnSubscription = new Subscription(subscription.getId(), subscription.getSubscriberIdentity(),
						subscription.getPackageId(), subscription.getProductOfferId(), new Timestamp(startTime),
						new Timestamp(endTime), newSubscriptionState, priority, subscription.getType(),
						subscription.getMetadata(), subscription.getParameter1(), subscription.getParameter2());
			}else if (newSubscriptionState == SubscriptionState.UNSUBSCRIBED) {
				if (changeStatus(subscription.getId(), newSubscriptionState.state, transactionFactory, rejectReason) == 0) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "No rows updated for susbcription. Please provide valid susbcriptionId(" + subscription.getId() + ")");
					}
					return null;
				}
				Timestamp startTime2 = startTime == null ? null :  new Timestamp(startTime);
				Timestamp endTime2 = endTime == null ? null : new Timestamp(endTime);
				addOnSubscription = new Subscription(subscription.getId(), subscription.getSubscriberIdentity(),
						subscription.getPackageId() , subscription.getProductOfferId(), startTime2, endTime2,
						newSubscriptionState, subscription.getPriority(), subscription.getType(),
						subscription.getMetadata(), subscription.getParameter1(), subscription.getParameter2());
			
				
			} else {
				throw new OperationFailedException("Invalid subscription status(" + newSubscriptionState + ") received. Old Status: "
						+ subscription.getStatus(), ResultCode.INVALID_INPUT_PARAMETER);
			}
		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Invalid subscription status(" + newSubscriptionState + ") received. Old Status: "
						+ subscription.getStatus());
			}

			throw new OperationFailedException("Invalid subscription status(" + newSubscriptionState + ") received. Old Status: " + subscription.getStatus(), ResultCode.INVALID_INPUT_PARAMETER);
		}
		
		return addOnSubscription;
	}

	private boolean isFutureSubscription(Subscription subscription, long currentTimeMS) {
		return subscription.getStartTime().getTime() > currentTimeMS;
	}

	private int changeSubscriptionDetails(String subscriptionId, Long startTime, Long endTime,
										  TransactionFactory transactionFactory, int priority,
										  SubscriptionMetadata metadata) throws OperationFailedException {

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Update subscription detail query: " + queryForUpdateSubscriptionDetailBySubscriptionId);
		}
		
		PreparedStatement psForUpdateStatus = null;
		try {
			transaction.begin();
			psForUpdateStatus = transaction.prepareStatement(queryForUpdateSubscriptionDetailBySubscriptionId);
			psForUpdateStatus.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
			psForUpdateStatus.setTimestamp(1, new Timestamp(startTime));
			psForUpdateStatus.setTimestamp(2, new Timestamp(endTime));
			psForUpdateStatus.setInt(3, priority);
			psForUpdateStatus.setString(4, SubscriptionUtil.createMetaString(metadata,subscriptionId,MODULE));
			psForUpdateStatus.setString(5, subscriptionId);
			
			Stopwatch watch = new Stopwatch();
			watch.start();
			int rowsUpdated = psForUpdateStatus.executeUpdate();
			watch.stop();

			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while updating subscription start or endtime. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time is high while updating subscription start or endtime. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}
			return rowsUpdated;
		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (isQueryTimeoutException(e)) {
				if (isTotalQueryTimeOutCountExceed()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "Total number of query timeouts(" + totalQueryTimeoutCount.intValue()
								+ ") exceeded then configured max number "
								+ "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
					}
					transaction.markDead();
				}
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while updating subscription from DB with data source name: "
								+ transaction.getDataSourceName());
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			
			throw new OperationFailedException(toStringSQLException(e), resultCode, e);
		} catch (TransactionException e) {

			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (isConnectionNotFoundException(e)) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to update subscription for subscription ID: " + subscriptionId + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			
			transaction.rollback();
			
			throw new OperationFailedException(e.getMessage(), sprErrorCode, e);

		} finally {
			closeQuietly(psForUpdateStatus);
			endTransaction(transaction);
		}
	}

	private int changeStatus(String subscriptionId, int newState, TransactionFactory transactionFactory, String rejectReason) throws OperationFailedException {

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}


		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Update subscription status query: " + queryForUpdateSubscriptionStatusBySubscriptionId);
		}

		PreparedStatement psForUpdateStatus = null;
		try {
			transaction.begin();
			psForUpdateStatus = transaction.prepareStatement(queryForUpdateSubscriptionStatusBySubscriptionId);
			psForUpdateStatus.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
			psForUpdateStatus.setInt(1, newState);
			psForUpdateStatus.setString(2, rejectReason);
			psForUpdateStatus.setString(3, subscriptionId);

			Stopwatch watch = new Stopwatch();
			watch.start();
			int rowsUpdated = psForUpdateStatus.executeUpdate();
			watch.stop();

			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while updating subscription. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time is high while updating subscription. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}
			return rowsUpdated;
		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (isQueryTimeoutException(e)) {
				if (isTotalQueryTimeOutCountExceed()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "Total number of query timeouts(" + totalQueryTimeoutCount.intValue()
								+ ") exceeded then configured max number "
								+ "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
					}
					transaction.markDead();
				}
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while updating subscription from DB with data source name: "
								+ transaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;

			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}
			transaction.rollback();
			throw new OperationFailedException(toStringSQLException(e), resultCode, e);
		} catch (TransactionException e) {

			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (isConnectionNotFoundException(e)) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to update subscription for subscription ID: " + subscriptionId + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();

			throw new OperationFailedException(e.getMessage(), sprErrorCode, e);

		} finally {
			closeQuietly(psForUpdateStatus);
			endTransaction(transaction);
		}
	}

	/*
	 * fetch subscription by subscription id.
	 * 
	 * Skips subscription for below scenario: > addOn not exist in repository >
	 * expired subscription > subscription in UNSUBSCRIBED state
	 */
	@VisibleForTesting
	Subscription getActiveSubscriptionBySubscriptionId(String subscriptionId, long currentTimeMS, TransactionFactory transactionFactory)
			throws OperationFailedException {
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "fetching subscription for subscription ID: " + subscriptionId);
		}

		DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
		if (dbTransaction == null) {
			throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		PreparedStatement psForFetchSubscription = null;
		ResultSet rsForFetchSubscription = null;
		try {
			dbTransaction.begin();
			psForFetchSubscription = dbTransaction.prepareStatement(queryForSelectSubscriptionBySubscriptionId);
			psForFetchSubscription.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
			psForFetchSubscription.setString(1, subscriptionId);
			Stopwatch watch = new Stopwatch();
			watch.start();
			rsForFetchSubscription = psForFetchSubscription.executeQuery();
			watch.stop();
			
			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while updating subscription. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time is high while updating subscription. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}
			
			resetTotalQueryTimeoutCount();
			
			if (rsForFetchSubscription.next()) {
				String subscriberID = rsForFetchSubscription.getString("SUBSCRIBER_ID");
				String packageId = rsForFetchSubscription.getString("PACKAGE_ID");
				String productOfferId = rsForFetchSubscription.getString("PRODUCT_OFFER_ID");
				Timestamp startTime = rsForFetchSubscription.getTimestamp("START_TIME");
				Timestamp endTime = rsForFetchSubscription.getTimestamp("END_TIME");
				int statusValue = rsForFetchSubscription.getInt("STATUS");
				SubscriptionState subscriptionState = SubscriptionState.fromValue(statusValue);
				String param1 = rsForFetchSubscription.getString("PARAM1");
				String param2 = rsForFetchSubscription.getString("PARAM2");
				int priority = rsForFetchSubscription.getInt("PRIORITY");
				String type = rsForFetchSubscription.getString("TYPE");
				String metadata = rsForFetchSubscription.getString("METADATA");

				if (isPackageExist(packageId, productOfferId)==false){
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Skip subscription: " + subscriberID + " for subscriber: " + subscriberID
								+ ". Reason: Subscription expired");
					}
					return null;
				}

                ProductOffer addOn = policyRepository.getProductOffer().addOn().byId(productOfferId);
                if (findActiveSubscription(subscriberID, packageId, addOn)) {
                    return null;
                }


				if (endTime != null && endTime.getTime() <= currentTimeMS) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Skip subscription: " + subscriberID + " for subscriber: " + subscriberID
								+ ". Reason: Subscription expired");
					}
					return null;
				}

				if (subscriptionState == SubscriptionState.UNSUBSCRIBED) {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, "Skip subscription: " + subscriberID + " for subscriber: " + subscriberID
								+ ". Reason: Subscription is unsubscribed");
					}
					return null;
				}

				Subscription addOnSubscription = new Subscription(subscriptionId, subscriberID, packageId,
						productOfferId, startTime, endTime, subscriptionState, priority,
						SubscriptionType.fromVal(type), SubscriptionUtil.createMetaData(metadata,subscriptionId,MODULE),
						param1, param2);
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Subscription found: " + addOnSubscription.getId());
				}

				return addOnSubscription;
			}
			
		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;
			if (isQueryTimeoutException(e)) {
				if (isTotalQueryTimeOutCountExceed()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "total number of query timeouts(" + totalQueryTimeoutCount.intValue()
								+ ") exceeded then configured max number "
								+ "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
					}
					dbTransaction.markDead();
				}
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while updating subscription from DB with data source name: "
								+ dbTransaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			} else if (dbTransaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + dbTransaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				dbTransaction.markDead();

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException(toStringSQLException(e), resultCode, e);
		} catch (TransactionException e) {

			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (isConnectionNotFoundException(e)) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to update subscription for subscription ID: " + subscriptionId + " from DB with data source name: "
								+ dbTransaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException(e.getMessage(), sprErrorCode, e);

		} finally {
			closeQuietly(rsForFetchSubscription);
			closeQuietly(psForFetchSubscription);
			endTransaction(dbTransaction);
		}

		return null;
	}

	private boolean isPackageExist(String packageId, String productOfferId) {
		ProductOffer addOn = policyRepository.getProductOffer().addOn().byId(productOfferId);
		if (addOn == null) {

            QuotaTopUp quotaTopUp = policyRepository.getActiveQuotaTopUpById(packageId);

            if(isNull(quotaTopUp)) {

                BoDPackage bodPackage = policyRepository.getBoDPackage().byId(packageId);
                if(isNull(bodPackage)) {
					return false;
                }

            }
        }
		return true;
	}

    private boolean findActiveSubscription(String subscriberID, String packageId, ProductOffer addOn) {
        if (addOn == null) {

            QuotaTopUp quotaTopUp = policyRepository.getActiveQuotaTopUpById(packageId);

            if(isNull(quotaTopUp)) {

                BoDPackage bodPackage = policyRepository.getBoDPackage().byId(packageId);
                if(isNull(bodPackage)) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skip subscription: " + subscriberID + " for subscriber: " + subscriberID
                                + ". Reason: Subscription expired");
                    }
                    return true;
                }

            }
        }
        return false;
    }

	public int unsubscribeBySubscriberId(String subscriberIdentity, Transaction transaction) throws OperationFailedException, TransactionException {
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Unsubscribing all subscription for subscriber("+subscriberIdentity+")");
		}
		
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Unsubscribe by subscriber identity query: " + queryForUnsubscribeBySubscriberId);
		}
		
		PreparedStatement psForUnsubscribeBySubscriberId = null;
		try {
			
			psForUnsubscribeBySubscriberId = transaction.prepareStatement(queryForUnsubscribeBySubscriberId);
			psForUnsubscribeBySubscriberId.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
			psForUnsubscribeBySubscriberId.setString(1, subscriberIdentity);
			
			Stopwatch watch = new Stopwatch();
			watch.start();
			int updateCount = psForUnsubscribeBySubscriberId.executeUpdate();
			watch.stop();
			
			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while unsubscribing subscription. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time is high while unsubscribing subscription. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}
			
			resetTotalQueryTimeoutCount();
			
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Total("+updateCount+") subscriptions are unsubscribed for subscriber("+subscriberIdentity+") successfully");
			}
			
			return updateCount;
			 
		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;
			if (isQueryTimeoutException(e)) {
				if (isTotalQueryTimeOutCountExceed()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "total number of query timeouts(" + totalQueryTimeoutCount.intValue()
								+ ") exceeded then configured max number "
								+ "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
					}
					transaction.markDead();
				}
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while unsubscribing subscription from DB with data source name: "
								+ transaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException(toStringSQLException(e), resultCode, e);
			
		} finally {
			closeQuietly(psForUnsubscribeBySubscriberId);
		}
	}

	private Subscription addRncSubscriptionDetails(ProductOffer addOnProductOffer, RnCPackage rncPackage,
												   String subscriberIdentity, String parentId,
												   Integer subscriptionStatusValue, long startTime, long endTime,
												   Integer priority, Transaction transaction, String param1,
												   String param2, String subscriptionId, String metadata)
	throws OperationFailedException, TransactionException,SQLException{


		SubscriptionState subscriptionState = getSubscriptionStateFromValue(subscriberIdentity, subscriptionStatusValue);

		Timestamp endTimeMSTS = new Timestamp(endTime);
		Timestamp startTimeMSTS = new Timestamp(startTime);

        if(isNull(priority)) {
            priority = CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY;
        }


		if (getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "Subscribe package query: " + queryForSubscribeAddon);
		}
		PreparedStatement psForSubscriberAddOn = null;
		try {
			psForSubscriberAddOn = transaction.prepareStatement(queryForSubscribeAddon);
			psForSubscriberAddOn.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);


			String addOnSubscriptionId = Strings.isNullOrBlank(subscriptionId) ? getNextSubscriptionId() : subscriptionId.trim();

			psForSubscriberAddOn.setString(1, addOnSubscriptionId);
			psForSubscriberAddOn.setString(2, rncPackage.getId());
			psForSubscriberAddOn.setString(3, addOnProductOffer.getId());
			psForSubscriberAddOn.setString(4, parentId);
			psForSubscriberAddOn.setString(5, subscriberIdentity);
			psForSubscriberAddOn.setString(6, CommonConstants.DEFAULT_SERVER_INSTANCE_VALUE);
			psForSubscriberAddOn.setTimestamp(7, startTimeMSTS);
			psForSubscriberAddOn.setTimestamp(8, endTimeMSTS);
			psForSubscriberAddOn.setString(9, subscriptionState.getStringVal());
			psForSubscriberAddOn.setInt(10, priority);
			psForSubscriberAddOn.setString(11, param1);
			psForSubscriberAddOn.setString(12, param2);
			psForSubscriberAddOn.setString(13, SubscriptionType.RO_ADDON.name());
			psForSubscriberAddOn.setString(14, metadata);


			Stopwatch watch = new Stopwatch();
			watch.start();
			psForSubscriberAddOn.execute();
			watch.stop();

			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while subscribing package. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time is high while subscribing package. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}

			resetTotalQueryTimeoutCount();

			return new Subscription(addOnSubscriptionId, subscriberIdentity, rncPackage.getId(),
					addOnProductOffer.getId(), startTimeMSTS, endTimeMSTS, subscriptionState, priority,
					SubscriptionType.RO_ADDON, SubscriptionUtil.createMetaData(metadata, subscriptionId,MODULE), param1,
					param2);
		} finally {
			closeQuietly(psForSubscriberAddOn);
		}
	}

	private Subscription addSubscriptionDetails(ProductOffer addOnProductOffer, String packageId, String subscriberIdentity, String parentId, Integer subscriptionStatusValue, long startTime,
												long endTime, Integer priority, Transaction transaction, String param1, String param2, String subscriptionId, SubscriptionType subscriptionType)
			throws OperationFailedException, TransactionException,SQLException{


		SubscriptionState subscriptionState = getSubscriptionStateFromValue(subscriberIdentity, subscriptionStatusValue);

		Timestamp endTimeMSTS = new Timestamp(endTime);
		Timestamp startTimeMSTS = new Timestamp(startTime);

		if(isNull(priority)) {
			priority = CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY;
		}


		if (getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "Subscribe package query: " + queryForSubscribeAddon);
		}
		PreparedStatement psForSubscriberAddOn = null;
		try {
			psForSubscriberAddOn = transaction.prepareStatement(queryForSubscribeAddon);
			psForSubscriberAddOn.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);


			String addOnSubscriptionId = Strings.isNullOrBlank(subscriptionId) ? getNextSubscriptionId() : subscriptionId.trim();

			psForSubscriberAddOn.setString(1, addOnSubscriptionId);
			psForSubscriberAddOn.setString(2, packageId);
			psForSubscriberAddOn.setString(3, addOnProductOffer.getId());
			psForSubscriberAddOn.setString(4, parentId);
			psForSubscriberAddOn.setString(5, subscriberIdentity);
			psForSubscriberAddOn.setString(6, CommonConstants.DEFAULT_SERVER_INSTANCE_VALUE);
			psForSubscriberAddOn.setTimestamp(7, startTimeMSTS);
			psForSubscriberAddOn.setTimestamp(8, endTimeMSTS);
			psForSubscriberAddOn.setString(9, subscriptionState.getStringVal());
			psForSubscriberAddOn.setInt(10, priority);
			psForSubscriberAddOn.setString(11, param1);
			psForSubscriberAddOn.setString(12, param2);
			psForSubscriberAddOn.setString(13, subscriptionType.name());
			psForSubscriberAddOn.setString(14, null);


			Stopwatch watch = new Stopwatch();
			watch.start();
			psForSubscriberAddOn.executeUpdate();
			watch.stop();

			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while subscribing package. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time is high while subscribing package. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}

			resetTotalQueryTimeoutCount();

			return new Subscription(addOnSubscriptionId, subscriberIdentity, addOnProductOffer.getDataServicePkgData().getId(),addOnProductOffer.getId(), startTimeMSTS, endTimeMSTS,
					subscriptionState, priority, SubscriptionType.ADDON, param1, param2);
		} finally {
			closeQuietly(psForSubscriberAddOn);
		}
	}

	public void importSubscriptionsAndUsages(SPRInfo sprInfo, SubscriberInfo subscriberInfo, Transaction transaction) throws OperationFailedException {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Importing usages and subscriptions for subscriber: " + sprInfo.getSubscriberIdentity());
		}

		try {
			List<SubscriberUsage> basePackageUsages = subscriberInfo.getBasePackageUsages();

			if (Collectionz.isNullOrEmpty(basePackageUsages) == false) {
				umOperation.insert(sprInfo.getSubscriberIdentity(), basePackageUsages, transaction);

			}else{

				if(Collectionz.isNullOrEmpty(subscriberInfo.getBasePackageNonMonetoryBalances()) == false){
					abmfOperation.insert(sprInfo.getSubscriberIdentity(), null, subscriberInfo.getBasePackageNonMonetoryBalances(), transaction);
				}

				if(Collectionz.isNullOrEmpty(subscriberInfo.getBasePackageRnCNonMonetaryBalances()) == false){
					rnCABMFOperation.insert(sprInfo.getSubscriberIdentity(), null, subscriberInfo.getBasePackageRnCNonMonetaryBalances(), transaction);
				}
			}

			if(Collectionz.isNullOrEmpty(subscriberInfo.getMonetaryBalances()) == false){
				monetaryABMFOperationImpl.insert(sprInfo.getSubscriberIdentity(), subscriberInfo.getMonetaryBalances(), transaction);
				if(nonNull(balanceEDRListener)){
					for(MonetaryBalance monetaryBalance : subscriberInfo.getMonetaryBalances()) {
						balanceEDRListener.addMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, null, sprInfo, IMPORT_SUBSCRIBER, ActionType.ADD.name(),null), null, IMPORT_SUBSCRIBER);
					}
				}
			}

			List<SubscriptionDetail> addOnSubscriptionDetails = subscriberInfo.getAddOnSubscriptionsDetails();
			if (Collectionz.isNullOrEmpty(addOnSubscriptionDetails) == false) {
				importSubscriptions(sprInfo.getSubscriberIdentity(), addOnSubscriptionDetails, transaction);
			}

		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (isQueryTimeoutException(e)) {
				if (isTotalQueryTimeOutCountExceed()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "total number of query timeouts(" + totalQueryTimeoutCount.intValue()
								+ ") exceeded then configured max number "
								+ "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
					}
					transaction.markDead();
				}
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB query timeout while migrating subscriber(" + sprInfo.getSubscriberIdentity() + ")  from DB with data source name: "
								+ transaction.getDataSourceName());
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException(toStringSQLException("importing subscriber", sprInfo.getSubscriberIdentity(), e), resultCode, e);
		} catch (TransactionException e) {
			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (isConnectionNotFoundException(e)) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to import subscriber with subscriber ID: " + sprInfo.getSubscriberIdentity() + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException("Unable to import subscriber with subscriber Id: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (OperationFailedException e) {
			throw new OperationFailedException("Unable to import subscriber with subscriber Id: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: " + e.getMessage(), e.getErrorCode(), e);
		} 

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Usages and subscriptions imported for subscriber: " + sprInfo.getSubscriberIdentity());
		}
	}

	public void importSubscriptions(String subscriberIdentity, List<SubscriptionDetail> addOnSubscriptionDetails, Transaction transaction) throws OperationFailedException, TransactionException, SQLException {
		
		for (SubscriptionDetail addOnSubscriptionDetail : addOnSubscriptionDetails) {

			Subscription addOnSubscription = addOnSubscriptionDetail.getAddOnSubscription();

			int validity;
			ValidityPeriodUnit validityPeriodUnit;
			// DO NOT USE getActive method for policy because RETIRED package should considered
			ProductOffer subscriptionPackage = policyRepository.getProductOffer().byId(addOnSubscription.getProductOfferId());
			
			if (subscriptionPackage == null) {

				QuotaTopUp quotaTopUp = policyRepository.getQuotaTopUpById(addOnSubscription.getPackageId());

				if (nonNull(quotaTopUp)) {
					validity = quotaTopUp.getValidity();
					validityPeriodUnit = quotaTopUp.getValidityPeriodUnit();
				} else {
					getLogger().warn(MODULE, "Skipping subscription(id:" + addOnSubscription.getId() + ") for subscriber id: " + subscriberIdentity
							+ ". Reason: Subscription package not found for id: " + addOnSubscription.getId());
					continue;
				}

			} else {
				validity = subscriptionPackage.getValidityPeriod();
				validityPeriodUnit = subscriptionPackage.getValidityPeriodUnit();
			}
			
			// INTENTIONALY not checked fail status
			
			long currentTimeMS = System.currentTimeMillis();
			long startTime = addOnSubscription.getStartTime() == null ? null : addOnSubscription.getStartTime().getTime();
			long endTime = addOnSubscription.getEndTime() == null ? null : addOnSubscription.getEndTime().getTime();


			
			startTime = Numbers.POSITIVE_LONG.apply(startTime) ? startTime : currentTimeMS;

			endTime = Numbers.POSITIVE_LONG.apply(endTime) ? endTime
					: addTime(startTime, validity, validityPeriodUnit);

			Subscription subscriptionReturned = addSubscriptionDetails(subscriptionPackage, addOnSubscription.getPackageId(), addOnSubscription.getSubscriberIdentity()
					, null, addOnSubscription.getStatus().state
					, startTime
					, endTime
					, addOnSubscription.getPriority()
					, transaction, addOnSubscription.getParameter1(), addOnSubscription.getParameter2(), addOnSubscriptionDetail.getAddOnSubscription().getId(), addOnSubscription.getType());

			List<SubscriberUsage> usages = addOnSubscriptionDetail.getUsages();

			if (Collectionz.isNullOrEmpty(usages) == false) {

				for (SubscriberUsage usage : usages) {
					usage.setSubscriptionId(subscriptionReturned.getId());

					QuotaProfile quotaProfile = subscriptionPackage.getDataServicePkgData().getQuotaProfile(usage.getQuotaProfileId());

					if(quotaProfile!=null
							&& quotaProfile.getRenewalInterval()>0
							&& quotaProfile.getRenewalIntervalUnit()!=null){
						long resetTime = quotaProfile.getRenewalIntervalUnit().addTime(usage.getBillingCycleTime(),quotaProfile.getRenewalInterval());
						if(resetTime>endTime){
							resetTime = endTime;
						}
						usage.setBillingCycleResetTime(resetTime);
					} else {
						usage.setBillingCycleResetTime(endTime);
					}

				}

				umOperation.insert(subscriberIdentity, usages, transaction);

			}else{

				importBalances(subscriberIdentity, addOnSubscriptionDetail, subscriptionReturned, transaction);
			}

			if (subscriptionReturned.getSubscriberIdentity().equals(subscriberIdentity) == false) {
				throw new OperationFailedException("Subscriber identity(" + subscriberIdentity + ") provided and subscriber identity(" 
							+ subscriptionReturned.getSubscriberIdentity() + ")from subscription does not match", ResultCode.INVALID_INPUT_PARAMETER);
			}
		}
	}

	private void importBalances(String subscriberIdentity, SubscriptionDetail addOnSubscriptionDetail, Subscription subscription, Transaction transaction)  throws OperationFailedException, TransactionException {

		List<NonMonetoryBalance> nonMonetoryBalances = addOnSubscriptionDetail.getNonMonetoryBalances();

		if(Collectionz.isNullOrEmpty(nonMonetoryBalances) == false){
			abmfOperation.insert(subscriberIdentity, subscription.getId(), nonMonetoryBalances, transaction);
		}

		List<RnCNonMonetaryBalance> rncNonMonetaryBalances = addOnSubscriptionDetail.getRnCNonMonetoryBalances();

		if(Collectionz.isNullOrEmpty(rncNonMonetaryBalances) == false){

			rnCABMFOperation.insert(subscriberIdentity, subscription.getId(), rncNonMonetaryBalances, transaction);
		}

	}

	public Subscription subscribeQuotaTopUpByName(SPRInfo sprInfo, String parentId, String quotaTopUpName, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority, double subscriptionPrice,String monetaryBalanceId,String param1, String param2, TransactionFactory transactionFactory) throws OperationFailedException {
		return topUpSubscriptionOperation.subscribeQuotaTopUpByName(sprInfo, parentId, quotaTopUpName, subscriptionStatusValue, startTime, endTime, priority,subscriptionPrice,monetaryBalanceId,param1, param2,transactionFactory);
	}

	public Subscription subscribeQuotaTopUpById(SPRInfo sprInfo, String parentId, String topUpId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority,double subscriptionPrice, String monetaryBalanceId, String param1, String param2, TransactionFactory transactionFactory) throws OperationFailedException {
		return topUpSubscriptionOperation.subscribeQuotaTopUpById(sprInfo, parentId, topUpId, subscriptionStatusValue, startTime, endTime, priority,subscriptionPrice, monetaryBalanceId, param1, param2,transactionFactory);
	}

	public Subscription getActiveQuotaTopUpSubscriptionBySubscriptionId(String subscriptionId, long currentTimeMS, TransactionFactory transactionFactory)
			throws OperationFailedException {
		return topUpSubscriptionOperation.getActiveSubscriptionBySubscriptionId(subscriptionId, currentTimeMS, transactionFactory);
	}

	public Subscription updateQuotaTopUpSubscription(
			String subscriberIdentity,
			String subscriptionId, Integer subscriptionStatusValue,
			Long startTime,
			Long endTime,
			Integer priority, String rejectReason,
			TransactionFactory transactionFactory
	) throws OperationFailedException {
		return topUpSubscriptionOperation.updateSubscription(subscriberIdentity, subscriptionId, subscriptionStatusValue, startTime, endTime, priority, rejectReason,transactionFactory);
	}

	public LinkedHashMap<String, Subscription> getQuotaTopUpSubscriptions(
			String subscriberIdentity,
			TransactionFactory transactionFactory) throws OperationFailedException {
		return topUpSubscriptionOperation.getQuotaTopUpSubscriptions(subscriberIdentity, transactionFactory);
	}

	public LinkedHashMap<String, Subscription> getBodSubscriptions(
			String subscriberIdentity,
			TransactionFactory transactionFactory) throws OperationFailedException {
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Fetching BoD subscriptions for subscriber ID: " + subscriberIdentity);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to fetch BoD subscription for subscriber ID: " + subscriberIdentity
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		DBTransaction dbTransaction = transactionFactory.createReadOnlyTransaction();
		if (dbTransaction == null) {
			throw new OperationFailedException("Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			dbTransaction.begin();
			preparedStatement = dbTransaction.prepareStatement(getQueryForSubscriptions());
			preparedStatement.setString(1, subscriberIdentity);
			preparedStatement.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			resultSet = preparedStatement.executeQuery();
			stopwatch.stop();
			long queryExecutionTime = stopwatch.elapsedTime(TimeUnit.MILLISECONDS);

			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while fetching active subscription. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isLogLevel(LogLevel.WARN)) {
					getLogger().warn(MODULE, "DB Query execution time is high while fetching active subscription. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}

			LinkedHashMap<String, Subscription> subscriptions = createSubscriptionsForBod(subscriberIdentity, resultSet);

			resetTotalQueryTimeoutCount();

			if ((subscriptions == null || subscriptions.isEmpty()) == false) {
				return subscriptions;
			}
		} catch (SQLException e) {
			handleBoDException(e, subscriberIdentity, dbTransaction);
		} catch (TransactionException e) {
			handleBoDTransactionException(e, subscriberIdentity, dbTransaction);
		} catch (Exception e) {
			throw new OperationFailedException("Error while fetching bod subscription for subscriber: " + subscriberIdentity
					+ ". Reason. " + e.getMessage(), ResultCode.INTERNAL_ERROR, e);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			endTransaction(dbTransaction);
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Active BoD Subscriptions are not exist with subscriber ID: " + subscriberIdentity);
		}

		return new LinkedHashMap<>();

	}

	public void changeBillDay(String subscriberId, Timestamp nextBillDate, Timestamp billChangeDate,TransactionFactory transactionFactory) throws OperationFailedException {
		PreparedStatement psforUpdate = null;

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Changing Bill Day  for subscriber-identifier : " + subscriberId);
		}


		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to change bill day for subscriber ID: " + subscriberId
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to change bill day for subscriber ID: " + subscriberId
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}
		try {
			transaction.begin();
			psforUpdate = transaction.prepareStatement(queryForChangeBillDay);
			psforUpdate.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);
			psforUpdate.setTimestamp(1, nextBillDate);
			psforUpdate.setTimestamp(2,billChangeDate);
			psforUpdate.setString(3, subscriberId);

			Stopwatch watch = new Stopwatch();
			watch.start();
			psforUpdate.executeUpdate();
			watch.stop();
			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);

			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert("", Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while changing bill day information. "
								 +"Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {//NOSONAR
					getLogger().warn(MODULE, "OPEN-DB update query execution time getting high, Last Query execution time = "
							+ queryExecutionTime + " milliseconds.");
				}
			}
			totalQueryTimeoutCount.set(0);
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Change bill day operation successfully completed for subscriber-identifier : " + subscriberId);
			}
		} catch (SQLException e) {

			ResultCode resultCode = ResultCode.INTERNAL_ERROR;

			if (e.getErrorCode() == CommonConstants.QUERY_TIMEOUT_ERRORCODE) {
				if (totalQueryTimeoutCount.incrementAndGet() > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "Total number of query timeouts exceeded than configured max number of query timeouts, so SPR marked as DEAD");
					}
					transaction.markDead();
					totalQueryTimeoutCount.set(0);
				}

				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE,
						"DB Query Timeout while changing bill day for subscriber ID: " + subscriberId + " to DB with data source name: "
								+ transaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			} else if (transaction.isDBDownSQLException(e)) {
				transaction.markDead();
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, SPR marked as DEAD. Reason: " + e.getMessage());
				}

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();

			throw new OperationFailedException(toStringSQLException("changing bill day", subscriberId, e), resultCode, e);

		} catch (TransactionException e) {
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;
			if (isConnectionNotFoundException(e)) {

				alertListener.generateSystemAlert("", Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to change bill day from " + transaction.getDataSourceName() +
								" database. Reason: Connection not available");
				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			throw new OperationFailedException("Error while changing bill day for subscriber ID:" + subscriberId + ". Reason. " + e.getMessage(), resultCode, e);
		} catch (Exception e) {
			transaction.rollback();
			throw new OperationFailedException("Error while changing bill day for subscriber ID: " +
					subscriberId + ". Reason: " + e.getMessage(), e);
		} finally {
			closeQuietly(psforUpdate);
			endTransaction(transaction);
		}
	}

	public Subscription subscribeBod(BoDPackage subscriptionPackage,
									  SPRInfo sprInfo,
									  String parentId,
									  Integer subscriptionStatusValue,
									  Long startTime,
									  Long endTime,
									  TransactionFactory transactionFactory,
									  Integer priority,
									  String param1,
									  String param2,
									  @Nullable String subscriptionId,
									  double subscriptionPrice,
									  MonetaryBalance monetaryBalance,
									  String requestIpAddress)
			throws OperationFailedException {


		long currentTimeMS = System.currentTimeMillis();

		startTime = Numbers.POSITIVE_LONG.apply(startTime) ? startTime.longValue() : currentTimeMS;

		endTime = Numbers.POSITIVE_LONG.apply(endTime) ? endTime.longValue() : addTime(startTime, subscriptionPackage.getValidityPeriod(), subscriptionPackage.getValidityPeriodUnit());

		if(nonNull(priority) && priority < 1) {
			throw new OperationFailedException("Unable to subscribe BOD(ID: " + subscriptionPackage.getId() + ", Name: " + subscriptionPackage.getName()
					+ ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ " Reason: Invalid priority:" + priority);
		}

		validateStartTimeAndEndTime(startTime, endTime, currentTimeMS);

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to subscribe BOD(ID: " + subscriptionPackage.getId() + ", Name: " + subscriptionPackage.getName()
					+ ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		Transaction transaction = transactionFactory.createTransaction();

		if (transaction == null) {
			throw new OperationFailedException("Unable to subscribe BOD(ID: " + subscriptionPackage.getId() + ", Name: " + subscriptionPackage.getName()
					+ ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ " Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		try {
			transaction.begin();

			Subscription bodSubscription = addSubscriptionDetailsForBod(subscriptionPackage, sprInfo.getSubscriberIdentity(), parentId, subscriptionStatusValue, startTime, endTime, priority, transaction, param1, param2, subscriptionId);


			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Package(" + subscriptionPackage.getName() + ") subscription successful for subscriber ID: "
						+ sprInfo.getSubscriberIdentity() + " with subscription ID(" + bodSubscription.getId() + ")");
			}

			if(subscriptionPrice > 0 && nonNull(monetaryBalance)) {
				monetaryABMFOperationImpl.directDebitBalance(sprInfo.getSubscriberIdentity(), monetaryBalance.getId(), subscriptionPrice, transaction);
				if(nonNull(balanceEDRListener)) {
					MonetaryBalance previousMonetaryBalance = monetaryBalance.copy();
					monetaryBalance.setAvailBalance(subscriptionPrice*-1);
					balanceEDRListener.updateMonetaryEDR(new SubscriberMonetaryBalanceWrapper(monetaryBalance, previousMonetaryBalance, sprInfo, SUBSCRIBE_BOD, ActionType.UPDATE.name(),null),
							requestIpAddress, SUBSCRIBE_BOD);
				}
			}

			return bodSubscription;

		} catch (SQLException e) {
			ResultCode resultCode = ResultCode.INTERNAL_ERROR;
			if (isQueryTimeoutException(e)) {
				if (isTotalQueryTimeOutCountExceed()) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "total number of query timeouts(" + totalQueryTimeoutCount.intValue()
								+ ") exceeded then configured max number "
								+ "of query timeouts(" + CommonConstants.MAX_QUERY_TIMEOUT_COUNT_DEFAULT + "), so System marked as DEAD");
					}
					transaction.markDead();
				}
				alertListener.generateSystemAlert(SEVERITY, Alerts.QUERY_TIME_OUT, MODULE, "DB query timeout while subscribing package from DB with data source name: "+ transaction.getDataSourceName());

				resultCode = ResultCode.SERVICE_UNAVAILABLE;

			} else if (transaction.isDBDownSQLException(e)) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Database with data source name: " + transaction.getDataSourceName()
							+ " is Down, System marked as dead. Reason: " + e.getMessage());
				}
				transaction.markDead();

				resultCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();

			throw new OperationFailedException(toStringSQLException("subscribing package: " + subscriptionPackage.getName(),sprInfo.getSubscriberIdentity(), e), resultCode, e);

		} catch (TransactionException e) {

			ResultCode sprErrorCode = ResultCode.INTERNAL_ERROR;

			if (isConnectionNotFoundException(e)) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, MODULE,
						"Unable to subscribe package for subscriber ID: " + sprInfo.getSubscriberIdentity() + " from DB with data source name: "
								+ transaction.getDataSourceName() + ". Reason: Connection not available");
				sprErrorCode = ResultCode.SERVICE_UNAVAILABLE;
			}

			transaction.rollback();
			throw new OperationFailedException("Unable to subscribe package(" + subscriptionPackage.getName() + ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: " + e.getMessage(), sprErrorCode, e);
		} catch (OperationFailedException e) {

			transaction.rollback();

			throw new OperationFailedException("Unable to subscribe package(" + subscriptionPackage.getName() + ") for subscriber ID: " + sprInfo.getSubscriberIdentity()
					+ ". Reason: " + e.getMessage(), e.getErrorCode(), e);
		} finally {

			endTransaction(transaction);
		}
	}

	private String buildQueryForSubscribeBod() {
		return new StringBuilder("INSERT INTO ")
				.append(TABLE_TBLT_SUBSCRIPTION)
				.append("(SUBSCRIPTION_ID, PACKAGE_ID, PRODUCT_OFFER_ID, PARENT_IDENTITY, SUBSCRIBER_ID, SUBSCRIPTION_TIME, SERVER_INSTANCE_ID,")
				.append(" START_TIME, END_TIME, LAST_UPDATE_TIME, STATUS, PRIORITY, PARAM1, PARAM2,TYPE)")
				.append(" VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?,?)")
				.toString();
	}

	private Subscription addSubscriptionDetailsForBod(BoDPackage subscriptionPackage,
												String subscriberIdentity,
												String parentId,
												Integer subscriptionStatusValue,
												long startTime,
												long endTime,
												Integer priority,
												Transaction transaction,
												String param1,
												String param2,
												String subscriptionId)
			throws OperationFailedException, TransactionException, SQLException {


		SubscriptionState subscriptionState = getSubscriptionStateFromValue(subscriberIdentity, subscriptionStatusValue);

		Timestamp endTimeMSTS = new Timestamp(endTime);
		Timestamp startTimeMSTS = new Timestamp(startTime);

		if(isNull(priority)) {
			priority = 100;
		}

		if (getLogger().isLogLevel(LogLevel.DEBUG)) {
			getLogger().debug(MODULE, "Subscribe package query: " + queryForSubscribeBod);
		}

		PreparedStatement psForSubscriberBod = null;

		try {
			psForSubscriberBod = transaction.prepareStatement(queryForSubscribeBod);
			psForSubscriberBod.setQueryTimeout(CommonConstants.QUERY_TIMEOUT_DEFAULT);

			String quotaBodSubscriptionId = Strings.isNullOrBlank(subscriptionId) ? getNextSubscriptionId() : subscriptionId.trim();

			psForSubscriberBod.setString(1, quotaBodSubscriptionId);
			psForSubscriberBod.setString(2, subscriptionPackage.getId());
			psForSubscriberBod.setString(3, null);
			psForSubscriberBod.setString(4, parentId);
			psForSubscriberBod.setString(5, subscriberIdentity);
			psForSubscriberBod.setString(6, CommonConstants.DEFAULT_SERVER_INSTANCE_VALUE);
			psForSubscriberBod.setTimestamp(7, startTimeMSTS);
			psForSubscriberBod.setTimestamp(8, endTimeMSTS);
			psForSubscriberBod.setString(9, subscriptionState.getStringVal());
			psForSubscriberBod.setInt(10, priority);

			psForSubscriberBod.setString(11, param1);
			psForSubscriberBod.setString(12, param2);
			psForSubscriberBod.setString(13, SubscriptionType.BOD.name());

			Stopwatch watch = new Stopwatch();
			watch.start();
			psForSubscriberBod.execute();
			watch.stop();

			long queryExecutionTime = watch.elapsedTime(TimeUnit.MILLISECONDS);
			if (queryExecutionTime > AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS) {

				alertListener.generateSystemAlert(SEVERITY, Alerts.HIGH_QUERY_RESPONSE_TIME, MODULE,
						"DB Query execution time is high while subscribing package. "
								+ "Last Query execution time: " + queryExecutionTime + " ms");

				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "DB Query execution time is high while subscribing package. "
							+ "Last Query execution time: " + queryExecutionTime + " ms");
				}
			}

			resetTotalQueryTimeoutCount();

			return new Subscription(quotaBodSubscriptionId, subscriberIdentity, subscriptionPackage.getId(), null, startTimeMSTS, endTimeMSTS,
					subscriptionState, priority, SubscriptionType.BOD, param1, param2);

		} finally {
			closeQuietly(psForSubscriberBod);
		}
	}

	public Subscription updateFnFGroup(SPRInfo sprInfo, Subscription subscription, TransactionFactory transactionFactory)
			throws OperationFailedException {

		String subscriptionId = subscription.getId();
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Updating FnF group in subscription subscription(" + subscriptionId + ")");
		}

		if (transactionFactory.isAlive() == false) {
			throw new OperationFailedException("Unable to update FnF group for subscription ID: " + subscriptionId
					+ ". Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
		}

		long currentTimeMS = System.currentTimeMillis();

		try {
			Subscription subscriptionFromDB = getActiveSubscriptionBySubscriptionId(subscriptionId, currentTimeMS, transactionFactory);

			if (isNull(subscriptionFromDB)) {
				throw new OperationFailedException("Active subscription not found with ID: " + subscriptionId, ResultCode.NOT_FOUND);
			}

			if (subscription.getSubscriberIdentity().equals(sprInfo.getSubscriberIdentity()) == false) {
				throw new OperationFailedException("SubscriberId(" + sprInfo.getSubscriberIdentity() + ") " +
						"and susbcriptionId(" + subscription.getId() + ") are not related");
			}

			subscriptionFromDB.getMetadata().setFnFGroup(subscription.getMetadata().getFnFGroup());

			return updateSubscription(subscriptionFromDB, subscriptionFromDB.getStatus(), subscriptionFromDB.getStartTime().getTime(),
					subscriptionFromDB.getEndTime().getTime(), subscriptionFromDB.getPriority(), null, currentTimeMS, transactionFactory);

		} catch (OperationFailedException e) {
			throw new OperationFailedException("Unable to update FnF group for subscription ID: " + subscriptionId
					+ ". Reason: " + e.getMessage(), e.getErrorCode(), e);
		}
	}
}
