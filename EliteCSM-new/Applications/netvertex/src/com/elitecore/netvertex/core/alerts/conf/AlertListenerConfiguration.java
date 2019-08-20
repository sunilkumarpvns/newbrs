package com.elitecore.netvertex.core.alerts.conf;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.List;

public class AlertListenerConfiguration implements ToStringable{

    private final List<TrapAlertListenerConfiguration> trapAlertListenerConfigurations;
    private final List<FileAlertListenerConfiguration> fileAlertListenerConfigurations;

    public AlertListenerConfiguration(List<TrapAlertListenerConfiguration> trapAlertListenerConfigurations,
                                      List<FileAlertListenerConfiguration> fileAlertListenerConfigurations) {
        this.trapAlertListenerConfigurations = trapAlertListenerConfigurations;
        this.fileAlertListenerConfigurations = fileAlertListenerConfigurations;
    }

    public List<FileAlertListenerConfiguration> getFileAlertListenerConfigurations() {
        return fileAlertListenerConfigurations;
    }

    public List<TrapAlertListenerConfiguration> getTrapAlertListenerConfigurations() {
        return trapAlertListenerConfigurations;
    }

    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        toString(builder);
        return builder.toString();
    }

    public void toString(IndentingToStringBuilder builder) {

        builder.appendHeading("-- Alert Configuration --");

        builder.incrementIndentation();
        builder.appendChildObject("File Alert Configurations", fileAlertListenerConfigurations);
        builder.appendChildObject("Trap Alert Configurations", trapAlertListenerConfigurations);
        builder.decrementIndentation();

    }
}
