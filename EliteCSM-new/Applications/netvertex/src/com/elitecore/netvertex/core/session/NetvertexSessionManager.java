package com.elitecore.netvertex.core.session;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl.SQLDialectFactory;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.commons.Predicate;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.util.ActivePCCRuleParser;
import com.elitecore.corenetvertex.util.CacheLoader;
import com.elitecore.corenetvertex.util.InMemoryCompositeIndexCache;
import com.elitecore.corenetvertex.util.PartitioningCache.CacheBuilder;
import com.elitecore.corenetvertex.util.PrimaryCache;
import com.elitecore.corenetvertex.util.SessionUsageParser;
import com.elitecore.corenetvertex.util.TaskScheduler;
import com.elitecore.netvertex.cli.CacheStatisticsDetailProvider;
import com.elitecore.netvertex.cli.ClearCacheDetailProvider;
import com.elitecore.netvertex.cli.ClearCacheStatisticsDetailProvider;
import com.elitecore.netvertex.cli.ClearSessionCacheDetailProvider;
import com.elitecore.netvertex.cli.ClearSessionCacheStatisticsDetailProvider;
import com.elitecore.netvertex.cli.SessionCacheStatisticsDetailProvider;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.util.LocationSerializationUtil;
import com.elitecore.netvertex.core.util.TaskSchedulerAdapter;
import com.elitecore.netvertex.gateway.diameter.af.AFSessionRule;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Strings.isNullOrBlank;
import static com.elitecore.commons.logging.LogManager.getLogger;

public class NetvertexSessionManager {

	private static final String MODULE = "NET-SESS-MGR";

	public static final String CORE_SESS_TABLE_NAME = "TBLT_SESSION";
	public static final String SESSION_RULE_TABLE_NAME = "TBLT_SUB_SESSION";

	private static final String SESSION_CACHE_BY_SUBSCRIBER_ID = "session-cache-by-subscriber-id";
	private static final String SESSION_CACHE_BY_ID = "session-cache-by-id";
	private static final String SESSION_CACHE_BY_SESSION_IPV4 = "session-cache-by-session-IPv4";
	private static final String SESSION_CACHE_BY_SESSION_IPV6 = "session-cache-by-session-IPv6";

	private final NetVertexServerContext serverContext;
	private final PrimaryCache<String, SessionData> primaryCache;
	private InMemoryCompositeIndexCache<String,String,SessionData> subscriberSessionCache;
	private InMemoryCompositeIndexCache<String,String,SessionData> subscriberSessionIPv4SessionCache;
	private InMemoryCompositeIndexCache<String,String,SessionData> subscriberSessionIPv6SessionCache;
	private SessionLocator sessionLocator;
	private SessionOperation sessionOperation;
	private SessionCacheStatisticsProvider sessCacheStatisticsProvider;
	private SessionDao sessionDao;


	public NetvertexSessionManager(final NetVertexServerContext serverContext, SessionDao sessionDao) {
		this.serverContext = serverContext;
		this.sessionDao = sessionDao;
		final TaskSchedulerAdapter taskScheduler = new TaskSchedulerAdapter(serverContext.getTaskScheduler());
		this.primaryCache = new CacheBuilder<String,SessionData>(taskScheduler)
				.withCacheLoader(new SessionCacheLoader()).build();

		createSecondaryCache(taskScheduler, primaryCache);
		createSessionLocatorAndOperation();
	}

	private void createSessionLocatorAndOperation() {
		this.sessionOperation = createSessionOperation();
		this.sessionLocator = createSessionLocator();
	}


	private void createSecondaryCache(TaskScheduler taskScheduler, PrimaryCache<String, SessionData> primaryCache) {
		this.subscriberSessionCache = new InMemoryCompositeIndexCache.CacheBuilder<String,String,SessionData>(
				taskScheduler,
				primaryCache,
				new KeyFunction(PCRFKeyConstants.CS_CORESESSION_ID.val),
				new KeyFunction(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val))
				.withCacheLoader(new SubscriberSessionCacheLoader()).build();

		this.subscriberSessionIPv4SessionCache = new InMemoryCompositeIndexCache.CacheBuilder<String,String,SessionData>(
				taskScheduler,
				primaryCache,
				new KeyFunction(PCRFKeyConstants.CS_CORESESSION_ID.val),
				new KeyFunction(PCRFKeyConstants.CS_SESSION_IPV4.val))
				.withCacheLoader(new SubscriberSessionIPv4SessionCacheLoader()).build();

		this.subscriberSessionIPv6SessionCache = new InMemoryCompositeIndexCache.CacheBuilder<String,String,SessionData>(
				taskScheduler,
				primaryCache,
				new KeyFunction(PCRFKeyConstants.CS_CORESESSION_ID.val),
				new KeyFunction(PCRFKeyConstants.CS_SESSION_IPV6.val))
				.withCacheLoader(new SubscriberSessionIPv6SessionCacheLoader()).build();
	}



	public void init() throws InitializationFailedException{


		SessionCacheStatisticsDetailProvider sessionCacheStatisticsDetailProvider = new SessionCacheStatisticsDetailProvider(SESSION_CACHE_BY_ID, primaryCache.statistics());
		try {
			CacheStatisticsDetailProvider.getInstance().registerDetailProvider(sessionCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Session Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}

		SessionCacheStatisticsDetailProvider subscriberSessionCacheStatisticsDetailProvider = new SessionCacheStatisticsDetailProvider(SESSION_CACHE_BY_SUBSCRIBER_ID, subscriberSessionCache.statistics());
		try {
			CacheStatisticsDetailProvider.getInstance().registerDetailProvider(subscriberSessionCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Session Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}


		SessionCacheStatisticsDetailProvider subscriberSessionIPv4SessionCacheStatisticsDetailProvider = new SessionCacheStatisticsDetailProvider(SESSION_CACHE_BY_SESSION_IPV4, subscriberSessionIPv4SessionCache.statistics());
		try {
			CacheStatisticsDetailProvider.getInstance().registerDetailProvider(subscriberSessionIPv4SessionCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Session Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}


		SessionCacheStatisticsDetailProvider subscriberSessionIPv6SessionCacheStatisticsDetailProvider = new SessionCacheStatisticsDetailProvider(SESSION_CACHE_BY_SESSION_IPV6, subscriberSessionIPv6SessionCache.statistics());
		try {
			CacheStatisticsDetailProvider.getInstance().registerDetailProvider(subscriberSessionIPv6SessionCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Session Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}



		ClearSessionCacheStatisticsDetailProvider clearSessionCacheStatisticsDetailProvider = new ClearSessionCacheStatisticsDetailProvider(SESSION_CACHE_BY_ID, primaryCache.statistics());
		try {
			ClearCacheStatisticsDetailProvider.getInstance().registerDetailProvider(clearSessionCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear Session Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}


		ClearSessionCacheStatisticsDetailProvider clearSubscriberSessionCacheStatisticsDetailProvider = new ClearSessionCacheStatisticsDetailProvider(SESSION_CACHE_BY_SUBSCRIBER_ID, subscriberSessionCache.statistics());
		try {
			ClearCacheStatisticsDetailProvider.getInstance().registerDetailProvider(clearSubscriberSessionCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear Session Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}

		ClearSessionCacheStatisticsDetailProvider clearSubscriberSessionIPv4SessionCacheStatisticsDetailProvider = new ClearSessionCacheStatisticsDetailProvider(SESSION_CACHE_BY_SESSION_IPV4, subscriberSessionIPv4SessionCache.statistics());
		try {
			ClearCacheStatisticsDetailProvider.getInstance().registerDetailProvider(clearSubscriberSessionIPv4SessionCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear Session Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}

		ClearSessionCacheStatisticsDetailProvider clearSubscriberSessionIPv6SessionCacheStatisticsDetailProvider = new ClearSessionCacheStatisticsDetailProvider(SESSION_CACHE_BY_SESSION_IPV6, subscriberSessionIPv6SessionCache.statistics());
		try {
			ClearCacheStatisticsDetailProvider.getInstance().registerDetailProvider(clearSubscriberSessionIPv6SessionCacheStatisticsDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear Session Cache Statistics detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}


		ClearSessionCacheDetailProvider<String,SessionData> clearSessionCacheDetailProvider = new ClearSessionCacheDetailProvider<String,SessionData>(SESSION_CACHE_BY_ID, primaryCache);
		try {
			ClearCacheDetailProvider.getInstance().registerDetailProvider(clearSessionCacheDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear Session Cache detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}

		ClearSessionCacheDetailProvider<String,Iterator<SessionData>> clearSubscriberSessionCacheDetailProvider = new ClearSessionCacheDetailProvider<String,Iterator<SessionData>>(SESSION_CACHE_BY_SUBSCRIBER_ID, subscriberSessionCache);
		try {
			ClearCacheDetailProvider.getInstance().registerDetailProvider(clearSubscriberSessionCacheDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear Session Cache detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}

		ClearSessionCacheDetailProvider<String,Iterator<SessionData>> clearSubscriberSessionIPv4SessionCacheDetailProvider = new ClearSessionCacheDetailProvider<String,Iterator<SessionData>>(SESSION_CACHE_BY_SESSION_IPV4, subscriberSessionIPv4SessionCache);
		try {
			ClearCacheDetailProvider.getInstance().registerDetailProvider(clearSubscriberSessionIPv4SessionCacheDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear Session Cache detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}

		ClearSessionCacheDetailProvider<String,Iterator<SessionData>> clearSubscriberSessionIPv6SessionCacheDetailProvider = new ClearSessionCacheDetailProvider<String,Iterator<SessionData>>(SESSION_CACHE_BY_SESSION_IPV6, subscriberSessionIPv6SessionCache);
		try {
			ClearCacheDetailProvider.getInstance().registerDetailProvider(clearSubscriberSessionIPv6SessionCacheDetailProvider);
		} catch (RegistrationFailedException e) {
			getLogger().error(MODULE, "Error while registering Clear Session Cache detail provider. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}

		// used for NETVERTEX_PCRF_MIBImpl
		sessCacheStatisticsProvider = new SessionCacheStatisticsProvider(primaryCache.statistics());


		getLogger().info(MODULE, "Netvertex session manager initialization completed");
	}

	/**
	 * it will remove the session from the cache based on the coreSessionId
	 * @param coreSessionId
	 */
	public void removeCache(String coreSessionId){

		if (isSessionCacheEnabled() == false) {
			return;
		}

		SessionData sessionData = primaryCache.remove(coreSessionId);
		if(sessionData == null){
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "Session not found for the Core-Session-Id: " + coreSessionId +".");
			}
			return;
		}
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Session is removed from the cache for the Core-Session-Id : " + coreSessionId);
		}
	}

	private boolean isSessionCacheEnabled() {
		return serverContext.getServerConfiguration().getMiscellaneousParameterConfiguration().isSessionCacheEnabled();
	}

	protected @Nullable SQLDialectFactory createDialectFactory() {
		return null;
	}

	public void reloadSessionManagerConfiguration(){
		sessionDao.reloadSessionManagerConfiguration();
	}

	/**
	 *
	 * @return
	 */
	public SessionLocator getSessionLookup() {
		return sessionLocator;
	}

	/**
	 *
	 * @return
	 */

	public SessionOperation getSessionOperation() {
		return sessionOperation;
	}


	/**
	 * @return
	 *
	 */

	private  SessionLocator createSessionLocator(){

		getLogger().info(MODULE, "Creating session locator");
		SessionLocator sessionLocator = new SessionLocatorImpl();

		getLogger().info(MODULE, "Session locator creation completed");
		return sessionLocator;

	}


	private SessionOperation createSessionOperation(){

		getLogger().info(MODULE, "Creating session operation");
		SessionOperation sessionOperation = new SessionOperationImpl();

		getLogger().debug(MODULE, "Session operation creation completed");
		return sessionOperation;
	}


	private List<SessionData> getCoreSessionBySessionIPv6(String sessionIP) {
		if(sessionDao.isAlive() == false){
			getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
			return null;
		}

		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "fetch records in core session based on session IP");

		List<SessionData> sessionDatas = sessionDao.getCoreSessionBySessionIPv6(sessionIP);

		if(isNullOrEmpty(sessionDatas) == false) {
			if(sessionDatas.size() > 1) {
				SessionSortOrder.DESCENDING.sort(sessionDatas);
			}
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Total session found = " + sessionDatas.size() + " with session IP = " + sessionIP);

			refreshSession(sessionDatas, SessionSortOrder.DESCENDING);
		} else {
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "No record found with Session IP = " + sessionIP);
		}

		return sessionDatas;
	}

	private List<SessionData> getCoreSessionBySessionIPv4(String sessionIP) {
		if(sessionDao.isAlive() == false){
			getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
			return null;
		}

		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "fetch records in core session based on session IP");

		List<SessionData> sessionDatas = sessionDao.getCoreSessionBySessionIPv4(sessionIP);

		if(isNullOrEmpty(sessionDatas) == false) {
			if(sessionDatas.size() > 1) {
				SessionSortOrder.DESCENDING.sort(sessionDatas);
			}
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Total session found = " + sessionDatas.size() + " with session IP = " + sessionIP);

			refreshSession(sessionDatas, SessionSortOrder.DESCENDING);
		} else {
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "No record found with Session IP = " + sessionIP);
		}
		return sessionDatas;
	}

	private SessionData getCoreSessionByCoreSessionID(String coreSessionId) {
		if(sessionDao.isAlive() == false){
			getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
			return null;
		}

		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "fetch records in core session based on core session ID = " + coreSessionId);

		List<SessionData> sessionDatas = sessionDao.getCoreSessionByCoreSessionID(coreSessionId);

		if (isNullOrEmpty(sessionDatas)) {
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "No record found for given criteria");

			return null;
		} else {
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Total session found = "+ sessionDatas.size() + " with core session ID = " + coreSessionId);

			if(sessionDatas.size() > 1) {
				SessionSortOrder.DESCENDING.sort(sessionDatas);
			}
			return sessionDatas.get(0);
		}
	}

	private List<SessionData> getCoreSessionBySubscriberIdentity(String subscriberIdentity) {
		if(sessionDao.isAlive() == false){
			getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
			return null;
		}

		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "Fetch records in core session based on subscriber identity = " + subscriberIdentity);

		List<SessionData> sessionDatas = sessionDao.getCoreSessionBySubscriberIdentity(subscriberIdentity);

		if(isNullOrEmpty(sessionDatas) == false) {
			if(sessionDatas.size() > 1) {
				SessionSortOrder.DESCENDING.sort(sessionDatas);
			}
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Total session found = " + sessionDatas.size() + " with subscriber identity = " + subscriberIdentity);

			refreshSession(sessionDatas, SessionSortOrder.DESCENDING);
		} else {
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "No record found with subscriber identity = " + subscriberIdentity);
		}

		return sessionDatas;
	}

	/**
	 *	Refresh session in particular sorting order
	 *
	 *
	 *  IF session list in ASCENDING ORDER THEN
	 *  	refresh session in same order
	 *  ELSE
	 *  	//If session list in descending order and  we refresh cache in same order then it may happen than old session override new session.
	 *  	refresh session in reverse order
	 *
	 * @param sessionDataList
	 * @param sortOrder session sorting order
	 */
	private void refreshSession(List<SessionData> sessionDataList, SessionSortOrder sortOrder) {

		if (isSessionCacheEnabled() == false) {
			return;
		}

		if(isNullOrEmpty(sessionDataList)) {
			return;
		}

		if (SessionSortOrder.ASCENDING == sortOrder) {
			for(int sessionDataIndex = 0; sessionDataIndex < sessionDataList.size(); sessionDataIndex++) {
				primaryCache.put(sessionDataList.get(sessionDataIndex).getValue(PCRFKeyConstants.CS_CORESESSION_ID.val), sessionDataList.get(sessionDataIndex));
			}

		} else {
			for(int sessionDataIndex = sessionDataList.size() - 1; sessionDataIndex >= 0; sessionDataIndex--) {
				primaryCache.put(sessionDataList.get(sessionDataIndex).getValue(PCRFKeyConstants.CS_CORESESSION_ID.val), sessionDataList.get(sessionDataIndex));
			}
		}


	}

	public SessionCacheStatisticsProvider getSessionCacheStatisticsProvider() {
		return sessCacheStatisticsProvider;
	}

	private class SessionCacheLoader implements CacheLoader<String, SessionData> {

		@Override
		public SessionData load(String coreSessionId) throws Exception {
			return getCoreSessionByCoreSessionID(coreSessionId);
		}

		@Override
		public SessionData reload(String coreSessionId) throws Exception {
			return load(coreSessionId);
		}
	}

	private class SubscriberSessionCacheLoader implements CacheLoader<String, Collection<SessionData>> {

		@Override
		public Collection<SessionData> load(String subscriberIdentity) throws Exception {
			return getCoreSessionBySubscriberIdentity(subscriberIdentity);
		}

		@Override
		public Collection<SessionData> reload(String subscriberIdentity) throws Exception {
			return load(subscriberIdentity);
		}
	}

	private class SubscriberSessionIPv4SessionCacheLoader implements CacheLoader<String, Collection<SessionData>> {

		@Override
		public Collection<SessionData> load(String sessionIPv4) throws Exception {
			return getCoreSessionBySessionIPv4(sessionIPv4);
		}

		@Override
		public Collection<SessionData> reload(String sessionIPv4) throws Exception {
			return load(sessionIPv4);

		}
	}

	private class SubscriberSessionIPv6SessionCacheLoader  implements CacheLoader<String, Collection<SessionData>> {

		@Override
		public Collection<SessionData> load(String sessionIPv6) throws Exception {

			return getCoreSessionBySessionIPv6(sessionIPv6);
		}

		@Override
		public Collection<SessionData> reload(String sessionIPv6) throws Exception {
			return load(sessionIPv6);
		}
	}


	public void addToCache(SessionData sessionData){
		primaryCache.put(sessionData.getSessionId(), sessionData);
	}

	private class SessionLocatorImpl implements SessionLocator {
		@Override
		public List<SessionData> getCoreSessionList(Criteria criteria) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
				return null;
			}
			if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "fetch records in core session based on criteria provided");

			List<SessionData> sessionDatas = sessionDao.getCoreSessionList(criteria);

			if(isNullOrEmpty(sessionDatas) == false) {
                if (sessionDatas.size() > 1) {
                    SessionSortOrder.DESCENDING.sort(sessionDatas);
                }

                refreshSession(sessionDatas, SessionSortOrder.DESCENDING);

                if(getLogger().isLogLevel(LogLevel.DEBUG)) {
					getLogger().debug(MODULE, "Total records found = " + sessionDatas.size() + " with criteria provided.");
				}

            } else {
				if(getLogger().isLogLevel(LogLevel.DEBUG)) {
					getLogger().debug(MODULE, "No record found for given criteria");
				}
            }


			return sessionDatas;
		}

		@Override
		public List<SessionData> getCoreSessionList(Criteria criteria, SessionSortOrder sortOrder) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
				return null;
			}
			if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "fetch records in core session based on criteria provided");

			List<SessionData> sessionDatas = sessionDao.getCoreSessionList(criteria);

			if(isNullOrEmpty(sessionDatas) == false) {
                if (sessionDatas.size() > 1) {
                    sortOrder.sort(sessionDatas);
                }

                refreshSession(sessionDatas, sortOrder);

                if(getLogger().isLogLevel(LogLevel.DEBUG))
                    getLogger().debug(MODULE, "Total records found = " + sessionDatas.size() + " with criteria provided.");
            } else {
                if(getLogger().isLogLevel(LogLevel.DEBUG))
                    getLogger().debug(MODULE, "No record found for given criteria");
            }


			return sessionDatas;
		}

		@Override
		public List<SessionData> getCoreSessionByGatewayAddress(String gatewayAddress) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
				return null;
			}
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "fetch records in core session based on gateway address = " + gatewayAddress);

			List<SessionData> sessionDataList = sessionDao.getCoreSessionByGatewayAddress(gatewayAddress);

			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, sessionDataList == null || sessionDataList.isEmpty() ? "No record found for given criteria" :"Total session found = " + sessionDataList.size() + " with gateway address = " + gatewayAddress);


			return sessionDataList;
		}

		@Override
		public Iterator<SessionData> getCoreSessionBySessionIPv6(String sessionIP) {

			checkArgument(isNullOrBlank(sessionIP) == false, "Session ip is not provided");

			Iterator<SessionData> sessionDataIterator = null;
			if(isSessionCacheEnabled()) {
				try {
					sessionDataIterator = subscriberSessionIPv6SessionCache.get(sessionIP);
				} catch (Exception e) {
					getLogger().error(MODULE, "Error while fetching coresession by session IPv6: " + sessionIP + ". Reason: " + e.getMessage());
					getLogger().trace(MODULE, e);
					return null;
				}

			} else {
				List<SessionData> sessionDatas = NetvertexSessionManager.this.getCoreSessionBySessionIPv6(sessionIP);
				if (isNullOrEmpty(sessionDatas) == false) {
					sessionDataIterator = sessionDatas.iterator();	//NOSONAR: sessionDatas will never be Null here
				}
			}
			return sessionDataIterator;
		}

		@Override
		public void getCoreSessionBySessionIPv6(String sessionIP, Consumer<Iterator<SessionData>> consumer) {
			consumer.accept(getCoreSessionBySessionIPv6(sessionIP));
		}

		@Override
		public @Nullable Iterator<SessionData> getCoreSessionBySessionIPv4(@Nonnull String sessionIP) {

			checkArgument(isNullOrBlank(sessionIP) == false, "Session ip is not provided");

			Iterator<SessionData> sessionDataIterator = null;
			if(isSessionCacheEnabled()) {

				try {
					sessionDataIterator = subscriberSessionIPv4SessionCache.get(sessionIP);
				} catch (Exception e) {
					getLogger().error(MODULE, "Error while fetching coresession by session IPv4: " + sessionIP + ". Reason: " + e.getMessage());
					getLogger().trace(MODULE, e);
					return null;
				}
			} else {
				List<SessionData> sessionDatas = NetvertexSessionManager.this.getCoreSessionBySessionIPv4(sessionIP);

				if(isNullOrEmpty(sessionDatas) == false) {
					sessionDataIterator = sessionDatas.iterator();	//NOSONAR: sessionDatas will never be Null here
				}
			}
			return sessionDataIterator;
		}

		@Override
		public void getCoreSessionBySessionIPv4(String sessionIP, Consumer<Iterator<SessionData>> consumer) {
			consumer.accept(getCoreSessionBySessionIPv4(sessionIP));
		}


		@Override
		public Iterator<SessionData> getCoreSessionByUserIdentity(String subscriberIdentity) {

			checkArgument(isNullOrBlank(subscriberIdentity) == false, "Subscriber id is not provided");

			Iterator<SessionData> sessionDataIterator = null;
			if(isSessionCacheEnabled()) {
				try {
					sessionDataIterator = subscriberSessionCache.get(subscriberIdentity);
				} catch (Exception e) {
					getLogger().error(MODULE, "Error while fetching coresession by subscriber ID: " + subscriberIdentity + ". Reason: " + e.getMessage());
					getLogger().trace(MODULE, e);
					return null;
				}
			} else {

				List<SessionData> sessionDatas = NetvertexSessionManager.this.getCoreSessionBySubscriberIdentity(subscriberIdentity);

				if(isNullOrEmpty(sessionDatas) == false) {
					sessionDataIterator = sessionDatas.iterator();	//NOSONAR: sessionDatas will never be Null here
				}
			}
			return sessionDataIterator;
		}

		@Override
		public Criteria getCoreSessionCriteria() throws SessionException{
			if(sessionDao.isAlive()){
				return sessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
			}
			throw new SessionException("Data Source or Factory is not alive.");
		}

		@Override
		public Criteria getSessionRuleCriteria() throws SessionException{
			if(sessionDao.isAlive()){
				return sessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
			}
			throw new SessionException("Data Source or Factory is not alive.");
		}

		@Override
		public List<SessionData> getSessionRules(Criteria criteria) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
				return null;
			}

			if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "fetch records in session rule based on criteria provided");

			List<SessionData> sessionDataList = sessionDao.getSessionRules(criteria);

			if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, sessionDataList == null || sessionDataList.isEmpty() ? "No record found for given criteria" :"Total records found = "
						+ sessionDataList.size() + " with criteria provided.");
			return sessionDataList;
		}

		@Override
		public List<SessionData> getSessionRuleByCoreSessionID(@Nonnull String coreSessionID) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
				return null;
			}
			if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "Fetch records in session rule based on core session ID = " + coreSessionID);

			List<SessionData> sessionDatas = sessionDao.getSessionRuleByCoreSessionID(coreSessionID);

			if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, sessionDatas == null || sessionDatas.isEmpty()
                        ? "No record found for given criteria"
                        :"Total session found = "+ sessionDatas.size() + " with core session ID = " + coreSessionID);

			return sessionDatas;
		}

		@Override
		public SessionData getCoreSessionByCoreSessionID(String coreSessionId) {

			checkArgument(isNullOrBlank(coreSessionId) == false, "Coresession id is not provided");

			if (isSessionCacheEnabled()) {
				try {
					return primaryCache.get(coreSessionId);
				} catch (Exception e) {
					getLogger().error(MODULE, "Error while fetching coresession by coresession ID: " + coreSessionId + ". Reason: " + e.getMessage());
					getLogger().trace(MODULE, e);
					return null;
				}
			} else {
				return NetvertexSessionManager.this.getCoreSessionByCoreSessionID(coreSessionId);
			}
		}

		@Override
		public SessionData getCoreSessionByCoreSessionID(String coreSessionID,  PCRFRequest pcrfRequest,
														 Predicate<PCRFRequest, SessionData> predicate) {

			if (isSessionCacheEnabled()) {
				SessionData sessionData = null;
				try {
					sessionData = primaryCache.get(coreSessionID);
				} catch (Exception e) {
					getLogger().error(MODULE, "Error while fetching coresession by coresession ID: " + coreSessionID + ". Reason: " + e.getMessage());
					getLogger().trace(MODULE, e);
				}

				if (sessionData == null || predicate.apply(pcrfRequest, sessionData)) {

					return sessionData;
				} else {
					try {
						return primaryCache.refresh(coreSessionID);
					} catch (Exception e) {
						getLogger().error(MODULE, "Error while fetching coresession by coresession ID: " + coreSessionID + ". Reason: " + e.getMessage());
						getLogger().trace(MODULE, e);
						return null;
					}
				}

			} else {
				return NetvertexSessionManager.this.getCoreSessionByCoreSessionID(coreSessionID);

			}
		}

		@Override
		public SessionData getCoreSessionByCoreSessionID(String coreSessionID, PCRFResponse pcrfResponse,
														 Predicate<PCRFResponse, SessionData> predicate) {

			if (isSessionCacheEnabled()) {

				SessionData sessionData = primaryCache.getWithoutLoad(coreSessionID);

				if(sessionData != null) {
					if (predicate.apply(pcrfResponse, sessionData)) {
						return sessionData;
					} else {

						try {
							return primaryCache.refresh(coreSessionID);
						} catch (Exception e) {
							getLogger().error(MODULE, "Error while fetching coresession by coresession ID: " + coreSessionID + ". Reason: " + e.getMessage());
							getLogger().trace(MODULE, e);
							return null;
						}
					}

				} else {
					return NetvertexSessionManager.this.getCoreSessionByCoreSessionID(coreSessionID);
				}



			} else {
				return NetvertexSessionManager.this.getCoreSessionByCoreSessionID(coreSessionID);

			}
		}

		@Override
		public Iterator<SessionData> getCoreSessionBySessionIPv6FromCache(String sessionIPv6) {

			checkArgument(isNullOrBlank(sessionIPv6) == false, "Session ip is not provided");

			if(isSessionCacheEnabled()) {
				return subscriberSessionIPv6SessionCache.getWithoutLoad(sessionIPv6);
			} else {
				return null;
			}

		}

		@Override
		public Iterator<SessionData> getCoreSessionByUserIdentityFromCache(String subscriberIdentity) {

			checkArgument(isNullOrBlank(subscriberIdentity) == false, "Subscriber id is not provided");

			if(isSessionCacheEnabled()) {
				return subscriberSessionCache.getWithoutLoad(subscriberIdentity);
			} else {
				return null;
			}
		}

		@Override
		public Iterator<SessionData> getCoreSessionBySessionIPv4FromCache(String sessionIPv4) {

			checkArgument(isNullOrBlank(sessionIPv4) == false, "Session ip is not provided");

			if(isSessionCacheEnabled()) {
				return subscriberSessionIPv4SessionCache.getWithoutLoad(sessionIPv4);
			} else {
				return null;
			}
		}

		@Override
		public SessionData getCoreSessionByCoreSessionIDFromCache(@Nonnull String coreSessionId) {

			checkArgument(isNullOrBlank(coreSessionId) == false, "Coresession id is not provided");

			if(isSessionCacheEnabled()) {
				return primaryCache.getWithoutLoad(coreSessionId);
			} else {
				return  null;
			}
		}
	}


	private class SessionOperationImpl implements SessionOperation {
		@Override
		public void createCoreSession(PCRFResponse pcrfResponse) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing");
				return;
			}

			if(getLogger().isLogLevel(LogLevel.INFO)) {
				getLogger().info(MODULE, "Create core-session for Session ID = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()));
			}

			SessionData sessionData = new SessionDataImpl(CORE_SESS_TABLE_NAME);

			if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "Set field mapping for core session");
			for(FieldMapping fieldMapping : serverContext.getServerConfiguration().getSessionManagerConfiguration().getCoreSessionFieldMappings()){

                if(fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_USAGE_RESERVATION.val)) {

                    if(Maps.isNullOrEmpty(pcrfResponse.getUsageReservations())) {

                        sessionData.addValue(fieldMapping.getPropertyName(), "");
                    } else {

                        StringBuilder reservationString = new StringBuilder();
                        for(Map.Entry<String, String> monitoringKeyToPackage : pcrfResponse.getUsageReservations().entrySet()) {
                            reservationString.append(monitoringKeyToPackage.getKey()).append(com.elitecore.corenetvertex.constants.CommonConstants.COLON).append(monitoringKeyToPackage.getValue()).append(com.elitecore.corenetvertex.constants.CommonConstants.SEMICOLON);
                        }
                        sessionData.addValue(fieldMapping.getPropertyName(),reservationString.toString());
                    }

                } else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val)) {

                    if (Maps.isNullOrEmpty(pcrfResponse.getActivePccRules())) {
                        sessionData.addValue(fieldMapping.getPropertyName(), "");
                    } else {
                        sessionData.addValue(fieldMapping.getPropertyName(), ActivePCCRuleParser.serialize(pcrfResponse.getActivePccRules()));
                    }
                } else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val)) {

                    if (Maps.isNullOrEmpty(pcrfResponse.getActiveChargingRuleBaseNames())) {
                        sessionData.addValue(fieldMapping.getPropertyName(), "");
                    } else {
                        sessionData.addValue(fieldMapping.getPropertyName(), ActivePCCRuleParser.serialize(pcrfResponse.getActiveChargingRuleBaseNames()));
                    }

                }  else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_PACKAGE_USAGE.val)) {
                    sessionData.addValue(fieldMapping.getPropertyName(), SessionUsageParser.serialize(pcrfResponse.getSessionUsage()));

                } else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_QUOTA_RESERVATION.val) && pcrfResponse.getQuotaReservation() != null) {
					sessionData.addValue(fieldMapping.getPropertyName(), pcrfResponse.getQuotaReservation().getAsJson());
                } else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val)) {
					if(Objects.isNull(pcrfResponse.getUnAccountedQuota())) {
						sessionData.addValue(fieldMapping.getPropertyName(), null);
					}else{
						sessionData.addValue(fieldMapping.getPropertyName(), pcrfResponse.getUnAccountedQuota().getAsJson());
					}
				} else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.val) && pcrfResponse.getPccProfileSelectionState() != null) {
					sessionData.addValue(fieldMapping.getPropertyName(), pcrfResponse.getPccProfileSelectionState().toJson());
				}  else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_LOCATION.val)) {
					sessionData.addValue(fieldMapping.getPropertyName(), LocationSerializationUtil.serialize(pcrfResponse));
				} else {
                    String attributeVal = pcrfResponse.getAttribute(fieldMapping.getPropertyName());
                    if(attributeVal != null){
                        sessionData.addValue(fieldMapping.getPropertyName(),attributeVal);
                    }
                }


            }
			sessionData.addValue(PCRFKeyConstants.CS_SESSION_MANAGER_ID.getVal(), serverContext.getServerInstanceId());

			if(isSessionCacheEnabled()){
				primaryCache.put(pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()), sessionData);
			}

			int result = sessionDao.saveCoreSession(sessionData);

			if(result >= 0){
				if(getLogger().isLogLevel(LogLevel.INFO))
					getLogger().info(MODULE, "Core-session for session = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + " creation completed");
			}else{
				if(getLogger().isLogLevel(LogLevel.WARN))
					getLogger().warn(MODULE, "Could not create the core-session for session = ." + pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()));
			}
		}

		@Override
		public void updateCoreSession(PCRFResponse pcrfResponse) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing");
				return;
			}

			SessionData sessionData = new SessionDataImpl(CORE_SESS_TABLE_NAME, pcrfResponse.getSessionStartTime(), Calendar.getInstance().getTime());

			if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "Set field mapping for core session");
			for(FieldMapping fieldMapping : serverContext.getServerConfiguration().getSessionManagerConfiguration().getCoreSessionFieldMappings()){

                if(fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_USAGE_RESERVATION.val)) {

                    if(Maps.isNullOrEmpty(pcrfResponse.getUsageReservations())) {

                        sessionData.addValue(fieldMapping.getPropertyName(), "");
                    } else {

                        StringBuilder reservationString = new StringBuilder();
                        for(Map.Entry<String, String> monitoringKeyToPackage : pcrfResponse.getUsageReservations().entrySet()) {
                            reservationString.append(monitoringKeyToPackage.getKey()).append(com.elitecore.corenetvertex.constants.CommonConstants.COLON).append(monitoringKeyToPackage.getValue()).append(com.elitecore.corenetvertex.constants.CommonConstants.SEMICOLON);
                        }
                        sessionData.addValue(fieldMapping.getPropertyName(),reservationString.toString());
                    }

                } else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val)) {

                    if (Maps.isNullOrEmpty(pcrfResponse.getActivePccRules())) {
                        sessionData.addValue(fieldMapping.getPropertyName(), "");
                    } else {
                        sessionData.addValue(fieldMapping.getPropertyName(), ActivePCCRuleParser.serialize(pcrfResponse.getActivePccRules()));
                    }
                } else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val)) {

                    if (Maps.isNullOrEmpty(pcrfResponse.getActiveChargingRuleBaseNames())) {
                        sessionData.addValue(fieldMapping.getPropertyName(), "");
                    } else {
                        sessionData.addValue(fieldMapping.getPropertyName(), ActivePCCRuleParser.serialize(pcrfResponse.getActiveChargingRuleBaseNames()));
                    }

                }  else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_PACKAGE_USAGE.val)) {
                    sessionData.addValue(fieldMapping.getPropertyName(), SessionUsageParser.serialize(pcrfResponse.getSessionUsage()));

                } else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_QUOTA_RESERVATION.val)) {
					if(pcrfResponse.getQuotaReservation() == null) {
						sessionData.addValue(fieldMapping.getPropertyName(), "");
					} else {
						sessionData.addValue(fieldMapping.getPropertyName(), pcrfResponse.getQuotaReservation().getAsJson());
					}
                } else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val)) {
                	if(Objects.isNull(pcrfResponse.getUnAccountedQuota())) {
						sessionData.addValue(fieldMapping.getPropertyName(), null);
					}else{
						sessionData.addValue(fieldMapping.getPropertyName(), pcrfResponse.getUnAccountedQuota().getAsJson());
					}
				} else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_PCC_PROFILE_SELECTION_STATE.val) && pcrfResponse.getPccProfileSelectionState() != null) {
					sessionData.addValue(fieldMapping.getPropertyName(), pcrfResponse.getPccProfileSelectionState().toJson());
				} else if (fieldMapping.getPropertyName().equals(PCRFKeyConstants.CS_LOCATION.val) && pcrfResponse.getQuotaReservation() != null) {
					sessionData.addValue(fieldMapping.getPropertyName(), LocationSerializationUtil.serialize(pcrfResponse));
				} else {
                    String attributeVal = pcrfResponse.getAttribute(fieldMapping.getPropertyName());
                    if(attributeVal != null){
                        sessionData.addValue(fieldMapping.getPropertyName(),attributeVal);
                    }
                }


            }
			sessionData.addValue(PCRFKeyConstants.CS_SESSION_MANAGER_ID.getVal(), serverContext.getServerInstanceId());

			String coreSessionId = pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal());

			if (isSessionCacheEnabled()) {
				primaryCache.put(coreSessionId, sessionData);
			}

			if(sessionDao.updateCoreSession(sessionData) == 0){
				if(getLogger().isLogLevel(LogLevel.DEBUG))
					getLogger().debug(MODULE, "No Core-session found with Session-ID = "+ coreSessionId +" .Creating session on update");
				createCoreSession(pcrfResponse);
			}else{
				if(getLogger().isLogLevel(LogLevel.INFO))
					getLogger().info(MODULE, "Updated core-session for Session ID = " + coreSessionId);
			}
		}

		@Override
		public SessionLocator getSessionLocator() {
			return sessionLocator;
		}

		@Override
		public Criteria getCoreSessionCriteria() {
			return sessionDao.getCriteriaByTableName(CORE_SESS_TABLE_NAME);
		}

		@Override
		public Criteria getSessionRuleCriteria() {
			return sessionDao.getCriteriaByTableName(SESSION_RULE_TABLE_NAME);
		}

		@Override
		public void createSessionRule(PCRFResponse pcrfResponse) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing");
				return;
			}

			if(getLogger().isLogLevel(LogLevel.INFO))
				getLogger().info(MODULE, "Create session rule for session ID = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()) + " with AF Session ID  = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_AF_SESSION_ID.getVal()));

			SessionData sessionData = new SessionDataImpl(SESSION_RULE_TABLE_NAME);

			List<PCCRule> pccRules = pcrfResponse.getInstallablePCCRules();

			if(pccRules == null || pccRules.isEmpty()){
				if(getLogger().isLogLevel(LogLevel.DEBUG))
					getLogger().debug(MODULE, "Skipping session rule creation for session ID = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + ". Reason: No PCC rules found for this session");
				return;
			}

			if(getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "Set field mapping for session rule");
			for(FieldMapping fieldMapping : serverContext.getServerConfiguration().getSessionManagerConfiguration().getSessionRuleFieldMappings()){
                String attributeVal = pcrfResponse.getAttribute(fieldMapping.getPropertyName());
                if(attributeVal != null){
                    sessionData.addValue(fieldMapping.getPropertyName(),attributeVal);
                }
            }


			for(PCCRule pccRule : pccRules){
				sessionData.addValue(PCRFKeyConstants.PCC_RULE_LIST.getVal(), pccRule.getName());
				int result = sessionDao.saveSessionRule(sessionData);
				if(result >= 0){
					if(getLogger().isLogLevel(LogLevel.INFO))
						getLogger().info(MODULE, "Session rule for session = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + " creation completed");
				}else{
					if(getLogger().isLogLevel(LogLevel.WARN))
						getLogger().warn(MODULE, "Could not create the session rule for session = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()));
				}
			}
		}

		@Override
		public void deleteSessionRule(@Nonnull Criteria criteria) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Could not delete session rule. Data Source or Factory is not alive.");
				return;
			}

			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Deleting session rules based on criteria provided");

			sessionDao.deleteSessionRule(criteria);
		}

		@Override
		public int deleteCoreSession(@Nonnull Criteria criteria) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Could not delete core session. Data Source or Factory is not alive.");
				return 0;
			}

			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Deleting core sessions based on criteria provided");

			return sessionDao.deleteCoreSession(criteria);
		}

		@Override
		public int deleteCoreSessionByCoreSessionId(String coreSessionId) {

			checkArgument(isNullOrBlank(coreSessionId) == false, "Coresession id is not provided");

			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Could not delete core session. Data Source or Factory is not alive.");
				return 0;
			}

			if (isSessionCacheEnabled()) {
				primaryCache.remove(coreSessionId);
			}

			return sessionDao.deleteCoreSessionByCoreSessionId(coreSessionId);
		}

		@Override
		public boolean deleteCoreSessionByCoreSessionIdFromCache(String coreSessionId) {
			checkArgument(isNullOrBlank(coreSessionId) == false, "Coresession id is not provided");
			if (isSessionCacheEnabled()) {
				if (primaryCache.remove(coreSessionId) != null) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void deleteSessionRuleByCoreSessionId(String coreSessionId) {
			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Could not delete core session. Data Source or Factory is not alive.");
				return;
			}
			sessionDao.deleteSessionRuleByCoreSessionId(coreSessionId);
		}



		@Override
		public void createSessionRule(String gxCoreSessionId, String afSessionId,
									  List<AFSessionRule> saveList, PCRFResponse pcrfResponse) {

			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
				return;
			}

			if(getLogger().isLogLevel(LogLevel.INFO))
				getLogger().info(MODULE, "Create session rule for session ID = " + gxCoreSessionId + " with AF Session ID  = " + afSessionId);

			for(int i=0; i < saveList.size(); i++) {

				int result = sessionDao.saveSessionRule(createSessionRuleData(gxCoreSessionId, saveList.get(i),pcrfResponse));
				if(result >= 0){
					if(getLogger().isLogLevel(LogLevel.INFO))
						getLogger().info(MODULE, "Session rule for session = " + gxCoreSessionId + " with AF Session ID  = " + afSessionId + " creation completed");
				}else{
					if(getLogger().isLogLevel(LogLevel.WARN))
						getLogger().warn(MODULE, "Could not create the session rule for session = " + " with AF Session ID  = " + afSessionId + gxCoreSessionId);
				}
			}

		}

		private SessionData createSessionRuleData(String gxCoreSessionId,
												  AFSessionRule afSessionRule,
												  PCRFResponse response) {

			SessionData sessionData = new SessionDataImpl(SESSION_RULE_TABLE_NAME,afSessionRule.getSrId(), Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Set field mapping for session rule");
			for(FieldMapping fieldMapping : serverContext.getServerConfiguration().getSessionManagerConfiguration().getSessionRuleFieldMappings()){
				String attributeVal = response.getAttribute(fieldMapping.getPropertyName());
				if(attributeVal != null){
					sessionData.addValue(fieldMapping.getPropertyName(),attributeVal);
				}
			}

			sessionData.addValue(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, response.getAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val));
			sessionData.addValue(PCRFKeyConstants.CS_CORESESSION_ID.val, gxCoreSessionId);

			afSessionRule.toSessionData(sessionData);

			return sessionData;
		}

		@Override
		public void updateSessionRule(String gxCoreSessionId, String afSessionId, List<AFSessionRule> updateList, PCRFResponse pcrfResponse) {

			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
				return;
			}

			if(getLogger().isLogLevel(LogLevel.INFO))
				getLogger().info(MODULE, "Updating session rule for session ID = " + gxCoreSessionId + " with AF Session ID  = " + afSessionId);

			for(int i=0; i < updateList.size(); i++) {

				SessionData sessionData = createSessionRuleData(gxCoreSessionId, updateList.get(i),pcrfResponse);
				int result = sessionDao.updateSessionRule(sessionData);
				if(result >= 0){
					if(getLogger().isLogLevel(LogLevel.INFO))
						getLogger().info(MODULE, "Session rule for session = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + " updation completed");
				}else{
					if(getLogger().isLogLevel(LogLevel.WARN))
						getLogger().warn(MODULE, "Could not update the session rule for session = " + gxCoreSessionId + " with AF session id = " + afSessionId);
					result = sessionDao.saveSessionRule(sessionData);
					if(result >= 0) {
						if(getLogger().isLogLevel(LogLevel.WARN))
							getLogger().info(MODULE, "Session rule for session = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + " creation completed");
					} else {
						if(getLogger().isLogLevel(LogLevel.WARN))
							getLogger().warn(MODULE, "Could not update the session rule for session = " + gxCoreSessionId + " with AF session id = " + afSessionId);
					}

				}
			}
		}

		@Override
		public void deleteSessionRule(String gxCoreSessionId, String afSessionId, List<AFSessionRule> deleteList, PCRFResponse pcrfResponse) {

			if(sessionDao.isAlive() == false){
				getLogger().error(MODULE, "Data Source or Factory is not alive. So skip further processing.");
				return;
			}

			if(getLogger().isLogLevel(LogLevel.INFO))
				getLogger().info(MODULE, "Delete session rule for session ID = " + gxCoreSessionId + " with AF Session ID  = " + afSessionId);

			for(int i=0; i < deleteList.size(); i++) {
				int result = sessionDao.deleteSessionRule(createSessionRuleData(gxCoreSessionId, deleteList.get(i),pcrfResponse));

				if(result >= 0){
					if(getLogger().isLogLevel(LogLevel.INFO))
						getLogger().info(MODULE, "Session rule for session = " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.getVal()) + " deletion completed");
				}else{
					if(getLogger().isLogLevel(LogLevel.WARN))
						getLogger().warn(MODULE, "Could not delete the session rule for session = " + gxCoreSessionId + " with AF session id = " + afSessionId);
				}
			}

		}
	}
}