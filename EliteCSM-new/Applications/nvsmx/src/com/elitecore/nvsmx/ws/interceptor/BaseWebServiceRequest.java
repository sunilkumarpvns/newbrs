package com.elitecore.nvsmx.ws.interceptor;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Prakashkumar Pala
 * @since 22-Oct-2018
 * General implementation of Web Service request for audit purpose like IP Address of client.
 * <br/> This class can be extended by BL Manager Requests
 */
public abstract class BaseWebServiceRequest implements WebServiceRequest{

    private String requestIpAddress;

    @Override
    @ApiModelProperty(hidden=true)
    public String getRequestIpAddress() {
        return requestIpAddress;
    }

    @Override
    public void setRequestIpAddress(String requestIpAddress) {
        this.requestIpAddress = requestIpAddress;
    }
}
