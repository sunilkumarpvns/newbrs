package com.elitecore.netvertex.rnc;

import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;

public class ReportOperationTest {

    @Test
    public void AllReportingReasonMappedToReportOperation() {


        List<ReportOperation> unMappedAlerts = Arrays.stream(ReportingReason.values())
                .peek(alerts -> System.out.println("===== Start ====="))
                .peek(System.out::println)
                .map(ReportOperation::fromReportingReason)
                .peek(System.out::println)
                .filter(Objects::isNull)
                .peek(System.out::println)
                .peek(alerts -> System.out.println("===== End ====="))
                .collect(Collectors.toList());

        Assert.assertThat(unMappedAlerts.isEmpty(), is(true));

    }

}