package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.netvertex.core.driver.cdr.conf.CSVDriverConfiguration;
import com.elitecore.netvertex.rnc.ReportOperation;
import com.elitecore.netvertex.rnc.ReportedUsageSummary;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class MsccCsvLineBuilder extends CSVLineBuilderSupport<ValueProviderExtImpl>{

    private static final String MODULE = "MSCC-LINE-BUILDER";
    private List<BiFunction<ReportedUsageSummary, PCRFResponse, String>> headerValueProviders;

    public MsccCsvLineBuilder(@Nonnull CSVDriverConfiguration csvDriverConf, @Nonnull TimeSource timeSource, List<BiFunction<ReportedUsageSummary, PCRFResponse, String>> headerValueProviders){
        super(csvDriverConf.getDelimiter(), csvDriverConf.getStripMappings(), csvDriverConf.getCDRTimeStampFormat(), timeSource);
        this.headerValueProviders = headerValueProviders;
    }

    @Override
    public @Nonnull List<String> getCSVRecords(@Nonnull ValueProviderExtImpl valueProvider) {

        PCRFResponse response = valueProvider.getResponse();

        if(Objects.isNull(response.getReportSummary())) {
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, "Skipping CSV CDR for this request. Reason: Report Summary Not found from response");
            return Collections.emptyList();
        }

        List<ReportedUsageSummary> reportedUsageSummaries = response.getReportSummary().getReportedUsageSummaries();
        List<String> records = new ArrayList<>(reportedUsageSummaries.size());

        for (ReportedUsageSummary reportedMSCC : reportedUsageSummaries) {

            if(reportedMSCC.getReportOperation() == ReportOperation.CLOSE_RESERVATION) {
                continue;
            }

            StringBuilder record = new StringBuilder(100);

            headerValueProviders.forEach(headerValueProvider -> this.appendValue(record, headerValueProvider.apply(reportedMSCC, response)));
            appendTimestamp(record);
            records.add(record.toString());
        }
        return records;
    }
}
