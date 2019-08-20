package com.elitecore.netvertex.core.session;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.Session;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionFactory;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;

import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.netvertex.core.session.NetvertexSessionManager.CORE_SESS_TABLE_NAME;
import static com.elitecore.netvertex.core.session.NetvertexSessionManager.SESSION_RULE_TABLE_NAME;

public class RelationalDBSessionDao implements SessionDao {
    private static final String MODULE = "SESSION-DAO-IMPL";
    private NetVertexServerContext serverContext;
    private SessionConfigurationImpl sessionConfiguration;
    private SessionFactory sessionFactory;
    private Session session;

    public RelationalDBSessionDao(NetVertexServerContext serverContext, SessionConfigurationImpl sessionConfiguration)
            throws InitializationFailedException {
        this.serverContext = serverContext;
        this.sessionConfiguration = sessionConfiguration;
        this.sessionFactory = sessionConfiguration.createSessionFactory();
        if(isAlive()){
            this.session = sessionFactory.getSession();
        }
        if(getLogger().isInfoLogLevel()){
            getLogger().info(MODULE, "Relational DB SessionDao created successfully.");
        }
    }

    @Override
    public void reloadSessionManagerConfiguration(){
        sessionConfiguration.setMaxBatchSize(serverContext.getServerConfiguration().getSessionManagerConfiguration().getBatchSize());
    }

    @Override
    public boolean isAlive(){
        return Objects.nonNull(sessionFactory);
    }

    @Override
    public int saveCoreSession(SessionData sessionData){
        return session.save(sessionData);
    }

    @Override
    public int updateCoreSession(SessionData sessionData){
        Criteria criteria = session.createCriteria(sessionData.getSchemaName());
        criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.getVal()
                , sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val)));

        String hint = serverContext.getServerConfiguration().
                getMiscellaneousParameterConfiguration().
                getParameterValue(MiscellaneousConfiguration.CORESESSION_CORESESSIONID);
        if(Strings.isNullOrBlank(hint) == false){
            criteria.setHint(hint);
        }

        return session.update(sessionData, criteria);
    }

    @Override
    public Criteria getCriteriaByTableName(String tableName){
        return session.createCriteria(tableName);
    }

    @Override
    public int saveSessionRule(SessionData sessionData){
        return session.save(sessionData);
    }

    @Override
    public int updateSessionRule(SessionData sessionData){
        return session.update(sessionData);
    }

    @Override
    public void deleteSessionRule(Criteria criteria){
        session.delete(criteria);
    }

    @Override
    public int deleteSessionRule(SessionData sessionData){
        return session.delete(sessionData);
    }

    @Override
    public int deleteCoreSession(Criteria criteria){
        return session.delete(criteria);
    }

    @Override
    public int deleteCoreSessionByCoreSessionId(String coreSessionId){
        Criteria criteria = session.createCriteria(CORE_SESS_TABLE_NAME);
        criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId));

        String hint = serverContext.getServerConfiguration().
                getMiscellaneousParameterConfiguration().
                getParameterValue(MiscellaneousConfiguration.CORESESSION_CORESESSIONID);

        if(Strings.isNullOrBlank(hint) == false){
            criteria.setHint(hint);
        }
        return deleteCoreSession(criteria);
    }

    @Override
    public void deleteSessionRuleByCoreSessionId(String coreSessionId){
        Criteria criteria = session.createCriteria(SESSION_RULE_TABLE_NAME);
        criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, coreSessionId));
        String hint = serverContext.getServerConfiguration().
                getMiscellaneousParameterConfiguration().
                getParameterValue(MiscellaneousConfiguration.SESSIONRULE_CORESESSIONID);

        if(Strings.isNullOrBlank(hint) == false){
            criteria.setHint(hint);
        }
        deleteSessionRule(criteria);
    }

    @Override
    public List<SessionData> getCoreSessionBySessionIPv6(String sessionIP){
        Criteria criteria = session.createCriteria(CORE_SESS_TABLE_NAME);
        criteria.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV6.getVal(), sessionIP));
        return session.list(criteria);
    }

    @Override
    public List<SessionData> getCoreSessionBySessionIPv4(String sessionIP){
        Criteria criteria = session.createCriteria(CORE_SESS_TABLE_NAME);
        criteria.add(Restrictions.eq(PCRFKeyConstants.CS_SESSION_IPV4.getVal(), sessionIP));
        return session.list(criteria);
    }

    @Override
    public List<SessionData> getCoreSessionByCoreSessionID(String coreSessionId){
        Criteria criteria = session.createCriteria(CORE_SESS_TABLE_NAME);
        criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), coreSessionId));

        String hint = serverContext.getServerConfiguration().
                getMiscellaneousParameterConfiguration().
                getParameterValue(MiscellaneousConfiguration.CORESESSION_CORESESSIONID);
        if(Strings.isNullOrBlank(hint) == false){
            criteria.setHint(hint);
        }

        return session.list(criteria);
    }

    @Override
    public List<SessionData> getCoreSessionBySubscriberIdentity(String subscriberIdentity){
        Criteria criteria = session.createCriteria(CORE_SESS_TABLE_NAME);
        criteria.add(Restrictions.eq(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.getVal(), subscriberIdentity));
        return session.list(criteria);
    }

    @Override
    public List<SessionData> getCoreSessionByGatewayAddress(String gatewayAddress){
        Criteria criteria = session.createCriteria(CORE_SESS_TABLE_NAME);
        criteria.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.getVal(), gatewayAddress));
        return session.list(criteria);
    }

    @Override
    public List<SessionData> getCoreSessionList(Criteria criteria){
        return session.list(criteria);
    }

    @Override
    public List<SessionData> getSessionRules(Criteria criteria){
        return session.list(criteria);
    }

    @Override
    public List<SessionData> getSessionRuleByCoreSessionID(String coreSessionID){
        Criteria criteria = session.createCriteria(SESSION_RULE_TABLE_NAME);
        criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), coreSessionID));
        String hint = serverContext.getServerConfiguration().
                getMiscellaneousParameterConfiguration().
                getParameterValue(MiscellaneousConfiguration.SESSIONRULE_CORESESSIONID);
        if(Strings.isNullOrBlank(hint) == false){
            criteria.setHint(hint);
        }
        return getSessionRules(criteria);
    }

}
