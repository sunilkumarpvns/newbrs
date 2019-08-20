package com.elitecore.nvsmx.ws.reload.response;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;

@XmlRootElement
public class RemoteRMIResponseForDataSlice implements Serializable{
    private static final long serialVersionUID = 1L;
    private ServerInformation instanceGroupData;
    private ServerInformation instanceData;
    private Integer responseCode;
    private String responseMessage;

    public RemoteRMIResponseForDataSlice(ServerInformation instanceGroupData,
                             ServerInformation instanceData,
                             Integer responseCode,
                             String responseMessage) {
        this.instanceGroupData = instanceGroupData;
        this.instanceData = instanceData;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public ServerInformation getInstanceGroupData() {
        return instanceGroupData;
    }

    public void setInstanceGroupData(ServerInformation instanceGroupData) {
        this.instanceGroupData = instanceGroupData;
    }

    public ServerInformation getInstanceData() {
        return instanceData;
    }

    public void setInstanceData(ServerInformation instanceData) {
        this.instanceData = instanceData;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public static RemoteRMIResponseForDataSlice from(RMIResponse<String> rmiResponse){
        if(rmiResponse.isSuccess()) {
            return new RemoteRMIResponseForDataSlice(ServerInformation.from(rmiResponse.getInstanceGroupData()), ServerInformation.from(rmiResponse.getInstanceData()),
                    ResultCode.SUCCESS.code, ResultCode.SUCCESS.name);
        } else {
            return new RemoteRMIResponseForDataSlice(ServerInformation.from(rmiResponse.getInstanceGroupData()), ServerInformation.from(rmiResponse.getInstanceData()),
                    ResultCode.INTERNAL_ERROR.code, rmiResponse.getError().getMessage());
        }

    }

}
