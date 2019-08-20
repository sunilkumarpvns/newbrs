
package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.AlternateIdentityMapper;
import com.elitecore.corenetvertex.spr.DeleteMarkedSubscriberPredicate;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.NotDeleteMarkedSubscriberPredicate;
import com.elitecore.corenetvertex.spr.QuotaProfileBalance;
import com.elitecore.corenetvertex.spr.RnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SPRProvider;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIdStatusDetail;
import com.elitecore.corenetvertex.spr.SubscriberAlternateIds;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalanceWrapper;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.SubscriberRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionRnCNonMonetaryBalance;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.data.AlternateIdActiveStatusPredicate;
import com.elitecore.corenetvertex.spr.data.AlternateIdType;
import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionDetail;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import com.elitecore.corenetvertex.spr.util.SubscriberPkgValidationUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 *
 * @author Chetan.Sankhala
 */
public class DDFTableImpl implements DDFTable {

	private static final String DATASOURCE_NOT_AVAILABLE = "Datasource not available";
	private static final String MODULE = "DDF-TBL";
	private DDFConfiguration ddfData;
	private SPRProvider subscriberRepositoryProvider;
	private List<String> stripPrefixes;
	private AlternateIdentityMapper identityMapper;
	private PolicyRepository policyRepository;
	private MonetaryBalanceOperation monetaryBalanceOperation;
	private RepositorySelector repositorySelector;


	public DDFTableImpl(DDFConfiguration ddfConfiguration,
						PolicyRepository policyRepository,
						SPRProvider subscriberRepositoryProvider,
						AlternateIdentityMapper mapper,
						RepositorySelector repositorySelector) {
		this.ddfData = ddfConfiguration;
		this.policyRepository = policyRepository;
		this.subscriberRepositoryProvider = subscriberRepositoryProvider;
		this.identityMapper = mapper;
		this.stripPrefixes =  buildStripPrefixList();
		this.repositorySelector = repositorySelector;
		this.monetaryBalanceOperation = new MonetaryBalanceOperation(repositorySelector);
	}

    private List<String> buildStripPrefixList() {
	    if(isNull(ddfData)){
	    	return null;
		}
		if(StringUtils.isEmpty(ddfData.getStripPrefixes())){
			return null;
		}
		return CommonConstants.COMMA_SPLITTER.split(ddfData.getStripPrefixes());
    }


	/*
	 * Strips first matched prefix only
	 */
	private String stripPrefixes(String identityValue) {
		if(CollectionUtils.isEmpty(stripPrefixes)){
			return identityValue;
		}
		for (int i = 0; i < stripPrefixes.size(); i++) {
			if (identityValue.startsWith(stripPrefixes.get(i))) {
				return StringUtils.replaceOnce(identityValue, stripPrefixes.get(i), "");
			}
		}
		return identityValue;
	}


	@Override
	public void addProfile(SubscriberDetails subscriberDetails, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		SPRInfo sprInfo = subscriberDetails.getSprInfo();
		if (sprInfo == null || Strings.isNullOrBlank(sprInfo.getSubscriberIdentity())) {
			throw new OperationFailedException("Unable to add subscriber profile. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		String subscriberIdentity = stripPrefixes(sprInfo.getSubscriberIdentity());
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.CREATE_SUBSCRIBER);

		((SPRInfoImpl) sprInfo).setSubscriberIdentity(subscriberIdentity);
		//validate alternate id mapping
		boolean isAlternateIdExists = alternateIdentityExists(sprInfo,repository);
		if(isAlternateIdExists){
			String subscriberId = getSubscriberIdForAlternateId(repository.getAlternateIdField().getStringValue(sprInfo), status -> true);
			if(StringUtils.isBlank(subscriberId) == false){
				if(subscriberId.equals(sprInfo.getSubscriberIdentity())){
					throw new OperationFailedException("Subscriber already exists with subscriber ID: "+subscriberId
							, ResultCode.ALREADY_EXIST);
				}else {
					throw new OperationFailedException("Unable to add subscriber profile for Subscriber ID: " + sprInfo.getSubscriberIdentity() + ". Reason: Alternate Id: " + repository.getAlternateIdField().getStringValue(sprInfo) + " already Exists"
							, ResultCode.INVALID_INPUT_PARAMETER);
				}
			}
		}
		repository.addProfile(subscriberDetails, requestIpAddress);

		addAlternateIdMapping(sprInfo, subscriberIdentity, repository);
	}

	private void addAlternateIdMapping(SPRInfo sprInfo, String subscriberIdentity, SubscriberRepository repository) throws OperationFailedException {

		if (alternateIdentityExists(sprInfo, repository)) {
			identityMapper.addMapping(subscriberIdentity, repository.getAlternateIdField().getStringValue(sprInfo));
		}
	}

	private void replaceAlternateIdMapping(SPRInfo sprInfo, String oldSubscriberIdentity, SubscriberRepository repository) throws OperationFailedException {
		if (identityMapper == null) {
			return;
		}
		SPRFields alternateIdField = repository.getAlternateIdField();
		identityMapper.replaceMapping(oldSubscriberIdentity,
				sprInfo.getSubscriberIdentity(),
				alternateIdField==null?null:alternateIdField.getStringValue(sprInfo));
	}

	private boolean alternateIdentityExists(SPRInfo sprInfo, SubscriberRepository repository) {

		if (identityMapper == null) {
			return false;
		}

		if (repository.getAlternateIdField() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not configured for subscriber repository: " + repository.getName());
			}
			return false;
		}

		String alternateIdValue = repository.getAlternateIdField().getStringValue(sprInfo);
		if (Strings.isNullOrBlank(alternateIdValue)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not found from profile");
			}
			return false;
		}

		return true;
	}

	@Override
	public void addProfile(SubscriberDetails subscriberDetails) throws OperationFailedException {
		try {
			addProfile(subscriberDetails, null, null);
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
		}
	}

	private void printWarnLog(UnauthorizedActionException e) {
		if (getLogger().isWarnLogLevel()) {
			getLogger().warn(MODULE, e.getMessage() + ". This message should never seen!!");
			getLogger().trace(MODULE, e);
		}
	}

	@Override
	public int updateProfile(SPRInfo sprInfo, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		if (sprInfo == null || Strings.isNullOrBlank(sprInfo.getSubscriberIdentity())) {
			throw new OperationFailedException("Unable to update subscriber profile. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		String subscriberIdentity = stripPrefixes(sprInfo.getSubscriberIdentity());
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);
		((SPRInfoImpl) sprInfo).setSubscriberIdentity(subscriberIdentity);
		boolean isAlternateIdExists = alternateIdentityExists(sprInfo,repository);
		if(isAlternateIdExists){
			String subscriberId = getSubscriberIdForAlternateId(repository.getAlternateIdField().getStringValue(sprInfo),status -> true);
			if(Strings.isNullOrBlank(subscriberId) == false && subscriberId.equals(sprInfo.getSubscriberIdentity()) == false){
				throw new OperationFailedException("Unable to update subscriber profile for Subscriber ID: " + sprInfo.getSubscriberIdentity() + ". Reason: Alternate Id: " + repository.getAlternateIdField().getStringValue(sprInfo) + " already Exists"
						, ResultCode.INVALID_INPUT_PARAMETER);
			}
		}

		int result = repository.updateProfile(sprInfo, requestIpAddress);

		if (identityMapper == null || result == 0) {
			return result;
		}

		if (repository.getAlternateIdField() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not configured for subscriber repository: " + repository.getName());
			}
			return result;
		}

		updateAlternateIdentityMapping(repository.getAlternateIdField().getStringValue(sprInfo), subscriberIdentity);
		return result;
	}

	private void updateAlternateIdentityMapping(String newAlternateIdValue, String subscriberIdentity)throws OperationFailedException {

		SubscriberAlternateIds allAlternateIds = identityMapper.getAlternateIds(subscriberIdentity);

		String oldAlternateIdValue = null;
		if (nonNull(allAlternateIds) && nonNull(allAlternateIds.getSprTypeAlternateId())) {
			oldAlternateIdValue = allAlternateIds.getSprTypeAlternateId().getAlternateId();
		}
		if (Strings.isNullOrBlank(newAlternateIdValue)) {
            /* IF new alternate id not received and mapping found THEN remove existing mapping */
			if (Strings.isNullOrBlank(oldAlternateIdValue) == false) {
				identityMapper.removeAlternateIdMappingByType(subscriberIdentity, oldAlternateIdValue, AlternateIdType.SPR, CommonConstants.STATUS_ACTIVE);
				return;
			}

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping update process of Alternate Id mapping for subscriber Id(" + subscriberIdentity + "). Reason: Alternate Id not changed");
			}
			return;
		}

		if (Strings.isNullOrBlank(oldAlternateIdValue)) {
            /* if alternate id not exist, than add mapping */
			if (isAlternateIdAlreadyExistInMapping(newAlternateIdValue, allAlternateIds) == false) {
				identityMapper.addMapping(subscriberIdentity, newAlternateIdValue);
				return;
			}else{
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping update process of Alternate Id mapping for subscriber Id(" + subscriberIdentity + "). Reason: Alternate Id "+newAlternateIdValue+" already Exist");
				}
				return;
			}
		}

		if (newAlternateIdValue.equalsIgnoreCase(oldAlternateIdValue)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping update process of Alternate Id mapping for subscriber Id(" + subscriberIdentity + "). Reason: Alternate Id not changed");
			}
			return;
		}

		if (isAlternateIdAlreadyExistInMapping(newAlternateIdValue, allAlternateIds) == false){
			identityMapper.updateMapping(subscriberIdentity, newAlternateIdValue);
		}
	}

	private boolean isAlternateIdAlreadyExistInMapping(String newAlternateIdValue, SubscriberAlternateIds allAlternateIds) {
		return nonNull(allAlternateIds) && nonNull(allAlternateIds.byAlternateId(newAlternateIdValue));
	}

	private String getAlternateIdOfSprType(String subscriberIdentity) throws OperationFailedException {
		SubscriberAlternateIds alternateIds = identityMapper.getAlternateIds(subscriberIdentity);
		if(isNull(alternateIds)){
			return null;
		}
		SubscriberAlternateIdStatusDetail sprTypeAlternateId = alternateIds.getSprTypeAlternateId();
		if(isNull(sprTypeAlternateId)){
			return null;
		}
		return sprTypeAlternateId.getAlternateId();

	}

	@Override
	public int updateProfile(SPRInfo sprInfo, String requestIpAddress) throws OperationFailedException {
		try {
			return updateProfile(sprInfo, null, requestIpAddress);
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
			return -1;
		}
	}

	@Override
	public Map<String, Integer> deleteProfile(List<String> subscriberIdentities, StaffData staffData, String requestIpAddress) throws OperationFailedException {

		if (Collectionz.isNullOrEmpty(subscriberIdentities)) {
			throw new OperationFailedException("Unable to delete subscribers. Reason: Subscriber Identities not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		Map<String, Integer> deletedSubscriberStatus = new HashMap<String, Integer>();

		for (String subscriberId : subscriberIdentities) {

			try {

				if (subscriberId == null) {
					continue;
				}

				subscriberId = stripPrefixes(subscriberId);
				SubscriberRepository repository = repositorySelector.select(subscriberId);
				checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.DELETE_SUBSCRIBER);
				int deleteResult = repository.markForDeleteProfile(subscriberId, requestIpAddress);
				deletedSubscriberStatus.put(subscriberId,deleteResult);

			} catch (Exception e) {
				deletedSubscriberStatus.put(subscriberId,-1);
				getLogger().error(MODULE, "Error while deleting subscriber with Subscriber Identity '" +subscriberId+ "'. Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		return deletedSubscriberStatus;
	}

	@Override
	public int deleteProfile(String subscriberIdentity, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to delete subscriber profile. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.DELETE_SUBSCRIBER);
		return repository.markForDeleteProfile(subscriberIdentity, requestIpAddress);
	}

	@Override
	public int deleteProfile(String subscriberIdentity, String requestIpAddress) throws OperationFailedException {
		try {
			return deleteProfile(subscriberIdentity, null, requestIpAddress);
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
			return -1;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SPRInfo searchSubscriber(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to search subscriber profile. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIBER);
		return repository.getProfile(subscriberIdentity, NotDeleteMarkedSubscriberPredicate.getInstance());
	}

	public void checkForUserAuthorizationBySubscriberIdentity(String subscriberIdentity, StaffData staffData, ACLAction aclAction) throws OperationFailedException, UnauthorizedActionException {
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to search Subscriber Session. Reason: SubscriberIdentity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBERSESSION, aclAction);
	}

	public void checkForUserAuthorizationByAlternateIdentity(String alternateIdentity, StaffData staffData, ACLAction aclAction) throws OperationFailedException, UnauthorizedActionException {
		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateIdentity);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to search subscriber profile. Reason: SubscriberIdentity not found for given AlternateIdentity"
					, ResultCode.NOT_FOUND);
		}

		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBERSESSION, aclAction);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SPRInfo searchSubscriberByAlternateId(String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to search subscriber profile. Reason: Subscriber Identity not found"
					, ResultCode.NOT_FOUND);
		}

		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIBER);
		return repository.getProfile(subscriberIdentity, NotDeleteMarkedSubscriberPredicate.getInstance());
	}

	@Override
	public String searchSubscriberIdByAlternateId(String alternateId, StaffData staffData) throws OperationFailedException {
		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Subscriber Identity not found for Alternate Identity: "+alternateId , ResultCode.NOT_FOUND);
		}

		return subscriberIdentity;
	}

	@Override
	public SPRInfo searchSubscriber(String subscriberIdentity) throws OperationFailedException {
		try {
			return searchSubscriber(subscriberIdentity, null);
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
			return null;
		}
	}

	@Override
	public LinkedHashMap<String, Subscription> getSubscriptions(String subscriberIdentity, StaffData staffData) throws OperationFailedException,
			UnauthorizedActionException {

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch active subscriptions. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		if (staffData != null) {
			List<String> groupNames = repository.getGroupIds();
			if (staffData.isAccessibleAction(groupNames, ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION) == false) {
				throw new com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException(ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);
			}
		}

		return repository.getAddonSubscriptions(subscriberIdentity);
	}

	@Override
	public LinkedHashMap<String, Subscription> getSubscriptions(String subscriberIdentity) throws OperationFailedException {

		try {
			return getSubscriptions(subscriberIdentity, null);
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
			return null;
		}
	}

	@Override
	public List<Subscription> subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId, String addonId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority, String param1, String param2, StaffData staffData, Integer billDay, String requestIpAddress, SubscriptionMetadata subscriptionMetadata) throws OperationFailedException, UnauthorizedActionException {
		try {
			SubscriptionResult subscriptionResult = subscribeAddOnProductOfferById(new SubscriptionParameter(sprInfo, parentId, addonId, subscriptionStatusValue, null, null, null,startTime, endTime, priority, param1, param2, billDay, subscriptionMetadata), null, requestIpAddress);
			return subscriptionResult.getSubscriptions();
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
		}
		return null;
	}

	@Override
	public SubscriptionResult subscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {

		String subscriberIdentity = subscriptionParameter.getSprInfo().getSubscriberIdentity();
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to subscribe addon. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.SUBSCRIBE_ADDON);
		return repository.subscribeAddOnProductOfferById(subscriptionParameter, requestIpAddress);
	}

	@Override
	public SubscriptionResult autoSubscribeAddOnProductOfferById(SubscriptionParameter subscriptionParameter) throws OperationFailedException, UnauthorizedActionException {
		String subscriberIdentity = subscriptionParameter.getSprInfo().getSubscriberIdentity();
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to subscribe addon. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		return repository.autoSubscribeAddOnProductOfferById(subscriptionParameter);
	}

	@Override
	public List<Subscription>  subscribeAddOnProductOfferById(SPRInfo sprInfo, String parentId, String addonId, Integer subscriptionStatusValue, Long startTime,
															  Long endTime, Integer priority, String param1, String param2, Integer billDay, String requestIpAddress, SubscriptionMetadata subscriptionMetadata) throws OperationFailedException {
		try {
			SubscriptionResult subscriptionResult = subscribeAddOnProductOfferById(new SubscriptionParameter(sprInfo, parentId, addonId, subscriptionStatusValue, null, null, null, startTime, endTime, priority, param1, param2, billDay, subscriptionMetadata), null, requestIpAddress);
			return subscriptionResult.getSubscriptions();
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
		}
		return null;
	}

	@Override
	public Subscription subscribeBodPackage(SubscriptionParameter subscriptionParameter, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		String subscriberIdentity = subscriptionParameter.getSprInfo().getSubscriberIdentity();
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to subscribe BOD. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository subscriberRepository = repositorySelector.select(subscriberIdentity);
		checkForUserAuthorization(staffData, subscriberRepository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.SUBSCRIBE_BOD);

		return subscriberRepository.subscribeBoDPackage(subscriptionParameter, requestIpAddress);
	}

	@Override
	public List<Subscription> subscribeProductOfferAddOnByName(SPRInfo sprInfo, String parentId, String addonName, Integer subscriptionStatusValue, Long startTime,
															   Long endTime, Integer priority, String param1, String param2, StaffData staffData, Integer billDay,
															   SubscriptionMetadata metadata) throws OperationFailedException, UnauthorizedActionException {
		SubscriberRepository subscriberRepository = repositorySelector.select(sprInfo.getSubscriberIdentity());

		checkForUserAuthorization(staffData, subscriberRepository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.SUBSCRIBE_ADDON);
		return subscriberRepository.subscribeProductOfferAddOnByName(sprInfo, parentId, addonName, subscriptionStatusValue, startTime, endTime, priority, param1, param2, billDay, metadata);
	}

	@Override
	public Subscription updateSubscription (SPRInfo sprInfo, String subscriptionId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority, String rejectReason,
										   String param1, String param2, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		String subscriberIdentity = sprInfo.getSubscriberIdentity();
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to update subscription. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UNSUBSCRIBE_ADDON);
		return repository.updateSubscription(sprInfo, subscriptionId, subscriptionStatusValue, startTime, endTime, priority, rejectReason, param1, param2, requestIpAddress);
	}

	@Override
	public Subscription updateSubscription(SPRInfo sprInfo, String subscriptionId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority, String rejectReason,
										   String param1, String param2, String requestIpAddress) throws OperationFailedException {
		try {
			return updateSubscription(sprInfo, subscriptionId, subscriptionStatusValue, startTime, endTime, priority, rejectReason, param1, param2, null, requestIpAddress);
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
		}
		return null;
	}

	@Override
	public boolean isTestSubscriber(String subscriberIdentity) throws OperationFailedException {
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch test subscriber. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		try {
			return repository.isTestSubscriber(subscriberIdentity);
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Assuming subcriber to be a live subscriber. Reason: Error in fetching repository while checking subscriber type");
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}
			return false;
		}
	}

	@Override
	public void addTestSubscriber(String subscriberIdentity, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to add test subscriber. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);
		repository.addTestSubscriber(subscriberIdentity);
	}

	@Override
	public Iterator<String> getTestSubscriberIterator() throws OperationFailedException {
		Collection<SubscriberRepository> allSubscriberRepository = subscriberRepositoryProvider.getAllSubscriberRepository();

		if (allSubscriberRepository.isEmpty()) {
			return Collections.emptyIterator();
		}

		return allSubscriberRepository.iterator().next().getTestSubscriberIterator();

	}

	@Override
	public void removeTestSubscriber(String subscriberIdentity,StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to remove test subscriber. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);
		repository.removeTestSubscriber(subscriberIdentity);
	}

	@Override
	public void removeTestSubscriber(List<String> subscriberIdentities, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {

		if (Collectionz.isNullOrEmpty(subscriberIdentities)) {
			throw new OperationFailedException("Unable to remove test subscriber. Reason: Subscriber Identities not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		Map<SubscriberRepository, List<String>> subscriberRepoToSubscriberIdMap = new HashMap<SubscriberRepository, List<String>>();

		for (String subscriberId : subscriberIdentities) {
			subscriberId = stripPrefixes(subscriberId);
			SubscriberRepository subscriberRepository = repositorySelector.select(subscriberId);
			checkForUserAuthorization(staffData, subscriberRepository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);

			List<String> subscriberIds = subscriberRepoToSubscriberIdMap.get(subscriberRepository);

			if (subscriberIds == null) {
				subscriberIds = new ArrayList<String>();
				subscriberRepoToSubscriberIdMap.put(subscriberRepository, subscriberIds);
			}
			subscriberIds.add(subscriberId);
		}

		for (Entry<SubscriberRepository, List<String>> entry : subscriberRepoToSubscriberIdMap.entrySet()) {
			entry.getKey().removeTestSubscriber(entry.getValue());
		}

	}

	@Override
	public List<SPRInfo> getDeleteMarkedProfiles(StaffData staffData) {
		List<SPRInfo> deleteMarkedProfiles = new ArrayList<SPRInfo>();

		for (SubscriberRepository repository : subscriberRepositoryProvider.getAllSubscriberRepository()) {

			try {
				List<String> groupNames = repository.getGroupIds();
				if (staffData.isAccessibleAction(groupNames, ACLModules.SUBSCRIBER, ACLAction.VIEW_DELETED_SUBSCRIBER) == false) {
					throw new com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException(ACLModules.SUBSCRIBER, ACLAction.VIEW_DELETED_SUBSCRIBER);
				}

				deleteMarkedProfiles.addAll(repository.getDeleteMarkedProfiles());
			} catch (Exception e) {
				getLogger().error(MODULE, "Error while fetching deleted subscribers in Subscriber Repository(" + repository.getName()
						+ "). Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		return deleteMarkedProfiles;
	}

	@Override
	public List<SPRInfo> getDeleteMarkedProfiles() throws OperationFailedException {

		return getDeleteMarkedProfiles(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SPRInfo getDeleteMarkedProfile(String subscriberIdentity, StaffData staffData) throws OperationFailedException,
			UnauthorizedActionException {

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch delete marked subscriber profile. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_DELETED_SUBSCRIBER);
		return repository.getProfile(subscriberIdentity, DeleteMarkedSubscriberPredicate.getInstance());
	}

	@Override
	public int purgeSubscriber(String subscriberIdentity, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to purge subscriber. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.PURGE_SUBSCRIBER);
		int result = repository.purgeProfile(subscriberIdentity, requestIpAddress);

		if (identityMapper == null || result == 0) {
			return result;
		}

		if (repository.getAlternateIdField() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not configured for subscriber repository: " + repository.getName());
			}
			return result;
		}

		identityMapper.removeMapping(subscriberIdentity);
		return result;
	}

	@Override
	public int purgeSubscriber(String subscriberIdentity, String requestIpAddress) throws OperationFailedException {
		try {
			return purgeSubscriber(subscriberIdentity, null, requestIpAddress);
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
			return 0;
		}
	}

	@Override
	public Map<String, Integer> purgeSubscriber(List<String> subscriberIdentities, StaffData staffData, String requestIpAddress) throws OperationFailedException {

		if (Collectionz.isNullOrEmpty(subscriberIdentities)) {
			throw new OperationFailedException("Unable to purge subscribers. Reason: Subscriber Identities not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		Map<SubscriberRepository, List<String>> subscriberRepoToSubscriberIdMap = new HashMap<SubscriberRepository, List<String>>();

		for (String subscriberId : subscriberIdentities) {

			if (subscriberId == null) {
				continue;
			}

			subscriberId = stripPrefixes(subscriberId);
			SubscriberRepository subscriberRepository = repositorySelector.select(subscriberId);
			List<String> subscriberIds = subscriberRepoToSubscriberIdMap.get(subscriberRepository);
			if (subscriberIds == null) {
				subscriberIds = new ArrayList<String>();
				subscriberRepoToSubscriberIdMap.put(subscriberRepository, subscriberIds);
			}
			subscriberIds.add(subscriberId);
		}

		Map<String, Integer> purgedSubscriberStatus = new HashMap<String, Integer>();

		for (Entry<SubscriberRepository, List<String>> entry : subscriberRepoToSubscriberIdMap.entrySet()) {

			try {

				SubscriberRepository repository = entry.getKey();
				checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.PURGE_SUBSCRIBER);
				Map<String, Integer> purgeResult = repository.purgeProfile(entry.getValue(), requestIpAddress);
				purgedSubscriberStatus.putAll(purgeResult);
				removeIdentityMapping(repository, purgeResult);

			} catch (Exception e) {
				getLogger().warn(MODULE, "Some subscriber may not be restored: " + entry.getValue());
				getLogger().error(MODULE, "Error while purging subscribers in Subscriber Repository(" + entry.getKey().getName()
						+ "). Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		return purgedSubscriberStatus;
	}

	private void removeIdentityMapping(SubscriberRepository repository, Map<String, Integer> results) throws OperationFailedException {

		if (identityMapper == null) {
			return;
		}

		if (repository.getAlternateIdField() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not configured for subscriber repository: " + repository.getName());
			}
			return;
		}

		for (Entry<String, Integer> entry: results.entrySet()) {
			if (entry.getValue() > 0) {
				identityMapper.removeMapping(entry.getKey());
			}
		}
	}

	@Override
	public Map<String, Integer> purgeSubscriber(List<String> subscriberIdentities) throws OperationFailedException {
		return purgeSubscriber(subscriberIdentities, null, null);
	}

	@Override
	public Map<String, Integer> purgeAllSubscribers(StaffData staffData, String requestIpAddress) throws OperationFailedException {

		Collection<SubscriberRepository> allRepositories = subscriberRepositoryProvider.getAllSubscriberRepository();

		Map<String, Integer> purgedSubscriberStatus = new HashMap<String, Integer>();

		for (SubscriberRepository repository : allRepositories) {
			try {
				checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.PURGE_SUBSCRIBER);
				Map<String, Integer> purgeResult = repository.purgeAllProfile(requestIpAddress);
				purgedSubscriberStatus.putAll(purgeResult);

				removeIdentityMapping(repository, purgeResult);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error while purging subscribers in Subscriber Repository(" + repository.getName()
						+ "). Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		return purgedSubscriberStatus;
	}

	@Override
	public int restoreSubscriber(String subscriberIdentity, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to restore subscriber. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.RESTORE_SUBSCRIBER);

		return repository.restoreProfile(subscriberIdentity, requestIpAddress);
	}

	@Override
	public Map<String, Integer> restoreAllSubscribers(StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		Map<String,Integer> restoreResults = new HashMap<String, Integer>();
		List<SPRInfo> deletedProfiles = getDeleteMarkedProfiles(staffData);

		for(SPRInfo spr : deletedProfiles){
			int result = restoreSubscriber(spr.getSubscriberIdentity(), requestIpAddress);
			restoreResults.put(spr.getSubscriberIdentity(), result);
		}
		return restoreResults;
	}

	@Override
	public int restoreSubscriber(String subscriberIdentity, String requestIpAddress) throws OperationFailedException {
		try {
			return restoreSubscriber(subscriberIdentity, null, requestIpAddress);
		} catch (UnauthorizedActionException e) {
			printWarnLog(e);
			return 0;
		}
	}

	@Override
	public Map<String, Integer> restoreSubscriber(List<String> subscriberIdentities, StaffData staffData) throws OperationFailedException {

		if (Collectionz.isNullOrEmpty(subscriberIdentities)) {
			throw new OperationFailedException("Unable to restore subscribers. Reason: Subscriber Identities not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		Map<SubscriberRepository, List<String>> subscriberRepoToSubscriberIdMap = new HashMap<SubscriberRepository, List<String>>();
		for (String subscriberId : subscriberIdentities) {

			if (subscriberId == null) {
				continue;
			}

			subscriberId = stripPrefixes(subscriberId);
			SubscriberRepository subscriberRepository = repositorySelector.select(subscriberId);
			List<String> subscriberIds = subscriberRepoToSubscriberIdMap.get(subscriberRepository);

			if (subscriberIds == null) {
				subscriberIds = new ArrayList<String>();
				subscriberRepoToSubscriberIdMap.put(subscriberRepository, subscriberIds);
			}
			subscriberIds.add(subscriberId);
		}

		Map<String, Integer> restoreSubscriberStatus = new HashMap<String, Integer>();

		for (Entry<SubscriberRepository, List<String>> entry : subscriberRepoToSubscriberIdMap.entrySet()) {
			try {
				checkForUserAuthorization(staffData, entry.getKey().getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.RESTORE_SUBSCRIBER);
				restoreSubscriberStatus.putAll(entry.getKey().restoreProfile(entry.getValue()));
			} catch (Exception e) {
				getLogger().warn(MODULE, "Some subscriber may not be restored: " + entry.getValue());
				getLogger().error(MODULE, "Error while restoring profile in Subscriber Repository("+entry.getKey().getName()
						+"). Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		return restoreSubscriberStatus;
	}

	@Override
	public Map<String, Integer> restoreSubscriber(List<String> subscriberIdentities) throws OperationFailedException {
		return restoreSubscriber(subscriberIdentities, null);
	}

	@Override
	public Map<String, Map<String, SubscriberUsage>> getCurrentUsage(String subscriberIdentity) throws OperationFailedException {

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch current usage for subscriber. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		return repositorySelector.select(subscriberIdentity).getCurrentUsage(subscriberIdentity);
	}

	@Override
	public List<SubscriptionInformation> getBalanceBySubscriberId(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException{
		return repositorySelector.select(sprInfo.getSubscriberIdentity()).getBalance(sprInfo, pkgId, subscriptionId);
	}

	@Override
	public List<SubscriptionInformation> getAllBalance(SPRInfo sprInfo) throws OperationFailedException {

		if (sprInfo == null) {
			throw new OperationFailedException("SPRInfo not found", ResultCode.NOT_FOUND);
		}
		String subscriberIdentity = sprInfo.getSubscriberIdentity();
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch balance for subscriber. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		return repositorySelector.select(subscriberIdentity).getBalance(sprInfo);
	}

	private void checkForUserAuthorization(@Nullable StaffData staffData, List<String> groupNames, ACLModules aclModule, ACLAction aclAction) throws UnauthorizedActionException {
		if (staffData != null) {
			if (staffData.isAccessibleAction(groupNames, aclModule, aclAction) == false) {
				throw new com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException(aclModule, aclAction);
			}
		}
	}

	private String checkSubscriberExistance(SPRInfo sprInfo) throws OperationFailedException{
		if (sprInfo == null) {
			throw new OperationFailedException("SPRInfo not found");
		}
		String subscriberIdentity = sprInfo.getSubscriberIdentity();
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch balance for subscriber. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		return stripPrefixes(subscriberIdentity);
	}

	@Override
	public SubscriberNonMonitoryBalance getRGNonMonitoryBalanceWithResetExpiredBalance(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException, UnauthorizedActionException {
		String subscriberIdentity = checkSubscriberExistance(sprInfo);
		checkForBillDateChange(sprInfo);
		return repositorySelector.select(subscriberIdentity).getRGNonMonitoryBalanceWithResetExpiredBalance(sprInfo, pkgId, subscriptionId);
	}

	@Override
	public SubscriberRnCNonMonetaryBalance getNonMonetaryBalanceForRnCPackage(SPRInfo sprInfo, String pkgId, String subscriptionId) throws OperationFailedException {
		String subscriberIdentity = checkSubscriberExistance(sprInfo);
		return repositorySelector.select(subscriberIdentity).getNonMonetaryBalanceForRnCPackage(sprInfo, pkgId, subscriptionId);
	}

	@Override
	public SubscriberNonMonitoryBalance getRGNonMonitoryBalance(SPRInfo sprInfo) throws OperationFailedException {
		String subscriberIdentity = checkSubscriberExistance(sprInfo);
		return repositorySelector.select(subscriberIdentity).getRGNonMonitoryBalance(sprInfo);
	}

	@Override
	public SubscriberRnCNonMonetaryBalance getRnCNonMonitoryBalance(SPRInfo sprInfo) throws OperationFailedException {
		String subscriberIdentity = checkSubscriberExistance(sprInfo);
		return repositorySelector.select(subscriberIdentity).getRnCNonMonetaryBalance(subscriberIdentity);
	}

	@Override
	public Subscription getSubscriptionBySubscriberId(String subscriberIdentity, String addOnSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {

		if (Strings.isNullOrBlank(addOnSubscriptionId)) {
			throw new OperationFailedException("Unable to fetch addon subscriptions. Reason: AddOn subscription id not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);
		return repository.getAddonSubscriptionsBySubscriptionId(subscriberIdentity,addOnSubscriptionId);
	}

	@Override
	public Subscription getSubscriptionByAlternateId(String alternateIdentity, String addOnSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException{
		if (Strings.isNullOrBlank(addOnSubscriptionId)) {
			throw new OperationFailedException("Unable to fetch addon subscriptions." +
					" Reason: AddOn subscription id not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateIdentity);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch addon subscriptions. Reason: subscriber identity not found for alternate Id: " + alternateIdentity
					, ResultCode.NOT_FOUND);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);
		return repository.getAddonSubscriptionsBySubscriptionId(subscriberIdentity, addOnSubscriptionId);
	}

	@Override
	public int updateProfileByAlternateId(SPRInfo sprInfo, String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {

		if (identityMapper ==null) {
			return 0;
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (sprInfo == null || Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to update subscriber profile."
					+ " Reason: Subscriber Identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		((SPRInfoImpl) sprInfo).setSubscriberIdentity(subscriberIdentity);

		return updateProfile(sprInfo, staffData, null);
	}

	@Override
	public int deleteProfileByAlternateId(String alternateId, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {

		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to delete subscriber profile. Reason: subscriber identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.DELETE_SUBSCRIBER);
		return repository.markForDeleteProfile(subscriberIdentity, requestIpAddress);
	}

	@Override
	public Map<String, Integer> deleteProfileByAlternateId(List<String> alternateIds, StaffData staffData, String requestIpAddress) throws OperationFailedException {

		if (Collectionz.isNullOrEmpty(alternateIds)) {
			throw new OperationFailedException("Unable to delete subscribers. Reason: Alternate Identities not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		Map<String, Integer> deletedSubscriberStatus = new HashMap<String, Integer>();

		for (String alternateId : alternateIds) {
			try {

				if (alternateId == null) {
					continue;
				}

				String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

				if (subscriberIdentity == null) {
					continue;
				}

				subscriberIdentity = stripPrefixes(subscriberIdentity);
				SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
				checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.DELETE_SUBSCRIBER);
				int deleteResult = repository.markForDeleteProfile(subscriberIdentity, requestIpAddress);
				deletedSubscriberStatus.put(alternateId,deleteResult);

			} catch (Exception e) {
				deletedSubscriberStatus.put(alternateId,-1);
				getLogger().error(MODULE, "Unable to delete subscriber profile. Subscriber Identity not found for Alternate Id: '" + alternateId+"'. Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		return deletedSubscriberStatus;

	}


	@Override
	public Map<String, Integer> purgeSubscriberByAlternateId(List<String> alternateIds, StaffData staffData, String requestIpAddress) throws OperationFailedException {
		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		Map<SubscriberRepository, List<String>> subscriberRepoToSubscriberIdMap = new HashMap<SubscriberRepository, List<String>>();

		Map<String,Integer> responseMap = Maps.newHashMap();
		Map<String,String> subscriberIdAlternateIdMap = Maps.newHashMap();
		for (String alternateId : alternateIds) {

			if (alternateId == null) {
				continue;
			}

			String subscriberId = identityMapper.getSubscriberId(alternateId);

			if (subscriberId == null) {
				responseMap.put(alternateId, 0);
				continue;
			}

			SubscriberRepository subscriberRepository = repositorySelector.select(subscriberId);
			List<String> subscriberIds = subscriberRepoToSubscriberIdMap.get(subscriberRepository);
			if (subscriberIds == null) {
				subscriberIds = new ArrayList<String>();
				subscriberRepoToSubscriberIdMap.put(subscriberRepository, subscriberIds);
			}
			subscriberIdAlternateIdMap.put(subscriberId, alternateId);
			subscriberIds.add(subscriberId);
		}


		for (Entry<SubscriberRepository, List<String>> entry : subscriberRepoToSubscriberIdMap.entrySet()) {

			try {
				SubscriberRepository repository = entry.getKey();
				checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.PURGE_SUBSCRIBER);
				Map<String, Integer> purgeResult = repository.purgeProfile(entry.getValue(), requestIpAddress);

				for (String subscriberId : entry.getValue()) {
					String alternateId = subscriberIdAlternateIdMap.get(subscriberId);
					if(purgeResult.containsKey(subscriberId)){
						responseMap.put(alternateId, purgeResult.get(subscriberId));
					}

				}
				removeIdentityMapping(repository, purgeResult);

			} catch (Exception e) {
				getLogger().warn(MODULE, "Some subscriber may not be restored: " + entry.getValue());
				getLogger().error(MODULE, "Error while purging subscribers in Subscriber Repository(" + entry.getKey().getName()
						+ "). Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		return responseMap;
	}

	@Override
	public Map<String, Integer> restoreSubscribersByAlternateId(List<String> alternateIds, StaffData staffData) throws OperationFailedException {
		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}
		Map<String,String> subscriberIdAlternateIdMap = Maps.newHashMap();
		List<String> subscriberIdentities = new ArrayList<String>();
		Map<String,Integer> responseMap = Maps.newHashMap();
		for (String alternateId : alternateIds) {

			if (alternateId == null) {
				continue;
			}

			String subscriberId = identityMapper.getSubscriberId(alternateId);

			if (subscriberId == null) {
				responseMap.put(alternateId, 0);
				continue;
			}
			subscriberIdAlternateIdMap.put(subscriberId,alternateId);
			subscriberIdentities.add(subscriberId);
		}



		Map<String,Integer> subscriberIdentityBasedMap =  restoreSubscriber(subscriberIdentities);



		for (String subscriberIdentity : subscriberIdentities) {
			String alternateId = subscriberIdAlternateIdMap.get(subscriberIdentity);
			if(subscriberIdentityBasedMap.containsKey(subscriberIdentity)){
				responseMap.put(alternateId, subscriberIdentityBasedMap.get(subscriberIdentity));
			}

		}
		return responseMap;


	}

	@Override
	public int purgeSubscriberByAlternateId(String alternateId, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException  {

		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to purge subscriber profile. Reason: subscriber identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.PURGE_SUBSCRIBER);
		int result = repository.purgeProfile(subscriberIdentity, requestIpAddress);

		if (result == 0) {
			return result;
		}

		if (repository.getAlternateIdField() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not configured for subscriber repository: " + repository.getName());
			}
			return result;
		}

		identityMapper.removeMapping(subscriberIdentity);
		return result;
	}

	@Override
	public int restoreSubscriberByAlternateId(String alternateId, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException  {

		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to restore subscriber profile. Reason: subscriber identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		return restoreSubscriber(subscriberIdentity, requestIpAddress);
	}

	@Override
	public LinkedHashMap<String, Subscription> getSubscriptionsbyAlternateId(String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {

		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch subscriptions. Reason: subscriber identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);

		return repository.getAddonSubscriptions(subscriberIdentity);
	}

	@Override
	public Map<String, Map<String, SubscriberUsage>> getCurrentUsageByAlternateId(String alternateId) throws OperationFailedException {

		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch current usage. Reason: subscriber identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		return repositorySelector.select(subscriberIdentity).getCurrentUsage(subscriberIdentity);
	}


	@Override
	public SPRInfo getProfile(String subscriberIdentity) throws OperationFailedException {

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch subscriber profile. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		return repository.getProfile(subscriberIdentity);
	}

	@Override
	public int updateProfile(String subscriberID, EnumMap<SPRFields, String> updatedProfile, StaffData staffData, String requestIpAddress)
			throws OperationFailedException, UnauthorizedActionException {
		if (Strings.isNullOrBlank(subscriberID)) {
			throw new OperationFailedException("Unable to update subscriber profile. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		String subscriberIdentity = stripPrefixes(subscriberID);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);
		String alternateId = getAlternateId(updatedProfile, repository);
		if(Strings.isNullOrBlank(alternateId) == false) {
			String subscriberIdForAlternateId = getSubscriberIdForAlternateId(alternateId,status -> true);
			if (Strings.isNullOrBlank(subscriberIdForAlternateId) == false && subscriberIdForAlternateId.equals(subscriberID) == false) {
				throw new OperationFailedException("Unable to update subscriber profile for Subscriber ID: " + subscriberID + ". Reason: Alternate Id: " + alternateId + " already Exists"
						, ResultCode.INVALID_INPUT_PARAMETER);
			}
		}

		int result = repository.updateProfile(subscriberIdentity, updatedProfile, requestIpAddress);

		if (identityMapper == null || result == 0) {
			return result;
		}

		if (repository.getAlternateIdField() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not configured for subscriber repository: " + repository.getName());
			}
			return result;
		}

		if (updatedProfile.containsKey(repository.getAlternateIdField()) == false) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not changed in updated profile for subscriber Id: " + subscriberID);
			}
			return result;
		}

		updateAlternateIdentityMapping(updatedProfile.get(repository.getAlternateIdField()), subscriberIdentity);
		return result;
	}

	private String getAlternateId(EnumMap<SPRFields, String> updatedProfile, SubscriberRepository repository) {
		if(isAlternateIdExists(updatedProfile,repository)){
			return updatedProfile.get(repository.getAlternateIdField());
		}
		return null;
	}

	public String getSubscriberIdForAlternateId(String alternateId) throws OperationFailedException {
		return getSubscriberIdForAlternateId(alternateId, AlternateIdActiveStatusPredicate.getInstance());
	}

	public String getSubscriberIdForAlternateId(String alternateId,Predicate<String> statusPredicate) throws OperationFailedException {
		if(identityMapper != null) {
			return identityMapper.getSubscriberId(alternateId,statusPredicate);
		}
		return null;
	}
	private boolean isAlternateIdExists(EnumMap<SPRFields, String> updatedProfile, SubscriberRepository repository) {

		if(identityMapper == null) {
			return false;
		}

		if(repository.getAlternateIdField() == null) {
			return false;
		}

		return updatedProfile.containsKey(repository.getAlternateIdField());
	}

	@Override
	public int updateProfileByAlternateId(String alternateId,
										  EnumMap<SPRFields, String> updatedProfile,
										  StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		if (identityMapper ==null) {
			return 0;
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to update subscriber profile."
					+ " Reason: Subscriber Identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		return updateProfile(subscriberIdentity, updatedProfile, staffData, requestIpAddress);
	}

	@Override
	public void resetBillingCycleBySubscriberIdentity(String subscriberID, String alternateID, String productOfferId,
													  long resetBillingCycleDate, String resetReason, String parameter1,
													  String parameter2, String parameter3) throws OperationFailedException {
		String subscriberIdentity = stripPrefixes(subscriberID);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.resetBillingCycle(subscriberIdentity, alternateID,productOfferId,resetBillingCycleDate, resetReason, parameter1, parameter2, parameter3);


	}

	@Override
	public void resetBillingCycleByAlternateID(String alternateID, String productOfferId,
											   long resetBillingCycleDate, String resetReason, String parameter1,
											   String parameter2, String parameter3) throws OperationFailedException {
		String subscriberID = null;
		if (identityMapper == null) {
			throw new OperationFailedException("Unable to insert reset usage information for subscriber."
					+ " Reason: Subscriber Identity not found for alternate Id: " + alternateID
					, ResultCode.NOT_FOUND);
		}

		if(Strings.isNullOrBlank(alternateID) == false){
			subscriberID = identityMapper.getSubscriberId(alternateID);
		}
		if(Strings.isNullOrBlank(subscriberID)){
			throw new OperationFailedException("Unable to insert reset usage information for subscriber."
					+ " Reason: Subscriber Identity not found for alternate Id: " + alternateID
					, ResultCode.NOT_FOUND);
		}
		resetBillingCycleBySubscriberIdentity(subscriberID, alternateID, productOfferId, resetBillingCycleDate, resetReason, parameter1, parameter2, parameter3);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void migrateSubscriber(String currentSubscriberIdentity, String newSubscriberIdentity, StaffData staffData, String requestIpAddress)
			throws UnauthorizedActionException, OperationFailedException {

		SPRInfo sprInfo = searchSubscriber(currentSubscriberIdentity);
		if (sprInfo == null) {
			throw new OperationFailedException("Unable to change subscriber identity. Reason: Subscriber not found with subscriber identity: " + currentSubscriberIdentity, ResultCode.NOT_FOUND);
		}

		String baseProductOfferName = sprInfo.getProductOffer();
		ProductOffer baseProductOffer = policyRepository.getProductOffer().byName(baseProductOfferName);
		SubscriberPkgValidationUtil.validateProductOffer(baseProductOffer, baseProductOfferName, sprInfo.getSubscriberIdentity(), SubscriberPkgValidationUtil.SUBSCRIBER_PROFILE_ADD_OPERATION);

		BasePackage basePkg = (BasePackage) baseProductOffer.getDataServicePkgData();
		if(nonNull(basePkg)) {
			SubscriberPkgValidationUtil.validateBasePackage(basePkg, basePkg.getName(), sprInfo.getSubscriberIdentity(), SubscriberPkgValidationUtil.SUBSCRIBER_PROFILE_ADD_OPERATION);
		}

		newSubscriberIdentity = stripPrefixes(newSubscriberIdentity);
		SubscriberRepository subscriberRepository = repositorySelector.select(newSubscriberIdentity);

		SPRInfo newProfileIfExists = subscriberRepository.getProfile(newSubscriberIdentity, Predicates.<SPRInfo>alwaysTrue());
		if (newProfileIfExists != null) {
			if (newProfileIfExists.getStatus().equalsIgnoreCase(SubscriberStatus.DELETED.name())) {
				throw new OperationFailedException("Subscriber already exists for new identity: "
						+ newSubscriberIdentity + " with DELETED status", ResultCode.ALREADY_EXIST);
			}
			throw new OperationFailedException("Subscriber already exists with new identity: " + newSubscriberIdentity, ResultCode.ALREADY_EXIST);
		}

		checkAuthorizationForMigrate(staffData, subscriberRepository);

		LinkedHashMap<String,Subscription> subscriptions = getSubscriptions(currentSubscriberIdentity);
		Map<String, Map<String, SubscriberUsage>> currentUsage = getCurrentUsage(currentSubscriberIdentity);

		SubscriberMonetaryBalance subscriberMonetaryBalance = getMonetaryBalance(currentSubscriberIdentity, com.elitecore.corenetvertex.util.util.Predicates.ALL_MONETARY_BALANCE);
		SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = getRGNonMonitoryBalance(sprInfo);
		SubscriberRnCNonMonetaryBalance subscriberRnCNonMonetaryBalance = getRnCNonMonitoryBalance(sprInfo);

		((SPRInfoImpl) sprInfo).setSubscriberIdentity(newSubscriberIdentity);
		((SPRInfoImpl) sprInfo).setModifiedDate(new Timestamp(System.currentTimeMillis()));

		SubscriberInfo subscriberInfo = new SubscriberInfo((SPRInfoImpl)sprInfo
				, createAddOnSubscriptionDetails(subscriptions, currentUsage, subscriberNonMonitoryBalance, subscriberRnCNonMonetaryBalance, newSubscriberIdentity)
				, getBasePackageUsages(currentSubscriberIdentity, newSubscriberIdentity, sprInfo, currentUsage));

		subscriberInfo.setBasePackageNonMonetoryBalances(createAndGetBasePackageNonMonetaryBalances(baseProductOffer, subscriberNonMonitoryBalance));
		subscriberInfo.setBasePackageRnCNonMonetaryBalances(createAndGetBasePackageRnCNonMonetaryBalances(baseProductOffer, subscriberRnCNonMonetaryBalance));
		subscriberInfo.setMonetaryBalances(createAndGetMonetaryBalances(subscriberMonetaryBalance));

		subscriberRepository.importSubscriber(subscriberInfo);

		getLogger().info(MODULE, "Added subscriber with new identity: " + newSubscriberIdentity + ", Dropping old subscriber with identity: " + currentSubscriberIdentity);
		replaceAlternateIdMapping(sprInfo, currentSubscriberIdentity, subscriberRepository);
		deleteProfile(currentSubscriberIdentity, requestIpAddress);
		purgeSubscriber(currentSubscriberIdentity, requestIpAddress);
		getLogger().info(MODULE, "Subscriber migrated successfully with new identity: " + newSubscriberIdentity);

	}

	private void checkAuthorizationForMigrate(StaffData staffData, SubscriberRepository subscriberRepository) throws UnauthorizedActionException {
		checkForUserAuthorization(staffData, subscriberRepository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.CREATE_SUBSCRIBER);
		checkForUserAuthorization(staffData, subscriberRepository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.DELETE_SUBSCRIBER);
		checkForUserAuthorization(staffData, subscriberRepository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.SUBSCRIBE_ADDON);
	}

	private List<SubscriberUsage> getBasePackageUsages(String currentSubscriberIdentity, String newSubscriberIdentity, SPRInfo sprInfo,
													   Map<String, Map<String, SubscriberUsage>> currentUsage) {
		List<SubscriberUsage> basePackageUsages = null;
		BasePackage basePackage = null;
		ProductOffer productOffer = policyRepository.getProductOffer().byName(sprInfo.getProductOffer());
		if(isNull(productOffer)){
			getLogger().warn(MODULE, "Skip adding base package usage for subscriber: " + currentSubscriberIdentity
					+ ". Reason: Product offer not found with name:" + sprInfo.getProductOffer());
		} else {
			basePackage = (BasePackage) productOffer.getDataServicePkgData();
		}

		if (basePackage == null) {
			getLogger().warn(MODULE, "Skip adding base package usage for subscriber: " + currentSubscriberIdentity
					+ ". Reason: Data package not associated with product offer:" + sprInfo.getProductOffer());
		} else {
			basePackageUsages = createBasePackageUsages(currentUsage, basePackage.getId(), currentSubscriberIdentity, newSubscriberIdentity);
		}
		return basePackageUsages;
	}

	private @Nullable List<SubscriberUsage> createBasePackageUsages(Map<String, Map<String, SubscriberUsage>> currentUsage, String dataPackageId, String currentSubscriberIdentity, String newSubscriberIdentity) {

		List<SubscriberUsage> usages = new ArrayList<SubscriberUsage>();
		Map<String, SubscriberUsage> dataPkgUsagesMap = currentUsage.get(dataPackageId);
		if (Maps.isNullOrEmpty(dataPkgUsagesMap) == false) {
			for (SubscriberUsage subscriberUsage : dataPkgUsagesMap.values()) {
				subscriberUsage.setSubscriberIdentity(newSubscriberIdentity);
				usages.add(subscriberUsage);
			}
		} else {
			getLogger().warn(MODULE, "Skip adding base package usage for subscriber: " + currentSubscriberIdentity
					+ ". Reason: Data package usage not found with package id:" + dataPackageId);
		}
		return usages;
	}

	private List<SubscriptionDetail> createAddOnSubscriptionDetails(LinkedHashMap<String, Subscription> subscriptions,
																	Map<String, Map<String, SubscriberUsage>> currentUsage, SubscriberNonMonitoryBalance subscriberNonMonitoryBalance,
																	SubscriberRnCNonMonetaryBalance subscriberRnCNonMonetaryBalance, String newSubscriberIdentity) {

		List<SubscriptionDetail> addOnSubscriptionDetails = new ArrayList<SubscriptionDetail>();

		for (Subscription addOnSubscription : subscriptions.values()) {

			Map<String, SubscriberUsage> usageMap = currentUsage.get(addOnSubscription.getId());
			List<SubscriberUsage> usages = new ArrayList<SubscriberUsage>();
			List<NonMonetoryBalance> nonMonetoryBalances = null;
			List<RnCNonMonetaryBalance> rncNonMonetaryBalances = null;


			if (Maps.isNullOrEmpty(usageMap) == false) {
				for (SubscriberUsage subscriberUsage : usageMap.values()) {
					subscriberUsage.setSubscriberIdentity(newSubscriberIdentity);
					usages.add(subscriberUsage);
				}
			}

			if(nonNull(subscriberNonMonitoryBalance) == true){
				nonMonetoryBalances = createAndGetNonMonetaryBalances(addOnSubscription.getId(), subscriberNonMonitoryBalance);
			}

			if(nonNull(subscriberNonMonitoryBalance) == true){
				rncNonMonetaryBalances = createAndGetRnCNonMonetaryBalances(addOnSubscription.getId(), subscriberRnCNonMonetaryBalance);
			}

			addOnSubscription.setSubscriberIdentity(newSubscriberIdentity);

			SubscriptionDetail subscriptionDetail  = new SubscriptionDetail(new Subscription(addOnSubscription), usages);
			subscriptionDetail.setNonMonetoryBalances(nonMonetoryBalances);
			subscriptionDetail.setRnCNonMonetoryBalances(rncNonMonetaryBalances);

			addOnSubscriptionDetails.add(subscriptionDetail);
		}

		return addOnSubscriptionDetails;
	}


	private List<NonMonetoryBalance> createAndGetNonMonetaryBalances(String packageOrsubscriptionId, SubscriberNonMonitoryBalance subscriberNonMonitoryBalance){

		SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance = subscriberNonMonitoryBalance.getPackageBalance(packageOrsubscriptionId);

		if(isNull(subscriptionNonMonitoryBalance) == true)
			return null;

		Map<String, QuotaProfileBalance> quotaProfileWiseBalance = subscriptionNonMonitoryBalance.getAllQuotaProfileBalance();

		if(Maps.isNullOrEmpty(quotaProfileWiseBalance) == true)
			return  null;

		List<NonMonetoryBalance> nonMonetoryBalances = new ArrayList<>();

		quotaProfileWiseBalance.forEach((quotaProfileId, balance) -> {

			if(nonNull(balance.getHsqBalance())) {
				nonMonetoryBalances.addAll(balance.getHsqBalance());
			}

			if(nonNull(balance.getFupLevel1Balance())) {
				nonMonetoryBalances.addAll(balance.getFupLevel1Balance());
			}

			if(nonNull(balance.getFupLevel2Balance())) {
				nonMonetoryBalances.addAll(balance.getFupLevel2Balance());
			}
		});

		return nonMonetoryBalances;
	}

	private List<RnCNonMonetaryBalance> createAndGetRnCNonMonetaryBalances(String packageOrsubscriptionId, SubscriberRnCNonMonetaryBalance subscriberRnCNonMonetaryBalance){

		SubscriptionRnCNonMonetaryBalance subscriptionRnCNonMonetaryBalance = subscriberRnCNonMonetaryBalance.getPackageBalance(packageOrsubscriptionId);

		if(isNull(subscriptionRnCNonMonetaryBalance) == true)
			return null;

		Map<String, RnCNonMonetaryBalance> ratecardWiseBalance = subscriptionRnCNonMonetaryBalance.getAllRateCardBalance();

		if(Maps.isNullOrEmpty(ratecardWiseBalance) == true)
			return null;

		List<RnCNonMonetaryBalance> rncNonMonetaryBalances = new ArrayList<>();

		ratecardWiseBalance.forEach((quotaProfileId, balance) -> rncNonMonetaryBalances.add(balance));

		return rncNonMonetaryBalances;

	}

	private List<NonMonetoryBalance> createAndGetBasePackageNonMonetaryBalances(ProductOffer baseProductOffer, SubscriberNonMonitoryBalance subscriberNonMonitoryBalance){

		BasePackage basePkg = (BasePackage) baseProductOffer.getDataServicePkgData();

		if(isNull(basePkg) == true)
			return null;

		return createAndGetNonMonetaryBalances(basePkg.getId(), subscriberNonMonitoryBalance);

	}

	private List<RnCNonMonetaryBalance> createAndGetBasePackageRnCNonMonetaryBalances(ProductOffer baseProductOffer, SubscriberRnCNonMonetaryBalance subscriberRnCNonMonetaryBalance){

		List<ProductOfferServicePkgRel> productOfferServicePkgRelList = baseProductOffer.getProductOfferServicePkgRelDataList();

		if(Collectionz.isNullOrEmpty(productOfferServicePkgRelList) == true)
			return null;

		List<RnCNonMonetaryBalance> rncNonMonetaryBalances = new ArrayList<>();
		SubscriptionRnCNonMonetaryBalance subscriptionRnCNonMonetaryBalance = null;
		Map<String, RnCNonMonetaryBalance> ratecardWiseBalance = null;

		for (ProductOfferServicePkgRel productOfferServicePkgRel: productOfferServicePkgRelList) {

			subscriptionRnCNonMonetaryBalance = subscriberRnCNonMonetaryBalance.getPackageBalance(productOfferServicePkgRel.getRncPackageId());

			if(isNull(subscriptionRnCNonMonetaryBalance) == true)
				continue;

			ratecardWiseBalance = subscriptionRnCNonMonetaryBalance.getAllRateCardBalance();

			if(Maps.isNullOrEmpty(ratecardWiseBalance) == true)
				continue;

			ratecardWiseBalance.forEach((quotaProfileId, balance) -> rncNonMonetaryBalances.add(balance));
		}

		return rncNonMonetaryBalances;
	}

	private List<MonetaryBalance> createAndGetMonetaryBalances(SubscriberMonetaryBalance subscriberMonetaryBalance){

		if(isNull(subscriberMonetaryBalance) == true)
			return null;

		Collection<MonetaryBalance> balances = subscriberMonetaryBalance.getAllBalance();

		if(Collectionz.isNullOrEmpty(balances) == true)
			return null;

		List<MonetaryBalance> monetaryBalances = new ArrayList<>();

		balances.forEach(monetaryBalances::add);

		return monetaryBalances;
	}

	public String getStrippedSubscriberIdentity(String subscriberID) {
		return stripPrefixes(subscriberID);
	}

	@Override
	public void importSubscriber(SubscriberInfo subscriberInfo) throws OperationFailedException {

		String subscriberIdentity = subscriberInfo.getSprInfo().getSubscriberIdentity();

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		((SPRInfoImpl) subscriberInfo.getSprInfo()).setSubscriberIdentity(subscriberIdentity);

		repository.importSubscriber(subscriberInfo);

		if (identityMapper == null) {
			return;
		}

		if (repository.getAlternateIdField() == null) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not configured for subscriber repository: " + repository.getName());
			}
			return;
		}

		String alternateIdValue = repository.getAlternateIdField().getStringValue(subscriberInfo.getSprInfo());
		if (Strings.isNullOrBlank(alternateIdValue)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping process of Alternate Id mapping. Reason: Alternate Id not found from profile");
			}
			return;
		}

		identityMapper.addMapping(subscriberIdentity, alternateIdValue);
	}

	public void importSubscriptions(String subscriberId, List<SubscriptionDetail> subscriptionDetails) throws OperationFailedException {

		subscriberId = stripPrefixes(subscriberId);
		SubscriberRepository repository = repositorySelector.select(subscriberId);

		SPRInfo subscriberProfile = repository.getProfile(subscriberId, NotDeleteMarkedSubscriberPredicate.getInstance());

		if (subscriberProfile == null) {
			throw new OperationFailedException("Subscriber profile is not exist with ID: " + subscriberId, ResultCode.NOT_FOUND);
		}

		repository.importSubscriptions(subscriberProfile, subscriptionDetails);
	}

	@Override
	public void resetUsageByAlternateId(String alternateId, String packageId) throws OperationFailedException {
		resetUsageBySubscriberId(getSubscriberIdForAlternateId(alternateId), packageId);
	}

	@Override
	public void resetUsageBySubscriberId(String subscriberId, String productOfferId) throws OperationFailedException {

		subscriberId = stripPrefixes(subscriberId);
		SubscriberRepository repository = repositorySelector.select(subscriberId);
		repository.resetUsage(subscriberId, productOfferId);
	}

	@Override
	public void resetQuotaBySubscriberId(String subscriberId, String productOfferId) throws OperationFailedException {
		subscriberId = stripPrefixes(subscriberId);
		SubscriberRepository repository = repositorySelector.select(subscriberId);
		repository.resetQuota(subscriberId, productOfferId);
	}

	@Override
	public void resetBalanceBySubscriberId(SPRInfo sprInfo, String productOfferId, RnCPackage rnCPackage) throws OperationFailedException {
		String subscriberIdentity = stripPrefixes(sprInfo.getSubscriberIdentity());
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.resetBalance(sprInfo, productOfferId, rnCPackage);
	}

	@Override
	public void refundRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> refundNonMonetaryBalances) throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.refundRnCBalance(subscriberIdentity, refundNonMonetaryBalances);
	}

	@Override
	public int changeDataPackageBySubscriberId(ChangeBaseProductOfferParams params, StaffData staffData, String requestIpAddress) throws UnauthorizedActionException, OperationFailedException {

		params.setSubscriberId(stripPrefixes(params.getSubscriberId()));
		SubscriberRepository repository = repositorySelector.select(params.getSubscriberId());
		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);
		params.setAlternateId(getAlternateIdOfSprType(params.getSubscriberId()));
		return repository.changeBaseProductOffer(params, requestIpAddress);
	}

	@Override
	public int changeIMSPackageBySubscriberId(String subscriberIdentity, String newPackageName, String parameter1, String parameter2, String parameter3, StaffData staffData) throws UnauthorizedActionException, OperationFailedException {

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);

		return repository.changeIMSPackage(subscriberIdentity,newPackageName, parameter1, parameter2, parameter3);

	}

	@Override
	public void reserveBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		repository.reserveBalance(subscriberIdentity, serviceWiseNonMonetaryBalances);
	}

	@Override
	public void reserveRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		repository.reserveRnCBalance(subscriberIdentity, serviceWiseNonMonetaryBalances);
	}

	@Override
	public void reportBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.reportBalance(subscriberIdentity, serviceWiseNonMonetaryBalances);
	}

	@Override
	public void reportRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.reportRnCBalance(subscriberIdentity, serviceWiseNonMonetaryBalances);
	}

	@Override
	public void resetBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.resetBalance(subscriberIdentity, serviceWiseNonMonetaryBalances);
	}

	@Override
	public void directDebitBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.directDebitBalance(subscriberIdentity, serviceWiseNonMonetaryBalances);
	}

	@Override
	public void reportAndReserveBalance(String subscriberIdentity, List<NonMonetoryBalance> serviceWiseNonMonetaryBalances)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.reportAndReserveBalance(subscriberIdentity, serviceWiseNonMonetaryBalances);
	}

	@Override
	public void reportAndReserveRnCBalance(String subscriberIdentity, List<RnCNonMonetaryBalance> serviceWiseNonMonetaryBalances)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.reportAndReserveRnCBalance(subscriberIdentity, serviceWiseNonMonetaryBalances);
	}

	@Override
	public void updateNextBillingCycleBalance(Set<Entry<String, NonMonetoryBalance>> nonMonetoryBalances, Integer newBillDay) throws OperationFailedException {
		String subscriberIdentity = stripPrefixes(nonMonetoryBalances.iterator().next().getValue().getSubscriberIdentity());
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.updateNextBillingCycleBalance(nonMonetoryBalances, newBillDay);
	}


	@Override
	public SubscriberMonetaryBalance getMonetaryBalance(String subscriberIdentity, Predicate<MonetaryBalance> predicate)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		return repository.getMonetaryBalance(subscriberIdentity, predicate);
	}

	@Override
	public void addMonetaryBalance(String subscriberIdentity, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper
			, String remark, String requestIPAddress)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.addMonetaryBalance(subscriberIdentity, subscriberMonetaryBalanceWrapper, remark,requestIPAddress);
	}

	@Override
	public void addMonetaryBalance(String subscriberIdentity, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper
			, String remark, String requestIPAddress,EnumMap<SPRFields, String> updatedProfile, String monetaryRechargePlanName)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.addMonetaryBalance(subscriberIdentity, subscriberMonetaryBalanceWrapper, remark,requestIPAddress,updatedProfile, monetaryRechargePlanName);
	}

	@Override
	public void updateMonetaryBalance(String subscriberIdentity, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper, String remark, String requestIPAddress)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.updateMonetaryBalance(subscriberIdentity, subscriberMonetaryBalanceWrapper, remark,requestIPAddress);
	}

	@Override
	public void rechargeMonetaryBalance(MonetaryRechargeData monetaryRechargeData,EnumMap<SPRFields, String> updatedProfile,String subscriberId, String requestIpAddress)
			throws OperationFailedException {
		String subscriberIdentity = stripPrefixes(monetaryRechargeData.getSubscriberId());
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.rechargeMonetaryBalance(monetaryRechargeData,updatedProfile,subscriberId, requestIpAddress);

	}

	@Override
	public void updateCreditLimit(String subscriberIdentity, SubscriberMonetaryBalanceWrapper subscriberMonetaryBalanceWrapper
			, String remark, String requestIPAddress)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.updateCreditLimit(subscriberIdentity, subscriberMonetaryBalanceWrapper, remark, requestIPAddress);
	}

	@Override
	public MonetaryBalanceOperation getMonetaryBalanceOp() {
		return this.monetaryBalanceOperation;
	}

	@Override
	public Subscription subscribeQuotaTopUpByName(String subscriberIdentity, String parentId, String topUpName, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority,double subscriptionPrice ,String monetaryBalanceId, String param1, String param2, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository subscriberRepository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, subscriberRepository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.SUBSCRIBE_ADDON);
		return subscriberRepository.subscribeQuotaTopUpByName(subscriberIdentity, parentId, topUpName, subscriptionStatusValue, startTime, endTime, priority,subscriptionPrice ,monetaryBalanceId , param1, param2);
	}


	@Override
	public Subscription subscribeTopUpByTopUpIdByAlternateId(String alternateId, String parentId, String topUpId, int subscriptionStatusValue, Long startTime, Long endTime,
															 Integer priority, double subscriptionPrice,String monetaryBalanceId,String parameter1, String parameter2, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch subscriptions. Reason: subscriber identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.SUBSCRIBE_TOPUP);

		return repository.subscribeQuotaTopUpById(subscriberIdentity, parentId, topUpId, subscriptionStatusValue, startTime, endTime, priority,subscriptionPrice,monetaryBalanceId, parameter1, parameter2);
	}

	@Override
	public Subscription getQuotaTopUpSubscriptionByAlternateId(String alternateIdentity, String topUpSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException{
		if (Strings.isNullOrBlank(topUpSubscriptionId)) {
			throw new OperationFailedException("Unable to fetch topup subscriptions." +
					" Reason: TopUp subscription id not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateIdentity);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch topup subscriptions. Reason: subscriber identity not found for alternate Id: " + alternateIdentity
					, ResultCode.NOT_FOUND);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);
		return repository.getTopUpSubscriptionsBySubscriptionId(topUpSubscriptionId);
	}

	@Override
	public Subscription getQuotaTopUpSubscriptionBySubscriberId(String subscriberIdentity, String topUpSubscriptionId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {

		if (Strings.isNullOrBlank(topUpSubscriptionId)) {
			throw new OperationFailedException("Unable to fetch topup subscriptions. Reason: TopUp subscription id not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);
		return repository.getTopUpSubscriptionsBySubscriptionId(topUpSubscriptionId);
	}

	@Override
	public Subscription updateQuotaTopUpSubscriptionByAlternateId(String alternateId, String topUpSubscriptionId, int subscriptionStatusValue, Long startTime, Long endTime,
																  Integer priority, String rejectReason, String parameter1, String parameter2, StaffData staffData) throws UnauthorizedActionException, OperationFailedException {
		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch subscriptions. Reason: subscriber identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UNSUBSCRIBE_TOPUP);

		return repository.updateQuotaTopUpSubscription(subscriberIdentity, topUpSubscriptionId, subscriptionStatusValue, startTime, endTime, priority, rejectReason);
	}

	@Override
	public Subscription updateQuotaTopUpSubscription(String subscriberIdentity, String subscriptionId, Integer subscriptionStatusValue, Long startTime, Long endTime, Integer priority, String rejectReason,
													 String param1, String param2, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to update subscription. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UNSUBSCRIBE_TOPUP);
		return repository.updateQuotaTopUpSubscription(subscriberIdentity, subscriptionId, subscriptionStatusValue, startTime, endTime, priority, rejectReason);
	}

	@Override
	public LinkedHashMap<String, Subscription> getQuotaTopUpSubscriptionsbyAlternateId(String alternateId, StaffData staffData) throws OperationFailedException, UnauthorizedActionException {

		if (identityMapper == null) {
			throw new OperationFailedException(DATASOURCE_NOT_AVAILABLE, ResultCode.SERVICE_UNAVAILABLE);
		}

		String subscriberIdentity = identityMapper.getSubscriberId(alternateId);

		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch subscriptions. Reason: subscriber identity not found for alternate Id: " + alternateId
					, ResultCode.NOT_FOUND);
		}

		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);

		return repository.getTopUpSubscriptions(subscriberIdentity);
	}

	@Override
	public LinkedHashMap<String, Subscription> getTopUpSubscriptions(String subscriberIdentity, StaffData staffData) throws OperationFailedException,
			UnauthorizedActionException {
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch active subscriptions. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		if (staffData != null) {
			List<String> groupNames = repository.getGroupIds();
			if (staffData.isAccessibleAction(groupNames, ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION) == false) {
				throw new com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException(ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);
			}
		}

		return repository.getTopUpSubscriptions(subscriberIdentity);
	}

	@Override
	public LinkedHashMap<String, Subscription> getBodSubscriptions(String subscriberIdentity, StaffData staffData) throws OperationFailedException,
			UnauthorizedActionException {
		if (Strings.isNullOrBlank(subscriberIdentity)) {
			throw new OperationFailedException("Unable to fetch active bod subscriptions. Reason: Subscriber Identity not found"
					, ResultCode.INPUT_PARAMETER_MISSING);
		}

		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		if (staffData != null) {
			List<String> groupNames = repository.getGroupIds();
			if (staffData.isAccessibleAction(groupNames, ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION) == false) {
				throw new com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException(ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);
			}
		}

		return repository.getBodSubscriptions(subscriberIdentity);
	}

	@Override
	public Subscription updateFnFGroup(SPRInfo sprInfo, Subscription subscription, StaffData staffData, String requestIpAddress) throws OperationFailedException, UnauthorizedActionException {
		String subscriberIdentity = stripPrefixes(sprInfo.getSubscriberIdentity());
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		if (nonNull(staffData)) {
			List<String> groupNames = repository.getGroupIds();
			if (staffData.isAccessibleAction(groupNames, ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION) == false) {
				throw new com.elitecore.corenetvertex.spr.exceptions.UnauthorizedActionException(ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIPTION);
			}
		}

		return repository.updateFnFGroup(sprInfo, subscription, requestIpAddress);
	}

	@Override
	public void processReset(SPRInfo sprInfo, String requestIpAddress) throws OperationFailedException {
		String subscriberIdentity = stripPrefixes(sprInfo.getSubscriberIdentity());
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.processReset(sprInfo, requestIpAddress);
	}

	@Override
	public void addExternalAlternateId(String subscriberIdentity, String alternateIdentity,StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);

		String subscriberId = identityMapper.getSubscriberId(alternateIdentity, status -> true);
		if (StringUtils.isBlank(subscriberId) == false) {
			if (subscriberId.equals(subscriberIdentity)) {
				throw new OperationFailedException("Alternate Id already exists"
						, ResultCode.ALREADY_EXIST);
			} else {
				throw new OperationFailedException("Unable to add alternate identity for Subscriber ID: " + subscriberIdentity + ". Reason: Alternate Id: " + alternateIdentity + " already Exists with subscriber: " + subscriberId
						, ResultCode.INVALID_INPUT_PARAMETER);
			}
		}

		identityMapper.addExternalAlternateIdMapping(subscriberIdentity, alternateIdentity);
	}

	@Override
	public int removeExternalAlternateId(String subscriberIdentity, String alternateIdentity,StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);
		String subscriberId = identityMapper.getSubscriberId(alternateIdentity, status -> true);
		if (StringUtils.isEmpty(subscriberId)) {
			throw new OperationFailedException("Alternate Id doesn't Exist"
					, ResultCode.NOT_FOUND);

		}
		if (subscriberId.equals(subscriberIdentity) == false) {
			throw new OperationFailedException("Unable remove alternate identity for Subscriber Id: " + subscriberIdentity + ". Reason: Alternate Id: " + alternateIdentity + " exists with different subscriber: " + subscriberId
					, ResultCode.INVALID_INPUT_PARAMETER);

		}

		SubscriberAlternateIds alternateIds = identityMapper.getAlternateIds(subscriberIdentity);
		String currentStatus = alternateIds.byAlternateId(alternateIdentity).getStatus();

		return identityMapper.removeAlternateIdMappingByType(subscriberIdentity, alternateIdentity, AlternateIdType.EXTERNAL, currentStatus);
	}

	@Override
	public int updateExternalAlternateId(String subscriberIdentity, String oldAlternateIdentity, String updatedAlternateIdentity,StaffData staffData) throws OperationFailedException, UnauthorizedActionException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);
		String subscriberId = identityMapper.getSubscriberId(oldAlternateIdentity, status -> true);
		if (StringUtils.isEmpty(subscriberId)) {
			throw new OperationFailedException("Alternate Id doesn't Exist", ResultCode.NOT_FOUND);

		}
		if (subscriberId.equals(subscriberIdentity) == false) {
			throw new OperationFailedException("Unable to update alternate identity for Subscriber Id: " + subscriberIdentity + ". Reason: Alternate Id: " + oldAlternateIdentity + " exists with different subscriber: " + subscriberId
					, ResultCode.INVALID_INPUT_PARAMETER);
		}
		String subscriberIdWithNewAlternateId = identityMapper.getSubscriberId(updatedAlternateIdentity, status -> true);
		if (StringUtils.isNotEmpty(subscriberIdWithNewAlternateId) && subscriberIdWithNewAlternateId.equals(subscriberIdentity) == false) {
			throw new OperationFailedException("Unable to update alternate identity for Subscriber Id: " + subscriberIdentity + ". Reason: Alternate Id: " + oldAlternateIdentity + " exists with different subscriber: " + subscriberIdWithNewAlternateId
					, ResultCode.INVALID_INPUT_PARAMETER);

		}
		if (StringUtils.isNotEmpty(subscriberIdWithNewAlternateId)) {
			throw new OperationFailedException("Unable to update alternate identity for Subscriber Id: " + subscriberIdentity + ". Reason: Alternate Id: " + updatedAlternateIdentity + " already exist"
					, ResultCode.ALREADY_EXIST);

		}
		SubscriberAlternateIds alternateIds = identityMapper.getAlternateIds(subscriberIdentity);
		String currentStatus = alternateIds.byAlternateId(oldAlternateIdentity).getStatus();

		return identityMapper.updateExternalAlternateIdentity(subscriberIdentity, oldAlternateIdentity, updatedAlternateIdentity, currentStatus);
	}

	@Override
	public SubscriberAlternateIds getExternalAlternateIds(String subscriberIdentity,StaffData staffData) throws OperationFailedException,UnauthorizedActionException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.VIEW_SUBSCRIBER);
		return identityMapper.getAlternateIds(subscriberIdentity);
	}

	@Override
	public int changeAlternateIdentityStatus(String subscriberIdentity, String alternateIdentity, String updatedStatus,StaffData staffData) throws OperationFailedException,UnauthorizedActionException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);

		checkForUserAuthorization(staffData, repository.getGroupIds(), ACLModules.SUBSCRIBER, ACLAction.UPDATE_SUBSCRIBER);
		String subscriberId = identityMapper.getSubscriberId(alternateIdentity, alternateIdStatus -> true);
		if (StringUtils.isEmpty(subscriberId)) {
			throw new OperationFailedException("Alternate Id doesn't Exist", ResultCode.NOT_FOUND);

		}
		if (subscriberId.equals(subscriberIdentity) == false) {
			throw new OperationFailedException("Unable update alternate identity for Subscriber Id: " + subscriberIdentity + ". Reason: Alternate Id: " + alternateIdentity + " exists with different subscriber: " + subscriberId
					, ResultCode.INVALID_INPUT_PARAMETER);
		}

		SubscriberAlternateIds alternateIds = identityMapper.getAlternateIds(subscriberIdentity);
		String currentStatus = alternateIds.byAlternateId(alternateIdentity).getStatus();
		if(updatedStatus.equalsIgnoreCase(currentStatus)){
			throw new OperationFailedException("Unable change alternate identity: "+alternateIdentity+" status.Reason: alternate Id already in "+updatedStatus
					, ResultCode.INVALID_INPUT_PARAMETER);
		}

		return identityMapper.changeExternalAlternateIdentityStatus(subscriberIdentity, alternateIdentity, currentStatus, updatedStatus);

    }

	@Override
	public void changeBillDay(String subscriberIdentity, Timestamp nextBillDate, Timestamp billChangeDate, String requestIpAddress)
			throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		repository.changeBillDay(subscriberIdentity, nextBillDate,billChangeDate, requestIpAddress);
	}

	@Override
	public void checkForBillDateChange(SPRInfo sprInfo) throws OperationFailedException, UnauthorizedActionException {

		Timestamp nextBillDate = sprInfo.getNextBillDate();

		if(isNull(nextBillDate)) {
			return;
		}

		if(isNull(sprInfo.getBillChangeDate())) {
			return;
		}

		if(System.currentTimeMillis() <= sprInfo.getBillChangeDate().getTime()) {
			return;
		}

		Calendar calenderForNewBillDate = Calendar.getInstance();
		calenderForNewBillDate.setTimeInMillis(nextBillDate.getTime());
		if(calenderForNewBillDate.get(Calendar.DATE) == sprInfo.getBillingDate()) {
			return;
		}

		EnumMap<SPRFields, String> sprFieldMap = new EnumMap<>(SPRFields.class);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(sprInfo.getNextBillDate().getTime());
		sprFieldMap.put(SPRFields.BILLING_DATE, String.valueOf(calendar.get(Calendar.DATE)));
		sprFieldMap.put(SPRFields.NEXT_BILL_DATE, null);
		sprFieldMap.put(SPRFields.BILL_CHANGE_DATE, null);
		updateProfile(sprInfo.getSubscriberIdentity(), sprFieldMap, null, null);
		Set<Entry<String, NonMonetoryBalance>> nonMonetoryBalances = getRGNonMonitoryBalance(sprInfo).getBalances();
		if(Collectionz.isNullOrEmpty(nonMonetoryBalances) == false) {
			updateNextBillingCycleBalance(nonMonetoryBalances, calendar.get(Calendar.DATE));
		}

	}

	@Override
	public SubscriptionNonMonitoryBalance addDataRnCBalance(String subscriberIdentity, Subscription subscription, ProductOffer productOffer) throws OperationFailedException {
		subscriberIdentity = stripPrefixes(subscriberIdentity);
		SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
		return repository.addDataRnCBalance(subscriberIdentity, subscription,productOffer);
	}
}