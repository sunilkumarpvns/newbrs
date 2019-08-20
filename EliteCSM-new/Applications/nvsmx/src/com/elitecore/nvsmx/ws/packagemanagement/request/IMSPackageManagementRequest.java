package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;

/**
 * A class that defines import operation on IMS Package from Web Service
 */
public class IMSPackageManagementRequest {
    private IMSPkgData imsPackageData;
    private String action;
    private String parameter1;
    private String parameter2;

    public IMSPackageManagementRequest(){}

    public IMSPackageManagementRequest(IMSPkgData imsPackageData, String action, String parameter1, String parameter2) {
        this.imsPackageData = imsPackageData;
        this.action = action;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public IMSPkgData getImsPackageData() {
        return imsPackageData;
    }

    public void setImsPackageData(IMSPkgData imsPackageData) {
        this.imsPackageData = imsPackageData;
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
