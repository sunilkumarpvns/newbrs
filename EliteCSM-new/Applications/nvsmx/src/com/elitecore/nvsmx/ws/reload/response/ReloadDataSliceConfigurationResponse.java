package com.elitecore.nvsmx.ws.reload.response;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReloadDataSliceConfigurationResponse {

    private int code;
    private String name;
    private String response;
    private List<RemoteRMIResponseForDataSlice> remoteRMIResponsesForDataSlice;

    public ReloadDataSliceConfigurationResponse(int code, String name, String response, List<RemoteRMIResponseForDataSlice> remoteRMIResponsesForDataSlice) {
        this.code = code;
        this.name = name;
        this.response = response;
        this.remoteRMIResponsesForDataSlice = remoteRMIResponsesForDataSlice;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<RemoteRMIResponseForDataSlice> getRemoteRMIResponsesForDataSlice() {
        return remoteRMIResponsesForDataSlice;
    }

    public void setRemoteRMIResponsesForDataSlice(List<RemoteRMIResponseForDataSlice> remoteRMIResponsesForDataSlice) {
        this.remoteRMIResponsesForDataSlice = remoteRMIResponsesForDataSlice;
    }
}
