package com.elitecore.netvertex.ws;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class ReAuthorizationControllerTestUtil {
    private static final String SCHEMA_NAME = "101";
    //this key is used to identify which session is being captured in argument capture
    public static final String PCRF_KEY_TO_VALIDATE = PCRFKeyConstants.CS_CALLED_NETWORK_NAME.val;


    public static SessionData createSessionDataWithIdentityValue(String identityValue, PCRFKeyConstants pcrfKey, Date time, String sessionType) {
        SessionData session = new SessionDataImpl(SCHEMA_NAME, identityValue, time, time);
        session.addValue(pcrfKey.getVal(), identityValue);
        session.addValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal(), sessionType);
        session.addValue(PCRF_KEY_TO_VALIDATE,time.toString());

        return session;
    }

    public static SessionData createSessionData(Date time, List<LinkedHashMap<PCRFKeyConstants, String>> list) {
        SessionData session = new SessionDataImpl(SCHEMA_NAME, time, time);
        list.forEach(map -> map.forEach((pcrfKeyConstants, s) -> session.addValue(pcrfKeyConstants.getVal(), s)));
        session.addValue(PCRF_KEY_TO_VALIDATE,time.toString());
        return session;
    }

    public static ReAuthorizedSessionMatcher contains(SessionData sessionData, String key){
        return new ReAuthorizedSessionMatcher(sessionData,key);
    }

}
