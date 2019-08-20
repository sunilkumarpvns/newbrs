package com.elitecore.netvertex.rnc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ReportingReasonFinalReportHandlerTest.class,
        ReportingReasonNonFinalReportHandlerTest.class,
        CloseUnAccountedQuotaReportHandlerTest.class,
        CloseReserveQuotaReportHandlerTest.class,
        FinalReportedRateCardProcessorTest.class,
        NonFinalReportedRateCardProcessorTest.class,
        CloseUnAccountedRateCardReportHandlerTest.class,
        CloseReserveRateCardReportHandlerTest.class,
        ReportHandlerTest.class
})
public class ReportHandlerTestSuite {

}