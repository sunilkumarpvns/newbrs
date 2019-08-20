package com.elitecore.corenetvertex.sm.serverprofile;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.RollingType;
import com.elitecore.corenetvertex.constants.TimeBasedRollingUnit;

public class ServerProfileDataBuilder {
    public static ServerProfileData create() {
        ServerProfileData serverProfileData = new ServerProfileData();

        serverProfileData.setBatchSize(10);
        serverProfileData.setPcrfServiceQueueSize(100);
        serverProfileData.setPcrfServiceMaxThreads(2);
        serverProfileData.setPcrfServiceMinThreads(1);
        serverProfileData.setPcrfServiceWorkerThreadPriority(Thread.NORM_PRIORITY);

        serverProfileData.setDiameterQueueSize(100);
        serverProfileData.setPcrfServiceMinThreads(1);
        serverProfileData.setPcrfServiceMaxThreads(2);


        serverProfileData.setDiameterDwInterval(3);
        serverProfileData.setDiameterDuplicateReqCheckEnabled(true);
        serverProfileData.setDiameterDuplicateReqPurgeInterval(15);
        serverProfileData.setDiameterSessionCleanupInterval(10);
        serverProfileData.setDiameterSessionTimeout(100);


        serverProfileData.setRadiusQueueSize(100);
        serverProfileData.setRadiusMinThreads(1);
        serverProfileData.setRadiusMaxThreads(2);
        serverProfileData.setRadiusDuplicateReqCheckEnabled(true);
        serverProfileData.setRadiusDuplicateReqPurgeInterval(15);


        serverProfileData.setLogLevel(LogLevel.WARN.name());
        serverProfileData.setRollingType(RollingType.TIME_BASED.value);
        serverProfileData.setRollingUnits(TimeBasedRollingUnit.DAILY.value);
        serverProfileData.setMaxRolledUnits(2);

        serverProfileData.setNotificationServiceExecutionPeriod(1);
        serverProfileData.setMaxParallelExecution(2);
        serverProfileData.setBatchSize(2);

        return serverProfileData;
    }
}
