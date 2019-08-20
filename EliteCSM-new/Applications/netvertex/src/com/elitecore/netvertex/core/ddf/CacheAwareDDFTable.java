package com.elitecore.netvertex.core.ddf;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.spr.AlternateIdentityMapper;
import com.elitecore.corenetvertex.spr.SPRProvider;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.ddf.DDFTableImpl;
import com.elitecore.corenetvertex.spr.ddf.RepositorySelector;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.Cache;
import com.elitecore.corenetvertex.util.CacheLoader;
import com.elitecore.corenetvertex.util.PartitioningCache;
import com.elitecore.netvertex.cli.CacheStatisticsDetailProvider;
import com.elitecore.netvertex.cli.ClearCacheDetailProvider;
import com.elitecore.netvertex.cli.ClearCacheStatisticsDetailProvider;
import com.elitecore.netvertex.cli.ClearSPRCacheDetailProvider;
import com.elitecore.netvertex.cli.ClearSPRCacheStatisticsDetailProvider;
import com.elitecore.netvertex.cli.SPRCacheStatisticsDetailProvider;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.driver.cdr.ExternalAlternateIdEDRListenerImpl;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.core.util.TaskSchedulerAdapter;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import static com.elitecore.commons.logging.LogManager.getLogger;


/**
 * NetVertex specific implementation of DDF.
 * - Profile cache operation is done here
 *
 * @author Chetan.Sankhala
 */
public class CacheAwareDDFTable extends DDFTableImpl {

	private static final String MODULE = "CACHE-AWARE-DDF";
	private static RepositorySelector repositorySelector;
	private final NetVertexServerContext serverContext;
	private Cache<String, SPRInfo> cache;
	private Cache<String, String> secondaryCache;
	private static SPRProvider sprProvider;
	private static boolean isInitialized = false;

	private static CacheAwareDDFTable cacheAwareDDFTable;

	public CacheAwareDDFTable(DDFConfiguration ddfConfiguration,
							  SPRProvider sprProvider,
							  NetVertexServerContext serverContext,
							  AlternateIdentityMapper mapper,
							  Cache<String, String> secondaryCache, RepositorySelector repositorySelector) {
		super(ddfConfiguration, serverContext.getPolicyRepository(), sprProvider, mapper, repositorySelector);
		this.serverContext = serverContext;
		this.cache = new PartitioningCache.CacheBuilder<String, SPRInfo>(new TaskSchedulerAdapter(serverContext.getTaskScheduler()))
				.withCacheLoader(new ProfileCacheLoader()).build();
		this.secondaryCache = secondaryCache;
		this.repositorySelector = repositorySelector;
	}

	public static void init(DDFConfiguration ddfConfiguration,
							NetVertexServerContext serverContext, TransactionFactory transactionFactory) {
		if(isInitialized) {
			return;
		}
		sprProvider = new SPRProviderImpl(serverContext);
		AlternateIdentityMapper mapper = null;
		if (transactionFactory != null) {
			mapper = new AlternateIdentityMapper(transactionFactory, new ExternalAlternateIdEDRListenerImpl());
		}
		RepositorySelector repositorySelector = RepositorySelector.create(ddfConfiguration,sprProvider);
		cacheAwareDDFTable = new CacheAwareDDFTable(ddfConfiguration, sprProvider, serverContext, mapper, new PartitioningCache.CacheBuilder<String, String>(new TaskSchedulerAdapter(serverContext.getTaskScheduler())).build(),repositorySelector);
		cacheAwareDDFTable.initialize();
		isInitialized = true;
	}

	public static CacheAwareDDFTable getInstance() {
		return cacheAwareDDFTable;
	}

	void initialize() {
		registerStatistics();
	}

	private void registerStatistics() {

		SPRCacheStatisticsDetailProvider sprCacheStatisticsDetailProvider = new SPRCacheStatisticsDetailProvider(cache.statistics());
		try {
			CacheStatisticsDetailProvider.getInstance().registerDetailProvider(sprCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering SPR Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}

		ClearSPRCacheStatisticsDetailProvider clearSPRCacheStatisticsDetailProvider = new ClearSPRCacheStatisticsDetailProvider(cache.statistics());
		try {
			ClearCacheStatisticsDetailProvider.getInstance().registerDetailProvider(clearSPRCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear SPR Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}

		ClearSPRCacheDetailProvider clearSPRCacheDetailProvider = new ClearSPRCacheDetailProvider(cache);
		try {
			ClearCacheDetailProvider.getInstance().registerDetailProvider(clearSPRCacheDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear SPR Cache detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	private boolean isSPRCacheEnable() {
		return serverContext.getServerConfiguration().getMiscellaneousParameterConfiguration().isSPRCacheEnabled();
	}

	@Override
	public SPRInfo getProfile(String subscriberIdentity) throws OperationFailedException {


		if (isSPRCacheEnable()) {
			try {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Fetching subscriber profile from cache. Reason: profile cache is enable");
				}
				SPRInfo sprInfo = cache.get(subscriberIdentity);

				if (sprInfo.isUnknownUser() || SPRInfo.UNAVAILABLE.equalsIgnoreCase(sprInfo.getStatus())) {
					cache.remove(subscriberIdentity);
				}

				return sprInfo;
			} catch (Exception e) {
				throw new OperationFailedException(e.getMessage(), e);
			}
		} else {
			return super.getProfile(subscriberIdentity);
		}

	}

	public String removeSecondaryCache(String alternateId){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Removing alternate identity(" + alternateId +") from secondary cache.");
		}
		return secondaryCache.remove(alternateId);
	}

	public SPRInfo getProfile(PCRFRequest pcrfRequest) throws OperationFailedException {

		String alternateId = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val);
		if(alternateId == null
				&& pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val) == null){
			throw new OperationFailedException("Subscriber identity not found in pcrf request");
		}

        String subscriberIdentity = null;
        if(alternateId != null){
            if(PCRFPacketUtil.isReAuthRequest(pcrfRequest)){
                removeSecondaryCache(alternateId);
            }
            subscriberIdentity = getSubscriberIdForAlternateId(alternateId);

            if(subscriberIdentity == null) {
				return createAnonymousSubscriber(alternateId);
            }
        }else if(pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val) != null){
            subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
        }

		SPRInfo sprInfo;

		if (isSPRCacheEnable()) {
			try {

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Fetching subscriber profile from cache. Reason: profile cache is enabled");
				}

				if ((pcrfRequest.isSessionFound() == false
						&& PCRFPacketUtil.isTerminateRequest(pcrfRequest) == false)
						|| PCRFPacketUtil.isInitialRequest(pcrfRequest)
						|| PCRFPacketUtil.isReAuthRequest(pcrfRequest)
						|| PCRFPacketUtil.isConsecutiveRequest(pcrfRequest) == false) {

					sprInfo = cache.refresh(subscriberIdentity);

					pcrfRequest.setSPRFetchTime(sprInfo.getSprLoadTime());
					pcrfRequest.setSPRReadTime(sprInfo.getSprReadTime());
				} else {
					sprInfo = cache.getWithoutLoad(subscriberIdentity);

					if(sprInfo == null) {
						sprInfo = cache.refresh(subscriberIdentity);
						pcrfRequest.setSPRFetchTime(sprInfo.getSprLoadTime());
						pcrfRequest.setSPRReadTime(sprInfo.getSprReadTime());
					}

					/* SESSION_STOP request, remove cache */
					if (PCRFPacketUtil.isTerminateRequest(pcrfRequest)) {
						cache.remove(subscriberIdentity);
					}
				}

				if (sprInfo.isUnknownUser() || SPRInfo.UNAVAILABLE.equalsIgnoreCase(sprInfo.getStatus())) {
					cache.remove(subscriberIdentity);
				}
			} catch (Exception e) {
				throw new OperationFailedException(e.getMessage(), e);
			}

		} else {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Fetching subscriber profile from datasource. Reason: profile cache is disabled");
			}
			sprInfo = super.getProfile(subscriberIdentity);
			pcrfRequest.setSPRFetchTime(sprInfo.getSprLoadTime());
			pcrfRequest.setSPRReadTime(sprInfo.getSprReadTime());
		}

		return sprInfo;
	}

	private SPRInfo createAnonymousSubscriber(String subscriberIdentity) {
          SubscriberRepository repository = repositorySelector.select(subscriberIdentity);
          return repository.createAnonymousProfile(subscriberIdentity);
	}

	@Override
	public String getSubscriberIdForAlternateId(String alternateId) throws OperationFailedException {

		if (isSPRCacheEnable() == false) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Fetching subscriber id from DB. Reason: Alternate identity cache is disabled");
			}
			return super.getSubscriberIdForAlternateId(alternateId);
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Fetching subscriber id from cache. Reason: Alternate identity cache is enabled");
		}
		String subscriberId = secondaryCache.getWithoutLoad(alternateId);

		if (Strings.isNullOrBlank(subscriberId)) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Fetching subscriber id from DB. Reason: Subscriber id is not found from cache");
			}
			subscriberId = super.getSubscriberIdForAlternateId(alternateId);

			if (Strings.isNullOrBlank(subscriberId) == false) {
				secondaryCache.put(alternateId, subscriberId);
			}
		}
		return subscriberId;
	}

	public SPRInfo removeCache(String subscriberIdentity) {
		if (isSPRCacheEnable() == false) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Unable to remove cache for subscriber identity(" + subscriberIdentity + "). Reason: Profile cache is disabled");
			}
			return null;
		}
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Removing subscriber identity(" + subscriberIdentity + ") from cache");
		}
		return cache.remove(subscriberIdentity);
	}

	/**
	 * this method gives subscriber profile
	 *
	 * DOES NOT GIVE UNKNOWN USER PROFILE
	 * @throws OperationFailedException
	 */
	@Override
	public SPRInfo searchSubscriber(String subscriberIdentity) throws OperationFailedException {
		SPRInfo profile = getProfile(subscriberIdentity);

		if (profile == null || profile.isUnknownUser() || SPRInfo.UNAVAILABLE.equalsIgnoreCase(profile.getStatus())) {
			return null;
		}

		return profile;
	}

	@VisibleForTesting //providing only because primary cache can't be passed as field so adding it only for testing
    void setPrimaryCache(Cache<String, SPRInfo> cache) {
	    this.cache = cache;
    }
    @VisibleForTesting //providing only because primary cache can't be test  in testing so exposing it only for testing
    Cache<String, SPRInfo> getPrimaryCache(){
	    return this.cache;
    }

	private class ProfileCacheLoader implements CacheLoader<String, SPRInfo> {

		@Override
		public SPRInfo load(String subscriberIdentity) throws OperationFailedException {
			return CacheAwareDDFTable.super.getProfile(subscriberIdentity);
		}

		@Override
		public SPRInfo reload(String subscriberIdentity) throws OperationFailedException {
			return load(subscriberIdentity);
		}
	}

	public static void stop() {
		if(sprProvider != null){
			for (SubscriberRepository repository: sprProvider.getAllSubscriberRepository()) {
				repository.stop();
			}
		}
	}
}

