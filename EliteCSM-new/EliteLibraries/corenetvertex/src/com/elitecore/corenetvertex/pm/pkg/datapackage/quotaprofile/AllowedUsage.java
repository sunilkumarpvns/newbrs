package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by harsh on 8/10/17.
 */
public interface AllowedUsage extends Serializable {

    AllowedUsage ALWAYS_ALLOWED = new UnlimitedAllowedUsage();

    Balance getBalance(SubscriberUsage subscriberUsage, Calendar currentTime);
    Balance getBalance(NonMonetoryBalance serviceRgNonMonitoryBalance, Calendar currentTime);

    public abstract long getTimeInSeconds();

    public abstract long getUploadInBytes();

    public abstract long getDownloadInBytes();

    public abstract long getTotalInBytes();

    DataUnit getTotalUnit();

	DataUnit getUploadUnit();

	DataUnit getDownloadUnit();

	TimeUnit getTimeUnit();

	long getTotal();

	long getUpload();

	long getDownload();

	long getTime();

    static long convertToSeconds(long time, TimeUnit timeUnit) {
        return time == CommonConstants.QUOTA_UNLIMITED || time == CommonConstants.QUOTA_UNDEFINED 
        		? time : timeUnit.toSeconds(time);
    }

    static long convertToByte(long dataValue, DataUnit dataUnit) {
        return dataValue == CommonConstants.QUOTA_UNLIMITED || dataValue == CommonConstants.QUOTA_UNDEFINED
        		? dataValue : dataUnit.toBytes(dataValue);
    }


}
