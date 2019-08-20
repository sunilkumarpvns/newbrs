package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;

/**
 * A class that defines import operation on Emergency Package from Web Service
 */
public class EmergencyPackageManagementRequest {
    private EmergencyPkgDataExt emergencyPackageData;
    private String action;
    private String parameter1;
    private String parameter2;

    public EmergencyPackageManagementRequest(){}

    public EmergencyPackageManagementRequest(EmergencyPkgDataExt emergencyPackageData, String action, String parameter1, String parameter2) {
        this.emergencyPackageData = emergencyPackageData;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public EmergencyPkgDataExt getEmergencyPackageData() {
        return emergencyPackageData;
    }

    public void setEmergencyPackageData(EmergencyPkgDataExt emergencyPackageData) {
        this.emergencyPackageData = emergencyPackageData;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }
}
