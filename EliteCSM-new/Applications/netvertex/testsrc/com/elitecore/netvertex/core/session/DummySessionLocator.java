package com.elitecore.netvertex.core.session;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.elitecore.commons.kpi.handler.IntervalBasedTask;
import com.elitecore.commons.kpi.handler.SingleExecutionTask;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.corenetvertex.commons.Predicate;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.util.InMemoryCompositeIndexCache;
import com.elitecore.corenetvertex.util.PartitioningCache;
import com.elitecore.corenetvertex.util.PrimaryCache;
import com.elitecore.corenetvertex.util.TaskScheduler;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;


public class DummySessionLocator implements SessionLocator{

    private PrimaryCache<String, SessionData> primaryCache;
    private InMemoryCompositeIndexCache<String,String,SessionData> subscriberSessionCache;
    private InMemoryCompositeIndexCache<String,String,SessionData> subscriberSessionIPv4SessionCache;
    private InMemoryCompositeIndexCache<String,String,SessionData> subscriberSessionIPv6SessionCache;

    public DummySessionLocator() {
        TaskScheduler taskScheduler = new TaskScheduler() {
            @Override
            public Future<?> scheduleSingleExecutionTask(SingleExecutionTask task) {
                return null;
            }

            @Override
            public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
                return null;
            }
        };
        this.primaryCache = new PartitioningCache.CacheBuilder<String,SessionData>(taskScheduler).build();

        createSecondaryCache(taskScheduler, primaryCache);
    }

    private void createSecondaryCache(TaskScheduler taskScheduler, PrimaryCache<String, SessionData> primaryCache) {
        this.subscriberSessionCache = new InMemoryCompositeIndexCache.CacheBuilder<String,String,SessionData>(
                taskScheduler,
                primaryCache,
                new KeyFunction(PCRFKeyConstants.CS_CORESESSION_ID.val),
                new KeyFunction(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val)).build();

        this.subscriberSessionIPv4SessionCache = new InMemoryCompositeIndexCache.CacheBuilder<String,String,SessionData>(
                taskScheduler,
                primaryCache,
                new KeyFunction(PCRFKeyConstants.CS_CORESESSION_ID.val),
                new KeyFunction(PCRFKeyConstants.CS_SESSION_IPV4.val)).build();

        this.subscriberSessionIPv6SessionCache = new InMemoryCompositeIndexCache.CacheBuilder<String,String,SessionData>(
                taskScheduler,
                primaryCache,
                new KeyFunction(PCRFKeyConstants.CS_CORESESSION_ID.val),
                new KeyFunction(PCRFKeyConstants.CS_SESSION_IPV6.val))
                .build();
    }

    @Nullable
    @Override
    public List<SessionData> getCoreSessionList(@Nonnull Criteria criteria) {
        return null;
    }

    @Nullable
    @Override
    public List<SessionData> getCoreSessionList(@Nonnull Criteria criteria, SessionSortOrder sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public Iterator<SessionData> getCoreSessionBySessionIPv6(@Nonnull String sessionIP) {
        return subscriberSessionIPv6SessionCache.getWithoutLoad(sessionIP);
    }

    @Override
    public void getCoreSessionBySessionIPv6(String sessionIP, Consumer<Iterator<SessionData>> consumer) {
        consumer.accept(getCoreSessionBySessionIPv6(sessionIP));
    }

    @Nullable
    @Override
    public Iterator<SessionData> getCoreSessionBySessionIPv4(String ipV4) {
        return subscriberSessionIPv4SessionCache.getWithoutLoad(ipV4);
    }


    @Override
    public void getCoreSessionBySessionIPv4(String sessionIP, Consumer<Iterator<SessionData>> sessionSort) {
        sessionSort.accept(getCoreSessionBySessionIPv4(sessionIP));
    }

    @Nullable
    @Override
    public Iterator<SessionData> getCoreSessionByUserIdentity(@Nonnull String userName) {
        return subscriberSessionCache.getWithoutLoad(userName);
    }

    @Nullable
    @Override
    public List<SessionData> getCoreSessionByGatewayAddress(@Nonnull String gatewayAddress) {
        return null;
    }

    @Nullable
    @Override
    public SessionData getCoreSessionByCoreSessionID(@Nonnull String coreSessionId) {
        return primaryCache.getWithoutLoad(coreSessionId);
    }

    @Nullable
    @Override
    public List<SessionData> getSessionRuleByCoreSessionID(@Nonnull String coreSessionID) {
        return null;
    }

    @Nullable
    @Override
    public List<SessionData> getSessionRules(@Nonnull Criteria criteria) {
        return null;
    }

    @Nonnull
    @Override
    public Criteria getCoreSessionCriteria() throws SessionException {
        return null;
    }

    @Nonnull
    @Override
    public Criteria getSessionRuleCriteria() throws SessionException {
        return null;
    }

    @Nullable
    @Override
    public SessionData getCoreSessionByCoreSessionID(@Nonnull String coreSessionID, @Nonnull PCRFResponse pcrfResponse, Predicate<PCRFResponse, SessionData> predicate) {
        return primaryCache.getWithoutLoad(coreSessionID);
    }

    @Nullable
    @Override
    public SessionData getCoreSessionByCoreSessionID(@Nonnull String coreSessionID, @Nonnull PCRFRequest pcrfRequest, Predicate<PCRFRequest, SessionData> predicate) {
        return primaryCache.getWithoutLoad(coreSessionID);
    }

    @Nullable
    @Override
    public Iterator<SessionData> getCoreSessionBySessionIPv6FromCache(@Nonnull String sessionIP) {
        return subscriberSessionIPv6SessionCache.getWithoutLoad(sessionIP);
    }

    @Nullable
    @Override
    public Iterator<SessionData> getCoreSessionByUserIdentityFromCache(@Nonnull String subscriberIdenity) {
        return subscriberSessionCache.getWithoutLoad(subscriberIdenity);
    }

    @Nullable
    @Override
    public Iterator<SessionData> getCoreSessionBySessionIPv4FromCache(@Nonnull String ipV4) {
        return subscriberSessionIPv4SessionCache.getWithoutLoad(ipV4);
    }

    @Nullable
    @Override
    public SessionData getCoreSessionByCoreSessionIDFromCache(@Nonnull String coreSessionId) {
        return primaryCache.getWithoutLoad(coreSessionId);
    }

    public SessionData addSession(SessionData sessionData) {
        return primaryCache.put(sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val), sessionData);
    }
}
