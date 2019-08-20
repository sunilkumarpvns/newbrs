package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

import java.util.Calendar;

public class UnlimitedAllowedUsage implements AllowedUsage {

    private static final long serialVersionUID = 1L;

    @Override
    public long getTimeInSeconds() {
        return CommonConstants.QUOTA_UNLIMITED;
    }

    @Override
    public long getUploadInBytes() {
        return CommonConstants.QUOTA_UNLIMITED;
    }

    @Override
    public long getDownloadInBytes() {
        return CommonConstants.QUOTA_UNLIMITED;
    }

    @Override
    public long getTotalInBytes() {
        return CommonConstants.QUOTA_UNLIMITED;
    }

    @Override
    public Balance getBalance(SubscriberUsage subscriberUsage, Calendar currentTime) {
        return Balance.UNLIMITED;
    }

    @Override
    public Balance getBalance(NonMonetoryBalance subscriberUsage, Calendar currentTime) {
        return Balance.UNLIMITED;
    }

    @Override
    public DataUnit getTotalUnit() {
        return DataUnit.BYTE;
    }

    @Override
    public DataUnit getUploadUnit() {
        return DataUnit.BYTE;
    }

    @Override
    public DataUnit getDownloadUnit() {
        return DataUnit.BYTE;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.SECOND;
    }

    @Override
    public long getTotal() {
        return CommonConstants.QUOTA_UNLIMITED;
    }

    @Override
    public long getUpload() {
        return CommonConstants.QUOTA_UNLIMITED;
    }

    @Override
    public long getDownload() {
        return CommonConstants.QUOTA_UNLIMITED;
    }

    @Override
    public long getTime() {
        return CommonConstants.QUOTA_UNLIMITED;
    }
}
