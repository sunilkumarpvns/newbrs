package com.elitecore.netvertex.core.session;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.util.commons.CacheEventListener;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class AutoSessionClosure implements CacheEventListener<String, SessionData> {

    private static final String MODULE = "AUTO-SESSION-CLOSURE";
    private List<SessionData> expiredSessionDatas;
    private NetvertexSessionManager netvertexSessionManager;
    private long expiryTime;

    public AutoSessionClosure(NetvertexSessionManager netvertexSessionManager, long expiryTime){
        this.netvertexSessionManager = netvertexSessionManager;
        this.expiryTime = expiryTime;
    }

    @Override
    public void cacheRemoved(String key, SessionData value) {
        // No need to implement this method
    }

    @Override
    public void cacheAdded(String key, SessionData value) {
        // No need to implement this method
    }

    @Override
    public void evict(Set<Map.Entry<String, SessionData>> entries){
        entries.parallelStream().forEach(this::evict);
    }

    private void evict(Map.Entry<String, SessionData> entry) {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Running auto session closure for session id : " + entry.getKey());

        }
        SessionData sessionData = entry.getValue();
        String sessionType = sessionData.getValue(PCRFKeyConstants.CS_SESSION_TYPE.val);

        SessionTypeConstant sessionTypeConstant = SessionTypeConstant.fromValue(sessionType);
        if(sessionTypeConstant != SessionTypeConstant.GY && sessionTypeConstant != SessionTypeConstant.RO
                && sessionTypeConstant != SessionTypeConstant.RADIUS ) {
            return;
        }

        if(sessionData.getLastUpdateTime().getTime() < System.currentTimeMillis() - expiryTime){

            String quotaReservationString = sessionData.getValue(PCRFKeyConstants.CS_QUOTA_RESERVATION.val);

            if(Strings.isNullOrBlank(quotaReservationString)) {
                return;
            }

            PCRFRequest pcrfRequest = new PCRFRequestImpl();
            PCRFPacketUtil.buildPCRFRequest(sessionData, pcrfRequest);

            QuotaReservation quotaReservation = pcrfRequest.getQuotaReservation();
            if(quotaReservation.isReservationExist()){

                try {
                    Criteria criteria = netvertexSessionManager.getSessionLookup().getCoreSessionCriteria();
                    criteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.val, entry.getKey()));
                    List<SessionData> sessionDataList = netvertexSessionManager.getSessionLookup().getCoreSessionList(criteria);

                    if(sessionDataList.get(0).getLastUpdateTime().getTime() < System.currentTimeMillis() - expiryTime){
                        if(expiredSessionDatas == null){
                            expiredSessionDatas = new ArrayList<>();
                        }
                        expiredSessionDatas.add(sessionData);
                    }
                } catch (SessionException e) {
                    getLogger().error(MODULE, "Error while auto session closure process. Reason: " + e.getMessage());
                    getLogger().trace(MODULE,e);
                }

            }
        }else{
            netvertexSessionManager.addToCache(sessionData);
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Auto session closure completed successfully for session id : " + entry.getKey());
        }

    }

    public List<SessionData> getExpiredSession() {
        return expiredSessionDatas;
    }
}