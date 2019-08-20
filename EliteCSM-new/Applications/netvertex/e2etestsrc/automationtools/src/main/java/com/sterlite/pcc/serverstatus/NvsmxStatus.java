package com.sterlite.pcc.serverstatus;

import okhttp3.OkHttpClient;

import java.io.IOException;

import static com.sterlite.pcc.serverstatus.PropertyConstant.NVSMX_SERVER_STATUS_ULR;
import static com.sterlite.pcc.serverstatus.ResponsePredicates.successful;

public class NvsmxStatus {

    private final Caller caller;
    private OkHttpClient client = new OkHttpClient();
    private Properties properties;
    private String baseUrl;
    private Logger logger = new Logger();

    public NvsmxStatus(Properties properties, String baseUrl) {
        this.properties = properties;
        this.baseUrl = baseUrl;
        this.caller = new Caller(client);
    }


    public static void main(String[] args) throws IOException, InterruptedException {


        if(args.length < 2) {
            throw new IllegalArgumentException("Minimum two argument required 1) Server IP 2) Server Port");
        }


        String baseUrl = "http://" + args[0] + ":" + args[1] + "/netvertexsm";

        Properties properties = Properties.load();

        NvsmxStatus serverStatus = new NvsmxStatus(properties, baseUrl);

        boolean result = serverStatus.checkServerStatus();

        if(result == false) {
            serverStatus.logger.debug("Nvxms status result is fail");
            System.exit(1);
        } else {
            serverStatus.logger.debug("Nvxms status result is pass");
        }

    }


    private boolean checkServerStatus() throws IOException, InterruptedException {
        return caller.callWithRetry(baseUrl + properties.get(NVSMX_SERVER_STATUS_ULR), successful());
    }


}
