package com.elitecore.netvertex.core.util;

import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.commons.Predicate;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by harsh on 10/19/16.
 */
public class InterimIntervalPredicate implements Predicate<PCRFRequest, SessionData>{

    private static final String MODULE = "INTERIM-INTERVAL-PREDICATE";
    private final long interimIntervalInMillis;

    public InterimIntervalPredicate(int interimInterval) {

        /*
            add delta of 5 min in interim interval. if interim interval is less than 5 than delta will be 1 min
         */
        if(interimInterval > 5) {
            this.interimIntervalInMillis = TimeUnit.MINUTES.toMillis(interimInterval + 5l);
        } else {
            this.interimIntervalInMillis = TimeUnit.MINUTES.toMillis(interimInterval + 1l);
        }
    }

    @Override
    public boolean apply(PCRFRequest pcrfRequest, SessionData sessionData) {

        if (pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_UPDATE) ==  false && pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP) ==  false ) {
            return true;
        }

        long expectedIntervalInMillis = sessionData.getLastUpdateTime().getTime() + interimIntervalInMillis;

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Expected Interim received Time is " + new Timestamp(expectedIntervalInMillis)
                    + "(" + expectedIntervalInMillis + ") and request received time is "
                    + new Timestamp(pcrfRequest.creationTimeMillis())
                    + "(" + pcrfRequest.creationTimeMillis() + ")" );
        }

        return expectedIntervalInMillis > pcrfRequest.creationTimeMillis();


    }
}
