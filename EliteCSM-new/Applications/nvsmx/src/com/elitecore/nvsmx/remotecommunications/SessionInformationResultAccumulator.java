package com.elitecore.nvsmx.remotecommunications;


import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.session.SessionInformation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by aditya on 5/8/17.
 */
public class  SessionInformationResultAccumulator {

    private @Nonnull Predicate<SessionInformation> predicate;
    private @Nonnull List<RMIResponse<Collection<SessionInformation>>> remoteRMIResponses;

    public SessionInformationResultAccumulator(@Nonnull Predicate<SessionInformation> predicate) {
        this.predicate = predicate;
        this.remoteRMIResponses = new ArrayList<RMIResponse<Collection<SessionInformation>>>();
    }

    public void accumulate(Collection<RMIResponse<Collection<SessionInformation>>> remoteRMIResponses) {
        this.remoteRMIResponses.addAll(remoteRMIResponses);
    }

    public void accumulate(RMIResponse<Collection<SessionInformation>> remoteRMIResponses) {
        this.remoteRMIResponses.add(remoteRMIResponses);
    }

    public Collection<SessionInformation> getSessionInformations() {
        return getSessionInformationToRMIResponse().keySet();
    }

    public Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> getSessionInformationToRMIResponse() {

        Map<String, SessionInformation> coreSessionIdToMap = Maps.newHashMap();
        Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionInformationToRMIResponse = Maps.newLinkedHashMap();
        for (RMIResponse<Collection<SessionInformation>> rmiResponse : remoteRMIResponses) {
            for (SessionInformation sessionInformation : rmiResponse.getResponse()) {
                store(coreSessionIdToMap, sessionInformationToRMIResponse, rmiResponse, sessionInformation);
            }
        }


        return sessionInformationToRMIResponse;
    }

    private void store(Map<String, SessionInformation> coreSessionIdToMap, Map<SessionInformation, RMIResponse<Collection<SessionInformation>>> sessionInformationToRMIResponse, RMIResponse<Collection<SessionInformation>> rmiResponse, SessionInformation sessionInformation) {
        if (predicate.apply(sessionInformation) == false) {
            return;
        }

        String coreSessionId = sessionInformation.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val);
        SessionInformation previousStoredSession = coreSessionIdToMap.get(coreSessionId);

        if (previousStoredSession != null) {
            if (sessionInformation.getCreationTime().after(previousStoredSession.getCreationTime())) {
                coreSessionIdToMap.put(coreSessionId, sessionInformation);
                sessionInformationToRMIResponse.remove(previousStoredSession);
                sessionInformationToRMIResponse.put(sessionInformation, rmiResponse);
            }
        } else {
            coreSessionIdToMap.put(coreSessionId, sessionInformation);
            sessionInformationToRMIResponse.put(sessionInformation, rmiResponse);
        }
    }


}
