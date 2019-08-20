package com.elitecore.nvsmx.ws.reload.response;

import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail;
import com.google.gson.Gson;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by aditya on 4/21/17.
 */
@XmlRootElement
public class RemoteRMIResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private PolicyCacheDetail policyCacheDetail;
    private ServerInformation instanceGroupData;
    private ServerInformation instanceData;
    private Integer responseCode;
    private String responseMessage;

    public RemoteRMIResponse(PolicyCacheDetail response,
                             ServerInformation instanceGroupData,
                             ServerInformation instanceData,
                             Integer responseCode,
                             String responseMessage) {
        this.policyCacheDetail = response;
        this.instanceGroupData = instanceGroupData;
        this.instanceData = instanceData;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public PolicyCacheDetail getPolicyCacheDetail() {
        return policyCacheDetail;
    }

    public void setPolicyCacheDetail(PolicyCacheDetail policyCacheDetail) {
        this.policyCacheDetail = policyCacheDetail;
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

    public RemoteRMIResponse() {
    }


    public PolicyCacheDetail getResponse() {
        return policyCacheDetail;
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

    public static RemoteRMIResponse from(RMIResponse<com.elitecore.corenetvertex.data.PolicyCacheDetail> rmiResponse){
        if(rmiResponse.isSuccess()) {
            return new RemoteRMIResponse(PolicyCacheDetail.from(rmiResponse.getResponse()), ServerInformation.from(rmiResponse.getInstanceGroupData()), ServerInformation.from(rmiResponse.getInstanceData()),
                    ResultCode.SUCCESS.code, ResultCode.SUCCESS.name);
        } else {
            return new RemoteRMIResponse(null,ServerInformation.from(rmiResponse.getInstanceGroupData()), ServerInformation.from(rmiResponse.getInstanceData()),
                    ResultCode.INTERNAL_ERROR.code, rmiResponse.getError().getMessage());
        }

   }
}
