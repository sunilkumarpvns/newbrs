package com.elitecore.corenetvertex.sm.serverprofile;

import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;

public class ServerInstanceDataBuilder {

    public static ServerInstanceData create() {
        ServerInstanceData serverInstanceData = new ServerInstanceData();


        serverInstanceData.setName("test");
        serverInstanceData.setJmxUrl("0.0.0.0:3454");
        serverInstanceData.setDescription("test");
        serverInstanceData.setJavaHome("javahome");
        serverInstanceData.setServerHome("serverhome");
        serverInstanceData.setRadiusUrl("0.0.0.0:9000");
        serverInstanceData.setSnmpUrl("0.0.0.0:1191");


        serverInstanceData.setDiameterEnabled(true);
        serverInstanceData.setDiameterUrl("0.0.0.0:3868");
        serverInstanceData.setDiameterOriginHost("test.pcc.sterlite.com");
        serverInstanceData.setDiameterOriginRealm("pcc.sterlite.com");

        serverInstanceData.setRadiusEnabled(true);
        serverInstanceData.setRadiusUrl("0.0.0.0:2813");

        /*ServerGroupServerInstanceRelData serverGroupServerInstanceRelData = new ServerGroupServerInstanceRelData();
        serverGroupServerInstanceRelData.setId(UUID.randomUUID().toString());
        serverGroupServerInstanceRelData.setServerInstanceData(serverInstanceData);
        serverGroupServerInstanceRelData.setServerGroupData();
        serverGroupServerInstanceRelData.setServerWeightage(1);
        serverInstanceData.setServerGroupId(UUID.randomUUID().toString());*/
        serverInstanceData.setOfflineRncService(false);

        return serverInstanceData;
    }
}
