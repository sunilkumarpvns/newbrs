package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

import java.util.Calendar;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage.convertToByte;
import static com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage.convertToSeconds;

/**
 * Created by harsh on 8/10/17.
 */
public final class DailyAllowedUsage implements AllowedUsage {

    private static final long serialVersionUID = 1L;
	private static final String MODULE = "DAILY-ALLOWED-USAGE";
	private final long total;
    private final long download;
    private final long upload;
    private final long time;
    private final DataUnit totalUnit;
    private final DataUnit downloadUnit;
    private final DataUnit uploadUnit;
    private final TimeUnit timeUnit;
    private final long totalInBytes;
    private final long downloadInBytes;
    private final long uploadInBytes;
    private final long timeInSeconds;
    /*
         Keeps initial allowed usage in balance,
        This balance used in below scenario:
             - when subscriberUsage not exist(on first request of USAGE_REPORT)
             - when reset time lapsed
     */
    private final Balance balance;

    public DailyAllowedUsage(long total, long download, long upload, long time,
                             DataUnit totalUnit, DataUnit downloadUnit, DataUnit uploadUnit, TimeUnit timeUnit) {
        this.total = total;
        this.download = download;
        this.upload = upload;
        this.time = time;
        this.totalUnit = totalUnit;
        this.downloadUnit = downloadUnit;
        this.uploadUnit = uploadUnit;
        this.timeUnit = timeUnit;
        this.totalInBytes = convertToByte(total, totalUnit);
        this.downloadInBytes = convertToByte(download, downloadUnit);
        this.uploadInBytes = convertToByte(upload, uploadUnit);
        this.timeInSeconds = convertToSeconds(time, timeUnit);
        balance = new BalanceImpl(totalInBytes, downloadInBytes, uploadInBytes, timeInSeconds);
    }



    @Override
    public long getTotalInBytes() {
        return totalInBytes;
    }

    @Override
    public long getDownloadInBytes() {
        return downloadInBytes;
    }

    @Override
    public long getUploadInBytes() {
        return uploadInBytes;
    }

    @Override
    public long getTimeInSeconds() {
        return timeInSeconds;
    }

    @Override
    public Balance getBalance(SubscriberUsage subscriberUsage, Calendar currentTime) {
        if (subscriberUsage == null) {
            return balance;
        } else {

            // When UM info is received with having reset time lower then
            // current time, reset time will be set to 0.
            if (subscriberUsage.getDailyResetTime() != 0 && currentTime.getTimeInMillis() > subscriberUsage.getDailyResetTime()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Considering subscriber daily usage as 0. Reason: Daily reset time("
                            + subscriberUsage.getDailyResetTime()
                            + ") have passed current time(" + currentTime.getTimeInMillis() + ")");
                }
                return balance;
            }

            return new BalanceImpl(totalInBytes - subscriberUsage.getDailyTotal(),
                    downloadInBytes - subscriberUsage.getDailyDownload(),
                    uploadInBytes - subscriberUsage.getDailyUpload(),
                    timeInSeconds - subscriberUsage.getDailyTime());
        }
    }

    @Override
    public Balance getBalance(NonMonetoryBalance nonMonetoryBalance, Calendar currentTime) {
        if (nonMonetoryBalance == null) {
            return balance;
        } else {

            // When Balance info is received with having reset time lower then
            // current time, reset time will be set to 0.
            if (nonMonetoryBalance.getDailyResetTime() != 0 && currentTime.getTimeInMillis() > nonMonetoryBalance.getDailyResetTime()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Considering subscriber daily usage as 0. Reason: Daily reset time("
                            + nonMonetoryBalance.getDailyResetTime()
                            + ") have passed current time(" + currentTime.getTimeInMillis() + ")");
                }
                return balance;
            }

            long total = totalInBytes;
            long upload = uploadInBytes;
            long download = downloadInBytes;
            long time = timeInSeconds;
            if(totalInBytes != CommonConstants.QUOTA_UNDEFINED) {
                total = totalInBytes - nonMonetoryBalance.getDailyVolume();
            } else if(download != CommonConstants.QUOTA_UNDEFINED) {
                download = downloadInBytes - nonMonetoryBalance.getDailyVolume();
            } else {
                upload = uploadInBytes - nonMonetoryBalance.getDailyVolume();
            }

            if(timeInSeconds != CommonConstants.QUOTA_UNDEFINED) {
                time = timeInSeconds - nonMonetoryBalance.getDailyTime();
            }

            return new BalanceImpl(total, download, upload, time);
        }
    }


    @Override
    public DataUnit getTotalUnit() {
        return totalUnit;
    }

    @Override
    public DataUnit getUploadUnit() {
        return uploadUnit;
    }

    @Override
    public DataUnit getDownloadUnit() {
        return downloadUnit;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public long getUpload() {
        return upload;
    }

    @Override
    public long getDownload() {
        return download;
    }

    @Override
    public long getTime() {
        return time;
    }

}
