package com.elitecore.netvertex.ws;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Collection;
import java.util.Iterator;

public class ReAuthorizedSessionMatcher extends TypeSafeDiagnosingMatcher<Collection<PCRFRequest>> {

    private SessionData sessionData;
    private String pcrfKeyToValidate;

    public ReAuthorizedSessionMatcher(SessionData sessionData, String pcrfKeyToValidate){
        this.sessionData = sessionData;
        this.pcrfKeyToValidate = pcrfKeyToValidate;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("session with core session id "+sessionData.getSessionId()+" is successfully reAuthorized");
    }

    @Override
    protected boolean matchesSafely(Collection<PCRFRequest> reAuthorizedRequests, Description mismatchDescription) {
        if(CollectionUtils.isEmpty(reAuthorizedRequests)){
          mismatchDescription.appendText("No request is re authorized");
         return false;
        }
        Iterator<PCRFRequest> iterator = reAuthorizedRequests.iterator();
        while (iterator.hasNext()) {
            PCRFRequest pcrfRequest = iterator.next();
            String coreSessionId = pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
            if (StringUtils.equalsIgnoreCase(sessionData.getValue(PCRFKeyConstants.CS_CORESESSION_ID.val), coreSessionId)) {
                if (sessionData.getValue(pcrfKeyToValidate).equals(pcrfRequest.getAttribute(pcrfKeyToValidate))) {
                    return true;
                }
            }
        }
        mismatchDescription.appendText("session with core session id "+sessionData.getSessionId()+" is not reAuthorized");
        return false;
    }
}
