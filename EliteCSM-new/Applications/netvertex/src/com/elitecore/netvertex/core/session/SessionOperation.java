package com.elitecore.netvertex.core.session;

import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.netvertex.gateway.diameter.af.AFSessionRule;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

public interface SessionOperation{
	void createCoreSession(@Nonnull PCRFResponse pcrfResponse);
	void updateCoreSession(@Nonnull PCRFResponse pcrfResponse);
	void createSessionRule(@Nonnull PCRFResponse pcrfResponse);
	int deleteCoreSession(@Nonnull Criteria criteria);
	int deleteCoreSessionByCoreSessionId(@Nonnull String coreSessionId);
	void deleteSessionRuleByCoreSessionId(@Nonnull String coreSessionId);
	void deleteSessionRule(@Nonnull Criteria criteria);
	@Nonnull SessionLocator getSessionLocator();
	@Nonnull Criteria getCoreSessionCriteria() throws SessionException;
	@Nonnull Criteria getSessionRuleCriteria() throws SessionException;

	void deleteSessionRule(String attribute, String attribute2,
			List<AFSessionRule> deleteList, PCRFResponse pcrfResponse);
	void createSessionRule(String attribute, String attribute2,
			List<AFSessionRule> list, PCRFResponse pcrfResponse);
	void updateSessionRule(String attribute, String attribute2,
			List<AFSessionRule> updateList, PCRFResponse pcrfResponse);
	boolean deleteCoreSessionByCoreSessionIdFromCache(String coreSessionId);

}
