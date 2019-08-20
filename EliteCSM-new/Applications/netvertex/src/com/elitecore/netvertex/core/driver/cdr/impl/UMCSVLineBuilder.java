package com.elitecore.netvertex.core.driver.cdr.impl;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.core.driverx.cdr.deprecated.CSVLineBuilderSupport;
import com.elitecore.netvertex.core.driver.cdr.ValueProviderExtImpl;
import com.elitecore.netvertex.core.driver.cdr.conf.CSVDriverConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.usagemetering.ServiceUnit;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

public class UMCSVLineBuilder extends CSVLineBuilderSupport<ValueProviderExtImpl> {
    private static final String MODULE = "UM-LINE-BUILDER";
    @Nonnull
    private CSVDriverConfiguration csvDriverConf;

    public UMCSVLineBuilder(@Nonnull CSVDriverConfiguration csvDriverConf, @Nonnull TimeSource timeSource){
        super(csvDriverConf.getDelimiter(), csvDriverConf.getStripMappings(), csvDriverConf.getCDRTimeStampFormat(), timeSource);
        this.csvDriverConf = csvDriverConf;
    }

    @Override
    public List<String> getCSVRecords(ValueProviderExtImpl valueProvider) {
        PCRFResponse response = valueProvider.getResponse();
        if(response.getReportedUsageInfoList() == null) {
            if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                LogManager.getLogger().debug(MODULE, "Skipping CSV CDR for this response. Reason: Usage Monitoring info not found");
            return Collections.emptyList();
        }

        List<String> records = new ArrayList<String>();
        for (UsageMonitoringInfo monitoringInfo : response.getReportedUsageInfoList()) {
            StringBuilder record = new StringBuilder(100);
            for(CSVFieldMapping mapping : csvDriverConf.getCDRFieldMappings())
                appendValue(record, getStrippedValue(mapping.getKey(), response.getAttribute(mapping.getKey())));

            appendServiceUnit(monitoringInfo.getUsedServiceUnit(), record, monitoringInfo.getMonitoringKey());
            appendTimestamp(record);
            records.add(record.toString());
        }
        return records;
    }

    private void appendServiceUnit(ServiceUnit serviceUnit, StringBuilder record, String ratingGroupIdentifier) {

        if(isNull(serviceUnit)){
            appendValue(record,null);
            appendValue(record,null);
        }else{
            if(isNull(ratingGroupIdentifier)){
                appendValue(record,null);
            }else{
                appendValue(record,ratingGroupIdentifier);
            }

            if(isNull(serviceUnit.getInputOctets())){
                appendValue(record,null);
            }else{
                appendValue(record,Long.toString(serviceUnit.getInputOctets()));
            }

            if(isNull(serviceUnit.getOutputOctets())){
                appendValue(record,null);
            }else{
                appendValue(record,Long.toString(serviceUnit.getOutputOctets()));
            }

            if(isNull(serviceUnit.getTotalOctets())){
                appendValue(record,null);
            }else{
                appendValue(record,Long.toString(serviceUnit.getTotalOctets()));
            }

            if(isNull(serviceUnit.getTime())){
                appendValue(record,null);
            }else{
                appendValue(record,Long.toString(serviceUnit.getTime()));
            }
        }
    }
}
