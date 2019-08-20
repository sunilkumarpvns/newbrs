package com.elitecore.netvertex.core.serverinstance;

import com.elitecore.core.systemx.esix.http.HTTPMethodType;
import com.elitecore.core.systemx.esix.http.RemoteMethod;
import com.elitecore.corenetvertex.sm.serverinstance.ConfigurationDatabase;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceRegistrationRequest;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ServerInstanceRegistrationProcess {
    private static final String MODULE = "SEREVER-INSTANCE-REGISTRATION-PROCESS";
    private String serverHome;
    private ServerInstanceRequestData serverInstanceReqData;
    private ServerInstanceReaderAndWriter readerAndWriter;
    private ServerInstanceDBInfoWriter serverInstanceDbInfowriter;
    private ServerInstanceRegistrationCall instanceRegCall;
    private String jmxPort;

    public ServerInstanceRegistrationProcess(String serverHome,
                                             ServerInstanceRequestData serverInstanceReqData,
                                             ServerInstanceReaderAndWriter readerAndWriter,
                                             ServerInstanceDBInfoWriter serverInstanceDbInfowriter,
                                             ServerInstanceRegistrationCall instanceRegCall,
                                             String jmxPort){
        this.serverHome = serverHome;
        this.serverInstanceReqData = serverInstanceReqData;
        this.jmxPort=jmxPort;
        this.readerAndWriter = readerAndWriter;
        this.serverInstanceDbInfowriter = serverInstanceDbInfowriter;
        this.instanceRegCall = instanceRegCall;
    }

    public void calltoSMToGetDBAndServerInstance(){
        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Calling ServerManager to register Server infomation");
        }

        ConfigurationDatabase configurationDB = null;
        String serverInstanceId = null;
        String serverInstanceName = null;
        try {
                if (readerAndWriter.isFileExits()) {
                    if(getLogger().isInfoLogLevel()) {
                        getLogger().info(MODULE, "SysInfo file is available");
                    }
                    readerAndWriter.read();
                    serverInstanceName = readerAndWriter.getName();
                    if (serverInstanceReqData.getServerName().equalsIgnoreCase(serverInstanceName)) {
                        if(getLogger().isInfoLogLevel()) {
                            getLogger().info(MODULE, "SysInfo file is available, Environment instance name is matched with file's instance name");
                        }
                        serverInstanceId = readerAndWriter.getId();
                    }

                }
                ServerInstanceRegistrationRequest serverInstanceRegistrationRequest = callToSm(serverInstanceId,jmxPort, serverHome);
                if (serverInstanceRegistrationRequest == null) {
                    return;
                }

                serverInstanceId = serverInstanceRegistrationRequest.getServerInstanceId();
                serverInstanceName = serverInstanceRegistrationRequest.getServerName();
                configurationDB = ConfigurationDatabase.from(serverInstanceRegistrationRequest);

                // write _sys.info
                readerAndWriter.writeServerInfo(serverInstanceId,serverInstanceName);
                // write dabase.json
                serverInstanceDbInfowriter.writeDBInfo(configurationDB);

            } catch (Exception e) {
               getLogger().error(MODULE, "Error while reading server info. Reason: " + e.getMessage());
               getLogger().trace(MODULE,e);
            }
    }

    private ServerInstanceRegistrationRequest callToSm(String serverId, String jmxPort, String serverHome){

        try {
            RemoteMethod remoteMethod = new RemoteMethod("/integration/serverinstanceregistration/server-instance-registration/*","/registerServerInstance.json", HTTPMethodType.POST);
            remoteMethod.addArgument("serverInstanceId",serverId);
            remoteMethod.addArgument("serverName",serverInstanceReqData.getServerName());
            remoteMethod.addArgument("serverGroupName",serverInstanceReqData.getServerGroupName());
            remoteMethod.addArgument("originHost",serverInstanceReqData.getOriginHost());
            remoteMethod.addArgument("originRealm",serverInstanceReqData.getOriginRealm());
            remoteMethod.addArgument("jmxPort",jmxPort);
            remoteMethod.addArgument("serverHome",serverHome);
            remoteMethod.addArgument("javaHome",System.getenv("JAVA_HOME"));

            return instanceRegCall.callToSm(remoteMethod);
        }catch(Exception e){
            getLogger().error(MODULE, "Exception occurred while calling server manager to register instance. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
        return null;
    }

}
