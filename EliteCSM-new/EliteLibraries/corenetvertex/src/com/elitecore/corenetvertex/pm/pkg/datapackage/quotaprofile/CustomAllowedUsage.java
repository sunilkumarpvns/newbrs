package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

import java.util.Calendar;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by harsh on 8/10/17.
 */
public final class CustomAllowedUsage implements AllowedUsage {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "CUSTOM-ALLOWED-USAGE";
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
     refer DailyAllowedUsage
     */
    private final Balance balance;

    public CustomAllowedUsage(long total, long download, long upload, long time,
                              DataUnit totalUnit, DataUnit downloadUnit, DataUnit uploadUnit, TimeUnit timeUnit) {
        super();
        this.total = total;
        this.download = download;
        this.upload = upload;
        this.time = time;
        this.totalUnit = totalUnit;
        this.downloadUnit = downloadUnit;
        this.uploadUnit = uploadUnit;
        this.timeUnit = timeUnit;
        this.totalInBytes = totalUnit.toBytes(total);
        this.downloadInBytes = downloadUnit.toBytes(download);
        this.uploadInBytes = uploadUnit.toBytes(upload);
        this.timeInSeconds = timeUnit.toSeconds(time);
        balance = new BalanceImpl(totalInBytes, downloadInBytes, uploadInBytes, timeInSeconds);
    }

    @Override
    public Balance getBalance(SubscriberUsage subscriberUsage, Calendar currentTime) {
        if (subscriberUsage == null) {
            return balance;
        } else {
            if (subscriberUsage.getCustomResetTime() != 0 && currentTime.getTimeInMillis() > subscriberUsage.getCustomResetTime()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Considering subscriber custom usage as 0. Reason: Custom reset time("
                            + subscriberUsage.getCustomResetTime()
                            + ") have passed current time(" + currentTime.getTimeInMillis() + ")");
                }
                return balance;
            }
            return new BalanceImpl(totalInBytes - subscriberUsage.getCustomTotal(),
                    downloadInBytes - subscriberUsage.getCustomDownload(),
                    uploadInBytes - subscriberUsage.getCustomUpload(),
                    timeInSeconds - subscriberUsage.getCustomTime());
        }
    }

    @Override
    public Balance getBalance(NonMonetoryBalance nonMonetoryBalance, Calendar currentTime) {
        return ALWAYS_ALLOWED.getBalance(nonMonetoryBalance, currentTime);
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public long getDownload() {
        return download;
    }

    @Override
    public long getUpload() {
        return upload;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public DataUnit getTotalUnit() {
        return totalUnit;
    }

    @Override
    public DataUnit getDownloadUnit() {
        return downloadUnit;
    }

    @Override
    public DataUnit getUploadUnit() {
        return uploadUnit;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return timeUnit;
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

}
