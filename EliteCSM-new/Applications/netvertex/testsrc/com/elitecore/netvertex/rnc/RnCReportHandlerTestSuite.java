package com.elitecore.netvertex.rnc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ReportingReasonFinalRnCReportHandlerTest.class,
        ReportingReasonNonFinalRnCReportHandlerTest.class,
        CloseUnAccountedQuotaRnCReportHandlerTest.class,
        CloseReserveQuotaRnCReportHandlerTest.class,
        ReportingReasonFinalNonMonetaryRnCReportHandlerTest.class,
        ReportingReasonNonFinalNonMonetaryRnCReportHandlerTest.class,
        CloseUnAccountedQuotaNonMonetaryRnCReportHandlerTest.class,
        CloseReserveQuotaNonMonetaryRnCReportHandlerTest.class,
        RnCReportHandlerTest.class
})
public class RnCReportHandlerTestSuite {

}