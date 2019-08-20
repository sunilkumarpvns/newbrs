package com.elitecore.netvertex.rnc;

import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;

public enum  ReportOperation {

    CLOSE_UNACCOUNTED_QUOTA,
    CLOSE_RESERVATION,
    THRESHOLD,
    QHT,
    FINAL,
    QUOTA_EXHAUSTED,
    VALIDITY_TIME,
    OTHER_QUOTA_TYPE,
    RATING_CONDITION_CHANGE,
    FORCED_REAUTHORISATION,
    POOL_EXHAUSTED,
    EVENT;

    public static ReportOperation fromReportingReason(ReportingReason reportingReason) {
        switch (reportingReason) {
            case THRESHOLD:
                return THRESHOLD;
            case QHT:
                return QHT;
            case FINAL:
                return FINAL;
            case QUOTA_EXHAUSTED:
                return QUOTA_EXHAUSTED;
            case VALIDITY_TIME:
                return VALIDITY_TIME;
            case OTHER_QUOTA_TYPE:
                return OTHER_QUOTA_TYPE;
            case RATING_CONDITION_CHANGE:
                return RATING_CONDITION_CHANGE;
            case FORCED_REAUTHORISATION:
                return FORCED_REAUTHORISATION;
            case POOL_EXHAUSTED:
                return POOL_EXHAUSTED;
            default:
                throw new IllegalArgumentException(reportingReason + " not match with existing report operation");

        }
    }
}
