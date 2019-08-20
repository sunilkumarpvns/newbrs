package com.elitecore.corenetvertex.spr;

public interface ExternalAlternateIdEDRListener {
    void addExternalAlternateIdEDR(String subscriberIdentity, String alternateIdentity, String operation);

    void updateExternalAlternateIdEDR(String subscriberIdentity, String oldAlternateIdentity, String updatedAlternateIdentity, String currentStatus, String updatedStatus, String operation);

    void removeExternalAlternateIdEDR(String subscriberIdentity, String alternateIdentity, String currentStatus, String operation);
}
