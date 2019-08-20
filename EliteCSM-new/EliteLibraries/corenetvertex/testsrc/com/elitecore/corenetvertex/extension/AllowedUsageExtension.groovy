package com.elitecore.corenetvertex.extension

import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.Balance
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BalanceImpl
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage
import com.elitecore.corenetvertex.spr.NonMonetoryBalance

public class AllowedUsageExtension {
    public static Balance minus(final DailyAllowedUsage self, NonMonetoryBalance serviceRgBalance) {

        return new BalanceImpl(
                self.totalInBytes - serviceRgBalance.getDailyVolume()
                ,self.downloadInBytes - serviceRgBalance.getDailyVolume()
                ,self.uploadInBytes - serviceRgBalance.getDailyVolume()
                ,self.timeInSeconds- serviceRgBalance.getDailyTime()
        )
    }

    public static Balance minus(final WeeklyAllowedUsage self, NonMonetoryBalance serviceRgBalance) {

        return new BalanceImpl(
                self.totalInBytes - serviceRgBalance.getWeeklyVolume()
                ,self.downloadInBytes - serviceRgBalance.getWeeklyVolume()
                ,self.uploadInBytes - serviceRgBalance.getWeeklyVolume()
                ,self.timeInSeconds- serviceRgBalance.getWeeklyTime()
        )
    }

    public static minusTotalAndTime(final DailyAllowedUsage self, NonMonetoryBalance serviceRgBalance) {
        return new BalanceImpl(
                self.totalInBytes - serviceRgBalance.getDailyVolume()
                ,self.downloadInBytes
                ,self.uploadInBytes
                ,self.timeInSeconds- serviceRgBalance.getDailyTime()
        )
    }

    public static minusUploadAndTime(final DailyAllowedUsage self, NonMonetoryBalance serviceRgBalance) {
        return new BalanceImpl(
                self.totalInBytes
                ,self.downloadInBytes
                ,self.uploadInBytes - serviceRgBalance.getDailyVolume()
                ,self.timeInSeconds- serviceRgBalance.getDailyTime()
        )
    }

    public static minusDownloadAndTime(final DailyAllowedUsage self, NonMonetoryBalance serviceRgBalance) {
        return new BalanceImpl(
                self.totalInBytes
                ,self.downloadInBytes- serviceRgBalance.getDailyVolume()
                ,self.uploadInBytes
                ,self.timeInSeconds- serviceRgBalance.getDailyTime()
        )
    }

    public static minusDownloadAndTime(final WeeklyAllowedUsage self, NonMonetoryBalance serviceRgBalance) {
        return new BalanceImpl(
                self.totalInBytes
                ,self.downloadInBytes- serviceRgBalance.getWeeklyVolume()
                ,self.uploadInBytes
                ,self.timeInSeconds- serviceRgBalance.getWeeklyTime()
        )
    }

    public static minusTotalAndTime(final WeeklyAllowedUsage self, NonMonetoryBalance serviceRgBalance) {
        return new BalanceImpl(
                self.totalInBytes - serviceRgBalance.getWeeklyVolume()
                ,self.downloadInBytes
                ,self.uploadInBytes
                ,self.timeInSeconds- serviceRgBalance.getWeeklyTime()
        )
    }

    public static minusUploadAndTime(final WeeklyAllowedUsage self, NonMonetoryBalance serviceRgBalance) {
        return new BalanceImpl(
                self.totalInBytes
                ,self.downloadInBytes
                ,self.uploadInBytes - serviceRgBalance.getWeeklyVolume()
                ,self.timeInSeconds- serviceRgBalance.getWeeklyTime()
        )
    }

}
