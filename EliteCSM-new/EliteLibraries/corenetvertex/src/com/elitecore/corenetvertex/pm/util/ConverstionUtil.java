package com.elitecore.corenetvertex.pm.util;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;

public class ConverstionUtil {

    public static long getQoSInBps(long qos, String qosUnit) {

        if (qos == 0) {
            return qos;
        }

        QoSUnit unit = QoSUnit.fromVal(qosUnit);
        long bps;
        if (unit != null) {
            bps = unit.toBps(qos);
        } else {
            bps = qos;
        }

		/*
		 * while calculating bits per seconds, it may possible that bps cross the max value long value.
		 * In that case it value will be less than zero
		 */
        if (bps > CommonConstants.UNSIGNED32_MAX_VALUE) {
            bps = CommonConstants.UNSIGNED32_MAX_VALUE;
        }

        return bps;
    }

    public static long getDataInBytes(Long dataQuantity, String dataUnit) {

        if (dataQuantity == null) {
            return 0;
        }

        DataUnit usageUnit = DataUnit.valueOf(dataUnit);

        if (usageUnit != null) {
            return usageUnit.toBytes(dataQuantity);
        }

        return dataQuantity;
    }

    public static long getTimeInSeconds(Long time, String timeUnitStr) {

        if (time == null) {
            return 0;
        }

        TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr);

        if (timeUnit != null) {
            return timeUnit.toSeconds(time);
        }

        return time;
    }
}
