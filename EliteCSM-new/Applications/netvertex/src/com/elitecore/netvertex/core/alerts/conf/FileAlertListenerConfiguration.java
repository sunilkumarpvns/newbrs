package com.elitecore.netvertex.core.alerts.conf;


import com.elitecore.corenetvertex.constants.RollingType;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.List;

public class FileAlertListenerConfiguration implements ToStringable {


    private final String listenerId;
    private final String fileName;
    private final int maxRollingUnit;
    private final int rollingType;
    private final int rollingUnit;
    private final boolean isCompRollingUnit;
    private final String listenerName;
    private final List<AlertsConfiguration> alertConfigurations;

    public FileAlertListenerConfiguration(String listenerId,
                                          String fileName,
                                          int maxRollingUnit,
                                          int rollingType,
                                          int rollingUnit,
                                          boolean isCompRollingUnit,
                                          String listenerName,
                                          List<AlertsConfiguration> alertConfigurations) {
        this.listenerId = listenerId;
        this.fileName = fileName;
        this.maxRollingUnit = maxRollingUnit;
        this.rollingType = rollingType;
        this.rollingUnit = rollingUnit;
        this.isCompRollingUnit = isCompRollingUnit;
        this.listenerName = listenerName;
        this.alertConfigurations = alertConfigurations;
    }

    public String getName() {
        return listenerName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getListenerId() {
        return listenerId;
    }

    public int getMaxRollingUnit() {
        return maxRollingUnit;
    }

    public int getRollingType() {
        return rollingType;
    }

    public int getRollingUnit() {
        return rollingUnit;
    }

    public boolean isCompRollingUnit() {
        return isCompRollingUnit;
    }

    public List<AlertsConfiguration> getAlertConfigurations() {
        return alertConfigurations;
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.appendHeading("File Alert Listener(" + listenerName + ") (" + listenerId + ")");
        builder.incrementIndentation();
        builder.append("File Name", fileName);
        builder.append("Rolling Type", RollingType.fromValue(rollingType));
        builder.append("Rolling Unit", rollingUnit);
        builder.append("Max Rolling Units", maxRollingUnit);
        builder.append("Compressed Rolling Unit", Boolean.toString(isCompRollingUnit).toUpperCase());
        builder.incrementIndentation();
        builder.appendChildObject("Alerts", alertConfigurations);
        builder.decrementIndentation();
        builder.decrementIndentation();
    }
}