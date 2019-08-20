package com.sterlite.pcc.serverstatus;

import okhttp3.OkHttpClient;

import java.io.IOException;

import static com.sterlite.pcc.serverstatus.PropertyConstant.*;
import static com.sterlite.pcc.serverstatus.ResponsePredicates.*;

public class ServerStatus {

    private final Caller caller;
    private OkHttpClient client = new OkHttpClient();
    private Properties properties;
    private String baseUrl;
    private Logger logger = new Logger();

    public ServerStatus(Properties properties, String baseUrl) {
        this.properties = properties;
        this.baseUrl = baseUrl;
        this.caller = new Caller(client);
    }


    public static void main(String[] args) throws IOException, InterruptedException {


        if(args.length < 2) {
            throw new IllegalArgumentException("Minimum two argument required 1) Server IP 2) Server Port");
        }

        String baseUrl = "http://" + args[0] + ":" + args[1] + "/netvertex";

        Properties properties = Properties.load();

        ServerStatus serverStatus = new ServerStatus(properties, baseUrl);

        checkServerStatus(serverStatus);

        checkServiceStatus(serverStatus);

        checkDiameterStatus(serverStatus);

        checkRadiusStatus(serverStatus);
    }

    private static void checkServerStatus(ServerStatus serverStatus) throws IOException, InterruptedException {
        serverStatus.logger.debug("Checking server status");
        boolean result = serverStatus.checkServerStatus();

        if(result == false) {
            serverStatus.logger.debug("Server status check fail");
            System.exit(1);
        } else {
            serverStatus.logger.debug("Server status check pass");
        }
    }

    private static void checkServiceStatus(ServerStatus serverStatus) throws IOException, InterruptedException {
        serverStatus.logger.debug("Checking PCRF service status");
        boolean serviceStatusResult = serverStatus.checkServiceStatus();

        if(serviceStatusResult == false) {
            serverStatus.logger.debug("PCRF service status fail");
            System.exit(1);
        } else {
            serverStatus.logger.debug("PCRF service status pass");
        }
    }

    private static void checkDiameterStatus(ServerStatus serverStatus) throws IOException, InterruptedException {
        serverStatus.logger.debug("Checking Diameter status");
        boolean diameterStatusResult = serverStatus.checkDiameterStatus();

        if(diameterStatusResult == false) {
            serverStatus.logger.debug("Diameter status check fail");
            System.exit(1);
        } else {
            serverStatus.logger.debug("Diameter status check pass");
        }
    }

    private static void checkRadiusStatus(ServerStatus serverStatus) throws IOException, InterruptedException {
        serverStatus.logger.debug("Checking Diameter status");
        boolean radiusStatusResult = serverStatus.checkRadius();

        if(radiusStatusResult == false) {
            serverStatus.logger.debug("RADIUS status check fail");
            System.exit(1);
        } else {
            serverStatus.logger.debug("RADIUS status check pass");
        }
    }

    private boolean checkServiceStatus() throws IOException, InterruptedException {
        return caller.callWithRetry(baseUrl + properties.get(PCRF_SERVICE_INFO_URL), successful().and(responseJsonContains("status", "RUNNING")));
    }

    private boolean checkDiameterStatus() throws IOException, InterruptedException {
        return caller.callWithRetry(baseUrl + properties.get(DIAMETER_GLOBAL_LISTENER_STATUS_ULR), successful().and(responseJsonContains("status", "RUNNING")));
    }

    private boolean checkRadius() throws IOException, InterruptedException {
        return caller.callWithRetry(baseUrl + properties.get(RADIUS_GLOBAL_LISTENER_STATUS_ULR), successful().and(responseJsonContains("status", "RUNNING")));
    }


    private boolean checkServerStatus() throws IOException, InterruptedException {
        return caller.callWithRetry(baseUrl + properties.get(SERVER_STATUS_URL), successful().and(responseEquals("RUNNING")));
    }


}
