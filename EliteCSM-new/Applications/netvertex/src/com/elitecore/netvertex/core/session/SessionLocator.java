package com.elitecore.netvertex.core.session;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.corenetvertex.commons.Predicate;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public interface SessionLocator {
	
	@Nullable List<SessionData> getCoreSessionList(@Nonnull Criteria criteria);
	@Nullable List<SessionData> getCoreSessionList(@Nonnull Criteria criteria, SessionSortOrder sortOrder);
	@Nullable Iterator<SessionData> getCoreSessionBySessionIPv6(@Nonnull String sessionIP);
	void getCoreSessionBySessionIPv6(String sessionIP, Consumer<Iterator<SessionData>> consumer);

	@Nullable Iterator<SessionData> getCoreSessionBySessionIPv4(String ipV4);
    void getCoreSessionBySessionIPv4(String sessionIP, Consumer<Iterator<SessionData>> sessionSort);

	@Nullable Iterator<SessionData> getCoreSessionByUserIdentity(@Nonnull String userName);
	@Nullable List<SessionData> getCoreSessionByGatewayAddress(@Nonnull String gatewayAddress);
	@Nullable SessionData getCoreSessionByCoreSessionID(@Nonnull String coreSessionId);
	@Nullable List<SessionData> getSessionRuleByCoreSessionID(@Nonnull String coreSessionID);
	@Nullable List<SessionData> getSessionRules(@Nonnull Criteria criteria);
	@Nonnull Criteria getCoreSessionCriteria() throws SessionException;
	@Nonnull Criteria getSessionRuleCriteria() throws SessionException;
	/**
	 * 
	 * @param coreSessionID
	 * @param pcrfResponse
	 * @param predicate
	 * @return
	 */
	@Nullable SessionData getCoreSessionByCoreSessionID(@Nonnull String coreSessionID, @Nonnull PCRFResponse pcrfResponse, Predicate<PCRFResponse, SessionData> predicate);
	
	/**
	 * 
	 * @param coreSessionID
	 * @param pcrfRequest
	 * @param predicate
	 * @return
	 */
	@Nullable SessionData getCoreSessionByCoreSessionID(@Nonnull String coreSessionID, @Nonnull PCRFRequest pcrfRequest, Predicate<PCRFRequest, SessionData> predicate);
	
	@Nullable
	Iterator<SessionData> getCoreSessionBySessionIPv6FromCache(@Nonnull String sessionIP);
	@Nullable
	Iterator<SessionData> getCoreSessionByUserIdentityFromCache(@Nonnull String subscriberIdenity);
	@Nullable
	Iterator<SessionData> getCoreSessionBySessionIPv4FromCache(@Nonnull String ipV4);

	@Nullable SessionData getCoreSessionByCoreSessionIDFromCache(@Nonnull String coreSessionId);
}
