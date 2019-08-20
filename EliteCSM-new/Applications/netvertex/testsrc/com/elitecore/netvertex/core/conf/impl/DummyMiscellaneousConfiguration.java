package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.service.offlinernc.util.RoundingModeTypes;

public class DummyMiscellaneousConfiguration implements MiscellaneousConfiguration {

    private boolean sprCacheEnabled;

    @Override
    public boolean getRAREnabled() {
        return false;
    }

    @Override
    public boolean getAddOnStartRAREnabled() {
        return false;
    }

    @Override
    public boolean getAddOnExpiryRAREnabled() {
        return false;
    }

    @Override
    public boolean getAddOnReservationEnabled() {
        return false;
    }

    @Override
    public boolean getSessionNoWait() {
        return false;
    }

    @Override
    public boolean getSessionBatch() {
        return false;
    }

    @Override
    public int getResultCodeOnOverload() {
        return 0;
    }

    @Override
    public int getRecordReservationLimit() {
        return 0;
    }

    @Override
    public String getParameterValue(String parameterName) {
        return null;
    }

    @Override
    public boolean isSessionCacheEnabled() {
        return false;
    }

    public void setSprCacheEnabled(boolean sprCacheEnabled){
        this.sprCacheEnabled = sprCacheEnabled;
    }

    @Override
    public boolean isSPRCacheEnabled() {
        return this.sprCacheEnabled;
    }

    @Override
    public int getCOADelay() {
        return 0;
    }

    @Override
    public String getCoresessionCoresessionId() {
        return null;
    }

    @Override
    public String getSessionRuleCoresessionId() {
        return null;
    }

    @Override
    public String getSessionRuleCoresessionIdAndPccrule() {
        return null;
    }

    @Override
    public String getCoresessionSubscriberIdentity() {
        return null;
    }

    @Override
    public boolean getServerInitiatedDestinationHost() {
        return false;
    }

    @Override
    public boolean isEPSQoSRoundingEnable() {
        return false;
    }

    @Override
    public boolean getSLREnabledOnSNR() {
        return false;
    }

    @Override
    public int getRatingDecimalPoints() {
        return 0;
    }

    @Override
    public RoundingModeTypes getRoundingMode() {
        return null;
    }
}
