package com.elitecore.corenetvertex.pkg.rnc;

import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import org.apache.commons.lang.math.RandomUtils;

public class RncProfileDetailDataFactory {

    public static final long USAGE_LIMIT = 10L;
    public static final String BALANCE_UNIT = "MB";
    public static final String TIME_BALANCE_UNIT = "MINUTE";

    public static RncProfileDetailData create() {
            RncProfileDetailData rncProfileDetailData = new RncProfileDetailData();

            rncProfileDetailData.setDailyTimeLimit(USAGE_LIMIT);
            rncProfileDetailData.setDailyUsageLimit(USAGE_LIMIT);

            rncProfileDetailData.setWeeklyTimeLimit(USAGE_LIMIT);
            rncProfileDetailData.setWeeklyUsageLimit(USAGE_LIMIT);

            rncProfileDetailData.setPulseTime(RandomUtils.nextLong());
            rncProfileDetailData.setPulseTimeUnit(TimeUnit.SECOND.name());

            rncProfileDetailData.setPulseVolumeUnit(DataUnit.MB.name());
            rncProfileDetailData.setBalance(USAGE_LIMIT);
            rncProfileDetailData.setBalanceUnit(BALANCE_UNIT);
            rncProfileDetailData.setUsageLimitUnit(DataUnit.MB.name());

            rncProfileDetailData.setTimeBalance(10L);
            rncProfileDetailData.setTimeBalanceUnit(TIME_BALANCE_UNIT);

            return rncProfileDetailData;
        }

}