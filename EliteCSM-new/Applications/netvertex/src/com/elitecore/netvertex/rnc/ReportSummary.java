package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.ArrayList;
import java.util.List;

public class ReportSummary implements ToStringable{
    private List<ReportedUsageSummary> reportedUsageSummaries;


    public ReportSummary() {
        reportedUsageSummaries = new ArrayList<>();
    }

    public void add(ReportedUsageSummary reportedUsageSummary) {
        this.reportedUsageSummaries.add(reportedUsageSummary);
    }

    public List<ReportedUsageSummary> getReportedUsageSummaries() {
        return reportedUsageSummaries;
    }

    @Override
    public String toString() {
        IndentingToStringBuilder toStringBuilder =  new IndentingToStringBuilder() ;

        toStringBuilder.appendHeading(" -- REPORT SUMMARY -- ");

        toString(toStringBuilder);

        return toStringBuilder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder toStringBuilder) {
        toStringBuilder.appendChildObject("REPORTED USAGE SUMMARY", reportedUsageSummaries);
    }
}
