package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import java.util.List;

/**
 * A class that defines import operation on list of Emergency Package from Web Service
 */
public class EmergencyPackageManagementListRequest {
    private List<EmergencyPkgDataExt> emergencyPkgData;
    private String action;
    private String parameter1;
    private String parameter2;

    public EmergencyPackageManagementListRequest(List<EmergencyPkgDataExt> emergencyPkgData, String action, String parameter1, String parameter2){
        this.emergencyPkgData = emergencyPkgData;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }


    public List<EmergencyPkgDataExt> getEmergencyPkgData() {
        return emergencyPkgData;
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

    @Override
    public String toString(){
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

        builder.append("Action ", action);
        builder.append("Parameter 1 ", getParameter1());
        builder.append("Parameter 2 ", getParameter2());

        return builder.toString();
    }
}