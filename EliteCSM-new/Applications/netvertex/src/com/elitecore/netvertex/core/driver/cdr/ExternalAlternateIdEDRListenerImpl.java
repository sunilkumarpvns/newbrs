package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.corenetvertex.spr.ExternalAlternateIdEDRListener;

public class ExternalAlternateIdEDRListenerImpl implements ExternalAlternateIdEDRListener {
    @Override
    public void addExternalAlternateIdEDR(String subscriberIdentity, String alternateIdentity, String operation) {
        //Blank Implementation
    }

    @Override
    public void updateExternalAlternateIdEDR(String subscriberIdentity, String oldAlternateIdentity, String updatedAlternateIdentity, String currentStatus, String updatedStatus, String operation) {
        //Blank Implementation
    }

    @Override
    public void removeExternalAlternateIdEDR(String subscriberIdentity, String alternateIdentity, String currentstatus, String operation) {
        //Blank Implementation
    }
}
