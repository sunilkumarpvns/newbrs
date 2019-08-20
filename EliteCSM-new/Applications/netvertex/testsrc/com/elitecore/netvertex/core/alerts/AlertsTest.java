package com.elitecore.netvertex.core.alerts;

import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;

public class AlertsTest {



    @Test
    public void AllCoreNetvertexLeafAlertsMappedToNetVertexAlert() {


        List<com.elitecore.netvertex.core.alerts.Alerts> unMappedAlerts = Arrays.stream(Alerts.values())
                .filter(alerts -> alerts.getType().equalsIgnoreCase("L"))
                .peek(alerts -> System.out.println("===== Start ====="))
                .peek(System.out::println)
                .map(com.elitecore.netvertex.core.alerts.Alerts::fromCoreNetvertexAlert)
                .peek(System.out::println)
                .filter(Objects::isNull)
                .peek(System.out::println)
                .peek(alerts -> System.out.println("===== End ====="))
                .collect(Collectors.toList());

        Assert.assertThat(unMappedAlerts.isEmpty(), is(true));

    }

    @Test
    public void AllCoreNetvertexParentAlertsShouldNotBeMappedToNetVertexAlert() {


        List<com.elitecore.netvertex.core.alerts.Alerts> unMappedAlerts = Arrays.stream(Alerts.values())
                .filter(alerts -> alerts.getType().equalsIgnoreCase("P"))
                .peek(alerts -> System.out.println("===== Start ====="))
                .peek(System.out::println)
                .map(com.elitecore.netvertex.core.alerts.Alerts::fromCoreNetvertexAlert)
                .peek(System.out::println)
                .filter(Objects::nonNull)
                .peek(System.out::println)
                .peek(alerts -> System.out.println("===== End ====="))
                .collect(Collectors.toList());

        Assert.assertThat(unMappedAlerts.isEmpty(), is(true));

    }

    @Test
    public void AllDiameterAlertsShouldBeMappedToNetVertexAlert() {


        List<com.elitecore.netvertex.core.alerts.Alerts> unMappedAlerts = Arrays.stream(DiameterStackAlerts.values())
                .peek(alerts -> System.out.println("===== Start ====="))
                .peek(System.out::println)
                .map(com.elitecore.netvertex.core.alerts.Alerts::fromStackAlert)
                .peek(System.out::println)
                .filter(Objects::isNull)
                .peek(System.out::println)
                .peek(alerts -> System.out.println("===== End ====="))
                .collect(Collectors.toList());

        Assert.assertThat(unMappedAlerts.isEmpty(), is(true));

    }

    @Test
    public void AllNetVertexAlertMappedtoCoreNetvertexLeafAlerts() {

        List<Alerts> unMappedAlerts = Arrays.stream(com.elitecore.netvertex.core.alerts.Alerts.values())
                .peek(alerts -> System.out.println("===== Start ====="))
                .peek(System.out::println)
                .map(com.elitecore.netvertex.core.alerts.Alerts::getCoreNetvertexAlert)
                .peek(System.out::println)
                .filter(Objects::isNull)
                .peek(System.out::println)
                .peek(alerts -> System.out.println("===== End ====="))
                .collect(Collectors.toList());

        Assert.assertThat(unMappedAlerts.isEmpty(), is(true));

    }

}