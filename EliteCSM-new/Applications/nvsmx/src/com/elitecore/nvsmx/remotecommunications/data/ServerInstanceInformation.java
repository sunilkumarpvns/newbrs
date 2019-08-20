package com.elitecore.nvsmx.remotecommunications.data;

public class ServerInstanceInformation extends ServerInformation{
    private String restApiUrl;

    public ServerInstanceInformation(String name, String netServerCode, String id, String restApiUrl) {
        super(name, netServerCode, id);
        this.restApiUrl = restApiUrl;
    }

    public String getRestApiUrl() {
        return restApiUrl;
    }
}
