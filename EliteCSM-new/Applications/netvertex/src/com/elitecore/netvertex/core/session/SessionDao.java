package com.elitecore.netvertex.core.session;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;

import java.util.List;

public interface SessionDao {
    void reloadSessionManagerConfiguration();
    boolean isAlive();
    int saveCoreSession(SessionData sessionData);
    int updateCoreSession(SessionData sessionData);
    Criteria getCriteriaByTableName(String tableName);
    int saveSessionRule(SessionData sessionData);
    int updateSessionRule(SessionData sessionData);
    void deleteSessionRule(Criteria criteria);
    int deleteSessionRule(SessionData sessionData);
    int deleteCoreSession(Criteria criteria);
    int deleteCoreSessionByCoreSessionId(String coreSessionId);
    void deleteSessionRuleByCoreSessionId(String coreSessionId);
    List<SessionData> getCoreSessionBySessionIPv6(String sessionIP);
    List<SessionData> getCoreSessionBySessionIPv4(String sessionIP);
    List<SessionData> getCoreSessionByCoreSessionID(String coreSessionId);
    List<SessionData> getCoreSessionBySubscriberIdentity(String subscriberIdentity);
    List<SessionData> getCoreSessionByGatewayAddress(String gatewayAddress);
    List<SessionData> getCoreSessionList(Criteria criteria);
    List<SessionData> getSessionRules(Criteria criteria);
    List<SessionData> getSessionRuleByCoreSessionID(String coreSessionID);
}
