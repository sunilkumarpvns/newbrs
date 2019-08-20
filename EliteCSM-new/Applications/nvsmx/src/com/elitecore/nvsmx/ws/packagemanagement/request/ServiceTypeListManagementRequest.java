package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import java.util.List;

/**
 * A class that defines import operation on list of Service Type Data from Web Service
 * Created by Ishani on 28/9/16.
 */
public class ServiceTypeListManagementRequest {

    private List<DataServiceTypeDataExt> serviceType;
    private String action;
    private String parameter1;
    private String parameter2;

    public ServiceTypeListManagementRequest(List<DataServiceTypeDataExt> serviceType, String action, String parameter1, String parameter2){
        this.serviceType = serviceType;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public List<DataServiceTypeDataExt> getServiceType() {
        return serviceType;
    }

    public String getParameter1() {
        return parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public String getAction() {
        return action;
    }

    public String toString(){
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

        builder.append("Action ", action);
        builder.append("Parameter 1 ", getParameter1());
        builder.append("Parameter 2 ", getParameter2());

        return builder.toString();
    }
}
