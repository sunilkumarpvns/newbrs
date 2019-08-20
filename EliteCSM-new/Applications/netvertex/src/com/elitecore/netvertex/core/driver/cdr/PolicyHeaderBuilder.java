package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.driverx.cdr.deprecated.CSVFieldMapping;
import com.elitecore.netvertex.core.driver.cdr.conf.CSVDriverConfiguration;

import javax.annotation.Nonnull;

public class PolicyHeaderBuilder {

    private static final String CDRTIMESTAMP = "CDRTIMESTAMP";
    private CSVDriverConfiguration csvDriverConf;
    private String delimiter;
    private String csvHeaderLine;

    public PolicyHeaderBuilder(@Nonnull CSVDriverConfiguration csvDriverConf){

        this.csvDriverConf = csvDriverConf;
        delimiter = csvDriverConf.getDelimiter();
    }

    public String getCSVHeaderLine() {
        if (csvHeaderLine == null) {
            csvHeaderLine = createCSVHeaderLine();
        }
        return csvHeaderLine;
    }

    private String createCSVHeaderLine() {
        StringBuilder csvHeaderLine = new StringBuilder();
        for (CSVFieldMapping mapping : csvDriverConf.getCDRFieldMappings())
            csvHeaderLine.append(mapping.getHeaderField()).append(delimiter);

        csvHeaderLine.append(getUsageHeader());
        if (csvDriverConf.getCDRTimeStampFormat() != null) {
            csvHeaderLine.append(CDRTIMESTAMP);
        } else {
            csvHeaderLine.delete(csvHeaderLine.length() - csvDriverConf.getDelimiter().length(), csvHeaderLine.length());
        }
        return csvHeaderLine.toString();
    }

    private String getUsageHeader() {
        StringBuilder csvHeaderLine = new StringBuilder();

        if(csvDriverConf.getReportingType() == null) {
            return csvHeaderLine.toString();
        }

        if(csvDriverConf.getUsageKeyHeader() != null) {
            csvHeaderLine.append(csvDriverConf.getUsageKeyHeader()).append(delimiter);
        }

        if(csvDriverConf.getInputOctetsHeader() != null) {
            csvHeaderLine.append(csvDriverConf.getInputOctetsHeader()).append(delimiter);
        }

        if(csvDriverConf.getOutputOctetsHeader() != null) {
            csvHeaderLine.append(csvDriverConf.getOutputOctetsHeader()).append(delimiter);
        }

        if(csvDriverConf.getTotalOctetsHeader() != null) {
            csvHeaderLine.append(csvDriverConf.getTotalOctetsHeader()).append(delimiter);
        }

        if(csvDriverConf.getUsageTimeHeader() != null) {
            csvHeaderLine.append(csvDriverConf.getUsageTimeHeader()).append(delimiter);
        }

        return csvHeaderLine.toString();
    }
}
