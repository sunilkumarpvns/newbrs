package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;

import java.io.StringWriter;
import java.util.Calendar;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage.convertToByte;
import static com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage.convertToSeconds;

/**
 * Created by harsh on 8/10/17.
 */
public final class BillingCycleAllowedUsage implements AllowedUsage {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = "BILLING-CYCLE-ALLOWED-USAGE";
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
     refer #DailyAllowedUsage
     */
    private Balance balance;

    public BillingCycleAllowedUsage(long total,
                                    long download,
                                    long upload,
                                    long time,
                                    DataUnit totalUnit,
                                    DataUnit downloadUnit,
                                    DataUnit uploadUnit,
                                    TimeUnit timeUnit) {
        super();
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

		balance = new BillingCycleBalance(totalInBytes, downloadInBytes, uploadInBytes, timeInSeconds);
    }

    @Override
    public Balance getBalance(SubscriberUsage subscriberUsage, Calendar currentTime) {
        Preconditions.checkNotNull(currentTime, "current time is null");

        if (subscriberUsage == null) {
            return balance;
        } else {
            if (subscriberUsage.getBillingCycleResetTime() != 0 && currentTime.getTimeInMillis() > subscriberUsage.getBillingCycleResetTime()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Considering subscriber billing cycle usage as 0. Reason: Billing cycle reset time("
                            + subscriberUsage.getBillingCycleResetTime()
                            + ") have passed current time(" + currentTime.getTimeInMillis() + ")");
                }
                return balance;
            }
            return new BalanceImpl(totalInBytes - subscriberUsage.getBillingCycleTotal(),
                    downloadInBytes - subscriberUsage.getBillingCycleDownload(),
                    uploadInBytes - subscriberUsage.getBillingCycleUpload(),
                    timeInSeconds - subscriberUsage.getBillingCycleTime());
        }
    }

    @Override
    public Balance getBalance(NonMonetoryBalance nonMonetoryBalance, Calendar currentTime) {
        Preconditions.checkNotNull(currentTime, "Current time is null");

        if (nonMonetoryBalance == null) {
            return balance;
        } else {

            if (currentTime.getTimeInMillis() > nonMonetoryBalance.getBillingCycleResetTime()) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Considering subscriber billing cycle usage as 0. Reason: Billing cycle reset time("
                            + nonMonetoryBalance.getBillingCycleResetTime()
                            + ") have passed current time(" + currentTime.getTimeInMillis() + ")");
                }
                return balance;
            }

            long totalBytes = totalInBytes;
            long uploadedBytes = uploadInBytes;
            long downloadedBytes = downloadInBytes;
            long timeInSec = timeInSeconds;
            if(totalInBytes != CommonConstants.QUOTA_UNDEFINED) {
                totalBytes = nonMonetoryBalance.getBillingCycleAvailableVolume() - nonMonetoryBalance.getReservationVolume();
            } else if(downloadedBytes != CommonConstants.QUOTA_UNDEFINED) {
                downloadedBytes = nonMonetoryBalance.getBillingCycleAvailableVolume() - nonMonetoryBalance.getReservationVolume();
            } else {
                uploadedBytes = nonMonetoryBalance.getBillingCycleAvailableVolume() - nonMonetoryBalance.getReservationVolume();
            }

            if(timeInSeconds != CommonConstants.QUOTA_UNDEFINED) {
                timeInSec = nonMonetoryBalance.getBillingCycleAvailableTime() - nonMonetoryBalance.getReservationTime();
            }


            return new BillingCycleBalance(totalBytes,
                    downloadedBytes,
                    uploadedBytes,
                    timeInSec);
        }
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

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingPrintWriter printWriter = new IndentingPrintWriter(stringWriter);
        toString(printWriter);
        return stringWriter.toString();
    }

    public void toString(IndentingPrintWriter stringWriter) {
        stringWriter.print("Billing-Cycle-Allowed-Usage[");
        stringWriter.print("Total:");
        stringWriter.print(total);
        stringWriter.print(totalUnit);

        stringWriter.print(", Download:");
        stringWriter.print(download);
        stringWriter.print(downloadUnit);

        stringWriter.print(", Upload:");
        stringWriter.print(upload);
        stringWriter.print(uploadUnit);

        stringWriter.print(", Time:");
        stringWriter.print(time);
        stringWriter.print(timeUnit);
        stringWriter.print("]");
    }

}
